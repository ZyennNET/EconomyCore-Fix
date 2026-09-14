package redis.clients.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
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
import redis.clients.jedis.bloom.TDigestMergeParams;
import redis.clients.jedis.commands.PipelineBinaryCommands;
import redis.clients.jedis.commands.PipelineCommands;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.commands.RedisModulePipelineCommands;
import redis.clients.jedis.graph.GraphCommandObjects;
import redis.clients.jedis.graph.ResultSet;
import redis.clients.jedis.json.JsonObjectMapper;
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
import redis.clients.jedis.search.FTSearchParams;
import redis.clients.jedis.search.FTSpellCheckParams;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.Schema;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.timeseries.AggregationType;
import redis.clients.jedis.timeseries.TSAlterParams;
import redis.clients.jedis.timeseries.TSCreateParams;
import redis.clients.jedis.timeseries.TSElement;
import redis.clients.jedis.timeseries.TSGetParams;
import redis.clients.jedis.timeseries.TSMGetElement;
import redis.clients.jedis.timeseries.TSMGetParams;
import redis.clients.jedis.timeseries.TSMRangeElements;
import redis.clients.jedis.timeseries.TSMRangeParams;
import redis.clients.jedis.timeseries.TSRangeParams;
import redis.clients.jedis.util.KeyValue;

public abstract class PipeliningBase implements PipelineCommands, PipelineBinaryCommands, RedisModulePipelineCommands {
   protected final CommandObjects commandObjects;
   private GraphCommandObjects graphCommandObjects;

   protected PipeliningBase(CommandObjects var1) {
      this.commandObjects = var1;
   }

   protected final void setGraphCommands(GraphCommandObjects var1) {
      this.graphCommandObjects = var1;
   }

   protected abstract <T> Response<T> appendCommand(CommandObject<T> var1);

   public Response<Boolean> exists(String var1) {
      return this.<Boolean>appendCommand(this.commandObjects.exists(var1));
   }

