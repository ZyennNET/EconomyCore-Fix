package com.google.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class UnsafeAllocator {
   public static final UnsafeAllocator INSTANCE = create();

   public abstract <T> T newInstance(Class<T> var1) throws Exception;

   private static void assertInstantiable(Class<?> var0) {
      String var1 = ConstructorConstructor.checkInstantiable(var0);
      if (var1 != null) {
         throw new AssertionError("UnsafeAllocator is used for non-instantiable type: " + var1);
      }
   }

   private static UnsafeAllocator create() {
      try {
         Class var8 = Class.forName("sun.misc.Unsafe");
         Field var9 = var8.getDeclaredField("theUnsafe");
         var9.setAccessible(true);
         final Object var10 = var9.get((Object)null);
         final Method var3 = var8.getMethod("allocateInstance", Class.class);
         return new UnsafeAllocator() {
            public <T> T newInstance(Class<T> var1) throws Exception {
               UnsafeAllocator.assertInstantiable(var1);
               return (T)var3.invoke(var10, var1);
            }
         };
      } catch (Exception var6) {
         try {
            Method var7 = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", Class.class);
            var7.setAccessible(true);
            final int var1 = (Integer)var7.invoke((Object)null, Object.class);
            final Method var2 = ObjectStreamClass.class.getDeclaredMethod("newInstance", Class.class, Integer.TYPE);
            var2.setAccessible(true);
            return new UnsafeAllocator() {
               public <T> T newInstance(Class<T> var1x) throws Exception {
                  UnsafeAllocator.assertInstantiable(var1x);
                  return (T)var2.invoke((Object)null, var1x, var1);
               }
            };
         } catch (Exception var5) {
            try {
               final Method var0 = ObjectInputStream.class.getDeclaredMethod("newInstance", Class.class, Class.class);
               var0.setAccessible(true);
               return new UnsafeAllocator() {
                  public <T> T newInstance(Class<T> var1) throws Exception {
                     UnsafeAllocator.assertInstantiable(var1);
                     return (T)var0.invoke((Object)null, var1, Object.class);
                  }
               };
            } catch (Exception var4) {
               return new UnsafeAllocator() {
                  public <T> T newInstance(Class<T> var1) {
                     throw new UnsupportedOperationException("Cannot allocate " + var1 + ". Usage of JDK sun.misc.Unsafe is enabled, but it could not be used. Make sure your runtime is configured correctly.");
                  }
               };
            }
         }
      }
   }
}
