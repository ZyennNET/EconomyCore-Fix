package com.h2ph.J.A;

import com.h2ph.PrismSurvival;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class A implements InventoryHolder {
   private final PrismSurvival A;
   private final UUID B;
   private Inventory C;

   public A(PrismSurvival var1, UUID var2) {
      this.A = var1;
      this.B = var2;
   }

   public void open(Player var1) {
      String var2 = this.B();
      String var3 = ChatColor.translateAlternateColorCodes('&', "&8" + var2 + "'s Punishments");
      this.C = Bukkit.createInventory(this, 54, var3);
      this.A();
      this.A(var1);
      var1.openInventory(this.C);
   }

   private void A() {
      ItemStack var1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var2 = var1.getItemMeta();
      var2.setDisplayName(" ");
      var1.setItemMeta(var2);

      for(int var3 = 0; var3 < 54; ++var3) {
         this.C.setItem(var3, var1);
      }

   }

   private void A(Player var1) {
      com.h2ph.J.B.F.A var2 = this.A.getOffendPlugin();
      if (var2 == null) {
         ItemStack var12 = new ItemStack(Material.BARRIER);
         ItemMeta var13 = var12.getItemMeta();
         var13.setDisplayName(String.valueOf(ChatColor.RED) + "Punishment system not available");
         var12.setItemMeta(var13);
         this.C.setItem(22, var12);
      } else {
         List var3 = var2.getAllActiveBans();
         ArrayList var4 = new ArrayList();

         for(com.h2ph.T.A.C._A var6 : var3) {
            if (var6.A != null && var6.A.equals(this.B.toString())) {
               var4.add(var6);
            }
         }

         if (var4.isEmpty()) {
            ItemStack var15 = new ItemStack(Material.PAPER);
            ItemMeta var17 = var15.getItemMeta();
            var17.setDisplayName(String.valueOf(ChatColor.GREEN) + "No punishments found");
            var15.setItemMeta(var17);
            this.C.setItem(22, var15);
         } else {
            SimpleDateFormat var14 = new SimpleDateFormat("yyyy-MM-dd");
            int var16 = 0;

            for(com.h2ph.T.A.C._A var8 : var4) {
               if (var16 >= 45) {
                  break;
               }

               ItemStack var9 = new ItemStack(Material.BOOK);
               ItemMeta var10 = var9.getItemMeta();
               var10.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cBan #" + var8.B));
               ArrayList var11 = new ArrayList();
               var11.add(ChatColor.translateAlternateColorCodes('&', "&7Reason: &f" + var8.G));
               var11.add(ChatColor.translateAlternateColorCodes('&', "&7Banned by: &f" + var8.F));
               String var10002 = var14.format(new Date(var8.D));
               var11.add(ChatColor.translateAlternateColorCodes('&', "&7Date: &f" + var10002));
               if (var8.C == -1L) {
                  var11.add(ChatColor.translateAlternateColorCodes('&', "&7Expires: &cPermanent"));
               } else {
                  var10002 = var14.format(new Date(var8.C));
                  var11.add(ChatColor.translateAlternateColorCodes('&', "&7Expires: &f" + var10002));
               }

               var10.setLore(var11);
               var9.setItemMeta(var10);
               this.C.setItem(var16++, var9);
            }

         }
      }
   }

   private String B() {
      Player var1 = Bukkit.getPlayer(this.B);
      return var1 != null ? var1.getName() : Bukkit.getOfflinePlayer(this.B).getName();
   }

   public Inventory getInventory() {
      return this.C;
   }
}
