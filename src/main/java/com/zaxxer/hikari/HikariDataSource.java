package com.zaxxer.hikari;

import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.pool.HikariPool;
import java.io.Closeable;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariDataSource extends HikariConfig implements DataSource, Closeable {
   private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSource.class);
   private final AtomicBoolean isShutdown = new AtomicBoolean();
   private final HikariPool fastPathPool;
   private volatile HikariPool pool;

   public HikariDataSource() {
      this.fastPathPool = null;
   }

   public HikariDataSource(HikariConfig var1) {
      var1.validate();
      var1.copyStateTo(this);
      LOGGER.info((String)"{} - Starting...", (Object)var1.getPoolName());
      this.pool = this.fastPathPool = new HikariPool(this);
      LOGGER.info((String)"{} - Start completed.", (Object)var1.getPoolName());
      this.seal();
   }

   public Connection getConnection() throws SQLException {
      if (this.isClosed()) {
         throw new SQLException("HikariDataSource " + this + " has been closed.");
      } else if (this.fastPathPool != null) {
         return this.fastPathPool.getConnection();
      } else {
         HikariPool var1 = this.pool;
         if (var1 == null) {
            synchronized(this) {
               var1 = this.pool;
               if (var1 == null) {
                  this.validate();
                  LOGGER.info((String)"{} - Starting...", (Object)this.getPoolName());

                  try {
                     this.pool = var1 = new HikariPool(this);
                     this.seal();
                  } catch (HikariPool.PoolInitializationException var5) {
                     if (var5.getCause() instanceof SQLException) {
                        throw (SQLException)var5.getCause();
                     }

                     throw var5;
                  }

                  LOGGER.info((String)"{} - Start completed.", (Object)this.getPoolName());
               }
            }
         }

         return var1.getConnection();
      }
   }

   public Connection getConnection(String var1, String var2) throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   public PrintWriter getLogWriter() throws SQLException {
      HikariPool var1 = this.pool;
      return var1 != null ? var1.getUnwrappedDataSource().getLogWriter() : null;
   }

   public void setLogWriter(PrintWriter var1) throws SQLException {
      HikariPool var2 = this.pool;
      if (var2 != null) {
         var2.getUnwrappedDataSource().setLogWriter(var1);
      }

   }

   public void setLoginTimeout(int var1) throws SQLException {
      HikariPool var2 = this.pool;
      if (var2 != null) {
         var2.getUnwrappedDataSource().setLoginTimeout(var1);
      }

   }

   public int getLoginTimeout() throws SQLException {
      HikariPool var1 = this.pool;
      return var1 != null ? var1.getUnwrappedDataSource().getLoginTimeout() : 0;
   }

   public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
      throw new SQLFeatureNotSupportedException();
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      if (var1.isInstance(this)) {
         return (T)this;
      } else {
         HikariPool var2 = this.pool;
         if (var2 != null) {
            DataSource var3 = var2.getUnwrappedDataSource();
            if (var1.isInstance(var3)) {
               return (T)var3;
            }

            if (var3 != null) {
               return (T)var3.unwrap(var1);
            }
         }

         throw new SQLException("Wrapped DataSource is not an instance of " + var1);
      }
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      if (var1.isInstance(this)) {
         return true;
      } else {
         HikariPool var2 = this.pool;
         if (var2 != null) {
            DataSource var3 = var2.getUnwrappedDataSource();
            if (var1.isInstance(var3)) {
               return true;
            }

            if (var3 != null) {
               return var3.isWrapperFor(var1);
            }
         }

         return false;
      }
   }

   public void setMetricRegistry(Object var1) {
      boolean var2 = this.getMetricRegistry() != null;
      super.setMetricRegistry(var1);
      HikariPool var3 = this.pool;
      if (var3 != null) {
         if (var2) {
            throw new IllegalStateException("MetricRegistry can only be set one time");
         }

         var3.setMetricRegistry(super.getMetricRegistry());
      }

   }

   public void setMetricsTrackerFactory(MetricsTrackerFactory var1) {
      boolean var2 = this.getMetricsTrackerFactory() != null;
      super.setMetricsTrackerFactory(var1);
      HikariPool var3 = this.pool;
      if (var3 != null) {
         if (var2) {
            throw new IllegalStateException("MetricsTrackerFactory can only be set one time");
         }

         var3.setMetricsTrackerFactory(super.getMetricsTrackerFactory());
      }

   }

   public void setHealthCheckRegistry(Object var1) {
      boolean var2 = this.getHealthCheckRegistry() != null;
      super.setHealthCheckRegistry(var1);
      HikariPool var3 = this.pool;
      if (var3 != null) {
         if (var2) {
            throw new IllegalStateException("HealthCheckRegistry can only be set one time");
         }

         var3.setHealthCheckRegistry(super.getHealthCheckRegistry());
      }

   }

   public boolean isRunning() {
      return this.pool != null && this.pool.poolState == 0;
   }

   public HikariPoolMXBean getHikariPoolMXBean() {
      return this.pool;
   }

   public HikariConfigMXBean getHikariConfigMXBean() {
      return this;
   }

   public void evictConnection(Connection var1) {
      HikariPool var2;
      if (!this.isClosed() && (var2 = this.pool) != null && var1.getClass().getName().startsWith("com.zaxxer.hikari")) {
         var2.evictConnection(var1);
      }

   }

   public void close() {
      if (!this.isShutdown.getAndSet(true)) {
         HikariPool var1 = this.pool;
         if (var1 != null) {
            try {
               LOGGER.info((String)"{} - Shutdown initiated...", (Object)this.getPoolName());
               var1.shutdown();
               LOGGER.info((String)"{} - Shutdown completed.", (Object)this.getPoolName());
            } catch (InterruptedException var3) {
               LOGGER.warn((String)"{} - Interrupted during closing", (Object)this.getPoolName(), (Object)var3);
               Thread.currentThread().interrupt();
            }
         }

      }
   }

   public boolean isClosed() {
      return this.isShutdown.get();
   }

   public String toString() {
      return "HikariDataSource (" + this.pool + ")";
   }
}
