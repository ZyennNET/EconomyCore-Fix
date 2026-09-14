package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ThrowableCallStack implements CallStack {
   private final String messageFormat;
   private final DateFormat dateFormat;
   private volatile Snapshot snapshot;

   public ThrowableCallStack(String var1, boolean var2) {
      this.messageFormat = var1;
      this.dateFormat = var2 ? new SimpleDateFormat(var1) : null;
   }

   public void clear() {
      this.snapshot = null;
   }

   public void fillInStackTrace() {
      this.snapshot = new Snapshot();
   }

   public synchronized boolean printStackTrace(PrintWriter var1) {
      Snapshot var2 = this.snapshot;
      if (var2 == null) {
         return false;
      } else {
         String var3;
         if (this.dateFormat == null) {
            var3 = this.messageFormat;
         } else {
            synchronized(this.dateFormat) {
               var3 = this.dateFormat.format(var2.timestampMillis);
            }
         }

         var1.println(var3);
         var2.printStackTrace(var1);
         return true;
      }
   }

   private static class Snapshot extends Throwable {
      private static final long serialVersionUID = 1L;
      private final long timestampMillis;

      private Snapshot() {
         this.timestampMillis = System.currentTimeMillis();
      }
   }
}
