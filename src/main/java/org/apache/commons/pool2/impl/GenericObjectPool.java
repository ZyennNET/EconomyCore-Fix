package org.apache.commons.pool2.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PoolUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.UsageTracking;

public class GenericObjectPool<T> extends BaseGenericObjectPool<T> implements ObjectPool<T>, GenericObjectPoolMXBean, UsageTracking<T> {
   private static final String ONAME_BASE = "org.apache.commons.pool2:type=GenericObjectPool,name=";
   private volatile String factoryType;
   private volatile int maxIdle;
   private volatile int minIdle;
   private final PooledObjectFactory<T> factory;
   private final ConcurrentHashMap<BaseGenericObjectPool.IdentityWrapper<T>, PooledObject<T>> allObjects;
   private final AtomicLong createCount;
   private long makeObjectCount;
   private final Object makeObjectCountLock;
   private final LinkedBlockingDeque<PooledObject<T>> idleObjects;

   private static void wait(Object var0, Duration var1) throws InterruptedException {
      var0.wait(var1.toMillis(), var1.getNano() % 1000000);
   }

   public GenericObjectPool(PooledObjectFactory<T> var1) {
      this(var1, new GenericObjectPoolConfig());
   }

   public GenericObjectPool(PooledObjectFactory<T> var1, GenericObjectPoolConfig<T> var2) {
      super(var2, "org.apache.commons.pool2:type=GenericObjectPool,name=", var2.getJmxNamePrefix());
      this.maxIdle = 8;
      this.minIdle = 0;
      this.allObjects = new ConcurrentHashMap();
      this.createCount = new AtomicLong();
      this.makeObjectCountLock = new Object();
      if (var1 == null) {
         this.jmxUnregister();
         throw new IllegalArgumentException("Factory may not be null");
      } else {
         this.factory = var1;
         this.idleObjects = new LinkedBlockingDeque<PooledObject<T>>(var2.getFairness());
         this.setConfig(var2);
      }
   }

   public GenericObjectPool(PooledObjectFactory<T> var1, GenericObjectPoolConfig<T> var2, AbandonedConfig var3) {
      this(var1, var2);
      this.setAbandonedConfig(var3);
   }

   private void addIdleObject(PooledObject<T> var1) throws Exception {
      if (!PooledObject.isNull(var1)) {
         this.factory.passivateObject(var1);
         if (this.getLifo()) {
            this.idleObjects.addFirst(var1);
         } else {
            this.idleObjects.addLast(var1);
         }
      }

   }

   public void addObject() throws Exception {
      this.assertOpen();
      if (this.factory == null) {
         throw new IllegalStateException("Cannot add objects without a factory.");
      } else {
         this.addIdleObject(this.create());
      }
   }

   public T borrowObject() throws Exception {
      return (T)this.borrowObject(this.getMaxWaitDuration());
   }

   public T borrowObject(Duration var1) throws Exception {
      this.assertOpen();
      AbandonedConfig var2 = this.abandonedConfig;
      if (var2 != null && var2.getRemoveAbandonedOnBorrow() && this.getNumIdle() < 2 && this.getNumActive() > this.getMaxTotal() - 3) {
         this.removeAbandoned(var2);
      }

      PooledObject var3 = null;
      boolean var4 = this.getBlockWhenExhausted();
      Instant var6 = Instant.now();

      while(var3 == null) {
         boolean var5 = false;
         var3 = this.idleObjects.pollFirst();
         if (var3 == null) {
            var3 = this.create();
            if (!PooledObject.isNull(var3)) {
               var5 = true;
            }
         }

         if (var4) {
            if (PooledObject.isNull(var3)) {
               var3 = var1.isNegative() ? (PooledObject)this.idleObjects.takeFirst() : (PooledObject)this.idleObjects.pollFirst(var1);
            }

            if (PooledObject.isNull(var3)) {
               throw new NoSuchElementException(this.appendStats("Timeout waiting for idle object, borrowMaxWaitDuration=" + var1));
            }
         } else if (PooledObject.isNull(var3)) {
            throw new NoSuchElementException(this.appendStats("Pool exhausted"));
         }

         if (!var3.allocate()) {
            var3 = null;
         }

         if (!PooledObject.isNull(var3)) {
            try {
               this.factory.activateObject(var3);
            } catch (Exception var13) {
               try {
                  this.destroy(var3, DestroyMode.NORMAL);
               } catch (Exception var12) {
               }

               var3 = null;
               if (var5) {
                  NoSuchElementException var8 = new NoSuchElementException(this.appendStats("Unable to activate object"));
                  var8.initCause(var13);
                  throw var8;
               }
            }

            if (!PooledObject.isNull(var3) && this.getTestOnBorrow()) {
               boolean var7 = false;
               Throwable var14 = null;

               try {
                  var7 = this.factory.validateObject(var3);
               } catch (Throwable var11) {
                  PoolUtils.checkRethrow(var11);
                  var14 = var11;
               }

               if (!var7) {
                  try {
                     this.destroy(var3, DestroyMode.NORMAL);
                     this.destroyedByBorrowValidationCount.incrementAndGet();
                  } catch (Exception var10) {
                  }

                  var3 = null;
                  if (var5) {
                     NoSuchElementException var9 = new NoSuchElementException(this.appendStats("Unable to validate object"));
                     var9.initCause(var14);
                     throw var9;
                  }
               }
            }
         }
      }

      this.updateStatsBorrow(var3, Duration.between(var6, Instant.now()));
      return (T)var3.getObject();
   }

