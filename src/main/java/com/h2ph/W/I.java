package com.h2ph.W;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class I implements Listener {
   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      InventoryHolder var2 = var1.getView().getTopInventory().getHolder();
      boolean var3 = var2 instanceof com.h2ph.J.D.K._B || var2 instanceof com.h2ph.J.D.K._A;
      if (var3) {
         var1.setCancelled(true);
         Inventory var4 = var1.getClickedInventory();
         if (var4 != null && var4.equals(var1.getView().getTopInventory())) {
            if (var1.getWhoClicked() instanceof Player) {
               Player var5 = (Player)var1.getWhoClicked();
               ItemStack var6 = var1.getCurrentItem();
               String var7 = com.h2ph.J.D.K.getActionId(var6);
               if (var7 != null) {
                  var5.playSound(var5.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
                  com.h2ph.J.D.K.handleAction(var5, var7);
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
   }
}
