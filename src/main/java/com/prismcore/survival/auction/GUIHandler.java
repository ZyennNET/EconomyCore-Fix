package com.prismcore.survival.auction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GUIHandler {
   public static final int ITEMS_PER_PAGE = 45;

   public static void openMainGUI(Player var0, int var1, AuctionController var2) {
      FileConfiguration var4 = var2.getConfig();
      String var3 = var0.hasMetadata("ah-filter") ? ((MetadataValue)var0.getMetadata("ah-filter").get(0)).asString() : "";
      if (var3 == null) {
         var3 = "";
      }

      var3 = var3.trim().toLowerCase();
      String var6 = var0.hasMetadata("ah-cat") ? ((MetadataValue)var0.getMetadata("ah-cat").get(0)).asString() : "All";
      List var7 = var2.getAuctionManager().getActiveItems();
      ArrayList var8 = new ArrayList();

      for(AuctionItem var10 : var7) {
         boolean var12 = var3.isEmpty() || Utils.prettifyMaterialName(var10.getItemStack().getType()).toLowerCase().contains(var3) || var10.getSeller().toLowerCase().contains(var3);
         boolean var11 = var6.equals("All") || var2.getFilterConfig().getStringList(var6).contains(var10.getItemStack().getType().name());
         if (var12 && var11) {
            var8.add(var10);
         }
      }

      String var38 = var2.getAuctionManager().getPlayerSort(var0.getUniqueId());
      sortItems(var8, var38);
      int var39 = var4.getInt("main-gui.items-per-page", 45);
      int var40 = var8.size();
      int var41 = Math.max(1, (int)Math.ceil((double)var40 / (double)var39));
      var1 = Math.max(1, Math.min(var1, var41));
      String var13 = var0.hasMetadata("ah-admin-view") ? "&8ᴀᴅᴍɪɴ ᴀᴜᴄᴛɪᴏɴ" : var4.getString("main-gui.title");
      String var14 = Utils.formatColors(var13.replace("%page%", String.valueOf(var1)).replace("%max-page%", String.valueOf(var41)));
      Inventory var15 = Bukkit.createInventory(new MainHolder(), 54, var14);
      int var16 = (var1 - 1) * var39;
      int var17 = Math.min(var16 + var39, var40);
      List var18 = var8.subList(var16, var17);

      for(int var19 = 0; var19 < var18.size(); ++var19) {
         AuctionItem var20 = (AuctionItem)var18.get(var19);
         ItemStack var21 = var20.getItemStack().clone();
         ItemMeta var22 = var21.getItemMeta();
         ArrayList var23 = var22 != null && var22.hasLore() ? new ArrayList(var22.getLore()) : new ArrayList();
         ArrayList var24 = new ArrayList();
         long var25 = (System.currentTimeMillis() - var20.getListedAt()) / 1000L;
         long var27 = (long)var20.getDuration() - var25;
         if (var27 < 0L) {
            var27 = 0L;
         }

         String var29 = FormatUtils.formatTime((int)var27);
         String var30 = Utils.formatNumber(var20.getPrice());

         for(String var32 : var4.getStringList("main-gui.lore-item")) {
            var24.add(Utils.formatColors(var32.replace("{priceFormatted}", var30).replace("{seller}", var20.getSeller()).replace("{time}", var29)));
         }

         ArrayList var42 = new ArrayList();
         if (!var23.isEmpty()) {
            var42.addAll(var23);
         }

         if (!var23.isEmpty() && !var24.isEmpty()) {
            var42.add("");
         }

         var42.addAll(var24);
         if (var0.hasMetadata("ah-admin-view")) {
            var42.add(Utils.formatColors("&7Click to see details"));
         }

         var22.setLore(var42);
         PersistentDataContainer var43 = var22.getPersistentDataContainer();
         NamespacedKey var33 = new NamespacedKey(var2.getPlugin(), "auction-expire");
         long var34 = var20.getListedAt() + (long)var20.getDuration() * 1000L;
         var43.set(var33, PersistentDataType.LONG, var34);
         var21.setItemMeta(var22);
         var15.setItem(var19, var21);
      }

      setBottomControls(var15, var4, var38, var6, var2, var1);
      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.openInventory(var15);
      var0.setMetadata("ah-page", new FixedMetadataValue(var2.getPlugin(), var1));
      var2.startUpdateTask(var0);
   }

   private static boolean isShulkerBoxItem(ItemStack var0) {
      if (var0 != null && !var0.getType().isAir()) {
         if (var0.getType() != Material.SHULKER_BOX && !var0.getType().name().endsWith("_SHULKER_BOX")) {
            return false;
         } else {
            return var0.hasItemMeta() && var0.getItemMeta() instanceof BlockStateMeta && ((BlockStateMeta)var0.getItemMeta()).getBlockState() instanceof ShulkerBox;
         }
      } else {
         return false;
      }
   }

   private static ItemStack makeItem(FileConfiguration var0, String var1) {
      Material var2 = Material.valueOf(var0.getString(var1 + ".material"));
      ItemStack var3 = new ItemStack(var2);
      ItemMeta var4 = var3.getItemMeta();
      var4.setDisplayName(Utils.formatColors(var0.getString(var1 + ".display-name")));
      var4.setLore(Utils.formatColors(var0.getStringList(var1 + ".lore")));
      var3.setItemMeta(var4);
      return var3;
   }

   public static String getNextCategory(AuctionController var0, String var1) {
      ArrayList var2 = new ArrayList();
      var2.add("All");
      var2.addAll(var0.getFilterConfig().getKeys(false));
      int var3 = var2.indexOf(var1);
      if (var3 == -1) {
         var3 = 0;
      }

      return (String)var2.get((var3 + 1) % var2.size());
   }

   public static void sortItems(List<AuctionItem> var0, String var1) {
      switch (var1) {
         case "Highest Price" -> var0.sort(Comparator.comparingDouble(AuctionItem::getPrice).reversed());
         case "Lowest Price" -> var0.sort(Comparator.comparingDouble(AuctionItem::getPrice));
         case "Last Listed" -> var0.sort(Comparator.comparingLong(AuctionItem::getListedAt));
         case "Recently Listed" -> var0.sort(Comparator.comparingLong(AuctionItem::getListedAt).reversed());
         default -> var0.sort(Comparator.comparingDouble(AuctionItem::getPrice).reversed());
      }

   }

   public static void openSellConfirm(Player var0, double var1, AuctionController var3) {
      FileConfiguration var4 = var3.getConfig();
      String var5 = Utils.formatColors(var4.getString("sell-confirm-gui.title"));
      Inventory var6 = Bukkit.createInventory(new SellConfirmHolder(), 27, var5);
      ItemStack var7 = var0.getInventory().getItemInMainHand();
      if (var7 != null && !var7.getType().isAir()) {
         ItemStack var8 = var7.clone();
         ItemMeta var9 = var8.getItemMeta();
         List var10 = var4.getStringList("sell-confirm-gui.preview-lore");
         ArrayList var11 = new ArrayList();
         String var12 = Utils.formatNumber(var1);

         for(Object var14 : var10) {
            String var15 = (String)var14;
            String var16 = var15.replace("{priceFormatted}", var12);
            var11.add(Utils.formatColors(var16));
         }

         var9.setLore(var11);
         var8.setItemMeta(var9);
         var6.setItem(13, var8);
         int var19 = var4.getInt("sell-confirm-gui.decline-button.slot");
         ItemStack var20 = new ItemStack(Material.valueOf(var4.getString("sell-confirm-gui.decline-button.material")));
         ItemMeta var21 = var20.getItemMeta();
         var21.setDisplayName(Utils.formatColors(var4.getString("sell-confirm-gui.decline-button.display-name")));
         var21.setLore(Utils.formatColors(var4.getStringList("sell-confirm-gui.decline-button.lore")));
         var20.setItemMeta(var21);
         var6.setItem(var19, var20);
         int var22 = var4.getInt("sell-confirm-gui.confirm-button.slot");
         ItemStack var17 = new ItemStack(Material.valueOf(var4.getString("sell-confirm-gui.confirm-button.material")));
         ItemMeta var18 = var17.getItemMeta();
         var18.setDisplayName(Utils.formatColors(var4.getString("sell-confirm-gui.confirm-button.display-name")));
         var18.setLore(Utils.formatColors(var4.getStringList("sell-confirm-gui.confirm-button.lore")));
         var17.setItemMeta(var18);
         var6.setItem(var22, var17);
         var0.setMetadata("ah-switching", new FixedMetadataValue(var3.getPlugin(), true));
         var0.openInventory(var6);
         var0.setMetadata("ah-sell-price", new FixedMetadataValue(var3.getPlugin(), var1));
      } else {
         var0.sendMessage(Utils.formatColors(var4.getString("messages.disabled-item")));
      }
   }

   public static void openBuyConfirm(Player var0, AuctionItem var1, AuctionController var2) {
      FileConfiguration var3 = var2.getConfig();
      String var4 = Utils.formatColors(var3.getString("purchase-confirm-gui.title"));
      boolean var5 = isShulkerBoxItem(var1.getItemStack());
      int var6 = var5 ? 54 : 27;
      Inventory var7 = Bukkit.createInventory(new BuyConfirmHolder(), var6, var4);
      ItemStack var8 = var1.getItemStack().clone();
      ItemMeta var9 = var8.getItemMeta();
      ArrayList var10 = var9 != null && var9.hasLore() ? new ArrayList(var9.getLore()) : new ArrayList();
      List var11 = var3.getStringList("main-gui.lore-item");
      ArrayList var12 = new ArrayList();
      long var13 = (System.currentTimeMillis() - var1.getListedAt()) / 1000L;
      long var15 = (long)var1.getDuration() - var13;
      if (var15 < 0L) {
         var15 = 0L;
      }

      String var17 = FormatUtils.formatTime((int)var15);
      String var18 = Utils.formatNumber(var1.getPrice());

      for(Object var20 : var11) {
         String var21 = (String)var20;
         String var22 = var21.replace("{priceFormatted}", var18).replace("{seller}", var1.getSeller()).replace("{time}", var17);
         var12.add(Utils.formatColors(var22));
      }

      ArrayList var31 = new ArrayList();
      if (!var10.isEmpty()) {
         var31.addAll(var10);
      }

      if (!var10.isEmpty() && !var12.isEmpty()) {
         var31.add("");
      }

      var31.addAll(var12);
      var9.setLore(var31);
      var8.setItemMeta(var9);
      var7.setItem(13, var8);
      int var32 = var3.getInt("purchase-confirm-gui.decline-button.slot");
      ItemStack var33 = new ItemStack(Material.valueOf(var3.getString("purchase-confirm-gui.decline-button.material")));
      ItemMeta var34 = var33.getItemMeta();
      var34.setDisplayName(Utils.formatColors(var3.getString("purchase-confirm-gui.decline-button.display-name")));
      var34.setLore(Utils.formatColors(var3.getStringList("purchase-confirm-gui.decline-button.lore")));
      var33.setItemMeta(var34);
      var7.setItem(var32, var33);
      int var23 = var3.getInt("purchase-confirm-gui.confirm-button.slot");
      ItemStack var24 = new ItemStack(Material.valueOf(var3.getString("purchase-confirm-gui.confirm-button.material")));
      ItemMeta var25 = var24.getItemMeta();
      var25.setDisplayName(Utils.formatColors(var3.getString("purchase-confirm-gui.confirm-button.display-name")));
      var25.setLore(Utils.formatColors(var3.getStringList("purchase-confirm-gui.confirm-button.lore")));
      var24.setItemMeta(var25);
      var7.setItem(var23, var24);
      if (var5) {
         BlockStateMeta var26 = (BlockStateMeta)var1.getItemStack().getItemMeta();
         ShulkerBox var27 = (ShulkerBox)var26.getBlockState();
         ItemStack[] var28 = var27.getInventory().getContents();

         for(int var29 = 0; var29 < 27 && var29 < var28.length; ++var29) {
            ItemStack var30 = var28[var29];
            if (var30 != null && !var30.getType().isAir()) {
               var7.setItem(27 + var29, var30.clone());
            }
         }
      }

      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.openInventory(var7);
      var0.setMetadata("ah-buy-item", new FixedMetadataValue(var2.getPlugin(), var1.getId().toString()));
   }

   public static List<String> buildFilterLore(String var0, AuctionController var1) {
      FileConfiguration var2 = var1.getConfig();
      String var3 = var2.getString("main-gui.sort-colors.current");
      String var4 = var2.getString("main-gui.sort-colors.not-current");
      ArrayList var5 = new ArrayList();
      var5.add("All");
      var5.addAll(var1.getFilterConfig().getKeys(false));
      ArrayList var6 = new ArrayList();

      for(String var8 : var5) {
         String var9 = var8.equals(var0) ? var3 : var4;
         var6.add(Utils.formatColors(var9 + "• " + var8));
      }

      return var6;
   }

   public static void openYourItemsGUI(Player var0, AuctionController var1) {
      FileConfiguration var2 = var1.getConfig();
      ArrayList var3 = new ArrayList();

      for(AuctionItem var5 : var1.getAuctionManager().getItems()) {
         if (var5.getSeller().equals(var0.getName())) {
            var3.add(var5);
         }
      }

      String var25 = Utils.formatColors(var2.getString("your-items-gui.title"));
      Inventory var26 = Bukkit.createInventory(new YourItemsHolder(), 27, var25);
      if (var3.isEmpty()) {
         int var6 = var2.getInt("your-items-gui.help.slot");
         ItemStack var7 = new ItemStack(Material.valueOf(var2.getString("your-items-gui.help.material")));
         ItemMeta var8 = var7.getItemMeta();
         var8.setDisplayName(Utils.formatColors(var2.getString("your-items-gui.help.display-name")));
         var8.setLore(Utils.formatColors(var2.getStringList("your-items-gui.help.lore")));
         var7.setItemMeta(var8);
         var26.setItem(var6, var7);
      } else {
         for(int var27 = 0; var27 < var3.size() && var27 < 25; ++var27) {
            AuctionItem var29 = (AuctionItem)var3.get(var27);
            ItemStack var31 = var29.getItemStack().clone();
            ItemMeta var9 = var31.getItemMeta();
            ArrayList var10 = var9 != null && var9.hasLore() ? new ArrayList(var9.getLore()) : new ArrayList();
            List var11 = var2.getStringList("main-gui.lore-item");
            ArrayList var12 = new ArrayList();
            long var13 = (System.currentTimeMillis() - var29.getListedAt()) / 1000L;
            long var15 = (long)var29.getDuration() - var13;
            if (var15 < 0L) {
               var15 = 0L;
            }

            String var17 = FormatUtils.formatTime((int)var15);
            String var18 = Utils.formatNumber(var29.getPrice());

            for(Object var20 : var11) {
               String var21 = (String)var20;
               String var22 = var21.replace("{priceFormatted}", var18).replace("{seller}", var29.getSeller()).replace("{time}", var17);
               var12.add(Utils.formatColors(var22));
            }

            ArrayList var36 = new ArrayList();
            if (!var10.isEmpty()) {
               var36.addAll(var10);
            }

            if (var15 == 0L) {
               var36.add(Utils.formatColors("&#ff4444Expired"));
            }

            if ((!var10.isEmpty() || var15 == 0L) && !var12.isEmpty()) {
               var36.add("");
            }

            var36.addAll(var12);
            var9.setLore(var36);
            PersistentDataContainer var37 = var9.getPersistentDataContainer();
            NamespacedKey var38 = new NamespacedKey(var1.getPlugin(), "auction-expire");
            long var39 = var29.getListedAt() + (long)var29.getDuration() * 1000L;
            var37.set(var38, PersistentDataType.LONG, var39);
            var31.setItemMeta(var9);
            int var24 = var27 < 18 ? var27 : var27 + 1;
            var26.setItem(var24, var31);
         }
      }

      int var28 = var2.getInt("your-items-gui.back-button.slot");
      ItemStack var30 = new ItemStack(Material.valueOf(var2.getString("your-items-gui.back-button.material")));
      ItemMeta var32 = var30.getItemMeta();
      var32.setDisplayName(Utils.formatColors(var2.getString("your-items-gui.back-button.display-name")));
      var32.setLore(Utils.formatColors(var2.getStringList("your-items-gui.back-button.lore")));
      var30.setItemMeta(var32);
      var26.setItem(var28, var30);
      int var33 = var2.getInt("your-items-gui.transactions-button.slot");
      ItemStack var34 = new ItemStack(Material.valueOf(var2.getString("your-items-gui.transactions-button.material")));
      ItemMeta var35 = var34.getItemMeta();
      var35.setDisplayName(Utils.formatColors(var2.getString("your-items-gui.transactions-button.display-name")));
      var35.setLore(Utils.formatColors(var2.getStringList("your-items-gui.transactions-button.lore")));
      var34.setItemMeta(var35);
      var26.setItem(var33, var34);
      var0.setMetadata("ah-switching", new FixedMetadataValue(var1.getPlugin(), true));
      var0.openInventory(var26);
      var1.startUpdateTask(var0);
   }

   public static void openTransactionsGUI(Player var0, int var1, AuctionController var2) {
      FileConfiguration var3 = var2.getConfig();
      String var4 = var0.hasMetadata("tx-filter") ? ((MetadataValue)var0.getMetadata("tx-filter").get(0)).asString() : "";
      String var5 = var4 == null ? "" : var4.trim().toLowerCase();
      List var6 = var2.getTransactionManager().getPlayerTransactions(var0.getUniqueId());
      List var7 = (List)var6.stream().filter((var1x) -> {
         if (var5.isEmpty()) {
            return true;
         } else {
            String var2 = Utils.prettifyMaterialName(var1x.getItem().getType()).toLowerCase();
            String var3 = var1x.isSale() ? var1x.getBuyer() : var1x.getSeller();
            return var2.contains(var5) || var3 != null && var3.toLowerCase().contains(var5);
         }
      }).collect(Collectors.toList());
      int var8 = var3.getInt("transactions-gui.items-per-page", 45);
      int var9 = var7.size();
      int var10 = Math.max(1, (int)Math.ceil((double)var9 / (double)var8));
      if (var1 < 1) {
         var1 = 1;
      }

      if (var1 > var10) {
         var1 = var10;
      }

      String var11 = Utils.formatColors(var3.getString("transactions-gui.title").replace("%page%", String.valueOf(var1)).replace("%max-page%", String.valueOf(var10)));
      Inventory var12 = Bukkit.createInventory(new TransactionsHolder(), 54, var11);
      int var13 = (var1 - 1) * var8;
      int var14 = Math.min(var13 + var8, var9);
      List var15 = var7.subList(var13, var14);

      for(int var16 = 0; var16 < var15.size(); ++var16) {
         Transaction var17 = (Transaction)var15.get(var16);
         ItemStack var18 = var17.getItem().clone();
         ItemMeta var19 = var18.getItemMeta();
         List var20 = var17.isSale() ? var3.getStringList("transactions-gui.lore-sold") : var3.getStringList("transactions-gui.lore-bought");
         ArrayList var21 = new ArrayList();
         long var22 = (System.currentTimeMillis() - var17.getTimestamp()) / 1000L;
         String var24 = FormatUtils.formatTime((int)var22);
         String var25 = var17.isSale() ? var17.getBuyer() : var17.getSeller();
         String var26 = Utils.prettifyMaterialName(var17.getItem().getType());
         String var27 = Utils.formatNumber(var17.getPrice());

         for(Object var29 : var20) {
            String var30 = (String)var29;
            String var31 = var30.replace("{player}", var25).replace("{item}", var26).replace("{amount}", var27).replace("{time-ago}", var24);
            var21.add(Utils.formatColors(var31));
         }

         var19.setLore(var21);
         PersistentDataContainer var46 = var19.getPersistentDataContainer();
         NamespacedKey var49 = new NamespacedKey(var2.getPlugin(), "transaction-timestamp");
         var46.set(var49, PersistentDataType.LONG, var17.getTimestamp());
         var18.setItemMeta(var19);
         var12.setItem(var16, var18);
      }

      int var37 = var3.getInt("transactions-gui.items.previous-page.slot");
      ItemStack var38 = new ItemStack(Material.valueOf(var3.getString("transactions-gui.items.previous-page.material")));
      ItemMeta var39 = var38.getItemMeta();
      var39.setDisplayName(Utils.formatColors(var3.getString("transactions-gui.items.previous-page.display-name")));
      var39.setLore(Utils.formatColors(var3.getStringList("transactions-gui.items.previous-page.lore")));
      var38.setItemMeta(var39);
      var12.setItem(var37, var38);
      int var40 = var3.getInt("transactions-gui.items.sort.slot", 46);
      if (var40 >= 0 && var40 < var12.getSize()) {
         var12.setItem(var40, (ItemStack)null);
      }

      int var41 = var3.getInt("transactions-gui.stats-button.slot");
      ItemStack var42 = new ItemStack(Material.valueOf(var3.getString("transactions-gui.stats-button.material")));
      ItemMeta var43 = var42.getItemMeta();
      var43.setDisplayName(Utils.formatColors(var3.getString("transactions-gui.stats-button.display-name")));
      double var23 = var2.getTransactionManager().getTotalSpent(var0.getUniqueId());
      double var44 = var2.getTransactionManager().getTotalMade(var0.getUniqueId());
      ArrayList var45 = new ArrayList();

      for(Object var50 : var3.getStringList("transactions-gui.stats-button.lore")) {
         String var52 = (String)var50;
         String var54 = var52.replace("{spent-amount}", Utils.formatNumber(var23)).replace("{made-amount}", Utils.formatNumber(var44));
         var45.add(Utils.formatColors(var54));
      }

      var43.setLore(var45);
      var42.setItemMeta(var43);
      var12.setItem(var41, var42);
      int var48 = var3.getInt("transactions-gui.items.refresh.slot");
      ItemStack var51 = new ItemStack(Material.valueOf(var3.getString("transactions-gui.items.refresh.material")));
      ItemMeta var53 = var51.getItemMeta();
      var53.setDisplayName(Utils.formatColors(var3.getString("transactions-gui.items.refresh.display-name")));
      var53.setLore(Utils.formatColors(var3.getStringList("transactions-gui.items.refresh.lore")));
      var51.setItemMeta(var53);
      var12.setItem(var48, var51);
      int var55 = var3.getInt("transactions-gui.items.search.slot");
      ItemStack var32 = new ItemStack(Material.valueOf(var3.getString("transactions-gui.items.search.material")));
      ItemMeta var33 = var32.getItemMeta();
      var33.setDisplayName(Utils.formatColors(var3.getString("transactions-gui.items.search.display-name")));
      var33.setLore(Utils.formatColors(var3.getStringList("transactions-gui.items.search.lore")));
      var32.setItemMeta(var33);
      var12.setItem(var55, var32);
      int var34 = var3.getInt("transactions-gui.items.next-page.slot");
      ItemStack var35 = new ItemStack(Material.valueOf(var3.getString("transactions-gui.items.next-page.material")));
      ItemMeta var36 = var35.getItemMeta();
      var36.setDisplayName(Utils.formatColors(var3.getString("transactions-gui.items.next-page.display-name")));
      var36.setLore(Utils.formatColors(var3.getStringList("transactions-gui.items.next-page.lore")));
      var35.setItemMeta(var36);
      var12.setItem(var34, var35);
      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.openInventory(var12);
      var0.setMetadata("tx-page", new FixedMetadataValue(var2.getPlugin(), var1));
      var2.startUpdateTask(var0);
   }

   public static void openFilterGUI(Player var0, AuctionController var1) {
      FileConfiguration var2 = var1.getFilterConfig();
      LinkedHashSet var3 = new LinkedHashSet();
      var3.add("All");
      var3.addAll(var2.getKeys(false));
      int var4 = (var3.size() + 8) / 9 * 9;
      Inventory var5 = Bukkit.createInventory(new FilterHolder(), var4, Utils.formatColors("&#444444Choose Filter"));
      int var6 = 0;

      for(String var8 : var3) {
         ItemStack var9 = new ItemStack(Material.PAPER);
         ItemMeta var10 = var9.getItemMeta();
         var10.setDisplayName(Utils.formatColors("&f" + var8));
         var9.setItemMeta(var10);
         var5.setItem(var6++, var9);
      }

      var0.setMetadata("ah-switching", new FixedMetadataValue(var1.getPlugin(), true));
      var0.openInventory(var5);
      var1.startUpdateTask(var0);
   }

   public static void openAdminPlayerDetailsGUI(Player var0, String var1, AuctionController var2) {
      FileConfiguration var3 = var2.getConfig();
      String var4 = Utils.formatColors("&8" + Utils.toSmallCaps(var1) + "'ѕ ᴀᴜᴄᴛɪᴏɴ ᴅᴇᴛᴀɪʟ");
      Inventory var5 = Bukkit.createInventory(new AdminPlayerDetailsHolder(), 54, var4);
      List var6 = (List)var2.getAuctionManager().getItems().stream().filter((var1x) -> var1x.getSeller().equalsIgnoreCase(var1)).collect(Collectors.toList());

      for(int var7 = 0; var7 < var6.size() && var7 < 27; ++var7) {
         AuctionItem var8 = (AuctionItem)var6.get(var7);
         ItemStack var9 = var8.getItemStack().clone();
         ItemMeta var10 = var9.getItemMeta();
         Object var11 = var10.hasLore() ? var10.getLore() : new ArrayList();
         long var12 = (System.currentTimeMillis() - var8.getListedAt()) / 1000L;
         long var14 = (long)var8.getDuration() - var12;
         if (var14 < 0L) {
            var14 = 0L;
         }

         String var16 = FormatUtils.formatTime((int)var14);
         String var17 = Utils.formatNumber(var8.getPrice());

         for(String var19 : var3.getStringList("main-gui.lore-item")) {
            ((List)var11).add(Utils.formatColors(var19.replace("{priceFormatted}", var17).replace("{seller}", var8.getSeller()).replace("{time}", var16)));
         }

         ((List)var11).add(Utils.formatColors("&fManage this item"));
         var10.setLore((List)var11);
         PersistentDataContainer var31 = var10.getPersistentDataContainer();
         NamespacedKey var33 = new NamespacedKey(var2.getPlugin(), "auction-expire");
         long var20 = var8.getListedAt() + (long)var8.getDuration() * 1000L;
         var31.set(var33, PersistentDataType.LONG, var20);
         var9.setItemMeta(var10);
         var5.setItem(var7, var9);
      }

      ItemStack var22 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var23 = var22.getItemMeta();
      var23.setDisplayName(Utils.formatColors("&cʙᴀᴄᴋ ᴛᴏ ʟɪѕᴛ"));
      ArrayList var24 = new ArrayList();
      var24.add(Utils.formatColors("&fClick to return to player list"));
      var23.setLore(var24);
      var22.setItemMeta(var23);
      var5.setItem(45, var22);
      ItemStack var25 = new ItemStack(Material.BARRIER);
      ItemMeta var26 = var25.getItemMeta();
      var26.setDisplayName(Utils.formatColors("&cᴅᴇʟᴇᴛᴇ ᴀʟʟ ɪᴛᴇᴍѕ"));
      ArrayList var27 = new ArrayList();
      var27.add(Utils.formatColors("&fClick to delete all items from this player"));
      var26.setLore(var27);
      var25.setItemMeta(var26);
      var5.setItem(48, var25);
      ItemStack var13 = new ItemStack(Material.OAK_SIGN);
      ItemMeta var28 = var13.getItemMeta();
      var28.setDisplayName(Utils.formatColors("&aѕᴇᴀʀᴄʜ"));
      ArrayList var15 = new ArrayList();
      var15.add(Utils.formatColors("&fClick to search an item"));
      var28.setLore(var15);
      var13.setItemMeta(var28);
      var5.setItem(49, var13);
      ItemStack var29 = new ItemStack(Material.PAPER);
      ItemMeta var30 = var29.getItemMeta();
      var30.setDisplayName(Utils.formatColors("&aᴛʀᴀɴѕᴀᴄᴛɪᴏɴѕ"));
      ArrayList var32 = new ArrayList();
      var32.add(Utils.formatColors("&fClick to view transactions"));
      var30.setLore(var32);
      var29.setItemMeta(var30);
      var5.setItem(50, var29);
      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.setMetadata("ah-admin-target", new FixedMetadataValue(var2.getPlugin(), var1));
      var0.openInventory(var5);
      var2.startUpdateTask(var0);
   }

   public static void openItemManagementGUI(Player var0, AuctionItem var1, AuctionController var2) {
      String var3 = Utils.formatColors("&8ɪᴛᴇᴍ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ");
      Inventory var4 = Bukkit.createInventory(new ItemManagementHolder(), 27, var3);
      ItemStack var5 = new ItemStack(Material.OAK_SIGN);
      ItemMeta var6 = var5.getItemMeta();
      FileConfiguration var7 = var2.getConfig();
      var6.setDisplayName(Utils.formatColors("&aᴇᴅɪᴛ ᴘʀɪᴄᴇ"));
      ArrayList var8 = new ArrayList();
      var8.add(Utils.formatColors("&fClick to edit the price"));
      var6.setLore(var8);
      var5.setItemMeta(var6);
      var4.setItem(11, var5);
      ItemStack var9 = var1.getItemStack().clone();
      ItemMeta var10 = var9.getItemMeta();
      ArrayList var11 = new ArrayList();
      var11.add(Utils.formatColors("&fPrice: &a" + Utils.formatNumber(var1.getPrice())));
      var11.add(Utils.formatColors("&fSeller: &a" + var1.getSeller()));
      long var12 = var1.getListedAt() + (long)var1.getDuration() * 1000L - System.currentTimeMillis();
      var11.add(Utils.formatColors("&fTime Left: &a" + FormatUtils.formatTime((int)(var12 / 1000L))));
      var10.setLore(var11);
      var9.setItemMeta(var10);
      var4.setItem(13, var9);
      ItemStack var14 = new ItemStack(Material.BARRIER);
      ItemMeta var15 = var14.getItemMeta();
      var15.setDisplayName(Utils.formatColors("&cᴅᴇʟᴇᴛᴇ ᴛʜɪѕ ɪᴛᴇᴍ"));
      ArrayList var16 = new ArrayList();
      var16.add(Utils.formatColors("&fClick to delete this item"));
      var15.setLore(var16);
      var14.setItemMeta(var15);
      var4.setItem(15, var14);
      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.setMetadata("ah-manage-item", new FixedMetadataValue(var2.getPlugin(), var1.getId().toString()));
      var0.openInventory(var4);
   }

   public static void openAdminTransactionsGUI(Player var0, OfflinePlayer var1, int var2, AuctionController var3) {
      FileConfiguration var4 = var3.getConfig();
      List var5 = var3.getTransactionManager().getPlayerTransactions(var1.getUniqueId());
      String var6 = var0.hasMetadata("tx-filter") ? ((MetadataValue)var0.getMetadata("tx-filter").get(0)).asString().toLowerCase() : "";
      List var7 = (List)var5.stream().filter((var1x) -> {
         if (var6.isEmpty()) {
            return true;
         } else {
            String var2 = Utils.prettifyMaterialName(var1x.getItem().getType()).toLowerCase();
            String var3 = var1x.getBuyer().toLowerCase();
            String var4 = var1x.getSeller().toLowerCase();
            String var5 = String.valueOf(var1x.getPrice());
            String var6x = Utils.formatNumber(var1x.getPrice()).toLowerCase();
            return var2.contains(var6) || var3.contains(var6) || var4.contains(var6) || var5.contains(var6) || var6x.contains(var6);
         }
      }).collect(Collectors.toList());
      int var8 = var4.getInt("transactions-gui.items-per-page", 45);
      int var9 = var7.size();
      int var10 = Math.max(1, (int)Math.ceil((double)var9 / (double)var8));
      if (var2 < 1) {
         var2 = 1;
      }

      if (var2 > var10) {
         var2 = var10;
      }

      String var11 = Utils.formatColors("&8" + Utils.toSmallCaps(var1.getName()) + "'ѕ ᴛʀᴀɴѕᴀᴄᴛɪᴏɴѕ");
      Inventory var12 = Bukkit.createInventory(new AdminTransactionsHolder(), 54, var11);
      int var13 = (var2 - 1) * var8;
      int var14 = Math.min(var13 + var8, var9);
      List var15 = var7.subList(var13, var14);

      for(int var16 = 0; var16 < var15.size(); ++var16) {
         Transaction var17 = (Transaction)var15.get(var16);
         ItemStack var18 = var17.getItem().clone();
         ItemMeta var19 = var18.getItemMeta();
         ArrayList var20 = new ArrayList();
         long var21 = (System.currentTimeMillis() - var17.getTimestamp()) / 1000L;
         String var23 = FormatUtils.formatTime((int)var21);
         if (var17.isSale()) {
            var17.getBuyer();
         } else {
            var17.getSeller();
         }

         String var25 = Utils.prettifyMaterialName(var17.getItem().getType());
         String var26 = Utils.formatNumber(var17.getPrice());
         String var10000 = var17.getBuyer();
         String var27 = Utils.formatColors("&a" + var10000 + "&f bought &a" + var17.getSeller() + "'s&f " + var25 + " for &a$" + var26);
         var20.add(var27);
         var20.add(Utils.formatColors("&a" + var23 + " ago"));
         var19.setLore(var20);
         PersistentDataContainer var28 = var19.getPersistentDataContainer();
         NamespacedKey var29 = new NamespacedKey(var3.getPlugin(), "transaction-timestamp");
         var28.set(var29, PersistentDataType.LONG, var17.getTimestamp());
         var18.setItemMeta(var19);
         var12.setItem(var16, var18);
      }

      if (var2 >= 2) {
         ItemStack var30 = makeItem(var4, "transactions-gui.items.previous-page");
         var12.setItem(45, var30);
      } else {
         ItemStack var31 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
         ItemMeta var33 = var31.getItemMeta();
         var33.setDisplayName(Utils.formatColors("&cʙᴀᴄᴋ ᴛᴏ ᴅᴇᴛᴀɪʟѕ"));
         var31.setItemMeta(var33);
         var12.setItem(45, var31);
      }

      byte var32 = 48;
      ItemStack var34 = new ItemStack(Material.PAPER);
      ItemMeta var35 = var34.getItemMeta();
      var35.setDisplayName(Utils.formatColors(var4.getString("transactions-gui.stats-button.display-name")));
      double var36 = var3.getTransactionManager().getTotalSpent(var1.getUniqueId());
      double var37 = var3.getTransactionManager().getTotalMade(var1.getUniqueId());
      ArrayList var38 = new ArrayList();

      for(String var40 : var4.getStringList("transactions-gui.stats-button.lore")) {
         String var42 = var40.replace("{spent-amount}", Utils.formatNumber(var36)).replace("{made-amount}", Utils.formatNumber(var37));
         var38.add(Utils.formatColors(var42));
      }

      var35.setLore(var38);
      var34.setItemMeta(var35);
      var12.setItem(var32, var34);
      byte var39 = 49;
      ItemStack var41 = makeItem(var4, "transactions-gui.items.refresh");
      var12.setItem(var39, var41);
      byte var43 = 50;
      ItemStack var44 = new ItemStack(Material.OAK_SIGN);
      ItemMeta var45 = var44.getItemMeta();
      if (var4.contains("transactions-gui.items.search")) {
         var45.setDisplayName(Utils.formatColors(var4.getString("transactions-gui.items.search.display-name")));
         var45.setLore(Utils.formatColors(var4.getStringList("transactions-gui.items.search.lore")));
      } else {
         var45.setDisplayName(Utils.formatColors("&aSearch"));
      }

      var44.setItemMeta(var45);
      var12.setItem(var43, var44);
      if (var2 < var10) {
         ItemStack var46 = makeItem(var4, "transactions-gui.items.next-page");
         var12.setItem(53, var46);
      }

      var0.setMetadata("ah-switching", new FixedMetadataValue(var3.getPlugin(), true));
      var0.setMetadata("ah-admin-target", new FixedMetadataValue(var3.getPlugin(), var1.getName()));
      var0.setMetadata("tx-page", new FixedMetadataValue(var3.getPlugin(), var2));
      var0.openInventory(var12);
      var3.startUpdateTask(var0);
   }

   private static void setBottomControls(Inventory var0, FileConfiguration var1, String var2, String var3, AuctionController var4, int var5) {
      if (var5 > 1) {
         ItemStack var6 = makeItem(var1, "main-gui.items.previous-page");
         var0.setItem(var1.getInt("main-gui.items.previous-page.slot"), var6);
      }

      ItemStack var15 = makeItem(var1, "main-gui.items.sort");
      ItemMeta var7 = var15.getItemMeta();
      var7.setLore(Utils.buildSortLore(var2, var1));
      var15.setItemMeta(var7);
      var0.setItem(var1.getInt("main-gui.items.sort.slot"), var15);
      ItemStack var8 = makeItem(var1, "main-gui.items.search");
      var0.setItem(var1.getInt("main-gui.items.search.slot"), var8);
      ItemStack var9 = new ItemStack(Material.HOPPER);
      ItemMeta var10 = var9.getItemMeta();
      var10.setDisplayName(Utils.formatColors("&#34ee80ꜰɪʟᴛᴇʀ"));
      List var11 = buildFilterLore(var3 == null ? "All" : var3, var4);
      var10.setLore(var11);
      var9.setItemMeta(var10);
      var0.setItem(48, var9);
      ItemStack var12 = makeItem(var1, "main-gui.items.refresh");
      var0.setItem(var1.getInt("main-gui.items.refresh.slot"), var12);
      ItemStack var13 = makeItem(var1, "main-gui.items.your-items");
      var0.setItem(var1.getInt("main-gui.items.your-items.slot"), var13);
      ItemStack var14 = makeItem(var1, "main-gui.items.next-page");
      var0.setItem(var1.getInt("main-gui.items.next-page.slot"), var14);
   }

   public static void openAdminDeleteConfirmGUI(Player var0, String var1, AuctionController var2) {
      Inventory var3 = Bukkit.createInventory(new AdminDeleteConfirmHolder(), 27, Utils.formatColors("&8" + Utils.toSmallCaps("CONFIRM DELETION")));
      ItemStack var4 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(Utils.formatColors("&c" + Utils.toSmallCaps("CANCEL")));
      ArrayList var6 = new ArrayList();
      var6.add(Utils.formatColors("&7Click to cancel"));
      var5.setLore(var6);
      var4.setItemMeta(var5);
      var3.setItem(11, var4);
      ItemStack var7 = new ItemStack(Material.PAPER);
      ItemMeta var8 = var7.getItemMeta();
      var8.setDisplayName(Utils.formatColors("&aᴄᴏɴꜰɪʀᴍᴀᴛɪᴏɴ"));
      ArrayList var9 = new ArrayList();
      var9.add(Utils.formatColors("&fAre you sure?"));
      var8.setLore(var9);
      var7.setItemMeta(var8);
      var3.setItem(13, var7);
      ItemStack var10 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var11 = var10.getItemMeta();
      var11.setDisplayName(Utils.formatColors("&a" + Utils.toSmallCaps("CONFIRM")));
      ArrayList var12 = new ArrayList();
      var12.add(Utils.formatColors("&7Click to confirm deletion"));
      var11.setLore(var12);
      var10.setItemMeta(var11);
      var3.setItem(15, var10);
      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.setMetadata("ah-admin-target", new FixedMetadataValue(var2.getPlugin(), var1));
      var0.openInventory(var3);
   }

   public static void openAdminPlayerListGUI(Player var0, int var1, AuctionController var2) {
      String var3 = Utils.formatColors("&8ᴀᴅᴍɪɴ ᴀᴜᴄᴛɪᴏɴ");
      Inventory var4 = Bukkit.createInventory(new AdminPlayerListHolder(), 54, var3);
      FileConfiguration var5 = var2.getConfig();
      var4.setItem(48, makeItem(var5, "main-gui.items.search"));
      var4.setItem(49, makeItem(var5, "transactions-gui.items.refresh"));
      var0.setMetadata("ah-switching", new FixedMetadataValue(var2.getPlugin(), true));
      var0.openInventory(var4);
      var2.getPlugin().getSchedulerAdapter().runTaskAsync(() -> {
         String var6 = var0.hasMetadata("ah-admin-player-filter") ? ((MetadataValue)var0.getMetadata("ah-admin-player-filter").get(0)).asString().toLowerCase() : "";
         List var7 = (List)Arrays.stream(Bukkit.getOfflinePlayers()).filter((var0x) -> var0x.getName() != null).filter((var1x) -> var6.isEmpty() || var1x.getName().toLowerCase().contains(var6)).distinct().sorted((var0x, var1x) -> String.CASE_INSENSITIVE_ORDER.compare(var0x.getName(), var1x.getName())).collect(Collectors.toList());
         byte var8 = 45;
         int var9 = var7.size();
         int var10 = Math.max(1, (int)Math.ceil((double)var9 / (double)var8));
         int var11 = Math.max(1, Math.min(var1, var10));
         int var12 = (var11 - 1) * var8;
         int var13 = Math.min(var12 + var8, var9);
         List var14 = var7.subList(var12, var13);
         var2.getPlugin().getSchedulerAdapter().runTask(() -> {
            if (var0.isOnline()) {
               if (var0.getOpenInventory().getTopInventory() == var4 || var0.getOpenInventory().getTitle().equals(var3)) {
                  String var8 = Utils.formatColors("&8ᴀᴅᴍɪɴ ᴀᴜᴄᴛɪᴏɴ (" + var11 + "/" + var10 + ")");
                  Inventory var9;
                  if (!var8.equals(var3)) {
                     var9 = Bukkit.createInventory(new AdminPlayerListHolder(), 54, var8);
                     var9.setItem(48, makeItem(var5, "main-gui.items.search"));
                     var9.setItem(49, makeItem(var5, "transactions-gui.items.refresh"));
                     var0.openInventory(var9);
                  } else {
                     var9 = var4;
                  }

                  for(int var10x = 0; var10x < var14.size(); ++var10x) {
                     OfflinePlayer var11x = (OfflinePlayer)var14.get(var10x);
                     String var12 = var11x.getName();
                     ItemStack var13 = new ItemStack(Material.PLAYER_HEAD);
                     ItemMeta var14x = var13.getItemMeta();
                     var14x.setDisplayName(Utils.formatColors("&a" + var12));
                     ArrayList var15 = new ArrayList();
                     var15.add(Utils.formatColors("&7Click to see details"));
                     var14x.setLore(var15);
                     PersistentDataContainer var16 = var14x.getPersistentDataContainer();
                     NamespacedKey var17 = new NamespacedKey(var2.getPlugin(), "admin-head-target");
                     var16.set(var17, PersistentDataType.STRING, var12);
                     if (var14x instanceof SkullMeta) {
                        SkullMeta var18 = (SkullMeta)var14x;
                        var18.setOwningPlayer(var11x);
                        if (var12 != null) {
                           var18.setOwner(var12);
                        }
                     }

                     var13.setItemMeta(var14x);
                     var9.setItem(var10x, var13);
                  }

                  if (var11 >= 2) {
                     var9.setItem(45, makeItem(var5, "transactions-gui.items.previous-page"));
                  }

                  if (var11 < var10) {
                     var9.setItem(53, makeItem(var5, "transactions-gui.items.next-page"));
                  }

                  var0.setMetadata("ah-admin-list-page", new FixedMetadataValue(var2.getPlugin(), var11));
               }
            }
         });
      });
   }

   public static class AdminDeleteConfirmHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class AdminPlayerDetailsHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class AdminPlayerListHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class AdminTransactionsHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class BuyConfirmHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class FilterHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class ItemManagementHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class MainHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class SellConfirmHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class TransactionsHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class YourItemsHolder implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }
}
