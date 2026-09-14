package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.ItemKey;
import com.prismcore.survival.orders.input.ChatInputManager;
import com.prismcore.survival.orders.store.OrderManager;
import com.prismcore.survival.orders.util.TaskUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.metadata.MetadataValue;

public class NewOrderMenu implements InventoryHolder, MenuOwner {
   private static final String META_CHOSEN = "prism.orders.tmpChosenStack";
   private static final String META_SUPPRESS_CLOSE = "prism.orders.suppressClose";
   private final PrismOrders pl;
   private final Player p;
   private Inventory inv;

   public NewOrderMenu(PrismOrders var1, Player var2) {
      this.pl = var1;
      this.p = var2;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   private static boolean hasAnyEnchants(ItemStack var0) {
      if (var0 == null) {
         return false;
      } else {
         ItemMeta var1 = var0.getItemMeta();
         return var1 != null && var1.hasEnchants();
      }
   }

   public void open() {
      ChatInputManager.NewOrderSession var1 = this.pl.chat().session(this.p.getUniqueId());
      if (var1.chosenItem == null) {
         var1.chosenItem = Material.STONE.name();
      }

      if (var1.amount == null) {
         var1.amount = 1;
      }

      if (var1.priceEach == null) {
         var1.priceEach = (double)1.0F;
      }

      ItemStack var2 = null;
      if (this.p.hasMetadata("prism.orders.tmpChosenStack") && this.p.getMetadata("prism.orders.tmpChosenStack").size() > 0) {
         Object var3 = ((MetadataValue)this.p.getMetadata("prism.orders.tmpChosenStack").get(0)).value();
         if (var3 instanceof ItemStack) {
            var2 = ((ItemStack)var3).clone();
         }
      }

      Material var20 = var2 != null ? var2.getType() : Material.matchMaterial(var1.chosenItem);
      if (var20 == null) {
         var20 = Material.STONE;
      }

      boolean var4 = this.p.hasMetadata("donutorder.skipEnchantOnce");
      if (var4) {
         this.p.removeMetadata("donutorder.skipEnchantOnce", this.pl.getPlugin());
      }

      if (!var4 && this.pl.enchants().hasOptionsFor(var20) && !hasAnyEnchants(var2)) {
         (new EnchantSelectMenu(this.pl, this.p, new ItemStack(var20))).open();
      } else {
         String var5 = var2 != null && var2.getItemMeta() != null && var2.getItemMeta().hasDisplayName() ? var2.getItemMeta().getDisplayName().replace("§", "&") : OrderManager.nice(var20);
         byte var6 = 3;
         String var7 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> New Order");
         this.inv = Bukkit.createInventory(this, var6 * 9, var7);
         ItemStack var8 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
         ItemMeta var9 = var8.getItemMeta();
         if (var9 != null) {
            var9.setDisplayName(Utils.formatColors("&4ᴄᴀɴᴄᴇʟ"));
            var9.setLore(Utils.formatColors(List.of("&fClick to return")));
            var9.addItemFlags(ItemFlag.values());
            var8.setItemMeta(var9);
         }

         this.inv.setItem(10, var8);
         HashMap var10 = new HashMap();
         var10.put("item", var5);
         var10.put("amount", Utils.abbr((double)var1.amount));
         var10.put("price_each", Utils.abbr(var1.priceEach));
         var10.put("total", Utils.abbr((double)var1.amount * var1.priceEach));
         ItemStack var11 = new ItemStack(var20);
         ItemMeta var12 = var11.getItemMeta();
         if (var12 != null) {
            var12.setDisplayName(Utils.formatColors("&aɪᴛᴇᴍ"));
            ArrayList var13 = new ArrayList(List.of("&fClick to choose item", "&7({item})"));
            ArrayList var14 = new ArrayList();

            for(String var16 : var13) {
               var14.add(var16.replace("{item}", (CharSequence)var10.get("item")));
            }

            var12.setLore(Utils.formatColors((List)var14));
            var12.addItemFlags(ItemFlag.values());
            var11.setItemMeta(var12);
         }

         if (var2 != null) {
            var11 = GuiVariant.merge(var11, var2);
         }

         this.inv.setItem(12, var11);
         ItemStack var21 = new ItemStack(Material.CHEST);
         ItemMeta var22 = var21.getItemMeta();
         if (var22 != null) {
            var22.setDisplayName(Utils.formatColors("&aᴀᴍᴏᴜɴᴛ"));
            List var23 = Utils.formatColors(List.of("&fClick to type number of items", "&7(" + (String)var10.get("amount") + ")"));
            var22.setLore(var23);
            var22.addItemFlags(ItemFlag.values());
            var21.setItemMeta(var22);
         }

         this.inv.setItem(13, var21);
         ItemStack var24 = new ItemStack(Material.EMERALD);
         ItemMeta var25 = var24.getItemMeta();
         if (var25 != null) {
            var25.setDisplayName(Utils.formatColors("&aᴘʀɪᴄᴇ"));
            List var17 = Utils.formatColors(List.of("&fClick to type the price per item", "&7($" + (String)var10.get("price_each") + ")"));
            var25.setLore(var17);
            var25.addItemFlags(ItemFlag.values());
            var24.setItemMeta(var25);
         }

         this.inv.setItem(14, var24);
         ItemStack var26 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
         ItemMeta var18 = var26.getItemMeta();
         if (var18 != null) {
            var18.setDisplayName(Utils.formatColors("&aᴄᴏɴꜰɪʀᴍ"));
            List var19 = Utils.formatColors(List.of("&fClick to confirm order", "&7(Total: &7$" + (String)var10.get("total") + ")"));
            var18.setLore(var19);
            var18.addItemFlags(ItemFlag.values());
            var26.setItemMeta(var18);
         }

         this.inv.setItem(16, var26);
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
            ChatInputManager.NewOrderSession var3 = this.pl.chat().session(this.p.getUniqueId());
            if (var2 == 10) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               (new YourOrdersMenu(this.pl, this.p)).open();
            } else if (var2 == 12) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.pl.state().select(this.p.getUniqueId()).reset();
               (new SelectItemMenu(this.pl, this.p)).open();
            } else if (var2 == 13) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.p.closeInventory();
               this.pl.chat().promptConfigured(this.p, "&aPlease enter the amount:", (var1x) -> {
                  if (this.p.isOnline()) {
                     String var2 = var1x == null ? "" : var1x.trim();
                     if (!var2.isEmpty() && !var2.equals("-")) {
                        int var3;
                        try {
                           var3 = (int)Utils.parseNumber(var2);
                        } catch (Exception var5) {
                           this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                           return;
                        }

                        if (var3 > 0 && var3 <= 999999) {
                           this.pl.chat().session(this.p.getUniqueId()).amount = var3;
                           TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                        } else {
                           this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                        }
                     } else {
                        TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                     }
                  }
               });
            } else if (var2 == 14) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.p.closeInventory();
               this.pl.chat().promptConfigured(this.p, "&aPlease enter the price per item:", (var1x) -> {
                  if (this.p.isOnline()) {
                     String var2 = var1x == null ? "" : var1x.trim();
                     if (!var2.isEmpty() && !var2.equals("-")) {
                        double var3;
                        try {
                           var3 = Utils.parseNumber(var2);
                        } catch (Exception var6) {
                           this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                           return;
                        }

                        if (var3 <= (double)0.0F) {
                           this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                        } else {
                           this.pl.chat().session(this.p.getUniqueId()).priceEach = var3;
                           TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                        }
                     } else {
                        TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
                     }
                  }
               });
            } else {
               if (var2 == 16) {
                  if (var3.chosenItem == null || var3.amount == null || var3.priceEach == null || var3.priceEach <= (double)0.0F || var3.amount <= 0) {
                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     return;
                  }

                  double var4 = (double)var3.amount * var3.priceEach;
                  if (!this.pl.canAfford(this.p, var4)) {
                     String var11 = "&cYou don't have enough money to place this order.";
                     this.p.sendMessage(Utils.formatColors(var11));
                     Utils.sendActionBar(this.p, var11);
                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     return;
                  }

                  if (!this.pl.takeMoney(this.p, var4)) {
                     String var10 = "&cYou don't have enough money to place this order.";
                     this.p.sendMessage(Utils.formatColors(var10));
                     Utils.sendActionBar(this.p, var10);
                     this.p.playSound(this.p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     return;
                  }

                  ItemStack var6 = null;
                  if (this.p.hasMetadata("prism.orders.tmpChosenStack") && this.p.getMetadata("prism.orders.tmpChosenStack").size() > 0) {
                     Object var7 = ((MetadataValue)this.p.getMetadata("prism.orders.tmpChosenStack").get(0)).value();
                     if (var7 instanceof ItemStack) {
                        var6 = (ItemStack)var7;
                     }
                  }

                  ItemKey var12 = var6 != null ? ItemKey.fromStack(var6) : ItemKey.of(Material.valueOf(var3.chosenItem));
                  this.pl.orders().create(this.p.getUniqueId(), var12, var3.amount, var3.priceEach);
                  String var8 = var12.displayName();
                  String var9 = Utils.formatColors("&7You ordered &a" + var3.amount + " " + var8);
                  this.p.sendMessage(var9);
                  this.p.playSound(this.p.getLocation(), Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1.0F, 1.0F);
                  if (this.p.hasMetadata("prism.orders.tmpChosenStack")) {
                     this.p.removeMetadata("prism.orders.tmpChosenStack", this.pl.getPlugin());
                  }

                  this.pl.chat().clearSession(this.p.getUniqueId());
                  this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
                  this.p.closeInventory();
                  TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new OrdersMainMenu(this.pl, this.p)).open(), 2L);
               }

            }
         }
      }
   }

   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() == this) {
         if (!this.p.hasMetadata("prism-orders-sign-input")) {
            if (this.p.hasMetadata("prism.orders.suppressClose")) {
               this.p.removeMetadata("prism.orders.suppressClose", this.pl.getPlugin());
            } else {
               TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new YourOrdersMenu(this.pl, this.p)).open(), 1L);
            }
         }
      }
   }
}
