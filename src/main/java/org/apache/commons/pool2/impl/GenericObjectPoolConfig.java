package org.apache.commons.pool2.impl;

public class GenericObjectPoolConfig<T> extends BaseObjectPoolConfig<T> {
   public static final int DEFAULT_MAX_TOTAL = 8;
   public static final int DEFAULT_MAX_IDLE = 8;
   public static final int DEFAULT_MIN_IDLE = 0;
   private int maxTotal = 8;
   private int maxIdle = 8;
   private int minIdle = 0;

   public GenericObjectPoolConfig<T> clone() {
      try {
         return (GenericObjectPoolConfig)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new AssertionError();
      }
   }

   public int getMaxIdle() {
      return this.maxIdle;
   }

   public int getMaxTotal() {
      return this.maxTotal;
   }

   public int getMinIdle() {
      return this.minIdle;
   }

   public void setMaxIdle(int var1) {
      this.maxIdle = var1;
   }

   public void setMaxTotal(int var1) {
      this.maxTotal = var1;
   }

   public void setMinIdle(int var1) {
      this.minIdle = var1;
   }

   protected void toStringAppendFields(StringBuilder var1) {
      super.toStringAppendFields(var1);
      var1.append(", maxTotal=");
      var1.append(this.maxTotal);
      var1.append(", maxIdle=");
      var1.append(this.maxIdle);
      var1.append(", minIdle=");
      var1.append(this.minIdle);
   }
}
