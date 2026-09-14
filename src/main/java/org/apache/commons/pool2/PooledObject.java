package org.apache.commons.pool2;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Deque;

public interface PooledObject<T> extends Comparable<PooledObject<T>> {
   static boolean isNull(PooledObject<?> var0) {
      return var0 == null || var0.getObject() == null;
   }

   boolean allocate();

   int compareTo(PooledObject<T> var1);

   boolean deallocate();

   boolean endEvictionTest(Deque<PooledObject<T>> var1);

   boolean equals(Object var1);

   default Duration getActiveDuration() {
      Instant var1 = this.getLastReturnInstant();
      Instant var2 = this.getLastBorrowInstant();
      return var1.isAfter(var2) ? Duration.between(var2, var1) : Duration.between(var2, Instant.now());
   }

   @Deprecated
   default Duration getActiveTime() {
      return this.getActiveDuration();
   }

   @Deprecated
   long getActiveTimeMillis();

   default long getBorrowedCount() {
      return -1L;
   }

   default Instant getCreateInstant() {
      return Instant.ofEpochMilli(this.getCreateTime());
   }

   @Deprecated
   long getCreateTime();

   default Duration getFullDuration() {
      return Duration.between(this.getCreateInstant(), Instant.now());
   }

   default Duration getIdleDuration() {
      return Duration.ofMillis(this.getIdleTimeMillis());
   }

   @Deprecated
   default Duration getIdleTime() {
      return Duration.ofMillis(this.getIdleTimeMillis());
   }

   @Deprecated
   long getIdleTimeMillis();

   default Instant getLastBorrowInstant() {
      return Instant.ofEpochMilli(this.getLastBorrowTime());
   }

   @Deprecated
   long getLastBorrowTime();

   default Instant getLastReturnInstant() {
      return Instant.ofEpochMilli(this.getLastReturnTime());
   }

   @Deprecated
   long getLastReturnTime();

   default Instant getLastUsedInstant() {
      return Instant.ofEpochMilli(this.getLastUsedTime());
   }

   @Deprecated
   long getLastUsedTime();

   T getObject();

   PooledObjectState getState();

   int hashCode();

   void invalidate();

   void markAbandoned();

   void markReturning();

   void printStackTrace(PrintWriter var1);

   void setLogAbandoned(boolean var1);

   default void setRequireFullStackTrace(boolean var1) {
   }

   boolean startEvictionTest();

   String toString();

   void use();
}
