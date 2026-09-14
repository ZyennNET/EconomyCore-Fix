package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum UnblockType implements Rawable {
   TIMEOUT,
   ERROR;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
