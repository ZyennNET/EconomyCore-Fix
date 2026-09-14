package com.h2ph.J.D;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class O implements CommandExecutor {
   private final com.h2ph.N.B A;

   public O(com.h2ph.N.B var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else {
         this.A.D(var5);
         return true;
      }
   }
}
