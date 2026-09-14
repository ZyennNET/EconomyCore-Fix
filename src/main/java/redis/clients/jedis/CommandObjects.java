package redis.clients.jedis;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import redis.clients.jedis.args.BitCountOption;
import redis.clients.jedis.args.BitOP;
import redis.clients.jedis.args.ExpiryOption;
import redis.clients.jedis.args.FlushMode;
import redis.clients.jedis.args.FunctionRestorePolicy;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.args.ListPosition;
import redis.clients.jedis.args.SortedSetOption;
import redis.clients.jedis.bloom.BFInsertParams;
import redis.clients.jedis.bloom.BFReserveParams;
import redis.clients.jedis.bloom.CFInsertParams;
import redis.clients.jedis.bloom.CFReserveParams;
import redis.clients.jedis.bloom.RedisBloomProtocol;
import redis.clients.jedis.bloom.TDigestMergeParams;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.gears.RedisGearsProtocol;
import redis.clients.jedis.gears.TFunctionListParams;
import redis.clients.jedis.gears.TFunctionLoadParams;
import redis.clients.jedis.gears.resps.GearsLibraryInfo;
import redis.clients.jedis.graph.GraphProtocol;
import redis.clients.jedis.json.DefaultGsonObjectMapper;
import redis.clients.jedis.json.JsonBuilderFactory;
import redis.clients.jedis.json.JsonObjectMapper;
import redis.clients.jedis.json.JsonProtocol;
import redis.clients.jedis.json.JsonSetParams;
import redis.clients.jedis.json.Path;
import redis.clients.jedis.json.Path2;
import redis.clients.jedis.params.BitPosParams;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.GeoRadiusStoreParam;
import redis.clients.jedis.params.GeoSearchParam;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.LCSParams;
import redis.clients.jedis.params.LPosParams;
import redis.clients.jedis.params.MigrateParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.SortingParams;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XAutoClaimParams;
import redis.clients.jedis.params.XClaimParams;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.params.XTrimParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;
import redis.clients.jedis.params.ZParams;
import redis.clients.jedis.params.ZRangeParams;
import redis.clients.jedis.resps.FunctionStats;
import redis.clients.jedis.resps.GeoRadiusResponse;
import redis.clients.jedis.resps.LCSMatchResult;
import redis.clients.jedis.resps.LibraryInfo;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.StreamConsumerInfo;
import redis.clients.jedis.resps.StreamConsumersInfo;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamFullInfo;
import redis.clients.jedis.resps.StreamGroupInfo;
import redis.clients.jedis.resps.StreamInfo;
import redis.clients.jedis.resps.StreamPendingEntry;
import redis.clients.jedis.resps.StreamPendingSummary;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.FTProfileParams;
import redis.clients.jedis.search.FTSearchParams;
import redis.clients.jedis.search.FTSpellCheckParams;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.Schema;
import redis.clients.jedis.search.SearchBuilderFactory;
import redis.clients.jedis.search.SearchProtocol;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.timeseries.AggregationType;
import redis.clients.jedis.timeseries.TSAlterParams;
import redis.clients.jedis.timeseries.TSCreateParams;
import redis.clients.jedis.timeseries.TSElement;
import redis.clients.jedis.timeseries.TSGetParams;
import redis.clients.jedis.timeseries.TSInfo;
import redis.clients.jedis.timeseries.TSMGetElement;
import redis.clients.jedis.timeseries.TSMGetParams;
import redis.clients.jedis.timeseries.TSMRangeElements;
import redis.clients.jedis.timeseries.TSMRangeParams;
import redis.clients.jedis.timeseries.TSRangeParams;
import redis.clients.jedis.timeseries.TimeSeriesBuilderFactory;
import redis.clients.jedis.timeseries.TimeSeriesProtocol;
import redis.clients.jedis.util.KeyValue;

public class CommandObjects {
   private RedisProtocol protocol;
   private volatile JsonObjectMapper jsonObjectMapper;
   private final AtomicInteger searchDialect = new AtomicInteger(0);
   private JedisBroadcastAndRoundRobinConfig broadcastAndRoundRobinConfig = null;
   private final CommandObject<String> PING_COMMAND_OBJECT;
   private final CommandObject<String> FLUSHALL_COMMAND_OBJECT;
   private final CommandObject<String> FLUSHDB_COMMAND_OBJECT;
   private final CommandObject<String> SCRIPT_FLUSH_COMMAND_OBJECT;
   private final CommandObject<String> SCRIPT_KILL_COMMAND_OBJECT;
   private final CommandObject<String> SLOWLOG_RESET_COMMAND_OBJECT;
   private final Builder<Object> JSON_GENERIC_OBJECT;
   private static final Builder<Map.Entry<Long, byte[]>> BLOOM_SCANDUMP_RESPONSE = new Builder<Map.Entry<Long, byte[]>>() {
      public Map.Entry<Long, byte[]> build(Object var1) {
         List var2 = (List)var1;
         return new KeyValue<Long, byte[]>(BuilderFactory.LONG.build(var2.get(0)), BuilderFactory.BINARY.build(var2.get(1)));
      }
   };

