package redis.clients.jedis;

import java.io.InputStream;
import java.util.Properties;
import org.slf4j.LoggerFactory;

class JedisMetaInfo {
   private static final String groupId;
   private static final String artifactId;
   private static final String version;

   public static String getGroupId() {
      return groupId;
   }

   public static String getArtifactId() {
      return artifactId;
   }

   public static String getVersion() {
      return version;
   }

   static {
      Properties var0 = new Properties();

      try {
         InputStream var1 = JedisMetaInfo.class.getClassLoader().getResourceAsStream("redis/clients/jedis/pom.properties");
         Throwable var2 = null;

         try {
            var0.load(var1);
         } catch (Throwable var12) {
            var2 = var12;
            throw var12;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var11) {
                     var2.addSuppressed(var11);
                  }
               } else {
                  var1.close();
               }
            }

         }
      } catch (Exception var14) {
         LoggerFactory.getLogger(JedisMetaInfo.class).error((String)"Load Jedis meta info from pom.properties failed", (Throwable)var14);
      }

      groupId = var0.getProperty("groupId", (String)null);
      artifactId = var0.getProperty("artifactId", (String)null);
      version = var0.getProperty("version", (String)null);
   }
}
