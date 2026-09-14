package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class E implements CommandExecutor, TabCompleter, Listener {
   private final PrismSurvival B;
   private final File N;
   private FileConfiguration V;
   private final Map<UUID, BukkitTask> R = new ConcurrentHashMap();
   private final Map<UUID, Integer> E = new ConcurrentHashMap();
   private final Map<UUID, UUID> W = new ConcurrentHashMap();
   private final Map<UUID, Integer> C = new ConcurrentHashMap();
   private final Set<UUID> A = ConcurrentHashMap.newKeySet();
   private int D;
   private int P;
   private static final String G = "teamhome";
   private String J;
   private int O;
   private int[] L;
   private int[] S;
   private int F;
   private Map<String, String> Q;
   private Map<String, _C> M;
   private _A I;
   private _B T;
   private static final String U = ChatColor.translateAlternateColorCodes('&', "&8Home Admin Panel");
   private static final String K = ChatColor.translateAlternateColorCodes('&', "&8Confirm Home Delete");
   private static final String H = "economysmpcore.home.admin";

   public E(PrismSurvival var1) {
      this.B = var1;
      this.N = new File(var1.getDataFolder(), "home/config.yml");
      this.A();
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   private void A() {
      if (!this.N.exists()) {
         this.N.getParentFile().mkdirs();
         this.B.saveResource("home/config.yml", false);
      }

      this.V = YamlConfiguration.loadConfiguration(this.N);
      this.J = ChatColor.translateAlternateColorCodes('&', this.V.getString("gui.title", "&8ʜᴏᴍᴇѕ"));
      this.O = this.V.getInt("gui.size", 36);
      this.L = this.V.getIntegerList("gui.home-slots").stream().mapToInt((var0) -> var0).toArray();
      if (this.L.length == 0) {
         this.L = new int[]{21, 22, 23, 24, 25};
      }

      this.S = this.V.getIntegerList("gui.bed-slots").stream().mapToInt((var0) -> var0).toArray();
      if (this.S.length == 0) {
         this.S = new int[]{12, 13, 14, 15, 16};
      }

      this.F = this.V.getInt("teleport.delay-seconds", 5);
      ConfigurationSection var1 = this.V.getConfigurationSection("team-home");
      this.T = new _B(var1);
      this.D = this.T.B;
      this.P = this.T.C;
      this.Q = new HashMap();
      ConfigurationSection var2 = this.V.getConfigurationSection("messages");
      if (var2 != null) {
         for(String var4 : var2.getKeys(false)) {
            this.Q.put(var4, ChatColor.translateAlternateColorCodes('&', var2.getString(var4, var4)));
         }
      }

      if (!this.Q.containsKey("no-permission")) {
         this.Q.put("no-permission", "&cNo permission.");
      }

      if (!this.Q.containsKey("home-not-exist")) {
         this.Q.put("home-not-exist", "&cNo home set.");
      }

      if (!this.Q.containsKey("home-set")) {
         this.Q.put("home-set", "&aHome set!");
      }

      if (!this.Q.containsKey("home-deleted")) {
         this.Q.put("home-deleted", "&aHome deleted!");
      }

      if (!this.Q.containsKey("delete-cancelled")) {
         this.Q.put("delete-cancelled", "&7Deletion cancelled.");
      }

      if (!this.Q.containsKey("teleport-success")) {
         this.Q.put("teleport-success", "&aTeleported!");
      }

      if (!this.Q.containsKey("teleport-cancelled-move")) {
         this.Q.put("teleport-cancelled-move", "&cTeleport cancelled because you moved.");
      }

      if (!this.Q.containsKey("teleporting")) {
         this.Q.put("teleporting", "&7Teleporting in &5%seconds%s...");
      }

      if (!this.Q.containsKey("already-teleporting")) {
         this.Q.put("already-teleporting", "&cYou are already teleporting.");
      }

      if (!this.Q.containsKey("team-home-set")) {
         this.Q.put("team-home-set", "&aTeam home set!");
      }

      if (!this.Q.containsKey("team-home-deleted")) {
         this.Q.put("team-home-deleted", "&aTeam home deleted!");
      }

      if (!this.Q.containsKey("team-home-no-team")) {
         this.Q.put("team-home-no-team", "&cYou are not in a team.");
      }

      if (!this.Q.containsKey("team-home-not-set")) {
         this.Q.put("team-home-not-set", "&cNo team home has been set.");
      }

      this.M = new HashMap();
      ConfigurationSection var7 = this.V.getConfigurationSection("items");

      for(String var5 : Arrays.asList("no-home-dye", "no-home-bed", "has-home-dye", "has-home-bed", "no-permission-dye", "no-permission-bed")) {
         ConfigurationSection var6 = var7 != null ? var7.getConfigurationSection(var5) : null;
         this.M.put(var5, var6 != null ? new _C(var6) : new _C((ConfigurationSection)null));
      }

      ConfigurationSection var9 = this.V.getConfigurationSection("confirm-delete");
      this.I = var9 != null ? new _A(var9) : new _A((ConfigurationSection)null);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         return true;
      } else if (var4.length >= 1 && var4[0].equalsIgnoreCase("admin")) {
         if (!var5.isOp() && !var5.hasPermission("economysmpcore.home.admin")) {
            this.A(var5, (String)this.Q.get("no-permission"));
            return true;
         } else if (var4.length < 2) {
            this.B(var5, "&cUsage: /home admin <player>");
            return true;
         } else {
            Object var10 = Bukkit.getPlayer(var4[1]);
            if (var10 == null) {
               var10 = Bukkit.getOfflinePlayer(var4[1]);
            }

            if (var10 != null && (((OfflinePlayer)var10).hasPlayedBefore() || ((OfflinePlayer)var10).isOnline())) {
               this.A((Player)var5, (OfflinePlayer)var10);
               return true;
            } else {
               this.B(var5, "&cPlayer not found (never played on this server).");
               return true;
            }
         }
      } else {
         if (var4.length == 1) {
            try {
               int var6 = Integer.parseInt(var4[0]);
               if (var6 >= 1 && var6 <= this.L.length) {
                  String var7 = "economysmpcore.home." + var6;
                  if (!var5.hasPermission(var7)) {
                     this.A(var5, (String)this.Q.get("no-permission"));
                     return true;
                  }

                  Location var8 = this.A(var5.getUniqueId(), var6);
                  if (var8 == null) {
                     this.A(var5, (String)this.Q.get("home-not-exist"));
                     return true;
                  }

                  this.A(var5, var8);
                  return true;
               }

               this.B(var5, "&cInvalid home number (1-" + this.L.length + ").");
               return true;
            } catch (NumberFormatException var9) {
            }
         }

         this.A(var5);
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         return Collections.emptyList();
      } else if (var4.length != 1 || !"admin".startsWith(var4[0].toLowerCase()) || !var5.isOp() && !var5.hasPermission("economysmpcore.home.admin")) {
         if (var4.length == 2 && var4[0].equalsIgnoreCase("admin")) {
            return !var5.isOp() && !var5.hasPermission("economysmpcore.home.admin") ? Collections.emptyList() : (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[1].toLowerCase())).collect(Collectors.toList());
         } else if (var4.length == 1) {
            ArrayList var8 = new ArrayList();

            for(int var9 = 1; var9 <= this.L.length; ++var9) {
               if (this.A(var5.getUniqueId(), var9) != null) {
                  var8.add(String.valueOf(var9));
               }
            }

            return (List)var8.stream().filter((var1x) -> var1x.startsWith(var4[0])).collect(Collectors.toList());
         } else {
            return Collections.emptyList();
         }
      } else {
         ArrayList var6 = new ArrayList();
         var6.add("admin");

         for(int var7 = 1; var7 <= this.L.length; ++var7) {
            if (this.A(var5.getUniqueId(), var7) != null) {
               var6.add(String.valueOf(var7));
            }
         }

         return (List)var6.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      }
   }

   private void A(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, this.O, this.J);

      for(int var3 = 0; var3 < this.L.length && var3 < this.S.length; ++var3) {
         int var4 = var3 + 1;
         String var5 = "economysmpcore.home." + var4;
         boolean var6 = var1.hasPermission(var5);
         Location var7 = this.A(var1.getUniqueId(), var4);
         ItemStack var8;
         if (!var6) {
            var8 = this.A((_C)this.M.get("no-permission-dye"), var4);
         } else if (var7 == null) {
            var8 = this.A((_C)this.M.get("no-home-dye"), var4);
         } else {
            var8 = this.A((_C)this.M.get("has-home-dye"), var4);
         }

         if (this.L[var3] < this.O) {
            var2.setItem(this.L[var3], var8);
         }

         ItemStack var9;
         if (!var6) {
            var9 = this.A((_C)this.M.get("no-permission-bed"), var4);
         } else if (var7 == null) {
            var9 = this.A((_C)this.M.get("no-home-bed"), var4);
         } else {
            var9 = this.A((_C)this.M.get("has-home-bed"), var4);
         }

         if (this.S[var3] < this.O) {
            var2.setItem(this.S[var3], var9);
         }
      }

      com.h2ph.H.A var10 = this.B.getDonutTeamModule();
      com.h2ph.H.A._H var11 = var10 != null ? var10.getPlayerTeam(var1.getUniqueId()) : null;
      Location var12 = this.A(var1.getUniqueId());
      ItemStack var13 = this.A((_C)(var11 == null ? this.T.H : (var12 == null ? this.T.D : this.T.E)), 0);
      if (this.D < this.O) {
         var2.setItem(this.D, var13);
      }

      ItemStack var14 = this.A((_C)(var11 == null ? this.T.A : (var12 == null ? this.T.G : this.T.F)), 0);
      if (this.P < this.O) {
         var2.setItem(this.P, var14);
      }

      var1.openInventory(var2);
   }

   private void A(Player var1, OfflinePlayer var2) {
      this.W.put(var1.getUniqueId(), var2.getUniqueId());
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, this.O, U);

      for(int var4 = 0; var4 < this.L.length && var4 < this.S.length; ++var4) {
         int var5 = var4 + 1;
         Location var6 = this.A(var2.getUniqueId(), var5);
         ItemStack var7 = var6 == null ? this.A((_C)this.M.get("no-home-dye"), var5) : this.A((_C)this.M.get("has-home-dye"), var5);
         if (this.L[var4] < this.O) {
            var3.setItem(this.L[var4], var7);
         }

         ItemStack var8;
         if (var6 == null) {
            var8 = this.A((_C)this.M.get("no-home-bed"), var5);
         } else {
            var8 = this.A((_C)this.M.get("has-home-bed"), var5);
            ItemMeta var9 = var8.getItemMeta();
            if (var9 != null) {
               ArrayList var10 = var9.getLore() != null ? new ArrayList(var9.getLore()) : new ArrayList();
               var10.add(ChatColor.translateAlternateColorCodes('&', "&7&o[Right-click] Teleport"));
               var10.add(ChatColor.translateAlternateColorCodes('&', "&c&o[Left-click] Delete home"));
               var9.setLore(var10);
               var8.setItemMeta(var9);
            }
         }

         if (this.S[var4] < this.O) {
            var3.setItem(this.S[var4], var8);
         }
      }

      var1.openInventory(var3);
      this.A(var1, Sound.BLOCK_CHEST_OPEN);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      HumanEntity var3 = var1.getWhoClicked();
      if (var3 instanceof Player var2) {
         String var10 = var1.getView().getTitle();
         if (var10.equals(U)) {
            this.A(var1, var2);
         } else if (var10.equals(K)) {
            this.B(var1, var2);
         } else if (var10.equals(this.J)) {
            var1.setCancelled(true);
            int var4 = var1.getRawSlot();
            if (var4 == this.D) {
               com.h2ph.H.A var12 = this.B.getDonutTeamModule();
               com.h2ph.H.A._H var15 = var12 != null ? var12.getPlayerTeam(var2.getUniqueId()) : null;
               if (var15 == null) {
                  this.A(var2, (String)this.Q.get("team-home-no-team"));
                  this.A(var2, Sound.ENTITY_VILLAGER_NO);
               } else {
                  Location var18 = this.A(var2.getUniqueId());
                  if (var18 == null) {
                     this.A(var2, (String)this.Q.get("team-home-not-set"));
                     this.A(var2, Sound.ENTITY_VILLAGER_NO);
                  } else {
                     var2.closeInventory();
                     this.A(var2, var18);
                  }
               }
            } else if (var4 == this.P) {
               com.h2ph.H.A var11 = this.B.getDonutTeamModule();
               com.h2ph.H.A._H var14 = var11 != null ? var11.getPlayerTeam(var2.getUniqueId()) : null;
               if (var14 == null) {
                  this.A(var2, (String)this.Q.get("team-home-no-team"));
                  this.A(var2, Sound.ENTITY_VILLAGER_NO);
               } else {
                  Location var17 = this.A(var2.getUniqueId());
                  if (var17 == null) {
                     this.A(var2.getUniqueId(), var2.getLocation());
                     this.A(var2, (String)this.Q.get("team-home-set"));
                     this.A(var2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                     this.A(var2);
                  } else {
                     this.E.put(var2.getUniqueId(), -1);
                     this.B((Player)var2, -1);
                     this.A(var2, Sound.BLOCK_NOTE_BLOCK_HAT);
                  }

               }
            } else {
               int var5 = -1;

               for(int var6 = 0; var6 < this.L.length; ++var6) {
                  if (this.L[var6] == var4) {
                     var5 = var6;
                     break;
                  }
               }

               boolean var13 = var5 != -1;
               if (!var13) {
                  for(int var7 = 0; var7 < this.S.length; ++var7) {
                     if (this.S[var7] == var4) {
                        var5 = var7;
                        break;
                     }
                  }
               }

               if (var5 != -1) {
                  int var16 = var5 + 1;
                  String var8 = "economysmpcore.home." + var16;
                  if (!var2.hasPermission(var8)) {
                     this.A(var2, (String)this.Q.get("no-permission"));
                     this.A(var2, Sound.ENTITY_VILLAGER_NO);
                  } else {
                     Location var9 = this.A(var2.getUniqueId(), var16);
                     if (var13) {
                        if (var9 != null) {
                           this.E.put(var2.getUniqueId(), var16);
                           this.B(var2, var16);
                           this.A(var2, Sound.BLOCK_NOTE_BLOCK_HAT);
                        } else {
                           this.A(var2, (String)this.Q.get("home-not-exist"));
                           this.A(var2, Sound.ENTITY_VILLAGER_NO);
                        }
                     } else if (var9 == null) {
                        this.A(var2.getUniqueId(), var16, var2.getLocation());
                        this.A(var2, (String)this.Q.get("home-set"));
                        this.A(var2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                        this.A(var2);
                     } else {
                        var2.closeInventory();
                        this.A(var2, var9);
                     }

                  }
               }
            }
         }
      }
   }

   private void A(InventoryClickEvent var1, Player var2) {
      var1.setCancelled(true);
      UUID var3 = (UUID)this.W.get(var2.getUniqueId());
      if (var3 != null) {
         int var4 = var1.getRawSlot();
         int var5 = -1;

         for(int var6 = 0; var6 < this.S.length; ++var6) {
            if (this.S[var6] == var4) {
               var5 = var6;
               break;
            }
         }

         if (var5 != -1) {
            int var10 = var5 + 1;
            Location var7 = this.A(var3, var10);
            if (var7 == null) {
               this.A(var2, "&cThat player has no home " + var10 + ".");
               this.A(var2, Sound.ENTITY_VILLAGER_NO);
            } else {
               boolean var8 = var1.getClick().isRightClick();
               boolean var9 = var1.getClick().isLeftClick();
               if (var8) {
                  var2.closeInventory();
                  this.A(var2, var7, "home " + var10);
               } else if (var9) {
                  this.C.put(var2.getUniqueId(), var10);
                  this.A(var2, var10);
                  this.A(var2, Sound.BLOCK_NOTE_BLOCK_HAT);
               }

            }
         }
      }
   }

   private void B(Player var1, int var2) {
      int var3 = this.I.B;
      String var4 = ChatColor.translateAlternateColorCodes('&', this.I.F);
      Inventory var5 = Bukkit.createInventory((InventoryHolder)null, var3, var4);
      var5.setItem(this.I.D, this.A(this.I.A, var2));
      var5.setItem(this.I.E, this.A(this.I.C, var2));
      var1.openInventory(var5);
   }

   @EventHandler
   public void onConfirmDeleteClick(InventoryClickEvent var1) {
      HumanEntity var3 = var1.getWhoClicked();
      if (var3 instanceof Player var2) {
         String var5 = ChatColor.translateAlternateColorCodes('&', this.I.F);
         if (var1.getView().getTitle().equals(var5)) {
            var1.setCancelled(true);
            Integer var4 = (Integer)this.E.remove(var2.getUniqueId());
            if (var4 != null) {
               if (var1.getRawSlot() == this.I.E) {
                  if (var4 == -1) {
                     this.C(var2.getUniqueId());
                     this.A(var2, (String)this.Q.get("team-home-deleted"));
                  } else {
                     this.B(var2.getUniqueId(), var4);
                     this.A(var2, (String)this.Q.get("home-deleted"));
                  }

                  this.A(var2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                  this.A(var2);
               } else if (var1.getRawSlot() == this.I.D) {
                  this.A(var2, (String)this.Q.get("delete-cancelled"));
                  this.A(var2, Sound.BLOCK_NOTE_BLOCK_HAT);
                  this.A(var2);
               }

            }
         }
      }
   }

   private void A(Player var1, int var2) {
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 27, K);
      ItemStack var4 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&cᴄᴀɴᴄᴇʟ"));
      var5.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&7Click to cancel")));
      var4.setItemMeta(var5);
      var3.setItem(11, var4);
      ItemStack var6 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var7 = var6.getItemMeta();
      var7.setDisplayName(" ");
      var6.setItemMeta(var7);

      for(int var8 = 0; var8 < 27; ++var8) {
         if (var3.getItem(var8) == null) {
            var3.setItem(var8, var6);
         }
      }

      ItemStack var10 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var9 = var10.getItemMeta();
      var9.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aᴄᴏɴꜰɪʀᴍ ᴅᴇʟᴇᴛᴇ"));
      var9.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&7Delete home &f" + var2 + " &7from this player"), ChatColor.translateAlternateColorCodes('&', "&cThis cannot be undone!")));
      var10.setItemMeta(var9);
      var3.setItem(15, var10);
      var1.openInventory(var3);
   }

   private void B(InventoryClickEvent var1, Player var2) {
      var1.setCancelled(true);
      UUID var3 = (UUID)this.W.get(var2.getUniqueId());
      Integer var4 = (Integer)this.C.remove(var2.getUniqueId());
      if (var3 != null && var4 != null) {
         int var5 = var1.getRawSlot();
         if (var5 == 15) {
            this.B(var3, var4);
            Player var6 = Bukkit.getPlayer(var3);
            String var7 = var6 != null ? var6.getName() : Bukkit.getOfflinePlayer(var3).getName();
            this.A(var2, "&aDeleted home " + var4 + " for " + var7 + ".");
            this.A(var2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
            if (var6 != null) {
               this.A((Player)var2, (OfflinePlayer)var6);
            } else {
               var2.closeInventory();
               this.W.remove(var2.getUniqueId());
            }
         } else if (var5 == 11) {
            this.A(var2, (String)this.Q.get("delete-cancelled"));
            this.A(var2, Sound.BLOCK_NOTE_BLOCK_HAT);
            Player var8 = Bukkit.getPlayer(var3);
            if (var8 != null) {
               this.A((Player)var2, (OfflinePlayer)var8);
            } else {
               var2.closeInventory();
               this.W.remove(var2.getUniqueId());
            }
         }

      }
   }

   private void A(Player var1, Location var2, String var3) {
      this.C(var1);
      var1.setGameMode(GameMode.SPECTATOR);
      this.D(var1);
      var1.teleport(var2);
      this.B(var1, "&aTeleported to " + var3 + ". &7(Spectator + vanished)");
   }

   private void D(Player var1) {
      if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "essentials:vanish " + var1.getName());
      } else {
         for(Player var3 : Bukkit.getOnlinePlayers()) {
            if (!var3.equals(var1)) {
               var3.hidePlayer(this.B, var1);
            }
         }
      }

   }

   private void C(Player var1) {
      if (!var1.getAllowFlight()) {
         var1.setAllowFlight(true);
         var1.setFlying(true);
         this.A.add(var1.getUniqueId());
      }
   }

   private void B(Player var1) {
      if (this.A.remove(var1.getUniqueId())) {
         if (var1.getGameMode() != GameMode.SPECTATOR) {
            var1.setFlying(false);
            var1.setAllowFlight(false);
         }

      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      if (this.A.contains(var3)) {
         Location var4 = var1.getFrom();
         Location var5 = var1.getTo();
         if (var5 != null && (var4.getBlockX() != var5.getBlockX() || var4.getBlockY() != var5.getBlockY() || var4.getBlockZ() != var5.getBlockZ())) {
            this.B(var2);
         }
      }

      if (this.R.containsKey(var3)) {
         Location var7 = var1.getFrom();
         Location var8 = var1.getTo();
         if (var8 != null && (var7.getBlockX() != var8.getBlockX() || var7.getBlockZ() != var8.getBlockZ())) {
            BukkitTask var6 = (BukkitTask)this.R.remove(var3);
            if (var6 != null) {
               var6.cancel();
            }

            this.A(var2, (String)this.Q.get("teleport-cancelled-move"));
            this.A(var2, Sound.ENTITY_VILLAGER_NO);
         }
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      com.h2ph.H.A var4 = this.B.getDonutTeamModule();
      if (var4 != null) {
         com.h2ph.H.A._H var5 = var4.getPlayerTeam(var3);
         if (var5 == null) {
            this.C(var3);
         }
      }

      this.R.remove(var3);
      this.E.remove(var3);
      this.W.remove(var3);
      this.C.remove(var3);
      this.A.remove(var3);
   }

   private File B(UUID var1) {
      return new File(this.B.getDataFolder(), "homes/" + var1.toString() + ".yml");
   }

   private void A(UUID var1, int var2, Location var3) {
      String var4 = "home" + var2;
      File var5 = this.B(var1);
      var5.getParentFile().mkdirs();
      YamlConfiguration var6 = YamlConfiguration.loadConfiguration(var5);
      var6.set(var4 + ".world", var3.getWorld().getName());
      var6.set(var4 + ".x", var3.getX());
      var6.set(var4 + ".y", var3.getY());
      var6.set(var4 + ".z", var3.getZ());
      var6.set(var4 + ".yaw", (double)var3.getYaw());
      var6.set(var4 + ".pitch", (double)var3.getPitch());

      try {
         var6.save(var5);
      } catch (Exception var8) {
      }

   }

   private Location A(UUID var1, int var2) {
      String var3 = "home" + var2;
      File var4 = this.B(var1);
      if (!var4.exists()) {
         return null;
      } else {
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
         if (!var5.contains(var3 + ".world")) {
            return null;
         } else {
            String var6 = var5.getString(var3 + ".world");
            if (Bukkit.getWorld(var6) == null) {
               return null;
            } else {
               double var7 = var5.getDouble(var3 + ".x");
               double var9 = var5.getDouble(var3 + ".y");
               double var11 = var5.getDouble(var3 + ".z");
               float var13 = (float)var5.getDouble(var3 + ".yaw");
               float var14 = (float)var5.getDouble(var3 + ".pitch");
               return new Location(Bukkit.getWorld(var6), var7, var9, var11, var13, var14);
            }
         }
      }
   }

   private void B(UUID var1, int var2) {
      String var3 = "home" + var2;
      File var4 = this.B(var1);
      if (var4.exists()) {
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
         var5.set(var3, (Object)null);

         try {
            var5.save(var4);
         } catch (Exception var7) {
         }

      }
   }

   private void A(UUID var1, Location var2) {
      File var3 = this.B(var1);
      var3.getParentFile().mkdirs();
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      var4.set("teamhome.world", var2.getWorld().getName());
      var4.set("teamhome.x", var2.getX());
      var4.set("teamhome.y", var2.getY());
      var4.set("teamhome.z", var2.getZ());
      var4.set("teamhome.yaw", (double)var2.getYaw());
      var4.set("teamhome.pitch", (double)var2.getPitch());

      try {
         var4.save(var3);
      } catch (Exception var6) {
      }

   }

   private Location A(UUID var1) {
      File var2 = this.B(var1);
      if (!var2.exists()) {
         return null;
      } else {
         YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
         if (!var3.contains("teamhome.world")) {
            return null;
         } else {
            String var4 = var3.getString("teamhome.world");
            if (Bukkit.getWorld(var4) == null) {
               return null;
            } else {
               double var5 = var3.getDouble("teamhome.x");
               double var7 = var3.getDouble("teamhome.y");
               double var9 = var3.getDouble("teamhome.z");
               float var11 = (float)var3.getDouble("teamhome.yaw");
               float var12 = (float)var3.getDouble("teamhome.pitch");
               return new Location(Bukkit.getWorld(var4), var5, var7, var9, var11, var12);
            }
         }
      }
   }

   private void C(UUID var1) {
      File var2 = this.B(var1);
      if (var2.exists()) {
         YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
         var3.set("teamhome", (Object)null);

         try {
            var3.save(var2);
         } catch (Exception var5) {
         }

      }
   }

   private void A(final Player var1, final Location var2) {
      final UUID var3 = var1.getUniqueId();
      BukkitTask var4 = (BukkitTask)this.R.remove(var3);
      if (var4 != null) {
         var4.cancel();
      }

      if (this.F > 0 && !var1.hasPermission("economysmpcore.home.instant")) {
         final Location var5 = var1.getLocation().clone();
         final int[] var6 = new int[]{this.F};
         final BukkitTask[] var7 = new BukkitTask[1];
         Runnable var8 = new Runnable() {
            public void run() {
               if (var1.isOnline() && E.this.R.containsKey(var3)) {
                  Location var1x = var1.getLocation();
                  if (var5.getWorld().equals(var1x.getWorld()) && !(var5.distanceSquared(var1x) > (double)0.25F)) {
                     if (var6[0] <= 0) {
                        E.this.R.remove(var3);
                        if (var7[0] != null) {
                           var7[0].cancel();
                        }

                        E.this.C(var1);
                        var1.teleportAsync(var2);
                        E.this.A(var1, (String)E.this.Q.get("teleport-success"));
                        E.this.A(var1, Sound.ENTITY_ENDERMAN_TELEPORT);
                     } else {
                        String var2x = ((String)E.this.Q.get("teleporting")).replace("%seconds%", String.valueOf(var6[0]));
                        E.this.A(var1, var2x);
                        E.this.A(var1, Sound.BLOCK_NOTE_BLOCK_HAT);
                        int var10002 = var6[0]--;
                     }

                  } else {
                     E.this.R.remove(var3);
                     if (var7[0] != null) {
                        var7[0].cancel();
                     }

                     E.this.A(var1, (String)E.this.Q.get("teleport-cancelled-move"));
                     E.this.A(var1, Sound.ENTITY_VILLAGER_NO);
                  }
               } else {
                  if (var7[0] != null) {
                     var7[0].cancel();
                  }

                  E.this.R.remove(var3);
               }
            }
         };
         BukkitTask var9 = this.B.getSchedulerAdapter().runEntityTaskTimer(var1, var8, 0L, 20L);
         var7[0] = var9;
         this.R.put(var3, var9);
      } else {
         this.C(var1);
         var1.teleportAsync(var2);
         this.A(var1, (String)this.Q.get("teleport-success"));
         this.A(var1, Sound.ENTITY_ENDERMAN_TELEPORT);
      }
   }

   private ItemStack A(_C var1, int var2) {
      if (var1 == null) {
         return new ItemStack(Material.STONE);
      } else {
         Material var3 = Material.matchMaterial(var1.B);
         if (var3 == null) {
            var3 = Material.STONE;
         }

         ItemStack var4 = new ItemStack(var3);
         ItemMeta var5 = var4.getItemMeta();
         if (var5 != null) {
            var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var1.A.replace("%number%", String.valueOf(var2)).replace("%home%", String.valueOf(var2))));
            ArrayList var6 = new ArrayList();

            for(String var8 : var1.C) {
               var6.add(ChatColor.translateAlternateColorCodes('&', var8.replace("%number%", String.valueOf(var2))));
            }

            var5.setLore(var6);
            var4.setItemMeta(var5);
         }

         return var4;
      }
   }

   private void A(Player var1, String var2) {
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var2)));
   }

   private void B(Player var1, String var2) {
      var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2));
   }

   private void A(Player var1, Sound var2) {
      var1.playSound(var1.getLocation(), var2, 1.0F, 1.0F);
   }

   private static class _A {
      String F;
      int B;
      int D;
      int E;
      _C A;
      _C C;

      _A(ConfigurationSection var1) {
         if (var1 == null) {
            this.F = "&8ᴄᴏɴꜰɪʀᴍ ᴅᴇʟᴇᴛᴇ";
            this.B = 27;
            this.D = 11;
            this.E = 15;
            this.A = new _C((ConfigurationSection)null);
            this.C = new _C((ConfigurationSection)null);
         } else {
            this.F = var1.getString("title", "&8ᴄᴏɴꜰɪʀᴍ ᴅᴇʟᴇᴛᴇ");
            this.B = var1.getInt("size", 27);
            this.D = var1.getInt("cancel-slot", 11);
            this.E = var1.getInt("confirm-slot", 15);
            this.A = new _C(var1.getConfigurationSection("cancel-item"));
            this.C = new _C(var1.getConfigurationSection("confirm-item"));
         }
      }
   }

   private static class _B {
      int B;
      int C;
      _C H;
      _C D;
      _C E;
      _C A;
      _C G;
      _C F;

      _B(ConfigurationSection var1) {
         if (var1 == null) {
            this.B = 10;
            this.C = 19;
            this.H = A("RED_BANNER", "&cTeam Home", "&fYou are not in a team.");
            this.D = A("GRAY_BANNER", "&7Team Home", "&fNo team home has been set.");
            this.E = A("LIGHT_BLUE_BANNER", "&bTeam Home", "&fClick to teleport to your team home.");
            this.A = A("RED_DYE", "&cTeam Home", "&fYou are not in a team.");
            this.G = A("GRAY_DYE", "&7Team Home", "&fClick to set your team home here.");
            this.F = A("LIGHT_BLUE_DYE", "&bTeam Home", "&fClick to delete your team home.");
         } else {
            this.B = var1.getInt("teleport-slot", 10);
            this.C = var1.getInt("save-slot", 19);
            this.H = A(var1, "banner.no-team", "RED_BANNER", "&cTeam Home", "&fYou are not in a team.");
            this.D = A(var1, "banner.no-home", "GRAY_BANNER", "&7Team Home", "&fNo team home has been set.");
            this.E = A(var1, "banner.has-home", "LIGHT_BLUE_BANNER", "&bTeam Home", "&fClick to teleport to your team home.");
            this.A = A(var1, "dye.no-team", "RED_DYE", "&cTeam Home", "&fYou are not in a team.");
            this.G = A(var1, "dye.no-home", "GRAY_DYE", "&7Team Home", "&fClick to set your team home here.");
            this.F = A(var1, "dye.has-home", "LIGHT_BLUE_DYE", "&bTeam Home", "&fClick to delete your team home.");
         }
      }

      private static _C A(ConfigurationSection var0, String var1, String var2, String var3, String var4) {
         ConfigurationSection var5 = var0.getConfigurationSection(var1);
         return var5 != null ? new _C(var5) : A(var2, var3, var4);
      }

      private static _C A(String var0, String var1, String var2) {
         YamlConfiguration var3 = new YamlConfiguration();
         var3.set("material", var0);
         var3.set("name", var1);
         var3.set("lore", Collections.singletonList(var2));
         return new _C(var3.getConfigurationSection(""));
      }
   }

   private static class _C {
      String B;
      String A;
      List<String> C;

      _C(ConfigurationSection var1) {
         if (var1 == null) {
            this.B = "STONE";
            this.A = "Default";
            this.C = Collections.emptyList();
         } else {
            this.B = var1.getString("material", "STONE");
            this.A = var1.getString("name", "");
            this.C = var1.getStringList("lore");
         }
      }
   }
}
