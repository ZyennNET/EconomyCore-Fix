package org.apache.commons.pool2.impl;

import java.time.Duration;
import org.apache.commons.pool2.BaseObject;

public abstract class BaseObjectPoolConfig<T> extends BaseObject implements Cloneable {
   public static final boolean DEFAULT_LIFO = true;
   public static final boolean DEFAULT_FAIRNESS = false;
   @Deprecated
   public static final long DEFAULT_MAX_WAIT_MILLIS = -1L;
   public static final Duration DEFAULT_MAX_WAIT = Duration.ofMillis(-1L);
   @Deprecated
   public static final long DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS = 1800000L;
   public static final Duration DEFAULT_MIN_EVICTABLE_IDLE_DURATION = Duration.ofMillis(1800000L);
   @Deprecated
   public static final Duration DEFAULT_MIN_EVICTABLE_IDLE_TIME = Duration.ofMillis(1800000L);
   @Deprecated
   public static final long DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = -1L;
   @Deprecated
   public static final Duration DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME = Duration.ofMillis(-1L);
   public static final Duration DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION = Duration.ofMillis(-1L);
   @Deprecated
   public static final long DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT_MILLIS = 10000L;
   public static final Duration DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT = Duration.ofMillis(10000L);
   public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = 3;
   public static final boolean DEFAULT_TEST_ON_CREATE = false;
   public static final boolean DEFAULT_TEST_ON_BORROW = false;
   public static final boolean DEFAULT_TEST_ON_RETURN = false;
   public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
   @Deprecated
   public static final long DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
   public static final Duration DEFAULT_DURATION_BETWEEN_EVICTION_RUNS = Duration.ofMillis(-1L);
   @Deprecated
   public static final Duration DEFAULT_TIME_BETWEEN_EVICTION_RUNS = Duration.ofMillis(-1L);
   public static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED = true;
   public static final boolean DEFAULT_JMX_ENABLE = true;
   public static final String DEFAULT_JMX_NAME_PREFIX = "pool";
   public static final String DEFAULT_JMX_NAME_BASE = null;
   public static final String DEFAULT_EVICTION_POLICY_CLASS_NAME = DefaultEvictionPolicy.class.getName();
   private boolean lifo = true;
   private boolean fairness = false;
   private Duration maxWaitDuration;
   private Duration minEvictableIdleDuration;
   private Duration evictorShutdownTimeoutDuration;
   private Duration softMinEvictableIdleDuration;
   private int numTestsPerEvictionRun;
   private EvictionPolicy<T> evictionPolicy;
   private String evictionPolicyClassName;
   private boolean testOnCreate;
   private boolean testOnBorrow;
   private boolean testOnReturn;
   private boolean testWhileIdle;
   private Duration durationBetweenEvictionRuns;
   private boolean blockWhenExhausted;
   private boolean jmxEnabled;
   private String jmxNamePrefix;
   private String jmxNameBase;

   public BaseObjectPoolConfig() {
      this.maxWaitDuration = DEFAULT_MAX_WAIT;
      this.minEvictableIdleDuration = DEFAULT_MIN_EVICTABLE_IDLE_TIME;
      this.evictorShutdownTimeoutDuration = DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT;
      this.softMinEvictableIdleDuration = DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME;
      this.numTestsPerEvictionRun = 3;
      this.evictionPolicyClassName = DEFAULT_EVICTION_POLICY_CLASS_NAME;
      this.testOnCreate = false;
      this.testOnBorrow = false;
      this.testOnReturn = false;
      this.testWhileIdle = false;
      this.durationBetweenEvictionRuns = DEFAULT_DURATION_BETWEEN_EVICTION_RUNS;
      this.blockWhenExhausted = true;
      this.jmxEnabled = true;
      this.jmxNamePrefix = "pool";
      this.jmxNameBase = DEFAULT_JMX_NAME_BASE;
   }

   public boolean getBlockWhenExhausted() {
      return this.blockWhenExhausted;
   }

