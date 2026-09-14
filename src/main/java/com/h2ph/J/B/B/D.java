package com.h2ph.J.B.B;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class D implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public D(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!(var1 instanceof Player var5)) {
         if (var4.length >= 3 && var1.hasPermission("prismcore.admin.shards")) {
            return this.A(var1, var4);
         } else {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can check shard balances.");
            return true;
         }
      } else if (var4.length == 0) {
         this.A(var5);
         return true;
      } else if (var4.length == 1) {
         String var6 = var4[0];
         if (!var6.equalsIgnoreCase("give") && !var6.equalsIgnoreCase("set") && !var6.equalsIgnoreCase("remove")) {
            this.A(var5, var6);
            return true;
         } else {
            this.A(var5);
            return true;
         }
      } else if (var4[0].equalsIgnoreCase("pay")) {
         return this.A(var5, var4);
      } else if (var1.hasPermission("prismcore.admin.shards")) {
         return this.A(var1, var4);
      } else {
         this.A(var5);
         return true;
      }
   }

   private void A(Player var1) {
      PlayerData var2 = this.A.getPlayerDataManager().get(var1.getUniqueId());
      int var3 = (int)var2.getShards();
      String var4 = this.A(var3);
      String var10000 = String.valueOf(ChatColor.GRAY);
      String var5 = var10000 + "Your shards: " + String.valueOf(ChatColor.DARK_PURPLE) + var4;
      var1.sendMessage(var5);
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var5));
   }

   private void A(Player var1, String var2) {
      Player var3 = Bukkit.getPlayer(var2);
      if (var3 != null) {
         this.B((Player)var1, (OfflinePlayer)var3);
      } else if (!var2.matches("[a-zA-Z0-9_]{3,16}")) {
         String var4 = String.valueOf(ChatColor.RED) + "That user does not exist.";
         var1.sendMessage(var4);
         this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
      } else {
         this.A.getSchedulerAdapter().runTaskAsync(() -> {
            try {
               OfflinePlayer var3 = Bukkit.getOfflinePlayer(var2);
               this.A.getSchedulerAdapter().runTask(() -> this.B(var1, var3));
            } catch (Exception var4) {
               this.A.getSchedulerAdapter().runTask(() -> {
                  String var2 = String.valueOf(ChatColor.RED) + "That user does not exist.";
                  var1.sendMessage(var2);
                  this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
               });
            }

         });
      }
   }

   private void B(Player var1, OfflinePlayer var2) {
      if (!var2.hasPlayedBefore() && !var2.isOnline()) {
         String var7 = String.valueOf(ChatColor.RED) + "That user does not exist.";
         var1.sendMessage(var7);
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var7));
         this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
      } else {
         PlayerData var3 = this.A.getPlayerDataManager().get(var2.getUniqueId());
         int var4 = (int)var3.getShards();
         String var5 = this.A(var4);
         String var10000 = String.valueOf(ChatColor.GRAY);
         String var6 = var10000 + var2.getName() + "'s shards: " + String.valueOf(ChatColor.DARK_PURPLE) + var5;
         var1.sendMessage(var6);
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var6));
      }
   }

   private String A(int var1) {
      if (var1 >= 1000000) {
         int var3 = var1 / 1000000;
         return var3 + "m";
      } else if (var1 >= 1000) {
         int var2 = var1 / 1000;
         return var2 + "k";
      } else {
         return String.valueOf(var1);
      }
   }

   private boolean A(Player var1, String[] var2) {
      if (var2.length < 3) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /shards pay <gamertag> <amount>");
         this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
         return true;
      } else {
         String var3 = var2[1];
         String var4 = var2[2];
         if (var1.getName().equalsIgnoreCase(var3)) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You cannot pay yourself!");
            this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
            return true;
         } else {
            int var5;
            try {
               var5 = this.A(var4);
            } catch (NumberFormatException var8) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount! Use numbers or suffixes (k, m, b, t). Example: 10k, 100m, 1t");
               this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
               return true;
            }

            if (var5 <= 0) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Amount must be positive!");
               this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
               return true;
            } else if (var3.length() >= 3 && var3.length() <= 16) {
               Player var9 = Bukkit.getPlayer(var3);
               if (var9 != null) {
                  this.A((Player)var1, (OfflinePlayer)var9, var5);
                  return true;
               } else if (!var3.matches("[a-zA-Z0-9_]{3,16}")) {
                  String var7 = String.valueOf(ChatColor.RED) + "That player does not exist.";
                  var1.sendMessage(var7);
                  this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
                  return true;
               } else {
                  this.A.getSchedulerAdapter().runTaskAsync(() -> {
                     try {
                        OfflinePlayer var4 = Bukkit.getOfflinePlayer(var3);
                        this.A.getSchedulerAdapter().runTask(() -> this.A(var1, var4, var5));
                     } catch (Exception var5x) {
                        this.A.getSchedulerAdapter().runTask(() -> {
                           String var2 = String.valueOf(ChatColor.RED) + "That player does not exist.";
                           var1.sendMessage(var2);
                           this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
                        });
                     }

                  });
                  return true;
               }
            } else {
               String var6 = String.valueOf(ChatColor.RED) + "That player does not exist.";
               var1.sendMessage(var6);
               this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
               return true;
            }
         }
      }
   }

   private void A(Player var1, OfflinePlayer var2, int var3) {
      if (!var2.hasPlayedBefore() && !var2.isOnline()) {
         String var10 = String.valueOf(ChatColor.RED) + "That player does not exist.";
         var1.sendMessage(var10);
         this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
      } else {
         PlayerData var4 = this.A.getPlayerDataManager().get(var1.getUniqueId());
         double var5 = var4.getShards();
         if (var5 < (double)var3) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have enough shards!");
            this.A((CommandSender)var1, (Sound)Sound.ENTITY_VILLAGER_NO);
         } else {
            PlayerData var7 = this.A.getPlayerDataManager().get(var2.getUniqueId());
            var4.setShards(var5 - (double)var3);
            var7.setShards(var7.getShards() + (double)var3);
            this.A.getPlayerDataManager().savePlayer(var1.getUniqueId());
            this.A.getPlayerDataManager().savePlayer(var2.getUniqueId());
            String var10001 = String.valueOf(ChatColor.GRAY);
            var1.sendMessage(var10001 + "You paid " + var2.getName() + " " + String.valueOf(ChatColor.DARK_PURPLE) + this.A(var3) + " shards");
            this.A((CommandSender)var1, (Sound)Sound.BLOCK_AMETHYST_BLOCK_CHIME);
            if (var2.isOnline()) {
               Player var8 = var2.getPlayer();
               if (var8 != null) {
                  String var10000 = String.valueOf(ChatColor.DARK_PURPLE);
                  String var9 = var10000 + var1.getName() + String.valueOf(ChatColor.GRAY) + " paid you " + String.valueOf(ChatColor.DARK_PURPLE) + this.A(var3);
                  var8.sendMessage(var9);
                  var8.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var9));
                  this.A((CommandSender)var8, (Sound)Sound.BLOCK_AMETHYST_BLOCK_PLACE);
               }
            }

         }
      }
   }

   private boolean A(CommandSender var1, String[] var2) {
      if (var2.length < 3) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /shards <give|set|remove> <player> <amount>");
         this.A(var1, Sound.ENTITY_VILLAGER_NO);
         return true;
      } else {
         String var3 = var2[0].toLowerCase();
         String var4 = var2[1];
         String var5 = var2[2];

         int var6;
         try {
            var6 = this.A(var5);
         } catch (NumberFormatException var9) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount! Use numbers or suffixes (k, m, b, t). Example: 10k, 100m, 1t");
            this.A(var1, Sound.ENTITY_VILLAGER_NO);
            return true;
         }

         if (!var3.equals("give") && !var3.equals("set") && !var3.equals("remove")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid action! Use: give, set, or remove");
            this.A(var1, Sound.ENTITY_VILLAGER_NO);
            return true;
         } else {
            Player var7 = Bukkit.getPlayer(var4);
            if (var7 != null) {
               this.A((CommandSender)var1, (OfflinePlayer)var7, var3, var6);
               return true;
            } else if (!var4.matches("[a-zA-Z0-9_]{3,16}")) {
               String var8 = String.valueOf(ChatColor.RED) + "That user does not exist.";
               var1.sendMessage(var8);
               this.A(var1, Sound.ENTITY_VILLAGER_NO);
               return true;
            } else {
               this.A.getSchedulerAdapter().runTaskAsync(() -> {
                  try {
                     OfflinePlayer var5 = Bukkit.getOfflinePlayer(var4);
                     this.A.getSchedulerAdapter().runTask(() -> this.A(var1, var5, var3, var6));
                  } catch (Exception var6x) {
                     this.A.getSchedulerAdapter().runTask(() -> {
                        String var2 = String.valueOf(ChatColor.RED) + "That user does not exist.";
                        var1.sendMessage(var2);
                        this.A(var1, Sound.ENTITY_VILLAGER_NO);
                     });
                  }

               });
               return true;
            }
         }
      }
   }

   private void A(CommandSender var1, OfflinePlayer var2, String var3, int var4) {
      if (!var2.hasPlayedBefore() && !var2.isOnline()) {
         String var13 = String.valueOf(ChatColor.RED) + "That user does not exist.";
         var1.sendMessage(var13);
         if (var1 instanceof Player) {
            ((Player)var1).spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var13));
         }

         this.A(var1, Sound.ENTITY_VILLAGER_NO);
      } else {
         PlayerData var5 = this.A.getPlayerDataManager().get(var2.getUniqueId());
         double var6 = var5.getShards();
         double var8 = var6;
         switch (var3) {
            case "give":
               var8 = var6 + (double)var4;
               var5.setShards(var8);
               String var15 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var15 + "Gave " + String.valueOf(ChatColor.GOLD) + var4 + String.valueOf(ChatColor.GREEN) + " shards to " + String.valueOf(ChatColor.YELLOW) + var2.getName() + String.valueOf(ChatColor.GREEN) + ". New balance: " + String.valueOf(ChatColor.GOLD) + (int)var8);
               break;
            case "set":
               var8 = (double)var4;
               var5.setShards(var8);
               String var14 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var14 + "Set " + String.valueOf(ChatColor.YELLOW) + var2.getName() + String.valueOf(ChatColor.GREEN) + "'s shards to " + String.valueOf(ChatColor.GOLD) + var4);
               break;
            case "remove":
               var8 = Math.max((double)0.0F, var6 - (double)var4);
               var5.setShards(var8);
               int var12 = (int)(var6 - var8);
               String var10001 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var10001 + "Removed " + String.valueOf(ChatColor.GOLD) + var12 + String.valueOf(ChatColor.GREEN) + " shards from " + String.valueOf(ChatColor.YELLOW) + var2.getName() + String.valueOf(ChatColor.GREEN) + ". New balance: " + String.valueOf(ChatColor.GOLD) + (int)var8);
         }

         this.A.getPlayerDataManager().savePlayer(var2.getUniqueId());
         this.A(var1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
         if (var2.isOnline()) {
            Player var10 = var2.getPlayer();
            if (var10 != null) {
               String var16 = String.valueOf(ChatColor.GRAY);
               var10.sendMessage(var16 + "Your shard balance has been updated to " + String.valueOf(ChatColor.DARK_PURPLE) + (int)var8 + " shards");
            }
         }

      }
   }

   private void A(CommandSender var1, Sound var2) {
      if (var1 instanceof Player var3) {
         try {
            var3.playSound(var3.getLocation(), var2, 1.0F, 1.0F);
         } catch (Exception var5) {
         }
      }

   }

   private int A(String var1) throws NumberFormatException {
      var1 = var1.toLowerCase().trim();
      if (var1.isEmpty()) {
         throw new NumberFormatException("Empty amount");
      } else {
         char var2 = var1.charAt(var1.length() - 1);
         int var3 = 1;
         String var4 = var1;
         if (Character.isLetter(var2)) {
            var4 = var1.substring(0, var1.length() - 1);
            switch (var2) {
               case 'b':
                  var3 = 1000000000;
                  break;
               case 'k':
                  var3 = 1000;
                  break;
               case 'm':
                  var3 = 1000000;
                  break;
               case 't':
                  double var5 = Double.parseDouble(var4);
                  long var7 = (long)(var5 * 1.0E12);
                  if (var7 > 2147483647L) {
                     return Integer.MAX_VALUE;
                  }

                  return (int)var7;
               default:
                  throw new NumberFormatException("Invalid suffix: " + var2);
            }
         }

         double var10 = Double.parseDouble(var4);
         long var11 = (long)(var10 * (double)var3);
         if (var11 > 2147483647L) {
            return Integer.MAX_VALUE;
         } else {
            return var11 < 0L ? 0 : (int)var11;
         }
      }
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      ArrayList var5 = new ArrayList();
      if (var4.length == 1) {
         ArrayList var9 = new ArrayList();
         var9.add("pay");
         if (var1.hasPermission("prismcore.admin.shards")) {
            var9.add("give");
            var9.add("set");
            var9.add("remove");
         }

         return (List)var9.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      } else {
         if (var4[0].equalsIgnoreCase("pay")) {
            if (var4.length == 2) {
               return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> !var1x.equals(var1.getName())).filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
            }

            if (var4.length == 3) {
               List var8 = Arrays.asList("10", "50", "100", "500", "1000", "1k", "10k", "100k", "1m");
               return (List)var8.stream().filter((var1x) -> var1x.startsWith(var4[2].toLowerCase())).collect(Collectors.toList());
            }
         }

         if (var1.hasPermission("prismcore.admin.shards")) {
            String var6 = var4[0].toLowerCase();
            if (var6.equals("give") || var6.equals("set") || var6.equals("remove")) {
               if (var4.length == 2) {
                  return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
               }

               if (var4.length == 3) {
                  List var7 = Arrays.asList("10", "50", "100", "500", "1000", "1k", "10k", "100k", "1m");
                  return (List)var7.stream().filter((var1x) -> var1x.startsWith(var4[2].toLowerCase())).collect(Collectors.toList());
               }
            }
         }

         return var5;
      }
   }
}
