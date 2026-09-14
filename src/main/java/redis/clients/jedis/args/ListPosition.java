package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum ListPosition implements Rawable {
   BEFORE,
   AFTER;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
