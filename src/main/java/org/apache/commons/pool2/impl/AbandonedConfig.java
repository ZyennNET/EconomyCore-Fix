package org.apache.commons.pool2.impl;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.time.Duration;

public class AbandonedConfig {
   private static final Duration DEFAULT_REMOVE_ABANDONED_TIMEOUT_DURATION = Duration.ofMinutes(5L);
   private boolean removeAbandonedOnBorrow;
   private boolean removeAbandonedOnMaintenance;
   private Duration removeAbandonedTimeoutDuration;
   private boolean logAbandoned;
   private boolean requireFullStackTrace;
   private PrintWriter logWriter;
   private boolean useUsageTracking;

   public static AbandonedConfig copy(AbandonedConfig var0) {
      return var0 == null ? null : new AbandonedConfig(var0);
   }

   public AbandonedConfig() {
      this.removeAbandonedTimeoutDuration = DEFAULT_REMOVE_ABANDONED_TIMEOUT_DURATION;
      this.requireFullStackTrace = true;
      this.logWriter = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()));
   }

   private AbandonedConfig(AbandonedConfig var1) {
      this.removeAbandonedTimeoutDuration = DEFAULT_REMOVE_ABANDONED_TIMEOUT_DURATION;
      this.requireFullStackTrace = true;
      this.logWriter = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()));
      this.setLogAbandoned(var1.getLogAbandoned());
      this.setLogWriter(var1.getLogWriter());
      this.setRemoveAbandonedOnBorrow(var1.getRemoveAbandonedOnBorrow());
      this.setRemoveAbandonedOnMaintenance(var1.getRemoveAbandonedOnMaintenance());
      this.setRemoveAbandonedTimeout(var1.getRemoveAbandonedTimeoutDuration());
      this.setUseUsageTracking(var1.getUseUsageTracking());
      this.setRequireFullStackTrace(var1.getRequireFullStackTrace());
   }

   public boolean getLogAbandoned() {
      return this.logAbandoned;
   }

   public PrintWriter getLogWriter() {
      return this.logWriter;
   }

   public boolean getRemoveAbandonedOnBorrow() {
      return this.removeAbandonedOnBorrow;
   }

   public boolean getRemoveAbandonedOnMaintenance() {
      return this.removeAbandonedOnMaintenance;
   }

   @Deprecated
   public int getRemoveAbandonedTimeout() {
      return (int)this.removeAbandonedTimeoutDuration.getSeconds();
   }

   public Duration getRemoveAbandonedTimeoutDuration() {
      return this.removeAbandonedTimeoutDuration;
   }

   public boolean getRequireFullStackTrace() {
      return this.requireFullStackTrace;
   }

   public boolean getUseUsageTracking() {
      return this.useUsageTracking;
   }

   public void setLogAbandoned(boolean var1) {
      this.logAbandoned = var1;
   }

   public void setLogWriter(PrintWriter var1) {
      this.logWriter = var1;
   }

   public void setRemoveAbandonedOnBorrow(boolean var1) {
      this.removeAbandonedOnBorrow = var1;
   }

   public void setRemoveAbandonedOnMaintenance(boolean var1) {
      this.removeAbandonedOnMaintenance = var1;
   }

   public void setRemoveAbandonedTimeout(Duration var1) {
      this.removeAbandonedTimeoutDuration = PoolImplUtils.nonNull(var1, DEFAULT_REMOVE_ABANDONED_TIMEOUT_DURATION);
   }

   @Deprecated
   public void setRemoveAbandonedTimeout(int var1) {
      this.setRemoveAbandonedTimeout(Duration.ofSeconds((long)var1));
   }

   public void setRequireFullStackTrace(boolean var1) {
      this.requireFullStackTrace = var1;
   }

   public void setUseUsageTracking(boolean var1) {
      this.useUsageTracking = var1;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("AbandonedConfig [removeAbandonedOnBorrow=");
      var1.append(this.removeAbandonedOnBorrow);
      var1.append(", removeAbandonedOnMaintenance=");
      var1.append(this.removeAbandonedOnMaintenance);
      var1.append(", removeAbandonedTimeoutDuration=");
      var1.append(this.removeAbandonedTimeoutDuration);
      var1.append(", logAbandoned=");
      var1.append(this.logAbandoned);
      var1.append(", logWriter=");
      var1.append(this.logWriter);
      var1.append(", useUsageTracking=");
      var1.append(this.useUsageTracking);
      var1.append("]");
      return var1.toString();
   }
}
