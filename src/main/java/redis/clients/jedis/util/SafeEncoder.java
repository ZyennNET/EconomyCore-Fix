package redis.clients.jedis.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class SafeEncoder {
   public static volatile Charset DEFAULT_CHARSET;

   private SafeEncoder() {
      throw new InstantiationError("Must not instantiate this class");
   }

   public static byte[][] encodeMany(String... var0) {
      byte[][] var1 = new byte[var0.length][];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = encode(var0[var2]);
      }

      return var1;
   }

   public static byte[] encode(String var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("null value cannot be sent to redis");
      } else {
         return var0.getBytes(DEFAULT_CHARSET);
      }
   }

   public static String encode(byte[] var0) {
      return new String(var0, DEFAULT_CHARSET);
   }

   public static Object encodeObject(Object var0) {
      if (var0 instanceof byte[]) {
         return encode((byte[])var0);
      } else if (var0 instanceof KeyValue) {
         KeyValue var5 = (KeyValue)var0;
         return new KeyValue(encodeObject(var5.getKey()), encodeObject(var5.getValue()));
      } else if (!(var0 instanceof List)) {
         return var0;
      } else {
         List var1 = (List)var0;
         ArrayList var2 = new ArrayList(var1.size());

         for(Object var4 : var1) {
            var2.add(encodeObject(var4));
         }

         return var2;
      }
   }

   static {
      DEFAULT_CHARSET = StandardCharsets.UTF_8;
   }
}
