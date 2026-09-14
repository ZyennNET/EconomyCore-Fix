package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.apache.commons.pool2.BaseObject;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.SwallowedExceptionListener;

public abstract class BaseGenericObjectPool<T> extends BaseObject implements AutoCloseable {
   public static final int MEAN_TIMING_STATS_CACHE_SIZE = 100;
   private static final String EVICTION_POLICY_TYPE_NAME = EvictionPolicy.class.getName();
   private static final Duration DEFAULT_REMOVE_ABANDONED_TIMEOUT = Duration.ofSeconds(2147483647L);
   private volatile int maxTotal = -1;
   private volatile boolean blockWhenExhausted = true;
   private volatile Duration maxWaitDuration;
   private volatile boolean lifo;
   private final boolean fairness;
   private volatile boolean testOnCreate;
   private volatile boolean testOnBorrow;
   private volatile boolean testOnReturn;
   private volatile boolean testWhileIdle;
   private volatile Duration durationBetweenEvictionRuns;
   private volatile int numTestsPerEvictionRun;
   private volatile Duration minEvictableIdleDuration;
   private volatile Duration softMinEvictableIdleDuration;
   private volatile EvictionPolicy<T> evictionPolicy;
   private volatile Duration evictorShutdownTimeoutDuration;
   final Object closeLock;
   volatile boolean closed;
   final Object evictionLock;
   private BaseGenericObjectPool<T>.Evictor evictor;
   BaseGenericObjectPool<T>.EvictionIterator evictionIterator;
   private final WeakReference<ClassLoader> factoryClassLoader;
   private final ObjectName objectName;
   private final String creationStackTrace;
   private final AtomicLong borrowedCount;
   private final AtomicLong returnedCount;
   final AtomicLong createdCount;
   final AtomicLong destroyedCount;
   final AtomicLong destroyedByEvictorCount;
   final AtomicLong destroyedByBorrowValidationCount;
   private final StatsStore activeTimes;
   private final StatsStore idleTimes;
   private final StatsStore waitTimes;
   private final AtomicReference<Duration> maxBorrowWaitDuration;
   private volatile SwallowedExceptionListener swallowedExceptionListener;
   private volatile boolean messageStatistics;
   protected volatile AbandonedConfig abandonedConfig;

   public BaseGenericObjectPool(BaseObjectPoolConfig<T> var1, String var2, String var3) {
      this.maxWaitDuration = BaseObjectPoolConfig.DEFAULT_MAX_WAIT;
      this.lifo = true;
      this.testOnCreate = false;
      this.testOnBorrow = false;
      this.testOnReturn = false;
      this.testWhileIdle = false;
      this.durationBetweenEvictionRuns = BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS;
      this.numTestsPerEvictionRun = 3;
      this.minEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION;
      this.softMinEvictableIdleDuration = BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION;
      this.evictorShutdownTimeoutDuration = BaseObjectPoolConfig.DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT;
      this.closeLock = new Object();
      this.evictionLock = new Object();
      this.borrowedCount = new AtomicLong();
      this.returnedCount = new AtomicLong();
      this.createdCount = new AtomicLong();
      this.destroyedCount = new AtomicLong();
      this.destroyedByEvictorCount = new AtomicLong();
      this.destroyedByBorrowValidationCount = new AtomicLong();
      this.activeTimes = new StatsStore(100);
      this.idleTimes = new StatsStore(100);
      this.waitTimes = new StatsStore(100);
      this.maxBorrowWaitDuration = new AtomicReference(Duration.ZERO);
      if (var1.getJmxEnabled()) {
         this.objectName = this.jmxRegister(var1, var2, var3);
      } else {
         this.objectName = null;
      }

      this.creationStackTrace = this.getStackTrace(new Exception());
      ClassLoader var4 = Thread.currentThread().getContextClassLoader();
      if (var4 == null) {
         this.factoryClassLoader = null;
      } else {
         this.factoryClassLoader = new WeakReference(var4);
      }

      this.fairness = var1.getFairness();
   }

   String appendStats(String var1) {
      return this.messageStatistics ? var1 + ", " + this.getStatsString() : var1;
   }

   final void assertOpen() throws IllegalStateException {
      if (this.isClosed()) {
         throw new IllegalStateException("Pool not open");
      }
   }

