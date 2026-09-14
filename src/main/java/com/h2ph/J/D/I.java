package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class I implements CommandExecutor {
   private final PrismSurvival A;

   public I(PrismSurvival var1) {
      this.A = var1;
      var1.getCommand("discord").setExecutor(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      List var5 = this.A.getConfig().getStringList("discord");
      if (var5.isEmpty()) {
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fJoin our discord: &ahttps://discord.gg/fmc"));
         return true;
      } else {
         for(String var7 : var5) {
            var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var7));
         }

         return true;
      }
   }
}
