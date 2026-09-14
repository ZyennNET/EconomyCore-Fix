package com.google.gson.internal.bind;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.ReflectionAccessFilterHelper;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ReflectiveTypeAdapterFactory implements TypeAdapterFactory {
   private final ConstructorConstructor constructorConstructor;
   private final FieldNamingStrategy fieldNamingPolicy;
   private final Excluder excluder;
   private final JsonAdapterAnnotationTypeAdapterFactory jsonAdapterFactory;
   private final List<ReflectionAccessFilter> reflectionFilters;

   public ReflectiveTypeAdapterFactory(ConstructorConstructor var1, FieldNamingStrategy var2, Excluder var3, JsonAdapterAnnotationTypeAdapterFactory var4, List<ReflectionAccessFilter> var5) {
      this.constructorConstructor = var1;
      this.fieldNamingPolicy = var2;
      this.excluder = var3;
      this.jsonAdapterFactory = var4;
      this.reflectionFilters = var5;
   }

   private boolean includeField(Field var1, boolean var2) {
      return !this.excluder.excludeField(var1, var2);
   }

   private List<String> getFieldNames(Field var1) {
      SerializedName var2 = (SerializedName)var1.getAnnotation(SerializedName.class);
      if (var2 == null) {
         String var6 = this.fieldNamingPolicy.translateName(var1);
         return Collections.singletonList(var6);
      } else {
         String var3 = var2.value();
         String[] var4 = var2.alternate();
         if (var4.length == 0) {
            return Collections.singletonList(var3);
         } else {
            ArrayList var5 = new ArrayList(var4.length + 1);
            var5.add(var3);
            Collections.addAll(var5, var4);
            return var5;
         }
      }
   }

   public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      Class var3 = var2.getRawType();
      if (!Object.class.isAssignableFrom(var3)) {
         return null;
      } else if (ReflectionHelper.isAnonymousOrNonStaticLocal(var3)) {
         return new TypeAdapter<T>() {
            public T read(JsonReader var1) throws IOException {
               var1.skipValue();
               return null;
            }

            public void write(JsonWriter var1, T var2) throws IOException {
               var1.nullValue();
            }

            public String toString() {
               return "AnonymousOrNonStaticLocalClassAdapter";
            }
         };
      } else {
         ReflectionAccessFilter.FilterResult var4 = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, var3);
         if (var4 == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
            throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + var3 + ". Register a TypeAdapter for this type or adjust the access filter.");
         } else {
            boolean var5 = var4 == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            if (ReflectionHelper.isRecord(var3)) {
               RecordAdapter var7 = new RecordAdapter(var3, this.getBoundFields(var1, var2, var3, var5, true), var5);
               return var7;
            } else {
               ObjectConstructor var6 = this.constructorConstructor.get(var2);
               return new FieldReflectionAdapter<T>(var6, this.getBoundFields(var1, var2, var3, var5, false));
            }
         }
      }
   }

   private static <M extends AccessibleObject & Member> void checkAccessible(Object var0, M var1) {
      if (!ReflectionAccessFilterHelper.canAccess(var1, Modifier.isStatic(((Member)var1).getModifiers()) ? null : var0)) {
         String var2 = ReflectionHelper.getAccessibleObjectDescription(var1, true);
         throw new JsonIOException(var2 + " is not accessible and ReflectionAccessFilter does not permit making it accessible. Register a TypeAdapter for the declaring type, adjust the access filter or increase the visibility of the element and its declaring type.");
      }
   }

   private BoundField createBoundField(Gson var1, Field var2, final Method var3, String var4, TypeToken<?> var5, boolean var6, final boolean var7) {
      final boolean var8 = Primitives.isPrimitive(var5.getRawType());
      int var9 = var2.getModifiers();
      final boolean var10 = Modifier.isStatic(var9) && Modifier.isFinal(var9);
      JsonAdapter var11 = (JsonAdapter)var2.getAnnotation(JsonAdapter.class);
      final TypeAdapter var12 = null;
      if (var11 != null) {
         var12 = this.jsonAdapterFactory.getTypeAdapter(this.constructorConstructor, var1, var5, var11, false);
      }

      boolean var13 = var12 != null;
      if (var12 == null) {
         var12 = var1.getAdapter(var5);
      }

      final Object var15;
      if (var6) {
         var15 = var13 ? var12 : new TypeAdapterRuntimeTypeWrapper(var1, var12, var5.getType());
      } else {
         var15 = var12;
      }

      return new BoundField(var4, var2) {
         void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException {
            if (var7) {
               if (var3 == null) {
                  ReflectiveTypeAdapterFactory.checkAccessible(var2, this.field);
               } else {
                  ReflectiveTypeAdapterFactory.checkAccessible(var2, var3);
               }
            }

            Object var3x;
            if (var3 != null) {
               try {
                  var3x = var3.invoke(var2);
               } catch (InvocationTargetException var6) {
                  String var5 = ReflectionHelper.getAccessibleObjectDescription(var3, false);
                  throw new JsonIOException("Accessor " + var5 + " threw exception", var6.getCause());
               }
            } else {
               var3x = this.field.get(var2);
            }

            if (var3x != var2) {
               var1.name(this.serializedName);
               ((TypeAdapter)var15).write(var1, var3x);
            }
         }

         void readIntoArray(JsonReader var1, int var2, Object[] var3x) throws IOException, JsonParseException {
            Object var4 = var12.read(var1);
            if (var4 == null && var8) {
               throw new JsonParseException("null is not allowed as value for record component '" + this.fieldName + "' of primitive type; at path " + var1.getPath());
            } else {
               var3x[var2] = var4;
            }
         }

         void readIntoField(JsonReader var1, Object var2) throws IOException, IllegalAccessException {
            Object var3x = var12.read(var1);
            if (var3x != null || !var8) {
               if (var7) {
                  ReflectiveTypeAdapterFactory.checkAccessible(var2, this.field);
               } else if (var10) {
                  String var4 = ReflectionHelper.getAccessibleObjectDescription(this.field, false);
                  throw new JsonIOException("Cannot set value of 'static final' " + var4);
               }

               this.field.set(var2, var3x);
            }

         }
      };
   }

   private static IllegalArgumentException createDuplicateFieldException(Class<?> var0, String var1, Field var2, Field var3) {
      throw new IllegalArgumentException("Class " + var0.getName() + " declares multiple JSON fields named '" + var1 + "'; conflict is caused by fields " + ReflectionHelper.fieldToString(var2) + " and " + ReflectionHelper.fieldToString(var3) + "\nSee " + TroubleshootingGuide.createUrl("duplicate-fields"));
   }

   private FieldsData getBoundFields(Gson var1, TypeToken<?> var2, Class<?> var3, boolean var4, boolean var5) {
      if (var3.isInterface()) {
         return ReflectiveTypeAdapterFactory.FieldsData.EMPTY;
      } else {
         LinkedHashMap var6 = new LinkedHashMap();
         LinkedHashMap var7 = new LinkedHashMap();

         for(Class var8 = var3; var3 != Object.class; var3 = var2.getRawType()) {
            Field[] var9 = var3.getDeclaredFields();
            if (var3 != var8 && var9.length > 0) {
               ReflectionAccessFilter.FilterResult var10 = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, var3);
               if (var10 == ReflectionAccessFilter.FilterResult.BLOCK_ALL) {
                  throw new JsonIOException("ReflectionAccessFilter does not permit using reflection for " + var3 + " (supertype of " + var8 + "). Register a TypeAdapter for this type or adjust the access filter.");
               }

               var4 = var10 == ReflectionAccessFilter.FilterResult.BLOCK_INACCESSIBLE;
            }

            for(Field var13 : var9) {
               boolean var14 = this.includeField(var13, true);
               boolean var15 = this.includeField(var13, false);
               if (var14 || var15) {
                  Method var16 = null;
                  if (var5) {
                     if (Modifier.isStatic(var13.getModifiers())) {
                        var15 = false;
                     } else {
                        var16 = ReflectionHelper.getAccessor(var3, var13);
                        if (!var4) {
                           ReflectionHelper.makeAccessible(var16);
                        }

                        if (var16.getAnnotation(SerializedName.class) != null && var13.getAnnotation(SerializedName.class) == null) {
                           String var25 = ReflectionHelper.getAccessibleObjectDescription(var16, false);
                           throw new JsonIOException("@SerializedName on " + var25 + " is not supported");
                        }
                     }
                  }

                  if (!var4 && var16 == null) {
                     ReflectionHelper.makeAccessible(var13);
                  }

                  Type var17 = $Gson$$Types.resolve(var2.getType(), var3, var13.getGenericType());
                  List var18 = this.getFieldNames(var13);
                  String var19 = (String)var18.get(0);
                  BoundField var20 = this.createBoundField(var1, var13, var16, var19, TypeToken.get(var17), var14, var4);
                  if (var15) {
                     for(String var22 : var18) {
                        BoundField var23 = (BoundField)var6.put(var22, var20);
                        if (var23 != null) {
                           throw createDuplicateFieldException(var8, var22, var23.field, var13);
                        }
                     }
                  }

                  if (var14) {
                     BoundField var26 = (BoundField)var7.put(var19, var20);
                     if (var26 != null) {
                        throw createDuplicateFieldException(var8, var19, var26.field, var13);
                     }
                  }
               }
            }

            var2 = TypeToken.get($Gson$$Types.resolve(var2.getType(), var3, var3.getGenericSuperclass()));
         }

         return new FieldsData(var6, new ArrayList(var7.values()));
      }
   }

   public abstract static class Adapter<T, A> extends TypeAdapter<T> {
      private final FieldsData fieldsData;

      Adapter(FieldsData var1) {
         this.fieldsData = var1;
      }

      public void write(JsonWriter var1, T var2) throws IOException {
         if (var2 == null) {
            var1.nullValue();
         } else {
            var1.beginObject();

            try {
               for(BoundField var4 : this.fieldsData.serializedFields) {
                  var4.write(var1, var2);
               }
            } catch (IllegalAccessException var5) {
               throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var5);
            }

            var1.endObject();
         }
      }

      public T read(JsonReader var1) throws IOException {
         if (var1.peek() == JsonToken.NULL) {
            var1.nextNull();
            return null;
         } else {
            Object var2 = this.createAccumulator();
            Map var3 = this.fieldsData.deserializedFields;

            try {
               var1.beginObject();

               while(var1.hasNext()) {
                  String var4 = var1.nextName();
                  BoundField var5 = (BoundField)var3.get(var4);
                  if (var5 == null) {
                     var1.skipValue();
                  } else {
                     this.readField(var2, var1, var5);
                  }
               }
            } catch (IllegalStateException var6) {
               throw new JsonSyntaxException(var6);
            } catch (IllegalAccessException var7) {
               throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var7);
            }

            var1.endObject();
            return (T)this.finalize(var2);
         }
      }

      abstract A createAccumulator();

      abstract void readField(A var1, JsonReader var2, BoundField var3) throws IllegalAccessException, IOException;

      abstract T finalize(A var1);
   }

   abstract static class BoundField {
      final String serializedName;
      final Field field;
      final String fieldName;

      protected BoundField(String var1, Field var2) {
         this.serializedName = var1;
         this.field = var2;
         this.fieldName = var2.getName();
      }

      abstract void write(JsonWriter var1, Object var2) throws IOException, IllegalAccessException;

      abstract void readIntoArray(JsonReader var1, int var2, Object[] var3) throws IOException, JsonParseException;

      abstract void readIntoField(JsonReader var1, Object var2) throws IOException, IllegalAccessException;
   }

   private static final class FieldReflectionAdapter<T> extends Adapter<T, T> {
      private final ObjectConstructor<T> constructor;

      FieldReflectionAdapter(ObjectConstructor<T> var1, FieldsData var2) {
         super(var2);
         this.constructor = var1;
      }

      T createAccumulator() {
         return this.constructor.construct();
      }

      void readField(T var1, JsonReader var2, BoundField var3) throws IllegalAccessException, IOException {
         var3.readIntoField(var2, var1);
      }

      T finalize(T var1) {
         return var1;
      }
   }

   private static class FieldsData {
      public static final FieldsData EMPTY = new FieldsData(Collections.emptyMap(), Collections.emptyList());
      public final Map<String, BoundField> deserializedFields;
      public final List<BoundField> serializedFields;

      public FieldsData(Map<String, BoundField> var1, List<BoundField> var2) {
         this.deserializedFields = var1;
         this.serializedFields = var2;
      }
   }

   private static final class RecordAdapter<T> extends Adapter<T, Object[]> {
      static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = primitiveDefaults();
      private final Constructor<T> constructor;
      private final Object[] constructorArgsDefaults;
      private final Map<String, Integer> componentIndices = new HashMap();

      RecordAdapter(Class<T> var1, FieldsData var2, boolean var3) {
         super(var2);
         this.constructor = ReflectionHelper.<T>getCanonicalRecordConstructor(var1);
         if (var3) {
            ReflectiveTypeAdapterFactory.checkAccessible((Object)null, this.constructor);
         } else {
            ReflectionHelper.makeAccessible(this.constructor);
         }

         String[] var4 = ReflectionHelper.getRecordComponentNames(var1);

         for(int var5 = 0; var5 < var4.length; ++var5) {
            this.componentIndices.put(var4[var5], var5);
         }

         Class[] var7 = this.constructor.getParameterTypes();
         this.constructorArgsDefaults = new Object[var7.length];

         for(int var6 = 0; var6 < var7.length; ++var6) {
            this.constructorArgsDefaults[var6] = PRIMITIVE_DEFAULTS.get(var7[var6]);
         }

      }

      private static Map<Class<?>, Object> primitiveDefaults() {
         HashMap var0 = new HashMap();
         var0.put(Byte.TYPE, (byte)0);
         var0.put(Short.TYPE, (short)0);
         var0.put(Integer.TYPE, 0);
         var0.put(Long.TYPE, 0L);
         var0.put(Float.TYPE, 0.0F);
         var0.put(Double.TYPE, (double)0.0F);
         var0.put(Character.TYPE, '\u0000');
         var0.put(Boolean.TYPE, false);
         return var0;
      }

      Object[] createAccumulator() {
         return this.constructorArgsDefaults.clone();
      }

      void readField(Object[] var1, JsonReader var2, BoundField var3) throws IOException {
         Integer var4 = (Integer)this.componentIndices.get(var3.fieldName);
         if (var4 == null) {
            throw new IllegalStateException("Could not find the index in the constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' for field with name '" + var3.fieldName + "', unable to determine which argument in the constructor the field corresponds to. This is unexpected behavior, as we expect the RecordComponents to have the same names as the fields in the Java class, and that the order of the RecordComponents is the same as the order of the canonical constructor parameters.");
         } else {
            var3.readIntoArray(var2, var4, var1);
         }
      }

      T finalize(Object[] var1) {
         try {
            return (T)this.constructor.newInstance(var1);
         } catch (IllegalAccessException var3) {
            throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var3);
         } catch (IllegalArgumentException | InstantiationException var4) {
            throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(var1), var4);
         } catch (InvocationTargetException var5) {
            throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(this.constructor) + "' with args " + Arrays.toString(var1), var5.getCause());
         }
      }
   }
}
