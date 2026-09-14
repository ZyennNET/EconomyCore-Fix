package org.apache.commons.pool2.impl;

import java.time.Duration;

public class EvictionConfig {
   private static final Duration MAX_DURATION = Duration.ofMillis(Long.MAX_VALUE);
   private final Duration idleEvictDuration;
   private final Duration idleSoftEvictDuration;
   private final int minIdle;

   public EvictionConfig(Duration var1, Duration var2, int var3) {
      this.idleEvictDuration = PoolImplUtils.isPositive(var1) ? var1 : MAX_DURATION;
      this.idleSoftEvictDuration = PoolImplUtils.isPositive(var2) ? var2 : MAX_DURATION;
      this.minIdle = var3;
   }

   @Deprecated
   public EvictionConfig(long var1, long var3, int var5) {
      this(Duration.ofMillis(var1), Duration.ofMillis(var3), var5);
   }

   public Duration getIdleEvictDuration() {
      return this.idleEvictDuration;
   }

   @Deprecated
   public long getIdleEvictTime() {
      return this.idleEvictDuration.toMillis();
   }

   @Deprecated
   public Duration getIdleEvictTimeDuration() {
      return this.idleEvictDuration;
   }

   public Duration getIdleSoftEvictDuration() {
      return this.idleSoftEvictDuration;
   }

   @Deprecated
   public long getIdleSoftEvictTime() {
      return this.idleSoftEvictDuration.toMillis();
   }

   @Deprecated
   public Duration getIdleSoftEvictTimeDuration() {
      return this.idleSoftEvictDuration;
   }

   public int getMinIdle() {
      return this.minIdle;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("EvictionConfig [idleEvictDuration=");
      var1.append(this.idleEvictDuration);
      var1.append(", idleSoftEvictDuration=");
      var1.append(this.idleSoftEvictDuration);
      var1.append(", minIdle=");
      var1.append(this.minIdle);
      var1.append("]");
      return var1.toString();
   }
}
