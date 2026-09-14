package com.zaxxer.hikari.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConcurrentBag<T extends ConcurrentBag.IConcurrentBagEntry> implements AutoCloseable {
   private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentBag.class);
   private final CopyOnWriteArrayList<T> sharedList;
   private final boolean weakThreadLocals;
   private final ThreadLocal<List<Object>> threadList;
   private final IBagStateListener listener;
   private final AtomicInteger waiters;
   private volatile boolean closed;
   private final SynchronousQueue<T> handoffQueue;

   public ConcurrentBag(IBagStateListener var1) {
      this.listener = var1;
      this.weakThreadLocals = this.useWeakThreadLocals();
      this.handoffQueue = new SynchronousQueue(true);
      this.waiters = new AtomicInteger();
      this.sharedList = new CopyOnWriteArrayList();
      if (this.weakThreadLocals) {
         this.threadList = ThreadLocal.withInitial(() -> new ArrayList(16));
      } else {
         this.threadList = ThreadLocal.withInitial(() -> new FastList(IConcurrentBagEntry.class, 16));
      }

   }

   public T borrow(long var1, TimeUnit var3) throws InterruptedException {
      List var4 = (List)this.threadList.get();

      for(int var5 = var4.size() - 1; var5 >= 0; --var5) {
         Object var6 = var4.remove(var5);
         IConcurrentBagEntry var7 = this.weakThreadLocals ? (IConcurrentBagEntry)((WeakReference)var6).get() : (IConcurrentBagEntry)var6;
         if (var7 != null && var7.compareAndSet(0, 1)) {
            return (T)var7;
         }
      }

      int var14 = this.waiters.incrementAndGet();

      try {
         for(IConcurrentBagEntry var18 : this.sharedList) {
            if (var18.compareAndSet(0, 1)) {
               if (var14 > 1) {
                  this.listener.addBagItem(var14 - 1);
               }

               IConcurrentBagEntry var8 = var18;
               return (T)var8;
            }
         }

         this.listener.addBagItem(var14);
         var1 = var3.toNanos(var1);

         do {
            long var16 = ClockSource.currentTime();
            IConcurrentBagEntry var19 = (IConcurrentBagEntry)this.handoffQueue.poll(var1, TimeUnit.NANOSECONDS);
            if (var19 == null || var19.compareAndSet(0, 1)) {
               IConcurrentBagEntry var9 = var19;
               return (T)var9;
            }

            var1 -= ClockSource.elapsedNanos(var16);
         } while(var1 > 10000L);

         Object var17 = null;
         return (T)var17;
      } finally {
         this.waiters.decrementAndGet();
      }
   }

   public void requite(T var1) {
      var1.setState(0);

      for(int var2 = 0; this.waiters.get() > 0; ++var2) {
         if (var1.getState() != 0 || this.handoffQueue.offer(var1)) {
            return;
         }

         if ((var2 & 255) == 255) {
            LockSupport.parkNanos(TimeUnit.MICROSECONDS.toNanos(10L));
         } else {
            Thread.yield();
         }
      }

      List var3 = (List)this.threadList.get();
      if (var3.size() < 50) {
         var3.add(this.weakThreadLocals ? new WeakReference(var1) : var1);
      }

   }

   public void add(T var1) {
      if (this.closed) {
         LOGGER.info("ConcurrentBag has been closed, ignoring add()");
         throw new IllegalStateException("ConcurrentBag has been closed, ignoring add()");
      } else {
         this.sharedList.add(var1);

         while(this.waiters.get() > 0 && var1.getState() == 0 && !this.handoffQueue.offer(var1)) {
            Thread.yield();
         }

      }
   }

   public boolean remove(T var1) {
      if (!var1.compareAndSet(1, -1) && !var1.compareAndSet(-2, -1) && !this.closed) {
         LOGGER.warn((String)"Attempt to remove an object from the bag that was not borrowed or reserved: {}", (Object)var1);
         return false;
      } else {
         boolean var2 = this.sharedList.remove(var1);
         if (!var2 && !this.closed) {
            LOGGER.warn((String)"Attempt to remove an object from the bag that does not exist: {}", (Object)var1);
         }

         ((List)this.threadList.get()).remove(var1);
         return var2;
      }
   }

   public void close() {
      this.closed = true;
   }

   public List<T> values(int var1) {
      List var2 = (List)this.sharedList.stream().filter((var1x) -> var1x.getState() == var1).collect(Collectors.toList());
      Collections.reverse(var2);
      return var2;
   }

   public List<T> values() {
      return (List)this.sharedList.clone();
   }

   public boolean reserve(T var1) {
      return var1.compareAndSet(0, -2);
   }

   public void unreserve(T var1) {
      if (var1.compareAndSet(-2, 0)) {
         while(this.waiters.get() > 0 && !this.handoffQueue.offer(var1)) {
            Thread.yield();
         }
      } else {
         LOGGER.warn((String)"Attempt to relinquish an object to the bag that was not reserved: {}", (Object)var1);
      }

   }

   public int getWaitingThreadCount() {
      return this.waiters.get();
   }

   public int getCount(int var1) {
      int var2 = 0;

      for(IConcurrentBagEntry var4 : this.sharedList) {
         if (var4.getState() == var1) {
            ++var2;
         }
      }

      return var2;
   }

   public int[] getStateCounts() {
      int[] var1 = new int[6];

      for(IConcurrentBagEntry var3 : this.sharedList) {
         ++var1[var3.getState()];
      }

      var1[4] = this.sharedList.size();
      var1[5] = this.waiters.get();
      return var1;
   }

   public int size() {
      return this.sharedList.size();
   }

   public void dumpState() {
      this.sharedList.forEach((var0) -> LOGGER.info(var0.toString()));
   }

   private boolean useWeakThreadLocals() {
      try {
         if (System.getProperty("com.zaxxer.hikari.useWeakReferences") != null) {
            return Boolean.getBoolean("com.zaxxer.hikari.useWeakReferences");
         } else {
            return this.getClass().getClassLoader() != ClassLoader.getSystemClassLoader();
         }
      } catch (SecurityException var2) {
         return true;
      }
   }

   public interface IBagStateListener {
      void addBagItem(int var1);
   }

   public interface IConcurrentBagEntry {
      int STATE_NOT_IN_USE = 0;
      int STATE_IN_USE = 1;
      int STATE_REMOVED = -1;
      int STATE_RESERVED = -2;

      boolean compareAndSet(int var1, int var2);

      void setState(int var1);

      int getState();
   }
}
