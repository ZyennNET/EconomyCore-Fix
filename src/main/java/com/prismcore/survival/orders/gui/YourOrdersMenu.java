package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.Order;
import com.prismcore.survival.orders.util.TaskUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

public class YourOrdersMenu implements InventoryHolder, MenuOwner {
   private static final String META_SUPPRESS_CLOSE = "prism.orders.suppressClose";
   private final PrismOrders pl;
   private final Player p;
   private Inventory inv;

   public YourOrdersMenu(PrismOrders var1, Player var2) {
      this.pl = var1;
      this.p = var2;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   public void open() {
      byte var1 = 3;
      String var2 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Your Orders");
      this.inv = Bukkit.createInventory(this, var1 * 9, var2);
      List var3 = (List)this.pl.orders().all().stream().filter((var1x) -> var1x.owner.equals(this.p.getUniqueId())).filter((var0) -> !var0.canceled).filter((var0) -> !var0.completed || !var0.storage.isEmpty()).collect(Collectors.toList());
      int var4 = 0;

      for(Order var6 : var3) {
         HashMap var7 = new HashMap();
         var7.put("player", this.p.getName());
         var7.put("item", var6.key.displayName());
         var7.put("requested", Utils.abbr((double)var6.requested));
         var7.put("delivered", Utils.abbr((double)var6.delivered));
         var7.put("price_each", Utils.abbr(var6.priceEach));
         var7.put("paid", Utils.abbr((double)var6.delivered * var6.priceEach));
         var7.put("total", Utils.abbr(var6.totalPrice()));
         ArrayList var8 = new ArrayList();
         var8.add("&#34ee80{requested} &f{item}");
         var8.add("&#34ee80${price_each} &feach");
         var8.add("");
         var8.add("&#7E7E29{delivered}/&#008D4C{requested} &#858585Delivered");
         var8.add("&#7E7E29${paid}/&#008D4C${total} &#858585Paid");
         List var9 = var6.key.enchantLoreLines("&7");
         if (!var9.isEmpty()) {
            var8.add("&7");
            var8.addAll(var9);
         }

         var8.add("");
         long var10 = System.currentTimeMillis();
         if (var10 >= var6.expiresAt) {
            var8.add("&cExpired");
         } else {
            var8.add("&8" + var6.getExpirationString() + " Until Order expires");
         }

         ArrayList var12 = new ArrayList();

         for(String var14 : var8) {
            String var15 = var14;

            for(Map.Entry var17 : var7.entrySet()) {
               var15 = var15.replace("{" + (String)var17.getKey() + "}", (CharSequence)var17.getValue());
            }

            var12.add(var15);
         }

         String var20 = Utils.formatColors("&#34ee80" + this.p.getName() + "'s Order");
         ItemStack var21 = new ItemStack(var6.key.material);
         ItemMeta var22 = var21.getItemMeta();
         if (var22 != null) {
            var22.setDisplayName(var20);
            var22.setLore(Utils.formatColors((List)var12));
            var22.addItemFlags(ItemFlag.values());
            var21.setItemMeta(var22);
         }

         ItemStack var23 = GuiVariant.merge(var21, var6.key.buildIcon());
         this.inv.setItem(var4++, var23);
      }

      ItemStack var18 = new ItemStack(Material.MAP);
      ItemMeta var19 = var18.getItemMeta();
      if (var19 != null) {
         var19.setDisplayName(Utils.formatColors("&#34ee80New Order"));
         var19.setLore(Utils.formatColors(List.of("&fClick to create new order")));
         var19.addItemFlags(ItemFlag.values());
         var18.setItemMeta(var19);
      }

      this.inv.setItem(Math.min(var4, this.inv.getSize() - 1), var18);
      this.p.openInventory(this.inv);
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            List var2 = this.pl.orders().all().stream().filter((var1x) -> var1x.owner.equals(this.p.getUniqueId())).filter((var0) -> !var0.canceled).filter((var0) -> !var0.completed || !var0.storage.isEmpty()).toList();
            int var3 = var1.getSlot();
            boolean var4 = var3 == var2.size() || var2.size() >= this.inv.getSize() && var3 == this.inv.getSize() - 1;
            if (var4) {
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               (new NewOrderMenu(this.pl, this.p)).open();
            } else {
               if (var3 >= 0 && var3 < var2.size()) {
                  this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
                  this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                  (new EditOrderMenu(this.pl, this.p, (Order)var2.get(var3))).open();
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
            TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new OrdersMainMenu(this.pl, this.p)).open(), 1L);
         }
      }
   }
}
