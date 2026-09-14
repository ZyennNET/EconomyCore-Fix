package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.SQLExceptionOverride;
import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.util.ClockSource;
import com.zaxxer.hikari.util.DriverDataSource;
import com.zaxxer.hikari.util.PropertyElf;
import com.zaxxer.hikari.util.UtilityElf;
import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLTransientConnectionException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class PoolBase {
   private final Logger logger = LoggerFactory.getLogger(PoolBase.class);
   public final HikariConfig config;
   IMetricsTrackerDelegate metricsTracker;
   protected final String poolName;
   volatile String catalog;
   final AtomicReference<Exception> lastConnectionFailure;
   long connectionTimeout;
   long validationTimeout;
   SQLExceptionOverride exceptionOverride;
   private static final String[] RESET_STATES = new String[]{"readOnly", "autoCommit", "isolation", "catalog", "netTimeout", "schema"};
   private static final int UNINITIALIZED = -1;
   private static final int TRUE = 1;
   private static final int FALSE = 0;
   private static final int MINIMUM_LOGIN_TIMEOUT = Integer.getInteger("com.zaxxer.hikari.minimumLoginTimeoutSecs", 1);
   private int networkTimeout;
   private int isNetworkTimeoutSupported;
   private int isQueryTimeoutSupported;
   private int defaultTransactionIsolation;
   private int transactionIsolation;
   private Executor netTimeoutExecutor;
   private DataSource dataSource;
   private final String schema;
   private final boolean isReadOnly;
   private final boolean isAutoCommit;
   private final boolean isUseJdbc4Validation;
   private final boolean isIsolateInternalQueries;
   private volatile boolean isValidChecked;

   PoolBase(HikariConfig var1) {
      this.config = var1;
      this.networkTimeout = -1;
      this.catalog = var1.getCatalog();
      this.schema = var1.getSchema();
      this.isReadOnly = var1.isReadOnly();
      this.isAutoCommit = var1.isAutoCommit();
      this.exceptionOverride = (SQLExceptionOverride)UtilityElf.createInstance(var1.getExceptionOverrideClassName(), SQLExceptionOverride.class);
      this.transactionIsolation = UtilityElf.getTransactionIsolation(var1.getTransactionIsolation());
      this.isQueryTimeoutSupported = -1;
      this.isNetworkTimeoutSupported = -1;
      this.isUseJdbc4Validation = var1.getConnectionTestQuery() == null;
      this.isIsolateInternalQueries = var1.isIsolateInternalQueries();
      this.poolName = var1.getPoolName();
      this.connectionTimeout = var1.getConnectionTimeout();
      this.validationTimeout = var1.getValidationTimeout();
      this.lastConnectionFailure = new AtomicReference();
      this.initializeDataSource();
   }

   public String toString() {
      return this.poolName;
   }

   abstract void recycle(PoolEntry var1);

   void quietlyCloseConnection(Connection var1, String var2) {
      if (var1 != null) {
         try {
            this.logger.debug("{} - Closing connection {}: {}", this.poolName, var1, var2);

            try {
               Connection var3 = var1;

               try {
                  Connection var4 = var1;

                  try {
                     if (!var1.isClosed()) {
                        this.setNetworkTimeout(var1, TimeUnit.SECONDS.toMillis(15L));
                     }
                  } catch (Throwable var9) {
                     if (var1 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var8) {
                           var9.addSuppressed(var8);
                        }
                     }

                     throw var9;
                  }

                  if (var1 != null) {
                     var1.close();
                  }
               } catch (Throwable var10) {
                  if (var1 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                     }
                  }

                  throw var10;
               }

               if (var1 != null) {
                  var1.close();
               }
            } catch (SQLException var11) {
            }
         } catch (Exception var12) {
            this.logger.debug("{} - Closing connection {} failed", this.poolName, var1, var12);
         }
      }

   }

   boolean isConnectionDead(Connection var1) {
      try {
         this.setNetworkTimeout(var1, this.validationTimeout);

         boolean var3;
         try {
            int var2 = (int)Math.max(1000L, this.validationTimeout) / 1000;
            if (!this.isUseJdbc4Validation) {
               Statement var15 = var1.createStatement();

               try {
                  if (this.isNetworkTimeoutSupported != 1) {
                     this.setQueryTimeout(var15, var2);
                  }

                  var15.execute(this.config.getConnectionTestQuery());
               } catch (Throwable var12) {
                  if (var15 != null) {
                     try {
                        var15.close();
                     } catch (Throwable var11) {
                        var12.addSuppressed(var11);
                     }
                  }

                  throw var12;
               }

               if (var15 != null) {
                  var15.close();
               }

               return false;
            }

            var3 = !var1.isValid(var2);
         } finally {
            this.setNetworkTimeout(var1, (long)this.networkTimeout);
            if (this.isIsolateInternalQueries && !this.isAutoCommit) {
               var1.rollback();
            }

         }

         return var3;
      } catch (Exception var14) {
         this.lastConnectionFailure.set(var14);
         this.logger.warn("{} - Failed to validate connection {} ({}). Possibly consider using a shorter maxLifetime value.", this.poolName, var1, var14.getMessage());
         return true;
      }
   }

   Exception getLastConnectionFailure() {
      return (Exception)this.lastConnectionFailure.get();
   }

   public DataSource getUnwrappedDataSource() {
      return this.dataSource;
   }

   PoolEntry newPoolEntry() throws Exception {
      return new PoolEntry(this.newConnection(), this, this.isReadOnly, this.isAutoCommit);
   }

   void resetConnectionState(Connection var1, ProxyConnection var2, int var3) throws SQLException {
      int var4 = 0;
      if ((var3 & 1) != 0 && var2.getReadOnlyState() != this.isReadOnly) {
         var1.setReadOnly(this.isReadOnly);
         var4 |= 1;
      }

      if ((var3 & 2) != 0 && var2.getAutoCommitState() != this.isAutoCommit) {
         var1.setAutoCommit(this.isAutoCommit);
         var4 |= 2;
      }

      if ((var3 & 4) != 0 && var2.getTransactionIsolationState() != this.transactionIsolation) {
         var1.setTransactionIsolation(this.transactionIsolation);
         var4 |= 4;
      }

      if ((var3 & 8) != 0 && this.catalog != null && !this.catalog.equals(var2.getCatalogState())) {
         var1.setCatalog(this.catalog);
         var4 |= 8;
      }

      if ((var3 & 16) != 0 && var2.getNetworkTimeoutState() != this.networkTimeout) {
         this.setNetworkTimeout(var1, (long)this.networkTimeout);
         var4 |= 16;
      }

      if ((var3 & 32) != 0 && this.schema != null && !this.schema.equals(var2.getSchemaState())) {
         var1.setSchema(this.schema);
         var4 |= 32;
      }

      if (var4 != 0 && this.logger.isDebugEnabled()) {
         this.logger.debug("{} - Reset ({}) on connection {}", this.poolName, this.stringFromResetBits(var4), var1);
      }

   }

   void shutdownNetworkTimeoutExecutor() {
      if (this.netTimeoutExecutor instanceof ThreadPoolExecutor) {
         ((ThreadPoolExecutor)this.netTimeoutExecutor).shutdownNow();
      }

   }

   long getLoginTimeout() {
      try {
         return this.dataSource != null ? (long)this.dataSource.getLoginTimeout() : TimeUnit.SECONDS.toSeconds(5L);
      } catch (SQLException var2) {
         return TimeUnit.SECONDS.toSeconds(5L);
      }
   }

   void handleMBeans(HikariPool var1, boolean var2) {
      if (this.config.isRegisterMbeans()) {
         try {
            MBeanServer var3 = ManagementFactory.getPlatformMBeanServer();
            ObjectName var4;
            ObjectName var5;
            if ("true".equals(System.getProperty("hikaricp.jmx.register2.0"))) {
               var4 = new ObjectName("com.zaxxer.hikari:type=PoolConfig,name=" + this.poolName);
               var5 = new ObjectName("com.zaxxer.hikari:type=Pool,name=" + this.poolName);
            } else {
               var4 = new ObjectName("com.zaxxer.hikari:type=PoolConfig (" + this.poolName + ")");
               var5 = new ObjectName("com.zaxxer.hikari:type=Pool (" + this.poolName + ")");
            }

            if (var2) {
               if (!var3.isRegistered(var4)) {
                  var3.registerMBean(this.config, var4);
                  var3.registerMBean(var1, var5);
               } else {
                  this.logger.error((String)"{} - JMX name ({}) is already registered.", (Object)this.poolName, (Object)this.poolName);
               }
            } else if (var3.isRegistered(var4)) {
               var3.unregisterMBean(var4);
               var3.unregisterMBean(var5);
            }
         } catch (Exception var6) {
            this.logger.warn("{} - Failed to {} management beans.", this.poolName, var2 ? "register" : "unregister", var6);
         }

      }
   }

   private void initializeDataSource() {
      String var1 = this.config.getJdbcUrl();
      String var2 = this.config.getUsername();
      String var3 = this.config.getPassword();
      String var4 = this.config.getDataSourceClassName();
      String var5 = this.config.getDriverClassName();
      String var6 = this.config.getDataSourceJNDI();
      Properties var7 = this.config.getDataSourceProperties();
      Object var8 = this.config.getDataSource();
      if (var4 != null && var8 == null) {
         var8 = (DataSource)UtilityElf.createInstance(var4, DataSource.class);
         PropertyElf.setTargetFromProperties(var8, var7);
      } else if (var1 != null && var8 == null) {
         var8 = new DriverDataSource(var1, var5, var7, var2, var3);
      } else if (var6 != null && var8 == null) {
         try {
            InitialContext var9 = new InitialContext();
            var8 = (DataSource)var9.lookup(var6);
         } catch (NamingException var10) {
            throw new HikariPool.PoolInitializationException(var10);
         }
      }

      if (var8 != null) {
         this.setLoginTimeout((DataSource)var8);
         this.createNetworkTimeoutExecutor((DataSource)var8, var4, var1);
      }

      this.dataSource = (DataSource)var8;
   }

   private Connection newConnection() throws Exception {
      long var1 = ClockSource.currentTime();
      Connection var3 = null;

      Connection var6;
      try {
         String var4 = this.config.getUsername();
         String var5 = this.config.getPassword();
         var3 = var4 == null ? this.dataSource.getConnection() : this.dataSource.getConnection(var4, var5);
         if (var3 == null) {
            throw new SQLTransientConnectionException("DataSource returned null unexpectedly");
         }

         this.setupConnection(var3);
         this.lastConnectionFailure.set((Object)null);
         var6 = var3;
      } catch (Exception var10) {
         if (var3 != null) {
            this.quietlyCloseConnection(var3, "(Failed to create/setup connection)");
         } else if (this.getLastConnectionFailure() == null) {
            this.logger.debug((String)"{} - Failed to create/setup connection: {}", (Object)this.poolName, (Object)var10.getMessage());
         }

         this.lastConnectionFailure.set(var10);
         throw var10;
      } finally {
         if (this.metricsTracker != null) {
            this.metricsTracker.recordConnectionCreated(ClockSource.elapsedMillis(var1));
         }

      }

      return var6;
   }

   private void setupConnection(Connection var1) throws ConnectionSetupException {
      try {
         if (this.networkTimeout == -1) {
            this.networkTimeout = this.getAndSetNetworkTimeout(var1, this.validationTimeout);
         } else {
            this.setNetworkTimeout(var1, this.validationTimeout);
         }

         if (var1.isReadOnly() != this.isReadOnly) {
            var1.setReadOnly(this.isReadOnly);
         }

         if (var1.getAutoCommit() != this.isAutoCommit) {
            var1.setAutoCommit(this.isAutoCommit);
         }

         this.checkDriverSupport(var1);
         if (this.transactionIsolation != this.defaultTransactionIsolation) {
            var1.setTransactionIsolation(this.transactionIsolation);
         }

         if (this.catalog != null) {
            var1.setCatalog(this.catalog);
         }

         if (this.schema != null) {
            var1.setSchema(this.schema);
         }

         this.executeSql(var1, this.config.getConnectionInitSql(), true);
         this.setNetworkTimeout(var1, (long)this.networkTimeout);
      } catch (SQLException var3) {
         throw new ConnectionSetupException(var3);
      }
   }

   private void checkDriverSupport(Connection var1) throws SQLException {
      if (!this.isValidChecked) {
         this.checkValidationSupport(var1);
         this.checkDefaultIsolation(var1);
         this.isValidChecked = true;
      }

   }

   private void checkValidationSupport(Connection var1) throws SQLException {
      try {
         if (this.isUseJdbc4Validation) {
            var1.isValid(1);
         } else {
            this.executeSql(var1, this.config.getConnectionTestQuery(), false);
         }

      } catch (AbstractMethodError | Exception var3) {
         this.logger.error("{} - Failed to execute{} connection test query ({}).", this.poolName, this.isUseJdbc4Validation ? " isValid() for connection, configure" : "", ((Throwable)var3).getMessage());
         throw var3;
      }
   }

   private void checkDefaultIsolation(Connection var1) throws SQLException {
      try {
         this.defaultTransactionIsolation = var1.getTransactionIsolation();
         if (this.transactionIsolation == -1) {
            this.transactionIsolation = this.defaultTransactionIsolation;
         }
      } catch (SQLException var3) {
         this.logger.warn((String)"{} - Default transaction isolation level detection failed ({}).", (Object)this.poolName, (Object)var3.getMessage());
         if (var3.getSQLState() != null && !var3.getSQLState().startsWith("08")) {
            throw var3;
         }
      }

   }

   private void setQueryTimeout(Statement var1, int var2) {
      if (this.isQueryTimeoutSupported != 0) {
         try {
            var1.setQueryTimeout(var2);
            this.isQueryTimeoutSupported = 1;
         } catch (Exception var4) {
            if (this.isQueryTimeoutSupported == -1) {
               this.isQueryTimeoutSupported = 0;
               this.logger.info((String)"{} - Failed to set query timeout for statement. ({})", (Object)this.poolName, (Object)var4.getMessage());
            }
         }
      }

   }

   private int getAndSetNetworkTimeout(Connection var1, long var2) {
      if (this.isNetworkTimeoutSupported != 0) {
         try {
            int var4 = var1.getNetworkTimeout();
            var1.setNetworkTimeout(this.netTimeoutExecutor, (int)var2);
            this.isNetworkTimeoutSupported = 1;
            return var4;
         } catch (AbstractMethodError | Exception var5) {
            if (this.isNetworkTimeoutSupported == -1) {
               this.isNetworkTimeoutSupported = 0;
               this.logger.info((String)"{} - Driver does not support get/set network timeout for connections. ({})", (Object)this.poolName, (Object)((Throwable)var5).getMessage());
               if (this.validationTimeout < TimeUnit.SECONDS.toMillis(1L)) {
                  this.logger.warn((String)"{} - A validationTimeout of less than 1 second cannot be honored on drivers without setNetworkTimeout() support.", (Object)this.poolName);
               } else if (this.validationTimeout % TimeUnit.SECONDS.toMillis(1L) != 0L) {
                  this.logger.warn((String)"{} - A validationTimeout with fractional second granularity cannot be honored on drivers without setNetworkTimeout() support.", (Object)this.poolName);
               }
            }
         }
      }

      return 0;
   }

   private void setNetworkTimeout(Connection var1, long var2) throws SQLException {
      if (this.isNetworkTimeoutSupported == 1) {
         var1.setNetworkTimeout(this.netTimeoutExecutor, (int)var2);
      }

   }

   private void executeSql(Connection var1, String var2, boolean var3) throws SQLException {
      if (var2 != null) {
         Statement var4 = var1.createStatement();

         try {
            var4.execute(var2);
         } catch (Throwable var8) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (var4 != null) {
            var4.close();
         }

         if (this.isIsolateInternalQueries && !this.isAutoCommit) {
            if (var3) {
               var1.commit();
            } else {
               var1.rollback();
            }
         }
      }

   }

   private void createNetworkTimeoutExecutor(DataSource var1, String var2, String var3) {
      if ((var2 == null || !var2.contains("Mysql")) && (var3 == null || !var3.contains("mysql")) && (var1 == null || !var1.getClass().getName().contains("Mysql"))) {
         ThreadFactory var4 = this.config.getThreadFactory();
         Object var6 = var4 != null ? var4 : new UtilityElf.DefaultThreadFactory(this.poolName + " network timeout executor");
         ThreadPoolExecutor var5 = (ThreadPoolExecutor)Executors.newCachedThreadPool((ThreadFactory)var6);
         var5.setKeepAliveTime(15L, TimeUnit.SECONDS);
         var5.allowCoreThreadTimeOut(true);
         this.netTimeoutExecutor = var5;
      } else {
         this.netTimeoutExecutor = new SynchronousExecutor();
      }

   }

   private void setLoginTimeout(DataSource var1) {
      if (this.connectionTimeout != 2147483647L) {
         try {
            var1.setLoginTimeout(Math.max(MINIMUM_LOGIN_TIMEOUT, (int)TimeUnit.MILLISECONDS.toSeconds(500L + this.connectionTimeout)));
         } catch (Exception var3) {
            this.logger.info((String)"{} - Failed to set login timeout for data source. ({})", (Object)this.poolName, (Object)var3.getMessage());
         }
      }

   }

   private String stringFromResetBits(int var1) {
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < RESET_STATES.length; ++var3) {
         if ((var1 & 1 << var3) != 0) {
            var2.append(RESET_STATES[var3]).append(", ");
         }
      }

      var2.setLength(var2.length() - 2);
      return var2.toString();
   }

   static class ConnectionSetupException extends Exception {
      private static final long serialVersionUID = 929872118275916521L;

      ConnectionSetupException(Throwable var1) {
         super(var1);
      }
   }

   interface IMetricsTrackerDelegate extends AutoCloseable {
      default void recordConnectionUsage(PoolEntry var1) {
      }

      default void recordConnectionCreated(long var1) {
      }

      default void recordBorrowTimeoutStats(long var1) {
      }

      default void recordBorrowStats(PoolEntry var1, long var2) {
      }

      default void recordConnectionTimeout() {
      }

      default void close() {
      }
   }

   static class MetricsTrackerDelegate implements IMetricsTrackerDelegate {
      final IMetricsTracker tracker;

      MetricsTrackerDelegate(IMetricsTracker var1) {
         this.tracker = var1;
      }

      public void recordConnectionUsage(PoolEntry var1) {
         this.tracker.recordConnectionUsageMillis(var1.getMillisSinceBorrowed());
      }

      public void recordConnectionCreated(long var1) {
         this.tracker.recordConnectionCreatedMillis(var1);
      }

      public void recordBorrowTimeoutStats(long var1) {
         this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(var1));
      }

      public void recordBorrowStats(PoolEntry var1, long var2) {
         long var4 = ClockSource.currentTime();
         var1.lastBorrowed = var4;
         this.tracker.recordConnectionAcquiredNanos(ClockSource.elapsedNanos(var2, var4));
      }

      public void recordConnectionTimeout() {
         this.tracker.recordConnectionTimeout();
      }

      public void close() {
         this.tracker.close();
      }
   }

   static final class NopMetricsTrackerDelegate implements IMetricsTrackerDelegate {
   }

   private static class SynchronousExecutor implements Executor {
      public void execute(Runnable var1) {
         try {
            var1.run();
         } catch (Exception var3) {
            LoggerFactory.getLogger(PoolBase.class).debug((String)"Failed to execute: {}", (Object)var1, (Object)var3);
         }

      }
   }
}
