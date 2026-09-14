package org.slf4j.helpers;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.event.Level;

public abstract class AbstractLogger implements Logger, Serializable {
   private static final long serialVersionUID = -2529255052481744503L;
   protected String name;

   public String getName() {
      return this.name;
   }

   protected Object readResolve() throws ObjectStreamException {
      return LoggerFactory.getLogger(this.getName());
   }

   public void trace(String var1) {
      if (this.isTraceEnabled()) {
         this.handle_0ArgsCall(Level.TRACE, (Marker)null, var1, (Throwable)null);
      }

   }

   public void trace(String var1, Object var2) {
      if (this.isTraceEnabled()) {
         this.handle_1ArgsCall(Level.TRACE, (Marker)null, var1, var2);
      }

   }

   public void trace(String var1, Object var2, Object var3) {
      if (this.isTraceEnabled()) {
         this.handle2ArgsCall(Level.TRACE, (Marker)null, var1, var2, var3);
      }

   }

   public void trace(String var1, Object... var2) {
      if (this.isTraceEnabled()) {
         this.handleArgArrayCall(Level.TRACE, (Marker)null, var1, var2);
      }

   }

   public void trace(String var1, Throwable var2) {
      if (this.isTraceEnabled()) {
         this.handle_0ArgsCall(Level.TRACE, (Marker)null, var1, var2);
      }

   }

   public void trace(Marker var1, String var2) {
      if (this.isTraceEnabled(var1)) {
         this.handle_0ArgsCall(Level.TRACE, var1, var2, (Throwable)null);
      }

   }

   public void trace(Marker var1, String var2, Object var3) {
      if (this.isTraceEnabled(var1)) {
         this.handle_1ArgsCall(Level.TRACE, var1, var2, var3);
      }

   }

   public void trace(Marker var1, String var2, Object var3, Object var4) {
      if (this.isTraceEnabled(var1)) {
         this.handle2ArgsCall(Level.TRACE, var1, var2, var3, var4);
      }

   }

   public void trace(Marker var1, String var2, Object... var3) {
      if (this.isTraceEnabled(var1)) {
         this.handleArgArrayCall(Level.TRACE, var1, var2, var3);
      }

   }

   public void trace(Marker var1, String var2, Throwable var3) {
      if (this.isTraceEnabled(var1)) {
         this.handle_0ArgsCall(Level.TRACE, var1, var2, var3);
      }

   }

   public void debug(String var1) {
      if (this.isDebugEnabled()) {
         this.handle_0ArgsCall(Level.DEBUG, (Marker)null, var1, (Throwable)null);
      }

   }

   public void debug(String var1, Object var2) {
      if (this.isDebugEnabled()) {
         this.handle_1ArgsCall(Level.DEBUG, (Marker)null, var1, var2);
      }

   }

   public void debug(String var1, Object var2, Object var3) {
      if (this.isDebugEnabled()) {
         this.handle2ArgsCall(Level.DEBUG, (Marker)null, var1, var2, var3);
      }

   }

   public void debug(String var1, Object... var2) {
      if (this.isDebugEnabled()) {
         this.handleArgArrayCall(Level.DEBUG, (Marker)null, var1, var2);
      }

   }

   public void debug(String var1, Throwable var2) {
      if (this.isDebugEnabled()) {
         this.handle_0ArgsCall(Level.DEBUG, (Marker)null, var1, var2);
      }

   }

   public void debug(Marker var1, String var2) {
      if (this.isDebugEnabled(var1)) {
         this.handle_0ArgsCall(Level.DEBUG, var1, var2, (Throwable)null);
      }

   }

   public void debug(Marker var1, String var2, Object var3) {
      if (this.isDebugEnabled(var1)) {
         this.handle_1ArgsCall(Level.DEBUG, var1, var2, var3);
      }

   }

   public void debug(Marker var1, String var2, Object var3, Object var4) {
      if (this.isDebugEnabled(var1)) {
         this.handle2ArgsCall(Level.DEBUG, var1, var2, var3, var4);
      }

   }

