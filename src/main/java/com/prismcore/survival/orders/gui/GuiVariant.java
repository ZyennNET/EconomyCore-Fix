package com.prismcore.survival.orders.gui;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public final class GuiVariant {
   private GuiVariant() {
   }

   public static ItemStack merge(ItemStack var0, ItemStack var1) {
      if (var0 != null && var1 != null) {
         Material var3 = var1.getType();
         if (var0.getType() != var3) {
            var0.setType(var3);
         }

         ItemMeta var4 = var0.getItemMeta();
         ItemMeta var5 = var1.getItemMeta();
         if (var5 == null) {
            return var0;
         } else {
            String var6 = var4 != null && var4.hasDisplayName() ? var4.getDisplayName() : null;
            List var7 = var4 != null && var4.hasLore() ? var4.getLore() : null;
            Set var2 = var4 != null ? var4.getItemFlags() : Collections.emptySet();
            if (var4 instanceof PotionMeta && var5 instanceof PotionMeta) {
               try {
                  PotionMeta var8 = (PotionMeta)var5;
                  PotionMeta var9 = (PotionMeta)var4;
                  if (var8.hasColor()) {
                     var9.setColor(var8.getColor());
                  }

                  try {
                     var9.setBasePotionType(var8.getBasePotionType());
                  } catch (Throwable var11) {
                  }

                  var0.setItemMeta(var9);
               } catch (Throwable var12) {
               }
            }

            if (var4 instanceof EnchantmentStorageMeta && var5 instanceof EnchantmentStorageMeta) {
               EnchantmentStorageMeta var13 = (EnchantmentStorageMeta)var4;
               EnchantmentStorageMeta var15 = (EnchantmentStorageMeta)var5;
               var15.getStoredEnchants().forEach((var1x, var2x) -> var13.addStoredEnchant(var1x, var2x, true));
               var0.setItemMeta(var13);
            }

            ItemMeta var14 = Bukkit.getItemFactory().asMetaFor(var1.getItemMeta(), var0.getType());
            if (var6 != null) {
               var14.setDisplayName(var6);
            }

            if (var7 != null) {
               var14.setLore(var7);
            }

            if (var2 != null && !var2.isEmpty()) {
               var14.addItemFlags((ItemFlag[])var2.toArray(new ItemFlag[0]));
            }

            var0.setItemMeta(var14);
            return var0;
         }
      } else {
         return var0;
      }
   }
}
