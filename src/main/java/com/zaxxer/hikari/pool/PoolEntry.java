package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.ConcurrentBag;
import com.zaxxer.hikari.util.FastList;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PoolEntry implements ConcurrentBag.IConcurrentBagEntry {
   private static final Logger LOGGER = LoggerFactory.getLogger(PoolEntry.class);
   private static final AtomicIntegerFieldUpdater<PoolEntry> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(PoolEntry.class, "state");
   Connection connection;
   long lastAccessed;
   long lastBorrowed;
   private volatile int state = 0;
   private volatile boolean evict;
   private volatile ScheduledFuture<?> endOfLife;
   private volatile ScheduledFuture<?> keepalive;
   private final FastList<Statement> openStatements;
   private final HikariPool hikariPool;
   private final boolean isReadOnly;
   private final boolean isAutoCommit;

   PoolEntry(Connection var1, PoolBase var2, boolean var3, boolean var4) {
      this.connection = var1;
      this.hikariPool = (HikariPool)var2;
      this.isReadOnly = var3;
      this.isAutoCommit = var4;
      this.lastAccessed = ClockSource.currentTime();
      this.openStatements = new FastList<Statement>(Statement.class, 16);
   }

   void recycle() {
      if (this.connection != null) {
         this.lastAccessed = ClockSource.currentTime();
         this.hikariPool.recycle(this);
      }

   }

   void setFutureEol(ScheduledFuture<?> var1) {
      this.endOfLife = var1;
   }

   public void setKeepalive(ScheduledFuture<?> var1) {
      this.keepalive = var1;
   }

   Connection createProxyConnection(ProxyLeakTask var1) {
      return ProxyFactory.getProxyConnection(this, this.connection, this.openStatements, var1, this.isReadOnly, this.isAutoCommit);
   }

   void resetConnectionState(ProxyConnection var1, int var2) throws SQLException {
      this.hikariPool.resetConnectionState(this.connection, var1, var2);
   }

   String getPoolName() {
      return this.hikariPool.toString();
   }

   boolean isMarkedEvicted() {
      return this.evict;
   }

   void markEvicted() {
      this.evict = true;
   }

   void evict(String var1) {
      this.hikariPool.closeConnection(this, var1);
   }

   long getMillisSinceBorrowed() {
      return ClockSource.elapsedMillis(this.lastBorrowed);
   }

   PoolBase getPoolBase() {
      return this.hikariPool;
   }

   public String toString() {
      long var1 = ClockSource.currentTime();
      Connection var10000 = this.connection;
      return var10000 + ", accessed " + ClockSource.elapsedDisplayString(this.lastAccessed, var1) + " ago, " + this.stateToString();
   }

   public int getState() {
      return stateUpdater.get(this);
   }

   public boolean compareAndSet(int var1, int var2) {
      return stateUpdater.compareAndSet(this, var1, var2);
   }

   public void setState(int var1) {
      stateUpdater.set(this, var1);
   }

   Connection close() {
      ScheduledFuture var1 = this.endOfLife;
      if (var1 != null && !var1.isDone() && !var1.cancel(false)) {
         LOGGER.warn((String)"{} - maxLifeTime expiration task cancellation unexpectedly returned false for connection {}", (Object)this.getPoolName(), (Object)this.connection);
      }

      ScheduledFuture var2 = this.keepalive;
      if (var2 != null && !var2.isDone() && !var2.cancel(false)) {
         LOGGER.warn((String)"{} - keepalive task cancellation unexpectedly returned false for connection {}", (Object)this.getPoolName(), (Object)this.connection);
      }

      Connection var3 = this.connection;
      this.connection = null;
      this.endOfLife = null;
      this.keepalive = null;
      return var3;
   }

   private String stateToString() {
      switch (this.state) {
         case -2:
            return "RESERVED";
         case -1:
            return "REMOVED";
         case 0:
            return "NOT_IN_USE";
         case 1:
            return "IN_USE";
         default:
            return "Invalid";
      }
   }
}
