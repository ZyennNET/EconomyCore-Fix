package org.slf4j.helpers;

public class FormattingTuple {
   public static FormattingTuple NULL = new FormattingTuple((String)null);
   private final String message;
   private final Throwable throwable;
   private final Object[] argArray;

   public FormattingTuple(String var1) {
      this(var1, (Object[])null, (Throwable)null);
   }

   public FormattingTuple(String var1, Object[] var2, Throwable var3) {
      this.message = var1;
      this.throwable = var3;
      this.argArray = var2;
   }

   public String getMessage() {
      return this.message;
   }

   public Object[] getArgArray() {
      return this.argArray;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }
}
