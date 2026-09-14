package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectState;
import org.apache.commons.pool2.TrackedUse;

public class DefaultPooledObject<T> implements PooledObject<T> {
   private final T object;
   private PooledObjectState state;
   private final Clock systemClock;
   private final Instant createInstant;
   private volatile Instant lastBorrowInstant;
   private volatile Instant lastUseInstant;
   private volatile Instant lastReturnInstant;
   private volatile boolean logAbandoned;
   private volatile CallStack borrowedBy;
   private volatile CallStack usedBy;
   private volatile long borrowedCount;

   public DefaultPooledObject(T var1) {
      this.state = PooledObjectState.IDLE;
      this.systemClock = Clock.systemUTC();
      this.createInstant = this.now();
      this.lastBorrowInstant = this.createInstant;
      this.lastUseInstant = this.createInstant;
      this.lastReturnInstant = this.createInstant;
      this.borrowedBy = NoOpCallStack.INSTANCE;
      this.usedBy = NoOpCallStack.INSTANCE;
      this.object = var1;
   }

   public synchronized boolean allocate() {
      if (this.state == PooledObjectState.IDLE) {
         this.state = PooledObjectState.ALLOCATED;
         this.lastBorrowInstant = this.now();
         this.lastUseInstant = this.lastBorrowInstant;
         ++this.borrowedCount;
         if (this.logAbandoned) {
            this.borrowedBy.fillInStackTrace();
         }

         return true;
      } else {
         if (this.state == PooledObjectState.EVICTION) {
            this.state = PooledObjectState.EVICTION_RETURN_TO_HEAD;
         }

         return false;
      }
   }

   public int compareTo(PooledObject<T> var1) {
      int var2 = this.getLastReturnInstant().compareTo(var1.getLastReturnInstant());
      return var2 == 0 ? System.identityHashCode(this) - System.identityHashCode(var1) : var2;
   }

   public synchronized boolean deallocate() {
      if (this.state != PooledObjectState.ALLOCATED && this.state != PooledObjectState.RETURNING) {
         return false;
      } else {
         this.state = PooledObjectState.IDLE;
         this.lastReturnInstant = this.now();
         this.borrowedBy.clear();
         return true;
      }
   }

   public synchronized boolean endEvictionTest(Deque<PooledObject<T>> var1) {
      if (this.state == PooledObjectState.EVICTION) {
         this.state = PooledObjectState.IDLE;
         return true;
      } else {
         if (this.state == PooledObjectState.EVICTION_RETURN_TO_HEAD) {
            this.state = PooledObjectState.IDLE;
            var1.offerFirst(this);
         }

         return false;
      }
   }

   public long getActiveTimeMillis() {
      return this.getActiveDuration().toMillis();
   }

   public long getBorrowedCount() {
      return this.borrowedCount;
   }

   public Instant getCreateInstant() {
      return this.createInstant;
   }

   public long getCreateTime() {
      return this.createInstant.toEpochMilli();
   }

   public Duration getIdleDuration() {
      Duration var1 = Duration.between(this.lastReturnInstant, this.now());
      return var1.isNegative() ? Duration.ZERO : var1;
   }

   public Duration getIdleTime() {
      return this.getIdleDuration();
   }

   public long getIdleTimeMillis() {
      return this.getIdleDuration().toMillis();
   }

   public Instant getLastBorrowInstant() {
      return this.lastBorrowInstant;
   }

   public long getLastBorrowTime() {
      return this.lastBorrowInstant.toEpochMilli();
   }

   public Instant getLastReturnInstant() {
      return this.lastReturnInstant;
   }

   public long getLastReturnTime() {
      return this.lastReturnInstant.toEpochMilli();
   }

   public Instant getLastUsedInstant() {
      return this.object instanceof TrackedUse ? PoolImplUtils.max(((TrackedUse)this.object).getLastUsedInstant(), this.lastUseInstant) : this.lastUseInstant;
   }

   public long getLastUsedTime() {
      return this.getLastUsedInstant().toEpochMilli();
   }

   public T getObject() {
      return this.object;
   }

   public synchronized PooledObjectState getState() {
      return this.state;
   }

   public synchronized void invalidate() {
      this.state = PooledObjectState.INVALID;
   }

   public synchronized void markAbandoned() {
      this.state = PooledObjectState.ABANDONED;
   }

   public synchronized void markReturning() {
      this.state = PooledObjectState.RETURNING;
   }

   private Instant now() {
      return this.systemClock.instant();
   }

   public void printStackTrace(PrintWriter var1) {
      boolean var2 = this.borrowedBy.printStackTrace(var1);
      var2 |= this.usedBy.printStackTrace(var1);
      if (var2) {
         var1.flush();
      }

   }

   public void setLogAbandoned(boolean var1) {
      this.logAbandoned = var1;
   }

   public void setRequireFullStackTrace(boolean var1) {
      this.borrowedBy = CallStackUtils.newCallStack("'Pooled object created' yyyy-MM-dd HH:mm:ss Z 'by the following code has not been returned to the pool:'", true, var1);
      this.usedBy = CallStackUtils.newCallStack("The last code to use this object was:", false, var1);
   }

   public synchronized boolean startEvictionTest() {
      if (this.state == PooledObjectState.IDLE) {
         this.state = PooledObjectState.EVICTION;
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Object: ");
      var1.append(this.object.toString());
      var1.append(", State: ");
      synchronized(this) {
         var1.append(this.state.toString());
      }

      return var1.toString();
   }

   public void use() {
      this.lastUseInstant = this.now();
      this.usedBy.fillInStackTrace();
   }
}
