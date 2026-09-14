package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum ListDirection implements Rawable {
   LEFT,
   RIGHT;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
