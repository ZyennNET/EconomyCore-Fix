package com.prismcore.survival.auction;

import org.bukkit.inventory.ItemStack;

public class Transaction {
   private final ItemStack item;
   private final double price;
   private final String buyer;
   private final String seller;
   private final long timestamp;
   private final boolean isSale;

   public Transaction(ItemStack var1, double var2, String var4, String var5, long var6, boolean var8) {
      this.item = var1;
      this.price = var2;
      this.buyer = var4;
      this.seller = var5;
      this.timestamp = var6;
      this.isSale = var8;
   }

   public ItemStack getItem() {
      return this.item;
   }

   public double getPrice() {
      return this.price;
   }

   public String getBuyer() {
      return this.buyer;
   }

   public String getSeller() {
      return this.seller;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public boolean isSale() {
      return this.isSale;
   }
}
