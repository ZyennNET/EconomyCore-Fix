package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

public class CrateEffectsManager {
   private final PrismSurvival plugin;
   private final Map<String, List<String>> crateEffectsCache = new HashMap();
   private double time = (double)0.0F;

   public CrateEffectsManager(PrismSurvival var1) {
      this.plugin = var1;
      this.startTask();
   }

   private void startTask() {
      this.plugin.getSchedulerAdapter().runTaskTimer(() -> {
         for(Map.Entry var2 : this.plugin.getCrateLocationRegistry().getAllLocations().entrySet()) {
            Location var3 = (Location)var2.getKey();
            String var4 = (String)var2.getValue();
            if (var3.getWorld() != null && var3.getWorld().isChunkLoaded(var3.getBlockX() >> 4, var3.getBlockZ() >> 4)) {
               List var5 = this.getEffectsForCrate(var4);
               if (var5 != null && !var5.isEmpty()) {
                  for(String var7 : var5) {
                     this.renderEffect(var3.clone().add((double)0.5F, (double)0.5F, (double)0.5F), var7);
                  }
               }
            }
         }

      }, 1L, 4L);
   }

   private List<String> getEffectsForCrate(String var1) {
      if (this.crateEffectsCache.containsKey(var1)) {
         return (List)this.crateEffectsCache.get(var1);
      } else {
         File var2 = new File(this.plugin.getDataFolder(), "crates/crate/" + var1 + "-crate.yml");
         if (!var2.exists()) {
            return null;
         } else {
            YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
            List var4 = ((FileConfiguration)var3).getStringList("effects");
            this.crateEffectsCache.put(var1, var4);
            return var4;
         }
      }
   }

   public void clearCache(String var1) {
      this.crateEffectsCache.remove(var1);
   }

   private void renderEffect(Location var1, String var2) {
      this.time += 0.1;
      if (this.time > (double)1000.0F) {
         this.time = (double)0.0F;
      }

      switch (var2.toUpperCase()) {
         case "HELIX" -> this.renderHelix(var1);
         case "DOUBLE_HELIX" -> this.renderDoubleHelix(var1);
         case "HALO" -> this.renderHalo(var1);
         case "GROUND_RINGS" -> this.renderGroundRings(var1);
         case "VORTEX" -> this.renderVortex(var1);
         case "FOUNTAIN" -> this.renderFountain(var1);
         case "DISCO" -> this.renderDisco(var1);
         case "BEACON" -> this.renderBeacon(var1);
         case "PULSE" -> this.renderPulse(var1);
         case "ORBIT" -> this.renderOrbit(var1);
         case "ENDER" -> this.renderEnder(var1);
         case "TORNADO" -> this.renderTornado(var1);
         case "SPHERE" -> this.renderSphere(var1);
         case "LAVA_DRIP" -> this.renderLavaDrip(var1);
         case "ENCHANT" -> this.renderEnchant(var1);
         case "FLAME_CROWN" -> this.renderFlameCrown(var1);
      }

   }

   private void renderHelix(Location var1) {
      double var2 = 1.2;

      for(double var4 = (double)0.0F; var4 <= (double)2.0F; var4 += 0.1) {
         double var6 = var2 * Math.cos(var4 * (double)4.0F + this.time);
         double var8 = var2 * Math.sin(var4 * (double)4.0F + this.time);
         var1.getWorld().spawnParticle(Particle.FIREWORK, var1.clone().add(var6, var4 - (double)0.5F, var8), 0);
      }

   }

