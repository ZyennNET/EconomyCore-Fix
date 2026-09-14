package redis.clients.jedis.timeseries;

import java.util.Locale;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.util.SafeEncoder;

public enum AggregationType implements Rawable {
   AVG,
   SUM,
   MIN,
   MAX,
   RANGE,
   COUNT,
   FIRST,
   LAST,
   STD_P("STD.P"),
   STD_S("STD.S"),
   VAR_P("VAR.P"),
   VAR_S("VAR.S"),
   TWA;

   private final byte[] raw;

   private AggregationType() {
      this.raw = SafeEncoder.encode(this.name());
   }

   private AggregationType(String var3) {
      this.raw = SafeEncoder.encode(var3);
   }

   public byte[] getRaw() {
      return this.raw;
   }

   public static AggregationType safeValueOf(String var0) {
      try {
         return valueOf(var0.replace('.', '_').toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }
}
