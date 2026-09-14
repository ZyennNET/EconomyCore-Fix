package com.h2ph.Q;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class A implements Listener, CommandExecutor, TabCompleter {
   private final PrismSurvival E;
   private int D = 5;
   private FileConfiguration A;
   private FileConfiguration B;
   private final File C;

   public A(PrismSurvival var1) {
      this.E = var1;
      File var2 = new File(var1.getDataFolder(), "spawn");
      if (!var2.exists()) {
         var2.mkdirs();
      }

      this.C = new File(var2, "location.yml");
      this.B();
      this.reloadConfig();
      this.reloadMessages();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("spawn").setExecutor(this);
      var1.getCommand("spawn").setTabCompleter(this);
      var1.getCommand("setspawn").setExecutor(this);
      var1.getCommand("setspawn").setTabCompleter(this);
      var1.getCommand("reloadspawnconfig").setExecutor(this);
      var1.getCommand("reloadspawnmessages").setExecutor(this);
   }

   private void B() {
      if (!this.C.exists()) {
         this.B = new YamlConfiguration();

         try {
            this.B.save(this.C);
         } catch (Exception var2) {
            this.E.getLogger().warning("Could not create spawn/location.yml");
         }
      } else {
         this.B = YamlConfiguration.loadConfiguration(this.C);
      }

   }

   public void reloadConfig() {
      File var1 = new File(this.E.getDataFolder(), "spawn/config.yml");
      if (!var1.exists()) {
         this.E.saveResource("spawn/config.yml", false);
      }

      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      this.D = ((FileConfiguration)var2).getInt("teleport-cooldown", 5);
   }

   public void reloadMessages() {
      File var1 = new File(this.E.getDataFolder(), "spawn/messages.yml");
      if (!var1.exists()) {
         this.E.saveResource("spawn/messages.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(var1);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage(this.A(this.A.getString("only-player", "&cOnly players can use this command.")));
         return true;
      } else {
         Player var5 = (Player)var1;
         switch (var2.getName().toLowerCase()) {
            case "spawn":
               if (var5.hasPermission("donutspawn.bypass")) {
                  this.A(var5, true);
                  return true;
               } else {
                  Location var8 = this.A();
                  if (var8 == null) {
                     var5.sendMessage(this.A(this.A.getString("spawn-location-not-set", "&cSpawn location not set!")));
                     return true;
                  }

                  this.A(var5, var8);
                  return true;
               }
            case "setspawn":
               if (!var5.hasPermission("donutspawn.setspawn")) {
                  var5.sendMessage(this.A(this.A.getString("no-permission", "&cYou don't have permission.")));
                  return true;
               }

               this.A(var5.getLocation());
               var5.sendMessage(this.A(this.A.getString("set-spawn-success", "&aSpawn location set!")));
               return true;
            case "reloadspawnconfig":
               if (!var5.hasPermission("donutspawn.reloadconfig")) {
                  var5.sendMessage(this.A(this.A.getString("no-permission", "&cYou don't have permission.")));
                  return true;
               }

               this.reloadConfig();
               var5.sendMessage(this.A(this.A.getString("reload-config-success", "&aSpawn config reloaded!")));
               return true;
            case "reloadspawnmessages":
               if (!var5.hasPermission("donutspawn.reloadmessages")) {
                  var5.sendMessage(this.A(this.A.getString("no-permission", "&cYou don't have permission.")));
                  return true;
               }

               this.reloadMessages();
               var5.sendMessage(this.A(this.A.getString("reload-messages-success", "&aMessages reloaded!")));
               return true;
            default:
               return false;
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      return new ArrayList();
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var2.hasPlayedBefore()) {
         this.A(var2, false);
      }

   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent var1) {
      Player var2 = var1.getPlayer();
      if (!var2.hasPlayedBefore()) {
         this.A(var2, true);
      } else {
         if (var2.getBedSpawnLocation() == null) {
            Location var3 = this.A();
            if (var3 != null) {
               var1.setRespawnLocation(var3);
            }
         }

      }
   }

   private void A(final Player var1, final Location var2) {
      (new BukkitRunnable() {
         int countdown;
         Location startLoc;

         {
            this.countdown = A.this.D;
            this.startLoc = var1.getLocation().clone();
         }

         public void run() {
            if (var1.isOnline() && this.startLoc.getWorld().equals(var1.getWorld()) && !(this.startLoc.distanceSquared(var1.getLocation()) > (double)1.0F)) {
               var1.playSound(var1.getLocation(), Sound.BLOCK_TRIPWIRE_ATTACH, 1.0F, 1.0F);
               String var1x = A.this.A.getString("teleport-countdown", "&eTeleporting in %countdown%...").replace("%countdown%", String.valueOf(this.countdown));
               var1.sendActionBar(A.this.A(var1x));
               var1.sendMessage(A.this.A(var1x));
               if (this.countdown == 1) {
                  var1.teleport(var2);
                  var1.playSound(var1.getLocation(), Sound.BLOCK_TRIPWIRE_ATTACH, 100.0F, 1.0F);
                  var1.sendActionBar(A.this.A(A.this.A.getString("teleport-success", "&aTeleported!")));
                  var1.sendMessage(A.this.A(A.this.A.getString("teleport-success", "&aTeleported!")));
                  this.cancel();
               } else {
                  --this.countdown;
               }

            } else {
               var1.sendActionBar(A.this.A(A.this.A.getString("teleport-cancelled", "&cTeleport cancelled!")));
               var1.sendMessage(A.this.A(A.this.A.getString("teleport-cancelled", "&cTeleport cancelled!")));
               this.cancel();
            }
         }
      }).runTaskTimer(this.E, 0L, 20L);
   }

   private void A(Player var1, boolean var2) {
      Location var3 = this.A();
      if (var3 == null) {
         var1.sendMessage(this.A(this.A.getString("spawn-location-not-set", "&cSpawn not set.")));
      } else {
         var1.teleport(var3);
         var1.playSound(var1.getLocation(), Sound.BLOCK_TRIPWIRE_ATTACH, 1.0F, 1.0F);
         if (var2) {
            var1.sendMessage(this.A(this.A.getString("teleport-direct", "&aYou were teleported to spawn.")));
         }

      }
   }

   private Location A() {
      if (!this.B.contains("spawn")) {
         return null;
      } else {
         String var1 = this.B.getString("spawn.world");
         if (var1 == null) {
            return null;
         } else {
            double var2 = this.B.getDouble("spawn.x");
            double var4 = this.B.getDouble("spawn.y");
            double var6 = this.B.getDouble("spawn.z");
            float var8 = (float)this.B.getDouble("spawn.yaw");
            float var9 = (float)this.B.getDouble("spawn.pitch");
            return new Location(this.E.getServer().getWorld(var1), var2, var4, var6, var8, var9);
         }
      }
   }

   private void A(Location var1) {
      this.B.set("spawn.world", var1.getWorld().getName());
      this.B.set("spawn.x", var1.getX());
      this.B.set("spawn.y", var1.getY());
      this.B.set("spawn.z", var1.getZ());
      this.B.set("spawn.yaw", var1.getYaw());
      this.B.set("spawn.pitch", var1.getPitch());

      try {
         this.B.save(this.C);
      } catch (Exception var3) {
         this.E.getLogger().warning("Failed to save spawn location: " + var3.getMessage());
      }

   }

   private String A(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }
}
