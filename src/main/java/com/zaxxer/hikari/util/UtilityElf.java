package com.zaxxer.hikari.util;

import java.lang.reflect.Constructor;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class UtilityElf {
   private UtilityElf() {
   }

   public static String getNullIfEmpty(String var0) {
      return var0 == null ? null : (var0.trim().isEmpty() ? null : var0.trim());
   }

   public static void quietlySleep(long var0) {
      try {
         Thread.sleep(var0);
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
      }

   }

   public static boolean safeIsAssignableFrom(Object var0, String var1) {
      try {
         Class var2 = Class.forName(var1);
         return var2.isAssignableFrom(var0.getClass());
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }

   public static <T> T createInstance(String var0, Class<T> var1, Object... var2) {
      if (var0 == null) {
         return null;
      } else {
         try {
            Class var3 = UtilityElf.class.getClassLoader().loadClass(var0);
            if (var2.length == 0) {
               return (T)var1.cast(var3.getDeclaredConstructor().newInstance());
            } else {
               Class[] var4 = new Class[var2.length];

               for(int var5 = 0; var5 < var2.length; ++var5) {
                  var4[var5] = var2[var5].getClass();
               }

               Constructor var7 = var3.getConstructor(var4);
               return (T)var1.cast(var7.newInstance(var2));
            }
         } catch (Exception var6) {
            throw new RuntimeException(var6);
         }
      }
   }

   public static ThreadPoolExecutor createThreadPoolExecutor(int var0, String var1, ThreadFactory var2, RejectedExecutionHandler var3) {
      return createThreadPoolExecutor(new LinkedBlockingQueue(var0), var1, var2, var3);
   }

   public static ThreadPoolExecutor createThreadPoolExecutor(BlockingQueue<Runnable> var0, String var1, ThreadFactory var2, RejectedExecutionHandler var3) {
      if (var2 == null) {
         var2 = new DefaultThreadFactory(var1);
      }

      ThreadPoolExecutor var4 = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, var0, (ThreadFactory)var2, var3);
      var4.allowCoreThreadTimeOut(true);
      return var4;
   }

   public static int getTransactionIsolation(String var0) {
      if (var0 != null) {
         try {
            String var1 = var0.toUpperCase(Locale.ENGLISH);
            return IsolationLevel.valueOf(var1).getLevelId();
         } catch (IllegalArgumentException var8) {
            try {
               int var2 = Integer.parseInt(var0);

               for(IsolationLevel var6 : IsolationLevel.values()) {
                  if (var6.getLevelId() == var2) {
                     return var6.getLevelId();
                  }
               }

               throw new IllegalArgumentException("Invalid transaction isolation value: " + var0);
            } catch (NumberFormatException var7) {
               throw new IllegalArgumentException("Invalid transaction isolation value: " + var0, var7);
            }
         }
      } else {
         return -1;
      }
   }

   public static class CustomDiscardPolicy implements RejectedExecutionHandler {
      public void rejectedExecution(Runnable var1, ThreadPoolExecutor var2) {
      }
   }

   public static final class DefaultThreadFactory implements ThreadFactory {
      private final String threadName;
      private final boolean daemon;

      public DefaultThreadFactory(String var1) {
         this.threadName = var1;
         this.daemon = true;
      }

      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread(var1, this.threadName);
         var2.setDaemon(this.daemon);
         return var2;
      }
   }
}
