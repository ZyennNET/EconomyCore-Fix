package org.slf4j.helpers;

import org.slf4j.Marker;

public abstract class LegacyAbstractLogger extends AbstractLogger {
   private static final long serialVersionUID = -7041884104854048950L;

   public boolean isTraceEnabled(Marker var1) {
      return this.isTraceEnabled();
   }

   public boolean isDebugEnabled(Marker var1) {
      return this.isDebugEnabled();
   }

   public boolean isInfoEnabled(Marker var1) {
      return this.isInfoEnabled();
   }

   public boolean isWarnEnabled(Marker var1) {
      return this.isWarnEnabled();
   }

   public boolean isErrorEnabled(Marker var1) {
      return this.isErrorEnabled();
   }
}
