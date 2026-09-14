package com.h2ph.J.A;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class F implements CommandExecutor, TabCompleter {
   private static final String B = "economysmpcore.invsee.use";
   private final PrismSurvival A;

   public F(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("economysmpcore.invsee.use")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
            return true;
         } else if (var4.length < 1) {
            String var8 = String.valueOf(ChatColor.RED);
            var5.sendMessage(var8 + "Usage: /" + var3 + " <player>");
            return true;
         } else {
            Player var6 = Bukkit.getPlayer(var4[0]);
            if (var6 == null) {
               String var7 = String.valueOf(ChatColor.RED);
               var5.sendMessage(var7 + "That player isn't online. " + String.valueOf(ChatColor.GRAY) + "/invsee only works on players currently on the server.");
               return true;
            } else if (var6.getUniqueId().equals(var5.getUniqueId())) {
               var5.sendMessage(String.valueOf(ChatColor.RED) + "You can't invsee yourself — just open your own inventory.");
               return true;
            } else {
               var5.openInventory(var6.getInventory());
               String var10001 = String.valueOf(ChatColor.GREEN);
               var5.sendMessage(var10001 + "Viewing " + var6.getName() + "'s inventory.");
               return true;
            }
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         if (var1 instanceof Player) {
            Player var5 = (Player)var1;
            if (var5.hasPermission("economysmpcore.invsee.use")) {
               ArrayList var6 = new ArrayList();

               for(Player var8 : Bukkit.getOnlinePlayers()) {
                  if (!var8.getUniqueId().equals(var5.getUniqueId())) {
                     var6.add(var8.getName());
                  }
               }

               return (List)var6.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
            }
         }

         return Collections.emptyList();
      } else {
         return Collections.emptyList();
      }
   }
}
