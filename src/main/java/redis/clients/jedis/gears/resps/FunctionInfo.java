package redis.clients.jedis.gears.resps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class FunctionInfo {
   private final String name;
   private final String description;
   private final boolean isAsync;
   private final List<String> flags;
   public static final Builder<List<FunctionInfo>> FUNCTION_INFO_LIST = new Builder<List<FunctionInfo>>() {
      public List<FunctionInfo> build(Object var1) {
         List var2 = (List)var1;
         if (!var2.isEmpty()) {
            boolean var3 = var2.get(0).getClass().isAssignableFrom(ArrayList.class);
            if (var3) {
               if (((List)((List)var1).get(0)).get(0) instanceof KeyValue) {
                  List var4 = (List)var1;
                  return (List)var4.stream().map((var0) -> {
                     String var1 = null;
                     String var2 = null;
                     List var3 = Collections.emptyList();
                     boolean var4 = false;

                     for(KeyValue var6 : var0) {
                        switch ((String)BuilderFactory.STRING.build(var6.getKey())) {
                           case "name":
                              var1 = BuilderFactory.STRING.build(var6.getValue());
                              break;
                           case "description":
                              var2 = BuilderFactory.STRING.build(var6.getValue());
                              break;
                           case "raw-arguments":
                              var3 = BuilderFactory.STRING_LIST.build(var6.getValue());
                              break;
                           case "is_async":
                              var4 = (Boolean)BuilderFactory.BOOLEAN.build(var6.getValue());
                        }
                     }

                     return new FunctionInfo(var1, var2, var4, var3);
                  }).collect(Collectors.toList());
               } else {
                  return (List)var2.stream().map((var0) -> (List)var0).map((var0) -> new FunctionInfo(BuilderFactory.STRING.build(var0.get(7)), BuilderFactory.STRING.build(var0.get(1)), (Boolean)BuilderFactory.BOOLEAN.build(var0.get(5)), BuilderFactory.STRING_LIST.build(var0.get(3)))).collect(Collectors.toList());
               }
            } else {
               Stream var10000 = var2.stream();
               Builder var10001 = BuilderFactory.STRING;
               var10001.getClass();
               return (List)var10000.map(var10001::build).map((var0) -> new FunctionInfo(var0, (String)null, false, (List)null)).collect(Collectors.toList());
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

   public boolean isAsync() {
      return this.isAsync;
   }

   public List<String> getFlags() {
      return this.flags;
   }

   public FunctionInfo(String var1, String var2, boolean var3, List<String> var4) {
      this.name = var1;
      this.description = var2;
      this.isAsync = var3;
      this.flags = var4;
   }
}