   public void debug(Marker var1, String var2, Object... var3) {
      if (this.isDebugEnabled(var1)) {
         this.handleArgArrayCall(Level.DEBUG, var1, var2, var3);
      }

   }

   public void debug(Marker var1, String var2, Throwable var3) {
      if (this.isDebugEnabled(var1)) {
         this.handle_0ArgsCall(Level.DEBUG, var1, var2, var3);
      }

   }

   public void info(String var1) {
      if (this.isInfoEnabled()) {
         this.handle_0ArgsCall(Level.INFO, (Marker)null, var1, (Throwable)null);
      }

   }

   public void info(String var1, Object var2) {
      if (this.isInfoEnabled()) {
         this.handle_1ArgsCall(Level.INFO, (Marker)null, var1, var2);
      }

   }

   public void info(String var1, Object var2, Object var3) {
      if (this.isInfoEnabled()) {
         this.handle2ArgsCall(Level.INFO, (Marker)null, var1, var2, var3);
      }

   }

   public void info(String var1, Object... var2) {
      if (this.isInfoEnabled()) {
         this.handleArgArrayCall(Level.INFO, (Marker)null, var1, var2);
      }

   }

   public void info(String var1, Throwable var2) {
      if (this.isInfoEnabled()) {
         this.handle_0ArgsCall(Level.INFO, (Marker)null, var1, var2);
      }

   }

   public void info(Marker var1, String var2) {
      if (this.isInfoEnabled(var1)) {
         this.handle_0ArgsCall(Level.INFO, var1, var2, (Throwable)null);
      }

   }

   public void info(Marker var1, String var2, Object var3) {
      if (this.isInfoEnabled(var1)) {
         this.handle_1ArgsCall(Level.INFO, var1, var2, var3);
      }

   }

   public void info(Marker var1, String var2, Object var3, Object var4) {
      if (this.isInfoEnabled(var1)) {
         this.handle2ArgsCall(Level.INFO, var1, var2, var3, var4);
      }

   }

   public void info(Marker var1, String var2, Object... var3) {
      if (this.isInfoEnabled(var1)) {
         this.handleArgArrayCall(Level.INFO, var1, var2, var3);
      }

   }

   public void info(Marker var1, String var2, Throwable var3) {
      if (this.isInfoEnabled(var1)) {
         this.handle_0ArgsCall(Level.INFO, var1, var2, var3);
      }

   }

   public void warn(String var1) {
      if (this.isWarnEnabled()) {
         this.handle_0ArgsCall(Level.WARN, (Marker)null, var1, (Throwable)null);
      }

   }

   public void warn(String var1, Object var2) {
      if (this.isWarnEnabled()) {
         this.handle_1ArgsCall(Level.WARN, (Marker)null, var1, var2);
      }

   }

   public void warn(String var1, Object var2, Object var3) {
      if (this.isWarnEnabled()) {
         this.handle2ArgsCall(Level.WARN, (Marker)null, var1, var2, var3);
      }

   }

   public void warn(String var1, Object... var2) {
      if (this.isWarnEnabled()) {
         this.handleArgArrayCall(Level.WARN, (Marker)null, var1, var2);
      }

   }

   public void warn(String var1, Throwable var2) {
      if (this.isWarnEnabled()) {
         this.handle_0ArgsCall(Level.WARN, (Marker)null, var1, var2);
      }

   }

   public void warn(Marker var1, String var2) {
      if (this.isWarnEnabled(var1)) {
         this.handle_0ArgsCall(Level.WARN, var1, var2, (Throwable)null);
      }

   }

   public void warn(Marker var1, String var2, Object var3) {
      if (this.isWarnEnabled(var1)) {
         this.handle_1ArgsCall(Level.WARN, var1, var2, var3);
      }

   }

   public void warn(Marker var1, String var2, Object var3, Object var4) {
      if (this.isWarnEnabled(var1)) {
         this.handle2ArgsCall(Level.WARN, var1, var2, var3, var4);
      }

   }

