package com.google.gson;

import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.Streams;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import com.google.gson.internal.bind.JsonAdapterAnnotationTypeAdapterFactory;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.MapTypeAdapterFactory;
import com.google.gson.internal.bind.NumberTypeAdapter;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.internal.bind.SerializationDelegatingTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.MalformedJsonException;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

public final class Gson {
   static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
   static final Strictness DEFAULT_STRICTNESS = null;
   static final FormattingStyle DEFAULT_FORMATTING_STYLE;
   static final boolean DEFAULT_ESCAPE_HTML = true;
   static final boolean DEFAULT_SERIALIZE_NULLS = false;
   static final boolean DEFAULT_COMPLEX_MAP_KEYS = false;
   static final boolean DEFAULT_SPECIALIZE_FLOAT_VALUES = false;
   static final boolean DEFAULT_USE_JDK_UNSAFE = true;
   static final String DEFAULT_DATE_PATTERN;
   static final FieldNamingStrategy DEFAULT_FIELD_NAMING_STRATEGY;
   static final ToNumberStrategy DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
   static final ToNumberStrategy DEFAULT_NUMBER_TO_NUMBER_STRATEGY;
   private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
   private final ThreadLocal<Map<TypeToken<?>, TypeAdapter<?>>> threadLocalAdapterResults;
   private final ConcurrentMap<TypeToken<?>, TypeAdapter<?>> typeTokenCache;
   private final ConstructorConstructor constructorConstructor;
   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
   final List<TypeAdapterFactory> factories;
   final Excluder excluder;
   final FieldNamingStrategy fieldNamingStrategy;
   final Map<Type, InstanceCreator<?>> instanceCreators;
   final boolean serializeNulls;
   final boolean complexMapKeySerialization;
   final boolean generateNonExecutableJson;
   final boolean htmlSafe;
   final FormattingStyle formattingStyle;
   final Strictness strictness;
   final boolean serializeSpecialFloatingPointValues;
   final boolean useJdkUnsafe;
   final String datePattern;
   final int dateStyle;
   final int timeStyle;
   final LongSerializationPolicy longSerializationPolicy;
   final List<TypeAdapterFactory> builderFactories;
   final List<TypeAdapterFactory> builderHierarchyFactories;
   final ToNumberStrategy objectToNumberStrategy;
   final ToNumberStrategy numberToNumberStrategy;
   final List<ReflectionAccessFilter> reflectionFilters;

