package com.zaxxer.hikari.pool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariPoolMXBean;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleHealthChecker;
import com.zaxxer.hikari.metrics.dropwizard.CodahaleMetricsTrackerFactory;
import com.zaxxer.hikari.metrics.micrometer.MicrometerMetricsTrackerFactory;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.ConcurrentBag;
import com.zaxxer.hikari.util.SuspendResumeLock;
import com.zaxxer.hikari.util.UtilityElf;
import io.micrometer.core.instrument.MeterRegistry;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HikariPool extends PoolBase implements HikariPoolMXBean, ConcurrentBag.IBagStateListener {
   private final Logger logger = LoggerFactory.getLogger(HikariPool.class);
   public static final int POOL_NORMAL = 0;
   public static final int POOL_SUSPENDED = 1;
   public static final int POOL_SHUTDOWN = 2;
   public volatile int poolState;
   private final long aliveBypassWindowMs;
   private final long housekeepingPeriodMs;
   private static final String EVICTED_CONNECTION_MESSAGE = "(connection was evicted)";
   private static final String DEAD_CONNECTION_MESSAGE = "(connection is dead)";
   private final PoolEntryCreator poolEntryCreator;
   private final PoolEntryCreator postFillPoolEntryCreator;
   private final ThreadPoolExecutor addConnectionExecutor;
   private final ThreadPoolExecutor closeConnectionExecutor;
   private final ConcurrentBag<PoolEntry> connectionBag;
   private final ProxyLeakTaskFactory leakTaskFactory;
   private final SuspendResumeLock suspendResumeLock;
   private final ScheduledExecutorService houseKeepingExecutorService;
   private ScheduledFuture<?> houseKeeperTask;

   public HikariPool(HikariConfig var1) {
      super(var1);
      this.aliveBypassWindowMs = Long.getLong("com.zaxxer.hikari.aliveBypassWindowMs", TimeUnit.MILLISECONDS.toMillis(500L));
      this.housekeepingPeriodMs = Long.getLong("com.zaxxer.hikari.housekeeping.periodMs", TimeUnit.SECONDS.toMillis(30L));
      this.poolEntryCreator = new PoolEntryCreator();
      this.postFillPoolEntryCreator = new PoolEntryCreator("After adding ");
      this.connectionBag = new ConcurrentBag<PoolEntry>(this);
      this.suspendResumeLock = var1.isAllowPoolSuspension() ? new SuspendResumeLock() : SuspendResumeLock.FAUX_LOCK;
      this.houseKeepingExecutorService = this.initializeHouseKeepingExecutorService();
      this.checkFailFast();
      if (var1.getMetricsTrackerFactory() != null) {
         this.setMetricsTrackerFactory(var1.getMetricsTrackerFactory());
      } else {
         this.setMetricRegistry(var1.getMetricRegistry());
      }

      this.setHealthCheckRegistry(var1.getHealthCheckRegistry());
      this.handleMBeans(this, true);
      ThreadFactory var2 = var1.getThreadFactory();
      int var3 = var1.getMaximumPoolSize();
      LinkedBlockingQueue var4 = new LinkedBlockingQueue(var3);
      this.addConnectionExecutor = UtilityElf.createThreadPoolExecutor(var4, this.poolName + " connection adder", var2, new UtilityElf.CustomDiscardPolicy());
      this.closeConnectionExecutor = UtilityElf.createThreadPoolExecutor(var3, this.poolName + " connection closer", var2, new ThreadPoolExecutor.CallerRunsPolicy());
      this.leakTaskFactory = new ProxyLeakTaskFactory(var1.getLeakDetectionThreshold(), this.houseKeepingExecutorService);
      this.houseKeeperTask = this.houseKeepingExecutorService.scheduleWithFixedDelay(new HouseKeeper(), 100L, this.housekeepingPeriodMs, TimeUnit.MILLISECONDS);
      if (Boolean.getBoolean("com.zaxxer.hikari.blockUntilFilled") && var1.getInitializationFailTimeout() > 1L) {
         this.addConnectionExecutor.setMaximumPoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
         this.addConnectionExecutor.setCorePoolSize(Math.min(16, Runtime.getRuntime().availableProcessors()));
         long var5 = ClockSource.currentTime();

         while(ClockSource.elapsedMillis(var5) < var1.getInitializationFailTimeout() && this.getTotalConnections() < var1.getMinimumIdle()) {
            UtilityElf.quietlySleep(TimeUnit.MILLISECONDS.toMillis(100L));
         }

         this.addConnectionExecutor.setCorePoolSize(1);
         this.addConnectionExecutor.setMaximumPoolSize(1);
      }

   }

   public Connection getConnection() throws SQLException {
      return this.getConnection(this.connectionTimeout);
   }

   public Connection getConnection(long var1) throws SQLException {
      this.suspendResumeLock.acquire();
      long var3 = ClockSource.currentTime();

      try {
         long var5 = var1;

         do {
            PoolEntry var7 = this.connectionBag.borrow(var5, TimeUnit.MILLISECONDS);
            if (var7 == null) {
               break;
            }

            long var8 = ClockSource.currentTime();
            if (!var7.isMarkedEvicted() && (ClockSource.elapsedMillis(var7.lastAccessed, var8) <= this.aliveBypassWindowMs || !this.isConnectionDead(var7.connection))) {
               this.metricsTracker.recordBorrowStats(var7, var3);
               Connection var10 = var7.createProxyConnection(this.leakTaskFactory.schedule(var7));
               return var10;
            }

            this.closeConnection(var7, var7.isMarkedEvicted() ? "(connection was evicted)" : "(connection is dead)");
            var5 = var1 - ClockSource.elapsedMillis(var3);
         } while(var5 > 0L);

         this.metricsTracker.recordBorrowTimeoutStats(var3);
         throw this.createTimeoutException(var3);
      } catch (InterruptedException var14) {
         Thread.currentThread().interrupt();
         throw new SQLException(this.poolName + " - Interrupted during connection acquisition", var14);
      } finally {
         this.suspendResumeLock.release();
      }
   }

   public synchronized void shutdown() throws InterruptedException {
      try {
         this.poolState = 2;
         if (this.addConnectionExecutor != null) {
            this.logPoolState("Before shutdown ");
            if (this.houseKeeperTask != null) {
               this.houseKeeperTask.cancel(false);
               this.houseKeeperTask = null;
            }

            this.softEvictConnections();
            this.addConnectionExecutor.shutdown();
            if (!this.addConnectionExecutor.awaitTermination(this.getLoginTimeout(), TimeUnit.SECONDS)) {
               this.logger.warn("Timed-out waiting for add connection executor to shutdown");
            }

            this.destroyHouseKeepingExecutorService();
            this.connectionBag.close();
            ThreadPoolExecutor var1 = UtilityElf.createThreadPoolExecutor(this.config.getMaximumPoolSize(), this.poolName + " connection assassinator", this.config.getThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

            try {
               long var2 = ClockSource.currentTime();

               do {
                  this.abortActiveConnections(var1);
                  this.softEvictConnections();
               } while(this.getTotalConnections() > 0 && ClockSource.elapsedMillis(var2) < TimeUnit.SECONDS.toMillis(10L));
            } finally {
               var1.shutdown();
               if (!var1.awaitTermination(10L, TimeUnit.SECONDS)) {
                  this.logger.warn("Timed-out waiting for connection assassin to shutdown");
               }

            }

            this.shutdownNetworkTimeoutExecutor();
            this.closeConnectionExecutor.shutdown();
            if (!this.closeConnectionExecutor.awaitTermination(10L, TimeUnit.SECONDS)) {
               this.logger.warn("Timed-out waiting for close connection executor to shutdown");
            }

            return;
         }
      } finally {
         this.logPoolState("After shutdown ");
         this.handleMBeans(this, false);
         this.metricsTracker.close();
      }

   }

   public void evictConnection(Connection var1) {
      ProxyConnection var2 = (ProxyConnection)var1;
      var2.cancelLeakTask();

      try {
         this.softEvictConnection(var2.getPoolEntry(), "(connection evicted by user)", !var1.isClosed());
      } catch (SQLException var4) {
      }

   }

   public void setMetricRegistry(Object var1) {
      if (var1 != null && UtilityElf.safeIsAssignableFrom(var1, "com.codahale.metrics.MetricRegistry")) {
         this.setMetricsTrackerFactory(new CodahaleMetricsTrackerFactory((MetricRegistry)var1));
      } else if (var1 != null && UtilityElf.safeIsAssignableFrom(var1, "io.micrometer.core.instrument.MeterRegistry")) {
         this.setMetricsTrackerFactory(new MicrometerMetricsTrackerFactory((MeterRegistry)var1));
      } else {
         this.setMetricsTrackerFactory((MetricsTrackerFactory)null);
      }

   }

   public void setMetricsTrackerFactory(MetricsTrackerFactory var1) {
      if (var1 != null) {
         this.metricsTracker = new PoolBase.MetricsTrackerDelegate(var1.create(this.config.getPoolName(), this.getPoolStats()));
      } else {
         this.metricsTracker = new PoolBase.NopMetricsTrackerDelegate();
      }

   }

   public void setHealthCheckRegistry(Object var1) {
      if (var1 != null) {
         CodahaleHealthChecker.registerHealthChecks(this, this.config, (HealthCheckRegistry)var1);
      }

   }

   public void addBagItem(int var1) {
      if (var1 > this.addConnectionExecutor.getQueue().size()) {
         this.addConnectionExecutor.submit(this.poolEntryCreator);
      }

   }

   public int getActiveConnections() {
      return this.connectionBag.getCount(1);
   }

   public int getIdleConnections() {
      return this.connectionBag.getCount(0);
   }

   public int getTotalConnections() {
      return this.connectionBag.size();
   }

   public int getThreadsAwaitingConnection() {
      return this.connectionBag.getWaitingThreadCount();
   }

   public void softEvictConnections() {
      this.connectionBag.values().forEach((var1) -> this.softEvictConnection(var1, "(connection evicted)", false));
   }

   public synchronized void suspendPool() {
      if (this.suspendResumeLock == SuspendResumeLock.FAUX_LOCK) {
         throw new IllegalStateException(this.poolName + " - is not suspendable");
      } else {
         if (this.poolState != 1) {
            this.suspendResumeLock.suspend();
            this.poolState = 1;
         }

      }
   }

   public synchronized void resumePool() {
      if (this.poolState == 1) {
         this.poolState = 0;
         this.fillPool(false);
         this.suspendResumeLock.resume();
      }

   }

   void logPoolState(String... var1) {
      if (this.logger.isDebugEnabled()) {
         this.logger.debug("{} - {}stats (total={}, active={}, idle={}, waiting={})", this.poolName, var1.length > 0 ? var1[0] : "", this.getTotalConnections(), this.getActiveConnections(), this.getIdleConnections(), this.getThreadsAwaitingConnection());
      }

   }

   void recycle(PoolEntry var1) {
      this.metricsTracker.recordConnectionUsage(var1);
      this.connectionBag.requite(var1);
   }

   void closeConnection(PoolEntry var1, String var2) {
      if (this.connectionBag.remove(var1)) {
         Connection var3 = var1.close();
         this.closeConnectionExecutor.execute(() -> {
            this.quietlyCloseConnection(var3, var2);
            if (this.poolState == 0) {
               this.fillPool(false);
            }

         });
      }

   }

   int[] getPoolStateCounts() {
      return this.connectionBag.getStateCounts();
   }

   private PoolEntry createPoolEntry() {
      try {
         PoolEntry var1 = this.newPoolEntry();
         long var2 = this.config.getMaxLifetime();
         if (var2 > 0L) {
            long var4 = var2 > 10000L ? ThreadLocalRandom.current().nextLong(var2 / 40L) : 0L;
            long var6 = var2 - var4;
            var1.setFutureEol(this.houseKeepingExecutorService.schedule(new MaxLifetimeTask(var1), var6, TimeUnit.MILLISECONDS));
         }

         long var12 = this.config.getKeepaliveTime();
         if (var12 > 0L) {
            long var13 = ThreadLocalRandom.current().nextLong(var12 / 10L);
            long var8 = var12 - var13;
            var1.setKeepalive(this.houseKeepingExecutorService.scheduleWithFixedDelay(new KeepaliveTask(var1), var8, var8, TimeUnit.MILLISECONDS));
         }

         return var1;
      } catch (PoolBase.ConnectionSetupException var10) {
         if (this.poolState == 0) {
            this.logger.error((String)"{} - Error thrown while acquiring connection from data source", (Object)this.poolName, (Object)var10.getCause());
            this.lastConnectionFailure.set(var10);
         }
      } catch (Exception var11) {
         if (this.poolState == 0) {
            this.logger.debug((String)"{} - Cannot acquire connection from data source", (Object)this.poolName, (Object)var11);
         }
      }

      return null;
   }

   private synchronized void fillPool(boolean var1) {
      int var2 = this.getIdleConnections();
      boolean var3 = this.getTotalConnections() < this.config.getMaximumPoolSize() && var2 < this.config.getMinimumIdle();
      if (var3) {
         int var4 = this.config.getMinimumIdle() - var2;

         for(int var5 = 0; var5 < var4; ++var5) {
            this.addConnectionExecutor.submit(var1 ? this.postFillPoolEntryCreator : this.poolEntryCreator);
         }
      } else if (var1) {
         this.logger.debug((String)"{} - Fill pool skipped, pool has sufficient level or currently being filled.", (Object)this.poolName);
      }

   }

   private void abortActiveConnections(ExecutorService var1) {
      for(PoolEntry var3 : this.connectionBag.values(1)) {
         Connection var4 = var3.close();

         try {
            var4.abort(var1);
         } catch (Throwable var9) {
            this.quietlyCloseConnection(var4, "(connection aborted during shutdown)");
         } finally {
            this.connectionBag.remove(var3);
         }
      }

   }

   private void checkFailFast() {
      long var1 = this.config.getInitializationFailTimeout();
      if (var1 >= 0L) {
         long var3 = ClockSource.currentTime();

         do {
            PoolEntry var5 = this.createPoolEntry();
            if (var5 != null) {
               if (this.config.getMinimumIdle() > 0) {
                  this.connectionBag.add(var5);
                  this.logger.info((String)"{} - Added connection {}", (Object)this.poolName, (Object)var5.connection);
               } else {
                  this.quietlyCloseConnection(var5.close(), "(initialization check complete and minimumIdle is zero)");
               }

               return;
            }

            if (this.getLastConnectionFailure() instanceof PoolBase.ConnectionSetupException) {
               this.throwPoolInitializationException(this.getLastConnectionFailure().getCause());
            }

            UtilityElf.quietlySleep(TimeUnit.SECONDS.toMillis(1L));
         } while(ClockSource.elapsedMillis(var3) < var1);

         if (var1 > 0L) {
            this.throwPoolInitializationException(this.getLastConnectionFailure());
         }

      }
   }

   private void throwPoolInitializationException(Throwable var1) {
      this.destroyHouseKeepingExecutorService();
      throw new PoolInitializationException(var1);
   }

   private boolean softEvictConnection(PoolEntry var1, String var2, boolean var3) {
      var1.markEvicted();
      if (!var3 && !this.connectionBag.reserve(var1)) {
         return false;
      } else {
         this.closeConnection(var1, var2);
         return true;
      }
   }

   private ScheduledExecutorService initializeHouseKeepingExecutorService() {
      if (this.config.getScheduledExecutor() == null) {
         ThreadFactory var1 = (ThreadFactory)Optional.ofNullable(this.config.getThreadFactory()).orElseGet(() -> new UtilityElf.DefaultThreadFactory(this.poolName + " housekeeper"));
         ScheduledThreadPoolExecutor var2 = new ScheduledThreadPoolExecutor(1, var1, new ThreadPoolExecutor.DiscardPolicy());
         var2.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
         var2.setRemoveOnCancelPolicy(true);
         return var2;
      } else {
         return this.config.getScheduledExecutor();
      }
   }

   private void destroyHouseKeepingExecutorService() {
      if (this.config.getScheduledExecutor() == null) {
         this.houseKeepingExecutorService.shutdownNow();
      }

   }

   private PoolStats getPoolStats() {
      return new PoolStats(TimeUnit.SECONDS.toMillis(1L)) {
         protected void update() {
            this.pendingThreads = HikariPool.this.getThreadsAwaitingConnection();
            this.idleConnections = HikariPool.this.getIdleConnections();
            this.totalConnections = HikariPool.this.getTotalConnections();
            this.activeConnections = HikariPool.this.getActiveConnections();
            this.maxConnections = HikariPool.this.config.getMaximumPoolSize();
            this.minConnections = HikariPool.this.config.getMinimumIdle();
         }
      };
   }

   private SQLException createTimeoutException(long var1) {
      this.logPoolState("Timeout failure ");
      this.metricsTracker.recordConnectionTimeout();
      String var3 = null;
      Exception var4 = this.getLastConnectionFailure();
      if (var4 instanceof SQLException) {
         var3 = ((SQLException)var4).getSQLState();
      }

      String var10002 = this.poolName;
      SQLTransientConnectionException var5 = new SQLTransientConnectionException(var10002 + " - Connection is not available, request timed out after " + ClockSource.elapsedMillis(var1) + "ms (total=" + this.getTotalConnections() + ", active=" + this.getActiveConnections() + ", idle=" + this.getIdleConnections() + ", waiting=" + this.getThreadsAwaitingConnection() + ")", var3, var4);
      if (var4 instanceof SQLException) {
         var5.setNextException((SQLException)var4);
      }

      return var5;
   }

   private final class HouseKeeper implements Runnable {
      private volatile long previous;
      private final AtomicReferenceFieldUpdater<PoolBase, String> catalogUpdater;

      private HouseKeeper() {
         this.previous = ClockSource.plusMillis(ClockSource.currentTime(), -HikariPool.this.housekeepingPeriodMs);
         this.catalogUpdater = AtomicReferenceFieldUpdater.newUpdater(PoolBase.class, String.class, "catalog");
      }

      public void run() {
         try {
            HikariPool.this.connectionTimeout = HikariPool.this.config.getConnectionTimeout();
            HikariPool.this.validationTimeout = HikariPool.this.config.getValidationTimeout();
            HikariPool.this.leakTaskFactory.updateLeakDetectionThreshold(HikariPool.this.config.getLeakDetectionThreshold());
            if (HikariPool.this.config.getCatalog() != null && !HikariPool.this.config.getCatalog().equals(HikariPool.this.catalog)) {
               this.catalogUpdater.set(HikariPool.this, HikariPool.this.config.getCatalog());
            }

            long var1 = HikariPool.this.config.getIdleTimeout();
            long var3 = ClockSource.currentTime();
            if (ClockSource.plusMillis(var3, 128L) < ClockSource.plusMillis(this.previous, HikariPool.this.housekeepingPeriodMs)) {
               HikariPool.this.logger.warn((String)"{} - Retrograde clock change detected (housekeeper delta={}), soft-evicting connections from pool.", (Object)HikariPool.this.poolName, (Object)ClockSource.elapsedDisplayString(this.previous, var3));
               this.previous = var3;
               HikariPool.this.softEvictConnections();
               return;
            }

            if (var3 > ClockSource.plusMillis(this.previous, 3L * HikariPool.this.housekeepingPeriodMs / 2L)) {
               HikariPool.this.logger.warn((String)"{} - Thread starvation or clock leap detected (housekeeper delta={}).", (Object)HikariPool.this.poolName, (Object)ClockSource.elapsedDisplayString(this.previous, var3));
            }

            this.previous = var3;
            if (var1 > 0L && HikariPool.this.config.getMinimumIdle() < HikariPool.this.config.getMaximumPoolSize()) {
               HikariPool.this.logPoolState("Before cleanup ");
               List var5 = HikariPool.this.connectionBag.values(0);
               int var6 = var5.size() - HikariPool.this.config.getMinimumIdle();

               for(PoolEntry var8 : var5) {
                  if (var6 > 0 && ClockSource.elapsedMillis(var8.lastAccessed, var3) > var1 && HikariPool.this.connectionBag.reserve(var8)) {
                     HikariPool.this.closeConnection(var8, "(connection has passed idleTimeout)");
                     --var6;
                  }
               }

               HikariPool.this.logPoolState("After cleanup  ");
            } else {
               HikariPool.this.logPoolState("Pool ");
            }

            HikariPool.this.fillPool(true);
         } catch (Exception var9) {
            HikariPool.this.logger.error((String)"Unexpected exception in housekeeping task", (Throwable)var9);
         }

      }
   }

   private final class KeepaliveTask implements Runnable {
      private final PoolEntry poolEntry;

      KeepaliveTask(PoolEntry var2) {
         this.poolEntry = var2;
      }

      public void run() {
         if (HikariPool.this.connectionBag.reserve(this.poolEntry)) {
            if (HikariPool.this.isConnectionDead(this.poolEntry.connection)) {
               HikariPool.this.softEvictConnection(this.poolEntry, "(connection is dead)", true);
               HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
            } else {
               HikariPool.this.connectionBag.unreserve(this.poolEntry);
               HikariPool.this.logger.debug((String)"{} - keepalive: connection {} is alive", (Object)HikariPool.this.poolName, (Object)this.poolEntry.connection);
            }
         }

      }
   }

   private final class MaxLifetimeTask implements Runnable {
      private final PoolEntry poolEntry;

      MaxLifetimeTask(PoolEntry var2) {
         this.poolEntry = var2;
      }

      public void run() {
         if (HikariPool.this.softEvictConnection(this.poolEntry, "(connection has passed maxLifetime)", false)) {
            HikariPool.this.addBagItem(HikariPool.this.connectionBag.getWaitingThreadCount());
         }

      }
   }

   private final class PoolEntryCreator implements Callable<Boolean> {
      private final String loggingPrefix;

      PoolEntryCreator() {
         this((String)null);
      }

      PoolEntryCreator(String var2) {
         this.loggingPrefix = var2;
      }

      public Boolean call() {
         long var1 = 10L;
         boolean var3 = false;

         try {
            while(this.shouldContinueCreating()) {
               PoolEntry var4 = HikariPool.this.createPoolEntry();
               if (var4 != null) {
                  var3 = true;
                  HikariPool.this.connectionBag.add(var4);
                  HikariPool.this.logger.debug((String)"{} - Added connection {}", (Object)HikariPool.this.poolName, (Object)var4.connection);
                  UtilityElf.quietlySleep(30L);
                  break;
               }

               if (this.loggingPrefix != null && var1 % 50L == 0L) {
                  HikariPool.this.logger.debug((String)"{} - Connection add failed, sleeping with backoff: {}ms", (Object)HikariPool.this.poolName, (Object)var1);
               }

               UtilityElf.quietlySleep(var1);
               var1 = Math.min(TimeUnit.SECONDS.toMillis(5L), var1 * 2L);
            }
         } finally {
            if (var3 && this.loggingPrefix != null) {
               HikariPool.this.logPoolState(this.loggingPrefix);
            } else {
               HikariPool.this.logPoolState("Connection not added, ");
            }

         }

         return Boolean.FALSE;
      }

      private synchronized boolean shouldContinueCreating() {
         return HikariPool.this.poolState == 0 && HikariPool.this.getTotalConnections() < HikariPool.this.config.getMaximumPoolSize() && (HikariPool.this.getIdleConnections() < HikariPool.this.config.getMinimumIdle() || HikariPool.this.connectionBag.getWaitingThreadCount() > HikariPool.this.getIdleConnections());
      }
   }

   public static class PoolInitializationException extends RuntimeException {
      private static final long serialVersionUID = 929872118275916520L;

      public PoolInitializationException(Throwable var1) {
         super("Failed to initialize pool: " + var1.getMessage(), var1);
      }
   }
}
