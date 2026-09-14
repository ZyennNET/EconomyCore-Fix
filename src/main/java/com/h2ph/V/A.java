package com.h2ph.V;

import com.h2ph.J.D.K;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class A implements Runnable {
   public void run() {
      for(Player var2 : Bukkit.getOnlinePlayers()) {
         Inventory var3 = var2.getOpenInventory().getTopInventory();
         InventoryHolder var4 = var3.getHolder();
         if (var4 instanceof K._B) {
            K.updateItems(var3, var2);
         } else if (var4 instanceof K._A) {
            K.updateOverworldItems(var3, var2);
         }
      }

   }
}
