package com.h2ph.b;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public final class D {
   private static final Pattern A = Pattern.compile("#([A-Fa-f0-9]{6})");

   private D() {
   }

   public static String A(String var0) {
      if (var0 == null) {
         return "";
      } else {
         Matcher var1 = A.matcher(var0);
         StringBuilder var2 = new StringBuilder(var0.length() + 32);

         int var3;
         for(var3 = 0; var1.find(); var3 = var1.end()) {
            var2.append(var0, var3, var1.start());
            var2.append(ChatColor.of("#" + var1.group(1)).toString());
         }

         var2.append(var0.substring(var3));
         return org.bukkit.ChatColor.translateAlternateColorCodes('&', var2.toString());
      }
   }

   public static List<String> A(List<String> var0) {
      ArrayList var1 = new ArrayList();
      if (var0 != null) {
         for(String var3 : var0) {
            var1.add(A(var3));
         }
      }

      return var1;
   }
}
