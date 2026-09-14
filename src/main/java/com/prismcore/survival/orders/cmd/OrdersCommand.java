package com.prismcore.survival.orders.cmd;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.gui.OrdersMainMenu;
import com.prismcore.survival.orders.store.PlayerStateManager;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class OrdersCommand implements CommandExecutor, TabCompleter {
   private final PrismOrders plugin;

   public OrdersCommand(PrismOrders var1) {
      this.plugin = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage("This command can only be used by players.");
         return true;
      } else {
         PlayerStateManager.View var6 = this.plugin.state().main(var5.getUniqueId());
         if (var4.length > 0) {
            String var7 = String.join(" ", var4).trim();
            var6.search = var7.isEmpty() ? null : var7.toLowerCase();
            var6.page = 0;
         }

         this.plugin.orders().reload(() -> (new OrdersMainMenu(this.plugin, var5)).open());
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      return Collections.emptyList();
   }
}
