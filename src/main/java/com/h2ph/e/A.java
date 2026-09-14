package com.h2ph.E;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class A {
   private static final Map<UUID, BukkitTask> D = new HashMap();
   private static final Map<UUID, Location> C = new HashMap();
   private static final Random B = new Random();
   private static final int A = 30;

   public static void A(Player var0, String var1) {
      C(var0, "europe", var1);
   }

   public static void C(Player var0, String var1, String var2) {
      A(var0, var1, var2, false, false);
   }

   public static void A(Player var0, String var1, String var2, boolean var3) {
      A(var0, var1, var2, var3, false);
   }

   public static void A(Player var0, String var1, String var2, boolean var3, boolean var4) {
      PrismSurvival var5 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      C(var0);
      PlayerData var6 = var5.getPlayerDataManager().get(var0.getUniqueId());
      if (!var3 && var6 != null && var6.getRtpCooldown() > System.currentTimeMillis()) {
         long var7 = var6.getRtpCooldown() - System.currentTimeMillis();
         if (var7 > 0L) {
            long var9 = var7 / 1000L + 1L;
            var0.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou can't rtp for another " + var9 + "s"));
            return;
         }
      }

      FileConfiguration var11 = var5.getRTPRegionConfig(var1);
      if (var11 != null && !var11.getBoolean("SUBREGIONS_ENABLED", true)) {
         var2 = var11.getString("DEFAULT_WORLD", var2);
      }

      if (var4) {
         B(var0, var1, var2);
      } else {
         C.put(var0.getUniqueId(), var0.getLocation().clone());
         A(var0, var1, var2);
      }

   }

   public static void A(Player var0, String var1, String var2, String var3) {
      PrismSurvival var4 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      FileConfiguration var5 = var4.getGlobalRTPConfig();
      boolean var6 = var5 != null && var5.getBoolean("CROSS_SERVER_ENABLED", false);
      if (!var6) {
         C(var0, var2, var3);
      } else {
         Logger var10000 = var4.getLogger();
         String var10001 = var0.getName();
         var10000.info("[RTP] Cross-server RTP requested: " + var10001 + " -> " + var1);
         C(var0, var2, var3);
      }
   }

   private static void A(Player var0, String var1, String var2) {
      UUID var3 = var0.getUniqueId();
      int[] var4 = new int[]{5};
      PrismSurvival var5 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      BukkitTask var6 = var5.getSchedulerAdapter().runTaskTimer(() -> {
         Location var5 = (Location)C.get(var3);
         if (var5 == null) {
            C(var0);
         } else if (A(var5, var0.getLocation())) {
            C(var0);
            var0.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cTeleport cancelled because you moved."));
            var0.playSound(var0.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } else {
            if (var4[0] > 0) {
               String var6 = ChatColor.translateAlternateColorCodes('&', "&7Teleporting in &b" + var4[0] + "s");
               var0.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var6));
               var0.playSound(var0.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
               int var10002 = var4[0]--;
            } else {
               C(var0);
               B(var0, var1, var2);
            }

         }
      }, 0L, 20L);
      D.put(var3, var6);
   }

   private static void B(Player var0, String var1, String var2) {
      PrismSurvival var3 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      FileConfiguration var4 = var3.getRTPRegionConfig(var1);
      String var5 = A(var2, var4);
      World var6 = Bukkit.getWorld(var5);
      if (var6 == null) {
         String var10001 = String.valueOf(ChatColor.RED);
         var0.sendMessage(var10001 + "World " + var5 + " is not loaded!");
      } else {
         FileConfiguration var7 = var3.getGlobalRTPConfig();
         List var8 = var7 != null ? var7.getStringList("blacklisted-blocks") : List.of();
         int var9 = 10000;
         int var10 = 50000;
         int var11 = 0;
         int var12 = 0;
         if (var4 != null && var4.contains("worlds." + var2 + ".min")) {
            var9 = var4.getInt("worlds." + var2 + ".min");
            var10 = var4.getInt("worlds." + var2 + ".max");
            var11 = var4.getInt("worlds." + var2 + ".center_x", 0);
            var12 = var4.getInt("worlds." + var2 + ".center_z", 0);
         }

         var0.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Searching for a safe location..."));
         B(var6, var11, var12, var9, var10, var8, 0, var0, var5, var3);
      }
   }

   private static void B(World var0, int var1, int var2, int var3, int var4, List<String> var5, int var6, Player var7, String var8, PrismSurvival var9) {
      if (var7.isOnline()) {
         if (var6 >= 30) {
            var7.sendMessage(String.valueOf(ChatColor.RED) + "Could not find a safe location. Please try again.");
         } else {
            int var10 = var1 + (B.nextBoolean() ? 1 : -1) * (var3 + B.nextInt(Math.max(1, var4 - var3 + 1)));
            int var11 = var2 + (B.nextBoolean() ? 1 : -1) * (var3 + B.nextInt(Math.max(1, var4 - var3 + 1)));
            int var12 = var10 >> 4;
            int var13 = var11 >> 4;
            var0.getChunkAtAsync(var12, var13, true).thenAccept((var12x) -> var9.getSchedulerAdapter().runTask(() -> {
                  Location var12 = A(var0, var10, var11, var5);
                  if (var12 != null) {
                     A(var7, var12, var8, var9);
                  } else {
                     B(var0, var1, var2, var3, var4, var5, var6 + 1, var7, var8, var9);
                  }

               })).exceptionally((var12x) -> {
               var9.getLogger().warning("[RTP] Chunk load failed for " + var10 + "," + var11 + ": " + var12x.getMessage());
               var9.getSchedulerAdapter().runTask(() -> B(var0, var1, var2, var3, var4, var5, var6 + 1, var7, var8, var9));
               return null;
            });
         }
      }
   }

   private static Location A(World var0, int var1, int var2, List<String> var3) {
      if (var0.getEnvironment() == Environment.NETHER) {
         for(int var6 = 100; var6 > 5; --var6) {
            Location var7 = new Location(var0, (double)var1, (double)var6, (double)var2);
            if (A(var7, var3)) {
               return var7.add((double)0.5F, (double)1.0F, (double)0.5F);
            }
         }

         return null;
      } else {
         int var4 = var0.getHighestBlockYAt(var1, var2);
         if (var4 <= var0.getMinHeight() + 1) {
            return null;
         } else {
            Location var5 = new Location(var0, (double)var1, (double)var4, (double)var2);
            return A(var5, var3) ? var5.add((double)0.5F, (double)1.0F, (double)0.5F) : null;
         }
      }
   }

   private static void A(Player var0, Location var1, String var2, PrismSurvival var3) {
      Logger var10000 = var3.getLogger();
      String var10001 = var0.getName();
      var10000.info("[RTP] Teleporting " + var10001 + " to " + var2 + " at " + var1.getBlockX() + ", " + var1.getBlockY() + ", " + var1.getBlockZ());
      var0.teleportAsync(var1).thenAccept((var2x) -> {
         if (var2x) {
            var0.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You teleported to a random location"));
            var0.playSound(var0.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            PlayerData var3x = var3.getPlayerDataManager().get(var0.getUniqueId());
            if (var3x != null) {
               var3x.setRtpCooldown(System.currentTimeMillis() + 15000L);
               var3.getPlayerDataManager().getPlayerDAO().B(var3x);
            }
         } else {
            var0.sendMessage(String.valueOf(ChatColor.RED) + "Teleport failed.");
         }

      });
   }

   private static String A(String var0, FileConfiguration var1) {
      if (var1 != null && var1.contains("worlds." + var0 + ".world")) {
         return var1.getString("worlds." + var0 + ".world");
      } else {
         switch (var0.toLowerCase()) {
            case "nether" -> {
               return "world_nether";
            }
            case "end" -> {
               return "world_the_end";
            }
            default -> {
               return "world";
            }
         }
      }
   }

   private static boolean A(Location var0, List<String> var1) {
      Block var2 = var0.getBlock();
      Material var3 = var2.getType();
      if (var1.contains(var3.name())) {
         return false;
      } else if (!var3.isSolid()) {
         return false;
      } else if (var3 != Material.LAVA && var3 != Material.WATER) {
         Block var4 = var0.clone().add((double)0.0F, (double)1.0F, (double)0.0F).getBlock();
         Block var5 = var0.clone().add((double)0.0F, (double)2.0F, (double)0.0F).getBlock();
         return A(var4) && A(var5);
      } else {
         return false;
      }
   }

   private static boolean A(Block var0) {
      Material var1 = var0.getType();
      if (var1 != Material.LAVA && var1 != Material.WATER) {
         return !var1.isSolid();
      } else {
         return false;
      }
   }

   private static void C(Player var0) {
      UUID var1 = var0.getUniqueId();
      BukkitTask var2 = (BukkitTask)D.remove(var1);
      if (var2 != null) {
         var2.cancel();
      }

      C.remove(var1);
   }

   private static boolean A(Location var0, Location var1) {
      if (!var0.getWorld().equals(var1.getWorld())) {
         return true;
      } else {
         return Math.abs(var0.getX() - var1.getX()) > (double)0.5F || Math.abs(var0.getZ() - var1.getZ()) > (double)0.5F || Math.abs(var0.getY() - var1.getY()) > (double)1.5F;
      }
   }

   public static boolean A(Player var0) {
      return D.containsKey(var0.getUniqueId());
   }

   public static boolean B(Player var0) {
      PrismSurvival var1 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      PlayerData var2 = var1.getPlayerDataManager().get(var0.getUniqueId());
      return var2 != null && var2.getRtpCooldown() > System.currentTimeMillis();
   }

   public static boolean A(String var0) {
      PrismSurvival var1 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      FileConfiguration var2 = var1.getRTPRegionConfig(var0);
      return var2 == null || var2.getBoolean("SUBREGIONS_ENABLED", true);
   }

   public static String B(String var0) {
      PrismSurvival var1 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      FileConfiguration var2 = var1.getRTPRegionConfig(var0);
      return var2 != null ? var2.getString("DEFAULT_WORLD", "overworld") : "overworld";
   }

   public static void A() {
      PrismSurvival var0 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      var0.loadRTPConfig();
      var0.loadGlobalRTPConfig();
      var0.getLogger().info("[RTP] Config reloaded.");
   }

   public static void B() {
   }
}
