package com.prismcore.survival.survival;

import com.h2ph.PrismSurvival;
import java.util.concurrent.atomic.AtomicReference;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class ItemMerger implements Listener {
   private final PrismSurvival plugin;
   private final double RADIUS = (double)5.0F;
   private final int MAX_STACK = 64;

   public ItemMerger(PrismSurvival var1) {
      this.plugin = var1;
   }

   @EventHandler
   public void onItemSpawn(ItemSpawnEvent var1) {
      final Item var2 = var1.getEntity();
      final AtomicReference var3 = new AtomicReference();
      BukkitTask var4 = this.plugin.getSchedulerAdapter().runEntityTaskTimer(var2, new Runnable() {
         public void run() {
            if (var2.isValid() && !var2.isDead()) {
               for(Entity var2x : var2.getNearbyEntities((double)5.0F, (double)5.0F, (double)5.0F)) {
                  if (var2x instanceof Item) {
                     Item var3x = (Item)var2x;
                     if (var3x.isValid() && !var3x.isDead() && !(var2.getLocation().distance(var3x.getLocation()) > (double)5.0F)) {
                        ItemMerger.this.tryMerge(var3x, var2);
                        if (var2.getItemStack().getAmount() >= 64) {
                           break;
                        }
                     }
                  }
               }

            } else {
               BukkitTask var1 = (BukkitTask)var3.get();
               if (var1 != null) {
                  var1.cancel();
               }

            }
         }
      }, 100L, 100L);
      var3.set(var4);
   }

   private void tryMerge(Item var1, Item var2) {
      ItemStack var3 = var1.getItemStack();
      ItemStack var4 = var2.getItemStack();
      if (var3.isSimilar(var4)) {
         int var5 = var3.getAmount();
         int var6 = var4.getAmount();
         if (var6 < 64) {
            int var7 = Math.min(var5, 64 - var6);
            if (var7 > 0) {
               var4.setAmount(var6 + var7);
               var2.setItemStack(var4);
               if (var5 - var7 <= 0) {
                  var1.remove();
               } else {
                  var3.setAmount(var5 - var7);
                  var1.setItemStack(var3);
               }
            }

         }
      }
   }
}
