package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.Order;
import com.prismcore.survival.orders.data.SortType;
import com.prismcore.survival.orders.store.PlayerStateManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OrdersMainMenu implements InventoryHolder, MenuOwner {
   private final PrismOrders pl;
   private final Player p;
   private Inventory inv;
   private static final Map<UUID, Long> lastRefreshTimes = new HashMap();

   public OrdersMainMenu(PrismOrders var1, Player var2) {
      this.pl = var1;
      this.p = var2;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   private int rows() {
      return 6;
   }

   private int perPage() {
      return (this.rows() - 1) * 9;
   }

   public void open() {
      PlayerStateManager.View var1 = this.pl.state().main(this.p.getUniqueId());
      if (var1.sort == null) {
         var1.sort = SortType.MOST_PAID;
      }

      if (var1.filter == null || var1.filter.isBlank()) {
         var1.filter = "All";
      }

      if (var1.page < 0) {
         var1.page = 0;
      }

      ArrayList var2 = new ArrayList(this.pl.orders().all());
      var2.removeIf((var0) -> var0.canceled || var0.completed);
      if (var1.search != null && !var1.search.isBlank()) {
         String var3 = var1.search.toLowerCase(Locale.ENGLISH);
         var2.removeIf((var1x) -> {
            String var2 = var1x.key.displayName().toLowerCase(Locale.ENGLISH);
            String var3x = var1x.key.material.name().toLowerCase(Locale.ENGLISH);
            return !var2.contains(var3) && !var3x.contains(var3);
         });
      }

      Set var33;
      if (!"All".equalsIgnoreCase(var1.filter) && (var33 = this.pl.filters().resolve(var1.filter)) != null && !var33.isEmpty()) {
         var2.removeIf((var1x) -> !var33.contains(var1x.key.material));
      }

      switch (var1.sort) {
         case MOST_PAID -> var2.sort(Comparator.comparingDouble((var0) -> -((double)var0.delivered * var0.priceEach)));
         case MOST_DELIVERED -> var2.sort(Comparator.comparingInt((var0) -> -var0.delivered));
         case RECENTLY_LISTED -> Collections.reverse(var2);
         case MOST_MONEY_PER_ITEM -> var2.sort(Comparator.comparingDouble((var0) -> -var0.priceEach));
      }

      int var4 = this.perPage();
      int var5 = Math.max(0, (var2.size() - 1) / Math.max(1, var4));
      if (var1.page > var5) {
         var1.page = var5;
      }

      String var6 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ (Page " + (var1.page + 1) + ")");
      this.inv = Bukkit.createInventory(this, this.rows() * 9, var6);
      byte var7 = 45;
      byte var8 = 47;
      byte var9 = 48;
      byte var10 = 49;
      byte var11 = 50;
      byte var12 = 51;
      byte var13 = 53;
      if (var1.page > 0) {
         this.inv.setItem(var7, this.createItem(Material.ARROW, "&aʙᴀᴄᴋ", List.of("&fClick to go to the previous page")));
      }

      if (var1.page < var5) {
         this.inv.setItem(var13, this.createItem(Material.ARROW, "&aɴᴇхᴛ", List.of("&fClick to go to the next page")));
      }

      this.inv.setItem(var10, this.createItem(Material.MAP, "&aᴏʀᴅᴇʀѕ", List.of("&fClick to refresh")));
      this.inv.setItem(var11, this.createItem(Material.OAK_SIGN, "&aѕᴇᴀʀᴄʜ", List.of("&fClick to search")));
      this.inv.setItem(var12, this.createItem(Material.CHEST, "&aʏᴏᴜʀ ᴏʀᴅᴇʀѕ", List.of("&fClick to view your orders")));
      ArrayList var14 = new ArrayList();

      for(SortType var18 : SortType.values()) {
         String var10001 = var1.sort == var18 ? "&b• " : "&f• ";
         var14.add(var10001 + this.nameFor(var18));
      }

      this.inv.setItem(var8, this.createItem(Material.CAULDRON, "&aѕᴏʀᴛ", var14));
      ArrayList var34 = new ArrayList();
      ArrayList var35 = new ArrayList();
      var35.add("All");
      var35.addAll(this.pl.filters().categoryNames());

      for(String var38 : var35) {
         String var44 = var38.equalsIgnoreCase(var1.filter) ? "&b• " : "&f• ";
         var34.add(var44 + var38);
      }

      this.inv.setItem(var9, this.createItem(Material.HOPPER, "&aꜰɪʟᴛᴇʀ", var34));
      int var37 = var1.page * var4;
      int var39 = Math.min(var37 + var4, var2.size());
      int var19 = 0;

      for(int var20 = var37; var20 < var39; ++var20) {
         Order var21 = (Order)var2.get(var20);
         OfflinePlayer var22 = Bukkit.getOfflinePlayer(var21.owner);
         String var23 = var22 != null && var22.getName() != null ? var22.getName() : "Unknown";
         HashMap var24 = new HashMap();
         var24.put("player", var23);
         var24.put("item", var21.key.displayName());
         var24.put("price_each", Utils.abbr(var21.priceEach));
         var24.put("delivered", Utils.abbr((double)var21.delivered));
         var24.put("requested", Utils.abbr((double)var21.requested));
         var24.put("paid", Utils.abbr((double)var21.delivered * var21.priceEach));
         var24.put("total", Utils.abbr(var21.totalPrice()));
         ArrayList var25 = new ArrayList();
         var25.add("&f{item}");
         var25.add("&#34ee80${price_each} &feach");
         var25.add("");
         var25.add("&#7E7E29{delivered}/&#008D4C{requested} &#858585Delivered");
         var25.add("&#7E7E29${paid}/&#008D4C${total} &#858585paid");
         var25.add("");
         var25.add("&fClick to deliver &#34ee80{player} &f{item}");
         List var26 = var21.key.enchantLoreLines("&7");
         if (!var26.isEmpty()) {
            var25.add("&7");
            var25.addAll(var26);
         }

         ArrayList var27 = new ArrayList();

         for(String var29 : var25) {
            String var30 = var29;

            for(Map.Entry var32 : var24.entrySet()) {
               var30 = var30.replace("{" + (String)var32.getKey() + "}", (CharSequence)var32.getValue());
            }

            var27.add(var30);
         }

         String var40 = Utils.formatColors("&#34ee80" + var23 + "'s Order");
         ItemStack var41 = new ItemStack(var21.key.material);
         ItemMeta var42 = var41.getItemMeta();
         if (var42 != null) {
            var42.setDisplayName(var40);
            var42.setLore(Utils.formatColors((List)var27));
            var42.addItemFlags(ItemFlag.values());
            var41.setItemMeta(var42);
         }

         ItemStack var43 = GuiVariant.merge(var41, var21.key.buildIcon());
         this.inv.setItem(var19++, var43);
      }

      this.p.openInventory(this.inv);
   }

   private String nameFor(SortType var1) {
      String var10000;
      switch (var1) {
         case MOST_PAID -> var10000 = "Most Paid";
         case MOST_DELIVERED -> var10000 = "Most Delivered";
         case RECENTLY_LISTED -> var10000 = "Recently Listed";
         case MOST_MONEY_PER_ITEM -> var10000 = "Most Money Per Item";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private ItemStack createItem(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(Utils.formatColors(var2));
         if (var3 != null && !var3.isEmpty()) {
            var5.setLore(Utils.formatColors(var3));
         }

         var5.addItemFlags(ItemFlag.values());
         var4.setItemMeta(var5);
      }

      return var4;
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            PlayerStateManager.View var2 = this.pl.state().main(this.p.getUniqueId());
            byte var3 = 45;
            byte var4 = 47;
            byte var5 = 48;
            byte var6 = 49;
            byte var7 = 50;
            byte var8 = 51;
            byte var9 = 53;
            int var10 = var1.getSlot();
            if (var10 == var3) {
               if (var2.page > 0) {
                  --var2.page;
                  this.p.playSound(this.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.1F);
                  this.open();
               }

            } else if (var10 == var9) {
               ArrayList var17 = new ArrayList(this.pl.orders().all());
               var17.removeIf((var0) -> var0.canceled || var0.completed);
               if (var2.search != null && !var2.search.isBlank()) {
                  String var21 = var2.search.toLowerCase(Locale.ENGLISH);
                  var17.removeIf((var1x) -> !var1x.key.displayName().toLowerCase(Locale.ENGLISH).contains(var21) && !var1x.key.material.name().toLowerCase(Locale.ENGLISH).contains(var21));
               }

               if (!"All".equalsIgnoreCase(var2.filter)) {
                  Set var22 = this.pl.filters().resolve(var2.filter);
                  if (var22 != null && !var22.isEmpty()) {
                     var17.removeIf((var1x) -> !var22.contains(var1x.key.material));
                  }
               }

               int var23 = this.perPage();
               int var25 = Math.max(0, (var17.size() - 1) / Math.max(1, var23));
               if (var2.page < var25) {
                  ++var2.page;
                  this.p.playSound(this.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.1F);
                  this.open();
               }

            } else if (var10 == var4) {
               var2.sort = var2.sort.next();
               this.pl.state().saveAllPrefs();
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               var2.page = 0;
               this.open();
            } else if (var10 == var5) {
               ArrayList var16 = new ArrayList();
               var16.add("All");
               var16.addAll(this.pl.filters().categoryNames());
               int var20 = var16.indexOf(var2.filter);
               var2.filter = (String)var16.get((var20 + 1) % var16.size());
               this.pl.state().saveAllPrefs();
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               var2.page = 0;
               this.open();
            } else if (var10 == var6) {
               long var15 = System.currentTimeMillis();
               Long var24 = (Long)lastRefreshTimes.get(this.p.getUniqueId());
               if (var24 == null || var15 - var24 >= 250L) {
                  lastRefreshTimes.put(this.p.getUniqueId(), var15);
                  var2.search = null;
                  var2.page = 0;
                  this.pl.state().saveAllPrefs();
                  this.p.playSound(this.p.getLocation(), Sound.UI_TOAST_IN, 1.0F, 1.3F);
                  this.pl.orders().reload(() -> this.open());
               }
            } else if (var10 == var7) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.closeInventory();
               this.pl.chat().promptConfigured(this.p, "&aEnter search term:", (var1x) -> {
                  if (this.p.isOnline()) {
                     String var2 = var1x == null ? "" : var1x.trim();
                     if (var2.equals("-")) {
                        var2 = "";
                     }

                     PlayerStateManager.View var3 = this.pl.state().main(this.p.getUniqueId());
                     var3.search = var2.isEmpty() ? null : var2;
                     var3.page = 0;
                     this.pl.state().saveAllPrefs();
                     Bukkit.getScheduler().runTask(this.pl.getPlugin(), () -> (new OrdersMainMenu(this.pl, this.p)).open());
                  }
               });
            } else if (var10 == var8) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               (new YourOrdersMenu(this.pl, this.p)).open();
            } else {
               ArrayList var11 = new ArrayList(this.pl.orders().all());
               var11.removeIf((var0) -> var0.canceled || var0.completed);
               if (var2.search != null && !var2.search.isBlank()) {
                  String var12 = var2.search.toLowerCase(Locale.ENGLISH);
                  var11.removeIf((var1x) -> !var1x.key.displayName().toLowerCase(Locale.ENGLISH).contains(var12) && !var1x.key.material.name().toLowerCase(Locale.ENGLISH).contains(var12));
               }

               if (!"All".equalsIgnoreCase(var2.filter)) {
                  Set var18 = this.pl.filters().resolve(var2.filter);
                  if (var18 != null && !var18.isEmpty()) {
                     var11.removeIf((var1x) -> !var18.contains(var1x.key.material));
                  }
               }

               switch (var2.sort) {
                  case MOST_PAID -> var11.sort(Comparator.comparingDouble((var0) -> -((double)var0.delivered * var0.priceEach)));
                  case MOST_DELIVERED -> var11.sort(Comparator.comparingInt((var0) -> -var0.delivered));
                  case RECENTLY_LISTED -> Collections.reverse(var11);
                  case MOST_MONEY_PER_ITEM -> var11.sort(Comparator.comparingDouble((var0) -> -var0.priceEach));
               }

               int var19 = this.perPage();
               int var13 = var2.page * var19 + var10;
               if (var13 >= 0 && var13 < var11.size()) {
                  Order var14 = (Order)var11.get(var13);
                  if (var14.owner.equals(this.p.getUniqueId())) {
                     this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                     return;
                  }

                  (new DeliverItemsMenu(this.pl, this.p, var14)).open();
               }

            }
         }
      }
   }

   public void onClose(InventoryCloseEvent var1) {
   }
}
