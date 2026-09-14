package com.h2ph.S;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class A implements Listener, CommandExecutor {
   private final PrismSurvival C;
   private final Set<UUID> B = new HashSet();
   private FileConfiguration A;

   public A(PrismSurvival var1) {
      this.C = var1;
      this.A();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("nightvision").setExecutor(this);
      var1.getCommand("nv").setExecutor(this);
   }

   private void A() {
      File var1 = new File(this.C.getDataFolder(), "nightvision.yml");
      if (!var1.exists()) {
         this.C.saveResource("nightvision.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(var1);
   }

   private String A(String var1) {
      String var2 = this.A.getString(var1, "&cMissing message: " + var1);
      return ChatColor.translateAlternateColorCodes('&', var2);
   }

   private void A(Player var1) {
      var1.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, false));
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         UUID var6 = var5.getUniqueId();
         if (!this.B.contains(var6)) {
            this.B.add(var6);
            var5.sendMessage(this.A("messages.activated"));
            this.A(var5);
         } else {
            this.B.remove(var6);
            var5.sendMessage(this.A("messages.deactivated"));
            var5.removePotionEffect(PotionEffectType.NIGHT_VISION);
         }

         return true;
      } else {
         var1.sendMessage(this.A("messages.console"));
         return true;
      }
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.B.contains(var2.getUniqueId())) {
         Bukkit.getScheduler().runTaskLater(this.C, () -> this.A(var2), 1L);
      }

   }

   @EventHandler
   public void onConsume(PlayerItemConsumeEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getItem().getType().toString().equals("MILK_BUCKET") && this.B.contains(var2.getUniqueId())) {
         Bukkit.getScheduler().runTaskLater(this.C, () -> this.A(var2), 0L);
      }

   }

   @EventHandler
   public void onTotemPop(EntityResurrectEvent var1) {
      LivingEntity var3 = var1.getEntity();
      if (var3 instanceof Player var2) {
         if (this.B.contains(var2.getUniqueId())) {
            Bukkit.getScheduler().runTaskLater(this.C, () -> this.A(var2), 1L);
         }
      }

   }
}
