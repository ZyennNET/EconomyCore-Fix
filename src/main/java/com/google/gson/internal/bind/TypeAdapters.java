package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public final class TypeAdapters {
   public static final TypeAdapter<Class> CLASS = (new TypeAdapter<Class>() {
      public void write(JsonWriter var1, Class var2) throws IOException {
         throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + var2.getName() + ". Forgot to register a type adapter?\nSee " + TroubleshootingGuide.createUrl("java-lang-class-unsupported"));
      }

      public Class read(JsonReader var1) throws IOException {
         throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?\nSee " + TroubleshootingGuide.createUrl("java-lang-class-unsupported"));
      }
   }).nullSafe();
   public static final TypeAdapterFactory CLASS_FACTORY;
   public static final TypeAdapter<BitSet> BIT_SET;
   public static final TypeAdapterFactory BIT_SET_FACTORY;
   public static final TypeAdapter<Boolean> BOOLEAN;
   public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING;
   public static final TypeAdapterFactory BOOLEAN_FACTORY;
   public static final TypeAdapter<Number> BYTE;
   public static final TypeAdapterFactory BYTE_FACTORY;
   public static final TypeAdapter<Number> SHORT;
   public static final TypeAdapterFactory SHORT_FACTORY;
   public static final TypeAdapter<Number> INTEGER;
   public static final TypeAdapterFactory INTEGER_FACTORY;
   public static final TypeAdapter<AtomicInteger> ATOMIC_INTEGER;
   public static final TypeAdapterFactory ATOMIC_INTEGER_FACTORY;
   public static final TypeAdapter<AtomicBoolean> ATOMIC_BOOLEAN;
   public static final TypeAdapterFactory ATOMIC_BOOLEAN_FACTORY;
   public static final TypeAdapter<AtomicIntegerArray> ATOMIC_INTEGER_ARRAY;
   public static final TypeAdapterFactory ATOMIC_INTEGER_ARRAY_FACTORY;
   public static final TypeAdapter<Number> LONG;
   public static final TypeAdapter<Number> FLOAT;
   public static final TypeAdapter<Number> DOUBLE;
   public static final TypeAdapter<Character> CHARACTER;
   public static final TypeAdapterFactory CHARACTER_FACTORY;
   public static final TypeAdapter<String> STRING;
   public static final TypeAdapter<BigDecimal> BIG_DECIMAL;
   public static final TypeAdapter<BigInteger> BIG_INTEGER;
   public static final TypeAdapter<LazilyParsedNumber> LAZILY_PARSED_NUMBER;
   public static final TypeAdapterFactory STRING_FACTORY;
   public static final TypeAdapter<StringBuilder> STRING_BUILDER;
   public static final TypeAdapterFactory STRING_BUILDER_FACTORY;
   public static final TypeAdapter<StringBuffer> STRING_BUFFER;
   public static final TypeAdapterFactory STRING_BUFFER_FACTORY;
   public static final TypeAdapter<URL> URL;
   public static final TypeAdapterFactory URL_FACTORY;
   public static final TypeAdapter<URI> URI;
   public static final TypeAdapterFactory URI_FACTORY;
   public static final TypeAdapter<InetAddress> INET_ADDRESS;
   public static final TypeAdapterFactory INET_ADDRESS_FACTORY;
   public static final TypeAdapter<UUID> UUID;
   public static final TypeAdapterFactory UUID_FACTORY;
   public static final TypeAdapter<Currency> CURRENCY;
   public static final TypeAdapterFactory CURRENCY_FACTORY;
   public static final TypeAdapter<Calendar> CALENDAR;
   public static final TypeAdapterFactory CALENDAR_FACTORY;
   public static final TypeAdapter<Locale> LOCALE;
   public static final TypeAdapterFactory LOCALE_FACTORY;
   public static final TypeAdapter<JsonElement> JSON_ELEMENT;
   public static final TypeAdapterFactory JSON_ELEMENT_FACTORY;
   public static final TypeAdapterFactory ENUM_FACTORY;

   private TypeAdapters() {
      throw new UnsupportedOperationException();
   }

   public static <TT> TypeAdapterFactory newFactory(final TypeToken<TT> var0, final TypeAdapter<TT> var1) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson var1x, TypeToken<T> var2) {
            return var2.equals(var0) ? var1 : null;
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactory(final Class<TT> var0, final TypeAdapter<TT> var1) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson var1x, TypeToken<T> var2) {
            return var2.getRawType() == var0 ? var1 : null;
         }

         public String toString() {
            return "Factory[type=" + var0.getName() + ",adapter=" + var1 + "]";
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactory(final Class<TT> var0, final Class<TT> var1, final TypeAdapter<? super TT> var2) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson var1x, TypeToken<T> var2x) {
            Class var3 = var2x.getRawType();
            return var3 != var0 && var3 != var1 ? null : var2;
         }

         public String toString() {
            return "Factory[type=" + var1.getName() + "+" + var0.getName() + ",adapter=" + var2 + "]";
         }
      };
   }

   public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(final Class<TT> var0, final Class<? extends TT> var1, final TypeAdapter<? super TT> var2) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson var1x, TypeToken<T> var2x) {
            Class var3 = var2x.getRawType();
            return var3 != var0 && var3 != var1 ? null : var2;
         }

         public String toString() {
            return "Factory[type=" + var0.getName() + "+" + var1.getName() + ",adapter=" + var2 + "]";
         }
      };
   }

   public static <T1> TypeAdapterFactory newTypeHierarchyFactory(final Class<T1> var0, final TypeAdapter<T1> var1) {
      return new TypeAdapterFactory() {
         public <T2> TypeAdapter<T2> create(Gson var1x, TypeToken<T2> var2) {
            final Class var3 = var2.getRawType();
            return !var0.isAssignableFrom(var3) ? null : new TypeAdapter<T1>() {
               public void write(JsonWriter var1x, T1 var2) throws IOException {
                  var1.write(var1x, var2);
               }

               public T1 read(JsonReader var1x) throws IOException {
                  Object var2 = var1.read(var1x);
                  if (var2 != null && !var3.isInstance(var2)) {
                     throw new JsonSyntaxException("Expected a " + var3.getName() + " but was " + var2.getClass().getName() + "; at path " + var1x.getPreviousPath());
                  } else {
                     return (T1)var2;
                  }
               }
            };
         }

         public String toString() {
            return "Factory[typeHierarchy=" + var0.getName() + ",adapter=" + var1 + "]";
         }
      };
   }

   static {
      CLASS_FACTORY = newFactory(Class.class, CLASS);
      BIT_SET = (new TypeAdapter<BitSet>() {
         public BitSet read(JsonReader var1) throws IOException {
            BitSet var2 = new BitSet();
            var1.beginArray();
            int var3 = 0;

            for(JsonToken var4 = var1.peek(); var4 != JsonToken.END_ARRAY; var4 = var1.peek()) {
               boolean var5;
               switch (var4) {
                  case NUMBER:
                  case STRING:
                     int var6 = var1.nextInt();
                     if (var6 == 0) {
                        var5 = false;
                     } else {
                        if (var6 != 1) {
                           throw new JsonSyntaxException("Invalid bitset value " + var6 + ", expected 0 or 1; at path " + var1.getPreviousPath());
                        }

                        var5 = true;
                     }
                     break;
                  case BOOLEAN:
                     var5 = var1.nextBoolean();
                     break;
                  default:
                     throw new JsonSyntaxException("Invalid bitset value type: " + var4 + "; at path " + var1.getPath());
               }

               if (var5) {
                  var2.set(var3);
               }

               ++var3;
            }

            var1.endArray();
            return var2;
         }

         public void write(JsonWriter var1, BitSet var2) throws IOException {
            var1.beginArray();
            int var3 = 0;

            for(int var4 = var2.length(); var3 < var4; ++var3) {
               int var5 = var2.get(var3) ? 1 : 0;
               var1.value((long)var5);
            }

            var1.endArray();
         }
      }).nullSafe();
      BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
      BOOLEAN = new TypeAdapter<Boolean>() {
         public Boolean read(JsonReader var1) throws IOException {
            JsonToken var2 = var1.peek();
            if (var2 == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return var2 == JsonToken.STRING ? Boolean.parseBoolean(var1.nextString()) : var1.nextBoolean();
            }
         }

         public void write(JsonWriter var1, Boolean var2) throws IOException {
            var1.value(var2);
         }
      };
      BOOLEAN_AS_STRING = new TypeAdapter<Boolean>() {
         public Boolean read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return Boolean.valueOf(var1.nextString());
            }
         }

         public void write(JsonWriter var1, Boolean var2) throws IOException {
            var1.value(var2 == null ? "null" : var2.toString());
         }
      };
      BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
      BYTE = new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               int var2;
               try {
                  var2 = var1.nextInt();
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException(var4);
               }

               if (var2 <= 255 && var2 >= -128) {
                  return (byte)var2;
               } else {
                  throw new JsonSyntaxException("Lossy conversion from " + var2 + " to byte; at path " + var1.getPreviousPath());
               }
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value((long)var2.byteValue());
            }

         }
      };
      BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, BYTE);
      SHORT = new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               int var2;
               try {
                  var2 = var1.nextInt();
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException(var4);
               }

               if (var2 <= 65535 && var2 >= -32768) {
                  return (short)var2;
               } else {
                  throw new JsonSyntaxException("Lossy conversion from " + var2 + " to short; at path " + var1.getPreviousPath());
               }
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value((long)var2.shortValue());
            }

         }
      };
      SHORT_FACTORY = newFactory(Short.TYPE, Short.class, SHORT);
      INTEGER = new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               try {
                  return var1.nextInt();
               } catch (NumberFormatException var3) {
                  throw new JsonSyntaxException(var3);
               }
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value((long)var2.intValue());
            }

         }
      };
      INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, INTEGER);
      ATOMIC_INTEGER = (new TypeAdapter<AtomicInteger>() {
         public AtomicInteger read(JsonReader var1) throws IOException {
            try {
               return new AtomicInteger(var1.nextInt());
            } catch (NumberFormatException var3) {
               throw new JsonSyntaxException(var3);
            }
         }

         public void write(JsonWriter var1, AtomicInteger var2) throws IOException {
            var1.value((long)var2.get());
         }
      }).nullSafe();
      ATOMIC_INTEGER_FACTORY = newFactory(AtomicInteger.class, ATOMIC_INTEGER);
      ATOMIC_BOOLEAN = (new TypeAdapter<AtomicBoolean>() {
         public AtomicBoolean read(JsonReader var1) throws IOException {
            return new AtomicBoolean(var1.nextBoolean());
         }

         public void write(JsonWriter var1, AtomicBoolean var2) throws IOException {
            var1.value(var2.get());
         }
      }).nullSafe();
      ATOMIC_BOOLEAN_FACTORY = newFactory(AtomicBoolean.class, ATOMIC_BOOLEAN);
      ATOMIC_INTEGER_ARRAY = (new TypeAdapter<AtomicIntegerArray>() {
         public AtomicIntegerArray read(JsonReader var1) throws IOException {
            ArrayList var2 = new ArrayList();
            var1.beginArray();

            while(var1.hasNext()) {
               try {
                  int var3 = var1.nextInt();
                  var2.add(var3);
               } catch (NumberFormatException var6) {
                  throw new JsonSyntaxException(var6);
               }
            }

            var1.endArray();
            int var7 = var2.size();
            AtomicIntegerArray var4 = new AtomicIntegerArray(var7);

            for(int var5 = 0; var5 < var7; ++var5) {
               var4.set(var5, (Integer)var2.get(var5));
            }

            return var4;
         }

         public void write(JsonWriter var1, AtomicIntegerArray var2) throws IOException {
            var1.beginArray();
            int var3 = 0;

            for(int var4 = var2.length(); var3 < var4; ++var3) {
               var1.value((long)var2.get(var3));
            }

            var1.endArray();
         }
      }).nullSafe();
      ATOMIC_INTEGER_ARRAY_FACTORY = newFactory(AtomicIntegerArray.class, ATOMIC_INTEGER_ARRAY);
      LONG = new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               try {
                  return var1.nextLong();
               } catch (NumberFormatException var3) {
                  throw new JsonSyntaxException(var3);
               }
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value(var2.longValue());
            }

         }
      };
      FLOAT = new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return (float)var1.nextDouble();
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               Object var3 = var2 instanceof Float ? var2 : var2.floatValue();
               var1.value((Number)var3);
            }

         }
      };
      DOUBLE = new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return var1.nextDouble();
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value(var2.doubleValue());
            }

         }
      };
      CHARACTER = new TypeAdapter<Character>() {
         public Character read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               String var2 = var1.nextString();
               if (var2.length() != 1) {
                  throw new JsonSyntaxException("Expecting character, got: " + var2 + "; at " + var1.getPreviousPath());
               } else {
                  return var2.charAt(0);
               }
            }
         }

         public void write(JsonWriter var1, Character var2) throws IOException {
            var1.value(var2 == null ? null : String.valueOf(var2));
         }
      };
      CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, CHARACTER);
      STRING = new TypeAdapter<String>() {
         public String read(JsonReader var1) throws IOException {
            JsonToken var2 = var1.peek();
            if (var2 == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return var2 == JsonToken.BOOLEAN ? Boolean.toString(var1.nextBoolean()) : var1.nextString();
            }
         }

         public void write(JsonWriter var1, String var2) throws IOException {
            var1.value(var2);
         }
      };
      BIG_DECIMAL = new TypeAdapter<BigDecimal>() {
         public BigDecimal read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               String var2 = var1.nextString();

               try {
                  return NumberLimits.parseBigDecimal(var2);
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException("Failed parsing '" + var2 + "' as BigDecimal; at path " + var1.getPreviousPath(), var4);
               }
            }
         }

         public void write(JsonWriter var1, BigDecimal var2) throws IOException {
            var1.value((Number)var2);
         }
      };
      BIG_INTEGER = new TypeAdapter<BigInteger>() {
         public BigInteger read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               String var2 = var1.nextString();

               try {
                  return NumberLimits.parseBigInteger(var2);
               } catch (NumberFormatException var4) {
                  throw new JsonSyntaxException("Failed parsing '" + var2 + "' as BigInteger; at path " + var1.getPreviousPath(), var4);
               }
            }
         }

         public void write(JsonWriter var1, BigInteger var2) throws IOException {
            var1.value((Number)var2);
         }
      };
      LAZILY_PARSED_NUMBER = new TypeAdapter<LazilyParsedNumber>() {
         public LazilyParsedNumber read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return new LazilyParsedNumber(var1.nextString());
            }
         }

         public void write(JsonWriter var1, LazilyParsedNumber var2) throws IOException {
            var1.value((Number)var2);
         }
      };
      STRING_FACTORY = newFactory(String.class, STRING);
      STRING_BUILDER = new TypeAdapter<StringBuilder>() {
         public StringBuilder read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return new StringBuilder(var1.nextString());
            }
         }

         public void write(JsonWriter var1, StringBuilder var2) throws IOException {
            var1.value(var2 == null ? null : var2.toString());
         }
      };
      STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
      STRING_BUFFER = new TypeAdapter<StringBuffer>() {
         public StringBuffer read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return new StringBuffer(var1.nextString());
            }
         }

         public void write(JsonWriter var1, StringBuffer var2) throws IOException {
            var1.value(var2 == null ? null : var2.toString());
         }
      };
      STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
      URL = new TypeAdapter<URL>() {
         public URL read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               String var2 = var1.nextString();
               return var2.equals("null") ? null : new URL(var2);
            }
         }

         public void write(JsonWriter var1, URL var2) throws IOException {
            var1.value(var2 == null ? null : var2.toExternalForm());
         }
      };
      URL_FACTORY = newFactory(URL.class, URL);
      URI = new TypeAdapter<URI>() {
         public URI read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               try {
                  String var2 = var1.nextString();
                  return var2.equals("null") ? null : new URI(var2);
               } catch (URISyntaxException var3) {
                  throw new JsonIOException(var3);
               }
            }
         }

         public void write(JsonWriter var1, URI var2) throws IOException {
            var1.value(var2 == null ? null : var2.toASCIIString());
         }
      };
      URI_FACTORY = newFactory(URI.class, URI);
      INET_ADDRESS = new TypeAdapter<InetAddress>() {
         public InetAddress read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               InetAddress var2 = InetAddress.getByName(var1.nextString());
               return var2;
            }
         }

         public void write(JsonWriter var1, InetAddress var2) throws IOException {
            var1.value(var2 == null ? null : var2.getHostAddress());
         }
      };
      INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
      UUID = new TypeAdapter<UUID>() {
         public UUID read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               String var2 = var1.nextString();

               try {
                  return java.util.UUID.fromString(var2);
               } catch (IllegalArgumentException var4) {
                  throw new JsonSyntaxException("Failed parsing '" + var2 + "' as UUID; at path " + var1.getPreviousPath(), var4);
               }
            }
         }

         public void write(JsonWriter var1, UUID var2) throws IOException {
            var1.value(var2 == null ? null : var2.toString());
         }
      };
      UUID_FACTORY = newFactory(UUID.class, UUID);
      CURRENCY = (new TypeAdapter<Currency>() {
         public Currency read(JsonReader var1) throws IOException {
            String var2 = var1.nextString();

            try {
               return Currency.getInstance(var2);
            } catch (IllegalArgumentException var4) {
               throw new JsonSyntaxException("Failed parsing '" + var2 + "' as Currency; at path " + var1.getPreviousPath(), var4);
            }
         }

         public void write(JsonWriter var1, Currency var2) throws IOException {
            var1.value(var2.getCurrencyCode());
         }
      }).nullSafe();
      CURRENCY_FACTORY = newFactory(Currency.class, CURRENCY);
      CALENDAR = new TypeAdapter<Calendar>() {
         private static final String YEAR = "year";
         private static final String MONTH = "month";
         private static final String DAY_OF_MONTH = "dayOfMonth";
         private static final String HOUR_OF_DAY = "hourOfDay";
         private static final String MINUTE = "minute";
         private static final String SECOND = "second";

         public Calendar read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               var1.beginObject();
               int var2 = 0;
               int var3 = 0;
               int var4 = 0;
               int var5 = 0;
               int var6 = 0;
               int var7 = 0;

               while(var1.peek() != JsonToken.END_OBJECT) {
                  String var8 = var1.nextName();
                  int var9 = var1.nextInt();
                  switch (var8) {
                     case "year":
                        var2 = var9;
                        break;
                     case "month":
                        var3 = var9;
                        break;
                     case "dayOfMonth":
                        var4 = var9;
                        break;
                     case "hourOfDay":
                        var5 = var9;
                        break;
                     case "minute":
                        var6 = var9;
                        break;
                     case "second":
                        var7 = var9;
                  }
               }

               var1.endObject();
               return new GregorianCalendar(var2, var3, var4, var5, var6, var7);
            }
         }

         public void write(JsonWriter var1, Calendar var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.beginObject();
               var1.name("year");
               var1.value((long)var2.get(1));
               var1.name("month");
               var1.value((long)var2.get(2));
               var1.name("dayOfMonth");
               var1.value((long)var2.get(5));
               var1.name("hourOfDay");
               var1.value((long)var2.get(11));
               var1.name("minute");
               var1.value((long)var2.get(12));
               var1.name("second");
               var1.value((long)var2.get(13));
               var1.endObject();
            }
         }
      };
      CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
      LOCALE = new TypeAdapter<Locale>() {
         public Locale read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               String var2 = var1.nextString();
               StringTokenizer var3 = new StringTokenizer(var2, "_");
               String var4 = null;
               String var5 = null;
               String var6 = null;
               if (var3.hasMoreElements()) {
                  var4 = var3.nextToken();
               }

               if (var3.hasMoreElements()) {
                  var5 = var3.nextToken();
               }

               if (var3.hasMoreElements()) {
                  var6 = var3.nextToken();
               }

               if (var5 == null && var6 == null) {
                  return new Locale(var4);
               } else {
                  return var6 == null ? new Locale(var4, var5) : new Locale(var4, var5, var6);
               }
            }
         }

         public void write(JsonWriter var1, Locale var2) throws IOException {
            var1.value(var2 == null ? null : var2.toString());
         }
      };
      LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
      JSON_ELEMENT = new TypeAdapter<JsonElement>() {
         private JsonElement tryBeginNesting(JsonReader var1, JsonToken var2) throws IOException {
            switch (var2) {
               case BEGIN_ARRAY:
                  var1.beginArray();
                  return new JsonArray();
               case BEGIN_OBJECT:
                  var1.beginObject();
                  return new JsonObject();
               default:
                  return null;
            }
         }

         private JsonElement readTerminal(JsonReader var1, JsonToken var2) throws IOException {
            switch (var2) {
               case NUMBER:
                  String var3 = var1.nextString();
                  return new JsonPrimitive(new LazilyParsedNumber(var3));
               case STRING:
                  return new JsonPrimitive(var1.nextString());
               case BOOLEAN:
                  return new JsonPrimitive(var1.nextBoolean());
               case BEGIN_ARRAY:
               case BEGIN_OBJECT:
               default:
                  throw new IllegalStateException("Unexpected token: " + var2);
               case NULL:
                  var1.nextNull();
                  return JsonNull.INSTANCE;
            }
         }

         public JsonElement read(JsonReader var1) throws IOException {
            if (var1 instanceof JsonTreeReader) {
               return ((JsonTreeReader)var1).nextJsonElement();
            } else {
               JsonToken var3 = var1.peek();
               JsonElement var2 = this.tryBeginNesting(var1, var3);
               if (var2 == null) {
                  return this.readTerminal(var1, var3);
               } else {
                  ArrayDeque var4 = new ArrayDeque();

                  while(true) {
                     while(!var1.hasNext()) {
                        if (var2 instanceof JsonArray) {
                           var1.endArray();
                        } else {
                           var1.endObject();
                        }

                        if (var4.isEmpty()) {
                           return var2;
                        }

                        var2 = (JsonElement)var4.removeLast();
                     }

                     String var5 = null;
                     if (var2 instanceof JsonObject) {
                        var5 = var1.nextName();
                     }

                     var3 = var1.peek();
                     JsonElement var6 = this.tryBeginNesting(var1, var3);
                     boolean var7 = var6 != null;
                     if (var6 == null) {
                        var6 = this.readTerminal(var1, var3);
                     }

                     if (var2 instanceof JsonArray) {
                        ((JsonArray)var2).add(var6);
                     } else {
                        ((JsonObject)var2).add(var5, var6);
                     }

                     if (var7) {
                        var4.addLast(var2);
                        var2 = var6;
                     }
                  }
               }
            }
         }

         public void write(JsonWriter var1, JsonElement var2) throws IOException {
            if (var2 != null && !var2.isJsonNull()) {
               if (var2.isJsonPrimitive()) {
                  JsonPrimitive var3 = var2.getAsJsonPrimitive();
                  if (var3.isNumber()) {
                     var1.value(var3.getAsNumber());
                  } else if (var3.isBoolean()) {
                     var1.value(var3.getAsBoolean());
                  } else {
                     var1.value(var3.getAsString());
                  }
               } else if (var2.isJsonArray()) {
                  var1.beginArray();

                  for(JsonElement var4 : var2.getAsJsonArray()) {
                     this.write(var1, var4);
                  }

                  var1.endArray();
               } else {
                  if (!var2.isJsonObject()) {
                     throw new IllegalArgumentException("Couldn't write " + var2.getClass());
                  }

                  var1.beginObject();

                  for(Map.Entry var7 : var2.getAsJsonObject().entrySet()) {
                     var1.name((String)var7.getKey());
                     this.write(var1, (JsonElement)var7.getValue());
                  }

                  var1.endObject();
               }
            } else {
               var1.nullValue();
            }

         }
      };
      JSON_ELEMENT_FACTORY = newTypeHierarchyFactory(JsonElement.class, JSON_ELEMENT);
      ENUM_FACTORY = new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
            Class var3 = var2.getRawType();
            if (Enum.class.isAssignableFrom(var3) && var3 != Enum.class) {
               if (!var3.isEnum()) {
                  var3 = var3.getSuperclass();
               }

               EnumTypeAdapter var4 = new EnumTypeAdapter(var3);
               return var4;
            } else {
               return null;
            }
         }
      };
   }

   private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T> {
      private final Map<String, T> nameToConstant = new HashMap();
      private final Map<String, T> stringToConstant = new HashMap();
      private final Map<T, String> constantToName = new HashMap();

      public EnumTypeAdapter(final Class<T> var1) {
         try {
            Field[] var2 = (Field[])AccessController.doPrivileged(new PrivilegedAction<Field[]>() {
               public Field[] run() {
                  Field[] var1x = var1.getDeclaredFields();
                  ArrayList var2 = new ArrayList(var1x.length);

                  for(Field var6 : var1x) {
                     if (var6.isEnumConstant()) {
                        var2.add(var6);
                     }
                  }

                  Field[] var7 = (Field[])var2.toArray(new Field[0]);
                  AccessibleObject.setAccessible(var7, true);
                  return var7;
               }
            });

            for(Field var6 : var2) {
               Enum var7 = (Enum)var6.get((Object)null);
               String var8 = var7.name();
               String var9 = var7.toString();
               SerializedName var10 = (SerializedName)var6.getAnnotation(SerializedName.class);
               if (var10 != null) {
                  var8 = var10.value();

                  for(String var14 : var10.alternate()) {
                     this.nameToConstant.put(var14, var7);
                  }
               }

               this.nameToConstant.put(var8, var7);
               this.stringToConstant.put(var9, var7);
               this.constantToName.put(var7, var8);
            }

         } catch (IllegalAccessException var15) {
            throw new AssertionError(var15);
         }
      }

      public T read(JsonReader var1) throws IOException {
         if (var1.peek() == JsonToken.NULL) {
            var1.nextNull();
            return null;
         } else {
            String var2 = var1.nextString();
            Enum var3 = (Enum)this.nameToConstant.get(var2);
            return (T)(var3 == null ? (Enum)this.stringToConstant.get(var2) : var3);
         }
      }

      public void write(JsonWriter var1, T var2) throws IOException {
         var1.value(var2 == null ? null : (String)this.constantToName.get(var2));
      }
   }
}
