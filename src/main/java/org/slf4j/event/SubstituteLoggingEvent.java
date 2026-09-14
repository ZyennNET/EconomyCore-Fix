package org.slf4j.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Marker;
import org.slf4j.helpers.SubstituteLogger;

public class SubstituteLoggingEvent implements LoggingEvent {
   Level level;
   List<Marker> markers;
   String loggerName;
   SubstituteLogger logger;
   String threadName;
   String message;
   Object[] argArray;
   List<KeyValuePair> keyValuePairList;
   long timeStamp;
   Throwable throwable;

   public Level getLevel() {
      return this.level;
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   public List<Marker> getMarkers() {
      return this.markers;
   }

   public void addMarker(Marker var1) {
      if (var1 != null) {
         if (this.markers == null) {
            this.markers = new ArrayList(2);
         }

         this.markers.add(var1);
      }
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public void setLoggerName(String var1) {
      this.loggerName = var1;
   }

   public SubstituteLogger getLogger() {
      return this.logger;
   }

   public void setLogger(SubstituteLogger var1) {
      this.logger = var1;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public Object[] getArgumentArray() {
      return this.argArray;
   }

   public void setArgumentArray(Object[] var1) {
      this.argArray = var1;
   }

   public List<Object> getArguments() {
      return this.argArray == null ? null : Arrays.asList(this.argArray);
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public void setTimeStamp(long var1) {
      this.timeStamp = var1;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public void setThreadName(String var1) {
      this.threadName = var1;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public void setThrowable(Throwable var1) {
      this.throwable = var1;
   }

   public List<KeyValuePair> getKeyValuePairs() {
      return this.keyValuePairList;
   }
}
