package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class G implements CommandExecutor, TabCompleter {
   private final PrismSurvival D;
   private final E A;
   private final B C;
   private final C B;
   private final F E;

   public G(PrismSurvival var1, B var2) {
      this.D = var1;
      this.C = var2;
      this.A = new E(var1, var2);
      this.B = new C(var1);
      this.E = new F(var1, this.B, var2);
      this.E.setRequestManager(this.A);
   }

   public E getRequestManager() {
      return this.A;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length == 0) {
         if (var1 instanceof Player) {
            Player var16 = (Player)var1;
            if (this.E.isInQueue(var16.getUniqueId())) {
               try {
                  var16.playSound(var16.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               } catch (Exception var13) {
               }

               return true;
            }

            this.E.openQueueGUI(var16);
         } else {
            this.A(var1);
         }

         return true;
      } else {
         String var5 = var4[0].toLowerCase();
         if (var5.equals("leave")) {
            if (!(var1 instanceof Player)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
               return true;
            } else {
               Player var20 = (Player)var1;
               if (this.C.I(var20)) {
                  String var25 = String.valueOf(ChatColor.GRAY) + "You forfeited the match.";
                  var1.sendMessage(var25);
                  var20.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var25));
                  this.C.G(var20);
                  var20.setHealth((double)0.0F);
                  return true;
               } else if (this.C.N(var20)) {
                  String var24 = String.valueOf(ChatColor.GRAY) + "You left the arena.";
                  var1.sendMessage(var24);
                  var20.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var24));
                  this.C.D(var20);
                  return true;
               } else {
                  String var23 = String.valueOf(ChatColor.RED) + "You are not in a duel.";
                  var1.sendMessage(var23);
                  var20.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var23));
                  return true;
               }
            }
         } else if (var5.equals("create")) {
            if (!var1.hasPermission("prismcore.admin.duel")) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
               return true;
            } else if (var4.length < 2) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /duel create <name>");
               return true;
            } else {
               this.A(var1, var4[1]);
               return true;
            }
         } else if (var5.equals("setspawn")) {
            if (!var1.hasPermission("prismcore.admin.duel")) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
               return true;
            } else if (!(var1 instanceof Player)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
               return true;
            } else {
               Player var19 = (Player)var1;
               this.C.A(var19.getLocation());
               var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Duel spawn set! Players will be teleported here after a duel ends or when they leave.");
               return true;
            }
         } else if (var5.equals("settings")) {
            if (var1.hasPermission("prismcore.admin.duel")) {
               this.B(var1);
            } else if (var1 instanceof Player) {
               this.E.openQueueGUI((Player)var1);
            } else {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
            }

            return true;
         } else if (var5.equals("queue")) {
            if (!(var1 instanceof Player)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
               return true;
            } else {
               Player var18 = (Player)var1;
               this.E.openQueueGUI(var18);
               return true;
            }
         } else if (var5.equals("cancel")) {
            if (!(var1 instanceof Player)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
               return true;
            } else {
               this.A.B((Player)var1);
               return true;
            }
         } else if (!var5.equals("accept") && !var5.equals("decline")) {
            Player var17 = Bukkit.getPlayer(var5);
            if (var17 != null) {
               if (!(var1 instanceof Player)) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can request duels.");
                  return true;
               } else {
                  Player var22 = (Player)var1;
                  if (this.E.isInQueue(var22.getUniqueId())) {
                     this.E.leaveQueue(var22);
                     var22.sendMessage(String.valueOf(ChatColor.YELLOW) + "You left the duel queue to send a request.");
                  }

                  A var26 = new A(this.D, this.A, var22, var17);
                  this.D.getServer().getPluginManager().registerEvents(var26, this.D);
                  var26.open();
                  return true;
               }
            } else if (!(var1 instanceof Player)) {
               if (var1 instanceof Player) {
                  this.E.openQueueGUI((Player)var1);
               } else {
                  this.A(var1);
               }

               return true;
            } else {
               Player var21 = (Player)var1;
               OfflinePlayer var8 = null;

               for(OfflinePlayer var12 : Bukkit.getOfflinePlayers()) {
                  if (var12.getName() != null && var12.getName().equalsIgnoreCase(var5)) {
                     var8 = var12;
                     break;
                  }
               }

               if (var8 != null) {
                  String var28 = String.valueOf(ChatColor.RED) + "This user is not online.";
                  var1.sendMessage(var28);
                  var21.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var28));

                  try {
                     var21.playSound(var21.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  } catch (Exception var14) {
                  }

                  return true;
               } else {
                  String var27 = String.valueOf(ChatColor.RED) + "That user does not exist.";
                  var1.sendMessage(var27);
                  var21.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var27));

                  try {
                     var21.playSound(var21.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  } catch (Exception var15) {
                  }

                  return true;
               }
            }
         } else if (!(var1 instanceof Player)) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
            return true;
         } else {
            Player var6 = (Player)var1;
            String var7 = null;
            if (var4.length > 1) {
               var7 = var4[1];
            }

            if (var5.equals("accept")) {
               this.A.A(var6, var7);
            } else {
               this.A.C(var6, var7);
            }

            return true;
         }
      }
   }

   private void A(CommandSender var1) {
      var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage:");
      var1.sendMessage(String.valueOf(ChatColor.RED) + "/duel <player>");
      var1.sendMessage(String.valueOf(ChatColor.RED) + "/duel cancel");
      if (var1.hasPermission("prismcore.admin.duel")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "/duel create <name>");
         var1.sendMessage(String.valueOf(ChatColor.RED) + "/duel settings");
         var1.sendMessage(String.valueOf(ChatColor.RED) + "/duel setspawn");
      }

   }

   private void A(CommandSender var1, String var2) {
      if (!(var1 instanceof Player var3)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can create duel regions.");
      } else {
         try {
            BukkitPlayer var4 = BukkitAdapter.adapt(var3);
            LocalSession var5 = WorldEdit.getInstance().getSessionManager().get(var4);
            Region var6 = var5.getSelection(var4.getWorld());
            if (var6 == null) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Please make a selection with WorldEdit first.");
               return;
            }

            BlockVector3 var7 = var6.getMinimumPoint();
            BlockVector3 var8 = var6.getMaximumPoint();
            String var9 = var3.getWorld().getName();
            int var10 = (var7.getX() + var8.getX()) / 2;
            int var11 = (var7.getY() + var8.getY()) / 2;
            int var12 = (var7.getZ() + var8.getZ()) / 2;
            Biome var13 = var3.getWorld().getBiome(var10, var11, var12);
            String var14 = var13.name().toLowerCase();
            char var10000 = Character.toUpperCase(var14.charAt(0));
            var14 = var10000 + var14.substring(1);
            this.C.A(var2, var9, var7, var8, var14, var3.getName());
            String var10001 = String.valueOf(ChatColor.GREEN);
            var1.sendMessage(var10001 + "Duel region " + String.valueOf(ChatColor.YELLOW) + var2 + String.valueOf(ChatColor.GREEN) + " saved successfully!");
         } catch (IncompleteRegionException var15) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Please make a complete selection (pos1 and pos2) first.");
         } catch (NoClassDefFoundError var16) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "WorldEdit is not installed or not working properly.");
         }

      }
   }

   private void B(CommandSender var1) {
      if (!(var1 instanceof Player var2)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can open settings GUI.");
      } else {
         D var3 = new D(this.D);
         var3.A(var2);
      }
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      ArrayList var5 = new ArrayList();
      if (var4.length == 1) {
         var5.add("queue");
         var5.add("cancel");
         var5.add("accept");
         var5.add("decline");
         var5.add("leave");
         if (var1.hasPermission("prismcore.admin.duel")) {
            var5.add("create");
            var5.add("settings");
            var5.add("setspawn");
         }

         var5.addAll((Collection)Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
         return (List)var5.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      } else {
         return var4.length != 2 || !var4[0].equalsIgnoreCase("accept") && !var4[0].equalsIgnoreCase("decline") ? Collections.emptyList() : null;
      }
   }
}
