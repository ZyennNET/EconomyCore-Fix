package redis.clients.jedis.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.exceptions.JedisException;

public final class JsonBuilderFactory {
   public static final Builder<Class<?>> JSON_TYPE = new Builder<Class<?>>() {
      public Class<?> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            switch ((String)BuilderFactory.STRING.build(var1)) {
               case "null":
                  return null;
               case "boolean":
                  return Boolean.TYPE;
               case "integer":
                  return Integer.TYPE;
               case "number":
                  return Float.TYPE;
               case "string":
                  return String.class;
               case "object":
                  return Object.class;
               case "array":
                  return List.class;
               default:
                  throw new JedisException("Unknown type: " + var2);
            }
         }
      }

      public String toString() {
         return "Class<?>";
      }
   };
   public static final Builder<List<Class<?>>> JSON_TYPE_LIST = new Builder<List<Class<?>>>() {
      public List<Class<?>> build(Object var1) {
         List var2 = (List)var1;
         ArrayList var3 = new ArrayList(var2.size());

         for(Object var5 : var2) {
            try {
               var3.add(JsonBuilderFactory.JSON_TYPE.build(var5));
            } catch (JedisException var7) {
               var3.add((Object)null);
            }
         }

         return var3;
      }
   };
   public static final Builder<List<List<Class<?>>>> JSON_TYPE_RESPONSE_RESP3 = new Builder<List<List<Class<?>>>>() {
      public List<List<Class<?>>> build(Object var1) {
         Stream var10000 = ((List)var1).stream();
         Builder var10001 = JsonBuilderFactory.JSON_TYPE_LIST;
         var10001.getClass();
         return (List)var10000.map(var10001::build).collect(Collectors.toList());
      }
   };
   public static final Builder<List<Class<?>>> JSON_TYPE_RESPONSE_RESP3_COMPATIBLE = new Builder<List<Class<?>>>() {
      public List<Class<?>> build(Object var1) {
         List var2 = JsonBuilderFactory.JSON_TYPE_RESPONSE_RESP3.build(var1);
         return var2 == null ? null : (List)var2.get(0);
      }
   };
   public static final Builder<Object> JSON_OBJECT = new Builder<Object>() {
      public Object build(Object var1) {
         if (var1 == null) {
            return null;
         } else if (!(var1 instanceof byte[])) {
            return var1;
         } else {
            String var2 = BuilderFactory.STRING.build(var1);
            if (var2.charAt(0) == '{') {
               try {
                  return new JSONObject(var2);
               } catch (Exception var5) {
               }
            } else if (var2.charAt(0) == '[') {
               try {
                  return new JSONArray(var2);
               } catch (Exception var4) {
               }
            }

            return var2;
         }
      }
   };
   public static final Builder<JSONArray> JSON_ARRAY = new Builder<JSONArray>() {
      public JSONArray build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            String var2 = BuilderFactory.STRING.build(var1);

            try {
               return new JSONArray(var2);
            } catch (JSONException var4) {
               throw new JedisException(var4);
            }
         }
      }
   };
   public static final Builder<Object> JSON_ARRAY_OR_DOUBLE_LIST = new Builder<Object>() {
      public Object build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            return var1 instanceof List ? BuilderFactory.DOUBLE_LIST.build(var1) : JsonBuilderFactory.JSON_ARRAY.build(var1);
         }
      }
   };
   public static final Builder<List<JSONArray>> JSON_ARRAY_LIST = new Builder<List<JSONArray>>() {
      public List<JSONArray> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return (List)var2.stream().map((var0) -> JsonBuilderFactory.JSON_ARRAY.build(var0)).collect(Collectors.toList());
         }
      }
   };

   private JsonBuilderFactory() {
      throw new InstantiationError("Must not instantiate this class");
   }
}
