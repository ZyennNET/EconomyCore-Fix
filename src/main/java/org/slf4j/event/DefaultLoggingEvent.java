package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.Marker;

public class DefaultLoggingEvent implements LoggingEvent {
   Logger logger;
   Level level;
   String message;
   List<Marker> markers;
   List<Object> arguments;
   List<KeyValuePair> keyValuePairs;
   Throwable throwable;
   String threadName;
   long timeStamp;
   String callerBoundary;

   public DefaultLoggingEvent(Level var1, Logger var2) {
      this.logger = var2;
      this.level = var1;
   }

   public void addMarker(Marker var1) {
      if (this.markers == null) {
         this.markers = new ArrayList(2);
      }

      this.markers.add(var1);
   }

   public List<Marker> getMarkers() {
      return this.markers;
   }

   public void addArgument(Object var1) {
      this.getNonNullArguments().add(var1);
   }

   public void addArguments(Object... var1) {
      this.getNonNullArguments().addAll(Arrays.asList(var1));
   }

   private List<Object> getNonNullArguments() {
      if (this.arguments == null) {
         this.arguments = new ArrayList(3);
      }

      return this.arguments;
   }

   public List<Object> getArguments() {
      return this.arguments;
   }

   public Object[] getArgumentArray() {
      return this.arguments == null ? null : this.arguments.toArray();
   }

   public void addKeyValue(String var1, Object var2) {
      this.getNonnullKeyValuePairs().add(new KeyValuePair(var1, var2));
   }

   private List<KeyValuePair> getNonnullKeyValuePairs() {
      if (this.keyValuePairs == null) {
         this.keyValuePairs = new ArrayList(4);
      }

      return this.keyValuePairs;
   }

   public List<KeyValuePair> getKeyValuePairs() {
      return this.keyValuePairs;
   }

   public void setThrowable(Throwable var1) {
      this.throwable = var1;
   }

   public Level getLevel() {
      return this.level;
   }

   public String getLoggerName() {
      return this.logger.getName();
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
   }

   public void setCallerBoundary(String var1) {
      this.callerBoundary = var1;
   }

   public String getCallerBoundary() {
      return this.callerBoundary;
   }
}
