package com.prismcore.survival.orders.catalog;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.store.OrderManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public final class ItemCatalog {
   private ItemCatalog() {
   }

   public static List<Entry> build(PrismOrders var0) {
      ArrayList var1 = new ArrayList();

      for(Material var5 : Material.values()) {
         if (var5.isItem() && var5 != Material.AIR) {
            ItemStack var6 = new ItemStack(var5);
            String var7 = OrderManager.nice(var5);
            String var8 = (var5.name() + " " + var7).toLowerCase(Locale.ENGLISH);
            var1.add(new Entry(var6, var7, var8, var5));
         }
      }

      for(Enchantment var22 : Enchantment.values()) {
         if (var22 != null) {
            int var24 = Math.max(1, var22.getMaxLevel());
            NamespacedKey var26 = var22.getKey();
            String var27 = var26 != null ? var26.getKey() : var22.getName();
            String var9 = titleCase(var27.replace('_', ' '));

            for(int var10 = 1; var10 <= var24; ++var10) {
               ItemStack var11 = new ItemStack(Material.ENCHANTED_BOOK);
               EnchantmentStorageMeta var12 = (EnchantmentStorageMeta)var11.getItemMeta();
               if (var12 != null) {
                  var12.addStoredEnchant(var22, var10, true);
                  var11.setItemMeta(var12);
               }

               String var13 = romanNumeral(var10);
               String var14 = var10 == 1 ? var9 : var9 + " " + var13;
               String var15 = (var27 + " " + var9 + " " + var10 + " " + var13).toLowerCase(Locale.ENGLISH);
               var1.add(new Entry(var11, var14, var15, Material.ENCHANTED_BOOK));
            }
         }
      }

      for(PotionType var23 : PotionType.values()) {
         if (isUsefulPotionType(var23)) {
            PotionLabel var25 = describePotionType(var23);
            var1.add(entryPotion(Material.POTION, var23, var25));
            var1.add(entryPotion(Material.SPLASH_POTION, var23, var25));
            var1.add(entryPotion(Material.LINGERING_POTION, var23, var25));
            var1.add(entryPotion(Material.TIPPED_ARROW, var23, var25));
         }
      }

      return var1;
   }

   private static Entry entryPotion(Material var0, PotionType var1, PotionLabel var2) {
      ItemStack var3 = new ItemStack(var0);
      ItemMeta var4 = var3.getItemMeta();
      if (var4 instanceof PotionMeta var5) {
         try {
            var5.setBasePotionType(var1);
            var3.setItemMeta(var5);
         } catch (Throwable var8) {
         }
      }

      String var10000;
      switch (var0) {
         case POTION -> var10000 = "Potion";
         case SPLASH_POTION -> var10000 = "Splash Potion";
         case LINGERING_POTION -> var10000 = "Lingering Potion";
         case TIPPED_ARROW -> var10000 = "Tipped Arrow";
         default -> var10000 = "";
      }

      String var9 = var10000;
      String var6 = var2.prefix(var9);
      String var7 = var2.searchKey(var1);
      return new Entry(var3, var6, var7, var0);
   }

   private static boolean isUsefulPotionType(PotionType var0) {
      String var1 = var0.name();
      return !var1.equals("WATER") && !var1.equals("MUNDANE") && !var1.equals("THICK") && !var1.equals("AWKWARD");
   }

   private static PotionLabel describePotionType(PotionType var0) {
      String var1 = var0.name();
      boolean var2 = var1.startsWith("LONG_");
      boolean var3 = var1.startsWith("STRONG_");
      String var4 = var1.replaceFirst("^(LONG_|STRONG_)", "");
      String var5 = titleCase(var4.toLowerCase(Locale.ENGLISH).replace('_', ' '));
      return new PotionLabel(var5, var2, var3);
   }

   private static String titleCase(String var0) {
      String[] var1 = var0.split("\\s+");
      StringBuilder var2 = new StringBuilder();

      for(String var6 : var1) {
         if (!var6.isEmpty()) {
            var2.append(Character.toUpperCase(var6.charAt(0))).append(var6.substring(1)).append(' ');
         }
      }

      return var2.toString().trim();
   }

   private static String romanNumeral(int var0) {
      int[] var1 = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
      String[] var2 = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
      StringBuilder var3 = new StringBuilder();

      for(int var4 = 0; var4 < var1.length; ++var4) {
         while(var0 >= var1[var4]) {
            var0 -= var1[var4];
            var3.append(var2[var4]);
         }
      }

      return var3.toString();
   }

   public static final class Entry {
      public final ItemStack stack;
      public final String display;
      public final String search;
      public final Material base;

      public Entry(ItemStack var1, String var2, String var3, Material var4) {
         this.stack = var1;
         this.display = var2;
         this.search = var3;
         this.base = var4;
      }
   }

   private static final class PotionLabel {
      final String effectName;
      final boolean extended;
      final boolean strong;

      PotionLabel(String var1, boolean var2, boolean var3) {
         this.effectName = var1;
         this.extended = var2;
         this.strong = var3;
      }

      String prefix(String var1) {
         if (this.strong) {
            return var1 + " of " + this.effectName + " II";
         } else {
            return this.extended ? var1 + " of " + this.effectName + " (Extended)" : var1 + " of " + this.effectName;
         }
      }

      String searchKey(PotionType var1) {
         String var2 = (var1.name() + " " + this.effectName).toLowerCase(Locale.ENGLISH);
         if (this.strong) {
            var2 = var2 + " strong ii 2";
         }

         if (this.extended) {
            var2 = var2 + " long extended";
         }

         return var2;
      }
   }
}
