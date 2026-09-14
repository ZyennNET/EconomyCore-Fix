package com.google.gson;

import com.google.gson.internal.ReflectionAccessFilterHelper;

public interface ReflectionAccessFilter {
   ReflectionAccessFilter BLOCK_INACCESSIBLE_JAVA = new ReflectionAccessFilter() {
      public FilterResult check(Class<?> var1) {
         return ReflectionAccessFilterHelper.isJavaType(var1) ? ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      public String toString() {
         return "ReflectionAccessFilter#BLOCK_INACCESSIBLE_JAVA";
      }
   };
   ReflectionAccessFilter BLOCK_ALL_JAVA = new ReflectionAccessFilter() {
      public FilterResult check(Class<?> var1) {
         return ReflectionAccessFilterHelper.isJavaType(var1) ? ReflectionAccessFilter.FilterResult.BLOCK_ALL : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      public String toString() {
         return "ReflectionAccessFilter#BLOCK_ALL_JAVA";
      }
   };
   ReflectionAccessFilter BLOCK_ALL_ANDROID = new ReflectionAccessFilter() {
      public FilterResult check(Class<?> var1) {
         return ReflectionAccessFilterHelper.isAndroidType(var1) ? ReflectionAccessFilter.FilterResult.BLOCK_ALL : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      public String toString() {
         return "ReflectionAccessFilter#BLOCK_ALL_ANDROID";
      }
   };
   ReflectionAccessFilter BLOCK_ALL_PLATFORM = new ReflectionAccessFilter() {
      public FilterResult check(Class<?> var1) {
         return ReflectionAccessFilterHelper.isAnyPlatformType(var1) ? ReflectionAccessFilter.FilterResult.BLOCK_ALL : ReflectionAccessFilter.FilterResult.INDECISIVE;
      }

      public String toString() {
         return "ReflectionAccessFilter#BLOCK_ALL_PLATFORM";
      }
   };

   FilterResult check(Class<?> var1);

   public static enum FilterResult {
      ALLOW,
      INDECISIVE,
      BLOCK_INACCESSIBLE,
      BLOCK_ALL;
   }
}
