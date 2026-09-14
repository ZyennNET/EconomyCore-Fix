package com.h2ph.T;

import com.google.gson.Gson;
import com.h2ph.PrismSurvival;
import com.h2ph.T.A.J;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class B {
   private final PrismSurvival D;
   private HikariDataSource H;
   private JedisPool B;
   private FileConfiguration C;
   private File G;
   private boolean A;
   private boolean F;
   private J E;

   public J E() {
      return this.E;
   }

   public B(PrismSurvival var1) {
      this.D = var1;
      this.H();
      this.D();
   }

   private void H() {
      this.G = new File(this.D.getDataFolder(), "survival/database.yml");
      if (!this.G.exists()) {
         try {
            this.D.saveResource("survival/database.yml", false);
         } catch (Exception var2) {
            this.D.getLogger().severe("Could not create database.yml! " + var2.getMessage());
         }
      }

      this.C = YamlConfiguration.loadConfiguration(this.G);
      this.A = this.C.getBoolean("mysql.enabled", false);
      this.F = this.C.getBoolean("redis.enabled", false);
   }

   private void D() {
      if (this.A) {
         try {
            HikariConfig var1 = new HikariConfig();
            String var2 = this.C.getString("mysql.host");
            int var3 = this.C.getInt("mysql.port");
            String var4 = this.C.getString("mysql.database");
            boolean var5 = this.C.getBoolean("mysql.ssl");
            var1.setJdbcUrl("jdbc:mysql://" + var2 + ":" + var3 + "/" + var4 + "?useSSL=" + var5 + "&autoReconnect=true");
            var1.setUsername(this.C.getString("mysql.username"));
            var1.setPassword(this.C.getString("mysql.password"));
            var1.setMaximumPoolSize(this.C.getInt("mysql.pool.maximum-pool-size", 10));
            this.H = new HikariDataSource(var1);
            this.D.getLogger().info("Connected to MySQL!");
            this.E = new J(this.D, this);
            this.A();
         } catch (Exception var9) {
            this.D.getLogger().log(Level.SEVERE, "Failed to connect to MySQL", var9);
            this.A = false;
         }
      }

      if (this.F) {
         try {
            String var12 = this.C.getString("redis.host");
            int var13 = this.C.getInt("redis.port");
            String var14 = this.C.getString("redis.password");
            JedisPoolConfig var15 = new JedisPoolConfig();
            var15.setMaxTotal(16);
            if (var14 != null && !var14.isEmpty()) {
               this.B = new JedisPool(var15, var12, var13, 2000, var14);
            } else {
               this.B = new JedisPool(var15, var12, var13);
            }

            Jedis var16 = this.B.getResource();

            try {
               var16.ping();
               this.D.getLogger().info("Connected to Redis!");
            } catch (Throwable var10) {
               if (var16 != null) {
                  try {
                     var16.close();
                  } catch (Throwable var8) {
                     var10.addSuppressed(var8);
                  }
               }

               throw var10;
            }

            if (var16 != null) {
               var16.close();
            }
         } catch (Exception var11) {
            this.D.getLogger().log(Level.SEVERE, "Failed to connect to Redis", var11);
            this.F = false;
         }
      }

   }

   private void A() {
      if (this.A) {
         try {
            Connection var1 = this.G();

            try {
               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `keys` MEDIUMTEXT");
               } catch (SQLException var26) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `offline_payments` MEDIUMTEXT");
               } catch (SQLException var25) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_rtp_type` VARCHAR(32)");
               } catch (SQLException var24) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_rtp_target_server` VARCHAR(64)");
               } catch (SQLException var23) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_name` VARCHAR(64)");
               } catch (SQLException var22) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_world` VARCHAR(64)");
               } catch (SQLException var21) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_x` DOUBLE");
               } catch (SQLException var20) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_y` DOUBLE");
               } catch (SQLException var19) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_z` DOUBLE");
               } catch (SQLException var18) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_yaw` FLOAT");
               } catch (SQLException var17) {
               }

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE player_data ADD COLUMN `pending_spawn_pitch` FLOAT");
               } catch (SQLException var16) {
               }

               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS rtp_locations (id INT AUTO_INCREMENT PRIMARY KEY,region VARCHAR(32),world VARCHAR(64),x INT,y INT,z INT,created_at BIGINT,INDEX (region))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS player_data (uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(16), money DOUBLE DEFAULT 0, shards DOUBLE DEFAULT 0, shop_spent DOUBLE DEFAULT 0, last_seen_update BIGINT DEFAULT 0, shard_booster_expiry BIGINT DEFAULT 0,`keys` MEDIUMTEXT,`offline_payments` MEDIUMTEXT,`rtp_cooldown` BIGINT DEFAULT 0,`pending_rtp_type` VARCHAR(32),`pending_rtp_target_server` VARCHAR(64));");
               ResultSet var2 = var1.getMetaData().getTables((String)null, (String)null, "player_keys", (String[])null);

               try {
                  if (var2.next()) {
                     this.D.getLogger().info("Migrating legacy player_keys table to player_data JSON...");
                     HashMap var3 = new HashMap();
                     Statement var4 = var1.createStatement();

                     try {
                        ResultSet var5 = var4.executeQuery("SELECT * FROM player_keys");

                        try {
                           while(var5.next()) {
                              String var6 = var5.getString("uuid");
                              String var7 = var5.getString("key_type");
                              int var8 = var5.getInt("amount");
                              ((Map)var3.computeIfAbsent(var6, (var0) -> new HashMap())).put(var7, var8);
                           }
                        } catch (Throwable var28) {
                           if (var5 != null) {
                              try {
                                 var5.close();
                              } catch (Throwable var13) {
                                 var28.addSuppressed(var13);
                              }
                           }

                           throw var28;
                        }

                        if (var5 != null) {
                           var5.close();
                        }
                     } catch (Throwable var29) {
                        if (var4 != null) {
                           try {
                              var4.close();
                           } catch (Throwable var12) {
                              var29.addSuppressed(var12);
                           }
                        }

                        throw var29;
                     }

                     if (var4 != null) {
                        var4.close();
                     }

                     Gson var33 = new Gson();
                     PreparedStatement var34 = var1.prepareStatement("UPDATE player_data SET `keys` = ? WHERE uuid = ?");

                     try {
                        for(Map.Entry var36 : var3.entrySet()) {
                           var34.setString(1, var33.toJson(var36.getValue()));
                           var34.setString(2, (String)var36.getKey());
                           var34.addBatch();
                        }

                        var34.executeBatch();
                     } catch (Throwable var27) {
                        if (var34 != null) {
                           try {
                              var34.close();
                           } catch (Throwable var11) {
                              var27.addSuppressed(var11);
                           }
                        }

                        throw var27;
                     }

                     if (var34 != null) {
                        var34.close();
                     }

                     var1.createStatement().executeUpdate("DROP TABLE player_keys");
                     this.D.getLogger().info("Migration of player_keys complete!");
                  }
               } catch (Throwable var30) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var10) {
                        var30.addSuppressed(var10);
                     }
                  }

                  throw var30;
               }

               if (var2 != null) {
                  var2.close();
               }

               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS duel_stats (uuid VARCHAR(36) PRIMARY KEY, wins INT DEFAULT 0, losses INT DEFAULT 0, streak INT DEFAULT 0, FOREIGN KEY (uuid) REFERENCES player_data(uuid) ON DELETE CASCADE);");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS bans (uuid VARCHAR(36) NOT NULL,player_name VARCHAR(16),ban_id VARCHAR(10),reason_key VARCHAR(50),display_reason TEXT,offense_count INT,date_banned BIGINT,expiry BIGINT,banned_by VARCHAR(16),PRIMARY KEY (uuid, reason_key))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS offenses (uuid VARCHAR(36) NOT NULL,reason_key VARCHAR(50) NOT NULL,count INT DEFAULT 0,PRIMARY KEY (uuid, reason_key))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS auctions (id VARCHAR(36) PRIMARY KEY,seller_uuid VARCHAR(36),seller_name VARCHAR(16),item_base64 MEDIUMTEXT,price DOUBLE,listed_at BIGINT,duration INT)");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS auction_pending_sales (seller_uuid VARCHAR(36),buyer_name VARCHAR(16),item_name VARCHAR(255),price DOUBLE,INDEX (seller_uuid))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS auction_preferences (uuid VARCHAR(36) PRIMARY KEY,sort_mode VARCHAR(32))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS auction_transactions (id INT AUTO_INCREMENT PRIMARY KEY,player_uuid VARCHAR(36),item_base64 MEDIUMTEXT,price DOUBLE,buyer VARCHAR(16),seller VARCHAR(16),timestamp BIGINT,is_sale BOOLEAN,INDEX (player_uuid),INDEX (timestamp))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS server_settings (key_name VARCHAR(64) PRIMARY KEY,value_json MEDIUMTEXT)");

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE afk_regions ADD COLUMN `spawn_x` DOUBLE");
                  var1.createStatement().executeUpdate("ALTER TABLE afk_regions ADD COLUMN `spawn_y` DOUBLE");
                  var1.createStatement().executeUpdate("ALTER TABLE afk_regions ADD COLUMN `spawn_z` DOUBLE");
                  var1.createStatement().executeUpdate("ALTER TABLE afk_regions ADD COLUMN `spawn_yaw` FLOAT");
                  var1.createStatement().executeUpdate("ALTER TABLE afk_regions ADD COLUMN `spawn_pitch` FLOAT");
               } catch (SQLException var15) {
               }

               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS afk_regions (name VARCHAR(64) PRIMARY KEY,world VARCHAR(64),min_x DOUBLE, min_y DOUBLE, min_z DOUBLE,max_x DOUBLE, max_y DOUBLE, max_z DOUBLE,spawn_x DOUBLE, spawn_y DOUBLE, spawn_z DOUBLE,spawn_yaw FLOAT, spawn_pitch FLOAT)");

               try {
                  var1.createStatement().executeUpdate("ALTER TABLE spawn_locations ADD COLUMN `region` VARCHAR(32)");
               } catch (SQLException var14) {
               }

               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS spawn_locations (name VARCHAR(64) PRIMARY KEY,world VARCHAR(64),x DOUBLE, y DOUBLE, z DOUBLE,yaw FLOAT, pitch FLOAT,region VARCHAR(32))");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS duel_arenas (name VARCHAR(64) PRIMARY KEY,data_json MEDIUMTEXT)");
               var1.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS bounties (target_uuid VARCHAR(36) PRIMARY KEY,target_name VARCHAR(16),amount DOUBLE,last_updated BIGINT)");
            } catch (Throwable var31) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var9) {
                     var31.addSuppressed(var9);
                  }
               }

               throw var31;
            }

            if (var1 != null) {
               var1.close();
            }
         } catch (SQLException var32) {
            this.D.getLogger().log(Level.SEVERE, "Failed to initialize tables", var32);
         }

      }
   }

   public Connection G() throws SQLException {
      if (this.H == null) {
         throw new SQLException("MySQL is not configured or failed to connect.");
      } else {
         return this.H.getConnection();
      }
   }

   public Jedis I() {
      return this.B == null ? null : this.B.getResource();
   }

   public void C() {
      if (this.H != null) {
         this.H.close();
      }

      if (this.B != null) {
         this.B.close();
      }

   }

   public String B(String var1) {
      if (!this.A) {
         return null;
      } else {
         try {
            Connection var2 = this.G();

            String var5;
            label117: {
               try {
                  PreparedStatement var3;
                  label108: {
                     var3 = var2.prepareStatement("SELECT value_json FROM server_settings WHERE key_name = ?");

                     try {
                        var3.setString(1, var1);
                        ResultSet var4 = var3.executeQuery();

                        label87: {
                           try {
                              if (var4.next()) {
                                 var5 = var4.getString("value_json");
                                 break label87;
                              }
                           } catch (Throwable var10) {
                              if (var4 != null) {
                                 try {
                                    var4.close();
                                 } catch (Throwable var9) {
                                    var10.addSuppressed(var9);
                                 }
                              }

                              throw var10;
                           }

                           if (var4 != null) {
                              var4.close();
                           }
                           break label108;
                        }

                        if (var4 != null) {
                           var4.close();
                        }
                     } catch (Throwable var11) {
                        if (var3 != null) {
                           try {
                              var3.close();
                           } catch (Throwable var8) {
                              var11.addSuppressed(var8);
                           }
                        }

                        throw var11;
                     }

                     if (var3 != null) {
                        var3.close();
                     }
                     break label117;
                  }

                  if (var3 != null) {
                     var3.close();
                  }
               } catch (Throwable var12) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var7) {
                        var12.addSuppressed(var7);
                     }
                  }

                  throw var12;
               }

               if (var2 != null) {
                  var2.close();
               }

               return null;
            }

            if (var2 != null) {
               var2.close();
            }

            return var5;
         } catch (SQLException var13) {
            this.D.getLogger().log(Level.SEVERE, "Failed to get server setting: " + var1, var13);
            return null;
         }
      }
   }

   public void A(String var1, String var2) {
      if (this.A) {
         try {
            Connection var3 = this.G();

            try {
               PreparedStatement var4 = var3.prepareStatement("INSERT INTO server_settings (key_name, value_json) VALUES (?, ?) ON DUPLICATE KEY UPDATE value_json = VALUES(value_json)");

               try {
                  var4.setString(1, var1);
                  var4.setString(2, var2);
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
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (SQLException var11) {
            this.D.getLogger().log(Level.SEVERE, "Failed to set server setting: " + var1, var11);
         }

      }
   }

   public boolean F() {
      return this.A;
   }

   public boolean B() {
      return this.F;
   }
}
