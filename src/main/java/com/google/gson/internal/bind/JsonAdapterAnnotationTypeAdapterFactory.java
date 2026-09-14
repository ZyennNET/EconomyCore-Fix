package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.reflect.TypeToken;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
   private static final TypeAdapterFactory TREE_TYPE_CLASS_DUMMY_FACTORY = new DummyTypeAdapterFactory();
   private static final TypeAdapterFactory TREE_TYPE_FIELD_DUMMY_FACTORY = new DummyTypeAdapterFactory();
   private final ConstructorConstructor constructorConstructor;
   private final ConcurrentMap<Class<?>, TypeAdapterFactory> adapterFactoryMap;

   public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor var1) {
      this.constructorConstructor = var1;
      this.adapterFactoryMap = new ConcurrentHashMap();
   }

   private static JsonAdapter getAnnotation(Class<?> var0) {
      return (JsonAdapter)var0.getAnnotation(JsonAdapter.class);
   }

   public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
      Class var3 = var2.getRawType();
      JsonAdapter var4 = getAnnotation(var3);
      return var4 == null ? null : this.getTypeAdapter(this.constructorConstructor, var1, var2, var4, true);
   }

   private static Object createAdapter(ConstructorConstructor var0, Class<?> var1) {
      return var0.get(TypeToken.get(var1)).construct();
   }

   private TypeAdapterFactory putFactoryAndGetCurrent(Class<?> var1, TypeAdapterFactory var2) {
      TypeAdapterFactory var3 = (TypeAdapterFactory)this.adapterFactoryMap.putIfAbsent(var1, var2);
      return var3 != null ? var3 : var2;
   }

   TypeAdapter<?> getTypeAdapter(ConstructorConstructor var1, Gson var2, TypeToken<?> var3, JsonAdapter var4, boolean var5) {
      Object var6 = createAdapter(var1, var4.value());
      boolean var8 = var4.nullSafe();
      Object var7;
      if (var6 instanceof TypeAdapter) {
         var7 = (TypeAdapter)var6;
      } else if (var6 instanceof TypeAdapterFactory) {
         TypeAdapterFactory var9 = (TypeAdapterFactory)var6;
         if (var5) {
            var9 = this.putFactoryAndGetCurrent(var3.getRawType(), var9);
         }

         var7 = var9.create(var2, var3);
      } else {
         if (!(var6 instanceof JsonSerializer) && !(var6 instanceof JsonDeserializer)) {
            throw new IllegalArgumentException("Invalid attempt to bind an instance of " + var6.getClass().getName() + " as a @JsonAdapter for " + var3.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer.");
         }

         JsonSerializer var13 = var6 instanceof JsonSerializer ? (JsonSerializer)var6 : null;
         JsonDeserializer var10 = var6 instanceof JsonDeserializer ? (JsonDeserializer)var6 : null;
         TypeAdapterFactory var11;
         if (var5) {
            var11 = TREE_TYPE_CLASS_DUMMY_FACTORY;
         } else {
            var11 = TREE_TYPE_FIELD_DUMMY_FACTORY;
         }

         TreeTypeAdapter var12 = new TreeTypeAdapter(var13, var10, var2, var3, var11, var8);
         var7 = var12;
         var8 = false;
      }

      if (var7 != null && var8) {
         var7 = ((TypeAdapter)var7).nullSafe();
      }

      return (TypeAdapter<?>)var7;
   }

   public boolean isClassJsonAdapterFactory(TypeToken<?> var1, TypeAdapterFactory var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      if (var2 == TREE_TYPE_CLASS_DUMMY_FACTORY) {
         return true;
      } else {
         Class var3 = var1.getRawType();
         TypeAdapterFactory var4 = (TypeAdapterFactory)this.adapterFactoryMap.get(var3);
         if (var4 != null) {
            return var4 == var2;
         } else {
            JsonAdapter var5 = getAnnotation(var3);
            if (var5 == null) {
               return false;
            } else {
               Class var6 = var5.value();
               if (!TypeAdapterFactory.class.isAssignableFrom(var6)) {
                  return false;
               } else {
                  Object var7 = createAdapter(this.constructorConstructor, var6);
                  TypeAdapterFactory var8 = (TypeAdapterFactory)var7;
                  return this.putFactoryAndGetCurrent(var3, var8) == var2;
               }
            }
         }
      }
   }

   private static class DummyTypeAdapterFactory implements TypeAdapterFactory {
      private DummyTypeAdapterFactory() {
      }

      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         throw new AssertionError("Factory should not be used");
      }
   }
}
