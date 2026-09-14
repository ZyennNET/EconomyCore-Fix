package com.google.gson.internal.bind.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class ISO8601Utils {
   private static final String UTC_ID = "UTC";
   private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone("UTC");

   private ISO8601Utils() {
   }

   public static String format(Date var0) {
      return format(var0, false, TIMEZONE_UTC);
   }

   public static String format(Date var0, boolean var1) {
      return format(var0, var1, TIMEZONE_UTC);
   }

   public static String format(Date var0, boolean var1, TimeZone var2) {
      GregorianCalendar var3 = new GregorianCalendar(var2, Locale.US);
      ((Calendar)var3).setTime(var0);
      int var4 = "yyyy-MM-ddThh:mm:ss".length();
      var4 += var1 ? ".sss".length() : 0;
      var4 += var2.getRawOffset() == 0 ? "Z".length() : "+hh:mm".length();
      StringBuilder var5 = new StringBuilder(var4);
      padInt(var5, ((Calendar)var3).get(1), "yyyy".length());
      var5.append('-');
      padInt(var5, ((Calendar)var3).get(2) + 1, "MM".length());
      var5.append('-');
      padInt(var5, ((Calendar)var3).get(5), "dd".length());
      var5.append('T');
      padInt(var5, ((Calendar)var3).get(11), "hh".length());
      var5.append(':');
      padInt(var5, ((Calendar)var3).get(12), "mm".length());
      var5.append(':');
      padInt(var5, ((Calendar)var3).get(13), "ss".length());
      if (var1) {
         var5.append('.');
         padInt(var5, ((Calendar)var3).get(14), "sss".length());
      }

      int var6 = var2.getOffset(((Calendar)var3).getTimeInMillis());
      if (var6 != 0) {
         int var7 = Math.abs(var6 / '\uea60' / 60);
         int var8 = Math.abs(var6 / '\uea60' % 60);
         var5.append((char)(var6 < 0 ? '-' : '+'));
         padInt(var5, var7, "hh".length());
         var5.append(':');
         padInt(var5, var8, "mm".length());
      } else {
         var5.append('Z');
      }

      return var5.toString();
   }

   public static Date parse(String var0, ParsePosition var1) throws ParseException {
      Object var2 = null;

      try {
         int var19 = var1.getIndex();
         int var20 = var19 + 4;
         int var27 = parseInt(var0, var19, var20);
         if (checkOffset(var0, var20, '-')) {
            ++var20;
         }

         var19 = var20 + 2;
         int var28 = parseInt(var0, var20, var19);
         if (checkOffset(var0, var19, '-')) {
            ++var19;
         }

         int var22 = var19 + 2;
         int var6 = parseInt(var0, var19, var22);
         int var7 = 0;
         int var8 = 0;
         int var9 = 0;
         int var10 = 0;
         boolean var11 = checkOffset(var0, var22, 'T');
         if (!var11 && var0.length() <= var22) {
            GregorianCalendar var31 = new GregorianCalendar(var27, var28 - 1, var6);
            ((Calendar)var31).setLenient(false);
            var1.setIndex(var22);
            return ((Calendar)var31).getTime();
         } else {
            if (var11) {
               ++var22;
               var19 = var22 + 2;
               var7 = parseInt(var0, var22, var19);
               if (checkOffset(var0, var19, ':')) {
                  ++var19;
               }

               var22 = var19 + 2;
               var8 = parseInt(var0, var19, var22);
               if (checkOffset(var0, var22, ':')) {
                  ++var22;
               }

               if (var0.length() > var22) {
                  char var12 = var0.charAt(var22);
                  if (var12 != 'Z' && var12 != '+' && var12 != '-') {
                     int var41 = var22;
                     var22 += 2;
                     var9 = parseInt(var0, var41, var22);
                     if (var9 > 59 && var9 < 63) {
                        var9 = 59;
                     }

                     if (checkOffset(var0, var22, '.')) {
                        ++var22;
                        int var13 = indexOfNonDigit(var0, var22 + 1);
                        int var14 = Math.min(var13, var22 + 3);
                        int var15 = parseInt(var0, var22, var14);
                        switch (var14 - var22) {
                           case 1:
                              var10 = var15 * 100;
                              break;
                           case 2:
                              var10 = var15 * 10;
                              break;
                           default:
                              var10 = var15;
                        }

                        var22 = var13;
                     }
                  }
               }
            }

            if (var0.length() <= var22) {
               throw new IllegalArgumentException("No time zone indicator");
            } else {
               Object var29 = null;
               char var32 = var0.charAt(var22);
               TimeZone var30;
               if (var32 == 'Z') {
                  var30 = TIMEZONE_UTC;
                  ++var22;
               } else {
                  if (var32 != '+' && var32 != '-') {
                     throw new IndexOutOfBoundsException("Invalid time zone indicator '" + var32 + "'");
                  }

                  String var33 = var0.substring(var22);
                  var33 = var33.length() >= 5 ? var33 : var33 + "00";
                  var22 += var33.length();
                  if (!var33.equals("+0000") && !var33.equals("+00:00")) {
                     String var36 = "GMT" + var33;
                     var30 = TimeZone.getTimeZone(var36);
                     String var16 = var30.getID();
                     if (!var16.equals(var36)) {
                        String var17 = var16.replace(":", "");
                        if (!var17.equals(var36)) {
                           throw new IndexOutOfBoundsException("Mismatching time zone indicator: " + var36 + " given, resolves to " + var30.getID());
                        }
                     }
                  } else {
                     var30 = TIMEZONE_UTC;
                  }
               }

               GregorianCalendar var35 = new GregorianCalendar(var30);
               ((Calendar)var35).setLenient(false);
               ((Calendar)var35).set(1, var27);
               ((Calendar)var35).set(2, var28 - 1);
               ((Calendar)var35).set(5, var6);
               ((Calendar)var35).set(11, var7);
               ((Calendar)var35).set(12, var8);
               ((Calendar)var35).set(13, var9);
               ((Calendar)var35).set(14, var10);
               var1.setIndex(var22);
               return ((Calendar)var35).getTime();
            }
         }
      } catch (IllegalArgumentException | IndexOutOfBoundsException var18) {
         String var3 = var0 == null ? null : '"' + var0 + '"';
         String var4 = ((Exception)var18).getMessage();
         if (var4 == null || var4.isEmpty()) {
            var4 = "(" + var18.getClass().getName() + ")";
         }

         ParseException var5 = new ParseException("Failed to parse date [" + var3 + "]: " + var4, var1.getIndex());
         var5.initCause(var18);
         throw var5;
      }
   }

   private static boolean checkOffset(String var0, int var1, char var2) {
      return var1 < var0.length() && var0.charAt(var1) == var2;
   }

   private static int parseInt(String var0, int var1, int var2) throws NumberFormatException {
      if (var1 >= 0 && var2 <= var0.length() && var1 <= var2) {
         int var3 = var1;
         int var4 = 0;
         if (var1 < var2) {
            var3 = var1 + 1;
            int var5 = Character.digit(var0.charAt(var1), 10);
            if (var5 < 0) {
               throw new NumberFormatException("Invalid number: " + var0.substring(var1, var2));
            }

            var4 = -var5;
         }

         while(var3 < var2) {
            int var7 = Character.digit(var0.charAt(var3++), 10);
            if (var7 < 0) {
               throw new NumberFormatException("Invalid number: " + var0.substring(var1, var2));
            }

            var4 *= 10;
            var4 -= var7;
         }

         return -var4;
      } else {
         throw new NumberFormatException(var0);
      }
   }

   private static void padInt(StringBuilder var0, int var1, int var2) {
      String var3 = Integer.toString(var1);

      for(int var4 = var2 - var3.length(); var4 > 0; --var4) {
         var0.append('0');
      }

      var0.append(var3);
   }

   private static int indexOfNonDigit(String var0, int var1) {
      for(int var2 = var1; var2 < var0.length(); ++var2) {
         char var3 = var0.charAt(var2);
         if (var3 < '0' || var3 > '9') {
            return var2;
         }
      }

      return var0.length();
   }
}
