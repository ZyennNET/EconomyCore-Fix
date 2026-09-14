package com.h2ph.N;

import java.util.UUID;

public class D {
   private final UUID A;
   private final UUID D;
   private final long C;
   private final _A B;

   public D(UUID var1, UUID var2, _A var3) {
      this.A = var1;
      this.D = var2;
      this.C = System.currentTimeMillis();
      this.B = var3;
   }

   public UUID A() {
      return this.A;
   }

   public UUID B() {
      return this.D;
   }

   public long D() {
      return this.C;
   }

   public _A E() {
      return this.B;
   }

   public boolean C() {
      return System.currentTimeMillis() - this.C > 30000L;
   }

   public static enum _A {
      B,
      C;

      // $FF: synthetic method
      private static _A[] A() {
         return new _A[]{B, C};
      }
   }
}
