package redis.clients.jedis.resps;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class FunctionStats {
   private final Map<String, Object> runningScript;
   private final Map<String, Map<String, Object>> engines;
   public static final Builder<FunctionStats> FUNCTION_STATS_BUILDER = new Builder<FunctionStats>() {
      public FunctionStats build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return null;
            } else if (var2.get(0) instanceof KeyValue) {
               Map var12 = null;
               LinkedHashMap var13 = null;

               for(KeyValue var15 : var2) {
                  switch ((String)BuilderFactory.STRING.build(var15.getKey())) {
                     case "running_script":
                        var12 = BuilderFactory.ENCODED_OBJECT_MAP.build(var15.getValue());
                        break;
                     case "engines":
                        List var9 = (List)var15.getValue();
                        var13 = new LinkedHashMap(var9.size());

                        for(KeyValue var11 : (List)var15.getValue()) {
                           var13.put(BuilderFactory.STRING.build(var11.getKey()), BuilderFactory.ENCODED_OBJECT_MAP.build(var11.getValue()));
                        }
                  }
               }

               return new FunctionStats(var12, var13);
            } else {
               Map var3 = var2.get(1) == null ? null : (Map)BuilderFactory.ENCODED_OBJECT_MAP.build(var2.get(1));
               List var4 = (List)var2.get(3);
               LinkedHashMap var5 = new LinkedHashMap(var4.size() / 2);

               for(int var6 = 0; var6 < var4.size(); var6 += 2) {
                  var5.put(BuilderFactory.STRING.build(var4.get(var6)), BuilderFactory.ENCODED_OBJECT_MAP.build(var4.get(var6 + 1)));
               }

               return new FunctionStats(var3, var5);
            }
         }
      }
   };

   public FunctionStats(Map<String, Object> var1, Map<String, Map<String, Object>> var2) {
      this.runningScript = var1;
      this.engines = var2;
   }

   public Map<String, Object> getRunningScript() {
      return this.runningScript;
   }

   public Map<String, Map<String, Object>> getEngines() {
      return this.engines;
   }
}
