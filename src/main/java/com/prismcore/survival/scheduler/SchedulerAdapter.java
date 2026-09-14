package com.prismcore.survival.scheduler;

import com.h2ph.PrismSurvival;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class SchedulerAdapter {
   private final PrismSurvival plugin;

   public SchedulerAdapter(PrismSurvival var1) {
      this.plugin = var1;
   }

   public void runTask(Runnable var1) {
      try {
         Bukkit.getGlobalRegionScheduler().run(this.plugin, (var1x) -> var1.run());
      } catch (NoClassDefFoundError | NoSuchMethodError var3) {
         Bukkit.getScheduler().runTask(this.plugin, var1);
      }

   }

   public void runTaskAsync(Runnable var1) {
      try {
         Bukkit.getAsyncScheduler().runNow(this.plugin, (var1x) -> var1.run());
      } catch (NoClassDefFoundError | NoSuchMethodError var3) {
         Bukkit.getScheduler().runTaskAsynchronously(this.plugin, var1);
      }

   }

   public void runTaskAsynchronously(Runnable var1) {
      this.runTaskAsync(var1);
   }

   public BukkitTask runTaskLater(Runnable var1, long var2) {
      try {
         ScheduledTask var4 = Bukkit.getGlobalRegionScheduler().runDelayed(this.plugin, (var1x) -> var1.run(), var2);
         return new FoliaBukkitTaskWrapper(var4);
      } catch (NoClassDefFoundError | NoSuchMethodError var5) {
         return Bukkit.getScheduler().runTaskLater(this.plugin, var1, var2);
      }
   }

   public void runTaskLaterAsync(Runnable var1, long var2) {
      try {
         Bukkit.getAsyncScheduler().runDelayed(this.plugin, (var1x) -> var1.run(), var2 * 50L, TimeUnit.MILLISECONDS);
      } catch (NoClassDefFoundError | NoSuchMethodError var5) {
         Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, var1, var2);
      }

   }

   public BukkitTask runTaskTimer(Runnable var1, long var2, long var4) {
      try {
         ScheduledTask var6 = Bukkit.getGlobalRegionScheduler().runAtFixedRate(this.plugin, (var1x) -> var1.run(), Math.max(1L, var2), var4);
         return new FoliaBukkitTaskWrapper(var6);
      } catch (NoClassDefFoundError | NoSuchMethodError var7) {
         return Bukkit.getScheduler().runTaskTimer(this.plugin, var1, var2, var4);
      }
   }

   public BukkitTask runTaskTimerAsync(Runnable var1, long var2, long var4) {
      try {
         ScheduledTask var6 = Bukkit.getAsyncScheduler().runAtFixedRate(this.plugin, (var1x) -> var1.run(), var2 * 50L, var4 * 50L, TimeUnit.MILLISECONDS);
         return new FoliaBukkitTaskWrapper(var6);
      } catch (NoClassDefFoundError | NoSuchMethodError var7) {
         return Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, var1, var2, var4);
      }
   }

   public void runEntityTask(Entity var1, Runnable var2) {
      try {
         var1.getScheduler().run(this.plugin, (var1x) -> var2.run(), (Runnable)null);
      } catch (NoClassDefFoundError | NoSuchMethodError var4) {
         Bukkit.getScheduler().runTask(this.plugin, var2);
      }

   }

   public void runEntityTaskLater(Entity var1, Runnable var2, long var3) {
      try {
         var1.getScheduler().runDelayed(this.plugin, (var1x) -> var2.run(), (Runnable)null, var3);
      } catch (NoClassDefFoundError | NoSuchMethodError var6) {
         Bukkit.getScheduler().runTaskLater(this.plugin, var2, var3);
      }

   }

   public BukkitTask runEntityTaskTimer(Entity var1, Runnable var2, long var3, long var5) {
      try {
         long var7 = Math.max(1L, var3);
         ScheduledTask var9 = var1.getScheduler().runAtFixedRate(this.plugin, (var1x) -> var2.run(), (Runnable)null, var7, var5);
         return new FoliaBukkitTaskWrapper(var9);
      } catch (NoClassDefFoundError | NoSuchMethodError var10) {
         return Bukkit.getScheduler().runTaskTimer(this.plugin, var2, var3, var5);
      }
   }

   public void runAtLocation(Location var1, Runnable var2) {
      try {
         Bukkit.getRegionScheduler().execute(this.plugin, var1, var2);
      } catch (NoClassDefFoundError | NoSuchMethodError var4) {
         Bukkit.getScheduler().runTask(this.plugin, var2);
      }

   }

   private static class FoliaBukkitTaskWrapper implements BukkitTask {
      private final Object foliaTask;

      public FoliaBukkitTaskWrapper(Object var1) {
         this.foliaTask = var1;
      }

      public int getTaskId() {
         return -1;
      }

      public Plugin getOwner() {
         try {
            Method var1 = this.foliaTask.getClass().getMethod("getOwner");
            var1.setAccessible(true);
            return (Plugin)var1.invoke(this.foliaTask);
         } catch (Exception var2) {
            return null;
         }
      }

      public boolean isSync() {
         return !this.foliaTask.getClass().getName().contains("Async");
      }

      public boolean isCancelled() {
         try {
            Method var1 = this.foliaTask.getClass().getMethod("isCancelled");
            var1.setAccessible(true);
            return (Boolean)var1.invoke(this.foliaTask);
         } catch (Exception var2) {
            return false;
         }
      }

      public void cancel() {
         try {
            Method var1 = this.foliaTask.getClass().getMethod("cancel");
            var1.setAccessible(true);
            var1.invoke(this.foliaTask);
         } catch (Exception var2) {
            var2.printStackTrace();
         }

      }
   }
}
