package com.google.gson.internal;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Objects;

final class $Gson$$Types$$WildcardTypeImpl implements WildcardType, Serializable {
   private final Type upperBound;
   private final Type lowerBound;
   private static final long serialVersionUID = 0L;

   public $Gson$$Types$$WildcardTypeImpl(Type[] var1, Type[] var2) {
      $Gson$$Preconditions.checkArgument(var2.length <= 1);
      $Gson$$Preconditions.checkArgument(var1.length == 1);
      if (var2.length == 1) {
         Objects.requireNonNull(var2[0]);
         $Gson$$Types.checkNotPrimitive(var2[0]);
         $Gson$$Preconditions.checkArgument(var1[0] == Object.class);
         this.lowerBound = $Gson$$Types.canonicalize(var2[0]);
         this.upperBound = Object.class;
      } else {
         Objects.requireNonNull(var1[0]);
         $Gson$$Types.checkNotPrimitive(var1[0]);
         this.lowerBound = null;
         this.upperBound = $Gson$$Types.canonicalize(var1[0]);
      }

   }

   public Type[] getUpperBounds() {
      return new Type[]{this.upperBound};
   }

   public Type[] getLowerBounds() {
      return this.lowerBound != null ? new Type[]{this.lowerBound} : $Gson$$Types.EMPTY_TYPE_ARRAY;
   }

   public boolean equals(Object var1) {
      return var1 instanceof WildcardType && $Gson$$Types.equals(this, (WildcardType)var1);
   }

   public int hashCode() {
      return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
   }

   public String toString() {
      if (this.lowerBound != null) {
         return "? super " + $Gson$$Types.typeToString(this.lowerBound);
      } else {
         return this.upperBound == Object.class ? "?" : "? extends " + $Gson$$Types.typeToString(this.upperBound);
      }
   }
}
