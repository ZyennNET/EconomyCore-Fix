package com.h2ph.A;

import com.h2ph.PrismSurvival;
import java.util.Arrays;
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

public class C implements CommandExecutor, TabCompleter {
   private final PrismSurvival B;
   private final A A;

   public C(PrismSurvival var1, A var2) {
      this.B = var1;
      this.A = var2;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage("Only players can use this command.");
         return true;
      } else {
         Player var5 = (Player)var1;
         switch (var2.getName().toLowerCase()) {
            case "msg":
            case "message":
            case "whisper":
            case "tell":
            case "dm":
            case "w":
               if (var4.length < 2) {
                  var5.sendMessage(this.A.A());
                  return true;
               } else {
                  Player var9 = Bukkit.getPlayer(var4[0]);
                  if (var9 == null) {
                     var5.sendMessage(this.A.C());
                     return true;
                  }

                  String var10 = String.join(" ", (CharSequence[])Arrays.copyOfRange(var4, 1, var4.length));
                  this.A.A(var5, var9, var10);
                  return true;
               }
            case "reply":
            case "r":
               if (var4.length < 1) {
                  var5.sendMessage(this.A.B());
                  return true;
               }

               String var11 = String.join(" ", var4);
               this.A.B(var5, var11);
               return true;
            case "block":
            case "ignore":
               if (var4.length < 1) {
                  String var17 = String.valueOf(ChatColor.RED);
                  var5.sendMessage(var17 + "Usage: /" + var3 + " <player>");
                  return true;
               } else {
                  String var12 = var4[0];
                  if (this.A.E(var5.getUniqueId()).contains(var12.toLowerCase())) {
                     var5.sendMessage(String.valueOf(ChatColor.RED) + "You already have this player blocked.");
                     return true;
                  }

                  this.A.B(var5.getUniqueId(), var12);
                  String var16 = String.valueOf(ChatColor.GREEN);
                  var5.sendMessage(var16 + "You blocked " + var12 + ".");
                  return true;
               }
            case "unblock":
            case "unignore":
               if (var4.length < 1) {
                  String var15 = String.valueOf(ChatColor.RED);
                  var5.sendMessage(var15 + "Usage: /" + var3 + " <player>");
                  return true;
               } else {
                  String var13 = var4[0];
                  if (!this.A.E(var5.getUniqueId()).contains(var13.toLowerCase())) {
                     var5.sendMessage(String.valueOf(ChatColor.RED) + "You have not blocked this player.");
                     return true;
                  }

                  this.A.A(var5.getUniqueId(), var13);
                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var5.sendMessage(var10001 + "You unblocked " + var13 + ".");
                  return true;
               }
            case "msgtoggle":
               boolean var14 = this.A.B(var5.getUniqueId());
               this.A.A(var5.getUniqueId(), !var14);
               if (!var14) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "You have disabled private messages.");
               } else {
                  var5.sendMessage(String.valueOf(ChatColor.GREEN) + "You have enabled private messages.");
               }

               return true;
            default:
               return false;
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         return Collections.emptyList();
      } else if (var4.length != 1) {
         return Collections.emptyList();
      } else {
         String var5 = var2.getName().toLowerCase();
         return !var5.equals("msg") && !var5.equals("message") && !var5.equals("whisper") && !var5.equals("tell") && !var5.equals("dm") && !var5.equals("w") && !var5.equals("block") && !var5.equals("ignore") && !var5.equals("unblock") && !var5.equals("unignore") ? Collections.emptyList() : (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      }
   }
}
