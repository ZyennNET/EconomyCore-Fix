package com.prismcore.survival.orders.store;

import com.prismcore.survival.orders.PrismOrders;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;

public class FilterManager {
   private final PrismOrders pl;
   private final LinkedHashMap<String, Set<Material>> categories = new LinkedHashMap();

   public FilterManager(PrismOrders var1) {
      this.pl = var1;
      this.reload();
   }

   public void reload() {
      this.categories.clear();
      LinkedHashSet var1 = new LinkedHashSet();
      LinkedHashSet var2 = new LinkedHashSet();
      LinkedHashSet var3 = new LinkedHashSet();
      LinkedHashSet var4 = new LinkedHashSet();
      LinkedHashSet var5 = new LinkedHashSet();
      LinkedHashSet var6 = new LinkedHashSet();
      LinkedHashSet var7 = new LinkedHashSet();
      LinkedHashSet var8 = new LinkedHashSet();
      LinkedHashSet var9 = new LinkedHashSet();

      for(Material var13 : Material.values()) {
         if (!var13.isAir() && var13.isItem()) {
            String var14 = var13.name();
            if (var13.isEdible()) {
               var4.add(var13);
            } else if (!var14.endsWith("_PICKAXE") && !var14.endsWith("_AXE") && !var14.endsWith("_SHOVEL") && !var14.endsWith("_HOE") && !var14.equals("SHEARS") && !var14.equals("FLINT_AND_STEEL") && !var14.equals("FISHING_ROD") && !var14.equals("SPYGLASS") && !var14.equals("BRUSH")) {
               if (!var14.endsWith("_SWORD") && !var14.endsWith("_HELMET") && !var14.endsWith("_CHESTPLATE") && !var14.endsWith("_LEGGINGS") && !var14.endsWith("_BOOTS") && !var14.equals("BOW") && !var14.equals("CROSSBOW") && !var14.equals("SHIELD") && !var14.equals("TRIDENT") && !var14.equals("ARROW") && !var14.equals("SPECTRAL_ARROW") && !var14.equals("TIPPED_ARROW")) {
                  if (!var14.contains("POTION") && !var14.equals("GLASS_BOTTLE") && !var14.equals("HONEY_BOTTLE")) {
                     if (var14.contains("BOOK") && !var14.contains("SHELF")) {
                        var6.add(var13);
                     } else if (!var14.equals("COMPASS") && !var14.equals("CLOCK") && !var14.contains("BUCKET") && !var14.equals("LEAD") && !var14.equals("NAME_TAG") && !var14.equals("TOTEM_OF_UNDYING")) {
                        if (var13.isBlock()) {
                           var1.add(var13);
                        } else if (!var14.endsWith("_INGOT") && !var14.endsWith("_NUGGET") && !var14.endsWith("_GEMS") && !var14.equals("DIAMOND") && !var14.equals("EMERALD") && !var14.equals("NETHERITE_SCRAP") && !var14.equals("COAL") && !var14.equals("CHARCOAL") && !var14.equals("REDSTONE") && !var14.equals("LAPIS_LAZULI") && !var14.equals("RAW_IRON") && !var14.equals("RAW_GOLD") && !var14.equals("RAW_COPPER") && !var14.equals("QUARTZ") && !var14.equals("AMETHYST_SHARD")) {
                           var9.add(var13);
                        } else {
                           var7.add(var13);
                        }
                     } else {
                        var8.add(var13);
                     }
                  } else {
                     var5.add(var13);
                  }
               } else {
                  var3.add(var13);
               }
            } else {
               var2.add(var13);
            }
         }
      }

      if (!var1.isEmpty()) {
         this.categories.put("Blocks", var1);
      }

      if (!var2.isEmpty()) {
         this.categories.put("Tools", var2);
      }

      if (!var3.isEmpty()) {
         this.categories.put("Combat", var3);
      }

      if (!var4.isEmpty()) {
         this.categories.put("Food", var4);
      }

      if (!var5.isEmpty()) {
         this.categories.put("Potions", var5);
      }

      if (!var6.isEmpty()) {
         this.categories.put("Books", var6);
      }

      if (!var7.isEmpty()) {
         this.categories.put("Materials", var7);
      }

      if (!var8.isEmpty()) {
         this.categories.put("Utilities", var8);
      }

      if (!var9.isEmpty()) {
         this.categories.put("Misc", var9);
      }

   }

   public List<String> categoryNames() {
      return new ArrayList(this.categories.keySet());
   }

   public Set<Material> resolve(String var1) {
      if (var1 == null) {
         return Collections.emptySet();
      } else {
         for(Map.Entry var3 : this.categories.entrySet()) {
            if (((String)var3.getKey()).equalsIgnoreCase(var1)) {
               return (Set)var3.getValue();
            }
         }

         return Collections.emptySet();
      }
   }
}
