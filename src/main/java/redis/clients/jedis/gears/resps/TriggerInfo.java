package redis.clients.jedis.gears.resps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class TriggerInfo {
   private final String name;
   private final String description;
   private final String lastError;
   private final long lastExecutionTime;
   private final long numFailed;
   private final long numFinished;
   private final long numSuccess;
   private final long numTrigger;
   private final long totalExecutionTime;
   public static final Builder<List<TriggerInfo>> KEYSPACE_TRIGGER_INFO_LIST = new Builder<List<TriggerInfo>>() {
      public List<TriggerInfo> build(Object var1) {
         List var2 = (List)var1;
         if (!var2.isEmpty()) {
            boolean var3 = var2.get(0).getClass().isAssignableFrom(ArrayList.class);
            if (var3) {
               if (((List)((List)var1).get(0)).get(0) instanceof KeyValue) {
                  List var4 = (List)var1;
                  return (List)var4.stream().map((var0) -> {
                     String var1 = null;
                     String var2 = null;
                     String var3 = null;
                     long var4 = 0L;
                     long var6 = 0L;
                     long var8 = 0L;
                     long var10 = 0L;
                     long var12 = 0L;
                     long var14 = 0L;

                     for(KeyValue var17 : var0) {
                        switch ((String)BuilderFactory.STRING.build(var17.getKey())) {
                           case "name":
                              var1 = BuilderFactory.STRING.build(var17.getValue());
                              break;
                           case "description":
                              var2 = BuilderFactory.STRING.build(var17.getValue());
                              break;
                           case "last_error":
                              var3 = BuilderFactory.STRING.build(var17.getValue());
                              break;
                           case "last_execution_time":
                              var4 = (Long)BuilderFactory.LONG.build(var17.getValue());
                              break;
                           case "num_failed":
                              var6 = (Long)BuilderFactory.LONG.build(var17.getValue());
                              break;
                           case "num_finished":
                              var8 = (Long)BuilderFactory.LONG.build(var17.getValue());
                              break;
                           case "num_success":
                              var10 = (Long)BuilderFactory.LONG.build(var17.getValue());
                              break;
                           case "num_trigger":
                              var12 = (Long)BuilderFactory.LONG.build(var17.getValue());
                              break;
                           case "total_execution_time":
                              var14 = (Long)BuilderFactory.LONG.build(var17.getValue());
                        }
                     }

                     return new TriggerInfo(var1, var2, var3, var8, var10, var6, var12, var4, var14);
                  }).collect(Collectors.toList());
               } else {
                  return (List)var2.stream().map((var0) -> (List)var0).map((var0) -> new TriggerInfo(BuilderFactory.STRING.build(var0.get(7)), BuilderFactory.STRING.build(var0.get(1)), BuilderFactory.STRING.build(var0.get(3)), (Long)BuilderFactory.LONG.build(var0.get(11)), (Long)BuilderFactory.LONG.build(var0.get(13)), (Long)BuilderFactory.LONG.build(var0.get(9)), (Long)BuilderFactory.LONG.build(var0.get(15)), (Long)BuilderFactory.LONG.build(var0.get(5)), (Long)BuilderFactory.LONG.build(var0.get(17)))).collect(Collectors.toList());
               }
            } else {
               Stream var10000 = var2.stream();
               Builder var10001 = BuilderFactory.STRING;
               var10001.getClass();
               return (List)var10000.map(var10001::build).map((var0) -> new TriggerInfo(var0, (String)null, (String)null, 0L, 0L, 0L, 0L, 0L, 0L)).collect(Collectors.toList());
            }
         } else {
            return Collections.emptyList();
         }
      }
   };

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getLastError() {
      return this.lastError;
   }

   public long getLastExecutionTime() {
      return this.lastExecutionTime;
   }

   public long getNumFailed() {
      return this.numFailed;
   }

   public long getNumFinished() {
      return this.numFinished;
   }

   public long getNumSuccess() {
      return this.numSuccess;
   }

   public long getNumTrigger() {
      return this.numTrigger;
   }

   public long getTotalExecutionTime() {
      return this.totalExecutionTime;
   }

   public TriggerInfo(String var1, String var2, String var3, long var4, long var6, long var8, long var10, long var12, long var14) {
      this.name = var1;
      this.description = var2;
      this.lastError = var3;
      this.numFinished = var4;
      this.numSuccess = var6;
      this.numFailed = var8;
      this.numTrigger = var10;
      this.lastExecutionTime = var12;
      this.totalExecutionTime = var14;
   }
}
