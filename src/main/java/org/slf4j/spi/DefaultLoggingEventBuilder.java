package org.slf4j.spi;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.DefaultLoggingEvent;
import org.slf4j.event.KeyValuePair;
import org.slf4j.event.Level;
import org.slf4j.event.LoggingEvent;

public class DefaultLoggingEventBuilder implements LoggingEventBuilder, CallerBoundaryAware {
   static String DLEB_FQCN = DefaultLoggingEventBuilder.class.getName();
   protected DefaultLoggingEvent loggingEvent;
   protected Logger logger;

   public DefaultLoggingEventBuilder(Logger var1, Level var2) {
      this.logger = var1;
      this.loggingEvent = new DefaultLoggingEvent(var2, var1);
   }

   public LoggingEventBuilder addMarker(Marker var1) {
      this.loggingEvent.addMarker(var1);
      return this;
   }

   public LoggingEventBuilder setCause(Throwable var1) {
      this.loggingEvent.setThrowable(var1);
      return this;
   }

   public LoggingEventBuilder addArgument(Object var1) {
      this.loggingEvent.addArgument(var1);
      return this;
   }

   public LoggingEventBuilder addArgument(Supplier<?> var1) {
      this.loggingEvent.addArgument(var1.get());
      return this;
   }

   public void setCallerBoundary(String var1) {
      this.loggingEvent.setCallerBoundary(var1);
   }

   public void log() {
      this.log((LoggingEvent)this.loggingEvent);
   }

   public LoggingEventBuilder setMessage(String var1) {
      this.loggingEvent.setMessage(var1);
      return this;
   }

   public LoggingEventBuilder setMessage(Supplier<String> var1) {
      this.loggingEvent.setMessage((String)var1.get());
      return this;
   }

   public void log(String var1) {
      this.loggingEvent.setMessage(var1);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(String var1, Object var2) {
      this.loggingEvent.setMessage(var1);
      this.loggingEvent.addArgument(var2);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(String var1, Object var2, Object var3) {
      this.loggingEvent.setMessage(var1);
      this.loggingEvent.addArgument(var2);
      this.loggingEvent.addArgument(var3);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(String var1, Object... var2) {
      this.loggingEvent.setMessage(var1);
      this.loggingEvent.addArguments(var2);
      this.log((LoggingEvent)this.loggingEvent);
   }

   public void log(Supplier<String> var1) {
      if (var1 == null) {
         this.log((String)null);
      } else {
         this.log((String)var1.get());
      }

   }

   protected void log(LoggingEvent var1) {
      this.setCallerBoundary(DLEB_FQCN);
      if (this.logger instanceof LoggingEventAware) {
         ((LoggingEventAware)this.logger).log(var1);
      } else {
         this.logViaPublicSLF4JLoggerAPI(var1);
      }

   }

   private void logViaPublicSLF4JLoggerAPI(LoggingEvent var1) {
      Object[] var2 = var1.getArgumentArray();
      int var3 = var2 == null ? 0 : var2.length;
      Throwable var4 = var1.getThrowable();
      int var5 = var4 == null ? 0 : 1;
      String var6 = var1.getMessage();
      Object[] var7 = new Object[var3 + var5];
      if (var2 != null) {
         System.arraycopy(var2, 0, var7, 0, var3);
      }

      if (var4 != null) {
         var7[var3] = var4;
      }

      var6 = this.mergeMarkersAndKeyValuePairs(var1, var6);
      switch (var1.getLevel()) {
         case TRACE:
            this.logger.trace(var6, var7);
            break;
         case DEBUG:
            this.logger.debug(var6, var7);
            break;
         case INFO:
            this.logger.info(var6, var7);
            break;
         case WARN:
            this.logger.warn(var6, var7);
            break;
         case ERROR:
            this.logger.error(var6, var7);
      }

   }

   private String mergeMarkersAndKeyValuePairs(LoggingEvent var1, String var2) {
      StringBuilder var3 = null;
      if (var1.getMarkers() != null) {
         var3 = new StringBuilder();

         for(Marker var5 : var1.getMarkers()) {
            var3.append(var5);
            var3.append(' ');
         }
      }

      if (var1.getKeyValuePairs() != null) {
         if (var3 == null) {
            var3 = new StringBuilder();
         }

         for(KeyValuePair var7 : var1.getKeyValuePairs()) {
            var3.append(var7.key);
            var3.append('=');
            var3.append(var7.value);
            var3.append(' ');
         }
      }

      if (var3 != null) {
         var3.append(var2);
         return var3.toString();
      } else {
         return var2;
      }
   }

   public LoggingEventBuilder addKeyValue(String var1, Object var2) {
      this.loggingEvent.addKeyValue(var1, var2);
      return this;
   }

   public LoggingEventBuilder addKeyValue(String var1, Supplier<Object> var2) {
      this.loggingEvent.addKeyValue(var1, var2.get());
      return this;
   }
}
