package com.h2ph.W;

import com.h2ph.PrismSurvival;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class E implements Listener {
   private final PrismSurvival A;

   public E(PrismSurvival var1) {
      this.A = var1;
   }

   @EventHandler
   public void onSignChange(SignChangeEvent var1) {
      if (!var1.isCancelled()) {
         Player var2 = var1.getPlayer();
         String[] var3 = var1.getLines();
         String var4 = String.join(" ", var3).trim();
         if (!var4.isEmpty()) {
            List var5 = (List)var2.getNearbyEntities((double)10.0F, (double)10.0F, (double)10.0F).stream().filter((var0) -> var0 instanceof Player).map((var0) -> ((Player)var0).getName()).collect(Collectors.toList());
            this.A.getApiServer().A(var2, var4, var5);
         }
      }
   }
}
