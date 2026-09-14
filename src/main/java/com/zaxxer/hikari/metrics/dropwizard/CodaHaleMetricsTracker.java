package com.zaxxer.hikari.metrics.dropwizard;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.PoolStats;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class CodaHaleMetricsTracker implements IMetricsTracker {
   private final String poolName;
   private final Timer connectionObtainTimer;
   private final Histogram connectionUsage;
   private final Histogram connectionCreation;
   private final Meter connectionTimeoutMeter;
   private final MetricRegistry registry;
   private static final String METRIC_CATEGORY = "pool";
   private static final String METRIC_NAME_WAIT = "Wait";
   private static final String METRIC_NAME_USAGE = "Usage";
   private static final String METRIC_NAME_CONNECT = "ConnectionCreation";
   private static final String METRIC_NAME_TIMEOUT_RATE = "ConnectionTimeoutRate";
   private static final String METRIC_NAME_TOTAL_CONNECTIONS = "TotalConnections";
   private static final String METRIC_NAME_IDLE_CONNECTIONS = "IdleConnections";
   private static final String METRIC_NAME_ACTIVE_CONNECTIONS = "ActiveConnections";
   private static final String METRIC_NAME_PENDING_CONNECTIONS = "PendingConnections";
   private static final String METRIC_NAME_MAX_CONNECTIONS = "MaxConnections";
   private static final String METRIC_NAME_MIN_CONNECTIONS = "MinConnections";

   CodaHaleMetricsTracker(String var1, PoolStats var2, MetricRegistry var3) {
      this.poolName = var1;
      this.registry = var3;
      this.connectionObtainTimer = var3.timer(MetricRegistry.name(var1, new String[]{"pool", "Wait"}));
      this.connectionUsage = var3.histogram(MetricRegistry.name(var1, new String[]{"pool", "Usage"}));
      this.connectionCreation = var3.histogram(MetricRegistry.name(var1, new String[]{"pool", "ConnectionCreation"}));
      this.connectionTimeoutMeter = var3.meter(MetricRegistry.name(var1, new String[]{"pool", "ConnectionTimeoutRate"}));
      String var10001 = MetricRegistry.name(var1, new String[]{"pool", "TotalConnections"});
      Objects.requireNonNull(var2);
      var3.register(var10001, var2::getTotalConnections);
      var10001 = MetricRegistry.name(var1, new String[]{"pool", "IdleConnections"});
      Objects.requireNonNull(var2);
      var3.register(var10001, var2::getIdleConnections);
      var10001 = MetricRegistry.name(var1, new String[]{"pool", "ActiveConnections"});
      Objects.requireNonNull(var2);
      var3.register(var10001, var2::getActiveConnections);
      var10001 = MetricRegistry.name(var1, new String[]{"pool", "PendingConnections"});
      Objects.requireNonNull(var2);
      var3.register(var10001, var2::getPendingThreads);
      var10001 = MetricRegistry.name(var1, new String[]{"pool", "MaxConnections"});
      Objects.requireNonNull(var2);
      var3.register(var10001, var2::getMaxConnections);
      var10001 = MetricRegistry.name(var1, new String[]{"pool", "MinConnections"});
      Objects.requireNonNull(var2);
      var3.register(var10001, var2::getMinConnections);
   }

   public void close() {
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "Wait"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "Usage"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ConnectionCreation"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ConnectionTimeoutRate"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "TotalConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "IdleConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "ActiveConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "PendingConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "MaxConnections"}));
      this.registry.remove(MetricRegistry.name(this.poolName, new String[]{"pool", "MinConnections"}));
   }

   public void recordConnectionAcquiredNanos(long var1) {
      this.connectionObtainTimer.update(var1, TimeUnit.NANOSECONDS);
   }

   public void recordConnectionUsageMillis(long var1) {
      this.connectionUsage.update(var1);
   }

   public void recordConnectionTimeout() {
      this.connectionTimeoutMeter.mark();
   }

   public void recordConnectionCreatedMillis(long var1) {
      this.connectionCreation.update(var1);
   }

   public Timer getConnectionAcquisitionTimer() {
      return this.connectionObtainTimer;
   }

   public Histogram getConnectionDurationHistogram() {
      return this.connectionUsage;
   }

   public Histogram getConnectionCreationHistogram() {
      return this.connectionCreation;
   }
}
