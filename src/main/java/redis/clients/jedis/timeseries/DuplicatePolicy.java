package redis.clients.jedis.timeseries;

import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.util.SafeEncoder;

public enum DuplicatePolicy implements Rawable {
   BLOCK,
   FIRST,
   LAST,
   MIN,
   MAX,
   SUM;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
