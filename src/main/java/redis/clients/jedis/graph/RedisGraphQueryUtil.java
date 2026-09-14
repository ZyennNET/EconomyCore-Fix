package redis.clients.jedis.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Deprecated
public class RedisGraphQueryUtil {
   public static final List<String> DUMMY_LIST = Collections.emptyList();
   public static final Map<String, List<String>> DUMMY_MAP = Collections.emptyMap();
   public static final String COMPACT_STRING = "--COMPACT";
   public static final String TIMEOUT_STRING = "TIMEOUT";

   private RedisGraphQueryUtil() {
   }

   @Deprecated
   public static String prepareQuery(String var0, Map<String, Object> var1) {
      StringBuilder var2 = new StringBuilder("CYPHER ");

      for(Map.Entry var4 : var1.entrySet()) {
         var2.append((String)var4.getKey()).append('=').append(valueToString(var4.getValue())).append(' ');
      }

      var2.append(var0);
      return var2.toString();
   }

   private static String valueToString(Object var0) {
      if (var0 == null) {
         return "null";
      } else if (var0 instanceof String) {
         return quoteString((String)var0);
      } else if (var0 instanceof Character) {
         return quoteString(((Character)var0).toString());
      } else if (var0 instanceof Object[]) {
         return arrayToString(var0);
      } else {
         return var0 instanceof List ? arrayToString((List)var0) : var0.toString();
      }
   }

   private static String quoteString(String var0) {
      StringBuilder var1 = new StringBuilder(var0.length() + 12);
      var1.append('"');
      var1.append(var0.replace("\"", "\\\""));
      var1.append('"');
      return var1.toString();
   }

   private static String arrayToString(Object[] var0) {
      return arrayToString(Arrays.asList(var0));
   }

   private static String arrayToString(List<Object> var0) {
      StringBuilder var1 = (new StringBuilder()).append('[');
      var1.append(String.join(", ", (Iterable)var0.stream().map(RedisGraphQueryUtil::valueToString).collect(Collectors.toList())));
      var1.append(']');
      return var1.toString();
   }
}