   public CommandObjects() {
      this.PING_COMMAND_OBJECT = new CommandObject<String>(this.commandArguments(Protocol.Command.PING), BuilderFactory.STRING);
      this.FLUSHALL_COMMAND_OBJECT = new CommandObject<String>(this.commandArguments(Protocol.Command.FLUSHALL), BuilderFactory.STRING);
      this.FLUSHDB_COMMAND_OBJECT = new CommandObject<String>(this.commandArguments(Protocol.Command.FLUSHDB), BuilderFactory.STRING);
      this.SCRIPT_FLUSH_COMMAND_OBJECT = new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.FLUSH), BuilderFactory.STRING);
      this.SCRIPT_KILL_COMMAND_OBJECT = new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.KILL), BuilderFactory.STRING);
      this.SLOWLOG_RESET_COMMAND_OBJECT = new CommandObject<String>(this.commandArguments(Protocol.Command.SLOWLOG).add(Protocol.Keyword.RESET), BuilderFactory.STRING);
      this.JSON_GENERIC_OBJECT = new JsonObjectBuilder<Object>(Object.class);
   }

   public final void setProtocol(RedisProtocol var1) {
      this.protocol = var1;
   }

   protected RedisProtocol getProtocol() {
      return this.protocol;
   }

   void setBroadcastAndRoundRobinConfig(JedisBroadcastAndRoundRobinConfig var1) {
      this.broadcastAndRoundRobinConfig = var1;
   }

   protected CommandArguments commandArguments(ProtocolCommand var1) {
      return new CommandArguments(var1);
   }

   public final CommandObject<String> ping() {
      return this.PING_COMMAND_OBJECT;
   }

   public final CommandObject<String> flushAll() {
      return this.FLUSHALL_COMMAND_OBJECT;
   }

   public final CommandObject<String> flushDB() {
      return this.FLUSHDB_COMMAND_OBJECT;
   }

   public final CommandObject<String> configSet(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.CONFIG).add(Protocol.Keyword.SET).add(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Boolean> exists(String var1) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.EXISTS).key(var1), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> exists(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXISTS).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Boolean> exists(byte[] var1) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.EXISTS).key(var1), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> exists(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXISTS).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> persist(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PERSIST).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> persist(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PERSIST).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<String> type(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.TYPE).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> type(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.TYPE).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> dump(String var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.DUMP).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> dump(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.DUMP).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<String> restore(String var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RESTORE).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> restore(String var1, long var2, byte[] var4, RestoreParams var5) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RESTORE).key(var1).add(var2).add(var4).addParams(var5), BuilderFactory.STRING);
   }

   public final CommandObject<String> restore(byte[] var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RESTORE).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> restore(byte[] var1, long var2, byte[] var4, RestoreParams var5) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RESTORE).key(var1).add(var2).add(var4).addParams(var5), BuilderFactory.STRING);
   }

   public final CommandObject<Long> expire(String var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIRE).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expire(byte[] var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIRE).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expire(String var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIRE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expire(byte[] var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIRE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpire(String var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIRE).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpire(byte[] var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIRE).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpire(String var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIRE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpire(byte[] var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIRE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expireTime(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIRETIME).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expireTime(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIRETIME).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpireTime(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIRETIME).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpireTime(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIRETIME).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expireAt(String var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIREAT).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expireAt(byte[] var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIREAT).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expireAt(String var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIREAT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> expireAt(byte[] var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.EXPIREAT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpireAt(String var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIREAT).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpireAt(byte[] var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIREAT).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpireAt(String var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIREAT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pexpireAt(byte[] var1, long var2, ExpiryOption var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PEXPIREAT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> ttl(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.TTL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> ttl(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.TTL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pttl(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PTTL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pttl(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PTTL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> touch(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.TOUCH).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> touch(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.TOUCH).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> touch(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.TOUCH).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> touch(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.TOUCH).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> sort(String var1) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.SORT).key(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> sort(String var1, SortingParams var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.SORT).key(var1).addParams(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<byte[]>> sort(byte[] var1) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.SORT).key(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> sort(byte[] var1, SortingParams var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.SORT).key(var1).addParams(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<Long> sort(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SORT).key(var1).add(Protocol.Keyword.STORE).key(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sort(String var1, SortingParams var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SORT).key(var1).addParams(var2).add(Protocol.Keyword.STORE).key(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sort(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SORT).key(var1).add(Protocol.Keyword.STORE).key(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sort(byte[] var1, SortingParams var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SORT).key(var1).addParams(var2).add(Protocol.Keyword.STORE).key(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<byte[]>> sortReadonly(byte[] var1, SortingParams var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.SORT_RO).key(var1).addParams(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<String>> sortReadonly(String var1, SortingParams var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.SORT_RO).key(var1).addParams(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<Long> del(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DEL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> del(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DEL).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> del(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DEL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> del(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DEL).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> unlink(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.UNLINK).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> unlink(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.UNLINK).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> unlink(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.UNLINK).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> unlink(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.UNLINK).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Boolean> copy(String var1, String var2, boolean var3) {
      CommandArguments var4 = this.commandArguments(Protocol.Command.COPY).key(var1).key(var2);
      if (var3) {
         var4.add(Protocol.Keyword.REPLACE);
      }

      return new CommandObject<Boolean>(var4, BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> copy(byte[] var1, byte[] var2, boolean var3) {
      CommandArguments var4 = this.commandArguments(Protocol.Command.COPY).key(var1).key(var2);
      if (var3) {
         var4.add(Protocol.Keyword.REPLACE);
      }

      return new CommandObject<Boolean>(var4, BuilderFactory.BOOLEAN);
   }

   public final CommandObject<String> rename(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RENAME).key(var1).key(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> renamenx(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.RENAMENX).key(var1).key(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> rename(byte[] var1, byte[] var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RENAME).key(var1).key(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> renamenx(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.RENAMENX).key(var1).key(var2), BuilderFactory.LONG);
   }

   public CommandObject<Long> dbSize() {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DBSIZE), BuilderFactory.LONG);
   }

   public CommandObject<Set<String>> keys(String var1) {
      CommandArguments var2 = this.commandArguments(Protocol.Command.KEYS).key(var1);
      return new CommandObject<Set<String>>(var2, BuilderFactory.STRING_SET);
   }

   public CommandObject<Set<byte[]>> keys(byte[] var1) {
      CommandArguments var2 = this.commandArguments(Protocol.Command.KEYS).key(var1);
      return new CommandObject<Set<byte[]>>(var2, BuilderFactory.BINARY_SET);
   }

   public CommandObject<ScanResult<String>> scan(String var1) {
      return new CommandObject<ScanResult<String>>(this.commandArguments(Protocol.Command.SCAN).add(var1), BuilderFactory.SCAN_RESPONSE);
   }

   public CommandObject<ScanResult<String>> scan(String var1, ScanParams var2) {
      return new CommandObject<ScanResult<String>>(this.commandArguments(Protocol.Command.SCAN).add(var1).addParams(var2), BuilderFactory.SCAN_RESPONSE);
   }

   public CommandObject<ScanResult<String>> scan(String var1, ScanParams var2, String var3) {
      return new CommandObject<ScanResult<String>>(this.commandArguments(Protocol.Command.SCAN).add(var1).addParams(var2).add(Protocol.Keyword.TYPE).add(var3), BuilderFactory.SCAN_RESPONSE);
   }

   public CommandObject<ScanResult<byte[]>> scan(byte[] var1) {
      return new CommandObject<ScanResult<byte[]>>(this.commandArguments(Protocol.Command.SCAN).add(var1), BuilderFactory.SCAN_BINARY_RESPONSE);
   }

   public CommandObject<ScanResult<byte[]>> scan(byte[] var1, ScanParams var2) {
      return new CommandObject<ScanResult<byte[]>>(this.commandArguments(Protocol.Command.SCAN).add(var1).addParams(var2), BuilderFactory.SCAN_BINARY_RESPONSE);
   }

   public CommandObject<ScanResult<byte[]>> scan(byte[] var1, ScanParams var2, byte[] var3) {
      return new CommandObject<ScanResult<byte[]>>(this.commandArguments(Protocol.Command.SCAN).add(var1).addParams(var2).add(Protocol.Keyword.TYPE).add(var3), BuilderFactory.SCAN_BINARY_RESPONSE);
   }

   public final CommandObject<String> randomKey() {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RANDOMKEY), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> randomBinaryKey() {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.RANDOMKEY), BuilderFactory.BINARY);
   }

   public final CommandObject<String> set(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> set(String var1, String var2, SetParams var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2).addParams(var3), BuilderFactory.STRING);
   }

   public final CommandObject<String> set(byte[] var1, byte[] var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> set(byte[] var1, byte[] var2, SetParams var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2).addParams(var3), BuilderFactory.STRING);
   }

   public final CommandObject<String> get(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.GET).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> setGet(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2).add(Protocol.Keyword.GET), BuilderFactory.STRING);
   }

   public final CommandObject<String> setGet(String var1, String var2, SetParams var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2).addParams(var3).add(Protocol.Keyword.GET), BuilderFactory.STRING);
   }

   public final CommandObject<String> getDel(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.GETDEL).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> getEx(String var1, GetExParams var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.GETEX).key(var1).addParams(var2), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> get(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.GET).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> setGet(byte[] var1, byte[] var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2).add(Protocol.Keyword.GET), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> setGet(byte[] var1, byte[] var2, SetParams var3) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.SET).key(var1).add(var2).addParams(var3).add(Protocol.Keyword.GET), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> getDel(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.GETDEL).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> getEx(byte[] var1, GetExParams var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.GETEX).key(var1).addParams(var2), BuilderFactory.BINARY);
   }

   public final CommandObject<String> getSet(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.GETSET).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> getSet(byte[] var1, byte[] var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.GETSET).key(var1).add(var2), BuilderFactory.BINARY);
   }

   public final CommandObject<Long> setnx(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SETNX).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> setex(String var1, long var2, String var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SETEX).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> psetex(String var1, long var2, String var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.PSETEX).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<Long> setnx(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SETNX).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> setex(byte[] var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SETEX).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> psetex(byte[] var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.PSETEX).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<Boolean> setbit(String var1, long var2, boolean var4) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.SETBIT).key(var1).add(var2).add(var4), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> setbit(byte[] var1, long var2, boolean var4) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.SETBIT).key(var1).add(var2).add(var4), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> getbit(String var1, long var2) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.GETBIT).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> getbit(byte[] var1, long var2) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.GETBIT).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> setrange(String var1, long var2, String var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SETRANGE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> setrange(byte[] var1, long var2, byte[] var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SETRANGE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<String> getrange(String var1, long var2, long var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.GETRANGE).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> getrange(byte[] var1, long var2, long var4) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.GETRANGE).key(var1).add(var2).add(var4), BuilderFactory.BINARY);
   }

   public final CommandObject<List<String>> mget(String... var1) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.MGET).keys(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<byte[]>> mget(byte[]... var1) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.MGET).keys(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<String> mset(String... var1) {
      return new CommandObject<String>(this.addFlatKeyValueArgs(this.commandArguments(Protocol.Command.MSET), var1), BuilderFactory.STRING);
   }

   public final CommandObject<Long> msetnx(String... var1) {
      return new CommandObject<Long>(this.addFlatKeyValueArgs(this.commandArguments(Protocol.Command.MSETNX), var1), BuilderFactory.LONG);
   }

   public final CommandObject<String> mset(byte[]... var1) {
      return new CommandObject<String>(this.addFlatKeyValueArgs(this.commandArguments(Protocol.Command.MSET), var1), BuilderFactory.STRING);
   }

   public final CommandObject<Long> msetnx(byte[]... var1) {
      return new CommandObject<Long>(this.addFlatKeyValueArgs(this.commandArguments(Protocol.Command.MSETNX), var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> incr(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.INCR).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> incrBy(String var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.INCRBY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Double> incrByFloat(String var1, double var2) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.INCRBYFLOAT).key(var1).add(var2), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Long> incr(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.INCR).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> incrBy(byte[] var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.INCRBY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Double> incrByFloat(byte[] var1, double var2) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.INCRBYFLOAT).key(var1).add(var2), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Long> decr(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DECR).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> decrBy(String var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DECRBY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> decr(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DECR).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> decrBy(byte[] var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.DECRBY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> append(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.APPEND).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> append(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.APPEND).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> substr(String var1, int var2, int var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SUBSTR).key(var1).add(var2).add(var3), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> substr(byte[] var1, int var2, int var3) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.SUBSTR).key(var1).add(var2).add(var3), BuilderFactory.BINARY);
   }

   public final CommandObject<Long> strlen(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.STRLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> strlen(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.STRLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitcount(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITCOUNT).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitcount(String var1, long var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITCOUNT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitcount(String var1, long var2, long var4, BitCountOption var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITCOUNT).key(var1).add(var2).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitcount(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITCOUNT).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitcount(byte[] var1, long var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITCOUNT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitcount(byte[] var1, long var2, long var4, BitCountOption var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITCOUNT).key(var1).add(var2).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitpos(String var1, boolean var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITPOS).key(var1).add(var2 ? 1 : 0), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitpos(String var1, boolean var2, BitPosParams var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITPOS).key(var1).add(var2 ? 1 : 0).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitpos(byte[] var1, boolean var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITPOS).key(var1).add(var2 ? 1 : 0), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitpos(byte[] var1, boolean var2, BitPosParams var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITPOS).key(var1).add(var2 ? 1 : 0).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> bitfield(String var1, String... var2) {
      return new CommandObject<List<Long>>(this.commandArguments(Protocol.Command.BITFIELD).key(var1).addObjects(var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> bitfieldReadonly(String var1, String... var2) {
      return new CommandObject<List<Long>>(this.commandArguments(Protocol.Command.BITFIELD_RO).key(var1).addObjects(var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> bitfield(byte[] var1, byte[]... var2) {
      return new CommandObject<List<Long>>(this.commandArguments(Protocol.Command.BITFIELD).key(var1).addObjects(var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> bitfieldReadonly(byte[] var1, byte[]... var2) {
      return new CommandObject<List<Long>>(this.commandArguments(Protocol.Command.BITFIELD_RO).key(var1).addObjects(var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<Long> bitop(BitOP var1, String var2, String... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITOP).add(var1).key(var2).keys(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> bitop(BitOP var1, byte[] var2, byte[]... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.BITOP).add(var1).key(var2).keys(var3), BuilderFactory.LONG);
   }

   public final CommandObject<LCSMatchResult> lcs(String var1, String var2, LCSParams var3) {
      return new CommandObject<LCSMatchResult>(this.commandArguments(Protocol.Command.LCS).key(var1).key(var2).addParams(var3), BuilderFactory.STR_ALGO_LCS_RESULT_BUILDER);
   }

   public final CommandObject<LCSMatchResult> lcs(byte[] var1, byte[] var2, LCSParams var3) {
      return new CommandObject<LCSMatchResult>(this.commandArguments(Protocol.Command.LCS).key(var1).key(var2).addParams(var3), BuilderFactory.STR_ALGO_LCS_RESULT_BUILDER);
   }

   public final CommandObject<Long> rpush(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.RPUSH).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> rpush(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.RPUSH).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lpush(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPUSH).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lpush(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPUSH).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> llen(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> llen(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> lrange(String var1, long var2, long var4) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.LRANGE).key(var1).add(var2).add(var4), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<byte[]>> lrange(byte[] var1, long var2, long var4) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.LRANGE).key(var1).add(var2).add(var4), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<String> ltrim(String var1, long var2, long var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LTRIM).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> ltrim(byte[] var1, long var2, long var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LTRIM).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> lindex(String var1, long var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LINDEX).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> lindex(byte[] var1, long var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.LINDEX).key(var1).add(var2), BuilderFactory.BINARY);
   }

   public final CommandObject<String> lset(String var1, long var2, String var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LSET).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> lset(byte[] var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LSET).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<Long> lrem(String var1, long var2, String var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LREM).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lrem(byte[] var1, long var2, byte[] var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LREM).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<String> lpop(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LPOP).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> lpop(String var1, int var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.LPOP).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<byte[]> lpop(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.LPOP).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<List<byte[]>> lpop(byte[] var1, int var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.LPOP).key(var1).add(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<String> rpop(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RPOP).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> rpop(String var1, int var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.RPOP).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<byte[]> rpop(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.RPOP).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<List<byte[]>> rpop(byte[] var1, int var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.RPOP).key(var1).add(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<Long> lpos(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPOS).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lpos(String var1, String var2, LPosParams var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPOS).key(var1).add(var2).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> lpos(String var1, String var2, LPosParams var3, long var4) {
      return new CommandObject<List<Long>>(this.commandArguments(Protocol.Command.LPOS).key(var1).add(var2).addParams(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<Long> lpos(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPOS).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lpos(byte[] var1, byte[] var2, LPosParams var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPOS).key(var1).add(var2).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> lpos(byte[] var1, byte[] var2, LPosParams var3, long var4) {
      return new CommandObject<List<Long>>(this.commandArguments(Protocol.Command.LPOS).key(var1).add(var2).addParams(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<Long> linsert(String var1, ListPosition var2, String var3, String var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LINSERT).key(var1).add(var2).add(var3).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> linsert(byte[] var1, ListPosition var2, byte[] var3, byte[] var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LINSERT).key(var1).add(var2).add(var3).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lpushx(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPUSHX).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> rpushx(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.RPUSHX).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> lpushx(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.LPUSHX).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> rpushx(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.RPUSHX).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> blpop(int var1, String var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.BLPOP).blocking().key(var2).add(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> blpop(int var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.BLPOP).blocking().keys(var2).add(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<KeyValue<String, String>> blpop(double var1, String var3) {
      return new CommandObject<KeyValue<String, String>>(this.commandArguments(Protocol.Command.BLPOP).blocking().key(var3).add(var1), BuilderFactory.KEYED_ELEMENT);
   }

   public final CommandObject<KeyValue<String, String>> blpop(double var1, String... var3) {
      return new CommandObject<KeyValue<String, String>>(this.commandArguments(Protocol.Command.BLPOP).blocking().keys(var3).add(var1), BuilderFactory.KEYED_ELEMENT);
   }

   public final CommandObject<List<byte[]>> blpop(int var1, byte[]... var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.BLPOP).blocking().keys(var2).add(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<KeyValue<byte[], byte[]>> blpop(double var1, byte[]... var3) {
      return new CommandObject<KeyValue<byte[], byte[]>>(this.commandArguments(Protocol.Command.BLPOP).blocking().keys(var3).add(var1), BuilderFactory.BINARY_KEYED_ELEMENT);
   }

   public final CommandObject<List<String>> brpop(int var1, String var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.BRPOP).blocking().key(var2).add(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> brpop(int var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.BRPOP).blocking().keys(var2).add(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<KeyValue<String, String>> brpop(double var1, String var3) {
      return new CommandObject<KeyValue<String, String>>(this.commandArguments(Protocol.Command.BRPOP).blocking().key(var3).add(var1), BuilderFactory.KEYED_ELEMENT);
   }

   public final CommandObject<KeyValue<String, String>> brpop(double var1, String... var3) {
      return new CommandObject<KeyValue<String, String>>(this.commandArguments(Protocol.Command.BRPOP).blocking().keys(var3).add(var1), BuilderFactory.KEYED_ELEMENT);
   }

   public final CommandObject<List<byte[]>> brpop(int var1, byte[]... var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.BRPOP).blocking().keys(var2).add(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<KeyValue<byte[], byte[]>> brpop(double var1, byte[]... var3) {
      return new CommandObject<KeyValue<byte[], byte[]>>(this.commandArguments(Protocol.Command.BRPOP).blocking().keys(var3).add(var1), BuilderFactory.BINARY_KEYED_ELEMENT);
   }

   public final CommandObject<String> rpoplpush(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.RPOPLPUSH).key(var1).key(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> brpoplpush(String var1, String var2, int var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.BRPOPLPUSH).blocking().key(var1).key(var2).add(var3), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> rpoplpush(byte[] var1, byte[] var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.RPOPLPUSH).key(var1).key(var2), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> brpoplpush(byte[] var1, byte[] var2, int var3) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.BRPOPLPUSH).blocking().key(var1).key(var2).add(var3), BuilderFactory.BINARY);
   }

   public final CommandObject<String> lmove(String var1, String var2, ListDirection var3, ListDirection var4) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.LMOVE).key(var1).key(var2).add(var3).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> blmove(String var1, String var2, ListDirection var3, ListDirection var4, double var5) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.BLMOVE).blocking().key(var1).key(var2).add(var3).add(var4).add(var5), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> lmove(byte[] var1, byte[] var2, ListDirection var3, ListDirection var4) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.LMOVE).key(var1).key(var2).add(var3).add(var4), BuilderFactory.BINARY);
   }

   public final CommandObject<byte[]> blmove(byte[] var1, byte[] var2, ListDirection var3, ListDirection var4, double var5) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.BLMOVE).blocking().key(var1).key(var2).add(var3).add(var4).add(var5), BuilderFactory.BINARY);
   }

   public final CommandObject<KeyValue<String, List<String>>> lmpop(ListDirection var1, String... var2) {
      return new CommandObject<KeyValue<String, List<String>>>(this.commandArguments(Protocol.Command.LMPOP).add(var2.length).keys(var2).add(var1), BuilderFactory.KEYED_STRING_LIST);
   }

   public final CommandObject<KeyValue<String, List<String>>> lmpop(ListDirection var1, int var2, String... var3) {
      return new CommandObject<KeyValue<String, List<String>>>(this.commandArguments(Protocol.Command.LMPOP).add(var3.length).keys(var3).add(var1).add(Protocol.Keyword.COUNT).add(var2), BuilderFactory.KEYED_STRING_LIST);
   }

   public final CommandObject<KeyValue<String, List<String>>> blmpop(double var1, ListDirection var3, String... var4) {
      return new CommandObject<KeyValue<String, List<String>>>(this.commandArguments(Protocol.Command.BLMPOP).blocking().add(var1).add(var4.length).keys(var4).add(var3), BuilderFactory.KEYED_STRING_LIST);
   }

   public final CommandObject<KeyValue<String, List<String>>> blmpop(double var1, ListDirection var3, int var4, String... var5) {
      return new CommandObject<KeyValue<String, List<String>>>(this.commandArguments(Protocol.Command.BLMPOP).blocking().add(var1).add(var5.length).keys(var5).add(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.KEYED_STRING_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<byte[]>>> lmpop(ListDirection var1, byte[]... var2) {
      return new CommandObject<KeyValue<byte[], List<byte[]>>>(this.commandArguments(Protocol.Command.LMPOP).add(var2.length).keys(var2).add(var1), BuilderFactory.KEYED_BINARY_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<byte[]>>> lmpop(ListDirection var1, int var2, byte[]... var3) {
      return new CommandObject<KeyValue<byte[], List<byte[]>>>(this.commandArguments(Protocol.Command.LMPOP).add(var3.length).keys(var3).add(var1).add(Protocol.Keyword.COUNT).add(var2), BuilderFactory.KEYED_BINARY_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<byte[]>>> blmpop(double var1, ListDirection var3, byte[]... var4) {
      return new CommandObject<KeyValue<byte[], List<byte[]>>>(this.commandArguments(Protocol.Command.BLMPOP).blocking().add(var1).add(var4.length).keys(var4).add(var3), BuilderFactory.KEYED_BINARY_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<byte[]>>> blmpop(double var1, ListDirection var3, int var4, byte[]... var5) {
      return new CommandObject<KeyValue<byte[], List<byte[]>>>(this.commandArguments(Protocol.Command.BLMPOP).blocking().add(var1).add(var5.length).keys(var5).add(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.KEYED_BINARY_LIST);
   }

   public final CommandObject<Long> hset(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSET).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hset(String var1, Map<String, String> var2) {
      return new CommandObject<Long>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.HSET).key(var1), var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> hget(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.HGET).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> hsetnx(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSETNX).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<String> hmset(String var1, Map<String, String> var2) {
      return new CommandObject<String>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.HMSET).key(var1), var2), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> hmget(String var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.HMGET).key(var1).addObjects(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<Long> hset(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSET).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hset(byte[] var1, Map<byte[], byte[]> var2) {
      return new CommandObject<Long>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.HSET).key(var1), var2), BuilderFactory.LONG);
   }

   public final CommandObject<byte[]> hget(byte[] var1, byte[] var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.HGET).key(var1).add(var2), BuilderFactory.BINARY);
   }

   public final CommandObject<Long> hsetnx(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSETNX).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<String> hmset(byte[] var1, Map<byte[], byte[]> var2) {
      return new CommandObject<String>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.HMSET).key(var1), var2), BuilderFactory.STRING);
   }

   public final CommandObject<List<byte[]>> hmget(byte[] var1, byte[]... var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.HMGET).key(var1).addObjects(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<Long> hincrBy(String var1, String var2, long var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HINCRBY).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Double> hincrByFloat(String var1, String var2, double var3) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.HINCRBYFLOAT).key(var1).add(var2).add(var3), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Boolean> hexists(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.HEXISTS).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> hdel(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HDEL).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hlen(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hincrBy(byte[] var1, byte[] var2, long var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HINCRBY).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Double> hincrByFloat(byte[] var1, byte[] var2, double var3) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.HINCRBYFLOAT).key(var1).add(var2).add(var3), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Boolean> hexists(byte[] var1, byte[] var2) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.HEXISTS).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> hdel(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HDEL).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hlen(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Set<String>> hkeys(String var1) {
      return new CommandObject<Set<String>>(this.commandArguments(Protocol.Command.HKEYS).key(var1), BuilderFactory.STRING_SET);
   }

   public final CommandObject<List<String>> hvals(String var1) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.HVALS).key(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<Set<byte[]>> hkeys(byte[] var1) {
      return new CommandObject<Set<byte[]>>(this.commandArguments(Protocol.Command.HKEYS).key(var1), BuilderFactory.BINARY_SET);
   }

   public final CommandObject<List<byte[]>> hvals(byte[] var1) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.HVALS).key(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<Map<String, String>> hgetAll(String var1) {
      return new CommandObject<Map<String, String>>(this.commandArguments(Protocol.Command.HGETALL).key(var1), BuilderFactory.STRING_MAP);
   }

   public final CommandObject<String> hrandfield(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.HRANDFIELD).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> hrandfield(String var1, long var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.HRANDFIELD).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Map.Entry<String, String>>> hrandfieldWithValues(String var1, long var2) {
      return new CommandObject<List<Map.Entry<String, String>>>(this.commandArguments(Protocol.Command.HRANDFIELD).key(var1).add(var2).add(Protocol.Keyword.WITHVALUES), this.protocol != RedisProtocol.RESP3 ? BuilderFactory.STRING_PAIR_LIST : BuilderFactory.STRING_PAIR_LIST_FROM_PAIRS);
   }

   public final CommandObject<Map<byte[], byte[]>> hgetAll(byte[] var1) {
      return new CommandObject<Map<byte[], byte[]>>(this.commandArguments(Protocol.Command.HGETALL).key(var1), BuilderFactory.BINARY_MAP);
   }

   public final CommandObject<byte[]> hrandfield(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.HRANDFIELD).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<List<byte[]>> hrandfield(byte[] var1, long var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.HRANDFIELD).key(var1).add(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Map.Entry<byte[], byte[]>>> hrandfieldWithValues(byte[] var1, long var2) {
      return new CommandObject<List<Map.Entry<byte[], byte[]>>>(this.commandArguments(Protocol.Command.HRANDFIELD).key(var1).add(var2).add(Protocol.Keyword.WITHVALUES), this.protocol != RedisProtocol.RESP3 ? BuilderFactory.BINARY_PAIR_LIST : BuilderFactory.BINARY_PAIR_LIST_FROM_PAIRS);
   }

   public final CommandObject<ScanResult<Map.Entry<String, String>>> hscan(String var1, String var2, ScanParams var3) {
      return new CommandObject<ScanResult<Map.Entry<String, String>>>(this.commandArguments(Protocol.Command.HSCAN).key(var1).add(var2).addParams(var3), BuilderFactory.HSCAN_RESPONSE);
   }

   public final CommandObject<Long> hstrlen(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSTRLEN).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<ScanResult<Map.Entry<byte[], byte[]>>> hscan(byte[] var1, byte[] var2, ScanParams var3) {
      return new CommandObject<ScanResult<Map.Entry<byte[], byte[]>>>(this.commandArguments(Protocol.Command.HSCAN).key(var1).add(var2).addParams(var3), BuilderFactory.HSCAN_BINARY_RESPONSE);
   }

   public final CommandObject<Long> hstrlen(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSTRLEN).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sadd(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SADD).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sadd(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SADD).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Set<String>> smembers(String var1) {
      return new CommandObject<Set<String>>(this.commandArguments(Protocol.Command.SMEMBERS).key(var1), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Set<byte[]>> smembers(byte[] var1) {
      return new CommandObject<Set<byte[]>>(this.commandArguments(Protocol.Command.SMEMBERS).key(var1), BuilderFactory.BINARY_SET);
   }

   public final CommandObject<Long> srem(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SREM).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> srem(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SREM).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> spop(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SPOP).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> spop(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.SPOP).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<Set<String>> spop(String var1, long var2) {
      return new CommandObject<Set<String>>(this.commandArguments(Protocol.Command.SPOP).key(var1).add(var2), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Set<byte[]>> spop(byte[] var1, long var2) {
      return new CommandObject<Set<byte[]>>(this.commandArguments(Protocol.Command.SPOP).key(var1).add(var2), BuilderFactory.BINARY_SET);
   }

   public final CommandObject<Long> scard(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SCARD).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> scard(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SCARD).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Boolean> sismember(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.SISMEMBER).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> sismember(byte[] var1, byte[] var2) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.SISMEMBER).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<List<Boolean>> smismember(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(Protocol.Command.SMISMEMBER).key(var1).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<List<Boolean>> smismember(byte[] var1, byte[]... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(Protocol.Command.SMISMEMBER).key(var1).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<String> srandmember(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SRANDMEMBER).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> srandmember(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.SRANDMEMBER).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<List<String>> srandmember(String var1, int var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.SRANDMEMBER).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<byte[]>> srandmember(byte[] var1, int var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.SRANDMEMBER).key(var1).add(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<ScanResult<String>> sscan(String var1, String var2, ScanParams var3) {
      return new CommandObject<ScanResult<String>>(this.commandArguments(Protocol.Command.SSCAN).key(var1).add(var2).addParams(var3), BuilderFactory.SSCAN_RESPONSE);
   }

   public final CommandObject<ScanResult<byte[]>> sscan(byte[] var1, byte[] var2, ScanParams var3) {
      return new CommandObject<ScanResult<byte[]>>(this.commandArguments(Protocol.Command.SSCAN).key(var1).add(var2).addParams(var3), BuilderFactory.SSCAN_BINARY_RESPONSE);
   }

   public final CommandObject<Set<String>> sdiff(String... var1) {
      return new CommandObject<Set<String>>(this.commandArguments(Protocol.Command.SDIFF).keys(var1), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Long> sdiffstore(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SDIFFSTORE).key(var1).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Set<byte[]>> sdiff(byte[]... var1) {
      return new CommandObject<Set<byte[]>>(this.commandArguments(Protocol.Command.SDIFF).keys(var1), BuilderFactory.BINARY_SET);
   }

   public final CommandObject<Long> sdiffstore(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SDIFFSTORE).key(var1).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Set<String>> sinter(String... var1) {
      return new CommandObject<Set<String>>(this.commandArguments(Protocol.Command.SINTER).keys(var1), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Long> sinterstore(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SINTERSTORE).key(var1).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sintercard(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SINTERCARD).add(var1.length).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sintercard(int var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SINTERCARD).add(var2.length).keys(var2).add(Protocol.Keyword.LIMIT).add(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Set<byte[]>> sinter(byte[]... var1) {
      return new CommandObject<Set<byte[]>>(this.commandArguments(Protocol.Command.SINTER).keys(var1), BuilderFactory.BINARY_SET);
   }

   public final CommandObject<Long> sinterstore(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SINTERSTORE).key(var1).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sintercard(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SINTERCARD).add(var1.length).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> sintercard(int var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SINTERCARD).add(var2.length).keys(var2).add(Protocol.Keyword.LIMIT).add(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Set<String>> sunion(String... var1) {
      return new CommandObject<Set<String>>(this.commandArguments(Protocol.Command.SUNION).keys(var1), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Long> sunionstore(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SUNIONSTORE).key(var1).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Set<byte[]>> sunion(byte[]... var1) {
      return new CommandObject<Set<byte[]>>(this.commandArguments(Protocol.Command.SUNION).keys(var1), BuilderFactory.BINARY_SET);
   }

   public final CommandObject<Long> sunionstore(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SUNIONSTORE).key(var1).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> smove(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SMOVE).key(var1).key(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> smove(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SMOVE).key(var1).key(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(String var1, double var2, String var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZADD).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(String var1, double var2, String var4, ZAddParams var5) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZADD).key(var1).addParams(var5).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(String var1, Map<String, Double> var2) {
      return new CommandObject<Long>(this.addSortedSetFlatMapArgs(this.commandArguments(Protocol.Command.ZADD).key(var1), var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(String var1, Map<String, Double> var2, ZAddParams var3) {
      return new CommandObject<Long>(this.addSortedSetFlatMapArgs(this.commandArguments(Protocol.Command.ZADD).key(var1).addParams(var3), var2), BuilderFactory.LONG);
   }

   public final CommandObject<Double> zaddIncr(String var1, double var2, String var4, ZAddParams var5) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZADD).key(var1).add(Protocol.Keyword.INCR).addParams(var5).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Long> zadd(byte[] var1, double var2, byte[] var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZADD).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(byte[] var1, double var2, byte[] var4, ZAddParams var5) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZADD).key(var1).addParams(var5).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(byte[] var1, Map<byte[], Double> var2) {
      return new CommandObject<Long>(this.addSortedSetFlatMapArgs(this.commandArguments(Protocol.Command.ZADD).key(var1), var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zadd(byte[] var1, Map<byte[], Double> var2, ZAddParams var3) {
      return new CommandObject<Long>(this.addSortedSetFlatMapArgs(this.commandArguments(Protocol.Command.ZADD).key(var1).addParams(var3), var2), BuilderFactory.LONG);
   }

   public final CommandObject<Double> zaddIncr(byte[] var1, double var2, byte[] var4, ZAddParams var5) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZADD).key(var1).add(Protocol.Keyword.INCR).addParams(var5).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> zincrby(String var1, double var2, String var4) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZINCRBY).key(var1).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> zincrby(String var1, double var2, String var4, ZIncrByParams var5) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZADD).key(var1).addParams(var5).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> zincrby(byte[] var1, double var2, byte[] var4) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZINCRBY).key(var1).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> zincrby(byte[] var1, double var2, byte[] var4, ZIncrByParams var5) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZADD).key(var1).addParams(var5).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Long> zrem(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREM).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zrem(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREM).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zrank(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZRANK).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zrevrank(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREVRANK).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<KeyValue<Long, Double>> zrankWithScore(String var1, String var2) {
      return new CommandObject<KeyValue<Long, Double>>(this.commandArguments(Protocol.Command.ZRANK).key(var1).add(var2).add(Protocol.Keyword.WITHSCORE), BuilderFactory.ZRANK_WITHSCORE_PAIR);
   }

   public final CommandObject<KeyValue<Long, Double>> zrevrankWithScore(String var1, String var2) {
      return new CommandObject<KeyValue<Long, Double>>(this.commandArguments(Protocol.Command.ZREVRANK).key(var1).add(var2).add(Protocol.Keyword.WITHSCORE), BuilderFactory.ZRANK_WITHSCORE_PAIR);
   }

   public final CommandObject<Long> zrank(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZRANK).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zrevrank(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREVRANK).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<KeyValue<Long, Double>> zrankWithScore(byte[] var1, byte[] var2) {
      return new CommandObject<KeyValue<Long, Double>>(this.commandArguments(Protocol.Command.ZRANK).key(var1).add(var2).add(Protocol.Keyword.WITHSCORE), BuilderFactory.ZRANK_WITHSCORE_PAIR);
   }

   public final CommandObject<KeyValue<Long, Double>> zrevrankWithScore(byte[] var1, byte[] var2) {
      return new CommandObject<KeyValue<Long, Double>>(this.commandArguments(Protocol.Command.ZREVRANK).key(var1).add(var2).add(Protocol.Keyword.WITHSCORE), BuilderFactory.ZRANK_WITHSCORE_PAIR);
   }

   public final CommandObject<String> zrandmember(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.ZRANDMEMBER).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> zrandmember(String var1, long var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANDMEMBER).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zrandmemberWithScores(String var1, long var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANDMEMBER).key(var1).add(var2).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<byte[]> zrandmember(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.ZRANDMEMBER).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<List<byte[]>> zrandmember(byte[] var1, long var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANDMEMBER).key(var1).add(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zrandmemberWithScores(byte[] var1, long var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANDMEMBER).key(var1).add(var2).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zcard(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZCARD).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Double> zscore(String var1, String var2) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZSCORE).key(var1).add(var2), BuilderFactory.DOUBLE);
   }

   public final CommandObject<List<Double>> zmscore(String var1, String... var2) {
      return new CommandObject<List<Double>>(this.commandArguments(Protocol.Command.ZMSCORE).key(var1).addObjects(var2), BuilderFactory.DOUBLE_LIST);
   }

   public final CommandObject<Long> zcard(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZCARD).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Double> zscore(byte[] var1, byte[] var2) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.ZSCORE).key(var1).add(var2), BuilderFactory.DOUBLE);
   }

   public final CommandObject<List<Double>> zmscore(byte[] var1, byte[]... var2) {
      return new CommandObject<List<Double>>(this.commandArguments(Protocol.Command.ZMSCORE).key(var1).addObjects(var2), BuilderFactory.DOUBLE_LIST);
   }

   public final CommandObject<Tuple> zpopmax(String var1) {
      return new CommandObject<Tuple>(this.commandArguments(Protocol.Command.ZPOPMAX).key(var1), BuilderFactory.TUPLE);
   }

   public final CommandObject<List<Tuple>> zpopmax(String var1, int var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZPOPMAX).key(var1).add(var2), this.getTupleListBuilder());
   }

   public final CommandObject<Tuple> zpopmin(String var1) {
      return new CommandObject<Tuple>(this.commandArguments(Protocol.Command.ZPOPMIN).key(var1), BuilderFactory.TUPLE);
   }

   public final CommandObject<List<Tuple>> zpopmin(String var1, int var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZPOPMIN).key(var1).add(var2), this.getTupleListBuilder());
   }

   public final CommandObject<Tuple> zpopmax(byte[] var1) {
      return new CommandObject<Tuple>(this.commandArguments(Protocol.Command.ZPOPMAX).key(var1), BuilderFactory.TUPLE);
   }

   public final CommandObject<List<Tuple>> zpopmax(byte[] var1, int var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZPOPMAX).key(var1).add(var2), this.getTupleListBuilder());
   }

   public final CommandObject<Tuple> zpopmin(byte[] var1) {
      return new CommandObject<Tuple>(this.commandArguments(Protocol.Command.ZPOPMIN).key(var1), BuilderFactory.TUPLE);
   }

   public final CommandObject<List<Tuple>> zpopmin(byte[] var1, int var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZPOPMIN).key(var1).add(var2), this.getTupleListBuilder());
   }

   public final CommandObject<KeyValue<String, Tuple>> bzpopmax(double var1, String... var3) {
      return new CommandObject<KeyValue<String, Tuple>>(this.commandArguments(Protocol.Command.BZPOPMAX).blocking().keys(var3).add(var1), BuilderFactory.KEYED_TUPLE);
   }

   public final CommandObject<KeyValue<String, Tuple>> bzpopmin(double var1, String... var3) {
      return new CommandObject<KeyValue<String, Tuple>>(this.commandArguments(Protocol.Command.BZPOPMIN).blocking().keys(var3).add(var1), BuilderFactory.KEYED_TUPLE);
   }

   public final CommandObject<KeyValue<byte[], Tuple>> bzpopmax(double var1, byte[]... var3) {
      return new CommandObject<KeyValue<byte[], Tuple>>(this.commandArguments(Protocol.Command.BZPOPMAX).blocking().keys(var3).add(var1), BuilderFactory.BINARY_KEYED_TUPLE);
   }

   public final CommandObject<KeyValue<byte[], Tuple>> bzpopmin(double var1, byte[]... var3) {
      return new CommandObject<KeyValue<byte[], Tuple>>(this.commandArguments(Protocol.Command.BZPOPMIN).blocking().keys(var3).add(var1), BuilderFactory.BINARY_KEYED_TUPLE);
   }

   public final CommandObject<Long> zcount(String var1, double var2, double var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZCOUNT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zcount(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZCOUNT).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zcount(byte[] var1, double var2, double var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZCOUNT).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zcount(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZCOUNT).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> zrange(String var1, long var2, long var4) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).add(var2).add(var4), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrange(String var1, long var2, long var4) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGE).key(var1).add(var2).add(var4), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zrangeWithScores(String var1, long var2, long var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeWithScores(String var1, long var2, long var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<String>> zrange(String var1, ZRangeParams var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).addParams(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zrangeWithScores(String var1, ZRangeParams var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).addParams(var2).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zrangestore(String var1, String var2, ZRangeParams var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZRANGESTORE).key(var1).add(var2).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> zrangeByScore(String var1, double var2, double var4) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrangeByScore(String var1, String var2, String var3) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrangeByScore(String var1, double var2, double var4) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrangeByScore(String var1, String var2, String var3) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrangeByScore(String var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrangeByScore(String var1, String var2, String var3, int var4, int var5) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrangeByScore(String var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrangeByScore(String var1, String var2, String var3, int var4, int var5) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(String var1, double var2, double var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(String var1, String var2, String var3) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(String var1, double var2, double var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(String var1, String var2, String var3) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<byte[]>> zrange(byte[] var1, long var2, long var4) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).add(var2).add(var4), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrange(byte[] var1, long var2, long var4) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGE).key(var1).add(var2).add(var4), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zrangeWithScores(byte[] var1, long var2, long var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeWithScores(byte[] var1, long var2, long var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<byte[]>> zrange(byte[] var1, ZRangeParams var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).addParams(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zrangeWithScores(byte[] var1, ZRangeParams var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGE).key(var1).addParams(var2).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zrangestore(byte[] var1, byte[] var2, ZRangeParams var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZRANGESTORE).key(var1).add(var2).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<byte[]>> zrangeByScore(byte[] var1, double var2, double var4) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrangeByScore(byte[] var1, double var2, double var4) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrangeByScore(byte[] var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrangeByScore(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrangeByScore(byte[] var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrangeByScore(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(byte[] var1, double var2, double var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, double var2, double var4) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(byte[] var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, double var2, double var4, int var6, int var7) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var4).add(Protocol.Keyword.LIMIT).add(var6).add(var7).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZREVRANGEBYSCORE).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zremrangeByRank(String var1, long var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYRANK).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zremrangeByScore(String var1, double var2, double var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYSCORE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zremrangeByScore(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYSCORE).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zremrangeByRank(byte[] var1, long var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYRANK).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zremrangeByScore(byte[] var1, double var2, double var4) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYSCORE).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zremrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYSCORE).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zlexcount(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZLEXCOUNT).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> zrangeByLex(String var1, String var2, String var3) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGEBYLEX).key(var1).add(var2).add(var3), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrangeByLex(String var1, String var2, String var3, int var4, int var5) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZRANGEBYLEX).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrangeByLex(String var1, String var2, String var3) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGEBYLEX).key(var1).add(var2).add(var3), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> zrevrangeByLex(String var1, String var2, String var3, int var4, int var5) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZREVRANGEBYLEX).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<Long> zremrangeByLex(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYLEX).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zlexcount(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZLEXCOUNT).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<List<byte[]>> zrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGEBYLEX).key(var1).add(var2).add(var3), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrangeByLex(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZRANGEBYLEX).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGEBYLEX).key(var1).add(var2).add(var3), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> zrevrangeByLex(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZREVRANGEBYLEX).key(var1).add(var2).add(var3).add(Protocol.Keyword.LIMIT).add(var4).add(var5), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<Long> zremrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZREMRANGEBYLEX).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<ScanResult<Tuple>> zscan(String var1, String var2, ScanParams var3) {
      return new CommandObject<ScanResult<Tuple>>(this.commandArguments(Protocol.Command.ZSCAN).key(var1).add(var2).addParams(var3), BuilderFactory.ZSCAN_RESPONSE);
   }

   public final CommandObject<ScanResult<Tuple>> zscan(byte[] var1, byte[] var2, ScanParams var3) {
      return new CommandObject<ScanResult<Tuple>>(this.commandArguments(Protocol.Command.ZSCAN).key(var1).add(var2).addParams(var3), BuilderFactory.ZSCAN_RESPONSE);
   }

   public final CommandObject<List<String>> zdiff(String... var1) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZDIFF).add(var1.length).keys(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zdiffWithScores(String... var1) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZDIFF).add(var1.length).keys(var1).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   @Deprecated
   public final CommandObject<Long> zdiffStore(String var1, String... var2) {
      return this.zdiffstore(var1, var2);
   }

   public final CommandObject<Long> zdiffstore(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZDIFFSTORE).key(var1).add(var2.length).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<byte[]>> zdiff(byte[]... var1) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZDIFF).add(var1.length).keys(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zdiffWithScores(byte[]... var1) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZDIFF).add(var1.length).keys(var1).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   @Deprecated
   public final CommandObject<Long> zdiffStore(byte[] var1, byte[]... var2) {
      return this.zdiffstore(var1, var2);
   }

   public final CommandObject<Long> zdiffstore(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZDIFFSTORE).key(var1).add(var2.length).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> zinter(ZParams var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZINTER).add(var2.length).keys(var2).addParams(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zinterWithScores(ZParams var1, String... var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZINTER).add(var2.length).keys(var2).addParams(var1).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zinterstore(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERSTORE).key(var1).add(var2.length).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zinterstore(String var1, ZParams var2, String... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERSTORE).key(var1).add(var3.length).keys(var3).addParams(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zintercard(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERCARD).add(var1.length).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zintercard(long var1, String... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERCARD).add(var3.length).keys(var3).add(Protocol.Keyword.LIMIT).add(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zinterstore(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERSTORE).key(var1).add(var2.length).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zinterstore(byte[] var1, ZParams var2, byte[]... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERSTORE).key(var1).add(var3.length).keys(var3).addParams(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zintercard(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERCARD).add(var1.length).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zintercard(long var1, byte[]... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZINTERCARD).add(var3.length).keys(var3).add(Protocol.Keyword.LIMIT).add(var1), BuilderFactory.LONG);
   }

   public final CommandObject<List<byte[]>> zinter(ZParams var1, byte[]... var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZINTER).add(var2.length).keys(var2).addParams(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zinterWithScores(ZParams var1, byte[]... var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZINTER).add(var2.length).keys(var2).addParams(var1).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zunionstore(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZUNIONSTORE).key(var1).add(var2.length).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zunionstore(String var1, ZParams var2, String... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZUNIONSTORE).key(var1).add(var3.length).keys(var3).addParams(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> zunion(ZParams var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.ZUNION).add(var2.length).keys(var2).addParams(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> zunionWithScores(ZParams var1, String... var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZUNION).add(var2.length).keys(var2).addParams(var1).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<Long> zunionstore(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZUNIONSTORE).key(var1).add(var2.length).keys(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> zunionstore(byte[] var1, ZParams var2, byte[]... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.ZUNIONSTORE).key(var1).add(var3.length).keys(var3).addParams(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<byte[]>> zunion(ZParams var1, byte[]... var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.ZUNION).add(var2.length).keys(var2).addParams(var1), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Tuple>> zunionWithScores(ZParams var1, byte[]... var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(Protocol.Command.ZUNION).add(var2.length).keys(var2).addParams(var1).add(Protocol.Keyword.WITHSCORES), this.getTupleListBuilder());
   }

   public final CommandObject<KeyValue<String, List<Tuple>>> zmpop(SortedSetOption var1, String... var2) {
      return new CommandObject<KeyValue<String, List<Tuple>>>(this.commandArguments(Protocol.Command.ZMPOP).add(var2.length).keys(var2).add(var1), BuilderFactory.KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<String, List<Tuple>>> zmpop(SortedSetOption var1, int var2, String... var3) {
      return new CommandObject<KeyValue<String, List<Tuple>>>(this.commandArguments(Protocol.Command.ZMPOP).add(var3.length).keys(var3).add(var1).add(Protocol.Keyword.COUNT).add(var2), BuilderFactory.KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<String, List<Tuple>>> bzmpop(double var1, SortedSetOption var3, String... var4) {
      return new CommandObject<KeyValue<String, List<Tuple>>>(this.commandArguments(Protocol.Command.BZMPOP).blocking().add(var1).add(var4.length).keys(var4).add(var3), BuilderFactory.KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<String, List<Tuple>>> bzmpop(double var1, SortedSetOption var3, int var4, String... var5) {
      return new CommandObject<KeyValue<String, List<Tuple>>>(this.commandArguments(Protocol.Command.BZMPOP).blocking().add(var1).add(var5.length).keys(var5).add(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<Tuple>>> zmpop(SortedSetOption var1, byte[]... var2) {
      return new CommandObject<KeyValue<byte[], List<Tuple>>>(this.commandArguments(Protocol.Command.ZMPOP).add(var2.length).keys(var2).add(var1), BuilderFactory.BINARY_KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<Tuple>>> zmpop(SortedSetOption var1, int var2, byte[]... var3) {
      return new CommandObject<KeyValue<byte[], List<Tuple>>>(this.commandArguments(Protocol.Command.ZMPOP).add(var3.length).keys(var3).add(var1).add(Protocol.Keyword.COUNT).add(var2), BuilderFactory.BINARY_KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<Tuple>>> bzmpop(double var1, SortedSetOption var3, byte[]... var4) {
      return new CommandObject<KeyValue<byte[], List<Tuple>>>(this.commandArguments(Protocol.Command.BZMPOP).blocking().add(var1).add(var4.length).keys(var4).add(var3), BuilderFactory.BINARY_KEYED_TUPLE_LIST);
   }

   public final CommandObject<KeyValue<byte[], List<Tuple>>> bzmpop(double var1, SortedSetOption var3, int var4, byte[]... var5) {
      return new CommandObject<KeyValue<byte[], List<Tuple>>>(this.commandArguments(Protocol.Command.BZMPOP).blocking().add(var1).add(var5.length).keys(var5).add(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.BINARY_KEYED_TUPLE_LIST);
   }

   private Builder<List<Tuple>> getTupleListBuilder() {
      return this.protocol == RedisProtocol.RESP3 ? BuilderFactory.TUPLE_LIST_RESP3 : BuilderFactory.TUPLE_LIST;
   }

   private Builder<Set<Tuple>> getTupleSetBuilder() {
      return this.protocol == RedisProtocol.RESP3 ? BuilderFactory.TUPLE_ZSET_RESP3 : BuilderFactory.TUPLE_ZSET;
   }

   public final CommandObject<Long> geoadd(String var1, double var2, double var4, String var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOADD).key(var1).add(var2).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geoadd(String var1, Map<String, GeoCoordinate> var2) {
      return new CommandObject<Long>(this.addGeoCoordinateFlatMapArgs(this.commandArguments(Protocol.Command.GEOADD).key(var1), var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geoadd(String var1, GeoAddParams var2, Map<String, GeoCoordinate> var3) {
      return new CommandObject<Long>(this.addGeoCoordinateFlatMapArgs(this.commandArguments(Protocol.Command.GEOADD).key(var1).addParams(var2), var3), BuilderFactory.LONG);
   }

   public final CommandObject<Double> geodist(String var1, String var2, String var3) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.GEODIST).key(var1).add(var2).add(var3), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> geodist(String var1, String var2, String var3, GeoUnit var4) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.GEODIST).key(var1).add(var2).add(var3).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<List<String>> geohash(String var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(Protocol.Command.GEOHASH).key(var1).addObjects(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<GeoCoordinate>> geopos(String var1, String... var2) {
      return new CommandObject<List<GeoCoordinate>>(this.commandArguments(Protocol.Command.GEOPOS).key(var1).addObjects(var2), BuilderFactory.GEO_COORDINATE_LIST);
   }

   public final CommandObject<Long> geoadd(byte[] var1, double var2, double var4, byte[] var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOADD).key(var1).add(var2).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geoadd(byte[] var1, Map<byte[], GeoCoordinate> var2) {
      return new CommandObject<Long>(this.addGeoCoordinateFlatMapArgs(this.commandArguments(Protocol.Command.GEOADD).key(var1), var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geoadd(byte[] var1, GeoAddParams var2, Map<byte[], GeoCoordinate> var3) {
      return new CommandObject<Long>(this.addGeoCoordinateFlatMapArgs(this.commandArguments(Protocol.Command.GEOADD).key(var1).addParams(var2), var3), BuilderFactory.LONG);
   }

   public final CommandObject<Double> geodist(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.GEODIST).key(var1).add(var2).add(var3), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> geodist(byte[] var1, byte[] var2, byte[] var3, GeoUnit var4) {
      return new CommandObject<Double>(this.commandArguments(Protocol.Command.GEODIST).key(var1).add(var2).add(var3).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<List<byte[]>> geohash(byte[] var1, byte[]... var2) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.GEOHASH).key(var1).addObjects(var2), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<GeoCoordinate>> geopos(byte[] var1, byte[]... var2) {
      return new CommandObject<List<GeoCoordinate>>(this.commandArguments(Protocol.Command.GEOPOS).key(var1).addObjects(var2), BuilderFactory.GEO_COORDINATE_LIST);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadius(String var1, double var2, double var4, double var6, GeoUnit var8) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS).key(var1).add(var2).add(var4).add(var6).add(var8), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadius(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS).key(var1).add(var2).add(var4).add(var6).add(var8).addParams(var9), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusReadonly(String var1, double var2, double var4, double var6, GeoUnit var8) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS_RO).key(var1).add(var2).add(var4).add(var6).add(var8), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusReadonly(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS_RO).key(var1).add(var2).add(var4).add(var6).add(var8).addParams(var9), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<Long> georadiusStore(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9, GeoRadiusStoreParam var10) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEORADIUS).key(var1).add(var2).add(var4).add(var6).add(var8).addParams(var9).addParams(var10), BuilderFactory.LONG);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMember(String var1, String var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER).key(var1).add(var2).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMember(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER).key(var1).add(var2).add(var3).add(var5).addParams(var6), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMemberReadonly(String var1, String var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER_RO).key(var1).add(var2).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMemberReadonly(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER_RO).key(var1).add(var2).add(var3).add(var5).addParams(var6), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<Long> georadiusByMemberStore(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6, GeoRadiusStoreParam var7) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER).key(var1).add(var2).add(var3).add(var5).addParams(var6).addParams(var7), BuilderFactory.LONG);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadius(byte[] var1, double var2, double var4, double var6, GeoUnit var8) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS).key(var1).add(var2).add(var4).add(var6).add(var8), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadius(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS).key(var1).add(var2).add(var4).add(var6).add(var8).addParams(var9), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusReadonly(byte[] var1, double var2, double var4, double var6, GeoUnit var8) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS_RO).key(var1).add(var2).add(var4).add(var6).add(var8), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusReadonly(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUS_RO).key(var1).add(var2).add(var4).add(var6).add(var8).addParams(var9), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<Long> georadiusStore(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9, GeoRadiusStoreParam var10) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEORADIUS).key(var1).add(var2).add(var4).add(var6).add(var8).addParams(var9).addParams(var10), BuilderFactory.LONG);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMember(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER).key(var1).add(var2).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMember(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER).key(var1).add(var2).add(var3).add(var5).addParams(var6), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMemberReadonly(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER_RO).key(var1).add(var2).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> georadiusByMemberReadonly(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER_RO).key(var1).add(var2).add(var3).add(var5).addParams(var6), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<Long> georadiusByMemberStore(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6, GeoRadiusStoreParam var7) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEORADIUSBYMEMBER).key(var1).add(var2).add(var3).add(var5).addParams(var6).addParams(var7), BuilderFactory.LONG);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(String var1, String var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMMEMBER).add(var2).add(Protocol.Keyword.BYRADIUS).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(String var1, GeoCoordinate var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMLONLAT).add(var2.getLongitude()).add(var2.getLatitude()).add(Protocol.Keyword.BYRADIUS).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(String var1, String var2, double var3, double var5, GeoUnit var7) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMMEMBER).add(var2).add(Protocol.Keyword.BYBOX).add(var3).add(var5).add(var7), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(String var1, GeoCoordinate var2, double var3, double var5, GeoUnit var7) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMLONLAT).add(var2.getLongitude()).add(var2.getLatitude()).add(Protocol.Keyword.BYBOX).add(var3).add(var5).add(var7), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(String var1, GeoSearchParam var2) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).addParams(var2), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<Long> geosearchStore(String var1, String var2, String var3, double var4, GeoUnit var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMMEMBER).add(var3).add(Protocol.Keyword.BYRADIUS).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(String var1, String var2, GeoCoordinate var3, double var4, GeoUnit var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMLONLAT).add(var3.getLongitude()).add(var3.getLatitude()).add(Protocol.Keyword.BYRADIUS).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(String var1, String var2, String var3, double var4, double var6, GeoUnit var8) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMMEMBER).add(var3).add(Protocol.Keyword.BYBOX).add(var4).add(var6).add(var8), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(String var1, String var2, GeoCoordinate var3, double var4, double var6, GeoUnit var8) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMLONLAT).add(var3.getLongitude()).add(var3.getLatitude()).add(Protocol.Keyword.BYBOX).add(var4).add(var6).add(var8), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(String var1, String var2, GeoSearchParam var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStoreStoreDist(String var1, String var2, GeoSearchParam var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).addParams(var3).add(Protocol.Keyword.STOREDIST), BuilderFactory.LONG);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMMEMBER).add(var2).add(Protocol.Keyword.BYRADIUS).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(byte[] var1, GeoCoordinate var2, double var3, GeoUnit var5) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMLONLAT).add(var2.getLongitude()).add(var2.getLatitude()).add(Protocol.Keyword.BYRADIUS).add(var3).add(var5), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(byte[] var1, byte[] var2, double var3, double var5, GeoUnit var7) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMMEMBER).add(var2).add(Protocol.Keyword.BYBOX).add(var3).add(var5).add(var7), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(byte[] var1, GeoCoordinate var2, double var3, double var5, GeoUnit var7) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).add(Protocol.Keyword.FROMLONLAT).add(var2.getLongitude()).add(var2.getLatitude()).add(Protocol.Keyword.BYBOX).add(var3).add(var5).add(var7), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<List<GeoRadiusResponse>> geosearch(byte[] var1, GeoSearchParam var2) {
      return new CommandObject<List<GeoRadiusResponse>>(this.commandArguments(Protocol.Command.GEOSEARCH).key(var1).addParams(var2), BuilderFactory.GEORADIUS_WITH_PARAMS_RESULT);
   }

   public final CommandObject<Long> geosearchStore(byte[] var1, byte[] var2, byte[] var3, double var4, GeoUnit var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMMEMBER).add(var3).add(Protocol.Keyword.BYRADIUS).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(byte[] var1, byte[] var2, GeoCoordinate var3, double var4, GeoUnit var6) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMLONLAT).add(var3.getLongitude()).add(var3.getLatitude()).add(Protocol.Keyword.BYRADIUS).add(var4).add(var6), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(byte[] var1, byte[] var2, byte[] var3, double var4, double var6, GeoUnit var8) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMMEMBER).add(var3).add(Protocol.Keyword.BYBOX).add(var4).add(var6).add(var8), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(byte[] var1, byte[] var2, GeoCoordinate var3, double var4, double var6, GeoUnit var8) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).add(Protocol.Keyword.FROMLONLAT).add(var3.getLongitude()).add(var3.getLatitude()).add(Protocol.Keyword.BYBOX).add(var4).add(var6).add(var8), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStore(byte[] var1, byte[] var2, GeoSearchParam var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).addParams(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> geosearchStoreStoreDist(byte[] var1, byte[] var2, GeoSearchParam var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.GEOSEARCHSTORE).key(var1).add(var2).addParams(var3).add(Protocol.Keyword.STOREDIST), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pfadd(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PFADD).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> pfmerge(String var1, String... var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.PFMERGE).key(var1).keys(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> pfadd(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PFADD).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<String> pfmerge(byte[] var1, byte[]... var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.PFMERGE).key(var1).keys(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> pfcount(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PFCOUNT).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pfcount(String... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PFCOUNT).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pfcount(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PFCOUNT).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> pfcount(byte[]... var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PFCOUNT).keys(var1), BuilderFactory.LONG);
   }

   public final CommandObject<StreamEntryID> xadd(String var1, StreamEntryID var2, Map<String, String> var3) {
      return new CommandObject<StreamEntryID>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.XADD).key(var1).add(var2 == null ? StreamEntryID.NEW_ENTRY : var2), var3), BuilderFactory.STREAM_ENTRY_ID);
   }

   public final CommandObject<StreamEntryID> xadd(String var1, XAddParams var2, Map<String, String> var3) {
      return new CommandObject<StreamEntryID>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.XADD).key(var1).addParams(var2), var3), BuilderFactory.STREAM_ENTRY_ID);
   }

   public final CommandObject<Long> xlen(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<byte[]> xadd(byte[] var1, XAddParams var2, Map<byte[], byte[]> var3) {
      return new CommandObject<byte[]>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.XADD).key(var1).addParams(var2), var3), BuilderFactory.BINARY);
   }

   public final CommandObject<Long> xlen(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<List<StreamEntry>> xrange(String var1, StreamEntryID var2, StreamEntryID var3) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XRANGE).key(var1).add(var2 == null ? "-" : var2).add(var3 == null ? "+" : var3), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrange(String var1, StreamEntryID var2, StreamEntryID var3, int var4) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XRANGE).key(var1).add(var2 == null ? "-" : var2).add(var3 == null ? "+" : var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrevrange(String var1, StreamEntryID var2, StreamEntryID var3) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XREVRANGE).key(var1).add(var2 == null ? "+" : var2).add(var3 == null ? "-" : var3), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrevrange(String var1, StreamEntryID var2, StreamEntryID var3, int var4) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XREVRANGE).key(var1).add(var2 == null ? "+" : var2).add(var3 == null ? "-" : var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrange(String var1, String var2, String var3) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XRANGE).key(var1).add(var2).add(var3), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrange(String var1, String var2, String var3, int var4) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XRANGE).key(var1).add(var2).add(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrevrange(String var1, String var2, String var3) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XREVRANGE).key(var1).add(var2).add(var3), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntry>> xrevrange(String var1, String var2, String var3, int var4) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XREVRANGE).key(var1).add(var2).add(var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<Object>> xrange(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XRANGE).key(var1).add(var2 == null ? "-" : var2).add(var3 == null ? "+" : var3), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> xrange(byte[] var1, byte[] var2, byte[] var3, int var4) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XRANGE).key(var1).add(var2 == null ? "-" : var2).add(var3 == null ? "+" : var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> xrevrange(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XREVRANGE).key(var1).add(var2 == null ? "+" : var2).add(var3 == null ? "-" : var3), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> xrevrange(byte[] var1, byte[] var2, byte[] var3, int var4) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XREVRANGE).key(var1).add(var2 == null ? "+" : var2).add(var3 == null ? "-" : var3).add(Protocol.Keyword.COUNT).add(var4), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<Long> xack(String var1, String var2, StreamEntryID... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XACK).key(var1).add(var2).addObjects(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> xack(byte[] var1, byte[] var2, byte[]... var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XACK).key(var1).add(var2).addObjects(var3), BuilderFactory.LONG);
   }

   public final CommandObject<String> xgroupCreate(String var1, String var2, StreamEntryID var3, boolean var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.CREATE).key(var1).add(var2).add(var3 == null ? "0-0" : var3);
      if (var4) {
         var5.add(Protocol.Keyword.MKSTREAM);
      }

      return new CommandObject<String>(var5, BuilderFactory.STRING);
   }

   public final CommandObject<String> xgroupSetID(String var1, String var2, StreamEntryID var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.SETID).key(var1).add(var2).add(var3), BuilderFactory.STRING);
   }

   public final CommandObject<Long> xgroupDestroy(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.DESTROY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Boolean> xgroupCreateConsumer(String var1, String var2, String var3) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.CREATECONSUMER).key(var1).add(var2).add(var3), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> xgroupDelConsumer(String var1, String var2, String var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.DELCONSUMER).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<String> xgroupCreate(byte[] var1, byte[] var2, byte[] var3, boolean var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.CREATE).key(var1).add(var2).add(var3);
      if (var4) {
         var5.add(Protocol.Keyword.MKSTREAM);
      }

      return new CommandObject<String>(var5, BuilderFactory.STRING);
   }

   public final CommandObject<String> xgroupSetID(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.SETID).key(var1).add(var2).add(var3), BuilderFactory.STRING);
   }

   public final CommandObject<Long> xgroupDestroy(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.DESTROY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Boolean> xgroupCreateConsumer(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Boolean>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.CREATECONSUMER).key(var1).add(var2).add(var3), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> xgroupDelConsumer(byte[] var1, byte[] var2, byte[] var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XGROUP).add(Protocol.Keyword.DELCONSUMER).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> xdel(String var1, StreamEntryID... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XDEL).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> xtrim(String var1, long var2, boolean var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.XTRIM).key(var1).add(Protocol.Keyword.MAXLEN);
      if (var4) {
         var5.add(Protocol.BYTES_TILDE);
      }

      var5.add(var2);
      return new CommandObject<Long>(var5, BuilderFactory.LONG);
   }

   public final CommandObject<Long> xtrim(String var1, XTrimParams var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XTRIM).key(var1).addParams(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> xdel(byte[] var1, byte[]... var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XDEL).key(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> xtrim(byte[] var1, long var2, boolean var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.XTRIM).key(var1).add(Protocol.Keyword.MAXLEN);
      if (var4) {
         var5.add(Protocol.BYTES_TILDE);
      }

      var5.add(var2);
      return new CommandObject<Long>(var5, BuilderFactory.LONG);
   }

   public final CommandObject<Long> xtrim(byte[] var1, XTrimParams var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.XTRIM).key(var1).addParams(var2), BuilderFactory.LONG);
   }

   public final CommandObject<StreamPendingSummary> xpending(String var1, String var2) {
      return new CommandObject<StreamPendingSummary>(this.commandArguments(Protocol.Command.XPENDING).key(var1).add(var2), BuilderFactory.STREAM_PENDING_SUMMARY);
   }

   public final CommandObject<List<StreamPendingEntry>> xpending(String var1, String var2, XPendingParams var3) {
      return new CommandObject<List<StreamPendingEntry>>(this.commandArguments(Protocol.Command.XPENDING).key(var1).add(var2).addParams(var3), BuilderFactory.STREAM_PENDING_ENTRY_LIST);
   }

   public final CommandObject<Object> xpending(byte[] var1, byte[] var2) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.XPENDING).key(var1).add(var2), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<List<Object>> xpending(byte[] var1, byte[] var2, XPendingParams var3) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XPENDING).key(var1).add(var2).addParams(var3), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<StreamEntry>> xclaim(String var1, String var2, String var3, long var4, XClaimParams var6, StreamEntryID... var7) {
      return new CommandObject<List<StreamEntry>>(this.commandArguments(Protocol.Command.XCLAIM).key(var1).add(var2).add(var3).add(var4).addObjects(var7).addParams(var6), BuilderFactory.STREAM_ENTRY_LIST);
   }

   public final CommandObject<List<StreamEntryID>> xclaimJustId(String var1, String var2, String var3, long var4, XClaimParams var6, StreamEntryID... var7) {
      return new CommandObject<List<StreamEntryID>>(this.commandArguments(Protocol.Command.XCLAIM).key(var1).add(var2).add(var3).add(var4).addObjects(var7).addParams(var6).add(Protocol.Keyword.JUSTID), BuilderFactory.STREAM_ENTRY_ID_LIST);
   }

   public final CommandObject<Map.Entry<StreamEntryID, List<StreamEntry>>> xautoclaim(String var1, String var2, String var3, long var4, StreamEntryID var6, XAutoClaimParams var7) {
      return new CommandObject<Map.Entry<StreamEntryID, List<StreamEntry>>>(this.commandArguments(Protocol.Command.XAUTOCLAIM).key(var1).add(var2).add(var3).add(var4).add(var6).addParams(var7), BuilderFactory.STREAM_AUTO_CLAIM_RESPONSE);
   }

   public final CommandObject<Map.Entry<StreamEntryID, List<StreamEntryID>>> xautoclaimJustId(String var1, String var2, String var3, long var4, StreamEntryID var6, XAutoClaimParams var7) {
      return new CommandObject<Map.Entry<StreamEntryID, List<StreamEntryID>>>(this.commandArguments(Protocol.Command.XAUTOCLAIM).key(var1).add(var2).add(var3).add(var4).add(var6).addParams(var7).add(Protocol.Keyword.JUSTID), BuilderFactory.STREAM_AUTO_CLAIM_JUSTID_RESPONSE);
   }

   public final CommandObject<List<byte[]>> xclaim(byte[] var1, byte[] var2, byte[] var3, long var4, XClaimParams var6, byte[]... var7) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.XCLAIM).key(var1).add(var2).add(var3).add(var4).addObjects(var7).addParams(var6), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<byte[]>> xclaimJustId(byte[] var1, byte[] var2, byte[] var3, long var4, XClaimParams var6, byte[]... var7) {
      return new CommandObject<List<byte[]>>(this.commandArguments(Protocol.Command.XCLAIM).key(var1).add(var2).add(var3).add(var4).addObjects(var7).addParams(var6).add(Protocol.Keyword.JUSTID), BuilderFactory.BINARY_LIST);
   }

   public final CommandObject<List<Object>> xautoclaim(byte[] var1, byte[] var2, byte[] var3, long var4, byte[] var6, XAutoClaimParams var7) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XAUTOCLAIM).key(var1).add(var2).add(var3).add(var4).add(var6).addParams(var7), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> xautoclaimJustId(byte[] var1, byte[] var2, byte[] var3, long var4, byte[] var6, XAutoClaimParams var7) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XAUTOCLAIM).key(var1).add(var2).add(var3).add(var4).add(var6).addParams(var7).add(Protocol.Keyword.JUSTID), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<StreamInfo> xinfoStream(String var1) {
      return new CommandObject<StreamInfo>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.STREAM).key(var1), BuilderFactory.STREAM_INFO);
   }

   public final CommandObject<Object> xinfoStream(byte[] var1) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.STREAM).key(var1), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<StreamFullInfo> xinfoStreamFull(String var1) {
      return new CommandObject<StreamFullInfo>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.STREAM).key(var1).add(Protocol.Keyword.FULL), BuilderFactory.STREAM_FULL_INFO);
   }

   public final CommandObject<StreamFullInfo> xinfoStreamFull(String var1, int var2) {
      return new CommandObject<StreamFullInfo>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.STREAM).key(var1).add(Protocol.Keyword.FULL).add(Protocol.Keyword.COUNT).add(var2), BuilderFactory.STREAM_FULL_INFO);
   }

   public final CommandObject<Object> xinfoStreamFull(byte[] var1, int var2) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.STREAM).key(var1).add(Protocol.Keyword.FULL).add(Protocol.Keyword.COUNT).add(var2), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> xinfoStreamFull(byte[] var1) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.STREAM).key(var1).add(Protocol.Keyword.FULL), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<List<StreamGroupInfo>> xinfoGroups(String var1) {
      return new CommandObject<List<StreamGroupInfo>>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.GROUPS).key(var1), BuilderFactory.STREAM_GROUP_INFO_LIST);
   }

   public final CommandObject<List<Object>> xinfoGroups(byte[] var1) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.GROUPS).key(var1), BuilderFactory.RAW_OBJECT_LIST);
   }

   @Deprecated
   public final CommandObject<List<StreamConsumersInfo>> xinfoConsumers(String var1, String var2) {
      return new CommandObject<List<StreamConsumersInfo>>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.CONSUMERS).key(var1).add(var2), BuilderFactory.STREAM_CONSUMERS_INFO_LIST);
   }

   public final CommandObject<List<StreamConsumerInfo>> xinfoConsumers2(String var1, String var2) {
      return new CommandObject<List<StreamConsumerInfo>>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.CONSUMERS).key(var1).add(var2), BuilderFactory.STREAM_CONSUMER_INFO_LIST);
   }

   public final CommandObject<List<Object>> xinfoConsumers(byte[] var1, byte[] var2) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.XINFO).add(Protocol.Keyword.CONSUMERS).key(var1).add(var2), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Map.Entry<String, List<StreamEntry>>>> xread(XReadParams var1, Map<String, StreamEntryID> var2) {
      CommandArguments var3 = this.commandArguments(Protocol.Command.XREAD).addParams(var1).add(Protocol.Keyword.STREAMS);
      Set var4 = var2.entrySet();
      var4.forEach((var1x) -> var3.key(var1x.getKey()));
      var4.forEach((var1x) -> var3.add(var1x.getValue()));
      return new CommandObject<List<Map.Entry<String, List<StreamEntry>>>>(var3, BuilderFactory.STREAM_READ_RESPONSE);
   }

   public final CommandObject<List<Map.Entry<String, List<StreamEntry>>>> xreadGroup(String var1, String var2, XReadGroupParams var3, Map<String, StreamEntryID> var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.XREADGROUP).add(Protocol.Keyword.GROUP).add(var1).add(var2).addParams(var3).add(Protocol.Keyword.STREAMS);
      Set var6 = var4.entrySet();
      var6.forEach((var1x) -> var5.key(var1x.getKey()));
      var6.forEach((var1x) -> var5.add(var1x.getValue()));
      return new CommandObject<List<Map.Entry<String, List<StreamEntry>>>>(var5, BuilderFactory.STREAM_READ_RESPONSE);
   }

   public final CommandObject<List<Object>> xread(XReadParams var1, Map.Entry<byte[], byte[]>... var2) {
      CommandArguments var3 = this.commandArguments(Protocol.Command.XREAD).addParams(var1).add(Protocol.Keyword.STREAMS);

      for(Map.Entry var7 : var2) {
         var3.key(var7.getKey());
      }

      for(Map.Entry var11 : var2) {
         var3.add(var11.getValue());
      }

      return new CommandObject<List<Object>>(var3, BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> xreadGroup(byte[] var1, byte[] var2, XReadGroupParams var3, Map.Entry<byte[], byte[]>... var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.XREADGROUP).add(Protocol.Keyword.GROUP).add(var1).add(var2).addParams(var3).add(Protocol.Keyword.STREAMS);

      for(Map.Entry var9 : var4) {
         var5.key(var9.getKey());
      }

      for(Map.Entry var13 : var4) {
         var5.add(var13.getValue());
      }

      return new CommandObject<List<Object>>(var5, BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<Object> eval(String var1) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(0), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> eval(String var1, String var2) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(0).processKey(var2), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> eval(String var1, int var2, String... var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(var2).addObjects(var3).processKeys((String[])Arrays.copyOf(var3, var2)), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> eval(String var1, List<String> var2, List<String> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> evalReadonly(String var1, List<String> var2, List<String> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL_RO).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> eval(byte[] var1) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(0), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> eval(byte[] var1, byte[] var2) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(0).processKey(var2), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> eval(byte[] var1, int var2, byte[]... var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(var2).addObjects(var3).processKeys((byte[][])Arrays.copyOf(var3, var2)), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> eval(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> evalReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVAL_RO).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> evalsha(String var1) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(0), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> evalsha(String var1, String var2) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(0).processKey(var2), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> evalsha(String var1, int var2, String... var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(var2).addObjects(var3).processKeys((String[])Arrays.copyOf(var3, var2)), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> evalsha(String var1, List<String> var2, List<String> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> evalshaReadonly(String var1, List<String> var2, List<String> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA_RO).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> evalsha(byte[] var1) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(0), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> evalsha(byte[] var1, byte[] var2) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(0).processKey(var2), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> evalsha(byte[] var1, int var2, byte[]... var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(var2).addObjects(var3).processKeys((byte[][])Arrays.copyOf(var3, var2)), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> evalsha(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> evalshaReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.EVALSHA_RO).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<List<Boolean>> scriptExists(List<String> var1) {
      return new CommandObject<List<Boolean>>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.EXISTS).addObjects((Collection)var1), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<List<Boolean>> scriptExists(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.EXISTS).addObjects(var2).processKey(var1), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<String> scriptLoad(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.LOAD).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> scriptLoad(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.LOAD).add(var1).processKey(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> scriptFlush() {
      return this.SCRIPT_FLUSH_COMMAND_OBJECT;
   }

   public final CommandObject<String> scriptFlush(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.FLUSH).processKey(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> scriptFlush(String var1, FlushMode var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.FLUSH).add(var2).processKey(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> scriptKill() {
      return this.SCRIPT_KILL_COMMAND_OBJECT;
   }

   public final CommandObject<String> scriptKill(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.KILL).processKey(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<Boolean>> scriptExists(byte[] var1, byte[]... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.EXISTS).addObjects(var2).processKey(var1), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<byte[]> scriptLoad(byte[] var1, byte[] var2) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.LOAD).add(var1).processKey(var2), BuilderFactory.BINARY);
   }

   public final CommandObject<String> scriptFlush(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.FLUSH).processKey(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> scriptFlush(byte[] var1, FlushMode var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.FLUSH).add(var2).processKey(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> scriptKill(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.SCRIPT).add(Protocol.Keyword.KILL).processKey(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> slowlogReset() {
      return this.SLOWLOG_RESET_COMMAND_OBJECT;
   }

   public final CommandObject<Object> fcall(String var1, List<String> var2, List<String> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.FCALL).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> fcallReadonly(String var1, List<String> var2, List<String> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.FCALL_RO).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<String> functionDelete(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.DELETE).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<LibraryInfo>> functionList() {
      return new CommandObject<List<LibraryInfo>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST), LibraryInfo.LIBRARY_INFO_LIST);
   }

   public final CommandObject<List<LibraryInfo>> functionList(String var1) {
      return new CommandObject<List<LibraryInfo>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST).add(Protocol.Keyword.LIBRARYNAME).add(var1), LibraryInfo.LIBRARY_INFO_LIST);
   }

   public final CommandObject<List<LibraryInfo>> functionListWithCode() {
      return new CommandObject<List<LibraryInfo>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST).add(Protocol.Keyword.WITHCODE), LibraryInfo.LIBRARY_INFO_LIST);
   }

   public final CommandObject<List<LibraryInfo>> functionListWithCode(String var1) {
      return new CommandObject<List<LibraryInfo>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST).add(Protocol.Keyword.LIBRARYNAME).add(var1).add(Protocol.Keyword.WITHCODE), LibraryInfo.LIBRARY_INFO_LIST);
   }

   public final CommandObject<String> functionLoad(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LOAD).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> functionLoadReplace(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LOAD).add(Protocol.Keyword.REPLACE).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<FunctionStats> functionStats() {
      return new CommandObject<FunctionStats>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.STATS), FunctionStats.FUNCTION_STATS_BUILDER);
   }

   public final CommandObject<Object> functionStatsBinary() {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.STATS), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<String> functionFlush() {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.FLUSH), BuilderFactory.STRING);
   }

   public final CommandObject<String> functionFlush(FlushMode var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.FLUSH).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> functionKill() {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.KILL), BuilderFactory.STRING);
   }

   public final CommandObject<Object> fcall(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.FCALL).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<Object> fcallReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return new CommandObject<Object>(this.commandArguments(Protocol.Command.FCALL_RO).add(var1).add(var2.size()).keys((Collection)var2).addObjects((Collection)var3), BuilderFactory.RAW_OBJECT);
   }

   public final CommandObject<String> functionDelete(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.DELETE).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<byte[]> functionDump() {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.DUMP), BuilderFactory.BINARY);
   }

   public final CommandObject<List<Object>> functionListBinary() {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> functionList(byte[] var1) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST).add(Protocol.Keyword.LIBRARYNAME).add(var1), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> functionListWithCodeBinary() {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST).add(Protocol.Keyword.WITHCODE), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<List<Object>> functionListWithCode(byte[] var1) {
      return new CommandObject<List<Object>>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LIST).add(Protocol.Keyword.LIBRARYNAME).add(var1).add(Protocol.Keyword.WITHCODE), BuilderFactory.RAW_OBJECT_LIST);
   }

   public final CommandObject<String> functionLoad(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LOAD).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> functionLoadReplace(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Keyword.LOAD).add(Protocol.Keyword.REPLACE).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> functionRestore(byte[] var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Command.RESTORE).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> functionRestore(byte[] var1, FunctionRestorePolicy var2) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.FUNCTION).add(Protocol.Command.RESTORE).add(var1).add(var2.getRaw()), BuilderFactory.STRING);
   }

   public final CommandObject<Boolean> copy(String var1, String var2, int var3, boolean var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.COPY).key(var1).key(var2).add(Protocol.Keyword.DB).add(var3);
      if (var4) {
         var5.add(Protocol.Keyword.REPLACE);
      }

      return new CommandObject<Boolean>(var5, BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> copy(byte[] var1, byte[] var2, int var3, boolean var4) {
      CommandArguments var5 = this.commandArguments(Protocol.Command.COPY).key(var1).key(var2).add(Protocol.Keyword.DB).add(var3);
      if (var4) {
         var5.add(Protocol.Keyword.REPLACE);
      }

      return new CommandObject<Boolean>(var5, BuilderFactory.BOOLEAN);
   }

   public final CommandObject<String> migrate(String var1, int var2, String var3, int var4) {
      return this.migrate(var1, var2, (String)var3, 0, var4);
   }

   public final CommandObject<String> migrate(String var1, int var2, String var3, int var4, int var5) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.MIGRATE).add(var1).add(var2).key(var3).add(var4).add(var5), BuilderFactory.STRING);
   }

   public final CommandObject<String> migrate(String var1, int var2, int var3, MigrateParams var4, String... var5) {
      return this.migrate(var1, var2, 0, var3, var4, (String[])var5);
   }

   public final CommandObject<String> migrate(String var1, int var2, int var3, int var4, MigrateParams var5, String... var6) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.MIGRATE).add(var1).add(var2).add(new byte[0]).add(var3).add(var4).addParams(var5).add(Protocol.Keyword.KEYS).keys(var6), BuilderFactory.STRING);
   }

   public final CommandObject<String> migrate(String var1, int var2, byte[] var3, int var4) {
      return this.migrate(var1, var2, (byte[])var3, 0, var4);
   }

   public final CommandObject<String> migrate(String var1, int var2, byte[] var3, int var4, int var5) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.MIGRATE).add(var1).add(var2).key(var3).add(var4).add(var5), BuilderFactory.STRING);
   }

   public final CommandObject<String> migrate(String var1, int var2, int var3, MigrateParams var4, byte[]... var5) {
      return this.migrate(var1, var2, 0, var3, var4, (byte[][])var5);
   }

   public final CommandObject<String> migrate(String var1, int var2, int var3, int var4, MigrateParams var5, byte[]... var6) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.MIGRATE).add(var1).add(var2).add(new byte[0]).add(var3).add(var4).addParams(var5).add(Protocol.Keyword.KEYS).keys(var6), BuilderFactory.STRING);
   }

   public final CommandObject<Long> memoryUsage(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.MEMORY).add(Protocol.Keyword.USAGE).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> memoryUsage(String var1, int var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.MEMORY).add(Protocol.Keyword.USAGE).key(var1).add(Protocol.Keyword.SAMPLES).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> memoryUsage(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.MEMORY).add(Protocol.Keyword.USAGE).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> memoryUsage(byte[] var1, int var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.MEMORY).add(Protocol.Keyword.USAGE).key(var1).add(Protocol.Keyword.SAMPLES).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> objectRefcount(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.REFCOUNT).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<String> objectEncoding(String var1) {
      return new CommandObject<String>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.ENCODING).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<Long> objectIdletime(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.IDLETIME).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> objectFreq(String var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.FREQ).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> objectRefcount(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.REFCOUNT).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<byte[]> objectEncoding(byte[] var1) {
      return new CommandObject<byte[]>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.ENCODING).key(var1), BuilderFactory.BINARY);
   }

   public final CommandObject<Long> objectIdletime(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.IDLETIME).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> objectFreq(byte[] var1) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.OBJECT).add(Protocol.Keyword.FREQ).key(var1), BuilderFactory.LONG);
   }

   public CommandObject<Long> waitReplicas(int var1, long var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.WAIT).add(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> waitReplicas(String var1, int var2, long var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.WAIT).add(var2).add(var3).processKey(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> waitReplicas(byte[] var1, int var2, long var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.WAIT).add(var2).add(var3).processKey(var1), BuilderFactory.LONG);
   }

   public CommandObject<KeyValue<Long, Long>> waitAOF(long var1, long var3, long var5) {
      return new CommandObject<KeyValue<Long, Long>>(this.commandArguments(Protocol.Command.WAITAOF).add(var1).add(var3).add(var5), BuilderFactory.LONG_LONG_PAIR);
   }

   public CommandObject<KeyValue<Long, Long>> waitAOF(byte[] var1, long var2, long var4, long var6) {
      return new CommandObject<KeyValue<Long, Long>>(this.commandArguments(Protocol.Command.WAITAOF).add(var2).add(var4).add(var6).processKey(var1), BuilderFactory.LONG_LONG_PAIR);
   }

   public CommandObject<KeyValue<Long, Long>> waitAOF(String var1, long var2, long var4, long var6) {
      return new CommandObject<KeyValue<Long, Long>>(this.commandArguments(Protocol.Command.WAITAOF).add(var2).add(var4).add(var6).processKey(var1), BuilderFactory.LONG_LONG_PAIR);
   }

   public final CommandObject<Long> publish(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PUBLISH).add(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> publish(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.PUBLISH).add(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> spublish(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SPUBLISH).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> spublish(byte[] var1, byte[] var2) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.SPUBLISH).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hsetObject(String var1, String var2, Object var3) {
      return new CommandObject<Long>(this.commandArguments(Protocol.Command.HSET).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> hsetObject(String var1, Map<String, Object> var2) {
      return new CommandObject<Long>(this.addFlatMapArgs(this.commandArguments(Protocol.Command.HSET).key(var1), var2), BuilderFactory.LONG);
   }

   private boolean isRoundRobinSearchCommand() {
      if (this.broadcastAndRoundRobinConfig == null) {
         return true;
      } else {
         return this.broadcastAndRoundRobinConfig.getRediSearchModeInCluster() != JedisBroadcastAndRoundRobinConfig.RediSearchMode.LIGHT;
      }
   }

   private CommandArguments checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand var1, String var2) {
      CommandArguments var3 = this.commandArguments(var1);
      if (this.isRoundRobinSearchCommand()) {
         var3.add(var2);
      } else {
         var3.key(var2);
      }

      return var3;
   }

   private CommandArguments checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand var1, String var2, String var3) {
      CommandArguments var4 = this.commandArguments(var1);
      if (this.isRoundRobinSearchCommand()) {
         var4.add(var2).add(var3);
      } else {
         var4.key(var2).key(var3);
      }

      return var4;
   }

   private CommandArguments checkAndRoundRobinSearchCommand(CommandArguments var1, byte[] var2) {
      return this.isRoundRobinSearchCommand() ? var1.add(var2) : var1.key(var2);
   }

   private <T> CommandObject<T> directSearchCommand(CommandObject<T> var1, String var2) {
      var1.getArguments().processKey(var2);
      return var1;
   }

   public final CommandObject<String> ftCreate(String var1, IndexOptions var2, Schema var3) {
      CommandArguments var4 = this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.CREATE, var1).addParams(var2).add(SearchProtocol.SearchKeyword.SCHEMA);
      var3.fields.forEach((var1x) -> var4.addParams(var1x));
      return new CommandObject<String>(var4, BuilderFactory.STRING);
   }

   public final CommandObject<String> ftCreate(String var1, FTCreateParams var2, Iterable<SchemaField> var3) {
      CommandArguments var4 = this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.CREATE, var1).addParams(var2).add(SearchProtocol.SearchKeyword.SCHEMA);
      var3.forEach((var1x) -> var4.addParams(var1x));
      return new CommandObject<String>(var4, BuilderFactory.STRING);
   }

   public final CommandObject<String> ftAlter(String var1, Schema var2) {
      CommandArguments var3 = this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.ALTER, var1).add(SearchProtocol.SearchKeyword.SCHEMA).add(SearchProtocol.SearchKeyword.ADD);
      var2.fields.forEach((var1x) -> var3.addParams(var1x));
      return new CommandObject<String>(var3, BuilderFactory.STRING);
   }

   public final CommandObject<String> ftAlter(String var1, Iterable<SchemaField> var2) {
      CommandArguments var3 = this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.ALTER, var1).add(SearchProtocol.SearchKeyword.SCHEMA).add(SearchProtocol.SearchKeyword.ADD);
      var2.forEach((var1x) -> var3.addParams(var1x));
      return new CommandObject<String>(var3, BuilderFactory.STRING);
   }

   public final CommandObject<String> ftAliasAdd(String var1, String var2) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.ALIASADD, var1, var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> ftAliasUpdate(String var1, String var2) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.ALIASUPDATE, var1, var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> ftAliasDel(String var1) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.ALIASDEL, var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> ftDropIndex(String var1) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.DROPINDEX, var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> ftDropIndexDD(String var1) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.DROPINDEX, var1).add(SearchProtocol.SearchKeyword.DD), BuilderFactory.STRING);
   }

   public final CommandObject<SearchResult> ftSearch(String var1, String var2) {
      return new CommandObject<SearchResult>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SEARCH, var1).add(var2), this.getSearchResultBuilder(() -> new SearchResult.SearchResultBuilder(true, false, true)));
   }

   public final CommandObject<SearchResult> ftSearch(String var1, String var2, FTSearchParams var3) {
      return new CommandObject<SearchResult>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SEARCH, var1).add(var2).addParams(var3.dialectOptional(this.searchDialect.get())), this.getSearchResultBuilder(() -> new SearchResult.SearchResultBuilder(!var3.getNoContent(), var3.getWithScores(), true)));
   }

   public final CommandObject<SearchResult> ftSearch(String var1, Query var2) {
      return new CommandObject<SearchResult>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SEARCH, var1).addParams(var2.dialectOptional(this.searchDialect.get())), this.getSearchResultBuilder(() -> new SearchResult.SearchResultBuilder(!var2.getNoContent(), var2.getWithScores(), true)));
   }

   @Deprecated
   public final CommandObject<SearchResult> ftSearch(byte[] var1, Query var2) {
      if (this.protocol == RedisProtocol.RESP3) {
         throw new UnsupportedOperationException("binary ft.search is not implemented with resp3.");
      } else {
         return new CommandObject<SearchResult>(this.checkAndRoundRobinSearchCommand(this.commandArguments(SearchProtocol.SearchCommand.SEARCH), var1).addParams(var2.dialectOptional(this.searchDialect.get())), this.getSearchResultBuilder(() -> new SearchResult.SearchResultBuilder(!var2.getNoContent(), var2.getWithScores(), false)));
      }
   }

   public final CommandObject<String> ftExplain(String var1, Query var2) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.EXPLAIN, var1).addParams(var2.dialectOptional(this.searchDialect.get())), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> ftExplainCLI(String var1, Query var2) {
      return new CommandObject<List<String>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.EXPLAINCLI, var1).addParams(var2.dialectOptional(this.searchDialect.get())), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<AggregationResult> ftAggregate(String var1, AggregationBuilder var2) {
      return new CommandObject<AggregationResult>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.AGGREGATE, var1).addParams(var2.dialectOptional(this.searchDialect.get())), !var2.isWithCursor() ? AggregationResult.SEARCH_AGGREGATION_RESULT : AggregationResult.SEARCH_AGGREGATION_RESULT_WITH_CURSOR);
   }

   public final CommandObject<AggregationResult> ftCursorRead(String var1, long var2, int var4) {
      return new CommandObject<AggregationResult>(this.commandArguments(SearchProtocol.SearchCommand.CURSOR).add(SearchProtocol.SearchKeyword.READ).key(var1).add(var2).add(SearchProtocol.SearchKeyword.COUNT).add(var4), AggregationResult.SEARCH_AGGREGATION_RESULT_WITH_CURSOR);
   }

   public final CommandObject<String> ftCursorDel(String var1, long var2) {
      return new CommandObject<String>(this.commandArguments(SearchProtocol.SearchCommand.CURSOR).add(SearchProtocol.SearchKeyword.DEL).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Map.Entry<AggregationResult, Map<String, Object>>> ftProfileAggregate(String var1, FTProfileParams var2, AggregationBuilder var3) {
      return new CommandObject<Map.Entry<AggregationResult, Map<String, Object>>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.PROFILE, var1).add(SearchProtocol.SearchKeyword.AGGREGATE).addParams(var2).add(SearchProtocol.SearchKeyword.QUERY).addParams(var3.dialectOptional(this.searchDialect.get())), new SearchProfileResponseBuilder(!var3.isWithCursor() ? AggregationResult.SEARCH_AGGREGATION_RESULT : AggregationResult.SEARCH_AGGREGATION_RESULT_WITH_CURSOR));
   }

   public final CommandObject<Map.Entry<SearchResult, Map<String, Object>>> ftProfileSearch(String var1, FTProfileParams var2, Query var3) {
      return new CommandObject<Map.Entry<SearchResult, Map<String, Object>>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.PROFILE, var1).add(SearchProtocol.SearchKeyword.SEARCH).addParams(var2).add(SearchProtocol.SearchKeyword.QUERY).addParams(var3.dialectOptional(this.searchDialect.get())), new SearchProfileResponseBuilder(this.getSearchResultBuilder(() -> new SearchResult.SearchResultBuilder(!var3.getNoContent(), var3.getWithScores(), true))));
   }

   public final CommandObject<Map.Entry<SearchResult, Map<String, Object>>> ftProfileSearch(String var1, FTProfileParams var2, String var3, FTSearchParams var4) {
      return new CommandObject<Map.Entry<SearchResult, Map<String, Object>>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.PROFILE, var1).add(SearchProtocol.SearchKeyword.SEARCH).addParams(var2).add(SearchProtocol.SearchKeyword.QUERY).add(var3).addParams(var4.dialectOptional(this.searchDialect.get())), new SearchProfileResponseBuilder(this.getSearchResultBuilder(() -> new SearchResult.SearchResultBuilder(!var4.getNoContent(), var4.getWithScores(), true))));
   }

   private Builder<SearchResult> getSearchResultBuilder(Supplier<Builder<SearchResult>> var1) {
      return this.protocol == RedisProtocol.RESP3 ? SearchResult.SEARCH_RESULT_BUILDER : (Builder)var1.get();
   }

   public final CommandObject<String> ftSynUpdate(String var1, String var2, String... var3) {
      return new CommandObject<String>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SYNUPDATE, var1).add(var2).addObjects(var3), BuilderFactory.STRING);
   }

   public final CommandObject<Map<String, List<String>>> ftSynDump(String var1) {
      return new CommandObject<Map<String, List<String>>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SYNDUMP, var1), SearchBuilderFactory.SEARCH_SYNONYM_GROUPS);
   }

   public final CommandObject<Long> ftDictAdd(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(SearchProtocol.SearchCommand.DICTADD).add(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> ftDictDel(String var1, String... var2) {
      return new CommandObject<Long>(this.commandArguments(SearchProtocol.SearchCommand.DICTDEL).add(var1).addObjects(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Set<String>> ftDictDump(String var1) {
      return new CommandObject<Set<String>>(this.commandArguments(SearchProtocol.SearchCommand.DICTDUMP).add(var1), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Long> ftDictAddBySampleKey(String var1, String var2, String... var3) {
      return this.<Long>directSearchCommand(this.ftDictAdd(var2, var3), var1);
   }

   public final CommandObject<Long> ftDictDelBySampleKey(String var1, String var2, String... var3) {
      return this.<Long>directSearchCommand(this.ftDictDel(var2, var3), var1);
   }

   public final CommandObject<Set<String>> ftDictDumpBySampleKey(String var1, String var2) {
      return this.<Set<String>>directSearchCommand(this.ftDictDump(var2), var1);
   }

   public final CommandObject<Map<String, Map<String, Double>>> ftSpellCheck(String var1, String var2) {
      return new CommandObject<Map<String, Map<String, Double>>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SPELLCHECK, var1).add(var2), SearchBuilderFactory.SEARCH_SPELLCHECK_RESPONSE);
   }

   public final CommandObject<Map<String, Map<String, Double>>> ftSpellCheck(String var1, String var2, FTSpellCheckParams var3) {
      return new CommandObject<Map<String, Map<String, Double>>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.SPELLCHECK, var1).add(var2).addParams(var3.dialectOptional(this.searchDialect.get())), SearchBuilderFactory.SEARCH_SPELLCHECK_RESPONSE);
   }

   public final CommandObject<Map<String, Object>> ftInfo(String var1) {
      return new CommandObject<Map<String, Object>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.INFO, var1), this.protocol == RedisProtocol.RESP3 ? BuilderFactory.AGGRESSIVE_ENCODED_OBJECT_MAP : BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<Set<String>> ftTagVals(String var1, String var2) {
      return new CommandObject<Set<String>>(this.checkAndRoundRobinSearchCommand(SearchProtocol.SearchCommand.TAGVALS, var1).add(var2), BuilderFactory.STRING_SET);
   }

   public final CommandObject<Map<String, Object>> ftConfigGet(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(SearchProtocol.SearchCommand.CONFIG).add(SearchProtocol.SearchKeyword.GET).add(var1), this.protocol == RedisProtocol.RESP3 ? BuilderFactory.AGGRESSIVE_ENCODED_OBJECT_MAP : BuilderFactory.ENCODED_OBJECT_MAP_FROM_PAIRS);
   }

   public final CommandObject<Map<String, Object>> ftConfigGet(String var1, String var2) {
      return this.<Map<String, Object>>directSearchCommand(this.ftConfigGet(var2), var1);
   }

   public final CommandObject<String> ftConfigSet(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(SearchProtocol.SearchCommand.CONFIG).add(SearchProtocol.SearchKeyword.SET).add(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> ftConfigSet(String var1, String var2, String var3) {
      return this.<String>directSearchCommand(this.ftConfigSet(var2, var3), var1);
   }

   public final CommandObject<Long> ftSugAdd(String var1, String var2, double var3) {
      return new CommandObject<Long>(this.commandArguments(SearchProtocol.SearchCommand.SUGADD).key(var1).add(var2).add(var3), BuilderFactory.LONG);
   }

   public final CommandObject<Long> ftSugAddIncr(String var1, String var2, double var3) {
      return new CommandObject<Long>(this.commandArguments(SearchProtocol.SearchCommand.SUGADD).key(var1).add(var2).add(var3).add(SearchProtocol.SearchKeyword.INCR), BuilderFactory.LONG);
   }

   public final CommandObject<List<String>> ftSugGet(String var1, String var2) {
      return new CommandObject<List<String>>(this.commandArguments(SearchProtocol.SearchCommand.SUGGET).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> ftSugGet(String var1, String var2, boolean var3, int var4) {
      CommandArguments var5 = this.commandArguments(SearchProtocol.SearchCommand.SUGGET).key(var1).add(var2);
      if (var3) {
         var5.add(SearchProtocol.SearchKeyword.FUZZY);
      }

      var5.add(SearchProtocol.SearchKeyword.MAX).add(var4);
      return new CommandObject<List<String>>(var5, BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Tuple>> ftSugGetWithScores(String var1, String var2) {
      return new CommandObject<List<Tuple>>(this.commandArguments(SearchProtocol.SearchCommand.SUGGET).key(var1).add(var2).add(SearchProtocol.SearchKeyword.WITHSCORES), BuilderFactory.TUPLE_LIST);
   }

   public final CommandObject<List<Tuple>> ftSugGetWithScores(String var1, String var2, boolean var3, int var4) {
      CommandArguments var5 = this.commandArguments(SearchProtocol.SearchCommand.SUGGET).key(var1).add(var2);
      if (var3) {
         var5.add(SearchProtocol.SearchKeyword.FUZZY);
      }

      var5.add(SearchProtocol.SearchKeyword.MAX).add(var4);
      var5.add(SearchProtocol.SearchKeyword.WITHSCORES);
      return new CommandObject<List<Tuple>>(var5, BuilderFactory.TUPLE_LIST);
   }

   public final CommandObject<Boolean> ftSugDel(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(SearchProtocol.SearchCommand.SUGDEL).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> ftSugLen(String var1) {
      return new CommandObject<Long>(this.commandArguments(SearchProtocol.SearchCommand.SUGLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Set<String>> ftList() {
      return new CommandObject<Set<String>>(this.commandArguments(SearchProtocol.SearchCommand._LIST), BuilderFactory.STRING_SET);
   }

   public final CommandObject<String> jsonSet(String var1, Path2 var2, Object var3) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(var3), BuilderFactory.STRING);
   }

   public final CommandObject<String> jsonSetWithEscape(String var1, Path2 var2, Object var3) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.STRING);
   }

   @Deprecated
   public final CommandObject<String> jsonSet(String var1, Path var2, Object var3) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.STRING);
   }

   @Deprecated
   public final CommandObject<String> jsonSetWithPlainString(String var1, Path var2, String var3) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(var3), BuilderFactory.STRING);
   }

   public final CommandObject<String> jsonSet(String var1, Path2 var2, Object var3, JsonSetParams var4) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(var3).addParams(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> jsonSetWithEscape(String var1, Path2 var2, Object var3, JsonSetParams var4) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)).addParams(var4), BuilderFactory.STRING);
   }

   @Deprecated
   public final CommandObject<String> jsonSet(String var1, Path var2, Object var3, JsonSetParams var4) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.SET).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)).addParams(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> jsonMerge(String var1, Path2 var2, Object var3) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.MERGE).key(var1).add(var2).add(var3), BuilderFactory.STRING);
   }

   @Deprecated
   public final CommandObject<String> jsonMerge(String var1, Path var2, Object var3) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.MERGE).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.STRING);
   }

   public final CommandObject<Object> jsonGet(String var1) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.GET).key(var1), this.protocol != RedisProtocol.RESP3 ? this.JSON_GENERIC_OBJECT : JsonBuilderFactory.JSON_OBJECT);
   }

   @Deprecated
   public final <T> CommandObject<T> jsonGet(String var1, Class<T> var2) {
      return new CommandObject<T>(this.commandArguments(JsonProtocol.JsonCommand.GET).key(var1), new JsonObjectBuilder(var2));
   }

   public final CommandObject<Object> jsonGet(String var1, Path2... var2) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.GET).key(var1).addObjects(var2), JsonBuilderFactory.JSON_OBJECT);
   }

   @Deprecated
   public final CommandObject<Object> jsonGet(String var1, Path... var2) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.GET).key(var1).addObjects(var2), this.JSON_GENERIC_OBJECT);
   }

   @Deprecated
   public final CommandObject<String> jsonGetAsPlainString(String var1, Path var2) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.GET).key(var1).add(var2), BuilderFactory.STRING);
   }

   @Deprecated
   public final <T> CommandObject<T> jsonGet(String var1, Class<T> var2, Path... var3) {
      return new CommandObject<T>(this.commandArguments(JsonProtocol.JsonCommand.GET).key(var1).addObjects(var3), new JsonObjectBuilder(var2));
   }

   public final CommandObject<List<JSONArray>> jsonMGet(Path2 var1, String... var2) {
      return new CommandObject<List<JSONArray>>(this.commandArguments(JsonProtocol.JsonCommand.MGET).keys(var2).add(var1), JsonBuilderFactory.JSON_ARRAY_LIST);
   }

   @Deprecated
   public final <T> CommandObject<List<T>> jsonMGet(Path var1, Class<T> var2, String... var3) {
      return new CommandObject<List<T>>(this.commandArguments(JsonProtocol.JsonCommand.MGET).keys(var3).add(var1), new JsonObjectListBuilder(var2));
   }

   public final CommandObject<Long> jsonDel(String var1) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.DEL).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> jsonDel(String var1, Path2 var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.DEL).key(var1).add(var2), BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Long> jsonDel(String var1, Path var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.DEL).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> jsonClear(String var1) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.CLEAR).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Long> jsonClear(String var1, Path2 var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.CLEAR).key(var1).add(var2), BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Long> jsonClear(String var1, Path var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.CLEAR).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<Boolean>> jsonToggle(String var1, Path2 var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(JsonProtocol.JsonCommand.TOGGLE).key(var1).add(var2), BuilderFactory.BOOLEAN_LIST);
   }

   @Deprecated
   public final CommandObject<String> jsonToggle(String var1, Path var2) {
      return new CommandObject<String>(this.commandArguments(JsonProtocol.JsonCommand.TOGGLE).key(var1).add(var2), BuilderFactory.STRING);
   }

   @Deprecated
   public final CommandObject<Class<?>> jsonType(String var1) {
      return new CommandObject<Class<?>>(this.commandArguments(JsonProtocol.JsonCommand.TYPE).key(var1), JsonBuilderFactory.JSON_TYPE);
   }

   public final CommandObject<List<Class<?>>> jsonType(String var1, Path2 var2) {
      return new CommandObject<List<Class<?>>>(this.commandArguments(JsonProtocol.JsonCommand.TYPE).key(var1).add(var2), this.protocol != RedisProtocol.RESP3 ? JsonBuilderFactory.JSON_TYPE_LIST : JsonBuilderFactory.JSON_TYPE_RESPONSE_RESP3_COMPATIBLE);
   }

   @Deprecated
   public final CommandObject<Class<?>> jsonType(String var1, Path var2) {
      return new CommandObject<Class<?>>(this.commandArguments(JsonProtocol.JsonCommand.TYPE).key(var1).add(var2), JsonBuilderFactory.JSON_TYPE);
   }

   @Deprecated
   public final CommandObject<Long> jsonStrAppend(String var1, Object var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.STRAPPEND).key(var1).add(this.getJsonObjectMapper().toJson(var2)), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonStrAppend(String var1, Path2 var2, Object var3) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.STRAPPEND).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonStrAppend(String var1, Path var2, Object var3) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.STRAPPEND).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Long> jsonStrLen(String var1) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.STRLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonStrLen(String var1, Path2 var2) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.STRLEN).key(var1).add(var2), BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonStrLen(String var1, Path var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.STRLEN).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Object> jsonNumIncrBy(String var1, Path2 var2, double var3) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.NUMINCRBY).key(var1).add(var2).add(var3), JsonBuilderFactory.JSON_ARRAY_OR_DOUBLE_LIST);
   }

   @Deprecated
   public final CommandObject<Double> jsonNumIncrBy(String var1, Path var2, double var3) {
      return new CommandObject<Double>(this.commandArguments(JsonProtocol.JsonCommand.NUMINCRBY).key(var1).add(var2).add(var3), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Long> jsonArrAppend(String var1, String var2, JSONObject... var3) {
      CommandArguments var4 = this.commandArguments(JsonProtocol.JsonCommand.ARRAPPEND).key(var1).add(var2);

      for(JSONObject var8 : var3) {
         var4.add(var8);
      }

      return new CommandObject<Long>(var4, BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonArrAppend(String var1, Path2 var2, Object... var3) {
      CommandArguments var4 = this.commandArguments(JsonProtocol.JsonCommand.ARRAPPEND).key(var1).add(var2).addObjects(var3);
      return new CommandObject<List<Long>>(var4, BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> jsonArrAppendWithEscape(String var1, Path2 var2, Object... var3) {
      CommandArguments var4 = this.commandArguments(JsonProtocol.JsonCommand.ARRAPPEND).key(var1).add(var2);

      for(Object var8 : var3) {
         var4.add(this.getJsonObjectMapper().toJson(var8));
      }

      return new CommandObject<List<Long>>(var4, BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonArrAppend(String var1, Path var2, Object... var3) {
      CommandArguments var4 = this.commandArguments(JsonProtocol.JsonCommand.ARRAPPEND).key(var1).add(var2);

      for(Object var8 : var3) {
         var4.add(this.getJsonObjectMapper().toJson(var8));
      }

      return new CommandObject<Long>(var4, BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonArrIndex(String var1, Path2 var2, Object var3) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.ARRINDEX).key(var1).add(var2).add(var3), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> jsonArrIndexWithEscape(String var1, Path2 var2, Object var3) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.ARRINDEX).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonArrIndex(String var1, Path var2, Object var3) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.ARRINDEX).key(var1).add(var2).add(this.getJsonObjectMapper().toJson(var3)), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonArrInsert(String var1, Path2 var2, int var3, Object... var4) {
      CommandArguments var5 = this.commandArguments(JsonProtocol.JsonCommand.ARRINSERT).key(var1).add(var2).add(var3).addObjects(var4);
      return new CommandObject<List<Long>>(var5, BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> jsonArrInsertWithEscape(String var1, Path2 var2, int var3, Object... var4) {
      CommandArguments var5 = this.commandArguments(JsonProtocol.JsonCommand.ARRINSERT).key(var1).add(var2).add(var3);

      for(Object var9 : var4) {
         var5.add(this.getJsonObjectMapper().toJson(var9));
      }

      return new CommandObject<List<Long>>(var5, BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonArrInsert(String var1, Path var2, int var3, Object... var4) {
      CommandArguments var5 = this.commandArguments(JsonProtocol.JsonCommand.ARRINSERT).key(var1).add(var2).add(var3);

      for(Object var9 : var4) {
         var5.add(this.getJsonObjectMapper().toJson(var9));
      }

      return new CommandObject<Long>(var5, BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Object> jsonArrPop(String var1) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1), new JsonObjectBuilder(Object.class));
   }

   @Deprecated
   public final <T> CommandObject<T> jsonArrPop(String var1, Class<T> var2) {
      return new CommandObject<T>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1), new JsonObjectBuilder(var2));
   }

   public final CommandObject<List<Object>> jsonArrPop(String var1, Path2 var2) {
      return new CommandObject<List<Object>>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1).add(var2), new JsonObjectListBuilder(Object.class));
   }

   @Deprecated
   public final CommandObject<Object> jsonArrPop(String var1, Path var2) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1).add(var2), new JsonObjectBuilder(Object.class));
   }

   @Deprecated
   public final <T> CommandObject<T> jsonArrPop(String var1, Class<T> var2, Path var3) {
      return new CommandObject<T>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1).add(var3), new JsonObjectBuilder(var2));
   }

   public final CommandObject<List<Object>> jsonArrPop(String var1, Path2 var2, int var3) {
      return new CommandObject<List<Object>>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1).add(var2).add(var3), new JsonObjectListBuilder(Object.class));
   }

   @Deprecated
   public final CommandObject<Object> jsonArrPop(String var1, Path var2, int var3) {
      return new CommandObject<Object>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1).add(var2).add(var3), new JsonObjectBuilder(Object.class));
   }

   @Deprecated
   public final <T> CommandObject<T> jsonArrPop(String var1, Class<T> var2, Path var3, int var4) {
      return new CommandObject<T>(this.commandArguments(JsonProtocol.JsonCommand.ARRPOP).key(var1).add(var3).add(var4), new JsonObjectBuilder(var2));
   }

   @Deprecated
   public final CommandObject<Long> jsonArrLen(String var1) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.ARRLEN).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonArrLen(String var1, Path2 var2) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.ARRLEN).key(var1).add(var2), BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonArrLen(String var1, Path var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.ARRLEN).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonArrTrim(String var1, Path2 var2, int var3, int var4) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.ARRTRIM).key(var1).add(var2).add(var3).add(var4), BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonArrTrim(String var1, Path var2, int var3, int var4) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.ARRTRIM).key(var1).add(var2).add(var3).add(var4), BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Long> jsonObjLen(String var1) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.OBJLEN).key(var1), BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Long> jsonObjLen(String var1, Path var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.OBJLEN).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonObjLen(String var1, Path2 var2) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.OBJLEN).key(var1).add(var2), BuilderFactory.LONG_LIST);
   }

   @Deprecated
   public final CommandObject<List<String>> jsonObjKeys(String var1) {
      return new CommandObject<List<String>>(this.commandArguments(JsonProtocol.JsonCommand.OBJKEYS).key(var1), BuilderFactory.STRING_LIST);
   }

   @Deprecated
   public final CommandObject<List<String>> jsonObjKeys(String var1, Path var2) {
      return new CommandObject<List<String>>(this.commandArguments(JsonProtocol.JsonCommand.OBJKEYS).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<List<String>>> jsonObjKeys(String var1, Path2 var2) {
      return new CommandObject<List<List<String>>>(this.commandArguments(JsonProtocol.JsonCommand.OBJKEYS).key(var1).add(var2), BuilderFactory.STRING_LIST_LIST);
   }

   @Deprecated
   public final CommandObject<Long> jsonDebugMemory(String var1) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.DEBUG).add("MEMORY").key(var1), BuilderFactory.LONG);
   }

   @Deprecated
   public final CommandObject<Long> jsonDebugMemory(String var1, Path var2) {
      return new CommandObject<Long>(this.commandArguments(JsonProtocol.JsonCommand.DEBUG).add("MEMORY").key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> jsonDebugMemory(String var1, Path2 var2) {
      return new CommandObject<List<Long>>(this.commandArguments(JsonProtocol.JsonCommand.DEBUG).add("MEMORY").key(var1).add(var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<String> tsCreate(String var1) {
      return new CommandObject<String>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.CREATE).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> tsCreate(String var1, TSCreateParams var2) {
      return new CommandObject<String>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.CREATE).key(var1).addParams(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> tsDel(String var1, long var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.DEL).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<String> tsAlter(String var1, TSAlterParams var2) {
      return new CommandObject<String>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.ALTER).key(var1).addParams(var2), BuilderFactory.STRING);
   }

   public final CommandObject<Long> tsAdd(String var1, double var2) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.ADD).key(var1).add(Protocol.BYTES_ASTERISK).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> tsAdd(String var1, long var2, double var4) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.ADD).key(var1).add(var2).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> tsAdd(String var1, long var2, double var4, TSCreateParams var6) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.ADD).key(var1).add(var2).add(var4).addParams(var6), BuilderFactory.LONG);
   }

   public final CommandObject<List<Long>> tsMAdd(Map.Entry<String, TSElement>... var1) {
      CommandArguments var2 = this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.MADD);

      for(Map.Entry var6 : var1) {
         var2.key(var6.getKey()).add(((TSElement)var6.getValue()).getTimestamp()).add(((TSElement)var6.getValue()).getValue());
      }

      return new CommandObject<List<Long>>(var2, BuilderFactory.LONG_LIST);
   }

   public final CommandObject<Long> tsIncrBy(String var1, double var2) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.INCRBY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> tsIncrBy(String var1, double var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.INCRBY).key(var1).add(var2).add(TimeSeriesProtocol.TimeSeriesKeyword.TIMESTAMP).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<Long> tsDecrBy(String var1, double var2) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.DECRBY).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Long> tsDecrBy(String var1, double var2, long var4) {
      return new CommandObject<Long>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.DECRBY).key(var1).add(var2).add(TimeSeriesProtocol.TimeSeriesKeyword.TIMESTAMP).add(var4), BuilderFactory.LONG);
   }

   public final CommandObject<List<TSElement>> tsRange(String var1, long var2, long var4) {
      return new CommandObject<List<TSElement>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.RANGE).key(var1).add(var2).add(var4), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST);
   }

   public final CommandObject<List<TSElement>> tsRange(String var1, TSRangeParams var2) {
      return new CommandObject<List<TSElement>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.RANGE).key(var1).addParams(var2), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST);
   }

   public final CommandObject<List<TSElement>> tsRevRange(String var1, long var2, long var4) {
      return new CommandObject<List<TSElement>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.REVRANGE).key(var1).add(var2).add(var4), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST);
   }

   public final CommandObject<List<TSElement>> tsRevRange(String var1, TSRangeParams var2) {
      return new CommandObject<List<TSElement>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.REVRANGE).key(var1).addParams(var2), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST);
   }

   public final CommandObject<Map<String, TSMRangeElements>> tsMRange(long var1, long var3, String... var5) {
      return new CommandObject<Map<String, TSMRangeElements>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.MRANGE).add(var1).add(var3).add(TimeSeriesProtocol.TimeSeriesKeyword.FILTER).addObjects(var5), this.getTimeseriesMultiRangeResponseBuilder());
   }

   public final CommandObject<Map<String, TSMRangeElements>> tsMRange(TSMRangeParams var1) {
      return new CommandObject<Map<String, TSMRangeElements>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.MRANGE).addParams(var1), this.getTimeseriesMultiRangeResponseBuilder());
   }

   public final CommandObject<Map<String, TSMRangeElements>> tsMRevRange(long var1, long var3, String... var5) {
      return new CommandObject<Map<String, TSMRangeElements>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.MREVRANGE).add(var1).add(var3).add(TimeSeriesProtocol.TimeSeriesKeyword.FILTER).addObjects(var5), this.getTimeseriesMultiRangeResponseBuilder());
   }

   public final CommandObject<Map<String, TSMRangeElements>> tsMRevRange(TSMRangeParams var1) {
      return new CommandObject<Map<String, TSMRangeElements>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.MREVRANGE).addParams(var1), this.getTimeseriesMultiRangeResponseBuilder());
   }

   public final CommandObject<TSElement> tsGet(String var1) {
      return new CommandObject<TSElement>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.GET).key(var1), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT);
   }

   public final CommandObject<TSElement> tsGet(String var1, TSGetParams var2) {
      return new CommandObject<TSElement>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.GET).key(var1).addParams(var2), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT);
   }

   public final CommandObject<Map<String, TSMGetElement>> tsMGet(TSMGetParams var1, String... var2) {
      return new CommandObject<Map<String, TSMGetElement>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.MGET).addParams(var1).add(TimeSeriesProtocol.TimeSeriesKeyword.FILTER).addObjects(var2), this.protocol == RedisProtocol.RESP3 ? TimeSeriesBuilderFactory.TIMESERIES_MGET_RESPONSE_RESP3 : TimeSeriesBuilderFactory.TIMESERIES_MGET_RESPONSE);
   }

   public final CommandObject<String> tsCreateRule(String var1, String var2, AggregationType var3, long var4) {
      return new CommandObject<String>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.CREATERULE).key(var1).key(var2).add(TimeSeriesProtocol.TimeSeriesKeyword.AGGREGATION).add(var3).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> tsCreateRule(String var1, String var2, AggregationType var3, long var4, long var6) {
      return new CommandObject<String>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.CREATERULE).key(var1).key(var2).add(TimeSeriesProtocol.TimeSeriesKeyword.AGGREGATION).add(var3).add(var4).add(var6), BuilderFactory.STRING);
   }

   public final CommandObject<String> tsDeleteRule(String var1, String var2) {
      return new CommandObject<String>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.DELETERULE).key(var1).key(var2), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> tsQueryIndex(String... var1) {
      return new CommandObject<List<String>>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.QUERYINDEX).addObjects(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<TSInfo> tsInfo(String var1) {
      return new CommandObject<TSInfo>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.INFO).key(var1), this.getTimeseriesInfoBuilder());
   }

   public final CommandObject<TSInfo> tsInfoDebug(String var1) {
      return new CommandObject<TSInfo>(this.commandArguments(TimeSeriesProtocol.TimeSeriesCommand.INFO).key(var1).add(TimeSeriesProtocol.TimeSeriesKeyword.DEBUG), this.getTimeseriesInfoBuilder());
   }

   private Builder<Map<String, TSMRangeElements>> getTimeseriesMultiRangeResponseBuilder() {
      return this.protocol == RedisProtocol.RESP3 ? TimeSeriesBuilderFactory.TIMESERIES_MRANGE_RESPONSE_RESP3 : TimeSeriesBuilderFactory.TIMESERIES_MRANGE_RESPONSE;
   }

   private Builder<TSInfo> getTimeseriesInfoBuilder() {
      return this.protocol == RedisProtocol.RESP3 ? TSInfo.TIMESERIES_INFO_RESP3 : TSInfo.TIMESERIES_INFO;
   }

   public final CommandObject<String> bfReserve(String var1, double var2, long var4) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.RESERVE).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> bfReserve(String var1, double var2, long var4, BFReserveParams var6) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.RESERVE).key(var1).add(var2).add(var4).addParams(var6), BuilderFactory.STRING);
   }

   public final CommandObject<Boolean> bfAdd(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.ADD).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<List<Boolean>> bfMAdd(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.MADD).key(var1).addObjects(var2), BuilderFactory.BOOLEAN_WITH_ERROR_LIST);
   }

   public final CommandObject<List<Boolean>> bfInsert(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.INSERT).key(var1).add(RedisBloomProtocol.RedisBloomKeyword.ITEMS).addObjects(var2), BuilderFactory.BOOLEAN_WITH_ERROR_LIST);
   }

   public final CommandObject<List<Boolean>> bfInsert(String var1, BFInsertParams var2, String... var3) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.INSERT).key(var1).addParams(var2).add(RedisBloomProtocol.RedisBloomKeyword.ITEMS).addObjects(var3), BuilderFactory.BOOLEAN_WITH_ERROR_LIST);
   }

   public final CommandObject<Boolean> bfExists(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.EXISTS).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<List<Boolean>> bfMExists(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.MEXISTS).key(var1).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<Map.Entry<Long, byte[]>> bfScanDump(String var1, long var2) {
      return new CommandObject<Map.Entry<Long, byte[]>>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.SCANDUMP).key(var1).add(var2), BLOOM_SCANDUMP_RESPONSE);
   }

   public final CommandObject<String> bfLoadChunk(String var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.LOADCHUNK).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<Long> bfCard(String var1) {
      return new CommandObject<Long>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.CARD).key(var1), BuilderFactory.LONG);
   }

   public final CommandObject<Map<String, Object>> bfInfo(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(RedisBloomProtocol.BloomFilterCommand.INFO).key(var1), BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<String> cfReserve(String var1, long var2) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.RESERVE).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> cfReserve(String var1, long var2, CFReserveParams var4) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.RESERVE).key(var1).add(var2).addParams(var4), BuilderFactory.STRING);
   }

   public final CommandObject<Boolean> cfAdd(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.ADD).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Boolean> cfAddNx(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.ADDNX).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<List<Boolean>> cfInsert(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.INSERT).key(var1).add(RedisBloomProtocol.RedisBloomKeyword.ITEMS).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<List<Boolean>> cfInsert(String var1, CFInsertParams var2, String... var3) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.INSERT).key(var1).addParams(var2).add(RedisBloomProtocol.RedisBloomKeyword.ITEMS).addObjects(var3), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<List<Boolean>> cfInsertNx(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.INSERTNX).key(var1).add(RedisBloomProtocol.RedisBloomKeyword.ITEMS).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<List<Boolean>> cfInsertNx(String var1, CFInsertParams var2, String... var3) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.INSERTNX).key(var1).addParams(var2).add(RedisBloomProtocol.RedisBloomKeyword.ITEMS).addObjects(var3), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<Boolean> cfExists(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.EXISTS).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<List<Boolean>> cfMExists(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.MEXISTS).key(var1).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<Boolean> cfDel(String var1, String var2) {
      return new CommandObject<Boolean>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.DEL).key(var1).add(var2), BuilderFactory.BOOLEAN);
   }

   public final CommandObject<Long> cfCount(String var1, String var2) {
      return new CommandObject<Long>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.COUNT).key(var1).add(var2), BuilderFactory.LONG);
   }

   public final CommandObject<Map.Entry<Long, byte[]>> cfScanDump(String var1, long var2) {
      return new CommandObject<Map.Entry<Long, byte[]>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.SCANDUMP).key(var1).add(var2), BLOOM_SCANDUMP_RESPONSE);
   }

   public final CommandObject<String> cfLoadChunk(String var1, long var2, byte[] var4) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.LOADCHUNK).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<Map<String, Object>> cfInfo(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(RedisBloomProtocol.CuckooFilterCommand.INFO).key(var1), BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<String> cmsInitByDim(String var1, long var2, long var4) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.INITBYDIM).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<String> cmsInitByProb(String var1, double var2, double var4) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.INITBYPROB).key(var1).add(var2).add(var4), BuilderFactory.STRING);
   }

   public final CommandObject<List<Long>> cmsIncrBy(String var1, Map<String, Long> var2) {
      CommandArguments var3 = this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.INCRBY).key(var1);
      var2.entrySet().forEach((var1x) -> var3.add(var1x.getKey()).add(var1x.getValue()));
      return new CommandObject<List<Long>>(var3, BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> cmsQuery(String var1, String... var2) {
      return new CommandObject<List<Long>>(this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.QUERY).key(var1).addObjects(var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<String> cmsMerge(String var1, String... var2) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.MERGE).key(var1).add(var2.length).keys(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> cmsMerge(String var1, Map<String, Long> var2) {
      CommandArguments var3 = this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.MERGE).key(var1);
      var3.add(var2.size());
      var2.entrySet().forEach((var1x) -> var3.key(var1x.getKey()));
      var3.add(RedisBloomProtocol.RedisBloomKeyword.WEIGHTS);
      var2.entrySet().forEach((var1x) -> var3.add(var1x.getValue()));
      return new CommandObject<String>(var3, BuilderFactory.STRING);
   }

   public final CommandObject<Map<String, Object>> cmsInfo(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(RedisBloomProtocol.CountMinSketchCommand.INFO).key(var1), BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<String> topkReserve(String var1, long var2) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TopKCommand.RESERVE).key(var1).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> topkReserve(String var1, long var2, long var4, long var6, double var8) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TopKCommand.RESERVE).key(var1).add(var2).add(var4).add(var6).add(var8), BuilderFactory.STRING);
   }

   public final CommandObject<List<String>> topkAdd(String var1, String... var2) {
      return new CommandObject<List<String>>(this.commandArguments(RedisBloomProtocol.TopKCommand.ADD).key(var1).addObjects(var2), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<String>> topkIncrBy(String var1, Map<String, Long> var2) {
      CommandArguments var3 = this.commandArguments(RedisBloomProtocol.TopKCommand.INCRBY).key(var1);
      var2.entrySet().forEach((var1x) -> var3.add(var1x.getKey()).add(var1x.getValue()));
      return new CommandObject<List<String>>(var3, BuilderFactory.STRING_LIST);
   }

   public final CommandObject<List<Boolean>> topkQuery(String var1, String... var2) {
      return new CommandObject<List<Boolean>>(this.commandArguments(RedisBloomProtocol.TopKCommand.QUERY).key(var1).addObjects(var2), BuilderFactory.BOOLEAN_LIST);
   }

   public final CommandObject<List<String>> topkList(String var1) {
      return new CommandObject<List<String>>(this.commandArguments(RedisBloomProtocol.TopKCommand.LIST).key(var1), BuilderFactory.STRING_LIST);
   }

   public final CommandObject<Map<String, Long>> topkListWithCount(String var1) {
      return new CommandObject<Map<String, Long>>(this.commandArguments(RedisBloomProtocol.TopKCommand.LIST).key(var1).add(RedisBloomProtocol.RedisBloomKeyword.WITHCOUNT), BuilderFactory.STRING_LONG_MAP);
   }

   public final CommandObject<Map<String, Object>> topkInfo(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(RedisBloomProtocol.TopKCommand.INFO).key(var1), BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<String> tdigestCreate(String var1) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TDigestCommand.CREATE).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> tdigestCreate(String var1, int var2) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TDigestCommand.CREATE).key(var1).add(RedisBloomProtocol.RedisBloomKeyword.COMPRESSION).add(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> tdigestReset(String var1) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TDigestCommand.RESET).key(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> tdigestMerge(String var1, String... var2) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TDigestCommand.MERGE).key(var1).add(var2.length).keys(var2), BuilderFactory.STRING);
   }

   public final CommandObject<String> tdigestMerge(TDigestMergeParams var1, String var2, String... var3) {
      return new CommandObject<String>(this.commandArguments(RedisBloomProtocol.TDigestCommand.MERGE).key(var2).add(var3.length).keys(var3).addParams(var1), BuilderFactory.STRING);
   }

   public final CommandObject<Map<String, Object>> tdigestInfo(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(RedisBloomProtocol.TDigestCommand.INFO).key(var1), BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<String> tdigestAdd(String var1, double... var2) {
      return new CommandObject<String>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.ADD).key(var1), var2), BuilderFactory.STRING);
   }

   public final CommandObject<List<Double>> tdigestCDF(String var1, double... var2) {
      return new CommandObject<List<Double>>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.CDF).key(var1), var2), BuilderFactory.DOUBLE_LIST);
   }

   public final CommandObject<List<Double>> tdigestQuantile(String var1, double... var2) {
      return new CommandObject<List<Double>>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.QUANTILE).key(var1), var2), BuilderFactory.DOUBLE_LIST);
   }

   public final CommandObject<Double> tdigestMin(String var1) {
      return new CommandObject<Double>(this.commandArguments(RedisBloomProtocol.TDigestCommand.MIN).key(var1), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> tdigestMax(String var1) {
      return new CommandObject<Double>(this.commandArguments(RedisBloomProtocol.TDigestCommand.MAX).key(var1), BuilderFactory.DOUBLE);
   }

   public final CommandObject<Double> tdigestTrimmedMean(String var1, double var2, double var4) {
      return new CommandObject<Double>(this.commandArguments(RedisBloomProtocol.TDigestCommand.TRIMMED_MEAN).key(var1).add(var2).add(var4), BuilderFactory.DOUBLE);
   }

   public final CommandObject<List<Long>> tdigestRank(String var1, double... var2) {
      return new CommandObject<List<Long>>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.RANK).key(var1), var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Long>> tdigestRevRank(String var1, double... var2) {
      return new CommandObject<List<Long>>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.REVRANK).key(var1), var2), BuilderFactory.LONG_LIST);
   }

   public final CommandObject<List<Double>> tdigestByRank(String var1, long... var2) {
      return new CommandObject<List<Double>>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.BYRANK).key(var1), var2), BuilderFactory.DOUBLE_LIST);
   }

   public final CommandObject<List<Double>> tdigestByRevRank(String var1, long... var2) {
      return new CommandObject<List<Double>>(this.addFlatArgs(this.commandArguments(RedisBloomProtocol.TDigestCommand.BYREVRANK).key(var1), var2), BuilderFactory.DOUBLE_LIST);
   }

   @Deprecated
   public final CommandObject<List<String>> graphList() {
      return new CommandObject<List<String>>(this.commandArguments(GraphProtocol.GraphCommand.LIST), BuilderFactory.STRING_LIST);
   }

   @Deprecated
   public final CommandObject<List<String>> graphProfile(String var1, String var2) {
      return new CommandObject<List<String>>(this.commandArguments(GraphProtocol.GraphCommand.PROFILE).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   @Deprecated
   public final CommandObject<List<String>> graphExplain(String var1, String var2) {
      return new CommandObject<List<String>>(this.commandArguments(GraphProtocol.GraphCommand.EXPLAIN).key(var1).add(var2), BuilderFactory.STRING_LIST);
   }

   @Deprecated
   public final CommandObject<List<List<Object>>> graphSlowlog(String var1) {
      return new CommandObject<List<List<Object>>>(this.commandArguments(GraphProtocol.GraphCommand.SLOWLOG).key(var1), BuilderFactory.ENCODED_OBJECT_LIST_LIST);
   }

   @Deprecated
   public final CommandObject<String> graphConfigSet(String var1, Object var2) {
      return new CommandObject<String>(this.commandArguments(GraphProtocol.GraphCommand.CONFIG).add(GraphProtocol.GraphKeyword.SET).add(var1).add(var2), BuilderFactory.STRING);
   }

   @Deprecated
   public final CommandObject<Map<String, Object>> graphConfigGet(String var1) {
      return new CommandObject<Map<String, Object>>(this.commandArguments(GraphProtocol.GraphCommand.CONFIG).add(GraphProtocol.GraphKeyword.GET).add(var1), BuilderFactory.ENCODED_OBJECT_MAP);
   }

   public final CommandObject<String> tFunctionLoad(String var1, TFunctionLoadParams var2) {
      return new CommandObject<String>(this.commandArguments(RedisGearsProtocol.GearsCommand.TFUNCTION).add(RedisGearsProtocol.GearsKeyword.LOAD).addParams(var2).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<String> tFunctionDelete(String var1) {
      return new CommandObject<String>(this.commandArguments(RedisGearsProtocol.GearsCommand.TFUNCTION).add(RedisGearsProtocol.GearsKeyword.DELETE).add(var1), BuilderFactory.STRING);
   }

   public final CommandObject<List<GearsLibraryInfo>> tFunctionList(TFunctionListParams var1) {
      return new CommandObject<List<GearsLibraryInfo>>(this.commandArguments(RedisGearsProtocol.GearsCommand.TFUNCTION).add(RedisGearsProtocol.GearsKeyword.LIST).addParams(var1), GearsLibraryInfo.GEARS_LIBRARY_INFO_LIST);
   }

   public final CommandObject<Object> tFunctionCall(String var1, String var2, List<String> var3, List<String> var4) {
      return new CommandObject<Object>(this.commandArguments(RedisGearsProtocol.GearsCommand.TFCALL).add(var1 + "." + var2).add(var3.size()).keys((Collection)var3).addObjects((Collection)var4), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   public final CommandObject<Object> tFunctionCallAsync(String var1, String var2, List<String> var3, List<String> var4) {
      return new CommandObject<Object>(this.commandArguments(RedisGearsProtocol.GearsCommand.TFCALLASYNC).add(var1 + "." + var2).add(var3.size()).keys((Collection)var3).addObjects((Collection)var4), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT);
   }

   private JsonObjectMapper getJsonObjectMapper() {
      Object var1 = this.jsonObjectMapper;
      if (Objects.isNull(var1)) {
         synchronized(this) {
            var1 = this.jsonObjectMapper;
            if (Objects.isNull(var1)) {
               this.jsonObjectMapper = (JsonObjectMapper)(var1 = new DefaultGsonObjectMapper());
            }
         }
      }

      return (JsonObjectMapper)var1;
   }

   public void setJsonObjectMapper(JsonObjectMapper var1) {
      this.jsonObjectMapper = var1;
   }

   public void setDefaultSearchDialect(int var1) {
      if (var1 == 0) {
         throw new IllegalArgumentException("DIALECT=0 cannot be set.");
      } else {
         this.searchDialect.set(var1);
      }
   }

   private CommandArguments addFlatArgs(CommandArguments var1, long... var2) {
      for(long var6 : var2) {
         var1.add(var6);
      }

      return var1;
   }

   private CommandArguments addFlatArgs(CommandArguments var1, double... var2) {
      for(double var6 : var2) {
         var1.add(var6);
      }

      return var1;
   }

   private CommandArguments addFlatKeyValueArgs(CommandArguments var1, String... var2) {
      for(int var3 = 0; var3 < var2.length; var3 += 2) {
         var1.key(var2[var3]).add(var2[var3 + 1]);
      }

      return var1;
   }

   private CommandArguments addFlatKeyValueArgs(CommandArguments var1, byte[]... var2) {
      for(int var3 = 0; var3 < var2.length; var3 += 2) {
         var1.key(var2[var3]).add(var2[var3 + 1]);
      }

      return var1;
   }

   private CommandArguments addFlatMapArgs(CommandArguments var1, Map<?, ?> var2) {
      for(Map.Entry var4 : var2.entrySet()) {
         var1.add(var4.getKey());
         var1.add(var4.getValue());
      }

      return var1;
   }

   private CommandArguments addSortedSetFlatMapArgs(CommandArguments var1, Map<?, Double> var2) {
      for(Map.Entry var4 : var2.entrySet()) {
         var1.add(var4.getValue());
         var1.add(var4.getKey());
      }

      return var1;
   }

   private CommandArguments addGeoCoordinateFlatMapArgs(CommandArguments var1, Map<?, GeoCoordinate> var2) {
      for(Map.Entry var4 : var2.entrySet()) {
         GeoCoordinate var5 = (GeoCoordinate)var4.getValue();
         var1.add(var5.getLongitude());
         var1.add(var5.getLatitude());
         var1.add(var4.getKey());
      }

      return var1;
   }

   private class JsonObjectBuilder<T> extends Builder<T> {
      private final Class<T> clazz;

      public JsonObjectBuilder(Class<T> var2) {
         this.clazz = var2;
      }

      public T build(Object var1) {
         return (T)CommandObjects.this.getJsonObjectMapper().fromJson(BuilderFactory.STRING.build(var1), this.clazz);
      }
   }

   private class JsonObjectListBuilder<T> extends Builder<List<T>> {
      private final Class<T> clazz;

      public JsonObjectListBuilder(Class<T> var2) {
         this.clazz = var2;
      }

      public List<T> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = BuilderFactory.STRING_LIST.build(var1);
            return (List)var2.stream().map((var1x) -> CommandObjects.this.getJsonObjectMapper().fromJson(var1x, this.clazz)).collect(Collectors.toList());
         }
      }
   }

   private class SearchProfileResponseBuilder<T> extends Builder<Map.Entry<T, Map<String, Object>>> {
      private static final String PROFILE_STR = "profile";
      private final Builder<T> replyBuilder;

      public SearchProfileResponseBuilder(Builder<T> var2) {
         this.replyBuilder = var2;
      }

      public Map.Entry<T, Map<String, Object>> build(Object var1) {
         List var2 = (List)var1;
         if (var2 != null && !var2.isEmpty()) {
            if (var2.get(0) instanceof KeyValue) {
               for(KeyValue var4 : (List)var1) {
                  if ("profile".equals(BuilderFactory.STRING.build(var4.getKey()))) {
                     return KeyValue.<T, Map<String, Object>>of(this.replyBuilder.build(var1), BuilderFactory.AGGRESSIVE_ENCODED_OBJECT_MAP.build(var4.getValue()));
                  }
               }
            }

            return KeyValue.<T, Map<String, Object>>of(this.replyBuilder.build(var2.get(0)), SearchBuilderFactory.SEARCH_PROFILE_PROFILE.build(var2.get(1)));
         } else {
            return null;
         }
      }
   }
}
