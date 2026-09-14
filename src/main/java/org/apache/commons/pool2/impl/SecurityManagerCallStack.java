package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityManagerCallStack implements CallStack {
   private final String messageFormat;
   private final DateFormat dateFormat;
   private final PrivateSecurityManager securityManager;
   private volatile Snapshot snapshot;

   public SecurityManagerCallStack(String var1, boolean var2) {
      this.messageFormat = var1;
      this.dateFormat = var2 ? new SimpleDateFormat(var1) : null;
      this.securityManager = (PrivateSecurityManager)AccessController.doPrivileged(() -> new PrivateSecurityManager());
   }

   public void clear() {
      this.snapshot = null;
   }

   public void fillInStackTrace() {
      this.snapshot = new Snapshot(this.securityManager.getCallStack());
   }

   public boolean printStackTrace(PrintWriter var1) {
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
         var2.stack.forEach((var1x) -> var1.println(var1x.get()));
         return true;
      }
   }

   private static class PrivateSecurityManager extends SecurityManager {
      private PrivateSecurityManager() {
      }

      private List<WeakReference<Class<?>>> getCallStack() {
         Stream var1 = Stream.of(this.getClassContext()).map(WeakReference::new);
         return (List)var1.collect(Collectors.toList());
      }
   }

   private static class Snapshot {
      private final long timestampMillis;
      private final List<WeakReference<Class<?>>> stack;

      private Snapshot(List<WeakReference<Class<?>>> var1) {
         this.timestampMillis = System.currentTimeMillis();
         this.stack = var1;
      }
   }
}
