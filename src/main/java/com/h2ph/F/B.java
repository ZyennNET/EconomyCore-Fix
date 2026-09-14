package com.h2ph.F;

import com.h2ph.PrismSurvival;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class B implements CommandExecutor {
   private final PrismSurvival A;

   public B(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("economysmpcore.clearlag.admin")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
         return true;
      } else if (var4.length == 1 && var4[0].equalsIgnoreCase("reload")) {
         this.A.getClearLagManager().T();
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "ClearLag configuration reloaded.");
         return true;
      } else {
         int var5 = this.A.getClearLagManager().E();
         String var10001 = String.valueOf(ChatColor.GREEN);
         var1.sendMessage(var10001 + "Manually cleared " + var5 + " entities.");
         return true;
      }
   }
}
