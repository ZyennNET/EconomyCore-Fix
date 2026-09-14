package com.zaxxer.hikari;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.util.PropertyElf;
import com.zaxxer.hikari.util.UtilityElf;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HikariConfig implements HikariConfigMXBean {
   private static final Logger LOGGER = LoggerFactory.getLogger(HikariConfig.class);
   private static final char[] ID_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
   private static final long CONNECTION_TIMEOUT;
   private static final long VALIDATION_TIMEOUT;
   private static final long SOFT_TIMEOUT_FLOOR;
   private static final long IDLE_TIMEOUT;
   private static final long MAX_LIFETIME;
   private static final long DEFAULT_KEEPALIVE_TIME = 0L;
   private static final int DEFAULT_POOL_SIZE = 10;
   private static boolean unitTest;
   private volatile String catalog;
   private volatile long connectionTimeout;
   private volatile long validationTimeout;
   private volatile long idleTimeout;
   private volatile long leakDetectionThreshold;
   private volatile long maxLifetime;
   private volatile int maxPoolSize;
   private volatile int minIdle;
   private volatile String username;
   private volatile String password;
   private long initializationFailTimeout;
   private String connectionInitSql;
   private String connectionTestQuery;
   private String dataSourceClassName;
   private String dataSourceJndiName;
   private String driverClassName;
   private String exceptionOverrideClassName;
   private String jdbcUrl;
   private String poolName;
   private String schema;
   private String transactionIsolationName;
   private boolean isAutoCommit;
   private boolean isReadOnly;
   private boolean isIsolateInternalQueries;
   private boolean isRegisterMbeans;
   private boolean isAllowPoolSuspension;
   private DataSource dataSource;
   private Properties dataSourceProperties;
   private ThreadFactory threadFactory;
   private ScheduledExecutorService scheduledExecutor;
   private MetricsTrackerFactory metricsTrackerFactory;
   private Object metricRegistry;
   private Object healthCheckRegistry;
   private Properties healthCheckProperties;
   private long keepaliveTime;
   private volatile boolean sealed;

   public HikariConfig() {
      this.dataSourceProperties = new Properties();
      this.healthCheckProperties = new Properties();
      this.minIdle = -1;
      this.maxPoolSize = -1;
      this.maxLifetime = MAX_LIFETIME;
      this.connectionTimeout = CONNECTION_TIMEOUT;
      this.validationTimeout = VALIDATION_TIMEOUT;
      this.idleTimeout = IDLE_TIMEOUT;
      this.initializationFailTimeout = 1L;
      this.isAutoCommit = true;
      this.keepaliveTime = 0L;
      String var1 = System.getProperty("hikaricp.configurationFile");
      if (var1 != null) {
         this.loadProperties(var1);
      }

   }

   public HikariConfig(Properties var1) {
      this();
      PropertyElf.setTargetFromProperties(this, var1);
   }

   public HikariConfig(String var1) {
      this();
      this.loadProperties(var1);
   }

   public String getCatalog() {
      return this.catalog;
   }

   public void setCatalog(String var1) {
      this.catalog = var1;
   }

   public long getConnectionTimeout() {
      return this.connectionTimeout;
   }

   public void setConnectionTimeout(long var1) {
      if (var1 == 0L) {
         this.connectionTimeout = 2147483647L;
      } else {
         if (var1 < SOFT_TIMEOUT_FLOOR) {
            throw new IllegalArgumentException("connectionTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
         }

         this.connectionTimeout = var1;
      }

   }

   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   public void setIdleTimeout(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("idleTimeout cannot be negative");
      } else {
         this.idleTimeout = var1;
      }
   }

   public long getLeakDetectionThreshold() {
      return this.leakDetectionThreshold;
   }

   public void setLeakDetectionThreshold(long var1) {
      this.leakDetectionThreshold = var1;
   }

   public long getMaxLifetime() {
      return this.maxLifetime;
   }

   public void setMaxLifetime(long var1) {
      this.maxLifetime = var1;
   }

   public int getMaximumPoolSize() {
      return this.maxPoolSize;
   }

   public void setMaximumPoolSize(int var1) {
      if (var1 < 1) {
         throw new IllegalArgumentException("maxPoolSize cannot be less than 1");
      } else {
         this.maxPoolSize = var1;
      }
   }

   public int getMinimumIdle() {
      return this.minIdle;
   }

   public void setMinimumIdle(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("minimumIdle cannot be negative");
      } else {
         this.minIdle = var1;
      }
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String var1) {
      this.password = var1;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String var1) {
      this.username = var1;
   }

   public long getValidationTimeout() {
      return this.validationTimeout;
   }

   public void setValidationTimeout(long var1) {
      if (var1 < SOFT_TIMEOUT_FLOOR) {
         throw new IllegalArgumentException("validationTimeout cannot be less than " + SOFT_TIMEOUT_FLOOR + "ms");
      } else {
         this.validationTimeout = var1;
      }
   }

   public String getConnectionTestQuery() {
      return this.connectionTestQuery;
   }

   public void setConnectionTestQuery(String var1) {
      this.checkIfSealed();
      this.connectionTestQuery = var1;
   }

   public String getConnectionInitSql() {
      return this.connectionInitSql;
   }

   public void setConnectionInitSql(String var1) {
      this.checkIfSealed();
      this.connectionInitSql = var1;
   }

   public DataSource getDataSource() {
      return this.dataSource;
   }

   public void setDataSource(DataSource var1) {
      this.checkIfSealed();
      this.dataSource = var1;
   }

   public String getDataSourceClassName() {
      return this.dataSourceClassName;
   }

   public void setDataSourceClassName(String var1) {
      this.checkIfSealed();
      this.dataSourceClassName = var1;
   }

   public void addDataSourceProperty(String var1, Object var2) {
      this.checkIfSealed();
      this.dataSourceProperties.put(var1, var2);
   }

   public String getDataSourceJNDI() {
      return this.dataSourceJndiName;
   }

   public void setDataSourceJNDI(String var1) {
      this.checkIfSealed();
      this.dataSourceJndiName = var1;
   }

   public Properties getDataSourceProperties() {
      return this.dataSourceProperties;
   }

   public void setDataSourceProperties(Properties var1) {
      this.checkIfSealed();
      this.dataSourceProperties.putAll(var1);
   }

   public String getDriverClassName() {
      return this.driverClassName;
   }

   public void setDriverClassName(String var1) {
      this.checkIfSealed();
      Class var2 = this.attemptFromContextLoader(var1);

      try {
         if (var2 == null) {
            var2 = this.getClass().getClassLoader().loadClass(var1);
            LOGGER.debug((String)"Driver class {} found in the HikariConfig class classloader {}", (Object)var1, (Object)this.getClass().getClassLoader());
         }
      } catch (ClassNotFoundException var5) {
         LOGGER.error((String)"Failed to load driver class {} from HikariConfig class classloader {}", (Object)var1, (Object)this.getClass().getClassLoader());
      }

      if (var2 == null) {
         throw new RuntimeException("Failed to load driver class " + var1 + " in either of HikariConfig class loader or Thread context classloader");
      } else {
         try {
            var2.getConstructor().newInstance();
            this.driverClassName = var1;
         } catch (Exception var4) {
            throw new RuntimeException("Failed to instantiate class " + var1, var4);
         }
      }
   }

   public String getJdbcUrl() {
      return this.jdbcUrl;
   }

   public void setJdbcUrl(String var1) {
      this.checkIfSealed();
      this.jdbcUrl = var1;
   }

   public boolean isAutoCommit() {
      return this.isAutoCommit;
   }

   public void setAutoCommit(boolean var1) {
      this.checkIfSealed();
      this.isAutoCommit = var1;
   }

   public boolean isAllowPoolSuspension() {
      return this.isAllowPoolSuspension;
   }

   public void setAllowPoolSuspension(boolean var1) {
      this.checkIfSealed();
      this.isAllowPoolSuspension = var1;
   }

   public long getInitializationFailTimeout() {
      return this.initializationFailTimeout;
   }

   public void setInitializationFailTimeout(long var1) {
      this.checkIfSealed();
      this.initializationFailTimeout = var1;
   }

   public boolean isIsolateInternalQueries() {
      return this.isIsolateInternalQueries;
   }

   public void setIsolateInternalQueries(boolean var1) {
      this.checkIfSealed();
      this.isIsolateInternalQueries = var1;
   }

   public MetricsTrackerFactory getMetricsTrackerFactory() {
      return this.metricsTrackerFactory;
   }

   public void setMetricsTrackerFactory(MetricsTrackerFactory var1) {
      if (this.metricRegistry != null) {
         throw new IllegalStateException("cannot use setMetricsTrackerFactory() and setMetricRegistry() together");
      } else {
         this.metricsTrackerFactory = var1;
      }
   }

   public Object getMetricRegistry() {
      return this.metricRegistry;
   }

   public void setMetricRegistry(Object var1) {
      if (this.metricsTrackerFactory != null) {
         throw new IllegalStateException("cannot use setMetricRegistry() and setMetricsTrackerFactory() together");
      } else {
         if (var1 != null) {
            var1 = this.getObjectOrPerformJndiLookup(var1);
            if (!UtilityElf.safeIsAssignableFrom(var1, "com.codahale.metrics.MetricRegistry") && !UtilityElf.safeIsAssignableFrom(var1, "io.micrometer.core.instrument.MeterRegistry")) {
               throw new IllegalArgumentException("Class must be instance of com.codahale.metrics.MetricRegistry or io.micrometer.core.instrument.MeterRegistry");
            }
         }

         this.metricRegistry = var1;
      }
   }

   public Object getHealthCheckRegistry() {
      return this.healthCheckRegistry;
   }

   public void setHealthCheckRegistry(Object var1) {
      this.checkIfSealed();
      if (var1 != null) {
         var1 = this.getObjectOrPerformJndiLookup(var1);
         if (!(var1 instanceof HealthCheckRegistry)) {
            throw new IllegalArgumentException("Class must be an instance of com.codahale.metrics.health.HealthCheckRegistry");
         }
      }

      this.healthCheckRegistry = var1;
   }

   public Properties getHealthCheckProperties() {
      return this.healthCheckProperties;
   }

   public void setHealthCheckProperties(Properties var1) {
      this.checkIfSealed();
      this.healthCheckProperties.putAll(var1);
   }

   public void addHealthCheckProperty(String var1, String var2) {
      this.checkIfSealed();
      this.healthCheckProperties.setProperty(var1, var2);
   }

   public long getKeepaliveTime() {
      return this.keepaliveTime;
   }

   public void setKeepaliveTime(long var1) {
      this.keepaliveTime = var1;
   }

   public boolean isReadOnly() {
      return this.isReadOnly;
   }

   public void setReadOnly(boolean var1) {
      this.checkIfSealed();
      this.isReadOnly = var1;
   }

   public boolean isRegisterMbeans() {
      return this.isRegisterMbeans;
   }

   public void setRegisterMbeans(boolean var1) {
      this.checkIfSealed();
      this.isRegisterMbeans = var1;
   }

   public String getPoolName() {
      return this.poolName;
   }

   public void setPoolName(String var1) {
      this.checkIfSealed();
      this.poolName = var1;
   }

   public ScheduledExecutorService getScheduledExecutor() {
      return this.scheduledExecutor;
   }

   public void setScheduledExecutor(ScheduledExecutorService var1) {
      this.checkIfSealed();
      this.scheduledExecutor = var1;
   }

   public String getTransactionIsolation() {
      return this.transactionIsolationName;
   }

   public String getSchema() {
      return this.schema;
   }

   public void setSchema(String var1) {
      this.checkIfSealed();
      this.schema = var1;
   }

   public String getExceptionOverrideClassName() {
      return this.exceptionOverrideClassName;
   }

   public void setExceptionOverrideClassName(String var1) {
      this.checkIfSealed();
      Class var2 = this.attemptFromContextLoader(var1);

      try {
         if (var2 == null) {
            var2 = this.getClass().getClassLoader().loadClass(var1);
            LOGGER.debug((String)"SQLExceptionOverride class {} found in the HikariConfig class classloader {}", (Object)var1, (Object)this.getClass().getClassLoader());
         }
      } catch (ClassNotFoundException var5) {
         LOGGER.error((String)"Failed to load SQLExceptionOverride class {} from HikariConfig class classloader {}", (Object)var1, (Object)this.getClass().getClassLoader());
      }

      if (var2 == null) {
         throw new RuntimeException("Failed to load SQLExceptionOverride class " + var1 + " in either of HikariConfig class loader or Thread context classloader");
      } else {
         try {
            var2.getConstructor().newInstance();
            this.exceptionOverrideClassName = var1;
         } catch (Exception var4) {
            throw new RuntimeException("Failed to instantiate class " + var1, var4);
         }
      }
   }

   public void setTransactionIsolation(String var1) {
      this.checkIfSealed();
      this.transactionIsolationName = var1;
   }

   public ThreadFactory getThreadFactory() {
      return this.threadFactory;
   }

   public void setThreadFactory(ThreadFactory var1) {
      this.checkIfSealed();
      this.threadFactory = var1;
   }

   void seal() {
      this.sealed = true;
   }

   public void copyStateTo(HikariConfig var1) {
      for(Field var5 : HikariConfig.class.getDeclaredFields()) {
         if (!Modifier.isFinal(var5.getModifiers())) {
            var5.setAccessible(true);

            try {
               var5.set(var1, var5.get(this));
            } catch (Exception var7) {
               throw new RuntimeException("Failed to copy HikariConfig state: " + var7.getMessage(), var7);
            }
         }
      }

      var1.sealed = false;
   }

   private Class<?> attemptFromContextLoader(String var1) {
      ClassLoader var2 = Thread.currentThread().getContextClassLoader();
      if (var2 != null) {
         try {
            Class var3 = var2.loadClass(var1);
            LOGGER.debug((String)"Driver class {} found in Thread context class loader {}", (Object)var1, (Object)var2);
            return var3;
         } catch (ClassNotFoundException var4) {
            LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", var1, var2, this.getClass().getClassLoader());
         }
      }

      return null;
   }

   public void validate() {
      if (this.poolName == null) {
         this.poolName = this.generatePoolName();
      } else if (this.isRegisterMbeans && this.poolName.contains(":")) {
         throw new IllegalArgumentException("poolName cannot contain ':' when used with JMX");
      }

      this.catalog = UtilityElf.getNullIfEmpty(this.catalog);
      this.connectionInitSql = UtilityElf.getNullIfEmpty(this.connectionInitSql);
      this.connectionTestQuery = UtilityElf.getNullIfEmpty(this.connectionTestQuery);
      this.transactionIsolationName = UtilityElf.getNullIfEmpty(this.transactionIsolationName);
      this.dataSourceClassName = UtilityElf.getNullIfEmpty(this.dataSourceClassName);
      this.dataSourceJndiName = UtilityElf.getNullIfEmpty(this.dataSourceJndiName);
      this.driverClassName = UtilityElf.getNullIfEmpty(this.driverClassName);
      this.jdbcUrl = UtilityElf.getNullIfEmpty(this.jdbcUrl);
      if (this.dataSource != null) {
         if (this.dataSourceClassName != null) {
            LOGGER.warn((String)"{} - using dataSource and ignoring dataSourceClassName.", (Object)this.poolName);
         }
      } else if (this.dataSourceClassName != null) {
         if (this.driverClassName != null) {
            LOGGER.error((String)"{} - cannot use driverClassName and dataSourceClassName together.", (Object)this.poolName);
            throw new IllegalStateException("cannot use driverClassName and dataSourceClassName together.");
         }

         if (this.jdbcUrl != null) {
            LOGGER.warn((String)"{} - using dataSourceClassName and ignoring jdbcUrl.", (Object)this.poolName);
         }
      } else if (this.jdbcUrl == null && this.dataSourceJndiName == null) {
         if (this.driverClassName != null) {
            LOGGER.error((String)"{} - jdbcUrl is required with driverClassName.", (Object)this.poolName);
            throw new IllegalArgumentException("jdbcUrl is required with driverClassName.");
         }

         LOGGER.error((String)"{} - dataSource or dataSourceClassName or jdbcUrl is required.", (Object)this.poolName);
         throw new IllegalArgumentException("dataSource or dataSourceClassName or jdbcUrl is required.");
      }

      this.validateNumerics();
      if (LOGGER.isDebugEnabled() || unitTest) {
         this.logConfiguration();
      }

   }

   private void validateNumerics() {
      if (this.maxLifetime != 0L && this.maxLifetime < TimeUnit.SECONDS.toMillis(30L)) {
         LOGGER.warn((String)"{} - maxLifetime is less than 30000ms, setting to default {}ms.", (Object)this.poolName, (Object)MAX_LIFETIME);
         this.maxLifetime = MAX_LIFETIME;
      }

      if (this.keepaliveTime != 0L && this.keepaliveTime < TimeUnit.SECONDS.toMillis(30L)) {
         LOGGER.warn((String)"{} - keepaliveTime is less than 30000ms, disabling it.", (Object)this.poolName);
         this.keepaliveTime = 0L;
      }

      if (this.keepaliveTime != 0L && this.maxLifetime != 0L && this.keepaliveTime >= this.maxLifetime) {
         LOGGER.warn((String)"{} - keepaliveTime is greater than or equal to maxLifetime, disabling it.", (Object)this.poolName);
         this.keepaliveTime = 0L;
      }

      if (this.leakDetectionThreshold > 0L && !unitTest && (this.leakDetectionThreshold < TimeUnit.SECONDS.toMillis(2L) || this.leakDetectionThreshold > this.maxLifetime && this.maxLifetime > 0L)) {
         LOGGER.warn((String)"{} - leakDetectionThreshold is less than 2000ms or more than maxLifetime, disabling it.", (Object)this.poolName);
         this.leakDetectionThreshold = 0L;
      }

      if (this.connectionTimeout < SOFT_TIMEOUT_FLOOR) {
         LOGGER.warn("{} - connectionTimeout is less than {}ms, setting to {}ms.", this.poolName, SOFT_TIMEOUT_FLOOR, CONNECTION_TIMEOUT);
         this.connectionTimeout = CONNECTION_TIMEOUT;
      }

      if (this.validationTimeout < SOFT_TIMEOUT_FLOOR) {
         LOGGER.warn("{} - validationTimeout is less than {}ms, setting to {}ms.", this.poolName, SOFT_TIMEOUT_FLOOR, VALIDATION_TIMEOUT);
         this.validationTimeout = VALIDATION_TIMEOUT;
      }

      if (this.maxPoolSize < 1) {
         this.maxPoolSize = 10;
      }

      if (this.minIdle < 0 || this.minIdle > this.maxPoolSize) {
         this.minIdle = this.maxPoolSize;
      }

      if (this.idleTimeout + TimeUnit.SECONDS.toMillis(1L) > this.maxLifetime && this.maxLifetime > 0L && this.minIdle < this.maxPoolSize) {
         LOGGER.warn((String)"{} - idleTimeout is close to or more than maxLifetime, disabling it.", (Object)this.poolName);
         this.idleTimeout = 0L;
      } else if (this.idleTimeout != 0L && this.idleTimeout < TimeUnit.SECONDS.toMillis(10L) && this.minIdle < this.maxPoolSize) {
         LOGGER.warn((String)"{} - idleTimeout is less than 10000ms, setting to default {}ms.", (Object)this.poolName, (Object)IDLE_TIMEOUT);
         this.idleTimeout = IDLE_TIMEOUT;
      } else if (this.idleTimeout != IDLE_TIMEOUT && this.idleTimeout != 0L && this.minIdle == this.maxPoolSize) {
         LOGGER.warn((String)"{} - idleTimeout has been set but has no effect because the pool is operating as a fixed size pool.", (Object)this.poolName);
      }

   }

   private void checkIfSealed() {
      if (this.sealed) {
         throw new IllegalStateException("The configuration of the pool is sealed once started. Use HikariConfigMXBean for runtime changes.");
      }
   }

   private void logConfiguration() {
      LOGGER.debug((String)"{} - configuration:", (Object)this.poolName);

      for(String var3 : new TreeSet(PropertyElf.getPropertyNames(HikariConfig.class))) {
         try {
            Object var4 = PropertyElf.getProperty(var3, this);
            if ("dataSourceProperties".equals(var3)) {
               Properties var5 = PropertyElf.copyProperties(this.dataSourceProperties);
               var5.setProperty("password", "<masked>");
               var4 = var5;
            }

            if ("initializationFailTimeout".equals(var3) && this.initializationFailTimeout == Long.MAX_VALUE) {
               var4 = "infinite";
            } else if ("transactionIsolation".equals(var3) && this.transactionIsolationName == null) {
               var4 = "default";
            } else if (var3.matches("scheduledExecutorService|threadFactory") && var4 == null) {
               var4 = "internal";
            } else if (var3.contains("jdbcUrl") && var4 instanceof String) {
               var4 = ((String)var4).replaceAll("([?&;][^&#;=]*[pP]assword=)[^&#;]*", "$1<masked>");
            } else if (var3.contains("password")) {
               var4 = "<masked>";
            } else if (var4 instanceof String) {
               var4 = "\"" + var4 + "\"";
            } else if (var4 == null) {
               var4 = "none";
            }

            LOGGER.debug((String)"{}{}", (Object)(var3 + "................................................").substring(0, 32), (Object)var4);
         } catch (Exception var6) {
         }
      }

   }

   private void loadProperties(String var1) {
      File var2 = new File(var1);

      try {
         Object var3 = var2.isFile() ? new FileInputStream(var2) : this.getClass().getResourceAsStream(var1);

         try {
            if (var3 == null) {
               throw new IllegalArgumentException("Cannot find property file: " + var1);
            }

            Properties var4 = new Properties();
            var4.load((InputStream)var3);
            PropertyElf.setTargetFromProperties(this, var4);
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  ((InputStream)var3).close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            ((InputStream)var3).close();
         }

      } catch (IOException var8) {
         throw new RuntimeException("Failed to read property file", var8);
      }
   }

   private String generatePoolName() {
      String var1 = "HikariPool-";

      try {
         synchronized(System.getProperties()) {
            String var8 = String.valueOf(Integer.getInteger("com.zaxxer.hikari.pool_number", 0) + 1);
            System.setProperty("com.zaxxer.hikari.pool_number", var8);
            return "HikariPool-" + var8;
         }
      } catch (AccessControlException var7) {
         ThreadLocalRandom var3 = ThreadLocalRandom.current();
         StringBuilder var4 = new StringBuilder("HikariPool-");

         for(int var5 = 0; var5 < 4; ++var5) {
            var4.append(ID_CHARACTERS[var3.nextInt(62)]);
         }

         LOGGER.info((String)"assigned random pool name '{}' (security manager prevented access to system properties)", (Object)var4);
         return var4.toString();
      }
   }

   private Object getObjectOrPerformJndiLookup(Object var1) {
      if (var1 instanceof String) {
         try {
            InitialContext var2 = new InitialContext();
            return var2.lookup((String)var1);
         } catch (NamingException var3) {
            throw new IllegalArgumentException(var3);
         }
      } else {
         return var1;
      }
   }

   static {
      CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);
      VALIDATION_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
      SOFT_TIMEOUT_FLOOR = Long.getLong("com.zaxxer.hikari.timeoutMs.floor", 250L);
      IDLE_TIMEOUT = TimeUnit.MINUTES.toMillis(10L);
      MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30L);
      unitTest = false;
   }
}
