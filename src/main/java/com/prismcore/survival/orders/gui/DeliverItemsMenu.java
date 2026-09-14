package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.ItemKey;
import com.prismcore.survival.orders.data.Order;
import com.prismcore.survival.orders.util.TaskUtil;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

public class DeliverItemsMenu implements InventoryHolder, MenuOwner {
   private final PrismOrders pl;
   private final Player p;
   private final Order order;
   private Inventory inv;

   public DeliverItemsMenu(PrismOrders var1, Player var2, Order var3) {
      this.pl = var1;
      this.p = var2;
      this.order = var3;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public void open() {
      if (this.order.remainingAmount() <= 0) {
         (new OrdersMainMenu(this.pl, this.p)).open();
      } else {
         byte var1 = 4;
         String var2 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Deliver Items");
         this.inv = Bukkit.createInventory(this, var1 * 9, var2);
         this.p.openInventory(this.inv);
      }
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         var1.setCancelled(false);
      }
   }

   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         ItemKey var2 = this.order.key;
         int var3 = this.order.remainingAmount();
         ArrayList var4 = new ArrayList();
         ArrayList var5 = new ArrayList();
         int var6 = 0;

         for(int var7 = 0; var7 < this.inv.getSize(); ++var7) {
            ItemStack var8 = this.inv.getItem(var7);
            if (var8 != null && var8.getType() != Material.AIR) {
               if (var2.matches(var8)) {
                  int var9 = Math.min(var3 - var6, var8.getAmount());
                  if (var9 > 0) {
                     ItemStack var10 = var8.clone();
                     var10.setAmount(var9);
                     var4.add(var10);
                     var6 += var9;
                     if (var8.getAmount() > var9) {
                        ItemStack var11 = var8.clone();
                        var11.setAmount(var8.getAmount() - var9);
                        var5.add(var11);
                     }
                  } else {
                     var5.add(var8);
                  }
               } else if (isShulker(var8)) {
                  BlockStateMeta var21 = (BlockStateMeta)var8.getItemMeta();
                  if (var21 != null && var21.getBlockState() instanceof ShulkerBox) {
                     ShulkerBox var22 = (ShulkerBox)var21.getBlockState();
                     ItemStack[] var23 = var22.getInventory().getContents();

                     for(ItemStack var15 : var23) {
                        if (var15 != null && var15.getType() != Material.AIR && var2.matches(var15)) {
                           int var16 = Math.min(var3 - var6, var15.getAmount());
                           if (var16 <= 0) {
                              break;
                           }

                           ItemStack var17 = var15.clone();
                           var17.setAmount(var16);
                           var4.add(var17);
                           var15.setAmount(var15.getAmount() - var16);
                           var6 += var16;
                           if (var6 >= var3) {
                              break;
                           }
                        }
                     }

                     var22.getInventory().setContents(var23);
                     var21.setBlockState(var22);
                     var8.setItemMeta(var21);
                     var5.add(var8);
                  } else {
                     var5.add(var8);
                  }
               } else {
                  var5.add(var8);
               }

               if (var6 >= var3 && !var5.contains(var8) && !var4.contains(var8)) {
               }
            }
         }

         for(ItemStack var19 : var5) {
            this.giveBackOrDrop(this.p, var19);
         }

         if (var6 <= 0) {
            TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new OrdersMainMenu(this.pl, this.p)).open(), 1L);
         } else {
            ArrayList var20 = new ArrayList(var4);
            TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new ConfirmDeliveryMenu(this.pl, this.p, this.order, var20, var6)).open(), 1L);
         }
      }
   }

   private static boolean isShulker(ItemStack var0) {
      Material var1 = var0.getType();
      return var1.name().endsWith("SHULKER_BOX") && var0.getItemMeta() instanceof BlockStateMeta;
   }

   private void giveBackOrDrop(Player var1, ItemStack var2) {
      HashMap var3 = var1.getInventory().addItem(new ItemStack[]{var2});
      var3.values().forEach((var1x) -> var1.getWorld().dropItemNaturally(var1.getLocation(), var1x));
   }
}
