package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public final class JsonPrimitive extends JsonElement {
   private final Object value;

   public JsonPrimitive(Boolean var1) {
      this.value = Objects.requireNonNull(var1);
   }

   public JsonPrimitive(Number var1) {
      this.value = Objects.requireNonNull(var1);
   }

   public JsonPrimitive(String var1) {
      this.value = Objects.requireNonNull(var1);
   }

   public JsonPrimitive(Character var1) {
      this.value = ((Character)Objects.requireNonNull(var1)).toString();
   }

   public JsonPrimitive deepCopy() {
      return this;
   }

   public boolean isBoolean() {
      return this.value instanceof Boolean;
   }

   public boolean getAsBoolean() {
      return this.isBoolean() ? (Boolean)this.value : Boolean.parseBoolean(this.getAsString());
   }

   public boolean isNumber() {
      return this.value instanceof Number;
   }

   public Number getAsNumber() {
      if (this.value instanceof Number) {
         return (Number)this.value;
      } else if (this.value instanceof String) {
         return new LazilyParsedNumber((String)this.value);
      } else {
         throw new UnsupportedOperationException("Primitive is neither a number nor a string");
      }
   }

   public boolean isString() {
      return this.value instanceof String;
   }

   public String getAsString() {
      if (this.value instanceof String) {
         return (String)this.value;
      } else if (this.isNumber()) {
         return this.getAsNumber().toString();
      } else if (this.isBoolean()) {
         return ((Boolean)this.value).toString();
      } else {
         throw new AssertionError("Unexpected value type: " + this.value.getClass());
      }
   }

   public double getAsDouble() {
      return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble(this.getAsString());
   }

   public BigDecimal getAsBigDecimal() {
      return this.value instanceof BigDecimal ? (BigDecimal)this.value : NumberLimits.parseBigDecimal(this.getAsString());
   }

   public BigInteger getAsBigInteger() {
      return this.value instanceof BigInteger ? (BigInteger)this.value : (isIntegral(this) ? BigInteger.valueOf(this.getAsNumber().longValue()) : NumberLimits.parseBigInteger(this.getAsString()));
   }

   public float getAsFloat() {
      return this.isNumber() ? this.getAsNumber().floatValue() : Float.parseFloat(this.getAsString());
   }

   public long getAsLong() {
      return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong(this.getAsString());
   }

   public short getAsShort() {
      return this.isNumber() ? this.getAsNumber().shortValue() : Short.parseShort(this.getAsString());
   }

   public int getAsInt() {
      return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt(this.getAsString());
   }

   public byte getAsByte() {
      return this.isNumber() ? this.getAsNumber().byteValue() : Byte.parseByte(this.getAsString());
   }

   @Deprecated
   public char getAsCharacter() {
      String var1 = this.getAsString();
      if (var1.isEmpty()) {
         throw new UnsupportedOperationException("String value is empty");
      } else {
         return var1.charAt(0);
      }
   }

   public int hashCode() {
      if (this.value == null) {
         return 31;
      } else if (isIntegral(this)) {
         long var3 = this.getAsNumber().longValue();
         return (int)(var3 ^ var3 >>> 32);
      } else if (this.value instanceof Number) {
         long var1 = Double.doubleToLongBits(this.getAsNumber().doubleValue());
         return (int)(var1 ^ var1 >>> 32);
      } else {
         return this.value.hashCode();
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         JsonPrimitive var2 = (JsonPrimitive)var1;
         if (this.value == null) {
            return var2.value == null;
         } else if (isIntegral(this) && isIntegral(var2)) {
            return !(this.value instanceof BigInteger) && !(var2.value instanceof BigInteger) ? this.getAsNumber().longValue() == var2.getAsNumber().longValue() : this.getAsBigInteger().equals(var2.getAsBigInteger());
         } else if (this.value instanceof Number && var2.value instanceof Number) {
            if (this.value instanceof BigDecimal && var2.value instanceof BigDecimal) {
               return this.getAsBigDecimal().compareTo(var2.getAsBigDecimal()) == 0;
            } else {
               double var3 = this.getAsDouble();
               double var5 = var2.getAsDouble();
               return var3 == var5 || Double.isNaN(var3) && Double.isNaN(var5);
            }
         } else {
            return this.value.equals(var2.value);
         }
      } else {
         return false;
      }
   }

   private static boolean isIntegral(JsonPrimitive var0) {
      if (!(var0.value instanceof Number)) {
         return false;
      } else {
         Number var1 = (Number)var0.value;
         return var1 instanceof BigInteger || var1 instanceof Long || var1 instanceof Integer || var1 instanceof Short || var1 instanceof Byte;
      }
   }
}
