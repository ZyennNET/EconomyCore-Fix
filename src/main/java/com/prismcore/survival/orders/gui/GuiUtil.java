package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.Utils;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GuiUtil {
   public static ItemStack make(Material var0, String var1, List<String> var2) {
      ItemStack var3 = new ItemStack(var0);
      ItemMeta var4 = var3.getItemMeta();
      if (var4 == null) {
         var4 = Bukkit.getItemFactory().getItemMeta(var0);
      }

      if (var4 != null) {
         if (var1 != null) {
            var4.setDisplayName(Utils.formatColors(var1));
         }

         if (var2 != null) {
            var4.setLore(Utils.formatColors(var2));
         }

         var4.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         var3.setItemMeta(var4);
      }

      return var3;
   }

   public static ItemStack pane(Material var0) {
      return make(var0, "&7 ", (List)null);
   }

   public static void giveOrDrop(Player var0, ItemStack... var1) {
      HashMap var2 = var0.getInventory().addItem(var1);
      if (!var2.isEmpty()) {
         var2.values().forEach((var1x) -> var0.getWorld().dropItem(var0.getLocation(), var1x));
      }

   }
}
