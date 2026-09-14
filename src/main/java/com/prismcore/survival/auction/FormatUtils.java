package com.prismcore.survival.auction;

public class FormatUtils {
   public static String formatTime(int var0) {
      int var1 = var0 / 3600;
      int var2 = var0 % 3600 / 60;
      int var3 = var0 % 60;
      return var1 + "h " + var2 + "m " + var3 + "s";
   }
}
