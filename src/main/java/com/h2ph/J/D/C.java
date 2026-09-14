package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class C implements CommandExecutor, TabCompleter, Listener {
   private final PrismSurvival E;
   private FileConfiguration A;
   private final File H;
   private FileConfiguration D;
   private final File G;
   private FileConfiguration C;
   private final File B;
   private final Map<UUID, BukkitRunnable> F = new HashMap();

   public C(PrismSurvival var1) {
      this.E = var1;
      this.H = new File(var1.getDataFolder(), "warps.yml");
      this.G = new File(var1.getDataFolder(), "warps/messages.yml");
      this.B = new File(var1.getDataFolder(), "warps/config.yml");
      this.reloadAll();
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   public void reloadAll() {
      if (!this.H.exists()) {
         this.E.saveResource("warps.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(this.H);
      if (!this.G.exists()) {
         this.E.saveResource("warps/messages.yml", false);
      }

      this.D = YamlConfiguration.loadConfiguration(this.G);
      if (!this.B.exists()) {
         this.E.saveResource("warps/config.yml", false);
      }

      this.C = YamlConfiguration.loadConfiguration(this.B);
   }

   private void A() {
      try {
         this.A.save(this.H);
      } catch (Exception var2) {
         this.E.getLogger().warning("Failed to save warps.yml: " + var2.getMessage());
      }

   }

   private String B(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private String A(String var1, String... var2) {
      String var3 = this.D.getString(var1, "&c[Missing message: " + var1 + "]");

      for(int var4 = 0; var4 + 1 < var2.length; var4 += 2) {
         var3 = var3.replace("%" + var2[var4] + "%", var2[var4 + 1]);
      }

      return this.B(var3);
   }

   private void A(String var1, Location var2) {
      this.A.set("warps." + var1 + ".world", var2.getWorld().getName());
      this.A.set("warps." + var1 + ".x", var2.getX());
      this.A.set("warps." + var1 + ".y", var2.getY());
      this.A.set("warps." + var1 + ".z", var2.getZ());
      this.A.set("warps." + var1 + ".yaw", var2.getYaw());
      this.A.set("warps." + var1 + ".pitch", var2.getPitch());
      this.A();
   }

   private Location A(String var1) {
      if (!this.A.contains("warps." + var1)) {
         return null;
      } else {
         String var2 = this.A.getString("warps." + var1 + ".world");
         double var3 = this.A.getDouble("warps." + var1 + ".x");
         double var5 = this.A.getDouble("warps." + var1 + ".y");
         double var7 = this.A.getDouble("warps." + var1 + ".z");
         float var9 = (float)this.A.getDouble("warps." + var1 + ".yaw");
         float var10 = (float)this.A.getDouble("warps." + var1 + ".pitch");
         return new Location(Bukkit.getWorld(var2), var3, var5, var7, var9, var10);
      }
   }

   private List<String> B() {
      return !this.A.contains("warps") ? new ArrayList() : new ArrayList(((ConfigurationSection)Objects.requireNonNull(this.A.getConfigurationSection("warps"))).getKeys(false));
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof final Player var5)) {
         var1.sendMessage("Only players can use this command.");
         return true;
      } else if (var2.getName().equalsIgnoreCase("warp")) {
         if (var4.length == 1 && var4[0].equalsIgnoreCase("reload")) {
            if (!var5.hasPermission("economysmpcore.warp.admin")) {
               var5.sendMessage(this.A("no-permission-reload"));
               return true;
            } else {
               this.reloadAll();
               var5.sendMessage(this.A("reload-success"));
               return true;
            }
         } else if (var4.length == 0) {
            List var10 = this.B();
            if (var10.isEmpty()) {
               var5.sendMessage(this.A("no-warps"));
            } else {
               var5.sendMessage(this.A("warp-list", "warps", String.join(", ", var10)));
               var5.sendMessage(this.A("warp-list-usage"));
            }

            return true;
         } else {
            final String var6 = var4[0];
            final Location var7 = this.A(var6);
            if (var7 == null) {
               var5.sendMessage(this.A("warp-not-found", "warp", var6));
               return true;
            } else if (this.F.containsKey(var5.getUniqueId())) {
               var5.sendMessage(this.A("already-teleporting", "warp", var6));
               return true;
            } else {
               final int[] var8 = new int[]{this.C.getInt("teleport-countdown-seconds", 5)};
               BukkitRunnable var9 = new BukkitRunnable() {
                  public void run() {
                     if (!var5.isOnline()) {
                        this.cancel();
                        C.this.F.remove(var5.getUniqueId());
                     } else if (var8[0] <= 0) {
                        var5.teleport(var7);
                        var5.sendMessage(C.this.A("teleport-success", "warp", var6));
                        C.this.F.remove(var5.getUniqueId());
                        this.cancel();
                     } else {
                        var5.sendMessage(C.this.A("teleport-countdown", "time", String.valueOf(var8[0])));
                        var5.playSound(var5.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
                        int var10002 = var8[0]--;
                     }
                  }
               };
               var9.runTaskTimer(this.E, 0L, 20L);
               this.F.put(var5.getUniqueId(), var9);
               return true;
            }
         }
      } else if (var2.getName().equalsIgnoreCase("setwarp")) {
         if (!var5.hasPermission("economysmpcore.warp.admin")) {
            var5.sendMessage(this.A("no-permission-setwarp"));
            return true;
         } else if (var4.length != 1) {
            var5.sendMessage(this.A("setwarp-usage"));
            return true;
         } else {
            this.A(var4[0], var5.getLocation());
            var5.sendMessage(this.A("setwarp-success", "warp", var4[0]));
            return true;
         }
      } else if (var2.getName().equalsIgnoreCase("delwarp")) {
         if (!var5.hasPermission("economysmpcore.warp.admin")) {
            var5.sendMessage(this.A("no-permission-delwarp"));
            return true;
         } else if (var4.length != 1) {
            var5.sendMessage(this.A("delwarp-usage"));
            return true;
         } else if (!this.A.contains("warps." + var4[0])) {
            var5.sendMessage(this.A("warp-not-exist"));
            return true;
         } else {
            this.A.set("warps." + var4[0], (Object)null);
            this.A();
            var5.sendMessage(this.A("delwarp-success", "warp", var4[0]));
            return true;
         }
      } else {
         return false;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         return Collections.emptyList();
      } else if (var2.getName().equalsIgnoreCase("warp") && var4.length == 1) {
         List var5 = this.B();
         return (List)var5.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
      } else {
         return Collections.emptyList();
      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent var1) {
      if (this.C.getBoolean("cancel-on-move", true)) {
         Player var2 = var1.getPlayer();
         if (this.F.containsKey(var2.getUniqueId())) {
            Location var3 = var1.getFrom();
            Location var4 = var1.getTo();
            if (var3.getBlockX() != var4.getBlockX() || var3.getBlockZ() != var4.getBlockZ() || Math.abs(var3.getY() - var4.getY()) > (double)1.5F) {
               BukkitRunnable var5 = (BukkitRunnable)this.F.remove(var2.getUniqueId());
               if (var5 != null) {
                  var5.cancel();
               }

               var2.sendMessage(this.A("teleport-cancelled-move"));
               var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            }
         }

      }
   }
}
