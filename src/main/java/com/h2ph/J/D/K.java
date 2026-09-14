package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class K implements TabExecutor {
   private static final String E = "&8ʀᴀɴᴅᴏᴍ ᴛᴇʟᴇᴘᴏʀᴛ";
   public static final String GUI_TITLE = ChatColor.translateAlternateColorCodes('&', "&8ʀᴀɴᴅᴏᴍ ᴛᴇʟᴇᴘᴏʀᴛ");
   private static final String C = "MAIN_OVERWORLD";
   private static final String D = "MAIN_NETHER";
   private static final String B = "MAIN_END";
   private static final String F = "SUB_BACK";
   private static final String A = "SUB_REGION:";

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else {
         if (var4.length > 0) {
            teleportOrConnect(var5, var4[0]);
         } else {
            openRTPGUI(var5);
         }

         return true;
      }
   }

   public static void teleportOrConnect(Player var0, String var1) {
      teleportOrConnect(var0, var1, "overworld");
   }

   public static void teleportOrConnect(Player var0, String var1, String var2) {
      PrismSurvival var3 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      String var4 = A(var3, var1);
      FileConfiguration var5 = var3.getRTPRegionConfig(var4);
      String var6 = var3.getSurvivalConfig().getString("current-region");
      if (var6 != null && var6.equalsIgnoreCase(var4)) {
         com.h2ph.E.A.C(var0, var4, var2);
      } else {
         if (var5 != null) {
            String var7 = var5.getString("server");
            String var8 = A(var3);
            if (var7 != null && var8 != null && !var7.equalsIgnoreCase(var8)) {
               com.h2ph.E.A.A(var0, var7, var4, var2);
            } else {
               com.h2ph.E.A.C(var0, var4, var2);
            }
         } else {
            openRTPGUI(var0);
         }

      }
   }

   private static String A(PrismSurvival var0, String var1) {
      File var2 = new File(var0.getDataFolder(), "rtp");
      if (var2.exists() && var2.isDirectory()) {
         File[] var3 = var2.listFiles();
         if (var3 != null) {
            for(File var7 : var3) {
               if (var7.isDirectory()) {
                  if (var7.getName().equalsIgnoreCase(var1)) {
                     return var7.getName();
                  }

                  File var8 = new File(var7, "config.yml");
                  if (var8.exists()) {
                     YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var8);
                     String var10 = ((FileConfiguration)var9).getString("ARGUMENT");
                     if (var10 != null && var10.equalsIgnoreCase(var1)) {
                        return var7.getName();
                     }
                  }
               }
            }
         }
      }

      return var1.toLowerCase();
   }

   public List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length != 1) {
         return Collections.emptyList();
      } else {
         ArrayList var5 = new ArrayList();
         PrismSurvival var6 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
         File var7 = new File(var6.getDataFolder(), "rtp");
         if (var7.exists() && var7.isDirectory()) {
            File[] var8 = var7.listFiles();
            if (var8 != null) {
               for(File var12 : var8) {
                  if (var12.isDirectory()) {
                     File var13 = new File(var12, "config.yml");
                     if (var13.exists()) {
                        String var14 = var12.getName();
                        YamlConfiguration var15 = YamlConfiguration.loadConfiguration(var13);
                        String var16 = ((FileConfiguration)var15).getString("ARGUMENT");
                        if (var16 != null && !var16.isEmpty()) {
                           var5.add(var16);
                        } else if (var14.equalsIgnoreCase("europe")) {
                           var5.add("Europe");
                        } else if (var14.equalsIgnoreCase("na")) {
                           var5.add("NA");
                        } else if (var14.equalsIgnoreCase("asia")) {
                           var5.add("Asia");
                        } else if (var14.equalsIgnoreCase("west")) {
                           var5.add("West");
                        } else {
                           String var10001 = var14.substring(0, 1).toUpperCase();
                           var5.add(var10001 + var14.substring(1));
                        }
                     }
                  }
               }
            }
         }

         String var17 = var4[0].toLowerCase();
         ArrayList var18 = new ArrayList();

         for(String var20 : var5) {
            if (var20.toLowerCase().startsWith(var17)) {
               var18.add(var20);
            }
         }

         return var18;
      }
   }

   private static FileConfiguration B() {
      try {
         PrismSurvival var0 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
         return var0.getGlobalRTPConfig();
      } catch (Throwable var1) {
         return null;
      }
   }

   public static String getGuiTitle() {
      FileConfiguration var0 = B();
      String var1 = var0 != null ? var0.getString("GUI.TITLE") : null;
      return ChatColor.translateAlternateColorCodes('&', var1 != null ? var1 : "&8ʀᴀɴᴅᴏᴍ ᴛᴇʟᴇᴘᴏʀᴛ");
   }

   public static boolean isOverworldMultiMode() {
      FileConfiguration var0 = B();
      String var1 = var0 != null ? var0.getString("GUI.OVERWORLD-MODE", "multi") : "multi";
      return !"single".equalsIgnoreCase(var1.trim());
   }

   public static String getOverworldDefaultRegion() {
      FileConfiguration var0 = B();
      return var0 != null ? var0.getString("GUI.OVERWORLD-DEFAULT-REGION", "europe") : "europe";
   }

   public static void openRTPGUI(Player var0) {
      Inventory var1 = Bukkit.createInventory(new _B(), 27, getGuiTitle());
      updateItems(var1, var0);
      var0.openInventory(var1);
   }

   public static void openOverworldGUI(Player var0) {
      Inventory var1 = Bukkit.createInventory(new _A(), 27, getGuiTitle());
      updateOverworldItems(var1, var0);
      var0.openInventory(var1);
   }

   public static void updateItems(Inventory var0, Player var1) {
      FileConfiguration var2 = B();
      ConfigurationSection var3 = var2 != null ? var2.getConfigurationSection("GUI.MAIN-MENU") : null;
      String var4 = A(A("overworld"));
      String var5 = A(A("nether"));
      String var6 = A(A("end"));
      String var7 = A(var1);
      String var8 = isOverworldMultiMode() ? "&fClick to select a region" : "&fClick to randomly teleport";
      A(var0, A(var3, "OVERWORLD"), "MAIN_OVERWORLD", 11, Material.GRASS_BLOCK, "&aᴏᴠᴇʀᴡᴏʀʟᴅ", List.of(var8, "", "&7Players (&5{PLAYERS}&7)"), Map.of("{PLAYERS}", var4, "{PING}", var7));
      A(var0, A(var3, "NETHER"), "MAIN_NETHER", 13, Material.NETHERRACK, "&aɴᴇᴛʜᴇʀ", List.of("&fClick to randomly teleport", "", "&7Players (&5{PLAYERS}&7)", "&7Europe (&5{PING}ms&7)"), Map.of("{PLAYERS}", var5, "{PING}", var7));
      A(var0, A(var3, "END"), "MAIN_END", 15, Material.END_STONE, "&aᴇɴᴅ", List.of("&fClick to randomly teleport", "", "&7Players (&5{PLAYERS}&7)", "&7Europe (&5{PING}ms&7)"), Map.of("{PLAYERS}", var6, "{PING}", var7));
   }

   public static void updateOverworldItems(Inventory var0, Player var1) {
      FileConfiguration var2 = B();
      ConfigurationSection var3 = var2 != null ? var2.getConfigurationSection("GUI.OVERWORLD-MENU") : null;
      ConfigurationSection var4 = var3 != null ? var3.getConfigurationSection("REGIONS") : null;
      com.h2ph.T.C var5 = ((PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class)).getServerStatusManager();
      A(var0, A(var3, "BACK"), "SUB_BACK", 22, Material.BARRIER, "&cBack", List.of("&7Return to the main menu"), Map.of());
      if (var4 != null && !var4.getKeys(false).isEmpty()) {
         for(String var7 : var4.getKeys(false)) {
            ConfigurationSection var8 = var4.getConfigurationSection(var7);
            if (var8 != null) {
               String var9 = var8.getString("REGION", var7.toLowerCase());
               com.h2ph.T.C._A var10 = var5.A(var9);
               A(var0, var8, "SUB_REGION:" + var9, 10, Material.GRASS_BLOCK, "&a" + var7, List.of("&fClick to randomly teleport", "", "&7Players (&5{PLAYERS}&7)", "&7Ping (&5{PING}ms&7)"), Map.of("{PLAYERS}", String.valueOf(var10.C()), "{PING}", String.valueOf(var10.A())));
            }
         }
      } else {
         A(var0, var5, "Europe", "europe", 10);
         A(var0, var5, "East", "east", 11);
         A(var0, var5, "Asia", "asia", 12);
      }

   }

   private static void A(Inventory var0, com.h2ph.T.C var1, String var2, String var3, int var4) {
      com.h2ph.T.C._A var5 = var1.A(var3);
      var0.setItem(var4, A(Material.GRASS_BLOCK, "&a" + var2, List.of("&fClick to randomly teleport", "", "&7Players (&5" + var5.C() + "&7)", "&7Ping (&5" + var5.A() + "ms&7)"), "SUB_REGION:" + var3));
   }

   public static void handleAction(Player var0, String var1) {
      if (var1 != null) {
         if (var1.equals("MAIN_OVERWORLD")) {
            if (isOverworldMultiMode()) {
               openOverworldGUI(var0);
            } else {
               var0.closeInventory();
               teleportOrConnect(var0, getOverworldDefaultRegion(), "overworld");
            }
         } else if (var1.equals("MAIN_NETHER")) {
            var0.closeInventory();
            com.h2ph.E.A.C(var0, "europe", "nether");
         } else if (var1.equals("MAIN_END")) {
            var0.closeInventory();
            com.h2ph.E.A.C(var0, "europe", "end");
         } else if (var1.equals("SUB_BACK")) {
            openRTPGUI(var0);
         } else if (var1.startsWith("SUB_REGION:")) {
            String var2 = var1.substring("SUB_REGION:".length());
            var0.closeInventory();
            teleportOrConnect(var0, var2);
         }

      }
   }

   public static String getActionId(ItemStack var0) {
      if (var0 == null) {
         return null;
      } else {
         ItemMeta var1 = var0.getItemMeta();
         return var1 == null ? null : (String)var1.getPersistentDataContainer().get(A(), PersistentDataType.STRING);
      }
   }

   private static NamespacedKey A() {
      PrismSurvival var0 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      return new NamespacedKey(var0, "rtp_action");
   }

   private static ConfigurationSection A(ConfigurationSection var0, String var1) {
      return var0 != null ? var0.getConfigurationSection(var1) : null;
   }

   private static void A(Inventory var0, ConfigurationSection var1, String var2, int var3, Material var4, String var5, List<String> var6, Map<String, String> var7) {
      int var8 = var1 != null ? var1.getInt("SLOT", var3) : var3;
      Material var9 = var4;
      String var10 = var1 != null ? var1.getString("MATERIAL") : null;
      if (var10 != null) {
         try {
            var9 = Material.valueOf(var10.trim().toUpperCase());
         } catch (IllegalArgumentException var16) {
            var9 = var4;
         }
      }

      String var11 = var1 != null && var1.getString("NAME") != null ? var1.getString("NAME") : var5;
      List var12 = var1 != null && var1.isList("LORE") ? var1.getStringList("LORE") : var6;
      ArrayList var13 = new ArrayList();

      for(String var15 : var12) {
         var13.add(A(var15, var7));
      }

      var0.setItem(var8, A(var9, A(var11, var7), var13, var2));
   }

   private static String A(String var0, Map<String, String> var1) {
      String var2 = var0;

      for(Map.Entry var4 : var1.entrySet()) {
         var2 = var2.replace((CharSequence)var4.getKey(), (CharSequence)var4.getValue());
      }

      return var2;
   }

   private static ItemStack A(Material var0, String var1, List<String> var2, String var3) {
      ItemStack var4 = new ItemStack(var0);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var1));
         ArrayList var6 = new ArrayList();

         for(String var8 : var2) {
            var6.add(ChatColor.translateAlternateColorCodes('&', var8));
         }

         var5.setLore(var6);
         if (var3 != null) {
            var5.getPersistentDataContainer().set(A(), PersistentDataType.STRING, var3);
         }

         var4.setItemMeta(var5);
      }

      return var4;
   }

   private static int A(String var0) {
      PrismSurvival var1 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      if (var1.getRTPConfig() == null) {
         return 0;
      } else {
         String var2 = var1.getRTPConfig().getString("worlds." + var0 + ".world");
         if (var2 == null) {
            return 0;
         } else {
            World var3 = Bukkit.getWorld(var2);
            return var3 == null ? 0 : var3.getPlayers().size();
         }
      }
   }

   private static String A(Player var0) {
      try {
         Object var1 = var0.getClass().getMethod("getHandle").invoke(var0);
         return String.valueOf(var1.getClass().getField("ping").getInt(var1));
      } catch (Exception var4) {
         try {
            return String.valueOf(var0.getPing());
         } catch (NoSuchMethodError var3) {
            return "0";
         }
      }
   }

   private static String A(int var0) {
      if (var0 < 1000) {
         return String.valueOf(var0);
      } else {
         int var1 = (int)(Math.log((double)var0) / Math.log((double)1000.0F));
         return String.format("%.2f%c", (double)var0 / Math.pow((double)1000.0F, (double)var1), "kMGTPE".charAt(var1 - 1));
      }
   }

   private static String A(PrismSurvival var0) {
      String var1 = var0.getSurvivalConfig().getString("current-region");
      if (var1 != null && !var1.isEmpty()) {
         FileConfiguration var2 = var0.getRTPRegionConfig(var1.toLowerCase());
         if (var2 != null) {
            return var2.getString("server");
         }
      }

      return var0.getSurvivalConfig().getString("server-id");
   }

   public static class _A implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static class _B implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }
}
