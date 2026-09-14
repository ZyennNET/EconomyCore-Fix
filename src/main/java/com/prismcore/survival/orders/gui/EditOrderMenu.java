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
import org.bukkit.metadata.FixedMetadataValue;

public class EditOrderMenu implements InventoryHolder, MenuOwner {
   private static final String META_SUPPRESS_CLOSE = "prism.orders.suppressClose";
   private final PrismOrders pl;
   private final Player p;
   private final Order order;
   private Inventory inv;

   public EditOrderMenu(PrismOrders var1, Player var2, Order var3) {
      this.pl = var1;
      this.p = var2;
      this.order = var3;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public void open() {
      byte var1 = 3;
      String var2 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Edit Order");
      this.inv = Bukkit.createInventory(this, var1 * 9, var2);
      int[] var3 = new int[]{0, 1, 2, 9, 11, 18, 19, 20};
      ItemStack var4 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName("§7 ");
         var4.setItemMeta(var5);
      }

      for(int var9 : var3) {
         if (var9 >= 0 && var9 < this.inv.getSize()) {
            this.inv.setItem(var9, var4);
         }
      }

      HashMap var18 = new HashMap();
      var18.put("item", this.order.key.displayName());
      var18.put("requested", Utils.abbr((double)this.order.requested));
      var18.put("delivered", Utils.abbr((double)this.order.delivered));
      var18.put("paid", Utils.abbr((double)this.order.delivered * this.order.priceEach));
      var18.put("total", Utils.abbr(this.order.totalPrice()));
      var18.put("player", this.p.getName());
      var18.put("price_each", Utils.abbr(this.order.priceEach));
      ArrayList var19 = new ArrayList();
      var19.add("&#34ee80{requested} &f{item}");
      var19.add("&#34ee80${price_each} &feach");
      var19.add("");
      var19.add("&#7E7E29{delivered}/&#008D4C{requested} &#858585Delivered");
      var19.add("&#7E7E29${paid}/&#008D4C${total} &#858585Paid");
      List var20 = this.order.key.enchantLoreLines("&7");
      if (!var20.isEmpty()) {
         var19.add("&7");
         var19.addAll(var20);
      }

      var19.add("");
      long var21 = System.currentTimeMillis();
      if (var21 >= this.order.expiresAt) {
         var19.add("&cExpired");
      } else {
         var19.add("&8" + this.order.getExpirationString() + " Until Order expires");
      }

      ArrayList var11 = new ArrayList();

      for(String var13 : var19) {
         String var14 = var13;

         for(Map.Entry var16 : var18.entrySet()) {
            var14 = var14.replace("{" + (String)var16.getKey() + "}", (CharSequence)var16.getValue());
         }

         var11.add(var14);
      }

      ItemStack var22 = new ItemStack(this.order.key.material);
      ItemMeta var24 = var22.getItemMeta();
      if (var24 != null) {
         var24.setDisplayName(Utils.formatColors("&f" + this.order.key.displayName()));
         var24.setLore(Utils.formatColors((List)var11));
         var24.addItemFlags(ItemFlag.values());
         var22.setItemMeta(var24);
      }

      var22 = GuiVariant.merge(var22, this.order.key.buildIcon());
      this.inv.setItem(10, var22);
      boolean var25 = !this.order.completed;
      if (var25) {
         ItemStack var26 = new ItemStack(Material.RED_TERRACOTTA);
         ItemMeta var28 = var26.getItemMeta();
         if (var28 != null) {
            var28.setDisplayName(Utils.formatColors("&aᴄᴀɴᴄᴇʟ"));
            var28.setLore(Utils.formatColors(List.of("&fClick to cancel this order")));
            var28.addItemFlags(ItemFlag.values());
            var26.setItemMeta(var28);
         }

         this.inv.setItem(13, var26);
      }

      int var27 = var25 ? 15 : 13;
      ItemStack var29 = new ItemStack(Material.CHEST);
      ItemMeta var17 = var29.getItemMeta();
      if (var17 != null) {
         var17.setDisplayName(Utils.formatColors("&aᴄᴏʟʟᴇᴄᴛ"));
         var17.setLore(Utils.formatColors(List.of("&fClick to collect items")));
         var17.addItemFlags(ItemFlag.values());
         var29.setItemMeta(var17);
      }

      this.inv.setItem(var27, var29);
      this.p.openInventory(this.inv);
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            int var2 = var1.getSlot();
            if (var2 == 13 && !this.order.completed) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               (new DeleteOrderMenu(this.pl, this.p, this.order)).open();
            } else {
               boolean var3 = var2 == 15 && !this.order.completed || var2 == 13 && this.order.completed;
               if (var3) {
                  if (this.order.storage.isEmpty()) {
                     String var4 = Utils.formatColors("&cYou have no items to collect.");
                     this.p.sendMessage(var4);
                     Utils.sendActionBar(this.p, "&cYou have no items to collect.");
                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  } else {
                     this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                     this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
                     (new CollectItemsMenu(this.pl, this.p, this.order)).open();
                  }
               }

            }
         }
      }
   }

   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         if (this.p.hasMetadata("prism.orders.suppressClose")) {
            this.p.removeMetadata("prism.orders.suppressClose", this.pl.getPlugin());
         } else {
            TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new YourOrdersMenu(this.pl, this.p)).open(), 1L);
         }
      }
   }
}
