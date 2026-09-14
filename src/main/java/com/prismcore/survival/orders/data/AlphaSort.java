package com.prismcore.survival.orders.data;

public enum AlphaSort {
   A_Z,
   Z_A;

   public AlphaSort toggle() {
      return this == A_Z ? Z_A : A_Z;
   }

   // $FF: synthetic method
   private static AlphaSort[] $values() {
      return new AlphaSort[]{A_Z, Z_A};
   }
}
