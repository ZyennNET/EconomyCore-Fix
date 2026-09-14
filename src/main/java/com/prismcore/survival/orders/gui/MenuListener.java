package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {
   private final PrismOrders plugin;

   public MenuListener(PrismOrders var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      InventoryHolder var2 = var1.getInventory().getHolder();
      if (var2 instanceof MenuOwner var3) {
         var3.onClick(var1);
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent var1) {
      InventoryHolder var2 = var1.getInventory().getHolder();
      if (var2 instanceof MenuOwner var3) {
         var3.onClose(var1);
      }

   }

   @EventHandler
   public void onDrag(InventoryDragEvent var1) {
      InventoryHolder var2 = var1.getInventory().getHolder();
      if (var2 instanceof MenuOwner var3) {
         var3.onDrag(var1);
      }
   }
}
