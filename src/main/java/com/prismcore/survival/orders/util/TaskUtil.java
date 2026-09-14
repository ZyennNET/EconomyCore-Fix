package com.prismcore.survival.orders.util;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public final class TaskUtil {
   private static volatile Boolean FOLIA = null;

   private TaskUtil() {
   }

   public static boolean isFolia() {
      if (FOLIA != null) {
         return FOLIA;
      } else {
         try {
            Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
            Bukkit.getServer().getClass().getMethod("getRegionScheduler");
            FOLIA = true;
         } catch (Throwable var1) {
            FOLIA = false;
         }

         return FOLIA;
      }
   }

   public static Handle runGlobal(Plugin var0, Runnable var1) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "runnable");
      if (!isFolia()) {
         return new BukkitHandle(Bukkit.getScheduler().runTask(var0, var1));
      } else {
         Object var2 = getGlobalScheduler();
         if (tryInvokeExecute(var2, var0, var1)) {
            return new ReflectHandle((Object)null);
         } else {
            Object var3 = tryInvokeRunGlobal(var2, var0, var1);
            return new ReflectHandle(var3);
         }
      }
   }

   public static void runAsync(Plugin var0, Runnable var1) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "runnable");
      if (!isFolia()) {
         Bukkit.getScheduler().runTaskAsynchronously(var0, var1);
      } else {
         CompletableFuture.runAsync(var1);
      }

   }

   public static Handle runGlobalLater(Plugin var0, Runnable var1, long var2) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "runnable");
      var2 = Math.max(0L, var2);
      if (!isFolia()) {
         return new BukkitHandle(Bukkit.getScheduler().runTaskLater(var0, var1, var2));
      } else {
         Object var4 = getGlobalScheduler();
         Object var5 = tryInvokeRunDelayedGlobal(var4, var0, var1, var2);
         return new ReflectHandle(var5);
      }
   }

   public static Handle runGlobalTimer(Plugin var0, Runnable var1, long var2, long var4) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "runnable");
      var2 = Math.max(0L, var2);
      var4 = Math.max(1L, var4);
      if (!isFolia()) {
         return new BukkitHandle(Bukkit.getScheduler().runTaskTimer(var0, var1, var2, var4));
      } else {
         Object var6 = getGlobalScheduler();
         Object var7 = tryInvokeRunAtFixedRateGlobal(var6, var0, var1, var2, var4);
         return new ReflectHandle(var7);
      }
   }

   public static Handle runAtLocation(Plugin var0, Location var1, Runnable var2) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "loc");
      Objects.requireNonNull(var2, "runnable");
      if (!isFolia()) {
         return new BukkitHandle(Bukkit.getScheduler().runTask(var0, var2));
      } else {
         Object var3 = getRegionScheduler();
         if (tryInvokeExecuteLocation(var3, var0, var1, var2)) {
            return new ReflectHandle((Object)null);
         } else {
            Object var4 = tryInvokeRunLocation(var3, var0, var1, var2);
            return new ReflectHandle(var4);
         }
      }
   }

   public static Handle runAtLocationLater(Plugin var0, Location var1, Runnable var2, long var3) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "loc");
      Objects.requireNonNull(var2, "runnable");
      var3 = Math.max(0L, var3);
      if (!isFolia()) {
         return new BukkitHandle(Bukkit.getScheduler().runTaskLater(var0, var2, var3));
      } else {
         Object var5 = getRegionScheduler();
         Object var6 = tryInvokeRunDelayedLocation(var5, var0, var1, var2, var3);
         return new ReflectHandle(var6);
      }
   }

   public static Handle runAtLocationTimer(Plugin var0, Location var1, Runnable var2, long var3, long var5) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var1, "loc");
      Objects.requireNonNull(var2, "runnable");
      var3 = Math.max(0L, var3);
      var5 = Math.max(1L, var5);
      if (!isFolia()) {
         return new BukkitHandle(Bukkit.getScheduler().runTaskTimer(var0, var2, var3, var5));
      } else {
         Object var7 = getRegionScheduler();
         Object var8 = tryInvokeRunAtFixedRateLocation(var7, var0, var1, var2, var3, var5);
         return new ReflectHandle(var8);
      }
   }

   public static Handle runEntity(Plugin var0, Entity var1, Runnable var2) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var2, "runnable");
      if (isFolia() && var1 != null) {
         Object var3 = getEntityScheduler(var1);
         if (var3 != null) {
            if (tryInvokeExecuteEntity(var3, var0, var2)) {
               return new ReflectHandle((Object)null);
            }

            Object var4 = tryInvokeRunEntity(var3, var0, var2);
            if (var4 != null) {
               return new ReflectHandle(var4);
            }
         }

         return runAtLocation(var0, var1.getLocation(), var2);
      } else {
         return new BukkitHandle(Bukkit.getScheduler().runTask(var0, var2));
      }
   }

   public static Handle runEntityLater(Plugin var0, Entity var1, Runnable var2, long var3) {
      Objects.requireNonNull(var0, "plugin");
      Objects.requireNonNull(var2, "runnable");
      var3 = Math.max(0L, var3);
      if (isFolia() && var1 != null) {
         Object var6 = getEntityScheduler(var1);
         Object var5;
         return (Handle)(var6 != null && (var5 = tryInvokeRunDelayedEntity(var6, var0, var2, var3)) != null ? new ReflectHandle(var5) : runAtLocationLater(var0, var1.getLocation(), var2, var3));
      } else {
         return new BukkitHandle(Bukkit.getScheduler().runTaskLater(var0, var2, var3));
      }
   }

   private static Object getGlobalScheduler() {
      try {
         Method var0 = Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
         return var0.invoke(Bukkit.getServer());
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Object getRegionScheduler() {
      try {
         Method var0 = Bukkit.getServer().getClass().getMethod("getRegionScheduler");
         return var0.invoke(Bukkit.getServer());
      } catch (Throwable var1) {
         return null;
      }
   }

   private static Object getEntityScheduler(Entity var0) {
      try {
         Method var1 = var0.getClass().getMethod("getScheduler");
         return var1.invoke(var0);
      } catch (Throwable var2) {
         return null;
      }
   }

   private static Consumer<Object> consumerOf(Runnable var0) {
      return (var1) -> {
         try {
            var0.run();
         } catch (Throwable var3) {
         }

      };
   }

   private static boolean tryInvokeExecute(Object var0, Plugin var1, Runnable var2) {
      if (var0 == null) {
         return false;
      } else {
         try {
            Method var3 = var0.getClass().getMethod("execute", Plugin.class, Runnable.class);
            var3.invoke(var0, var1, var2);
            return true;
         } catch (Throwable var4) {
            return false;
         }
      }
   }

   private static boolean tryInvokeExecuteLocation(Object var0, Plugin var1, Location var2, Runnable var3) {
      if (var0 == null) {
         return false;
      } else {
         try {
            Method var4 = var0.getClass().getMethod("execute", Plugin.class, Location.class, Runnable.class);
            var4.invoke(var0, var1, var2, var3);
            return true;
         } catch (Throwable var5) {
            return false;
         }
      }
   }

   private static boolean tryInvokeExecuteEntity(Object var0, Plugin var1, Runnable var2) {
      if (var0 == null) {
         return false;
      } else {
         try {
            Method var3 = var0.getClass().getMethod("execute", Plugin.class, Runnable.class, Runnable.class);
            var3.invoke(var0, var1, var2, (Runnable)() -> {
            });
            return true;
         } catch (Throwable var6) {
            try {
               Method var4 = var0.getClass().getMethod("execute", Plugin.class, Runnable.class);
               var4.invoke(var0, var1, var2);
               return true;
            } catch (Throwable var5) {
               return false;
            }
         }
      }
   }

   private static Object tryInvokeRunGlobal(Object var0, Plugin var1, Runnable var2) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var3 = var0.getClass().getMethod("run", Plugin.class, Consumer.class);
            return var3.invoke(var0, var1, consumerOf(var2));
         } catch (Throwable var4) {
            return null;
         }
      }
   }

   private static Object tryInvokeRunDelayedGlobal(Object var0, Plugin var1, Runnable var2, long var3) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var5 = var0.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, Long.TYPE);
            return var5.invoke(var0, var1, consumerOf(var2), var3);
         } catch (Throwable var6) {
            return null;
         }
      }
   }

   private static Object tryInvokeRunAtFixedRateGlobal(Object var0, Plugin var1, Runnable var2, long var3, long var5) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var7 = var0.getClass().getMethod("runAtFixedRate", Plugin.class, Consumer.class, Long.TYPE, Long.TYPE);
            return var7.invoke(var0, var1, consumerOf(var2), var3, var5);
         } catch (Throwable var8) {
            return null;
         }
      }
   }

   private static Object tryInvokeRunLocation(Object var0, Plugin var1, Location var2, Runnable var3) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var4 = var0.getClass().getMethod("run", Plugin.class, Location.class, Consumer.class);
            return var4.invoke(var0, var1, var2, consumerOf(var3));
         } catch (Throwable var5) {
            return null;
         }
      }
   }

   private static Object tryInvokeRunDelayedLocation(Object var0, Plugin var1, Location var2, Runnable var3, long var4) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var6 = var0.getClass().getMethod("runDelayed", Plugin.class, Location.class, Consumer.class, Long.TYPE);
            return var6.invoke(var0, var1, var2, consumerOf(var3), var4);
         } catch (Throwable var7) {
            return null;
         }
      }
   }

   private static Object tryInvokeRunAtFixedRateLocation(Object var0, Plugin var1, Location var2, Runnable var3, long var4, long var6) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var8 = var0.getClass().getMethod("runAtFixedRate", Plugin.class, Location.class, Consumer.class, Long.TYPE, Long.TYPE);
            return var8.invoke(var0, var1, var2, consumerOf(var3), var4, var6);
         } catch (Throwable var9) {
            return null;
         }
      }
   }

   private static Object tryInvokeRunEntity(Object var0, Plugin var1, Runnable var2) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var3 = var0.getClass().getMethod("run", Plugin.class, Consumer.class, Runnable.class);
            return var3.invoke(var0, var1, consumerOf(var2), (Runnable)() -> {
            });
         } catch (Throwable var6) {
            try {
               Method var4 = var0.getClass().getMethod("run", Plugin.class, Consumer.class);
               return var4.invoke(var0, var1, consumerOf(var2));
            } catch (Throwable var5) {
               return null;
            }
         }
      }
   }

   private static Object tryInvokeRunDelayedEntity(Object var0, Plugin var1, Runnable var2, long var3) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Method var5 = var0.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class, Long.TYPE);
            return var5.invoke(var0, var1, consumerOf(var2), (Runnable)() -> {
            }, var3);
         } catch (Throwable var8) {
            try {
               Method var6 = var0.getClass().getMethod("runDelayed", Plugin.class, Consumer.class, Long.TYPE);
               return var6.invoke(var0, var1, consumerOf(var2), var3);
            } catch (Throwable var7) {
               return null;
            }
         }
      }
   }

   private static final class BukkitHandle implements Handle {
      private final BukkitTask task;

      BukkitHandle(BukkitTask var1) {
         this.task = var1;
      }

      public void cancel() {
         if (this.task != null) {
            this.task.cancel();
         }

      }
   }

   public interface Handle {
      void cancel();
   }

   private static final class ReflectHandle implements Handle {
      private final Object task;

      ReflectHandle(Object var1) {
         this.task = var1;
      }

      public void cancel() {
         if (this.task != null) {
            try {
               Method var1 = this.task.getClass().getMethod("cancel");
               var1.invoke(this.task);
            } catch (Throwable var4) {
               try {
                  Method var2 = this.task.getClass().getMethod("cancel", Boolean.TYPE);
                  var2.invoke(this.task, true);
               } catch (Throwable var3) {
               }

            }
         }
      }
   }
}
