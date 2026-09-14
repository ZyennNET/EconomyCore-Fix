package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class N implements CommandExecutor, Listener {
   private final PrismSurvival A;

   public N(PrismSurvival var1) {
      this.A = var1;
      var1.getCommand("fly").setExecutor(this);
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("economysmpcore.fly")) {
            return true;
         } else if (!this.A(var5)) {
            return true;
         } else {
            boolean var6 = !var5.getAllowFlight();
            var5.setAllowFlight(var6);
            var5.setFlying(var6);
            return true;
         }
      } else {
         return true;
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onWorldChange(PlayerChangedWorldEvent var1) {
      Player var2 = var1.getPlayer();
      if (!this.A(var2)) {
         this.C(var2);
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onTeleport(PlayerTeleportEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.getTo() != null && !this.A(var2)) {
         this.C(var2);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onToggleFlight(PlayerToggleFlightEvent var1) {
      Player var2 = var1.getPlayer();
      if (var1.isFlying() && !this.A(var2)) {
         var1.setCancelled(true);
         var2.setAllowFlight(false);
      }

   }

   private void C(Player var1) {
      this.A.getSchedulerAdapter().runTaskLater(() -> {
         if (var1.isOnline() && !this.A(var1)) {
            var1.setFlying(false);
            var1.setAllowFlight(false);
         }

      }, 2L);
   }

   private boolean A(Player var1) {
      if (var1.getGameMode() == GameMode.CREATIVE) {
         return true;
      } else {
         List var2 = this.A.getSurvivalConfig().getStringList("disabled-fly");
         return var2.stream().noneMatch((var1x) -> var1x.equalsIgnoreCase(var1.getWorld().getName()));
      }
   }
}
