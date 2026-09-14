package redis.clients.jedis.util;

import redis.clients.jedis.args.Rawable;

public class LazyRawable implements Rawable {
   private byte[] raw = null;

   public void setRaw(byte[] var1) {
      this.raw = var1;
   }

   public byte[] getRaw() {
      return this.raw;
   }
}
