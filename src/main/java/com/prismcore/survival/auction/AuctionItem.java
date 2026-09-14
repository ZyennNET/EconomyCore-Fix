package com.prismcore.survival.auction;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class AuctionItem {
   private final UUID id;
   private final String seller;
   private final ItemStack itemStack;
   private double price;
   private final long listedAt;
   private final int duration;
   private final String searchName;
   private final String searchSeller;

   public AuctionItem(UUID var1, String var2, ItemStack var3, double var4, long var6, int var8) {
      this.id = var1;
      this.seller = var2;
      this.itemStack = var3.clone();
      this.price = var4;
      this.listedAt = var6;
      this.duration = var8;
      this.searchName = Utils.prettifyMaterialName(var3.getType()).toLowerCase();
      this.searchSeller = var2.toLowerCase();
   }

   public UUID getId() {
      return this.id;
   }

   public String getSeller() {
      return this.seller;
   }

   public ItemStack getItemStack() {
      return this.itemStack.clone();
   }

   public double getPrice() {
      return this.price;
   }

   public void setPrice(double var1) {
      this.price = var1;
   }

   public long getListedAt() {
      return this.listedAt;
   }

   public int getDuration() {
      return this.duration;
   }

   public String getSearchName() {
      return this.searchName;
   }

   public String getSearchSeller() {
      return this.searchSeller;
   }
}