   public Duration getDurationBetweenEvictionRuns() {
      return this.durationBetweenEvictionRuns;
   }

   public EvictionPolicy<T> getEvictionPolicy() {
      return this.evictionPolicy;
   }

   public String getEvictionPolicyClassName() {
      return this.evictionPolicyClassName;
   }

   @Deprecated
   public Duration getEvictorShutdownTimeout() {
      return this.evictorShutdownTimeoutDuration;
   }

   public Duration getEvictorShutdownTimeoutDuration() {
      return this.evictorShutdownTimeoutDuration;
   }

   @Deprecated
   public long getEvictorShutdownTimeoutMillis() {
      return this.evictorShutdownTimeoutDuration.toMillis();
   }

   public boolean getFairness() {
      return this.fairness;
   }

   public boolean getJmxEnabled() {
      return this.jmxEnabled;
   }

   public String getJmxNameBase() {
      return this.jmxNameBase;
   }

   public String getJmxNamePrefix() {
      return this.jmxNamePrefix;
   }

   public boolean getLifo() {
      return this.lifo;
   }

   public Duration getMaxWaitDuration() {
      return this.maxWaitDuration;
   }

   @Deprecated
   public long getMaxWaitMillis() {
      return this.maxWaitDuration.toMillis();
   }

   public Duration getMinEvictableIdleDuration() {
      return this.minEvictableIdleDuration;
   }

   @Deprecated
   public Duration getMinEvictableIdleTime() {
      return this.minEvictableIdleDuration;
   }

   @Deprecated
   public long getMinEvictableIdleTimeMillis() {
      return this.minEvictableIdleDuration.toMillis();
   }

   public int getNumTestsPerEvictionRun() {
      return this.numTestsPerEvictionRun;
   }

   public Duration getSoftMinEvictableIdleDuration() {
      return this.softMinEvictableIdleDuration;
   }

   @Deprecated
   public Duration getSoftMinEvictableIdleTime() {
      return this.softMinEvictableIdleDuration;
   }

   @Deprecated
   public long getSoftMinEvictableIdleTimeMillis() {
      return this.softMinEvictableIdleDuration.toMillis();
   }

   public boolean getTestOnBorrow() {
      return this.testOnBorrow;
   }

   public boolean getTestOnCreate() {
      return this.testOnCreate;
   }

   public boolean getTestOnReturn() {
      return this.testOnReturn;
   }

   public boolean getTestWhileIdle() {
      return this.testWhileIdle;
   }

   @Deprecated
   public Duration getTimeBetweenEvictionRuns() {
      return this.durationBetweenEvictionRuns;
   }

   @Deprecated
   public long getTimeBetweenEvictionRunsMillis() {
      return this.durationBetweenEvictionRuns.toMillis();
   }

   public void setBlockWhenExhausted(boolean var1) {
      this.blockWhenExhausted = var1;
   }

   public void setEvictionPolicy(EvictionPolicy<T> var1) {
      this.evictionPolicy = var1;
   }

   public void setEvictionPolicyClassName(String var1) {
      this.evictionPolicyClassName = var1;
   }

   public void setEvictorShutdownTimeout(Duration var1) {
      this.evictorShutdownTimeoutDuration = PoolImplUtils.nonNull(var1, DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT);
   }

   @Deprecated
   public void setEvictorShutdownTimeoutMillis(Duration var1) {
      this.setEvictorShutdownTimeout(var1);
   }

   @Deprecated
   public void setEvictorShutdownTimeoutMillis(long var1) {
      this.setEvictorShutdownTimeout(Duration.ofMillis(var1));
   }

   public void setFairness(boolean var1) {
      this.fairness = var1;
   }

   public void setJmxEnabled(boolean var1) {
      this.jmxEnabled = var1;
   }

   public void setJmxNameBase(String var1) {
      this.jmxNameBase = var1;
   }

   public void setJmxNamePrefix(String var1) {
      this.jmxNamePrefix = var1;
   }

   public void setLifo(boolean var1) {
      this.lifo = var1;
   }

