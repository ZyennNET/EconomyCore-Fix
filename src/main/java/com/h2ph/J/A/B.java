package com.h2ph.J.A;

import com.h2ph.PrismSurvival;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class B implements CommandExecutor {
   private final PrismSurvival A;

   public B(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length >= 2 && var4[0].equalsIgnoreCase("admin") && var4[1].equalsIgnoreCase("reload")) {
         if (!var1.isOp() && !var1.hasPermission("economysmpcore.profile.admin")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
            return true;
         } else {
            this.A.reloadConfig();
            String var8 = this.A.getConfig().getString("profile.messages.config-reloaded", "&aProfile viewer configuration reloaded.");
            var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var8));
            return true;
         }
      } else if (var1 instanceof Player) {
         Player var5 = (Player)var1;
         if (!var5.hasPermission("economysmpcore.profile.view")) {
            String var9 = this.A.getConfig().getString("profile.messages.no-permission", "&cYou don't have permission to view player profiles.");
            var5.sendMessage(ChatColor.translateAlternateColorCodes('&', var9));
            return true;
         } else if (var4.length == 0) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /profile <player>");
            return true;
         } else {
            Player var6 = Bukkit.getPlayer(var4[0]);
            if (var6 == null) {
               String var7 = this.A.getConfig().getString("profile.messages.player-not-found", "&cPlayer not online.");
               var5.sendMessage(ChatColor.translateAlternateColorCodes('&', var7));
               return true;
            } else {
               (new D(this.A, var6.getUniqueId())).open(var5);
               return true;
            }
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      }
   }
}
