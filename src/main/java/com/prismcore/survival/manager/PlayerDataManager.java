package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import com.h2ph.T.A.D;
import com.h2ph.T.A.H;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PlayerDataManager {
   private final PrismSurvival plugin;
   private final D playerDAO;
   private final Map<UUID, PlayerData> playerDataMap = new HashMap();
   private final File dataFolderShards;
   private final File dataFolderMoney;
   private final File dataFolderCrates;
   private long lastShardsUpdate = 0L;
   private List<LeaderboardEntry> cachedShardsTop = null;
   private long lastMoneyUpdate = 0L;
   private List<LeaderboardEntry> cachedMoneyTop = null;
   private static final long CACHE_DURATION = 60000L;
   private boolean isUpdatingMoney = false;

   public D getPlayerDAO() {
      return this.playerDAO;
   }

   public PlayerDataManager(PrismSurvival var1) {
      this.plugin = var1;
      this.dataFolderShards = new File(var1.getDataFolder(), "economy/shards/players");
      this.dataFolderMoney = new File(var1.getDataFolder(), "economy/money/players");
      this.dataFolderCrates = new File(var1.getDataFolder(), "crates/data");
      if (!this.dataFolderShards.exists()) {
         this.dataFolderShards.mkdirs();
      }

      if (!this.dataFolderMoney.exists()) {
         this.dataFolderMoney.mkdirs();
      }

      if (!this.dataFolderCrates.exists()) {
         this.dataFolderCrates.mkdirs();
      }

      if (var1.getDatabaseManager() != null && var1.getDatabaseManager().F()) {
         this.playerDAO = new H(var1, var1.getDatabaseManager());
      } else {
         this.playerDAO = null;
      }

   }

   public PlayerData get(UUID var1) {
      if (this.playerDataMap.containsKey(var1)) {
         return (PlayerData)this.playerDataMap.get(var1);
      } else {
         PlayerData var2 = this.loadPlayer(var1);
         this.playerDataMap.put(var1, var2);
         return var2;
      }
   }

   public PlayerData loadPlayer(UUID var1) {
      if (this.playerDAO != null) {
         return this.playerDAO.A(var1);
      } else {
         PlayerData var2 = new PlayerData(var1);
         File var3 = new File(this.dataFolderShards, var1.toString() + "-shards.db");
         if (var3.exists()) {
            YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
            var2.setShards(((FileConfiguration)var4).getDouble("shards", (double)0.0F));
            var2.setShopSpent(((FileConfiguration)var4).getDouble("shop_spent", (double)0.0F));
            if (((FileConfiguration)var4).contains("keys")) {
               for(String var6 : ((FileConfiguration)var4).getConfigurationSection("keys").getKeys(false)) {
                  int var7 = ((FileConfiguration)var4).getInt("keys." + var6, 0);
                  var2.setKeyCount(var6, var7);
               }
            }
         }

         File var10 = new File(this.dataFolderCrates, var1.toString() + ".db");
         if (var10.exists()) {
            YamlConfiguration var11 = YamlConfiguration.loadConfiguration(var10);
            if (((FileConfiguration)var11).contains("keys")) {
               for(String var17 : ((FileConfiguration)var11).getConfigurationSection("keys").getKeys(false)) {
                  int var8 = ((FileConfiguration)var11).getInt("keys." + var17, 0);
                  var2.setKeyCount(var17, var8);
               }
            }

            if (((FileConfiguration)var11).contains("last_seen_update")) {
               var2.setLastSeenUpdate(((FileConfiguration)var11).getLong("last_seen_update"));
            }

            if (((FileConfiguration)var11).contains("shard_booster_expiry")) {
               var2.setShardBoosterExpiry(((FileConfiguration)var11).getLong("shard_booster_expiry"));
            }
         }

         File var12 = new File(this.dataFolderMoney, var1.toString() + "-money.db");
         if (var12.exists()) {
            YamlConfiguration var14 = YamlConfiguration.loadConfiguration(var12);
            var2.setMoney(((FileConfiguration)var14).getDouble("money", (double)0.0F));
            if (((FileConfiguration)var14).contains("cached_name")) {
               var2.setName(((FileConfiguration)var14).getString("cached_name"));
            }

            if (((FileConfiguration)var14).contains("offline_payments")) {
               for(String var9 : ((FileConfiguration)var14).getStringList("offline_payments")) {
                  var2.addOfflinePayment(var9);
               }
            }
         }

         if (var2.getName() == null && var3.exists()) {
            YamlConfiguration var15 = YamlConfiguration.loadConfiguration(var3);
            if (((FileConfiguration)var15).contains("cached_name")) {
               var2.setName(((FileConfiguration)var15).getString("cached_name"));
            }
         }

         if (var2.getName() == null) {
            OfflinePlayer var16 = Bukkit.getOfflinePlayer(var1);
            if (var16.getName() != null) {
               var2.setName(var16.getName());
            }
         }

         return var2;
      }
   }

   public void savePlayer(UUID var1) {
      PlayerData var2 = (PlayerData)this.playerDataMap.get(var1);
      if (var2 != null) {
         if (this.playerDAO != null) {
            this.playerDAO.A(var2);
         } else {
            File var3 = new File(this.dataFolderShards, var1.toString() + "-shards.db");
            YamlConfiguration var4 = new YamlConfiguration();
            ((FileConfiguration)var4).set("shards", var2.getShards());
            ((FileConfiguration)var4).set("shop_spent", var2.getShopSpent());

            try {
               ((FileConfiguration)var4).save(var3);
            } catch (IOException var13) {
               Logger var10000 = this.plugin.getLogger();
               String var10001 = String.valueOf(var1);
               var10000.severe("Failed to save shards data for " + var10001 + ": " + var13.getMessage());
            }

            File var5 = new File(this.dataFolderCrates, var1.toString() + ".db");
            YamlConfiguration var6 = new YamlConfiguration();
            Map var7 = var2.getKeys();

            for(Map.Entry var9 : var7.entrySet()) {
               ((FileConfiguration)var6).set("keys." + (String)var9.getKey(), var9.getValue());
            }

            ((FileConfiguration)var6).set("last_seen_update", var2.getLastSeenUpdate());
            ((FileConfiguration)var6).set("shard_booster_expiry", var2.getShardBoosterExpiry());

            try {
               ((FileConfiguration)var6).save(var5);
            } catch (IOException var12) {
               Logger var16 = this.plugin.getLogger();
               String var18 = String.valueOf(var1);
               var16.severe("Failed to save crates data for " + var18 + ": " + var12.getMessage());
            }

            File var14 = new File(this.dataFolderMoney, var1.toString() + "-money.db");
            YamlConfiguration var15 = new YamlConfiguration();
            ((FileConfiguration)var15).set("money", var2.getMoney());
            if (var2.getName() != null) {
               ((FileConfiguration)var15).set("cached_name", var2.getName());
            } else {
               OfflinePlayer var10 = Bukkit.getOfflinePlayer(var1);
               if (var10.getName() != null) {
                  ((FileConfiguration)var15).set("cached_name", var10.getName());
                  var2.setName(var10.getName());
               }
            }

            if (!var2.getOfflinePayments().isEmpty()) {
               ((FileConfiguration)var15).set("offline_payments", var2.getOfflinePayments());
            }

            try {
               ((FileConfiguration)var15).save(var14);
            } catch (IOException var11) {
               Logger var17 = this.plugin.getLogger();
               String var19 = String.valueOf(var1);
               var17.severe("Failed to save money data for " + var19 + ": " + var11.getMessage());
            }

         }
      }
   }

   public void unload(UUID var1) {
      this.savePlayer(var1);
      this.playerDataMap.remove(var1);
   }

   public List<LeaderboardEntry> getTopShards(int var1) {
      if (this.cachedShardsTop != null && System.currentTimeMillis() - this.lastShardsUpdate < 60000L) {
         return this.cachedShardsTop.size() > var1 ? this.cachedShardsTop.subList(0, var1) : this.cachedShardsTop;
      } else {
         ArrayList var2 = new ArrayList();
         if (this.dataFolderShards.exists()) {
            File[] var3 = this.dataFolderShards.listFiles((var0, var1x) -> var1x.endsWith("-shards.db"));
            if (var3 != null) {
               for(File var7 : var3) {
                  YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var7);
                  double var9 = ((FileConfiguration)var8).getDouble("shards", (double)0.0F);
                  if (var9 > (double)0.0F) {
                     String var11 = var7.getName().replace("-shards.db", "");

                     try {
                        UUID var12 = UUID.fromString(var11);
                        String var13 = ((FileConfiguration)var8).getString("cached_name");
                        if (var13 == null) {
                           OfflinePlayer var14 = Bukkit.getOfflinePlayer(var12);
                           var13 = var14.getName();
                           if (var13 != null) {
                              ((FileConfiguration)var8).set("cached_name", var13);

                              try {
                                 ((FileConfiguration)var8).save(var7);
                              } catch (IOException var16) {
                              }
                           }
                        }

                        if (var13 != null) {
                           var2.add(new LeaderboardEntry(var13, var12, var9));
                        }
                     } catch (IllegalArgumentException var17) {
                     }
                  }
               }
            }
         }

         var2.sort((var0, var1x) -> Double.compare(var1x.value, var0.value));
         this.cachedShardsTop = var2;
         this.lastShardsUpdate = System.currentTimeMillis();
         return (List<LeaderboardEntry>)(var2.size() > var1 ? var2.subList(0, var1) : var2);
      }
   }

   private void fetchVaultTopMoney() {
      if (this.plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
         RegisteredServiceProvider var1 = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
         if (var1 != null) {
            Economy var2 = (Economy)var1.getProvider();
            if (var2 != null) {
               ArrayList var3 = new ArrayList();

               for(OfflinePlayer var7 : this.plugin.getServer().getOfflinePlayers()) {
                  if (var7.getName() != null) {
                     try {
                        if (var2.hasAccount(var7)) {
                           double var8 = var2.getBalance(var7);
                           if (var8 > (double)0.0F) {
                              var3.add(new LeaderboardEntry(var7.getName(), var7.getUniqueId(), var8));
                           }
                        }
                     } catch (Exception var10) {
                     }
                  }
               }

               var3.sort((var0, var1x) -> Double.compare(var1x.value, var0.value));
               this.cachedMoneyTop = var3;
               this.lastMoneyUpdate = System.currentTimeMillis();
            }
         }
      }
   }

   public List<LeaderboardEntry> getTopMoney(int var1) {
      if (this.cachedMoneyTop != null && !this.cachedMoneyTop.isEmpty()) {
         if (System.currentTimeMillis() - this.lastMoneyUpdate > 60000L) {
            this.triggerVaultUpdateAsync();
         }

         return this.cachedMoneyTop.size() > var1 ? this.cachedMoneyTop.subList(0, var1) : this.cachedMoneyTop;
      } else {
         if (this.plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            this.triggerVaultUpdateAsync();
         }

         ArrayList var2 = new ArrayList();
         if (this.dataFolderMoney.exists()) {
            File[] var3 = this.dataFolderMoney.listFiles((var0, var1x) -> var1x.endsWith("-money.db"));
            if (var3 != null) {
               for(File var7 : var3) {
                  YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var7);
                  double var9 = ((FileConfiguration)var8).getDouble("money", (double)0.0F);
                  if (var9 > (double)0.0F) {
                     String var11 = var7.getName().replace("-money.db", "");

                     try {
                        UUID var12 = UUID.fromString(var11);
                        String var13 = ((FileConfiguration)var8).getString("cached_name");
                        if (var13 == null) {
                           OfflinePlayer var14 = Bukkit.getOfflinePlayer(var12);
                           var13 = var14.getName();
                           if (var13 != null) {
                              ((FileConfiguration)var8).set("cached_name", var13);

                              try {
                                 ((FileConfiguration)var8).save(var7);
                              } catch (IOException var16) {
                              }
                           }
                        }

                        if (var13 != null) {
                           var2.add(new LeaderboardEntry(var13, var12, var9));
                        }
                     } catch (IllegalArgumentException var17) {
                     }
                  }
               }
            }
         }

         var2.sort((var0, var1x) -> Double.compare(var1x.value, var0.value));
         if ((this.cachedMoneyTop == null || this.cachedMoneyTop.isEmpty()) && !var2.isEmpty()) {
            this.cachedMoneyTop = var2;
            this.lastMoneyUpdate = System.currentTimeMillis();
         }

         return (List<LeaderboardEntry>)(var2.size() > var1 ? var2.subList(0, var1) : var2);
      }
   }

   private void triggerVaultUpdateAsync() {
      if (!this.isUpdatingMoney) {
         this.isUpdatingMoney = true;
         this.plugin.getSchedulerAdapter().runTaskAsync(() -> {
            try {
               this.fetchVaultTopMoney();
            } finally {
               this.isUpdatingMoney = false;
            }

         });
      }
   }

   public static class LeaderboardEntry {
      public String name;
      public UUID uuid;
      public double value;

      public LeaderboardEntry(String var1, UUID var2, double var3) {
         this.name = var1;
         this.uuid = var2;
         this.value = var3;
      }
   }
}