   public void setMaxWait(Duration var1) {
      this.maxWaitDuration = PoolImplUtils.nonNull(var1, DEFAULT_MAX_WAIT);
   }

   @Deprecated
   public void setMaxWaitMillis(long var1) {
      this.setMaxWait(Duration.ofMillis(var1));
   }

   public void setMinEvictableIdleDuration(Duration var1) {
      this.minEvictableIdleDuration = PoolImplUtils.nonNull(var1, DEFAULT_MIN_EVICTABLE_IDLE_TIME);
   }

   @Deprecated
   public void setMinEvictableIdleTime(Duration var1) {
      this.minEvictableIdleDuration = PoolImplUtils.nonNull(var1, DEFAULT_MIN_EVICTABLE_IDLE_TIME);
   }

   @Deprecated
   public void setMinEvictableIdleTimeMillis(long var1) {
      this.minEvictableIdleDuration = Duration.ofMillis(var1);
   }

   public void setNumTestsPerEvictionRun(int var1) {
      this.numTestsPerEvictionRun = var1;
   }

   public void setSoftMinEvictableIdleDuration(Duration var1) {
      this.softMinEvictableIdleDuration = PoolImplUtils.nonNull(var1, DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME);
   }

   @Deprecated
   public void setSoftMinEvictableIdleTime(Duration var1) {
      this.softMinEvictableIdleDuration = PoolImplUtils.nonNull(var1, DEFAULT_SOFT_MIN_EVICTABLE_IDLE_TIME);
   }

   @Deprecated
   public void setSoftMinEvictableIdleTimeMillis(long var1) {
      this.setSoftMinEvictableIdleTime(Duration.ofMillis(var1));
   }

   public void setTestOnBorrow(boolean var1) {
      this.testOnBorrow = var1;
   }

   public void setTestOnCreate(boolean var1) {
      this.testOnCreate = var1;
   }

   public void setTestOnReturn(boolean var1) {
      this.testOnReturn = var1;
   }

   public void setTestWhileIdle(boolean var1) {
      this.testWhileIdle = var1;
   }

   public void setTimeBetweenEvictionRuns(Duration var1) {
      this.durationBetweenEvictionRuns = PoolImplUtils.nonNull(var1, DEFAULT_DURATION_BETWEEN_EVICTION_RUNS);
   }

   @Deprecated
   public void setTimeBetweenEvictionRunsMillis(long var1) {
      this.setTimeBetweenEvictionRuns(Duration.ofMillis(var1));
   }

   protected void toStringAppendFields(StringBuilder var1) {
      var1.append("lifo=");
      var1.append(this.lifo);
      var1.append(", fairness=");
      var1.append(this.fairness);
      var1.append(", maxWaitDuration=");
      var1.append(this.maxWaitDuration);
      var1.append(", minEvictableIdleTime=");
      var1.append(this.minEvictableIdleDuration);
      var1.append(", softMinEvictableIdleTime=");
      var1.append(this.softMinEvictableIdleDuration);
      var1.append(", numTestsPerEvictionRun=");
      var1.append(this.numTestsPerEvictionRun);
      var1.append(", evictionPolicyClassName=");
      var1.append(this.evictionPolicyClassName);
      var1.append(", testOnCreate=");
      var1.append(this.testOnCreate);
      var1.append(", testOnBorrow=");
      var1.append(this.testOnBorrow);
      var1.append(", testOnReturn=");
      var1.append(this.testOnReturn);
      var1.append(", testWhileIdle=");
      var1.append(this.testWhileIdle);
      var1.append(", timeBetweenEvictionRuns=");
      var1.append(this.durationBetweenEvictionRuns);
      var1.append(", blockWhenExhausted=");
      var1.append(this.blockWhenExhausted);
      var1.append(", jmxEnabled=");
      var1.append(this.jmxEnabled);
      var1.append(", jmxNamePrefix=");
      var1.append(this.jmxNamePrefix);
      var1.append(", jmxNameBase=");
      var1.append(this.jmxNameBase);
   }
}
