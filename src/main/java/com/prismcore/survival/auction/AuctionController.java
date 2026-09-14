package com.prismcore.survival.auction;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

public class AuctionController {
   private final PrismSurvival plugin;
   private AuctionManager auctionManager;
   private TransactionManager transactionManager;
   private GUIListener guiListener;
   private File savesFile;
   private FileConfiguration savesConfig;
   private File filterFile;
   private FileConfiguration filterConfig;
   private File configFile;
   private FileConfiguration config;
   private final Map<UUID, BukkitTask> activeTasks = new ConcurrentHashMap();

   public AuctionController(PrismSurvival var1) {
      this.plugin = var1;
   }

   public void enable() {
      this.loadConfig();
      this.setupFilterFile();
      this.auctionManager = new AuctionManager(this, this.plugin.getAuctionDAO());
      this.transactionManager = new TransactionManager(this, this.plugin.getAuctionDAO());
      boolean var1 = this.config.getBoolean("settings.use-vault", true);
      EconomyHandler.setup(this.plugin, var1);
      this.auctionManager.loadFromConfig();
      this.transactionManager.loadFromConfig();
      this.auctionManager.startSyncTask();
      AHCommand var2 = new AHCommand(this);
      this.plugin.getCommand("ah").setExecutor(var2);
      this.plugin.getCommand("ah").setTabCompleter(var2);
      AdminCommand var3 = new AdminCommand(this);
      if (this.plugin.getCommand("auction") != null) {
         this.plugin.getCommand("auction").setExecutor(var3);
         this.plugin.getCommand("auction").setTabCompleter(var3);
      } else {
         this.plugin.getLogger().severe("Command 'auction' not found in plugin.yml!");
      }

      this.guiListener = new GUIListener(this);
      this.plugin.getServer().getPluginManager().registerEvents(this.guiListener, this.plugin);
      this.plugin.getLogger().info("Auction system enabled.");
   }

   public void disable() {
      this.saveAllData();
      this.plugin.getLogger().info("Auction system disabled.");
   }

   private void loadConfig() {
      this.configFile = new File(this.plugin.getDataFolder(), "economy/auction/config.yml");
      if (!this.configFile.exists()) {
         this.configFile.getParentFile().mkdirs();
         this.plugin.saveResource("economy/auction/config.yml", false);
      }

      this.config = YamlConfiguration.loadConfiguration(this.configFile);
   }

   public FileConfiguration getConfig() {
      if (this.config == null) {
         this.loadConfig();
      }

      return this.config;
   }

   public void reloadConfig() {
      this.loadConfig();
   }

   private void setupSavesFile() {
      this.savesFile = new File(this.plugin.getDataFolder(), "economy/auction/saves.yml");
      if (!this.savesFile.exists()) {
         this.savesFile.getParentFile().mkdirs();
         this.plugin.saveResource("economy/auction/saves.yml", false);
      }

      this.savesConfig = YamlConfiguration.loadConfiguration(this.savesFile);
   }

   private void setupFilterFile() {
      this.filterFile = new File(this.plugin.getDataFolder(), "economy/auction/filter.yml");
      if (!this.filterFile.exists()) {
         this.filterFile.getParentFile().mkdirs();
         this.plugin.saveResource("economy/auction/filter.yml", false);
      }

      this.filterConfig = YamlConfiguration.loadConfiguration(this.filterFile);
   }

   public void reloadAllConfigs() {
      this.reloadConfig();
      this.setupFilterFile();
   }

   public FileConfiguration getFilterConfig() {
      return this.filterConfig;
   }

   public AuctionManager getAuctionManager() {
      return this.auctionManager;
   }

   public TransactionManager getTransactionManager() {
      return this.transactionManager;
   }

   public FileConfiguration getSavesConfig() {
      return this.savesConfig;
   }

   public void saveSavesFile() {
      if (this.savesConfig != null && this.savesFile != null) {
         try {
            this.savesConfig.save(this.savesFile);
         } catch (IOException var2) {
            this.plugin.getLogger().severe("Could not save saves.yml: " + var2.getMessage());
         }

      }
   }

   public void startAutoSaveTask() {
      this.plugin.getSchedulerAdapter().runTaskTimerAsync(() -> this.saveAllData(), 12000L, 12000L);
   }

