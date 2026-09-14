package com.google.gson.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.math.BigDecimal;

public final class LazilyParsedNumber extends Number {
   private final String value;

   public LazilyParsedNumber(String var1) {
      this.value = var1;
   }

   private BigDecimal asBigDecimal() {
      return NumberLimits.parseBigDecimal(this.value);
   }

   public int intValue() {
      try {
         return Integer.parseInt(this.value);
      } catch (NumberFormatException var4) {
         try {
            return (int)Long.parseLong(this.value);
         } catch (NumberFormatException var3) {
            return this.asBigDecimal().intValue();
         }
      }
   }

   public long longValue() {
      try {
         return Long.parseLong(this.value);
      } catch (NumberFormatException var2) {
         return this.asBigDecimal().longValue();
      }
   }

   public float floatValue() {
      return Float.parseFloat(this.value);
   }

   public double doubleValue() {
      return Double.parseDouble(this.value);
   }

   public String toString() {
      return this.value;
   }

   private Object writeReplace() throws ObjectStreamException {
      return this.asBigDecimal();
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      throw new InvalidObjectException("Deserialization is unsupported");
   }

   public int hashCode() {
      return this.value.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof LazilyParsedNumber) {
         LazilyParsedNumber var2 = (LazilyParsedNumber)var1;
         return this.value.equals(var2.value);
      } else {
         return false;
      }
   }
}
