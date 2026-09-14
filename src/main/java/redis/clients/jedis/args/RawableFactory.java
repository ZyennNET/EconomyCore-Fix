package redis.clients.jedis.args;

import java.util.Arrays;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

public final class RawableFactory {
   public static Rawable from(int var0) {
      return from(Protocol.toByteArray(var0));
   }

   public static Rawable from(double var0) {
      return from(Protocol.toByteArray(var0));
   }

   public static Rawable from(byte[] var0) {
      return new Raw(var0);
   }

   public static Rawable from(String var0) {
      return new RawString(var0);
   }

   private RawableFactory() {
      throw new InstantiationError();
   }

   public static class Raw implements Rawable {
      private final byte[] raw;

      public Raw(byte[] var1) {
         this.raw = Arrays.copyOf(var1, var1.length);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static class RawString implements Rawable {
      private final byte[] raw;

      public RawString(String var1) {
         this.raw = SafeEncoder.encode(var1);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