   public Response<Long> exists(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.exists(var1));
   }

   public Response<Long> persist(String var1) {
      return this.<Long>appendCommand(this.commandObjects.persist(var1));
   }

   public Response<String> type(String var1) {
      return this.<String>appendCommand(this.commandObjects.type(var1));
   }

   public Response<byte[]> dump(String var1) {
      return this.<byte[]>appendCommand(this.commandObjects.dump(var1));
   }

   public Response<String> restore(String var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.restore(var1, var2, var4));
   }

   public Response<String> restore(String var1, long var2, byte[] var4, RestoreParams var5) {
      return this.<String>appendCommand(this.commandObjects.restore(var1, var2, var4, var5));
   }

   public Response<Long> expire(String var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.expire(var1, var2));
   }

   public Response<Long> expire(String var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.expire(var1, var2, var4));
   }

   public Response<Long> pexpire(String var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.pexpire(var1, var2));
   }

   public Response<Long> pexpire(String var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.pexpire(var1, var2, var4));
   }

   public Response<Long> expireTime(String var1) {
      return this.<Long>appendCommand(this.commandObjects.expireTime(var1));
   }

   public Response<Long> pexpireTime(String var1) {
      return this.<Long>appendCommand(this.commandObjects.pexpireTime(var1));
   }

   public Response<Long> expireAt(String var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.expireAt(var1, var2));
   }

   public Response<Long> expireAt(String var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.expireAt(var1, var2, var4));
   }

   public Response<Long> pexpireAt(String var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.pexpireAt(var1, var2));
   }

   public Response<Long> pexpireAt(String var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.pexpireAt(var1, var2, var4));
   }

   public Response<Long> ttl(String var1) {
      return this.<Long>appendCommand(this.commandObjects.ttl(var1));
   }

   public Response<Long> pttl(String var1) {
      return this.<Long>appendCommand(this.commandObjects.pttl(var1));
   }

   public Response<Long> touch(String var1) {
      return this.<Long>appendCommand(this.commandObjects.touch(var1));
   }

   public Response<Long> touch(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.touch(var1));
   }

   public Response<List<String>> sort(String var1) {
      return this.<List<String>>appendCommand(this.commandObjects.sort(var1));
   }

   public Response<Long> sort(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.sort(var1, var2));
   }

   public Response<List<String>> sort(String var1, SortingParams var2) {
      return this.<List<String>>appendCommand(this.commandObjects.sort(var1, var2));
   }

   public Response<Long> sort(String var1, SortingParams var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.sort(var1, var2, var3));
   }

   public Response<List<String>> sortReadonly(String var1, SortingParams var2) {
      return this.<List<String>>appendCommand(this.commandObjects.sortReadonly(var1, var2));
   }

   public Response<Long> del(String var1) {
      return this.<Long>appendCommand(this.commandObjects.del(var1));
   }

   public Response<Long> del(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.del(var1));
   }

   public Response<Long> unlink(String var1) {
      return this.<Long>appendCommand(this.commandObjects.unlink(var1));
   }

   public Response<Long> unlink(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.unlink(var1));
   }

   public Response<Boolean> copy(String var1, String var2, boolean var3) {
      return this.<Boolean>appendCommand(this.commandObjects.copy(var1, var2, var3));
   }

   public Response<String> rename(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.rename(var1, var2));
   }

   public Response<Long> renamenx(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.renamenx(var1, var2));
   }

   public Response<Long> memoryUsage(String var1) {
      return this.<Long>appendCommand(this.commandObjects.memoryUsage(var1));
   }

   public Response<Long> memoryUsage(String var1, int var2) {
      return this.<Long>appendCommand(this.commandObjects.memoryUsage(var1, var2));
   }

   public Response<Long> objectRefcount(String var1) {
      return this.<Long>appendCommand(this.commandObjects.objectRefcount(var1));
   }

   public Response<String> objectEncoding(String var1) {
      return this.<String>appendCommand(this.commandObjects.objectEncoding(var1));
   }

   public Response<Long> objectIdletime(String var1) {
      return this.<Long>appendCommand(this.commandObjects.objectIdletime(var1));
   }

   public Response<Long> objectFreq(String var1) {
      return this.<Long>appendCommand(this.commandObjects.objectFreq(var1));
   }

   public Response<String> migrate(String var1, int var2, String var3, int var4) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4));
   }

   public Response<String> migrate(String var1, int var2, int var3, MigrateParams var4, String... var5) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public Response<Set<String>> keys(String var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.keys(var1));
   }

   public Response<ScanResult<String>> scan(String var1) {
      return this.<ScanResult<String>>appendCommand(this.commandObjects.scan(var1));
   }

   public Response<ScanResult<String>> scan(String var1, ScanParams var2) {
      return this.<ScanResult<String>>appendCommand(this.commandObjects.scan(var1, var2));
   }

   public Response<ScanResult<String>> scan(String var1, ScanParams var2, String var3) {
      return this.<ScanResult<String>>appendCommand(this.commandObjects.scan(var1, var2, var3));
   }

   public Response<String> randomKey() {
      return this.<String>appendCommand(this.commandObjects.randomKey());
   }

   public Response<String> get(String var1) {
      return this.<String>appendCommand(this.commandObjects.get(var1));
   }

   public Response<String> setGet(String var1, String var2, SetParams var3) {
      return this.<String>appendCommand(this.commandObjects.setGet(var1, var2, var3));
   }

   public Response<String> getDel(String var1) {
      return this.<String>appendCommand(this.commandObjects.getDel(var1));
   }

   public Response<String> getEx(String var1, GetExParams var2) {
      return this.<String>appendCommand(this.commandObjects.getEx(var1, var2));
   }

   public Response<Boolean> setbit(String var1, long var2, boolean var4) {
      return this.<Boolean>appendCommand(this.commandObjects.setbit(var1, var2, var4));
   }

   public Response<Boolean> getbit(String var1, long var2) {
      return this.<Boolean>appendCommand(this.commandObjects.getbit(var1, var2));
   }

   public Response<Long> setrange(String var1, long var2, String var4) {
      return this.<Long>appendCommand(this.commandObjects.setrange(var1, var2, var4));
   }

   public Response<String> getrange(String var1, long var2, long var4) {
      return this.<String>appendCommand(this.commandObjects.getrange(var1, var2, var4));
   }

   public Response<String> getSet(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.getSet(var1, var2));
   }

   public Response<Long> setnx(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.setnx(var1, var2));
   }

   public Response<String> setex(String var1, long var2, String var4) {
      return this.<String>appendCommand(this.commandObjects.setex(var1, var2, var4));
   }

   public Response<String> psetex(String var1, long var2, String var4) {
      return this.<String>appendCommand(this.commandObjects.psetex(var1, var2, var4));
   }

   public Response<List<String>> mget(String... var1) {
      return this.<List<String>>appendCommand(this.commandObjects.mget(var1));
   }

   public Response<String> mset(String... var1) {
      return this.<String>appendCommand(this.commandObjects.mset(var1));
   }

   public Response<Long> msetnx(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.msetnx(var1));
   }

   public Response<Long> incr(String var1) {
      return this.<Long>appendCommand(this.commandObjects.incr(var1));
   }

   public Response<Long> incrBy(String var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.incrBy(var1, var2));
   }

   public Response<Double> incrByFloat(String var1, double var2) {
      return this.<Double>appendCommand(this.commandObjects.incrByFloat(var1, var2));
   }

   public Response<Long> decr(String var1) {
      return this.<Long>appendCommand(this.commandObjects.decr(var1));
   }

   public Response<Long> decrBy(String var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.decrBy(var1, var2));
   }

   public Response<Long> append(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.append(var1, var2));
   }

   public Response<String> substr(String var1, int var2, int var3) {
      return this.<String>appendCommand(this.commandObjects.substr(var1, var2, var3));
   }

   public Response<Long> strlen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.strlen(var1));
   }

   public Response<Long> bitcount(String var1) {
      return this.<Long>appendCommand(this.commandObjects.bitcount(var1));
   }

   public Response<Long> bitcount(String var1, long var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.bitcount(var1, var2, var4));
   }

   public Response<Long> bitcount(String var1, long var2, long var4, BitCountOption var6) {
      return this.<Long>appendCommand(this.commandObjects.bitcount(var1, var2, var4, var6));
   }

   public Response<Long> bitpos(String var1, boolean var2) {
      return this.<Long>appendCommand(this.commandObjects.bitpos(var1, var2));
   }

   public Response<Long> bitpos(String var1, boolean var2, BitPosParams var3) {
      return this.<Long>appendCommand(this.commandObjects.bitpos(var1, var2, var3));
   }

   public Response<List<Long>> bitfield(String var1, String... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.bitfield(var1, var2));
   }

   public Response<List<Long>> bitfieldReadonly(String var1, String... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.bitfieldReadonly(var1, var2));
   }

   public Response<Long> bitop(BitOP var1, String var2, String... var3) {
      return this.<Long>appendCommand(this.commandObjects.bitop(var1, var2, var3));
   }

   public Response<LCSMatchResult> lcs(String var1, String var2, LCSParams var3) {
      return this.<LCSMatchResult>appendCommand(this.commandObjects.lcs(var1, var2, var3));
   }

   public Response<String> set(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.set(var1, var2));
   }

   public Response<String> set(String var1, String var2, SetParams var3) {
      return this.<String>appendCommand(this.commandObjects.set(var1, var2, var3));
   }

   public Response<Long> rpush(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.rpush(var1, var2));
   }

   public Response<Long> lpush(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.lpush(var1, var2));
   }

   public Response<Long> llen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.llen(var1));
   }

   public Response<List<String>> lrange(String var1, long var2, long var4) {
      return this.<List<String>>appendCommand(this.commandObjects.lrange(var1, var2, var4));
   }

   public Response<String> ltrim(String var1, long var2, long var4) {
      return this.<String>appendCommand(this.commandObjects.ltrim(var1, var2, var4));
   }

   public Response<String> lindex(String var1, long var2) {
      return this.<String>appendCommand(this.commandObjects.lindex(var1, var2));
   }

   public Response<String> lset(String var1, long var2, String var4) {
      return this.<String>appendCommand(this.commandObjects.lset(var1, var2, var4));
   }

   public Response<Long> lrem(String var1, long var2, String var4) {
      return this.<Long>appendCommand(this.commandObjects.lrem(var1, var2, var4));
   }

   public Response<String> lpop(String var1) {
      return this.<String>appendCommand(this.commandObjects.lpop(var1));
   }

   public Response<List<String>> lpop(String var1, int var2) {
      return this.<List<String>>appendCommand(this.commandObjects.lpop(var1, var2));
   }

   public Response<Long> lpos(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.lpos(var1, var2));
   }

   public Response<Long> lpos(String var1, String var2, LPosParams var3) {
      return this.<Long>appendCommand(this.commandObjects.lpos(var1, var2, var3));
   }

   public Response<List<Long>> lpos(String var1, String var2, LPosParams var3, long var4) {
      return this.<List<Long>>appendCommand(this.commandObjects.lpos(var1, var2, var3, var4));
   }

   public Response<String> rpop(String var1) {
      return this.<String>appendCommand(this.commandObjects.rpop(var1));
   }

   public Response<List<String>> rpop(String var1, int var2) {
      return this.<List<String>>appendCommand(this.commandObjects.rpop(var1, var2));
   }

   public Response<Long> linsert(String var1, ListPosition var2, String var3, String var4) {
      return this.<Long>appendCommand(this.commandObjects.linsert(var1, var2, var3, var4));
   }

   public Response<Long> lpushx(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.lpushx(var1, var2));
   }

   public Response<Long> rpushx(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.rpushx(var1, var2));
   }

   public Response<List<String>> blpop(int var1, String var2) {
      return this.<List<String>>appendCommand(this.commandObjects.blpop(var1, var2));
   }

   public Response<KeyValue<String, String>> blpop(double var1, String var3) {
      return this.<KeyValue<String, String>>appendCommand(this.commandObjects.blpop(var1, var3));
   }

   public Response<List<String>> brpop(int var1, String var2) {
      return this.<List<String>>appendCommand(this.commandObjects.brpop(var1, var2));
   }

   public Response<KeyValue<String, String>> brpop(double var1, String var3) {
      return this.<KeyValue<String, String>>appendCommand(this.commandObjects.brpop(var1, var3));
   }

   public Response<List<String>> blpop(int var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.blpop(var1, var2));
   }

   public Response<KeyValue<String, String>> blpop(double var1, String... var3) {
      return this.<KeyValue<String, String>>appendCommand(this.commandObjects.blpop(var1, var3));
   }

   public Response<List<String>> brpop(int var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.brpop(var1, var2));
   }

   public Response<KeyValue<String, String>> brpop(double var1, String... var3) {
      return this.<KeyValue<String, String>>appendCommand(this.commandObjects.brpop(var1, var3));
   }

   public Response<String> rpoplpush(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.rpoplpush(var1, var2));
   }

   public Response<String> brpoplpush(String var1, String var2, int var3) {
      return this.<String>appendCommand(this.commandObjects.brpoplpush(var1, var2, var3));
   }

   public Response<String> lmove(String var1, String var2, ListDirection var3, ListDirection var4) {
      return this.<String>appendCommand(this.commandObjects.lmove(var1, var2, var3, var4));
   }

   public Response<String> blmove(String var1, String var2, ListDirection var3, ListDirection var4, double var5) {
      return this.<String>appendCommand(this.commandObjects.blmove(var1, var2, var3, var4, var5));
   }

   public Response<KeyValue<String, List<String>>> lmpop(ListDirection var1, String... var2) {
      return this.<KeyValue<String, List<String>>>appendCommand(this.commandObjects.lmpop(var1, var2));
   }

   public Response<KeyValue<String, List<String>>> lmpop(ListDirection var1, int var2, String... var3) {
      return this.<KeyValue<String, List<String>>>appendCommand(this.commandObjects.lmpop(var1, var2, var3));
   }

   public Response<KeyValue<String, List<String>>> blmpop(double var1, ListDirection var3, String... var4) {
      return this.<KeyValue<String, List<String>>>appendCommand(this.commandObjects.blmpop(var1, var3, var4));
   }

   public Response<KeyValue<String, List<String>>> blmpop(double var1, ListDirection var3, int var4, String... var5) {
      return this.<KeyValue<String, List<String>>>appendCommand(this.commandObjects.blmpop(var1, var3, var4, var5));
   }

   public Response<Long> hset(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.hset(var1, var2, var3));
   }

   public Response<Long> hset(String var1, Map<String, String> var2) {
      return this.<Long>appendCommand(this.commandObjects.hset(var1, var2));
   }

   public Response<String> hget(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.hget(var1, var2));
   }

   public Response<Long> hsetnx(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.hsetnx(var1, var2, var3));
   }

   public Response<String> hmset(String var1, Map<String, String> var2) {
      return this.<String>appendCommand(this.commandObjects.hmset(var1, var2));
   }

   public Response<List<String>> hmget(String var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.hmget(var1, var2));
   }

   public Response<Long> hincrBy(String var1, String var2, long var3) {
      return this.<Long>appendCommand(this.commandObjects.hincrBy(var1, var2, var3));
   }

   public Response<Double> hincrByFloat(String var1, String var2, double var3) {
      return this.<Double>appendCommand(this.commandObjects.hincrByFloat(var1, var2, var3));
   }

   public Response<Boolean> hexists(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.hexists(var1, var2));
   }

   public Response<Long> hdel(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.hdel(var1, var2));
   }

   public Response<Long> hlen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.hlen(var1));
   }

   public Response<Set<String>> hkeys(String var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.hkeys(var1));
   }

   public Response<List<String>> hvals(String var1) {
      return this.<List<String>>appendCommand(this.commandObjects.hvals(var1));
   }

   public Response<Map<String, String>> hgetAll(String var1) {
      return this.<Map<String, String>>appendCommand(this.commandObjects.hgetAll(var1));
   }

   public Response<String> hrandfield(String var1) {
      return this.<String>appendCommand(this.commandObjects.hrandfield(var1));
   }

   public Response<List<String>> hrandfield(String var1, long var2) {
      return this.<List<String>>appendCommand(this.commandObjects.hrandfield(var1, var2));
   }

   public Response<List<Map.Entry<String, String>>> hrandfieldWithValues(String var1, long var2) {
      return this.<List<Map.Entry<String, String>>>appendCommand(this.commandObjects.hrandfieldWithValues(var1, var2));
   }

   public Response<ScanResult<Map.Entry<String, String>>> hscan(String var1, String var2, ScanParams var3) {
      return this.<ScanResult<Map.Entry<String, String>>>appendCommand(this.commandObjects.hscan(var1, var2, var3));
   }

   public Response<Long> hstrlen(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.hstrlen(var1, var2));
   }

   public Response<Long> sadd(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.sadd(var1, var2));
   }

   public Response<Set<String>> smembers(String var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.smembers(var1));
   }

   public Response<Long> srem(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.srem(var1, var2));
   }

   public Response<String> spop(String var1) {
      return this.<String>appendCommand(this.commandObjects.spop(var1));
   }

   public Response<Set<String>> spop(String var1, long var2) {
      return this.<Set<String>>appendCommand(this.commandObjects.spop(var1, var2));
   }

   public Response<Long> scard(String var1) {
      return this.<Long>appendCommand(this.commandObjects.scard(var1));
   }

   public Response<Boolean> sismember(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.sismember(var1, var2));
   }

   public Response<List<Boolean>> smismember(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.smismember(var1, var2));
   }

   public Response<String> srandmember(String var1) {
      return this.<String>appendCommand(this.commandObjects.srandmember(var1));
   }

   public Response<List<String>> srandmember(String var1, int var2) {
      return this.<List<String>>appendCommand(this.commandObjects.srandmember(var1, var2));
   }

   public Response<ScanResult<String>> sscan(String var1, String var2, ScanParams var3) {
      return this.<ScanResult<String>>appendCommand(this.commandObjects.sscan(var1, var2, var3));
   }

   public Response<Set<String>> sdiff(String... var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.sdiff(var1));
   }

   public Response<Long> sdiffStore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.sdiffstore(var1, var2));
   }

   public Response<Set<String>> sinter(String... var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.sinter(var1));
   }

   public Response<Long> sinterstore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.sinterstore(var1, var2));
   }

   public Response<Long> sintercard(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.sintercard(var1));
   }

   public Response<Long> sintercard(int var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.sintercard(var1, var2));
   }

   public Response<Set<String>> sunion(String... var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.sunion(var1));
   }

   public Response<Long> sunionstore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.sunionstore(var1, var2));
   }

   public Response<Long> smove(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.smove(var1, var2, var3));
   }

   public Response<Long> zadd(String var1, double var2, String var4) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2, var4));
   }

   public Response<Long> zadd(String var1, double var2, String var4, ZAddParams var5) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2, var4, var5));
   }

   public Response<Long> zadd(String var1, Map<String, Double> var2) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2));
   }

   public Response<Long> zadd(String var1, Map<String, Double> var2, ZAddParams var3) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2, var3));
   }

   public Response<Double> zaddIncr(String var1, double var2, String var4, ZAddParams var5) {
      return this.<Double>appendCommand(this.commandObjects.zaddIncr(var1, var2, var4, var5));
   }

   public Response<Long> zrem(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.zrem(var1, var2));
   }

   public Response<Double> zincrby(String var1, double var2, String var4) {
      return this.<Double>appendCommand(this.commandObjects.zincrby(var1, var2, var4));
   }

   public Response<Double> zincrby(String var1, double var2, String var4, ZIncrByParams var5) {
      return this.<Double>appendCommand(this.commandObjects.zincrby(var1, var2, var4, var5));
   }

   public Response<Long> zrank(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.zrank(var1, var2));
   }

   public Response<Long> zrevrank(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.zrevrank(var1, var2));
   }

   public Response<KeyValue<Long, Double>> zrankWithScore(String var1, String var2) {
      return this.<KeyValue<Long, Double>>appendCommand(this.commandObjects.zrankWithScore(var1, var2));
   }

   public Response<KeyValue<Long, Double>> zrevrankWithScore(String var1, String var2) {
      return this.<KeyValue<Long, Double>>appendCommand(this.commandObjects.zrevrankWithScore(var1, var2));
   }

   public Response<List<String>> zrange(String var1, long var2, long var4) {
      return this.<List<String>>appendCommand(this.commandObjects.zrange(var1, var2, var4));
   }

   public Response<List<String>> zrevrange(String var1, long var2, long var4) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrange(var1, var2, var4));
   }

   public Response<List<Tuple>> zrangeWithScores(String var1, long var2, long var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeWithScores(var1, var2, var4));
   }

   public Response<List<Tuple>> zrevrangeWithScores(String var1, long var2, long var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeWithScores(var1, var2, var4));
   }

   public Response<String> zrandmember(String var1) {
      return this.<String>appendCommand(this.commandObjects.zrandmember(var1));
   }

   public Response<List<String>> zrandmember(String var1, long var2) {
      return this.<List<String>>appendCommand(this.commandObjects.zrandmember(var1, var2));
   }

   public Response<List<Tuple>> zrandmemberWithScores(String var1, long var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrandmemberWithScores(var1, var2));
   }

   public Response<Long> zcard(String var1) {
      return this.<Long>appendCommand(this.commandObjects.zcard(var1));
   }

   public Response<Double> zscore(String var1, String var2) {
      return this.<Double>appendCommand(this.commandObjects.zscore(var1, var2));
   }

   public Response<List<Double>> zmscore(String var1, String... var2) {
      return this.<List<Double>>appendCommand(this.commandObjects.zmscore(var1, var2));
   }

   public Response<Tuple> zpopmax(String var1) {
      return this.<Tuple>appendCommand(this.commandObjects.zpopmax(var1));
   }

   public Response<List<Tuple>> zpopmax(String var1, int var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zpopmax(var1, var2));
   }

   public Response<Tuple> zpopmin(String var1) {
      return this.<Tuple>appendCommand(this.commandObjects.zpopmin(var1));
   }

   public Response<List<Tuple>> zpopmin(String var1, int var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zpopmin(var1, var2));
   }

   public Response<Long> zcount(String var1, double var2, double var4) {
      return this.<Long>appendCommand(this.commandObjects.zcount(var1, var2, var4));
   }

   public Response<Long> zcount(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.zcount(var1, var2, var3));
   }

   public Response<List<String>> zrangeByScore(String var1, double var2, double var4) {
      return this.<List<String>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var4));
   }

   public Response<List<String>> zrangeByScore(String var1, String var2, String var3) {
      return this.<List<String>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var3));
   }

   public Response<List<String>> zrevrangeByScore(String var1, double var2, double var4) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4));
   }

   public Response<List<String>> zrangeByScore(String var1, double var2, double var4, int var6, int var7) {
      return this.<List<String>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var4, var6, var7));
   }

   public Response<List<String>> zrevrangeByScore(String var1, String var2, String var3) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3));
   }

   public Response<List<String>> zrangeByScore(String var1, String var2, String var3, int var4, int var5) {
      return this.<List<String>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var3, var4, var5));
   }

   public Response<List<String>> zrevrangeByScore(String var1, double var2, double var4, int var6, int var7) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4, var6, var7));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(String var1, double var2, double var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(String var1, double var2, double var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public Response<List<String>> zrevrangeByScore(String var1, String var2, String var3, int var4, int var5) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3, var4, var5));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(String var1, String var2, String var3) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(String var1, String var2, String var3) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public Response<List<String>> zrange(String var1, ZRangeParams var2) {
      return this.<List<String>>appendCommand(this.commandObjects.zrange(var1, var2));
   }

   public Response<List<Tuple>> zrangeWithScores(String var1, ZRangeParams var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeWithScores(var1, var2));
   }

   public Response<Long> zrangestore(String var1, String var2, ZRangeParams var3) {
      return this.<Long>appendCommand(this.commandObjects.zrangestore(var1, var2, var3));
   }

   public Response<Long> zremrangeByRank(String var1, long var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByRank(var1, var2, var4));
   }

   public Response<Long> zremrangeByScore(String var1, double var2, double var4) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByScore(var1, var2, var4));
   }

   public Response<Long> zremrangeByScore(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByScore(var1, var2, var3));
   }

   public Response<Long> zlexcount(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.zlexcount(var1, var2, var3));
   }

   public Response<List<String>> zrangeByLex(String var1, String var2, String var3) {
      return this.<List<String>>appendCommand(this.commandObjects.zrangeByLex(var1, var2, var3));
   }

   public Response<List<String>> zrangeByLex(String var1, String var2, String var3, int var4, int var5) {
      return this.<List<String>>appendCommand(this.commandObjects.zrangeByLex(var1, var2, var3, var4, var5));
   }

   public Response<List<String>> zrevrangeByLex(String var1, String var2, String var3) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3));
   }

   public Response<List<String>> zrevrangeByLex(String var1, String var2, String var3, int var4, int var5) {
      return this.<List<String>>appendCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3, var4, var5));
   }

   public Response<Long> zremrangeByLex(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByLex(var1, var2, var3));
   }

   public Response<ScanResult<Tuple>> zscan(String var1, String var2, ScanParams var3) {
      return this.<ScanResult<Tuple>>appendCommand(this.commandObjects.zscan(var1, var2, var3));
   }

   public Response<KeyValue<String, Tuple>> bzpopmax(double var1, String... var3) {
      return this.<KeyValue<String, Tuple>>appendCommand(this.commandObjects.bzpopmax(var1, var3));
   }

   public Response<KeyValue<String, Tuple>> bzpopmin(double var1, String... var3) {
      return this.<KeyValue<String, Tuple>>appendCommand(this.commandObjects.bzpopmin(var1, var3));
   }

   public Response<KeyValue<String, List<Tuple>>> zmpop(SortedSetOption var1, String... var2) {
      return this.<KeyValue<String, List<Tuple>>>appendCommand(this.commandObjects.zmpop(var1, var2));
   }

   public Response<KeyValue<String, List<Tuple>>> zmpop(SortedSetOption var1, int var2, String... var3) {
      return this.<KeyValue<String, List<Tuple>>>appendCommand(this.commandObjects.zmpop(var1, var2, var3));
   }

   public Response<KeyValue<String, List<Tuple>>> bzmpop(double var1, SortedSetOption var3, String... var4) {
      return this.<KeyValue<String, List<Tuple>>>appendCommand(this.commandObjects.bzmpop(var1, var3, var4));
   }

   public Response<KeyValue<String, List<Tuple>>> bzmpop(double var1, SortedSetOption var3, int var4, String... var5) {
      return this.<KeyValue<String, List<Tuple>>>appendCommand(this.commandObjects.bzmpop(var1, var3, var4, var5));
   }

   public Response<List<String>> zdiff(String... var1) {
      return this.<List<String>>appendCommand(this.commandObjects.zdiff(var1));
   }

   public Response<List<Tuple>> zdiffWithScores(String... var1) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zdiffWithScores(var1));
   }

   @Deprecated
   public Response<Long> zdiffStore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.zdiffStore(var1, var2));
   }

   public Response<Long> zdiffstore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.zdiffstore(var1, var2));
   }

   public Response<Long> zinterstore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.zinterstore(var1, var2));
   }

   public Response<Long> zinterstore(String var1, ZParams var2, String... var3) {
      return this.<Long>appendCommand(this.commandObjects.zinterstore(var1, var2, var3));
   }

   public Response<List<String>> zinter(ZParams var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.zinter(var1, var2));
   }

   public Response<List<Tuple>> zinterWithScores(ZParams var1, String... var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zinterWithScores(var1, var2));
   }

   public Response<Long> zintercard(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.zintercard(var1));
   }

   public Response<Long> zintercard(long var1, String... var3) {
      return this.<Long>appendCommand(this.commandObjects.zintercard(var1, var3));
   }

   public Response<List<String>> zunion(ZParams var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.zunion(var1, var2));
   }

   public Response<List<Tuple>> zunionWithScores(ZParams var1, String... var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zunionWithScores(var1, var2));
   }

   public Response<Long> zunionstore(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.zunionstore(var1, var2));
   }

   public Response<Long> zunionstore(String var1, ZParams var2, String... var3) {
      return this.<Long>appendCommand(this.commandObjects.zunionstore(var1, var2, var3));
   }

   public Response<Long> geoadd(String var1, double var2, double var4, String var6) {
      return this.<Long>appendCommand(this.commandObjects.geoadd(var1, var2, var4, var6));
   }

   public Response<Long> geoadd(String var1, Map<String, GeoCoordinate> var2) {
      return this.<Long>appendCommand(this.commandObjects.geoadd(var1, var2));
   }

   public Response<Long> geoadd(String var1, GeoAddParams var2, Map<String, GeoCoordinate> var3) {
      return this.<Long>appendCommand(this.commandObjects.geoadd(var1, var2, var3));
   }

   public Response<Double> geodist(String var1, String var2, String var3) {
      return this.<Double>appendCommand(this.commandObjects.geodist(var1, var2, var3));
   }

   public Response<Double> geodist(String var1, String var2, String var3, GeoUnit var4) {
      return this.<Double>appendCommand(this.commandObjects.geodist(var1, var2, var3, var4));
   }

   public Response<List<String>> geohash(String var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.geohash(var1, var2));
   }

   public Response<List<GeoCoordinate>> geopos(String var1, String... var2) {
      return this.<List<GeoCoordinate>>appendCommand(this.commandObjects.geopos(var1, var2));
   }

   public Response<List<GeoRadiusResponse>> georadius(String var1, double var2, double var4, double var6, GeoUnit var8) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8));
   }

   public Response<List<GeoRadiusResponse>> georadiusReadonly(String var1, double var2, double var4, double var6, GeoUnit var8) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8));
   }

   public Response<List<GeoRadiusResponse>> georadius(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8, var9));
   }

   public Response<List<GeoRadiusResponse>> georadiusReadonly(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8, var9));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMember(String var1, String var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMemberReadonly(String var1, String var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMember(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5, var6));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMemberReadonly(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5, var6));
   }

   public Response<Long> georadiusStore(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9, GeoRadiusStoreParam var10) {
      return this.<Long>appendCommand(this.commandObjects.georadiusStore(var1, var2, var4, var6, var8, var9, var10));
   }

   public Response<Long> georadiusByMemberStore(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6, GeoRadiusStoreParam var7) {
      return this.<Long>appendCommand(this.commandObjects.georadiusByMemberStore(var1, var2, var3, var5, var6, var7));
   }

   public Response<List<GeoRadiusResponse>> geosearch(String var1, String var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> geosearch(String var1, GeoCoordinate var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> geosearch(String var1, String var2, double var3, double var5, GeoUnit var7) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public Response<List<GeoRadiusResponse>> geosearch(String var1, GeoCoordinate var2, double var3, double var5, GeoUnit var7) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public Response<List<GeoRadiusResponse>> geosearch(String var1, GeoSearchParam var2) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2));
   }

   public Response<Long> geosearchStore(String var1, String var2, String var3, double var4, GeoUnit var6) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public Response<Long> geosearchStore(String var1, String var2, GeoCoordinate var3, double var4, GeoUnit var6) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public Response<Long> geosearchStore(String var1, String var2, String var3, double var4, double var6, GeoUnit var8) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public Response<Long> geosearchStore(String var1, String var2, GeoCoordinate var3, double var4, double var6, GeoUnit var8) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public Response<Long> geosearchStore(String var1, String var2, GeoSearchParam var3) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3));
   }

   public Response<Long> geosearchStoreStoreDist(String var1, String var2, GeoSearchParam var3) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStoreStoreDist(var1, var2, var3));
   }

   public Response<Long> pfadd(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.pfadd(var1, var2));
   }

   public Response<String> pfmerge(String var1, String... var2) {
      return this.<String>appendCommand(this.commandObjects.pfmerge(var1, var2));
   }

   public Response<Long> pfcount(String var1) {
      return this.<Long>appendCommand(this.commandObjects.pfcount(var1));
   }

   public Response<Long> pfcount(String... var1) {
      return this.<Long>appendCommand(this.commandObjects.pfcount(var1));
   }

   public Response<StreamEntryID> xadd(String var1, StreamEntryID var2, Map<String, String> var3) {
      return this.<StreamEntryID>appendCommand(this.commandObjects.xadd(var1, var2, var3));
   }

   public Response<StreamEntryID> xadd(String var1, XAddParams var2, Map<String, String> var3) {
      return this.<StreamEntryID>appendCommand(this.commandObjects.xadd(var1, var2, var3));
   }

   public Response<Long> xlen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.xlen(var1));
   }

   public Response<List<StreamEntry>> xrange(String var1, StreamEntryID var2, StreamEntryID var3) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrange(var1, var2, var3));
   }

   public Response<List<StreamEntry>> xrange(String var1, StreamEntryID var2, StreamEntryID var3, int var4) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrange(var1, var2, var3, var4));
   }

   public Response<List<StreamEntry>> xrevrange(String var1, StreamEntryID var2, StreamEntryID var3) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrevrange(var1, var2, var3));
   }

   public Response<List<StreamEntry>> xrevrange(String var1, StreamEntryID var2, StreamEntryID var3, int var4) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrevrange(var1, var2, var3, var4));
   }

   public Response<List<StreamEntry>> xrange(String var1, String var2, String var3) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrange(var1, var2, var3));
   }

   public Response<List<StreamEntry>> xrange(String var1, String var2, String var3, int var4) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrange(var1, var2, var3, var4));
   }

   public Response<List<StreamEntry>> xrevrange(String var1, String var2, String var3) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrevrange(var1, var2, var3));
   }

   public Response<List<StreamEntry>> xrevrange(String var1, String var2, String var3, int var4) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xrevrange(var1, var2, var3, var4));
   }

   public Response<Long> xack(String var1, String var2, StreamEntryID... var3) {
      return this.<Long>appendCommand(this.commandObjects.xack(var1, var2, var3));
   }

   public Response<String> xgroupCreate(String var1, String var2, StreamEntryID var3, boolean var4) {
      return this.<String>appendCommand(this.commandObjects.xgroupCreate(var1, var2, var3, var4));
   }

   public Response<String> xgroupSetID(String var1, String var2, StreamEntryID var3) {
      return this.<String>appendCommand(this.commandObjects.xgroupSetID(var1, var2, var3));
   }

   public Response<Long> xgroupDestroy(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.xgroupDestroy(var1, var2));
   }

   public Response<Boolean> xgroupCreateConsumer(String var1, String var2, String var3) {
      return this.<Boolean>appendCommand(this.commandObjects.xgroupCreateConsumer(var1, var2, var3));
   }

   public Response<Long> xgroupDelConsumer(String var1, String var2, String var3) {
      return this.<Long>appendCommand(this.commandObjects.xgroupDelConsumer(var1, var2, var3));
   }

   public Response<StreamPendingSummary> xpending(String var1, String var2) {
      return this.<StreamPendingSummary>appendCommand(this.commandObjects.xpending(var1, var2));
   }

   public Response<List<StreamPendingEntry>> xpending(String var1, String var2, XPendingParams var3) {
      return this.<List<StreamPendingEntry>>appendCommand(this.commandObjects.xpending(var1, var2, var3));
   }

   public Response<Long> xdel(String var1, StreamEntryID... var2) {
      return this.<Long>appendCommand(this.commandObjects.xdel(var1, var2));
   }

   public Response<Long> xtrim(String var1, long var2, boolean var4) {
      return this.<Long>appendCommand(this.commandObjects.xtrim(var1, var2, var4));
   }

   public Response<Long> xtrim(String var1, XTrimParams var2) {
      return this.<Long>appendCommand(this.commandObjects.xtrim(var1, var2));
   }

   public Response<List<StreamEntry>> xclaim(String var1, String var2, String var3, long var4, XClaimParams var6, StreamEntryID... var7) {
      return this.<List<StreamEntry>>appendCommand(this.commandObjects.xclaim(var1, var2, var3, var4, var6, var7));
   }

   public Response<List<StreamEntryID>> xclaimJustId(String var1, String var2, String var3, long var4, XClaimParams var6, StreamEntryID... var7) {
      return this.<List<StreamEntryID>>appendCommand(this.commandObjects.xclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public Response<Map.Entry<StreamEntryID, List<StreamEntry>>> xautoclaim(String var1, String var2, String var3, long var4, StreamEntryID var6, XAutoClaimParams var7) {
      return this.<Map.Entry<StreamEntryID, List<StreamEntry>>>appendCommand(this.commandObjects.xautoclaim(var1, var2, var3, var4, var6, var7));
   }

   public Response<Map.Entry<StreamEntryID, List<StreamEntryID>>> xautoclaimJustId(String var1, String var2, String var3, long var4, StreamEntryID var6, XAutoClaimParams var7) {
      return this.<Map.Entry<StreamEntryID, List<StreamEntryID>>>appendCommand(this.commandObjects.xautoclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public Response<StreamInfo> xinfoStream(String var1) {
      return this.<StreamInfo>appendCommand(this.commandObjects.xinfoStream(var1));
   }

   public Response<StreamFullInfo> xinfoStreamFull(String var1) {
      return this.<StreamFullInfo>appendCommand(this.commandObjects.xinfoStreamFull(var1));
   }

   public Response<StreamFullInfo> xinfoStreamFull(String var1, int var2) {
      return this.<StreamFullInfo>appendCommand(this.commandObjects.xinfoStreamFull(var1, var2));
   }

   public Response<List<StreamGroupInfo>> xinfoGroups(String var1) {
      return this.<List<StreamGroupInfo>>appendCommand(this.commandObjects.xinfoGroups(var1));
   }

   public Response<List<StreamConsumersInfo>> xinfoConsumers(String var1, String var2) {
      return this.<List<StreamConsumersInfo>>appendCommand(this.commandObjects.xinfoConsumers(var1, var2));
   }

   public Response<List<StreamConsumerInfo>> xinfoConsumers2(String var1, String var2) {
      return this.<List<StreamConsumerInfo>>appendCommand(this.commandObjects.xinfoConsumers2(var1, var2));
   }

   public Response<List<Map.Entry<String, List<StreamEntry>>>> xread(XReadParams var1, Map<String, StreamEntryID> var2) {
      return this.<List<Map.Entry<String, List<StreamEntry>>>>appendCommand(this.commandObjects.xread(var1, var2));
   }

   public Response<List<Map.Entry<String, List<StreamEntry>>>> xreadGroup(String var1, String var2, XReadGroupParams var3, Map<String, StreamEntryID> var4) {
      return this.<List<Map.Entry<String, List<StreamEntry>>>>appendCommand(this.commandObjects.xreadGroup(var1, var2, var3, var4));
   }

   public Response<Object> eval(String var1) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1));
   }

   public Response<Object> eval(String var1, int var2, String... var3) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Response<Object> eval(String var1, List<String> var2, List<String> var3) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Response<Object> evalReadonly(String var1, List<String> var2, List<String> var3) {
      return this.<Object>appendCommand(this.commandObjects.evalReadonly(var1, var2, var3));
   }

   public Response<Object> evalsha(String var1) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1));
   }

   public Response<Object> evalsha(String var1, int var2, String... var3) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Response<Object> evalsha(String var1, List<String> var2, List<String> var3) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Response<Object> evalshaReadonly(String var1, List<String> var2, List<String> var3) {
      return this.<Object>appendCommand(this.commandObjects.evalshaReadonly(var1, var2, var3));
   }

   public Response<Long> waitReplicas(String var1, int var2, long var3) {
      return this.<Long>appendCommand(this.commandObjects.waitReplicas(var1, var2, var3));
   }

   public Response<KeyValue<Long, Long>> waitAOF(String var1, long var2, long var4, long var6) {
      return this.<KeyValue<Long, Long>>appendCommand(this.commandObjects.waitAOF(var1, var2, var4, var6));
   }

   public Response<Object> eval(String var1, String var2) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1, var2));
   }

   public Response<Object> evalsha(String var1, String var2) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1, var2));
   }

   public Response<List<Boolean>> scriptExists(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.scriptExists(var1, var2));
   }

   public Response<String> scriptLoad(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.scriptLoad(var1, var2));
   }

   public Response<String> scriptFlush(String var1) {
      return this.<String>appendCommand(this.commandObjects.scriptFlush(var1));
   }

   public Response<String> scriptFlush(String var1, FlushMode var2) {
      return this.<String>appendCommand(this.commandObjects.scriptFlush(var1, var2));
   }

   public Response<String> scriptKill(String var1) {
      return this.<String>appendCommand(this.commandObjects.scriptKill(var1));
   }

   public Response<Object> fcall(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return this.<Object>appendCommand(this.commandObjects.fcall(var1, var2, var3));
   }

   public Response<Object> fcall(String var1, List<String> var2, List<String> var3) {
      return this.<Object>appendCommand(this.commandObjects.fcall(var1, var2, var3));
   }

   public Response<Object> fcallReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return this.<Object>appendCommand(this.commandObjects.fcallReadonly(var1, var2, var3));
   }

   public Response<Object> fcallReadonly(String var1, List<String> var2, List<String> var3) {
      return this.<Object>appendCommand(this.commandObjects.fcallReadonly(var1, var2, var3));
   }

   public Response<String> functionDelete(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.functionDelete(var1));
   }

   public Response<String> functionDelete(String var1) {
      return this.<String>appendCommand(this.commandObjects.functionDelete(var1));
   }

   public Response<byte[]> functionDump() {
      return this.<byte[]>appendCommand(this.commandObjects.functionDump());
   }

   public Response<List<LibraryInfo>> functionList(String var1) {
      return this.<List<LibraryInfo>>appendCommand(this.commandObjects.functionList(var1));
   }

   public Response<List<LibraryInfo>> functionList() {
      return this.<List<LibraryInfo>>appendCommand(this.commandObjects.functionList());
   }

   public Response<List<LibraryInfo>> functionListWithCode(String var1) {
      return this.<List<LibraryInfo>>appendCommand(this.commandObjects.functionListWithCode(var1));
   }

   public Response<List<LibraryInfo>> functionListWithCode() {
      return this.<List<LibraryInfo>>appendCommand(this.commandObjects.functionListWithCode());
   }

   public Response<List<Object>> functionListBinary() {
      return this.<List<Object>>appendCommand(this.commandObjects.functionListBinary());
   }

   public Response<List<Object>> functionList(byte[] var1) {
      return this.<List<Object>>appendCommand(this.commandObjects.functionList(var1));
   }

   public Response<List<Object>> functionListWithCodeBinary() {
      return this.<List<Object>>appendCommand(this.commandObjects.functionListWithCodeBinary());
   }

   public Response<List<Object>> functionListWithCode(byte[] var1) {
      return this.<List<Object>>appendCommand(this.commandObjects.functionListWithCode(var1));
   }

   public Response<String> functionLoad(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.functionLoad(var1));
   }

   public Response<String> functionLoad(String var1) {
      return this.<String>appendCommand(this.commandObjects.functionLoad(var1));
   }

   public Response<String> functionLoadReplace(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.functionLoadReplace(var1));
   }

   public Response<String> functionLoadReplace(String var1) {
      return this.<String>appendCommand(this.commandObjects.functionLoadReplace(var1));
   }

   public Response<String> functionRestore(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.functionRestore(var1));
   }

   public Response<String> functionRestore(byte[] var1, FunctionRestorePolicy var2) {
      return this.<String>appendCommand(this.commandObjects.functionRestore(var1, var2));
   }

   public Response<String> functionFlush() {
      return this.<String>appendCommand(this.commandObjects.functionFlush());
   }

   public Response<String> functionFlush(FlushMode var1) {
      return this.<String>appendCommand(this.commandObjects.functionFlush(var1));
   }

   public Response<String> functionKill() {
      return this.<String>appendCommand(this.commandObjects.functionKill());
   }

   public Response<FunctionStats> functionStats() {
      return this.<FunctionStats>appendCommand(this.commandObjects.functionStats());
   }

   public Response<Object> functionStatsBinary() {
      return this.<Object>appendCommand(this.commandObjects.functionStatsBinary());
   }

   public Response<Long> geoadd(byte[] var1, double var2, double var4, byte[] var6) {
      return this.<Long>appendCommand(this.commandObjects.geoadd(var1, var2, var4, var6));
   }

   public Response<Long> geoadd(byte[] var1, Map<byte[], GeoCoordinate> var2) {
      return this.<Long>appendCommand(this.commandObjects.geoadd(var1, var2));
   }

   public Response<Long> geoadd(byte[] var1, GeoAddParams var2, Map<byte[], GeoCoordinate> var3) {
      return this.<Long>appendCommand(this.commandObjects.geoadd(var1, var2, var3));
   }

   public Response<Double> geodist(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Double>appendCommand(this.commandObjects.geodist(var1, var2, var3));
   }

   public Response<Double> geodist(byte[] var1, byte[] var2, byte[] var3, GeoUnit var4) {
      return this.<Double>appendCommand(this.commandObjects.geodist(var1, var2, var3, var4));
   }

   public Response<List<byte[]>> geohash(byte[] var1, byte[]... var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.geohash(var1, var2));
   }

   public Response<List<GeoCoordinate>> geopos(byte[] var1, byte[]... var2) {
      return this.<List<GeoCoordinate>>appendCommand(this.commandObjects.geopos(var1, var2));
   }

   public Response<List<GeoRadiusResponse>> georadius(byte[] var1, double var2, double var4, double var6, GeoUnit var8) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8));
   }

   public Response<List<GeoRadiusResponse>> georadiusReadonly(byte[] var1, double var2, double var4, double var6, GeoUnit var8) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8));
   }

   public Response<List<GeoRadiusResponse>> georadius(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8, var9));
   }

   public Response<List<GeoRadiusResponse>> georadiusReadonly(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8, var9));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMember(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMemberReadonly(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMember(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5, var6));
   }

   public Response<List<GeoRadiusResponse>> georadiusByMemberReadonly(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5, var6));
   }

   public Response<Long> georadiusStore(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9, GeoRadiusStoreParam var10) {
      return this.<Long>appendCommand(this.commandObjects.georadiusStore(var1, var2, var4, var6, var8, var9, var10));
   }

   public Response<Long> georadiusByMemberStore(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6, GeoRadiusStoreParam var7) {
      return this.<Long>appendCommand(this.commandObjects.georadiusByMemberStore(var1, var2, var3, var5, var6, var7));
   }

   public Response<List<GeoRadiusResponse>> geosearch(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> geosearch(byte[] var1, GeoCoordinate var2, double var3, GeoUnit var5) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public Response<List<GeoRadiusResponse>> geosearch(byte[] var1, byte[] var2, double var3, double var5, GeoUnit var7) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public Response<List<GeoRadiusResponse>> geosearch(byte[] var1, GeoCoordinate var2, double var3, double var5, GeoUnit var7) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public Response<List<GeoRadiusResponse>> geosearch(byte[] var1, GeoSearchParam var2) {
      return this.<List<GeoRadiusResponse>>appendCommand(this.commandObjects.geosearch(var1, var2));
   }

   public Response<Long> geosearchStore(byte[] var1, byte[] var2, byte[] var3, double var4, GeoUnit var6) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public Response<Long> geosearchStore(byte[] var1, byte[] var2, GeoCoordinate var3, double var4, GeoUnit var6) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public Response<Long> geosearchStore(byte[] var1, byte[] var2, byte[] var3, double var4, double var6, GeoUnit var8) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public Response<Long> geosearchStore(byte[] var1, byte[] var2, GeoCoordinate var3, double var4, double var6, GeoUnit var8) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public Response<Long> geosearchStore(byte[] var1, byte[] var2, GeoSearchParam var3) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStore(var1, var2, var3));
   }

   public Response<Long> geosearchStoreStoreDist(byte[] var1, byte[] var2, GeoSearchParam var3) {
      return this.<Long>appendCommand(this.commandObjects.geosearchStoreStoreDist(var1, var2, var3));
   }

   public Response<Long> hset(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.hset(var1, var2, var3));
   }

   public Response<Long> hset(byte[] var1, Map<byte[], byte[]> var2) {
      return this.<Long>appendCommand(this.commandObjects.hset(var1, var2));
   }

   public Response<byte[]> hget(byte[] var1, byte[] var2) {
      return this.<byte[]>appendCommand(this.commandObjects.hget(var1, var2));
   }

   public Response<Long> hsetnx(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.hsetnx(var1, var2, var3));
   }

   public Response<String> hmset(byte[] var1, Map<byte[], byte[]> var2) {
      return this.<String>appendCommand(this.commandObjects.hmset(var1, var2));
   }

   public Response<List<byte[]>> hmget(byte[] var1, byte[]... var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.hmget(var1, var2));
   }

   public Response<Long> hincrBy(byte[] var1, byte[] var2, long var3) {
      return this.<Long>appendCommand(this.commandObjects.hincrBy(var1, var2, var3));
   }

   public Response<Double> hincrByFloat(byte[] var1, byte[] var2, double var3) {
      return this.<Double>appendCommand(this.commandObjects.hincrByFloat(var1, var2, var3));
   }

   public Response<Boolean> hexists(byte[] var1, byte[] var2) {
      return this.<Boolean>appendCommand(this.commandObjects.hexists(var1, var2));
   }

   public Response<Long> hdel(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.hdel(var1, var2));
   }

   public Response<Long> hlen(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.hlen(var1));
   }

   public Response<Set<byte[]>> hkeys(byte[] var1) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.hkeys(var1));
   }

   public Response<List<byte[]>> hvals(byte[] var1) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.hvals(var1));
   }

   public Response<Map<byte[], byte[]>> hgetAll(byte[] var1) {
      return this.<Map<byte[], byte[]>>appendCommand(this.commandObjects.hgetAll(var1));
   }

   public Response<byte[]> hrandfield(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.hrandfield(var1));
   }

   public Response<List<byte[]>> hrandfield(byte[] var1, long var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.hrandfield(var1, var2));
   }

   public Response<List<Map.Entry<byte[], byte[]>>> hrandfieldWithValues(byte[] var1, long var2) {
      return this.<List<Map.Entry<byte[], byte[]>>>appendCommand(this.commandObjects.hrandfieldWithValues(var1, var2));
   }

   public Response<ScanResult<Map.Entry<byte[], byte[]>>> hscan(byte[] var1, byte[] var2, ScanParams var3) {
      return this.<ScanResult<Map.Entry<byte[], byte[]>>>appendCommand(this.commandObjects.hscan(var1, var2, var3));
   }

   public Response<Long> hstrlen(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.hstrlen(var1, var2));
   }

   public Response<Long> pfadd(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.pfadd(var1, var2));
   }

   public Response<String> pfmerge(byte[] var1, byte[]... var2) {
      return this.<String>appendCommand(this.commandObjects.pfmerge(var1, var2));
   }

   public Response<Long> pfcount(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.pfcount(var1));
   }

   public Response<Long> pfcount(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.pfcount(var1));
   }

   public Response<Boolean> exists(byte[] var1) {
      return this.<Boolean>appendCommand(this.commandObjects.exists(var1));
   }

   public Response<Long> exists(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.exists(var1));
   }

   public Response<Long> persist(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.persist(var1));
   }

   public Response<String> type(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.type(var1));
   }

   public Response<byte[]> dump(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.dump(var1));
   }

   public Response<String> restore(byte[] var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.restore(var1, var2, var4));
   }

   public Response<String> restore(byte[] var1, long var2, byte[] var4, RestoreParams var5) {
      return this.<String>appendCommand(this.commandObjects.restore(var1, var2, var4, var5));
   }

   public Response<Long> expire(byte[] var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.expire(var1, var2));
   }

   public Response<Long> expire(byte[] var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.expire(var1, var2, var4));
   }

   public Response<Long> pexpire(byte[] var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.pexpire(var1, var2));
   }

   public Response<Long> pexpire(byte[] var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.pexpire(var1, var2, var4));
   }

   public Response<Long> expireTime(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.expireTime(var1));
   }

   public Response<Long> pexpireTime(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.pexpireTime(var1));
   }

   public Response<Long> expireAt(byte[] var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.expireAt(var1, var2));
   }

   public Response<Long> expireAt(byte[] var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.expireAt(var1, var2));
   }

   public Response<Long> pexpireAt(byte[] var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.pexpireAt(var1, var2));
   }

   public Response<Long> pexpireAt(byte[] var1, long var2, ExpiryOption var4) {
      return this.<Long>appendCommand(this.commandObjects.pexpireAt(var1, var2, var4));
   }

   public Response<Long> ttl(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.ttl(var1));
   }

   public Response<Long> pttl(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.pttl(var1));
   }

   public Response<Long> touch(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.touch(var1));
   }

   public Response<Long> touch(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.touch(var1));
   }

   public Response<List<byte[]>> sort(byte[] var1) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.sort(var1));
   }

   public Response<List<byte[]>> sort(byte[] var1, SortingParams var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.sort(var1, var2));
   }

   public Response<List<byte[]>> sortReadonly(byte[] var1, SortingParams var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.sortReadonly(var1, var2));
   }

   public Response<Long> del(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.del(var1));
   }

   public Response<Long> del(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.del(var1));
   }

   public Response<Long> unlink(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.unlink(var1));
   }

   public Response<Long> unlink(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.unlink(var1));
   }

   public Response<Boolean> copy(byte[] var1, byte[] var2, boolean var3) {
      return this.<Boolean>appendCommand(this.commandObjects.copy(var1, var2, var3));
   }

   public Response<String> rename(byte[] var1, byte[] var2) {
      return this.<String>appendCommand(this.commandObjects.rename(var1, var2));
   }

   public Response<Long> renamenx(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.renamenx(var1, var2));
   }

   public Response<Long> sort(byte[] var1, SortingParams var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.sort(var1, var2, var3));
   }

   public Response<Long> sort(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.sort(var1, var2));
   }

   public Response<Long> memoryUsage(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.memoryUsage(var1));
   }

   public Response<Long> memoryUsage(byte[] var1, int var2) {
      return this.<Long>appendCommand(this.commandObjects.memoryUsage(var1, var2));
   }

   public Response<Long> objectRefcount(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.objectRefcount(var1));
   }

   public Response<byte[]> objectEncoding(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.objectEncoding(var1));
   }

   public Response<Long> objectIdletime(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.objectIdletime(var1));
   }

   public Response<Long> objectFreq(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.objectFreq(var1));
   }

   public Response<String> migrate(String var1, int var2, byte[] var3, int var4) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4));
   }

   public Response<String> migrate(String var1, int var2, int var3, MigrateParams var4, byte[]... var5) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public Response<Set<byte[]>> keys(byte[] var1) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.keys(var1));
   }

   public Response<ScanResult<byte[]>> scan(byte[] var1) {
      return this.<ScanResult<byte[]>>appendCommand(this.commandObjects.scan(var1));
   }

   public Response<ScanResult<byte[]>> scan(byte[] var1, ScanParams var2) {
      return this.<ScanResult<byte[]>>appendCommand(this.commandObjects.scan(var1, var2));
   }

   public Response<ScanResult<byte[]>> scan(byte[] var1, ScanParams var2, byte[] var3) {
      return this.<ScanResult<byte[]>>appendCommand(this.commandObjects.scan(var1, var2, var3));
   }

   public Response<byte[]> randomBinaryKey() {
      return this.<byte[]>appendCommand(this.commandObjects.randomBinaryKey());
   }

   public Response<Long> rpush(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.rpush(var1, var2));
   }

   public Response<Long> lpush(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.lpush(var1, var2));
   }

   public Response<Long> llen(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.llen(var1));
   }

   public Response<List<byte[]>> lrange(byte[] var1, long var2, long var4) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.lrange(var1, var2, var4));
   }

   public Response<String> ltrim(byte[] var1, long var2, long var4) {
      return this.<String>appendCommand(this.commandObjects.ltrim(var1, var2, var4));
   }

   public Response<byte[]> lindex(byte[] var1, long var2) {
      return this.<byte[]>appendCommand(this.commandObjects.lindex(var1, var2));
   }

   public Response<String> lset(byte[] var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.lset(var1, var2, var4));
   }

   public Response<Long> lrem(byte[] var1, long var2, byte[] var4) {
      return this.<Long>appendCommand(this.commandObjects.lrem(var1, var2, var4));
   }

   public Response<byte[]> lpop(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.lpop(var1));
   }

   public Response<List<byte[]>> lpop(byte[] var1, int var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.lpop(var1, var2));
   }

   public Response<Long> lpos(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.lpos(var1, var2));
   }

   public Response<Long> lpos(byte[] var1, byte[] var2, LPosParams var3) {
      return this.<Long>appendCommand(this.commandObjects.lpos(var1, var2, var3));
   }

   public Response<List<Long>> lpos(byte[] var1, byte[] var2, LPosParams var3, long var4) {
      return this.<List<Long>>appendCommand(this.commandObjects.lpos(var1, var2, var3, var4));
   }

   public Response<byte[]> rpop(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.rpop(var1));
   }

   public Response<List<byte[]>> rpop(byte[] var1, int var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.rpop(var1, var2));
   }

   public Response<Long> linsert(byte[] var1, ListPosition var2, byte[] var3, byte[] var4) {
      return this.<Long>appendCommand(this.commandObjects.linsert(var1, var2, var3, var4));
   }

   public Response<Long> lpushx(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.lpushx(var1, var2));
   }

   public Response<Long> rpushx(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.rpushx(var1, var2));
   }

   public Response<List<byte[]>> blpop(int var1, byte[]... var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.blpop(var1, var2));
   }

   public Response<KeyValue<byte[], byte[]>> blpop(double var1, byte[]... var3) {
      return this.<KeyValue<byte[], byte[]>>appendCommand(this.commandObjects.blpop(var1, var3));
   }

   public Response<List<byte[]>> brpop(int var1, byte[]... var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.brpop(var1, var2));
   }

   public Response<KeyValue<byte[], byte[]>> brpop(double var1, byte[]... var3) {
      return this.<KeyValue<byte[], byte[]>>appendCommand(this.commandObjects.brpop(var1, var3));
   }

   public Response<byte[]> rpoplpush(byte[] var1, byte[] var2) {
      return this.<byte[]>appendCommand(this.commandObjects.rpoplpush(var1, var2));
   }

   public Response<byte[]> brpoplpush(byte[] var1, byte[] var2, int var3) {
      return this.<byte[]>appendCommand(this.commandObjects.brpoplpush(var1, var2, var3));
   }

   public Response<byte[]> lmove(byte[] var1, byte[] var2, ListDirection var3, ListDirection var4) {
      return this.<byte[]>appendCommand(this.commandObjects.lmove(var1, var2, var3, var4));
   }

   public Response<byte[]> blmove(byte[] var1, byte[] var2, ListDirection var3, ListDirection var4, double var5) {
      return this.<byte[]>appendCommand(this.commandObjects.blmove(var1, var2, var3, var4, var5));
   }

   public Response<KeyValue<byte[], List<byte[]>>> lmpop(ListDirection var1, byte[]... var2) {
      return this.<KeyValue<byte[], List<byte[]>>>appendCommand(this.commandObjects.lmpop(var1, var2));
   }

   public Response<KeyValue<byte[], List<byte[]>>> lmpop(ListDirection var1, int var2, byte[]... var3) {
      return this.<KeyValue<byte[], List<byte[]>>>appendCommand(this.commandObjects.lmpop(var1, var2, var3));
   }

   public Response<KeyValue<byte[], List<byte[]>>> blmpop(double var1, ListDirection var3, byte[]... var4) {
      return this.<KeyValue<byte[], List<byte[]>>>appendCommand(this.commandObjects.blmpop(var1, var3, var4));
   }

   public Response<KeyValue<byte[], List<byte[]>>> blmpop(double var1, ListDirection var3, int var4, byte[]... var5) {
      return this.<KeyValue<byte[], List<byte[]>>>appendCommand(this.commandObjects.blmpop(var1, var3, var4, var5));
   }

   public Response<Long> waitReplicas(byte[] var1, int var2, long var3) {
      return this.<Long>appendCommand(this.commandObjects.waitReplicas(var1, var2, var3));
   }

   public Response<KeyValue<Long, Long>> waitAOF(byte[] var1, long var2, long var4, long var6) {
      return this.<KeyValue<Long, Long>>appendCommand(this.commandObjects.waitAOF(var1, var2, var4, var6));
   }

   public Response<Object> eval(byte[] var1, byte[] var2) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1, var2));
   }

   public Response<Object> evalsha(byte[] var1, byte[] var2) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1, var2));
   }

   public Response<List<Boolean>> scriptExists(byte[] var1, byte[]... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.scriptExists(var1, var2));
   }

   public Response<byte[]> scriptLoad(byte[] var1, byte[] var2) {
      return this.<byte[]>appendCommand(this.commandObjects.scriptLoad(var1, var2));
   }

   public Response<String> scriptFlush(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.scriptFlush(var1));
   }

   public Response<String> scriptFlush(byte[] var1, FlushMode var2) {
      return this.<String>appendCommand(this.commandObjects.scriptFlush(var1, var2));
   }

   public Response<String> scriptKill(byte[] var1) {
      return this.<String>appendCommand(this.commandObjects.scriptKill(var1));
   }

   public Response<Object> eval(byte[] var1) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1));
   }

   public Response<Object> eval(byte[] var1, int var2, byte[]... var3) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Response<Object> eval(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return this.<Object>appendCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Response<Object> evalReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return this.<Object>appendCommand(this.commandObjects.evalReadonly(var1, var2, var3));
   }

   public Response<Object> evalsha(byte[] var1) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1));
   }

   public Response<Object> evalsha(byte[] var1, int var2, byte[]... var3) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Response<Object> evalsha(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return this.<Object>appendCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Response<Object> evalshaReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      return this.<Object>appendCommand(this.commandObjects.evalshaReadonly(var1, var2, var3));
   }

   public Response<Long> sadd(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.sadd(var1, var2));
   }

   public Response<Set<byte[]>> smembers(byte[] var1) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.smembers(var1));
   }

   public Response<Long> srem(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.srem(var1, var2));
   }

   public Response<byte[]> spop(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.spop(var1));
   }

   public Response<Set<byte[]>> spop(byte[] var1, long var2) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.spop(var1, var2));
   }

   public Response<Long> scard(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.scard(var1));
   }

   public Response<Boolean> sismember(byte[] var1, byte[] var2) {
      return this.<Boolean>appendCommand(this.commandObjects.sismember(var1, var2));
   }

   public Response<List<Boolean>> smismember(byte[] var1, byte[]... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.smismember(var1, var2));
   }

   public Response<byte[]> srandmember(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.srandmember(var1));
   }

   public Response<List<byte[]>> srandmember(byte[] var1, int var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.srandmember(var1, var2));
   }

   public Response<ScanResult<byte[]>> sscan(byte[] var1, byte[] var2, ScanParams var3) {
      return this.<ScanResult<byte[]>>appendCommand(this.commandObjects.sscan(var1, var2, var3));
   }

   public Response<Set<byte[]>> sdiff(byte[]... var1) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.sdiff(var1));
   }

   public Response<Long> sdiffstore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.sdiffstore(var1, var2));
   }

   public Response<Set<byte[]>> sinter(byte[]... var1) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.sinter(var1));
   }

   public Response<Long> sinterstore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.sinterstore(var1, var2));
   }

   public Response<Long> sintercard(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.sintercard(var1));
   }

   public Response<Long> sintercard(int var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.sintercard(var1, var2));
   }

   public Response<Set<byte[]>> sunion(byte[]... var1) {
      return this.<Set<byte[]>>appendCommand(this.commandObjects.sunion(var1));
   }

   public Response<Long> sunionstore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.sunionstore(var1, var2));
   }

   public Response<Long> smove(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.smove(var1, var2, var3));
   }

   public Response<Long> zadd(byte[] var1, double var2, byte[] var4) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2, var4));
   }

   public Response<Long> zadd(byte[] var1, double var2, byte[] var4, ZAddParams var5) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2, var4, var5));
   }

   public Response<Long> zadd(byte[] var1, Map<byte[], Double> var2) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2));
   }

   public Response<Long> zadd(byte[] var1, Map<byte[], Double> var2, ZAddParams var3) {
      return this.<Long>appendCommand(this.commandObjects.zadd(var1, var2, var3));
   }

   public Response<Double> zaddIncr(byte[] var1, double var2, byte[] var4, ZAddParams var5) {
      return this.<Double>appendCommand(this.commandObjects.zaddIncr(var1, var2, var4, var5));
   }

   public Response<Long> zrem(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.zrem(var1, var2));
   }

   public Response<Double> zincrby(byte[] var1, double var2, byte[] var4) {
      return this.<Double>appendCommand(this.commandObjects.zincrby(var1, var2, var4));
   }

   public Response<Double> zincrby(byte[] var1, double var2, byte[] var4, ZIncrByParams var5) {
      return this.<Double>appendCommand(this.commandObjects.zincrby(var1, var2, var4, var5));
   }

   public Response<Long> zrank(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.zrank(var1, var2));
   }

   public Response<Long> zrevrank(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.zrevrank(var1, var2));
   }

   public Response<KeyValue<Long, Double>> zrankWithScore(byte[] var1, byte[] var2) {
      return this.<KeyValue<Long, Double>>appendCommand(this.commandObjects.zrankWithScore(var1, var2));
   }

   public Response<KeyValue<Long, Double>> zrevrankWithScore(byte[] var1, byte[] var2) {
      return this.<KeyValue<Long, Double>>appendCommand(this.commandObjects.zrevrankWithScore(var1, var2));
   }

   public Response<List<byte[]>> zrange(byte[] var1, long var2, long var4) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrange(var1, var2, var4));
   }

   public Response<List<byte[]>> zrevrange(byte[] var1, long var2, long var4) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrange(var1, var2, var4));
   }

   public Response<List<Tuple>> zrangeWithScores(byte[] var1, long var2, long var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeWithScores(var1, var2, var4));
   }

   public Response<List<Tuple>> zrevrangeWithScores(byte[] var1, long var2, long var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeWithScores(var1, var2, var4));
   }

   public Response<byte[]> zrandmember(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.zrandmember(var1));
   }

   public Response<List<byte[]>> zrandmember(byte[] var1, long var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrandmember(var1, var2));
   }

   public Response<List<Tuple>> zrandmemberWithScores(byte[] var1, long var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrandmemberWithScores(var1, var2));
   }

   public Response<Long> zcard(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.zcard(var1));
   }

   public Response<Double> zscore(byte[] var1, byte[] var2) {
      return this.<Double>appendCommand(this.commandObjects.zscore(var1, var2));
   }

   public Response<List<Double>> zmscore(byte[] var1, byte[]... var2) {
      return this.<List<Double>>appendCommand(this.commandObjects.zmscore(var1, var2));
   }

   public Response<Tuple> zpopmax(byte[] var1) {
      return this.<Tuple>appendCommand(this.commandObjects.zpopmax(var1));
   }

   public Response<List<Tuple>> zpopmax(byte[] var1, int var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zpopmax(var1, var2));
   }

   public Response<Tuple> zpopmin(byte[] var1) {
      return this.<Tuple>appendCommand(this.commandObjects.zpopmin(var1));
   }

   public Response<List<Tuple>> zpopmin(byte[] var1, int var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zpopmin(var1, var2));
   }

   public Response<Long> zcount(byte[] var1, double var2, double var4) {
      return this.<Long>appendCommand(this.commandObjects.zcount(var1, var2, var4));
   }

   public Response<Long> zcount(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.zcount(var1, var2, var3));
   }

   public Response<List<byte[]>> zrangeByScore(byte[] var1, double var2, double var4) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var4));
   }

   public Response<List<byte[]>> zrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var3));
   }

   public Response<List<byte[]>> zrevrangeByScore(byte[] var1, double var2, double var4) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4));
   }

   public Response<List<byte[]>> zrangeByScore(byte[] var1, double var2, double var4, int var6, int var7) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var4, var6, var7));
   }

   public Response<List<byte[]>> zrevrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3));
   }

   public Response<List<byte[]>> zrangeByScore(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrangeByScore(var1, var2, var3, var4, var5));
   }

   public Response<List<byte[]>> zrevrangeByScore(byte[] var1, double var2, double var4, int var6, int var7) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4, var6, var7));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(byte[] var1, double var2, double var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, double var2, double var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(byte[] var1, double var2, double var4, int var6, int var7) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public Response<List<byte[]>> zrevrangeByScore(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3, var4, var5));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3));
   }

   public Response<List<Tuple>> zrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, double var2, double var4, int var6, int var7) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public Response<List<Tuple>> zrevrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public Response<Long> zremrangeByRank(byte[] var1, long var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByRank(var1, var2, var4));
   }

   public Response<Long> zremrangeByScore(byte[] var1, double var2, double var4) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByScore(var1, var2, var4));
   }

   public Response<Long> zremrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByScore(var1, var2, var3));
   }

   public Response<Long> zlexcount(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.zlexcount(var1, var2, var3));
   }

   public Response<List<byte[]>> zrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrangeByLex(var1, var2, var3));
   }

   public Response<List<byte[]>> zrangeByLex(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrangeByLex(var1, var2, var3, var4, var5));
   }

   public Response<List<byte[]>> zrevrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3));
   }

   public Response<List<byte[]>> zrevrangeByLex(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3, var4, var5));
   }

   public Response<List<byte[]>> zrange(byte[] var1, ZRangeParams var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zrange(var1, var2));
   }

   public Response<List<Tuple>> zrangeWithScores(byte[] var1, ZRangeParams var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zrangeWithScores(var1, var2));
   }

   public Response<Long> zrangestore(byte[] var1, byte[] var2, ZRangeParams var3) {
      return this.<Long>appendCommand(this.commandObjects.zrangestore(var1, var2, var3));
   }

   public Response<Long> zremrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.zremrangeByLex(var1, var2, var3));
   }

   public Response<ScanResult<Tuple>> zscan(byte[] var1, byte[] var2, ScanParams var3) {
      return this.<ScanResult<Tuple>>appendCommand(this.commandObjects.zscan(var1, var2, var3));
   }

   public Response<KeyValue<byte[], Tuple>> bzpopmax(double var1, byte[]... var3) {
      return this.<KeyValue<byte[], Tuple>>appendCommand(this.commandObjects.bzpopmax(var1, var3));
   }

   public Response<KeyValue<byte[], Tuple>> bzpopmin(double var1, byte[]... var3) {
      return this.<KeyValue<byte[], Tuple>>appendCommand(this.commandObjects.bzpopmin(var1, var3));
   }

   public Response<KeyValue<byte[], List<Tuple>>> zmpop(SortedSetOption var1, byte[]... var2) {
      return this.<KeyValue<byte[], List<Tuple>>>appendCommand(this.commandObjects.zmpop(var1, var2));
   }

   public Response<KeyValue<byte[], List<Tuple>>> zmpop(SortedSetOption var1, int var2, byte[]... var3) {
      return this.<KeyValue<byte[], List<Tuple>>>appendCommand(this.commandObjects.zmpop(var1, var2, var3));
   }

   public Response<KeyValue<byte[], List<Tuple>>> bzmpop(double var1, SortedSetOption var3, byte[]... var4) {
      return this.<KeyValue<byte[], List<Tuple>>>appendCommand(this.commandObjects.bzmpop(var1, var3, var4));
   }

   public Response<KeyValue<byte[], List<Tuple>>> bzmpop(double var1, SortedSetOption var3, int var4, byte[]... var5) {
      return this.<KeyValue<byte[], List<Tuple>>>appendCommand(this.commandObjects.bzmpop(var1, var3, var4, var5));
   }

   public Response<List<byte[]>> zdiff(byte[]... var1) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zdiff(var1));
   }

   public Response<List<Tuple>> zdiffWithScores(byte[]... var1) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zdiffWithScores(var1));
   }

   @Deprecated
   public Response<Long> zdiffStore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.zdiffStore(var1, var2));
   }

   public Response<Long> zdiffstore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.zdiffstore(var1, var2));
   }

   public Response<List<byte[]>> zinter(ZParams var1, byte[]... var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zinter(var1, var2));
   }

   public Response<List<Tuple>> zinterWithScores(ZParams var1, byte[]... var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zinterWithScores(var1, var2));
   }

   public Response<Long> zinterstore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.zinterstore(var1, var2));
   }

   public Response<Long> zinterstore(byte[] var1, ZParams var2, byte[]... var3) {
      return this.<Long>appendCommand(this.commandObjects.zinterstore(var1, var2, var3));
   }

   public Response<Long> zintercard(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.zintercard(var1));
   }

   public Response<Long> zintercard(long var1, byte[]... var3) {
      return this.<Long>appendCommand(this.commandObjects.zintercard(var1, var3));
   }

   public Response<List<byte[]>> zunion(ZParams var1, byte[]... var2) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.zunion(var1, var2));
   }

   public Response<List<Tuple>> zunionWithScores(ZParams var1, byte[]... var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.zunionWithScores(var1, var2));
   }

   public Response<Long> zunionstore(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.zunionstore(var1, var2));
   }

   public Response<Long> zunionstore(byte[] var1, ZParams var2, byte[]... var3) {
      return this.<Long>appendCommand(this.commandObjects.zunionstore(var1, var2, var3));
   }

   public Response<byte[]> xadd(byte[] var1, XAddParams var2, Map<byte[], byte[]> var3) {
      return this.<byte[]>appendCommand(this.commandObjects.xadd(var1, var2, var3));
   }

   public Response<Long> xlen(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.xlen(var1));
   }

   public Response<List<Object>> xrange(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<Object>>appendCommand(this.commandObjects.xrange(var1, var2, var3));
   }

   public Response<List<Object>> xrange(byte[] var1, byte[] var2, byte[] var3, int var4) {
      return this.<List<Object>>appendCommand(this.commandObjects.xrange(var1, var2, var3, var4));
   }

   public Response<List<Object>> xrevrange(byte[] var1, byte[] var2, byte[] var3) {
      return this.<List<Object>>appendCommand(this.commandObjects.xrevrange(var1, var2, var3));
   }

   public Response<List<Object>> xrevrange(byte[] var1, byte[] var2, byte[] var3, int var4) {
      return this.<List<Object>>appendCommand(this.commandObjects.xrevrange(var1, var2, var3, var4));
   }

   public Response<Long> xack(byte[] var1, byte[] var2, byte[]... var3) {
      return this.<Long>appendCommand(this.commandObjects.xack(var1, var2, var3));
   }

   public Response<String> xgroupCreate(byte[] var1, byte[] var2, byte[] var3, boolean var4) {
      return this.<String>appendCommand(this.commandObjects.xgroupCreate(var1, var2, var3, var4));
   }

   public Response<String> xgroupSetID(byte[] var1, byte[] var2, byte[] var3) {
      return this.<String>appendCommand(this.commandObjects.xgroupSetID(var1, var2, var3));
   }

   public Response<Long> xgroupDestroy(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.xgroupDestroy(var1, var2));
   }

   public Response<Boolean> xgroupCreateConsumer(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Boolean>appendCommand(this.commandObjects.xgroupCreateConsumer(var1, var2, var3));
   }

   public Response<Long> xgroupDelConsumer(byte[] var1, byte[] var2, byte[] var3) {
      return this.<Long>appendCommand(this.commandObjects.xgroupDelConsumer(var1, var2, var3));
   }

   public Response<Long> xdel(byte[] var1, byte[]... var2) {
      return this.<Long>appendCommand(this.commandObjects.xdel(var1, var2));
   }

   public Response<Long> xtrim(byte[] var1, long var2, boolean var4) {
      return this.<Long>appendCommand(this.commandObjects.xtrim(var1, var2, var4));
   }

   public Response<Long> xtrim(byte[] var1, XTrimParams var2) {
      return this.<Long>appendCommand(this.commandObjects.xtrim(var1, var2));
   }

   public Response<Object> xpending(byte[] var1, byte[] var2) {
      return this.<Object>appendCommand(this.commandObjects.xpending(var1, var2));
   }

   public Response<List<Object>> xpending(byte[] var1, byte[] var2, XPendingParams var3) {
      return this.<List<Object>>appendCommand(this.commandObjects.xpending(var1, var2, var3));
   }

   public Response<List<byte[]>> xclaim(byte[] var1, byte[] var2, byte[] var3, long var4, XClaimParams var6, byte[]... var7) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.xclaim(var1, var2, var3, var4, var6, var7));
   }

   public Response<List<byte[]>> xclaimJustId(byte[] var1, byte[] var2, byte[] var3, long var4, XClaimParams var6, byte[]... var7) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.xclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public Response<List<Object>> xautoclaim(byte[] var1, byte[] var2, byte[] var3, long var4, byte[] var6, XAutoClaimParams var7) {
      return this.<List<Object>>appendCommand(this.commandObjects.xautoclaim(var1, var2, var3, var4, var6, var7));
   }

   public Response<List<Object>> xautoclaimJustId(byte[] var1, byte[] var2, byte[] var3, long var4, byte[] var6, XAutoClaimParams var7) {
      return this.<List<Object>>appendCommand(this.commandObjects.xautoclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public Response<Object> xinfoStream(byte[] var1) {
      return this.<Object>appendCommand(this.commandObjects.xinfoStream(var1));
   }

   public Response<Object> xinfoStreamFull(byte[] var1) {
      return this.<Object>appendCommand(this.commandObjects.xinfoStreamFull(var1));
   }

   public Response<Object> xinfoStreamFull(byte[] var1, int var2) {
      return this.<Object>appendCommand(this.commandObjects.xinfoStreamFull(var1, var2));
   }

   public Response<List<Object>> xinfoGroups(byte[] var1) {
      return this.<List<Object>>appendCommand(this.commandObjects.xinfoGroups(var1));
   }

   public Response<List<Object>> xinfoConsumers(byte[] var1, byte[] var2) {
      return this.<List<Object>>appendCommand(this.commandObjects.xinfoConsumers(var1, var2));
   }

   public Response<List<Object>> xread(XReadParams var1, Map.Entry<byte[], byte[]>... var2) {
      return this.<List<Object>>appendCommand(this.commandObjects.xread(var1, var2));
   }

   public Response<List<Object>> xreadGroup(byte[] var1, byte[] var2, XReadGroupParams var3, Map.Entry<byte[], byte[]>... var4) {
      return this.<List<Object>>appendCommand(this.commandObjects.xreadGroup(var1, var2, var3, var4));
   }

   public Response<String> set(byte[] var1, byte[] var2) {
      return this.<String>appendCommand(this.commandObjects.set(var1, var2));
   }

   public Response<String> set(byte[] var1, byte[] var2, SetParams var3) {
      return this.<String>appendCommand(this.commandObjects.set(var1, var2, var3));
   }

   public Response<byte[]> get(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.get(var1));
   }

   public Response<byte[]> setGet(byte[] var1, byte[] var2, SetParams var3) {
      return this.<byte[]>appendCommand(this.commandObjects.setGet(var1, var2, var3));
   }

   public Response<byte[]> getDel(byte[] var1) {
      return this.<byte[]>appendCommand(this.commandObjects.getDel(var1));
   }

   public Response<byte[]> getEx(byte[] var1, GetExParams var2) {
      return this.<byte[]>appendCommand(this.commandObjects.getEx(var1, var2));
   }

   public Response<Boolean> setbit(byte[] var1, long var2, boolean var4) {
      return this.<Boolean>appendCommand(this.commandObjects.setbit(var1, var2, var4));
   }

   public Response<Boolean> getbit(byte[] var1, long var2) {
      return this.<Boolean>appendCommand(this.commandObjects.getbit(var1, var2));
   }

   public Response<Long> setrange(byte[] var1, long var2, byte[] var4) {
      return this.<Long>appendCommand(this.commandObjects.setrange(var1, var2, var4));
   }

   public Response<byte[]> getrange(byte[] var1, long var2, long var4) {
      return this.<byte[]>appendCommand(this.commandObjects.getrange(var1, var2, var4));
   }

   public Response<byte[]> getSet(byte[] var1, byte[] var2) {
      return this.<byte[]>appendCommand(this.commandObjects.getSet(var1, var2));
   }

   public Response<Long> setnx(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.setnx(var1, var2));
   }

   public Response<String> setex(byte[] var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.setex(var1, var2, var4));
   }

   public Response<String> psetex(byte[] var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.psetex(var1, var2, var4));
   }

   public Response<List<byte[]>> mget(byte[]... var1) {
      return this.<List<byte[]>>appendCommand(this.commandObjects.mget(var1));
   }

   public Response<String> mset(byte[]... var1) {
      return this.<String>appendCommand(this.commandObjects.mset(var1));
   }

   public Response<Long> msetnx(byte[]... var1) {
      return this.<Long>appendCommand(this.commandObjects.msetnx(var1));
   }

   public Response<Long> incr(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.incr(var1));
   }

   public Response<Long> incrBy(byte[] var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.incrBy(var1, var2));
   }

   public Response<Double> incrByFloat(byte[] var1, double var2) {
      return this.<Double>appendCommand(this.commandObjects.incrByFloat(var1, var2));
   }

   public Response<Long> decr(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.decr(var1));
   }

   public Response<Long> decrBy(byte[] var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.decrBy(var1, var2));
   }

   public Response<Long> append(byte[] var1, byte[] var2) {
      return this.<Long>appendCommand(this.commandObjects.append(var1, var2));
   }

   public Response<byte[]> substr(byte[] var1, int var2, int var3) {
      return this.<byte[]>appendCommand(this.commandObjects.substr(var1, var2, var3));
   }

   public Response<Long> strlen(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.strlen(var1));
   }

   public Response<Long> bitcount(byte[] var1) {
      return this.<Long>appendCommand(this.commandObjects.bitcount(var1));
   }

   public Response<Long> bitcount(byte[] var1, long var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.bitcount(var1, var2, var4));
   }

   public Response<Long> bitcount(byte[] var1, long var2, long var4, BitCountOption var6) {
      return this.<Long>appendCommand(this.commandObjects.bitcount(var1, var2, var4, var6));
   }

   public Response<Long> bitpos(byte[] var1, boolean var2) {
      return this.<Long>appendCommand(this.commandObjects.bitpos(var1, var2));
   }

   public Response<Long> bitpos(byte[] var1, boolean var2, BitPosParams var3) {
      return this.<Long>appendCommand(this.commandObjects.bitpos(var1, var2, var3));
   }

   public Response<List<Long>> bitfield(byte[] var1, byte[]... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.bitfield(var1, var2));
   }

   public Response<List<Long>> bitfieldReadonly(byte[] var1, byte[]... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.bitfieldReadonly(var1, var2));
   }

   public Response<Long> bitop(BitOP var1, byte[] var2, byte[]... var3) {
      return this.<Long>appendCommand(this.commandObjects.bitop(var1, var2, var3));
   }

   public Response<String> ftCreate(String var1, IndexOptions var2, Schema var3) {
      return this.<String>appendCommand(this.commandObjects.ftCreate(var1, var2, var3));
   }

   public Response<String> ftCreate(String var1, FTCreateParams var2, Iterable<SchemaField> var3) {
      return this.<String>appendCommand(this.commandObjects.ftCreate(var1, var2, var3));
   }

   public Response<String> ftAlter(String var1, Schema var2) {
      return this.<String>appendCommand(this.commandObjects.ftAlter(var1, var2));
   }

   public Response<String> ftAlter(String var1, Iterable<SchemaField> var2) {
      return this.<String>appendCommand(this.commandObjects.ftAlter(var1, var2));
   }

   public Response<String> ftAliasAdd(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.ftAliasAdd(var1, var2));
   }

   public Response<String> ftAliasUpdate(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.ftAliasUpdate(var1, var2));
   }

   public Response<String> ftAliasDel(String var1) {
      return this.<String>appendCommand(this.commandObjects.ftAliasDel(var1));
   }

   public Response<String> ftDropIndex(String var1) {
      return this.<String>appendCommand(this.commandObjects.ftDropIndex(var1));
   }

   public Response<String> ftDropIndexDD(String var1) {
      return this.<String>appendCommand(this.commandObjects.ftDropIndexDD(var1));
   }

   public Response<SearchResult> ftSearch(String var1, String var2) {
      return this.<SearchResult>appendCommand(this.commandObjects.ftSearch(var1, var2));
   }

   public Response<SearchResult> ftSearch(String var1, String var2, FTSearchParams var3) {
      return this.<SearchResult>appendCommand(this.commandObjects.ftSearch(var1, var2, var3));
   }

   public Response<SearchResult> ftSearch(String var1, Query var2) {
      return this.<SearchResult>appendCommand(this.commandObjects.ftSearch(var1, var2));
   }

   @Deprecated
   public Response<SearchResult> ftSearch(byte[] var1, Query var2) {
      return this.<SearchResult>appendCommand(this.commandObjects.ftSearch(var1, var2));
   }

   public Response<String> ftExplain(String var1, Query var2) {
      return this.<String>appendCommand(this.commandObjects.ftExplain(var1, var2));
   }

   public Response<List<String>> ftExplainCLI(String var1, Query var2) {
      return this.<List<String>>appendCommand(this.commandObjects.ftExplainCLI(var1, var2));
   }

   public Response<AggregationResult> ftAggregate(String var1, AggregationBuilder var2) {
      return this.<AggregationResult>appendCommand(this.commandObjects.ftAggregate(var1, var2));
   }

   public Response<String> ftSynUpdate(String var1, String var2, String... var3) {
      return this.<String>appendCommand(this.commandObjects.ftSynUpdate(var1, var2, var3));
   }

   public Response<Map<String, List<String>>> ftSynDump(String var1) {
      return this.<Map<String, List<String>>>appendCommand(this.commandObjects.ftSynDump(var1));
   }

   public Response<Long> ftDictAdd(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.ftDictAdd(var1, var2));
   }

   public Response<Long> ftDictDel(String var1, String... var2) {
      return this.<Long>appendCommand(this.commandObjects.ftDictDel(var1, var2));
   }

   public Response<Set<String>> ftDictDump(String var1) {
      return this.<Set<String>>appendCommand(this.commandObjects.ftDictDump(var1));
   }

   public Response<Long> ftDictAddBySampleKey(String var1, String var2, String... var3) {
      return this.<Long>appendCommand(this.commandObjects.ftDictAddBySampleKey(var1, var2, var3));
   }

   public Response<Long> ftDictDelBySampleKey(String var1, String var2, String... var3) {
      return this.<Long>appendCommand(this.commandObjects.ftDictDelBySampleKey(var1, var2, var3));
   }

   public Response<Set<String>> ftDictDumpBySampleKey(String var1, String var2) {
      return this.<Set<String>>appendCommand(this.commandObjects.ftDictDumpBySampleKey(var1, var2));
   }

   public Response<Map<String, Map<String, Double>>> ftSpellCheck(String var1, String var2) {
      return this.<Map<String, Map<String, Double>>>appendCommand(this.commandObjects.ftSpellCheck(var1, var2));
   }

   public Response<Map<String, Map<String, Double>>> ftSpellCheck(String var1, String var2, FTSpellCheckParams var3) {
      return this.<Map<String, Map<String, Double>>>appendCommand(this.commandObjects.ftSpellCheck(var1, var2, var3));
   }

   public Response<Map<String, Object>> ftInfo(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.ftInfo(var1));
   }

   public Response<Set<String>> ftTagVals(String var1, String var2) {
      return this.<Set<String>>appendCommand(this.commandObjects.ftTagVals(var1, var2));
   }

   public Response<Map<String, Object>> ftConfigGet(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.ftConfigGet(var1));
   }

   public Response<Map<String, Object>> ftConfigGet(String var1, String var2) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.ftConfigGet(var1, var2));
   }

   public Response<String> ftConfigSet(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.ftConfigSet(var1, var2));
   }

   public Response<String> ftConfigSet(String var1, String var2, String var3) {
      return this.<String>appendCommand(this.commandObjects.ftConfigSet(var1, var2, var3));
   }

   public Response<Long> ftSugAdd(String var1, String var2, double var3) {
      return this.<Long>appendCommand(this.commandObjects.ftSugAdd(var1, var2, var3));
   }

   public Response<Long> ftSugAddIncr(String var1, String var2, double var3) {
      return this.<Long>appendCommand(this.commandObjects.ftSugAddIncr(var1, var2, var3));
   }

   public Response<List<String>> ftSugGet(String var1, String var2) {
      return this.<List<String>>appendCommand(this.commandObjects.ftSugGet(var1, var2));
   }

   public Response<List<String>> ftSugGet(String var1, String var2, boolean var3, int var4) {
      return this.<List<String>>appendCommand(this.commandObjects.ftSugGet(var1, var2, var3, var4));
   }

   public Response<List<Tuple>> ftSugGetWithScores(String var1, String var2) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.ftSugGetWithScores(var1, var2));
   }

   public Response<List<Tuple>> ftSugGetWithScores(String var1, String var2, boolean var3, int var4) {
      return this.<List<Tuple>>appendCommand(this.commandObjects.ftSugGetWithScores(var1, var2, var3, var4));
   }

   public Response<Boolean> ftSugDel(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.ftSugDel(var1, var2));
   }

   public Response<Long> ftSugLen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.ftSugLen(var1));
   }

   public Response<LCSMatchResult> lcs(byte[] var1, byte[] var2, LCSParams var3) {
      return this.<LCSMatchResult>appendCommand(this.commandObjects.lcs(var1, var2, var3));
   }

   public Response<String> jsonSet(String var1, Path2 var2, Object var3) {
      return this.<String>appendCommand(this.commandObjects.jsonSet(var1, var2, var3));
   }

   public Response<String> jsonSetWithEscape(String var1, Path2 var2, Object var3) {
      return this.<String>appendCommand(this.commandObjects.jsonSetWithEscape(var1, var2, var3));
   }

   public Response<String> jsonSet(String var1, Path var2, Object var3) {
      return this.<String>appendCommand(this.commandObjects.jsonSet(var1, var2, var3));
   }

   public Response<String> jsonSet(String var1, Path2 var2, Object var3, JsonSetParams var4) {
      return this.<String>appendCommand(this.commandObjects.jsonSet(var1, var2, var3, var4));
   }

   public Response<String> jsonSetWithEscape(String var1, Path2 var2, Object var3, JsonSetParams var4) {
      return this.<String>appendCommand(this.commandObjects.jsonSetWithEscape(var1, var2, var3, var4));
   }

   public Response<String> jsonSet(String var1, Path var2, Object var3, JsonSetParams var4) {
      return this.<String>appendCommand(this.commandObjects.jsonSet(var1, var2, var3, var4));
   }

   public Response<String> jsonMerge(String var1, Path2 var2, Object var3) {
      return this.<String>appendCommand(this.commandObjects.jsonMerge(var1, var2, var3));
   }

   public Response<String> jsonMerge(String var1, Path var2, Object var3) {
      return this.<String>appendCommand(this.commandObjects.jsonMerge(var1, var2, var3));
   }

   public Response<Object> jsonGet(String var1) {
      return this.<Object>appendCommand(this.commandObjects.jsonGet(var1));
   }

   public <T> Response<T> jsonGet(String var1, Class<T> var2) {
      return this.<T>appendCommand(this.commandObjects.jsonGet(var1, var2));
   }

   public Response<Object> jsonGet(String var1, Path2... var2) {
      return this.<Object>appendCommand(this.commandObjects.jsonGet(var1, var2));
   }

   public Response<Object> jsonGet(String var1, Path... var2) {
      return this.<Object>appendCommand(this.commandObjects.jsonGet(var1, var2));
   }

   public <T> Response<T> jsonGet(String var1, Class<T> var2, Path... var3) {
      return this.<T>appendCommand(this.commandObjects.jsonGet(var1, var2, var3));
   }

   public Response<List<JSONArray>> jsonMGet(Path2 var1, String... var2) {
      return this.<List<JSONArray>>appendCommand(this.commandObjects.jsonMGet(var1, var2));
   }

   public <T> Response<List<T>> jsonMGet(Path var1, Class<T> var2, String... var3) {
      return this.<List<T>>appendCommand(this.commandObjects.jsonMGet(var1, var2, var3));
   }

   public Response<Long> jsonDel(String var1) {
      return this.<Long>appendCommand(this.commandObjects.jsonDel(var1));
   }

   public Response<Long> jsonDel(String var1, Path2 var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonDel(var1, var2));
   }

   public Response<Long> jsonDel(String var1, Path var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonDel(var1, var2));
   }

   public Response<Long> jsonClear(String var1) {
      return this.<Long>appendCommand(this.commandObjects.jsonClear(var1));
   }

   public Response<Long> jsonClear(String var1, Path2 var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonClear(var1, var2));
   }

   public Response<Long> jsonClear(String var1, Path var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonClear(var1, var2));
   }

   public Response<List<Boolean>> jsonToggle(String var1, Path2 var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.jsonToggle(var1, var2));
   }

   public Response<String> jsonToggle(String var1, Path var2) {
      return this.<String>appendCommand(this.commandObjects.jsonToggle(var1, var2));
   }

   public Response<Class<?>> jsonType(String var1) {
      return this.<Class<?>>appendCommand(this.commandObjects.jsonType(var1));
   }

   public Response<List<Class<?>>> jsonType(String var1, Path2 var2) {
      return this.<List<Class<?>>>appendCommand(this.commandObjects.jsonType(var1, var2));
   }

   public Response<Class<?>> jsonType(String var1, Path var2) {
      return this.<Class<?>>appendCommand(this.commandObjects.jsonType(var1, var2));
   }

   public Response<Long> jsonStrAppend(String var1, Object var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonStrAppend(var1, var2));
   }

   public Response<List<Long>> jsonStrAppend(String var1, Path2 var2, Object var3) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonStrAppend(var1, var2, var3));
   }

   public Response<Long> jsonStrAppend(String var1, Path var2, Object var3) {
      return this.<Long>appendCommand(this.commandObjects.jsonStrAppend(var1, var2, var3));
   }

   public Response<Long> jsonStrLen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.jsonStrLen(var1));
   }

   public Response<List<Long>> jsonStrLen(String var1, Path2 var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonStrLen(var1, var2));
   }

   public Response<Long> jsonStrLen(String var1, Path var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonStrLen(var1, var2));
   }

   public Response<Object> jsonNumIncrBy(String var1, Path2 var2, double var3) {
      return this.<Object>appendCommand(this.commandObjects.jsonNumIncrBy(var1, var2, var3));
   }

   public Response<Double> jsonNumIncrBy(String var1, Path var2, double var3) {
      return this.<Double>appendCommand(this.commandObjects.jsonNumIncrBy(var1, var2, var3));
   }

   public Response<List<Long>> jsonArrAppend(String var1, Path2 var2, Object... var3) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrAppend(var1, var2, var3));
   }

   public Response<List<Long>> jsonArrAppendWithEscape(String var1, Path2 var2, Object... var3) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrAppendWithEscape(var1, var2, var3));
   }

   public Response<Long> jsonArrAppend(String var1, Path var2, Object... var3) {
      return this.<Long>appendCommand(this.commandObjects.jsonArrAppend(var1, var2, var3));
   }

   public Response<List<Long>> jsonArrIndex(String var1, Path2 var2, Object var3) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrIndex(var1, var2, var3));
   }

   public Response<List<Long>> jsonArrIndexWithEscape(String var1, Path2 var2, Object var3) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrIndexWithEscape(var1, var2, var3));
   }

   public Response<Long> jsonArrIndex(String var1, Path var2, Object var3) {
      return this.<Long>appendCommand(this.commandObjects.jsonArrIndex(var1, var2, var3));
   }

   public Response<List<Long>> jsonArrInsert(String var1, Path2 var2, int var3, Object... var4) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrInsert(var1, var2, var3, var4));
   }

   public Response<List<Long>> jsonArrInsertWithEscape(String var1, Path2 var2, int var3, Object... var4) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrInsertWithEscape(var1, var2, var3, var4));
   }

   public Response<Long> jsonArrInsert(String var1, Path var2, int var3, Object... var4) {
      return this.<Long>appendCommand(this.commandObjects.jsonArrInsert(var1, var2, var3, var4));
   }

   public Response<Object> jsonArrPop(String var1) {
      return this.<Object>appendCommand(this.commandObjects.jsonArrPop(var1));
   }

   public Response<Long> jsonArrLen(String var1, Path var2) {
      return this.<Long>appendCommand(this.commandObjects.jsonArrLen(var1, var2));
   }

   public Response<List<Long>> jsonArrTrim(String var1, Path2 var2, int var3, int var4) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrTrim(var1, var2, var3, var4));
   }

   public Response<Long> jsonArrTrim(String var1, Path var2, int var3, int var4) {
      return this.<Long>appendCommand(this.commandObjects.jsonArrTrim(var1, var2, var3, var4));
   }

   public <T> Response<T> jsonArrPop(String var1, Class<T> var2, Path var3) {
      return this.<T>appendCommand(this.commandObjects.jsonArrPop(var1, var2, var3));
   }

   public Response<List<Object>> jsonArrPop(String var1, Path2 var2, int var3) {
      return this.<List<Object>>appendCommand(this.commandObjects.jsonArrPop(var1, var2, var3));
   }

   public Response<Object> jsonArrPop(String var1, Path var2, int var3) {
      return this.<Object>appendCommand(this.commandObjects.jsonArrPop(var1, var2, var3));
   }

   public <T> Response<T> jsonArrPop(String var1, Class<T> var2, Path var3, int var4) {
      return this.<T>appendCommand(this.commandObjects.jsonArrPop(var1, var2, var3, var4));
   }

   public Response<Long> jsonArrLen(String var1) {
      return this.<Long>appendCommand(this.commandObjects.jsonArrLen(var1));
   }

   public Response<List<Long>> jsonArrLen(String var1, Path2 var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.jsonArrLen(var1, var2));
   }

   public <T> Response<T> jsonArrPop(String var1, Class<T> var2) {
      return this.<T>appendCommand(this.commandObjects.jsonArrPop(var1, var2));
   }

   public Response<List<Object>> jsonArrPop(String var1, Path2 var2) {
      return this.<List<Object>>appendCommand(this.commandObjects.jsonArrPop(var1, var2));
   }

   public Response<Object> jsonArrPop(String var1, Path var2) {
      return this.<Object>appendCommand(this.commandObjects.jsonArrPop(var1, var2));
   }

   public Response<String> tsCreate(String var1) {
      return this.<String>appendCommand(this.commandObjects.tsCreate(var1));
   }

   public Response<String> tsCreate(String var1, TSCreateParams var2) {
      return this.<String>appendCommand(this.commandObjects.tsCreate(var1, var2));
   }

   public Response<Long> tsDel(String var1, long var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.tsDel(var1, var2, var4));
   }

   public Response<String> tsAlter(String var1, TSAlterParams var2) {
      return this.<String>appendCommand(this.commandObjects.tsAlter(var1, var2));
   }

   public Response<Long> tsAdd(String var1, double var2) {
      return this.<Long>appendCommand(this.commandObjects.tsAdd(var1, var2));
   }

   public Response<Long> tsAdd(String var1, long var2, double var4) {
      return this.<Long>appendCommand(this.commandObjects.tsAdd(var1, var2, var4));
   }

   public Response<Long> tsAdd(String var1, long var2, double var4, TSCreateParams var6) {
      return this.<Long>appendCommand(this.commandObjects.tsAdd(var1, var2, var4, var6));
   }

   public Response<List<Long>> tsMAdd(Map.Entry<String, TSElement>... var1) {
      return this.<List<Long>>appendCommand(this.commandObjects.tsMAdd(var1));
   }

   public Response<Long> tsIncrBy(String var1, double var2) {
      return this.<Long>appendCommand(this.commandObjects.tsIncrBy(var1, var2));
   }

   public Response<Long> tsIncrBy(String var1, double var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.tsIncrBy(var1, var2, var4));
   }

   public Response<Long> tsDecrBy(String var1, double var2) {
      return this.<Long>appendCommand(this.commandObjects.tsDecrBy(var1, var2));
   }

   public Response<Long> tsDecrBy(String var1, double var2, long var4) {
      return this.<Long>appendCommand(this.commandObjects.tsDecrBy(var1, var2, var4));
   }

   public Response<List<TSElement>> tsRange(String var1, long var2, long var4) {
      return this.<List<TSElement>>appendCommand(this.commandObjects.tsRange(var1, var2, var4));
   }

   public Response<List<TSElement>> tsRange(String var1, TSRangeParams var2) {
      return this.<List<TSElement>>appendCommand(this.commandObjects.tsRange(var1, var2));
   }

   public Response<List<TSElement>> tsRevRange(String var1, long var2, long var4) {
      return this.<List<TSElement>>appendCommand(this.commandObjects.tsRevRange(var1, var2, var4));
   }

   public Response<List<TSElement>> tsRevRange(String var1, TSRangeParams var2) {
      return this.<List<TSElement>>appendCommand(this.commandObjects.tsRevRange(var1, var2));
   }

   public Response<Map<String, TSMRangeElements>> tsMRange(long var1, long var3, String... var5) {
      return this.<Map<String, TSMRangeElements>>appendCommand(this.commandObjects.tsMRange(var1, var3, var5));
   }

   public Response<Map<String, TSMRangeElements>> tsMRange(TSMRangeParams var1) {
      return this.<Map<String, TSMRangeElements>>appendCommand(this.commandObjects.tsMRange(var1));
   }

   public Response<Map<String, TSMRangeElements>> tsMRevRange(long var1, long var3, String... var5) {
      return this.<Map<String, TSMRangeElements>>appendCommand(this.commandObjects.tsMRevRange(var1, var3, var5));
   }

   public Response<Map<String, TSMRangeElements>> tsMRevRange(TSMRangeParams var1) {
      return this.<Map<String, TSMRangeElements>>appendCommand(this.commandObjects.tsMRevRange(var1));
   }

   public Response<TSElement> tsGet(String var1) {
      return this.<TSElement>appendCommand(this.commandObjects.tsGet(var1));
   }

   public Response<TSElement> tsGet(String var1, TSGetParams var2) {
      return this.<TSElement>appendCommand(this.commandObjects.tsGet(var1, var2));
   }

   public Response<Map<String, TSMGetElement>> tsMGet(TSMGetParams var1, String... var2) {
      return this.<Map<String, TSMGetElement>>appendCommand(this.commandObjects.tsMGet(var1, var2));
   }

   public Response<String> tsCreateRule(String var1, String var2, AggregationType var3, long var4) {
      return this.<String>appendCommand(this.commandObjects.tsCreateRule(var1, var2, var3, var4));
   }

   public Response<String> tsCreateRule(String var1, String var2, AggregationType var3, long var4, long var6) {
      return this.<String>appendCommand(this.commandObjects.tsCreateRule(var1, var2, var3, var4, var6));
   }

   public Response<String> tsDeleteRule(String var1, String var2) {
      return this.<String>appendCommand(this.commandObjects.tsDeleteRule(var1, var2));
   }

   public Response<List<String>> tsQueryIndex(String... var1) {
      return this.<List<String>>appendCommand(this.commandObjects.tsQueryIndex(var1));
   }

   public Response<String> bfReserve(String var1, double var2, long var4) {
      return this.<String>appendCommand(this.commandObjects.bfReserve(var1, var2, var4));
   }

   public Response<String> bfReserve(String var1, double var2, long var4, BFReserveParams var6) {
      return this.<String>appendCommand(this.commandObjects.bfReserve(var1, var2, var4, var6));
   }

   public Response<Boolean> bfAdd(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.bfAdd(var1, var2));
   }

   public Response<List<Boolean>> bfMAdd(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.bfMAdd(var1, var2));
   }

   public Response<List<Boolean>> bfInsert(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.bfInsert(var1, var2));
   }

   public Response<List<Boolean>> bfInsert(String var1, BFInsertParams var2, String... var3) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.bfInsert(var1, var2, var3));
   }

   public Response<Boolean> bfExists(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.bfExists(var1, var2));
   }

   public Response<List<Boolean>> bfMExists(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.bfMExists(var1, var2));
   }

   public Response<Map.Entry<Long, byte[]>> bfScanDump(String var1, long var2) {
      return this.<Map.Entry<Long, byte[]>>appendCommand(this.commandObjects.bfScanDump(var1, var2));
   }

   public Response<String> bfLoadChunk(String var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.bfLoadChunk(var1, var2, var4));
   }

   public Response<Long> bfCard(String var1) {
      return this.<Long>appendCommand(this.commandObjects.bfCard(var1));
   }

   public Response<Map<String, Object>> bfInfo(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.bfInfo(var1));
   }

   public Response<String> cfReserve(String var1, long var2) {
      return this.<String>appendCommand(this.commandObjects.cfReserve(var1, var2));
   }

   public Response<String> cfReserve(String var1, long var2, CFReserveParams var4) {
      return this.<String>appendCommand(this.commandObjects.cfReserve(var1, var2, var4));
   }

   public Response<Boolean> cfAdd(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.cfAdd(var1, var2));
   }

   public Response<Boolean> cfAddNx(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.cfAddNx(var1, var2));
   }

   public Response<List<Boolean>> cfInsert(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.cfInsert(var1, var2));
   }

   public Response<List<Boolean>> cfInsert(String var1, CFInsertParams var2, String... var3) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.cfInsert(var1, var2, var3));
   }

   public Response<List<Boolean>> cfInsertNx(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.cfInsertNx(var1, var2));
   }

   public Response<List<Boolean>> cfInsertNx(String var1, CFInsertParams var2, String... var3) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.cfInsertNx(var1, var2, var3));
   }

   public Response<Boolean> cfExists(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.cfExists(var1, var2));
   }

   public Response<Boolean> cfDel(String var1, String var2) {
      return this.<Boolean>appendCommand(this.commandObjects.cfDel(var1, var2));
   }

   public Response<Long> cfCount(String var1, String var2) {
      return this.<Long>appendCommand(this.commandObjects.cfCount(var1, var2));
   }

   public Response<Map.Entry<Long, byte[]>> cfScanDump(String var1, long var2) {
      return this.<Map.Entry<Long, byte[]>>appendCommand(this.commandObjects.cfScanDump(var1, var2));
   }

   public Response<String> cfLoadChunk(String var1, long var2, byte[] var4) {
      return this.<String>appendCommand(this.commandObjects.cfLoadChunk(var1, var2, var4));
   }

   public Response<Map<String, Object>> cfInfo(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.cfInfo(var1));
   }

   public Response<String> cmsInitByDim(String var1, long var2, long var4) {
      return this.<String>appendCommand(this.commandObjects.cmsInitByDim(var1, var2, var4));
   }

   public Response<String> cmsInitByProb(String var1, double var2, double var4) {
      return this.<String>appendCommand(this.commandObjects.cmsInitByProb(var1, var2, var4));
   }

   public Response<List<Long>> cmsIncrBy(String var1, Map<String, Long> var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.cmsIncrBy(var1, var2));
   }

   public Response<List<Long>> cmsQuery(String var1, String... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.cmsQuery(var1, var2));
   }

   public Response<String> cmsMerge(String var1, String... var2) {
      return this.<String>appendCommand(this.commandObjects.cmsMerge(var1, var2));
   }

   public Response<String> cmsMerge(String var1, Map<String, Long> var2) {
      return this.<String>appendCommand(this.commandObjects.cmsMerge(var1, var2));
   }

   public Response<Map<String, Object>> cmsInfo(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.cmsInfo(var1));
   }

   public Response<String> topkReserve(String var1, long var2) {
      return this.<String>appendCommand(this.commandObjects.topkReserve(var1, var2));
   }

   public Response<String> topkReserve(String var1, long var2, long var4, long var6, double var8) {
      return this.<String>appendCommand(this.commandObjects.topkReserve(var1, var2, var4, var6, var8));
   }

   public Response<List<String>> topkAdd(String var1, String... var2) {
      return this.<List<String>>appendCommand(this.commandObjects.topkAdd(var1, var2));
   }

   public Response<List<String>> topkIncrBy(String var1, Map<String, Long> var2) {
      return this.<List<String>>appendCommand(this.commandObjects.topkIncrBy(var1, var2));
   }

   public Response<List<Boolean>> topkQuery(String var1, String... var2) {
      return this.<List<Boolean>>appendCommand(this.commandObjects.topkQuery(var1, var2));
   }

   public Response<List<String>> topkList(String var1) {
      return this.<List<String>>appendCommand(this.commandObjects.topkList(var1));
   }

   public Response<Map<String, Long>> topkListWithCount(String var1) {
      return this.<Map<String, Long>>appendCommand(this.commandObjects.topkListWithCount(var1));
   }

   public Response<Map<String, Object>> topkInfo(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.topkInfo(var1));
   }

   public Response<String> tdigestCreate(String var1) {
      return this.<String>appendCommand(this.commandObjects.tdigestCreate(var1));
   }

   public Response<String> tdigestCreate(String var1, int var2) {
      return this.<String>appendCommand(this.commandObjects.tdigestCreate(var1, var2));
   }

   public Response<String> tdigestReset(String var1) {
      return this.<String>appendCommand(this.commandObjects.tdigestReset(var1));
   }

   public Response<String> tdigestMerge(String var1, String... var2) {
      return this.<String>appendCommand(this.commandObjects.tdigestMerge(var1, var2));
   }

   public Response<String> tdigestMerge(TDigestMergeParams var1, String var2, String... var3) {
      return this.<String>appendCommand(this.commandObjects.tdigestMerge(var1, var2, var3));
   }

   public Response<Map<String, Object>> tdigestInfo(String var1) {
      return this.<Map<String, Object>>appendCommand(this.commandObjects.tdigestInfo(var1));
   }

   public Response<String> tdigestAdd(String var1, double... var2) {
      return this.<String>appendCommand(this.commandObjects.tdigestAdd(var1, var2));
   }

   public Response<List<Double>> tdigestCDF(String var1, double... var2) {
      return this.<List<Double>>appendCommand(this.commandObjects.tdigestCDF(var1, var2));
   }

   public Response<List<Double>> tdigestQuantile(String var1, double... var2) {
      return this.<List<Double>>appendCommand(this.commandObjects.tdigestQuantile(var1, var2));
   }

   public Response<Double> tdigestMin(String var1) {
      return this.<Double>appendCommand(this.commandObjects.tdigestMin(var1));
   }

   public Response<Double> tdigestMax(String var1) {
      return this.<Double>appendCommand(this.commandObjects.tdigestMax(var1));
   }

   public Response<Double> tdigestTrimmedMean(String var1, double var2, double var4) {
      return this.<Double>appendCommand(this.commandObjects.tdigestTrimmedMean(var1, var2, var4));
   }

   public Response<List<Long>> tdigestRank(String var1, double... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.tdigestRank(var1, var2));
   }

   public Response<List<Long>> tdigestRevRank(String var1, double... var2) {
      return this.<List<Long>>appendCommand(this.commandObjects.tdigestRevRank(var1, var2));
   }

   public Response<List<Double>> tdigestByRank(String var1, long... var2) {
      return this.<List<Double>>appendCommand(this.commandObjects.tdigestByRank(var1, var2));
   }

   public Response<List<Double>> tdigestByRevRank(String var1, long... var2) {
      return this.<List<Double>>appendCommand(this.commandObjects.tdigestByRevRank(var1, var2));
   }

   public Response<ResultSet> graphQuery(String var1, String var2) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphQuery(var1, var2));
   }

   public Response<ResultSet> graphReadonlyQuery(String var1, String var2) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphReadonlyQuery(var1, var2));
   }

   public Response<ResultSet> graphQuery(String var1, String var2, long var3) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphQuery(var1, var2, var3));
   }

   public Response<ResultSet> graphReadonlyQuery(String var1, String var2, long var3) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphReadonlyQuery(var1, var2, var3));
   }

   public Response<ResultSet> graphQuery(String var1, String var2, Map<String, Object> var3) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphQuery(var1, var2, var3));
   }

   public Response<ResultSet> graphReadonlyQuery(String var1, String var2, Map<String, Object> var3) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphReadonlyQuery(var1, var2, var3));
   }

   public Response<ResultSet> graphQuery(String var1, String var2, Map<String, Object> var3, long var4) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphQuery(var1, var2, var3, var4));
   }

   public Response<ResultSet> graphReadonlyQuery(String var1, String var2, Map<String, Object> var3, long var4) {
      return this.<ResultSet>appendCommand(this.graphCommandObjects.graphReadonlyQuery(var1, var2, var3, var4));
   }

   public Response<String> graphDelete(String var1) {
      return this.<String>appendCommand(this.graphCommandObjects.graphDelete(var1));
   }

   public Response<List<String>> graphProfile(String var1, String var2) {
      return this.<List<String>>appendCommand(this.commandObjects.graphProfile(var1, var2));
   }

   public Response<Object> sendCommand(ProtocolCommand var1, String... var2) {
      return this.sendCommand((new CommandArguments(var1)).addObjects(var2));
   }

   public Response<Object> sendCommand(ProtocolCommand var1, byte[]... var2) {
      return this.sendCommand((new CommandArguments(var1)).addObjects(var2));
   }

   public Response<Object> sendCommand(CommandArguments var1) {
      return this.<Object>executeCommand(new CommandObject(var1, BuilderFactory.RAW_OBJECT));
   }

   public <T> Response<T> executeCommand(CommandObject<T> var1) {
      return this.<T>appendCommand(var1);
   }

   public void setJsonObjectMapper(JsonObjectMapper var1) {
      this.commandObjects.setJsonObjectMapper(var1);
   }
}
