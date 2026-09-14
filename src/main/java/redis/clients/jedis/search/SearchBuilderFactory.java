package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.DoublePrecision;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.SafeEncoder;

public final class SearchBuilderFactory {
   public static final Builder<Map<String, Object>> SEARCH_PROFILE_PROFILE = new Builder<Map<String, Object>>() {
      private final String ITERATORS_PROFILE_STR = "Iterators profile";
      private final String CHILD_ITERATORS_STR = "Child iterators";
      private final String RESULT_PROCESSORS_PROFILE_STR = "Result processors profile";

      public Map<String, Object> build(Object var1) {
         List var2 = (List)SafeEncoder.encodeObject(var1);
         HashMap var3 = new HashMap(var2.size(), 1.0F);

         for(Object var5 : var2) {
            List var6 = (List)var5;
            String var7 = (String)var6.get(0);
            Object var8;
            if (var6.size() == 2) {
               Object var11 = var6.get(1);
               if (var7.equals("Iterators profile")) {
                  var8 = this.parseIterators(var11);
               } else if (var7.endsWith(" time")) {
                  var8 = DoublePrecision.parseEncodedFloatingPointNumber(var11);
               } else {
                  var8 = var11;
               }
            } else if (var6.size() <= 2) {
               var8 = null;
            } else if (!var7.equals("Result processors profile")) {
               var8 = var6.subList(1, var6.size());
            } else {
               ArrayList var9 = new ArrayList(var6.size() - 1);

               for(int var10 = 1; var10 < var6.size(); ++var10) {
                  var9.add(this.parseResultProcessors(var6.get(var10)));
               }

               var8 = var9;
            }

            var3.put(var7, var8);
         }

         return var3;
      }

      private Map<String, Object> parseResultProcessors(Object var1) {
         List var2 = (List)var1;
         HashMap var3 = new HashMap(var2.size() / 2, 1.0F);

         for(int var4 = 0; var4 < var2.size(); var4 += 2) {
            String var5 = (String)var2.get(var4);
            Object var6 = var2.get(var4 + 1);
            if (var5.equals("Time")) {
               var6 = DoublePrecision.parseEncodedFloatingPointNumber(var6);
            }

            var3.put(var5, var6);
         }

         return var3;
      }

      private Object parseIterators(Object var1) {
         if (!(var1 instanceof List)) {
            return var1;
         } else {
            List var2 = (List)var1;
            int var3 = var2.indexOf("Child iterators");
            if (var3 < 0) {
               var3 = var2.indexOf("Child iterator");
            }

            HashMap var4;
            if (var3 < 0) {
               var3 = var2.size();
               var4 = new HashMap(var3 / 2, 1.0F);
            } else {
               var4 = new HashMap(1 + var3 / 2, 1.0F);
            }

            for(int var5 = 0; var5 < var3; var5 += 2) {
               String var6 = (String)var2.get(var5);
               Object var7 = var2.get(var5 + 1);
               if (var6.equals("Time")) {
                  var7 = DoublePrecision.parseEncodedFloatingPointNumber(var7);
               }

               var4.put(var6, var7);
            }

            if (var3 + 1 < var2.size()) {
               ArrayList var8 = new ArrayList(var2.size() - var3 - 1);

               for(int var9 = var3 + 1; var9 < var2.size(); ++var9) {
                  var8.add(this.parseIterators(var2.get(var9)));
               }

               var4.put("Child iterators", var8);
            }

            return var4;
         }
      }
   };
   public static final Builder<Map<String, List<String>>> SEARCH_SYNONYM_GROUPS = new Builder<Map<String, List<String>>>() {
      public Map<String, List<String>> build(Object var1) {
         List var2 = (List)var1;
         if (var2.isEmpty()) {
            return Collections.emptyMap();
         } else if (var2.get(0) instanceof KeyValue) {
            return (Map)((List)var1).stream().collect(Collectors.toMap((var0) -> BuilderFactory.STRING.build(var0.getKey()), (var0) -> BuilderFactory.STRING_LIST.build(var0.getValue())));
         } else {
            HashMap var3 = new HashMap(var2.size() / 2, 1.0F);

            for(int var4 = 0; var4 < var2.size(); var4 += 2) {
               var3.put(BuilderFactory.STRING.build(var2.get(var4)), BuilderFactory.STRING_LIST.build(var2.get(var4 + 1)));
            }

            return var3;
         }
      }
   };
   public static final Builder<Map<String, Map<String, Double>>> SEARCH_SPELLCHECK_RESPONSE = new Builder<Map<String, Map<String, Double>>>() {
      private static final String TERM = "TERM";
      private static final String RESULTS = "results";

      public Map<String, Map<String, Double>> build(Object var1) {
         List var2 = (List)var1;
         if (var2.isEmpty()) {
            return Collections.emptyMap();
         } else if (var2.get(0) instanceof KeyValue) {
            KeyValue var11 = (KeyValue)var2.get(0);
            String var12 = BuilderFactory.STRING.build(var11.getKey());
            if (!"results".equals(var12)) {
               throw new IllegalStateException("Unrecognized header: " + var12);
            } else {
               return (Map)((List)var11.getValue()).stream().collect(Collectors.toMap((var0) -> BuilderFactory.STRING.build(var0.getKey()), (var0) -> (Map)((List)var0.getValue()).stream().collect(Collectors.toMap((var0x) -> BuilderFactory.STRING.build(((KeyValue)var0x.get(0)).getKey()), (var0x) -> BuilderFactory.DOUBLE.build(((KeyValue)var0x.get(0)).getValue()))), (var0, var1x) -> var0, LinkedHashMap::new));
            }
         } else {
            LinkedHashMap var3 = new LinkedHashMap(var2.size());

            for(Object var5 : var2) {
               List var6 = (List)var5;
               String var7 = BuilderFactory.STRING.build(var6.get(0));
               if (!"TERM".equals(var7)) {
                  throw new IllegalStateException("Unrecognized header: " + var7);
               }

               String var8 = BuilderFactory.STRING.build(var6.get(1));
               List var9 = (List)var6.get(2);
               LinkedHashMap var10 = new LinkedHashMap(var9.size());
               var9.forEach((var1x) -> {
                  Double var10000 = (Double)var10.put(BuilderFactory.STRING.build(var1x.get(1)), BuilderFactory.DOUBLE.build(var1x.get(0)));
               });
               var3.put(var8, var10);
            }

            return var3;
         }
      }
   };

   private SearchBuilderFactory() {
      throw new InstantiationError("Must not instantiate this class");
   }
}
