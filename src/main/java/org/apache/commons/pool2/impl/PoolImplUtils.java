package org.apache.commons.pool2.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.apache.commons.pool2.PooledObjectFactory;

class PoolImplUtils {
   static Class<?> getFactoryType(Class<? extends PooledObjectFactory> var0) {
      Class var1 = PooledObjectFactory.class;
      Object var2 = getGenericType(var1, var0);
      if (var2 instanceof Integer) {
         ParameterizedType var3 = getParameterizedType(var1, var0);
         if (var3 != null) {
            Type[] var4 = ((TypeVariable)var3.getActualTypeArguments()[(Integer)var2]).getBounds();
            if (var4 != null && var4.length > 0) {
               Type var5 = var4[0];
               if (var5 instanceof Class) {
                  return (Class)var5;
               }
            }
         }

         return Object.class;
      } else {
         return (Class)var2;
      }
   }

   private static <T> Object getGenericType(Class<T> var0, Class<? extends T> var1) {
      if (var0 != null && var1 != null) {
         ParameterizedType var2 = getParameterizedType(var0, var1);
         if (var2 != null) {
            return getTypeParameter(var1, var2.getActualTypeArguments()[0]);
         } else {
            Class var3 = var1.getSuperclass();
            Object var4 = getGenericType(var0, var3);
            if (var4 instanceof Class) {
               return var4;
            } else if (var4 instanceof Integer) {
               ParameterizedType var5 = (ParameterizedType)var1.getGenericSuperclass();
               return getTypeParameter(var1, var5.getActualTypeArguments()[(Integer)var4]);
            } else {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   private static <T> ParameterizedType getParameterizedType(Class<T> var0, Class<? extends T> var1) {
      for(Type var5 : var1.getGenericInterfaces()) {
         if (var5 instanceof ParameterizedType) {
            ParameterizedType var6 = (ParameterizedType)var5;
            if (var6.getRawType() instanceof Class && var0.isAssignableFrom((Class)var6.getRawType())) {
               return var6;
            }
         }
      }

      return null;
   }

   private static Object getTypeParameter(Class<?> var0, Type var1) {
      if (var1 instanceof Class) {
         return var1;
      } else {
         TypeVariable[] var2 = var0.getTypeParameters();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].equals(var1)) {
               return var3;
            }
         }

         return null;
      }
   }

   static boolean isPositive(Duration var0) {
      return var0 != null && !var0.isNegative() && !var0.isZero();
   }

   static Instant max(Instant var0, Instant var1) {
      return var0.compareTo(var1) > 0 ? var0 : var1;
   }

   static Instant min(Instant var0, Instant var1) {
      return var0.compareTo(var1) < 0 ? var0 : var1;
   }

   static Duration nonNull(Duration var0, Duration var1) {
      return var0 != null ? var0 : (Duration)Objects.requireNonNull(var1, "defaultValue");
   }

   static ChronoUnit toChronoUnit(TimeUnit var0) {
      switch ((TimeUnit)Objects.requireNonNull(var0)) {
         case NANOSECONDS:
            return ChronoUnit.NANOS;
         case MICROSECONDS:
            return ChronoUnit.MICROS;
         case MILLISECONDS:
            return ChronoUnit.MILLIS;
         case SECONDS:
            return ChronoUnit.SECONDS;
         case MINUTES:
            return ChronoUnit.MINUTES;
         case HOURS:
            return ChronoUnit.HOURS;
         case DAYS:
            return ChronoUnit.DAYS;
         default:
            throw new IllegalArgumentException(var0.toString());
      }
   }

   static Duration toDuration(long var0, TimeUnit var2) {
      return Duration.of(var0, toChronoUnit(var2));
   }
}
