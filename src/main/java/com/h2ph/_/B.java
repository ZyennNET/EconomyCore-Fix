package com.h2ph._;

import com.h2ph.PrismSurvival;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class B implements CommandExecutor {
   private final PrismSurvival A;
   private final C B;

   public B(PrismSurvival var1, C var2) {
      this.A = var1;
      this.B = var2;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (var2.getName().equalsIgnoreCase("undisguise")) {
            if (!this.A.getDisguiseManager().B(var5.getUniqueId())) {
               var5.sendMessage(String.valueOf(ChatColor.RED) + "You aren't disguised.");
               return true;
            } else {
               this.A.getDisguiseManager().C(var5);
               var5.sendMessage(String.valueOf(ChatColor.GREEN) + "Disguise removed.");
               return true;
            }
         } else {
            this.B.open(var5, 0);
            return true;
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      }
   }
}
