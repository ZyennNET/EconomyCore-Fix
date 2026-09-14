package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.data.SortType;
import com.prismcore.survival.orders.input.ChatInputManager;
import com.prismcore.survival.orders.store.PlayerStateManager;
import com.prismcore.survival.orders.util.TaskUtil;
import com.prismcore.survival.orders.utils.SignInputUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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

public class SelectItemMenu implements InventoryHolder, MenuOwner {
   private static final String META_SUPPRESS_CLOSE = "prism.orders.suppressClose";
   private final PrismOrders pl;
   private final Player p;
   private Inventory inv;

   public SelectItemMenu(PrismOrders var1, Player var2) {
      this.pl = var1;
      this.p = var2;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   private int perPage() {
      return 45;
   }

   private String nameFor(SortType var1) {
      switch (var1) {
         case MOST_PAID -> {
            return "A-Z";
         }
         case MOST_DELIVERED -> {
            return "Z-A";
         }
         case MOST_MONEY_PER_ITEM -> {
            return "Highest Price";
         }
         case RECENTLY_LISTED -> {
            return "Lowest Price";
         }
         default -> {
            return var1.name();
         }
      }
   }

   private ItemStack createItem(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(Utils.formatColors(var2));
         if (var3 != null) {
            var5.setLore(Utils.formatColors(var3));
         }

         var5.addItemFlags(ItemFlag.values());
         var4.setItemMeta(var5);
      }

      return var4;
   }

   public void open() {
      this.open(true);
   }

