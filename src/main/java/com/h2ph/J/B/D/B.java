package com.h2ph.J.B.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class B implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public B(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (!var5.hasPermission("prism.crates.admin")) {
         var5.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
         return true;
      } else if (var4.length < 2) {
         var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate <create|edit|get> ...");
         return true;
      } else if (var4[0].equalsIgnoreCase("create")) {
         if (var4.length < 3) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate create <name> <key> [type] [container]");
            return true;
         } else {
            String var6 = "NORMAL";
            if (var4.length >= 4) {
               var6 = var4[3];
            }

            String var7 = "CHEST";
            if (var4.length >= 5) {
               var7 = var4[4];
            }

            return this.A(var5, var4[1], var4[2], var6, var7);
         }
      } else if (var4[0].equalsIgnoreCase("edit")) {
         if (var4.length < 2) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate edit <name>");
            return true;
         } else {
            return this.C(var5, var4[1]);
         }
      } else if (var4[0].equalsIgnoreCase("get")) {
         if (var4.length < 2) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate get <name>");
            return true;
         } else {
            return this.B(var5, var4[1]);
         }
      } else if (var4[0].equalsIgnoreCase("delete")) {
         if (var4.length < 2) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate delete <name>");
            return true;
         } else {
            return this.A(var5, var4[1]);
         }
      } else if (var4[0].equalsIgnoreCase("effects")) {
         if (var4.length < 4) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate effects <add|remove> <crate> <effect>");
            return true;
         } else {
            return this.B(var5, var4[1], var4[2], var4[3]);
         }
      } else {
         return true;
      }
   }

   private boolean A(Player var1, String var2, String var3, String var4, String var5) {
      if (!this.A.getKeyAllManager().isValidKey(var3)) {
         String var12 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var12 + "Invalid key: " + var3);
         var12 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var12 + "Available keys: " + String.join(", ", this.A.getKeyAllManager().getValidKeys()));
         return true;
      } else {
         String var6 = var4.toUpperCase();
         if (!var6.equals("NORMAL") && !var6.equals("CAROUSEL")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid crate type. Options: NORMAL, CAROUSEL");
            return true;
         } else {
            String var7 = var5.toUpperCase();
            Material var8 = Material.getMaterial(var7);
            if (var8 != null && (var8.equals(Material.CHEST) || var8.equals(Material.ENDER_CHEST) || var8.name().endsWith("SHULKER_BOX"))) {
               File var9 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
               if (var9.exists()) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "A crate with that name already exists!");
                  return true;
               } else {
                  try {
                     var9.getParentFile().mkdirs();
                     YamlConfiguration var10 = new YamlConfiguration();
                     ((FileConfiguration)var10).set("key", var3);
                     ((FileConfiguration)var10).set("type", var6);
                     ((FileConfiguration)var10).set("container", var7);
                     ((FileConfiguration)var10).save(var9);
                     String var10001 = String.valueOf(ChatColor.GREEN);
                     var1.sendMessage(var10001 + "Crate config created: " + var9.getName() + " (" + var6 + ", " + var7 + ")");
                  } catch (IOException var11) {
                     var1.sendMessage(String.valueOf(ChatColor.RED) + "Failed to create crate config file.");
                     var11.printStackTrace();
                     return true;
                  }

                  this.A(var1, var2, var3, var7);
                  return true;
               }
            } else {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid container type. Must be CHEST, ENDER_CHEST, or SHULKER_BOX variant.");
               return true;
            }
         }
      }
   }

   private boolean B(Player var1, String var2) {
      File var3 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var3.exists()) {
         String var10001 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var10001 + "Crate not found: " + var2);
         return true;
      } else {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         String var5 = ((FileConfiguration)var4).getString("key", "unknown");
         String var6 = ((FileConfiguration)var4).getString("container", "CHEST");
         this.A(var1, var2, var5, var6);
         return true;
      }
   }

   private boolean A(Player var1, String var2) {
      File var3 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var3.exists()) {
         String var4 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var4 + "Crate not found: " + var2);
         return true;
      } else {
         if (var3.delete()) {
            String var10001 = String.valueOf(ChatColor.GREEN);
            var1.sendMessage(var10001 + "Crate " + var2 + " deleted successfully!");
         } else {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Failed to delete crate file.");
         }

         return true;
      }
   }

   private void A(Player var1, String var2, String var3, String var4) {
      Material var5 = Material.getMaterial(var4);
      if (var5 == null) {
         var5 = Material.CHEST;
      }

      ItemStack var6 = new ItemStack(var5);
      ItemMeta var7 = var6.getItemMeta();
      if (var7 != null) {
         var7.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&e" + var2 + " Crate"));
         ArrayList var8 = new ArrayList();
         String var10001 = String.valueOf(ChatColor.GRAY);
         var8.add(var10001 + "Key: " + String.valueOf(ChatColor.YELLOW) + var3);
         var8.add(String.valueOf(ChatColor.GRAY) + "Place this to create the crate.");
         var7.setLore(var8);
         PersistentDataContainer var9 = var7.getPersistentDataContainer();
         NamespacedKey var10 = new NamespacedKey(this.A, "crate_id");
         var9.set(var10, PersistentDataType.STRING, var2);
         var6.setItemMeta(var7);
      }

      var1.getInventory().addItem(new ItemStack[]{var6});
      String var11 = String.valueOf(ChatColor.GREEN);
      var1.sendMessage(var11 + "You received a " + var2 + " crate!");
   }

   private boolean C(Player var1, String var2) {
      File var3 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var3.exists()) {
         String var10001 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var10001 + "Crate not found: " + var2);
         return true;
      } else {
         Inventory var4 = Bukkit.createInventory(var1, 27, ChatColor.translateAlternateColorCodes('&', "&eEditing: " + var2));
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var3);
         String var6 = ((FileConfiguration)var5).getString("type", "NORMAL");
         if (var6.equalsIgnoreCase("CAROUSEL")) {
            ItemStack var7 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta var8 = var7.getItemMeta();
            var8.setDisplayName(String.valueOf(ChatColor.RED) + "Reserved Slot");
            var8.setLore(Collections.singletonList(String.valueOf(ChatColor.GRAY) + "Reserved for UI Elements"));
            var7.setItemMeta(var8);
            var4.setItem(4, var7);
            var4.setItem(22, var7);
         }

         if (((FileConfiguration)var5).contains("contents")) {
            ConfigurationSection var13 = ((FileConfiguration)var5).getConfigurationSection("contents");

            for(String var9 : var13.getKeys(false)) {
               try {
                  int var10 = Integer.parseInt(var9);
                  ItemStack var11 = ((FileConfiguration)var5).getItemStack("contents." + var9);
                  var4.setItem(var10, var11);
               } catch (NumberFormatException var12) {
               }
            }
         }

         var1.openInventory(var4);
         return true;
      }
   }

   private boolean B(Player var1, String var2, String var3, String var4) {
      File var5 = new File(this.A.getDataFolder(), "crates/crate/" + var3 + "-crate.yml");
      if (!var5.exists()) {
         String var10 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var10 + "Crate not found: " + var3);
         return true;
      } else {
         YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
         List var7 = ((FileConfiguration)var6).getStringList("effects");
         if (var2.equalsIgnoreCase("add")) {
            if (var7.contains(var4)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "This effect is already added.");
               return true;
            }

            var7.add(var4);
            ((FileConfiguration)var6).set("effects", var7);
            var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Added effect " + var4 + " to " + var3);
         } else if (var2.equalsIgnoreCase("set")) {
            var7.clear();
            var7.add(var4);
            ((FileConfiguration)var6).set("effects", var7);
            var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Set effect to " + var4 + " for " + var3);
         } else {
            if (!var2.equalsIgnoreCase("remove")) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /crate effects <add|remove|set> <crate> <effect|all>");
               return true;
            }

            if (var4.equalsIgnoreCase("all")) {
               var7.clear();
               ((FileConfiguration)var6).set("effects", var7);
               String var10001 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var10001 + "Removed ALL effects from " + var3);
            } else {
               if (!var7.contains(var4)) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "This effect is not present.");
                  return true;
               }

               var7.remove(var4);
               ((FileConfiguration)var6).set("effects", var7);
               var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Removed effect " + var4 + " from " + var3);
            }
         }

         try {
            ((FileConfiguration)var6).save(var5);
            if (this.A.getCrateEffectsManager() != null) {
               this.A.getCrateEffectsManager().clearCache(var3);
            }
         } catch (IOException var9) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Failed to save config.");
            var9.printStackTrace();
         }

         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("prism.crates.admin")) {
         return Collections.emptyList();
      } else if (var4.length == 1) {
         ArrayList var16 = new ArrayList();
         var16.add("create");
         var16.add("edit");
         var16.add("get");
         var16.add("delete");
         var16.add("effects");
         return (List)var16.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      } else {
         if (var4.length == 2) {
            if (var4[0].equalsIgnoreCase("effects")) {
               ArrayList var15 = new ArrayList();
               var15.add("add");
               var15.add("remove");
               var15.add("set");
               return (List)var15.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
            }

            if (var4[0].equalsIgnoreCase("edit") || var4[0].equalsIgnoreCase("get") || var4[0].equalsIgnoreCase("delete")) {
               File var5 = new File(this.A.getDataFolder(), "crates/crate");
               if (var5.exists() && var5.isDirectory()) {
                  ArrayList var18 = new ArrayList();

                  for(File var25 : var5.listFiles()) {
                     if (var25.getName().endsWith("-crate.yml")) {
                        var18.add(var25.getName().replace("-crate.yml", ""));
                     }
                  }

                  return (List)var18.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
               }
            }
         }

         if (var4.length == 3 && var4[0].equalsIgnoreCase("effects")) {
            File var11 = new File(this.A.getDataFolder(), "crates/crate");
            if (var11.exists() && var11.isDirectory()) {
               ArrayList var17 = new ArrayList();

               for(File var10 : var11.listFiles()) {
                  if (var10.getName().endsWith("-crate.yml")) {
                     var17.add(var10.getName().replace("-crate.yml", ""));
                  }
               }

               return (List)var17.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[2].toLowerCase())).collect(Collectors.toList());
            }
         }

         if (var4.length == 4 && var4[0].equalsIgnoreCase("effects")) {
            ArrayList var14 = new ArrayList();
            if (var4[1].equalsIgnoreCase("remove")) {
               var14.add("all");
            }

            var14.add("HELIX");
            var14.add("DOUBLE_HELIX");
            var14.add("HALO");
            var14.add("GROUND_RINGS");
            var14.add("VORTEX");
            var14.add("FOUNTAIN");
            var14.add("DISCO");
            var14.add("BEACON");
            var14.add("PULSE");
            var14.add("ORBIT");
            var14.add("ENDER");
            var14.add("TORNADO");
            var14.add("SPHERE");
            var14.add("LAVA_DRIP");
            var14.add("ENCHANT");
            var14.add("FLAME_CROWN");
            return (List)var14.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[3].toLowerCase())).collect(Collectors.toList());
         } else if (var4.length == 3 && var4[0].equalsIgnoreCase("create")) {
            return (List)(new ArrayList(this.A.getKeyAllManager().getValidKeys())).stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[2].toLowerCase())).collect(Collectors.toList());
         } else if (var4.length == 4 && var4[0].equalsIgnoreCase("create")) {
            ArrayList var13 = new ArrayList();
            var13.add("NORMAL");
            var13.add("CAROUSEL");
            return (List)var13.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[3].toLowerCase())).collect(Collectors.toList());
         } else if (var4.length == 5 && var4[0].equalsIgnoreCase("create")) {
            ArrayList var12 = new ArrayList();
            var12.add("CHEST");
            var12.add("ENDER_CHEST");

            for(Material var9 : Material.values()) {
               if (var9.name().endsWith("SHULKER_BOX")) {
                  var12.add(var9.name());
               }
            }

            return (List)var12.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[4].toLowerCase())).collect(Collectors.toList());
         } else {
            return Collections.emptyList();
         }
      }
   }
}
