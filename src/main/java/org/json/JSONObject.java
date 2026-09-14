package org.json;

import java.io.Closeable;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

public class JSONObject {
   static final Pattern NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?");
   private final Map<String, Object> map;
   public static final Object NULL = new Null();

   public Class<? extends Map> getMapType() {
      return this.map.getClass();
   }

   public JSONObject() {
      this.map = new HashMap();
   }

   public JSONObject(JSONObject var1, String... var2) {
      this(var2.length);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         try {
            this.putOnce(var2[var3], var1.opt(var2[var3]));
         } catch (Exception var5) {
         }
      }

   }

   public JSONObject(JSONTokener var1) throws JSONException {
      this();
      if (var1.nextClean() != '{') {
         throw var1.syntaxError("A JSONObject text must begin with '{'");
      } else {
         while(true) {
            char var2 = var1.nextClean();
            switch (var2) {
               case '\u0000':
                  throw var1.syntaxError("A JSONObject text must end with '}'");
               case '}':
                  return;
               default:
                  String var3 = var1.nextSimpleValue(var2).toString();
                  var2 = var1.nextClean();
                  if (var2 != ':') {
                     throw var1.syntaxError("Expected a ':' after a key");
                  }

                  if (var3 != null) {
                     if (this.opt(var3) != null) {
                        throw var1.syntaxError("Duplicate key \"" + var3 + "\"");
                     }

                     Object var4 = var1.nextValue();
                     if (var4 != null) {
                        this.put(var3, var4);
                     }
                  }

                  switch (var1.nextClean()) {
                     case ',':
                     case ';':
                        if (var1.nextClean() == '}') {
                           return;
                        }

                        if (var1.end()) {
                           throw var1.syntaxError("A JSONObject text must end with '}'");
                        }

                        var1.back();
                        break;
                     case '}':
                        return;
                     default:
                        throw var1.syntaxError("Expected a ',' or '}'");
                  }
            }
         }
      }
   }

   public JSONObject(Map<?, ?> var1) {
      if (var1 == null) {
         this.map = new HashMap();
      } else {
         this.map = new HashMap(var1.size());

         for(Map.Entry var3 : var1.entrySet()) {
            if (var3.getKey() == null) {
               throw new NullPointerException("Null key.");
            }

            Object var4 = var3.getValue();
            if (var4 != null) {
               testValidity(var4);
               this.map.put(String.valueOf(var3.getKey()), wrap(var4));
            }
         }
      }

   }

   public JSONObject(Object var1) {
      this();
      this.populateMap(var1);
   }

   private JSONObject(Object var1, Set<Object> var2) {
      this();
      this.populateMap(var1, var2);
   }

   public JSONObject(Object var1, String... var2) {
      this(var2.length);
      Class var3 = var1.getClass();

      for(int var4 = 0; var4 < var2.length; ++var4) {
         String var5 = var2[var4];

         try {
            this.putOpt(var5, var3.getField(var5).get(var1));
         } catch (Exception var7) {
         }
      }

   }

   public JSONObject(String var1) throws JSONException {
      this(new JSONTokener(var1));
   }

   public JSONObject(String var1, Locale var2) throws JSONException {
      this();
      ResourceBundle var3 = ResourceBundle.getBundle(var1, var2, Thread.currentThread().getContextClassLoader());
      Enumeration var4 = var3.getKeys();

      while(var4.hasMoreElements()) {
         Object var5 = var4.nextElement();
         if (var5 != null) {
            String[] var6 = ((String)var5).split("\\.");
            int var7 = var6.length - 1;
            JSONObject var8 = this;

            for(int var9 = 0; var9 < var7; ++var9) {
               String var10 = var6[var9];
               JSONObject var11 = var8.optJSONObject(var10);
               if (var11 == null) {
                  var11 = new JSONObject();
                  var8.put(var10, var11);
               }

               var8 = var11;
            }

            var8.put(var6[var7], var3.getString((String)var5));
         }
      }

   }

   protected JSONObject(int var1) {
      this.map = new HashMap(var1);
   }

   public JSONObject accumulate(String var1, Object var2) throws JSONException {
      testValidity(var2);
      Object var3 = this.opt(var1);
      if (var3 == null) {
         this.put(var1, var2 instanceof JSONArray ? (new JSONArray()).put(var2) : var2);
      } else if (var3 instanceof JSONArray) {
         ((JSONArray)var3).put(var2);
      } else {
         this.put(var1, (new JSONArray()).put(var3).put(var2));
      }

      return this;
   }

   public JSONObject append(String var1, Object var2) throws JSONException {
      testValidity(var2);
      Object var3 = this.opt(var1);
      if (var3 == null) {
         this.put(var1, (new JSONArray()).put(var2));
      } else {
         if (!(var3 instanceof JSONArray)) {
            throw wrongValueFormatException(var1, "JSONArray", (Object)null, (Throwable)null);
         }

         this.put(var1, ((JSONArray)var3).put(var2));
      }

      return this;
   }

   public static String doubleToString(double var0) {
      if (!Double.isInfinite(var0) && !Double.isNaN(var0)) {
         String var2 = Double.toString(var0);
         if (var2.indexOf(46) > 0 && var2.indexOf(101) < 0 && var2.indexOf(69) < 0) {
            while(var2.endsWith("0")) {
               var2 = var2.substring(0, var2.length() - 1);
            }

            if (var2.endsWith(".")) {
               var2 = var2.substring(0, var2.length() - 1);
            }
         }

         return var2;
      } else {
         return "null";
      }
   }

   public Object get(String var1) throws JSONException {
      if (var1 == null) {
         throw new JSONException("Null key.");
      } else {
         Object var2 = this.opt(var1);
         if (var2 == null) {
            throw new JSONException("JSONObject[" + quote(var1) + "] not found.");
         } else {
            return var2;
         }
      }
   }

   public <E extends Enum<E>> E getEnum(Class<E> var1, String var2) throws JSONException {
      Enum var3 = this.optEnum(var1, var2);
      if (var3 == null) {
         throw wrongValueFormatException(var2, "enum of type " + quote(var1.getSimpleName()), this.opt(var2), (Throwable)null);
      } else {
         return (E)var3;
      }
   }

   public boolean getBoolean(String var1) throws JSONException {
      Object var2 = this.get(var1);
      if (!var2.equals(Boolean.FALSE) && (!(var2 instanceof String) || !((String)var2).equalsIgnoreCase("false"))) {
         if (!var2.equals(Boolean.TRUE) && (!(var2 instanceof String) || !((String)var2).equalsIgnoreCase("true"))) {
            throw wrongValueFormatException(var1, "Boolean", var2, (Throwable)null);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public BigInteger getBigInteger(String var1) throws JSONException {
      Object var2 = this.get(var1);
      BigInteger var3 = objectToBigInteger(var2, (BigInteger)null);
      if (var3 != null) {
         return var3;
      } else {
         throw wrongValueFormatException(var1, "BigInteger", var2, (Throwable)null);
      }
   }

   public BigDecimal getBigDecimal(String var1) throws JSONException {
      Object var2 = this.get(var1);
      BigDecimal var3 = objectToBigDecimal(var2, (BigDecimal)null);
      if (var3 != null) {
         return var3;
      } else {
         throw wrongValueFormatException(var1, "BigDecimal", var2, (Throwable)null);
      }
   }

   public double getDouble(String var1) throws JSONException {
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

   public float getFloat(String var1) throws JSONException {
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

   public Number getNumber(String var1) throws JSONException {
      Object var2 = this.get(var1);

      try {
         return var2 instanceof Number ? (Number)var2 : stringToNumber(var2.toString());
      } catch (Exception var4) {
         throw wrongValueFormatException(var1, "number", var2, var4);
      }
   }

   public int getInt(String var1) throws JSONException {
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

   public JSONArray getJSONArray(String var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof JSONArray) {
         return (JSONArray)var2;
      } else {
         throw wrongValueFormatException(var1, "JSONArray", var2, (Throwable)null);
      }
   }

   public JSONObject getJSONObject(String var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof JSONObject) {
         return (JSONObject)var2;
      } else {
         throw wrongValueFormatException(var1, "JSONObject", var2, (Throwable)null);
      }
   }

   public long getLong(String var1) throws JSONException {
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

   public static String[] getNames(JSONObject var0) {
      return var0.isEmpty() ? null : (String[])var0.keySet().toArray(new String[var0.length()]);
   }

   public static String[] getNames(Object var0) {
      if (var0 == null) {
         return null;
      } else {
         Class var1 = var0.getClass();
         Field[] var2 = var1.getFields();
         int var3 = var2.length;
         if (var3 == 0) {
            return null;
         } else {
            String[] var4 = new String[var3];

            for(int var5 = 0; var5 < var3; ++var5) {
               var4[var5] = var2[var5].getName();
            }

            return var4;
         }
      }
   }

   public String getString(String var1) throws JSONException {
      Object var2 = this.get(var1);
      if (var2 instanceof String) {
         return (String)var2;
      } else {
         throw wrongValueFormatException(var1, "string", var2, (Throwable)null);
      }
   }

   public boolean has(String var1) {
      return this.map.containsKey(var1);
   }

   public JSONObject increment(String var1) throws JSONException {
      Object var2 = this.opt(var1);
      if (var2 == null) {
         this.put(var1, 1);
      } else if (var2 instanceof Integer) {
         this.put(var1, (Integer)var2 + 1);
      } else if (var2 instanceof Long) {
         this.put(var1, (Long)var2 + 1L);
      } else if (var2 instanceof BigInteger) {
         this.put(var1, ((BigInteger)var2).add(BigInteger.ONE));
      } else if (var2 instanceof Float) {
         this.put(var1, (Float)var2 + 1.0F);
      } else if (var2 instanceof Double) {
         this.put(var1, (Double)var2 + (double)1.0F);
      } else {
         if (!(var2 instanceof BigDecimal)) {
            throw new JSONException("Unable to increment [" + quote(var1) + "].");
         }

         this.put(var1, ((BigDecimal)var2).add(BigDecimal.ONE));
      }

      return this;
   }

   public boolean isNull(String var1) {
      return NULL.equals(this.opt(var1));
   }

   public Iterator<String> keys() {
      return this.keySet().iterator();
   }

   public Set<String> keySet() {
      return this.map.keySet();
   }

   protected Set<Map.Entry<String, Object>> entrySet() {
      return this.map.entrySet();
   }

   public int length() {
      return this.map.size();
   }

   public void clear() {
      this.map.clear();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public JSONArray names() {
      return this.map.isEmpty() ? null : new JSONArray(this.map.keySet());
   }

   public static String numberToString(Number var0) throws JSONException {
      if (var0 == null) {
         throw new JSONException("Null pointer");
      } else {
         testValidity(var0);
         String var1 = var0.toString();
         if (var1.indexOf(46) > 0 && var1.indexOf(101) < 0 && var1.indexOf(69) < 0) {
            while(var1.endsWith("0")) {
               var1 = var1.substring(0, var1.length() - 1);
            }

            if (var1.endsWith(".")) {
               var1 = var1.substring(0, var1.length() - 1);
            }
         }

         return var1;
      }
   }

   public Object opt(String var1) {
      return var1 == null ? null : this.map.get(var1);
   }

   public <E extends Enum<E>> E optEnum(Class<E> var1, String var2) {
      return (E)this.optEnum(var1, var2, (Enum)null);
   }

   public <E extends Enum<E>> E optEnum(Class<E> var1, String var2, E var3) {
      try {
         Object var4 = this.opt(var2);
         if (NULL.equals(var4)) {
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

   public boolean optBoolean(String var1) {
      return this.optBoolean(var1, false);
   }

   public boolean optBoolean(String var1, boolean var2) {
      Object var3 = this.opt(var1);
      if (NULL.equals(var3)) {
         return var2;
      } else if (var3 instanceof Boolean) {
         return (Boolean)var3;
      } else {
         try {
            return this.getBoolean(var1);
         } catch (Exception var5) {
            return var2;
         }
      }
   }

   public Boolean optBooleanObject(String var1) {
      return this.optBooleanObject(var1, false);
   }

   public Boolean optBooleanObject(String var1, Boolean var2) {
      Object var3 = this.opt(var1);
      if (NULL.equals(var3)) {
         return var2;
      } else if (var3 instanceof Boolean) {
         return (Boolean)var3;
      } else {
         try {
            return this.getBoolean(var1);
         } catch (Exception var5) {
            return var2;
         }
      }
   }

   public BigDecimal optBigDecimal(String var1, BigDecimal var2) {
      Object var3 = this.opt(var1);
      return objectToBigDecimal(var3, var2);
   }

   static BigDecimal objectToBigDecimal(Object var0, BigDecimal var1) {
      return objectToBigDecimal(var0, var1, true);
   }

   static BigDecimal objectToBigDecimal(Object var0, BigDecimal var1, boolean var2) {
      if (NULL.equals(var0)) {
         return var1;
      } else if (var0 instanceof BigDecimal) {
         return (BigDecimal)var0;
      } else if (var0 instanceof BigInteger) {
         return new BigDecimal((BigInteger)var0);
      } else if (!(var0 instanceof Double) && !(var0 instanceof Float)) {
         if (!(var0 instanceof Long) && !(var0 instanceof Integer) && !(var0 instanceof Short) && !(var0 instanceof Byte)) {
            try {
               return new BigDecimal(var0.toString());
            } catch (Exception var4) {
               return var1;
            }
         } else {
            return new BigDecimal(((Number)var0).longValue());
         }
      } else if (!numberIsFinite((Number)var0)) {
         return var1;
      } else {
         return var2 ? new BigDecimal(((Number)var0).doubleValue()) : new BigDecimal(var0.toString());
      }
   }

   public BigInteger optBigInteger(String var1, BigInteger var2) {
      Object var3 = this.opt(var1);
      return objectToBigInteger(var3, var2);
   }

   static BigInteger objectToBigInteger(Object var0, BigInteger var1) {
      if (NULL.equals(var0)) {
         return var1;
      } else if (var0 instanceof BigInteger) {
         return (BigInteger)var0;
      } else if (var0 instanceof BigDecimal) {
         return ((BigDecimal)var0).toBigInteger();
      } else if (!(var0 instanceof Double) && !(var0 instanceof Float)) {
         if (!(var0 instanceof Long) && !(var0 instanceof Integer) && !(var0 instanceof Short) && !(var0 instanceof Byte)) {
            try {
               String var2 = var0.toString();
               return isDecimalNotation(var2) ? (new BigDecimal(var2)).toBigInteger() : new BigInteger(var2);
            } catch (Exception var3) {
               return var1;
            }
         } else {
            return BigInteger.valueOf(((Number)var0).longValue());
         }
      } else {
         return !numberIsFinite((Number)var0) ? var1 : (new BigDecimal(((Number)var0).doubleValue())).toBigInteger();
      }
   }

   public double optDouble(String var1) {
      return this.optDouble(var1, Double.NaN);
   }

   public double optDouble(String var1, double var2) {
      Number var4 = this.optNumber(var1);
      return var4 == null ? var2 : var4.doubleValue();
   }

   public Double optDoubleObject(String var1) {
      return this.optDoubleObject(var1, Double.NaN);
   }

   public Double optDoubleObject(String var1, Double var2) {
      Number var3 = this.optNumber(var1);
      return var3 == null ? var2 : var3.doubleValue();
   }

   public float optFloat(String var1) {
      return this.optFloat(var1, Float.NaN);
   }

   public float optFloat(String var1, float var2) {
      Number var3 = this.optNumber(var1);
      if (var3 == null) {
         return var2;
      } else {
         float var4 = var3.floatValue();
         return var4;
      }
   }

   public Float optFloatObject(String var1) {
      return this.optFloatObject(var1, Float.NaN);
   }

   public Float optFloatObject(String var1, Float var2) {
      Number var3 = this.optNumber(var1);
      if (var3 == null) {
         return var2;
      } else {
         Float var4 = var3.floatValue();
         return var4;
      }
   }

   public int optInt(String var1) {
      return this.optInt(var1, 0);
   }

   public int optInt(String var1, int var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      return var3 == null ? var2 : var3.intValue();
   }

   public Integer optIntegerObject(String var1) {
      return this.optIntegerObject(var1, 0);
   }

   public Integer optIntegerObject(String var1, Integer var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      return var3 == null ? var2 : var3.intValue();
   }

   public JSONArray optJSONArray(String var1) {
      return this.optJSONArray(var1, (JSONArray)null);
   }

   public JSONArray optJSONArray(String var1, JSONArray var2) {
      Object var3 = this.opt(var1);
      return var3 instanceof JSONArray ? (JSONArray)var3 : var2;
   }

   public JSONObject optJSONObject(String var1) {
      return this.optJSONObject(var1, (JSONObject)null);
   }

   public JSONObject optJSONObject(String var1, JSONObject var2) {
      Object var3 = this.opt(var1);
      return var3 instanceof JSONObject ? (JSONObject)var3 : var2;
   }

   public long optLong(String var1) {
      return this.optLong(var1, 0L);
   }

   public long optLong(String var1, long var2) {
      Number var4 = this.optNumber(var1, (Number)null);
      return var4 == null ? var2 : var4.longValue();
   }

   public Long optLongObject(String var1) {
      return this.optLongObject(var1, 0L);
   }

   public Long optLongObject(String var1, Long var2) {
      Number var3 = this.optNumber(var1, (Number)null);
      return var3 == null ? var2 : var3.longValue();
   }

   public Number optNumber(String var1) {
      return this.optNumber(var1, (Number)null);
   }

   public Number optNumber(String var1, Number var2) {
      Object var3 = this.opt(var1);
      if (NULL.equals(var3)) {
         return var2;
      } else if (var3 instanceof Number) {
         return (Number)var3;
      } else {
         try {
            return stringToNumber(var3.toString());
         } catch (Exception var5) {
            return var2;
         }
      }
   }

   public String optString(String var1) {
      return this.optString(var1, "");
   }

   public String optString(String var1, String var2) {
      Object var3 = this.opt(var1);
      return NULL.equals(var3) ? var2 : var3.toString();
   }

   private void populateMap(Object var1) {
      this.populateMap(var1, Collections.newSetFromMap(new IdentityHashMap()));
   }

   private void populateMap(Object var1, Set<Object> var2) {
      Class var3 = var1.getClass();
      boolean var4 = var3.getClassLoader() != null;
      Method[] var5 = var4 ? var3.getMethods() : var3.getDeclaredMethods();

      for(Method var9 : var5) {
         int var10 = var9.getModifiers();
         if (Modifier.isPublic(var10) && !Modifier.isStatic(var10) && var9.getParameterTypes().length == 0 && !var9.isBridge() && var9.getReturnType() != Void.TYPE && isValidMethodName(var9.getName())) {
            String var11 = getKeyNameFromMethod(var9);
            if (var11 != null && !var11.isEmpty()) {
               try {
                  Object var12 = var9.invoke(var1);
                  if (var12 != null) {
                     if (var2.contains(var12)) {
                        throw recursivelyDefinedObjectException(var11);
                     }

                     var2.add(var12);
                     testValidity(var12);
                     this.map.put(var11, wrap(var12, var2));
                     var2.remove(var12);
                     if (var12 instanceof Closeable) {
                        try {
                           ((Closeable)var12).close();
                        } catch (IOException var14) {
                        }
                     }
                  }
               } catch (IllegalAccessException var15) {
               } catch (IllegalArgumentException var16) {
               } catch (InvocationTargetException var17) {
               }
            }
         }
      }

   }

   private static boolean isValidMethodName(String var0) {
      return !"getClass".equals(var0) && !"getDeclaringClass".equals(var0);
   }

   private static String getKeyNameFromMethod(Method var0) {
      int var1 = getAnnotationDepth(var0, JSONPropertyIgnore.class);
      if (var1 > 0) {
         int var2 = getAnnotationDepth(var0, JSONPropertyName.class);
         if (var2 < 0 || var1 <= var2) {
            return null;
         }
      }

      JSONPropertyName var5 = (JSONPropertyName)getAnnotation(var0, JSONPropertyName.class);
      if (var5 != null && var5.value() != null && !var5.value().isEmpty()) {
         return var5.value();
      } else {
         String var4 = var0.getName();
         String var3;
         if (var4.startsWith("get") && var4.length() > 3) {
            var3 = var4.substring(3);
         } else {
            if (!var4.startsWith("is") || var4.length() <= 2) {
               return null;
            }

            var3 = var4.substring(2);
         }

         if (var3.length() != 0 && !Character.isLowerCase(var3.charAt(0))) {
            if (var3.length() == 1) {
               var3 = var3.toLowerCase(Locale.ROOT);
            } else if (!Character.isUpperCase(var3.charAt(1))) {
               var3 = var3.substring(0, 1).toLowerCase(Locale.ROOT) + var3.substring(1);
            }

            return var3;
         } else {
            return null;
         }
      }
   }

   private static <A extends Annotation> A getAnnotation(Method var0, Class<A> var1) {
      if (var0 != null && var1 != null) {
         if (var0.isAnnotationPresent(var1)) {
            return (A)var0.getAnnotation(var1);
         } else {
            Class var2 = var0.getDeclaringClass();
            if (var2.getSuperclass() == null) {
               return null;
            } else {
               for(Class var6 : var2.getInterfaces()) {
                  try {
                     Method var7 = var6.getMethod(var0.getName(), var0.getParameterTypes());
                     return (A)getAnnotation(var7, var1);
                  } catch (SecurityException var10) {
                  } catch (NoSuchMethodException var11) {
                  }
               }

               try {
                  return (A)getAnnotation(var2.getSuperclass().getMethod(var0.getName(), var0.getParameterTypes()), var1);
               } catch (SecurityException var8) {
                  return null;
               } catch (NoSuchMethodException var9) {
                  return null;
               }
            }
         }
      } else {
         return null;
      }
   }

   private static int getAnnotationDepth(Method var0, Class<? extends Annotation> var1) {
      if (var0 != null && var1 != null) {
         if (var0.isAnnotationPresent(var1)) {
            return 1;
         } else {
            Class var2 = var0.getDeclaringClass();
            if (var2.getSuperclass() == null) {
               return -1;
            } else {
               for(Class var6 : var2.getInterfaces()) {
                  try {
                     Method var7 = var6.getMethod(var0.getName(), var0.getParameterTypes());
                     int var8 = getAnnotationDepth(var7, var1);
                     if (var8 > 0) {
                        return var8 + 1;
                     }
                  } catch (SecurityException var11) {
                  } catch (NoSuchMethodException var12) {
                  }
               }

               try {
                  int var13 = getAnnotationDepth(var2.getSuperclass().getMethod(var0.getName(), var0.getParameterTypes()), var1);
                  return var13 > 0 ? var13 + 1 : -1;
               } catch (SecurityException var9) {
                  return -1;
               } catch (NoSuchMethodException var10) {
                  return -1;
               }
            }
         }
      } else {
         return -1;
      }
   }

   public JSONObject put(String var1, boolean var2) throws JSONException {
      return this.put(var1, var2 ? Boolean.TRUE : Boolean.FALSE);
   }

   public JSONObject put(String var1, Collection<?> var2) throws JSONException {
      return this.put(var1, new JSONArray(var2));
   }

   public JSONObject put(String var1, double var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONObject put(String var1, float var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONObject put(String var1, int var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONObject put(String var1, long var2) throws JSONException {
      return this.put(var1, var2);
   }

   public JSONObject put(String var1, Map<?, ?> var2) throws JSONException {
      return this.put(var1, new JSONObject(var2));
   }

   public JSONObject put(String var1, Object var2) throws JSONException {
      if (var1 == null) {
         throw new NullPointerException("Null key.");
      } else {
         if (var2 != null) {
            testValidity(var2);
            this.map.put(var1, var2);
         } else {
            this.remove(var1);
         }

         return this;
      }
   }

   public JSONObject putOnce(String var1, Object var2) throws JSONException {
      if (var1 != null && var2 != null) {
         if (this.opt(var1) != null) {
            throw new JSONException("Duplicate key \"" + var1 + "\"");
         } else {
            return this.put(var1, var2);
         }
      } else {
         return this;
      }
   }

   public JSONObject putOpt(String var1, Object var2) throws JSONException {
      return var1 != null && var2 != null ? this.put(var1, var2) : this;
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

   public static String quote(String var0) {
      StringWriter var1 = new StringWriter();

      try {
         return quote(var0, var1).toString();
      } catch (IOException var3) {
         return "";
      }
   }

   public static Writer quote(String var0, Writer var1) throws IOException {
      if (var0 != null && !var0.isEmpty()) {
         char var3 = 0;
         int var6 = var0.length();
         var1.write(34);

         for(int var5 = 0; var5 < var6; ++var5) {
            char var2 = var3;
            var3 = var0.charAt(var5);
            switch (var3) {
               case '\b':
                  var1.write("\\b");
                  break;
               case '\t':
                  var1.write("\\t");
                  break;
               case '\n':
                  var1.write("\\n");
                  break;
               case '\f':
                  var1.write("\\f");
                  break;
               case '\r':
                  var1.write("\\r");
                  break;
               case '"':
               case '\\':
                  var1.write(92);
                  var1.write(var3);
                  break;
               case '/':
                  if (var2 == '<') {
                     var1.write(92);
                  }

                  var1.write(var3);
                  break;
               default:
                  if (var3 >= ' ' && (var3 < 128 || var3 >= 160) && (var3 < 8192 || var3 >= 8448)) {
                     var1.write(var3);
                  } else {
                     var1.write("\\u");
                     String var4 = Integer.toHexString(var3);
                     var1.write("0000", 0, 4 - var4.length());
                     var1.write(var4);
                  }
            }
         }

         var1.write(34);
         return var1;
      } else {
         var1.write("\"\"");
         return var1;
      }
   }

   public Object remove(String var1) {
      return this.map.remove(var1);
   }

   public boolean similar(Object var1) {
      try {
         if (!(var1 instanceof JSONObject)) {
            return false;
         } else if (!this.keySet().equals(((JSONObject)var1).keySet())) {
            return false;
         } else {
            for(Map.Entry var3 : this.entrySet()) {
               String var4 = (String)var3.getKey();
               Object var5 = var3.getValue();
               Object var6 = ((JSONObject)var1).get(var4);
               if (var5 != var6) {
                  if (var5 == null) {
                     return false;
                  }

                  if (var5 instanceof JSONObject) {
                     if (!((JSONObject)var5).similar(var6)) {
                        return false;
                     }
                  } else if (var5 instanceof JSONArray) {
                     if (!((JSONArray)var5).similar(var6)) {
                        return false;
                     }
                  } else if (var5 instanceof Number && var6 instanceof Number) {
                     if (!isNumberSimilar((Number)var5, (Number)var6)) {
                        return false;
                     }
                  } else if (var5 instanceof JSONString && var6 instanceof JSONString) {
                     if (!((JSONString)var5).toJSONString().equals(((JSONString)var6).toJSONString())) {
                        return false;
                     }
                  } else if (!var5.equals(var6)) {
                     return false;
                  }
               }
            }

            return true;
         }
      } catch (Throwable var7) {
         return false;
      }
   }

   static boolean isNumberSimilar(Number var0, Number var1) {
      if (numberIsFinite(var0) && numberIsFinite(var1)) {
         if (var0.getClass().equals(var1.getClass()) && var0 instanceof Comparable) {
            int var4 = ((Comparable)var0).compareTo(var1);
            return var4 == 0;
         } else {
            BigDecimal var2 = objectToBigDecimal(var0, (BigDecimal)null, false);
            BigDecimal var3 = objectToBigDecimal(var1, (BigDecimal)null, false);
            if (var2 != null && var3 != null) {
               return var2.compareTo(var3) == 0;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static boolean numberIsFinite(Number var0) {
      if (!(var0 instanceof Double) || !((Double)var0).isInfinite() && !((Double)var0).isNaN()) {
         return !(var0 instanceof Float) || !((Float)var0).isInfinite() && !((Float)var0).isNaN();
      } else {
         return false;
      }
   }

   protected static boolean isDecimalNotation(String var0) {
      return var0.indexOf(46) > -1 || var0.indexOf(101) > -1 || var0.indexOf(69) > -1 || "-0".equals(var0);
   }

   protected static Number stringToNumber(String var0) throws NumberFormatException {
      String var1 = var0;
      if (var0.startsWith(".")) {
         var1 = "0" + var0;
      }

      if (var1.startsWith("-.")) {
         var1 = "-0." + var1.substring(2);
      }

      char var2 = var1.charAt(0);
      if ((var2 < '0' || var2 > '9') && var2 != '-') {
         throw new NumberFormatException("val [" + var0 + "] is not a valid number.");
      } else if (isDecimalNotation(var1)) {
         try {
            BigDecimal var11 = new BigDecimal(var1);
            return (Number)(var2 == '-' && BigDecimal.ZERO.compareTo(var11) == 0 ? (double)-0.0F : var11);
         } catch (NumberFormatException var6) {
            try {
               Double var12 = Double.valueOf(var1);
               if (!var12.isNaN() && !var12.isInfinite()) {
                  return var12;
               } else {
                  throw new NumberFormatException("val [" + var0 + "] is not a valid number.");
               }
            } catch (NumberFormatException var5) {
               throw new NumberFormatException("val [" + var0 + "] is not a valid number.");
            }
         }
      } else {
         var1 = removeLeadingZerosOfNumber(var0);
         var2 = var1.charAt(0);
         if (var2 == '0' && var1.length() > 1) {
            char var9 = var1.charAt(1);
            if (var9 >= '0' && var9 <= '9') {
               throw new NumberFormatException("val [" + var0 + "] is not a valid number.");
            }
         } else if (var2 == '-' && var1.length() > 2) {
            char var3 = var1.charAt(1);
            char var4 = var1.charAt(2);
            if (var3 == '0' && var4 >= '0' && var4 <= '9') {
               throw new NumberFormatException("val [" + var0 + "] is not a valid number.");
            }
         }

         BigInteger var10 = new BigInteger(var1);
         if (var10.bitLength() <= 31) {
            return var10.intValue();
         } else {
            return (Number)(var10.bitLength() <= 63 ? var10.longValue() : var10);
         }
      }
   }

   public static Object stringToValue(String var0) {
      if ("".equals(var0)) {
         return var0;
      } else if ("true".equalsIgnoreCase(var0)) {
         return Boolean.TRUE;
      } else if ("false".equalsIgnoreCase(var0)) {
         return Boolean.FALSE;
      } else if ("null".equalsIgnoreCase(var0)) {
         return NULL;
      } else {
         if (potentialNumber(var0)) {
            try {
               return stringToNumber(var0);
            } catch (Exception var2) {
            }
         }

         return var0;
      }
   }

   private static boolean potentialNumber(String var0) {
      return var0 != null && !var0.isEmpty() ? potentialPositiveNumberStartingAtIndex(var0, var0.charAt(0) == '-' ? 1 : 0) : false;
   }

   private static boolean potentialPositiveNumberStartingAtIndex(String var0, int var1) {
      return var1 >= var0.length() ? false : digitAtIndex(var0, var0.charAt(var1) == '.' ? var1 + 1 : var1);
   }

   private static boolean digitAtIndex(String var0, int var1) {
      if (var1 >= var0.length()) {
         return false;
      } else {
         return var0.charAt(var1) >= '0' && var0.charAt(var1) <= '9';
      }
   }

   public static void testValidity(Object var0) throws JSONException {
      if (var0 instanceof Number && !numberIsFinite((Number)var0)) {
         throw new JSONException("JSON does not allow non-finite numbers.");
      }
   }

   public JSONArray toJSONArray(JSONArray var1) throws JSONException {
      if (var1 != null && !var1.isEmpty()) {
         JSONArray var2 = new JSONArray();

         for(int var3 = 0; var3 < var1.length(); ++var3) {
            var2.put(this.opt(var1.getString(var3)));
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

   public static String valueToString(Object var0) throws JSONException {
      return JSONWriter.valueToString(var0);
   }

   public static Object wrap(Object var0) {
      return wrap(var0, (Set)null);
   }

   private static Object wrap(Object var0, Set<Object> var1) {
      try {
         if (NULL.equals(var0)) {
            return NULL;
         } else if (!(var0 instanceof JSONObject) && !(var0 instanceof JSONArray) && !NULL.equals(var0) && !(var0 instanceof JSONString) && !(var0 instanceof Byte) && !(var0 instanceof Character) && !(var0 instanceof Short) && !(var0 instanceof Integer) && !(var0 instanceof Long) && !(var0 instanceof Boolean) && !(var0 instanceof Float) && !(var0 instanceof Double) && !(var0 instanceof String) && !(var0 instanceof BigInteger) && !(var0 instanceof BigDecimal) && !(var0 instanceof Enum)) {
            if (var0 instanceof Collection) {
               Collection var7 = (Collection)var0;
               return new JSONArray(var7);
            } else if (var0.getClass().isArray()) {
               return new JSONArray(var0);
            } else if (var0 instanceof Map) {
               Map var6 = (Map)var0;
               return new JSONObject(var6);
            } else {
               Package var2 = var0.getClass().getPackage();
               String var3 = var2 != null ? var2.getName() : "";
               if (!var3.startsWith("java.") && !var3.startsWith("javax.") && var0.getClass().getClassLoader() != null) {
                  return var1 != null ? new JSONObject(var0, var1) : new JSONObject(var0);
               } else {
                  return var0.toString();
               }
            }
         } else {
            return var0;
         }
      } catch (JSONException var4) {
         throw var4;
      } catch (Exception var5) {
         return null;
      }
   }

   public Writer write(Writer var1) throws JSONException {
      return this.write(var1, 0, 0);
   }

   static final Writer writeValue(Writer var0, Object var1, int var2, int var3) throws JSONException, IOException {
      if (var1 != null && !var1.equals((Object)null)) {
         if (var1 instanceof JSONString) {
            String var4;
            try {
               var4 = ((JSONString)var1).toJSONString();
            } catch (Exception var6) {
               throw new JSONException(var6);
            }

            var0.write(var4 != null ? var4.toString() : quote(var1.toString()));
         } else if (var1 instanceof Number) {
            String var7 = numberToString((Number)var1);
            if (NUMBER_PATTERN.matcher(var7).matches()) {
               var0.write(var7);
            } else {
               quote(var7, var0);
            }
         } else if (var1 instanceof Boolean) {
            var0.write(var1.toString());
         } else if (var1 instanceof Enum) {
            var0.write(quote(((Enum)var1).name()));
         } else if (var1 instanceof JSONObject) {
            ((JSONObject)var1).write(var0, var2, var3);
         } else if (var1 instanceof JSONArray) {
            ((JSONArray)var1).write(var0, var2, var3);
         } else if (var1 instanceof Map) {
            Map var8 = (Map)var1;
            (new JSONObject(var8)).write(var0, var2, var3);
         } else if (var1 instanceof Collection) {
            Collection var9 = (Collection)var1;
            (new JSONArray(var9)).write(var0, var2, var3);
         } else if (var1.getClass().isArray()) {
            (new JSONArray(var1)).write(var0, var2, var3);
         } else {
            quote(var1.toString(), var0);
         }
      } else {
         var0.write("null");
      }

      return var0;
   }

   static final void indent(Writer var0, int var1) throws IOException {
      for(int var2 = 0; var2 < var1; ++var2) {
         var0.write(32);
      }

   }

   public Writer write(Writer var1, int var2, int var3) throws JSONException {
      try {
         boolean var4 = false;
         int var5 = this.length();
         var1.write(123);
         if (var5 == 1) {
            Map.Entry var6 = (Map.Entry)this.entrySet().iterator().next();
            String var7 = (String)var6.getKey();
            var1.write(quote(var7));
            var1.write(58);
            if (var2 > 0) {
               var1.write(32);
            }

            try {
               writeValue(var1, var6.getValue(), var2, var3);
            } catch (Exception var12) {
               throw new JSONException("Unable to write JSONObject value for key: " + var7, var12);
            }
         } else if (var5 != 0) {
            int var14 = var3 + var2;

            for(Map.Entry var8 : this.entrySet()) {
               if (var4) {
                  var1.write(44);
               }

               if (var2 > 0) {
                  var1.write(10);
               }

               indent(var1, var14);
               String var9 = (String)var8.getKey();
               var1.write(quote(var9));
               var1.write(58);
               if (var2 > 0) {
                  var1.write(32);
               }

               try {
                  writeValue(var1, var8.getValue(), var2, var14);
               } catch (Exception var11) {
                  throw new JSONException("Unable to write JSONObject value for key: " + var9, var11);
               }

               var4 = true;
            }

            if (var2 > 0) {
               var1.write(10);
            }

            indent(var1, var3);
         }

         var1.write(125);
         return var1;
      } catch (IOException var13) {
         throw new JSONException(var13);
      }
   }

   public Map<String, Object> toMap() {
      HashMap var1 = new HashMap();

      for(Map.Entry var3 : this.entrySet()) {
         Object var4;
         if (var3.getValue() != null && !NULL.equals(var3.getValue())) {
            if (var3.getValue() instanceof JSONObject) {
               var4 = ((JSONObject)var3.getValue()).toMap();
            } else if (var3.getValue() instanceof JSONArray) {
               var4 = ((JSONArray)var3.getValue()).toList();
            } else {
               var4 = var3.getValue();
            }
         } else {
            var4 = null;
         }

         var1.put(var3.getKey(), var4);
      }

      return var1;
   }

   private static JSONException wrongValueFormatException(String var0, String var1, Object var2, Throwable var3) {
      if (var2 == null) {
         return new JSONException("JSONObject[" + quote(var0) + "] is not a " + var1 + " (null).", var3);
      } else {
         return !(var2 instanceof Map) && !(var2 instanceof Iterable) && !(var2 instanceof JSONObject) ? new JSONException("JSONObject[" + quote(var0) + "] is not a " + var1 + " (" + var2.getClass() + " : " + var2 + ").", var3) : new JSONException("JSONObject[" + quote(var0) + "] is not a " + var1 + " (" + var2.getClass() + ").", var3);
      }
   }

   private static JSONException recursivelyDefinedObjectException(String var0) {
      return new JSONException("JavaBean object contains recursively defined member variable of key " + quote(var0));
   }

   private static String removeLeadingZerosOfNumber(String var0) {
      if (var0.equals("-")) {
         return var0;
      } else {
         boolean var1 = var0.charAt(0) == '-';

         for(int var2 = var1 ? 1 : 0; var2 < var0.length(); ++var2) {
            if (var0.charAt(var2) != '0') {
               if (var1) {
                  return "-".concat(var0.substring(var2));
               }

               return var0.substring(var2);
            }
         }

         if (var1) {
            return "-0";
         } else {
            return "0";
         }
      }
   }

   private static final class Null {
      private Null() {
      }

      protected final Object clone() {
         return this;
      }

      public boolean equals(Object var1) {
         return var1 == null || var1 == this;
      }

      public int hashCode() {
         return 0;
      }

      public String toString() {
         return "null";
      }
   }
}