   public void open(boolean var1) {
      PlayerStateManager.ItemView var2 = this.pl.state().select(this.p.getUniqueId());
      if (var2.sort == null) {
         var2.sort = SortType.MOST_PAID;
      }

      if (var2.filter == null || var2.filter.isBlank()) {
         var2.filter = "All";
      }

      if (var2.page < 0) {
         var2.page = 0;
      }

      String var3 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Select Item");
      this.inv = Bukkit.createInventory(this, 54, var3);
      byte var4 = 45;
      byte var5 = 53;
      byte var6 = 48;
      byte var7 = 49;
      byte var8 = 50;
      List var9 = (List)Arrays.stream(Material.values()).filter((var0) -> var0.isItem() && !var0.isAir()).collect(Collectors.toList());
      if (var2.search != null && !var2.search.isBlank()) {
         String var10 = var2.search.toLowerCase(Locale.ENGLISH);
         var9.removeIf((var1x) -> !var1x.name().replace("_", " ").toLowerCase(Locale.ENGLISH).contains(var10));
      }

      Set var20;
      if (!"All".equalsIgnoreCase(var2.filter) && (var20 = this.pl.filters().resolve(var2.filter)) != null && !var20.isEmpty()) {
         var9.removeIf((var1x) -> !var20.contains(var1x));
      }

      switch (var2.sort) {
         case MOST_PAID -> var9.sort(Comparator.comparing(Enum::name));
         case MOST_DELIVERED -> var9.sort(Comparator.comparing(Enum::name).reversed());
         default -> var9.sort(Comparator.comparing(Enum::name));
      }

      int var11 = Math.max(0, (var9.size() - 1) / this.perPage());
      if (var2.page > var11) {
         var2.page = var11;
      }

      int var12 = var2.page * this.perPage();
      int var13 = Math.min(var12 + this.perPage(), var9.size());

      for(int var14 = var12; var14 < var13; ++var14) {
         Material var15 = (Material)var9.get(var14);
         ItemStack var16 = new ItemStack(var15);
         ItemMeta var17 = var16.getItemMeta();
         if (var17 != null) {
            var17.setLore(Utils.formatColors(List.of("&fClick to select")));
            var16.setItemMeta(var17);
         }

         this.inv.setItem(var14 - var12, var16);
      }

      if (var2.page > 0) {
         this.inv.setItem(var4, this.createItem(Material.ARROW, "&aʙᴀᴄᴋ", List.of("&fClick to go to the previous page")));
      }

      if (var2.page < var11) {
         this.inv.setItem(var5, this.createItem(Material.ARROW, "&aɴᴇхᴛ", List.of("&fClick to go to the next page")));
      }

      ArrayList var21 = new ArrayList();
      SortType[] var22 = new SortType[]{SortType.MOST_PAID, SortType.MOST_DELIVERED, SortType.MOST_MONEY_PER_ITEM, SortType.RECENTLY_LISTED};

      for(SortType var19 : var22) {
         String var10001 = var2.sort == var19 ? "&5• " : "&f• ";
         var21.add(var10001 + this.nameFor(var19));
      }

      this.inv.setItem(var6, this.createItem(Material.CAULDRON, "&aѕᴏʀᴛ", var21));
      ArrayList var24 = new ArrayList();
      ArrayList var26 = new ArrayList();
      var26.add("All");
      var26.addAll(this.pl.filters().categoryNames());

      for(String var28 : var26) {
         String var29 = var28.equalsIgnoreCase(var2.filter) ? "&5• " : "&f• ";
         var24.add(var29 + var28);
      }

      this.inv.setItem(var7, this.createItem(Material.HOPPER, "&aꜰɪʟᴛᴇʀ", var24));
      this.inv.setItem(var8, this.createItem(Material.OAK_SIGN, "&aѕᴇᴀʀᴄʜ", List.of("&fClick to search")));
      this.p.openInventory(this.inv);
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            PlayerStateManager.ItemView var2 = this.pl.state().select(this.p.getUniqueId());
            int var3 = var1.getSlot();
            byte var4 = 45;
            byte var5 = 53;
            byte var6 = 48;
            byte var7 = 49;
            byte var8 = 50;
            if (var3 == var4) {
               if (var2.page > 0) {
                  --var2.page;
                  this.p.playSound(this.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
                  this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
                  this.open(false);
               }

            } else if (var3 == var5) {
               List var12 = (List)Arrays.stream(Material.values()).filter((var0) -> var0.isItem() && !var0.isAir()).collect(Collectors.toList());
               if (var2.search != null && !var2.search.isBlank()) {
                  String var14 = var2.search.toLowerCase(Locale.ENGLISH);
                  var12.removeIf((var1x) -> !var1x.name().replace("_", " ").toLowerCase(Locale.ENGLISH).contains(var14));
               }

               if (!"All".equalsIgnoreCase(var2.filter)) {
                  Set var15 = this.pl.filters().resolve(var2.filter);
                  if (var15 != null && !var15.isEmpty()) {
                     var12.removeIf((var1x) -> !var15.contains(var1x));
                  }
               }

               int var16 = Math.max(0, (var12.size() - 1) / this.perPage());
               if (var2.page < var16) {
                  ++var2.page;
                  this.p.playSound(this.p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
                  this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
                  this.open(false);
               }

            } else if (var3 == var6) {
               if (var2.sort == SortType.MOST_PAID) {
                  var2.sort = SortType.MOST_DELIVERED;
               } else if (var2.sort == SortType.MOST_DELIVERED) {
                  var2.sort = SortType.MOST_MONEY_PER_ITEM;
               } else if (var2.sort == SortType.MOST_MONEY_PER_ITEM) {
                  var2.sort = SortType.RECENTLY_LISTED;
               } else {
                  var2.sort = SortType.MOST_PAID;
               }

               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.open(false);
            } else if (var3 == var7) {
               ArrayList var11 = new ArrayList();
               var11.add("All");
               var11.addAll(this.pl.filters().categoryNames());
               int var13 = var11.indexOf(var2.filter);
               var2.filter = (String)var11.get((var13 + 1) % var11.size());
               var2.page = 0;
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.open(false);
            } else if (var3 == var8) {
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
               this.p.closeInventory();
               SignInputUtil.open(this.pl.getPlugin(), this.p, "&aEnter search term:", (var1x) -> {
                  if (this.p.isOnline()) {
                     String var2 = var1x == null ? "" : var1x.trim();
                     if (var2.equals("-")) {
                        var2 = "";
                     }

                     this.pl.state().select(this.p.getUniqueId()).search = var2.isEmpty() ? null : var2;
                     this.pl.state().select(this.p.getUniqueId()).page = 0;
                     this.pl.state().saveAllPrefs();
                     Bukkit.getScheduler().runTask(this.pl.getPlugin(), () -> (new SelectItemMenu(this.pl, this.p)).open());
                  }
               });
            } else {
               if (var3 < 45 && var3 >= 0) {
                  ItemStack var9 = var1.getCurrentItem();
                  if (var9 != null && var9.getType() != Material.AIR) {
                     ChatInputManager.NewOrderSession var10 = this.pl.chat().session(this.p.getUniqueId());
                     var10.chosenItem = var9.getType().name();
                     this.p.setMetadata("prism.orders.tmpChosenStack", new FixedMetadataValue(this.pl.getPlugin(), var9.clone()));
                     this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                     this.p.setMetadata("prism.orders.suppressClose", new FixedMetadataValue(this.pl.getPlugin(), true));
                     (new NewOrderMenu(this.pl, this.p)).open();
                  }
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
               TaskUtil.runEntityLater(this.pl.getPlugin(), this.p, () -> (new NewOrderMenu(this.pl, this.p)).open(), 1L);
            }
         }
      }
   }
}
