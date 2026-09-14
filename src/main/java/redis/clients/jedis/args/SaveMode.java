package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum SaveMode implements Rawable {
   NOSAVE,
   SAVE;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
