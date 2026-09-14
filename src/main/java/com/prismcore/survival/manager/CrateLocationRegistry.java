package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CrateLocationRegistry {
   private final PrismSurvival plugin;
   private final File file;
   private FileConfiguration config;
   private final Map<Location, String> crateLocations = new ConcurrentHashMap();

   public CrateLocationRegistry(PrismSurvival var1) {
      this.plugin = var1;
      this.file = new File(var1.getDataFolder(), "crates/locations.yml");
      this.load();
   }

   public void load() {
      if (this.file.exists()) {
         this.config = YamlConfiguration.loadConfiguration(this.file);
         this.crateLocations.clear();
         if (this.config.contains("crates")) {
            ConfigurationSection var1 = this.config.getConfigurationSection("crates");

            for(String var3 : var1.getKeys(false)) {
               String var4 = var1.getString(var3 + ".world");
               double var5 = var1.getDouble(var3 + ".x");
               double var7 = var1.getDouble(var3 + ".y");
               double var9 = var1.getDouble(var3 + ".z");
               String var11 = var1.getString(var3 + ".crate");
               if (var4 != null && var11 != null) {
                  World var12 = Bukkit.getWorld(var4);
                  if (var12 != null) {
                     Location var13 = new Location(var12, var5, var7, var9);
                     this.crateLocations.put(var13, var11);
                  }
               }
            }
         }

      }
   }

   public void save() {
      this.config = new YamlConfiguration();
      int var1 = 0;

      for(Map.Entry var3 : this.crateLocations.entrySet()) {
         Location var4 = (Location)var3.getKey();
         String var5 = (String)var3.getValue();
         String var6 = "crates." + var1;
         this.config.set(var6 + ".world", var4.getWorld().getName());
         this.config.set(var6 + ".x", var4.getX());
         this.config.set(var6 + ".y", var4.getY());
         this.config.set(var6 + ".z", var4.getZ());
         this.config.set(var6 + ".crate", var5);
         ++var1;
      }

      try {
         this.config.save(this.file);
      } catch (IOException var7) {
         this.plugin.getLogger().severe("Failed to save crate locations!");
         var7.printStackTrace();
      }

   }

   public void addLocation(String var1, Location var2) {
      Location var3 = new Location(var2.getWorld(), (double)var2.getBlockX(), (double)var2.getBlockY(), (double)var2.getBlockZ());
      this.crateLocations.put(var3, var1);
      this.save();
   }

   public void removeLocation(Location var1) {
      Location var2 = new Location(var1.getWorld(), (double)var1.getBlockX(), (double)var1.getBlockY(), (double)var1.getBlockZ());
      this.crateLocations.remove(var2);
      this.save();
   }

   public String getCrateName(Location var1) {
      Location var2 = new Location(var1.getWorld(), (double)var1.getBlockX(), (double)var1.getBlockY(), (double)var1.getBlockZ());
      return (String)this.crateLocations.get(var2);
   }

   public Map<Location, String> getAllLocations() {
      return Collections.unmodifiableMap(this.crateLocations);
   }
}
