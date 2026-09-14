package com.zaxxer.hikari.metrics;

public interface IMetricsTracker extends AutoCloseable {
   default void recordConnectionCreatedMillis(long var1) {
   }

   default void recordConnectionAcquiredNanos(long var1) {
   }

   default void recordConnectionUsageMillis(long var1) {
   }

   default void recordConnectionTimeout() {
   }

   default void close() {
   }
}
