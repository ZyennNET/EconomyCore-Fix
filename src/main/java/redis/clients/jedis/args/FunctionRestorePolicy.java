package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum FunctionRestorePolicy implements Rawable {
   FLUSH,
   APPEND,
   REPLACE;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
