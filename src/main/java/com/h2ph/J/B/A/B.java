package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.SpawnManager;
import com.sk89q.worldedit.math.BlockVector3;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class B {
   private final PrismSurvival C;
   private final C G;
   private final Map<UUID, UUID> D = new HashMap();
   private final Map<UUID, String> E = new HashMap();
   private final Map<UUID, BukkitTask> L = new HashMap();
   private final Map<UUID, Location> N = new HashMap();
   private final Set<UUID> J = new HashSet();
   private final Map<UUID, BukkitTask> F = new HashMap();
   private final Map<String, List<BlockState>> A = new HashMap();
   private List<String> I = new ArrayList();
   private List<String> M = new ArrayList();
   private final Set<UUID> K = new HashSet();
   private Location B = null;
   private final Map<String, _A> H = new HashMap();

   public PrismSurvival G() {
      return this.C;
   }

   public B(PrismSurvival var1, C var2) {
      this.C = var1;
      this.G = var2;
      this.F();
   }

   public void F() {
      File var1 = new File(this.C.getDataFolder(), "survival/duels/config.yml");
      if (!var1.exists()) {
         try {
            this.C.saveResource("survival/duels/config.yml", false);
         } catch (Exception var4) {
         }
      }

      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      this.I = var2.getStringList("ignored-commands");
      if (this.I == null) {
         this.I = new ArrayList();
      }

      this.M = var2.getStringList("banned-commands");
      if (this.M == null) {
         this.M = new ArrayList();
      }

      if (var2.contains("lobby-spawn.world")) {
         World var3 = Bukkit.getWorld(var2.getString("lobby-spawn.world"));
         if (var3 != null) {
            this.B = new Location(var3, var2.getDouble("lobby-spawn.x"), var2.getDouble("lobby-spawn.y"), var2.getDouble("lobby-spawn.z"), (float)var2.getDouble("lobby-spawn.yaw"), (float)var2.getDouble("lobby-spawn.pitch"));
         } else {
            this.B = null;
         }
      } else {
         this.B = null;
      }

      this.B();
   }

   public void A(Location var1) {
      this.B = var1.clone();
      File var2 = new File(this.C.getDataFolder(), "survival/duels/config.yml");
      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
      var3.set("lobby-spawn.world", var1.getWorld().getName());
      var3.set("lobby-spawn.x", var1.getX());
      var3.set("lobby-spawn.y", var1.getY());
      var3.set("lobby-spawn.z", var1.getZ());
      var3.set("lobby-spawn.yaw", (double)var1.getYaw());
      var3.set("lobby-spawn.pitch", (double)var1.getPitch());

      try {
         var3.save(var2);
      } catch (IOException var5) {
         this.C.getLogger().warning("Failed to save duel lobby spawn to survival/duels/config.yml");
      }

   }

   public Location C() {
      return this.B;
   }

   public void B() {
      this.H.clear();
      com.h2ph.T.B var1 = this.C.getDatabaseManager();
      if (var1.F()) {
         try {
            Connection var2 = var1.G();

            try {
               PreparedStatement var3 = var2.prepareStatement("SELECT * FROM duel_arenas");

               try {
                  ResultSet var4 = var3.executeQuery();

                  while(var4.next()) {
                     String var5 = var4.getString("name");
                     String var6 = var4.getString("data_json");

                     try {
                        YamlConfiguration var7 = new YamlConfiguration();
                        var7.loadFromString(var6);
                        _A var8 = new _A(var5, var7);
                        this.H.put(var5, var8);
                     } catch (Exception var13) {
                        this.C.getLogger().warning("Failed to parse duel arena " + var5 + " from DB");
                     }
                  }
               } catch (Throwable var15) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var12) {
                        var15.addSuppressed(var12);
                     }
                  }

                  throw var15;
               }

               if (var3 != null) {
                  var3.close();
               }
            } catch (Throwable var16) {
               if (var2 != null) {
                  try {
                     var2.close();
                  } catch (Throwable var11) {
                     var16.addSuppressed(var11);
                  }
               }

               throw var16;
            }

            if (var2 != null) {
               var2.close();
            }
         } catch (Exception var17) {
            this.C.getLogger().severe("Failed to load duel arenas from SQL.");
            var17.printStackTrace();
         }
      }

      File var18 = new File(this.C.getDataFolder(), "survival/regions/duels");
      if (!var18.exists()) {
         var18.mkdirs();
      } else {
         File[] var19 = var18.listFiles((var0, var1x) -> var1x.endsWith(".yml"));
         if (var19 != null) {
            for(File var23 : var19) {
               try {
                  String var24 = var23.getName().replace(".yml", "");
                  if (!this.H.containsKey(var24)) {
                     YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var23);
                     if (var9.contains("spawn1.world") || var9.contains("min.x")) {
                        _A var10 = new _A(var24, var9);
                        this.H.put(var24, var10);
                        if (var1.F()) {
                           this.A(var24, var9);
                           this.C.getLogger().info("Migrated duel arena " + var24 + " to SQL.");
                           var23.renameTo(new File(var23.getParent(), var23.getName() + ".migrated"));
                        }
                     }
                  }
               } catch (Exception var14) {
                  this.C.getLogger().warning("Failed to load arena file: " + var23.getName());
                  var14.printStackTrace();
               }
            }
         }
      }

      this.C.getLogger().info("Loaded " + this.H.size() + " duel arenas.");
   }

   public void A(String var1, String var2, BlockVector3 var3, BlockVector3 var4, String var5, String var6) {
      YamlConfiguration var7 = new YamlConfiguration();
      var7.set("world", var2);
      var7.set("min.x", var3.getX());
      var7.set("min.y", var3.getY());
      var7.set("min.z", var3.getZ());
      var7.set("max.x", var4.getX());
      var7.set("max.y", var4.getY());
      var7.set("max.z", var4.getZ());
      var7.set("created-by", var6);
      var7.set("created-at", System.currentTimeMillis());
      var7.set("biome", var5);
      this.A(var1, var7);
      File var8 = new File(this.C.getDataFolder(), "survival/regions/duels/" + var1 + ".yml");

      try {
         if (!var8.getParentFile().exists()) {
            var8.getParentFile().mkdirs();
         }

         var7.save(var8);
      } catch (IOException var10) {
         var10.printStackTrace();
      }

   }

   private void A(String var1, YamlConfiguration var2) {
      com.h2ph.T.B var3 = this.C.getDatabaseManager();
      if (var3.F()) {
         String var4 = var2.saveToString();
         this.C.getSchedulerAdapter().runTaskAsync(() -> {
            try {
               Connection var3x = var3.G();

               try {
                  PreparedStatement var4x = var3x.prepareStatement("INSERT INTO duel_arenas (name, data_json) VALUES (?, ?) ON DUPLICATE KEY UPDATE data_json=VALUES(data_json)");

                  try {
                     var4x.setString(1, var1);
                     var4x.setString(2, var4);
                     var4x.executeUpdate();
                  } catch (Throwable var9) {
                     if (var4x != null) {
                        try {
                           var4x.close();
                        } catch (Throwable var8) {
                           var9.addSuppressed(var8);
                        }
                     }

                     throw var9;
                  }

                  if (var4x != null) {
                     var4x.close();
                  }
               } catch (Throwable var10) {
                  if (var3x != null) {
                     try {
                        var3x.close();
                     } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                     }
                  }

                  throw var10;
               }

               if (var3x != null) {
                  var3x.close();
               }
            } catch (Exception var11) {
               var11.printStackTrace();
            }

         });
      }
   }

   public List<String> E() {
      ArrayList var1 = new ArrayList();

      for(_A var3 : this.H.values()) {
         if (var3.K != null && !var1.contains(var3.K)) {
            var1.add(var3.K);
         }
      }

      Collections.sort(var1);
      return var1;
   }

   public boolean D(String var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         String[] var2 = var1.substring(1).split(" ");
         String var3 = var2[0].toLowerCase();
         return this.I.contains(var3);
      }
   }

   public boolean A(String var1) {
      if (var1.isEmpty()) {
         return false;
      } else {
         String[] var2 = var1.substring(1).split(" ");
         String var3 = var2[0].toLowerCase();
         return this.M.contains(var3);
      }
   }

   public void G(Player var1) {
      this.K.add(var1.getUniqueId());
   }

   public boolean J(Player var1) {
      return this.K.contains(var1.getUniqueId());
   }

   public void M(Player var1) {
      this.N.put(var1.getUniqueId(), var1.getLocation());
   }

   public void A(String var1, BlockState var2) {
      ((List)this.A.computeIfAbsent(var1, (var0) -> new ArrayList())).add(var2);
   }

   public void C(String var1) {
      if (var1 != null) {
         List var2 = (List)this.A.remove(var1);
         if (var2 != null) {
            Collections.reverse(var2);

            for(BlockState var4 : var2) {
               this.C.getSchedulerAdapter().runAtLocation(var4.getLocation(), () -> var4.update(true, false));
            }
         }

         _A var18 = (_A)this.H.get(var1);
         if (var18 != null) {
            World var19 = Bukkit.getWorld(var18.G);
            if (var19 != null) {
               double var5 = var18.H;
               double var7 = var18.F;
               double var9 = var18.E;
               double var11 = var18.C;
               double var13 = var18.A;
               double var15 = var18.L;
               Location var17 = new Location(var19, (var5 + var11) / (double)2.0F, (var7 + var13) / (double)2.0F, (var9 + var15) / (double)2.0F);
               this.C.getSchedulerAdapter().runAtLocation(var17, () -> {
                  for(Entity var14 : var19.getEntities()) {
                     if (!(var14 instanceof Player)) {
                        Location var15x = var14.getLocation();
                        double var16 = var15x.getX();
                        double var18 = var15x.getY();
                        double var20 = var15x.getZ();
                        if (var16 >= var5 && var16 <= var11 && var18 >= var7 && var18 <= var13 && var20 >= var9 && var20 <= var15) {
                           var14.remove();
                        }
                     }
                  }

                  int var24 = (int)Math.floor(var5);
                  int var25 = (int)Math.floor(var7);
                  int var26 = (int)Math.floor(var9);
                  int var27 = (int)Math.ceil(var11);
                  int var17 = (int)Math.ceil(var13);
                  int var28 = (int)Math.ceil(var15);

                  for(int var19x = var24; var19x <= var27; ++var19x) {
                     for(int var29 = var25; var29 <= var17; ++var29) {
                        for(int var21 = var26; var21 <= var28; ++var21) {
                           Block var22 = var19.getBlockAt(var19x, var29, var21);
                           Material var23 = var22.getType();
                           if (var23 == Material.FIRE || var23 == Material.SOUL_FIRE) {
                              var22.setType(Material.AIR);
                           }
                        }
                     }
                  }

               });
            }
         }
      }
   }

   public boolean D(Player var1, Player var2) {
      return this.A(var1, var2, 5, "Random");
   }

   public String A(Player var1) {
      return (String)this.E.get(var1.getUniqueId());
   }

   public boolean A(Player var1, Player var2, int var3, String var4) {
      this.H(var1);
      this.H(var2);
      _A var5 = this.E(var4);
      if (var5 == null) {
         if (!var4.equalsIgnoreCase("Random")) {
            return false;
         }

         var5 = this.E("Random");
      }

      if (var5 == null) {
         return false;
      } else if (var5 == null) {
         return false;
      } else {
         Location var6 = var5.J;
         Location var7 = var5.I;
         var1.teleportAsync(var6);
         var2.teleportAsync(var7);
         this.D.put(var1.getUniqueId(), var2.getUniqueId());
         this.D.put(var2.getUniqueId(), var1.getUniqueId());
         this.E.put(var1.getUniqueId(), var5.D);
         this.E.put(var2.getUniqueId(), var5.D);
         String var8 = ChatColor.translateAlternateColorCodes('&', "&4" + com.h2ph.J.B.A.D.A("casual duel"));
         String var9 = ChatColor.translateAlternateColorCodes('&', "&fFight players and steal their loot.");
         var1.sendTitle(var8, var9, 10, 60, 20);
         var2.sendTitle(var8, var9, 10, 60, 20);
         String var10 = this.G.B(var1.getUniqueId());
         String var11 = this.G.B(var2.getUniqueId());
         String var10001 = var2.getName();
         String var12 = ChatColor.translateAlternateColorCodes('&', "&7Your opponent &a" + var10001 + "&7 has a &5" + var11 + "&7 win rate. Good luck.");
         var10001 = var1.getName();
         String var13 = ChatColor.translateAlternateColorCodes('&', "&7Your opponent &a" + var10001 + "&7 has a &5" + var10 + "&7 win rate. Good luck.");
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var12));
         var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var13));

         try {
            var1.playSound(var1.getLocation(), "ambient.cave", 1.0F, 1.0F);
            var2.playSound(var2.getLocation(), "ambient.cave", 1.0F, 1.0F);
         } catch (Exception var15) {
         }

         this.A(var1, var2, var3 * 60);
         return true;
      }
   }

   private void H(Player var1) {
      if (this.L.containsKey(var1.getUniqueId())) {
         BukkitTask var2 = (BukkitTask)this.L.remove(var1.getUniqueId());
         if (var2 != null) {
            var2.cancel();
         }

         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
         String var3 = (String)this.E.remove(var1.getUniqueId());
         if (var3 != null) {
            this.C(var3);
         }
      }

      if (this.F.containsKey(var1.getUniqueId())) {
         BukkitTask var4 = (BukkitTask)this.F.remove(var1.getUniqueId());
         if (var4 != null) {
            var4.cancel();
         }
      }

   }

   private void A(final Player var1, final Player var2, final int var3) {
      final AtomicReference var4 = new AtomicReference();
      BukkitTask var5 = this.C.getSchedulerAdapter().runEntityTaskTimer(var1, new Runnable() {
         int B = var3;

         public void run() {
            if (var1.isOnline() && var2.isOnline()) {
               if (this.B <= 0) {
                  B.this.B(var1, var2);
                  BukkitTask var5 = (BukkitTask)var4.get();
                  if (var5 != null) {
                     var5.cancel();
                  }

               } else {
                  if (this.B % 60 == 0) {
                     int var4x = this.B / 60;
                     String var2x = var4x == 1 ? "minute" : "minutes";
                     String var3x = ChatColor.translateAlternateColorCodes('&', "&7There are &a" + var4x + " " + var2x + "&f left before the match to end");
                     if (var1.isOnline()) {
                        var1.sendMessage(var3x);
                     }

                     if (var2.isOnline()) {
                        var2.sendMessage(var3x);
                     }
                  }

                  --this.B;
               }
            } else {
               if (!var1.isOnline() && !var2.isOnline()) {
                  BukkitTask var1x = (BukkitTask)var4.get();
                  if (var1x != null) {
                     var1x.cancel();
                  }

                  B.this.F.remove(var1.getUniqueId());
                  B.this.F.remove(var2.getUniqueId());
               }

            }
         }
      }, 20L, 20L);
      var4.set(var5);
      this.F.put(var1.getUniqueId(), var5);
      this.F.put(var2.getUniqueId(), var5);
   }

   public void B(Player var1, Player var2) {
      String var3 = (String)this.E.get(var1.getUniqueId());
      this.A(var1, var2);
      this.E.remove(var1.getUniqueId());
      this.E.remove(var2.getUniqueId());
      String var4 = ChatColor.translateAlternateColorCodes('&', "&7&l" + com.h2ph.J.B.A.D.A("draw"));
      String var5 = ChatColor.translateAlternateColorCodes('&', "&fNo one lost");
      this.A(var1, var4, var5);
      this.A(var2, var4, var5);
      this.C.getSchedulerAdapter().runEntityTaskLater(var1, () -> {
         if (var1.isOnline()) {
            this.C(var1);
         }

         if (var2.isOnline()) {
            this.C(var2);
         }

         this.C(var3);
      }, 60L);
   }

   private void A(Player var1, String var2, String var3) {
      if (var1.isOnline()) {
         var1.sendTitle(var2, var3, 10, 60, 20);
         var1.sendMessage(String.valueOf(ChatColor.GRAY) + "Time limit reached! It's a draw.");
      }

   }

   private void A(Player var1, Player var2) {
      this.D.remove(var1.getUniqueId());
      this.D.remove(var2.getUniqueId());
      if (this.F.containsKey(var1.getUniqueId())) {
         ((BukkitTask)this.F.remove(var1.getUniqueId())).cancel();
      }

      if (this.F.containsKey(var2.getUniqueId())) {
         ((BukkitTask)this.F.remove(var2.getUniqueId())).cancel();
      }

   }

   public void C(Player var1, Player var2) {
      this.A(var1, var2, B._B.C);
   }

   public void A(Player var1, Player var2, _B var3) {
      this.K.remove(var1.getUniqueId());
      this.K.remove(var2.getUniqueId());
      if (!this.N.containsKey(var2.getUniqueId())) {
         this.N.put(var2.getUniqueId(), var2.getLocation());
      }

      String var4 = (String)this.E.get(var1.getUniqueId());
      this.A(var1, var2);
      this.E.remove(var2.getUniqueId());
      this.G.E(var1.getUniqueId());
      this.G.D(var2.getUniqueId());
      int var5 = 5;
      if (var4 != null) {
         _A var6 = (_A)this.H.get(var4);
         if (var6 != null) {
            var5 = var6.B;
         }
      }

      String var21;
      if (var3 == B._B.A) {
         var21 = ChatColor.translateAlternateColorCodes('&', "&4&l" + com.h2ph.J.B.A.D.A("opponent left"));
      } else {
         var21 = ChatColor.translateAlternateColorCodes('&', "&a&l" + com.h2ph.J.B.A.D.A("you won"));
      }

      String var7 = ChatColor.translateAlternateColorCodes('&', "&fGet your loot before the time runs out");
      var1.sendTitle(var21, var7, 10, 60, 20);

      try {
         var1.playSound(var1.getLocation(), "ambient.cave", 1.0F, 1.0F);
      } catch (Exception var20) {
      }

      int var8 = var5 * 60;
      AtomicInteger var9 = new AtomicInteger(var8);
      AtomicReference var10 = new AtomicReference();
      BukkitTask var11 = this.C.getSchedulerAdapter().runEntityTaskTimer(var1, () -> {
         if (!var1.isOnline()) {
            BukkitTask var11 = (BukkitTask)var10.get();
            if (var11 != null) {
               var11.cancel();
            }

            this.L.remove(var1.getUniqueId());
            this.E.remove(var1.getUniqueId());
            this.C(var4);
         } else {
            int var5 = var9.get();
            if (var5 > 0) {
               int var6 = var5 / 60;
               int var7 = var5 % 60;
               String var8 = ChatColor.translateAlternateColorCodes('&', "&7You have &5" + var6 + "m " + var7 + "s&7 to collect the loot");
               var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var8));

               try {
                  var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
               } catch (IllegalArgumentException | NoSuchFieldError var10x) {
               }

               var9.decrementAndGet();
            } else {
               this.C(var1);
               var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
               this.L.remove(var1.getUniqueId());
               this.E.remove(var1.getUniqueId());
               this.C(var4);
               BukkitTask var12 = (BukkitTask)var10.get();
               if (var12 != null) {
                  var12.cancel();
               }
            }

         }
      }, 0L, 20L);
      var10.set(var11);
      this.L.put(var1.getUniqueId(), var11);
      String var12 = ChatColor.translateAlternateColorCodes('&', "&4&l" + com.h2ph.J.B.A.D.A("you lose"));
      String var13 = ChatColor.translateAlternateColorCodes('&', "&fBetter luck next time");
      Location var14 = (Location)this.N.get(var2.getUniqueId());
      Runnable var15 = () -> {
         if (var2.isOnline() && !var2.isDead()) {
            var2.setGameMode(GameMode.SPECTATOR);
            if (var14 != null) {
               var2.teleportAsync(var14);
            }
         }

      };
      if (!var2.isDead()) {
         this.C.getSchedulerAdapter().runEntityTask(var2, var15);
         this.C.getSchedulerAdapter().runEntityTaskLater(var2, var15, 1L);
         this.C.getSchedulerAdapter().runEntityTaskLater(var2, var15, 5L);
         this.C.getSchedulerAdapter().runEntityTaskLater(var2, var15, 10L);
      } else {
         this.C.getSchedulerAdapter().runEntityTaskLater(var2, () -> {
            if (var2.isOnline() && var2.isDead()) {
               var2.spigot().respawn();
            }

            this.C.getSchedulerAdapter().runEntityTaskLater(var2, var15, 1L);
            this.C.getSchedulerAdapter().runEntityTaskLater(var2, var15, 5L);
            this.C.getSchedulerAdapter().runEntityTaskLater(var2, var15, 10L);
         }, 1L);
      }

      var2.sendTitle(var12, var13, 10, 60, 20);

      try {
         var2.playSound(var2.getLocation(), "ambient.cave", 1.0F, 1.0F);
      } catch (Exception var19) {
      }

      AtomicInteger var16 = new AtomicInteger(5);
      AtomicReference var17 = new AtomicReference();
      BukkitTask var18 = this.C.getSchedulerAdapter().runEntityTaskTimer(var2, () -> {
         if (!var2.isOnline()) {
            this.N.remove(var2.getUniqueId());
            this.J.remove(var2.getUniqueId());
            if (var17.get() != null) {
               ((BukkitTask)var17.get()).cancel();
            }

         } else {
            int var4 = var16.get();
            if (var4 > 0) {
               String var5 = ChatColor.translateAlternateColorCodes('&', "&7Teleporting you back in &5" + var4 + " seconds");
               var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var5));

               try {
                  var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
               } catch (IllegalArgumentException | NoSuchFieldError var7) {
                  var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               }

               var16.decrementAndGet();
            } else {
               this.N.remove(var2.getUniqueId());
               if (var2.isDead()) {
                  this.J.add(var2.getUniqueId());
               } else {
                  this.C(var2);
                  var2.setGameMode(GameMode.SURVIVAL);
               }

               var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
               if (var17.get() != null) {
                  ((BukkitTask)var17.get()).cancel();
               }
            }

         }
      }, 0L, 20L);
      var17.set(var18);
   }

   public Location L(Player var1) {
      return (Location)this.N.get(var1.getUniqueId());
   }

   public boolean B(Player var1) {
      if (this.J.contains(var1.getUniqueId())) {
         this.J.remove(var1.getUniqueId());
         return true;
      } else {
         return false;
      }
   }

   public boolean I(Player var1) {
      return this.D.containsKey(var1.getUniqueId());
   }

   public boolean N(Player var1) {
      return this.L.containsKey(var1.getUniqueId());
   }

   public void D(Player var1) {
      UUID var2 = var1.getUniqueId();
      if (this.L.containsKey(var2)) {
         BukkitTask var3 = (BukkitTask)this.L.remove(var2);
         if (var3 != null) {
            var3.cancel();
         }

         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
         String var4 = (String)this.E.remove(var2);
         if (var4 != null) {
            this.C(var4);
         }

         this.C(var1);
      }

   }

   public List<Player> A() {
      ArrayList var1 = new ArrayList();

      for(UUID var3 : this.D.keySet()) {
         Player var4 = Bukkit.getPlayer(var3);
         if (var4 != null) {
            var1.add(var4);
         }
      }

      return var1;
   }

   public void K(Player var1) {
      if (this.I(var1)) {
         this.G(var1);
         var1.setHealth((double)0.0F);
      }
   }

   public Player E(Player var1) {
      UUID var2 = (UUID)this.D.get(var1.getUniqueId());
      return var2 != null ? Bukkit.getPlayer(var2) : null;
   }

   public boolean C(Location var1) {
      return this.B(var1) != null;
   }

   public String B(Location var1) {
      if (var1 != null && var1.getWorld() != null) {
         for(_A var3 : this.H.values()) {
            if (var3.A(var1)) {
               return var3.D;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public void F(Player var1) {
      this.C(var1);
      var1.setGameMode(GameMode.SURVIVAL);
      if (this.N.containsKey(var1.getUniqueId())) {
         this.N.remove(var1.getUniqueId());
      }

   }

   public void C(Player var1) {
      if (this.B != null && this.B.getWorld() != null) {
         var1.teleportAsync(this.B);
      } else {
         SpawnManager.SpawnPoint var2 = this.C.getSpawnManager().getSpawn("spawn");
         if (var2 != null) {
            Location var3 = var2.toBukkitLocation();
            if (var3 != null) {
               var1.teleportAsync(var3);
            } else {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Spawn world not loaded.");
            }
         } else {
            var1.teleportAsync(((World)Bukkit.getWorlds().get(0)).getSpawnLocation());
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Global spawn not set, teleported to world spawn.");
         }

      }
   }

   private _A E(String var1) {
      if (this.H.isEmpty()) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(this.H.values());
         Collections.shuffle(var2);

         for(_A var4 : var2) {
            if (!this.E.containsValue(var4.D)) {
               if (this.A.containsKey(var4.D)) {
                  this.C(var4.D);
               }

               if (var1 == null || var1.equals("Random") || var4.K != null && var4.K.equalsIgnoreCase(var1)) {
                  return var4;
               }
            }
         }

         return null;
      }
   }

   private _A D() {
      return this.E("Random");
   }

   private static class _A {
      final String D;
      final String G;
      final String K;
      final double H;
      final double F;
      final double E;
      final double C;
      final double A;
      final double L;
      final Location J;
      final Location I;
      final int B;

      _A(String var1, YamlConfiguration var2) {
         this.D = var1;
         this.G = var2.getString("world");
         this.K = var2.getString("biome");
         this.B = var2.getInt("looting-minutes", 5);
         this.H = Math.min(var2.getDouble("min.x"), var2.getDouble("max.x"));
         this.F = Math.min(var2.getDouble("min.y"), var2.getDouble("max.y"));
         this.E = Math.min(var2.getDouble("min.z"), var2.getDouble("max.z"));
         this.C = Math.max(var2.getDouble("min.x"), var2.getDouble("max.x"));
         this.A = Math.max(var2.getDouble("min.y"), var2.getDouble("max.y"));
         this.L = Math.max(var2.getDouble("min.z"), var2.getDouble("max.z"));
         this.J = new Location(Bukkit.getWorld(var2.getString("spawn1.world")), (double)var2.getInt("spawn1.x") + (double)0.5F, (double)var2.getInt("spawn1.y"), (double)var2.getInt("spawn1.z") + (double)0.5F, (float)var2.getDouble("spawn1.yaw"), (float)var2.getDouble("spawn1.pitch"));
         this.I = new Location(Bukkit.getWorld(var2.getString("spawn2.world")), (double)var2.getInt("spawn2.x") + (double)0.5F, (double)var2.getInt("spawn2.y"), (double)var2.getInt("spawn2.z") + (double)0.5F, (float)var2.getDouble("spawn2.yaw"), (float)var2.getDouble("spawn2.pitch"));
      }

      boolean A(Location var1) {
         if (var1 != null && var1.getWorld() != null && var1.getWorld().getName().equals(this.G)) {
            double var2 = var1.getX();
            double var4 = var1.getY();
            double var6 = var1.getZ();
            return var2 >= this.H && var2 <= this.C && var4 >= this.F && var4 <= this.A && var6 >= this.E && var6 <= this.L;
         } else {
            return false;
         }
      }
   }

   public static enum _B {
      C,
      A;

      // $FF: synthetic method
      private static _B[] A() {
         return new _B[]{C, A};
      }
   }
}
