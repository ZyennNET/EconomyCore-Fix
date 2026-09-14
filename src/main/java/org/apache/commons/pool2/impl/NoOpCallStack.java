package org.apache.commons.pool2.impl;

import java.io.PrintWriter;

public class NoOpCallStack implements CallStack {
   public static final CallStack INSTANCE = new NoOpCallStack();

   private NoOpCallStack() {
   }

   public void clear() {
   }

   public void fillInStackTrace() {
   }

   public boolean printStackTrace(PrintWriter var1) {
      return false;
   }
}
