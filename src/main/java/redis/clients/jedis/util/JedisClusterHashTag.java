package redis.clients.jedis.util;

public final class JedisClusterHashTag {
   private JedisClusterHashTag() {
      throw new InstantiationError("Must not instantiate this class");
   }

   public static String getHashTag(String var0) {
      return extractHashTag(var0, true);
   }

   public static boolean isClusterCompliantMatchPattern(byte[] var0) {
      return isClusterCompliantMatchPattern(SafeEncoder.encode(var0));
   }

   public static boolean isClusterCompliantMatchPattern(String var0) {
      String var1 = extractHashTag(var0, false);
      return var1 != null && !var1.isEmpty();
   }

   private static String extractHashTag(String var0, boolean var1) {
      int var2 = var0.indexOf("{");
      if (var2 > -1) {
         int var3 = var0.indexOf("}", var2 + 1);
         if (var3 > -1 && var3 != var2 + 1) {
            return var0.substring(var2 + 1, var3);
         }
      }

      return var1 ? var0 : null;
   }
}
