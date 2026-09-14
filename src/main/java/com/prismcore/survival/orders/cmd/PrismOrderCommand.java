package com.prismcore.survival.orders.cmd;

import com.prismcore.survival.orders.PrismOrders;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class PrismOrderCommand implements CommandExecutor, TabCompleter {
   private final PrismOrders pl;

   public PrismOrderCommand(PrismOrders var1) {
      this.pl = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      var1.sendMessage(String.valueOf(ChatColor.RED) + "This command has been disabled.");
      return true;
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      return new ArrayList();
   }
}
