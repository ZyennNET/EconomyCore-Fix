package com.h2ph.J.B.F;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.SpawnManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class B implements TabExecutor {
   private final PrismSurvival A;

   public B(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage("Only players can use this command.");
         return true;
      } else if (!var5.hasPermission("economysmpcore.admin.setspawn")) {
         var5.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to do that.");
         return true;
      } else if (var4.length < 1) {
         var5.sendMessage(String.valueOf(ChatColor.YELLOW) + "Usage: /setspawn <name> OR /setspawn delete <name>");
         return true;
      } else {
         SpawnManager var6 = this.A.getSpawnManager();
         if (var6 == null) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "SpawnManager is not initialized.");
            return true;
         } else if (var4.length < 2 || !var4[0].equalsIgnoreCase("delete") && !var4[0].equalsIgnoreCase("del")) {
            String var9 = var4[0];
            boolean var10 = var6.saveSpawn(var9, var5.getLocation());
            if (var10) {
               String var12 = String.valueOf(ChatColor.GREEN);
               var5.sendMessage(var12 + "Saved spawn '" + var9 + "'.");
            } else {
               var5.sendMessage(String.valueOf(ChatColor.RED) + "Failed to save spawn. Check server logs.");
            }

            return true;
         } else {
            String var7 = var4[1];
            boolean var8 = var6.deleteSpawn(var7);
            if (var8) {
               String var10001 = String.valueOf(ChatColor.GREEN);
               var5.sendMessage(var10001 + "Deleted spawn '" + var7 + "'.");
            } else {
               String var11 = String.valueOf(ChatColor.RED);
               var5.sendMessage(var11 + "Spawn '" + var7 + "' not found or could not be deleted.");
            }

            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         String var11 = var4[0].toLowerCase();
         ArrayList var12 = new ArrayList();
         if ("delete".startsWith(var11)) {
            var12.add("delete");
         }

         return var12;
      } else if (var4.length == 2 && (var4[0].equalsIgnoreCase("delete") || var4[0].equalsIgnoreCase("del"))) {
         String var5 = var4[1].toLowerCase();
         ArrayList var6 = new ArrayList();

         try {
            if (this.A.getSpawnManager() != null) {
               var6.addAll(this.A.getSpawnManager().listSpawns());
            }
         } catch (Throwable var10) {
         }

         ArrayList var7 = new ArrayList();

         for(String var9 : var6) {
            if (var9.toLowerCase().startsWith(var5)) {
               var7.add(var9);
            }
         }

         return var7;
      } else {
         return Collections.emptyList();
      }
   }
}