   public Gson() {
      this(Excluder.DEFAULT, DEFAULT_FIELD_NAMING_STRATEGY, Collections.emptyMap(), false, false, false, true, DEFAULT_FORMATTING_STYLE, DEFAULT_STRICTNESS, false, true, LongSerializationPolicy.DEFAULT, DEFAULT_DATE_PATTERN, 2, 2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), DEFAULT_OBJECT_TO_NUMBER_STRATEGY, DEFAULT_NUMBER_TO_NUMBER_STRATEGY, Collections.emptyList());
   }

   Gson(Excluder var1, FieldNamingStrategy var2, Map<Type, InstanceCreator<?>> var3, boolean var4, boolean var5, boolean var6, boolean var7, FormattingStyle var8, Strictness var9, boolean var10, boolean var11, LongSerializationPolicy var12, String var13, int var14, int var15, List<TypeAdapterFactory> var16, List<TypeAdapterFactory> var17, List<TypeAdapterFactory> var18, ToNumberStrategy var19, ToNumberStrategy var20, List<ReflectionAccessFilter> var21) {
      this.threadLocalAdapterResults = new ThreadLocal();
      this.typeTokenCache = new ConcurrentHashMap();
      this.excluder = var1;
      this.fieldNamingStrategy = var2;
      this.instanceCreators = var3;
      this.constructorConstructor = new ConstructorConstructor(var3, var11, var21);
      this.serializeNulls = var4;
      this.complexMapKeySerialization = var5;
      this.generateNonExecutableJson = var6;
      this.htmlSafe = var7;
      this.formattingStyle = var8;
      this.strictness = var9;
      this.serializeSpecialFloatingPointValues = var10;
      this.useJdkUnsafe = var11;
      this.longSerializationPolicy = var12;
      this.datePattern = var13;
      this.dateStyle = var14;
      this.timeStyle = var15;
      this.builderFactories = var16;
      this.builderHierarchyFactories = var17;
      this.objectToNumberStrategy = var19;
      this.numberToNumberStrategy = var20;
      this.reflectionFilters = var21;
      ArrayList var22 = new ArrayList();
      var22.add(TypeAdapters.JSON_ELEMENT_FACTORY);
      var22.add(ObjectTypeAdapter.getFactory(var19));
      var22.add(var1);
      var22.addAll(var18);
      var22.add(TypeAdapters.STRING_FACTORY);
      var22.add(TypeAdapters.INTEGER_FACTORY);
      var22.add(TypeAdapters.BOOLEAN_FACTORY);
      var22.add(TypeAdapters.BYTE_FACTORY);
      var22.add(TypeAdapters.SHORT_FACTORY);
      TypeAdapter var23 = longAdapter(var12);
      var22.add(TypeAdapters.newFactory(Long.TYPE, Long.class, var23));
      var22.add(TypeAdapters.newFactory(Double.TYPE, Double.class, this.doubleAdapter(var10)));
      var22.add(TypeAdapters.newFactory(Float.TYPE, Float.class, this.floatAdapter(var10)));
      var22.add(NumberTypeAdapter.getFactory(var20));
      var22.add(TypeAdapters.ATOMIC_INTEGER_FACTORY);
      var22.add(TypeAdapters.ATOMIC_BOOLEAN_FACTORY);
      var22.add(TypeAdapters.newFactory(AtomicLong.class, atomicLongAdapter(var23)));
      var22.add(TypeAdapters.newFactory(AtomicLongArray.class, atomicLongArrayAdapter(var23)));
      var22.add(TypeAdapters.ATOMIC_INTEGER_ARRAY_FACTORY);
      var22.add(TypeAdapters.CHARACTER_FACTORY);
      var22.add(TypeAdapters.STRING_BUILDER_FACTORY);
      var22.add(TypeAdapters.STRING_BUFFER_FACTORY);
      var22.add(TypeAdapters.newFactory(BigDecimal.class, TypeAdapters.BIG_DECIMAL));
      var22.add(TypeAdapters.newFactory(BigInteger.class, TypeAdapters.BIG_INTEGER));
      var22.add(TypeAdapters.newFactory(LazilyParsedNumber.class, TypeAdapters.LAZILY_PARSED_NUMBER));
      var22.add(TypeAdapters.URL_FACTORY);
      var22.add(TypeAdapters.URI_FACTORY);
      var22.add(TypeAdapters.UUID_FACTORY);
      var22.add(TypeAdapters.CURRENCY_FACTORY);
      var22.add(TypeAdapters.LOCALE_FACTORY);
      var22.add(TypeAdapters.INET_ADDRESS_FACTORY);
      var22.add(TypeAdapters.BIT_SET_FACTORY);
      var22.add(DefaultDateTypeAdapter.DEFAULT_STYLE_FACTORY);
      var22.add(TypeAdapters.CALENDAR_FACTORY);
      if (SqlTypesSupport.SUPPORTS_SQL_TYPES) {
         var22.add(SqlTypesSupport.TIME_FACTORY);
         var22.add(SqlTypesSupport.DATE_FACTORY);
         var22.add(SqlTypesSupport.TIMESTAMP_FACTORY);
      }

      var22.add(ArrayTypeAdapter.FACTORY);
      var22.add(TypeAdapters.CLASS_FACTORY);
      var22.add(new CollectionTypeAdapterFactory(this.constructorConstructor));
      var22.add(new MapTypeAdapterFactory(this.constructorConstructor, var5));
      this.jsonAdapterFactory = new JsonAdapterAnnotationTypeAdapterFactory(this.constructorConstructor);
      var22.add(this.jsonAdapterFactory);
      var22.add(TypeAdapters.ENUM_FACTORY);
      var22.add(new ReflectiveTypeAdapterFactory(this.constructorConstructor, var2, var1, this.jsonAdapterFactory, var21));
      this.factories = Collections.unmodifiableList(var22);
   }

   public GsonBuilder newBuilder() {
      return new GsonBuilder(this);
   }

   @Deprecated
   public Excluder excluder() {
      return this.excluder;
   }

   public FieldNamingStrategy fieldNamingStrategy() {
      return this.fieldNamingStrategy;
   }

   public boolean serializeNulls() {
      return this.serializeNulls;
   }

   public boolean htmlSafe() {
      return this.htmlSafe;
   }

   private TypeAdapter<Number> doubleAdapter(boolean var1) {
      return var1 ? TypeAdapters.DOUBLE : new TypeAdapter<Number>() {
         public Double read(JsonReader var1) throws IOException {
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
               double var3 = var2.doubleValue();
               Gson.checkValidFloatingPoint(var3);
               var1.value(var3);
            }
         }
      };
   }

   private TypeAdapter<Number> floatAdapter(boolean var1) {
      return var1 ? TypeAdapters.FLOAT : new TypeAdapter<Number>() {
         public Float read(JsonReader var1) throws IOException {
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
               float var3 = var2.floatValue();
               Gson.checkValidFloatingPoint((double)var3);
               Object var4 = var2 instanceof Float ? var2 : var3;
               var1.value((Number)var4);
            }
         }
      };
   }

   static void checkValidFloatingPoint(double var0) {
      if (Double.isNaN(var0) || Double.isInfinite(var0)) {
         throw new IllegalArgumentException(var0 + " is not a valid double value as per JSON specification. To override this behavior, use GsonBuilder.serializeSpecialFloatingPointValues() method.");
      }
   }

   private static TypeAdapter<Number> longAdapter(LongSerializationPolicy var0) {
      return var0 == LongSerializationPolicy.DEFAULT ? TypeAdapters.LONG : new TypeAdapter<Number>() {
         public Number read(JsonReader var1) throws IOException {
            if (var1.peek() == JsonToken.NULL) {
               var1.nextNull();
               return null;
            } else {
               return var1.nextLong();
            }
         }

         public void write(JsonWriter var1, Number var2) throws IOException {
            if (var2 == null) {
               var1.nullValue();
            } else {
               var1.value(var2.toString());
            }
         }
      };
   }

   private static TypeAdapter<AtomicLong> atomicLongAdapter(final TypeAdapter<Number> var0) {
      return (new TypeAdapter<AtomicLong>() {
         public void write(JsonWriter var1, AtomicLong var2) throws IOException {
            var0.write(var1, var2.get());
         }

         public AtomicLong read(JsonReader var1) throws IOException {
            Number var2 = (Number)var0.read(var1);
            return new AtomicLong(var2.longValue());
         }
      }).nullSafe();
   }

   private static TypeAdapter<AtomicLongArray> atomicLongArrayAdapter(final TypeAdapter<Number> var0) {
      return (new TypeAdapter<AtomicLongArray>() {
         public void write(JsonWriter var1, AtomicLongArray var2) throws IOException {
            var1.beginArray();
            int var3 = 0;

            for(int var4 = var2.length(); var3 < var4; ++var3) {
               var0.write(var1, var2.get(var3));
            }

            var1.endArray();
         }

         public AtomicLongArray read(JsonReader var1) throws IOException {
            ArrayList var2 = new ArrayList();
            var1.beginArray();

            while(var1.hasNext()) {
               long var3 = ((Number)var0.read(var1)).longValue();
               var2.add(var3);
            }

            var1.endArray();
            int var6 = var2.size();
            AtomicLongArray var4 = new AtomicLongArray(var6);

            for(int var5 = 0; var5 < var6; ++var5) {
               var4.set(var5, (Long)var2.get(var5));
            }

            return var4;
         }
      }).nullSafe();
   }

   public <T> TypeAdapter<T> getAdapter(TypeToken<T> var1) {
      Objects.requireNonNull(var1, "type must not be null");
      TypeAdapter var2 = (TypeAdapter)this.typeTokenCache.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Object var3 = (Map)this.threadLocalAdapterResults.get();
         boolean var4 = false;
         if (var3 == null) {
            var3 = new HashMap();
            this.threadLocalAdapterResults.set(var3);
            var4 = true;
         } else {
            TypeAdapter var5 = (TypeAdapter)((Map)var3).get(var1);
            if (var5 != null) {
               return var5;
            }
         }

         TypeAdapter var12 = null;

         try {
            FutureTypeAdapter var6 = new FutureTypeAdapter();
            ((Map)var3).put(var1, var6);

            for(TypeAdapterFactory var8 : this.factories) {
               var12 = var8.create(this, var1);
               if (var12 != null) {
                  var6.setDelegate(var12);
                  ((Map)var3).put(var1, var12);
                  break;
               }
            }
         } finally {
            if (var4) {
               this.threadLocalAdapterResults.remove();
            }

         }

         if (var12 == null) {
            throw new IllegalArgumentException("GSON (2.11.0) cannot handle " + var1);
         } else {
            if (var4) {
               this.typeTokenCache.putAll((Map)var3);
            }

            return var12;
         }
      }
   }

   public <T> TypeAdapter<T> getAdapter(Class<T> var1) {
      return this.getAdapter(TypeToken.get(var1));
   }

   public <T> TypeAdapter<T> getDelegateAdapter(TypeAdapterFactory var1, TypeToken<T> var2) {
      Objects.requireNonNull(var1, "skipPast must not be null");
      Objects.requireNonNull(var2, "type must not be null");
      if (this.jsonAdapterFactory.isClassJsonAdapterFactory(var2, (TypeAdapterFactory)var1)) {
         var1 = this.jsonAdapterFactory;
      }

      boolean var3 = false;

      for(TypeAdapterFactory var5 : this.factories) {
         if (!var3) {
            if (var5 == var1) {
               var3 = true;
            }
         } else {
            TypeAdapter var6 = var5.create(this, var2);
            if (var6 != null) {
               return var6;
            }
         }
      }

      if (var3) {
         throw new IllegalArgumentException("GSON cannot serialize or deserialize " + var2);
      } else {
         return this.getAdapter(var2);
      }
   }

   public JsonElement toJsonTree(Object var1) {
      return (JsonElement)(var1 == null ? JsonNull.INSTANCE : this.toJsonTree(var1, var1.getClass()));
   }

   public JsonElement toJsonTree(Object var1, Type var2) {
      JsonTreeWriter var3 = new JsonTreeWriter();
      this.toJson(var1, var2, (JsonWriter)var3);
      return var3.get();
   }

   public String toJson(Object var1) {
      return var1 == null ? this.toJson((JsonElement)JsonNull.INSTANCE) : this.toJson((Object)var1, (Type)var1.getClass());
   }

   public String toJson(Object var1, Type var2) {
      StringWriter var3 = new StringWriter();
      this.toJson(var1, var2, (Appendable)var3);
      return var3.toString();
   }

   public void toJson(Object var1, Appendable var2) throws JsonIOException {
      if (var1 != null) {
         this.toJson(var1, var1.getClass(), (Appendable)var2);
      } else {
         this.toJson((JsonElement)JsonNull.INSTANCE, (Appendable)var2);
      }

   }

   public void toJson(Object var1, Type var2, Appendable var3) throws JsonIOException {
      try {
         JsonWriter var4 = this.newJsonWriter(Streams.writerForAppendable(var3));
         this.toJson(var1, var2, var4);
      } catch (IOException var5) {
         throw new JsonIOException(var5);
      }
   }

   public void toJson(Object var1, Type var2, JsonWriter var3) throws JsonIOException {
      TypeAdapter var4 = this.getAdapter(TypeToken.get(var2));
      Strictness var5 = var3.getStrictness();
      if (this.strictness != null) {
         var3.setStrictness(this.strictness);
      } else if (var3.getStrictness() == Strictness.LEGACY_STRICT) {
         var3.setStrictness(Strictness.LENIENT);
      }

      boolean var6 = var3.isHtmlSafe();
      boolean var7 = var3.getSerializeNulls();
      var3.setHtmlSafe(this.htmlSafe);
      var3.setSerializeNulls(this.serializeNulls);

      try {
         var4.write(var3, var1);
      } catch (IOException var13) {
         throw new JsonIOException(var13);
      } catch (AssertionError var14) {
         throw new AssertionError("AssertionError (GSON 2.11.0): " + var14.getMessage(), var14);
      } finally {
         var3.setStrictness(var5);
         var3.setHtmlSafe(var6);
         var3.setSerializeNulls(var7);
      }

   }

   public String toJson(JsonElement var1) {
      StringWriter var2 = new StringWriter();
      this.toJson((JsonElement)var1, (Appendable)var2);
      return var2.toString();
   }

   public void toJson(JsonElement var1, Appendable var2) throws JsonIOException {
      try {
         JsonWriter var3 = this.newJsonWriter(Streams.writerForAppendable(var2));
         this.toJson(var1, var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }
   }

   public void toJson(JsonElement var1, JsonWriter var2) throws JsonIOException {
      Strictness var3 = var2.getStrictness();
      boolean var4 = var2.isHtmlSafe();
      boolean var5 = var2.getSerializeNulls();
      var2.setHtmlSafe(this.htmlSafe);
      var2.setSerializeNulls(this.serializeNulls);
      if (this.strictness != null) {
         var2.setStrictness(this.strictness);
      } else if (var2.getStrictness() == Strictness.LEGACY_STRICT) {
         var2.setStrictness(Strictness.LENIENT);
      }

      try {
         Streams.write(var1, var2);
      } catch (IOException var11) {
         throw new JsonIOException(var11);
      } catch (AssertionError var12) {
         throw new AssertionError("AssertionError (GSON 2.11.0): " + var12.getMessage(), var12);
      } finally {
         var2.setStrictness(var3);
         var2.setHtmlSafe(var4);
         var2.setSerializeNulls(var5);
      }

   }

   public JsonWriter newJsonWriter(Writer var1) throws IOException {
      if (this.generateNonExecutableJson) {
         var1.write(")]}'\n");
      }

      JsonWriter var2 = new JsonWriter(var1);
      var2.setFormattingStyle(this.formattingStyle);
      var2.setHtmlSafe(this.htmlSafe);
      var2.setStrictness(this.strictness == null ? Strictness.LEGACY_STRICT : this.strictness);
      var2.setSerializeNulls(this.serializeNulls);
      return var2;
   }

   public JsonReader newJsonReader(Reader var1) {
      JsonReader var2 = new JsonReader(var1);
      var2.setStrictness(this.strictness == null ? Strictness.LEGACY_STRICT : this.strictness);
      return var2;
   }

   public <T> T fromJson(String var1, Class<T> var2) throws JsonSyntaxException {
      Object var3 = this.fromJson(var1, TypeToken.get(var2));
      return (T)Primitives.wrap(var2).cast(var3);
   }

   public <T> T fromJson(String var1, Type var2) throws JsonSyntaxException {
      return (T)this.fromJson(var1, TypeToken.get(var2));
   }

   public <T> T fromJson(String var1, TypeToken<T> var2) throws JsonSyntaxException {
      if (var1 == null) {
         return null;
      } else {
         StringReader var3 = new StringReader(var1);
         return (T)this.fromJson((Reader)var3, var2);
      }
   }

   public <T> T fromJson(Reader var1, Class<T> var2) throws JsonSyntaxException, JsonIOException {
      Object var3 = this.fromJson(var1, TypeToken.get(var2));
      return (T)Primitives.wrap(var2).cast(var3);
   }

   public <T> T fromJson(Reader var1, Type var2) throws JsonIOException, JsonSyntaxException {
      return (T)this.fromJson(var1, TypeToken.get(var2));
   }

   public <T> T fromJson(Reader var1, TypeToken<T> var2) throws JsonIOException, JsonSyntaxException {
      JsonReader var3 = this.newJsonReader(var1);
      Object var4 = this.fromJson(var3, var2);
      assertFullConsumption(var4, var3);
      return (T)var4;
   }

   public <T> T fromJson(JsonReader var1, Type var2) throws JsonIOException, JsonSyntaxException {
      return (T)this.fromJson(var1, TypeToken.get(var2));
   }

   public <T> T fromJson(JsonReader var1, TypeToken<T> var2) throws JsonIOException, JsonSyntaxException {
      boolean var3 = true;
      Strictness var4 = var1.getStrictness();
      if (this.strictness != null) {
         var1.setStrictness(this.strictness);
      } else if (var1.getStrictness() == Strictness.LEGACY_STRICT) {
         var1.setStrictness(Strictness.LENIENT);
      }

      TypeAdapter var6;
      try {
         JsonToken var5 = var1.peek();
         var3 = false;
         var6 = this.getAdapter(var2);
         Object var7 = var6.read(var1);
         return (T)var7;
      } catch (EOFException var14) {
         if (!var3) {
            throw new JsonSyntaxException(var14);
         }

         var6 = null;
      } catch (IllegalStateException var15) {
         throw new JsonSyntaxException(var15);
      } catch (IOException var16) {
         throw new JsonSyntaxException(var16);
      } catch (AssertionError var17) {
         throw new AssertionError("AssertionError (GSON 2.11.0): " + var17.getMessage(), var17);
      } finally {
         var1.setStrictness(var4);
      }

      return (T)var6;
   }

   public <T> T fromJson(JsonElement var1, Class<T> var2) throws JsonSyntaxException {
      Object var3 = this.fromJson(var1, TypeToken.get(var2));
      return (T)Primitives.wrap(var2).cast(var3);
   }

   public <T> T fromJson(JsonElement var1, Type var2) throws JsonSyntaxException {
      return (T)this.fromJson(var1, TypeToken.get(var2));
   }

   public <T> T fromJson(JsonElement var1, TypeToken<T> var2) throws JsonSyntaxException {
      return (T)(var1 == null ? null : this.fromJson((JsonReader)(new JsonTreeReader(var1)), var2));
   }

   private static void assertFullConsumption(Object var0, JsonReader var1) {
      try {
         if (var0 != null && var1.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonSyntaxException("JSON document was not fully consumed.");
         }
      } catch (MalformedJsonException var3) {
         throw new JsonSyntaxException(var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }
   }

   public String toString() {
      return "{serializeNulls:" + this.serializeNulls + ",factories:" + this.factories + ",instanceCreators:" + this.constructorConstructor + "}";
   }

   static {
      DEFAULT_FORMATTING_STYLE = FormattingStyle.COMPACT;
      DEFAULT_DATE_PATTERN = null;
      DEFAULT_FIELD_NAMING_STRATEGY = FieldNamingPolicy.IDENTITY;
      DEFAULT_OBJECT_TO_NUMBER_STRATEGY = ToNumberPolicy.DOUBLE;
      DEFAULT_NUMBER_TO_NUMBER_STRATEGY = ToNumberPolicy.LAZILY_PARSED_NUMBER;
   }

   static class FutureTypeAdapter<T> extends SerializationDelegatingTypeAdapter<T> {
      private TypeAdapter<T> delegate = null;

      public void setDelegate(TypeAdapter<T> var1) {
         if (this.delegate != null) {
            throw new AssertionError("Delegate is already set");
         } else {
            this.delegate = var1;
         }
      }

      private TypeAdapter<T> delegate() {
         TypeAdapter var1 = this.delegate;
         if (var1 == null) {
            throw new IllegalStateException("Adapter for type with cyclic dependency has been used before dependency has been resolved");
         } else {
            return var1;
         }
      }

      public TypeAdapter<T> getSerializationDelegate() {
         return this.delegate();
      }

      public T read(JsonReader var1) throws IOException {
         return (T)this.delegate().read(var1);
      }

      public void write(JsonWriter var1, T var2) throws IOException {
         this.delegate().write(var1, var2);
      }
   }
}
