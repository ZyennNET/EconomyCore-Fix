package redis.clients.jedis.args;

import redis.clients.jedis.util.SafeEncoder;

public enum ClientAttributeOption implements Rawable {
   LIB_NAME("LIB-NAME"),
   LIB_VER("LIB-VER");

   private final byte[] raw;

   private ClientAttributeOption(String var3) {
      this.raw = SafeEncoder.encode(var3);
   }

   public byte[] getRaw() {
      return this.raw;
   }
}
