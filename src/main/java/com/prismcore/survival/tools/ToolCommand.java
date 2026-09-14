package com.prismcore.survival.tools;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;

public class ToolCommand implements CommandExecutor, TabCompleter {
   private final ToolsManager manager;

   public ToolCommand(ToolsManager var1) {
      this.manager = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("donuttools.admin")) {
         var1.sendMessage(Utils.formatColors("&cYou do not have permission."));
         return true;
      } else {
         if (var4.length >= 3 && var4[0].equalsIgnoreCase("give")) {
            Player var5 = Bukkit.getPlayerExact(var4[1]);
            String var6 = var4[2].toLowerCase();
            long var7 = 0L;
            if (var4.length == 4) {
               var7 = Utils.parseDuration(var4[3]);
               if (var7 <= 0L) {
                  var1.sendMessage(Utils.formatColors("&cInvalid time format. Use: 1d, 12h, 30m, 1w"));
                  return true;
               }
            }

            if (var5 == null) {
               var1.sendMessage(Utils.formatColors("&cPlayer not found."));
               return true;
            }

            if (List.of("drill", "axe", "shovel").contains(var6)) {
               this.giveTool(var5, var6, var7);
               var1.sendMessage(Utils.formatColors("&aGiven " + var6 + " to &f" + var5.getName()));
               return true;
            }

            if (var6.equals("multitool")) {
               this.giveMultiTool(var5, var7);
               var1.sendMessage(Utils.formatColors("&aGiven multitool to &f" + var5.getName()));
               return true;
            }

            if (var6.equals("bucket")) {
               this.giveBucket(var5, var7);
               var1.sendMessage(Utils.formatColors("&aGiven countdown bucket to &f" + var5.getName()));
               return true;
            }

            if (var6.equals("shardbooster")) {
               this.giveShardBooster(var5, var7);
               var1.sendMessage(Utils.formatColors("&aGiven shard booster to &f" + var5.getName()));
               return true;
            }
         }

         var1.sendMessage(Utils.formatColors("&eUsage: /tools give <player> <drill|axe|shovel|multitool|bucket|shardbooster> [time]"));
         return true;
      }
   }

   private void giveTool(Player var1, String var2, long var3) {
      ConfigurationSection var6 = this.manager.getConfig().getConfigurationSection(var2);
      if (var6 == null) {
         var1.sendMessage(Utils.formatColors("&cError: " + var2 + " section missing in config.yml"));
      } else {
         boolean var7 = var6.getBoolean("use-countdown", true);
         long var8 = var3 > 0L ? var3 : var6.getLong("timer", 0L);

         Material var5;
         try {
            var5 = Material.valueOf(var6.getString("material", "").toUpperCase());
         } catch (IllegalArgumentException var16) {
            var1.sendMessage(Utils.formatColors("&cInvalid material for " + var2));
            return;
         }

         ItemStack var10 = new ItemStack(var5);
         ItemMeta var11 = var10.getItemMeta();
         if (var11 == null) {
            var1.sendMessage(Utils.formatColors("&cFailed to create " + var2));
         } else {
            if (var6.contains("enchantments")) {
               for(String var13 : var6.getConfigurationSection("enchantments").getKeys(false)) {
                  Enchantment var14 = Enchantment.getByName(var13.toUpperCase());
                  int var15 = var6.getInt("enchantments." + var13, 1);
                  if (var14 != null) {
                     var11.addEnchant(var14, var15, true);
                  }
               }
            }

            if (var6.getBoolean("hide-enchantments", false)) {
               var11.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }

            if (var6.isString("display-name")) {
               var11.setDisplayName(Utils.formatColors(var6.getString("display-name")));
            }

            if (var6.isList("lore")) {
               List var17 = var6.getStringList("lore");
               List var18 = var17.stream().map((var3x) -> {
                  if (var7) {
                     String var4 = Utils.formatDuration(var8);
                     return Utils.formatColors(var3x.replace("%countdown%", var4));
                  } else {
                     return Utils.formatColors(var3x);
                  }
               }).toList();
               var11.setLore(var18);
            }

            if (var11 instanceof Damageable) {
               ((Damageable)var11).setDamage(0);
            }

            var11.getPersistentDataContainer().set(ToolsManager.REMAINING_KEY, PersistentDataType.LONG, var8);
            var10.setItemMeta(var11);
            var1.getInventory().addItem(new ItemStack[]{var10});
         }
      }
   }

   private void giveMultiTool(Player var1, long var2) {
      ConfigurationSection var5 = this.manager.getConfig().getConfigurationSection("multitool");
      if (var5 == null) {
         var1.sendMessage(Utils.formatColors("&cError: multitool section missing in config.yml"));
      } else {
         boolean var6 = var5.getBoolean("use-countdown", true);
         long var7 = var2 > 0L ? var2 : var5.getLong("timer", 0L);

         Material var4;
         try {
            var4 = Material.valueOf(var5.getString("material", "").toUpperCase());
         } catch (IllegalArgumentException var15) {
            var4 = Material.DIAMOND_HOE;
         }

         ItemStack var9 = new ItemStack(var4);
         ItemMeta var10 = var9.getItemMeta();
         if (var10 == null) {
            var1.sendMessage(Utils.formatColors("&cFailed to create multitool"));
         } else {
            if (var5.contains("enchantments")) {
               for(String var12 : var5.getConfigurationSection("enchantments").getKeys(false)) {
                  Enchantment var13 = Enchantment.getByName(var12.toUpperCase());
                  int var14 = var5.getInt("enchantments." + var12, 1);
                  if (var13 != null) {
                     var10.addEnchant(var13, var14, true);
                  }
               }
            }

            if (var5.getBoolean("hide-enchantments", false)) {
               var10.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }

            if (var5.isString("display-name")) {
               var10.setDisplayName(Utils.formatColors(var5.getString("display-name")));
            }

            if (var5.isList("lore")) {
               List var16 = var5.getStringList("lore");
               List var17 = var16.stream().map((var3) -> {
                  if (var6) {
                     String var4 = Utils.formatDuration(var7);
                     return Utils.formatColors(var3.replace("%countdown%", var4));
                  } else {
                     return Utils.formatColors(var3);
                  }
               }).toList();
               var10.setLore(var17);
            }

            var10.getPersistentDataContainer().set(ToolsManager.MULTI_KEY, PersistentDataType.BYTE, (byte)1);
            var10.getPersistentDataContainer().set(ToolsManager.REMAINING_KEY, PersistentDataType.LONG, var7);
            var9.setItemMeta(var10);
            var1.getInventory().addItem(new ItemStack[]{var9});
         }
      }
   }

   private void giveBucket(Player var1, long var2) {
      ConfigurationSection var5 = this.manager.getConfig().getConfigurationSection("bucket");
      if (var5 == null) {
         var1.sendMessage(Utils.formatColors("&cError: bucket section missing in config.yml"));
      } else {
         boolean var6 = var5.getBoolean("use-countdown", true);
         long var7 = var2 > 0L ? var2 : var5.getLong("timer", 0L);

         Material var4;
         try {
            var4 = Material.valueOf(var5.getString("material", "BUCKET").toUpperCase());
         } catch (IllegalArgumentException var15) {
            var4 = Material.BUCKET;
         }

         ItemStack var9 = new ItemStack(var4);
         ItemMeta var10 = var9.getItemMeta();
         if (var10 == null) {
            var1.sendMessage(Utils.formatColors("&cFailed to create bucket"));
         } else {
            if (var5.contains("enchantments")) {
               for(String var12 : var5.getConfigurationSection("enchantments").getKeys(false)) {
                  Enchantment var13 = Enchantment.getByName(var12.toUpperCase());
                  int var14 = var5.getInt("enchantments." + var12, 1);
                  if (var13 != null) {
                     var10.addEnchant(var13, var14, true);
                  }
               }
            }

            if (var5.getBoolean("hide-enchantments", false)) {
               var10.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
            }

            if (var5.isString("display-name")) {
               var10.setDisplayName(Utils.formatColors(var5.getString("display-name")));
            }

            if (var5.isList("lore")) {
               List var16 = var5.getStringList("lore");
               List var17 = var16.stream().map((var3) -> {
                  if (var6) {
                     String var4 = Utils.formatDuration(var7);
                     return Utils.formatColors(var3.replace("%countdown%", var4));
                  } else {
                     return Utils.formatColors(var3);
                  }
               }).toList();
               var10.setLore(var17);
            }

            var10.getPersistentDataContainer().set(ToolsManager.REMAINING_KEY, PersistentDataType.LONG, var7);
            var9.setItemMeta(var10);
            var1.getInventory().addItem(new ItemStack[]{var9});
         }
      }
   }

   private void giveShardBooster(Player var1, long var2) {
      ConfigurationSection var4 = this.manager.getConfig().getConfigurationSection("shardbooster");
      if (var4 == null) {
         var1.sendMessage(Utils.formatColors("&cError: shardbooster section missing in config.yml"));
      } else {
         long var5 = var2 > 0L ? var2 : var4.getLong("timer", 86400L);
         ItemStack var7 = new ItemStack(Material.POTION);
         PotionMeta var8 = (PotionMeta)var7.getItemMeta();
         if (var8 == null) {
            var1.sendMessage(Utils.formatColors("&cFailed to create shard booster"));
         } else {
            var8.setBasePotionType(PotionType.WATER);
            String var9 = var4.getString("potion-color", "8B5CF6");

            try {
               int var10 = Integer.parseInt(var9, 16);
               var8.setColor(Color.fromRGB(var10));
            } catch (NumberFormatException var13) {
               var8.setColor(Color.PURPLE);
            }

            if (var4.isString("display-name")) {
               var8.setDisplayName(Utils.formatColors(var4.getString("display-name")));
            }

            if (var4.isList("lore")) {
               List var14 = var4.getStringList("lore");
               String var11 = Utils.formatDuration(var5);
               List var12 = var14.stream().map((var1x) -> Utils.formatColors(var1x.replace("%countdown%", var11))).toList();
               var8.setLore(var12);
            }

            var8.getPersistentDataContainer().set(ToolsManager.BOOSTER_KEY, PersistentDataType.BYTE, (byte)1);
            var8.getPersistentDataContainer().set(ToolsManager.REMAINING_KEY, PersistentDataType.LONG, var5);
            var7.setItemMeta(var8);
            var1.getInventory().addItem(new ItemStack[]{var7});
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         return (List)List.of("give").stream().filter((var1x) -> var1x.startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("give")) {
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
      } else if (var4.length == 3 && var4[0].equalsIgnoreCase("give")) {
         return (List)List.of("drill", "axe", "shovel", "multitool", "bucket", "shardbooster").stream().filter((var1x) -> var1x.startsWith(var4[2].toLowerCase())).collect(Collectors.toList());
      } else {
         return var4.length == 4 && var4[0].equalsIgnoreCase("give") ? List.of("1d", "12h", "30m", "1w") : List.of();
      }
   }
}
