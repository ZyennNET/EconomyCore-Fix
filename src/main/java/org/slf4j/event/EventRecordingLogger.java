package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Marker;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.SubstituteLogger;

public class EventRecordingLogger extends LegacyAbstractLogger {
   private static final long serialVersionUID = -176083308134819629L;
   String name;
   SubstituteLogger logger;
   Queue<SubstituteLoggingEvent> eventQueue;
   static final boolean RECORD_ALL_EVENTS = true;

   public EventRecordingLogger(SubstituteLogger var1, Queue<SubstituteLoggingEvent> var2) {
      this.logger = var1;
      this.name = var1.getName();
      this.eventQueue = var2;
   }

   public String getName() {
      return this.name;
   }

   public boolean isTraceEnabled() {
      return true;
   }

   public boolean isDebugEnabled() {
      return true;
   }

   public boolean isInfoEnabled() {
      return true;
   }

   public boolean isWarnEnabled() {
      return true;
   }

   public boolean isErrorEnabled() {
      return true;
   }

   protected void handleNormalizedLoggingCall(Level var1, Marker var2, String var3, Object[] var4, Throwable var5) {
      SubstituteLoggingEvent var6 = new SubstituteLoggingEvent();
      var6.setTimeStamp(System.currentTimeMillis());
      var6.setLevel(var1);
      var6.setLogger(this.logger);
      var6.setLoggerName(this.name);
      if (var2 != null) {
         var6.addMarker(var2);
      }

      var6.setMessage(var3);
      var6.setThreadName(Thread.currentThread().getName());
      var6.setArgumentArray(var4);
      var6.setThrowable(var5);
      this.eventQueue.add(var6);
   }

   protected String getFullyQualifiedCallerName() {
      return null;
   }
}
