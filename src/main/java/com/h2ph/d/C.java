package com.h2ph.D;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class C implements CommandExecutor {
   private final B A;

   public C(B var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("prismcore.maintenance")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
         return true;
      } else {
         boolean var5 = !this.A.C();
         this.A.A(var5);
         if (var5) {
            String var10001 = String.valueOf(ChatColor.GRAY);
            var1.sendMessage(var10001 + "Maintenance has been " + String.valueOf(ChatColor.GREEN) + "enabled" + String.valueOf(ChatColor.GRAY) + ".");
         } else {
            String var6 = String.valueOf(ChatColor.GRAY);
            var1.sendMessage(var6 + "Maintenance has been " + String.valueOf(ChatColor.RED) + "disabled" + String.valueOf(ChatColor.GRAY) + ".");
         }

         return true;
      }
   }
}
