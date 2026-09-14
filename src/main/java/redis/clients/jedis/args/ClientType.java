package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum ClientType implements Rawable {
   NORMAL,
   MASTER,
   SLAVE,
   REPLICA,
   PUBSUB;

   private final byte[] raw = SafeEncoder.encode(this.name().toLowerCase());

   public byte[] getRaw() {
      return this.raw;
   }
}
