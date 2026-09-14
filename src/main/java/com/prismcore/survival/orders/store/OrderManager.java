package com.prismcore.survival.orders.store;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.ItemKey;
import com.prismcore.survival.orders.data.Order;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class OrderManager {
   private final PrismOrders pl;
   private final Map<UUID, Order> orders = new LinkedHashMap();
   private final File ordersFile;
   private YamlConfiguration ordersConfig;
   private File databaseConfigFile;
   private YamlConfiguration databaseConfig;
   private String storageType = "yaml";
   private Connection dbConnection;

   public OrderManager(PrismOrders var1) {
      this.pl = var1;
      this.ordersFile = new File(var1.getPlugin().getDataFolder(), "orders/orders.yml");
      this.loadDatabaseConfig();
      if (!"yaml".equalsIgnoreCase(this.storageType)) {
         this.initDatabase();
      }

      this.loadAll();
   }

   public Collection<Order> all() {
      return this.orders.values();
   }

   public Order getOrder(UUID var1) {
      return (Order)this.orders.get(var1);
   }

   public Order create(UUID var1, Material var2, int var3, double var4) {
      return this.create(var1, ItemKey.of(var2), var3, var4);
   }

   public Order create(UUID var1, ItemKey var2, int var3, double var4) {
      Order var6 = new Order();
      var6.id = UUID.randomUUID();
      var6.owner = var1;
      var6.key = var2;
      var6.requested = Math.max(1, var3);
      var6.delivered = 0;
      var6.priceEach = var4;
      var6.paid = var6.totalPrice();
      var6.canceled = false;
      var6.completed = false;
      var6.createdAt = System.currentTimeMillis();
      var6.expiresAt = var6.createdAt + 604800000L;
      var6.storage.clear();
      this.orders.put(var6.id, var6);
      this.saveAll();
      return var6;
   }

   public void cancel(Order var1) {
      var1.canceled = true;
      int var2 = var1.remainingAmount();
      double var3 = (double)var2 * var1.priceEach;
      OfflinePlayer var5 = Bukkit.getOfflinePlayer(var1.owner);
      if (var5.isOnline()) {
         this.pl.giveMoney(var5, var3);
      }

      var1.requested = var1.delivered;
      var1.completed = true;
      this.saveAll();
   }

   public void applyDelivery(Order var1, List<ItemStack> var2, int var3, UUID var4) {
      if (var3 > 0) {
         for(ItemStack var6 : var2) {
            if (var6 != null && var6.getType() != Material.AIR && var6.getAmount() > 0) {
               var1.storage.add(var6);
            }
         }

         double var8 = (double)var3 * var1.priceEach;
         Player var7 = Bukkit.getPlayer(var4);
         if (var7 != null) {
            this.pl.giveMoney(Bukkit.getOfflinePlayer(var4), var8);
         }

         var1.delivered += var3;
         if (var1.delivered >= var1.requested) {
            var1.completed = true;
         }

         var1.paid = Math.max((double)0.0F, var1.totalPrice() - (double)var1.delivered * var1.priceEach);
         this.saveAll();
         this.sendReceiverActionbar(var1, var4, var3);
      }
   }

   private void sendReceiverActionbar(Order var1, UUID var2, int var3) {
      Player var4 = Bukkit.getPlayer(var1.owner);
      if (var4 != null) {
         String var5 = (String)Optional.ofNullable(Bukkit.getPlayer(var2)).map(Player::getName).orElseGet(() -> {
            OfflinePlayer var1 = Bukkit.getOfflinePlayer(var2);
            return var1.getName() != null ? var1.getName() : "Someone";
         });
         HashMap var6 = new HashMap();
         var6.put("player", var5);
         var6.put("amount", String.valueOf(var3));
         var6.put("item", var1.key.displayName());
         String var7 = "&a{player} has delivered you {amount} {item}!";
         String var8 = Utils.applyPlaceholders(var7, var6);
         var8 = Utils.formatColors(var8);
         var4.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var8));
      }
   }

   private void loadAll() {
      this.orders.clear();
      if (!"yaml".equalsIgnoreCase(this.storageType)) {
         this.loadFromDatabase();
      } else if (!this.ordersFile.exists()) {
         this.ordersFile.getParentFile().mkdirs();
      } else {
         this.ordersConfig = YamlConfiguration.loadConfiguration(this.ordersFile);

         for(String var2 : this.ordersConfig.getKeys(false)) {
            try {
               Order var3 = new Order();
               var3.id = UUID.fromString(var2);
               var3.owner = UUID.fromString(this.ordersConfig.getString(var2 + ".owner"));
               var3.key = ItemKey.deserialize(this.ordersConfig.getString(var2 + ".key"));
               var3.requested = this.ordersConfig.getInt(var2 + ".requested");
               var3.delivered = this.ordersConfig.getInt(var2 + ".delivered");
               var3.priceEach = this.ordersConfig.getDouble(var2 + ".priceEach");
               var3.paid = this.ordersConfig.getDouble(var2 + ".paid");
               var3.canceled = this.ordersConfig.getBoolean(var2 + ".canceled");
               var3.completed = this.ordersConfig.getBoolean(var2 + ".completed");
               var3.createdAt = this.ordersConfig.getLong(var2 + ".createdAt");
               var3.expiresAt = this.ordersConfig.getLong(var2 + ".expiresAt");
               var3.storage.clear();

               for(String var6 : this.ordersConfig.getStringList(var2 + ".storage")) {
                  ItemStack var7 = this.itemStackFromBase64(var6);
                  if (var7 != null) {
                     var3.storage.add(var7);
                  }
               }

               this.orders.put(var3.id, var3);
            } catch (Exception var8) {
               this.pl.getPlugin().getLogger().warning("Failed to load order " + var2 + ": " + var8.getMessage());
            }
         }

         this.pl.getPlugin().getLogger().info("Loaded " + this.orders.size() + " orders from YAML.");
      }
   }

   public void saveAll() {
      if (!"yaml".equalsIgnoreCase(this.storageType)) {
         this.saveAllToDatabase();
      } else {
         if (this.ordersConfig == null) {
            this.ordersConfig = new YamlConfiguration();
         }

         for(Order var2 : this.orders.values()) {
            String var3 = var2.id.toString();
            this.ordersConfig.set(var3 + ".owner", var2.owner.toString());
            this.ordersConfig.set(var3 + ".key", var2.key.serialize());
            this.ordersConfig.set(var3 + ".requested", var2.requested);
            this.ordersConfig.set(var3 + ".delivered", var2.delivered);
            this.ordersConfig.set(var3 + ".priceEach", var2.priceEach);
            this.ordersConfig.set(var3 + ".paid", var2.paid);
            this.ordersConfig.set(var3 + ".canceled", var2.canceled);
            this.ordersConfig.set(var3 + ".completed", var2.completed);
            this.ordersConfig.set(var3 + ".createdAt", var2.createdAt);
            this.ordersConfig.set(var3 + ".expiresAt", var2.expiresAt);
            ArrayList var4 = new ArrayList();

            for(ItemStack var6 : var2.storage) {
               String var7 = this.itemStackToBase64(var6);
               if (var7 != null) {
                  var4.add(var7);
               }
            }

            this.ordersConfig.set(var3 + ".storage", var4);
         }

         try {
            this.ordersConfig.save(this.ordersFile);
         } catch (Exception var8) {
            this.pl.getPlugin().getLogger().severe("Failed to save orders: " + var8.getMessage());
         }

      }
   }

   private void loadDatabaseConfig() {
      this.databaseConfigFile = new File(this.pl.getPlugin().getDataFolder(), "economy/orders/database.yml");
      if (this.databaseConfigFile.getParentFile() != null && !this.databaseConfigFile.getParentFile().exists()) {
         this.databaseConfigFile.getParentFile().mkdirs();
      }

      this.databaseConfig = YamlConfiguration.loadConfiguration(this.databaseConfigFile);
      boolean var1 = false;
      if (!this.databaseConfig.isSet("storage.type")) {
         this.databaseConfig.set("storage.type", "yaml");
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.sqlite.file")) {
         this.databaseConfig.set("storage.sqlite.file", "orders/orders.db");
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.mysql.host")) {
         this.databaseConfig.set("storage.mysql.host", "localhost");
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.mysql.port")) {
         this.databaseConfig.set("storage.mysql.port", 3306);
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.mysql.database")) {
         this.databaseConfig.set("storage.mysql.database", "prismsurvival");
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.mysql.username")) {
         this.databaseConfig.set("storage.mysql.username", "root");
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.mysql.password")) {
         this.databaseConfig.set("storage.mysql.password", "");
         var1 = true;
      }

      if (!this.databaseConfig.isSet("storage.mysql.useSSL")) {
         this.databaseConfig.set("storage.mysql.useSSL", false);
         var1 = true;
      }

      if (var1) {
         try {
            this.databaseConfig.save(this.databaseConfigFile);
         } catch (Exception var3) {
            this.pl.getPlugin().getLogger().warning("Failed to save orders/database.yml: " + var3.getMessage());
         }
      }

      this.storageType = this.databaseConfig.getString("storage.type", "yaml");
   }

   private void initDatabase() {
      try {
         if ("sqlite".equalsIgnoreCase(this.storageType)) {
            String var1 = this.databaseConfig.getString("storage.sqlite.file", "orders/orders.db");
            File var2 = new File(this.pl.getPlugin().getDataFolder(), var1);
            if (var2.getParentFile() != null && !var2.getParentFile().exists()) {
               var2.getParentFile().mkdirs();
            }

            Class.forName("org.sqlite.JDBC");
            this.dbConnection = DriverManager.getConnection("jdbc:sqlite:" + var2.getAbsolutePath());
         } else {
            if (!"mysql".equalsIgnoreCase(this.storageType)) {
               this.pl.getPlugin().getLogger().warning("Unknown storage.type '" + this.storageType + "' in orders/database.yml. Falling back to YAML.");
               this.storageType = "yaml";
               return;
            }

            String var10 = this.databaseConfig.getString("storage.mysql.host", "localhost");
            int var11 = this.databaseConfig.getInt("storage.mysql.port", 3306);
            String var3 = this.databaseConfig.getString("storage.mysql.database", "prismsurvival");
            String var4 = this.databaseConfig.getString("storage.mysql.username", "root");
            String var5 = this.databaseConfig.getString("storage.mysql.password", "");
            boolean var6 = this.databaseConfig.getBoolean("storage.mysql.useSSL", false);
            String var7 = "jdbc:mysql://" + var10 + ":" + var11 + "/" + var3 + "?useSSL=" + var6 + "&autoReconnect=true&characterEncoding=UTF-8";
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.dbConnection = DriverManager.getConnection(var7, var4, var5);
         }

         this.createOrdersTable();
         this.pl.getPlugin().getLogger().info("Order storage connected via " + this.storageType.toUpperCase(Locale.ENGLISH) + ".");
      } catch (ClassNotFoundException var8) {
         this.pl.getPlugin().getLogger().severe("JDBC driver for " + this.storageType + " not found on classpath. Add the dependency to your plugin build. Falling back to YAML storage.");
         this.storageType = "yaml";
      } catch (Exception var9) {
         Logger var10000 = this.pl.getPlugin().getLogger();
         String var10001 = this.storageType;
         var10000.severe("Failed to connect order storage (" + var10001 + "): " + var9.getMessage() + ". Falling back to YAML storage.");
         this.storageType = "yaml";
      }

   }

   private void createOrdersTable() throws SQLException {
      Statement var1 = this.dbConnection.createStatement();

      try {
         var1.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id VARCHAR(36) PRIMARY KEY, owner VARCHAR(36), item_key TEXT, requested INT, delivered INT, price_each DOUBLE, paid DOUBLE, canceled INT, completed INT, created_at BIGINT, expires_at BIGINT, storage TEXT)");
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

   private void loadFromDatabase() {
      this.orders.clear();
      if (this.dbConnection != null) {
         String var1 = "SELECT id, owner, item_key, requested, delivered, price_each, paid, canceled, completed, created_at, expires_at, storage FROM orders";

         try {
            Statement var2 = this.dbConnection.createStatement();

            try {
               ResultSet var3 = var2.executeQuery(var1);

               try {
                  while(var3.next()) {
                     try {
                        Order var4 = new Order();
                        var4.id = UUID.fromString(var3.getString("id"));
                        var4.owner = UUID.fromString(var3.getString("owner"));
                        var4.key = ItemKey.deserialize(var3.getString("item_key"));
                        var4.requested = var3.getInt("requested");
                        var4.delivered = var3.getInt("delivered");
                        var4.priceEach = var3.getDouble("price_each");
                        var4.paid = var3.getDouble("paid");
                        var4.canceled = var3.getInt("canceled") != 0;
                        var4.completed = var3.getInt("completed") != 0;
                        var4.createdAt = var3.getLong("created_at");
                        var4.expiresAt = var3.getLong("expires_at");
                        var4.storage.clear();
                        String var5 = var3.getString("storage");
                        if (var5 != null && !var5.isEmpty()) {
                           for(String var9 : var5.split("\\|")) {
                              ItemStack var10 = this.itemStackFromBase64(var9);
                              if (var10 != null) {
                                 var4.storage.add(var10);
                              }
                           }
                        }

                        this.orders.put(var4.id, var4);
                     } catch (Exception var13) {
                        this.pl.getPlugin().getLogger().warning("Failed to load order row: " + var13.getMessage());
                     }
                  }
               } catch (Throwable var14) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var12) {
                        var14.addSuppressed(var12);
                     }
                  }

                  throw var14;
               }

               if (var3 != null) {
                  var3.close();
               }
            } catch (Throwable var15) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var11) {
                     var15.addSuppressed(var11);
                  }
               }

               throw var15;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (SQLException var16) {
            this.pl.getPlugin().getLogger().severe("Failed to load orders from database: " + var16.getMessage());
         }

         Logger var10000 = this.pl.getPlugin().getLogger();
         int var10001 = this.orders.size();
         var10000.info("Loaded " + var10001 + " orders from " + this.storageType.toUpperCase(Locale.ENGLISH) + ".");
      }
   }

   private void saveAllToDatabase() {
      if (this.dbConnection != null) {
         String var1 = "REPLACE INTO orders (id, owner, item_key, requested, delivered, price_each, paid, canceled, completed, created_at, expires_at, storage) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

         try {
            PreparedStatement var2 = this.dbConnection.prepareStatement(var1);

            try {
               for(Order var4 : this.orders.values()) {
                  var2.setString(1, var4.id.toString());
                  var2.setString(2, var4.owner.toString());
                  var2.setString(3, var4.key.serialize());
                  var2.setInt(4, var4.requested);
                  var2.setInt(5, var4.delivered);
                  var2.setDouble(6, var4.priceEach);
                  var2.setDouble(7, var4.paid);
                  var2.setInt(8, var4.canceled ? 1 : 0);
                  var2.setInt(9, var4.completed ? 1 : 0);
                  var2.setLong(10, var4.createdAt);
                  var2.setLong(11, var4.expiresAt);
                  ArrayList var5 = new ArrayList();

                  for(ItemStack var7 : var4.storage) {
                     String var8 = this.itemStackToBase64(var7);
                     if (var8 != null) {
                        var5.add(var8);
                     }
                  }

                  var2.setString(12, String.join("|", var5));
                  var2.addBatch();
               }

               var2.executeBatch();
            } catch (Throwable var10) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (SQLException var11) {
            this.pl.getPlugin().getLogger().severe("Failed to save orders to database: " + var11.getMessage());
         }

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
            this.pl.getPlugin().getLogger().warning("Failed to serialize item: " + var10.getMessage());
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
            this.pl.getPlugin().getLogger().warning("Failed to deserialize item: " + var10.getMessage());
            return null;
         }
      } else {
         return null;
      }
   }

   public void saveOrder(Order var1) {
      this.saveAll();
   }

   public void reload(Runnable var1) {
      this.loadAll();
      if (var1 != null) {
         var1.run();
      }

   }

   public void saveAllSync() {
      this.saveAll();
   }

   public static String nice(Material var0) {
      String var1 = var0.name().toLowerCase(Locale.ENGLISH).replace('_', ' ');
      String[] var2 = var1.split("\\s+");
      StringBuilder var3 = new StringBuilder();

      for(String var7 : var2) {
         if (!var7.isEmpty()) {
            var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1)).append(' ');
         }
      }

      return var3.toString().trim();
   }
}
