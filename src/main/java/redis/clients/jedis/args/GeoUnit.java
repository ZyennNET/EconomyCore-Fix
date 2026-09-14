package redis.clients.jedis.args;

import java.util.Locale;
import redis.clients.jedis.util.SafeEncoder;

public enum GeoUnit implements Rawable {
   M,
   KM,
   MI,
   FT;

   private final byte[] raw;

   private GeoUnit() {
      this.raw = SafeEncoder.encode(this.name().toLowerCase(Locale.ENGLISH));
   }

   public byte[] getRaw() {
      return this.raw;
   }
}
