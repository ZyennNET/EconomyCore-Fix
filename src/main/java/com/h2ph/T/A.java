package com.h2ph.T;

import com.h2ph.PrismSurvival;
import com.h2ph.T.A.D;
import com.h2ph.T.A.F;
import com.prismcore.survival.auction.AuctionItem;
import com.prismcore.survival.auction.Transaction;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class A {
   private final PrismSurvival F;
   private final B E;
   private final D D;
   private final F G;
   private final com.h2ph.T.A.C B;
   private final com.h2ph.T.A.B C;
   private final File A;

   public A(PrismSurvival var1, B var2, D var3, F var4, com.h2ph.T.A.C var5, com.h2ph.T.A.B var6) {
      this.F = var1;
      this.E = var2;
      this.D = var3;
      this.G = var4;
      this.B = var5;
      this.C = var6;
      this.A = new File(var1.getDataFolder(), "migrated");
      if (!this.A.exists()) {
         this.A.mkdirs();
      }

   }

   public void A() {
      if (!this.E.F()) {
         this.F.getLogger().warning("Skipping migration: MySQL is disabled.");
      } else {
         this.F.getLogger().info("Starting database migration...");
         int var1 = 0;
         var1 += this.B(new File(this.F.getDataFolder(), "economy/shards/players"), "shards");
         var1 += this.B(new File(this.F.getDataFolder(), "economy/money/players"), "money");
         var1 += this.B(new File(this.F.getDataFolder(), "crates/data"), "crates");
         var1 += this.B(new File(this.F.getDataFolder(), "survival/duels/stats"), "duels");
         var1 += this.C();
         var1 += this.B();
         this.F.getLogger().info("Database migration completed. Total files migrated: " + var1);
      }
   }

   private int C() {
      File var1 = new File(this.F.getDataFolder(), "economy/auction/saves.yml");
      if (!var1.exists()) {
         return 0;
      } else {
         this.F.getLogger().info("Found auction saves.yml. Starting migration logic...");
         int var2 = 0;

         try {
            String var3 = new String(Files.readAllBytes(var1.toPath()), StandardCharsets.UTF_8);
            String var4 = var3.replace("==: org.bukkit.inventory.ItemStack", "internal_class: ItemStack").replace("==: com.prismcore.survival.auction.AuctionItem", "internal_class: AuctionItem").replace("==: com.prismcore.survival.auction.Transaction", "internal_class: Transaction");
            YamlConfiguration var5 = new YamlConfiguration();

            try {
               ((FileConfiguration)var5).loadFromString(var4);
            } catch (Exception var20) {
               this.F.getLogger().warning("Failed to load sanitized auction file: " + var20.getMessage());
               var5 = YamlConfiguration.loadConfiguration(var1);
            }

            if (((FileConfiguration)var5).isConfigurationSection("auctions")) {
               ConfigurationSection var6 = ((FileConfiguration)var5).getConfigurationSection("auctions");
               Set var7 = var6.getKeys(false);
               this.F.getLogger().info("Found " + var7.size() + " active auctions to migrate.");

               for(String var9 : var7) {
                  try {
                     ConfigurationSection var10 = var6.getConfigurationSection(var9);
                     if (var10 != null) {
                        Map var11 = this.A(var10);
                        var11.put("id", var9);
                        this.A(var11);
                        ++var2;
                     }
                  } catch (Exception var19) {
                     this.F.getLogger().warning("Failed to migrate auction " + var9 + ": " + var19.getMessage());
                  }
               }
            }

            if (((FileConfiguration)var5).isConfigurationSection("transactions")) {
               ConfigurationSection var23 = ((FileConfiguration)var5).getConfigurationSection("transactions");
               Set var27 = var23.getKeys(false);
               this.F.getLogger().info("Found " + var27.size() + " players with transaction history.");
               int var31 = 0;

               for(String var37 : var27) {
                  try {
                     UUID var39 = UUID.fromString(var37);
                     ConfigurationSection var12 = var23.getConfigurationSection(var37);
                     if (var12 != null) {
                        for(String var14 : var12.getKeys(false)) {
                           try {
                              ConfigurationSection var15 = var12.getConfigurationSection(var14);
                              if (var15 != null) {
                                 Map var16 = this.A(var15);
                                 this.A(var39, var16);
                                 ++var31;
                              }
                           } catch (Exception var18) {
                           }
                        }
                     }
                  } catch (Exception var21) {
                     this.F.getLogger().warning("Failed to migrate transactions for " + var37 + ": " + var21.getMessage());
                  }
               }

               this.F.getLogger().info("Migrated " + var31 + " individual transactions.");
            } else {
               this.F.getLogger().info("No 'transactions' section found.");
            }

            if (((FileConfiguration)var5).isConfigurationSection("sort")) {
               ConfigurationSection var24 = ((FileConfiguration)var5).getConfigurationSection("sort");

               for(String var32 : var24.getKeys(false)) {
                  try {
                     String var35 = var24.getString(var32);
                     this.C.A(UUID.fromString(var32), var35);
                  } catch (Exception var17) {
                  }
               }
            }

            if (((FileConfiguration)var5).isConfigurationSection("pending-sales")) {
               ConfigurationSection var25 = ((FileConfiguration)var5).getConfigurationSection("pending-sales");

               for(String var33 : var25.getKeys(false)) {
                  List var36 = var25.getList(var33);
                  if (var36 != null) {
                     for(Object var40 : var36) {
                        if (var40 instanceof Map) {
                           Map var41 = (Map)var40;
                           String var42 = (String)var41.get("buyer");
                           String var43 = (String)var41.get("item");
                           Double var44 = null;
                           if (var41.get("price") instanceof Number) {
                              var44 = ((Number)var41.get("price")).doubleValue();
                           }

                           if (var42 != null && var43 != null && var44 != null) {
                              this.C.A(UUID.fromString(var33), var42, var43, var44);
                           }
                        }
                     }
                  }
               }
            }

            File var26 = new File(this.A, "auction");
            if (!var26.exists()) {
               var26.mkdirs();
            }

            File var30 = new File(var26, "saves.yml");
            if (var1.renameTo(var30)) {
               this.F.getLogger().info("Successfully migrated auctions & transactions.");
            } else {
               this.F.getLogger().warning("Failed to move migrated auction saves.yml");
            }

            return 1;
         } catch (Exception var22) {
            this.F.getLogger().warning("Failed to migrate auctions: " + var22.getMessage());
            var22.printStackTrace();
            return 0;
         }
      }
   }

   private Map<String, Object> A(ConfigurationSection var1) {
      LinkedHashMap var2 = new LinkedHashMap();

      for(String var4 : var1.getKeys(false)) {
         Object var5 = var1.get(var4);
         if (var5 instanceof ConfigurationSection) {
            var2.put(var4, this.A((ConfigurationSection)var5));
         } else {
            var2.put(var4, var5);
         }
      }

      return var2;
   }

   private void A(Map<String, Object> var1) {
      try {
         String var2 = (String)var1.get("id");
         UUID var3 = UUID.fromString(var2);
         String var4 = (String)var1.get("seller");
         Double var5 = null;
         if (var1.get("price") instanceof Number) {
            var5 = ((Number)var1.get("price")).doubleValue();
         }

         Long var6 = null;
         if (var1.get("listedAt") instanceof Number) {
            var6 = ((Number)var1.get("listedAt")).longValue();
         }

         Integer var7 = null;
         if (var1.get("duration") instanceof Number) {
            var7 = ((Number)var1.get("duration")).intValue();
         }

         if (var5 != null && var6 != null && var7 != null) {
            Object var8 = var1.get("item");
            if (var8 instanceof Map) {
               Map var9 = (Map)var8;
               var9.remove("internal_class");
               ItemStack var10 = ItemStack.deserialize(var9);
               AuctionItem var11 = new AuctionItem(var3, var4, var10, var5, var6, var7);
               this.C.A(var11);
            }

         }
      } catch (Exception var12) {
         throw new RuntimeException("Reconstruction failed: " + var12.getMessage());
      }
   }

   private void A(UUID var1, Map<?, ?> var2) {
      try {
         Double var3 = (double)0.0F;
         if (var2.get("price") instanceof Number) {
            var3 = ((Number)var2.get("price")).doubleValue();
         }

         String var4 = (String)var2.get("buyer");
         String var5 = (String)var2.get("seller");
         Long var6 = 0L;
         if (var2.get("timestamp") instanceof Number) {
            var6 = ((Number)var2.get("timestamp")).longValue();
         }

         Boolean var7 = Boolean.FALSE;
         if (var2.get("isSale") instanceof Boolean) {
            var7 = (Boolean)var2.get("isSale");
         }

         Object var8 = var2.get("item");
         if (var8 instanceof Map var9) {
            var9.remove("internal_class");
            ItemStack var10 = ItemStack.deserialize(var9);
            Transaction var11 = new Transaction(var10, var3, var4, var5, var6, var7);
            this.C.A(var1, var11);
         }
      } catch (Exception var12) {
         this.F.getLogger().warning("Failed transaction migration: " + var12.getMessage());
      }

   }

   private int B() {
      return 0;
   }

   private int B(File var1, String var2) {
      if (var1.exists() && var1.isDirectory()) {
         File[] var3 = var1.listFiles((var0, var1x) -> var1x.endsWith(".db") || var1x.endsWith(".yml"));
         if (var3 != null && var3.length != 0) {
            int var4 = 0;
            File var5 = new File(this.A, var2);
            if (!var5.exists()) {
               var5.mkdirs();
            }

            for(File var9 : var3) {
               try {
                  String var10 = var9.getName();
                  String var11 = null;
                  boolean var12 = false;

                  try {
                     if (var2.equals("shards") && var10.endsWith("-shards.db")) {
                        var11 = var10.replace("-shards.db", "");
                        this.C(var9, UUID.fromString(var11));
                        var12 = true;
                     } else if (var2.equals("money") && var10.endsWith("-money.db")) {
                        var11 = var10.replace("-money.db", "");
                        this.D(var9, UUID.fromString(var11));
                        var12 = true;
                     } else if (var2.equals("crates") && var10.endsWith(".db")) {
                        var11 = var10.replace(".db", "");
                        this.A(var9, UUID.fromString(var11));
                        var12 = true;
                     } else if (var2.equals("duels") && var10.endsWith(".yml")) {
                        var11 = var10.replace(".yml", "");
                        this.B(var9, UUID.fromString(var11));
                        var12 = true;
                     }
                  } catch (IllegalArgumentException var14) {
                  }

                  if (var12 && var11 != null) {
                     File var13 = new File(var5, var9.getName());
                     if (var9.renameTo(var13)) {
                        ++var4;
                     } else {
                        this.F.getLogger().warning("Failed to move migrated file: " + var9.getName());
                     }
                  }
               } catch (Exception var15) {
                  this.F.getLogger().log(Level.WARNING, "Failed to migrate file: " + var9.getName(), var15);
               }
            }

            if (var4 > 0) {
               this.F.getLogger().info("Migrated " + var4 + " " + var2 + " files.");
            }

            return var4;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private void C(File var1, UUID var2) {
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var1);
      double var4 = ((FileConfiguration)var3).getDouble("shards", (double)0.0F);
      double var6 = ((FileConfiguration)var3).getDouble("shop_spent", (double)0.0F);
      PlayerData var8 = this.D.A(var2);
      if (var4 > (double)0.0F) {
         var8.setShards(var4);
      }

      if (var6 > (double)0.0F) {
         var8.setShopSpent(var6);
      }

      if (((FileConfiguration)var3).contains("keys")) {
         for(String var10 : ((FileConfiguration)var3).getConfigurationSection("keys").getKeys(false)) {
            int var11 = ((FileConfiguration)var3).getInt("keys." + var10, 0);
            if (var11 > 0) {
               var8.setKeyCount(var10, var11);
            }
         }
      }

      this.D.A(var8);
   }

   private void D(File var1, UUID var2) {
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var1);
      double var4 = ((FileConfiguration)var3).getDouble("money", (double)0.0F);
      String var6 = ((FileConfiguration)var3).getString("cached_name");
      PlayerData var7 = this.D.A(var2);
      if (var4 > (double)0.0F) {
         var7.setMoney(var4);
      }

      if (var6 != null) {
         var7.setName(var6);
      }

      this.D.A(var7);
   }

   private void A(File var1, UUID var2) {
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var1);
      PlayerData var4 = this.D.A(var2);
      if (((FileConfiguration)var3).contains("keys")) {
         for(String var6 : ((FileConfiguration)var3).getConfigurationSection("keys").getKeys(false)) {
            int var7 = ((FileConfiguration)var3).getInt("keys." + var6, 0);
            if (var7 > 0) {
               var4.setKeyCount(var6, var7);
            }
         }
      }

      if (((FileConfiguration)var3).contains("last_seen_update")) {
         var4.setLastSeenUpdate(((FileConfiguration)var3).getLong("last_seen_update"));
      }

      if (((FileConfiguration)var3).contains("shard_booster_expiry")) {
         var4.setShardBoosterExpiry(((FileConfiguration)var3).getLong("shard_booster_expiry"));
      }

      this.D.A(var4);
   }

   private void B(File var1, UUID var2) {
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var1);
      int var4 = ((FileConfiguration)var3).getInt("wins", 0);
      int var5 = ((FileConfiguration)var3).getInt("losses", 0);
      int var6 = ((FileConfiguration)var3).getInt("streak", 0);
      if (this.E.F()) {
         try {
            Connection var7 = this.E.G();

            try {
               String var8 = "INSERT INTO duel_stats (uuid, wins, losses, streak) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE wins = VALUES(wins), losses = VALUES(losses), streak = VALUES(streak)";
               PreparedStatement var9 = var7.prepareStatement(var8);

               try {
                  var9.setString(1, var2.toString());
                  var9.setInt(2, var4);
                  var9.setInt(3, var5);
                  var9.setInt(4, var6);
                  var9.executeUpdate();
               } catch (Throwable var14) {
                  if (var9 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (var9 != null) {
                  var9.close();
               }
            } catch (Throwable var15) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
                  }
               }

               throw var15;
            }

            if (var7 != null) {
               var7.close();
            }
         } catch (Exception var16) {
            this.F.getLogger().warning("Failed to migrate duel stats for " + String.valueOf(var2));
            var16.printStackTrace();
         }
      }

   }
}
