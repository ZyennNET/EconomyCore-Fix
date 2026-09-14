package redis.clients.jedis;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisAccessControlException;
import redis.clients.jedis.exceptions.JedisAskDataException;
import redis.clients.jedis.exceptions.JedisBusyException;
import redis.clients.jedis.exceptions.JedisClusterException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisMovedDataException;
import redis.clients.jedis.exceptions.JedisNoScriptException;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.RedisInputStream;
import redis.clients.jedis.util.RedisOutputStream;
import redis.clients.jedis.util.SafeEncoder;

public final class Protocol {
   public static final String DEFAULT_HOST = "127.0.0.1";
   public static final int DEFAULT_PORT = 6379;
   public static final int DEFAULT_SENTINEL_PORT = 26379;
   public static final int DEFAULT_TIMEOUT = 2000;
   public static final int DEFAULT_DATABASE = 0;
   public static final int CLUSTER_HASHSLOTS = 16384;
   public static final Charset CHARSET;
   public static final byte ASTERISK_BYTE = 42;
   public static final byte COLON_BYTE = 58;
   public static final byte COMMA_BYTE = 44;
   public static final byte DOLLAR_BYTE = 36;
   public static final byte EQUAL_BYTE = 61;
   public static final byte GREATER_THAN_BYTE = 62;
   public static final byte HASH_BYTE = 35;
   public static final byte LEFT_BRACE_BYTE = 40;
   public static final byte MINUS_BYTE = 45;
   public static final byte PERCENT_BYTE = 37;
   public static final byte PLUS_BYTE = 43;
   public static final byte TILDE_BYTE = 126;
   public static final byte UNDERSCORE_BYTE = 95;
   public static final byte[] BYTES_TRUE;
   public static final byte[] BYTES_FALSE;
   public static final byte[] BYTES_TILDE;
   public static final byte[] BYTES_EQUAL;
   public static final byte[] BYTES_ASTERISK;
   public static final byte[] POSITIVE_INFINITY_BYTES;
   public static final byte[] NEGATIVE_INFINITY_BYTES;
   private static final String ASK_PREFIX = "ASK ";
   private static final String MOVED_PREFIX = "MOVED ";
   private static final String CLUSTERDOWN_PREFIX = "CLUSTERDOWN ";
   private static final String BUSY_PREFIX = "BUSY ";
   private static final String NOSCRIPT_PREFIX = "NOSCRIPT ";
   private static final String WRONGPASS_PREFIX = "WRONGPASS";
   private static final String NOPERM_PREFIX = "NOPERM";

   private Protocol() {
      throw new InstantiationError("Must not instantiate this class");
   }

   public static void sendCommand(RedisOutputStream var0, CommandArguments var1) {
      try {
         var0.write((byte)42);
         var0.writeIntCrLf(var1.size());

         for(Rawable var3 : var1) {
            var0.write((byte)36);
            byte[] var4 = var3.getRaw();
            var0.writeIntCrLf(var4.length);
            var0.write(var4);
            var0.writeCrLf();
         }

      } catch (IOException var5) {
         throw new JedisConnectionException(var5);
      }
   }

   private static void processError(RedisInputStream var0) {
      String var1 = var0.readLine();
      if (var1.startsWith("MOVED ")) {
         String[] var3 = parseTargetHostAndSlot(var1);
         throw new JedisMovedDataException(var1, HostAndPort.from(var3[1]), Integer.parseInt(var3[0]));
      } else if (var1.startsWith("ASK ")) {
         String[] var2 = parseTargetHostAndSlot(var1);
         throw new JedisAskDataException(var1, HostAndPort.from(var2[1]), Integer.parseInt(var2[0]));
      } else if (var1.startsWith("CLUSTERDOWN ")) {
         throw new JedisClusterException(var1);
      } else if (var1.startsWith("BUSY ")) {
         throw new JedisBusyException(var1);
      } else if (var1.startsWith("NOSCRIPT ")) {
         throw new JedisNoScriptException(var1);
      } else if (var1.startsWith("WRONGPASS")) {
         throw new JedisAccessControlException(var1);
      } else if (var1.startsWith("NOPERM")) {
         throw new JedisAccessControlException(var1);
      } else {
         throw new JedisDataException(var1);
      }
   }

   public static String readErrorLineIfPossible(RedisInputStream var0) {
      byte var1 = var0.readByte();
      return var1 != 45 ? null : var0.readLine();
   }

