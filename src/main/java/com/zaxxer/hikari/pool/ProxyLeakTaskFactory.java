package com.zaxxer.hikari.pool;

import java.util.concurrent.ScheduledExecutorService;

class ProxyLeakTaskFactory {
   private ScheduledExecutorService executorService;
   private long leakDetectionThreshold;

   ProxyLeakTaskFactory(long var1, ScheduledExecutorService var3) {
      this.executorService = var3;
      this.leakDetectionThreshold = var1;
   }

   ProxyLeakTask schedule(PoolEntry var1) {
      return this.leakDetectionThreshold == 0L ? ProxyLeakTask.NO_LEAK : this.scheduleNewTask(var1);
   }

   void updateLeakDetectionThreshold(long var1) {
      this.leakDetectionThreshold = var1;
   }

   private ProxyLeakTask scheduleNewTask(PoolEntry var1) {
      ProxyLeakTask var2 = new ProxyLeakTask(var1);
      var2.schedule(this.executorService, this.leakDetectionThreshold);
      return var2;
   }
}
