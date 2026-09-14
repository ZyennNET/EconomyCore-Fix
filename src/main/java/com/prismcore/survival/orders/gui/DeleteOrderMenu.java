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

public class DeleteOrderMenu implements InventoryHolder, MenuOwner {
   private final PrismOrders pl;
   private final Player p;
   private final Order order;
   private Inventory inv;

   public DeleteOrderMenu(PrismOrders var1, Player var2, Order var3) {
      this.pl = var1;
      this.p = var2;
      this.order = var3;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public void open() {
      byte var1 = 3;
      String var2 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Delete Order");
      this.inv = Bukkit.createInventory(this, var1 * 9, var2);
      ItemStack var3 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var4 = var3.getItemMeta();
      if (var4 != null) {
         var4.setDisplayName(Utils.formatColors("&4ᴄᴀɴᴄᴇʟ"));
         var4.setLore(Utils.formatColors(List.of("&fClick to return")));
         var4.addItemFlags(ItemFlag.values());
         var3.setItemMeta(var4);
      }

      this.inv.setItem(10, var3);
      ItemStack var5 = new ItemStack(this.order.key.material);
      ItemMeta var6 = var5.getItemMeta();
      if (var6 != null) {
         var6.setDisplayName(Utils.formatColors("&f" + this.order.key.displayName()));
         HashMap var7 = new HashMap();
         var7.put("item", this.order.key.displayName());
         var7.put("requested", Utils.abbr((double)this.order.requested));
         var7.put("delivered", Utils.abbr((double)this.order.delivered));
         var7.put("price_each", Utils.abbr(this.order.priceEach));
         var7.put("paid", Utils.abbr((double)this.order.delivered * this.order.priceEach));
         var7.put("total", Utils.abbr(this.order.totalPrice()));
         ArrayList var8 = new ArrayList();
         var8.add("&#34ee80{requested} &f{item}");
         var8.add("&#34ee80${price_each} &feach");
         var8.add("");
         var8.add("&#7E7E29{delivered}/&#008D4C{requested} &#858585Delivered");
         var8.add("&#7E7E29${paid}/&#008D4C${total} &#858585Paid");
         List var9 = this.order.key.enchantLoreLines("&7");
         if (!var9.isEmpty()) {
            var8.add(Utils.formatColors("&7"));
            var8.addAll(Utils.formatColors(var9));
         }

         var8.add("");
         long var10 = System.currentTimeMillis();
         if (var10 >= this.order.expiresAt) {
            var8.add("&cExpired");
         } else {
            var8.add("&8" + this.order.getExpirationString() + " Until Order expires");
         }

         ArrayList var12 = new ArrayList();

         for(String var14 : var8) {
            String var15 = var14;

            for(Map.Entry var17 : var7.entrySet()) {
               var15 = var15.replace("{" + (String)var17.getKey() + "}", (CharSequence)var17.getValue());
            }

            var12.add(var15);
         }

         var6.setLore(Utils.formatColors((List)var12));
         var6.addItemFlags(ItemFlag.values());
         var5.setItemMeta(var6);
      }

      var5 = GuiVariant.merge(var5, this.order.key.buildIcon());
      this.inv.setItem(13, var5);
      ItemStack var19 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var20 = var19.getItemMeta();
      if (var20 != null) {
         var20.setDisplayName(Utils.formatColors("&aᴄᴏɴꜰɪʀᴍ"));
         var20.setLore(Utils.formatColors(List.of("&fclick to cancel order")));
         var20.addItemFlags(ItemFlag.values());
         var19.setItemMeta(var20);
      }

      this.inv.setItem(16, var19);
      this.p.openInventory(this.inv);
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            int var2 = var1.getSlot();
            if (var2 == 10) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               (new EditOrderMenu(this.pl, this.p, this.order)).open();
            } else {
               if (var2 == 16) {
                  if (!this.order.storage.isEmpty()) {
                     String var4 = Utils.formatColors("&cPlease collect the items before cancelling this order.");
                     this.p.sendMessage(var4);
                     Utils.sendActionBar(this.p, "&cPlease collect the items before cancelling this order.");
                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     return;
                  }

                  this.pl.orders().cancel(this.order);
                  String var3 = Utils.formatColors("&fYou cancelled the order successfully");
                  this.p.sendMessage(var3);
                  Utils.sendActionBar(this.p, "&fYou cancelled the order successfully");
                  this.p.playSound(this.p.getLocation(), Sound.UI_TOAST_IN, 1.0F, 1.0F);
                  (new YourOrdersMenu(this.pl, this.p)).open();
               }

            }
         }
      }
   }

   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         Player var2 = (Player)var1.getPlayer();
         TaskUtil.runEntityLater(this.pl.getPlugin(), var2, () -> {
            if (var2.getOpenInventory().getTopInventory() != null && !(var2.getOpenInventory().getTopInventory().getHolder() instanceof MenuOwner)) {
               (new YourOrdersMenu(this.pl, var2)).open();
            }

         }, 1L);
      }
   }
}
