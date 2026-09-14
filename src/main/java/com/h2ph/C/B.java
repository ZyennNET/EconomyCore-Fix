package com.h2ph.c;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class B implements Listener {
   private final F A;

   public B(F var1) {
      this.A = var1;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      this.A.G().A(var1.getPlayer());
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      this.A.G().A();
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onBreak(BlockBreakEvent var1) {
      this.A.G().A(var1.getPlayer(), 1);
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPlace(BlockPlaceEvent var1) {
      this.A.G().B(var1.getPlayer(), 1);
   }
}