   private static String[] parseTargetHostAndSlot(String var0) {
      String[] var1 = new String[2];
      String[] var2 = var0.split(" ");
      var1[0] = var2[1];
      var1[1] = var2[2];
      return var1;
   }

   private static Object process(RedisInputStream var0) {
      byte var1 = var0.readByte();
      switch (var1) {
         case 35:
            return var0.readBooleanCrLf();
         case 36:
         case 61:
            return processBulkReply(var0);
         case 37:
            return processMapKeyValueReply(var0);
         case 40:
            return var0.readBigIntegerCrLf();
         case 42:
            return processMultiBulkReply(var0);
         case 43:
            return var0.readLineBytes();
         case 44:
            return var0.readDoubleCrLf();
         case 45:
            processError(var0);
            return null;
         case 58:
            return var0.readLongCrLf();
         case 62:
            return processMultiBulkReply(var0);
         case 95:
            return var0.readNullCrLf();
         case 126:
            return processMultiBulkReply(var0);
         default:
            throw new JedisConnectionException("Unknown reply: " + (char)var1);
      }
   }

   private static byte[] processBulkReply(RedisInputStream var0) {
      int var1 = var0.readIntCrLf();
      if (var1 == -1) {
         return null;
      } else {
         byte[] var2 = new byte[var1];

         int var4;
         for(int var3 = 0; var3 < var1; var3 += var4) {
            var4 = var0.read(var2, var3, var1 - var3);
            if (var4 == -1) {
               throw new JedisConnectionException("It seems like server has closed the connection.");
            }
         }

         var0.readByte();
         var0.readByte();
         return var2;
      }
   }

   private static List<Object> processMultiBulkReply(RedisInputStream var0) {
      int var1 = var0.readIntCrLf();
      if (var1 == -1) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            try {
               var2.add(process(var0));
            } catch (JedisDataException var5) {
               var2.add(var5);
            }
         }

