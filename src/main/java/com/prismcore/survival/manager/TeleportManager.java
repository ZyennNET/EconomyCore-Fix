package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class TeleportManager {
   private final PrismSurvival plugin;
   private final Map<UUID, BukkitTask> activeTasks = new HashMap();

   public TeleportManager(PrismSurvival var1) {
      this.plugin = var1;
   }

   public void startCountdown(final Player var1, final Location var2, final int var3, final String var4) {
      final UUID var5 = var1.getUniqueId();
      if (this.activeTasks.containsKey(var5)) {
         try {
            var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } catch (Throwable var7) {
         }

      } else {
         BukkitTask var6 = this.plugin.getSchedulerAdapter().runEntityTaskTimer(var1, new Runnable() {
            int timeLeft = var3;
            final Location startLoc = var1.getLocation();

            public void run() {
               if (!var1.isOnline()) {
                  BukkitTask var10 = (BukkitTask)TeleportManager.this.activeTasks.get(var5);
                  if (var10 != null) {
                     var10.cancel();
                     TeleportManager.this.activeTasks.remove(var5);
                  }

               } else {
                  Location var1x = var1.getLocation();
                  double var2x = Math.pow(var1x.getX() - this.startLoc.getX(), (double)2.0F) + Math.pow(var1x.getZ() - this.startLoc.getZ(), (double)2.0F);
                  double var4x = Math.abs(var1x.getY() - this.startLoc.getY());
                  if (!(var2x > 0.1) && !(var4x > (double)1.5F)) {
                     if (this.timeLeft <= 0) {
                        if (var2.getWorld() == null) {
                           var1.sendMessage(String.valueOf(ChatColor.RED) + "Teleport failed: Target world is not loaded.");
                           BukkitTask var13 = (BukkitTask)TeleportManager.this.activeTasks.get(var5);
                           if (var13 != null) {
                              var13.cancel();
                              TeleportManager.this.activeTasks.remove(var5);
                           }

                        } else {
                           var1.teleportAsync(var2);
                           var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                           String var12 = ChatColor.translateAlternateColorCodes('&', "&7You teleport to &5ѕᴘᴀᴡɴ " + var4);
                           var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var12));
                           var1.sendMessage(var12);
                           BukkitTask var14 = (BukkitTask)TeleportManager.this.activeTasks.get(var5);
                           if (var14 != null) {
                              var14.cancel();
                              TeleportManager.this.activeTasks.remove(var5);
                           }

                        }
                     } else {
                        String var11 = ChatColor.translateAlternateColorCodes('&', "&7Teleporting in &5" + this.timeLeft + "s");
                        var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var11));

                        try {
                           var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 2.0F);
                        } catch (Throwable var8) {
                        }

                        --this.timeLeft;
                     }
                  } else {
                     String var6 = String.valueOf(ChatColor.RED) + "Teleport cancelled because you moved.";
                     var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var6));
                     var1.sendMessage(var6);

                     try {
                        var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     } catch (Throwable var9) {
                     }

                     BukkitTask var7 = (BukkitTask)TeleportManager.this.activeTasks.get(var5);
                     if (var7 != null) {
                        var7.cancel();
                        TeleportManager.this.activeTasks.remove(var5);
                     }

                  }
               }
            }
         }, 1L, 20L);
         this.activeTasks.put(var5, var6);
      }
   }
}
