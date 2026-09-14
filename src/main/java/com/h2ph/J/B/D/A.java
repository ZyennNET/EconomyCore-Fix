package com.h2ph.J.B.D;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class A implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("economysmpcore.admin.key")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission.");
         return true;
      } else if (var3.equalsIgnoreCase("keyall")) {
         return this.A(var1, var4);
      } else if (var4.length < 3) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /key <give|set|remove|reset> <player> <key> [amount]");
         return true;
      } else {
         String var5 = var4[0].toLowerCase();
         String var6 = var4[1];
         String var7 = var4[2];
         int var8 = 0;
         if (var4.length > 3) {
            try {
               var8 = Integer.parseInt(var4[3]);
               if (var8 < 0) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Amount cannot be negative.");
                  return true;
               }
            } catch (NumberFormatException var11) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount.");
               return true;
            }
         }

         if (!this.A.getKeyAllManager().isValidKey(var7)) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid key name.");
            return true;
         } else {
            if (!var6.equals("*") && !var6.equalsIgnoreCase("all")) {
               OfflinePlayer var12 = Bukkit.getOfflinePlayer(var6);
               if (var12 == null || !var12.hasPlayedBefore() && !var12.isOnline()) {
                  this.A(var1, "That player does not exist.");
                  return true;
               }

               this.A(var1, var12, var5, var7, var8, false);
            } else {
               if (Bukkit.getOnlinePlayers().isEmpty()) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "No players online.");
                  return true;
               }

               for(Player var10 : Bukkit.getOnlinePlayers()) {
                  this.A(var1, var10, var5, var7, var8, true);
               }

               String var10001 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var10001 + "Updated keys for " + Bukkit.getOnlinePlayers().size() + " players.");
            }

            return true;
         }
      }
   }

   private boolean A(CommandSender var1, String[] var2) {
      if (var2.length < 1) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /keyall <keyname> [amount]");
         return true;
      } else {
         String var3 = var2[0];
         int var4 = 1;
         if (var2.length > 1) {
            try {
               var4 = Integer.parseInt(var2[1]);
               if (var4 < 0) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Amount cannot be negative.");
                  return true;
               }
            } catch (NumberFormatException var8) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount.");
               return true;
            }
         }

         if (!this.A.getKeyAllManager().isValidKey(var3)) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid key name.");
            return true;
         } else if (Bukkit.getOnlinePlayers().isEmpty()) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "No players online.");
            return true;
         } else {
            int var5 = 0;

            for(Player var7 : Bukkit.getOnlinePlayers()) {
               this.A(var1, var7, var3, var4);
               ++var5;
            }

            var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Gave " + var4 + " " + var3 + " key(s) to " + var5 + " player(s).");
            return true;
         }
      }
   }

   private void A(CommandSender var1, Player var2, String var3, int var4) {
      this.A(var1, var2, "give", var3, var4, true);
   }

   private void A(CommandSender var1, OfflinePlayer var2, String var3, String var4, int var5, boolean var6) {
      PlayerData var7 = this.A.getPlayerDataManager().get(var2.getUniqueId());
      if (var7 == null) {
         if (!var6) {
            this.A(var1, "Could not load data for " + var2.getName());
         }

      } else {
         int var8 = var7.getKeyCount(var4);
         int var9;
         switch (var3) {
            case "give":
               var9 = var8 + var5;
               break;
            case "set":
               var9 = var5;
               break;
            case "remove":
               var9 = Math.max(0, var8 - var5);
               break;
            case "reset":
               var9 = 0;
               break;
            default:
               if (!var6) {
                  String var13 = String.valueOf(ChatColor.RED);
                  var1.sendMessage(var13 + "Unknown subcommand: " + var3);
               }

               return;
         }

         var7.setKeyCount(var4, var9);
         if (!var2.isOnline()) {
            this.A.getPlayerDataManager().savePlayer(var2.getUniqueId());
         }

         if (!var6) {
            String var10001 = String.valueOf(ChatColor.GREEN);
            var1.sendMessage(var10001 + "Updated keys for " + var2.getName() + ". New balance: " + var9);
         }

         if (var2.isOnline() && (var3.equals("give") || var3.equals("set") && var9 > var8)) {
            Player var10 = var2.getPlayer();
            var11 = var3.equals("give") ? var5 : var9 - var8;
            this.A(var10, var4, var11);
         }

      }
   }

   private void A(CommandSender var1, String var2) {
      String var10000 = String.valueOf(ChatColor.RED);
      String var3 = var10000 + var2;
      var1.sendMessage(var3);
      if (var1 instanceof Player var4) {
         var4.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var3));
         var4.playSound(var4.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      }

   }

   private void A(Player var1, String var2, int var3) {
      var1.playSound(var1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
      var1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have received &a" + var3 + " " + var2 + " keys&7."));
      TextComponent var4 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a[Click to teleport]"));
      var4.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/warp crates"));
      var4.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Click to warp")).create()));
      TextComponent var5 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&7 to teleport or type &a/warp crates"));
      var4.addExtra(var5);
      var1.spigot().sendMessage(var4);
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("economysmpcore.admin.key")) {
         return Collections.emptyList();
      } else if (var3.equalsIgnoreCase("keyall")) {
         return this.A(var4);
      } else if (var4.length == 1) {
         return this.A(var4[0], Arrays.asList("give", "set", "remove", "reset"));
      } else if (var4.length == 2) {
         return null;
      } else if (var4.length == 3) {
         Set var5 = this.A.getKeyAllManager().getValidKeys();
         return this.A((String)var4[2], (List)(new ArrayList(var5)));
      } else {
         return var4.length == 4 && !var4[0].equalsIgnoreCase("reset") ? this.A(var4[3], Arrays.asList("1", "16", "64")) : Collections.emptyList();
      }
   }

   private List<String> A(String[] var1) {
      if (var1.length == 1) {
         Set var2 = this.A.getKeyAllManager().getValidKeys();
         return this.A((String)var1[0], (List)(new ArrayList(var2)));
      } else {
         return var1.length == 2 ? this.A(var1[1], Arrays.asList("1", "5", "10", "16", "32", "64")) : Collections.emptyList();
      }
   }

   private List<String> A(String var1, List<String> var2) {
      return (List)var2.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var1.toLowerCase())).collect(Collectors.toList());
   }
}
