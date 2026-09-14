package com.h2ph.T.A;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class H implements D {
   private final PrismSurvival B;
   private final com.h2ph.T.B A;

   public H(PrismSurvival var1, com.h2ph.T.B var2) {
      this.B = var1;
      this.A = var2;
   }

   public PlayerData A(UUID var1) {
      if (this.A.B()) {
      }

      PlayerData var2 = new PlayerData(var1);

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement("SELECT * FROM player_data WHERE uuid = ?");

            try {
               var4.setString(1, var1.toString());
               ResultSet var5 = var4.executeQuery();
               if (var5.next()) {
                  var2.setName(var5.getString("name"));
                  var2.setMoney(var5.getDouble("money"));
                  var2.setShards(var5.getDouble("shards"));
                  var2.setShopSpent(var5.getDouble("shop_spent"));
                  var2.setLastSeenUpdate(var5.getLong("last_seen_update"));
                  var2.setShardBoosterExpiry(var5.getLong("shard_booster_expiry"));
                  var2.setRtpCooldown(var5.getLong("rtp_cooldown"));
                  var2.setPendingRtpType(var5.getString("pending_rtp_type"));
                  var2.setPendingRtpTargetServer(var5.getString("pending_rtp_target_server"));
                  var2.setPendingSpawnName(var5.getString("pending_spawn_name"));
                  var2.setPendingSpawnWorld(var5.getString("pending_spawn_world"));
                  if (var5.getObject("pending_spawn_x") != null) {
                     var2.setPendingSpawnX(var5.getDouble("pending_spawn_x"));
                  }

                  if (var5.getObject("pending_spawn_y") != null) {
                     var2.setPendingSpawnY(var5.getDouble("pending_spawn_y"));
                  }

                  if (var5.getObject("pending_spawn_z") != null) {
                     var2.setPendingSpawnZ(var5.getDouble("pending_spawn_z"));
                  }

                  if (var5.getObject("pending_spawn_yaw") != null) {
                     var2.setPendingSpawnYaw(var5.getFloat("pending_spawn_yaw"));
                  }

                  if (var5.getObject("pending_spawn_pitch") != null) {
                     var2.setPendingSpawnPitch(var5.getFloat("pending_spawn_pitch"));
                  }

                  Gson var6 = new Gson();
                  String var7 = var5.getString("keys");
                  if (var7 != null && !var7.isEmpty()) {
                     Type var8 = (new TypeToken<Map<String, Integer>>(this) {
                     }).getType();
                     Map var9 = (Map)var6.fromJson(var7, var8);
                     if (var9 != null) {
                        for(Map.Entry var11 : var9.entrySet()) {
                           var2.setKeyCount((String)var11.getKey(), (Integer)var11.getValue());
                        }
                     }
                  }

                  String var19 = var5.getString("offline_payments");
                  if (var19 != null && !var19.isEmpty()) {
                     Type var20 = (new TypeToken<List<String>>(this) {
                     }).getType();
                     List var21 = (List)var6.fromJson(var19, var20);
                     if (var21 != null) {
                        for(String var12 : var21) {
                           var2.addOfflinePayment(var12);
                        }
                     }
                  }
               }
            } catch (Throwable var15) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var14) {
                     var15.addSuppressed(var14);
                  }
               }

               throw var15;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (Throwable var16) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var13) {
                  var16.addSuppressed(var13);
               }
            }

            throw var16;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var17) {
         this.B.getLogger().severe("Failed to load player data (SQL) for " + String.valueOf(var1));
         var17.printStackTrace();
      }

      if (var2.getName() == null) {
         OfflinePlayer var18 = Bukkit.getOfflinePlayer(var1);
         if (var18.getName() != null) {
            var2.setName(var18.getName());
         }
      }

      return var2;
   }

   public void A(PlayerData var1) {
      try {
         Connection var2 = this.A.G();

         try {
            String var3 = "INSERT INTO player_data (uuid, name, money, shards, shop_spent, last_seen_update, shard_booster_expiry, `keys`, `offline_payments`, `rtp_cooldown`, `pending_rtp_type`, `pending_rtp_target_server`, `pending_spawn_name`, `pending_spawn_world`, `pending_spawn_x`, `pending_spawn_y`, `pending_spawn_z`, `pending_spawn_yaw`, `pending_spawn_pitch`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name), money = VALUES(money), shards = VALUES(shards), shop_spent = VALUES(shop_spent), last_seen_update = VALUES(last_seen_update), shard_booster_expiry = VALUES(shard_booster_expiry), `keys` = VALUES(`keys`), `offline_payments` = VALUES(`offline_payments`), `rtp_cooldown` = VALUES(`rtp_cooldown`), `pending_rtp_type` = VALUES(`pending_rtp_type`), `pending_rtp_target_server` = VALUES(`pending_rtp_target_server`), `pending_rtp_target_server` = VALUES(`pending_rtp_target_server`), `pending_spawn_name` = VALUES(`pending_spawn_name`), `pending_spawn_world` = VALUES(`pending_spawn_world`), `pending_spawn_x` = VALUES(`pending_spawn_x`), `pending_spawn_y` = VALUES(`pending_spawn_y`), `pending_spawn_z` = VALUES(`pending_spawn_z`), `pending_spawn_yaw` = VALUES(`pending_spawn_yaw`), `pending_spawn_pitch` = VALUES(`pending_spawn_pitch`)";
            PreparedStatement var4 = var2.prepareStatement(var3);

            try {
               var4.setString(1, var1.getUuid().toString());
               var4.setString(2, var1.getName());
               var4.setDouble(3, var1.getMoney());
               var4.setDouble(4, var1.getShards());
               var4.setDouble(5, var1.getShopSpent());
               var4.setLong(6, var1.getLastSeenUpdate());
               var4.setLong(7, var1.getShardBoosterExpiry());
               Gson var5 = new Gson();
               var4.setString(8, var5.toJson((Object)var1.getKeys()));
               var4.setString(9, var5.toJson((Object)var1.getOfflinePayments()));
               var4.setLong(10, var1.getRtpCooldown());
               var4.setString(11, var1.getPendingRtpType());
               var4.setString(12, var1.getPendingRtpTargetServer());
               var4.setString(13, var1.getPendingSpawnName());
               var4.setString(14, var1.getPendingSpawnWorld());
               var4.setObject(15, var1.getPendingSpawnX());
               var4.setObject(16, var1.getPendingSpawnY());
               var4.setObject(17, var1.getPendingSpawnZ());
               var4.setObject(18, var1.getPendingSpawnYaw());
               var4.setObject(19, var1.getPendingSpawnPitch());
               var4.executeUpdate();
            } catch (Throwable var9) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (Throwable var10) {
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var7) {
                  var10.addSuppressed(var7);
               }
            }

            throw var10;
         }

         if (var2 != null) {
            var2.close();
         }
      } catch (SQLException var11) {
         this.B.getLogger().severe("Failed to save player data (SQL) for " + String.valueOf(var1.getUuid()));
         var11.printStackTrace();
      }

      if (this.A.B()) {
      }

   }

   public CompletableFuture<Void> B(PlayerData var1) {
      return CompletableFuture.runAsync(() -> this.A(var1));
   }

   public CompletableFuture<PlayerData> B(UUID var1) {
      return CompletableFuture.supplyAsync(() -> this.A(var1));
   }
}
