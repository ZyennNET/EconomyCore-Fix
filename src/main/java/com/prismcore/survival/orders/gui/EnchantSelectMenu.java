package com.prismcore.survival.orders.gui;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.store.EnchantmentsManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class EnchantSelectMenu implements InventoryHolder, MenuOwner {
   private final PrismOrders pl;
   private final Player p;
   private final ItemStack base;
   private Inventory inv;
   private final Map<Enchantment, Integer> selected = new LinkedHashMap();
   private List<EnchantmentsManager.EnchantOption> options = new ArrayList();
   private List<Integer> gridSlots = new ArrayList();
   private int page = 0;

   public EnchantSelectMenu(PrismOrders var1, Player var2, ItemStack var3) {
      this.pl = var1;
      this.p = var2;
      this.base = var3.clone();
   }

   public Inventory getInventory() {
      return this.inv;
   }

   private void buildGridSlots() {
      byte var1 = 6;
      int var2 = var1 * 9;
      int var3 = (var1 - 1) * 9;
      byte var4 = 4;
      byte var5 = 45;
      byte var6 = 53;
      byte var7 = 52;
      byte var8 = 46;
      HashSet var9 = new HashSet(List.of(var4, var5, var6, var7, var8));
      this.gridSlots.clear();

      for(int var10 = 0; var10 < var2; ++var10) {
         if (var10 < var3 && !var9.contains(var10)) {
            this.gridSlots.add(var10);
         }
      }

   }

   private boolean canAnyEnchant(ItemStack var1) {
      for(Enchantment var5 : Enchantment.values()) {
         if (var5 != null && var5.canEnchantItem(var1)) {
            return true;
         }
      }

      return false;
   }

   public void open() {
      if (this.pl.enchants().hasOptionsFor(this.base.getType()) && this.canAnyEnchant(this.base)) {
         String var1 = Utils.formatColors("&8ᴏʀᴅᴇʀѕ -> Add Enchantments");
         this.inv = Bukkit.createInventory(this, 54, var1);
         this.buildGridSlots();
         this.options = new ArrayList(this.pl.enchants().optionsFor(this.base.getType()));
         this.options.removeIf((var1x) -> var1x.ench == null || !var1x.ench.canEnchantItem(this.base));
         this.render();
         this.p.openInventory(this.inv);
      } else {
         this.p.setMetadata("prism.orders.tmpChosenStack", new FixedMetadataValue(this.pl.getPlugin(), this.base.clone()));
         this.pl.chat().session(this.p.getUniqueId()).chosenItem = this.base.getType().name();
         (new NewOrderMenu(this.pl, this.p)).open();
      }
   }

   private void render() {
      byte var1 = 4;
      byte var2 = 45;
      byte var3 = 53;
      byte var4 = 52;
      byte var5 = 46;
      this.inv.clear();
      ItemStack var6 = this.base.clone();
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

         for(Map.Entry var9 : this.selected.entrySet()) {
            var7.addEnchant((Enchantment)var9.getKey(), (Integer)var9.getValue(), true);
         }

         var6.setItemMeta(var7);
      }

      this.inv.setItem(var1, var6);
      int var18 = this.gridSlots.size();
      int var19 = Math.max(0, (this.options.size() - 1) / Math.max(1, var18));
      if (this.page > var19) {
         this.page = var19;
      }

      if (this.page < 0) {
         this.page = 0;
      }

      int var10 = this.page * var18;

      for(int var11 = 0; var11 < var18; ++var11) {
         int var12 = var10 + var11;
         if (var12 >= this.options.size()) {
            break;
         }

         EnchantmentsManager.EnchantOption var13 = (EnchantmentsManager.EnchantOption)this.options.get(var12);
         boolean var14 = this.selected.containsKey(var13.ench) && Objects.equals(this.selected.get(var13.ench), var13.level);
         boolean var15 = !var14 && this.conflictsWithCurrent(var13.ench);
         String var16 = "&fClick to select";
         if (var14) {
            var16 = "&aѕᴇʟᴇᴄᴛᴇᴅ";
         } else if (var15) {
            var16 = "&cConflicts with selection";
         }

         ArrayList var17 = new ArrayList();
         if (var14) {
            var17.add("&aѕᴇʟᴇᴄᴛᴇᴅ");
            var17.add("&cClick to remove");
         } else {
            var17.add(var16);
         }

         this.inv.setItem((Integer)this.gridSlots.get(var11), this.makeBookOption(var13, var17));
      }

      if (this.page > 0) {
         this.inv.setItem(var2, this.makeButton(Material.ARROW, "&fPrevious Page", (List)null));
      }

      if (this.page < var19) {
         this.inv.setItem(var3, this.makeButton(Material.ARROW, "&fNext Page", (List)null));
      }

      this.inv.setItem(var4, this.makeButton(Material.LIME_STAINED_GLASS_PANE, "&aᴄᴏɴꜰɪʀᴍ", List.of("&fClick to confirm enchants")));
      this.inv.setItem(var5, this.makeButton(Material.RED_STAINED_GLASS_PANE, "&4ᴄᴀɴᴄᴇʟ", List.of("&fClick to cancel")));
      ItemStack var20 = this.makeButton(Material.GRAY_STAINED_GLASS_PANE, "&7 ", (List)null);

      for(int var21 = 45; var21 < 54; ++var21) {
         if (this.inv.getItem(var21) == null) {
            this.inv.setItem(var21, var20);
         }
      }

   }

   private boolean conflictsWithCurrent(Enchantment var1) {
      for(Enchantment var3 : this.selected.keySet()) {
         if (var1.conflictsWith(var3) || var3.conflictsWith(var1)) {
            return true;
         }
      }

      return false;
   }

   public void onClick(InventoryClickEvent var1) {
      if (var1.getClickedInventory() != null) {
         if (var1.getClickedInventory().getHolder() != this) {
            var1.setCancelled(true);
         } else {
            var1.setCancelled(true);
            int var2 = var1.getSlot();
            byte var3 = 45;
            byte var4 = 53;
            byte var5 = 52;
            byte var6 = 46;
            if (var2 == var3) {
               if (this.page > 0) {
                  --this.page;
                  this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.1F);
                  this.render();
               }

            } else if (var2 == var4) {
               int var12 = this.gridSlots.size();
               int var14 = Math.max(0, (this.options.size() - 1) / Math.max(1, var12));
               if (this.page < var14) {
                  ++this.page;
                  this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.1F);
                  this.render();
               }

            } else if (var2 == var6) {
               this.p.playSound(this.p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.8F);
               (new SelectItemMenu(this.pl, this.p)).open();
            } else if (var2 != var5) {
               int var11 = this.gridSlots.indexOf(var2);
               if (var11 >= 0) {
                  int var13 = this.gridSlots.size();
                  int var15 = this.page * var13 + var11;
                  if (var15 < this.options.size()) {
                     this.toggle((EnchantmentsManager.EnchantOption)this.options.get(var15));
                  }
               }

            } else {
               ItemStack var7 = this.base.clone();
               ItemMeta var8 = var7.getItemMeta();
               if (var8 != null) {
                  var8.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});

                  for(Map.Entry var10 : this.selected.entrySet()) {
                     var8.addEnchant((Enchantment)var10.getKey(), (Integer)var10.getValue(), true);
                  }

                  var7.setItemMeta(var8);
               }

               if (this.selected.isEmpty()) {
                  this.p.setMetadata("prism.orders.skipEnchantOnce", new FixedMetadataValue(this.pl.getPlugin(), true));
               }

               this.pl.chat().session(this.p.getUniqueId()).chosenItem = this.base.getType().name();
               this.p.setMetadata("prism.orders.tmpChosenStack", new FixedMetadataValue(this.pl.getPlugin(), var7));
               this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               (new NewOrderMenu(this.pl, this.p)).open();
            }
         }
      }
   }

   private void toggle(EnchantmentsManager.EnchantOption var1) {
      boolean var2 = this.selected.containsKey(var1.ench) && Objects.equals(this.selected.get(var1.ench), var1.level);
      if (var2) {
         this.selected.remove(var1.ench);
         this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
      } else if (!this.conflictsWithCurrent(var1.ench)) {
         this.selected.put(var1.ench, var1.level);
         this.p.playSound(this.p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
      } else {
         this.p.playSound(this.p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.8F);
      }

      this.render();
   }

   public void onClose(InventoryCloseEvent var1) {
   }

   private ItemStack makeButton(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         if (var2 != null) {
            var5.setDisplayName(Utils.formatColors(var2));
         }

         if (var3 != null && !var3.isEmpty()) {
            var5.setLore(Utils.formatColors(var3));
         }

         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack makeBookOption(EnchantmentsManager.EnchantOption var1, List<String> var2) {
      ItemStack var3 = new ItemStack(Material.ENCHANTED_BOOK);
      EnchantmentStorageMeta var4 = (EnchantmentStorageMeta)var3.getItemMeta();
      if (var4 != null) {
         var4.addStoredEnchant(var1.ench, var1.level, true);
         if (var2 != null && !var2.isEmpty()) {
            var4.setLore(Utils.formatColors(var2));
         }

         var3.setItemMeta(var4);
      }

      return var3;
   }
}
