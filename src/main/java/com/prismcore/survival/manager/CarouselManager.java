package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.scheduler.SchedulerAdapter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

public class CarouselManager {
   private final PrismSurvival plugin;
   private final Map<UUID, BukkitTask> activeTasks = new HashMap();
   private final Map<UUID, ItemStack> pendingRewards = new HashMap();
   private final Random random = new Random();
   private final Sound[] carouselSounds;
   private final Map<UUID, BukkitTask> backgroundTasks;

   public CarouselManager(PrismSurvival var1) {
      this.carouselSounds = new Sound[]{Sound.BLOCK_NOTE_BLOCK_PLING, Sound.BLOCK_NOTE_BLOCK_HAT, Sound.BLOCK_NOTE_BLOCK_BIT};
      this.backgroundTasks = new HashMap();
      this.plugin = var1;
   }

   public void openCarouselGUI(Player var1, String var2) {
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.translateAlternateColorCodes('&', "&8ᴄʟɪᴄᴋ ѕᴛᴀʀᴛ ᴛᴏ ѕᴘɪɴ"));
      this.setupStaticItems(var3);
      File var4 = new File(this.plugin.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (var4.exists()) {
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
         if (((FileConfiguration)var5).contains("contents")) {
            ConfigurationSection var6 = ((FileConfiguration)var5).getConfigurationSection("contents");

            for(String var8 : var6.getKeys(false)) {
               try {
                  int var9 = Integer.parseInt(var8);
                  ItemStack var10 = ((FileConfiguration)var5).getItemStack("contents." + var8);
                  if (var10 != null && var10.getType() != Material.AIR) {
                     var3.setItem(var9, var10);
                  }
               } catch (NumberFormatException var11) {
               }
            }
         }

         List var12 = this.getCrateContents(var5);
         if (!var12.isEmpty()) {
            for(int var13 = 10; var13 <= 16; ++var13) {
               if (var3.getItem(var13) == null || var3.getItem(var13).getType() == Material.AIR) {
                  var3.setItem(var13, this.getRandomItem(var12));
               }
            }

            for(int var14 = 0; var14 < var3.getSize(); ++var14) {
               if (var14 != 4 && var14 != 22 && (var14 < 10 || var14 > 16) && (var3.getItem(var14) == null || var3.getItem(var14).getType() == Material.AIR)) {
                  var3.setItem(var14, this.getRandomItem(var12));
               }
            }
         }
      }

      var1.openInventory(var3);
      var1.setMetadata("prism_active_crate_type", new FixedMetadataValue(this.plugin, "CAROUSEL"));
      var1.setMetadata("prism_active_crate", new FixedMetadataValue(this.plugin, var2));
      this.startBackgroundAnimation(var1, var3, 5L);
   }

