package com.h2ph._;

import com.h2ph.PrismSurvival;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class A implements Listener {
   private final PrismSurvival A;

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      this.A.getDisguiseManager().A(var1.getPlayer());
   }
}
