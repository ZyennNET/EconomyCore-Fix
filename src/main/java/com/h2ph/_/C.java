package com.h2ph._;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class C implements Listener {
   private static final int A = 45;
   private final PrismSurvival B;

   public C(PrismSurvival var1) {
      this.B = var1;
      Bukkit.getPluginManager().registerEvents(this, var1);
   }

   public void open(Player var1, int var2) {
      D var3 = this.B.getDisguiseManager();
      ArrayList var4 = new ArrayList(var3.A());
      int var5 = Math.max(0, (int)Math.ceil((double)var4.size() / (double)45.0F) - 1);
      int var6 = Math.min(Math.max(var2, 0), var5);
      String var7 = ChatColor.translateAlternateColorCodes('&', var3.E());
      String var8 = var7 + " (Page " + (var6 + 1) + ")";
      Inventory var9 = Bukkit.createInventory(new _A(var6, var4), 54, var8);
      int var10 = var6 * 45;
      int var11 = Math.min(var10 + 45, var4.size());
      int var12 = 0;

      for(int var13 = var10; var13 < var11; ++var13) {
         D._A var14 = (D._A)var4.get(var13);
         var9.setItem(var12, this.A(var14));
         ++var12;
      }

      if (var6 > 0) {
         var9.setItem(45, this.A(Material.ARROW, "&aBack", List.of("&fPrevious page")));
      }

      if (var11 < var4.size()) {
         var9.setItem(53, this.A(Material.ARROW, "&aNext", List.of("&fNext page")));
      }

      boolean var17 = var3.B(var1.getUniqueId());
      if (var17) {
         String var18 = var3.A(var1.getUniqueId());
         D._A var15 = var3.A(var18);
         String var16 = var15 != null ? var15.A : var18;
         var9.setItem(49, this.A(Material.BARRIER, "&cRemove Disguise", List.of("&7Currently disguised as", "&f" + ChatColor.translateAlternateColorCodes('&', var16), "", "&eClick to undisguise")));
      } else {
         var9.setItem(49, this.A(Material.NAME_TAG, "&7Not Disguised", List.of("&7Pick a name below to", "&7disguise yourself.")));
      }

      var1.openInventory(var9);
   }

   private ItemStack A(D._A var1) {
      ItemStack var2 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var3 = (SkullMeta)var2.getItemMeta();
      if (var3 != null) {
         try {
            OfflinePlayer var4 = Bukkit.getOfflinePlayer(var1.C);
            var3.setOwningPlayer(var4);
         } catch (Exception var5) {
         }

         var3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d" + var1.A));
         ArrayList var6 = new ArrayList();
         var6.add(ChatColor.translateAlternateColorCodes('&', "&7Key: &f" + var1.B));
         var6.add("");
         var6.add(ChatColor.translateAlternateColorCodes('&', "&eClick to disguise as this name"));
         var3.setLore(var6);
         var2.setItemMeta(var3);
      }

      return var2;
   }

   private ItemStack A(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
         ArrayList var6 = new ArrayList();

         for(String var8 : var3) {
            var6.add(ChatColor.translateAlternateColorCodes('&', var8));
         }

         var5.setLore(var6);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      InventoryHolder var3 = var1.getInventory().getHolder();
      if (var3 instanceof _A var2) {
         var1.setCancelled(true);
         HumanEntity var4 = var1.getWhoClicked();
         if (var4 instanceof Player var7) {
            int var8 = var1.getRawSlot();
            if (var8 == 45) {
               this.open(var7, var2.B - 1);
            } else if (var8 == 53) {
               this.open(var7, var2.B + 1);
            } else if (var8 == 49) {
               D var9 = this.B.getDisguiseManager();
               if (var9.B(var7.getUniqueId())) {
                  var9.C(var7);
                  var7.closeInventory();
                  var7.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aDisguise removed."));
               }

            } else if (var8 >= 0 && var8 < 45) {
               int var5 = var2.B * 45 + var8;
               if (var5 >= 0 && var5 < var2.A.size()) {
                  D._A var6 = (D._A)var2.A.get(var5);
                  this.B.getDisguiseManager().A(var7, var6.B);
                  var7.closeInventory();
                  var7.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou are now disguised as &f" + var6.A + "&a."));
               }
            }
         }
      }
   }

   private static class _A implements InventoryHolder {
      private final int B;
      private final List<D._A> A;

      _A(int var1, List<D._A> var2) {
         this.B = var1;
         this.A = var2;
      }

      public Inventory getInventory() {
         return null;
      }
   }
}
