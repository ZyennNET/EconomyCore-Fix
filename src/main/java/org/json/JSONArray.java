package org.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JSONArray implements Iterable<Object> {
   private final ArrayList<Object> myArrayList;

   public JSONArray() {
      this.myArrayList = new ArrayList();
   }

   public JSONArray(JSONTokener var1) throws JSONException {
      this();
      if (var1.nextClean() != '[') {
         throw var1.syntaxError("A JSONArray text must start with '['");
      } else {
         char var2 = var1.nextClean();
         if (var2 == 0) {
            throw var1.syntaxError("Expected a ',' or ']'");
         } else if (var2 != ']') {
            var1.back();

            while(true) {
               if (var1.nextClean() == ',') {
                  var1.back();
                  this.myArrayList.add(JSONObject.NULL);
               } else {
                  var1.back();
                  this.myArrayList.add(var1.nextValue());
               }

               switch (var1.nextClean()) {
                  case '\u0000':
                     throw var1.syntaxError("Expected a ',' or ']'");
                  case ',':
                     var2 = var1.nextClean();
                     if (var2 == 0) {
                        throw var1.syntaxError("Expected a ',' or ']'");
                     }

                     if (var2 == ']') {
                        return;
                     }

                     var1.back();
                     break;
                  case ']':
                     return;
                  default:
                     throw var1.syntaxError("Expected a ',' or ']'");
               }
            }
         }
      }
   }

   public JSONArray(String var1) throws JSONException {
      this(new JSONTokener(var1));
   }

   public JSONArray(Collection<?> var1) {
      if (var1 == null) {
         this.myArrayList = new ArrayList();
      } else {
         this.myArrayList = new ArrayList(var1.size());
         this.addAll(var1, true);
      }

   }

   public JSONArray(Iterable<?> var1) {
      this();
      if (var1 != null) {
         this.addAll(var1, true);
      }
   }

   public JSONArray(JSONArray var1) {
      if (var1 == null) {
         this.myArrayList = new ArrayList();
      } else {
         this.myArrayList = new ArrayList(var1.myArrayList);
      }

   }

   public JSONArray(Object var1) throws JSONException {
      this();
      if (!var1.getClass().isArray()) {
         throw new JSONException("JSONArray initial value should be a string or collection or array.");
      } else {
         this.addAll(var1, true);
      }
   }

   public JSONArray(int var1) throws JSONException {
      if (var1 < 0) {
         throw new JSONException("JSONArray initial capacity cannot be negative.");
      } else {
         this.myArrayList = new ArrayList(var1);
      }
   }

   public Iterator<Object> iterator() {
      return this.myArrayList.iterator();
   }

   public Object get(int var1) throws JSONException {
      Object var2 = this.opt(var1);
      if (var2 == null) {
         throw new JSONException("JSONArray[" + var1 + "] not found.");
      } else {
         return var2;
      }
   }

   public boolean getBoolean(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (!var2.equals(Boolean.FALSE) && (!(var2 instanceof String) || !((String)var2).equalsIgnoreCase("false"))) {
         if (!var2.equals(Boolean.TRUE) && (!(var2 instanceof String) || !((String)var2).equalsIgnoreCase("true"))) {
            throw wrongValueFormatException(var1, "boolean", var2, (Throwable)null);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public double getDouble(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof Number) {
         return ((Number)var2).doubleValue();
      } else {
         try {
            return Double.parseDouble(var2.toString());
         } catch (Exception var4) {
            throw wrongValueFormatException(var1, "double", var2, var4);
         }
      }
   }

   public float getFloat(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof Number) {
         return ((Number)var2).floatValue();
      } else {
         try {
            return Float.parseFloat(var2.toString());
         } catch (Exception var4) {
            throw wrongValueFormatException(var1, "float", var2, var4);
         }
      }
   }

   public Number getNumber(int var1) throws JSONException {
      Object var2 = this.get(var1);

      try {
         return var2 instanceof Number ? (Number)var2 : JSONObject.stringToNumber(var2.toString());
      } catch (Exception var4) {
         throw wrongValueFormatException(var1, "number", var2, var4);
      }
   }

   public <E extends Enum<E>> E getEnum(Class<E> var1, int var2) throws JSONException {
      Enum var3 = this.optEnum(var1, var2);
      if (var3 == null) {
         throw wrongValueFormatException(var2, "enum of type " + JSONObject.quote(var1.getSimpleName()), this.opt(var2), (Throwable)null);
      } else {
         return (E)var3;
      }
   }

   public BigDecimal getBigDecimal(int var1) throws JSONException {
      Object var2 = this.get(var1);
      BigDecimal var3 = JSONObject.objectToBigDecimal(var2, (BigDecimal)null);
      if (var3 == null) {
         throw wrongValueFormatException(var1, "BigDecimal", var2, (Throwable)null);
      } else {
         return var3;
      }
   }

   public BigInteger getBigInteger(int var1) throws JSONException {
      Object var2 = this.get(var1);
      BigInteger var3 = JSONObject.objectToBigInteger(var2, (BigInteger)null);
      if (var3 == null) {
         throw wrongValueFormatException(var1, "BigInteger", var2, (Throwable)null);
      } else {
         return var3;
      }
   }

   public int getInt(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof Number) {
         return ((Number)var2).intValue();
      } else {
         try {
            return Integer.parseInt(var2.toString());
         } catch (Exception var4) {
            throw wrongValueFormatException(var1, "int", var2, var4);
         }
      }
   }

   public JSONArray getJSONArray(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof JSONArray) {
         return (JSONArray)var2;
      } else {
         throw wrongValueFormatException(var1, "JSONArray", var2, (Throwable)null);
      }
   }

   public JSONObject getJSONObject(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof JSONObject) {
         return (JSONObject)var2;
      } else {
         throw wrongValueFormatException(var1, "JSONObject", var2, (Throwable)null);
      }
   }

   public long getLong(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof Number) {
         return ((Number)var2).longValue();
      } else {
         try {
            return Long.parseLong(var2.toString());
         } catch (Exception var4) {
            throw wrongValueFormatException(var1, "long", var2, var4);
         }
      }
   }

   public String getString(int var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof String) {
         return (String)var2;
      } else {
         throw wrongValueFormatException(var1, "String", var2, (Throwable)null);
      }
   }

   public boolean isNull(int var1) {
      return JSONObject.NULL.equals(this.opt(var1));
   }

   public String join(String var1) throws JSONException {
      int var2 = this.length();
      if (var2 == 0) {
         return "";
      } else {
         StringBuilder var3 = new StringBuilder(JSONObject.valueToString(this.myArrayList.get(0)));

         for(int var4 = 1; var4 < var2; ++var4) {
            var3.append(var1).append(JSONObject.valueToString(this.myArrayList.get(var4)));
         }

         return var3.toString();
      }
   }

   public int length() {
      return this.myArrayList.size();
   }

   public void clear() {
      this.myArrayList.clear();
   }

   public Object opt(int var1) {
      return var1 >= 0 && var1 < this.length() ? this.myArrayList.get(var1) : null;
   }

   public boolean optBoolean(int var1) {
      return this.optBoolean(var1, false);
   }

   public boolean optBoolean(int var1, boolean var2) {
      try {
         return this.getBoolean(var1);
      } catch (Exception var4) {
         return var2;
      }
   }

   public Boolean optBooleanObject(int var1) {
      return this.optBooleanObject(var1, false);
   }

   public Boolean optBooleanObject(int var1, Boolean var2) {
      try {
         return this.getBoolean(var1);
      } catch (Exception var4) {
         return var2;
      }
   }

   public double optDouble(int var1) {
      return this.optDouble(var1, Double.NaN);
   }

   public double optDouble(int var1, double var2) {
      Number var4 = this.optNumber(var1, (Number)null);
      if (var4 == null) {
         return var2;
      } else {
         double var5 = var4.doubleValue();
         return var5;
      }
   }

   public Double optDoubleObject(int var1) {
      return this.optDoubleObject(var1, Double.NaN);
   }

   public Double optDoubleObject(int var1, Double var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      if (var3 == null) {
         return var2;
      } else {
         Double var4 = var3.doubleValue();
         return var4;
      }
   }

   public float optFloat(int var1) {
      return this.optFloat(var1, Float.NaN);
   }

   public float optFloat(int var1, float var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      if (var3 == null) {
         return var2;
      } else {
         float var4 = var3.floatValue();
         return var4;
      }
   }

   public Float optFloatObject(int var1) {
      return this.optFloatObject(var1, Float.NaN);
   }

   public Float optFloatObject(int var1, Float var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      if (var3 == null) {
         return var2;
      } else {
         Float var4 = var3.floatValue();
         return var4;
      }
   }

   public int optInt(int var1) {
      return this.optInt(var1, 0);
   }

   public int optInt(int var1, int var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      return var3 == null ? var2 : var3.intValue();
   }

   public Integer optIntegerObject(int var1) {
      return this.optIntegerObject(var1, 0);
   }

   public Integer optIntegerObject(int var1, Integer var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      return var3 == null ? var2 : var3.intValue();
   }

   public <E extends Enum<E>> E optEnum(Class<E> var1, int var2) {
      return (E)this.optEnum(var1, var2, (Enum)null);
   }

   public <E extends Enum<E>> E optEnum(Class<E> var1, int var2, E var3) {
      try {
         Object var4 = this.opt(var2);
         if (JSONObject.NULL.equals(var4)) {
            return (E)var3;
         } else if (var1.isAssignableFrom(var4.getClass())) {
            Enum var5 = (Enum)var4;
            return (E)var5;
         } else {
            return (E)Enum.valueOf(var1, var4.toString());
         }
      } catch (IllegalArgumentException var6) {
         return (E)var3;
      } catch (NullPointerException var7) {
         return (E)var3;
      }
   }

   public BigInteger optBigInteger(int var1, BigInteger var2) {
      Object var3 = this.opt(var1);
      return JSONObject.objectToBigInteger(var3, var2);
   }

   public BigDecimal optBigDecimal(int var1, BigDecimal var2) {
      Object var3 = this.opt(var1);
      return JSONObject.objectToBigDecimal(var3, var2);
   }

   public JSONArray optJSONArray(int var1) {
      return this.optJSONArray(var1, (JSONArray)null);
   }

   public JSONArray optJSONArray(int var1, JSONArray var2) {
      Object var3 = this.opt(var1);
      return var3 instanceof JSONArray ? (JSONArray)var3 : var2;
   }

   public JSONObject optJSONObject(int var1) {
      return this.optJSONObject(var1, (JSONObject)null);
   }

   public JSONObject optJSONObject(int var1, JSONObject var2) {
      Object var3 = this.opt(var1);
      return var3 instanceof JSONObject ? (JSONObject)var3 : var2;
   }

   public long optLong(int var1) {
      return this.optLong(var1, 0L);
   }

   public long optLong(int var1, long var2) {
      Number var4 = this.optNumber(var1, (Number)null);
      return var4 == null ? var2 : var4.longValue();
   }

   public Long optLongObject(int var1) {
      return this.optLongObject(var1, 0L);
   }

   public Long optLongObject(int var1, Long var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      return var3 == null ? var2 : var3.longValue();
   }

   public Number optNumber(int var1) {
      return this.optNumber(var1, (Number)null);
   }

   public Number optNumber(int var1, Number var2) {
      Object var3 = this.opt(var1);
      if (JSONObject.NULL.equals(var3)) {
         return var2;
      } else if (var3 instanceof Number) {
         return (Number)var3;
      } else if (var3 instanceof String) {
         try {
            return JSONObject.stringToNumber((String)var3);
         } catch (Exception var5) {
            return var2;
         }
      } else {
         return var2;
      }
   }

   public String optString(int var1) {
      return this.optString(var1, "");
   }

   public String optString(int var1, String var2) {
      Object var3 = this.opt(var1);
      return JSONObject.NULL.equals(var3) ? var2 : var3.toString();
   }

   public JSONArray put(boolean var1) {
      return this.put(var1 ? Boolean.TRUE : Boolean.FALSE);
   }

   public JSONArray put(Collection<?> var1) {
      return this.put(new JSONArray(var1));
   }

   public JSONArray put(double var1) throws JSONException {
      return this.put(var1);
   }

   public JSONArray put(float var1) throws JSONException {
      return this.put(var1);
   }

   public JSONArray put(int var1) {
      return this.put(var1);
   }

   public JSONArray put(long var1) {
      return this.put(var1);
   }

   public JSONArray put(Map<?, ?> var1) {
      return this.put(new JSONObject(var1));
   }

   public JSONArray put(Object var1) {
      JSONObject.testValidity(var1);
      this.myArrayList.add(var1);
      return this;
   }

   public JSONArray put(int var1, boolean var2) throws JSONException {
      return this.put(var1, var2 ? Boolean.TRUE : Boolean.FALSE);
   }

   public JSONArray put(int var1, Collection<?> var2) throws JSONException {
      return this.put(var1, new JSONArray(var2));
   }

   public JSONArray put(int var1, double var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONArray put(int var1, float var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONArray put(int var1, int var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONArray put(int var1, long var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONArray put(int var1, Map<?, ?> var2) throws JSONException {
      this.put(var1, new JSONObject(var2));
      return this;
   }

   public JSONArray put(int var1, Object var2) throws JSONException {
      if (var1 < 0) {
         throw new JSONException("JSONArray[" + var1 + "] not found.");
      } else if (var1 < this.length()) {
         JSONObject.testValidity(var2);
         this.myArrayList.set(var1, var2);
         return this;
      } else if (var1 == this.length()) {
         return this.put(var2);
      } else {
         this.myArrayList.ensureCapacity(var1 + 1);

         while(var1 != this.length()) {
            this.myArrayList.add(JSONObject.NULL);
         }

         return this.put(var2);
      }
   }

   public JSONArray putAll(Collection<?> var1) {
      this.addAll(var1, false);
      return this;
   }

   public JSONArray putAll(Iterable<?> var1) {
      this.addAll(var1, false);
      return this;
   }

   public JSONArray putAll(JSONArray var1) {
      this.myArrayList.addAll(var1.myArrayList);
      return this;
   }

   public JSONArray putAll(Object var1) throws JSONException {
      this.addAll(var1, false);
      return this;
   }

   public Object query(String var1) {
      return this.query(new JSONPointer(var1));
   }

   public Object query(JSONPointer var1) {
      return var1.queryFrom(this);
   }

   public Object optQuery(String var1) {
      return this.optQuery(new JSONPointer(var1));
   }

   public Object optQuery(JSONPointer var1) {
      try {
         return var1.queryFrom(this);
      } catch (JSONPointerException var3) {
         return null;
      }
   }

   public Object remove(int var1) {
      return var1 >= 0 && var1 < this.length() ? this.myArrayList.remove(var1) : null;
   }

   public boolean similar(Object var1) {
      if (!(var1 instanceof JSONArray)) {
         return false;
      } else {
         int var2 = this.length();
         if (var2 != ((JSONArray)var1).length()) {
            return false;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               Object var4 = this.myArrayList.get(var3);
               Object var5 = ((JSONArray)var1).myArrayList.get(var3);
               if (var4 != var5) {
                  if (var4 == null) {
                     return false;
                  }

                  if (var4 instanceof JSONObject) {
                     if (!((JSONObject)var4).similar(var5)) {
                        return false;
                     }
                  } else if (var4 instanceof JSONArray) {
                     if (!((JSONArray)var4).similar(var5)) {
                        return false;
                     }
                  } else if (var4 instanceof Number && var5 instanceof Number) {
                     if (!JSONObject.isNumberSimilar((Number)var4, (Number)var5)) {
                        return false;
                     }
                  } else if (var4 instanceof JSONString && var5 instanceof JSONString) {
                     if (!((JSONString)var4).toJSONString().equals(((JSONString)var5).toJSONString())) {
                        return false;
                     }
                  } else if (!var4.equals(var5)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public JSONObject toJSONObject(JSONArray var1) throws JSONException {
      if (var1 != null && !var1.isEmpty() && !this.isEmpty()) {
         JSONObject var2 = new JSONObject(var1.length());

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            var2.put(var1.getString(var3), this.opt(var3));
         }

         return var2;
      } else {
         return null;
      }
   }

   public String toString() {
      try {
         return this.toString(0);
      } catch (Exception var2) {
         return null;
      }
   }

   public String toString(int var1) throws JSONException {
      StringWriter var2 = new StringWriter();
      return this.write(var2, var1, 0).toString();
   }

   public Writer write(Writer var1) throws JSONException {
      return this.write(var1, 0, 0);
   }

   public Writer write(Writer var1, int var2, int var3) throws JSONException {
      try {
         boolean var4 = false;
         int var5 = this.length();
         var1.write(91);
         if (var5 == 1) {
            try {
               JSONObject.writeValue(var1, this.myArrayList.get(0), var2, var3);
            } catch (Exception var10) {
               throw new JSONException("Unable to write JSONArray value at index: 0", var10);
            }
         } else if (var5 != 0) {
            int var6 = var3 + var2;

            for(int var7 = 0; var7 < var5; ++var7) {
               if (var4) {
                  var1.write(44);
               }

               if (var2 > 0) {
                  var1.write(10);
               }

               JSONObject.indent(var1, var6);

               try {
                  JSONObject.writeValue(var1, this.myArrayList.get(var7), var2, var6);
               } catch (Exception var9) {
                  throw new JSONException("Unable to write JSONArray value at index: " + var7, var9);
               }

               var4 = true;
            }

            if (var2 > 0) {
               var1.write(10);
            }

            JSONObject.indent(var1, var3);
         }

         var1.write(93);
         return var1;
      } catch (IOException var11) {
         throw new JSONException(var11);
      }
   }

   public List<Object> toList() {
      ArrayList var1 = new ArrayList(this.myArrayList.size());

      for(Object var3 : this.myArrayList) {
         if (var3 != null && !JSONObject.NULL.equals(var3)) {
            if (var3 instanceof JSONArray) {
               var1.add(((JSONArray)var3).toList());
            } else if (var3 instanceof JSONObject) {
               var1.add(((JSONObject)var3).toMap());
            } else {
               var1.add(var3);
            }
         } else {
            var1.add((Object)null);
         }
      }

      return var1;
   }

   public boolean isEmpty() {
      return this.myArrayList.isEmpty();
   }

   private void addAll(Collection<?> var1, boolean var2) {
      this.myArrayList.ensureCapacity(this.myArrayList.size() + var1.size());
      if (var2) {
         for(Object var4 : var1) {
            this.put(JSONObject.wrap(var4));
         }
      } else {
         for(Object var6 : var1) {
            this.put(var6);
         }
      }

   }

   private void addAll(Iterable<?> var1, boolean var2) {
      if (var2) {
         for(Object var4 : var1) {
            this.put(JSONObject.wrap(var4));
         }
      } else {
         for(Object var6 : var1) {
            this.put(var6);
         }
      }

   }

   private void addAll(Object var1, boolean var2) throws JSONException {
      if (var1.getClass().isArray()) {
         int var3 = Array.getLength(var1);
         this.myArrayList.ensureCapacity(this.myArrayList.size() + var3);
         if (var2) {
            for(int var4 = 0; var4 < var3; ++var4) {
               this.put(JSONObject.wrap(Array.get(var1, var4)));
            }
         } else {
            for(int var5 = 0; var5 < var3; ++var5) {
               this.put(Array.get(var1, var5));
            }
         }
      } else if (var1 instanceof JSONArray) {
         this.myArrayList.addAll(((JSONArray)var1).myArrayList);
      } else if (var1 instanceof Collection) {
         this.addAll((Collection)var1, var2);
      } else {
         if (!(var1 instanceof Iterable)) {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
         }

         this.addAll((Iterable)var1, var2);
      }

   }

   private static JSONException wrongValueFormatException(int var0, String var1, Object var2, Throwable var3) {
      if (var2 == null) {
         return new JSONException("JSONArray[" + var0 + "] is not a " + var1 + " (null).", var3);
      } else {
         return !(var2 instanceof Map) && !(var2 instanceof Iterable) && !(var2 instanceof JSONObject) ? new JSONException("JSONArray[" + var0 + "] is not a " + var1 + " (" + var2.getClass() + " : " + var2 + ").", var3) : new JSONException("JSONArray[" + var0 + "] is not a " + var1 + " (" + var2.getClass() + ").", var3);
      }
   }
}
