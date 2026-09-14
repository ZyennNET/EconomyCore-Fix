package com.zaxxer.hikari.util;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverDataSource implements DataSource {
   private static final Logger LOGGER = LoggerFactory.getLogger(DriverDataSource.class);
   private static final String PASSWORD = "password";
   private static final String USER = "user";
   private final String jdbcUrl;
   private final Properties driverProperties;
   private Driver driver;

   public DriverDataSource(String var1, String var2, Properties var3, String var4, String var5) {
      this.jdbcUrl = var1;
      this.driverProperties = new Properties();

      for(Map.Entry var7 : var3.entrySet()) {
         this.driverProperties.setProperty(var7.getKey().toString(), var7.getValue().toString());
      }

      if (var4 != null) {
         this.driverProperties.put("user", this.driverProperties.getProperty("user", var4));
      }

      if (var5 != null) {
         this.driverProperties.put("password", this.driverProperties.getProperty("password", var5));
      }

      if (var2 != null) {
         Enumeration var14 = DriverManager.getDrivers();

         while(var14.hasMoreElements()) {
            Driver var16 = (Driver)var14.nextElement();
            if (var16.getClass().getName().equals(var2)) {
               this.driver = var16;
               break;
            }
         }

         if (this.driver == null) {
            LOGGER.warn((String)"Registered driver with driverClassName={} was not found, trying direct instantiation.", (Object)var2);
            Class var17 = null;
            ClassLoader var8 = Thread.currentThread().getContextClassLoader();

            try {
               if (var8 != null) {
                  try {
                     var17 = var8.loadClass(var2);
                     LOGGER.debug((String)"Driver class {} found in Thread context class loader {}", (Object)var2, (Object)var8);
                  } catch (ClassNotFoundException var12) {
                     LOGGER.debug("Driver class {} not found in Thread context class loader {}, trying classloader {}", var2, var8, this.getClass().getClassLoader());
                  }
               }

               if (var17 == null) {
                  var17 = this.getClass().getClassLoader().loadClass(var2);
                  LOGGER.debug((String)"Driver class {} found in the HikariConfig class classloader {}", (Object)var2, (Object)this.getClass().getClassLoader());
               }
            } catch (ClassNotFoundException var13) {
               LOGGER.debug((String)"Failed to load driver class {} from HikariConfig class classloader {}", (Object)var2, (Object)this.getClass().getClassLoader());
            }

            if (var17 != null) {
               try {
                  this.driver = (Driver)var17.getDeclaredConstructor().newInstance();
               } catch (Exception var11) {
                  LOGGER.warn((String)"Failed to create instance of driver class {}, trying jdbcUrl resolution", (Object)var2, (Object)var11);
               }
            }
         }
      }

      String var15 = var1.replaceAll("([?&;][^&#;=]*[pP]assword=)[^&#;]*", "$1<masked>");

      try {
         if (this.driver == null) {
            this.driver = DriverManager.getDriver(var1);
            LOGGER.debug((String)"Loaded driver with class name {} for jdbcUrl={}", (Object)this.driver.getClass().getName(), (Object)var15);
         } else if (!this.driver.acceptsURL(var1)) {
            throw new RuntimeException("Driver " + var2 + " claims to not accept jdbcUrl, " + var15);
         }

      } catch (SQLException var10) {
         throw new RuntimeException("Failed to get driver instance for jdbcUrl=" + var15, var10);
      }
   }

   public Connection getConnection() throws SQLException {
      return this.driver.connect(this.jdbcUrl, this.driverProperties);
   }

   public Connection getConnection(String var1, String var2) throws SQLException {
      Properties var3 = (Properties)this.driverProperties.clone();
      if (var1 != null) {
         var3.put("user", var1);
         if (var3.containsKey("username")) {
            var3.put("username", var1);
         }
      }

      if (var2 != null) {
         var3.put("password", var2);
      }

      return this.driver.connect(this.jdbcUrl, var3);
   }

   public PrintWriter getLogWriter() throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   public void setLogWriter(PrintWriter var1) throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   public void setLoginTimeout(int var1) throws SQLException {
      DriverManager.setLoginTimeout(var1);
   }

   public int getLoginTimeout() throws SQLException {
      return DriverManager.getLoginTimeout();
   }

   public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
      return this.driver.getParentLogger();
   }

   public <T> T unwrap(Class<T> var1) throws SQLException {
      throw new SQLFeatureNotSupportedException();
   }

   public boolean isWrapperFor(Class<?> var1) throws SQLException {
      return false;
   }
}
