package com.zaxxer.hikari.metrics;

import com.zaxxer.hikari.util.ClockSource;
import java.util.concurrent.atomic.AtomicLong;

public abstract class PoolStats {
   private final AtomicLong reloadAt;
   private final long timeoutMs;
   protected volatile int totalConnections;
   protected volatile int idleConnections;
   protected volatile int activeConnections;
   protected volatile int pendingThreads;
   protected volatile int maxConnections;
   protected volatile int minConnections;

   public PoolStats(long var1) {
      this.timeoutMs = var1;
      this.reloadAt = new AtomicLong();
   }

   public int getTotalConnections() {
      if (this.shouldLoad()) {
         this.update();
      }

      return this.totalConnections;
   }

   public int getIdleConnections() {
      if (this.shouldLoad()) {
         this.update();
      }

      return this.idleConnections;
   }

   public int getActiveConnections() {
      if (this.shouldLoad()) {
         this.update();
      }

      return this.activeConnections;
   }

   public int getPendingThreads() {
      if (this.shouldLoad()) {
         this.update();
      }

      return this.pendingThreads;
   }

   public int getMaxConnections() {
      if (this.shouldLoad()) {
         this.update();
      }

      return this.maxConnections;
   }

   public int getMinConnections() {
      if (this.shouldLoad()) {
         this.update();
      }

      return this.minConnections;
   }

   protected abstract void update();

   private boolean shouldLoad() {
      long var1;
      long var3;
      do {
         var1 = ClockSource.currentTime();
         var3 = this.reloadAt.get();
         if (var3 > var1) {
            return false;
         }
      } while(!this.reloadAt.compareAndSet(var3, ClockSource.plusMillis(var1, this.timeoutMs)));

      return true;
   }
}
