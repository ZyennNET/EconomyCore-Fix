package com.h2ph.b;

public class C {
   public static String A(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         char[] var1 = var0.toCharArray();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] >= 'a' && var1[var2] <= 'z') {
               var1[var2] = (char)(var1[var2] - 97 + 7424);
            } else if (var1[var2] >= 'A' && var1[var2] <= 'Z') {
               var1[var2] = (char)(var1[var2] - 65 + 7424);
            }
         }

         return new String(var1);
      } else {
         return var0;
      }
   }
}
