package com.h2ph.Y;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class A implements Listener, CommandExecutor {
   private final PrismSurvival H;
   private final Map<UUID, Boolean> B = new HashMap();
   private final Map<UUID, Boolean> J = new HashMap();
   private final Map<UUID, Boolean> L = new HashMap();
   private final Map<UUID, Boolean> A = new HashMap();
   private final Map<UUID, Boolean> K = new HashMap();
   private volatile boolean M = false;
   private volatile boolean F = false;
   private static final int D = 50;
   private static final int I = 2500;
   private static final int G = 15;
   private FileConfiguration E;
   private final File C;

   public A(PrismSurvival var1) {
      this.H = var1;
      this.C = new File(var1.getDataFolder(), "toggle/toggles.yml");
      this.D();
      this.A();
      Bukkit.getPluginManager().registerEvents(this, var1);
      var1.getCommand("totemparticle").setExecutor(this);
      var1.getCommand("explosionparticle").setExecutor(this);
      var1.getCommand("dueltoggle").setExecutor(this);
      var1.getCommand("paymenttoggle").setExecutor(this);
      var1.getCommand("mobtoggle").setExecutor(this);
      var1.getCommand("phantom").setExecutor(this);
      this.C();
      var1.getLogger().info("DonutToggleManager loaded (data stored in toggle/toggles.yml)");
   }

   private void C() {
      try {
         CommandMap var1 = Bukkit.getCommandMap();
         Command var2 = new Command("fc") {
            public boolean execute(CommandSender var1, String var2, String[] var3) {
               if (!(var1 instanceof Player var4)) {
                  var1.sendMessage("§cOnly players can use this.");
                  return true;
               } else {
                  boolean var5 = (Boolean)A.this.K.getOrDefault(var4.getUniqueId(), false);
                  boolean var6 = !var5;
                  A.this.K.put(var4.getUniqueId(), var6);
                  A.this.E();
                  var4.sendMessage(var6 ? "§aFast Crystal §eon" : "§cFast Crystal §coff");
                  return true;
               }
            }
         };
         var2.setDescription("Toggle fast crystal (auto‑explode after placing)");
         var2.setUsage("/fc");
         var1.register(this.H.getDescription().getName().toLowerCase(), var2);
      } catch (Exception var3) {
         this.H.getLogger().warning("Failed to register /fc command: " + var3.getMessage());
      }

   }

   private void D() {
      if (!this.C.exists()) {
         this.C.getParentFile().mkdirs();
         this.E = new YamlConfiguration();

         try {
            this.E.save(this.C);
         } catch (Exception var2) {
            this.H.getLogger().warning("Could not create toggle/toggles.yml: " + var2.getMessage());
         }
      } else {
         this.E = YamlConfiguration.loadConfiguration(this.C);
      }

   }

   private void B() {
      try {
         this.E.save(this.C);
      } catch (Exception var2) {
         this.H.getLogger().warning("Failed to save toggle/toggles.yml: " + var2.getMessage());
      }

   }

   private void A() {
      this.B.clear();
      this.J.clear();
      this.L.clear();
      this.A.clear();
      this.K.clear();
      this.M = this.E.getBoolean("global.phantom-disabled", false);
      this.F = this.E.getBoolean("global.mobspawn-disabled", false);
      if (this.E.contains("totem")) {
         for(String var2 : this.E.getConfigurationSection("totem").getKeys(false)) {
            try {
               this.B.put(UUID.fromString(var2), this.E.getBoolean("totem." + var2));
            } catch (IllegalArgumentException var8) {
            }
         }
      }

      if (this.E.contains("explosion")) {
         for(String var13 : this.E.getConfigurationSection("explosion").getKeys(false)) {
            try {
               this.J.put(UUID.fromString(var13), this.E.getBoolean("explosion." + var13));
            } catch (IllegalArgumentException var7) {
            }
         }
      }

      if (this.E.contains("duel")) {
         for(String var14 : this.E.getConfigurationSection("duel").getKeys(false)) {
            try {
               this.L.put(UUID.fromString(var14), this.E.getBoolean("duel." + var14));
            } catch (IllegalArgumentException var6) {
            }
         }
      }

      if (this.E.contains("payment")) {
         for(String var15 : this.E.getConfigurationSection("payment").getKeys(false)) {
            try {
               this.A.put(UUID.fromString(var15), this.E.getBoolean("payment." + var15));
            } catch (IllegalArgumentException var5) {
            }
         }
      }

      if (this.E.contains("fastcrystal")) {
         for(String var16 : this.E.getConfigurationSection("fastcrystal").getKeys(false)) {
            try {
               this.K.put(UUID.fromString(var16), this.E.getBoolean("fastcrystal." + var16));
            } catch (IllegalArgumentException var4) {
            }
         }
      }

   }

   private void E() {
      this.E.set("totem", (Object)null);
      this.E.set("explosion", (Object)null);
      this.E.set("duel", (Object)null);
      this.E.set("payment", (Object)null);
      this.E.set("fastcrystal", (Object)null);

      for(Map.Entry var2 : this.B.entrySet()) {
         this.E.set("totem." + ((UUID)var2.getKey()).toString(), var2.getValue());
      }

      for(Map.Entry var7 : this.J.entrySet()) {
         this.E.set("explosion." + ((UUID)var7.getKey()).toString(), var7.getValue());
      }

      for(Map.Entry var8 : this.L.entrySet()) {
         this.E.set("duel." + ((UUID)var8.getKey()).toString(), var8.getValue());
      }

      for(Map.Entry var9 : this.A.entrySet()) {
         this.E.set("payment." + ((UUID)var9.getKey()).toString(), var9.getValue());
      }

      for(Map.Entry var10 : this.K.entrySet()) {
         this.E.set("fastcrystal." + ((UUID)var10.getKey()).toString(), var10.getValue());
      }

      this.E.set("global.phantom-disabled", this.M);
      this.E.set("global.mobspawn-disabled", this.F);
      this.B();
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      String var5 = var2.getName().toLowerCase();
      if (var5.equals("phantom")) {
         return this.handleGlobalPhantomToggle(var1);
      } else if (var5.equals("mobtoggle")) {
         return this.handleGlobalMobToggle(var1);
      } else if (var1 instanceof Player) {
         Player var6 = (Player)var1;
         switch (var5) {
            case "totemparticle":
               if (var4.length == 0) {
                  var6.sendMessage("Usage: /totemparticle on/off");
                  return true;
               }

               if (var4[0].equalsIgnoreCase("on")) {
                  this.C(var6, true);
                  var6.sendMessage("§aTotem particles enabled (state saved).");
               } else if (var4[0].equalsIgnoreCase("off")) {
                  this.C(var6, false);
                  var6.sendMessage("§cTotem particles disabled (state saved).");
               } else {
                  var6.sendMessage("Usage: /totemparticle on/off");
               }

               return true;
            case "explosionparticle":
               if (var4.length == 0) {
                  var6.sendMessage("Usage: /explosionparticle on/off");
                  return true;
               }

               if (var4[0].equalsIgnoreCase("on")) {
                  this.B(var6, true);
                  var6.sendMessage("§aExplosion particles enabled (state saved).");
               } else if (var4[0].equalsIgnoreCase("off")) {
                  this.B(var6, false);
                  var6.sendMessage("§cExplosion particles disabled (state saved).");
               } else {
                  var6.sendMessage("Usage: /explosionparticle on/off");
               }

               return true;
            case "dueltoggle":
               if (var4.length == 0) {
                  var6.sendMessage("Usage: /dueltoggle on/off");
                  return true;
               }

               if (var4[0].equalsIgnoreCase("on")) {
                  this.A(var6, true);
                  var6.sendMessage("§aDuel requests enabled.");
               } else if (var4[0].equalsIgnoreCase("off")) {
                  this.A(var6, false);
                  var6.sendMessage("§cDuel requests disabled.");
               } else {
                  var6.sendMessage("Usage: /dueltoggle on/off");
               }

               return true;
            case "paymenttoggle":
               if (var4.length == 0) {
                  var6.sendMessage("Usage: /paymenttoggle on/off");
                  return true;
               }

               if (var4[0].equalsIgnoreCase("on")) {
                  this.D(var6, true);
                  var6.sendMessage("§aPayments enabled.");
               } else if (var4[0].equalsIgnoreCase("off")) {
                  this.D(var6, false);
                  var6.sendMessage("§cPayments disabled.");
               } else {
                  var6.sendMessage("Usage: /paymenttoggle on/off");
               }

               return true;
            default:
               return false;
         }
      } else {
         var1.sendMessage("Only players can use this.");
         return true;
      }
   }

   private boolean C(Player var1) {
      return (Boolean)this.B.getOrDefault(var1.getUniqueId(), true);
   }

   private boolean A(Player var1) {
      return (Boolean)this.J.getOrDefault(var1.getUniqueId(), true);
   }

   private boolean D(Player var1) {
      return (Boolean)this.L.getOrDefault(var1.getUniqueId(), true);
   }

   private boolean B(Player var1) {
      return (Boolean)this.A.getOrDefault(var1.getUniqueId(), true);
   }

   private void C(Player var1, boolean var2) {
      this.B.put(var1.getUniqueId(), var2);
      this.E();
   }

   private void B(Player var1, boolean var2) {
      this.J.put(var1.getUniqueId(), var2);
      this.E();
   }

   private void A(Player var1, boolean var2) {
      this.L.put(var1.getUniqueId(), var2);
      this.E();
   }

   private void D(Player var1, boolean var2) {
      this.A.put(var1.getUniqueId(), var2);
      this.E();
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var1.getMessage();
      String[] var4 = var3.split(" ");
      if (var4.length >= 2) {
         if (var4[0].equalsIgnoreCase("/duel")) {
            Player var5 = Bukkit.getPlayer(var4[1]);
            if (var5 != null && !this.D(var5)) {
               var1.setCancelled(true);
               var2.sendMessage("§c" + var5.getName() + " has disabled duel requests.");
            }
         } else if (var4[0].equalsIgnoreCase("/pay") && var4.length >= 3) {
            Player var6 = Bukkit.getPlayer(var4[1]);
            if (var6 != null && !this.B(var6)) {
               var1.setCancelled(true);
               var2.sendMessage("§c" + var6.getName() + " has disabled payments.");
            }
         }

      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      if (!this.B.containsKey(var3)) {
         this.B.put(var3, true);
      }

      if (!this.J.containsKey(var3)) {
         this.J.put(var3, true);
      }

      if (!this.L.containsKey(var3)) {
         this.L.put(var3, true);
      }

      if (!this.A.containsKey(var3)) {
         this.A.put(var3, true);
      }

      if (!this.K.containsKey(var3)) {
         this.K.put(var3, false);
      }

   }

   public boolean isGlobalPhantomDisabled() {
      return this.M;
   }

   public boolean isGlobalMobSpawnDisabled() {
      return this.F;
   }

   public boolean handleGlobalPhantomToggle(CommandSender var1) {
      if (var1 instanceof Player var2) {
         if (!var2.hasPermission("economysmpcore.phantom.toggle")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
            return true;
         }
      }

      this.M = !this.M;
      this.E();
      var1.sendMessage(this.A("&7Phantom Spawning " + (this.M ? "&cDisabled" : "&aEnabled")));
      if (this.M) {
         this.B(true);
      }

      return true;
   }

   public boolean handleGlobalMobToggle(CommandSender var1) {
      if (var1 instanceof Player var2) {
         if (!var2.hasPermission("economysmpcore.mobtoggle.toggle")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to do that.");
            return true;
         }
      }

      this.F = !this.F;
      this.E();
      var1.sendMessage(this.A("&7Hostile Mob Spawning " + (this.F ? "&cDisabled" : "&aEnabled")));
      if (this.F) {
         this.B(false);
      }

      return true;
   }

   private String A(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private boolean A(Location var1) {
      if (var1.getWorld() == null) {
         return false;
      } else {
         byte var2 = 2;
         int var3 = var1.getBlockX();
         int var4 = var1.getBlockY();
         int var5 = var1.getBlockZ();

         for(int var6 = -var2; var6 <= var2; ++var6) {
            for(int var7 = -var2; var7 <= var2; ++var7) {
               for(int var8 = -var2; var8 <= var2; ++var8) {
                  if (var1.getWorld().getBlockAt(var3 + var6, var4 + var7, var5 + var8).getType() == Material.TRIAL_SPAWNER) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private boolean A(Entity var1) {
      if (var1 instanceof Player) {
         return false;
      } else {
         return var1 instanceof Monster || var1 instanceof Slime || var1 instanceof Ghast || var1 instanceof Shulker;
      }
   }

   private void B(boolean var1) {
      Bukkit.getScheduler().runTask(this.H, () -> {
         final ArrayDeque var2 = new ArrayDeque();
         HashSet var3 = new HashSet();

         for(Player var5 : Bukkit.getOnlinePlayers()) {
            Location var6 = var5.getLocation();

            for(Entity var8 : var5.getNearbyEntities((double)50.0F, (double)50.0F, (double)50.0F)) {
               if (var8 instanceof LivingEntity) {
                  boolean var9 = var1 ? var8.getType() == EntityType.PHANTOM : this.A(var8);
                  if (var9 && !(var6.distanceSquared(var8.getLocation()) > (double)2500.0F) && var3.add(var8.getUniqueId())) {
                     var2.add(var8);
                  }
               }
            }
         }

         if (!var2.isEmpty()) {
            (new BukkitRunnable(this) {
               public void run() {
                  for(int var1 = 0; var1 < 15 && !var2.isEmpty(); ++var1) {
                     Entity var2x = (Entity)var2.poll();
                     if (var2x != null && !var2x.isDead()) {
                        var2x.remove();
                     }
                  }

                  if (var2.isEmpty()) {
                     this.cancel();
                  }

               }
            }).runTaskTimer(this.H, 0L, 1L);
         }
      });
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onMobSpawn(CreatureSpawnEvent var1) {
      CreatureSpawnEvent.SpawnReason var2 = var1.getSpawnReason();
      if (var2 != SpawnReason.SPAWNER && var2 != SpawnReason.TRIAL_SPAWNER) {
         LivingEntity var3 = var1.getEntity();
         Location var4 = var3.getLocation();
         if (var2 != SpawnReason.DEFAULT && var2 != SpawnReason.CHUNK_GEN || !this.A(var4)) {
            boolean var5 = var3.getType() == EntityType.PHANTOM;
            boolean var6 = this.A((Entity)var3);
            if (var5 || var6) {
               if (var5 && !this.M) {
                  var5 = false;
               }

               if (var6 && !this.F) {
                  var6 = false;
               }

               if (var5 || var6) {
                  for(Player var8 : Bukkit.getOnlinePlayers()) {
                     if (var8.getWorld().equals(var4.getWorld())) {
                        double var9 = var8.getLocation().distanceSquared(var4);
                        if (!(var9 > (double)2500.0F)) {
                           var1.setCancelled(true);
                           return;
                        }
                     }
                  }

               }
            }
         }
      }
   }

   public boolean isFastCrystalEnabled(UUID var1) {
      return (Boolean)this.K.getOrDefault(var1, false);
   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public void onEntityPlace(EntityPlaceEvent var1) {
      if (var1.getEntity() instanceof EnderCrystal) {
         Player var2 = var1.getPlayer();
         if (var2 != null) {
            UUID var3 = var2.getUniqueId();
            if ((Boolean)this.K.getOrDefault(var3, false)) {
               final EnderCrystal var4 = (EnderCrystal)var1.getEntity();
               (new BukkitRunnable(this) {
                  public void run() {
                     if (!var4.isDead()) {
                        Location var1 = var4.getLocation();
                        var4.getWorld().createExplosion(var1, 6.0F, false, false);
                        var4.remove();
                     }
                  }
               }).runTaskLater(this.H, 2L);
            }
         }
      }
   }
}
