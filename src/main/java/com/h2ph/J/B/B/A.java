package com.h2ph.J.B.B;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class A implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!var1.hasPermission("prismcore.admin.economy")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
         return true;
      } else if (var4.length < 3) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /economy <give|set|remove> <player> <amount>");
         return true;
      } else {
         return this.A(var1, var4);
      }
   }

   private String A(double var1) {
      if (var1 >= 1.0E12) {
         return this.A(var1, 1.0E12, "t");
      } else if (var1 >= (double)1.0E9F) {
         return this.A(var1, (double)1.0E9F, "b");
      } else if (var1 >= (double)1000000.0F) {
         return this.A(var1, (double)1000000.0F, "m");
      } else if (var1 >= (double)1000.0F) {
         return this.A(var1, (double)1000.0F, "k");
      } else {
         return var1 % (double)1.0F == (double)0.0F ? String.valueOf((long)var1) : String.format("%.2f", var1);
      }
   }

   private String A(double var1, double var3, String var5) {
      double var6 = var1 / var3;
      return var6 == (double)((long)var6) ? String.format("%.0f%s", var6, var5) : String.format("%.2f%s", var6, var5);
   }

   private boolean A(String var1) {
      return var1.equalsIgnoreCase("give") || var1.equalsIgnoreCase("set") || var1.equalsIgnoreCase("remove");
   }

   private boolean A(CommandSender var1, String[] var2) {
      String var3 = var2[0].toLowerCase();
      String var4 = var2[1];
      String var5 = var2[2];
      if (var2.length < 3) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /economy <give|set|remove> <player> <amount>");
         this.A(var1, Sound.ENTITY_VILLAGER_NO);
         return true;
      } else {
         double var6;
         try {
            var6 = this.B(var5);
         } catch (NumberFormatException var18) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount! Examples: 100h, 10k, 1.5m");
            this.A(var1, Sound.ENTITY_VILLAGER_NO);
            return true;
         }

         if (!this.A(var3)) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid action! Use: give, set, remove");
            this.A(var1, Sound.ENTITY_VILLAGER_NO);
            return true;
         } else {
            OfflinePlayer var8 = Bukkit.getOfflinePlayer(var4);
            if (!var8.hasPlayedBefore() && !var8.isOnline()) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "That user does not exist.");
               this.A(var1, Sound.ENTITY_VILLAGER_NO);
               return true;
            } else {
               PlayerData var9 = this.A.getPlayerDataManager().get(var8.getUniqueId());
               double var10 = var9.getMoney();
               double var12 = var10;
               switch (var3) {
                  case "give":
                     var12 = var10 + var6;
                     var9.setMoney(var12);
                     String var20 = String.valueOf(ChatColor.GREEN);
                     var1.sendMessage(var20 + "Gave " + String.valueOf(ChatColor.GOLD) + "$" + this.A(var6) + String.valueOf(ChatColor.GREEN) + " to " + String.valueOf(ChatColor.YELLOW) + var4 + String.valueOf(ChatColor.GREEN) + ". New balance: " + String.valueOf(ChatColor.GOLD) + "$" + this.A(var12));
                     break;
                  case "set":
                     var12 = var6;
                     var9.setMoney(var6);
                     String var19 = String.valueOf(ChatColor.GREEN);
                     var1.sendMessage(var19 + "Set " + String.valueOf(ChatColor.YELLOW) + var4 + String.valueOf(ChatColor.GREEN) + "'s balance to " + String.valueOf(ChatColor.GOLD) + "$" + this.A(var6));
                     break;
                  case "remove":
                     var12 = Math.max((double)0.0F, var10 - var6);
                     var9.setMoney(var12);
                     double var16 = var10 - var12;
                     String var10001 = String.valueOf(ChatColor.GREEN);
                     var1.sendMessage(var10001 + "Removed " + String.valueOf(ChatColor.GOLD) + "$" + this.A(var16) + String.valueOf(ChatColor.GREEN) + " from " + String.valueOf(ChatColor.YELLOW) + var4 + String.valueOf(ChatColor.GREEN) + ". New balance: " + String.valueOf(ChatColor.GOLD) + "$" + this.A(var12));
               }

               this.A.getPlayerDataManager().savePlayer(var8.getUniqueId());
               this.A(var1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
               if (var8.isOnline() && var8.getPlayer() != null) {
                  Player var10000 = var8.getPlayer();
                  String var21 = String.valueOf(ChatColor.GRAY);
                  var10000.sendMessage(var21 + "Your balance has been updated to " + String.valueOf(ChatColor.GREEN) + "$" + this.A(var12));
               }

               return true;
            }
         }
      }
   }

   private void A(CommandSender var1, Sound var2) {
      if (var1 instanceof Player) {
         try {
            Player var3 = (Player)var1;
            var3.playSound(var3.getLocation(), var2, 1.0F, 1.0F);
         } catch (Exception var4) {
         }
      }

   }

   private double B(String var1) throws NumberFormatException {
      var1 = var1.toLowerCase().trim();
      if (var1.isEmpty()) {
         throw new NumberFormatException("Empty amount");
      } else {
         char var2 = var1.charAt(var1.length() - 1);
         double var3 = (double)1.0F;
         String var5 = var1;
         if (Character.isLetter(var2)) {
            var5 = var1.substring(0, var1.length() - 1);
            switch (var2) {
               case 'b' -> var3 = (double)1.0E9F;
               case 'h' -> var3 = (double)100.0F;
               case 'k' -> var3 = (double)1000.0F;
               case 'm' -> var3 = (double)1000000.0F;
               case 't' -> var3 = 1.0E12;
               default -> throw new NumberFormatException("Invalid suffix: " + var2);
            }
         }

         double var6 = Double.parseDouble(var5);
         double var8 = var6 * var3;
         return var8 < (double)0.0F ? (double)0.0F : var8;
      }
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!var1.hasPermission("prismcore.admin.economy")) {
         return new ArrayList();
      } else if (var4.length == 1) {
         List var5 = Arrays.asList("give", "set", "remove");
         return (List)var5.stream().filter((var1x) -> var1x.startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      } else if (var4.length == 2) {
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
      } else {
         return (List<String>)(var4.length == 3 ? Arrays.asList("100h", "10k", "100k", "1m", "10m", "1b") : new ArrayList());
      }
   }
}
