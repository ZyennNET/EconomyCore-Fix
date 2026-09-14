package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum FlushMode implements Rawable {
   SYNC,
   ASYNC;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