   public T borrowObject(long var1) throws Exception {
      return (T)this.borrowObject(Duration.ofMillis(var1));
   }

   public void clear() {
      for(PooledObject var1 = this.idleObjects.poll(); var1 != null; var1 = this.idleObjects.poll()) {
         try {
            this.destroy(var1, DestroyMode.NORMAL);
         } catch (Exception var3) {
            this.swallowException(var3);
         }
      }

   }

   public void close() {
      if (!this.isClosed()) {
         synchronized(this.closeLock) {
            if (!this.isClosed()) {
               this.stopEvictor();
               this.closed = true;
               this.clear();
               this.jmxUnregister();
               this.idleObjects.interuptTakeWaiters();
            }
         }
      }
   }

   private PooledObject<T> create() throws Exception {
      int var1 = this.getMaxTotal();
      if (var1 < 0) {
         var1 = Integer.MAX_VALUE;
      }

      Instant var2 = Instant.now();
      Duration var3 = this.getMaxWaitDuration();
      Duration var4 = var3.isNegative() ? Duration.ZERO : var3;
      Boolean var5 = null;

      while(var5 == null) {
         synchronized(this.makeObjectCountLock) {
            long var7 = this.createCount.incrementAndGet();
            if (var7 > (long)var1) {
               this.createCount.decrementAndGet();
               if (this.makeObjectCount == 0L) {
                  var5 = Boolean.FALSE;
               } else {
                  wait(this.makeObjectCountLock, var4);
               }
            } else {
               ++this.makeObjectCount;
               var5 = Boolean.TRUE;
            }
         }

         if (var5 == null && var4.compareTo(Duration.ZERO) > 0 && Duration.between(var2, Instant.now()).compareTo(var4) >= 0) {
            var5 = Boolean.FALSE;
         }
      }

      if (!var5) {
         return null;
      } else {
         PooledObject var6;
         try {
            var6 = this.factory.makeObject();
            if (PooledObject.isNull(var6)) {
               this.createCount.decrementAndGet();
               throw new NullPointerException(String.format("%s.makeObject() = null", this.factory.getClass().getSimpleName()));
            }

            if (this.getTestOnCreate() && !this.factory.validateObject(var6)) {
               this.createCount.decrementAndGet();
               Object var25 = null;
               return (PooledObject<T>)var25;
            }
         } catch (Throwable var23) {
            this.createCount.decrementAndGet();
            throw var23;
         } finally {
            synchronized(this.makeObjectCountLock) {
               --this.makeObjectCount;
               this.makeObjectCountLock.notifyAll();
            }
         }

         AbandonedConfig var26 = this.abandonedConfig;
         if (var26 != null && var26.getLogAbandoned()) {
            var6.setLogAbandoned(true);
            var6.setRequireFullStackTrace(var26.getRequireFullStackTrace());
         }

         this.createdCount.incrementAndGet();
         this.allObjects.put(new BaseGenericObjectPool.IdentityWrapper(var6.getObject()), var6);
         return var6;
      }
   }

   private void destroy(PooledObject<T> var1, DestroyMode var2) throws Exception {
      var1.invalidate();
      this.idleObjects.remove(var1);
      this.allObjects.remove(new BaseGenericObjectPool.IdentityWrapper(var1.getObject()));

      try {
         this.factory.destroyObject(var1, var2);
      } finally {
         this.destroyedCount.incrementAndGet();
         this.createCount.decrementAndGet();
      }

   }

   private void ensureIdle(int var1, boolean var2) throws Exception {
      if (var1 >= 1 && !this.isClosed() && (var2 || this.idleObjects.hasTakeWaiters())) {
         while(this.idleObjects.size() < var1) {
            PooledObject var3 = this.create();
            if (PooledObject.isNull(var3)) {
               break;
            }

            if (this.getLifo()) {
               this.idleObjects.addFirst(var3);
            } else {
               this.idleObjects.addLast(var3);
            }
         }

         if (this.isClosed()) {
            this.clear();
         }

      }
   }

