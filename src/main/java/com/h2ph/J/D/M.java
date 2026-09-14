package com.h2ph.J.D;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class M implements CommandExecutor {
   private final com.h2ph.N.B A;

   public M(com.h2ph.N.B var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (var4.length == 0) {
         var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return true;
      } else {
         String var6 = var4[0];
         if (var6.equalsIgnoreCase(var5.getName())) {
            var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return true;
         } else {
            Player var7 = Bukkit.getPlayer(var6);
            if (var7 == null) {
               this.A(var5, "&cThis user is not online.");
               return true;
            } else {
               W.openConfirmationGUI(var5, var7, com.h2ph.N.D._A.C);
               return true;
            }
         }
      }
   }

   private void A(Player var1, String var2) {
      var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2));
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var2)));
      var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
   }
}