         return var2;
      }
   }

   private static List<KeyValue> processMapKeyValueReply(RedisInputStream var0) {
      int var1 = var0.readIntCrLf();
      if (var1 == -1) {
         return null;
      } else {
         ArrayList var2 = new ArrayList(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.add(new KeyValue(process(var0), process(var0)));
         }

         return var2;
      }
   }

   public static Object read(RedisInputStream var0) {
      return process(var0);
   }

   public static final byte[] toByteArray(boolean var0) {
      return var0 ? BYTES_TRUE : BYTES_FALSE;
   }

   public static final byte[] toByteArray(int var0) {
      return SafeEncoder.encode(String.valueOf(var0));
   }

   public static final byte[] toByteArray(long var0) {
      return SafeEncoder.encode(String.valueOf(var0));
   }

   public static final byte[] toByteArray(double var0) {
      if (var0 == Double.POSITIVE_INFINITY) {
         return POSITIVE_INFINITY_BYTES;
      } else {
         return var0 == Double.NEGATIVE_INFINITY ? NEGATIVE_INFINITY_BYTES : SafeEncoder.encode(String.valueOf(var0));
      }
   }

   static {
      CHARSET = StandardCharsets.UTF_8;
      BYTES_TRUE = toByteArray(1);
      BYTES_FALSE = toByteArray(0);
      BYTES_TILDE = SafeEncoder.encode("~");
      BYTES_EQUAL = SafeEncoder.encode("=");
      BYTES_ASTERISK = SafeEncoder.encode("*");
      POSITIVE_INFINITY_BYTES = "+inf".getBytes();
      NEGATIVE_INFINITY_BYTES = "-inf".getBytes();
   }

   public static enum ClusterKeyword implements Rawable {
      MEET,
      RESET,
      INFO,
      FAILOVER,
      SLOTS,
      NODES,
      REPLICAS,
      SLAVES,
      MYID,
      ADDSLOTS,
      DELSLOTS,
      GETKEYSINSLOT,
      SETSLOT,
      NODE,
      MIGRATING,
      IMPORTING,
      STABLE,
      FORGET,
      FLUSHSLOTS,
      KEYSLOT,
      COUNTKEYSINSLOT,
      SAVECONFIG,
      REPLICATE,
      LINKS,
      ADDSLOTSRANGE,
      DELSLOTSRANGE,
      BUMPEPOCH,
      MYSHARDID,
      SHARDS;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum Command implements ProtocolCommand {
      PING,
      AUTH,
      HELLO,
      SET,
      GET,
      GETDEL,
      GETEX,
      EXISTS,
      DEL,
      UNLINK,
      TYPE,
      FLUSHDB,
      FLUSHALL,
      MOVE,
      KEYS,
      RANDOMKEY,
      RENAME,
      RENAMENX,
      DUMP,
      RESTORE,
      DBSIZE,
      SELECT,
      SWAPDB,
      MIGRATE,
      ECHO,
      EXPIRE,
      EXPIREAT,
      EXPIRETIME,
      PEXPIRE,
      PEXPIREAT,
      PEXPIRETIME,
      TTL,
      PTTL,
      MULTI,
      DISCARD,
      EXEC,
      WATCH,
      UNWATCH,
      SORT,
      SORT_RO,
      INFO,
      SHUTDOWN,
      MONITOR,
      CONFIG,
      LCS,
      GETSET,
      MGET,
      SETNX,
      SETEX,
      PSETEX,
      MSET,
      MSETNX,
      DECR,
      DECRBY,
      INCR,
      INCRBY,
      INCRBYFLOAT,
      STRLEN,
      APPEND,
      SUBSTR,
      SETBIT,
      GETBIT,
      BITPOS,
      SETRANGE,
      GETRANGE,
      BITCOUNT,
      BITOP,
      BITFIELD,
      BITFIELD_RO,
      HSET,
      HGET,
      HSETNX,
      HMSET,
      HMGET,
      HINCRBY,
      HEXISTS,
      HDEL,
      HLEN,
      HKEYS,
      HVALS,
      HGETALL,
      HSTRLEN,
      HRANDFIELD,
      HINCRBYFLOAT,
      RPUSH,
      LPUSH,
      LLEN,
      LRANGE,
      LTRIM,
      LINDEX,
      LSET,
      LREM,
      LPOP,
      RPOP,
      BLPOP,
      BRPOP,
      LINSERT,
      LPOS,
      RPOPLPUSH,
      BRPOPLPUSH,
      BLMOVE,
      LMOVE,
      LMPOP,
      BLMPOP,
      LPUSHX,
      RPUSHX,
      SADD,
      SMEMBERS,
      SREM,
      SPOP,
      SMOVE,
      SCARD,
      SRANDMEMBER,
      SINTER,
      SINTERSTORE,
      SUNION,
      SUNIONSTORE,
      SDIFF,
      SDIFFSTORE,
      SISMEMBER,
      SMISMEMBER,
      SINTERCARD,
      ZADD,
      ZDIFF,
      ZDIFFSTORE,
      ZRANGE,
      ZREM,
      ZINCRBY,
      ZRANK,
      ZREVRANK,
      ZREVRANGE,
      ZRANDMEMBER,
      ZCARD,
      ZSCORE,
      ZPOPMAX,
      ZPOPMIN,
      ZCOUNT,
      ZUNION,
      ZUNIONSTORE,
      ZINTER,
      ZINTERSTORE,
      ZRANGEBYSCORE,
      ZREVRANGEBYSCORE,
      ZREMRANGEBYRANK,
      ZREMRANGEBYSCORE,
      ZLEXCOUNT,
      ZRANGEBYLEX,
      ZREVRANGEBYLEX,
      ZREMRANGEBYLEX,
      ZMSCORE,
      ZRANGESTORE,
      ZINTERCARD,
      ZMPOP,
      BZMPOP,
      BZPOPMIN,
      BZPOPMAX,
      GEOADD,
      GEODIST,
      GEOHASH,
      GEOPOS,
      GEORADIUS,
      GEORADIUS_RO,
      GEOSEARCH,
      GEOSEARCHSTORE,
      GEORADIUSBYMEMBER,
      GEORADIUSBYMEMBER_RO,
      PFADD,
      PFCOUNT,
      PFMERGE,
      XADD,
      XLEN,
      XDEL,
      XTRIM,
      XRANGE,
      XREVRANGE,
      XREAD,
      XACK,
      XGROUP,
      XREADGROUP,
      XPENDING,
      XCLAIM,
      XAUTOCLAIM,
      XINFO,
      EVAL,
      EVALSHA,
      SCRIPT,
      EVAL_RO,
      EVALSHA_RO,
      FUNCTION,
      FCALL,
      FCALL_RO,
      SUBSCRIBE,
      UNSUBSCRIBE,
      PSUBSCRIBE,
      PUNSUBSCRIBE,
      PUBLISH,
      PUBSUB,
      SSUBSCRIBE,
      SUNSUBSCRIBE,
      SPUBLISH,
      SAVE,
      BGSAVE,
      BGREWRITEAOF,
      LASTSAVE,
      PERSIST,
      ROLE,
      FAILOVER,
      SLOWLOG,
      OBJECT,
      CLIENT,
      TIME,
      SCAN,
      HSCAN,
      SSCAN,
      ZSCAN,
      WAIT,
      CLUSTER,
      ASKING,
      READONLY,
      READWRITE,
      SLAVEOF,
      REPLICAOF,
      COPY,
      SENTINEL,
      MODULE,
      ACL,
      TOUCH,
      MEMORY,
      LOLWUT,
      COMMAND,
      RESET,
      LATENCY,
      WAITAOF;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum Keyword implements Rawable {
      AGGREGATE,
      ALPHA,
      BY,
      GET,
      LIMIT,
      NO,
      NOSORT,
      ONE,
      SET,
      STORE,
      WEIGHTS,
      WITHSCORE,
      WITHSCORES,
      RESETSTAT,
      REWRITE,
      RESET,
      FLUSH,
      EXISTS,
      LOAD,
      LEN,
      HELP,
      SCHEDULE,
      MATCH,
      COUNT,
      TYPE,
      KEYS,
      REFCOUNT,
      ENCODING,
      IDLETIME,
      FREQ,
      REPLACE,
      GETNAME,
      SETNAME,
      SETINFO,
      LIST,
      ID,
      KILL,
      PERSIST,
      STREAMS,
      CREATE,
      MKSTREAM,
      SETID,
      DESTROY,
      DELCONSUMER,
      MAXLEN,
      GROUP,
      IDLE,
      TIME,
      BLOCK,
      NOACK,
      RETRYCOUNT,
      STREAM,
      GROUPS,
      CONSUMERS,
      JUSTID,
      WITHVALUES,
      NOMKSTREAM,
      MINID,
      CREATECONSUMER,
      SETUSER,
      GETUSER,
      DELUSER,
      WHOAMI,
      USERS,
      CAT,
      GENPASS,
      LOG,
      SAVE,
      DRYRUN,
      COPY,
      AUTH,
      AUTH2,
      NX,
      XX,
      EX,
      PX,
      EXAT,
      PXAT,
      ABSTTL,
      KEEPTTL,
      INCR,
      LT,
      GT,
      CH,
      INFO,
      PAUSE,
      UNPAUSE,
      UNBLOCK,
      REV,
      WITHCOORD,
      WITHDIST,
      WITHHASH,
      ANY,
      FROMMEMBER,
      FROMLONLAT,
      BYRADIUS,
      BYBOX,
      BYLEX,
      BYSCORE,
      STOREDIST,
      TO,
      FORCE,
      TIMEOUT,
      DB,
      UNLOAD,
      ABORT,
      IDX,
      MINMATCHLEN,
      WITHMATCHLEN,
      FULL,
      DELETE,
      LIBRARYNAME,
      WITHCODE,
      DESCRIPTION,
      GETKEYS,
      GETKEYSANDFLAGS,
      DOCS,
      FILTERBY,
      DUMP,
      MODULE,
      ACLCAT,
      PATTERN,
      DOCTOR,
      USAGE,
      SAMPLES,
      PURGE,
      STATS,
      LOADEX,
      CONFIG,
      ARGS,
      RANK,
      NOW,
      VERSION,
      ADDR,
      SKIPME,
      USER,
      LADDR,
      CHANNELS,
      NUMPAT,
      NUMSUB,
      SHARDCHANNELS,
      SHARDNUMSUB;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum ResponseKeyword implements Rawable {
      SUBSCRIBE,
      PSUBSCRIBE,
      UNSUBSCRIBE,
      PUNSUBSCRIBE,
      MESSAGE,
      PMESSAGE,
      PONG,
      SSUBSCRIBE,
      SUNSUBSCRIBE,
      SMESSAGE;

      private final byte[] raw;

      private ResponseKeyword() {
         this.raw = SafeEncoder.encode(this.name().toLowerCase(Locale.ENGLISH));
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum SentinelKeyword implements Rawable {
      MYID,
      MASTERS,
      MASTER,
      SENTINELS,
      SLAVES,
      REPLICAS,
      RESET,
      FAILOVER,
      REMOVE,
      SET,
      MONITOR,
      GET_MASTER_ADDR_BY_NAME("GET-MASTER-ADDR-BY-NAME");

      private final byte[] raw;

      private SentinelKeyword() {
         this.raw = SafeEncoder.encode(this.name());
      }

      private SentinelKeyword(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
