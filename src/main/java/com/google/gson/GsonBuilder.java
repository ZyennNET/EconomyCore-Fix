package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.internal.$Gson$$Preconditions;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.bind.DefaultDateTypeAdapter;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.internal.sql.SqlTypesSupport;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class GsonBuilder {
   private Excluder excluder;
   private LongSerializationPolicy longSerializationPolicy;
   private FieldNamingStrategy fieldNamingPolicy;
   private final Map<Type, InstanceCreator<?>> instanceCreators;
   private final List<TypeAdapterFactory> factories;
   private final List<TypeAdapterFactory> hierarchyFactories;
   private boolean serializeNulls;
   private String datePattern;
   private int dateStyle;
   private int timeStyle;
   private boolean complexMapKeySerialization;
   private boolean serializeSpecialFloatingPointValues;
   private boolean escapeHtmlChars;
   private FormattingStyle formattingStyle;
   private boolean generateNonExecutableJson;
   private Strictness strictness;
   private boolean useJdkUnsafe;
   private ToNumberStrategy objectToNumberStrategy;
   private ToNumberStrategy numberToNumberStrategy;
   private final ArrayDeque<ReflectionAccessFilter> reflectionFilters;

   public GsonBuilder() {
      this.excluder = Excluder.DEFAULT;
      this.longSerializationPolicy = LongSerializationPolicy.DEFAULT;
      this.fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
      this.instanceCreators = new HashMap();
      this.factories = new ArrayList();
      this.hierarchyFactories = new ArrayList();
      this.serializeNulls = false;
      this.datePattern = Gson.DEFAULT_DATE_PATTERN;
      this.dateStyle = 2;
      this.timeStyle = 2;
      this.complexMapKeySerialization = false;
      this.serializeSpecialFloatingPointValues = false;
      this.escapeHtmlChars = true;
      this.formattingStyle = Gson.DEFAULT_FORMATTING_STYLE;
      this.generateNonExecutableJson = false;
      this.strictness = Gson.DEFAULT_STRICTNESS;
      this.useJdkUnsafe = true;
      this.objectToNumberStrategy = Gson.DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
      this.numberToNumberStrategy = Gson.DEFAULT_NUMBER_TO_NUMBER_STRATEGY;
      this.reflectionFilters = new ArrayDeque();
   }

   GsonBuilder(Gson var1) {
      this.excluder = Excluder.DEFAULT;
      this.longSerializationPolicy = LongSerializationPolicy.DEFAULT;
      this.fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
      this.instanceCreators = new HashMap();
      this.factories = new ArrayList();
      this.hierarchyFactories = new ArrayList();
      this.serializeNulls = false;
      this.datePattern = Gson.DEFAULT_DATE_PATTERN;
      this.dateStyle = 2;
      this.timeStyle = 2;
      this.complexMapKeySerialization = false;
      this.serializeSpecialFloatingPointValues = false;
      this.escapeHtmlChars = true;
      this.formattingStyle = Gson.DEFAULT_FORMATTING_STYLE;
      this.generateNonExecutableJson = false;
      this.strictness = Gson.DEFAULT_STRICTNESS;
      this.useJdkUnsafe = true;
      this.objectToNumberStrategy = Gson.DEFAULT_OBJECT_TO_NUMBER_STRATEGY;
      this.numberToNumberStrategy = Gson.DEFAULT_NUMBER_TO_NUMBER_STRATEGY;
      this.reflectionFilters = new ArrayDeque();
      this.excluder = var1.excluder;
      this.fieldNamingPolicy = var1.fieldNamingStrategy;
      this.instanceCreators.putAll(var1.instanceCreators);
      this.serializeNulls = var1.serializeNulls;
      this.complexMapKeySerialization = var1.complexMapKeySerialization;
      this.generateNonExecutableJson = var1.generateNonExecutableJson;
      this.escapeHtmlChars = var1.htmlSafe;
      this.formattingStyle = var1.formattingStyle;
      this.strictness = var1.strictness;
      this.serializeSpecialFloatingPointValues = var1.serializeSpecialFloatingPointValues;
      this.longSerializationPolicy = var1.longSerializationPolicy;
      this.datePattern = var1.datePattern;
      this.dateStyle = var1.dateStyle;
      this.timeStyle = var1.timeStyle;
      this.factories.addAll(var1.builderFactories);
      this.hierarchyFactories.addAll(var1.builderHierarchyFactories);
      this.useJdkUnsafe = var1.useJdkUnsafe;
      this.objectToNumberStrategy = var1.objectToNumberStrategy;
      this.numberToNumberStrategy = var1.numberToNumberStrategy;
      this.reflectionFilters.addAll(var1.reflectionFilters);
   }

   @CanIgnoreReturnValue
   public GsonBuilder setVersion(double var1) {
      if (!Double.isNaN(var1) && !(var1 < (double)0.0F)) {
         this.excluder = this.excluder.withVersion(var1);
         return this;
      } else {
         throw new IllegalArgumentException("Invalid version: " + var1);
      }
   }

   @CanIgnoreReturnValue
   public GsonBuilder excludeFieldsWithModifiers(int... var1) {
      Objects.requireNonNull(var1);
      this.excluder = this.excluder.withModifiers(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder generateNonExecutableJson() {
      this.generateNonExecutableJson = true;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder excludeFieldsWithoutExposeAnnotation() {
      this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder serializeNulls() {
      this.serializeNulls = true;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder enableComplexMapKeySerialization() {
      this.complexMapKeySerialization = true;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder disableInnerClassSerialization() {
      this.excluder = this.excluder.disableInnerClassSerialization();
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy var1) {
      this.longSerializationPolicy = (LongSerializationPolicy)Objects.requireNonNull(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy var1) {
      return this.setFieldNamingStrategy(var1);
   }

   @CanIgnoreReturnValue
   public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy var1) {
      this.fieldNamingPolicy = (FieldNamingStrategy)Objects.requireNonNull(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setObjectToNumberStrategy(ToNumberStrategy var1) {
      this.objectToNumberStrategy = (ToNumberStrategy)Objects.requireNonNull(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setNumberToNumberStrategy(ToNumberStrategy var1) {
      this.numberToNumberStrategy = (ToNumberStrategy)Objects.requireNonNull(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setExclusionStrategies(ExclusionStrategy... var1) {
      Objects.requireNonNull(var1);

      for(ExclusionStrategy var5 : var1) {
         this.excluder = this.excluder.withExclusionStrategy(var5, true, true);
      }

      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy var1) {
      Objects.requireNonNull(var1);
      this.excluder = this.excluder.withExclusionStrategy(var1, true, false);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy var1) {
      Objects.requireNonNull(var1);
      this.excluder = this.excluder.withExclusionStrategy(var1, false, true);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setPrettyPrinting() {
      return this.setFormattingStyle(FormattingStyle.PRETTY);
   }

   @CanIgnoreReturnValue
   public GsonBuilder setFormattingStyle(FormattingStyle var1) {
      this.formattingStyle = (FormattingStyle)Objects.requireNonNull(var1);
      return this;
   }

   @Deprecated
   @InlineMe(
      replacement = "this.setStrictness(Strictness.LENIENT)",
      imports = {"com.google.gson.Strictness"}
   )
   @CanIgnoreReturnValue
   public GsonBuilder setLenient() {
      return this.setStrictness(Strictness.LENIENT);
   }

   @CanIgnoreReturnValue
   public GsonBuilder setStrictness(Strictness var1) {
      this.strictness = (Strictness)Objects.requireNonNull(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder disableHtmlEscaping() {
      this.escapeHtmlChars = false;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setDateFormat(String var1) {
      if (var1 != null) {
         try {
            new SimpleDateFormat(var1);
         } catch (IllegalArgumentException var3) {
            throw new IllegalArgumentException("The date pattern '" + var1 + "' is not valid", var3);
         }
      }

      this.datePattern = var1;
      return this;
   }

   @Deprecated
   @CanIgnoreReturnValue
   public GsonBuilder setDateFormat(int var1) {
      this.dateStyle = checkDateFormatStyle(var1);
      this.datePattern = null;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder setDateFormat(int var1, int var2) {
      this.dateStyle = checkDateFormatStyle(var1);
      this.timeStyle = checkDateFormatStyle(var2);
      this.datePattern = null;
      return this;
   }

   private static int checkDateFormatStyle(int var0) {
      if (var0 >= 0 && var0 <= 3) {
         return var0;
      } else {
         throw new IllegalArgumentException("Invalid style: " + var0);
      }
   }

   @CanIgnoreReturnValue
   public GsonBuilder registerTypeAdapter(Type var1, Object var2) {
      Objects.requireNonNull(var1);
      $Gson$$Preconditions.checkArgument(var2 instanceof JsonSerializer || var2 instanceof JsonDeserializer || var2 instanceof InstanceCreator || var2 instanceof TypeAdapter);
      if (isTypeObjectOrJsonElement(var1)) {
         throw new IllegalArgumentException("Cannot override built-in adapter for " + var1);
      } else {
         if (var2 instanceof InstanceCreator) {
            this.instanceCreators.put(var1, (InstanceCreator)var2);
         }

         if (var2 instanceof JsonSerializer || var2 instanceof JsonDeserializer) {
            TypeToken var3 = TypeToken.get(var1);
            this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(var3, var2));
         }

         if (var2 instanceof TypeAdapter) {
            TypeAdapterFactory var4 = TypeAdapters.newFactory(TypeToken.get(var1), (TypeAdapter)var2);
            this.factories.add(var4);
         }

         return this;
      }
   }

   private static boolean isTypeObjectOrJsonElement(Type var0) {
      return var0 instanceof Class && (var0 == Object.class || JsonElement.class.isAssignableFrom((Class)var0));
   }

   @CanIgnoreReturnValue
   public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory var1) {
      Objects.requireNonNull(var1);
      this.factories.add(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder registerTypeHierarchyAdapter(Class<?> var1, Object var2) {
      Objects.requireNonNull(var1);
      $Gson$$Preconditions.checkArgument(var2 instanceof JsonSerializer || var2 instanceof JsonDeserializer || var2 instanceof TypeAdapter);
      if (JsonElement.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Cannot override built-in adapter for " + var1);
      } else {
         if (var2 instanceof JsonDeserializer || var2 instanceof JsonSerializer) {
            this.hierarchyFactories.add(TreeTypeAdapter.newTypeHierarchyFactory(var1, var2));
         }

         if (var2 instanceof TypeAdapter) {
            TypeAdapterFactory var3 = TypeAdapters.newTypeHierarchyFactory(var1, (TypeAdapter)var2);
            this.factories.add(var3);
         }

         return this;
      }
   }

   @CanIgnoreReturnValue
   public GsonBuilder serializeSpecialFloatingPointValues() {
      this.serializeSpecialFloatingPointValues = true;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder disableJdkUnsafe() {
      this.useJdkUnsafe = false;
      return this;
   }

   @CanIgnoreReturnValue
   public GsonBuilder addReflectionAccessFilter(ReflectionAccessFilter var1) {
      Objects.requireNonNull(var1);
      this.reflectionFilters.addFirst(var1);
      return this;
   }

   public Gson create() {
      ArrayList var1 = new ArrayList(this.factories.size() + this.hierarchyFactories.size() + 3);
      var1.addAll(this.factories);
      Collections.reverse(var1);
      ArrayList var2 = new ArrayList(this.hierarchyFactories);
      Collections.reverse(var2);
      var1.addAll(var2);
      addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, var1);
      return new Gson(this.excluder, this.fieldNamingPolicy, new HashMap(this.instanceCreators), this.serializeNulls, this.complexMapKeySerialization, this.generateNonExecutableJson, this.escapeHtmlChars, this.formattingStyle, this.strictness, this.serializeSpecialFloatingPointValues, this.useJdkUnsafe, this.longSerializationPolicy, this.datePattern, this.dateStyle, this.timeStyle, new ArrayList(this.factories), new ArrayList(this.hierarchyFactories), var1, this.objectToNumberStrategy, this.numberToNumberStrategy, new ArrayList(this.reflectionFilters));
   }

   private static void addTypeAdaptersForDate(String var0, int var1, int var2, List<TypeAdapterFactory> var3) {
      boolean var5 = SqlTypesSupport.SUPPORTS_SQL_TYPES;
      TypeAdapterFactory var6 = null;
      TypeAdapterFactory var7 = null;
      TypeAdapterFactory var4;
      if (var0 != null && !var0.trim().isEmpty()) {
         var4 = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(var0);
         if (var5) {
            var6 = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(var0);
            var7 = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(var0);
         }
      } else {
         if (var1 == 2 && var2 == 2) {
            return;
         }

         var4 = DefaultDateTypeAdapter.DateType.DATE.createAdapterFactory(var1, var2);
         if (var5) {
            var6 = SqlTypesSupport.TIMESTAMP_DATE_TYPE.createAdapterFactory(var1, var2);
            var7 = SqlTypesSupport.DATE_DATE_TYPE.createAdapterFactory(var1, var2);
         }
      }

      var3.add(var4);
      if (var5) {
         var3.add(var6);
         var3.add(var7);
      }

   }
}
