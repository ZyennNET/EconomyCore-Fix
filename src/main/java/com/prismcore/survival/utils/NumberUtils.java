package com.prismcore.survival.utils;

import java.text.DecimalFormat;

public class NumberUtils {
   private static final DecimalFormat DF = new DecimalFormat("#.##");

   public static String format(double var0) {
      if (var0 >= 1.0E12) {
         return formatWithSuffix(var0, 1.0E12, "t");
      } else if (var0 >= (double)1.0E9F) {
         return formatWithSuffix(var0, (double)1.0E9F, "B");
      } else if (var0 >= (double)1000000.0F) {
         return formatWithSuffix(var0, (double)1000000.0F, "M");
      } else {
         return var0 >= (double)1000.0F ? formatWithSuffix(var0, (double)1000.0F, "K") : DF.format(var0);
      }
   }

   private static String formatWithSuffix(double var0, double var2, String var4) {
      double var5 = var0 / var2;
      String var10000 = DF.format(var5);
      return var10000 + var4;
   }

   public static String formatMoney(double var0) {
      return "$" + format(var0);
   }
}
