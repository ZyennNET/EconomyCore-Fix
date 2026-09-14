package com.prismcore.survival.tools;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DrillClickListener implements Listener {
   private final ToolsManager manager;

   public DrillClickListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      HumanEntity var2 = var1.getWhoClicked();
      if (var2 instanceof Player var3) {
         this.manager.updatePlayerTools(var3);
      }
   }
}
