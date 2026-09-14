package com.zaxxer.hikari.metrics.dropwizard;

import com.codahale.metrics.MetricRegistry;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;

public final class CodahaleMetricsTrackerFactory implements MetricsTrackerFactory {
   private final MetricRegistry registry;

   public CodahaleMetricsTrackerFactory(MetricRegistry var1) {
      this.registry = var1;
   }

   public MetricRegistry getRegistry() {
      return this.registry;
   }

   public IMetricsTracker create(String var1, PoolStats var2) {
      return new CodaHaleMetricsTracker(var1, var2, this.registry);
   }
}
