package com.prismcore.survival.tools;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ToolsManager {
   private final PrismSurvival plugin;
   private FileConfiguration config;
   private File configFile;
   public static NamespacedKey EXPIRY_KEY;
   public static NamespacedKey REMAINING_KEY;
   public static NamespacedKey MULTI_KEY;
   public static NamespacedKey BOOSTER_KEY;
   private static ToolsManager instance;

   public ToolsManager(PrismSurvival var1) {
      this.plugin = var1;
      instance = this;
      EXPIRY_KEY = new NamespacedKey(var1, "tool-expiry");
      REMAINING_KEY = new NamespacedKey(var1, "tool-remaining");
      MULTI_KEY = new NamespacedKey(var1, "is-multitool");
      BOOSTER_KEY = new NamespacedKey(var1, "is-shardbooster");
      this.loadConfig();
      this.registerListeners();
      this.startUpdateTask();
   }

   public static ToolsManager getInstance() {
      return instance;
   }

   public void loadConfig() {
      this.configFile = new File(this.plugin.getDataFolder(), "survival/tools/config.yml");
      if (!this.configFile.exists()) {
         this.configFile.getParentFile().mkdirs();
         if (this.plugin.getResource("survival/tools/config.yml") != null) {
            this.plugin.saveResource("survival/tools/config.yml", false);
         } else {
            try {
               this.configFile.createNewFile();
               this.config = YamlConfiguration.loadConfiguration(this.configFile);
            } catch (IOException var2) {
               this.plugin.getLogger().log(Level.SEVERE, "Could not create config for tools", var2);
            }
         }
      }

      this.config = YamlConfiguration.loadConfiguration(this.configFile);
   }

   public void reloadConfig() {
      this.loadConfig();
   }

   public FileConfiguration getConfig() {
      if (this.config == null) {
         this.loadConfig();
      }

      return this.config;
   }

   private void registerListeners() {
      this.plugin.getServer().getPluginManager().registerEvents(new DrillBlockBreakListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new AxeBlockBreakListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new ShovelBlockBreakListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new DrillClickListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new MultitoolBlockBreakListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new DrillInventoryListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new BucketUseListener(this), this.plugin);
      this.plugin.getServer().getPluginManager().registerEvents(new ShardBoosterListener(this, this.plugin), this.plugin);
   }

   private void startUpdateTask() {
      long var1 = this.getConfig().getLong("drill.update-interval", 20L);
      long var3 = var1 * 20L;
      if (var3 <= 0L) {
         var3 = 100L;
      }

      this.plugin.getSchedulerAdapter().runTaskTimer(() -> {
         for(Player var4 : this.plugin.getServer().getOnlinePlayers()) {
            this.plugin.getSchedulerAdapter().runEntityTask(var4, () -> this.updatePlayerTools(var4, var1));
         }

      }, var3, var3);
   }

   public void updatePlayerTools(Player var1) {
      this.updatePlayerTools(var1, 0L);
   }

   public void updatePlayerTools(Player var1, long var2) {
      for(ItemStack var7 : var1.getInventory().getContents()) {
         if (var7 != null && var7.hasItemMeta()) {
            ItemMeta var8 = var7.getItemMeta();
            boolean var9 = var8.getPersistentDataContainer().has(REMAINING_KEY, PersistentDataType.LONG);
            boolean var10 = var8.getPersistentDataContainer().has(EXPIRY_KEY, PersistentDataType.LONG);
            if (var9 || var10) {
               String var11 = null;
               if (var8.getPersistentDataContainer().has(MULTI_KEY, PersistentDataType.BYTE)) {
                  var11 = "multitool";
               } else {
                  String var12 = var7.getType().name();
                  if (var12.endsWith("_PICKAXE")) {
                     var11 = "drill";
                  } else if (var12.endsWith("_AXE")) {
                     var11 = "axe";
                  } else if (var12.endsWith("_SHOVEL")) {
                     var11 = "shovel";
                  } else if (var12.endsWith("_BUCKET") || var12.equals("BUCKET")) {
                     var11 = "bucket";
                  }
               }

               if (var11 != null && this.getConfig().getBoolean(var11 + ".use-countdown", true)) {
                  long var17;
                  if (var9) {
                     long var14 = (Long)var8.getPersistentDataContainer().get(REMAINING_KEY, PersistentDataType.LONG);
                     var17 = var14 - var2;
                  } else {
                     long var18 = (Long)var8.getPersistentDataContainer().get(EXPIRY_KEY, PersistentDataType.LONG);
                     var17 = (var18 - System.currentTimeMillis()) / 1000L;
                     var8.getPersistentDataContainer().remove(EXPIRY_KEY);
                  }

                  if (var17 <= 0L) {
                     var1.getInventory().remove(var7);
                     var1.playSound(var1.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                  } else {
                     var8.getPersistentDataContainer().set(REMAINING_KEY, PersistentDataType.LONG, var17);
                     String var19 = Utils.formatDuration(var17);
                     List var15 = this.getConfig().getStringList(var11 + ".lore");
                     List var16 = var15.stream().map((var1x) -> var1x.replace("%countdown%", var19)).map(Utils::formatColors).toList();
                     var8.setLore(var16);
                     var7.setItemMeta(var8);
                  }
               }
            }
         }
      }

   }
}
