package com.h2ph.W;

import com.prismcore.survival.manager.SpawnManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class B implements Listener {
   private final com.h2ph.J.B.A.B A;

   public B(com.h2ph.J.B.A.B var1) {
      this.A = var1;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent var1) {
      Player var2 = var1.getEntity();
      if (this.A.N(var2)) {
         this.A.D(var2);
      } else {
         if (this.A.I(var2)) {
            this.A.M(var2);
            Player var3 = var2.getKiller();
            Player var4 = this.A.E(var2);
            Player var5 = var3 != null ? var3 : var4;
            if (var5 == null || !var5.getUniqueId().equals(var4.getUniqueId())) {
               var5 = var4;
            }

            if (var5 != null) {
               com.h2ph.J.B.A.B._B var6 = this.A.J(var2) ? com.h2ph.J.B.A.B._B.A : com.h2ph.J.B.A.B._B.C;
               this.A.A(var5, var2, var6);
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerRespawn(PlayerRespawnEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.B(var2)) {
         Location var6 = this.A.C();
         if (var6 != null && var6.getWorld() != null) {
            var1.setRespawnLocation(var6);
         } else {
            SpawnManager.SpawnPoint var4 = this.A.G().getSpawnManager().getSpawn("spawn");
            if (var4 != null) {
               Location var5 = var4.toBukkitLocation();
               if (var5 != null) {
                  var1.setRespawnLocation(var5);
               }
            }

         }
      } else {
         Location var3 = this.A.L(var2);
         if (var3 != null) {
            var1.setRespawnLocation(var3);
            var2.setGameMode(GameMode.SPECTATOR);
            this.A.G().getSchedulerAdapter().runEntityTaskLater(var2, () -> var2.setGameMode(GameMode.SPECTATOR), 1L);
         }

      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.I(var2)) {
         this.A.G(var2);
         var2.setHealth((double)0.0F);
      }

   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.C(var2.getLocation())) {
         this.A.G().getSchedulerAdapter().runEntityTaskLater(var2, () -> {
            if (var2.isOnline()) {
               this.A.F(var2);
            }

         }, 1L);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.I(var2) || this.A.N(var2)) {
         String var3 = this.A.A(var2);
         if (var3 != null && this.A.C(var1.getBlock().getLocation())) {
            this.A.A(var3, var1.getBlock().getState());
            var1.setDropItems(false);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBlockPlace(BlockPlaceEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.I(var2) || this.A.N(var2)) {
         String var3 = this.A.A(var2);
         if (var3 != null && this.A.C(var1.getBlock().getLocation())) {
            this.A.A(var3, var1.getBlockReplacedState());
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onEntityExplode(EntityExplodeEvent var1) {
      if (!var1.blockList().isEmpty()) {
         String var2 = this.A.B(var1.getLocation());
         if (var2 == null && !var1.blockList().isEmpty()) {
            var2 = this.A.B(((Block)var1.blockList().get(0)).getLocation());
         }

         if (var2 != null) {
            var1.setYield(0.0F);

            for(Block var4 : var1.blockList()) {
               this.A.A(var2, var4.getState());
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBlockExplode(BlockExplodeEvent var1) {
      if (!var1.blockList().isEmpty()) {
         String var2 = this.A.B(var1.getBlock().getLocation());
         if (var2 != null) {
            var1.setYield(0.0F);

            for(Block var4 : var1.blockList()) {
               this.A.A(var2, var4.getState());
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBlockSpread(BlockSpreadEvent var1) {
      String var2 = this.A.B(var1.getBlock().getLocation());
      if (var2 != null) {
         this.A.A(var2, var1.getBlock().getState());
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBlockBurn(BlockBurnEvent var1) {
      String var2 = this.A.B(var1.getBlock().getLocation());
      if (var2 != null) {
         this.A.A(var2, var1.getBlock().getState());
      }

   }

   @EventHandler
   public void onPlayerTeleport(PlayerTeleportEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.N(var2) && var1.getCause() != TeleportCause.ENDER_PEARL && var1.getCause() != TeleportCause.CHORUS_FRUIT) {
         this.A.D(var2);
      }

   }

   @EventHandler
   public void onPlayerCommand(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getMessage().toLowerCase();
      if (!var3.startsWith("/duel leave")) {
         if ((this.A.I(var2) || this.A.N(var2)) && this.A.A(var1.getMessage())) {
            var1.setCancelled(true);
            String var8 = ChatColor.translateAlternateColorCodes('&', "&cYou cannot able to use this command on a duels.");
            var2.sendMessage(var8);
            var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var8));

            try {
               var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            } catch (Exception var6) {
            }

         } else {
            if (this.A.I(var2) && this.A.D(var1.getMessage())) {
               var1.setCancelled(true);
               String var4 = ChatColor.translateAlternateColorCodes('&', "&cYou cannot use this command while on duels! &7Type &a/duel leave&7 to left the match.");
               var2.sendMessage(var4);

               try {
                  var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               } catch (Exception var7) {
               }
            }

         }
      }
   }
}
