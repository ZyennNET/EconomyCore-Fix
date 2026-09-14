package com.h2ph.J.D;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class F implements CommandExecutor {
   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("economysmpcore.craftingtable")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission.");
            return true;
         } else {
            var5.openWorkbench(var5.getLocation(), true);
            return true;
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can open a crafting table.");
         return true;
      }
   }
}
