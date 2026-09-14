package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.Order;
import com.prismcore.survival.orders.util.TaskUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class ConfirmDeliveryMenu implements InventoryHolder, MenuOwner {
   private final PrismOrders pl;
   private final Player p;
   private final Order order;
   private final List<ItemStack> accepted;
   private final int acceptedAmount;
   private Inventory inv;
   private boolean finalized = false;

   public ConfirmDeliveryMenu(PrismOrders var1, Player var2, Order var3, List<ItemStack> var4, int var5) {
      this.pl = var1;
      this.p = var2;
      this.order = var3;
      this.accepted = var4;
      this.acceptedAmount = var5;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public void open() {
      byte var1 = 3;
      String var2 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Confirm Delivery");
      this.inv = Bukkit.createInventory(this, var1 * 9, var2);
      byte var3 = 11;
      byte var4 = 13;
      byte var5 = 15;
      String var6 = Bukkit.getOfflinePlayer(this.order.owner).getName();
      if (var6 == null) {
         var6 = "Unknown";
      }

      HashMap var7 = new HashMap();
      var7.put("player", var6);
      var7.put("item", this.order.key.displayName());
      var7.put("amount", Utils.abbr((double)this.acceptedAmount));
      var7.put("price_each", Utils.abbr(this.order.priceEach));
      var7.put("receive", Utils.abbr((double)this.acceptedAmount * this.order.priceEach));
      ArrayList var8 = new ArrayList(List.of("&f{amount} {item}", "&f${price_each} &7each", "", "&7You're delivering &f{amount} {item}"));
      List var9 = this.order.key.enchantLoreLines("&7");
      if (!var9.isEmpty()) {
         var8.add("&7");
         var8.addAll(var9);
      }

      ArrayList var10 = new ArrayList();

      for(String var12 : var8) {
         String var13 = var12;

         for(Map.Entry var15 : var7.entrySet()) {
            var13 = var13.replace("{" + (String)var15.getKey() + "}", (CharSequence)var15.getValue());
         }

         var10.add(var13);
      }

      ItemStack var18 = new ItemStack(this.order.key.material);
      ItemMeta var20 = var18.getItemMeta();
      if (var20 != null) {
         var20.setDisplayName(Utils.formatColors("&f" + var6 + " Order"));
         var20.setLore(Utils.formatColors((List)var10));
         var20.addItemFlags(ItemFlag.values());
         var18.setItemMeta(var20);
      }

      var18 = GuiVariant.merge(var18, this.order.key.buildIcon());
      this.inv.setItem(var4, var18);
      ItemStack var21 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var22 = var21.getItemMeta();
      if (var22 != null) {
         var22.setDisplayName(Utils.formatColors("&cCANCEL"));
         var22.setLore(Utils.formatColors(List.of("&fClick to go back")));
         var22.addItemFlags(ItemFlag.values());
         var21.setItemMeta(var22);
      }

      this.inv.setItem(var3, var21);
      ItemStack var23 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var16 = var23.getItemMeta();
      if (var16 != null) {
         var16.setDisplayName(Utils.formatColors("&aCONFIRM"));
         String var17 = (String)var7.get("receive");
         var16.setLore(Utils.formatColors(List.of("&fClick to deliver items", "&7(" + var17 + ")")));
         var16.addItemFlags(ItemFlag.values());
         var23.setItemMeta(var16);
      }

      this.inv.setItem(var5, var23);
      this.p.openInventory(this.inv);
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            int var2 = var1.getSlot();
            if (var2 != 11) {
               if (var2 == 15) {
                  this.p.playSound(this.p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.2F);
                  this.finalized = true;
                  this.pl.orders().applyDelivery(this.order, this.accepted, this.acceptedAmount, this.p.getUniqueId());
                  if (this.order.remainingAmount() > 0) {
                     TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new DeliverItemsMenu(this.pl, this.p, this.order)).open(), 1L);
                  } else {
                     TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new OrdersMainMenu(this.pl, this.p)).open(), 1L);
                  }
               }

            } else {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.finalized = true;

               for(ItemStack var4 : this.accepted) {
                  this.giveBackOrDrop(var4);
               }

               TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new DeliverItemsMenu(this.pl, this.p, this.order)).open(), 1L);
            }
         }
      }
   }

   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         if (!this.finalized) {
            for(ItemStack var3 : this.accepted) {
               this.giveBackOrDrop(var3);
            }

            TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new OrdersMainMenu(this.pl, this.p)).open(), 1L);
         }

      }
   }

   private void giveBackOrDrop(ItemStack var1) {
      HashMap var2 = this.p.getInventory().addItem(new ItemStack[]{var1});
      var2.values().forEach((var1x) -> this.p.getWorld().dropItemNaturally(this.p.getLocation(), var1x));
   }
}