   void ensureMinIdle() throws Exception {
      this.ensureIdle(this.getMinIdle(), true);
   }

   public void evict() throws Exception {
      this.assertOpen();
      if (!this.idleObjects.isEmpty()) {
         Object var1 = null;
         EvictionPolicy var2 = this.getEvictionPolicy();
         synchronized(this.evictionLock) {
            EvictionConfig var4 = new EvictionConfig(this.getMinEvictableIdleDuration(), this.getSoftMinEvictableIdleDuration(), this.getMinIdle());
            boolean var5 = this.getTestWhileIdle();
            int var6 = 0;

            for(int var7 = this.getNumTests(); var6 < var7; ++var6) {
               if (this.evictionIterator == null || !this.evictionIterator.hasNext()) {
                  this.evictionIterator = new BaseGenericObjectPool.EvictionIterator(this.idleObjects);
               }

               if (!this.evictionIterator.hasNext()) {
                  return;
               }

               try {
                  var20 = this.evictionIterator.next();
               } catch (NoSuchElementException var18) {
                  --var6;
                  this.evictionIterator = null;
                  continue;
               }

               if (!var20.startEvictionTest()) {
                  --var6;
               } else {
                  boolean var8;
                  try {
                     var8 = var2.evict(var4, var20, this.idleObjects.size());
                  } catch (Throwable var17) {
                     PoolUtils.checkRethrow(var17);
                     this.swallowException(new Exception(var17));
                     var8 = false;
                  }

                  if (var8) {
                     this.destroy(var20, DestroyMode.NORMAL);
                     this.destroyedByEvictorCount.incrementAndGet();
                  } else {
                     if (var5) {
                        boolean var9 = false;

                        try {
                           this.factory.activateObject(var20);
                           var9 = true;
                        } catch (Exception var16) {
                           this.destroy(var20, DestroyMode.NORMAL);
                           this.destroyedByEvictorCount.incrementAndGet();
                        }

                        if (var9) {
                           boolean var10 = false;
                           Throwable var11 = null;

                           try {
                              var10 = this.factory.validateObject(var20);
                           } catch (Throwable var15) {
                              PoolUtils.checkRethrow(var15);
                              var11 = var15;
                           }

                           if (!var10) {
                              this.destroy(var20, DestroyMode.NORMAL);
                              this.destroyedByEvictorCount.incrementAndGet();
                              if (var11 != null) {
                                 if (var11 instanceof RuntimeException) {
                                    throw (RuntimeException)var11;
                                 }

                                 throw (Error)var11;
                              }
                           } else {
                              try {
                                 this.factory.passivateObject(var20);
                              } catch (Exception var14) {
                                 this.destroy(var20, DestroyMode.NORMAL);
                                 this.destroyedByEvictorCount.incrementAndGet();
                              }
                           }
                        }
                     }

                     var20.endEvictionTest(this.idleObjects);
                  }
               }
            }
         }
      }

      AbandonedConfig var21 = this.abandonedConfig;
      if (var21 != null && var21.getRemoveAbandonedOnMaintenance()) {
         this.removeAbandoned(var21);
      }

   }

   public PooledObjectFactory<T> getFactory() {
      return this.factory;
   }

   public String getFactoryType() {
      if (this.factoryType == null) {
         StringBuilder var1 = new StringBuilder();
         var1.append(this.factory.getClass().getName());
         var1.append('<');
         Class var2 = PoolImplUtils.getFactoryType(this.factory.getClass());
         var1.append(var2.getName());
         var1.append('>');
         this.factoryType = var1.toString();
      }

      return this.factoryType;
   }

   public int getMaxIdle() {
      return this.maxIdle;
   }

   public int getMinIdle() {
      int var1 = this.getMaxIdle();
      return Math.min(this.minIdle, var1);
   }

   public int getNumActive() {
      return this.allObjects.size() - this.idleObjects.size();
   }

   public int getNumIdle() {
      return this.idleObjects.size();
   }

   private int getNumTests() {
      int var1 = this.getNumTestsPerEvictionRun();
      return var1 >= 0 ? Math.min(var1, this.idleObjects.size()) : (int)Math.ceil((double)this.idleObjects.size() / Math.abs((double)var1));
   }

   public int getNumWaiters() {
      return this.getBlockWhenExhausted() ? this.idleObjects.getTakeQueueLength() : 0;
   }

   PooledObject<T> getPooledObject(T var1) {
      return (PooledObject)this.allObjects.get(new BaseGenericObjectPool.IdentityWrapper(var1));
   }

   String getStatsString() {
      return super.getStatsString() + String.format(", createdCount=%,d, makeObjectCount=%,d, maxIdle=%,d, minIdle=%,d", this.createdCount.get(), this.makeObjectCount, this.maxIdle, this.minIdle);
   }

