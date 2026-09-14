package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {
   private final PrismSurvival plugin;
   private Connection connection;

   public DatabaseManager(PrismSurvival var1) {
      this.plugin = var1;
      this.initializeDatabase();
   }

   private void initializeDatabase() {
      try {
         File var1 = new File(this.plugin.getDataFolder(), "survival/moderations/offend/database");
         if (!var1.exists()) {
            var1.mkdirs();
         }

         File var2 = new File(var1, "bans.db");
         Class.forName("org.sqlite.JDBC");
         this.connection = DriverManager.getConnection("jdbc:sqlite:" + var2.getAbsolutePath());
         this.createTables();
      } catch (Exception var3) {
         this.plugin.getLogger().log(Level.SEVERE, "Failed to initialize ban database", var3);
      }

   }

   private void createTables() {
      try {
         Statement var1 = this.connection.createStatement();

         try {
            String var2 = "CREATE TABLE IF NOT EXISTS bans (uuid VARCHAR(36) NOT NULL,player_name VARCHAR(16),ban_id VARCHAR(10),reason_key VARCHAR(50),display_reason TEXT,offense_count INT,date_banned BIGINT,expiry BIGINT,banned_by VARCHAR(16),PRIMARY KEY (uuid, reason_key))";
            var1.execute(var2);
            String var3 = "CREATE TABLE IF NOT EXISTS offenses (uuid VARCHAR(36) NOT NULL,reason_key VARCHAR(50) NOT NULL,count INT DEFAULT 0,PRIMARY KEY (uuid, reason_key))";
            var1.execute(var3);
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }
      } catch (SQLException var6) {
         var6.printStackTrace();
      }

   }

   private Connection getConnection() {
      try {
         if (this.connection == null || this.connection.isClosed()) {
            this.initializeDatabase();
         }
      } catch (SQLException var2) {
         this.initializeDatabase();
      }

      return this.connection;
   }

   public List<String> getBannedPlayerNames() {
      ArrayList var1 = new ArrayList();
      String var2 = "SELECT DISTINCT player_name FROM bans WHERE expiry = -1 OR expiry > ?";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         try {
            var3.setLong(1, System.currentTimeMillis());
            ResultSet var4 = var3.executeQuery();

            try {
               while(var4.next()) {
                  var1.add(var4.getString("player_name"));
               }
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
         var11.printStackTrace();
      }

      return var1;
   }

   public BanInfo getBanInfo(UUID var1) {
      String var2 = "SELECT * FROM bans WHERE uuid = ? AND (expiry = -1 OR expiry > ?) ORDER BY date_banned DESC LIMIT 1";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         BanInfo var5;
         label84: {
            try {
               var3.setString(1, var1.toString());
               var3.setLong(2, System.currentTimeMillis());
               ResultSet var4 = var3.executeQuery();

               label78: {
                  try {
                     if (!var4.next()) {
                        break label78;
                     }

                     var5 = this.mapToBanInfo(var4);
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
                  break label84;
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

            return null;
         }

         if (var3 != null) {
            var3.close();
         }

         return var5;
      } catch (SQLException var11) {
         var11.printStackTrace();
         return null;
      }
   }

   public BanInfo getBanInfoByName(String var1) {
      String var2 = "SELECT * FROM bans WHERE player_name LIKE ? AND (expiry = -1 OR expiry > ?) ORDER BY date_banned DESC LIMIT 1";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         BanInfo var5;
         label84: {
            try {
               var3.setString(1, var1);
               var3.setLong(2, System.currentTimeMillis());
               ResultSet var4 = var3.executeQuery();

               label78: {
                  try {
                     if (!var4.next()) {
                        break label78;
                     }

                     var5 = this.mapToBanInfo(var4);
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
                  break label84;
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

            return null;
         }

         if (var3 != null) {
            var3.close();
         }

         return var5;
      } catch (SQLException var11) {
         var11.printStackTrace();
         return null;
      }
   }

   public BanInfo getBanInfoById(String var1) {
      String var2 = "SELECT * FROM bans WHERE ban_id = ?";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         BanInfo var5;
         label84: {
            try {
               var3.setString(1, var1);
               ResultSet var4 = var3.executeQuery();

               label78: {
                  try {
                     if (!var4.next()) {
                        break label78;
                     }

                     var5 = this.mapToBanInfo(var4);
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
                  break label84;
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

            return null;
         }

         if (var3 != null) {
            var3.close();
         }

         return var5;
      } catch (SQLException var11) {
         var11.printStackTrace();
         return null;
      }
   }

   public void removeBan(String var1) {
      String var2 = "DELETE FROM bans WHERE player_name = ?";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         try {
            var3.setString(1, var1);
            var3.executeUpdate();
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var8) {
         var8.printStackTrace();
      }

   }

   public void removeBan(UUID var1) {
      String var2 = "DELETE FROM bans WHERE uuid = ?";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         try {
            var3.setString(1, var1.toString());
            var3.executeUpdate();
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var8) {
         var8.printStackTrace();
      }

   }

   public void removeBanById(String var1) {
      String var2 = "DELETE FROM bans WHERE ban_id = ?";

      try {
         PreparedStatement var3 = this.getConnection().prepareStatement(var2);

         try {
            var3.setString(1, var1);
            var3.executeUpdate();
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var8) {
         var8.printStackTrace();
      }

   }

   public int getOffenseCount(UUID var1, String var2) {
      String var3 = "SELECT count FROM offenses WHERE uuid = ? AND reason_key = ?";

      try {
         PreparedStatement var4 = this.getConnection().prepareStatement(var3);

         int var6;
         label84: {
            try {
               var4.setString(1, var1.toString());
               var4.setString(2, var2);
               ResultSet var5 = var4.executeQuery();

               label78: {
                  try {
                     if (!var5.next()) {
                        break label78;
                     }

                     var6 = var5.getInt("count");
                  } catch (Throwable var10) {
                     if (var5 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var9) {
                           var10.addSuppressed(var9);
                        }
                     }

                     throw var10;
                  }

                  if (var5 != null) {
                     var5.close();
                  }
                  break label84;
               }

               if (var5 != null) {
                  var5.close();
               }
            } catch (Throwable var11) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var8) {
                     var11.addSuppressed(var8);
                  }
               }

               throw var11;
            }

            if (var4 != null) {
               var4.close();
            }

            return 0;
         }

         if (var4 != null) {
            var4.close();
         }

         return var6;
      } catch (SQLException var12) {
         var12.printStackTrace();
         return 0;
      }
   }

   public void setOffenseCount(UUID var1, String var2, int var3) {
      String var4 = "INSERT OR REPLACE INTO offenses (uuid, reason_key, count) VALUES (?, ?, ?)";

      try {
         PreparedStatement var5 = this.getConnection().prepareStatement(var4);

         try {
            var5.setString(1, var1.toString());
            var5.setString(2, var2);
            var5.setInt(3, var3);
            var5.executeUpdate();
         } catch (Throwable var9) {
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var8) {
                  var9.addSuppressed(var8);
               }
            }

            throw var9;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (SQLException var10) {
         var10.printStackTrace();
      }

   }

   public void resetOffenseCount(UUID var1, String var2) {
      this.setOffenseCount(var1, var2, 1);
      this.setOffenseCount(var1, var2, 0);
   }

   public void resetOffenseCount(String var1, String var2) {
      try {
         this.resetOffenseCount(UUID.fromString(var1), var2);
      } catch (IllegalArgumentException var4) {
      }

   }

   public void addBan(UUID var1, String var2, String var3, String var4, String var5, int var6, long var7, long var9, String var11) {
      String var12 = "REPLACE INTO bans (uuid, player_name, ban_id, reason_key, display_reason, offense_count, date_banned, expiry, banned_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

      try {
         PreparedStatement var13 = this.getConnection().prepareStatement(var12);

         try {
            var13.setString(1, var1.toString());
            var13.setString(2, var2);
            var13.setString(3, var3);
            var13.setString(4, var4);
            var13.setString(5, var5);
            var13.setInt(6, var6);
            var13.setLong(7, var7);
            var13.setLong(8, var9);
            var13.setString(9, var11);
            var13.executeUpdate();
         } catch (Throwable var17) {
            if (var13 != null) {
               try {
                  var13.close();
               } catch (Throwable var16) {
                  var17.addSuppressed(var16);
               }
            }

            throw var17;
         }

         if (var13 != null) {
            var13.close();
         }
      } catch (SQLException var18) {
         var18.printStackTrace();
      }

   }

   public boolean isBanned(UUID var1) {
      return this.getBanInfo(var1) != null;
   }

   private BanInfo mapToBanInfo(ResultSet var1) throws SQLException {
      BanInfo var2 = new BanInfo();
      var2.uuid = var1.getString("uuid");
      var2.playerName = var1.getString("player_name");
      var2.id = var1.getString("ban_id");
      var2.reasonKey = var1.getString("reason_key");
      var2.reason = var1.getString("display_reason");
      var2.count = var1.getInt("offense_count");
      var2.date = var1.getLong("date_banned");
      var2.expire = var1.getLong("expiry");
      var2.bannedBy = var1.getString("banned_by");
      return var2;
   }

   public static class BanInfo {
      public String uuid;
      public String playerName;
      public String id;
      public String reasonKey;
      public String reason;
      public int count;
      public long date;
      public long expire;
      public String bannedBy;
   }
}
