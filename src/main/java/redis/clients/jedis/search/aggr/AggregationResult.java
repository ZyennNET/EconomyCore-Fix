package redis.clients.jedis.search.aggr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.SafeEncoder;

public class AggregationResult {
   private final long totalResults;
   private final List<Map<String, Object>> results;
   private Long cursorId;
   public static final Builder<AggregationResult> SEARCH_AGGREGATION_RESULT = new Builder<AggregationResult>() {
      private static final String TOTAL_RESULTS_STR = "total_results";
      private static final String RESULTS_STR = "results";
      private static final String FIELDS_STR = "extra_attributes";

      public AggregationResult build(Object var1) {
         List var2 = (List)var1;
         if (var2.get(0) instanceof KeyValue) {
            List var18 = (List)var1;
            long var4 = -1L;
            ArrayList var19 = null;

            for(KeyValue var21 : var18) {
               switch ((String)BuilderFactory.STRING.build(var21.getKey())) {
                  case "total_results":
                     var4 = (Long)BuilderFactory.LONG.build(var21.getValue());
                     break;
                  case "results":
                     List var12 = (List)var21.getValue();
                     var19 = new ArrayList(var12.size());

                     for(List var14 : var12) {
                        for(KeyValue var16 : var14) {
                           if ("extra_attributes".equals(BuilderFactory.STRING.build(var16.getKey()))) {
                              var19.add(BuilderFactory.ENCODED_OBJECT_MAP.build(var16.getValue()));
                              break;
                           }
                        }
                     }
               }
            }

            return new AggregationResult(var4, var19);
         } else {
            var2 = (List)SafeEncoder.encodeObject(var1);
            long var3 = (Long)var2.get(0);
            ArrayList var5 = new ArrayList(var2.size() - 1);

            for(int var6 = 1; var6 < var2.size(); ++var6) {
               List var7 = (List)var2.get(var6);
               HashMap var8 = new HashMap(var7.size() / 2, 1.0F);

               for(int var9 = 0; var9 < var7.size(); var9 += 2) {
                  Object var10 = var7.get(var9);
                  if (var10 instanceof JedisDataException) {
                     throw (JedisDataException)var10;
                  }

                  var8.put((String)var10, var7.get(var9 + 1));
               }

               var5.add(var8);
            }

            return new AggregationResult(var3, var5);
         }
      }
   };
   public static final Builder<AggregationResult> SEARCH_AGGREGATION_RESULT_WITH_CURSOR = new Builder<AggregationResult>() {
      public AggregationResult build(Object var1) {
         List var2 = (List)var1;
         AggregationResult var3 = AggregationResult.SEARCH_AGGREGATION_RESULT.build(var2.get(0));
         var3.setCursorId((Long)var2.get(1));
         return var3;
      }
   };

   private AggregationResult(Object var1, long var2) {
      this(var1);
      this.cursorId = var2;
   }

   private AggregationResult(Object var1) {
      this.cursorId = -1L;
      List var2 = (List)SafeEncoder.encodeObject(var1);
      this.totalResults = (Long)var2.get(0);
      this.results = new ArrayList(var2.size() - 1);

      for(int var3 = 1; var3 < var2.size(); ++var3) {
         List var4 = (List)var2.get(var3);
         HashMap var5 = new HashMap(var4.size() / 2, 1.0F);

         for(int var6 = 0; var6 < var4.size(); var6 += 2) {
            Object var7 = var4.get(var6);
            if (var7 instanceof JedisDataException) {
               throw (JedisDataException)var7;
            }

            var5.put((String)var7, var4.get(var6 + 1));
         }

         this.results.add(var5);
      }

   }

   private AggregationResult(long var1, List<Map<String, Object>> var3) {
      this.cursorId = -1L;
      this.totalResults = var1;
      this.results = var3;
   }

   private void setCursorId(Long var1) {
      this.cursorId = var1;
   }

   public Long getCursorId() {
      return this.cursorId;
   }

   public long getTotalResults() {
      return this.totalResults;
   }

   public List<Map<String, Object>> getResults() {
      return Collections.unmodifiableList(this.results);
   }

   public List<Row> getRows() {
      return (List)this.results.stream().map(Row::new).collect(Collectors.toList());
   }

   public Row getRow(int var1) {
      return new Row((Map)this.results.get(var1));
   }
}