   private void startBackgroundAnimation(final Player var1, final Inventory var2, long var3) {
      if (this.backgroundTasks.containsKey(var1.getUniqueId())) {
         ((BukkitTask)this.backgroundTasks.get(var1.getUniqueId())).cancel();
         this.backgroundTasks.remove(var1.getUniqueId());
      }

      final List var5 = Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 17, 26, 25, 24, 23, 21, 20, 19, 18, 9);
      BukkitTask var6 = this.plugin.getSchedulerAdapter().runTaskTimer(new Runnable() {
         public void run() {
            if (var1.isOnline() && var1.getOpenInventory().getTitle().contains("ᴄʟɪᴄᴋ ѕᴛᴀʀᴛ ᴛᴏ ѕᴘɪɴ")) {
               ItemStack var1x = var2.getItem((Integer)var5.get(var5.size() - 1));

               for(int var2x = var5.size() - 1; var2x > 0; --var2x) {
                  int var3 = (Integer)var5.get(var2x);
                  int var4 = (Integer)var5.get(var2x - 1);
                  var2.setItem(var3, var2.getItem(var4));
               }

               var2.setItem((Integer)var5.get(0), var1x);
            } else {
               CarouselManager.this.handleClose(var1);
            }
         }
      }, var3, var3);
      this.backgroundTasks.put(var1.getUniqueId(), var6);
   }

   private void setupStaticItems(Inventory var1) {
      ItemStack var2 = new ItemStack(Material.ENDER_EYE);
      ItemMeta var3 = var2.getItemMeta();
      var3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aʏᴏᴜʀ ʀᴇᴡᴀʀᴅ"));
      var2.setItemMeta(var3);
      var1.setItem(4, var2);
      ItemStack var4 = new ItemStack(Material.OAK_SIGN);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aѕᴛᴀʀᴛ"));
      var4.setItemMeta(var5);
      var1.setItem(22, var4);
   }

   public void handleStartClick(Player var1, String var2, Inventory var3) {
      if (!this.activeTasks.containsKey(var1.getUniqueId())) {
         if (var1.getInventory().firstEmpty() == -1) {
            String var5 = ChatColor.translateAlternateColorCodes('&', "&cYour inventory is full.");
            var1.sendMessage(var5);
            var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var5));
            var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } else if (!this.hasKey(var1, var2)) {
            var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } else {
            this.deductKey(var1, var2);
            ItemStack var4 = this.pickReward(var2);
            this.pendingRewards.put(var1.getUniqueId(), var4);
            this.startBackgroundAnimation(var1, var3, 2L);
            this.startVariableSpeedAnimation(var1, var2, var3, var4);
         }
      }
   }

   private void startVariableSpeedAnimation(final Player var1, String var2, final Inventory var3, final ItemStack var4) {
      File var5 = new File(this.plugin.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
      final List var7 = this.getCrateContents(var6);
      Runnable var8 = new Runnable() {
         int ticksElapsed = 0;
         int currentStep = 0;
         long currentDelay = 2L;

         public void run() {
            if (!var1.isOnline()) {
               CarouselManager.this.stopAndClean(var1.getUniqueId());
            } else {
               for(int var1x = 16; var1x > 10; --var1x) {
                  var3.setItem(var1x, var3.getItem(var1x - 1));
               }

               if (this.currentStep == 46) {
                  var3.setItem(10, var4);
               } else {
                  var3.setItem(10, CarouselManager.this.getRandomItem(var7));
               }

               var1.playSound(var1.getLocation(), CarouselManager.this.carouselSounds[CarouselManager.this.random.nextInt(CarouselManager.this.carouselSounds.length)], 1.0F, 2.0F);
               ++this.currentStep;
               if (this.currentStep >= 50) {
                  CarouselManager.this.finish(var1, var3, var4);
               } else {
                  if (this.currentStep < 30) {
                     this.currentDelay = 2L;
                  } else if (this.currentStep < 40) {
                     this.currentDelay = 5L;
                  } else if (this.currentStep < 45) {
                     this.currentDelay = 10L;
                  } else {
                     this.currentDelay = 15L;
                  }

                  BukkitTask var2 = CarouselManager.this.plugin.getSchedulerAdapter().runTaskLater(this, this.currentDelay);
                  CarouselManager.this.activeTasks.put(var1.getUniqueId(), var2);
               }
            }
         }
      };
      BukkitTask var9 = this.plugin.getSchedulerAdapter().runTaskLater(var8, 2L);
      this.activeTasks.put(var1.getUniqueId(), var9);
   }

   private void startAnimation(Player var1, String var2, Inventory var3, ItemStack var4) {
      this.startVariableSpeedAnimation(var1, var2, var3, var4);
   }

   private void finish(Player var1, Inventory var2, ItemStack var3) {
      this.stopAndClean(var1.getUniqueId());
      if (var1.isOnline()) {
         var1.getInventory().addItem(new ItemStack[]{var3});
      }

      this.pendingRewards.remove(var1.getUniqueId());
      var2.setItem(13, var3);

      try {
         var1.playSound(var1.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
         var1.playSound(var1.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0F, 1.0F);
         var1.playSound(var1.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0F, 1.0F);
         this.plugin.getSchedulerAdapter().runAtLocation(var1.getLocation(), () -> {
            try {
               Firework var2 = (Firework)var1.getWorld().spawnEntity(var1.getLocation(), EntityType.FIREWORK_ROCKET);
               FireworkMeta var3 = var2.getFireworkMeta();
               var3.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor(new Color[]{Color.GREEN, Color.LIME, Color.AQUA}).withFade(Color.WHITE).flicker(true).trail(true).build());
               var3.setPower(1);
               var2.setFireworkMeta(var3);
               SchedulerAdapter var10000 = this.plugin.getSchedulerAdapter();
               Location var10001 = var1.getLocation();
               Objects.requireNonNull(var2);
               var10000.runAtLocation(var10001, var2::detonate);
            } catch (Exception var4) {
            }

         });
      } catch (Exception var5) {
      }

      this.startBackgroundAnimation(var1, var2, 5L);
   }

   public void handleClose(Player var1) {
      UUID var2 = var1.getUniqueId();
      if (this.activeTasks.containsKey(var2)) {
         ((BukkitTask)this.activeTasks.get(var2)).cancel();
         this.activeTasks.remove(var2);
      }

      if (this.backgroundTasks.containsKey(var2)) {
         ((BukkitTask)this.backgroundTasks.get(var2)).cancel();
         this.backgroundTasks.remove(var2);
      }

      if (this.pendingRewards.containsKey(var2)) {
         ItemStack var3 = (ItemStack)this.pendingRewards.remove(var2);
         if (var1.isOnline()) {
            var1.getInventory().addItem(new ItemStack[]{var3});
            var1.sendMessage(String.valueOf(ChatColor.GREEN) + "You received your crate reward!");
         }
      }

   }

   private void stopAndClean(UUID var1) {
      if (this.activeTasks.containsKey(var1)) {
         ((BukkitTask)this.activeTasks.get(var1)).cancel();
         this.activeTasks.remove(var1);
      }

   }

   private List<ItemStack> getCrateContents(FileConfiguration var1) {
      ArrayList var2 = new ArrayList();
      if (var1.contains("contents")) {
         ConfigurationSection var3 = var1.getConfigurationSection("contents");

         for(String var5 : var3.getKeys(false)) {
            ItemStack var6 = var1.getItemStack("contents." + var5);
            if (var6 != null && !this.isDecoration(var6)) {
               var2.add(var6);
            }
         }
      }

      return var2;
   }

   private boolean isDecoration(ItemStack var1) {
      String var2 = var1.getType().name();
      return var2.endsWith("GLASS_PANE");
   }

   private ItemStack getRandomItem(List<ItemStack> var1) {
      return var1.isEmpty() ? new ItemStack(Material.AIR) : ((ItemStack)var1.get(this.random.nextInt(var1.size()))).clone();
   }

   private ItemStack pickReward(String var1) {
      File var2 = new File(this.plugin.getDataFolder(), "crates/crate/" + var1 + "-crate.yml");
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
      List var4 = this.getCrateContents(var3);
      return this.getRandomItem(var4);
   }

   private boolean hasKey(Player var1, String var2) {
      File var3 = new File(this.plugin.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var3.exists()) {
         return false;
      } else {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         String var5 = ((FileConfiguration)var4).getString("key");
         PlayerData var6 = this.plugin.getPlayerDataManager().get(var1.getUniqueId());
         return var6 != null && var6.getKeyCount(var5) > 0;
      }
   }

   private void deductKey(Player var1, String var2) {
      File var3 = new File(this.plugin.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (var3.exists()) {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         String var5 = ((FileConfiguration)var4).getString("key");
         PlayerData var6 = this.plugin.getPlayerDataManager().get(var1.getUniqueId());
         if (var6 != null) {
            var6.removeKey(var5);
         }

      }
   }

   public boolean isSpinning(Player var1) {
      return this.activeTasks.containsKey(var1.getUniqueId());
   }
}