   public void startUpdateTask(Player var1) {
      if (!this.activeTasks.containsKey(var1.getUniqueId())) {
         NamespacedKey var2 = new NamespacedKey(this.plugin, "auction-expire");
         NamespacedKey var3 = new NamespacedKey(this.plugin, "transaction-timestamp");
         BukkitTask var4 = this.plugin.getSchedulerAdapter().runEntityTaskTimer(var1, () -> {
            if (!var1.isOnline()) {
               this.stopUpdateTask(var1);
            } else {
               InventoryView var4 = var1.getOpenInventory();
               if (var4 != null) {
                  Inventory var5 = var4.getTopInventory();
                  InventoryHolder var6 = var5.getHolder();
                  if (var6 instanceof GUIHandler.MainHolder || var6 instanceof GUIHandler.YourItemsHolder || var6 instanceof GUIHandler.TransactionsHolder || var6 instanceof GUIHandler.AdminPlayerDetailsHolder || var6 instanceof GUIHandler.AdminTransactionsHolder) {
                     for(ItemStack var10 : var5.getContents()) {
                        if (var10 != null && var10.hasItemMeta()) {
                           ItemMeta var11 = var10.getItemMeta();
                           PersistentDataContainer var12 = var11.getPersistentDataContainer();
                           if (var12.has(var2, PersistentDataType.LONG)) {
                              long var23 = (Long)var12.get(var2, PersistentDataType.LONG);
                              long var24 = var23 - System.currentTimeMillis();
                              if (var24 < 0L) {
                                 var24 = 0L;
                              }

                              String var25 = FormatUtils.formatTime((int)(var24 / 1000L));
                              if (var11.hasLore()) {
                                 List var26 = var11.getLore();
                                 boolean var27 = false;

                                 for(int var28 = 0; var28 < var26.size(); ++var28) {
                                    if (((String)var26.get(var28)).contains("Time left:")) {
                                       String var29 = Utils.formatColors("&fTime left: &#34ee80" + var25);
                                       if (!((String)var26.get(var28)).equals(var29)) {
                                          var26.set(var28, var29);
                                          var27 = true;
                                       }
                                    }
                                 }

                                 if (var27) {
                                    var11.setLore(var26);
                                    var10.setItemMeta(var11);
                                 }
                              }
                           } else if (var12.has(var3, PersistentDataType.LONG)) {
                              long var13 = (Long)var12.get(var3, PersistentDataType.LONG);
                              long var15 = (System.currentTimeMillis() - var13) / 1000L;
                              String var17 = FormatUtils.formatTime((int)var15);
                              if (var11.hasLore()) {
                                 List var18 = var11.getLore();
                                 boolean var19 = false;

                                 for(int var20 = 0; var20 < var18.size(); ++var20) {
                                    String var21 = (String)var18.get(var20);
                                    if (var21.contains("Sold:")) {
                                       String var22 = Utils.formatColors("&fSold: &7" + var17 + " ago");
                                       if (!var21.equals(var22)) {
                                          var18.set(var20, var22);
                                          var19 = true;
                                       }
                                    } else if (var21.endsWith(" ago")) {
                                       String var30 = Utils.formatColors("&a" + var17 + " ago");
                                       if (!var21.equals(var30)) {
                                          var18.set(var20, var30);
                                          var19 = true;
                                       }
                                    }
                                 }

                                 if (var19) {
                                    var11.setLore(var18);
                                    var10.setItemMeta(var11);
                                 }
                              }
                           }
                        }
                     }

                  }
               }
            }
         }, 20L, 20L);
         this.activeTasks.put(var1.getUniqueId(), var4);
      }
   }

   public void stopUpdateTask(Player var1) {
      BukkitTask var2 = (BukkitTask)this.activeTasks.remove(var1.getUniqueId());
      if (var2 != null) {
         var2.cancel();
      }

   }

   public void saveAllData() {
      if (this.auctionManager != null) {
         this.auctionManager.saveToConfig();
      }

      if (this.transactionManager != null) {
         this.transactionManager.saveToConfig();
      }

      this.saveSavesFile();
   }

   public PrismSurvival getPlugin() {
      return this.plugin;
   }
}
