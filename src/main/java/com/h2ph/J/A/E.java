package com.h2ph.J.A;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class E implements Listener {
   private final PrismSurvival D;
   private final NamespacedKey E;
   private final NamespacedKey C;
   private final Set<UUID> A = new HashSet();
   private static final String B = "economysmpcore.profile.punishments";

   public E(PrismSurvival var1) {
      this.D = var1;
      this.E = new NamespacedKey(var1, "profile_action");
      this.C = new NamespacedKey(var1, "profile_home_index");
   }

   @EventHandler
   public void onProfileMenuClick(InventoryClickEvent var1) {
      InventoryHolder var3 = var1.getInventory().getHolder();
      if (var3 instanceof D var2) {
         var1.setCancelled(true);
         HumanEntity var4 = var1.getWhoClicked();
         if (var4 instanceof Player var12) {
            ItemStack var13 = var1.getCurrentItem();
            if (var13 != null && var13.hasItemMeta()) {
               ItemMeta var5 = var13.getItemMeta();
               String var6 = (String)var5.getPersistentDataContainer().get(this.E, PersistentDataType.STRING);
               if (var6 != null) {
                  UUID var7 = var2.getTargetUuid();
                  switch (var6) {
                     case "HOME":
                        Integer var14 = (Integer)var5.getPersistentDataContainer().get(this.C, PersistentDataType.INTEGER);
                        if (var14 != null && var14 >= 1 && var14 <= 5) {
                           Location var11 = var2.getHome(var7, var14);
                           if (var11 != null) {
                              var12.closeInventory();
                              this.C(var12, var11, "home " + var14);
                           } else {
                              var12.sendMessage(String.valueOf(ChatColor.RED) + "That home does not exist.");
                           }
                        }
                        break;
                     case "TEAM_HOME":
                        Location var10 = var2.getTeamHome(var7);
                        if (var10 != null) {
                           var12.closeInventory();
                           this.C(var12, var10, "team home");
                        } else {
                           var12.sendMessage(String.valueOf(ChatColor.RED) + "That player has no team home set.");
                        }
                        break;
                     case "PUNISHMENT_HISTORY":
                        if (!var12.hasPermission("economysmpcore.profile.punishments")) {
                           var12.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to view punishment history.");
                           return;
                        }

                        (new A(this.D, var7)).open(var12);
                        break;
                     case "COMING_SOON":
                        var12.sendMessage(String.valueOf(ChatColor.YELLOW) + "This feature is coming soon.");
                     case "NONE":
                  }

               }
            }
         }
      }
   }

   @EventHandler
   public void onProfileMenuDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof D) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onHomesMenuClick(InventoryClickEvent var1) {
      InventoryHolder var3 = var1.getInventory().getHolder();
      if (var3 instanceof C var2) {
         var1.setCancelled(true);
         HumanEntity var4 = var1.getWhoClicked();
         if (var4 instanceof Player var7) {
            int var8 = var1.getRawSlot();
            if (var8 == 45 && var2.hasPreviousPage()) {
               var2.previousPage(var7);
            } else if (var8 == 53 && var2.hasNextPage()) {
               var2.nextPage(var7);
            } else if (var8 == 49) {
               var7.closeInventory();
            } else if (var8 >= 0 && var8 < 45) {
               Location var5 = var2.getHomeAtSlot(var8);
               String var6 = var2.getHomeNameAtSlot(var8);
               if (var5 != null) {
                  var7.closeInventory();
                  this.C(var7, var5, var6);
               }
            }

         }
      }
   }

   @EventHandler
   public void onHomesMenuDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof C) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPunishmentMenuClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() instanceof A) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onPunishmentMenuDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof A) {
         var1.setCancelled(true);
      }

   }

   private void C(Player var1, Location var2, String var3) {
      this.A(var1);
      var1.setGameMode(GameMode.SPECTATOR);
      this.B(var1);
      var1.sendMessage(String.valueOf(ChatColor.YELLOW) + "Loading chunks, please wait...");
      World var4 = var2.getWorld();
      int var5 = var2.getBlockX() >> 4;
      int var6 = var2.getBlockZ() >> 4;
      byte var7 = 2;
      ArrayList var8 = new ArrayList();

      for(int var9 = -var7; var9 <= var7; ++var9) {
         for(int var10 = -var7; var10 <= var7; ++var10) {
            var8.add(var4.getChunkAtAsync(var5 + var9, var6 + var10));
         }
      }

      CompletableFuture.allOf((CompletableFuture[])var8.toArray(new CompletableFuture[0])).thenRun(() -> this.D.getSchedulerAdapter().runTaskLater(() -> {
            if (var1.isOnline()) {
               var1.teleport(var2);
               String var10001 = String.valueOf(ChatColor.GREEN);
               var1.sendMessage(var10001 + "Teleported to " + var3 + ". " + String.valueOf(ChatColor.GRAY) + "(Spectator + vanished)");
            }
         }, 60L));
   }

   private void B(Player var1) {
      if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "essentials:vanish " + var1.getName());
      } else {
         for(Player var3 : Bukkit.getOnlinePlayers()) {
            if (!var3.equals(var1)) {
               var3.hidePlayer(this.D, var1);
            }
         }
      }

   }

   private void A(Player var1) {
      if (!var1.getAllowFlight()) {
         var1.setAllowFlight(true);
         var1.setFlying(true);
         this.A.add(var1.getUniqueId());
      }
   }

   private void C(Player var1) {
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
      if (this.A.contains(var2.getUniqueId())) {
         Location var3 = var1.getFrom();
         Location var4 = var1.getTo();
         if (var4 != null) {
            if (var3.getBlockX() != var4.getBlockX() || var3.getBlockY() != var4.getBlockY() || var3.getBlockZ() != var4.getBlockZ()) {
               this.C(var2);
            }

         }
      }
   }
}
