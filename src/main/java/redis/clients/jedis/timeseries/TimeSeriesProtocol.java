package redis.clients.jedis.timeseries;

import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public class TimeSeriesProtocol {
   public static final byte[] PLUS = SafeEncoder.encode("+");
   public static final byte[] MINUS = SafeEncoder.encode("-");

   public static enum TimeSeriesCommand implements ProtocolCommand {
      CREATE("TS.CREATE"),
      RANGE("TS.RANGE"),
      REVRANGE("TS.REVRANGE"),
      MRANGE("TS.MRANGE"),
      MREVRANGE("TS.MREVRANGE"),
      CREATERULE("TS.CREATERULE"),
      DELETERULE("TS.DELETERULE"),
      ADD("TS.ADD"),
      MADD("TS.MADD"),
      DEL("TS.DEL"),
      INCRBY("TS.INCRBY"),
      DECRBY("TS.DECRBY"),
      INFO("TS.INFO"),
      GET("TS.GET"),
      MGET("TS.MGET"),
      ALTER("TS.ALTER"),
      QUERYINDEX("TS.QUERYINDEX");

      private final byte[] raw;

      private TimeSeriesCommand(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum TimeSeriesKeyword implements Rawable {
      RESET,
      FILTER,
      AGGREGATION,
      LABELS,
      RETENTION,
      TIMESTAMP,
      WITHLABELS,
      SELECTED_LABELS,
      COUNT,
      ENCODING,
      COMPRESSED,
      UNCOMPRESSED,
      CHUNK_SIZE,
      DUPLICATE_POLICY,
      ON_DUPLICATE,
      ALIGN,
      FILTER_BY_TS,
      FILTER_BY_VALUE,
      GROUPBY,
      REDUCE,
      DEBUG,
      LATEST,
      EMPTY,
      BUCKETTIMESTAMP;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
