package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.internal.NonNullElementWrapperList;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable<JsonElement> {
   private final ArrayList<JsonElement> elements;

   public JsonArray() {
      this.elements = new ArrayList();
   }

   public JsonArray(int var1) {
      this.elements = new ArrayList(var1);
   }

   public JsonArray deepCopy() {
      if (this.elements.isEmpty()) {
         return new JsonArray();
      } else {
         JsonArray var1 = new JsonArray(this.elements.size());

         for(JsonElement var3 : this.elements) {
            var1.add(var3.deepCopy());
         }

         return var1;
      }
   }

   public void add(Boolean var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(Character var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(Number var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(String var1) {
      this.elements.add(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
   }

   public void add(JsonElement var1) {
      if (var1 == null) {
         var1 = JsonNull.INSTANCE;
      }

      this.elements.add(var1);
   }

   public void addAll(JsonArray var1) {
      this.elements.addAll(var1.elements);
   }

   @CanIgnoreReturnValue
   public JsonElement set(int var1, JsonElement var2) {
      return (JsonElement)this.elements.set(var1, var2 == null ? JsonNull.INSTANCE : var2);
   }

   @CanIgnoreReturnValue
   public boolean remove(JsonElement var1) {
      return this.elements.remove(var1);
   }

   @CanIgnoreReturnValue
   public JsonElement remove(int var1) {
      return (JsonElement)this.elements.remove(var1);
   }

   public boolean contains(JsonElement var1) {
      return this.elements.contains(var1);
   }

   public int size() {
      return this.elements.size();
   }

   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   public Iterator<JsonElement> iterator() {
      return this.elements.iterator();
   }

   public JsonElement get(int var1) {
      return (JsonElement)this.elements.get(var1);
   }

   private JsonElement getAsSingleElement() {
      int var1 = this.elements.size();
      if (var1 == 1) {
         return (JsonElement)this.elements.get(0);
      } else {
         throw new IllegalStateException("Array must have size 1, but has size " + var1);
      }
   }

   public Number getAsNumber() {
      return this.getAsSingleElement().getAsNumber();
   }

   public String getAsString() {
      return this.getAsSingleElement().getAsString();
   }

   public double getAsDouble() {
      return this.getAsSingleElement().getAsDouble();
   }

   public BigDecimal getAsBigDecimal() {
      return this.getAsSingleElement().getAsBigDecimal();
   }

   public BigInteger getAsBigInteger() {
      return this.getAsSingleElement().getAsBigInteger();
   }

   public float getAsFloat() {
      return this.getAsSingleElement().getAsFloat();
   }

   public long getAsLong() {
      return this.getAsSingleElement().getAsLong();
   }

   public int getAsInt() {
      return this.getAsSingleElement().getAsInt();
   }

   public byte getAsByte() {
      return this.getAsSingleElement().getAsByte();
   }

   @Deprecated
   public char getAsCharacter() {
      return this.getAsSingleElement().getAsCharacter();
   }

   public short getAsShort() {
      return this.getAsSingleElement().getAsShort();
   }

   public boolean getAsBoolean() {
      return this.getAsSingleElement().getAsBoolean();
   }

   public List<JsonElement> asList() {
      return new NonNullElementWrapperList<JsonElement>(this.elements);
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof JsonArray && ((JsonArray)var1).elements.equals(this.elements);
   }

   public int hashCode() {
      return this.elements.hashCode();
   }
}
