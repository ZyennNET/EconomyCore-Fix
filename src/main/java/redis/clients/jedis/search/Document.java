package redis.clients.jedis.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.SafeEncoder;

public class Document implements Serializable {
   private static final long serialVersionUID = 4884173545291367373L;
   private final String id;
   private Double score;
   private final Map<String, Object> fields;
   static Builder<Document> SEARCH_DOCUMENT = new Builder<Document>() {
      private static final String ID_STR = "id";
      private static final String SCORE_STR = "score";
      private static final String FIELDS_STR = "extra_attributes";

      public Document build(Object var1) {
         List var2 = (List)var1;
         String var3 = null;
         Double var4 = null;
         Map var5 = null;

         for(KeyValue var7 : var2) {
            switch ((String)BuilderFactory.STRING.build(var7.getKey())) {
               case "id":
                  var3 = BuilderFactory.STRING.build(var7.getValue());
                  break;
               case "score":
                  var4 = BuilderFactory.DOUBLE.build(var7.getValue());
                  break;
               case "extra_attributes":
                  var5 = BuilderFactory.ENCODED_OBJECT_MAP.build(var7.getValue());
            }
         }

         return new Document(var3, var4, var5);
      }
   };

   public Document(String var1) {
      this(var1, (double)1.0F);
   }

   public Document(String var1, double var2) {
      this(var1, new HashMap(), var2);
   }

   public Document(String var1, Map<String, Object> var2) {
      this(var1, var2, (double)1.0F);
   }

   public Document(String var1, Map<String, Object> var2, double var3) {
      this.id = var1;
      this.fields = var2;
      this.score = var3;
   }

   private Document(String var1, Double var2, Map<String, Object> var3) {
      this.id = var1;
      this.score = var2;
      this.fields = var3;
   }

   public Iterable<Map.Entry<String, Object>> getProperties() {
      return this.fields.entrySet();
   }

   public static Document load(String var0, double var1, byte[] var3, List<byte[]> var4) {
      return load(var0, var1, var4, true);
   }

   public static Document load(String var0, double var1, List<byte[]> var3, boolean var4) {
      Document var5 = new Document(var0, var1);
      if (var3 != null) {
         for(int var6 = 0; var6 < var3.size(); var6 += 2) {
            byte[] var7 = (byte[])var3.get(var6);
            byte[] var8 = (byte[])var3.get(var6 + 1);
            String var9 = SafeEncoder.encode(var7);
            Object var10 = var8 == null ? null : (var4 ? SafeEncoder.encode(var8) : var8);
            var5.set(var9, var10);
         }
      }

      return var5;
   }

   public String getId() {
      return this.id;
   }

   public Double getScore() {
      return this.score;
   }

   public Object get(String var1) {
      return this.fields.get(var1);
   }

   public String getString(String var1) {
      Object var2 = this.fields.get(var1);
      if (var2 instanceof String) {
         return (String)var2;
      } else {
         return var2 instanceof byte[] ? SafeEncoder.encode((byte[])var2) : var2.toString();
      }
   }

   public boolean hasProperty(String var1) {
      return this.fields.containsKey(var1);
   }

   public Document set(String var1, Object var2) {
      this.fields.put(var1, var2);
      return this;
   }

   public Document setScore(float var1) {
      this.score = (double)var1;
      return this;
   }

   public String toString() {
      return "id:" + this.getId() + ", score: " + this.getScore() + ", properties:" + this.getProperties();
   }
}
