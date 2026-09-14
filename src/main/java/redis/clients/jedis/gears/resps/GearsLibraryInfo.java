package redis.clients.jedis.gears.resps;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class GearsLibraryInfo {
   private final String apiVersion;
   private final List<String> clusterFunctions;
   private final String code;
   private final String configuration;
   private final String engine;
   private final List<FunctionInfo> functions;
   private final List<TriggerInfo> keyspaceTriggers;
   private final String name;
   private final List<String> pendingAsyncCalls;
   private final long pendingJobs;
   private final List<StreamTriggerInfo> streamTriggers;
   private final String user;
   public static final Builder<GearsLibraryInfo> GEARS_LIBRARY_INFO = new Builder<GearsLibraryInfo>() {
      public GearsLibraryInfo build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return null;
            } else {
               String var3 = null;
               List var4 = Collections.emptyList();
               String var5 = null;
               String var6 = null;
               String var7 = null;
               List var8 = Collections.emptyList();
               List var9 = Collections.emptyList();
               String var10 = null;
               List var11 = null;
               long var12 = 0L;
               List var14 = Collections.emptyList();
               String var15 = null;
               if (var2.get(0) instanceof KeyValue) {
                  for(KeyValue var17 : var2) {
                     switch ((String)BuilderFactory.STRING.build(var17.getKey())) {
                        case "api_version":
                           var3 = BuilderFactory.STRING.build(var17.getValue());
                           break;
                        case "cluster_functions":
                           var4 = BuilderFactory.STRING_LIST.build(var17.getValue());
                           break;
                        case "configuration":
                           var6 = BuilderFactory.STRING.build(var17.getValue());
                           break;
                        case "engine":
                           var7 = BuilderFactory.STRING.build(var17.getValue());
                           break;
                        case "functions":
                           var8 = FunctionInfo.FUNCTION_INFO_LIST.build(var17.getValue());
                           break;
                        case "keyspace_triggers":
                           var9 = TriggerInfo.KEYSPACE_TRIGGER_INFO_LIST.build(var17.getValue());
                           break;
                        case "name":
                           var10 = BuilderFactory.STRING.build(var17.getValue());
                           break;
                        case "pending_async_calls":
                           var11 = BuilderFactory.STRING_LIST.build(var17.getValue());
                           break;
                        case "pending_jobs":
                           var12 = (Long)BuilderFactory.LONG.build(var17.getValue());
                           break;
                        case "stream_triggers":
                           var14 = StreamTriggerInfo.STREAM_TRIGGER_INFO_LIST.build(var17.getValue());
                           break;
                        case "user":
                           var15 = BuilderFactory.STRING.build(var17.getValue());
                           break;
                        case "code":
                           var5 = BuilderFactory.STRING.build(var17.getValue());
                     }
                  }
               } else {
                  boolean var20 = var2.size() > 23;
                  int var21 = var20 ? 2 : 0;
                  var3 = BuilderFactory.STRING.build(var2.get(1));
                  var4 = BuilderFactory.STRING_LIST.build(var2.get(3));
                  var5 = var20 ? (String)BuilderFactory.STRING.build(var2.get(5)) : null;
                  var6 = BuilderFactory.STRING.build(var2.get(5 + var21));
                  var7 = BuilderFactory.STRING.build(var2.get(7 + var21));
                  var8 = FunctionInfo.FUNCTION_INFO_LIST.build(var2.get(9 + var21));
                  var9 = TriggerInfo.KEYSPACE_TRIGGER_INFO_LIST.build(var2.get(11 + var21));
                  var10 = BuilderFactory.STRING.build(var2.get(13 + var21));
                  var11 = BuilderFactory.STRING_LIST.build(var2.get(15 + var21));
                  var12 = (Long)BuilderFactory.LONG.build(var2.get(17 + var21));
                  var14 = StreamTriggerInfo.STREAM_TRIGGER_INFO_LIST.build(var2.get(19 + var21));
                  var15 = BuilderFactory.STRING.build(var2.get(21 + var21));
               }

               return new GearsLibraryInfo(var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var14, var15);
            }
         }
      }
   };
   public static final Builder<List<GearsLibraryInfo>> GEARS_LIBRARY_INFO_LIST = new Builder<List<GearsLibraryInfo>>() {
      public List<GearsLibraryInfo> build(Object var1) {
         List var2 = (List)var1;
         return (List)var2.stream().map((var0) -> GearsLibraryInfo.GEARS_LIBRARY_INFO.build(var0)).collect(Collectors.toList());
      }
   };

   public GearsLibraryInfo(String var1, List<String> var2, String var3, String var4, String var5, List<FunctionInfo> var6, List<TriggerInfo> var7, String var8, List<String> var9, long var10, List<StreamTriggerInfo> var12, String var13) {
      this.apiVersion = var1;
      this.clusterFunctions = var2;
      this.code = var3;
      this.configuration = var4;
      this.engine = var5;
      this.functions = var6;
      this.keyspaceTriggers = var7;
      this.name = var8;
      this.pendingAsyncCalls = var9;
      this.pendingJobs = var10;
      this.streamTriggers = var12;
      this.user = var13;
   }

   public String getApiVersion() {
      return this.apiVersion;
   }

   public List<String> getClusterFunctions() {
      return this.clusterFunctions;
   }

   public String getCode() {
      return this.code;
   }

   public String getConfiguration() {
      return this.configuration;
   }

   public String getEngine() {
      return this.engine;
   }

   public List<FunctionInfo> getFunctions() {
      return this.functions;
   }

   public List<TriggerInfo> getKeyspaceTriggers() {
      return this.keyspaceTriggers;
   }

   public String getName() {
      return this.name;
   }

   public List<String> getPendingAsyncCalls() {
      return this.pendingAsyncCalls;
   }

   public long getPendingJobs() {
      return this.pendingJobs;
   }

   public List<StreamTriggerInfo> getStreamTriggers() {
      return this.streamTriggers;
   }

   public String getUser() {
      return this.user;
   }
}
