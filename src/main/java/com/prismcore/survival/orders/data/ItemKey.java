package com.prismcore.survival.orders.data;

import com.prismcore.survival.orders.store.OrderManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

public final class ItemKey {
   public final Material material;
   public final PotionType potionType;
   public final Map<String, Integer> enchants;

   private ItemKey(Material var1, PotionType var2, Map<String, Integer> var3) {
      this.material = var1;
      this.potionType = var2;
      this.enchants = var3 == null ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap(var3));
   }

   public static ItemKey of(Material var0) {
      return new ItemKey(var0, (PotionType)null, (Map)null);
   }

   public static ItemKey potion(Material var0, PotionType var1) {
      return new ItemKey(var0, var1, (Map)null);
   }

   public static ItemKey book(Map<Enchantment, Integer> var0) {
      LinkedHashMap var1 = new LinkedHashMap();

      for(Map.Entry var3 : var0.entrySet()) {
         var1.put(keyOf((Enchantment)var3.getKey()), (Integer)var3.getValue());
      }

      return new ItemKey(Material.ENCHANTED_BOOK, (PotionType)null, var1);
   }

   public static ItemKey fromStack(ItemStack var0) {
      if (var0 != null && var0.getType() != Material.AIR) {
         Material var3 = var0.getType();
         ItemMeta var2;
         if (var3 == Material.ENCHANTED_BOOK && (var2 = var0.getItemMeta()) instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta var11 = (EnchantmentStorageMeta)var2;
            Map var9 = var11.getStoredEnchants();
            return book(var9);
         } else {
            ItemMeta var1;
            if (isPotionLike(var3) && (var1 = var0.getItemMeta()) instanceof PotionMeta) {
               PotionMeta var10 = (PotionMeta)var1;

               try {
                  PotionType var12 = var10.getBasePotionType();
                  return potion(var3, var12);
               } catch (Throwable var8) {
                  return of(var3);
               }
            } else {
               LinkedHashMap var4 = new LinkedHashMap();
               ItemMeta var5 = var0.getItemMeta();
               if (var5 != null && var5.hasEnchants()) {
                  for(Map.Entry var7 : var5.getEnchants().entrySet()) {
                     var4.put(keyOf((Enchantment)var7.getKey()), (Integer)var7.getValue());
                  }
               }

               return new ItemKey(var3, (PotionType)null, var4.isEmpty() ? null : var4);
            }
         }
      } else {
         return null;
      }
   }

   public boolean matches(ItemStack var1) {
      if (var1 != null && var1.getType() == this.material) {
         if (this.material == Material.ENCHANTED_BOOK) {
            ItemMeta var13 = var1.getItemMeta();
            if (!(var13 instanceof EnchantmentStorageMeta)) {
               return false;
            } else {
               EnchantmentStorageMeta var15 = (EnchantmentStorageMeta)var13;
               Map var17 = var15.getStoredEnchants();

               for(Map.Entry var19 : this.enchants.entrySet()) {
                  boolean var20 = false;

                  for(Map.Entry var22 : var17.entrySet()) {
                     String var10 = keyOf((Enchantment)var22.getKey());
                     if (var10.equalsIgnoreCase((String)var19.getKey()) && Objects.equals(var22.getValue(), var19.getValue())) {
                        var20 = true;
                        break;
                     }
                  }

                  if (!var20) {
                     return false;
                  }
               }

               return true;
            }
         } else if (isPotionLike(this.material)) {
            ItemMeta var12 = var1.getItemMeta();
            if (!(var12 instanceof PotionMeta)) {
               return false;
            } else {
               PotionMeta var14 = (PotionMeta)var12;

               try {
                  PotionType var16 = var14.getBasePotionType();
                  return Objects.equals(this.potionType, var16);
               } catch (Throwable var11) {
                  return this.potionType == null;
               }
            }
         } else {
            if (!this.enchants.isEmpty()) {
               ItemMeta var2 = var1.getItemMeta();
               if (var2 == null) {
                  return false;
               }

               Map var3 = var2.getEnchants();

               for(Map.Entry var5 : this.enchants.entrySet()) {
                  boolean var6 = false;

                  for(Map.Entry var8 : var3.entrySet()) {
                     String var9 = keyOf((Enchantment)var8.getKey());
                     if (var9.equalsIgnoreCase((String)var5.getKey()) && Objects.equals(var8.getValue(), var5.getValue())) {
                        var6 = true;
                        break;
                     }
                  }

                  if (!var6) {
                     return false;
                  }
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean isVariant() {
      return this.material == Material.ENCHANTED_BOOK && !this.enchants.isEmpty() || isPotionLike(this.material) && this.potionType != null || !this.enchants.isEmpty();
   }

   public static boolean isPotionLike(Material var0) {
      return var0 == Material.POTION || var0 == Material.SPLASH_POTION || var0 == Material.LINGERING_POTION || var0 == Material.TIPPED_ARROW;
   }

   public ItemStack buildIcon() {
      ItemStack var2 = new ItemStack(this.material);
      if (this.material == Material.ENCHANTED_BOOK && !this.enchants.isEmpty()) {
         ItemMeta var10 = var2.getItemMeta();
         if (var10 instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta var12 = (EnchantmentStorageMeta)var10;

            for(Map.Entry var6 : this.enchants.entrySet()) {
               Enchantment var7 = findByKey((String)var6.getKey());
               if (var7 != null) {
                  var12.addStoredEnchant(var7, (Integer)var6.getValue(), true);
               }
            }

            var2.setItemMeta(var12);
         }
      } else if (isPotionLike(this.material) && this.potionType != null) {
         ItemMeta var9 = var2.getItemMeta();
         if (var9 instanceof PotionMeta) {
            PotionMeta var11 = (PotionMeta)var9;

            try {
               var11.setBasePotionType(this.potionType);
               var2.setItemMeta(var11);
            } catch (Throwable var8) {
            }
         }
      } else {
         ItemMeta var1;
         if (!this.enchants.isEmpty() && (var1 = var2.getItemMeta()) != null) {
            for(Map.Entry var4 : this.enchants.entrySet()) {
               Enchantment var5 = findByKey((String)var4.getKey());
               if (var5 != null) {
                  var1.addEnchant(var5, (Integer)var4.getValue(), true);
               }
            }

            var2.setItemMeta(var1);
         }
      }

      return var2;
   }

   private static Enchantment findByKey(String var0) {
      for(Enchantment var4 : Enchantment.values()) {
         if (var4 != null && keyOf(var4).equalsIgnoreCase(var0)) {
            return var4;
         }
      }

      return null;
   }

   private static String keyOf(Enchantment var0) {
      if (var0 == null) {
         return "";
      } else {
         NamespacedKey var1 = var0.getKey();
         if (var1 != null) {
            return var1.getKey();
         } else {
            String var2 = var0.getName();
            return var2 != null ? var2.toLowerCase(Locale.ENGLISH) : "";
         }
      }
   }

   public String displayName() {
      if (this.material == Material.ENCHANTED_BOOK && !this.enchants.isEmpty()) {
         if (this.enchants.size() == 1) {
            Map.Entry var5 = (Map.Entry)this.enchants.entrySet().iterator().next();
            return bookEnchantLabel((String)var5.getKey(), (Integer)var5.getValue());
         } else {
            String var10000 = (String)this.enchants.entrySet().stream().limit(3L).map((var0) -> bookEnchantLabel((String)var0.getKey(), (Integer)var0.getValue())).collect(Collectors.joining(", "));
            return "Enchanted Book (" + var10000 + (this.enchants.size() > 3 ? ", ..." : "") + ")";
         }
      } else if (isPotionLike(this.material) && this.potionType != null) {
         String var2 = potionEffectName(this.potionType);
         boolean var3 = this.potionType.name().startsWith("STRONG_");
         boolean var4 = this.potionType.name().startsWith("LONG_");
         String var1;
         switch (this.material) {
            case POTION -> var1 = "Potion of ";
            case SPLASH_POTION -> var1 = "Splash Potion of ";
            case LINGERING_POTION -> var1 = "Lingering Potion of ";
            case TIPPED_ARROW -> var1 = "Tipped Arrow of ";
            default -> var1 = "";
         }

         if (var3) {
            return var1 + var2 + " II";
         } else {
            return var4 ? var1 + var2 + " (Extended)" : var1 + var2;
         }
      } else {
         return OrderManager.nice(this.material);
      }
   }

   public List<String> enchantLoreLines(String var1) {
      if (this.enchants.isEmpty()) {
         return List.of();
      } else {
         ArrayList var2 = new ArrayList();

         for(Map.Entry var4 : this.enchants.entrySet()) {
            var2.add((var1 == null ? "&7" : var1) + bookEnchantLabel((String)var4.getKey(), (Integer)var4.getValue()));
         }

         return var2;
      }
   }

   private static String bookEnchantLabel(String var0, int var1) {
      String var2 = var0;
      int var3 = var0.indexOf(58);
      if (var3 >= 0 && var3 + 1 < var0.length()) {
         var2 = var0.substring(var3 + 1);
      }

      var2 = var2.replace('_', ' ').toLowerCase(Locale.ENGLISH);
      var2 = title(var2);
      Enchantment var4 = findByKey(var0);
      int var5 = 1;
      if (var4 != null) {
         try {
            var5 = Math.max(1, var4.getMaxLevel());
         } catch (Throwable var7) {
         }
      }

      return var5 > 1 && var1 > 1 ? var2 + " " + roman(var1) : var2;
   }

   private static String potionEffectName(PotionType var0) {
      String var1 = var0.name().replaceFirst("^(LONG_|STRONG_)", "");
      return title(var1.replace('_', ' ').toLowerCase(Locale.ENGLISH));
   }

   private static String title(String var0) {
      String[] var1 = var0.split("\\s+");
      StringBuilder var2 = new StringBuilder();

      for(String var6 : var1) {
         if (!var6.isEmpty()) {
            var2.append(Character.toUpperCase(var6.charAt(0))).append(var6.substring(1)).append(' ');
         }
      }

      return var2.toString().trim();
   }

   private static String roman(int var0) {
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

   public String serialize() {
      if (!this.enchants.isEmpty() && this.material == Material.ENCHANTED_BOOK) {
         String var2 = (String)this.enchants.entrySet().stream().map((var0) -> {
            String var10000 = (String)var0.getKey();
            return var10000 + "=" + String.valueOf(var0.getValue());
         }).collect(Collectors.joining(","));
         return "BOOK|" + var2;
      } else if (isPotionLike(this.material) && this.potionType != null) {
         String var3 = this.material.name();
         return var3 + "|POTION:" + this.potionType.name();
      } else if (!this.enchants.isEmpty()) {
         String var1 = (String)this.enchants.entrySet().stream().map((var0) -> {
            String var10000 = (String)var0.getKey();
            return var10000 + "=" + String.valueOf(var0.getValue());
         }).collect(Collectors.joining(","));
         String var10000 = this.material.name();
         return var10000 + "|ENCH:" + var1;
      } else {
         return this.material.name();
      }
   }

   public static ItemKey deserialize(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         if (var0.startsWith("BOOK|")) {
            String var10 = var0.substring("BOOK|".length());
            LinkedHashMap var12 = new LinkedHashMap();
            if (!var10.isEmpty()) {
               for(String var17 : var10.split(",")) {
                  String[] var18 = var17.split("=");
                  if (var18.length == 2) {
                     var12.put(var18[0], Integer.parseInt(var18[1]));
                  }
               }
            }

            return new ItemKey(Material.ENCHANTED_BOOK, (PotionType)null, var12);
         } else if (var0.contains("|POTION:")) {
            String[] var9 = var0.split("\\|POTION:");
            Material var11 = Material.matchMaterial(var9[0]);
            PotionType var13 = PotionType.valueOf(var9[1]);
            return new ItemKey(var11, var13, (Map)null);
         } else if (!var0.contains("|ENCH:")) {
            return new ItemKey(Material.matchMaterial(var0), (PotionType)null, (Map)null);
         } else {
            String[] var1 = var0.split("\\|ENCH:");
            Material var2 = Material.matchMaterial(var1[0]);
            LinkedHashMap var3 = new LinkedHashMap();
            if (var1.length > 1 && !var1[1].isEmpty()) {
               for(String var7 : var1[1].split(",")) {
                  String[] var8 = var7.split("=");
                  if (var8.length == 2) {
                     var3.put(var8[0], Integer.parseInt(var8[1]));
                  }
               }
            }

            return new ItemKey(var2, (PotionType)null, var3);
         }
      } else {
         return null;
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ItemKey)) {
         return false;
      } else {
         ItemKey var2 = (ItemKey)var1;
         return this.material == var2.material && Objects.equals(this.potionType, var2.potionType) && Objects.equals(this.enchants, var2.enchants);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.material, this.potionType, this.enchants});
   }

   public String toString() {
      return "ItemKey[" + this.serialize() + "]";
   }
}
