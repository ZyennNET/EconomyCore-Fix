package com.h2ph.c;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class E {
   private static final Map<String, FileConfiguration> B = new HashMap();
   private static final String[] A = new String[]{"main.yml", "money.yml", "shards.yml", "shop_spent.yml", "playtime.yml", "deaths.yml", "blocks-placed.yml", "blocks-broken.yml", "mobs-killed.yml", "player-kills.yml"};

   private E() {
   }

   public static void A(F var0) {
      B.clear();
      File var1 = new File(var0.H().getDataFolder(), "leaderboards/gui");
      if (!var1.exists()) {
         var1.mkdirs();
      }

      for(String var5 : A) {
         File var6 = new File(var1, var5);
         if (!var6.exists()) {
            var0.H().saveResource("leaderboards/gui/" + var5, false);
         }

         B.put(var5.replace(".yml", ""), YamlConfiguration.loadConfiguration(var6));
      }

   }

   public static void A(Player var0) {
      FileConfiguration var1 = (FileConfiguration)B.get("main");
      if (var1 != null) {
         String var2 = var1.getString("title", "&8ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅѕ");
         int var3 = Math.max(1, Math.min(6, var1.getInt("rows", 3)));
         Inventory var4 = Bukkit.createInventory(var0, var3 * 9, com.h2ph.c.A.A(var2));
         ConfigurationSection var5 = var1.getConfigurationSection("items");
         if (var5 != null) {
            for(String var7 : var5.getKeys(false)) {
               ConfigurationSection var8 = var5.getConfigurationSection(var7);
               if (var8 != null && var8.getBoolean("enabled", true)) {
                  int var9 = var8.getInt("slot", -1);
                  if (var9 >= 0 && var9 < var4.getSize()) {
                     var4.setItem(var9, A(var8, (String)null, (String)null, (String)null, (String)null));
                  }
               }
            }
         }

         I.C.put(var0.getUniqueId(), I._A.D);
         I.B.remove(var0.getUniqueId());
         I.A.remove(var0.getUniqueId());
         var0.openInventory(var4);
      }
   }

   public static void A(F var0, Player var1, H._A var2, int var3) {
      String var4 = H.C(var2);
      Object var5 = (FileConfiguration)B.get(var4);
      if (var5 == null) {
         var5 = new YamlConfiguration();
         ((FileConfiguration)var5).set("title", "&8" + var4);
         ((FileConfiguration)var5).set("rows", 6);
         B.put(var4, var5);
      }

      List var6 = var0.G().B(var2);
      byte var7 = 45;
      int var8 = Math.max(1, (int)Math.ceil((double)var6.size() / (double)var7));
      if (var3 < 0) {
         var3 = 0;
      }

      if (var3 >= var8) {
         var3 = var8 - 1;
      }

      String var9 = ((FileConfiguration)var5).getString("title", "&8Leaderboard").replace("%page%", String.valueOf(var3 + 1)).replace("%total%", String.valueOf(var8));
      int var10 = Math.max(1, Math.min(6, ((FileConfiguration)var5).getInt("rows", 6)));
      Inventory var11 = Bukkit.createInventory(var1, var10 * 9, com.h2ph.c.A.A(var9));
      ConfigurationSection var12 = ((FileConfiguration)var5).getConfigurationSection("items");
      ConfigurationSection var13 = var12 != null ? var12.getConfigurationSection("player") : null;
      int var14 = var3 * var7;
      int var15 = Math.min(var14 + var7, var6.size());

      for(int var16 = var14; var16 < var15; ++var16) {
         H._B var17 = (H._B)var6.get(var16);
         var11.setItem(var16 - var14, A(var13, var17, var16 + 1, var2));
      }

      if (var12 != null) {
         A(var11, var12.getConfigurationSection("back"));
         A(var11, var12.getConfigurationSection("next"));
         A(var11, var12.getConfigurationSection("refresh"));
         A(var11, var12.getConfigurationSection("search"));
         ConfigurationSection var18 = var12.getConfigurationSection("self");
         if (var18 != null) {
            int var19 = var18.getInt("slot", 49);
            if (var19 >= 0 && var19 < var11.getSize()) {
               A(var0, var1, var2, var18, var11, var19);
            }
         }
      }

      I.C.put(var1.getUniqueId(), I._A.C);
      I.B.put(var1.getUniqueId(), var2);
      I.A.put(var1.getUniqueId(), var3);
      var1.openInventory(var11);
   }

   private static void A(F var0, Player var1, H._A var2, ConfigurationSection var3, Inventory var4, int var5) {
      H var6 = var0.G();
      int var7 = var6.B(var2, var1.getUniqueId());
      double var8 = var6.C(var2, var1.getUniqueId());
      H._B var10 = new H._B(var1.getUniqueId(), var1.getName(), var8);
      var4.setItem(var5, A(var3, var10, Math.max(var7, 0), var2));
   }

   private static void A(Inventory var0, ConfigurationSection var1) {
      if (var1 != null) {
         int var2 = var1.getInt("slot", -1);
         if (var2 >= 0 && var2 < var0.getSize()) {
            var0.setItem(var2, A(var1, (String)null, (String)null, (String)null, (String)null));
         }
      }
   }

   private static ItemStack A(ConfigurationSection var0, H._B var1, int var2, H._A var3) {
      ItemStack var4 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var5 = (SkullMeta)var4.getItemMeta();
      if (var5 != null) {
         try {
            var5.setOwningPlayer(Bukkit.getOfflinePlayer(var1.C()));
         } catch (Exception var11) {
         }

         String var6 = var0 != null ? var0.getString("display-name", "&#04fc84%name%") : "&#04fc84%name%";
         var6 = A(var6, var1, var2, var3);
         var5.displayName(com.h2ph.c.A.A(var6));
         List var7 = var0 != null ? var0.getStringList("lore") : List.of("&f%value% &#04fc84(%rank%)");
         ArrayList var8 = new ArrayList();

         for(String var10 : var7) {
            var8.add(com.h2ph.c.A.A(A(var10, var1, var2, var3)));
         }

         var5.lore(var8);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private static String A(String var0, H._B var1, int var2, H._A var3) {
      return var0.replace("%name%", var1.B()).replace("%player%", var1.B()).replace("%rank%", var2 > 0 ? "#" + var2 : "-").replace("%value%", H.A(var3, var1.A()));
   }

   private static ItemStack A(ConfigurationSection var0, String var1, String var2, String var3, String var4) {
      Material var5 = Material.STONE;

      try {
         var5 = Material.valueOf(var0.getString("material", "STONE").toUpperCase());
      } catch (Exception var13) {
      }

      ItemStack var6 = new ItemStack(var5);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         String var8 = var0.getString("display-name", var5.name());
         var7.displayName(com.h2ph.c.A.A(var8));
         List var9 = var0.getStringList("lore");
         if (!var9.isEmpty()) {
            ArrayList var10 = new ArrayList();

            for(String var12 : var9) {
               var10.add(com.h2ph.c.A.A(var12));
            }

            var7.lore(var10);
         }

         var6.setItemMeta(var7);
      }

      return var6;
   }
}
