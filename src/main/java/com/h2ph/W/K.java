package com.h2ph.W;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import com.prismcore.survival.manager.SpawnManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class K implements Listener {
   private final PrismSurvival A;

   public K(PrismSurvival var1) {
      this.A = var1;
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      PlayerData var2 = this.A.getPlayerDataManager().get(var1.getPlayer().getUniqueId());
      if (var2 != null && !var2.getOfflinePayments().isEmpty()) {
         for(String var4 : var2.getOfflinePayments()) {
            String var5 = ChatColor.translateAlternateColorCodes('&', var4);
            var1.getPlayer().sendMessage(var5);
            var1.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var5));
         }

         var2.clearOfflinePayments();
         this.A.getSchedulerAdapter().runTaskAsync(() -> this.A.getPlayerDataManager().savePlayer(var1.getPlayer().getUniqueId()));
      }

      if (var2 != null && var2.getPendingRtpType() != null && (var2.getPendingSpawnName() == null || !var2.getPendingSpawnName().equals("RTP"))) {
         String var7 = var2.getPendingRtpTargetServer();
         String var9 = this.A.getSurvivalConfig().getString("server-id");
         if (var7 != null && var9 != null && !var7.equalsIgnoreCase(var9)) {
            var2.setPendingRtpType((String)null);
            var2.setPendingRtpTargetServer((String)null);
            this.A.getSchedulerAdapter().runTaskAsync(() -> this.A.getPlayerDataManager().savePlayer(var1.getPlayer().getUniqueId()));
            return;
         }

         String var11 = var2.getPendingRtpType();
         var2.setPendingRtpType((String)null);
         var2.setPendingRtpTargetServer((String)null);
         this.A.getSchedulerAdapter().runTaskAsync(() -> this.A.getPlayerDataManager().savePlayer(var1.getPlayer().getUniqueId()));
         String var6 = this.A.getSurvivalConfig().getString("current-region");
         if (var6 != null) {
            this.A.getSchedulerAdapter().runTaskLater(() -> com.h2ph.E.A.A(var1.getPlayer(), var6, var11, true, true), 10L);
         }
      }

      if (var2 != null && var2.getPendingSpawnName() != null) {
         String var8 = var2.getPendingSpawnName();
         String var10 = var2.getPendingRtpTargetServer();
         String var12 = this.A.getSurvivalConfig().getString("server-id");
         if (var10 != null && var12 != null && !var10.equalsIgnoreCase(var12)) {
         }

         var2.setPendingSpawnName((String)null);
         var2.setPendingRtpTargetServer((String)null);
         var2.setPendingRtpType((String)null);
         this.A.getSchedulerAdapter().runTaskAsync(() -> this.A.getPlayerDataManager().savePlayer(var1.getPlayer().getUniqueId()));
         this.A.getSchedulerAdapter().runTaskLater(() -> {
            Location var4 = null;
            if (var2.getPendingSpawnWorld() != null) {
               World var5 = Bukkit.getWorld(var2.getPendingSpawnWorld());
               if (var5 == null) {
                  var5 = Bukkit.createWorld(new WorldCreator(var2.getPendingSpawnWorld()));
               }

               if (var5 != null) {
                  var4 = new Location(var5, var2.getPendingSpawnX(), var2.getPendingSpawnY(), var2.getPendingSpawnZ(), var2.getPendingSpawnYaw(), var2.getPendingSpawnPitch());
               }
            }

            if (var4 == null && this.A.getSpawnManager() != null) {
               SpawnManager.SpawnPoint var6 = this.A.getSpawnManager().getSpawn(var8);
               if (var6 != null) {
                  var4 = var6.toBukkitLocation();
               }
            }

            if (var4 != null) {
               var1.getPlayer().teleportAsync(var4).thenAccept((var2x) -> {
                  if (var2x) {
                     String var3;
                     if ("RTP".equals(var8)) {
                        var3 = ChatColor.translateAlternateColorCodes('&', "&7You teleported to a random location");
                     } else {
                        String var10000 = String.valueOf(ChatColor.GRAY);
                        var3 = var10000 + "You teleported to " + String.valueOf(ChatColor.DARK_PURPLE) + "ѕᴘᴀᴡɴ";
                     }

                     var1.getPlayer().sendMessage(var3);
                     var1.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var3));
                     var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                  }

               });
               var2.setPendingSpawnName((String)null);
               var2.setPendingSpawnWorld((String)null);
               var2.setPendingSpawnX((Double)null);
               var2.setPendingSpawnY((Double)null);
               var2.setPendingSpawnZ((Double)null);
               var2.setPendingSpawnYaw((Float)null);
               var2.setPendingSpawnPitch((Float)null);
               this.A.getSchedulerAdapter().runTaskAsync(() -> this.A.getPlayerDataManager().getPlayerDAO().A(var2));
            } else {
               this.A.getLogger().warning("Pending spawn '" + var8 + "' location could not be resolved.");
            }

         }, 20L);
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      this.A.getPlayerDataManager().unload(var1.getPlayer().getUniqueId());
   }
}
