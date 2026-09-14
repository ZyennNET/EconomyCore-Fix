package com.h2ph.J.C;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.text.DecimalFormat;
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

public class B implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;
   private static final DecimalFormat B = new DecimalFormat("#.#");

   public B(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length == 0) {
         if (!(var1 instanceof Player)) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Console must specify a player.");
            return true;
         }

         Player var5 = (Player)var1;
         this.A(var1, var5.getUniqueId(), var5.getName(), true);
      } else {
         String var7 = var4[0];
         Player var6 = Bukkit.getPlayer(var7);
         if (var6 != null) {
            this.A(var1, var6.getUniqueId(), var6.getName(), false);
         } else {
            this.A.getSchedulerAdapter().runTaskAsync(() -> {
               OfflinePlayer var3 = Bukkit.getOfflinePlayer(var7);
               if (!var3.hasPlayedBefore() && !var3.isOnline()) {
                  this.A.getSchedulerAdapter().runTask(() -> {
                     String var1x = ChatColor.translateAlternateColorCodes('&', "&cThat player does not exist.");
                     var1.sendMessage(var1x);
                     if (var1 instanceof Player var2) {
                        var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var1x));
                        var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     }

                  });
               } else {
                  this.A(var1, var3.getUniqueId(), var3.getName(), false);
               }
            });
         }
      }

      return true;
   }

   private void A(CommandSender var1, UUID var2, String var3, boolean var4) {
      Player var5 = Bukkit.getPlayer(var2);
      if (var5 != null && var5.isOnline()) {
         if (!Bukkit.isPrimaryThread()) {
            this.A.getSchedulerAdapter().runTask(() -> this.A(var1, var2, var3, false));
            return;
         }

         PlayerData var11 = this.A.getPlayerDataManager().get(var2);
         double var12 = var11 != null ? var11.getMoney() : (double)0.0F;
         this.A(var1, var3, var12, var1 instanceof Player && ((Player)var1).getUniqueId().equals(var2));
      } else {
         if (Bukkit.isPrimaryThread()) {
            this.A.getSchedulerAdapter().runTaskAsync(() -> this.A(var1, var2, var3, false));
            return;
         }

         PlayerData var6 = this.A.getPlayerDataManager().loadPlayer(var2);
         double var7 = var6 != null ? var6.getMoney() : (double)0.0F;
         String var9 = var6 != null && var6.getName() != null ? var6.getName() : var3;
         this.A.getSchedulerAdapter().runTask(() -> this.A(var1, var9, var7, var1 instanceof Player && ((Player)var1).getUniqueId().equals(var2)));
      }

   }

   private void A(CommandSender var1, String var2, double var3, boolean var5) {
      String var6 = this.A(var3);
      String var7;
      if (var5) {
         var7 = ChatColor.translateAlternateColorCodes('&', "&7You have &a$" + var6);
      } else {
         var7 = ChatColor.translateAlternateColorCodes('&', "&b" + var2 + "&7 has &a$" + var6);
      }

      var1.sendMessage(var7);
      if (var1 instanceof Player) {
         ((Player)var1).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var7));
      }

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
         String var5 = var4[0].toLowerCase();
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var5)).collect(Collectors.toList());
      } else {
         return Collections.emptyList();
      }
   }
}
