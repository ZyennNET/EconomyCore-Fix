package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class S implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public S(PrismSurvival var1) {
      this.A = var1;
      var1.getCommand("checktotem").setExecutor(this);
      var1.getCommand("checktotem").setTabCompleter(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (var4.length < 1) {
            var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return true;
         } else {
            Player var6 = Bukkit.getPlayer(var4[0]);
            if (var6 == null) {
               this.A.getSchedulerAdapter().runTaskAsynchronously(() -> {
                  OfflinePlayer var3 = Bukkit.getOfflinePlayer(var4[0]);
                  this.A.getSchedulerAdapter().runTask(() -> {
                     if (var3.hasPlayedBefore()) {
                        var5.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(ChatColor.RED) + "That player is not online."));
                     } else {
                        var5.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(String.valueOf(ChatColor.RED) + "That player does not exist."));
                     }

                     var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  });
               });
               return true;
            } else {
               ItemStack var7 = var6.getInventory().getItemInOffHand();
               if (var7 != null && var7.getType() == Material.TOTEM_OF_UNDYING && var7.getAmount() != 0) {
                  var6.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                  var5.playSound(var5.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
                  var6.playSound(var6.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                  return true;
               } else {
                  String var10000 = String.valueOf(ChatColor.RED);
                  String var8 = var10000 + var6.getName() + " does not have a totem in their offhand.";
                  var5.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var8));
                  var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  return true;
               }
            }
         }
      } else {
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         ArrayList var5 = new ArrayList();

         for(Player var7 : Bukkit.getOnlinePlayers()) {
            if (var1 instanceof Player && ((Player)var1).canSee(var7)) {
               var5.add(var7.getName());
            }
         }

         return var5;
      } else {
         return Collections.emptyList();
      }
   }
}
