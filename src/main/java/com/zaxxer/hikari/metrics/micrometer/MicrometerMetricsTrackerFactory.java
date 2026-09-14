package com.zaxxer.hikari.metrics.micrometer;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import io.micrometer.core.instrument.MeterRegistry;

public class MicrometerMetricsTrackerFactory implements MetricsTrackerFactory {
   private final MeterRegistry registry;

   public MicrometerMetricsTrackerFactory(MeterRegistry var1) {
      this.registry = var1;
   }

   public IMetricsTracker create(String var1, PoolStats var2) {
      return new MicrometerMetricsTracker(var1, var2, this.registry);
   }
}
