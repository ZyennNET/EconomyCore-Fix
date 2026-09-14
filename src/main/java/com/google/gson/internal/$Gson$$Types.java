package com.google.gson.internal;

import [Ljava.lang.reflect.Type;;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;

public final class $Gson$$Types {
   static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

   private $Gson$$Types() {
      throw new UnsupportedOperationException();
   }

   public static ParameterizedType newParameterizedTypeWithOwner(Type var0, Type var1, Type... var2) {
      return new $Gson$$Types$$ParameterizedTypeImpl(var0, var1, var2);
   }

   public static GenericArrayType arrayOf(Type var0) {
      return new $Gson$$Types$$GenericArrayTypeImpl(var0);
   }

   public static WildcardType subtypeOf(Type var0) {
      Type[] var1;
      if (var0 instanceof WildcardType) {
         var1 = ((WildcardType)var0).getUpperBounds();
      } else {
         var1 = new Type[]{var0};
      }

      return new $Gson$$Types$$WildcardTypeImpl(var1, EMPTY_TYPE_ARRAY);
   }

   public static WildcardType supertypeOf(Type var0) {
      Type[] var1;
      if (var0 instanceof WildcardType) {
         var1 = ((WildcardType)var0).getLowerBounds();
      } else {
         var1 = new Type[]{var0};
      }

      return new $Gson$$Types$$WildcardTypeImpl(new Type[]{Object.class}, var1);
   }

   public static Type canonicalize(Type var0) {
      if (var0 instanceof Class) {
         Class var4 = (Class)var0;
         return (Type)(var4.isArray() ? new $Gson$$Types$$GenericArrayTypeImpl(canonicalize(var4.getComponentType())) : var4);
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var3 = (ParameterizedType)var0;
         return new $Gson$$Types$$ParameterizedTypeImpl(var3.getOwnerType(), var3.getRawType(), var3.getActualTypeArguments());
      } else if (var0 instanceof GenericArrayType) {
         GenericArrayType var2 = (GenericArrayType)var0;
         return new $Gson$$Types$$GenericArrayTypeImpl(var2.getGenericComponentType());
      } else if (var0 instanceof WildcardType) {
         WildcardType var1 = (WildcardType)var0;
         return new $Gson$$Types$$WildcardTypeImpl(var1.getUpperBounds(), var1.getLowerBounds());
      } else {
         return var0;
      }
   }

   public static Class<?> getRawType(Type var0) {
      if (var0 instanceof Class) {
         return (Class)var0;
      } else if (var0 instanceof ParameterizedType) {
         ParameterizedType var5 = (ParameterizedType)var0;
         Type var2 = var5.getRawType();
         $Gson$$Preconditions.checkArgument(var2 instanceof Class);
         return (Class)var2;
      } else if (var0 instanceof GenericArrayType) {
         Type var4 = ((GenericArrayType)var0).getGenericComponentType();
         return Array.newInstance(getRawType(var4), 0).getClass();
      } else if (var0 instanceof TypeVariable) {
         return Object.class;
      } else if (var0 instanceof WildcardType) {
         Type[] var3 = ((WildcardType)var0).getUpperBounds();

         assert var3.length == 1;

         return getRawType(var3[0]);
      } else {
         String var1 = var0 == null ? "null" : var0.getClass().getName();
         throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + var0 + "> is of type " + var1);
      }
   }

   private static boolean equal(Object var0, Object var1) {
      return Objects.equals(var0, var1);
   }

   public static boolean equals(Type var0, Type var1) {
      if (var0 == var1) {
         return true;
      } else if (var0 instanceof Class) {
         return var0.equals(var1);
      } else if (var0 instanceof ParameterizedType) {
         if (!(var1 instanceof ParameterizedType)) {
            return false;
         } else {
            ParameterizedType var6 = (ParameterizedType)var0;
            ParameterizedType var9 = (ParameterizedType)var1;
            return equal(var6.getOwnerType(), var9.getOwnerType()) && var6.getRawType().equals(var9.getRawType()) && Arrays.equals(var6.getActualTypeArguments(), var9.getActualTypeArguments());
         }
      } else if (var0 instanceof GenericArrayType) {
         if (!(var1 instanceof GenericArrayType)) {
            return false;
         } else {
            GenericArrayType var5 = (GenericArrayType)var0;
            GenericArrayType var8 = (GenericArrayType)var1;
            return equals(var5.getGenericComponentType(), var8.getGenericComponentType());
         }
      } else if (var0 instanceof WildcardType) {
         if (!(var1 instanceof WildcardType)) {
            return false;
         } else {
            WildcardType var4 = (WildcardType)var0;
            WildcardType var7 = (WildcardType)var1;
            return Arrays.equals(var4.getUpperBounds(), var7.getUpperBounds()) && Arrays.equals(var4.getLowerBounds(), var7.getLowerBounds());
         }
      } else if (var0 instanceof TypeVariable) {
         if (!(var1 instanceof TypeVariable)) {
            return false;
         } else {
            TypeVariable var2 = (TypeVariable)var0;
            TypeVariable var3 = (TypeVariable)var1;
            return Objects.equals(var2.getGenericDeclaration(), var3.getGenericDeclaration()) && var2.getName().equals(var3.getName());
         }
      } else {
         return false;
      }
   }

   public static String typeToString(Type var0) {
      return var0 instanceof Class ? ((Class)var0).getName() : var0.toString();
   }

   private static Type getGenericSupertype(Type var0, Class<?> var1, Class<?> var2) {
      if (var2 == var1) {
         return var0;
      } else {
         if (var2.isInterface()) {
            Class[] var3 = var1.getInterfaces();
            int var4 = 0;

            for(int var5 = var3.length; var4 < var5; ++var4) {
               if (var3[var4] == var2) {
                  return var1.getGenericInterfaces()[var4];
               }

               if (var2.isAssignableFrom(var3[var4])) {
                  return getGenericSupertype(var1.getGenericInterfaces()[var4], var3[var4], var2);
               }
            }
         }

         if (!var1.isInterface()) {
            while(var1 != Object.class) {
               Class var6 = var1.getSuperclass();
               if (var6 == var2) {
                  return var1.getGenericSuperclass();
               }

               if (var2.isAssignableFrom(var6)) {
                  return getGenericSupertype(var1.getGenericSuperclass(), var6, var2);
               }

               var1 = var6;
            }
         }

         return var2;
      }
   }

   private static Type getSupertype(Type var0, Class<?> var1, Class<?> var2) {
      if (var0 instanceof WildcardType) {
         Type[] var3 = ((WildcardType)var0).getUpperBounds();

         assert var3.length == 1;

         var0 = var3[0];
      }

      $Gson$$Preconditions.checkArgument(var2.isAssignableFrom(var1));
      return resolve(var0, var1, getGenericSupertype(var0, var1, var2));
   }

   public static Type getArrayComponentType(Type var0) {
      return (Type)(var0 instanceof GenericArrayType ? ((GenericArrayType)var0).getGenericComponentType() : ((Class)var0).getComponentType());
   }

   public static Type getCollectionElementType(Type var0, Class<?> var1) {
      Type var2 = getSupertype(var0, var1, Collection.class);
      return (Type)(var2 instanceof ParameterizedType ? ((ParameterizedType)var2).getActualTypeArguments()[0] : Object.class);
   }

   public static Type[] getMapKeyAndValueTypes(Type var0, Class<?> var1) {
      if (var0 == Properties.class) {
         return new Type[]{String.class, String.class};
      } else {
         Type var2 = getSupertype(var0, var1, Map.class);
         if (var2 instanceof ParameterizedType) {
            ParameterizedType var3 = (ParameterizedType)var2;
            return var3.getActualTypeArguments();
         } else {
            return new Type[]{Object.class, Object.class};
         }
      }
   }

   public static Type resolve(Type var0, Class<?> var1, Type var2) {
      return resolve(var0, var1, var2, new HashMap());
   }

   private static Type resolve(Type var0, Class<?> var1, Type var2, Map<TypeVariable<?>, Type> var3) {
      TypeVariable var4 = null;

      while(true) {
         if (var2 instanceof TypeVariable) {
            TypeVariable var5 = (TypeVariable)var2;
            Type var6 = (Type)var3.get(var5);
            if (var6 != null) {
               return (Type)(var6 == Void.TYPE ? var2 : var6);
            }

            var3.put(var5, Void.TYPE);
            if (var4 == null) {
               var4 = var5;
            }

            var2 = resolveTypeVariable(var0, var1, var5);
            if (var2 != var5) {
               continue;
            }
         } else if (var2 instanceof Class && ((Class)var2).isArray()) {
            Class var16 = (Class)var2;
            Class var20 = var16.getComponentType();
            Type var23 = resolve(var0, var1, var20, var3);
            var2 = equal(var20, var23) ? var16 : arrayOf(var23);
         } else if (var2 instanceof GenericArrayType) {
            GenericArrayType var13 = (GenericArrayType)var2;
            Type var17 = var13.getGenericComponentType();
            Type var7 = resolve(var0, var1, var17, var3);
            var2 = equal(var17, var7) ? var13 : arrayOf(var7);
         } else if (var2 instanceof ParameterizedType) {
            ParameterizedType var14 = (ParameterizedType)var2;
            Type var18 = var14.getOwnerType();
            Type var21 = resolve(var0, var1, var18, var3);
            boolean var8 = !equal(var21, var18);
            Type[] var9 = var14.getActualTypeArguments();
            int var10 = 0;

            for(int var11 = var9.length; var10 < var11; ++var10) {
               Type var12 = resolve(var0, var1, var9[var10], var3);
               if (!equal(var12, var9[var10])) {
                  if (!var8) {
                     var9 = (Type[])((Type;)var9).clone();
                     var8 = true;
                  }

                  var9[var10] = var12;
               }
            }

            var2 = var8 ? newParameterizedTypeWithOwner(var21, var14.getRawType(), var9) : var14;
         } else if (var2 instanceof WildcardType) {
            label82: {
               WildcardType var15 = (WildcardType)var2;
               Type[] var19 = var15.getLowerBounds();
               Type[] var22 = var15.getUpperBounds();
               if (var19.length == 1) {
                  Type var24 = resolve(var0, var1, var19[0], var3);
                  if (var24 != var19[0]) {
                     var2 = supertypeOf(var24);
                     break label82;
                  }
               } else if (var22.length == 1) {
                  Type var25 = resolve(var0, var1, var22[0], var3);
                  if (var25 != var22[0]) {
                     var2 = subtypeOf(var25);
                     break label82;
                  }
               }

               var2 = var15;
            }
         }

         if (var4 != null) {
            var3.put(var4, var2);
         }

         return (Type)var2;
      }
   }

   private static Type resolveTypeVariable(Type var0, Class<?> var1, TypeVariable<?> var2) {
      Class var3 = declaringClassOf(var2);
      if (var3 == null) {
         return var2;
      } else {
         Type var4 = getGenericSupertype(var0, var1, var3);
         if (var4 instanceof ParameterizedType) {
            int var5 = indexOf(var3.getTypeParameters(), var2);
            return ((ParameterizedType)var4).getActualTypeArguments()[var5];
         } else {
            return var2;
         }
      }
   }

   private static int indexOf(Object[] var0, Object var1) {
      int var2 = 0;

      for(int var3 = var0.length; var2 < var3; ++var2) {
         if (var1.equals(var0[var2])) {
            return var2;
         }
      }

      throw new NoSuchElementException();
   }

   private static Class<?> declaringClassOf(TypeVariable<?> var0) {
      GenericDeclaration var1 = var0.getGenericDeclaration();
      return var1 instanceof Class ? (Class)var1 : null;
   }

   static void checkNotPrimitive(Type var0) {
      $Gson$$Preconditions.checkArgument(!(var0 instanceof Class) || !((Class)var0).isPrimitive());
   }

   public static boolean requiresOwnerType(Type var0) {
      if (!(var0 instanceof Class)) {
         return false;
      } else {
         Class var1 = (Class)var0;
         return !Modifier.isStatic(var1.getModifiers()) && var1.getDeclaringClass() != null;
      }
   }
}
