package redis.clients.jedis.util;

import java.net.URI;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.RedisProtocol;

public final class JedisURIHelper {
   private static final String REDIS = "redis";
   private static final String REDISS = "rediss";

   private JedisURIHelper() {
      throw new InstantiationError("Must not instantiate this class");
   }

   public static HostAndPort getHostAndPort(URI var0) {
      return new HostAndPort(var0.getHost(), var0.getPort());
   }

   public static String getUser(URI var0) {
      String var1 = var0.getUserInfo();
      if (var1 != null) {
         String var2 = var1.split(":", 2)[0];
         if (var2.isEmpty()) {
            var2 = null;
         }

         return var2;
      } else {
         return null;
      }
   }

   public static String getPassword(URI var0) {
      String var1 = var0.getUserInfo();
      return var1 != null ? var1.split(":", 2)[1] : null;
   }

   public static int getDBIndex(URI var0) {
      String[] var1 = var0.getPath().split("/", 2);
      if (var1.length > 1) {
         String var2 = var1[1];
         return var2.isEmpty() ? 0 : Integer.parseInt(var2);
      } else {
         return 0;
      }
   }

   public static RedisProtocol getRedisProtocol(URI var0) {
      if (var0.getQuery() == null) {
         return null;
      } else {
         String[] var1 = var0.getQuery().split("&");

         for(String var5 : var1) {
            int var6 = var5.indexOf("=");
            if ("protocol".equals(var5.substring(0, var6))) {
               String var7 = var5.substring(var6 + 1);

               for(RedisProtocol var11 : RedisProtocol.values()) {
                  if (var11.version().equals(var7)) {
                     return var11;
                  }
               }

               throw new IllegalArgumentException("Unknown protocol " + var7);
            }
         }

         return null;
      }
   }

   public static boolean isValid(URI var0) {
      return !isEmpty(var0.getScheme()) && !isEmpty(var0.getHost()) && var0.getPort() != -1;
   }

   private static boolean isEmpty(String var0) {
      return var0 == null || var0.trim().length() == 0;
   }

   public static boolean isRedisScheme(URI var0) {
      return "redis".equals(var0.getScheme());
   }

   public static boolean isRedisSSLScheme(URI var0) {
      return "rediss".equals(var0.getScheme());
   }
}
