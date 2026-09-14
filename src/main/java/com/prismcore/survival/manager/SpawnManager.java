package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import com.h2ph.T.B;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SpawnManager {
   private final PrismSurvival plugin;
   private final File file;
   private FileConfiguration config;
   private final Map<String, SpawnPoint> spawnCache = new ConcurrentHashMap();

   public SpawnManager(PrismSurvival var1) {
      this.plugin = var1;
      this.file = new File(var1.getDataFolder(), "survival/regions/spawn/locations.yml");
      this.loadSpawns();
   }

   public void refreshSpawnsAsync(Runnable var1) {
      this.plugin.getSchedulerAdapter().runTaskAsync(() -> {
         this.loadSpawns();
         if (var1 != null) {
            this.plugin.getSchedulerAdapter().runTask(var1);
         }

      });
   }

   public void loadSpawns() {
      HashMap var1 = new HashMap();
      B var2 = this.plugin.getDatabaseManager();
      if (var2.F()) {
         try {
            Connection var3 = var2.G();

            try {
               PreparedStatement var4 = var3.prepareStatement("SELECT * FROM spawn_locations");

               try {
                  ResultSet var5 = var4.executeQuery();

                  while(var5.next()) {
                     String var6 = var5.getString("name");
                     String var7 = var5.getString("world");
                     String var8 = var5.getString("region");
                     SpawnPoint var9 = new SpawnPoint(var6, var8, var7, var5.getDouble("x"), var5.getDouble("y"), var5.getDouble("z"), var5.getFloat("yaw"), var5.getFloat("pitch"));
                     var1.put(var6.toLowerCase(), var9);
                  }
               } catch (Throwable var19) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var18) {
                        var19.addSuppressed(var18);
                     }
                  }

                  throw var19;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var20) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var17) {
                     var20.addSuppressed(var17);
                  }
               }

               throw var20;
            }

            if (var3 != null) {
               var3.close();
            }
         } catch (Exception var21) {
            this.plugin.getLogger().severe("Failed to load spawn locations from SQL.");
            var21.printStackTrace();
         }
      }

      if (this.file.exists()) {
         this.reloadConfig();
         if (this.config != null && this.config.isConfigurationSection("spawns")) {
            String var22 = this.plugin.getSurvivalConfig().getString("current-region", "unknown");

            for(String var24 : this.config.getConfigurationSection("spawns").getKeys(false)) {
               if (!var1.containsKey(var24.toLowerCase())) {
                  String var25 = "spawns." + var24;
                  String var26 = this.config.getString(var25 + ".world");
                  if (var26 != null) {
                     double var27 = this.config.getDouble(var25 + ".x");
                     double var10 = this.config.getDouble(var25 + ".y");
                     double var12 = this.config.getDouble(var25 + ".z");
                     float var14 = (float)this.config.getDouble(var25 + ".yaw");
                     float var15 = (float)this.config.getDouble(var25 + ".pitch");
                     SpawnPoint var16 = new SpawnPoint(var24, var22, var26, var27, var10, var12, var14, var15);
                     var1.put(var24.toLowerCase(), var16);
                     if (var2.F()) {
                        this.saveSpawnToDB(var16);
                     }
                  }
               }
            }
         }
      }

      this.spawnCache.clear();
      this.spawnCache.putAll(var1);
   }

   public void reloadConfig() {
      if (this.file.exists()) {
         try {
            List var1 = Files.readAllLines(this.file.toPath());
            String var2 = (String)var1.stream().filter((var0) -> !var0.trim().startsWith("==: org.bukkit.Location")).collect(Collectors.joining("\n"));
            this.config = new YamlConfiguration();
            this.config.loadFromString(var2);
         } catch (Exception var3) {
            this.config = new YamlConfiguration();
         }

      }
   }

   private void saveSpawnToDB(SpawnPoint var1) {
      B var2 = this.plugin.getDatabaseManager();
      if (var2.F()) {
         this.plugin.getSchedulerAdapter().runTaskAsync(() -> {
            try {
               Connection var2x = var2.G();

               try {
                  PreparedStatement var3 = var2x.prepareStatement("INSERT INTO spawn_locations (name, world, x, y, z, yaw, pitch, region) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE world=VALUES(world), x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch), region=VALUES(region)");

                  try {
                     var3.setString(1, var1.getName().toLowerCase());
                     var3.setString(2, var1.getWorldName());
                     var3.setDouble(3, var1.getX());
                     var3.setDouble(4, var1.getY());
                     var3.setDouble(5, var1.getZ());
                     var3.setFloat(6, var1.getYaw());
                     var3.setFloat(7, var1.getPitch());
                     var3.setString(8, var1.getRegion());
                     var3.executeUpdate();
                  } catch (Throwable var8) {
                     if (var3 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var7) {
                           var8.addSuppressed(var7);
                        }
                     }

                     throw var8;
                  }

                  if (var3 != null) {
                     var3.close();
                  }
               } catch (Throwable var9) {
                  if (var2x != null) {
                     try {
                        var2x.close();
                     } catch (Throwable var6) {
                        var9.addSuppressed(var6);
                     }
                  }

                  throw var9;
               }

               if (var2x != null) {
                  var2x.close();
               }
            } catch (Exception var10) {
               var10.printStackTrace();
            }

         });
      }
   }

   public boolean saveSpawn(String var1, Location var2) {
      String var3 = this.plugin.getSurvivalConfig().getString("current-region", "unknown");
      SpawnPoint var4 = new SpawnPoint(var1, var3, var2.getWorld().getName(), var2.getX(), var2.getY(), var2.getZ(), var2.getYaw(), var2.getPitch());
      this.spawnCache.put(var1.toLowerCase(), var4);
      this.saveSpawnToDB(var4);
      return true;
   }

   public boolean deleteSpawn(String var1) {
      if (!this.spawnCache.containsKey(var1.toLowerCase())) {
         return false;
      } else {
         this.spawnCache.remove(var1.toLowerCase());
         B var2 = this.plugin.getDatabaseManager();
         if (var2.F()) {
            this.plugin.getSchedulerAdapter().runTaskAsync(() -> {
               try {
                  Connection var2x = var2.G();

                  try {
                     PreparedStatement var3 = var2x.prepareStatement("DELETE FROM spawn_locations WHERE name = ?");

                     try {
                        var3.setString(1, var1.toLowerCase());
                        var3.executeUpdate();
                     } catch (Throwable var8) {
                        if (var3 != null) {
                           try {
                              var3.close();
                           } catch (Throwable var7) {
                              var8.addSuppressed(var7);
                           }
                        }

                        throw var8;
                     }

                     if (var3 != null) {
                        var3.close();
                     }
                  } catch (Throwable var9) {
                     if (var2x != null) {
                        try {
                           var2x.close();
                        } catch (Throwable var6) {
                           var9.addSuppressed(var6);
                        }
                     }

                     throw var9;
                  }

                  if (var2x != null) {
                     var2x.close();
                  }
               } catch (Exception var10) {
                  var10.printStackTrace();
               }

            });
         }

         return true;
      }
   }

   public SpawnPoint getSpawn(String var1) {
      return (SpawnPoint)this.spawnCache.get(var1.toLowerCase());
   }

   public List<String> listSpawns() {
      return new ArrayList(this.spawnCache.keySet());
   }

   public static class SpawnPoint {
      private final String name;
      private final String region;
      private final String worldName;
      private final double x;
      private final double y;
      private final double z;
      private final float yaw;
      private final float pitch;

      public SpawnPoint(String var1, String var2, String var3, double var4, double var6, double var8, float var10, float var11) {
         this.name = var1;
         this.region = var2;
         this.worldName = var3;
         this.x = var4;
         this.y = var6;
         this.z = var8;
         this.yaw = var10;
         this.pitch = var11;
      }

      public Location toBukkitLocation() {
         World var1 = Bukkit.getWorld(this.worldName);
         return var1 == null ? null : new Location(var1, this.x, this.y, this.z, this.yaw, this.pitch);
      }

      public String getName() {
         return this.name;
      }

      public String getRegion() {
         return this.region != null ? this.region : "unknown";
      }

      public String getWorldName() {
         return this.worldName;
      }

      public double getX() {
         return this.x;
      }

      public double getY() {
         return this.y;
      }

      public double getZ() {
         return this.z;
      }

      public float getYaw() {
         return this.yaw;
      }

      public float getPitch() {
         return this.pitch;
      }
   }
}
