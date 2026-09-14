package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum ClusterFailoverOption implements Rawable {
   FORCE,
   TAKEOVER;

   private final byte[] raw = SafeEncoder.encode(this.name());

   public byte[] getRaw() {
      return this.raw;
   }
}
