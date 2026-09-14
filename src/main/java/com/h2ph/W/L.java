package com.h2ph.W;

import com.h2ph.PrismSurvival;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class L implements Listener {
   private final PrismSurvival A;

   public L(PrismSurvival var1) {
      this.A = var1;
   }

   @EventHandler
   public void onCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      if (!var1.isCancelled()) {
         this.A.getApiServer().A(var1.getPlayer(), var1.getMessage());
      }
   }
}
