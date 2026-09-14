package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum SortingOrder implements Rawable {
   ASC,
   DESC;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
