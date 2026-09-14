package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

final class $Gson$$Types$$GenericArrayTypeImpl implements GenericArrayType, Serializable {
   private final Type componentType;
   private static final long serialVersionUID = 0L;

   public $Gson$$Types$$GenericArrayTypeImpl(Type var1) {
      Objects.requireNonNull(var1);
      this.componentType = $Gson$$Types.canonicalize(var1);
   }

   public Type getGenericComponentType() {
      return this.componentType;
   }

   public boolean equals(Object var1) {
      return var1 instanceof GenericArrayType && $Gson$$Types.equals(this, (GenericArrayType)var1);
   }

   public int hashCode() {
      return this.componentType.hashCode();
   }

   public String toString() {
      return $Gson$$Types.typeToString(this.componentType) + "[]";
   }
}