   public abstract void close();

   ArrayList<PooledObject<T>> createRemoveList(AbandonedConfig var1, Map<IdentityWrapper<T>, PooledObject<T>> var2) {
      Instant var3 = Instant.now().minus(var1.getRemoveAbandonedTimeoutDuration());
      ArrayList var4 = new ArrayList();
      var2.values().forEach((var2x) -> {
         synchronized(var2x) {
            if (var2x.getState() == PooledObjectState.ALLOCATED && var2x.getLastUsedInstant().compareTo(var3) <= 0) {
               var2x.markAbandoned();
               var4.add(var2x);
            }

         }
      });
      return var4;
   }

   abstract void ensureMinIdle() throws Exception;

   public abstract void evict() throws Exception;

   public final boolean getBlockWhenExhausted() {
      return this.blockWhenExhausted;
   }

   public final long getBorrowedCount() {
      return this.borrowedCount.get();
   }

   public final long getCreatedCount() {
      return this.createdCount.get();
   }

   public final String getCreationStackTrace() {
      return this.creationStackTrace;
   }

   public final long getDestroyedByBorrowValidationCount() {
      return this.destroyedByBorrowValidationCount.get();
   }

   public final long getDestroyedByEvictorCount() {
      return this.destroyedByEvictorCount.get();
   }

   public final long getDestroyedCount() {
      return this.destroyedCount.get();
   }

   public final Duration getDurationBetweenEvictionRuns() {
      return this.durationBetweenEvictionRuns;
   }

   public EvictionPolicy<T> getEvictionPolicy() {
      return this.evictionPolicy;
   }

   public final String getEvictionPolicyClassName() {
      return this.evictionPolicy.getClass().getName();
   }

   @Deprecated
   public final Duration getEvictorShutdownTimeout() {
      return this.evictorShutdownTimeoutDuration;
   }

   public final Duration getEvictorShutdownTimeoutDuration() {
      return this.evictorShutdownTimeoutDuration;
   }

   @Deprecated
   public final long getEvictorShutdownTimeoutMillis() {
      return this.evictorShutdownTimeoutDuration.toMillis();
   }

   public final boolean getFairness() {
      return this.fairness;
   }

   public final ObjectName getJmxName() {
      return this.objectName;
   }

   public final boolean getLifo() {
      return this.lifo;
   }

   public boolean getLogAbandoned() {
      AbandonedConfig var1 = this.abandonedConfig;
      return var1 != null && var1.getLogAbandoned();
   }

   public final Duration getMaxBorrowWaitDuration() {
      return (Duration)this.maxBorrowWaitDuration.get();
   }

   @Deprecated
   public final long getMaxBorrowWaitTimeMillis() {
      return ((Duration)this.maxBorrowWaitDuration.get()).toMillis();
   }

   public final int getMaxTotal() {
      return this.maxTotal;
   }

   public final Duration getMaxWaitDuration() {
      return this.maxWaitDuration;
   }

   @Deprecated
   public final long getMaxWaitMillis() {
      return this.maxWaitDuration.toMillis();
   }

   public final Duration getMeanActiveDuration() {
      return this.activeTimes.getMeanDuration();
   }

   @Deprecated
   public final long getMeanActiveTimeMillis() {
      return this.activeTimes.getMean();
   }

   public final Duration getMeanBorrowWaitDuration() {
      return this.waitTimes.getMeanDuration();
   }

   @Deprecated
   public final long getMeanBorrowWaitTimeMillis() {
      return this.waitTimes.getMean();
   }

   public final Duration getMeanIdleDuration() {
      return this.idleTimes.getMeanDuration();
   }

   @Deprecated
   public final long getMeanIdleTimeMillis() {
      return this.idleTimes.getMean();
   }

   public boolean getMessageStatistics() {
      return this.messageStatistics;
   }

   public final Duration getMinEvictableIdleDuration() {
      return this.minEvictableIdleDuration;
   }

   @Deprecated
   public final Duration getMinEvictableIdleTime() {
      return this.minEvictableIdleDuration;
   }

   @Deprecated
   public final long getMinEvictableIdleTimeMillis() {
      return this.minEvictableIdleDuration.toMillis();
   }

