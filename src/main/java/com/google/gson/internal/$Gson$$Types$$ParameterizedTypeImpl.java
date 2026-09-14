package com.google.gson.internal;

import [Ljava.lang.reflect.Type;;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

final class $Gson$$Types$$ParameterizedTypeImpl implements ParameterizedType, Serializable {
   private final Type ownerType;
   private final Type rawType;
   private final Type[] typeArguments;
   private static final long serialVersionUID = 0L;

   public $Gson$$Types$$ParameterizedTypeImpl(Type var1, Type var2, Type... var3) {
      Objects.requireNonNull(var2);
      if (var1 == null && $Gson$$Types.requiresOwnerType(var2)) {
         throw new IllegalArgumentException("Must specify owner type for " + var2);
      } else {
         this.ownerType = var1 == null ? null : $Gson$$Types.canonicalize(var1);
         this.rawType = $Gson$$Types.canonicalize(var2);
         this.typeArguments = (Type[])((Type;)var3).clone();
         int var4 = 0;

         for(int var5 = this.typeArguments.length; var4 < var5; ++var4) {
            Objects.requireNonNull(this.typeArguments[var4]);
            $Gson$$Types.checkNotPrimitive(this.typeArguments[var4]);
            this.typeArguments[var4] = $Gson$$Types.canonicalize(this.typeArguments[var4]);
         }

      }
   }

   public Type[] getActualTypeArguments() {
      return (Type[])this.typeArguments.clone();
   }

   public Type getRawType() {
      return this.rawType;
   }

   public Type getOwnerType() {
      return this.ownerType;
   }

   public boolean equals(Object var1) {
      return var1 instanceof ParameterizedType && $Gson$$Types.equals(this, (ParameterizedType)var1);
   }

   private static int hashCodeOrZero(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }

   public int hashCode() {
      return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ hashCodeOrZero(this.ownerType);
   }

   public String toString() {
      int var1 = this.typeArguments.length;
      if (var1 == 0) {
         return $Gson$$Types.typeToString(this.rawType);
      } else {
         StringBuilder var2 = new StringBuilder(30 * (var1 + 1));
         var2.append($Gson$$Types.typeToString(this.rawType)).append("<").append($Gson$$Types.typeToString(this.typeArguments[0]));

         for(int var3 = 1; var3 < var1; ++var3) {
            var2.append(", ").append($Gson$$Types.typeToString(this.typeArguments[var3]));
         }

         return var2.append(">").toString();
      }
   }
}
