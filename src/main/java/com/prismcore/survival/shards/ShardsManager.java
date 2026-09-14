package com.prismcore.survival.shards;

import com.h2ph.PrismSurvival;
import com.h2ph.C.A;
import com.h2ph.C.B;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class ShardsManager implements Listener {
   private final PrismSurvival plugin;
   private File configFile;
   private FileConfiguration config;
   private int intervalSeconds;
   private int rewardAmount;
   private String permission;
   private int killRewardAmount;
   private int killRewardCooldown;
   private String passiveChatMessage;
   private String passiveActionbarMessage;
   private String activeActionbarMessage;
   private String passiveSound;
   private final Map<UUID, Map<UUID, Long>> killCooldowns = new HashMap();
   private final Map<UUID, Integer> activeTime = new HashMap();
   private BukkitTask task;

   public ShardsManager(PrismSurvival var1) {
      this.plugin = var1;
      this.loadConfig();
      this.startTask();
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   public void loadConfig() {
      this.configFile = new File(this.plugin.getDataFolder(), "survival/shards/config.yml");
      if (!this.configFile.exists()) {
         this.plugin.saveResource("survival/shards/config.yml", false);
      }

      this.config = YamlConfiguration.loadConfiguration(this.configFile);
      this.intervalSeconds = this.config.getInt("interval", 60);
      this.rewardAmount = this.config.getInt("amount", 1);
      this.permission = this.config.getString("permission", "prism.shards.passive");
      this.killRewardAmount = this.config.getInt("kill-reward.amount", 1);
      this.killRewardCooldown = this.config.getInt("kill-reward.cooldown", 300);
      this.passiveActionbarMessage = this.config.getString("messages.passive.actionbar", "&7You have received &5{shard} shards.");
      this.activeActionbarMessage = this.config.getString("messages.active.actionbar", "&5+{shards} shards&7 for killing &f{PLAYER}");
      this.passiveSound = this.config.getString("sounds.passive", "BLOCK_AMETHYST_BLOCK_CHIME");
   }

   public void reloadConfig() {
      this.loadConfig();
      if (this.task != null && !this.task.isCancelled()) {
         this.task.cancel();
         this.task = null;
      }

      this.startTask();
   }

   private void startTask() {
      this.task = this.plugin.getSchedulerAdapter().runTaskTimer(() -> {
         for(Player var2 : Bukkit.getOnlinePlayers()) {
            this.updateActiveTime(var2);
         }

      }, 20L, 20L);
   }

   private void updateActiveTime(Player var1) {
      if (var1.hasPermission(this.permission)) {
         A var2 = this.plugin.getAfkManager();
         if (var2 != null) {
            B var3 = var2.A(var1.getLocation());
            if (var3 != null) {
               return;
            }
         }

         UUID var5 = var1.getUniqueId();
         int var4 = (Integer)this.activeTime.getOrDefault(var5, 0);
         ++var4;
         if (var4 >= this.intervalSeconds) {
            this.givePassiveReward(var1);
            var4 = 0;
         }

         this.activeTime.put(var5, var4);
      }
   }

   private void givePassiveReward(Player var1) {
      this.plugin.getPlayerDataManager().get(var1.getUniqueId()).addShards((double)this.rewardAmount);
      String var2 = String.valueOf(this.rewardAmount);
      if (this.passiveChatMessage != null && !this.passiveChatMessage.isEmpty()) {
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', this.passiveChatMessage.replace("{shard}", var2)));
      }

      if (this.passiveActionbarMessage != null && !this.passiveActionbarMessage.isEmpty()) {
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', this.passiveActionbarMessage.replace("{shard}", var2))));
      }

      if (this.passiveSound != null && !this.passiveSound.isEmpty()) {
         try {
            Sound var3 = Sound.valueOf(this.passiveSound.toUpperCase());
            var1.playSound(var1.getLocation(), var3, 1.0F, 1.0F);
         } catch (IllegalArgumentException var4) {
            this.plugin.getLogger().warning("Invalid sound in shards config: " + this.passiveSound);
         }
      }

   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      this.activeTime.put(var1.getPlayer().getUniqueId(), 0);
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      this.activeTime.remove(var1.getPlayer().getUniqueId());
      this.killCooldowns.remove(var1.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent var1) {
      Player var2 = var1.getEntity();
      Player var3 = var2.getKiller();
      if (var3 != null && var3 != var2) {
         UUID var4 = var3.getUniqueId();
         UUID var5 = var2.getUniqueId();
         if (!this.isOnCooldown(var4, var5)) {
            this.plugin.getPlayerDataManager().get(var4).addShards((double)this.killRewardAmount);
            if (this.activeActionbarMessage != null && !this.activeActionbarMessage.isEmpty()) {
               String var6 = this.activeActionbarMessage.replace("{shards}", String.valueOf(this.killRewardAmount)).replace("{PLAYER}", var2.getName());
               var3.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var6)));
            }

            this.setCooldown(var4, var5);
         }
      }
   }

   private boolean isOnCooldown(UUID var1, UUID var2) {
      if (!this.killCooldowns.containsKey(var1)) {
         return false;
      } else {
         Map var3 = (Map)this.killCooldowns.get(var1);
         if (!var3.containsKey(var2)) {
            return false;
         } else {
            long var4 = (Long)var3.get(var2);
            if (System.currentTimeMillis() > var4) {
               var3.remove(var2);
               return false;
            } else {
               return true;
            }
         }
      }
   }

   private void setCooldown(UUID var1, UUID var2) {
      ((Map)this.killCooldowns.computeIfAbsent(var1, (var0) -> new HashMap())).put(var2, System.currentTimeMillis() + (long)this.killRewardCooldown * 1000L);
   }
}
