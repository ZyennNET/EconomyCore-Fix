package com.h2ph.J.B.B;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class B implements CommandExecutor, Listener, TabCompleter {
   private final PrismSurvival E;
   private final String B = ChatColor.translateAlternateColorCodes('&', "&8ʙɪʟʟꜰᴏʀᴅ");
   private final String J = ChatColor.translateAlternateColorCodes('&', "&8ʙɪʟʟꜰᴏʀᴅ &cᴀᴅᴍɪɴ");
   private final File I;
   private FileConfiguration G;
   private Map<Integer, ItemStack> H = new HashMap();
   private ItemStack F;
   private final List<Integer> A = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
   private final int D = 25;
   private final int C = 53;

   public B(PrismSurvival var1) {
      this.E = var1;
      this.I = new File(var1.getDataFolder(), "survival/billford/billford.yml");
      this.A();
   }

   private void A() {
      this.H.clear();
      com.h2ph.T.B var1 = this.E.getDatabaseManager();
      if (var1.F()) {
         String var2 = var1.B("billford");
         if (var2 != null && !var2.isEmpty()) {
            try {
               YamlConfiguration var12 = new YamlConfiguration();
               var12.loadFromString(var2);
               if (var12.contains("inputs")) {
                  for(String var14 : var12.getConfigurationSection("inputs").getKeys(false)) {
                     int var6 = Integer.parseInt(var14);
                     ItemStack var7 = var12.getItemStack("inputs." + var14);
                     if (var7 != null) {
                        this.H.put(var6, var7);
                     }
                  }
               }

               this.F = var12.getItemStack("output");
               if (this.F == null) {
                  this.F = new ItemStack(Material.ARROW, 16);
               }

               return;
            } catch (Exception var9) {
               this.E.getLogger().warning("Failed to parse Billford settings from DB.");
               var9.printStackTrace();
            }
         }
      }

      if (this.I.exists()) {
         this.G = YamlConfiguration.loadConfiguration(this.I);
         if (this.G.contains("inputs")) {
            for(String var3 : this.G.getConfigurationSection("inputs").getKeys(false)) {
               try {
                  int var4 = Integer.parseInt(var3);
                  ItemStack var5 = this.G.getItemStack("inputs." + var3);
                  if (var5 != null) {
                     this.H.put(var4, var5);
                  }
               } catch (NumberFormatException var8) {
               }
            }
         } else {
            this.H.put(10, new ItemStack(Material.IRON_INGOT, 64));
         }

         if (this.G.contains("output")) {
            this.F = this.G.getItemStack("output");
         } else {
            this.F = new ItemStack(Material.ARROW, 16);
         }

         if (var1.F()) {
            this.A(this.H, this.F);
            this.E.getLogger().info("Migrated Billford trade to Database.");
            File var11 = new File(this.I.getParent(), "billford.yml.bak");
            this.I.renameTo(var11);
         }
      } else {
         this.H.put(10, new ItemStack(Material.IRON_INGOT, 64));
         this.F = new ItemStack(Material.ARROW, 16);
      }

   }

   private void A(Map<Integer, ItemStack> var1, ItemStack var2) {
      YamlConfiguration var3 = new YamlConfiguration();

      for(Map.Entry var5 : var1.entrySet()) {
         var3.set("inputs." + String.valueOf(var5.getKey()), var5.getValue());
      }

      var3.set("output", var2);
      String var8 = var3.saveToString();
      com.h2ph.T.B var9 = this.E.getDatabaseManager();
      if (var9.F()) {
         var9.A("billford", var8);
      }

      try {
         if (!this.I.getParentFile().exists()) {
            this.I.getParentFile().mkdirs();
         }

         var3.save(this.I);
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      this.H = new HashMap(var1);
      this.F = var2;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var1 instanceof Player var5) {
         if (var4.length > 0 && var4[0].equalsIgnoreCase("admin")) {
            if (!var5.hasPermission("economysmpcore.admin.billford")) {
               this.C(var5);
               return true;
            } else {
               this.B(var5);
               return true;
            }
         } else {
            this.C(var5);
            return true;
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use Billford.");
         return true;
      }
   }

   private void C(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 54, this.B);
      this.A(var2);

      for(Map.Entry var4 : this.H.entrySet()) {
         var2.setItem((Integer)var4.getKey(), com.h2ph.b.A.A(((ItemStack)var4.getValue()).clone()));
      }

      if (this.F != null) {
         var2.setItem(25, com.h2ph.b.A.A(this.F.clone()));
      }

      ItemStack var11 = new ItemStack(Material.HOPPER);
      ItemMeta var12 = var11.getItemMeta();
      var12.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8ᴛʀᴀᴅᴇ"));
      ArrayList var5 = new ArrayList();
      var5.add(String.valueOf(ChatColor.WHITE) + "Click to confirm the trade");
      var5.add("");
      var5.add(String.valueOf(ChatColor.GRAY) + "(you need the items in your inventory)");
      var12.setLore(var5);
      var11.setItemMeta(var12);
      var2.setItem(23, var11);
      ItemStack var6 = new ItemStack(Material.BOOK);
      ItemMeta var7 = var6.getItemMeta();
      var7.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aʙɪʟʟꜰᴏʀᴅ᾽ѕ ᴛʀᴀᴅᴇ"));
      ArrayList var8 = new ArrayList();

      for(ItemStack var10 : this.H.values()) {
         String var10001 = String.valueOf(ChatColor.GRAY);
         var8.add(var10001 + var10.getAmount() + "x " + String.valueOf(ChatColor.WHITE) + this.A(var10));
      }

      var8.add(String.valueOf(ChatColor.GRAY) + "for");
      if (this.F != null) {
         String var13 = String.valueOf(ChatColor.GRAY);
         var8.add(var13 + this.F.getAmount() + "x " + String.valueOf(ChatColor.GREEN) + this.A(this.F));
      }

      var7.setLore(var8);
      var6.setItemMeta(var7);
      var2.setItem(49, var6);
      var1.openInventory(var2);
   }

   private void B(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 54, this.J);
      this.A(var2);

      for(int var4 : this.A) {
         var2.setItem(var4, (ItemStack)null);
      }

      var2.setItem(25, (ItemStack)null);

      for(Map.Entry var7 : this.H.entrySet()) {
         var2.setItem((Integer)var7.getKey(), ((ItemStack)var7.getValue()).clone());
      }

      if (this.F != null) {
         var2.setItem(25, this.F.clone());
      }

      ItemStack var6 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var8 = var6.getItemMeta();
      var8.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aѕᴀᴠᴇ ᴛʀᴀᴅᴇ"));
      var8.setLore(List.of(ChatColor.translateAlternateColorCodes('&', "&fClick to save the billford")));
      var6.setItemMeta(var8);
      var2.setItem(53, var6);
      var1.openInventory(var2);
   }

   private void A(Inventory var1) {
      ItemStack var2 = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
      ItemMeta var3 = var2.getItemMeta();
      var3.setDisplayName(" ");
      var2.setItemMeta(var3);

      for(int var4 = 0; var4 < 54; ++var4) {
         var1.setItem(var4, var2);
      }

      for(int var5 : this.A) {
         var1.setItem(var5, (ItemStack)null);
      }

      var1.setItem(25, (ItemStack)null);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.equals(this.B) || var2.equals(this.J)) {
         Player var3 = (Player)var1.getWhoClicked();
         ItemStack var4 = var1.getCurrentItem();
         int var5 = var1.getSlot();
         if (var2.equals(this.B)) {
            var1.setCancelled(true);
            if (var4 != null && var4.getType() != Material.BLACK_STAINED_GLASS_PANE && var4.getType() != Material.AIR) {
               this.A(var3, Sound.BLOCK_TRIPWIRE_CLICK_ON);
            }

            if (var5 == 23 && var4 != null && var4.getType() == Material.HOPPER) {
               this.A(var3);
            }
         } else if (var2.equals(this.J)) {
            boolean var6 = var1.getClickedInventory() == var1.getView().getTopInventory();
            if (var6) {
               if (var5 == 53) {
                  var1.setCancelled(true);
                  this.A(var3, var1.getInventory());
                  return;
               }

               if (this.A.contains(var5) || var5 == 25) {
                  return;
               }

               var1.setCancelled(true);
            }
         }

      }
   }

   private void A(Player var1, Inventory var2) {
      HashMap var3 = new HashMap();
      ItemStack var4 = var2.getItem(25);
      boolean var5 = false;

      for(int var7 : this.A) {
         ItemStack var8 = var2.getItem(var7);
         if (var8 != null && var8.getType() != Material.AIR) {
            var3.put(var7, var8);
            var5 = true;
         }
      }

      if (var5 && var4 != null && var4.getType() != Material.AIR) {
         this.A(var3, (ItemStack)var4);
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Billford Trade Saved!");
         this.A(var1, Sound.ENTITY_PLAYER_LEVELUP);
         var1.closeInventory();
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Missing items! Place items in the input area and an output in Slot 25.");
         this.A(var1, Sound.ENTITY_VILLAGER_NO);
      }
   }

   private void A(Player var1) {
      for(ItemStack var3 : this.H.values()) {
         if (!var1.getInventory().containsAtLeast(var3, var3.getAmount())) {
            String var4 = String.valueOf(ChatColor.RED) + "You do not have all the required contents.";
            var1.sendMessage(var4);
            var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var4));
            this.A(var1, Sound.ENTITY_VILLAGER_NO);
            return;
         }
      }

      boolean var7 = false;
      if (var1.getInventory().firstEmpty() != -1) {
         var7 = true;
      } else {
         for(ItemStack var6 : var1.getInventory().getContents()) {
            if (var6 != null && var6.isSimilar(this.F) && var6.getAmount() + this.F.getAmount() <= var6.getMaxStackSize()) {
               var7 = true;
               break;
            }
         }
      }

      if (!var7) {
         String var11 = String.valueOf(ChatColor.RED) + "Your inventory is full!";
         var1.sendMessage(var11);
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var11));
         this.A(var1, Sound.ENTITY_VILLAGER_NO);
      } else {
         for(ItemStack var13 : this.H.values()) {
            var1.getInventory().removeItem(new ItemStack[]{var13});
         }

         ItemStack var10 = this.F.clone();
         var1.getInventory().addItem(new ItemStack[]{var10});
         int var10001 = this.F.getAmount();
         String var14 = ChatColor.translateAlternateColorCodes('&', "&7Traded for &f" + var10001 + "x &f" + this.A(this.F));
         var1.sendMessage(var14);
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var14));
      }
   }

   private void A(Player var1, Sound var2) {
      try {
         var1.playSound(var1.getLocation(), var2, 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }

   private String A(ItemStack var1) {
      if (var1.hasItemMeta() && var1.getItemMeta().hasDisplayName()) {
         return var1.getItemMeta().getDisplayName();
      } else {
         String var2 = var1.getType().name().toLowerCase().replace("_", " ");
         StringBuilder var3 = new StringBuilder();

         for(String var7 : var2.split(" ")) {
            var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1)).append(" ");
         }

         return var3.toString().trim();
      }
   }

   public List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length == 1 && var1.hasPermission("economysmpcore.admin.billford")) {
         ArrayList var5 = new ArrayList();
         if ("admin".startsWith(var4[0].toLowerCase())) {
            var5.add("admin");
         }

         return var5;
      } else {
         return new ArrayList();
      }
   }
}
