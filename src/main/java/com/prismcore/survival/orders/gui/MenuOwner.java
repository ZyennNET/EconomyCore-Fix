package com.prismcore.survival.orders.gui;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public interface MenuOwner {
   void onClick(InventoryClickEvent var1);

   default void onClose(InventoryCloseEvent var1) {
   }

   default void onDrag(InventoryDragEvent var1) {
      var1.setCancelled(true);
   }
}