   public void warn(Marker var1, String var2, Object... var3) {
      if (this.isWarnEnabled(var1)) {
         this.handleArgArrayCall(Level.WARN, var1, var2, var3);
      }

   }

   public void warn(Marker var1, String var2, Throwable var3) {
      if (this.isWarnEnabled(var1)) {
         this.handle_0ArgsCall(Level.WARN, var1, var2, var3);
      }

   }

   public void error(String var1) {
      if (this.isErrorEnabled()) {
         this.handle_0ArgsCall(Level.ERROR, (Marker)null, var1, (Throwable)null);
      }

   }

   public void error(String var1, Object var2) {
      if (this.isErrorEnabled()) {
         this.handle_1ArgsCall(Level.ERROR, (Marker)null, var1, var2);
      }

   }

   public void error(String var1, Object var2, Object var3) {
      if (this.isErrorEnabled()) {
         this.handle2ArgsCall(Level.ERROR, (Marker)null, var1, var2, var3);
      }

   }

   public void error(String var1, Object... var2) {
      if (this.isErrorEnabled()) {
         this.handleArgArrayCall(Level.ERROR, (Marker)null, var1, var2);
      }

   }

   public void error(String var1, Throwable var2) {
      if (this.isErrorEnabled()) {
         this.handle_0ArgsCall(Level.ERROR, (Marker)null, var1, var2);
      }

   }

   public void error(Marker var1, String var2) {
      if (this.isErrorEnabled(var1)) {
         this.handle_0ArgsCall(Level.ERROR, var1, var2, (Throwable)null);
      }

   }

   public void error(Marker var1, String var2, Object var3) {
      if (this.isErrorEnabled(var1)) {
         this.handle_1ArgsCall(Level.ERROR, var1, var2, var3);
      }

   }

   public void error(Marker var1, String var2, Object var3, Object var4) {
      if (this.isErrorEnabled(var1)) {
         this.handle2ArgsCall(Level.ERROR, var1, var2, var3, var4);
      }

   }

   public void error(Marker var1, String var2, Object... var3) {
      if (this.isErrorEnabled(var1)) {
         this.handleArgArrayCall(Level.ERROR, var1, var2, var3);
      }

   }

   public void error(Marker var1, String var2, Throwable var3) {
      if (this.isErrorEnabled(var1)) {
         this.handle_0ArgsCall(Level.ERROR, var1, var2, var3);
      }

   }

   private void handle_0ArgsCall(Level var1, Marker var2, String var3, Throwable var4) {
      this.handleNormalizedLoggingCall(var1, var2, var3, (Object[])null, var4);
   }

   private void handle_1ArgsCall(Level var1, Marker var2, String var3, Object var4) {
      this.handleNormalizedLoggingCall(var1, var2, var3, new Object[]{var4}, (Throwable)null);
   }

   private void handle2ArgsCall(Level var1, Marker var2, String var3, Object var4, Object var5) {
      if (var5 instanceof Throwable) {
         this.handleNormalizedLoggingCall(var1, var2, var3, new Object[]{var4}, (Throwable)var5);
      } else {
         this.handleNormalizedLoggingCall(var1, var2, var3, new Object[]{var4, var5}, (Throwable)null);
      }

   }

   private void handleArgArrayCall(Level var1, Marker var2, String var3, Object[] var4) {
      Throwable var5 = MessageFormatter.getThrowableCandidate(var4);
      if (var5 != null) {
         Object[] var6 = MessageFormatter.trimmedCopy(var4);
         this.handleNormalizedLoggingCall(var1, var2, var3, var6, var5);
      } else {
         this.handleNormalizedLoggingCall(var1, var2, var3, var4, (Throwable)null);
      }

   }

   protected abstract String getFullyQualifiedCallerName();

   protected abstract void handleNormalizedLoggingCall(Level var1, Marker var2, String var3, Object[] var4, Throwable var5);
}