   private void renderDoubleHelix(Location var1) {
      double var2 = (double)1.0F;

      for(double var4 = (double)0.0F; var4 <= (double)2.0F; var4 += 0.2) {
         double var6 = var2 * Math.cos(var4 * (double)3.0F + this.time);
         double var8 = var2 * Math.sin(var4 * (double)3.0F + this.time);
         var1.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, var1.clone().add(var6, var4 - (double)0.5F, var8), 0);
         double var10 = var2 * Math.cos(var4 * (double)3.0F + this.time + Math.PI);
         double var12 = var2 * Math.sin(var4 * (double)3.0F + this.time + Math.PI);
         var1.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, var1.clone().add(var10, var4 - (double)0.5F, var12), 0);
      }

   }

   private void renderHalo(Location var1) {
      double var2 = 0.8;
      double var4 = 1.2 + Math.sin(this.time) * 0.2;

      for(int var6 = 0; var6 < 20; ++var6) {
         double var7 = (Math.PI * 2D) * (double)var6 / (double)20.0F;
         double var9 = var2 * Math.cos(var7 + this.time);
         double var11 = var2 * Math.sin(var7 + this.time);
         var1.getWorld().spawnParticle(Particle.END_ROD, var1.clone().add(var9, var4, var11), 0);
      }

   }

   private void renderGroundRings(Location var1) {
      double var2 = (double)2.0F;
      double var4 = (double)1.0F;
      double var6 = this.time * var4 % var2;

      for(int var8 = 0; var8 < 30; ++var8) {
         double var9 = (Math.PI * 2D) * (double)var8 / (double)30.0F;
         double var11 = var6 * Math.cos(var9);
         double var13 = var6 * Math.sin(var9);
         var1.getWorld().spawnParticle(Particle.WITCH, var1.clone().add(var11, -0.4, var13), 0);
      }

   }

   private void renderVortex(Location var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         double var3 = (this.time * (double)2.0F + (double)(var2 * 2)) % (double)3.0F;
         double var5 = (double)1.5F * ((double)1.0F - var3 / (double)3.0F);
         double var7 = var3 * (double)4.0F + this.time * (double)2.0F;
         double var9 = var5 * Math.cos(var7);
         double var11 = var5 * Math.sin(var7);
         var1.getWorld().spawnParticle(Particle.PORTAL, var1.clone().add(var9, var3 - (double)0.5F, var11), 0);
      }

   }

   private void renderFountain(Location var1) {
      var1.getWorld().spawnParticle(Particle.SPLASH, var1.clone().add((double)0.0F, (double)0.5F, (double)0.0F), 10, 0.2, (double)0.5F, 0.2, 0.1);
      var1.getWorld().spawnParticle(Particle.BUBBLE_POP, var1.clone().add((double)0.0F, (double)0.5F, (double)0.0F), 5, 0.3, 0.3, 0.3, 0.05);
   }

   private void renderDisco(Location var1) {
      if (Math.random() < 0.3) {
         var1.getWorld().spawnParticle(Particle.NOTE, var1.clone().add((double)0.0F, (double)0.5F, (double)0.0F), 3, (double)0.5F, (double)0.5F, (double)0.5F, (double)1.0F);
      }

      double var2 = Math.random();
      double var4 = Math.random();
      double var6 = Math.random();
      Particle.DustOptions var8 = new Particle.DustOptions(Color.fromRGB((int)(var2 * (double)255.0F), (int)(var4 * (double)255.0F), (int)(var6 * (double)255.0F)), 1.5F);
      double var9 = Math.random() * Math.PI * (double)2.0F;
      var1.getWorld().spawnParticle(Particle.DUST, var1.clone().add(Math.cos(var9), Math.random(), Math.sin(var9)), 0, var8);
   }

   private void renderBeacon(Location var1) {
      for(double var2 = (double)0.0F; var2 < (double)4.0F; var2 += (double)0.5F) {
         var1.getWorld().spawnParticle(Particle.INSTANT_EFFECT, var1.clone().add((double)0.0F, var2, (double)0.0F), 0);
         var1.getWorld().spawnParticle(Particle.WAX_OFF, var1.clone().add(0.2, var2, 0.2), 0);
         var1.getWorld().spawnParticle(Particle.WAX_OFF, var1.clone().add(-0.2, var2, -0.2), 0);
         var1.getWorld().spawnParticle(Particle.WAX_OFF, var1.clone().add(0.2, var2, -0.2), 0);
         var1.getWorld().spawnParticle(Particle.WAX_OFF, var1.clone().add(-0.2, var2, 0.2), 0);
      }

   }

   private void renderPulse(Location var1) {
      double var2 = (double)2.5F;
      double var4 = (Math.sin(this.time) + (double)1.0F) / (double)2.0F * var2;

      for(int var6 = 0; var6 < 20; ++var6) {
         double var7 = Math.random() * Math.PI;
         double var9 = Math.random() * Math.PI * (double)2.0F;
         double var11 = var4 * Math.sin(var7) * Math.cos(var9);
         double var13 = var4 * Math.sin(var7) * Math.sin(var9);
         double var15 = var4 * Math.cos(var7);
         var1.getWorld().spawnParticle(Particle.FLAME, var1.clone().add(var11, var13 + (double)0.5F, var15), 0);
      }

   }

   private void renderOrbit(Location var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         double var3 = this.time * (double)2.0F + (double)var2 * 2.0943951023931953;
         double var5 = (double)1.5F * Math.cos(var3);
         double var7 = (double)1.5F * Math.sin(var3);
         double var9 = Math.sin(this.time + (double)var2) * (double)0.5F + (double)0.5F;
         var1.getWorld().spawnParticle(Particle.DRAGON_BREATH, var1.clone().add(var5, var9, var7), 0, (double)0.0F, (double)0.0F, (double)0.0F);
         var1.getWorld().spawnParticle(Particle.WITCH, var1.clone().add(var5, var9, var7), 0);
      }

   }

   private void renderEnder(Location var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         double var3 = Math.random() * Math.PI * (double)2.0F;
         double var5 = (double)2.0F;
         double var7 = var5 * Math.cos(var3);
         double var9 = var5 * Math.sin(var3);
         double var11 = Math.random() * (double)2.0F - (double)0.5F;
         Location var13 = var1.clone().add(var7, var11, var9);
         Vector var14 = var1.toVector().subtract(var13.toVector()).normalize().multiply(0.2);
         var1.getWorld().spawnParticle(Particle.REVERSE_PORTAL, var13, 0, var14.getX(), var14.getY(), var14.getZ(), (double)1.0F);
      }

   }

   private void renderTornado(Location var1) {
      double var2 = (double)3.0F;
      double var4 = (double)1.5F;

      for(double var6 = (double)0.0F; var6 < var2; var6 += 0.2) {
         double var8 = var6 / var2 * var4;
         double var10 = var6 * (double)3.0F + this.time * (double)3.0F;
         double var12 = var8 * Math.cos(var10);
         double var14 = var8 * Math.sin(var10);
         var1.getWorld().spawnParticle(Particle.CLOUD, var1.clone().add(var12, var6, var14), 0, (double)0.0F, (double)0.0F, (double)0.0F);
      }

   }

   private void renderSphere(Location var1) {
      double var2 = (double)1.5F;

      for(int var4 = 0; var4 < 15; ++var4) {
         double var5 = Math.random();
         double var7 = Math.random();
         double var9 = (Math.PI * 2D) * var5;
         double var11 = Math.acos((double)2.0F * var7 - (double)1.0F);
         double var13 = var2 * Math.sin(var11) * Math.cos(var9);
         double var15 = var2 * Math.sin(var11) * Math.sin(var9);
         double var17 = var2 * Math.cos(var11);
         var1.getWorld().spawnParticle(Particle.CRIT, var1.clone().add(var13, var15 + (double)0.5F, var17), 0);
      }

   }

   private void renderLavaDrip(Location var1) {
      if (Math.random() < 0.2) {
         double var2 = (Math.random() - (double)0.5F) * (double)1.5F;
         double var4 = (Math.random() - (double)0.5F) * (double)1.5F;
         var1.getWorld().spawnParticle(Particle.DRIPPING_LAVA, var1.clone().add(var2, (double)2.5F, var4), 0);
      }

   }

   private void renderEnchant(Location var1) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = (Math.random() - (double)0.5F) * (double)3.0F;
         double var5 = (Math.random() - (double)0.5F) * (double)3.0F;
         Location var7 = var1.clone().add(var3, (double)2.0F, var5);
         Vector var8 = var1.toVector().add(new Vector((double)0.0F, (double)0.5F, (double)0.0F)).subtract(var7.toVector()).normalize().multiply(0.2);
         var1.getWorld().spawnParticle(Particle.ENCHANT, var7, 0, var8.getX(), var8.getY(), var8.getZ());
      }

   }

   private void renderFlameCrown(Location var1) {
      double var2 = 0.7;
      double var4 = 1.2;
      byte var6 = 8;

      for(int var7 = 0; var7 < var6; ++var7) {
         double var8 = (Math.PI * 2D) * (double)var7 / (double)var6 + this.time;
         double var10 = var2 * Math.cos(var8);
         double var12 = var2 * Math.sin(var8);
         var1.getWorld().spawnParticle(Particle.FLAME, var1.clone().add(var10, var4, var12), 0, (double)0.0F, 0.05, (double)0.0F);
      }

   }
}
