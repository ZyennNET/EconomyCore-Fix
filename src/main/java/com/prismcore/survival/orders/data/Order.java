package com.prismcore.survival.orders.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class Order {
   public UUID id;
   public UUID owner;
   public ItemKey key;
   public int requested;
   public int delivered;
   public double priceEach;
   public double paid;
   public boolean canceled;
   public boolean completed;
   public long createdAt;
   public long expiresAt;
   public final List<ItemStack> storage = new ArrayList();

   public int remainingAmount() {
      return Math.max(0, this.requested - this.delivered);
   }

   public double totalPrice() {
      return (double)this.requested * this.priceEach;
   }

   public String getExpirationString() {
      long var1 = System.currentTimeMillis();
      if (var1 >= this.expiresAt) {
         return "Expired";
      } else {
         long var3 = this.expiresAt - var1;
         long var5 = var3 / 86400000L;
         long var7 = var3 / 3600000L % 24L;
         long var9 = var3 / 60000L % 60L;
         if (var5 > 0L) {
            return var5 + "d " + var7 + "h " + var9 + "m";
         } else {
            return var7 > 0L ? var7 + "h " + var9 + "m" : var9 + "m";
         }
      }
   }
}
