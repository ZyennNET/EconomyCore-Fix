package com.google.gson.internal;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberLimits {
   private static final int MAX_NUMBER_STRING_LENGTH = 10000;

   private NumberLimits() {
   }

   private static void checkNumberStringLength(String var0) {
      if (var0.length() > 10000) {
         throw new NumberFormatException("Number string too large: " + var0.substring(0, 30) + "...");
      }
   }

   public static BigDecimal parseBigDecimal(String var0) throws NumberFormatException {
      checkNumberStringLength(var0);
      BigDecimal var1 = new BigDecimal(var0);
      if (Math.abs((long)var1.scale()) >= 10000L) {
         throw new NumberFormatException("Number has unsupported scale: " + var0);
      } else {
         return var1;
      }
   }

   public static BigInteger parseBigInteger(String var0) throws NumberFormatException {
      checkNumberStringLength(var0);
      return new BigInteger(var0);
   }
}
