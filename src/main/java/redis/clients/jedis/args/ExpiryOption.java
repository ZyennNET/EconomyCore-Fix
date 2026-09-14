package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum ExpiryOption implements Rawable {
   NX,
   XX,
   GT,
   LT;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
