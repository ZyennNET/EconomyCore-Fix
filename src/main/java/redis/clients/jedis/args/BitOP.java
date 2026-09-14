package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum BitOP implements Rawable {
   AND,
   OR,
   XOR,
   NOT;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
