package org.apache.commons.pool2.impl;

import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

class EvictionTimer {
   private static ScheduledThreadPoolExecutor executor;
   private static final HashMap<WeakReference<BaseGenericObjectPool<?>.Evictor>, WeakRunner<BaseGenericObjectPool<?>.Evictor>> TASK_MAP = new HashMap();

   static synchronized void cancel(BaseGenericObjectPool<?>.Evictor var0, Duration var1, boolean var2) {
      if (var0 != null) {
         var0.cancel();
         remove(var0);
      }

      if (!var2 && executor != null && TASK_MAP.isEmpty()) {
         executor.shutdown();

         try {
            executor.awaitTermination(var1.toMillis(), TimeUnit.MILLISECONDS);
         } catch (InterruptedException var4) {
         }

         executor.setCorePoolSize(0);
         executor = null;
      }

   }

   static ScheduledThreadPoolExecutor getExecutor() {
      return executor;
   }

   static synchronized int getNumTasks() {
      return TASK_MAP.size();
   }

   static HashMap<WeakReference<BaseGenericObjectPool<?>.Evictor>, WeakRunner<BaseGenericObjectPool<?>.Evictor>> getTaskMap() {
      return TASK_MAP;
   }

   private static void remove(BaseGenericObjectPool<?>.Evictor var0) {
      for(Map.Entry var2 : TASK_MAP.entrySet()) {
         if (((WeakReference)var2.getKey()).get() == var0) {
            executor.remove((Runnable)var2.getValue());
            TASK_MAP.remove(var2.getKey());
            break;
         }
      }

   }

   static synchronized void schedule(BaseGenericObjectPool<?>.Evictor var0, Duration var1, Duration var2) {
      if (null == executor) {
         executor = new ScheduledThreadPoolExecutor(1, new EvictorThreadFactory());
         executor.setRemoveOnCancelPolicy(true);
         executor.scheduleAtFixedRate(new Reaper(), var1.toMillis(), var2.toMillis(), TimeUnit.MILLISECONDS);
      }

      WeakReference var3 = new WeakReference(var0);
      WeakRunner var4 = new WeakRunner(var3);
      ScheduledFuture var5 = executor.scheduleWithFixedDelay(var4, var1.toMillis(), var2.toMillis(), TimeUnit.MILLISECONDS);
      var0.setScheduledFuture(var5);
      TASK_MAP.put(var3, var4);
   }

   private EvictionTimer() {
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("EvictionTimer []");
      return var1.toString();
   }

   private static class EvictorThreadFactory implements ThreadFactory {
      private EvictorThreadFactory() {
      }

      public Thread newThread(Runnable var1) {
         Thread var2 = new Thread((ThreadGroup)null, var1, "commons-pool-evictor");
         var2.setDaemon(true);
         AccessController.doPrivileged(() -> {
            var2.setContextClassLoader(EvictorThreadFactory.class.getClassLoader());
            return null;
         });
         return var2;
      }
   }

   private static class Reaper implements Runnable {
      private Reaper() {
      }

      public void run() {
         synchronized(EvictionTimer.class) {
            for(Map.Entry var3 : EvictionTimer.TASK_MAP.entrySet()) {
               if (((WeakReference)var3.getKey()).get() == null) {
                  EvictionTimer.executor.remove((Runnable)var3.getValue());
                  EvictionTimer.TASK_MAP.remove(var3.getKey());
               }
            }

            if (EvictionTimer.TASK_MAP.isEmpty() && EvictionTimer.executor != null) {
               EvictionTimer.executor.shutdown();
               EvictionTimer.executor.setCorePoolSize(0);
               EvictionTimer.executor = null;
            }

         }
      }
   }

   private static class WeakRunner<R extends Runnable> implements Runnable {
      private final WeakReference<R> ref;

      private WeakRunner(WeakReference<R> var1) {
         this.ref = var1;
      }

      public void run() {
         Runnable var1 = (Runnable)this.ref.get();
         if (var1 != null) {
            var1.run();
         } else {
            EvictionTimer.executor.remove(this);
            EvictionTimer.TASK_MAP.remove(this.ref);
         }

      }
   }
}
