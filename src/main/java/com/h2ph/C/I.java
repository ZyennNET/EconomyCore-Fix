package com.h2ph.c;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class I {
   public static final Map<UUID, _A> C = new ConcurrentHashMap();
   public static final Map<UUID, H._A> B = new ConcurrentHashMap();
   public static final Map<UUID, Integer> A = new ConcurrentHashMap();

   private I() {
   }

   public static void A(UUID var0) {
      C.remove(var0);
      B.remove(var0);
      A.remove(var0);
   }

   public static enum _A {
      D,
      C,
      A;

      // $FF: synthetic method
      private static _A[] A() {
         return new _A[]{D, C, A};
      }
   }
}
