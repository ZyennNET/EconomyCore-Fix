package com.h2ph.T.A;

import java.util.List;
import java.util.UUID;

public interface K {
   void A(UUID var1, String var2, double var3);

   void A(UUID var1);

   double B(UUID var1);

   List<_A> A();

   public static class _A {
      public final UUID B;
      public final String C;
      public final double A;
      public final long D;

      public _A(UUID var1, String var2, double var3, long var5) {
         this.B = var1;
         this.C = var2;
         this.A = var3;
         this.D = var5;
      }
   }
}
