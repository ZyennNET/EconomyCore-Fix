package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class D {
   private final PrismSurvival C;
   private static final Map<Character, Character> D = new HashMap();
   private final NamespacedKey A;
   private final NamespacedKey B;

   public D(PrismSurvival var1) {
      this.C = var1;
      this.A = new NamespacedKey(var1, "duel_region_name");
      this.B = new NamespacedKey(var1, "duel_player_uuid");
   }

   public NamespacedKey B() {
      return this.B;
   }

   public static String A(String var0) {
      StringBuilder var1 = new StringBuilder();

      for(char var5 : var0.toCharArray()) {
         var1.append(D.getOrDefault(var5, var5));
      }

      return var1.toString();
   }

   public void A(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', "&8ᴅᴜᴇʟ ѕᴇᴛᴛɪɴɢѕ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ"));
      ItemStack var3 = this.A(Material.GRASS_BLOCK, "&aʀᴇɢɪᴏɴѕ", (String)null, "&fClick to view all regions");
      var2.setItem(11, var3);
      ItemStack var4 = this.A(Material.WRITABLE_BOOK, "&aѕᴇᴛᴛɪɴɢѕ", (String)null, "&fClick to open settings");
      var2.setItem(13, var4);
      ItemStack var5 = this.A(Material.NAME_TAG, "&aᴍᴀɴᴀɢᴇ ᴘʟᴀʏᴇʀѕ", (String)null, "&fClick to manage players");
      var2.setItem(15, var5);
      var1.openInventory(var2);
   }

   public void B(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', "&8ʀᴇɢɪᴏɴѕ"));
      File var3 = new File(this.C.getDataFolder(), "survival/regions/duels");
      if (!var3.exists()) {
         var3.mkdirs();
      }

      File[] var4 = var3.listFiles((var0, var1x) -> var1x.endsWith(".yml"));
      if (var4 != null) {
         int var5 = 0;

         for(File var9 : var4) {
            if (var5 > 44) {
               break;
            }

            String var10 = var9.getName().replace(".yml", "");
            String var11 = A(var10);
            YamlConfiguration var12 = YamlConfiguration.loadConfiguration(var9);
            String var13 = var12.getString("created-by", "Unknown");
            ItemStack var14 = this.A(Material.GRASS_BLOCK, "&a" + var11, var10, "&fCreated by &f" + var13, "&cClick to view settings");
            var2.setItem(var5, var14);
            ++var5;
         }
      }

      var1.openInventory(var2);
   }

   public void A(Player var1, String var2) {
      String var3 = ChatColor.translateAlternateColorCodes('&', "&8" + A(var2) + " ѕᴇᴛᴛɪɴɢѕ");
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, 27, var3);
      File var5 = new File(this.C.getDataFolder(), "survival/regions/duels/" + var2 + ".yml");
      YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
      int var7 = var6.getInt("looting-minutes", 5);
      ArrayList var8 = new ArrayList();
      if (var6.contains("spawn1.world")) {
         int var9 = var6.getInt("spawn1.x");
         int var10 = var6.getInt("spawn1.y");
         int var11 = var6.getInt("spawn1.z");
         var8.add("&7" + var9 + " " + var10 + " " + var11);
         var8.add("&cClick to unset");
      } else {
         var8.add("&fClick to set pos 1");
      }

      ItemStack var15 = this.A(Material.ARMOR_STAND, "&aᴘʟᴀʏᴇʀ 1", var2, (String[])var8.toArray(new String[0]));
      var4.setItem(11, var15);
      ItemStack var16 = this.A(Material.CLOCK, "&aᴍɪɴᴜᴛᴇѕ ꜰᴏʀ ʟᴏᴏᴛɪɴɢ", var2, "&7" + var7 + " minutes", "&fLeft click to extend / Right click to deduct");
      var4.setItem(13, var16);
      ArrayList var17 = new ArrayList();
      if (var6.contains("spawn2.world")) {
         int var12 = var6.getInt("spawn2.x");
         int var13 = var6.getInt("spawn2.y");
         int var14 = var6.getInt("spawn2.z");
         var17.add("&7" + var12 + " " + var13 + " " + var14);
         var17.add("&cClick to unset");
      } else {
         var17.add("&fClick to set pos 2");
      }

      ItemStack var18 = this.A(Material.ARMOR_STAND, "&aᴘʟᴀʏᴇʀ 2", var2, (String[])var17.toArray(new String[0]));
      var4.setItem(15, var18);
      ItemStack var19 = this.A(Material.RED_STAINED_GLASS_PANE, "&4ᴅᴇʟᴇᴛᴇ ʀᴇɢɪᴏɴ", var2, "&fClick to delete this region");
      var4.setItem(26, var19);
      var1.openInventory(var4);
   }

   public NamespacedKey A() {
      return this.A;
   }

   public void C(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.translateAlternateColorCodes('&', "&8ɢᴇɴᴇʀᴀʟ ѕᴇᴛᴛɪɴɢѕ"));
      File var3 = new File(this.C.getDataFolder(), "survival/duels/config.yml");
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      int var5 = var4.getInt("pending-timeout", 60);
      int var6 = var4.getInt("request-cooldown", 10);
      ItemStack var7 = this.A(Material.CLOCK, "&aᴘᴇɴᴅɪɴɢ ᴛɪᴍᴇᴏᴜᴛ", (String)null, "&7" + var5 + " seconds", "&fLeft click to add 5s / Right click to remove 5s");
      var2.setItem(11, var7);
      ItemStack var8 = this.A(Material.CLOCK, "&aʀᴇǫᴜᴇѕᴛ ᴄᴏᴏʟᴅᴏᴡɴ", (String)null, "&7" + var6 + " seconds", "&fLeft click to add 1s / Right click to remove 1s");
      var2.setItem(15, var8);
      ItemStack var9 = this.A(Material.ARROW, "&cʙᴀᴄᴋ", (String)null, "&fReturn to duel settings");
      var2.setItem(22, var9);
      var1.openInventory(var2);
   }

   public void A(Player var1, List<Player> var2, B var3) {
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', "&8ᴍᴀɴᴀɢᴇ ᴘʟᴀʏᴇʀѕ"));
      if (var2.isEmpty()) {
         ItemStack var5 = this.A(Material.GRAY_STAINED_GLASS_PANE, "&7ɴᴏ ᴀᴄᴛɪᴠᴇ ᴅᴜᴇʟѕ", (String)null, "&7No players are currently dueling.");
         var4.setItem(22, var5);
      } else {
         int var13 = 0;

         for(Player var7 : var2) {
            if (var13 > 44) {
               break;
            }

            Player var8 = var3.E(var7);
            String var9 = var8 != null ? var8.getName() : "Unknown";
            ItemStack var10 = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta var11 = (SkullMeta)var10.getItemMeta();
            if (var11 != null) {
               var11.setOwningPlayer(var7);
               var11.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + var7.getName()));
               ArrayList var12 = new ArrayList();
               var12.add(ChatColor.translateAlternateColorCodes('&', "&7Opponent: &f" + var9));
               var12.add(ChatColor.translateAlternateColorCodes('&', "&cClick to force-forfeit this player"));
               var11.setLore(var12);
               var11.getPersistentDataContainer().set(this.B, PersistentDataType.STRING, var7.getUniqueId().toString());
               var10.setItemMeta(var11);
            }

            var4.setItem(var13, var10);
            ++var13;
         }
      }

      ItemStack var14 = this.A(Material.ARROW, "&cʙᴀᴄᴋ", (String)null, "&fReturn to duel settings");
      var4.setItem(49, var14);
      var1.openInventory(var4);
   }

   public void A(Player var1, C var2, int var3) {
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.translateAlternateColorCodes('&', "&8ᴅᴜᴇʟ ǫᴜᴇᴜᴇ & ᴄᴏɴꜰɪʀᴍ"));
      UUID var5 = var1.getUniqueId();
      int var6 = var2.G(var5);
      int var7 = var2.F(var5);
      int var8 = var2.A(var5);
      ItemStack var9 = this.A(Material.RED_STAINED_GLASS_PANE, "&4ᴄᴀɴᴄᴇʟ", (String)null, "&fClick to cancel");
      var4.setItem(10, var9);
      ItemStack var10 = this.A(Material.CLOCK, "&aᴡᴀɪᴛ ᴛɪᴍᴇ", (String)null, "&7Estimated Wait: Calculating...", "&7Currently queued: " + var3);
      var4.setItem(12, var10);
      ItemStack var11 = this.A(Material.GRAY_DYE, "&aѕᴛᴀᴛɪѕᴛɪᴄѕ", (String)null, "&7Wins: " + var6, "&7Losses: " + var7, "&7Streak: " + var8);
      var4.setItem(13, var11);
      ItemStack var12 = this.A(Material.FEATHER, "&aʀᴇɢɪᴏɴ", (String)null, "&7Europe (&5--&7)");
      var4.setItem(14, var12);
      ItemStack var13 = this.A(Material.GREEN_STAINED_GLASS_PANE, "&aᴄᴏɴꜰɪʀᴍ", (String)null, "&fClick to start searching for match");
      var4.setItem(16, var13);
      var1.openInventory(var4);
   }

   private ItemStack A(Material var1, String var2, String var3, String... var4) {
      ItemStack var5 = new ItemStack(var1);
      ItemMeta var6 = var5.getItemMeta();
      if (var6 != null) {
         var6.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
         ArrayList var7 = new ArrayList();

         for(String var11 : var4) {
            var7.add(ChatColor.translateAlternateColorCodes('&', var11));
         }

         var6.setLore(var7);
         if (var3 != null) {
            var6.getPersistentDataContainer().set(this.A, PersistentDataType.STRING, var3);
         }

         var5.setItemMeta(var6);
      }

      return var5;
   }

   static {
      char[] var0 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
      char[] var1 = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ".toCharArray();

      for(int var2 = 0; var2 < var0.length && var2 < var1.length; ++var2) {
         D.put(var0[var2], var1[var2]);
      }

   }
}
