package com.h2ph.J.B.C;

import com.h2ph.PrismSurvival;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class A implements CommandExecutor, TabCompleter {
   private final PrismSurvival B;
   private final com.h2ph.C.A A;

   public A(PrismSurvival var1) {
      this.B = var1;
      this.A = var1.getAfkManager();
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!var1.hasPermission("prismcore.admin.afk")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
         return true;
      } else if (var4.length < 2) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /setafk <confirm|delete> <region_name>");
         return true;
      } else {
         String var5 = var4[0].toLowerCase();
         String var6 = var4[1];
         if (var5.equals("delete")) {
            if (this.A.D(var6)) {
               String var19 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var19 + "Deleted AFK region: " + String.valueOf(ChatColor.YELLOW) + var6);
            } else {
               String var20 = String.valueOf(ChatColor.RED);
               var1.sendMessage(var20 + "Region not found: " + var6);
            }

            return true;
         } else if (var5.equals("confirm")) {
            if (!(var1 instanceof Player)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can confirm selections.");
               return true;
            } else {
               Player var7 = (Player)var1;

               try {
                  BukkitPlayer var8 = BukkitAdapter.adapt(var7);
                  LocalSession var9 = WorldEdit.getInstance().getSessionManager().get(var8);
                  Region var10 = var9.getSelection(var8.getWorld());
                  if (var10 == null) {
                     var1.sendMessage(String.valueOf(ChatColor.RED) + "Please make a selection with WorldEdit first.");
                     return true;
                  }

                  BlockVector3 var11 = var10.getMinimumPoint();
                  BlockVector3 var12 = var10.getMaximumPoint();
                  String var13 = var7.getWorld().getName();
                  Vector var14 = new Vector(var11.getX(), var11.getY(), var11.getZ());
                  Vector var15 = new Vector(var12.getX(), var12.getY(), var12.getZ());
                  this.A.A(var6, var13, var14, var15);
                  String var18 = String.valueOf(ChatColor.GREEN);
                  var1.sendMessage(var18 + "Created AFK region " + String.valueOf(ChatColor.YELLOW) + var6 + String.valueOf(ChatColor.GREEN) + " in world " + String.valueOf(ChatColor.AQUA) + var13);
               } catch (IncompleteRegionException var16) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Please make a complete selection (pos1 and pos2) first.");
               } catch (Exception var17) {
                  String var10001 = String.valueOf(ChatColor.RED);
                  var1.sendMessage(var10001 + "Error accessing WorldEdit selection: " + var17.getMessage());
                  var17.printStackTrace();
               }

               return true;
            }
         } else {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /setafk <confirm|delete> <region_name>");
            return true;
         }
      }
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!var1.hasPermission("prismcore.admin.afk")) {
         return Collections.emptyList();
      } else if (var4.length == 1) {
         return Arrays.asList("confirm", "delete");
      } else {
         return (List<String>)(var4.length == 2 && var4[0].equalsIgnoreCase("delete") ? new ArrayList(this.A.E()) : Collections.emptyList());
      }
   }
}