   public abstract int getNumIdle();

   public final int getNumTestsPerEvictionRun() {
      return this.numTestsPerEvictionRun;
   }

   public boolean getRemoveAbandonedOnBorrow() {
      AbandonedConfig var1 = this.abandonedConfig;
      return var1 != null && var1.getRemoveAbandonedOnBorrow();
   }

   public boolean getRemoveAbandonedOnMaintenance() {
      AbandonedConfig var1 = this.abandonedConfig;
      return var1 != null && var1.getRemoveAbandonedOnMaintenance();
   }

   @Deprecated
   public int getRemoveAbandonedTimeout() {
      return (int)this.getRemoveAbandonedTimeoutDuration().getSeconds();
   }

   public Duration getRemoveAbandonedTimeoutDuration() {
      AbandonedConfig var1 = this.abandonedConfig;
      return var1 != null ? var1.getRemoveAbandonedTimeoutDuration() : DEFAULT_REMOVE_ABANDONED_TIMEOUT;
   }

   public final long getReturnedCount() {
      return this.returnedCount.get();
   }

   public final Duration getSoftMinEvictableIdleDuration() {
      return this.softMinEvictableIdleDuration;
   }

   @Deprecated
   public final Duration getSoftMinEvictableIdleTime() {
      return this.softMinEvictableIdleDuration;
   }

   @Deprecated
   public final long getSoftMinEvictableIdleTimeMillis() {
      return this.softMinEvictableIdleDuration.toMillis();
   }

   private String getStackTrace(Exception var1) {
      StringWriter var2 = new StringWriter();
      PrintWriter var3 = new PrintWriter(var2);
      var1.printStackTrace(var3);
      return var2.toString();
   }

   String getStatsString() {
      return String.format("activeTimes=%s, blockWhenExhausted=%s, borrowedCount=%,d, closed=%s, createdCount=%,d, destroyedByBorrowValidationCount=%,d, destroyedByEvictorCount=%,d, evictorShutdownTimeoutDuration=%s, fairness=%s, idleTimes=%s, lifo=%s, maxBorrowWaitDuration=%s, maxTotal=%s, maxWaitDuration=%s, minEvictableIdleDuration=%s, numTestsPerEvictionRun=%s, returnedCount=%s, softMinEvictableIdleDuration=%s, testOnBorrow=%s, testOnCreate=%s, testOnReturn=%s, testWhileIdle=%s, durationBetweenEvictionRuns=%s, waitTimes=%s", this.activeTimes.getValues(), this.blockWhenExhausted, this.borrowedCount.get(), this.closed, this.createdCount.get(), this.destroyedByBorrowValidationCount.get(), this.destroyedByEvictorCount.get(), this.evictorShutdownTimeoutDuration, this.fairness, this.idleTimes.getValues(), this.lifo, this.maxBorrowWaitDuration.get(), this.maxTotal, this.maxWaitDuration, this.minEvictableIdleDuration, this.numTestsPerEvictionRun, this.returnedCount, this.softMinEvictableIdleDuration, this.testOnBorrow, this.testOnCreate, this.testOnReturn, this.testWhileIdle, this.durationBetweenEvictionRuns, this.waitTimes.getValues());
   }

   public final SwallowedExceptionListener getSwallowedExceptionListener() {
      return this.swallowedExceptionListener;
   }

   public final boolean getTestOnBorrow() {
      return this.testOnBorrow;
   }

   public final boolean getTestOnCreate() {
      return this.testOnCreate;
   }

   public final boolean getTestOnReturn() {
      return this.testOnReturn;
   }

   public final boolean getTestWhileIdle() {
      return this.testWhileIdle;
   }

   @Deprecated
   public final Duration getTimeBetweenEvictionRuns() {
      return this.durationBetweenEvictionRuns;
   }

   @Deprecated
   public final long getTimeBetweenEvictionRunsMillis() {
      return this.durationBetweenEvictionRuns.toMillis();
   }

   public boolean isAbandonedConfig() {
      return this.abandonedConfig != null;
   }

   public final boolean isClosed() {
      return this.closed;
   }

