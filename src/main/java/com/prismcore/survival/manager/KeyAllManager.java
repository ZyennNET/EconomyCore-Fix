package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class KeyAllManager {
   private final PrismSurvival plugin;
   private long nextKeyAllTime;
   private long intervalMillis;
   private File configFile;
   private FileConfiguration config;
   private final Set<String> validKeys = new HashSet();
   private String rewardKey;
   private int rewardAmount;

   public KeyAllManager(PrismSurvival var1) {
      this.plugin = var1;
      this.loadConfig();
      this.scheduleNextKeyAll();
      this.startTask();
   }

   private void loadConfig() {
      this.configFile = new File(this.plugin.getDataFolder(), "crates/keys/config.yml");
      if (!this.configFile.exists()) {
         try {
            this.plugin.saveResource("crates/keys/config.yml", false);
         } catch (IllegalArgumentException var3) {
         }
      }

      if (this.configFile.exists()) {
         this.config = YamlConfiguration.loadConfiguration(this.configFile);
      } else {
         this.config = new YamlConfiguration();
      }

      long var1 = this.config.getLong("keyall.interval", 60L);
      this.intervalMillis = var1 * 60L * 1000L;
      this.validKeys.clear();
      this.validKeys.addAll(this.config.getStringList("keys"));
      this.rewardKey = this.config.getString("reward.key", "common");
      this.rewardAmount = this.config.getInt("reward.amount", 1);
   }

   public boolean isValidKey(String var1) {
      return this.validKeys.contains(var1);
   }

   public Set<String> getValidKeys() {
      return Collections.unmodifiableSet(this.validKeys);
   }

   private void scheduleNextKeyAll() {
      this.nextKeyAllTime = System.currentTimeMillis() + this.intervalMillis;
   }

   private void startTask() {
      this.plugin.getSchedulerAdapter().runTaskTimer(() -> {
         if (System.currentTimeMillis() >= this.nextKeyAllTime) {
            this.runKeyAll();
         }

      }, 20L, 20L);
   }

   public void runKeyAll() {
      for(Player var2 : this.plugin.getServer().getOnlinePlayers()) {
         PlayerData var3 = this.plugin.getPlayerDataManager().get(var2.getUniqueId());
         if (var3 != null) {
            int var4 = var3.getKeyCount(this.rewardKey);
            var3.setKeyCount(this.rewardKey, var4 + this.rewardAmount);
            var2.playSound(var2.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have received a &a" + this.rewardKey + "&7 from keyall"));
            TextComponent var5 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a[Click to teleport]"));
            var5.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/warp crates"));
            var5.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Click to warp")).create()));
            TextComponent var6 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7 to teleport or type &a/warp crates"));
            var5.addExtra(var6);
            var2.spigot().sendMessage(var5);
         }
      }

      this.nextKeyAllTime = System.currentTimeMillis() + this.intervalMillis;
   }

   public String getTimeRemainingFormatted() {
      long var1 = this.nextKeyAllTime - System.currentTimeMillis();
      if (var1 < 0L) {
         var1 = 0L;
      }

      long var3 = var1 / 1000L;
      long var5 = var3 / 60L;
      long var7 = var3 % 60L;
      return String.format("%dm %ds", var5, var7);
   }
}
