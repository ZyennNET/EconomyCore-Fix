package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.ToNumberPolicy;
import com.google.gson.ToNumberStrategy;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ObjectTypeAdapter extends TypeAdapter<Object> {
   private static final TypeAdapterFactory DOUBLE_FACTORY;
   private final Gson gson;
   private final ToNumberStrategy toNumberStrategy;

   private ObjectTypeAdapter(Gson var1, ToNumberStrategy var2) {
      this.gson = var1;
      this.toNumberStrategy = var2;
   }

   private static TypeAdapterFactory newFactory(final ToNumberStrategy var0) {
      return new TypeAdapterFactory() {
         public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
            return var2.getRawType() == Object.class ? new ObjectTypeAdapter(var1, var0) : null;
         }
      };
   }

   public static TypeAdapterFactory getFactory(ToNumberStrategy var0) {
      return var0 == ToNumberPolicy.DOUBLE ? DOUBLE_FACTORY : newFactory(var0);
   }

   private Object tryBeginNesting(JsonReader var1, JsonToken var2) throws IOException {
      switch (var2) {
         case BEGIN_ARRAY:
            var1.beginArray();
            return new ArrayList();
         case BEGIN_OBJECT:
            var1.beginObject();
            return new LinkedTreeMap();
         default:
            return null;
      }
   }

   private Object readTerminal(JsonReader var1, JsonToken var2) throws IOException {
      switch (var2) {
         case STRING:
            return var1.nextString();
         case NUMBER:
            return this.toNumberStrategy.readNumber(var1);
         case BOOLEAN:
            return var1.nextBoolean();
         case NULL:
            var1.nextNull();
            return null;
         default:
            throw new IllegalStateException("Unexpected token: " + var2);
      }
   }

   public Object read(JsonReader var1) throws IOException {
      JsonToken var3 = var1.peek();
      Object var2 = this.tryBeginNesting(var1, var3);
      if (var2 == null) {
         return this.readTerminal(var1, var3);
      } else {
         ArrayDeque var4 = new ArrayDeque();

         while(true) {
            while(!var1.hasNext()) {
               if (var2 instanceof List) {
                  var1.endArray();
               } else {
                  var1.endObject();
               }

               if (var4.isEmpty()) {
                  return var2;
               }

               var2 = var4.removeLast();
            }

            String var5 = null;
            if (var2 instanceof Map) {
               var5 = var1.nextName();
            }

            var3 = var1.peek();
            Object var6 = this.tryBeginNesting(var1, var3);
            boolean var7 = var6 != null;
            if (var6 == null) {
               var6 = this.readTerminal(var1, var3);
            }

            if (var2 instanceof List) {
               List var8 = (List)var2;
               var8.add(var6);
            } else {
               Map var10 = (Map)var2;
               var10.put(var5, var6);
            }

            if (var7) {
               var4.addLast(var2);
               var2 = var6;
            }
         }
      }
   }

   public void write(JsonWriter var1, Object var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         TypeAdapter var3 = this.gson.getAdapter(var2.getClass());
         if (var3 instanceof ObjectTypeAdapter) {
            var1.beginObject();
            var1.endObject();
         } else {
            var3.write(var1, var2);
         }
      }
   }

   static {
      DOUBLE_FACTORY = newFactory(ToNumberPolicy.DOUBLE);
   }
}
