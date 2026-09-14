package com.prismcore.survival.auction;

import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class AdminCommand implements CommandExecutor, TabCompleter {
   private final AuctionController controller;

   public AdminCommand(AuctionController var1) {
      this.controller = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("donutauction.reload")) {
         var1.sendMessage(Utils.formatColors("&#ff4444You do not have permission to use this command."));
         return true;
      } else if (var4.length == 1 && var4[0].equalsIgnoreCase("reload")) {
         Bukkit.getOnlinePlayers().forEach((var0) -> var0.closeInventory());
         this.controller.reloadAllConfigs();
         this.controller.getAuctionManager().saveToConfig();
         this.controller.getAuctionManager().loadFromConfig();
         String var5 = Utils.formatColors("&#44ff44Auction system reloaded.");
         var1.sendMessage(var5);
         return true;
      } else {
         var1.sendMessage(Utils.formatColors("&#ff4444Usage: /ah reload"));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      return var4.length == 1 && var1.hasPermission("donutauction.reload") ? Collections.singletonList("reload") : Collections.emptyList();
   }
}
