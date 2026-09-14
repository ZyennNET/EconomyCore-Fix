package com.google.gson.internal;

import java.lang.reflect.Type;

public final class Primitives {
   private Primitives() {
   }

   public static boolean isPrimitive(Type var0) {
      return var0 instanceof Class && ((Class)var0).isPrimitive();
   }

   public static boolean isWrapperType(Type var0) {
      return var0 == Integer.class || var0 == Float.class || var0 == Byte.class || var0 == Double.class || var0 == Long.class || var0 == Character.class || var0 == Boolean.class || var0 == Short.class || var0 == Void.class;
   }

   public static <T> Class<T> wrap(Class<T> var0) {
      if (var0 == Integer.TYPE) {
         return Integer.class;
      } else if (var0 == Float.TYPE) {
         return Float.class;
      } else if (var0 == Byte.TYPE) {
         return Byte.class;
      } else if (var0 == Double.TYPE) {
         return Double.class;
      } else if (var0 == Long.TYPE) {
         return Long.class;
      } else if (var0 == Character.TYPE) {
         return Character.class;
      } else if (var0 == Boolean.TYPE) {
         return Boolean.class;
      } else if (var0 == Short.TYPE) {
         return Short.class;
      } else {
         return var0 == Void.TYPE ? Void.class : var0;
      }
   }

   public static <T> Class<T> unwrap(Class<T> var0) {
      if (var0 == Integer.class) {
         return Integer.TYPE;
      } else if (var0 == Float.class) {
         return Float.TYPE;
      } else if (var0 == Byte.class) {
         return Byte.TYPE;
      } else if (var0 == Double.class) {
         return Double.TYPE;
      } else if (var0 == Long.class) {
         return Long.TYPE;
      } else if (var0 == Character.class) {
         return Character.TYPE;
      } else if (var0 == Boolean.class) {
         return Boolean.TYPE;
      } else if (var0 == Short.class) {
         return Short.TYPE;
      } else {
         return var0 == Void.class ? Void.TYPE : var0;
      }
   }
}
