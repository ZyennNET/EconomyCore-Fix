package com.h2ph.J.D;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class U implements CommandExecutor {
   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("economysmpcore.anvil")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission.");
            return true;
         } else {
            var5.openAnvil(var5.getLocation(), true);
            return true;
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can open an anvil.");
         return true;
      }
   }
}
