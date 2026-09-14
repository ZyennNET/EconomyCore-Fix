package com.google.gson.reflect;

import com.google.gson.internal.$Gson$$Types;
import com.google.gson.internal.TroubleshootingGuide;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypeToken<T> {
   private final Class<? super T> rawType;
   private final Type type;
   private final int hashCode;

   protected TypeToken() {
      this.type = this.getTypeTokenTypeArgument();
      this.rawType = $Gson$$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   private TypeToken(Type var1) {
      this.type = $Gson$$Types.canonicalize((Type)Objects.requireNonNull(var1));
      this.rawType = $Gson$$Types.getRawType(this.type);
      this.hashCode = this.type.hashCode();
   }

   private static boolean isCapturingTypeVariablesForbidden() {
      return !Objects.equals(System.getProperty("gson.allowCapturingTypeVariables"), "true");
   }

   private Type getTypeTokenTypeArgument() {
      Type var1 = this.getClass().getGenericSuperclass();
      if (var1 instanceof ParameterizedType) {
         ParameterizedType var2 = (ParameterizedType)var1;
         if (var2.getRawType() == TypeToken.class) {
            Type var3 = $Gson$$Types.canonicalize(var2.getActualTypeArguments()[0]);
            if (isCapturingTypeVariablesForbidden()) {
               verifyNoTypeVariable(var3);
            }

            return var3;
         }
      } else if (var1 == TypeToken.class) {
         throw new IllegalStateException("TypeToken must be created with a type argument: new TypeToken<...>() {}; When using code shrinkers (ProGuard, R8, ...) make sure that generic signatures are preserved.\nSee " + TroubleshootingGuide.createUrl("type-token-raw"));
      }

      throw new IllegalStateException("Must only create direct subclasses of TypeToken");
   }

   private static void verifyNoTypeVariable(Type var0) {
      if (var0 instanceof TypeVariable) {
         TypeVariable var8 = (TypeVariable)var0;
         throw new IllegalArgumentException("TypeToken type argument must not contain a type variable; captured type variable " + var8.getName() + " declared by " + var8.getGenericDeclaration() + "\nSee " + TroubleshootingGuide.createUrl("typetoken-type-variable"));
      } else {
         if (var0 instanceof GenericArrayType) {
            verifyNoTypeVariable(((GenericArrayType)var0).getGenericComponentType());
         } else if (var0 instanceof ParameterizedType) {
            ParameterizedType var1 = (ParameterizedType)var0;
            Type var2 = var1.getOwnerType();
            if (var2 != null) {
               verifyNoTypeVariable(var2);
            }

            for(Type var6 : var1.getActualTypeArguments()) {
               verifyNoTypeVariable(var6);
            }
         } else if (var0 instanceof WildcardType) {
            WildcardType var7 = (WildcardType)var0;

            for(Type var15 : var7.getLowerBounds()) {
               verifyNoTypeVariable(var15);
            }

            for(Type var16 : var7.getUpperBounds()) {
               verifyNoTypeVariable(var16);
            }
         } else if (var0 == null) {
            throw new IllegalArgumentException("TypeToken captured `null` as type argument; probably a compiler / runtime bug");
         }

      }
   }

   public final Class<? super T> getRawType() {
      return this.rawType;
   }

   public final Type getType() {
      return this.type;
   }

   @Deprecated
   public boolean isAssignableFrom(Class<?> var1) {
      return this.isAssignableFrom(var1);
   }

   @Deprecated
   public boolean isAssignableFrom(Type var1) {
      if (var1 == null) {
         return false;
      } else if (this.type.equals(var1)) {
         return true;
      } else if (this.type instanceof Class) {
         return this.rawType.isAssignableFrom($Gson$$Types.getRawType(var1));
      } else if (this.type instanceof ParameterizedType) {
         return isAssignableFrom(var1, (ParameterizedType)this.type, new HashMap());
      } else if (!(this.type instanceof GenericArrayType)) {
         throw buildUnsupportedTypeException(this.type, Class.class, ParameterizedType.class, GenericArrayType.class);
      } else {
         return this.rawType.isAssignableFrom($Gson$$Types.getRawType(var1)) && isAssignableFrom(var1, (GenericArrayType)this.type);
      }
   }

   @Deprecated
   public boolean isAssignableFrom(TypeToken<?> var1) {
      return this.isAssignableFrom(var1.getType());
   }

   private static boolean isAssignableFrom(Type var0, GenericArrayType var1) {
      Type var2 = var1.getGenericComponentType();
      if (!(var2 instanceof ParameterizedType)) {
         return true;
      } else {
         Object var3 = var0;
         if (var0 instanceof GenericArrayType) {
            var3 = ((GenericArrayType)var0).getGenericComponentType();
         } else if (var0 instanceof Class) {
            Class var4;
            for(var4 = (Class)var0; var4.isArray(); var4 = var4.getComponentType()) {
            }

            var3 = var4;
         }

         return isAssignableFrom((Type)var3, (ParameterizedType)var2, new HashMap());
      }
   }

   private static boolean isAssignableFrom(Type var0, ParameterizedType var1, Map<String, Type> var2) {
      if (var0 == null) {
         return false;
      } else if (var1.equals(var0)) {
         return true;
      } else {
         Class var3 = $Gson$$Types.getRawType(var0);
         ParameterizedType var4 = null;
         if (var0 instanceof ParameterizedType) {
            var4 = (ParameterizedType)var0;
         }

         if (var4 != null) {
            Type[] var5 = var4.getActualTypeArguments();
            TypeVariable[] var6 = var3.getTypeParameters();

            for(int var7 = 0; var7 < var5.length; ++var7) {
               Type var8 = var5[var7];

               TypeVariable var9;
               TypeVariable var10;
               for(var9 = var6[var7]; var8 instanceof TypeVariable; var8 = (Type)var2.get(var10.getName())) {
                  var10 = (TypeVariable)var8;
               }

               var2.put(var9.getName(), var8);
            }

            if (typeEquals(var4, var1, var2)) {
               return true;
            }
         }

         for(Type var15 : var3.getGenericInterfaces()) {
            if (isAssignableFrom(var15, var1, new HashMap(var2))) {
               return true;
            }
         }

         Type var12 = var3.getGenericSuperclass();
         return isAssignableFrom(var12, var1, new HashMap(var2));
      }
   }

   private static boolean typeEquals(ParameterizedType var0, ParameterizedType var1, Map<String, Type> var2) {
      if (var0.getRawType().equals(var1.getRawType())) {
         Type[] var3 = var0.getActualTypeArguments();
         Type[] var4 = var1.getActualTypeArguments();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (!matches(var3[var5], var4[var5], var2)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static IllegalArgumentException buildUnsupportedTypeException(Type var0, Class<?>... var1) {
      StringBuilder var2 = new StringBuilder("Unsupported type, expected one of: ");

      for(Class var6 : var1) {
         var2.append(var6.getName()).append(", ");
      }

      var2.append("but got: ").append(var0.getClass().getName()).append(", for type token: ").append(var0.toString());
      return new IllegalArgumentException(var2.toString());
   }

   private static boolean matches(Type var0, Type var1, Map<String, Type> var2) {
      return var1.equals(var0) || var0 instanceof TypeVariable && var1.equals(var2.get(((TypeVariable)var0).getName()));
   }

   public final int hashCode() {
      return this.hashCode;
   }

   public final boolean equals(Object var1) {
      return var1 instanceof TypeToken && $Gson$$Types.equals(this.type, ((TypeToken)var1).type);
   }

   public final String toString() {
      return $Gson$$Types.typeToString(this.type);
   }

   public static TypeToken<?> get(Type var0) {
      return new TypeToken(var0);
   }

   public static <T> TypeToken<T> get(Class<T> var0) {
      return new TypeToken<T>(var0);
   }

   public static TypeToken<?> getParameterized(Type var0, Type... var1) {
      Objects.requireNonNull(var0);
      Objects.requireNonNull(var1);
      if (!(var0 instanceof Class)) {
         throw new IllegalArgumentException("rawType must be of type Class, but was " + var0);
      } else {
         Class var2 = (Class)var0;
         TypeVariable[] var3 = var2.getTypeParameters();
         int var4 = var3.length;
         int var5 = var1.length;
         if (var5 != var4) {
            throw new IllegalArgumentException(var2.getName() + " requires " + var4 + " type arguments, but got " + var5);
         } else if (var1.length == 0) {
            return get(var2);
         } else if ($Gson$$Types.requiresOwnerType(var0)) {
            throw new IllegalArgumentException("Raw type " + var2.getName() + " is not supported because it requires specifying an owner type");
         } else {
            for(int var6 = 0; var6 < var4; ++var6) {
               Type var7 = (Type)Objects.requireNonNull(var1[var6], "Type argument must not be null");
               Class var8 = $Gson$$Types.getRawType(var7);
               TypeVariable var9 = var3[var6];

               for(Type var13 : var9.getBounds()) {
                  Class var14 = $Gson$$Types.getRawType(var13);
                  if (!var14.isAssignableFrom(var8)) {
                     throw new IllegalArgumentException("Type argument " + var7 + " does not satisfy bounds for type variable " + var9 + " declared by " + var0);
                  }
               }
            }

            return new TypeToken($Gson$$Types.newParameterizedTypeWithOwner((Type)null, var0, var1));
         }
      }
   }

   public static TypeToken<?> getArray(Type var0) {
      return new TypeToken($Gson$$Types.arrayOf(var0));
   }
}
