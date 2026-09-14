package redis.clients.jedis.gears.resps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class StreamTriggerInfo {
   private final String name;
   private final String description;
   private final String prefix;
   private final boolean trim;
   private final long window;
   private final List<FunctionStreamInfo> streams;
   public static final Builder<List<StreamTriggerInfo>> STREAM_TRIGGER_INFO_LIST = new Builder<List<StreamTriggerInfo>>() {
      public List<StreamTriggerInfo> build(Object var1) {
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
                     boolean var6 = false;
                     List var7 = null;

                     for(KeyValue var9 : var0) {
                        switch ((String)BuilderFactory.STRING.build(var9.getKey())) {
                           case "name":
                              var1 = BuilderFactory.STRING.build(var9.getValue());
                              break;
                           case "description":
                              var2 = BuilderFactory.STRING.build(var9.getValue());
                              break;
                           case "prefix":
                              var3 = BuilderFactory.STRING.build(var9.getValue());
                              break;
                           case "window":
                              var4 = (Long)BuilderFactory.LONG.build(var9.getValue());
                              break;
                           case "trim":
                              var6 = (Boolean)BuilderFactory.BOOLEAN.build(var9.getValue());
                              break;
                           case "streams":
                              var7 = FunctionStreamInfo.STREAM_INFO_LIST.build(var9.getValue());
                        }
                     }

                     return new StreamTriggerInfo(var1, var2, var3, var4, var6, var7);
                  }).collect(Collectors.toList());
               } else {
                  return (List)var2.stream().map((var0) -> (List)var0).map((var0) -> {
                     StreamTriggerInfo var1 = null;
                     switch (var0.size()) {
                        case 1:
                           var1 = new StreamTriggerInfo(BuilderFactory.STRING.build(var0.get(0)));
                           break;
                        case 10:
                           var1 = new StreamTriggerInfo(BuilderFactory.STRING.build(var0.get(3)), BuilderFactory.STRING.build(var0.get(1)), BuilderFactory.STRING.build(var0.get(5)), (Long)BuilderFactory.LONG.build(var0.get(9)), (Boolean)BuilderFactory.BOOLEAN.build(var0.get(7)));
                           break;
                        case 12:
                           var1 = new StreamTriggerInfo(BuilderFactory.STRING.build(var0.get(3)), BuilderFactory.STRING.build(var0.get(1)), BuilderFactory.STRING.build(var0.get(5)), (Long)BuilderFactory.LONG.build(var0.get(11)), (Boolean)BuilderFactory.BOOLEAN.build(var0.get(9)), FunctionStreamInfo.STREAM_INFO_LIST.build(var0.get(7)));
                     }

                     return var1;
                  }).collect(Collectors.toList());
               }
            } else {
               Stream var10000 = var2.stream();
               Builder var10001 = BuilderFactory.STRING;
               var10001.getClass();
               return (List)var10000.map(var10001::build).map((var0) -> new StreamTriggerInfo(var0, (String)null, (String)null, 0L, false)).collect(Collectors.toList());
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

   public String getPrefix() {
      return this.prefix;
   }

   public boolean isTrim() {
      return this.trim;
   }

   public long getWindow() {
      return this.window;
   }

   public List<FunctionStreamInfo> getStreams() {
      return this.streams;
   }

   public StreamTriggerInfo(String var1, String var2, String var3, long var4, boolean var6, List<FunctionStreamInfo> var7) {
      this.name = var1;
      this.description = var2;
      this.prefix = var3;
      this.window = var4;
      this.trim = var6;
      this.streams = var7;
   }

   public StreamTriggerInfo(String var1) {
      this(var1, (String)null, (String)null, 0L, false, Collections.emptyList());
   }

   public StreamTriggerInfo(String var1, String var2, String var3, long var4, boolean var6) {
      this(var1, var2, var3, var4, var6, Collections.emptyList());
   }
}
