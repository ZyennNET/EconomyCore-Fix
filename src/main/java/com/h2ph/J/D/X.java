package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import com.prismcore.survival.manager.SpawnManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class X implements CommandExecutor, TabCompleter, Listener {
   private final PrismSurvival B;
   public static final String GUI_TITLE = ChatColor.translateAlternateColorCodes('&', "&8ѕᴘᴀᴡɴ");
   private final Map<UUID, BukkitTask> A = new ConcurrentHashMap();

   public X(PrismSurvival var1) {
      this.B = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         return true;
      } else if (this.B.getSpawnManager() == null) {
         this.A(var5);
         return true;
      } else {
         this.B.getSpawnManager().refreshSpawnsAsync(() -> {
            if (var4.length >= 1) {
               String var3 = var4[0];

               try {
                  int var4x = Integer.parseInt(var3);
                  ArrayList var12 = new ArrayList();
                  if (this.B.getSpawnManager() != null) {
                     try {
                        var12.addAll(this.B.getSpawnManager().listSpawns());
                     } catch (Throwable var10) {
                     }
                  }

                  if (!var12.isEmpty() && var4x >= 1 && var4x <= var12.size()) {
                     String var6 = (String)var12.get(var4x - 1);
                     SpawnManager.SpawnPoint var7 = null;
                     if (this.B.getSpawnManager() != null) {
                        try {
                           var7 = this.B.getSpawnManager().getSpawn(var6);
                        } catch (Throwable var9) {
                        }
                     }

                     if (var7 != null) {
                        this.A(var5, var7, String.valueOf(var4x));
                     } else {
                        this.A(var5);
                     }

                  } else {
                     this.A(var5);
                  }
               } catch (NumberFormatException var11) {
                  if (this.B.getSpawnManager() != null) {
                     SpawnManager.SpawnPoint var5x = this.B.getSpawnManager().getSpawn(var3);
                     if (var5x != null) {
                        this.A(var5, var5x, var3);
                        return;
                     }
                  }

                  this.A(var5);
               }
            } else {
               this.A(var5);
            }
         });
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 1) {
         return Collections.emptyList();
      } else {
         String var5 = var4[0].toLowerCase();
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();

         try {
            if (this.B.getSpawnManager() != null) {
               var7.addAll(this.B.getSpawnManager().listSpawns());
            }
         } catch (Throwable var11) {
         }

         for(int var8 = 1; var8 <= var7.size(); ++var8) {
            var6.add(String.valueOf(var8));
         }

         ArrayList var12 = new ArrayList();

         for(String var10 : var6) {
            if (var10.toLowerCase().startsWith(var5)) {
               var12.add(var10);
            }
         }

         return var12;
      }
   }

   private void A(Player var1, SpawnManager.SpawnPoint var2, String var3) {
      String var6 = this.B.getSurvivalConfig().getString("current-region", "unknown");
      String var4;
      Location var5;
      if (!var2.getRegion().equalsIgnoreCase(var6) && !var2.getRegion().equalsIgnoreCase("unknown")) {
         String var7 = var2.getRegion();
         String var8 = var7;
         FileConfiguration var9 = this.B.getRTPRegionConfig(var7);
         if (var9 != null) {
            var8 = var9.getString("server", var7);
         }

         var4 = var8;
         var5 = null;
      } else {
         var4 = null;
         var5 = var2.toBukkitLocation();
         if (var5 == null) {
            String var10001 = String.valueOf(ChatColor.RED);
            var1.sendMessage(var10001 + "Spawn world '" + var2.getWorldName() + "' is not loaded on this server.");
            return;
         }
      }

      if (this.A.containsKey(var1.getUniqueId())) {
         String var11 = String.valueOf(ChatColor.RED) + "You are already teleporting.";
         var1.sendMessage(var11);
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var11));
         var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      } else {
         Location var10 = var1.getLocation();
         AtomicInteger var12 = new AtomicInteger(5);
         BukkitTask var13 = this.B.getSchedulerAdapter().runEntityTaskTimer(var1, () -> {
            if (this.A.containsKey(var1.getUniqueId())) {
               if (!var1.isOnline()) {
                  BukkitTask var13 = (BukkitTask)this.A.remove(var1.getUniqueId());
                  if (var13 != null) {
                     var13.cancel();
                  }

               } else {
                  Location var8 = var1.getLocation();
                  if (var10.getWorld() == var8.getWorld() && !(Math.pow(var10.getX() - var8.getX(), (double)2.0F) + Math.pow(var10.getZ() - var8.getZ(), (double)2.0F) > (double)0.25F) && !(Math.abs(var10.getY() - var8.getY()) > (double)1.5F)) {
                     int var14 = var12.getAndDecrement();
                     if (var14 > 0) {
                        String var10000 = String.valueOf(ChatColor.GRAY);
                        String var15 = var10000 + "Teleporting in " + String.valueOf(ChatColor.DARK_PURPLE) + var14 + "s";
                        var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var15));
                        var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                     } else {
                        BukkitTask var16 = (BukkitTask)this.A.remove(var1.getUniqueId());
                        if (var16 != null) {
                           var16.cancel();
                        }

                        if (var4 != null) {
                           int var11 = this.B.getTeleportQueueManager().getQueuePosition();
                           String var17 = String.valueOf(ChatColor.GRAY);
                           String var12x = var17 + "You position is at queue " + String.valueOf(ChatColor.DARK_PURPLE) + "#" + var11;
                           var1.sendMessage(var12x);
                           var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var12x));
                           this.B.getTeleportQueueManager().submit(var1, () -> {
                              try {
                                 PlayerData var4x = this.B.getPlayerDataManager().get(var1.getUniqueId());
                                 if (var4x != null) {
                                    var4x.setPendingSpawnName(var2.getName());
                                    var4x.setPendingSpawnWorld(var2.getWorldName());
                                    var4x.setPendingSpawnX(var2.getX());
                                    var4x.setPendingSpawnY(var2.getY());
                                    var4x.setPendingSpawnZ(var2.getZ());
                                    var4x.setPendingSpawnYaw(var2.getYaw());
                                    var4x.setPendingSpawnPitch(var2.getPitch());
                                    var4x.setPendingRtpTargetServer(var4);
                                    if (this.B.getPlayerDataManager().getPlayerDAO() != null) {
                                       this.B.getPlayerDataManager().getPlayerDAO().A(var4x);
                                    }
                                 }

                                 this.B.getSchedulerAdapter().runTask(() -> com.h2ph.b.F.A(var1, var4));
                              } catch (Exception var5) {
                                 var5.printStackTrace();
                              }

                           });
                        } else {
                           var1.teleportAsync(var5).thenAccept((var2x) -> {
                              if (var2x) {
                                 var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                                 String var10000 = String.valueOf(ChatColor.GRAY);
                                 String var3x = var10000 + "You teleported to " + String.valueOf(ChatColor.DARK_PURPLE) + "ѕᴘᴀᴡɴ " + var3;
                                 var1.sendMessage(var3x);
                                 var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var3x));
                              }

                           });
                        }
                     }

                  } else {
                     String var9 = String.valueOf(ChatColor.RED) + "Teleport cancelled because you moved.";
                     var1.sendMessage(var9);
                     var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var9));
                     var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     BukkitTask var10x = (BukkitTask)this.A.remove(var1.getUniqueId());
                     if (var10x != null) {
                        var10x.cancel();
                     }

                  }
               }
            }
         }, 1L, 20L);
         this.A.put(var1.getUniqueId(), var13);
      }
   }

   private void A(Player var1) {
      ArrayList var2 = new ArrayList();
      if (this.B.getSpawnManager() != null) {
         try {
            var2.addAll(this.B.getSpawnManager().listSpawns());
         } catch (Throwable var17) {
         }
      }

      byte var3 = 54;
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, var3, GUI_TITLE);

      for(int var5 = 0; var5 < var2.size() && var5 < var3; ++var5) {
         String var6 = (String)var2.get(var5);
         ItemStack var7 = new ItemStack(Material.GLOW_ITEM_FRAME);
         ItemMeta var8 = var7.getItemMeta();
         if (var8 != null) {
            String var9 = ChatColor.translateAlternateColorCodes('&', "&5ѕᴘᴀᴡɴ " + (var5 + 1));
            var8.setDisplayName(var9);
            ArrayList var10 = new ArrayList();
            SpawnManager.SpawnPoint var11 = null;

            try {
               var11 = this.B.getSpawnManager().getSpawn(var6);
            } catch (Throwable var16) {
            }

            String var12 = var11 != null && var11.getRegion() != null ? var11.getRegion() : "unknown";
            int var13 = 0;
            int var14 = 0;
            com.h2ph.T.C._A var15 = this.B.getServerStatusManager().A(var12);
            if (var15 != null) {
               var13 = var15.C();
               var14 = var15.B();
            }

            if (var14 == 0) {
               var14 = Bukkit.getMaxPlayers();
            }

            var10.add(ChatColor.translateAlternateColorCodes('&', "&8" + var13 + "/" + var14));
            var10.add(ChatColor.translateAlternateColorCodes('&', "&7Click to go to this"));
            var10.add(ChatColor.translateAlternateColorCodes('&', "&7Spawn area."));
            var8.setLore(var10);
            var8.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
            var7.setItemMeta(var8);
         }

         var4.setItem(var5, var7);
      }

      ItemStack var18 = new ItemStack(Material.BEACON);
      ItemMeta var19 = var18.getItemMeta();
      if (var19 != null) {
         var19.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aѕᴘᴀᴡɴ"));
         ArrayList var20 = new ArrayList();
         var20.add(ChatColor.translateAlternateColorCodes('&', "&fClick to teleport to a random spawn"));
         var19.setLore(var20);
         var18.setItemMeta(var19);
      }

      var4.setItem(49, var18);
      var1.openInventory(var4);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getView().getTitle().equals(GUI_TITLE)) {
         var1.setCancelled(true);
         if (var1.getWhoClicked() instanceof Player) {
            Player var2 = (Player)var1.getWhoClicked();
            ItemStack var3 = var1.getCurrentItem();
            if (var3 != null && var3.getType() != Material.AIR) {
               if (var3.getType() == Material.GLOW_ITEM_FRAME) {
                  int var4 = var1.getRawSlot();
                  ArrayList var5 = new ArrayList();

                  try {
                     if (this.B.getSpawnManager() != null) {
                        var5.addAll(this.B.getSpawnManager().listSpawns());
                     }
                  } catch (Throwable var9) {
                  }

                  if (var4 >= 0 && var4 < var5.size()) {
                     String var6 = (String)var5.get(var4);
                     SpawnManager.SpawnPoint var7 = this.B.getSpawnManager().getSpawn(var6);
                     if (var7 != null) {
                        var2.closeInventory();
                        this.A(var2, var7, String.valueOf(var4 + 1));
                     }
                  }
               } else if (var3.getType() == Material.BEACON) {
                  ArrayList var10 = new ArrayList();

                  try {
                     if (this.B.getSpawnManager() != null) {
                        var10.addAll(this.B.getSpawnManager().listSpawns());
                     }
                  } catch (Throwable var8) {
                  }

                  if (!var10.isEmpty()) {
                     int var11 = ThreadLocalRandom.current().nextInt(var10.size());
                     String var12 = (String)var10.get(var11);
                     SpawnManager.SpawnPoint var13 = this.B.getSpawnManager().getSpawn(var12);
                     if (var13 != null) {
                        var2.closeInventory();
                        this.A(var2, var13, String.valueOf(var11 + 1));
                     }
                  }
               }

            }
         }
      }
   }
}
