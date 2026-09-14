package com.prismcore.survival.auction;

import com.h2ph.T.A.B;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
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

public class AuctionManager {
   private final AuctionController controller;
   private final B auctionDAO;
   private final Map<UUID, String> sortPreferences;
   private final List<AuctionItem> items;
   private final int defaultTime;
   private final Map<UUID, List<OfflineSale>> pendingSales;
   private final File auctionFile;
   private FileConfiguration auctionConfig;
   private Connection sqliteConnection;
   private boolean useSQLite;
   private long lastYamLLogTime = 0L;
   private static final long LOG_INTERVAL_MS = 60000L;

   public AuctionManager(AuctionController var1, B var2) {
      this.controller = var1;
      this.auctionDAO = var2;
      this.items = new ArrayList();
      this.sortPreferences = new ConcurrentHashMap();
      this.pendingSales = new ConcurrentHashMap();
      this.defaultTime = var1.getConfig().getInt("settings.item-time");
      this.auctionFile = new File(var1.getPlugin().getDataFolder(), "auctions.yml");
      this.useSQLite = !this.controller.getPlugin().getDatabaseManager().F() && "sqlite".equalsIgnoreCase(var1.getConfig().getString("settings.storage-type", "yaml"));
      if (this.useSQLite) {
         this.initSQLite();
      }

      this.loadFromConfig();
   }

