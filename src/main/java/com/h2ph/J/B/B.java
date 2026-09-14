package com.h2ph.J.B;

import com.h2ph.PrismSurvival;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class B implements CommandExecutor, TabCompleter {
   private final PrismSurvival B;
   private final com.h2ph.X.A A;

   public B(PrismSurvival var1, com.h2ph.X.A var2) {
      this.B = var1;
      this.A = var2;
      var1.getCommand("tab").setExecutor(this);
      var1.getCommand("tab").setTabCompleter(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("economysmpcore.tab.admin")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "No permission.");
         return true;
      } else if (var4.length == 0) {
         this.B(var1);
         return true;
      } else {
         switch (var4[0].toLowerCase()) {
            case "reload":
               this.A.reloadTabList();
               var1.sendMessage(String.valueOf(ChatColor.GREEN) + "TabList config reloaded.");
               break;
            case "refresh":
               this.A.refreshTabListSorting();
               var1.sendMessage(String.valueOf(ChatColor.GREEN) + "TabList sorting refreshed.");
               break;
            case "rankings":
               this.A(var1);
               break;
            case "setranking":
               if (var4.length < 3) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /tab setranking <group> <ranking>");
                  return true;
               }

               this.A(var1, var4[1], var4[2]);
               break;
            case "toggle":
               boolean var7 = !this.A.isGroupSortingEnabled();
               this.A.setGroupSortingEnabled(var7);
               String var10001 = String.valueOf(ChatColor.YELLOW);
               var1.sendMessage(var10001 + "Group sorting " + (var7 ? "enabled" : "disabled") + ".");
               break;
            case "info":
               this.C(var1);
               break;
            default:
               this.B(var1);
         }

         return true;
      }
   }

   private void B(CommandSender var1) {
      var1.sendMessage(String.valueOf(ChatColor.GOLD) + "=== TabList Management ===");
      String var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "/tab reload" + String.valueOf(ChatColor.WHITE) + " - Reload config");
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "/tab refresh" + String.valueOf(ChatColor.WHITE) + " - Refresh sorting");
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "/tab rankings" + String.valueOf(ChatColor.WHITE) + " - Show group rankings");
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "/tab setranking <group> <ranking>" + String.valueOf(ChatColor.WHITE) + " - Set ranking");
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "/tab toggle" + String.valueOf(ChatColor.WHITE) + " - Toggle sorting");
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "/tab info" + String.valueOf(ChatColor.WHITE) + " - Show info");
   }

   private void A(CommandSender var1) {
      Map var2 = this.A.getGroupRankings();
      if (var2.isEmpty()) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "No rankings configured.");
      } else {
         var1.sendMessage(String.valueOf(ChatColor.GOLD) + "=== Group Rankings ===");
         var2.entrySet().stream().sorted(Entry.comparingByValue().reversed()).forEach((var1x) -> {
            String var10001 = String.valueOf(ChatColor.YELLOW);
            var1.sendMessage(var10001 + (String)var1x.getKey() + String.valueOf(ChatColor.WHITE) + ": " + String.valueOf(ChatColor.GREEN) + String.valueOf(var1x.getValue()));
         });
      }
   }

   private void A(CommandSender var1, String var2, String var3) {
      try {
         int var4 = Integer.parseInt(var3);
         this.A.setGroupRanking(var2, var4);
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Set ranking for group '" + var2 + "' to " + var4);
      } catch (NumberFormatException var5) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid number.");
      }

   }

   private void C(CommandSender var1) {
      var1.sendMessage(String.valueOf(ChatColor.GOLD) + "=== TabList Info ===");
      String var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "Group sorting: " + (this.A.isGroupSortingEnabled() ? String.valueOf(ChatColor.GREEN) + "enabled" : String.valueOf(ChatColor.RED) + "disabled"));
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "Configured groups: " + String.valueOf(ChatColor.WHITE) + this.A.getGroupRankings().size());
      var10001 = String.valueOf(ChatColor.YELLOW);
      var1.sendMessage(var10001 + "Online players: " + String.valueOf(ChatColor.WHITE) + this.B.getServer().getOnlinePlayers().size());
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         return Arrays.asList("reload", "refresh", "rankings", "setranking", "toggle", "info");
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("setranking")) {
         return Arrays.asList("owner", "admin", "mod", "vip", "default");
      } else {
         return var4.length == 3 && var4[0].equalsIgnoreCase("setranking") ? Arrays.asList("100", "90", "80", "70", "60", "50", "40", "30", "20", "10") : Collections.emptyList();
      }
   }
}
