package redis.clients.jedis.resps;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class LibraryInfo {
   private final String libraryName;
   private final String engine;
   private final List<Map<String, Object>> functions;
   private final String libraryCode;
   public static final Builder<LibraryInfo> LIBRARY_INFO = new Builder<LibraryInfo>() {
      public LibraryInfo build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return null;
            } else if (var2.get(0) instanceof KeyValue) {
               String var11 = null;
               String var12 = null;
               String var13 = null;
               List var14 = null;

               for(KeyValue var8 : var2) {
                  switch ((String)BuilderFactory.STRING.build(var8.getKey())) {
                     case "library_name":
                        var11 = BuilderFactory.STRING.build(var8.getValue());
                        break;
                     case "engine":
                        var12 = BuilderFactory.STRING.build(var8.getValue());
                        break;
                     case "functions":
                        var14 = (List)((List)var8.getValue()).stream().map((var0) -> BuilderFactory.ENCODED_OBJECT_MAP.build(var0)).collect(Collectors.toList());
                        break;
                     case "library_code":
                        var13 = BuilderFactory.STRING.build(var8.getValue());
                  }
               }

               return new LibraryInfo(var11, var12, var14, var13);
            } else {
               String var3 = BuilderFactory.STRING.build(var2.get(1));
               String var4 = BuilderFactory.STRING.build(var2.get(3));
               List var5 = (List)var2.get(5);
               List var6 = (List)var5.stream().map((var0) -> BuilderFactory.ENCODED_OBJECT_MAP.build(var0)).collect(Collectors.toList());
               if (var2.size() <= 6) {
                  return new LibraryInfo(var3, var4, var6);
               } else {
                  String var7 = BuilderFactory.STRING.build(var2.get(7));
                  return new LibraryInfo(var3, var4, var6, var7);
               }
            }
         }
      }
   };
   @Deprecated
   public static final Builder<LibraryInfo> LIBRARY_BUILDER;
   public static final Builder<List<LibraryInfo>> LIBRARY_INFO_LIST;

   public LibraryInfo(String var1, String var2, List<Map<String, Object>> var3) {
      this(var1, var2, var3, (String)null);
   }

   public LibraryInfo(String var1, String var2, List<Map<String, Object>> var3, String var4) {
      this.libraryName = var1;
      this.engine = var2;
      this.functions = var3;
      this.libraryCode = var4;
   }

   public String getLibraryName() {
      return this.libraryName;
   }

   public String getEngine() {
      return this.engine;
   }

   public List<Map<String, Object>> getFunctions() {
      return this.functions;
   }

   public String getLibraryCode() {
      return this.libraryCode;
   }

   static {
      LIBRARY_BUILDER = LIBRARY_INFO;
      LIBRARY_INFO_LIST = new Builder<List<LibraryInfo>>() {
         public List<LibraryInfo> build(Object var1) {
            List var2 = (List)var1;
            return (List)var2.stream().map((var0) -> LibraryInfo.LIBRARY_INFO.build(var0)).collect(Collectors.toList());
         }
      };
   }
}
