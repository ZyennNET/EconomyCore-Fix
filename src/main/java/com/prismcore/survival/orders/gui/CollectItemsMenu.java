package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.Order;
import com.prismcore.survival.orders.util.TaskUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class CollectItemsMenu implements InventoryHolder, MenuOwner {
   private static final String META_SUPPRESS_CLOSE = "prism.orders.suppressClose";
   private final PrismOrders pl;
   private final Player p;
   private final UUID orderId;
   private Inventory inv;
   private int page = 0;

   public CollectItemsMenu(PrismOrders var1, Player var2, Order var3) {
      this.pl = var1;
      this.p = var2;
      this.orderId = var3.id;
   }

   private Order getFreshOrder() {
      Order var1 = this.pl.orders().getOrder(this.orderId);
      if (var1 == null) {
         this.p.sendMessage(Utils.formatColors("&cOrder not found."));
         this.p.closeInventory();
         return null;
      } else {
         return var1;
      }
   }

   public Inventory getInventory() {
      return this.inv;
   }

   private int perPage() {
      return 36;
   }

   public void open() {
      Order var1 = this.getFreshOrder();
      if (var1 != null) {
         byte var2 = 6;
         String var3 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Collect Items");
         this.inv = Bukkit.createInventory(this, var2 * 9, var3);
         List var4 = var1.storage;
         int var5 = Math.max(0, (var4.size() - 1) / this.perPage());
         if (this.page > var5) {
            this.page = var5;
         }

         if (this.page > 0) {
            ItemStack var6 = new ItemStack(Material.ARROW);
            ItemMeta var7 = var6.getItemMeta();
            if (var7 != null) {
               var7.setDisplayName(Utils.formatColors("&aPrevious Page"));
               var7.setLore(Utils.formatColors(List.of("&fGo to page " + this.page)));
               var7.addItemFlags(ItemFlag.values());
               var6.setItemMeta(var7);
            }

            this.inv.setItem(45, var6);
         }

         if (this.page < var5) {
            ItemStack var11 = new ItemStack(Material.ARROW);
            ItemMeta var13 = var11.getItemMeta();
            if (var13 != null) {
               var13.setDisplayName(Utils.formatColors("&aNext Page"));
               var13.setLore(Utils.formatColors(List.of("&fGo to page " + (this.page + 2))));
               var13.addItemFlags(ItemFlag.values());
               var11.setItemMeta(var13);
            }

            this.inv.setItem(53, var11);
         }

         ItemStack var12 = new ItemStack(Material.HOPPER);
         ItemMeta var14 = var12.getItemMeta();
         if (var14 != null) {
            var14.setDisplayName(Utils.formatColors("&fDrop Items"));
            var14.setLore(Utils.formatColors(List.of("&fClick to drop the items", "&fon this page at your feet.")));
            var14.addItemFlags(ItemFlag.values());
            var12.setItemMeta(var14);
         }

         this.inv.setItem(49, var12);
         int var8 = this.page * this.perPage();
         int var9 = Math.min(var8 + this.perPage(), var4.size());

         for(int var10 = var8; var10 < var9; ++var10) {
            this.inv.setItem(var10 - var8, (ItemStack)var4.get(var10));
         }

         this.p.openInventory(this.inv);
      }
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            int var2 = var1.getSlot();
            if (var2 == 45 && this.page > 0) {
               --this.page;
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.suppressCloseAndReopen();
            } else if (var2 == 53) {
               Order var10 = this.getFreshOrder();
               if (var10 != null) {
                  int var12 = Math.max(0, (var10.storage.size() - 1) / this.perPage());
                  if (this.page < var12) {
                     ++this.page;
                     this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                     this.suppressCloseAndReopen();
                  }

               }
            } else if (var2 != 49) {
               if (var2 < 36 && var2 >= 0) {
                  Order var9 = this.getFreshOrder();
                  if (var9 == null) {
                     return;
                  }

                  int var11 = this.page * this.perPage() + var2;
                  if (var11 >= 0 && var11 < var9.storage.size()) {
                     ItemStack var13 = (ItemStack)var9.storage.remove(var11);
                     ItemStack var14 = var13.clone();
                     HashMap var16 = this.p.getInventory().addItem(new ItemStack[]{var14});
                     if (!var16.isEmpty()) {
                        ItemStack var17 = (ItemStack)var16.values().iterator().next();
                        var9.storage.add(var11, var17);
                        this.pl.orders().saveOrder(var9);
                        if (var17.getAmount() >= var13.getAmount()) {
                           this.p.sendMessage(Utils.formatColors("&cYour inventory is full!"));
                        } else {
                           this.p.sendMessage(Utils.formatColors("&eYour inventory is full! Picked up what fit, the rest stays in the order."));
                        }

                        this.p.playSound(this.p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F);
                        this.suppressCloseAndReopen();
                        return;
                     }

                     this.pl.orders().saveOrder(var9);
                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
                     this.suppressCloseAndReopen();
                  }
               }

            } else {
               Order var3 = this.getFreshOrder();
               if (var3 != null) {
                  int var4 = this.page * this.perPage();
                  int var5 = Math.min(var4 + this.perPage(), var3.storage.size());
                  if (var4 < var5) {
                     ArrayList var6 = new ArrayList(var3.storage.subList(var4, var5));
                     var3.storage.subList(var4, var5).clear();
                     this.pl.orders().saveOrder(var3);

                     for(ItemStack var8 : var6) {
                        this.p.getWorld().dropItemNaturally(this.p.getLocation(), var8);
                     }

                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1.0F, 1.0F);
                     int var15 = Math.max(0, (var3.storage.size() - 1) / this.perPage());
                     if (this.page > var15) {
                        this.page = var15;
                     }

                     this.suppressCloseAndReopen();
                  }
               }
            }
         }
      }
   }

   private void suppressCloseAndReopen() {
      this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
      this.open();
   }

   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         if (this.p.hasMetadata("prism.orders.suppressClose")) {
            this.p.removeMetadata("prism.orders.suppressClose", this.pl.getPlugin());
         } else {
            TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> {
               Order var1 = this.getFreshOrder();
               if (var1 != null) {
                  (new EditOrderMenu(this.pl, this.p, var1)).open();
               } else {
                  (new YourOrdersMenu(this.pl, this.p)).open();
               }

            }, 1L);
         }
      }
   }
}