   public void addItem(AuctionItem var1) {
      this.items.add(var1);
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.auctionDAO.A(var1);
      } else if (this.useSQLite) {
         this.saveItemToSQLite(var1);
      } else {
         this.saveToYaml();
      }

   }

   public void removeItem(AuctionItem var1) {
      this.items.remove(var1);
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.auctionDAO.C(var1.getId());
      } else if (this.useSQLite) {
         this.removeItemFromSQLite(var1.getId());
      } else {
         this.saveToYaml();
      }

   }

   public boolean isExpired(AuctionItem var1) {
      long var2 = System.currentTimeMillis();
      return var2 - var1.getListedAt() >= (long)var1.getDuration() * 1000L;
   }

   public List<AuctionItem> getItems() {
      return Collections.unmodifiableList(this.items);
   }

   public List<AuctionItem> getActiveItems() {
      long var1 = System.currentTimeMillis();
      ArrayList var3 = new ArrayList();

      for(AuctionItem var5 : this.items) {
         if (var1 - var5.getListedAt() < (long)var5.getDuration() * 1000L) {
            var3.add(var5);
         }
      }

      return Collections.unmodifiableList(var3);
   }

   public void addPendingSale(UUID var1, String var2, String var3, double var4) {
      ((List)this.pendingSales.computeIfAbsent(var1, (var0) -> new ArrayList())).add(new OfflineSale(var2, var3, var4));
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.auctionDAO.A(var1, var2, var3, var4);
      } else if (this.useSQLite) {
         this.addPendingSaleToSQLite(var1, var2, var3, var4);
      } else {
         this.saveToYaml();
      }

   }

   public List<OfflineSale> getPendingSales(UUID var1) {
      return (List)this.pendingSales.getOrDefault(var1, Collections.emptyList());
   }

   public void clearPendingSales(UUID var1) {
      this.pendingSales.remove(var1);
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.auctionDAO.E(var1);
      } else if (this.useSQLite) {
         this.clearPendingSalesFromSQLite(var1);
      } else {
         this.saveToYaml();
      }

   }

   public void updatePrice(UUID var1, double var2) {
      for(AuctionItem var5 : this.items) {
         if (var5.getId().equals(var1)) {
            var5.setPrice(var2);
            if (this.controller.getPlugin().getDatabaseManager().F()) {
               this.auctionDAO.A(var1, var2);
            } else if (this.useSQLite) {
               this.updatePriceInSQLite(var1, var2);
            } else {
               this.saveToYaml();
            }

            return;
         }
      }

   }

   public void removeAllItems(String var1) {
      ArrayList var2 = new ArrayList();

      for(AuctionItem var4 : this.items) {
         if (var4.getSeller().equalsIgnoreCase(var1)) {
            var2.add(var4);
         }
      }

      for(AuctionItem var6 : var2) {
         this.removeItem(var6);
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

   private void saveToYaml() {
      if (this.auctionConfig == null) {
         this.auctionConfig = new YamlConfiguration();
      }

      this.auctionConfig.set("auctions", (Object)null);

      for(AuctionItem var2 : this.items) {
         String var3 = "auctions." + var2.getId().toString();
         this.auctionConfig.set(var3 + ".seller", var2.getSeller());
         String var4 = this.itemStackToBase64(var2.getItemStack());
         this.auctionConfig.set(var3 + ".item", var4);
         this.auctionConfig.set(var3 + ".price", var2.getPrice());
         this.auctionConfig.set(var3 + ".listed_at", var2.getListedAt());
         this.auctionConfig.set(var3 + ".duration", var2.getDuration());
      }

      this.auctionConfig.set("pending_sales", (Object)null);

      for(Map.Entry var11 : this.pendingSales.entrySet()) {
         String var13 = "pending_sales." + ((UUID)var11.getKey()).toString();
         ArrayList var14 = new ArrayList();

         for(OfflineSale var6 : (List)var11.getValue()) {
            HashMap var7 = new HashMap();
            var7.put("buyer", var6.buyer);
            var7.put("item", var6.item);
            var7.put("price", var6.price);
            var14.add(var7);
         }

         this.auctionConfig.set(var13, var14);
      }

      this.auctionConfig.set("sort_preferences", (Object)null);

      for(Map.Entry var12 : this.sortPreferences.entrySet()) {
         this.auctionConfig.set("sort_preferences." + ((UUID)var12.getKey()).toString(), var12.getValue());
      }

      try {
         this.auctionConfig.save(this.auctionFile);
      } catch (Exception var8) {
         this.controller.getPlugin().getLogger().warning("Failed to save auctions.yml: " + var8.getMessage());
      }

   }

   private void initSQLite() {
      try {
         String var1 = this.controller.getConfig().getString("settings.sqlite-file", "economy/auction/auctions.db");
         File var2 = new File(this.controller.getPlugin().getDataFolder(), var1);
         if (var2.getParentFile() != null && !var2.getParentFile().exists()) {
            var2.getParentFile().mkdirs();
         }

         Class.forName("org.sqlite.JDBC");
         this.sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + var2.getAbsolutePath());
         this.createSQLiteTables();
         this.controller.getPlugin().getLogger().info("SQLite auction storage initialized at " + var2.getPath());
      } catch (ClassNotFoundException var3) {
         this.controller.getPlugin().getLogger().severe("SQLite JDBC driver not found on classpath (org.sqlite.JDBC). Add the sqlite-jdbc dependency to your plugin build. Falling back to YAML storage.");
         this.useSQLite = false;
      } catch (Exception var4) {
         this.controller.getPlugin().getLogger().severe("Failed to initialize SQLite storage: " + var4.getMessage() + ". Falling back to YAML storage.");
         this.useSQLite = false;
      }

   }

   private void createSQLiteTables() throws SQLException {
      Statement var1 = this.sqliteConnection.createStatement();

      try {
         var1.executeUpdate("CREATE TABLE IF NOT EXISTS auctions (id TEXT PRIMARY KEY, seller TEXT, item TEXT, price REAL, listed_at INTEGER, duration INTEGER)");
         var1.executeUpdate("CREATE TABLE IF NOT EXISTS pending_sales (id INTEGER PRIMARY KEY AUTOINCREMENT, seller TEXT, buyer TEXT, item TEXT, price REAL)");
         var1.executeUpdate("CREATE TABLE IF NOT EXISTS sort_preferences (player_uuid TEXT PRIMARY KEY, mode TEXT)");
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

   }

   private void saveItemToSQLite(AuctionItem var1) {
      if (this.sqliteConnection != null) {
         String var2 = "INSERT OR REPLACE INTO auctions (id, seller, item, price, listed_at, duration) VALUES (?, ?, ?, ?, ?, ?)";

         try {
            PreparedStatement var3 = this.sqliteConnection.prepareStatement(var2);

            try {
               var3.setString(1, var1.getId().toString());
               var3.setString(2, var1.getSeller());
               var3.setString(3, this.itemStackToBase64(var1.getItemStack()));
               var3.setDouble(4, var1.getPrice());
               var3.setLong(5, var1.getListedAt());
               var3.setInt(6, var1.getDuration());
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
            this.controller.getPlugin().getLogger().warning("Failed to save auction to SQLite: " + var8.getMessage());
         }

      }
   }

   private void removeItemFromSQLite(UUID var1) {
      if (this.sqliteConnection != null) {
         try {
            PreparedStatement var2 = this.sqliteConnection.prepareStatement("DELETE FROM auctions WHERE id = ?");

            try {
               var2.setString(1, var1.toString());
               var2.executeUpdate();
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (SQLException var7) {
            this.controller.getPlugin().getLogger().warning("Failed to remove auction from SQLite: " + var7.getMessage());
         }

      }
   }

   private void updatePriceInSQLite(UUID var1, double var2) {
      if (this.sqliteConnection != null) {
         try {
            PreparedStatement var4 = this.sqliteConnection.prepareStatement("UPDATE auctions SET price = ? WHERE id = ?");

            try {
               var4.setDouble(1, var2);
               var4.setString(2, var1.toString());
               var4.executeUpdate();
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (SQLException var9) {
            this.controller.getPlugin().getLogger().warning("Failed to update auction price in SQLite: " + var9.getMessage());
         }

      }
   }

   private void addPendingSaleToSQLite(UUID var1, String var2, String var3, double var4) {
      if (this.sqliteConnection != null) {
         String var6 = "INSERT INTO pending_sales (seller, buyer, item, price) VALUES (?, ?, ?, ?)";

         try {
            PreparedStatement var7 = this.sqliteConnection.prepareStatement(var6);

            try {
               var7.setString(1, var1.toString());
               var7.setString(2, var2);
               var7.setString(3, var3);
               var7.setDouble(4, var4);
               var7.executeUpdate();
            } catch (Throwable var11) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (var7 != null) {
               var7.close();
            }
         } catch (SQLException var12) {
            this.controller.getPlugin().getLogger().warning("Failed to save pending sale to SQLite: " + var12.getMessage());
         }

      }
   }

   private void clearPendingSalesFromSQLite(UUID var1) {
      if (this.sqliteConnection != null) {
         try {
            PreparedStatement var2 = this.sqliteConnection.prepareStatement("DELETE FROM pending_sales WHERE seller = ?");

            try {
               var2.setString(1, var1.toString());
               var2.executeUpdate();
            } catch (Throwable var6) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (SQLException var7) {
            this.controller.getPlugin().getLogger().warning("Failed to clear pending sales in SQLite: " + var7.getMessage());
         }

      }
   }

   private void setPlayerSortInSQLite(UUID var1, String var2) {
      if (this.sqliteConnection != null) {
         String var3 = "INSERT OR REPLACE INTO sort_preferences (player_uuid, mode) VALUES (?, ?)";

         try {
            PreparedStatement var4 = this.sqliteConnection.prepareStatement(var3);

            try {
               var4.setString(1, var1.toString());
               var4.setString(2, var2);
               var4.executeUpdate();
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (SQLException var9) {
            this.controller.getPlugin().getLogger().warning("Failed to save sort preference to SQLite: " + var9.getMessage());
         }

      }
   }

   private String getPlayerSortFromSQLite(UUID var1, String var2) {
      if (this.sqliteConnection == null) {
         return var2;
      } else {
         String var3 = "SELECT mode FROM sort_preferences WHERE player_uuid = ?";

         try {
            PreparedStatement var4 = this.sqliteConnection.prepareStatement(var3);

            String var6;
            label89: {
               try {
                  var4.setString(1, var1.toString());
                  ResultSet var5 = var4.executeQuery();

                  label82: {
                     try {
                        if (!var5.next()) {
                           break label82;
                        }

                        var6 = var5.getString("mode");
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
                     break label89;
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

               return var2;
            }

            if (var4 != null) {
               var4.close();
            }

            return var6;
         } catch (SQLException var12) {
            this.controller.getPlugin().getLogger().warning("Failed to load sort preference from SQLite: " + var12.getMessage());
            return var2;
         }
      }
   }

   private void loadFromSQLite() {
      if (this.sqliteConnection != null) {
         this.items.clear();
         this.pendingSales.clear();
         this.sortPreferences.clear();

         try {
            Statement var1 = this.sqliteConnection.createStatement();

            try {
               ResultSet var2 = var1.executeQuery("SELECT id, seller, item, price, listed_at, duration FROM auctions");

               try {
                  while(var2.next()) {
                     try {
                        UUID var3 = UUID.fromString(var2.getString("id"));
                        String var4 = var2.getString("seller");
                        ItemStack var5 = this.itemStackFromBase64(var2.getString("item"));
                        if (var5 != null) {
                           double var6 = var2.getDouble("price");
                           long var8 = var2.getLong("listed_at");
                           int var10 = var2.getInt("duration");
                           this.items.add(new AuctionItem(var3, var4, var5, var6, var8, var10));
                        }
                     } catch (Exception var19) {
                        this.controller.getPlugin().getLogger().warning("Failed to load auction from SQLite row: " + var19.getMessage());
                     }
                  }
               } catch (Throwable var26) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var18) {
                        var26.addSuppressed(var18);
                     }
                  }

                  throw var26;
               }

               if (var2 != null) {
                  var2.close();
               }
            } catch (Throwable var27) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var17) {
                     var27.addSuppressed(var17);
                  }
               }

               throw var27;
            }

            if (var1 != null) {
               var1.close();
            }
         } catch (SQLException var28) {
            this.controller.getPlugin().getLogger().warning("Failed to load auctions from SQLite: " + var28.getMessage());
         }

         try {
            Statement var29 = this.sqliteConnection.createStatement();

            try {
               ResultSet var32 = var29.executeQuery("SELECT seller, buyer, item, price FROM pending_sales");

               try {
                  while(var32.next()) {
                     try {
                        UUID var34 = UUID.fromString(var32.getString("seller"));
                        String var36 = var32.getString("buyer");
                        String var37 = var32.getString("item");
                        double var38 = var32.getDouble("price");
                        ((List)this.pendingSales.computeIfAbsent(var34, (var0) -> new ArrayList())).add(new OfflineSale(var36, var37, var38));
                     } catch (Exception var16) {
                        this.controller.getPlugin().getLogger().warning("Failed to load pending sale from SQLite row: " + var16.getMessage());
                     }
                  }
               } catch (Throwable var23) {
                  if (var32 != null) {
                     try {
                        var32.close();
                     } catch (Throwable var15) {
                        var23.addSuppressed(var15);
                     }
                  }

                  throw var23;
               }

               if (var32 != null) {
                  var32.close();
               }
            } catch (Throwable var24) {
               if (var29 != null) {
                  try {
                     var29.close();
                  } catch (Throwable var14) {
                     var24.addSuppressed(var14);
                  }
               }

               throw var24;
            }

            if (var29 != null) {
               var29.close();
            }
         } catch (SQLException var25) {
            this.controller.getPlugin().getLogger().warning("Failed to load pending sales from SQLite: " + var25.getMessage());
         }

         try {
            Statement var30 = this.sqliteConnection.createStatement();

            try {
               ResultSet var33 = var30.executeQuery("SELECT player_uuid, mode FROM sort_preferences");

               try {
                  while(var33.next()) {
                     try {
                        UUID var35 = UUID.fromString(var33.getString("player_uuid"));
                        this.sortPreferences.put(var35, var33.getString("mode"));
                     } catch (Exception var13) {
                        this.controller.getPlugin().getLogger().warning("Failed to load sort preference from SQLite row: " + var13.getMessage());
                     }
                  }
               } catch (Throwable var20) {
                  if (var33 != null) {
                     try {
                        var33.close();
                     } catch (Throwable var12) {
                        var20.addSuppressed(var12);
                     }
                  }

                  throw var20;
               }

               if (var33 != null) {
                  var33.close();
               }
            } catch (Throwable var21) {
               if (var30 != null) {
                  try {
                     var30.close();
                  } catch (Throwable var11) {
                     var21.addSuppressed(var11);
                  }
               }

               throw var21;
            }

            if (var30 != null) {
               var30.close();
            }
         } catch (SQLException var22) {
            this.controller.getPlugin().getLogger().warning("Failed to load sort preferences from SQLite: " + var22.getMessage());
         }

         long var31 = System.currentTimeMillis();
         if (var31 - this.lastYamLLogTime >= 60000L) {
            this.controller.getPlugin().getLogger().info("Loaded " + this.items.size() + " auctions from SQLite.");
            this.lastYamLLogTime = var31;
         }

      }
   }

   private void loadFromYaml() {
      if (this.auctionFile.exists()) {
         this.auctionConfig = YamlConfiguration.loadConfiguration(this.auctionFile);
         this.items.clear();
         this.pendingSales.clear();
         this.sortPreferences.clear();
         if (this.auctionConfig.contains("auctions")) {
            for(String var2 : this.auctionConfig.getConfigurationSection("auctions").getKeys(false)) {
               try {
                  String var3 = "auctions." + var2;
                  UUID var4 = UUID.fromString(var2);
                  String var5 = this.auctionConfig.getString(var3 + ".seller");
                  String var6 = this.auctionConfig.getString(var3 + ".item");
                  ItemStack var7 = this.itemStackFromBase64(var6);
                  if (var7 != null) {
                     double var8 = this.auctionConfig.getDouble(var3 + ".price");
                     long var10 = this.auctionConfig.getLong(var3 + ".listed_at");
                     int var12 = this.auctionConfig.getInt(var3 + ".duration");
                     AuctionItem var13 = new AuctionItem(var4, var5, var7, var8, var10, var12);
                     this.items.add(var13);
                  }
               } catch (Exception var14) {
                  this.controller.getPlugin().getLogger().warning("Failed to load auction " + var2 + ": " + var14.getMessage());
               }
            }
         }

         if (this.auctionConfig.contains("pending_sales")) {
            for(String var18 : this.auctionConfig.getConfigurationSection("pending_sales").getKeys(false)) {
               UUID var20 = UUID.fromString(var18);
               List var22 = this.auctionConfig.getList("pending_sales." + var18);
               if (var22 != null) {
                  ArrayList var24 = new ArrayList();

                  for(Object var26 : var22) {
                     if (var26 instanceof Map) {
                        Map var27 = (Map)var26;
                        String var9 = (String)var27.get("buyer");
                        String var28 = (String)var27.get("item");
                        double var11 = ((Number)var27.get("price")).doubleValue();
                        var24.add(new OfflineSale(var9, var28, var11));
                     }
                  }

                  this.pendingSales.put(var20, var24);
               }
            }
         }

         if (this.auctionConfig.contains("sort_preferences")) {
            for(String var19 : this.auctionConfig.getConfigurationSection("sort_preferences").getKeys(false)) {
               UUID var21 = UUID.fromString(var19);
               String var23 = this.auctionConfig.getString("sort_preferences." + var19);
               this.sortPreferences.put(var21, var23);
            }
         }

         long var17 = System.currentTimeMillis();
         if (var17 - this.lastYamLLogTime >= 60000L) {
            this.controller.getPlugin().getLogger().info("Loaded " + this.items.size() + " auctions from YAML.");
            this.lastYamLLogTime = var17;
         }

      }
   }

   public void loadFromConfig() {
      this.items.clear();
      this.pendingSales.clear();
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.items.addAll(this.auctionDAO.A());
         this.controller.getPlugin().getLogger().info("Loaded " + this.items.size() + " auctions from MySQL.");
      } else if (this.useSQLite) {
         this.loadFromSQLite();
      } else {
         this.loadFromYaml();
      }

   }

   public void saveToConfig() {
      if (!this.controller.getPlugin().getDatabaseManager().F() && !this.useSQLite) {
         this.saveToYaml();
      }
   }

   public int getDefaultTime() {
      return this.defaultTime;
   }

   public void refresh(Runnable var1) {
      if (this.useSQLite) {
         this.loadFromSQLite();
         if (var1 != null) {
            var1.run();
         }

      } else if (!this.controller.getPlugin().getDatabaseManager().F()) {
         this.loadFromYaml();
         if (var1 != null) {
            var1.run();
         }

      } else {
         this.controller.getPlugin().getSchedulerAdapter().runTaskAsync(() -> {
            try {
               List var2 = this.auctionDAO.A();
               this.controller.getPlugin().getSchedulerAdapter().runTask(() -> {
                  this.items.clear();
                  this.items.addAll(var2);
                  if (var1 != null) {
                     var1.run();
                  }

               });
            } catch (Exception var3) {
               var3.printStackTrace();
               if (var1 != null) {
                  this.controller.getPlugin().getSchedulerAdapter().runTask(var1);
               }
            }

         });
      }
   }

   public void startSyncTask() {
      this.controller.getPlugin().getSchedulerAdapter().runTaskTimer(() -> this.refresh((Runnable)null), 60L, 60L);
   }

   public void setPlayerSort(UUID var1, String var2) {
      this.sortPreferences.put(var1, var2);
      if (this.controller.getPlugin().getDatabaseManager().F()) {
         this.auctionDAO.A(var1, var2);
      } else if (this.useSQLite) {
         this.setPlayerSortInSQLite(var1, var2);
      } else {
         this.saveToYaml();
      }

   }

   public String getPlayerSort(UUID var1) {
      if (!this.sortPreferences.containsKey(var1)) {
         String var2 = "Highest Price";
         if (this.controller.getPlugin().getDatabaseManager().F()) {
            var2 = this.auctionDAO.A(var1);
         } else if (this.useSQLite) {
            var2 = this.getPlayerSortFromSQLite(var1, var2);
         }

         this.sortPreferences.put(var1, var2);
      }

      return (String)this.sortPreferences.get(var1);
   }

   public static class OfflineSale {
      public final String buyer;
      public final String item;
      public final double price;

      public OfflineSale(String var1, String var2, double var3) {
         this.buyer = var1;
         this.item = var2;
         this.price = var3;
      }
   }
}
