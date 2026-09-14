package com.h2ph.H;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class A implements Listener, CommandExecutor, TabCompleter {
   private final PrismSurvival B;
   private _L C;
   private boolean A = false;

   public A(PrismSurvival var1) {
      this.B = var1;
      this.reload();
   }

   private String A(String var1, String var2) {
      return this.B.getConfig().getString("donutteam." + var1, var2);
   }

   private List<String> A(String var1, List<String> var2) {
      List var3 = this.B.getConfig().getStringList("donutteam." + var1);
      return var3.isEmpty() ? var2 : var3;
   }

   private String A(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   public void reload() {
      if (!this.B.getConfig().getBoolean("modules.team-system", true)) {
         if (this.A) {
            this.disable();
         }

         this.B.getLogger().info("DonutTeam module disabled by config.");
      } else if (!this.A) {
         this.C = new _L();
         this.C.A();
         PluginCommand var1 = this.B.getCommand("team");
         if (var1 != null) {
            var1.setExecutor(this);
            var1.setTabCompleter(this);
         } else {
            this.B.getLogger().warning("Command 'team' not found in plugin.yml – DonutTeam will not work.");
         }

         Bukkit.getPluginManager().registerEvents(this, this.B);
         Bukkit.getPluginManager().registerEvents(new _D(), this.B);
         Bukkit.getPluginManager().registerEvents(new _J(), this.B);
         Bukkit.getPluginManager().registerEvents(new _C(), this.B);
         Bukkit.getPluginManager().registerEvents(new _A(), this.B);
         if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            (new _I()).register();
            this.B.getLogger().info("DonutTeam PlaceholderAPI expansion registered.");
         }

         this.A = true;
         this.B.getLogger().info("DonutTeam module enabled.");
      }
   }

   public void disable() {
      if (this.C != null) {
         this.C.C();
      }

      this.A = false;
   }

   public _H getPlayerTeam(UUID var1) {
      return this.C.C(var1);
   }

   public String getTeamPlaceholderValue(UUID var1) {
      _H var2 = this.getPlayerTeam(var1);
      return var2 != null ? var2.D() : this.A("placeholder.no-team", "N/A");
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!this.A) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Team system is currently disabled.");
            return true;
         } else {
            _H var6 = this.C.C(var5.getUniqueId());
            if (var4.length == 0) {
               if (var6 == null) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team. Type /team create <name> to create one.");
                  return true;
               } else {
                  (new _G(var5)).open();
                  return true;
               }
            } else {
               switch (var4[0].toLowerCase()) {
                  case "create":
                     return this.A(var5, var4);
                  case "invite":
                     return this.C(var5, var4);
                  case "accept":
                     return this.D(var5);
                  case "decline":
                     return this.H(var5);
                  case "kick":
                     return this.B(var5, var4);
                  case "leave":
                     return this.F(var5);
                  case "delete":
                     return this.G(var5);
                  case "info":
                     return this.I(var5);
                  case "list":
                     return this.A(var5);
                  case "sethome":
                     return this.B(var5);
                  case "home":
                     return this.E(var5);
                  case "reload":
                     if (!var5.isOp()) {
                        var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
                        return true;
                     }

                     this.B.reloadConfig();
                     var5.sendMessage(this.A(this.A("messages.config-reloaded", "&aTeam config reloaded.")));
                     return true;
                  default:
                     this.C(var5);
                     return true;
               }
            }
         }
      } else {
         var1.sendMessage("Only players can use /team.");
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      ArrayList var5 = new ArrayList();
      if (var4.length == 1) {
         String[] var6 = new String[]{"create", "invite", "accept", "decline", "kick", "leave", "delete", "info", "list", "sethome", "home"};

         for(String var10 : var6) {
            if (var10.startsWith(var4[0].toLowerCase())) {
               var5.add(var10);
            }
         }

         if (var1.isOp() && "reload".startsWith(var4[0].toLowerCase())) {
            var5.add("reload");
         }
      } else if (var4.length == 2 && (var4[0].equalsIgnoreCase("invite") || var4[0].equalsIgnoreCase("kick"))) {
         for(Player var12 : Bukkit.getOnlinePlayers()) {
            if (var12.getName().toLowerCase().startsWith(var4[1].toLowerCase())) {
               var5.add(var12.getName());
            }
         }
      }

      return var5;
   }

   private boolean A(Player var1, String[] var2) {
      if (var2.length < 2) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /team create <name>");
         return true;
      } else if (this.C.F(var1.getUniqueId())) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You already have a team!");
         return true;
      } else {
         String var3 = String.join(" ", (CharSequence[])Arrays.copyOfRange(var2, 1, var2.length));
         int var4 = this.B.getConfig().getInt("donutteam.name.min-length", 3);
         int var5 = this.B.getConfig().getInt("donutteam.name.max-length", 16);
         if (var3.length() < var4) {
            var1.sendMessage(this.A(this.A("messages.name-too-short", "&cTeam name must be at least &f" + var4 + " &ccharacters.").replace("%min%", String.valueOf(var4))));
            return true;
         } else if (var3.length() > var5) {
            var1.sendMessage(this.A(this.A("messages.name-too-long", "&cTeam name must be at most &f" + var5 + " &ccharacters.").replace("%max%", String.valueOf(var5))));
            return true;
         } else {
            for(String var8 : this.B.getConfig().getStringList("donutteam.name.restricted")) {
               if (var3.equalsIgnoreCase(var8)) {
                  var1.sendMessage(this.A(this.A("messages.name-restricted", "&cThat team name is not allowed.")));
                  return true;
               }
            }

            if (this.C.A(var3)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "A team with that name already exists!");
               return true;
            } else {
               _H var9 = this.C.A(var3, var1);
               if (var9 != null) {
                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var1.sendMessage(var10001 + "Team '" + var3 + "' created!");
               } else {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Failed to create team.");
               }

               return true;
            }
         }
      }
   }

   private boolean C(Player var1, String[] var2) {
      if (var2.length < 2) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /team invite <player>");
         return true;
      } else {
         _H var3 = this.C.C(var1.getUniqueId());
         if (var3 == null) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
            return true;
         } else if (!var3.A(var1.getUniqueId(), "invite")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to invite!");
            return true;
         } else {
            Player var4 = Bukkit.getPlayer(var2[1]);
            if (var4 == null) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Player not found!");
               return true;
            } else if (this.C.F(var4.getUniqueId())) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "That player already has a team!");
               return true;
            } else {
               this.C.B(var3.B(), var4.getUniqueId());
               String var10001 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var10001 + "Invited " + var4.getName() + "!");
               var10001 = String.valueOf(ChatColor.GRAY);
               var4.sendMessage(var10001 + "You have been invited to team '" + var3.D() + "'. Type /team accept to join.");
               return true;
            }
         }
      }
   }

   private boolean D(Player var1) {
      UUID var2 = this.C.G(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You have no pending invites!");
         return true;
      } else {
         _H var3 = this.C.A(var2);
         if (var3 == null) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "That team no longer exists!");
            this.C.E(var1.getUniqueId());
            return true;
         } else {
            this.C.A(var1.getUniqueId(), var2, A._F.F);
            String var10001 = String.valueOf(ChatColor.GREEN);
            var1.sendMessage(var10001 + "You joined team '" + var3.D() + "'!");

            for(UUID var5 : var3.A()) {
               Player var6 = Bukkit.getPlayer(var5);
               if (var6 != null && !var6.equals(var1)) {
                  var10001 = String.valueOf(ChatColor.GRAY);
                  var6.sendMessage(var10001 + var1.getName() + " joined the team!");
               }
            }

            return true;
         }
      }
   }

   private boolean H(Player var1) {
      UUID var2 = this.C.G(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You have no pending invites!");
         return true;
      } else {
         this.C.E(var1.getUniqueId());
         var1.sendMessage(String.valueOf(ChatColor.GRAY) + "You declined the invite.");
         return true;
      }
   }

   private boolean B(Player var1, String[] var2) {
      if (var2.length < 2) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /team kick <player>");
         return true;
      } else {
         _H var3 = this.C.C(var1.getUniqueId());
         if (var3 == null) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
            return true;
         } else if (!var3.A(var1.getUniqueId(), "kick")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to kick!");
            return true;
         } else {
            Player var4 = Bukkit.getPlayer(var2[1]);
            if (var4 == null) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Player not found!");
               return true;
            } else if (!var3.B(var4.getUniqueId())) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "That player is not in your team!");
               return true;
            } else if (var3.J().equals(var4.getUniqueId())) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "You cannot kick the owner!");
               return true;
            } else {
               this.C.A(var4.getUniqueId(), var3.B());
               String var10001 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var10001 + "Kicked " + var4.getName() + "!");
               var10001 = String.valueOf(ChatColor.RED);
               var4.sendMessage(var10001 + "You were kicked from team '" + var3.D() + "'!");
               return true;
            }
         }
      }
   }

   private boolean F(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
         return true;
      } else if (var2.J().equals(var1.getUniqueId())) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You are the owner. Use /team delete to disband the team.");
         return true;
      } else {
         (new _K(var1, A._K._A.A)).open();
         return true;
      }
   }

   void doLeave(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 != null) {
         ArrayList var3 = new ArrayList(var2.A());
         this.C.B(var1.getUniqueId());
         var1.sendMessage(this.A(this.A("messages.left-team", "&7You left the team.")));

         for(UUID var5 : var3) {
            Player var6 = Bukkit.getPlayer(var5);
            if (var6 != null && !var6.equals(var1)) {
               var6.sendMessage(this.A(this.A("messages.member-left", "&7%player% left the team.").replace("%player%", var1.getName())));
            }
         }

      }
   }

   private boolean G(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
         return true;
      } else if (!var2.J().equals(var1.getUniqueId())) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only the owner can delete the team!");
         return true;
      } else {
         (new _K(var1, A._K._A.C)).open();
         return true;
      }
   }

   void doDelete(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 != null) {
         ArrayList var3 = new ArrayList(var2.A());
         this.C.D(var2.B());
         var1.sendMessage(this.A(this.A("messages.team-deleted", "&aTeam deleted!")));

         for(UUID var5 : var3) {
            Player var6 = Bukkit.getPlayer(var5);
            if (var6 != null && !var6.equals(var1)) {
               var6.sendMessage(this.A(this.A("messages.team-disbanded", "&cYour team has been disbanded!")));
            }
         }

      }
   }

   private boolean I(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
         return true;
      } else {
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "=== Team Info ===");
         String var10001 = String.valueOf(ChatColor.GRAY);
         var1.sendMessage(var10001 + "Name: " + String.valueOf(ChatColor.WHITE) + var2.D());
         var10001 = String.valueOf(ChatColor.GRAY);
         var1.sendMessage(var10001 + "Owner: " + String.valueOf(ChatColor.WHITE) + Bukkit.getOfflinePlayer(var2.J()).getName());
         var10001 = String.valueOf(ChatColor.GRAY);
         var1.sendMessage(var10001 + "Members: " + String.valueOf(ChatColor.WHITE) + var2.A().size());
         var10001 = String.valueOf(ChatColor.GRAY);
         var1.sendMessage(var10001 + "Your Role: " + String.valueOf(ChatColor.WHITE) + var2.C(var1.getUniqueId()).name());
         return true;
      }
   }

   private boolean A(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
         return true;
      } else {
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "=== Team Members ===");

         for(UUID var4 : var2.A()) {
            String var5 = Bukkit.getOfflinePlayer(var4).getName();
            String var10001 = String.valueOf(ChatColor.GRAY);
            var1.sendMessage(var10001 + "- " + var5 + " " + String.valueOf(ChatColor.DARK_GRAY) + "(" + var2.C(var4).name() + ")");
         }

         return true;
      }
   }

   private boolean B(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
         return true;
      } else if (!var2.A(var1.getUniqueId(), "edit_permissions")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to set the team home!");
         return true;
      } else {
         var2.A(var1.getLocation());
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Team home set!");

         for(UUID var4 : var2.A()) {
            Player var5 = Bukkit.getPlayer(var4);
            if (var5 != null && !var5.equals(var1)) {
               String var10001 = String.valueOf(ChatColor.GRAY);
               var5.sendMessage(var10001 + var1.getName() + " set a new team home!");
            }
         }

         return true;
      }
   }

   private boolean E(Player var1) {
      _H var2 = this.C.C(var1.getUniqueId());
      if (var2 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have a team!");
         return true;
      } else if (!var2.F()) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Team home not set!");
         return true;
      } else {
         var1.teleport(var2.H());
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Teleported to team home!");
         return true;
      }
   }

   private void C(Player var1) {
      var1.sendMessage(String.valueOf(ChatColor.GREEN) + "=== Team Commands ===");
      String var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team" + String.valueOf(ChatColor.WHITE) + " - Open team GUI");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team create <name>" + String.valueOf(ChatColor.WHITE) + " - Create a team");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team invite <player>" + String.valueOf(ChatColor.WHITE) + " - Invite a player");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team accept" + String.valueOf(ChatColor.WHITE) + " - Accept invite");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team decline" + String.valueOf(ChatColor.WHITE) + " - Decline invite");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team kick <player>" + String.valueOf(ChatColor.WHITE) + " - Kick a player");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team leave" + String.valueOf(ChatColor.WHITE) + " - Leave team");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team delete" + String.valueOf(ChatColor.WHITE) + " - Delete team");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team info" + String.valueOf(ChatColor.WHITE) + " - Team info");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team list" + String.valueOf(ChatColor.WHITE) + " - List members");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team sethome" + String.valueOf(ChatColor.WHITE) + " - Set team home");
      var10001 = String.valueOf(ChatColor.GRAY);
      var1.sendMessage(var10001 + "/team home" + String.valueOf(ChatColor.WHITE) + " - Teleport to team home");
   }

   private class _A implements Listener {
      @EventHandler
      public void onClick(InventoryClickEvent var1) {
         InventoryHolder var3 = var1.getInventory().getHolder();
         if (var3 instanceof _K var2) {
            var1.setCancelled(true);
            HumanEntity var4 = var1.getWhoClicked();
            if (var4 instanceof Player var5) {
               int var6 = var1.getRawSlot();
               if (var6 == 15) {
                  var5.closeInventory();
                  if (var2.C == A._K._A.A) {
                     A.this.doLeave(var5);
                  } else {
                     A.this.doDelete(var5);
                  }
               } else if (var6 == 11) {
                  var5.closeInventory();
                  var5.sendMessage(A.this.A(A.this.A("messages.cancelled", "&7Action cancelled.")));
               }

            }
         }
      }
   }

   private class _B implements Listener {
      private final Player B;
      private final _G A;
      private boolean C = true;

      _B(Player var2, _G var3) {
         this.B = var2;
         this.A = var3;
      }

      void start() {
         A.this.B.getServer().getPluginManager().registerEvents(this, A.this.B);
         (new BukkitRunnable() {
            // $FF: synthetic field
            final A._B this$1;

            {
               this.this$1 = var1;
            }

            public void run() {
               if (this.this$1.C) {
                  this.this$1.C = false;
                  AsyncPlayerChatEvent.getHandlerList().unregister(this.this$1);
                  this.this$1.B.sendMessage(String.valueOf(ChatColor.RED) + "Search timed out.");
                  this.this$1.A.open();
               }

            }
         }).runTaskLater(A.this.B, 300L);
      }

      @EventHandler(
         priority = EventPriority.LOWEST
      )
      public void onChat(AsyncPlayerChatEvent var1) {
         if (var1.getPlayer().equals(this.B)) {
            var1.setCancelled(true);
            this.C = false;
            AsyncPlayerChatEvent.getHandlerList().unregister(this);
            String var2 = var1.getMessage().trim();
            if (var2.equalsIgnoreCase("cancel")) {
               this.B.sendMessage(String.valueOf(ChatColor.GRAY) + "Search cancelled.");
               BukkitScheduler var10000 = A.this.B.getServer().getScheduler();
               PrismSurvival var10001 = A.this.B;
               _G var10002 = this.A;
               Objects.requireNonNull(var10002);
               var10000.runTask(var10001, var10002::open);
            } else {
               A.this.B.getServer().getScheduler().runTask(A.this.B, () -> {
                  this.A.E = var2;
                  this.A.setupInventory();
                  this.A.open();
                  Player var10000 = this.B;
                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var10000.sendMessage(var10001 + "Searching for: " + var2);
               });
            }

         }
      }
   }

   private class _C implements Listener {
      @EventHandler
      public void onClick(InventoryClickEvent var1) {
         if (var1.getInventory().getHolder() instanceof _G) {
            var1.setCancelled(true);
            Player var2 = (Player)var1.getWhoClicked();
            _G var3 = (_G)var1.getInventory().getHolder();
            _H var4 = var3.B;
            if (var4.B(var2.getUniqueId())) {
               int var5 = var1.getRawSlot();
               if (var5 >= 0 && var5 < 45) {
                  ItemStack var11 = var1.getCurrentItem();
                  if (var11 != null && var11.getType() == Material.PLAYER_HEAD) {
                     String var12 = ChatColor.stripColor(var11.getItemMeta().getDisplayName());
                     UUID var13 = null;

                     for(UUID var10 : var4.A()) {
                        if (this.A(var10).equals(var12)) {
                           var13 = var10;
                           break;
                        }
                     }

                     if (var13 != null) {
                        var2.closeInventory();
                        (A.this.new _E(var2, var4, var13)).open();
                     }
                  }

               } else {
                  switch (var5) {
                     case 46:
                        var3.A = A._G._A.values()[(var3.A.ordinal() + 1) % A._G._A.values().length];
                        var3.setupInventory();
                        String var16 = String.valueOf(ChatColor.GREEN);
                        var2.sendMessage(var16 + "Sorting by: " + var3.A.name());
                     case 47:
                     default:
                        break;
                     case 48:
                        if (var3.D > 0) {
                           --var3.D;
                           var3.setupInventory();
                           var2.updateInventory();
                        }
                        break;
                     case 49:
                        var3.setupInventory();
                        var2.sendMessage(String.valueOf(ChatColor.GREEN) + "Team GUI refreshed!");
                        break;
                     case 50:
                        if ((var3.D + 1) * 45 < var4.A().size()) {
                           ++var3.D;
                           var3.setupInventory();
                           var2.updateInventory();
                        }
                        break;
                     case 51:
                        if (var4.F()) {
                           var2.closeInventory();
                           var2.teleport(var4.H());
                           var2.sendMessage(String.valueOf(ChatColor.GREEN) + "Teleported to team home!");
                        } else {
                           var2.sendMessage(String.valueOf(ChatColor.RED) + "No team home set!");
                        }
                        break;
                     case 52:
                        if (var1.isRightClick() && !var3.E.isEmpty()) {
                           var3.E = "";
                           var3.setupInventory();
                           var2.sendMessage(String.valueOf(ChatColor.GRAY) + "Search cleared!");
                        } else if (var1.isLeftClick()) {
                           var2.closeInventory();
                           var2.sendMessage(String.valueOf(ChatColor.GREEN) + "Enter player name to search (type 'cancel' to cancel):");
                           (A.this.new _B(var2, var3)).start();
                        }
                        break;
                     case 53:
                        if (var4.A(var2.getUniqueId(), "edit_permissions")) {
                           var4.A(!var4.E());
                           var3.setupInventory();
                           String var6 = var4.E() ? "enabled" : "disabled";
                           String var10001 = String.valueOf(ChatColor.GRAY);
                           var2.sendMessage(var10001 + "Team PVP " + var6 + "!");

                           for(UUID var8 : var4.A()) {
                              Player var9 = Bukkit.getPlayer(var8);
                              if (var9 != null && !var9.equals(var2)) {
                                 var10001 = String.valueOf(ChatColor.GRAY);
                                 var9.sendMessage(var10001 + var2.getName() + " " + var6 + " team PVP.");
                              }
                           }
                        } else {
                           var2.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to change PVP!");
                        }
                  }

               }
            }
         }
      }

      private String A(UUID var1) {
         Player var2 = Bukkit.getPlayer(var1);
         return var2 != null ? var2.getName() : Bukkit.getOfflinePlayer(var1).getName();
      }
   }

   private class _D implements Listener {
      @EventHandler(
         priority = EventPriority.HIGH
      )
      public void onDamage(EntityDamageByEntityEvent var1) {
         if (var1.getDamager() instanceof Player && var1.getEntity() instanceof Player) {
            Player var2 = (Player)var1.getDamager();
            Player var3 = (Player)var1.getEntity();
            _H var4 = A.this.C.C(var2.getUniqueId());
            _H var5 = A.this.C.C(var3.getUniqueId());
            if (var4 != null && var5 != null && var4.B().equals(var5.B()) && !var4.E()) {
               var1.setCancelled(true);
               if (A.this.B.getConfig().getBoolean("donutteam.show-pvp-disabled-message", false)) {
                  var2.sendMessage(String.valueOf(ChatColor.RED) + "Team PVP is disabled!");
               }
            }

         }
      }
   }

   private class _E implements InventoryHolder {
      private final Player C;
      private final _H B;
      private final UUID D;
      private final Inventory A;

      _E(Player var2, _H var3, UUID var4) {
         this.C = var2;
         this.B = var3;
         this.D = var4;
         this.A = Bukkit.createInventory(this, 27, A.this.A(A.this.A("gui.perms-title", "&8ᴇᴅɪᴛ ᴘᴇʀᴍɪssɪᴏɴs")));
         this.setup();
      }

      void setup() {
         this.A.clear();
         String var1 = this.A(this.D);
         _F var2 = this.B.C(this.D);
         ItemStack var3 = new ItemStack(Material.CHEST);
         ItemMeta var4 = var3.getItemMeta();
         var4.setDisplayName(String.valueOf(ChatColor.GREEN) + "ᴀᴄᴄᴇss ɪɴᴠᴇɴᴛᴏʀʏ");
         String var10001 = String.valueOf(ChatColor.WHITE);
         var4.setLore(Collections.singletonList(var10001 + "Required: " + String.valueOf(ChatColor.GRAY) + "MEMBER"));
         var3.setItemMeta(var4);
         this.A.setItem(2, var3);
         ItemStack var5 = new ItemStack(Material.IRON_HELMET);
         ItemMeta var6 = var5.getItemMeta();
         var6.setDisplayName(String.valueOf(ChatColor.GREEN) + "ʙᴜɪʟᴅ");
         var10001 = String.valueOf(ChatColor.WHITE);
         var6.setLore(Collections.singletonList(var10001 + "Required: " + String.valueOf(ChatColor.GRAY) + "MODERATOR"));
         var5.setItemMeta(var6);
         this.A.setItem(3, var5);
         ItemStack var7 = new ItemStack(Material.IRON_SWORD);
         ItemMeta var8 = var7.getItemMeta();
         var8.setDisplayName(String.valueOf(ChatColor.GREEN) + "ᴋɪᴄᴋ");
         var10001 = String.valueOf(ChatColor.WHITE);
         var8.setLore(Collections.singletonList(var10001 + "Required: " + String.valueOf(ChatColor.GRAY) + "ADMIN"));
         var7.setItemMeta(var8);
         this.A.setItem(4, var7);
         ItemStack var9 = new ItemStack(Material.EMERALD);
         ItemMeta var10 = var9.getItemMeta();
         var10.setDisplayName(String.valueOf(ChatColor.GREEN) + "ɪɴᴠɪᴛᴇ");
         var10001 = String.valueOf(ChatColor.WHITE);
         var10.setLore(Collections.singletonList(var10001 + "Required: " + String.valueOf(ChatColor.GRAY) + "ADMIN"));
         var9.setItemMeta(var10);
         this.A.setItem(5, var9);
         ItemStack var11 = new ItemStack(Material.FEATHER);
         ItemMeta var12 = var11.getItemMeta();
         var12.setDisplayName(String.valueOf(ChatColor.GREEN) + "ᴇᴅɪᴛ ᴘᴇʀᴍɪssɪᴏɴs");
         var10001 = String.valueOf(ChatColor.WHITE);
         var12.setLore(Collections.singletonList(var10001 + "Required: " + String.valueOf(ChatColor.GRAY) + "OWNER"));
         var11.setItemMeta(var12);
         this.A.setItem(6, var11);
         ItemStack var13 = new ItemStack(Material.PLAYER_HEAD);
         SkullMeta var14 = (SkullMeta)var13.getItemMeta();
         var10001 = String.valueOf(ChatColor.GRAY);
         var14.setDisplayName(var10001 + var1);
         ArrayList var15 = new ArrayList();
         var10001 = String.valueOf(ChatColor.WHITE);
         var15.add(var10001 + "Current Role: " + String.valueOf(ChatColor.YELLOW) + var2.name());
         if (!this.B.J().equals(this.D)) {
            var15.add("");
            var10001 = String.valueOf(ChatColor.GREEN);
            var15.add(var10001 + "Left Click: " + String.valueOf(ChatColor.WHITE) + "Promote");
            var10001 = String.valueOf(ChatColor.RED);
            var15.add(var10001 + "Right Click: " + String.valueOf(ChatColor.WHITE) + "Demote");
         } else {
            var15.add(String.valueOf(ChatColor.GOLD) + "★ Team Owner");
         }

         var14.setLore(var15);
         Player var16 = Bukkit.getPlayer(this.D);
         if (var16 != null) {
            var14.setOwningPlayer(var16);
         }

         var13.setItemMeta(var14);
         this.A.setItem(13, var13);
         ItemStack var17 = new ItemStack(Material.ARROW);
         ItemMeta var18 = var17.getItemMeta();
         var18.setDisplayName(String.valueOf(ChatColor.GREEN) + "ʙᴀᴄᴋ");
         var18.setLore(Collections.singletonList(String.valueOf(ChatColor.WHITE) + "Return to team menu"));
         var17.setItemMeta(var18);
         this.A.setItem(22, var17);
      }

      void open() {
         this.C.openInventory(this.A);
      }

      public Inventory getInventory() {
         return this.A;
      }

      private String A(UUID var1) {
         Player var2 = Bukkit.getPlayer(var1);
         return var2 != null ? var2.getName() : Bukkit.getOfflinePlayer(var1).getName();
      }
   }

   public static enum _F {
      B(new String[]{"*"}),
      A(new String[]{"invite", "kick", "edit_permissions", "access_inventory", "place", "break", "interact"}),
      C(new String[]{"access_inventory", "place", "break", "interact"}),
      F(new String[]{"access_inventory"});

      private final Set<String> E = new HashSet();

      private _F(String[] var3) {
         for(String var7 : var3) {
            this.E.add(var7);
         }

      }

      public boolean A(String var1) {
         return this.E.contains("*") || this.E.contains(var1);
      }

      // $FF: synthetic method
      private static _F[] A() {
         return new _F[]{B, A, C, F};
      }
   }

   private class _G implements InventoryHolder {
      private final Player C;
      private final _H B;
      private Inventory F;
      private int D = 0;
      private _A A;
      private String E;

      _G(Player var2) {
         this.A = A._G._A.B;
         this.E = "";
         this.C = var2;
         this.B = A.this.C.C(var2.getUniqueId());
         this.setupInventory();
      }

      void setupInventory() {
         String var1 = A.this.A(A.this.A("gui.team-title", "&8ᴛᴇᴀᴍ &7(Page %page%)")).replace("%page%", String.valueOf(this.D + 1));
         this.F = Bukkit.createInventory(this, 54, var1);
         ArrayList var2 = new ArrayList(this.B.A());
         if (!this.E.isEmpty()) {
            var2.removeIf((var1x) -> !this.B(var1x).toLowerCase().contains(this.E.toLowerCase()));
         }

         switch (this.A.ordinal()) {
            case 1 -> var2.sort((var1x, var2x) -> Integer.compare(this.A(this.B.C(var2x)), this.A(this.B.C(var1x))));
            case 2 -> var2.sort(Comparator.comparing(this::B, String.CASE_INSENSITIVE_ORDER));
            case 3 -> var2.sort((var0, var1x) -> Boolean.compare(Bukkit.getPlayer(var1x) != null, Bukkit.getPlayer(var0) != null));
         }

         int var3 = this.D * 45;

         for(int var4 = 0; var4 < 45 && var3 + var4 < var2.size(); ++var4) {
            UUID var5 = (UUID)var2.get(var3 + var4);
            Player var6 = Bukkit.getPlayer(var5);
            ItemStack var7 = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta var8 = (SkullMeta)var7.getItemMeta();
            String var10001 = String.valueOf(ChatColor.GRAY);
            var8.setDisplayName(var10001 + this.B(var5));
            ArrayList var9 = new ArrayList();
            var10001 = String.valueOf(ChatColor.WHITE);
            var9.add(var10001 + "Role: " + String.valueOf(ChatColor.YELLOW) + this.B.C(var5).name());
            var10001 = String.valueOf(ChatColor.WHITE);
            var9.add(var10001 + "Mode: " + (var6 != null ? String.valueOf(ChatColor.GREEN) + "ONLINE" : String.valueOf(ChatColor.RED) + "OFFLINE"));
            var8.setLore(var9);
            if (var6 != null) {
               var8.setOwningPlayer(var6);
            }

            var7.setItemMeta(var8);
            this.F.setItem(var4, var7);
         }

         ItemStack var18 = new ItemStack(Material.HOPPER);
         ItemMeta var19 = var18.getItemMeta();
         var19.setDisplayName(A.this.A(A.this.A("gui.sort-name", "&asᴏʀᴛ")));
         String var26 = new String[]{String.valueOf(ChatColor.WHITE) + "Sort by:", String.valueOf(ChatColor.GRAY) + "- Join Date", String.valueOf(ChatColor.GRAY) + "- Permissions", String.valueOf(ChatColor.GRAY) + "- Alphabetically", String.valueOf(ChatColor.GRAY) + "- Online Members", "", null};
         String var10004 = String.valueOf(ChatColor.WHITE);
         ((Object[])var26)[6] = var10004 + "Selected: " + String.valueOf(ChatColor.GREEN) + this.A.name();
         var19.setLore(Arrays.asList(var26));
         var18.setItemMeta(var19);
         this.F.setItem(46, var18);
         ItemStack var20 = new ItemStack(Material.ARROW);
         ItemMeta var21 = var20.getItemMeta();
         var21.setDisplayName(A.this.A(A.this.A("gui.prev-name", "&aʙᴀᴄᴋ")));
         var21.setLore(A.this.A("gui.prev-lore", Collections.singletonList("&fPrevious page")));
         var20.setItemMeta(var21);
         this.F.setItem(48, var20);
         ItemStack var22 = new ItemStack(Material.IRON_HELMET);
         ItemMeta var23 = var22.getItemMeta();
         var23.setDisplayName(A.this.A(A.this.A("gui.refresh-name", "&aᴛᴇᴀᴍ %team%").replace("%team%", this.B.D())));
         var23.setLore(Arrays.asList(String.valueOf(ChatColor.WHITE) + "Click to refresh", String.valueOf(ChatColor.GRAY) + "Add up to 50 team members"));
         var22.setItemMeta(var23);
         this.F.setItem(49, var22);
         ItemStack var10 = new ItemStack(Material.ARROW);
         ItemMeta var11 = var10.getItemMeta();
         var11.setDisplayName(A.this.A(A.this.A("gui.next-name", "&aɴᴇxᴛ")));
         var11.setLore(A.this.A("gui.next-lore", Collections.singletonList("&fNext page")));
         var10.setItemMeta(var11);
         this.F.setItem(50, var10);
         ItemStack var12 = new ItemStack(Material.WHITE_BANNER);
         ItemMeta var13 = var12.getItemMeta();
         var13.setDisplayName(A.this.A(A.this.A("gui.home-name", "&aᴛᴇᴀᴍ ʜᴏᴍᴇ")));
         var13.setLore(A.this.A("gui.home-lore", Collections.singletonList("&fClick to teleport to team home")));
         var12.setItemMeta(var13);
         this.F.setItem(51, var12);
         ItemStack var14 = new ItemStack(Material.OAK_SIGN);
         ItemMeta var15 = var14.getItemMeta();
         var15.setDisplayName(A.this.A(A.this.A("gui.search-name", "&asᴇᴀʀᴄʜ")));
         if (!this.E.isEmpty()) {
            var26 = new String[3];
            var10004 = String.valueOf(ChatColor.WHITE);
            var26[0] = var10004 + "Searching: " + String.valueOf(ChatColor.YELLOW) + this.E;
            var26[1] = "";
            var26[2] = String.valueOf(ChatColor.RED) + "Right-click to clear";
            var15.setLore(Arrays.asList(var26));
         } else {
            var15.setLore(Collections.singletonList(String.valueOf(ChatColor.WHITE) + "Click to search"));
         }

         var14.setItemMeta(var15);
         this.F.setItem(52, var14);
         ItemStack var16 = new ItemStack(Material.IRON_SWORD);
         ItemMeta var17 = var16.getItemMeta();
         var17.setDisplayName(A.this.A(A.this.A("gui.pvp-name", "&aᴘᴠᴘ")));
         var26 = String.valueOf(ChatColor.WHITE);
         var17.setLore(Collections.singletonList(var26 + "Currently: " + (this.B.E() ? String.valueOf(ChatColor.GREEN) + "ON" : String.valueOf(ChatColor.RED) + "OFF")));
         var16.setItemMeta(var17);
         this.F.setItem(53, var16);
      }

      void open() {
         this.C.openInventory(this.F);
      }

      public Inventory getInventory() {
         return this.F;
      }

      private String B(UUID var1) {
         Player var2 = Bukkit.getPlayer(var1);
         return var2 != null ? var2.getName() : Bukkit.getOfflinePlayer(var1).getName();
      }

      private int A(_F var1) {
         switch (var1.ordinal()) {
            case 0 -> {
               return 4;
            }
            case 1 -> {
               return 3;
            }
            case 2 -> {
               return 2;
            }
            default -> {
               return 1;
            }
         }
      }

      static enum _A {
         B,
         A,
         D,
         E;

         // $FF: synthetic method
         private static _A[] A() {
            return new _A[]{B, A, D, E};
         }
      }
   }

   public static class _H {
      private final UUID G;
      private String A;
      private UUID B;
      private final List<UUID> F;
      private final Map<UUID, _F> I;
      private final ItemStack[] C;
      private final long H;
      private boolean D;
      private Location E;

      public _H(String var1, UUID var2) {
         this.G = UUID.randomUUID();
         this.A = var1;
         this.B = var2;
         this.F = new ArrayList();
         this.F.add(var2);
         this.I = new HashMap();
         this.I.put(var2, A._F.B);
         this.C = new ItemStack[45];
         this.H = System.currentTimeMillis();
         this.D = false;
         this.E = null;
      }

      public _H(UUID var1, String var2, UUID var3, List<UUID> var4, Map<UUID, _F> var5, ItemStack[] var6, long var7) {
         this.G = var1;
         this.A = var2;
         this.B = var3;
         this.F = var4;
         this.I = var5;
         this.C = var6;
         this.H = var7;
      }

      public UUID B() {
         return this.G;
      }

      public String D() {
         return this.A;
      }

      public void A(String var1) {
         this.A = var1;
      }

      public UUID J() {
         return this.B;
      }

      public void D(UUID var1) {
         this.B = var1;
      }

      public List<UUID> A() {
         return this.F;
      }

      public void B(UUID var1, _F var2) {
         if (!this.F.contains(var1)) {
            this.F.add(var1);
            this.I.put(var1, var2);
         }

      }

      public void A(UUID var1) {
         this.F.remove(var1);
         this.I.remove(var1);
      }

      public boolean B(UUID var1) {
         return this.F.contains(var1);
      }

      public _F C(UUID var1) {
         return (_F)this.I.getOrDefault(var1, A._F.F);
      }

      public void A(UUID var1, _F var2) {
         if (this.B(var1)) {
            this.I.put(var1, var2);
         }

      }

      public Map<UUID, _F> I() {
         return this.I;
      }

      public ItemStack[] C() {
         return this.C;
      }

      public void A(int var1, ItemStack var2) {
         if (var1 >= 0 && var1 < this.C.length) {
            this.C[var1] = var2;
         }

      }

      public ItemStack A(int var1) {
         return var1 >= 0 && var1 < this.C.length ? this.C[var1] : null;
      }

      public long G() {
         return this.H;
      }

      public boolean A(UUID var1, String var2) {
         return this.C(var1).A(var2);
      }

      public boolean E() {
         return this.D;
      }

      public void A(boolean var1) {
         this.D = var1;
      }

      public Location H() {
         return this.E;
      }

      public void A(Location var1) {
         this.E = var1;
      }

      public boolean F() {
         return this.E != null;
      }
   }

   private class _I extends PlaceholderExpansion {
      public String getIdentifier() {
         return "donutteam";
      }

      public String getAuthor() {
         return A.this.B.getDescription().getAuthors().toString();
      }

      public String getVersion() {
         return A.this.B.getDescription().getVersion();
      }

      public boolean persist() {
         return true;
      }

      public String onRequest(OfflinePlayer var1, String var2) {
         if (var1 == null) {
            return "";
         } else {
            _H var3 = A.this.C.C(var1.getUniqueId());
            if (var2.equalsIgnoreCase("team")) {
               return var3 != null ? var3.D() : "N/A";
            } else if (var2.equalsIgnoreCase("role")) {
               return var3 != null ? var3.C(var1.getUniqueId()).name() : "N/A";
            } else if (var2.equalsIgnoreCase("members")) {
               return var3 != null ? String.valueOf(var3.A().size()) : "0";
            } else if (var2.equalsIgnoreCase("owner")) {
               return var3 != null ? Bukkit.getOfflinePlayer(var3.J()).getName() : "N/A";
            } else if (var2.equalsIgnoreCase("pvp")) {
               return var3 != null ? (var3.E() ? "ON" : "OFF") : "N/A";
            } else if (var2.equalsIgnoreCase("has_team")) {
               return var3 != null ? "Yes" : "No";
            } else {
               return null;
            }
         }
      }
   }

   private class _J implements Listener {
      @EventHandler
      public void onClick(InventoryClickEvent var1) {
         if (var1.getInventory().getHolder() instanceof _E) {
            var1.setCancelled(true);
            Player var2 = (Player)var1.getWhoClicked();
            _E var3 = (_E)var1.getInventory().getHolder();
            if (!var3.B.A(var2.getUniqueId(), "edit_permissions")) {
               var2.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to edit permissions!");
            } else {
               int var4 = var1.getRawSlot();
               if (var4 == 13) {
                  if (var3.B.J().equals(var3.D)) {
                     var2.sendMessage(String.valueOf(ChatColor.RED) + "Cannot change owner's role!");
                     return;
                  }

                  _F var5 = var3.B.C(var3.D);
                  _F var6;
                  if (var1.getClick() == ClickType.LEFT) {
                     switch (var5.ordinal()) {
                        case 2:
                           var6 = A._F.A;
                           break;
                        case 3:
                           var6 = A._F.C;
                           break;
                        default:
                           var2.sendMessage(String.valueOf(ChatColor.RED) + "Cannot promote further!");
                           return;
                     }
                  } else {
                     if (var1.getClick() != ClickType.RIGHT) {
                        return;
                     }

                     switch (var5.ordinal()) {
                        case 1:
                           var6 = A._F.C;
                           break;
                        case 2:
                           var6 = A._F.F;
                           break;
                        default:
                           var2.sendMessage(String.valueOf(ChatColor.RED) + "Cannot demote further!");
                           return;
                     }
                  }

                  if (var6 != var5) {
                     var3.B.A(var3.D, var6);
                     String var10001 = String.valueOf(ChatColor.GREEN);
                     var2.sendMessage(var10001 + "Changed role of " + this.A(var3.D) + " to " + var6.name());
                     Player var7 = Bukkit.getPlayer(var3.D);
                     if (var7 != null) {
                        var10001 = String.valueOf(ChatColor.GRAY);
                        var7.sendMessage(var10001 + "Your role has been changed to " + String.valueOf(ChatColor.YELLOW) + var6.name());
                     }

                     var3.setup();
                  }
               } else if (var4 == 22) {
                  var2.closeInventory();
                  (A.this.new _G(var2)).open();
               }

            }
         }
      }

      private String A(UUID var1) {
         Player var2 = Bukkit.getPlayer(var1);
         return var2 != null ? var2.getName() : Bukkit.getOfflinePlayer(var1).getName();
      }
   }

   private class _K implements InventoryHolder {
      private final Player B;
      private final _A C;
      private final Inventory A;

      _K(Player var2, _A var3) {
         this.B = var2;
         this.C = var3;
         String var4 = var3 == A._K._A.A ? "confirm-gui.leave-title" : "confirm-gui.delete-title";
         String var5 = var3 == A._K._A.A ? "&8ᴄᴏɴꜰɪʀᴍ ʟᴇᴀᴠᴇ" : "&8ᴄᴏɴꜰɪʀᴍ ᴅɪsʙᴀɴᴅ";
         this.A = Bukkit.createInventory(this, 27, A.this.A(A.this.A(var4, var5)));
         ItemStack var6 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
         ItemMeta var7 = var6.getItemMeta();
         var7.setDisplayName(A.this.A(A.this.A("confirm-gui.cancel-name", "&cᴄᴀɴᴄᴇʟ")));
         var7.setLore(this.A(A.this.A("confirm-gui.cancel-lore", Collections.singletonList("&7Click to cancel"))));
         var6.setItemMeta(var7);
         this.A.setItem(11, var6);
         ItemStack var8 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
         ItemMeta var9 = var8.getItemMeta();
         String var10 = var3 == A._K._A.A ? "confirm-gui.confirm-leave-name" : "confirm-gui.confirm-delete-name";
         String var11 = var3 == A._K._A.A ? "&aᴄᴏɴꜰɪʀᴍ ʟᴇᴀᴠᴇ" : "&aᴄᴏɴꜰɪʀᴍ ᴅɪsʙᴀɴᴅ";
         String var12 = var3 == A._K._A.A ? "confirm-gui.confirm-leave-lore" : "confirm-gui.confirm-delete-lore";
         List var13 = var3 == A._K._A.A ? Collections.singletonList("&7Click to leave your team") : Collections.singletonList("&7Click to disband your team");
         var9.setDisplayName(A.this.A(A.this.A(var10, var11)));
         var9.setLore(this.A(A.this.A(var12, var13)));
         var8.setItemMeta(var9);
         this.A.setItem(15, var8);
      }

      private List<String> A(List<String> var1) {
         ArrayList var2 = new ArrayList();

         for(String var4 : var1) {
            var2.add(A.this.A(var4));
         }

         return var2;
      }

      void open() {
         this.B.openInventory(this.A);
      }

      public Inventory getInventory() {
         return this.A;
      }

      static enum _A {
         A,
         C;

         // $FF: synthetic method
         private static _A[] A() {
            return new _A[]{A, C};
         }
      }
   }

   private class _L {
      private final Map<UUID, _H> C = new ConcurrentHashMap();
      private final Map<UUID, UUID> D = new ConcurrentHashMap();
      private final Map<UUID, UUID> B = new ConcurrentHashMap();

      _L() {
      }

      public _H A(String var1, Player var2) {
         if (!this.F(var2.getUniqueId()) && !this.A(var1)) {
            _H var3 = new _H(var1, var2.getUniqueId());
            this.C.put(var3.B(), var3);
            this.D.put(var2.getUniqueId(), var3.B());
            return var3;
         } else {
            return null;
         }
      }

      public boolean A(String var1) {
         return this.C.values().stream().anyMatch((var1x) -> var1x.D().equalsIgnoreCase(var1));
      }

      public void D(UUID var1) {
         _H var2 = (_H)this.C.remove(var1);
         if (var2 != null) {
            for(UUID var4 : var2.A()) {
               this.D.remove(var4);
            }
         }

      }

      public _H A(UUID var1) {
         return (_H)this.C.get(var1);
      }

      public _H C(UUID var1) {
         UUID var2 = (UUID)this.D.get(var1);
         return var2 == null ? null : (_H)this.C.get(var2);
      }

      public boolean F(UUID var1) {
         return this.D.containsKey(var1);
      }

      public void B(UUID var1, UUID var2) {
         this.B.put(var2, var1);
      }

      public UUID G(UUID var1) {
         return (UUID)this.B.get(var1);
      }

      public void E(UUID var1) {
         this.B.remove(var1);
      }

      public void A(UUID var1, UUID var2, _F var3) {
         _H var4 = (_H)this.C.get(var2);
         if (var4 != null) {
            var4.B(var1, var3);
            this.D.put(var1, var2);
            this.B.remove(var1);
         }

      }

      public void A(UUID var1, UUID var2) {
         _H var3 = (_H)this.C.get(var2);
         if (var3 != null) {
            var3.A(var1);
            this.D.remove(var1);
         }

      }

      public void B(UUID var1) {
         _H var2 = this.C(var1);
         if (var2 != null) {
            var2.A(var1);
            this.D.remove(var1);
            if (var2.J().equals(var1)) {
               if (var2.A().isEmpty()) {
                  this.D(var2.B());
               } else {
                  UUID var3 = (UUID)var2.A().get(0);
                  var2.D(var3);
                  var2.A(var3, A._F.B);
               }
            }
         }

      }

      public Collection<_H> B() {
         return this.C.values();
      }

      public void A() {
         File var1 = new File(A.this.B.getDataFolder(), "donutteam/teams.yml");
         if (var1.exists()) {
            YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
            ConfigurationSection var3 = var2.getConfigurationSection("teams");
            if (var3 != null) {
               for(String var5 : var3.getKeys(false)) {
                  try {
                     UUID var6 = UUID.fromString(var5);
                     ConfigurationSection var7 = var3.getConfigurationSection(var5);
                     if (var7 != null) {
                        String var8 = var7.getString("name");
                        UUID var9 = UUID.fromString(var7.getString("owner"));
                        long var10 = var7.getLong("created-at");
                        boolean var12 = var7.getBoolean("pvp-enabled", false);
                        ArrayList var13 = new ArrayList();

                        for(String var15 : var7.getStringList("members")) {
                           var13.add(UUID.fromString(var15));
                        }

                        HashMap var21 = new HashMap();
                        ConfigurationSection var22 = var7.getConfigurationSection("permissions");
                        if (var22 != null) {
                           for(String var17 : var22.getKeys(false)) {
                              var21.put(UUID.fromString(var17), A._F.valueOf(var22.getString(var17)));
                           }
                        }

                        _H var23 = new _H(var6, var8, var9, var13, var21, new ItemStack[45], var10);
                        var23.A(var12);
                        if (var7.contains("home")) {
                           ConfigurationSection var24 = var7.getConfigurationSection("home");
                           if (var24 != null) {
                              World var18 = Bukkit.getWorld(var24.getString("world"));
                              if (var18 != null) {
                                 Location var19 = new Location(var18, var24.getDouble("x"), var24.getDouble("y"), var24.getDouble("z"), (float)var24.getDouble("yaw"), (float)var24.getDouble("pitch"));
                                 var23.A(var19);
                              }
                           }
                        }

                        this.C.put(var6, var23);

                        for(UUID var26 : var13) {
                           this.D.put(var26, var6);
                        }
                     }
                  } catch (Exception var20) {
                     A.this.B.getLogger().warning("Failed to load team " + var5 + ": " + var20.getMessage());
                  }
               }

               A.this.B.getLogger().info("Loaded " + this.C.size() + " teams.");
            }
         }
      }

      public void C() {
         File var1 = new File(A.this.B.getDataFolder(), "donutteam/teams.yml");
         var1.getParentFile().mkdirs();
         YamlConfiguration var2 = new YamlConfiguration();

         for(Map.Entry var4 : this.C.entrySet()) {
            _H var5 = (_H)var4.getValue();
            String var6 = "teams." + ((UUID)var4.getKey()).toString();
            var2.set(var6 + ".name", var5.D());
            var2.set(var6 + ".owner", var5.J().toString());
            var2.set(var6 + ".created-at", var5.G());
            var2.set(var6 + ".pvp-enabled", var5.E());
            ArrayList var7 = new ArrayList();

            for(UUID var9 : var5.A()) {
               var7.add(var9.toString());
            }

            var2.set(var6 + ".members", var7);

            for(Map.Entry var13 : var5.I().entrySet()) {
               var2.set(var6 + ".permissions." + ((UUID)var13.getKey()).toString(), ((_F)var13.getValue()).name());
            }

            if (var5.F()) {
               Location var12 = var5.H();
               var2.set(var6 + ".home.world", var12.getWorld().getName());
               var2.set(var6 + ".home.x", var12.getX());
               var2.set(var6 + ".home.y", var12.getY());
               var2.set(var6 + ".home.z", var12.getZ());
               var2.set(var6 + ".home.yaw", var12.getYaw());
               var2.set(var6 + ".home.pitch", var12.getPitch());
            }
         }

         try {
            var2.save(var1);
         } catch (IOException var10) {
            A.this.B.getLogger().severe("Failed to save teams: " + var10.getMessage());
         }

      }
   }

   private class _M extends PlaceholderExpansion {
      public String getIdentifier() {
         return "donutcore";
      }

      public String getAuthor() {
         return A.this.B.getDescription().getAuthors().toString();
      }

      public String getVersion() {
         return A.this.B.getDescription().getVersion();
      }

      public boolean persist() {
         return true;
      }

      public String onRequest(OfflinePlayer var1, String var2) {
         if (var1 == null) {
            return "";
         } else if (!var2.equalsIgnoreCase("team")) {
            return null;
         } else {
            _H var3 = A.this.C.C(var1.getUniqueId());
            return var3 != null ? var3.D() : A.this.A("placeholder.no-team", "N/A");
         }
      }
   }
}
