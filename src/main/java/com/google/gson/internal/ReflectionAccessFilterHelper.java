package com.google.gson.internal;

import com.google.gson.ReflectionAccessFilter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectionAccessFilterHelper {
   private ReflectionAccessFilterHelper() {
   }

   public static boolean isJavaType(Class<?> var0) {
      return isJavaType(var0.getName());
   }

   private static boolean isJavaType(String var0) {
      return var0.startsWith("java.") || var0.startsWith("javax.");
   }

   public static boolean isAndroidType(Class<?> var0) {
      return isAndroidType(var0.getName());
   }

   private static boolean isAndroidType(String var0) {
      return var0.startsWith("android.") || var0.startsWith("androidx.") || isJavaType(var0);
   }

   public static boolean isAnyPlatformType(Class<?> var0) {
      String var1 = var0.getName();
      return isAndroidType(var1) || var1.startsWith("kotlin.") || var1.startsWith("kotlinx.") || var1.startsWith("scala.");
   }

   public static ReflectionAccessFilter.FilterResult getFilterResult(List<ReflectionAccessFilter> var0, Class<?> var1) {
      for(ReflectionAccessFilter var3 : var0) {
         ReflectionAccessFilter.FilterResult var4 = var3.check(var1);
         if (var4 != ReflectionAccessFilter.FilterResult.INDECISIVE) {
            return var4;
         }
      }

      return ReflectionAccessFilter.FilterResult.ALLOW;
   }

   public static boolean canAccess(AccessibleObject var0, Object var1) {
      return ReflectionAccessFilterHelper.AccessChecker.INSTANCE.canAccess(var0, var1);
   }

   private abstract static class AccessChecker {
      public static final AccessChecker INSTANCE;

      private AccessChecker() {
      }

      public abstract boolean canAccess(AccessibleObject var1, Object var2);

      static {
         AccessChecker var0 = null;
         if (JavaVersion.isJava9OrLater()) {
            try {
               final Method var1 = AccessibleObject.class.getDeclaredMethod("canAccess", Object.class);
               var0 = new AccessChecker() {
                  public boolean canAccess(AccessibleObject var1x, Object var2) {
                     try {
                        return (Boolean)var1.invoke(var1x, var2);
                     } catch (Exception var4) {
                        throw new RuntimeException("Failed invoking canAccess", var4);
                     }
                  }
               };
            } catch (NoSuchMethodException var2) {
            }
         }

         if (var0 == null) {
            var0 = new AccessChecker() {
               public boolean canAccess(AccessibleObject var1, Object var2) {
                  return true;
               }
            };
         }

         INSTANCE = var0;
      }
   }
}