   public void invalidateObject(T var1) throws Exception {
      this.invalidateObject(var1, DestroyMode.NORMAL);
   }

   public void invalidateObject(T var1, DestroyMode var2) throws Exception {
      PooledObject var3 = this.getPooledObject(var1);
      if (var3 == null) {
         if (!this.isAbandonedConfig()) {
            throw new IllegalStateException("Invalidated object not currently part of this pool");
         }
      } else {
         synchronized(var3) {
            if (var3.getState() != PooledObjectState.INVALID) {
               this.destroy(var3, var2);
            }
         }

         this.ensureIdle(1, false);
      }
   }

   public Set<DefaultPooledObjectInfo> listAllObjects() {
      return (Set)this.allObjects.values().stream().map(DefaultPooledObjectInfo::new).collect(Collectors.toSet());
   }

   public void preparePool() throws Exception {
      if (this.getMinIdle() >= 1) {
         this.ensureMinIdle();
      }
   }

   private void removeAbandoned(AbandonedConfig var1) {
      ArrayList var2 = this.createRemoveList(var1, this.allObjects);
      var2.forEach((var2x) -> {
         if (var1.getLogAbandoned()) {
            var2x.printStackTrace(var1.getLogWriter());
         }

         try {
            this.invalidateObject(var2x.getObject(), DestroyMode.ABANDONED);
         } catch (Exception var4) {
            this.swallowException(var4);
         }

      });
   }

   public void returnObject(T var1) {
      PooledObject var2 = this.getPooledObject(var1);
      if (var2 == null) {
         if (!this.isAbandonedConfig()) {
            throw new IllegalStateException("Returned object not currently part of this pool");
         }
      } else {
         this.markReturningState(var2);
         Duration var3 = var2.getActiveDuration();
         if (this.getTestOnReturn() && !this.factory.validateObject(var2)) {
            try {
               this.destroy(var2, DestroyMode.NORMAL);
            } catch (Exception var9) {
               this.swallowException(var9);
            }

            try {
               this.ensureIdle(1, false);
            } catch (Exception var8) {
               this.swallowException(var8);
            }

            this.updateStatsReturn(var3);
         } else {
            try {
               this.factory.passivateObject(var2);
            } catch (Exception var12) {
               this.swallowException(var12);

               try {
                  this.destroy(var2, DestroyMode.NORMAL);
               } catch (Exception var7) {
                  this.swallowException(var7);
               }

               try {
                  this.ensureIdle(1, false);
               } catch (Exception var6) {
                  this.swallowException(var6);
               }

               this.updateStatsReturn(var3);
               return;
            }

            if (!var2.deallocate()) {
               throw new IllegalStateException("Object has already been returned to this pool or is invalid");
            } else {
               int var4 = this.getMaxIdle();
               if (this.isClosed() || var4 > -1 && var4 <= this.idleObjects.size()) {
                  try {
                     this.destroy(var2, DestroyMode.NORMAL);
                  } catch (Exception var11) {
                     this.swallowException(var11);
                  }

                  try {
                     this.ensureIdle(1, false);
                  } catch (Exception var10) {
                     this.swallowException(var10);
                  }
               } else {
                  if (this.getLifo()) {
                     this.idleObjects.addFirst(var2);
                  } else {
                     this.idleObjects.addLast(var2);
                  }

                  if (this.isClosed()) {
                     this.clear();
                  }
               }

               this.updateStatsReturn(var3);
            }
         }
      }
   }

   public void setConfig(GenericObjectPoolConfig<T> var1) {
      super.setConfig(var1);
      this.setMaxIdle(var1.getMaxIdle());
      this.setMinIdle(var1.getMinIdle());
      this.setMaxTotal(var1.getMaxTotal());
   }

   public void setMaxIdle(int var1) {
      this.maxIdle = var1;
   }

   public void setMinIdle(int var1) {
      this.minIdle = var1;
   }

   protected void toStringAppendFields(StringBuilder var1) {
      super.toStringAppendFields(var1);
      var1.append(", factoryType=");
      var1.append(this.factoryType);
      var1.append(", maxIdle=");
      var1.append(this.maxIdle);
      var1.append(", minIdle=");
      var1.append(this.minIdle);
      var1.append(", factory=");
      var1.append(this.factory);
      var1.append(", allObjects=");
      var1.append(this.allObjects);
      var1.append(", createCount=");
      var1.append(this.createCount);
      var1.append(", idleObjects=");
      var1.append(this.idleObjects);
      var1.append(", abandonedConfig=");
      var1.append(this.abandonedConfig);
   }

   public void use(T var1) {
      AbandonedConfig var2 = this.abandonedConfig;
      if (var2 != null && var2.getUseUsageTracking()) {
         PooledObject var3 = this.getPooledObject(var1);
         if (var3 != null) {
            var3.use();
         }
      }

   }
}
