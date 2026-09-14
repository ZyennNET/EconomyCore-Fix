package com.prismcore.survival.tools;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class DrillInventoryListener implements Listener {
   private final ToolsManager manager;

   public DrillInventoryListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler
   public void onInventoryOpen(InventoryOpenEvent var1) {
      Player var2 = (Player)var1.getPlayer();
      this.manager.updatePlayerTools(var2);
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent var1) {
      this.manager.updatePlayerTools(var1.getPlayer());
   }

   @EventHandler
   public void onHotbarScroll(PlayerItemHeldEvent var1) {
      this.manager.updatePlayerTools(var1.getPlayer());
   }

   @EventHandler
   public void onSwap(PlayerSwapHandItemsEvent var1) {
      this.manager.updatePlayerTools(var1.getPlayer());
   }
}
