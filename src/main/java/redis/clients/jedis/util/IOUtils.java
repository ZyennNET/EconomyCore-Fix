package redis.clients.jedis.util;

import java.io.IOException;
import java.net.Socket;

public class IOUtils {
   public static void closeQuietly(Socket var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
         }
      }

   }

   public static void closeQuietly(AutoCloseable var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (Exception var2) {
         }
      }

   }

   private IOUtils() {
      throw new InstantiationError("Must not instantiate this class");
   }
}
