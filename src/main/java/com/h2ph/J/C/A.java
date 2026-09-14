package com.h2ph.J.C;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
   private static final DecimalFormat B = new DecimalFormat("#.##");

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (var4.length < 2) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /pay <player> <amount>");
         return true;
      } else {
         String var6 = var4[0];
         String var7 = var4[1];

         double var8;
         try {
            var8 = this.A(var7);
         } catch (NumberFormatException var11) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount format. Examples: 100, 1k, 1m");
            return true;
         }

         if (var8 <= (double)0.0F) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Amount must be positive.");
            return true;
         } else {
            Player var10 = Bukkit.getPlayer(var6);
            if (var10 != null) {
               this.A(var5, var10.getUniqueId(), var10.getName(), var8);
            } else {
               this.A.getSchedulerAdapter().runTaskAsync(() -> {
                  OfflinePlayer var5x = Bukkit.getOfflinePlayer(var6);
                  if (!var5x.hasPlayedBefore() && !var5x.isOnline()) {
                     this.A.getSchedulerAdapter().runTask(() -> this.A(var5, "&cThat player does not exist."));
                  } else {
                     this.A(var5, var5x.getUniqueId(), var5x.getName(), var8);
                  }
               });
            }

            return true;
         }
      }
   }

   private void A(Player var1, UUID var2, String var3, double var4) {
      if (var1.getUniqueId().equals(var2)) {
         if (!Bukkit.isPrimaryThread()) {
            this.A.getSchedulerAdapter().runTask(() -> this.A(var1, "&cYou cannot pay yourself."));
         } else {
            this.A(var1, "&cYou cannot pay yourself.");
         }

      } else if (!Bukkit.isPrimaryThread()) {
         this.A.getSchedulerAdapter().runTask(() -> this.A(var1, var2, var3, var4));
      } else {
         PlayerData var6 = this.A.getPlayerDataManager().get(var1.getUniqueId());
         if (var6 == null) {
            var6 = this.A.getPlayerDataManager().loadPlayer(var1.getUniqueId());
         }

         if (var6.getMoney() < var4) {
            String var13 = ChatColor.translateAlternateColorCodes('&', "&cYou do not have enough money.");
            var1.sendMessage(var13);
            var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var13));
         } else {
            PlayerData var7 = this.A.getPlayerDataManager().get(var2);
            boolean var8 = var7 != null;
            if (var7 == null) {
               var7 = this.A.getPlayerDataManager().loadPlayer(var2);
            }

            var6.setMoney(var6.getMoney() - var4);
            var7.setMoney(var7.getMoney() + var4);
            String var9 = this.A(var4);
            String var10 = ChatColor.translateAlternateColorCodes('&', "&7You paid &b" + var3 + "&a $" + var9);
            var1.sendMessage(var10);
            var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var10));
            Player var11 = Bukkit.getPlayer(var2);
            if (var11 != null) {
               String var10001 = var1.getName();
               String var12 = ChatColor.translateAlternateColorCodes('&', "&b" + var10001 + "&7 paid you &a$" + var9);
               var11.sendMessage(var12);
               var11.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var12));
            } else {
               String var15 = var1.getName();
               String var14 = ChatColor.translateAlternateColorCodes('&', "&b" + var15 + "&7 paid you &a$" + var9);
               var7.addOfflinePayment(var14);
            }

            this.A.getPlayerDataManager().savePlayer(var1.getUniqueId());
            this.A.getPlayerDataManager().savePlayer(var2);
            if (!var8 && var11 == null) {
               this.A.getPlayerDataManager().unload(var2);
            }

         }
      }
   }

   private void A(Player var1, String var2) {
      String var3 = ChatColor.translateAlternateColorCodes('&', var2);
      var1.sendMessage(var3);
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var3));
      var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
   }

   private double A(String var1) throws NumberFormatException {
      var1 = var1.toLowerCase();
      double var2 = (double)1.0F;
      if (var1.endsWith("k")) {
         var2 = (double)1000.0F;
         var1 = var1.substring(0, var1.length() - 1);
      } else if (var1.endsWith("m")) {
         var2 = (double)1000000.0F;
         var1 = var1.substring(0, var1.length() - 1);
      } else if (var1.endsWith("b")) {
         var2 = (double)1.0E9F;
         var1 = var1.substring(0, var1.length() - 1);
      } else if (var1.endsWith("t")) {
         var2 = 1.0E12;
         var1 = var1.substring(0, var1.length() - 1);
      }

      double var4 = Double.parseDouble(var1);
      return var4 * var2;
   }

   private String A(double var1) {
      if (var1 >= 1.0E12) {
         return this.A(var1, 1.0E12, "t");
      } else if (var1 >= (double)1.0E9F) {
         return this.A(var1, (double)1.0E9F, "b");
      } else if (var1 >= (double)1000000.0F) {
         return this.A(var1, (double)1000000.0F, "m");
      } else {
         return var1 >= (double)1000.0F ? this.A(var1, (double)1000.0F, "k") : B.format(Math.floor(var1 * (double)10.0F) / (double)10.0F);
      }
   }

   private String A(double var1, double var3, String var5) {
      double var6 = var1 / var3;
      var6 = Math.floor(var6 * (double)10.0F) / (double)10.0F;
      String var10000 = B.format(var6);
      return var10000 + var5;
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length == 1) {
         String var6 = var4[0].toLowerCase();
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var6)).collect(Collectors.toList());
      } else if (var4.length == 2) {
         ArrayList var5 = new ArrayList();
         var5.add("100");
         var5.add("1k");
         var5.add("10k");
         var5.add("1m");
         return var5;
      } else {
         return Collections.emptyList();
      }
   }
}
