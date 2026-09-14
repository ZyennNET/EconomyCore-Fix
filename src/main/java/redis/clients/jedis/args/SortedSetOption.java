package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum SortedSetOption implements Rawable {
   MIN,
   MAX;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
