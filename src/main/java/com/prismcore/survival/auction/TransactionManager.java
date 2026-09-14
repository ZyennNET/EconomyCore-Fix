package com.prismcore.survival.auction;

import com.h2ph.T.A.B;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class TransactionManager {
   private final AuctionController controller;
   private final B auctionDAO;
   private final File transactionFile;
   private FileConfiguration transactionConfig;
   private final Map<UUID, List<Transaction>> transactionCache = new ConcurrentHashMap();

   public TransactionManager(AuctionController var1, B var2) {
      this.controller = var1;
      this.auctionDAO = var2;
      this.transactionFile = new File(var1.getPlugin().getDataFolder(), "transactions.yml");
      this.loadFromYaml();
   }

   public void recordSale(ItemStack var1, double var2, String var4, String var5) {
      long var6 = System.currentTimeMillis();
      Transaction var8 = new Transaction(var1.clone(), var2, var5, var4, var6, true);
      UUID var9 = this.controller.getPlugin().getServer().getOfflinePlayer(var4).getUniqueId();
      Transaction var10 = new Transaction(var1.clone(), var2, var5, var4, var6, false);
      UUID var11 = this.controller.getPlugin().getServer().getOfflinePlayer(var5).getUniqueId();
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.auctionDAO.A(var9, var8);
         this.auctionDAO.A(var11, var10);
      } else {
         ((List)this.transactionCache.computeIfAbsent(var9, (var0) -> new ArrayList())).add(var8);
         ((List)this.transactionCache.computeIfAbsent(var11, (var0) -> new ArrayList())).add(var10);
         this.saveToYaml();
      }

   }

   public List<Transaction> getPlayerTransactions(UUID var1) {
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         List var3 = this.auctionDAO.D(var1);
         var3.sort(Comparator.comparingLong(Transaction::getTimestamp).reversed());
         return var3;
      } else {
         List var2 = (List)this.transactionCache.getOrDefault(var1, Collections.emptyList());
         var2.sort(Comparator.comparingLong(Transaction::getTimestamp).reversed());
         return new ArrayList(var2);
      }
   }

   public double getTotalSpent(UUID var1) {
      double var2 = (double)0.0F;

      for(Transaction var5 : this.getPlayerTransactions(var1)) {
         if (!var5.isSale()) {
            var2 += var5.getPrice();
         }
      }

      return var2;
   }

   public double getTotalMade(UUID var1) {
      double var2 = (double)0.0F;

      for(Transaction var5 : this.getPlayerTransactions(var1)) {
         if (var5.isSale()) {
            var2 += var5.getPrice();
         }
      }

      return var2;
   }

   public void loadFromConfig() {
      if (!this.controller.getPlugin().getDatabaseManager().F()) {
         this.loadFromYaml();
      }

   }

   public void saveToConfig() {
      if (!this.controller.getPlugin().getDatabaseManager().F()) {
         this.saveToYaml();
      }

   }

   private void loadFromYaml() {
      this.transactionCache.clear();
      if (!this.transactionFile.exists()) {
         try {
            this.transactionFile.getParentFile().mkdirs();
            this.transactionFile.createNewFile();
            this.controller.getPlugin().getLogger().info("Created new transactions.yml");
         } catch (IOException var17) {
            this.controller.getPlugin().getLogger().severe("Could not create transactions.yml: " + var17.getMessage());
         }

      } else {
         this.transactionConfig = YamlConfiguration.loadConfiguration(this.transactionFile);
         if (this.transactionConfig.contains("transactions")) {
            for(String var2 : this.transactionConfig.getConfigurationSection("transactions").getKeys(false)) {
               try {
                  UUID var3 = UUID.fromString(var2);
                  List var4 = this.transactionConfig.getMapList("transactions." + var2);
                  ArrayList var5 = new ArrayList();

                  for(Map var7 : var4) {
                     String var8 = (String)var7.get("item");
                     ItemStack var9 = this.itemStackFromBase64(var8);
                     if (var9 != null) {
                        double var10 = ((Number)var7.get("price")).doubleValue();
                        String var12 = (String)var7.get("buyer");
                        String var13 = (String)var7.get("seller");
                        long var14 = ((Number)var7.get("timestamp")).longValue();
                        boolean var16 = (Boolean)var7.get("isSale");
                        var5.add(new Transaction(var9, var10, var12, var13, var14, var16));
                     }
                  }

                  if (!var5.isEmpty()) {
                     this.transactionCache.put(var3, var5);
                  }
               } catch (Exception var18) {
                  this.controller.getPlugin().getLogger().warning("Failed to load transaction entry for " + var2 + ": " + var18.getMessage());
               }
            }

            this.controller.getPlugin().getLogger().info("Loaded " + this.transactionCache.size() + " player transaction records from YAML.");
         }
      }
   }

   private void saveToYaml() {
      if (this.transactionConfig == null) {
         this.transactionConfig = new YamlConfiguration();
      }

      this.transactionConfig.set("transactions", (Object)null);

      for(Map.Entry var2 : this.transactionCache.entrySet()) {
         ArrayList var3 = new ArrayList();

         for(Transaction var5 : (List)var2.getValue()) {
            HashMap var6 = new HashMap();
            var6.put("item", this.itemStackToBase64(var5.getItem()));
            var6.put("price", var5.getPrice());
            var6.put("buyer", var5.getBuyer());
            var6.put("seller", var5.getSeller());
            var6.put("timestamp", var5.getTimestamp());
            var6.put("isSale", var5.isSale());
            var3.add(var6);
         }

         this.transactionConfig.set("transactions." + ((UUID)var2.getKey()).toString(), var3);
      }

      try {
         this.transactionConfig.save(this.transactionFile);
      } catch (IOException var7) {
         this.controller.getPlugin().getLogger().severe("Failed to save transactions.yml: " + var7.getMessage());
      }

   }

   private String itemStackToBase64(ItemStack var1) {
      if (var1 == null) {
         return null;
      } else {
         try {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream();

            String var4;
            try {
               BukkitObjectOutputStream var3 = new BukkitObjectOutputStream(var2);

               try {
                  var3.writeObject(var1);
                  var4 = Base64.getEncoder().encodeToString(var2.toByteArray());
               } catch (Throwable var8) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }

                  throw var8;
               }

               var3.close();
            } catch (Throwable var9) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }

               throw var9;
            }

            var2.close();
            return var4;
         } catch (Exception var10) {
            this.controller.getPlugin().getLogger().warning("Failed to serialize item: " + var10.getMessage());
            return null;
         }
      }
   }

   private ItemStack itemStackFromBase64(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            ByteArrayInputStream var2 = new ByteArrayInputStream(Base64.getDecoder().decode(var1));

            ItemStack var4;
            try {
               BukkitObjectInputStream var3 = new BukkitObjectInputStream(var2);

               try {
                  var4 = (ItemStack)var3.readObject();
               } catch (Throwable var8) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }

                  throw var8;
               }

               var3.close();
            } catch (Throwable var9) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var9.addSuppressed(var6);
               }

               throw var9;
            }

            var2.close();
            return var4;
         } catch (Exception var10) {
            this.controller.getPlugin().getLogger().warning("Failed to deserialize item: " + var10.getMessage());
            return null;
         }
      } else {
         return null;
      }
   }
}