   private ObjectName jmxRegister(BaseObjectPoolConfig<T> var1, String var2, String var3) {
      ObjectName var4 = null;
      MBeanServer var5 = ManagementFactory.getPlatformMBeanServer();
      int var6 = 1;
      boolean var7 = false;
      String var8 = var1.getJmxNameBase();
      if (var8 == null) {
         var8 = var2;
      }

      while(!var7) {
         try {
            ObjectName var9;
            if (var6 == 1) {
               var9 = new ObjectName(var8 + var3);
            } else {
               var9 = new ObjectName(var8 + var3 + var6);
            }

            if (!var5.isRegistered(var9)) {
               var5.registerMBean(this, var9);
               var4 = var9;
               var7 = true;
            } else {
               ++var6;
            }
         } catch (MalformedObjectNameException var10) {
            if ("pool".equals(var3) && var2.equals(var8)) {
               var7 = true;
            } else {
               var3 = "pool";
               var8 = var2;
            }
         } catch (InstanceAlreadyExistsException var11) {
            ++var6;
         } catch (NotCompliantMBeanException | MBeanRegistrationException var12) {
            var7 = true;
         }
      }

      return var4;
   }

   final void jmxUnregister() {
      if (this.objectName != null) {
         try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(this.objectName);
         } catch (InstanceNotFoundException | MBeanRegistrationException var2) {
            this.swallowException(var2);
         }
      }

   }

   protected void markReturningState(PooledObject<T> var1) {
      synchronized(var1) {
         if (var1.getState() != PooledObjectState.ALLOCATED) {
            throw new IllegalStateException("Object has already been returned to this pool or is invalid");
         } else {
            var1.markReturning();
         }
      }
   }

   public void setAbandonedConfig(AbandonedConfig var1) {
      this.abandonedConfig = AbandonedConfig.copy(var1);
   }

   public final void setBlockWhenExhausted(boolean var1) {
      this.blockWhenExhausted = var1;
   }

   protected void setConfig(BaseObjectPoolConfig<T> var1) {
      this.setLifo(var1.getLifo());
      this.setMaxWait(var1.getMaxWaitDuration());
      this.setBlockWhenExhausted(var1.getBlockWhenExhausted());
      this.setTestOnCreate(var1.getTestOnCreate());
      this.setTestOnBorrow(var1.getTestOnBorrow());
      this.setTestOnReturn(var1.getTestOnReturn());
      this.setTestWhileIdle(var1.getTestWhileIdle());
      this.setNumTestsPerEvictionRun(var1.getNumTestsPerEvictionRun());
      this.setMinEvictableIdleDuration(var1.getMinEvictableIdleDuration());
      this.setDurationBetweenEvictionRuns(var1.getDurationBetweenEvictionRuns());
      this.setSoftMinEvictableIdleDuration(var1.getSoftMinEvictableIdleDuration());
      EvictionPolicy var2 = var1.getEvictionPolicy();
      if (var2 == null) {
         this.setEvictionPolicyClassName(var1.getEvictionPolicyClassName());
      } else {
         this.setEvictionPolicy(var2);
      }

      this.setEvictorShutdownTimeout(var1.getEvictorShutdownTimeoutDuration());
   }

   public final void setDurationBetweenEvictionRuns(Duration var1) {
      this.durationBetweenEvictionRuns = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS);
      this.startEvictor(this.durationBetweenEvictionRuns);
   }

   public void setEvictionPolicy(EvictionPolicy<T> var1) {
      this.evictionPolicy = var1;
   }

   private void setEvictionPolicy(String var1, ClassLoader var2) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      Class var3 = Class.forName(var1, true, var2);
      Object var4 = var3.getConstructor().newInstance();
      this.evictionPolicy = (EvictionPolicy)var4;
   }

   public final void setEvictionPolicyClassName(String var1) {
      this.setEvictionPolicyClassName(var1, Thread.currentThread().getContextClassLoader());
   }

   public final void setEvictionPolicyClassName(String var1, ClassLoader var2) {
      Class var3 = EvictionPolicy.class;
      ClassLoader var4 = var3.getClassLoader();

      try {
         try {
            this.setEvictionPolicy(var1, var2);
         } catch (ClassNotFoundException | ClassCastException var6) {
            this.setEvictionPolicy(var1, var4);
         }

      } catch (ClassCastException var7) {
         throw new IllegalArgumentException("Class " + var1 + " from class loaders [" + var2 + ", " + var4 + "] do not implement " + EVICTION_POLICY_TYPE_NAME);
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException var8) {
         throw new IllegalArgumentException("Unable to create " + EVICTION_POLICY_TYPE_NAME + " instance of type " + var1, var8);
      }
   }

   public final void setEvictorShutdownTimeout(Duration var1) {
      this.evictorShutdownTimeoutDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_EVICTOR_SHUTDOWN_TIMEOUT);
   }

   @Deprecated
   public final void setEvictorShutdownTimeoutMillis(long var1) {
      this.setEvictorShutdownTimeout(Duration.ofMillis(var1));
   }

   public final void setLifo(boolean var1) {
      this.lifo = var1;
   }

   public final void setMaxTotal(int var1) {
      this.maxTotal = var1;
   }

   public final void setMaxWait(Duration var1) {
      this.maxWaitDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_MAX_WAIT);
   }

   @Deprecated
   public final void setMaxWaitMillis(long var1) {
      this.setMaxWait(Duration.ofMillis(var1));
   }

   public void setMessagesStatistics(boolean var1) {
      this.messageStatistics = var1;
   }

   @Deprecated
   public final void setMinEvictableIdle(Duration var1) {
      this.minEvictableIdleDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION);
   }

   public final void setMinEvictableIdleDuration(Duration var1) {
      this.minEvictableIdleDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION);
   }

   @Deprecated
   public final void setMinEvictableIdleTime(Duration var1) {
      this.minEvictableIdleDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_MIN_EVICTABLE_IDLE_DURATION);
   }

   @Deprecated
   public final void setMinEvictableIdleTimeMillis(long var1) {
      this.setMinEvictableIdleTime(Duration.ofMillis(var1));
   }

   public final void setNumTestsPerEvictionRun(int var1) {
      this.numTestsPerEvictionRun = var1;
   }

   @Deprecated
   public final void setSoftMinEvictableIdle(Duration var1) {
      this.softMinEvictableIdleDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION);
   }

   public final void setSoftMinEvictableIdleDuration(Duration var1) {
      this.softMinEvictableIdleDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION);
   }

   @Deprecated
   public final void setSoftMinEvictableIdleTime(Duration var1) {
      this.softMinEvictableIdleDuration = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_SOFT_MIN_EVICTABLE_IDLE_DURATION);
   }

   @Deprecated
   public final void setSoftMinEvictableIdleTimeMillis(long var1) {
      this.setSoftMinEvictableIdleTime(Duration.ofMillis(var1));
   }

   public final void setSwallowedExceptionListener(SwallowedExceptionListener var1) {
      this.swallowedExceptionListener = var1;
   }

   public final void setTestOnBorrow(boolean var1) {
      this.testOnBorrow = var1;
   }

   public final void setTestOnCreate(boolean var1) {
      this.testOnCreate = var1;
   }

   public final void setTestOnReturn(boolean var1) {
      this.testOnReturn = var1;
   }

   public final void setTestWhileIdle(boolean var1) {
      this.testWhileIdle = var1;
   }

   @Deprecated
   public final void setTimeBetweenEvictionRuns(Duration var1) {
      this.durationBetweenEvictionRuns = PoolImplUtils.nonNull(var1, BaseObjectPoolConfig.DEFAULT_DURATION_BETWEEN_EVICTION_RUNS);
      this.startEvictor(this.durationBetweenEvictionRuns);
   }

   @Deprecated
   public final void setTimeBetweenEvictionRunsMillis(long var1) {
      this.setTimeBetweenEvictionRuns(Duration.ofMillis(var1));
   }

   final void startEvictor(Duration var1) {
      synchronized(this.evictionLock) {
         boolean var3 = PoolImplUtils.isPositive(var1);
         if (this.evictor == null) {
            if (var3) {
               this.evictor = new Evictor();
               EvictionTimer.schedule(this.evictor, var1, var1);
            }
         } else if (var3) {
            synchronized(EvictionTimer.class) {
               EvictionTimer.cancel(this.evictor, this.evictorShutdownTimeoutDuration, true);
               this.evictor = null;
               this.evictionIterator = null;
               this.evictor = new Evictor();
               EvictionTimer.schedule(this.evictor, var1, var1);
            }
         } else {
            EvictionTimer.cancel(this.evictor, this.evictorShutdownTimeoutDuration, false);
         }

      }
   }

   void stopEvictor() {
      this.startEvictor(Duration.ofMillis(-1L));
   }

   final void swallowException(Exception var1) {
      SwallowedExceptionListener var2 = this.getSwallowedExceptionListener();
      if (var2 != null) {
         try {
            var2.onSwallowException(var1);
         } catch (VirtualMachineError var4) {
            throw var4;
         } catch (Throwable var5) {
         }

      }
   }

   protected void toStringAppendFields(StringBuilder var1) {
      var1.append("maxTotal=");
      var1.append(this.maxTotal);
      var1.append(", blockWhenExhausted=");
      var1.append(this.blockWhenExhausted);
      var1.append(", maxWaitDuration=");
      var1.append(this.maxWaitDuration);
      var1.append(", lifo=");
      var1.append(this.lifo);
      var1.append(", fairness=");
      var1.append(this.fairness);
      var1.append(", testOnCreate=");
      var1.append(this.testOnCreate);
      var1.append(", testOnBorrow=");
      var1.append(this.testOnBorrow);
      var1.append(", testOnReturn=");
      var1.append(this.testOnReturn);
      var1.append(", testWhileIdle=");
      var1.append(this.testWhileIdle);
      var1.append(", durationBetweenEvictionRuns=");
      var1.append(this.durationBetweenEvictionRuns);
      var1.append(", numTestsPerEvictionRun=");
      var1.append(this.numTestsPerEvictionRun);
      var1.append(", minEvictableIdleTimeDuration=");
      var1.append(this.minEvictableIdleDuration);
      var1.append(", softMinEvictableIdleTimeDuration=");
      var1.append(this.softMinEvictableIdleDuration);
      var1.append(", evictionPolicy=");
      var1.append(this.evictionPolicy);
      var1.append(", closeLock=");
      var1.append(this.closeLock);
      var1.append(", closed=");
      var1.append(this.closed);
      var1.append(", evictionLock=");
      var1.append(this.evictionLock);
      var1.append(", evictor=");
      var1.append(this.evictor);
      var1.append(", evictionIterator=");
      var1.append(this.evictionIterator);
      var1.append(", factoryClassLoader=");
      var1.append(this.factoryClassLoader);
      var1.append(", oname=");
      var1.append(this.objectName);
      var1.append(", creationStackTrace=");
      var1.append(this.creationStackTrace);
      var1.append(", borrowedCount=");
      var1.append(this.borrowedCount);
      var1.append(", returnedCount=");
      var1.append(this.returnedCount);
      var1.append(", createdCount=");
      var1.append(this.createdCount);
      var1.append(", destroyedCount=");
      var1.append(this.destroyedCount);
      var1.append(", destroyedByEvictorCount=");
      var1.append(this.destroyedByEvictorCount);
      var1.append(", destroyedByBorrowValidationCount=");
      var1.append(this.destroyedByBorrowValidationCount);
      var1.append(", activeTimes=");
      var1.append(this.activeTimes);
      var1.append(", idleTimes=");
      var1.append(this.idleTimes);
      var1.append(", waitTimes=");
      var1.append(this.waitTimes);
      var1.append(", maxBorrowWaitDuration=");
      var1.append(this.maxBorrowWaitDuration);
      var1.append(", swallowedExceptionListener=");
      var1.append(this.swallowedExceptionListener);
   }

   final void updateStatsBorrow(PooledObject<T> var1, Duration var2) {
      this.borrowedCount.incrementAndGet();
      this.idleTimes.add(var1.getIdleDuration());
      this.waitTimes.add(var2);

      Duration var3;
      do {
         var3 = (Duration)this.maxBorrowWaitDuration.get();
      } while(var3.compareTo(var2) < 0 && !this.maxBorrowWaitDuration.compareAndSet(var3, var2));

   }

   final void updateStatsReturn(Duration var1) {
      this.returnedCount.incrementAndGet();
      this.activeTimes.add(var1);
   }

   class EvictionIterator implements Iterator<PooledObject<T>> {
      private final Deque<PooledObject<T>> idleObjects;
      private final Iterator<PooledObject<T>> idleObjectIterator;

      EvictionIterator(Deque<PooledObject<T>> var2) {
         this.idleObjects = var2;
         if (BaseGenericObjectPool.this.getLifo()) {
            this.idleObjectIterator = var2.descendingIterator();
         } else {
            this.idleObjectIterator = var2.iterator();
         }

      }

      public Deque<PooledObject<T>> getIdleObjects() {
         return this.idleObjects;
      }

      public boolean hasNext() {
         return this.idleObjectIterator.hasNext();
      }

      public PooledObject<T> next() {
         return (PooledObject)this.idleObjectIterator.next();
      }

      public void remove() {
         this.idleObjectIterator.remove();
      }
   }

   class Evictor implements Runnable {
      private ScheduledFuture<?> scheduledFuture;

      void cancel() {
         this.scheduledFuture.cancel(false);
      }

      BaseGenericObjectPool<T> owner() {
         return BaseGenericObjectPool.this;
      }

      public void run() {
         ClassLoader var1 = Thread.currentThread().getContextClassLoader();

         try {
            if (BaseGenericObjectPool.this.factoryClassLoader != null) {
               ClassLoader var2 = (ClassLoader)BaseGenericObjectPool.this.factoryClassLoader.get();
               if (var2 == null) {
                  this.cancel();
                  return;
               }

               Thread.currentThread().setContextClassLoader(var2);
            }

            try {
               BaseGenericObjectPool.this.evict();
            } catch (Exception var9) {
               BaseGenericObjectPool.this.swallowException(var9);
            } catch (OutOfMemoryError var10) {
               var10.printStackTrace(System.err);
            }

            try {
               BaseGenericObjectPool.this.ensureMinIdle();
            } catch (Exception var8) {
               BaseGenericObjectPool.this.swallowException(var8);
            }

         } finally {
            Thread.currentThread().setContextClassLoader(var1);
         }
      }

      void setScheduledFuture(ScheduledFuture<?> var1) {
         this.scheduledFuture = var1;
      }

      public String toString() {
         return this.getClass().getName() + " [scheduledFuture=" + this.scheduledFuture + "]";
      }
   }

   static class IdentityWrapper<T> {
      private final T instance;

      public IdentityWrapper(T var1) {
         this.instance = var1;
      }

      public boolean equals(Object var1) {
         return var1 instanceof IdentityWrapper && ((IdentityWrapper)var1).instance == this.instance;
      }

      public T getObject() {
         return this.instance;
      }

      public int hashCode() {
         return System.identityHashCode(this.instance);
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("IdentityWrapper [instance=");
         var1.append(this.instance);
         var1.append("]");
         return var1.toString();
      }
   }

   private static class StatsStore {
      private static final int NONE = -1;
      private final AtomicLong[] values;
      private final int size;
      private int index;

      StatsStore(int var1) {
         this.size = var1;
         this.values = new AtomicLong[var1];
         Arrays.setAll(this.values, (var0) -> new AtomicLong(-1L));
      }

      void add(Duration var1) {
         this.add(var1.toMillis());
      }

      synchronized void add(long var1) {
         this.values[this.index].set(var1);
         ++this.index;
         if (this.index == this.size) {
            this.index = 0;
         }

      }

      public long getMean() {
         double var1 = (double)0.0F;
         int var3 = 0;

         for(int var4 = 0; var4 < this.size; ++var4) {
            long var5 = this.values[var4].get();
            if (var5 != -1L) {
               ++var3;
               var1 = var1 * ((double)(var3 - 1) / (double)var3) + (double)var5 / (double)var3;
            }
         }

         return (long)var1;
      }

      Duration getMeanDuration() {
         return Duration.ofMillis(this.getMean());
      }

      synchronized List<AtomicLong> getValues() {
         return (List)Arrays.stream(this.values, 0, this.index).collect(Collectors.toList());
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("StatsStore [");
         var1.append(this.getValues());
         var1.append("], size=");
         var1.append(this.size);
         var1.append(", index=");
         var1.append(this.index);
         var1.append("]");
         return var1.toString();
      }
   }
}
