package redis.clients.jedis.search;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.util.SafeEncoder;

public class RediSearchUtil {
   private static final Set<Character> ESCAPE_CHARS = new HashSet(Arrays.asList(',', '.', '<', '>', '{', '}', '[', ']', '"', '\'', ':', ';', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '+', '=', '~', '|'));

   public static Map<String, String> toStringMap(Map<String, Object> var0) {
      return toStringMap(var0, false);
   }

   public static Map<String, String> toStringMap(Map<String, Object> var0, boolean var1) {
      HashMap var2 = new HashMap(var0.size());

      for(Map.Entry var4 : var0.entrySet()) {
         String var5 = (String)var4.getKey();
         Object var6 = var4.getValue();
         if (var5 == null || var6 == null) {
            throw new NullPointerException("A null argument cannot be sent to Redis.");
         }

         String var7;
         if (var6 instanceof byte[]) {
            var7 = SafeEncoder.encode((byte[])var6);
         } else if (var6 instanceof GeoCoordinate) {
            GeoCoordinate var8 = (GeoCoordinate)var6;
            var7 = var8.getLongitude() + "," + var8.getLatitude();
         } else if (var6 instanceof String) {
            var7 = var1 ? escape((String)var6) : (String)var6;
         } else {
            var7 = String.valueOf(var6);
         }

         var2.put(var5, var7);
      }

      return var2;
   }

   public static byte[] toByteArray(float[] var0) {
      byte[] var1 = new byte[4 * var0.length];
      ByteBuffer.wrap(var1).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().put(var0);
      return var1;
   }

   @Deprecated
   public static byte[] ToByteArray(float[] var0) {
      return toByteArray(var0);
   }

   public static String escape(String var0) {
      return escape(var0, false);
   }

   public static String escapeQuery(String var0) {
      return escape(var0, true);
   }

   public static String escape(String var0, boolean var1) {
      char[] var2 = var0.toCharArray();
      StringBuilder var3 = new StringBuilder();

      for(char var7 : var2) {
         if (ESCAPE_CHARS.contains(var7) || var1 && var7 == ' ') {
            var3.append("\\");
         }

         var3.append(var7);
      }

      return var3.toString();
   }

   public static String unescape(String var0) {
      return var0.replace("\\", "");
   }

   private RediSearchUtil() {
      throw new InstantiationError("Must not instantiate this class");
   }
}
