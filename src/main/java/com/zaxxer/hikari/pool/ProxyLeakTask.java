package com.zaxxer.hikari.pool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ProxyLeakTask implements Runnable {
   private static final Logger LOGGER = LoggerFactory.getLogger(ProxyLeakTask.class);
   static final ProxyLeakTask NO_LEAK = new ProxyLeakTask() {
      void schedule(ScheduledExecutorService var1, long var2) {
      }

      public void run() {
      }

      public void cancel() {
      }
   };
   private ScheduledFuture<?> scheduledFuture;
   private String connectionName;
   private Exception exception;
   private String threadName;
   private boolean isLeaked;

   ProxyLeakTask(PoolEntry var1) {
      this.exception = new Exception("Apparent connection leak detected");
      this.threadName = Thread.currentThread().getName();
      this.connectionName = var1.connection.toString();
   }

   private ProxyLeakTask() {
   }

   void schedule(ScheduledExecutorService var1, long var2) {
      this.scheduledFuture = var1.schedule(this, var2, TimeUnit.MILLISECONDS);
   }

   public void run() {
      this.isLeaked = true;
      StackTraceElement[] var1 = this.exception.getStackTrace();
      StackTraceElement[] var2 = new StackTraceElement[var1.length - 5];
      System.arraycopy(var1, 5, var2, 0, var2.length);
      this.exception.setStackTrace(var2);
      LOGGER.warn("Connection leak detection triggered for {} on thread {}, stack trace follows", this.connectionName, this.threadName, this.exception);
   }

   void cancel() {
      this.scheduledFuture.cancel(false);
      if (this.isLeaked) {
         LOGGER.info((String)"Previously reported leaked connection {} on thread {} was returned to the pool (unleaked)", (Object)this.connectionName, (Object)this.threadName);
      }

   }
}
