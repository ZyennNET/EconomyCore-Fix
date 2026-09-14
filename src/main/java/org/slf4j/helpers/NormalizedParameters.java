package org.slf4j.helpers;

import org.slf4j.event.LoggingEvent;

public class NormalizedParameters {
   final String message;
   final Object[] arguments;
   final Throwable throwable;

   public NormalizedParameters(String var1, Object[] var2, Throwable var3) {
      this.message = var1;
      this.arguments = var2;
      this.throwable = var3;
   }

   public NormalizedParameters(String var1, Object[] var2) {
      this(var1, var2, (Throwable)null);
   }

   public String getMessage() {
      return this.message;
   }

   public Object[] getArguments() {
      return this.arguments;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public static Throwable getThrowableCandidate(Object[] var0) {
      if (var0 != null && var0.length != 0) {
         Object var1 = var0[var0.length - 1];
         return var1 instanceof Throwable ? (Throwable)var1 : null;
      } else {
         return null;
      }
   }

   public static Object[] trimmedCopy(Object[] var0) {
      if (var0 != null && var0.length != 0) {
         int var1 = var0.length - 1;
         Object[] var2 = new Object[var1];
         if (var1 > 0) {
            System.arraycopy(var0, 0, var2, 0, var1);
         }

         return var2;
      } else {
         throw new IllegalStateException("non-sensical empty or null argument array");
      }
   }

   public static NormalizedParameters normalize(String var0, Object[] var1, Throwable var2) {
      if (var2 != null) {
         return new NormalizedParameters(var0, var1, var2);
      } else if (var1 != null && var1.length != 0) {
         Throwable var3 = getThrowableCandidate(var1);
         if (var3 != null) {
            Object[] var4 = MessageFormatter.trimmedCopy(var1);
            return new NormalizedParameters(var0, var4, var3);
         } else {
            return new NormalizedParameters(var0, var1);
         }
      } else {
         return new NormalizedParameters(var0, var1, var2);
      }
   }

   public static NormalizedParameters normalize(LoggingEvent var0) {
      return normalize(var0.getMessage(), var0.getArgumentArray(), var0.getThrowable());
   }
}
