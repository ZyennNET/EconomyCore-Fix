package redis.clients.jedis;

import java.io.Closeable;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import redis.clients.jedis.args.BitCountOption;
import redis.clients.jedis.args.BitOP;
import redis.clients.jedis.args.ClientAttributeOption;
import redis.clients.jedis.args.ClientPauseMode;
import redis.clients.jedis.args.ClientType;
import redis.clients.jedis.args.ClusterFailoverOption;
import redis.clients.jedis.args.ClusterResetType;
import redis.clients.jedis.args.ExpiryOption;
import redis.clients.jedis.args.FlushMode;
import redis.clients.jedis.args.FunctionRestorePolicy;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.args.ListDirection;
import redis.clients.jedis.args.ListPosition;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.SortedSetOption;
import redis.clients.jedis.args.UnblockType;
import redis.clients.jedis.commands.ClusterCommands;
import redis.clients.jedis.commands.ControlBinaryCommands;
import redis.clients.jedis.commands.ControlCommands;
import redis.clients.jedis.commands.DatabaseCommands;
import redis.clients.jedis.commands.GenericControlCommands;
import redis.clients.jedis.commands.JedisBinaryCommands;
import redis.clients.jedis.commands.JedisCommands;
import redis.clients.jedis.commands.ModuleCommands;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.commands.SentinelCommands;
import redis.clients.jedis.commands.ServerCommands;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.BitPosParams;
import redis.clients.jedis.params.ClientKillParams;
import redis.clients.jedis.params.CommandListFilterByParams;
import redis.clients.jedis.params.FailoverParams;
import redis.clients.jedis.params.GeoAddParams;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.GeoRadiusStoreParam;
import redis.clients.jedis.params.GeoSearchParam;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.LCSParams;
import redis.clients.jedis.params.LPosParams;
import redis.clients.jedis.params.LolwutParams;
import redis.clients.jedis.params.MigrateParams;
import redis.clients.jedis.params.ModuleLoadExParams;
import redis.clients.jedis.params.RestoreParams;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ShutdownParams;
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
import redis.clients.jedis.resps.AccessControlLogEntry;
import redis.clients.jedis.resps.AccessControlUser;
import redis.clients.jedis.resps.ClusterShardInfo;
import redis.clients.jedis.resps.CommandDocument;
import redis.clients.jedis.resps.CommandInfo;
import redis.clients.jedis.resps.FunctionStats;
import redis.clients.jedis.resps.GeoRadiusResponse;
import redis.clients.jedis.resps.LCSMatchResult;
import redis.clients.jedis.resps.LibraryInfo;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.Slowlog;
import redis.clients.jedis.resps.StreamConsumerInfo;
import redis.clients.jedis.resps.StreamConsumersInfo;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamFullInfo;
import redis.clients.jedis.resps.StreamGroupInfo;
import redis.clients.jedis.resps.StreamInfo;
import redis.clients.jedis.resps.StreamPendingEntry;
import redis.clients.jedis.resps.StreamPendingSummary;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.util.JedisURIHelper;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.Pool;
import redis.clients.jedis.util.SafeEncoder;

public class Jedis implements ServerCommands, DatabaseCommands, JedisCommands, JedisBinaryCommands, ControlCommands, ControlBinaryCommands, ClusterCommands, ModuleCommands, GenericControlCommands, SentinelCommands, Closeable {
   protected final Connection connection;
   private final CommandObjects commandObjects;
   private int db;
   private Transaction transaction;
   private boolean isInMulti;
   private boolean isInWatch;
   private Pipeline pipeline;
   protected static final byte[][] DUMMY_ARRAY = new byte[0][];
   private Pool<Jedis> dataSource;

   public Jedis() {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = new Connection();
   }

   public Jedis(String var1) {
      this(URI.create(var1));
   }

   public Jedis(HostAndPort var1) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = new Connection(var1);
   }

   public Jedis(String var1, int var2) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = new Connection(var1, var2);
   }

   public Jedis(String var1, int var2, JedisClientConfig var3) {
      this(new HostAndPort(var1, var2), var3);
   }

   public Jedis(HostAndPort var1, JedisClientConfig var2) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = new Connection(var1, var2);
      RedisProtocol var3 = var2.getRedisProtocol();
      if (var3 != null) {
         this.commandObjects.setProtocol(var3);
      }

   }

   public Jedis(String var1, int var2, boolean var3) {
      this(var1, var2, DefaultJedisClientConfig.builder().ssl(var3).build());
   }

   public Jedis(String var1, int var2, boolean var3, SSLSocketFactory var4, SSLParameters var5, HostnameVerifier var6) {
      this(var1, var2, DefaultJedisClientConfig.builder().ssl(var3).sslSocketFactory(var4).sslParameters(var5).hostnameVerifier(var6).build());
   }

   public Jedis(String var1, int var2, int var3) {
      this(var1, var2, var3, var3);
   }

   public Jedis(String var1, int var2, int var3, boolean var4) {
      this(var1, var2, var3, var3, var4);
   }

   public Jedis(String var1, int var2, int var3, boolean var4, SSLSocketFactory var5, SSLParameters var6, HostnameVerifier var7) {
      this(var1, var2, var3, var3, var4, var5, var6, var7);
   }

   public Jedis(String var1, int var2, int var3, int var4) {
      this(var1, var2, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var3).socketTimeoutMillis(var4).build());
   }

   public Jedis(String var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var3).socketTimeoutMillis(var4).blockingSocketTimeoutMillis(var5).build());
   }

   public Jedis(String var1, int var2, int var3, int var4, boolean var5) {
      this(var1, var2, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var3).socketTimeoutMillis(var4).ssl(var5).build());
   }

   public Jedis(String var1, int var2, int var3, int var4, boolean var5, SSLSocketFactory var6, SSLParameters var7, HostnameVerifier var8) {
      this(var1, var2, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var3).socketTimeoutMillis(var4).ssl(var5).sslSocketFactory(var6).sslParameters(var7).hostnameVerifier(var8).build());
   }

   public Jedis(String var1, int var2, int var3, int var4, int var5, boolean var6, SSLSocketFactory var7, SSLParameters var8, HostnameVerifier var9) {
      this(var1, var2, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var3).socketTimeoutMillis(var4).blockingSocketTimeoutMillis(var5).ssl(var6).sslSocketFactory(var7).sslParameters(var8).hostnameVerifier(var9).build());
   }

   public Jedis(URI var1) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      if (!JedisURIHelper.isValid(var1)) {
         throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI \"%s\".", var1.toString()));
      } else {
         this.connection = new Connection(new HostAndPort(var1.getHost(), var1.getPort()), DefaultJedisClientConfig.builder().user(JedisURIHelper.getUser(var1)).password(JedisURIHelper.getPassword(var1)).database(JedisURIHelper.getDBIndex(var1)).protocol(JedisURIHelper.getRedisProtocol(var1)).ssl(JedisURIHelper.isRedisSSLScheme(var1)).build());
      }
   }

   public Jedis(URI var1, SSLSocketFactory var2, SSLParameters var3, HostnameVerifier var4) {
      this((URI)var1, DefaultJedisClientConfig.builder().sslSocketFactory(var2).sslParameters(var3).hostnameVerifier(var4).build());
   }

   public Jedis(URI var1, int var2) {
      this(var1, var2, var2);
   }

   public Jedis(URI var1, int var2, SSLSocketFactory var3, SSLParameters var4, HostnameVerifier var5) {
      this(var1, var2, var2, var3, var4, var5);
   }

   public Jedis(URI var1, int var2, int var3) {
      this((URI)var1, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var2).socketTimeoutMillis(var3).build());
   }

   public Jedis(URI var1, int var2, int var3, SSLSocketFactory var4, SSLParameters var5, HostnameVerifier var6) {
      this((URI)var1, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var2).socketTimeoutMillis(var3).sslSocketFactory(var4).sslParameters(var5).hostnameVerifier(var6).build());
   }

   public Jedis(URI var1, int var2, int var3, int var4, SSLSocketFactory var5, SSLParameters var6, HostnameVerifier var7) {
      this((URI)var1, DefaultJedisClientConfig.builder().connectionTimeoutMillis(var2).socketTimeoutMillis(var3).blockingSocketTimeoutMillis(var4).sslSocketFactory(var5).sslParameters(var6).hostnameVerifier(var7).build());
   }

   public Jedis(URI var1, JedisClientConfig var2) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      if (!JedisURIHelper.isValid(var1)) {
         throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI \"%s\".", var1.toString()));
      } else {
         this.connection = new Connection(new HostAndPort(var1.getHost(), var1.getPort()), DefaultJedisClientConfig.builder().connectionTimeoutMillis(var2.getConnectionTimeoutMillis()).socketTimeoutMillis(var2.getSocketTimeoutMillis()).blockingSocketTimeoutMillis(var2.getBlockingSocketTimeoutMillis()).user(JedisURIHelper.getUser(var1)).password(JedisURIHelper.getPassword(var1)).database(JedisURIHelper.getDBIndex(var1)).clientName(var2.getClientName()).protocol(JedisURIHelper.getRedisProtocol(var1)).ssl(JedisURIHelper.isRedisSSLScheme(var1)).sslSocketFactory(var2.getSslSocketFactory()).sslParameters(var2.getSslParameters()).hostnameVerifier(var2.getHostnameVerifier()).build());
         RedisProtocol var3 = var2.getRedisProtocol();
         if (var3 != null) {
            this.commandObjects.setProtocol(var3);
         }

      }
   }

   public Jedis(JedisSocketFactory var1) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = new Connection(var1);
   }

   public Jedis(JedisSocketFactory var1, JedisClientConfig var2) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = new Connection(var1, var2);
      RedisProtocol var3 = var2.getRedisProtocol();
      if (var3 != null) {
         this.commandObjects.setProtocol(var3);
      }

   }

   public Jedis(Connection var1) {
      this.commandObjects = new CommandObjects();
      this.db = 0;
      this.transaction = null;
      this.isInMulti = false;
      this.isInWatch = false;
      this.pipeline = null;
      this.dataSource = null;
      this.connection = var1;
   }

   public String toString() {
      return "Jedis{" + this.connection + '}';
   }

   public Connection getClient() {
      return this.getConnection();
   }

   public Connection getConnection() {
      return this.connection;
   }

   public void connect() {
      this.connection.connect();
   }

   public void disconnect() {
      this.connection.disconnect();
   }

   public boolean isConnected() {
      return this.connection.isConnected();
   }

   public boolean isBroken() {
      return this.connection.isBroken();
   }

   public void resetState() {
      if (this.isConnected()) {
         if (this.transaction != null) {
            this.transaction.close();
         }

         if (this.pipeline != null) {
            this.pipeline.close();
         }

         if (this.isInWatch) {
            this.connection.sendCommand((ProtocolCommand)Protocol.Command.UNWATCH);
            this.connection.getStatusCodeReply();
            this.isInWatch = false;
         }
      }

      this.transaction = null;
      this.pipeline = null;
   }

   protected void setDataSource(Pool<Jedis> var1) {
      this.dataSource = var1;
   }

   public void close() {
      if (this.dataSource != null) {
         Pool var1 = this.dataSource;
         this.dataSource = null;
         if (this.isBroken()) {
            var1.returnBrokenResource(this);
         } else {
            var1.returnResource(this);
         }
      } else {
         this.connection.close();
      }

   }

   public Transaction multi() {
      this.transaction = new Transaction(this);
      return this.transaction;
   }

   public Pipeline pipelined() {
      this.pipeline = new Pipeline(this);
      return this.pipeline;
   }

   protected void checkIsInMultiOrPipeline() {
      if (this.transaction != null) {
         throw new IllegalStateException("Cannot use Jedis when in Multi. Please use Transaction or reset jedis state.");
      } else if (this.pipeline != null && this.pipeline.hasPipelinedResponse()) {
         throw new IllegalStateException("Cannot use Jedis when in Pipeline. Please use Pipeline or reset jedis state.");
      }
   }

   public int getDB() {
      return this.db;
   }

   public String ping() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.PING);
      return this.connection.getStatusCodeReply();
   }

   public byte[] ping(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PING, (byte[][])(var1));
      return this.connection.getBinaryBulkReply();
   }

   public String select(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.SELECT, (byte[][])(Protocol.toByteArray(var1)));
      String var2 = this.connection.getStatusCodeReply();
      this.db = var1;
      return var2;
   }

   public String swapDB(int var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.SWAPDB, (byte[][])(Protocol.toByteArray(var1), Protocol.toByteArray(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String flushDB() {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.flushDB());
   }

   public String flushDB(FlushMode var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.FLUSHDB, (byte[][])(var1.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public String flushAll() {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.flushAll());
   }

   public String flushAll(FlushMode var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.FLUSHALL, (byte[][])(var1.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public boolean copy(byte[] var1, byte[] var2, int var3, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.copy(var1, var2, var3, var4));
   }

   public boolean copy(byte[] var1, byte[] var2, boolean var3) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.copy(var1, var2, var3));
   }

   public String set(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.set(var1, var2));
   }

   public String set(byte[] var1, byte[] var2, SetParams var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.set(var1, var2, var3));
   }

   public byte[] get(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.get(var1));
   }

   public byte[] setGet(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.setGet(var1, var2));
   }

   public byte[] setGet(byte[] var1, byte[] var2, SetParams var3) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.setGet(var1, var2, var3));
   }

   public byte[] getDel(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.getDel(var1));
   }

   public byte[] getEx(byte[] var1, GetExParams var2) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.getEx(var1, var2));
   }

   public long exists(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.exists(var1));
   }

   public boolean exists(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.exists(var1));
   }

   public long del(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.del(var1));
   }

   public long del(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.del(var1));
   }

   public long unlink(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.unlink(var1));
   }

   public long unlink(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.unlink(var1));
   }

   public String type(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.type(var1));
   }

   public Set<byte[]> keys(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.keys(var1));
   }

   public byte[] randomBinaryKey() {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.randomBinaryKey());
   }

   public String rename(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.rename(var1, var2));
   }

   public long renamenx(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.renamenx(var1, var2));
   }

   public long dbSize() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.DBSIZE);
      return this.connection.getIntegerReply();
   }

   public long expire(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expire(var1, var2));
   }

   public long expire(byte[] var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expire(var1, var2, var4));
   }

   public long pexpire(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpire(var1, var2));
   }

   public long pexpire(byte[] var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpire(var1, var2, var4));
   }

   public long expireTime(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expireTime(var1));
   }

   public long pexpireTime(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpireTime(var1));
   }

   public long expireAt(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expireAt(var1, var2));
   }

   public long expireAt(byte[] var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expireAt(var1, var2, var4));
   }

   public long pexpireAt(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpireAt(var1, var2));
   }

   public long pexpireAt(byte[] var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpireAt(var1, var2, var4));
   }

   public long ttl(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.ttl(var1));
   }

   public long touch(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.touch(var1));
   }

   public long touch(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.touch(var1));
   }

   public long move(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MOVE, (byte[][])(var1, Protocol.toByteArray(var2)));
      return this.connection.getIntegerReply();
   }

   public byte[] getSet(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.getSet(var1, var2));
   }

   public List<byte[]> mget(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.mget(var1));
   }

   public long setnx(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.setnx(var1, var2));
   }

   public String setex(byte[] var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.setex(var1, var2, var4));
   }

   public String mset(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.mset(var1));
   }

   public long msetnx(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.msetnx(var1));
   }

   public long decrBy(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.decrBy(var1, var2));
   }

   public long decr(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.decr(var1));
   }

   public long incrBy(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.incrBy(var1, var2));
   }

   public double incrByFloat(byte[] var1, double var2) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.incrByFloat(var1, var2));
   }

   public long incr(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.incr(var1));
   }

   public long append(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.append(var1, var2));
   }

   public byte[] substr(byte[] var1, int var2, int var3) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.substr(var1, var2, var3));
   }

   public long hset(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hset(var1, var2, var3));
   }

   public long hset(byte[] var1, Map<byte[], byte[]> var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hset(var1, var2));
   }

   public byte[] hget(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.hget(var1, var2));
   }

   public long hsetnx(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hsetnx(var1, var2, var3));
   }

   public String hmset(byte[] var1, Map<byte[], byte[]> var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.hmset(var1, var2));
   }

   public List<byte[]> hmget(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hmget(var1, var2));
   }

   public long hincrBy(byte[] var1, byte[] var2, long var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hincrBy(var1, var2, var3));
   }

   public double hincrByFloat(byte[] var1, byte[] var2, double var3) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.hincrByFloat(var1, var2, var3));
   }

   public boolean hexists(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.hexists(var1, var2));
   }

   public long hdel(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hdel(var1, var2));
   }

   public long hlen(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hlen(var1));
   }

   public Set<byte[]> hkeys(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.hkeys(var1));
   }

   public List<byte[]> hvals(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hvals(var1));
   }

   public Map<byte[], byte[]> hgetAll(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Map)this.connection.executeCommand(this.commandObjects.hgetAll(var1));
   }

   public byte[] hrandfield(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.hrandfield(var1));
   }

   public List<byte[]> hrandfield(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hrandfield(var1, var2));
   }

   public List<Map.Entry<byte[], byte[]>> hrandfieldWithValues(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hrandfieldWithValues(var1, var2));
   }

   public long rpush(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.rpush(var1, var2));
   }

   public long lpush(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpush(var1, var2));
   }

   public long llen(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.llen(var1));
   }

   public List<byte[]> lrange(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.lrange(var1, var2, var4));
   }

   public String ltrim(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.ltrim(var1, var2, var4));
   }

   public byte[] lindex(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.lindex(var1, var2));
   }

   public String lset(byte[] var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.lset(var1, var2, var4));
   }

   public long lrem(byte[] var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lrem(var1, var2, var4));
   }

   public byte[] lpop(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.lpop(var1));
   }

   public List<byte[]> lpop(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.lpop(var1, var2));
   }

   public Long lpos(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpos(var1, var2));
   }

   public Long lpos(byte[] var1, byte[] var2, LPosParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpos(var1, var2, var3));
   }

   public List<Long> lpos(byte[] var1, byte[] var2, LPosParams var3, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.lpos(var1, var2, var3, var4));
   }

   public byte[] rpop(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.rpop(var1));
   }

   public List<byte[]> rpop(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.rpop(var1, var2));
   }

   public byte[] rpoplpush(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.rpoplpush(var1, var2));
   }

   public long sadd(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sadd(var1, var2));
   }

   public Set<byte[]> smembers(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.smembers(var1));
   }

   public long srem(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.srem(var1, var2));
   }

   public byte[] spop(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.spop(var1));
   }

   public Set<byte[]> spop(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.spop(var1, var2));
   }

   public long smove(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.smove(var1, var2, var3));
   }

   public long scard(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.scard(var1));
   }

   public boolean sismember(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.sismember(var1, var2));
   }

   public List<Boolean> smismember(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.smismember(var1, var2));
   }

   public Set<byte[]> sinter(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.sinter(var1));
   }

   public long sinterstore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sinterstore(var1, var2));
   }

   public long sintercard(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sintercard(var1));
   }

   public long sintercard(int var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sintercard(var1, var2));
   }

   public Set<byte[]> sunion(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.sunion(var1));
   }

   public long sunionstore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sunionstore(var1, var2));
   }

   public Set<byte[]> sdiff(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.sdiff(var1));
   }

   public long sdiffstore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sdiffstore(var1, var2));
   }

   public byte[] srandmember(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.srandmember(var1));
   }

   public List<byte[]> srandmember(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.srandmember(var1, var2));
   }

   public long zadd(byte[] var1, double var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2, var4));
   }

   public long zadd(byte[] var1, double var2, byte[] var4, ZAddParams var5) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2, var4, var5));
   }

   public long zadd(byte[] var1, Map<byte[], Double> var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2));
   }

   public long zadd(byte[] var1, Map<byte[], Double> var2, ZAddParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2, var3));
   }

   public Double zaddIncr(byte[] var1, double var2, byte[] var4, ZAddParams var5) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zaddIncr(var1, var2, var4, var5));
   }

   public List<byte[]> zrange(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrange(var1, var2, var4));
   }

   public long zrem(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrem(var1, var2));
   }

   public double zincrby(byte[] var1, double var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zincrby(var1, var2, var4));
   }

   public Double zincrby(byte[] var1, double var2, byte[] var4, ZIncrByParams var5) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zincrby(var1, var2, var4, var5));
   }

   public Long zrank(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrank(var1, var2));
   }

   public Long zrevrank(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrevrank(var1, var2));
   }

   public KeyValue<Long, Double> zrankWithScore(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zrankWithScore(var1, var2));
   }

   public KeyValue<Long, Double> zrevrankWithScore(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zrevrankWithScore(var1, var2));
   }

   public List<byte[]> zrevrange(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrange(var1, var2, var4));
   }

   public List<Tuple> zrangeWithScores(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeWithScores(var1, var2, var4));
   }

   public List<Tuple> zrevrangeWithScores(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeWithScores(var1, var2, var4));
   }

   public List<byte[]> zrange(byte[] var1, ZRangeParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrange(var1, var2));
   }

   public List<Tuple> zrangeWithScores(byte[] var1, ZRangeParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeWithScores(var1, var2));
   }

   public long zrangestore(byte[] var1, byte[] var2, ZRangeParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrangestore(var1, var2, var3));
   }

   public byte[] zrandmember(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.zrandmember(var1));
   }

   public List<byte[]> zrandmember(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrandmember(var1, var2));
   }

   public List<Tuple> zrandmemberWithScores(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrandmemberWithScores(var1, var2));
   }

   public long zcard(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zcard(var1));
   }

   public Double zscore(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zscore(var1, var2));
   }

   public List<Double> zmscore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zmscore(var1, var2));
   }

   public Tuple zpopmax(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Tuple)this.connection.executeCommand(this.commandObjects.zpopmax(var1));
   }

   public List<Tuple> zpopmax(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zpopmax(var1, var2));
   }

   public Tuple zpopmin(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Tuple)this.connection.executeCommand(this.commandObjects.zpopmin(var1));
   }

   public List<Tuple> zpopmin(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zpopmin(var1, var2));
   }

   public String watch(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.WATCH, (byte[][])var1);
      String var2 = this.connection.getStatusCodeReply();
      this.isInWatch = true;
      return var2;
   }

   public String unwatch() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.UNWATCH);
      return this.connection.getStatusCodeReply();
   }

   public List<byte[]> sort(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.sort(var1));
   }

   public List<byte[]> sort(byte[] var1, SortingParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.sort(var1, var2));
   }

   public long sort(byte[] var1, SortingParams var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sort(var1, var2, var3));
   }

   public long sort(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sort(var1, var2));
   }

   public List<byte[]> sortReadonly(byte[] var1, SortingParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.sortReadonly(var1, var2));
   }

   public byte[] lmove(byte[] var1, byte[] var2, ListDirection var3, ListDirection var4) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.lmove(var1, var2, var3, var4));
   }

   public byte[] blmove(byte[] var1, byte[] var2, ListDirection var3, ListDirection var4, double var5) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.blmove(var1, var2, var3, var4, var5));
   }

   public List<byte[]> blpop(int var1, byte[]... var2) {
      return (List)this.connection.executeCommand(this.commandObjects.blpop(var1, var2));
   }

   public KeyValue<byte[], byte[]> blpop(double var1, byte[]... var3) {
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blpop(var1, var3));
   }

   public List<byte[]> brpop(int var1, byte[]... var2) {
      return (List)this.connection.executeCommand(this.commandObjects.brpop(var1, var2));
   }

   public KeyValue<byte[], byte[]> brpop(double var1, byte[]... var3) {
      return (KeyValue)this.connection.executeCommand(this.commandObjects.brpop(var1, var3));
   }

   public KeyValue<byte[], List<byte[]>> lmpop(ListDirection var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.lmpop(var1, var2));
   }

   public KeyValue<byte[], List<byte[]>> lmpop(ListDirection var1, int var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.lmpop(var1, var2, var3));
   }

   public KeyValue<byte[], List<byte[]>> blmpop(double var1, ListDirection var3, byte[]... var4) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blmpop(var1, var3, var4));
   }

   public KeyValue<byte[], List<byte[]>> blmpop(double var1, ListDirection var3, int var4, byte[]... var5) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blmpop(var1, var3, var4, var5));
   }

   public KeyValue<byte[], Tuple> bzpopmax(double var1, byte[]... var3) {
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzpopmax(var1, var3));
   }

   public KeyValue<byte[], Tuple> bzpopmin(double var1, byte[]... var3) {
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzpopmin(var1, var3));
   }

   public String auth(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.AUTH, (String[])(var1));
      return this.connection.getStatusCodeReply();
   }

   public String auth(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.AUTH, (String[])(var1, var2));
      return this.connection.getStatusCodeReply();
   }

   public long zcount(byte[] var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zcount(var1, var2, var4));
   }

   public long zcount(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zcount(var1, var2, var3));
   }

   public List<byte[]> zdiff(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zdiff(var1));
   }

   public List<Tuple> zdiffWithScores(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zdiffWithScores(var1));
   }

   @Deprecated
   public long zdiffStore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zdiffStore(var1, var2));
   }

   public long zdiffstore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zdiffstore(var1, var2));
   }

   public List<byte[]> zrangeByScore(byte[] var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var4));
   }

   public List<byte[]> zrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var3));
   }

   public List<byte[]> zrangeByScore(byte[] var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var4, var6, var7));
   }

   public List<byte[]> zrangeByScore(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var3, var4, var5));
   }

   public List<Tuple> zrangeByScoreWithScores(byte[] var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4));
   }

   public List<Tuple> zrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3));
   }

   public List<Tuple> zrangeByScoreWithScores(byte[] var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public List<Tuple> zrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public List<byte[]> zrevrangeByScore(byte[] var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4));
   }

   public List<byte[]> zrevrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3));
   }

   public List<byte[]> zrevrangeByScore(byte[] var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4, var6, var7));
   }

   public List<byte[]> zrevrangeByScore(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3, var4, var5));
   }

   public List<Tuple> zrevrangeByScoreWithScores(byte[] var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4));
   }

   public List<Tuple> zrevrangeByScoreWithScores(byte[] var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public List<Tuple> zrevrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3));
   }

   public List<Tuple> zrevrangeByScoreWithScores(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public long zremrangeByRank(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByRank(var1, var2, var4));
   }

   public long zremrangeByScore(byte[] var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByScore(var1, var2, var4));
   }

   public long zremrangeByScore(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByScore(var1, var2, var3));
   }

   public List<byte[]> zunion(ZParams var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zunion(var1, var2));
   }

   public List<Tuple> zunionWithScores(ZParams var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zunionWithScores(var1, var2));
   }

   public long zunionstore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zunionstore(var1, var2));
   }

   public long zunionstore(byte[] var1, ZParams var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zunionstore(var1, var2, var3));
   }

   public List<byte[]> zinter(ZParams var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zinter(var1, var2));
   }

   public List<Tuple> zinterWithScores(ZParams var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zinterWithScores(var1, var2));
   }

   public long zinterstore(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zinterstore(var1, var2));
   }

   public long zinterstore(byte[] var1, ZParams var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zinterstore(var1, var2, var3));
   }

   public long zintercard(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zintercard(var1));
   }

   public long zintercard(long var1, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zintercard(var1, var3));
   }

   public long zlexcount(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zlexcount(var1, var2, var3));
   }

   public List<byte[]> zrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByLex(var1, var2, var3));
   }

   public List<byte[]> zrangeByLex(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByLex(var1, var2, var3, var4, var5));
   }

   public List<byte[]> zrevrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3));
   }

   public List<byte[]> zrevrangeByLex(byte[] var1, byte[] var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3, var4, var5));
   }

   public long zremrangeByLex(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByLex(var1, var2, var3));
   }

   public KeyValue<byte[], List<Tuple>> zmpop(SortedSetOption var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zmpop(var1, var2));
   }

   public KeyValue<byte[], List<Tuple>> zmpop(SortedSetOption var1, int var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zmpop(var1, var2, var3));
   }

   public KeyValue<byte[], List<Tuple>> bzmpop(double var1, SortedSetOption var3, byte[]... var4) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzmpop(var1, var3, var4));
   }

   public KeyValue<byte[], List<Tuple>> bzmpop(double var1, SortedSetOption var3, int var4, byte[]... var5) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzmpop(var1, var3, var4, var5));
   }

   public String save() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.SAVE);
      return this.connection.getStatusCodeReply();
   }

   public String bgsave() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.BGSAVE);
      return this.connection.getStatusCodeReply();
   }

   public String bgsaveSchedule() {
      this.connection.sendCommand(Protocol.Command.BGSAVE, (Rawable)Protocol.Keyword.SCHEDULE);
      return this.connection.getStatusCodeReply();
   }

   public String bgrewriteaof() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.BGREWRITEAOF);
      return this.connection.getStatusCodeReply();
   }

   public long lastsave() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.LASTSAVE);
      return this.connection.getIntegerReply();
   }

   public void shutdown() throws JedisException {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.SHUTDOWN);

      try {
         throw new JedisException(this.connection.getStatusCodeReply());
      } catch (JedisConnectionException var2) {
         this.connection.setBroken();
      }
   }

   public void shutdown(ShutdownParams var1) throws JedisException {
      this.connection.sendCommand((new CommandArguments(Protocol.Command.SHUTDOWN)).addParams(var1));

      try {
         throw new JedisException(this.connection.getStatusCodeReply());
      } catch (JedisConnectionException var3) {
         this.connection.setBroken();
      }
   }

   public String shutdownAbort() {
      this.connection.sendCommand(Protocol.Command.SHUTDOWN, (Rawable)Protocol.Keyword.ABORT);
      return this.connection.getStatusCodeReply();
   }

   public String info() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.INFO);
      return this.connection.getBulkReply();
   }

   public String info(String var1) {
      this.connection.sendCommand(Protocol.Command.INFO, (String[])(var1));
      return this.connection.getBulkReply();
   }

   public void monitor(JedisMonitor var1) {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.MONITOR);
      this.connection.getStatusCodeReply();
      var1.proceed(this.connection);
   }

   @Deprecated
   public String slaveof(String var1, int var2) {
      this.connection.sendCommand(Protocol.Command.SLAVEOF, (byte[][])(SafeEncoder.encode(var1), Protocol.toByteArray(var2)));
      return this.connection.getStatusCodeReply();
   }

   @Deprecated
   public String slaveofNoOne() {
      this.connection.sendCommand(Protocol.Command.SLAVEOF, (byte[][])(Protocol.Keyword.NO.getRaw(), Protocol.Keyword.ONE.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public String replicaof(String var1, int var2) {
      this.connection.sendCommand(Protocol.Command.REPLICAOF, (byte[][])(SafeEncoder.encode(var1), Protocol.toByteArray(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String replicaofNoOne() {
      this.connection.sendCommand(Protocol.Command.REPLICAOF, (byte[][])(Protocol.Keyword.NO.getRaw(), Protocol.Keyword.ONE.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public List<Object> roleBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.ROLE);
      return BuilderFactory.RAW_OBJECT_LIST.build(this.connection.getOne());
   }

   public Map<byte[], byte[]> configGet(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (byte[][])(Protocol.Keyword.GET.getRaw(), var1));
      return BuilderFactory.BINARY_MAP.build(this.connection.getOne());
   }

   public Map<byte[], byte[]> configGet(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (byte[][])joinParameters(Protocol.Keyword.GET.getRaw(), var1));
      return BuilderFactory.BINARY_MAP.build(this.connection.getOne());
   }

   public String configResetStat() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (Rawable)Protocol.Keyword.RESETSTAT);
      return this.connection.getStatusCodeReply();
   }

   public String configRewrite() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (Rawable)Protocol.Keyword.REWRITE);
      return this.connection.getStatusCodeReply();
   }

   public String configSet(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (byte[][])(Protocol.Keyword.SET.getRaw(), var1, var2));
      return this.connection.getStatusCodeReply();
   }

   public String configSet(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (byte[][])joinParameters(Protocol.Keyword.SET.getRaw(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String configSetBinary(Map<byte[], byte[]> var1) {
      this.checkIsInMultiOrPipeline();
      CommandArguments var2 = (new CommandArguments(Protocol.Command.CONFIG)).add(Protocol.Keyword.SET);
      var1.forEach((var1x, var2x) -> var2.add(var1x).add(var2x));
      this.connection.sendCommand(var2);
      return this.connection.getStatusCodeReply();
   }

   public long strlen(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.strlen(var1));
   }

   public LCSMatchResult lcs(byte[] var1, byte[] var2, LCSParams var3) {
      this.checkIsInMultiOrPipeline();
      return (LCSMatchResult)this.connection.executeCommand(this.commandObjects.lcs(var1, var2, var3));
   }

   public long lpushx(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpushx(var1, var2));
   }

   public long persist(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.persist(var1));
   }

   public long rpushx(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.rpushx(var1, var2));
   }

   public byte[] echo(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ECHO, (byte[][])(var1));
      return this.connection.getBinaryBulkReply();
   }

   public long linsert(byte[] var1, ListPosition var2, byte[] var3, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.linsert(var1, var2, var3, var4));
   }

   public byte[] brpoplpush(byte[] var1, byte[] var2, int var3) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.brpoplpush(var1, var2, var3));
   }

   public boolean setbit(byte[] var1, long var2, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.setbit(var1, var2, var4));
   }

   public boolean getbit(byte[] var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.getbit(var1, var2));
   }

   public long bitpos(byte[] var1, boolean var2) {
      return this.bitpos(var1, var2, new BitPosParams());
   }

   public long bitpos(byte[] var1, boolean var2, BitPosParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitpos(var1, var2, var3));
   }

   public long setrange(byte[] var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.setrange(var1, var2, var4));
   }

   public byte[] getrange(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.getrange(var1, var2, var4));
   }

   public long publish(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.publish(var1, var2));
   }

   public void subscribe(BinaryJedisPubSub var1, byte[]... var2) {
      var1.proceed(this.connection, var2);
   }

   public void psubscribe(BinaryJedisPubSub var1, byte[]... var2) {
      var1.proceedWithPatterns(this.connection, var2);
   }

   public Object eval(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Object evalReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalReadonly(var1, var2, var3));
   }

   protected static byte[][] getParamsWithBinary(List<byte[]> var0, List<byte[]> var1) {
      int var2 = var0.size();
      int var3 = var1.size();
      byte[][] var4 = new byte[var2 + var3][];

      for(int var5 = 0; var5 < var2; ++var5) {
         var4[var5] = (byte[])var0.get(var5);
      }

      for(int var6 = 0; var6 < var3; ++var6) {
         var4[var2 + var6] = (byte[])var1.get(var6);
      }

      return var4;
   }

   public Object eval(byte[] var1, int var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Object eval(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.eval(var1));
   }

   public Object evalsha(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalsha(var1));
   }

   public Object evalsha(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Object evalshaReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalshaReadonly(var1, var2, var3));
   }

   public Object evalsha(byte[] var1, int var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public String scriptFlush() {
      this.connection.sendCommand(Protocol.Command.SCRIPT, (Rawable)Protocol.Keyword.FLUSH);
      return this.connection.getStatusCodeReply();
   }

   public String scriptFlush(FlushMode var1) {
      this.connection.sendCommand(Protocol.Command.SCRIPT, (byte[][])(Protocol.Keyword.FLUSH.getRaw(), var1.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public Boolean scriptExists(byte[] var1) {
      byte[][] var2 = new byte[][]{var1};
      return (Boolean)this.scriptExists(var2).get(0);
   }

   public List<Boolean> scriptExists(byte[]... var1) {
      this.connection.sendCommand(Protocol.Command.SCRIPT, (byte[][])joinParameters(Protocol.Keyword.EXISTS.getRaw(), var1));
      return BuilderFactory.BOOLEAN_LIST.build(this.connection.getOne());
   }

   public byte[] scriptLoad(byte[] var1) {
      this.connection.sendCommand(Protocol.Command.SCRIPT, (byte[][])(Protocol.Keyword.LOAD.getRaw(), var1));
      return this.connection.getBinaryBulkReply();
   }

   public String scriptKill() {
      return (String)this.connection.executeCommand(this.commandObjects.scriptKill());
   }

   public String slowlogReset() {
      return (String)this.connection.executeCommand(this.commandObjects.slowlogReset());
   }

   public long slowlogLen() {
      this.connection.sendCommand(Protocol.Command.SLOWLOG, (Rawable)Protocol.Keyword.LEN);
      return this.connection.getIntegerReply();
   }

   public List<Object> slowlogGetBinary() {
      this.connection.sendCommand(Protocol.Command.SLOWLOG, (Rawable)Protocol.Keyword.GET);
      return this.connection.getObjectMultiBulkReply();
   }

   public List<Object> slowlogGetBinary(long var1) {
      this.connection.sendCommand(Protocol.Command.SLOWLOG, (byte[][])(Protocol.Keyword.GET.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getObjectMultiBulkReply();
   }

   public Long objectRefcount(byte[] var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (byte[][])(Protocol.Keyword.REFCOUNT.getRaw(), var1));
      return this.connection.getIntegerReply();
   }

   public byte[] objectEncoding(byte[] var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (byte[][])(Protocol.Keyword.ENCODING.getRaw(), var1));
      return this.connection.getBinaryBulkReply();
   }

   public Long objectIdletime(byte[] var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (byte[][])(Protocol.Keyword.IDLETIME.getRaw(), var1));
      return this.connection.getIntegerReply();
   }

   public List<byte[]> objectHelpBinary() {
      this.connection.sendCommand(Protocol.Command.OBJECT, (Rawable)Protocol.Keyword.HELP);
      return this.connection.getBinaryMultiBulkReply();
   }

   public Long objectFreq(byte[] var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (byte[][])(Protocol.Keyword.FREQ.getRaw(), var1));
      return this.connection.getIntegerReply();
   }

   public long bitcount(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitcount(var1));
   }

   public long bitcount(byte[] var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitcount(var1, var2, var4));
   }

   public long bitcount(byte[] var1, long var2, long var4, BitCountOption var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitcount(var1, var2, var4, var6));
   }

   public long bitop(BitOP var1, byte[] var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitop(var1, var2, var3));
   }

   public byte[] dump(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.dump(var1));
   }

   public String restore(byte[] var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.restore(var1, var2, var4));
   }

   public String restore(byte[] var1, long var2, byte[] var4, RestoreParams var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.restore(var1, var2, var4, var5));
   }

   public long pttl(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pttl(var1));
   }

   public String psetex(byte[] var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.psetex(var1, var2, var4));
   }

   public byte[] memoryDoctorBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (Rawable)Protocol.Keyword.DOCTOR);
      return this.connection.getBinaryBulkReply();
   }

   public Long memoryUsage(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (byte[][])(Protocol.Keyword.USAGE.getRaw(), var1));
      return this.connection.getIntegerReply();
   }

   public Long memoryUsage(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (byte[][])(Protocol.Keyword.USAGE.getRaw(), var1, Protocol.Keyword.SAMPLES.getRaw(), Protocol.toByteArray(var2)));
      return this.connection.getIntegerReply();
   }

   public String failover() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.FAILOVER);
      this.connection.setTimeoutInfinite();

      String var1;
      try {
         var1 = this.connection.getStatusCodeReply();
      } finally {
         this.connection.rollbackTimeout();
      }

      return var1;
   }

   public String failover(FailoverParams var1) {
      this.checkIsInMultiOrPipeline();
      CommandArguments var2 = (new ClusterCommandArguments(Protocol.Command.FAILOVER)).addParams(var1);
      this.connection.sendCommand(var2);
      this.connection.setTimeoutInfinite();

      String var3;
      try {
         var3 = this.connection.getStatusCodeReply();
      } finally {
         this.connection.rollbackTimeout();
      }

      return var3;
   }

   public String failoverAbort() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.FAILOVER, (Rawable)Protocol.Keyword.ABORT);
      return this.connection.getStatusCodeReply();
   }

   public byte[] aclWhoAmIBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.WHOAMI);
      return this.connection.getBinaryBulkReply();
   }

   public byte[] aclGenPassBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.GENPASS);
      return this.connection.getBinaryBulkReply();
   }

   public byte[] aclGenPassBinary(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.GENPASS.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getBinaryBulkReply();
   }

   public List<byte[]> aclListBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.LIST);
      return this.connection.getBinaryMultiBulkReply();
   }

   public List<byte[]> aclUsersBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.USERS);
      return this.connection.getBinaryMultiBulkReply();
   }

   public AccessControlUser aclGetUser(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.GETUSER.getRaw(), var1));
      return BuilderFactory.ACCESS_CONTROL_USER.build(this.connection.getObjectMultiBulkReply());
   }

   public String aclSetUser(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.SETUSER.getRaw(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String aclSetUser(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])joinParameters(Protocol.Keyword.SETUSER.getRaw(), var1, var2));
      return this.connection.getStatusCodeReply();
   }

   public long aclDelUser(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])joinParameters(Protocol.Keyword.DELUSER.getRaw(), var1));
      return this.connection.getIntegerReply();
   }

   public List<byte[]> aclCatBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.CAT);
      return this.connection.getBinaryMultiBulkReply();
   }

   public List<byte[]> aclCat(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.CAT.getRaw(), var1));
      return this.connection.getBinaryMultiBulkReply();
   }

   public List<byte[]> aclLogBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.LOG);
      return this.connection.getBinaryMultiBulkReply();
   }

   public List<byte[]> aclLogBinary(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.LOG.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getBinaryMultiBulkReply();
   }

   public String aclLogReset() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.LOG.getRaw(), Protocol.Keyword.RESET.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public String clientKill(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.KILL.getRaw(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String clientKill(String var1, int var2) {
      return this.clientKill(var1 + ':' + var2);
   }

   public long clientKill(ClientKillParams var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((new CommandArguments(Protocol.Command.CLIENT)).add(Protocol.Keyword.KILL).addParams(var1));
      return this.connection.getIntegerReply();
   }

   public byte[] clientGetnameBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.GETNAME);
      return this.connection.getBinaryBulkReply();
   }

   public byte[] clientListBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.LIST);
      return this.connection.getBinaryBulkReply();
   }

   public byte[] clientListBinary(ClientType var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.LIST.getRaw(), var1.getRaw()));
      return this.connection.getBinaryBulkReply();
   }

   public byte[] clientListBinary(long... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])this.clientListParams(var1));
      return this.connection.getBinaryBulkReply();
   }

   private byte[][] clientListParams(long... var1) {
      byte[][] var2 = new byte[2 + var1.length][];
      int var3 = 0;
      var2[var3++] = Protocol.Keyword.LIST.getRaw();
      var2[var3++] = Protocol.Keyword.ID.getRaw();

      for(long var7 : var1) {
         var2[var3++] = Protocol.toByteArray(var7);
      }

      return var2;
   }

   public byte[] clientInfoBinary() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.INFO);
      return this.connection.getBinaryBulkReply();
   }

   public String clientSetInfo(ClientAttributeOption var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.SETINFO.getRaw(), var1.getRaw(), var2));
      return this.connection.getStatusCodeReply();
   }

   public String clientSetname(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.SETNAME.getRaw(), var1));
      return this.connection.getBulkReply();
   }

   public long clientId() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.ID);
      return this.connection.getIntegerReply();
   }

   public long clientUnblock(long var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.UNBLOCK.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getIntegerReply();
   }

   public long clientUnblock(long var1, UnblockType var3) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.UNBLOCK.getRaw(), Protocol.toByteArray(var1), var3.getRaw()));
      return this.connection.getIntegerReply();
   }

   public String clientPause(long var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.PAUSE.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getBulkReply();
   }

   public String clientPause(long var1, ClientPauseMode var3) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.PAUSE.getRaw(), Protocol.toByteArray(var1), var3.getRaw()));
      return this.connection.getBulkReply();
   }

   public String clientUnpause() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.UNPAUSE);
      return this.connection.getBulkReply();
   }

   public String clientNoEvictOn() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (String[])("NO-EVICT", "ON"));
      return this.connection.getBulkReply();
   }

   public String clientNoEvictOff() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (String[])("NO-EVICT", "OFF"));
      return this.connection.getBulkReply();
   }

   public String clientNoTouchOn() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (String[])("NO-TOUCH", "ON"));
      return this.connection.getStatusCodeReply();
   }

   public String clientNoTouchOff() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (String[])("NO-TOUCH", "OFF"));
      return this.connection.getStatusCodeReply();
   }

   public List<String> time() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.TIME);
      return this.connection.getMultiBulkReply();
   }

   public String migrate(String var1, int var2, byte[] var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public String migrate(String var1, int var2, int var3, int var4, MigrateParams var5, byte[]... var6) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5, var6));
   }

   public String migrate(String var1, int var2, byte[] var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4));
   }

   public String migrate(String var1, int var2, int var3, MigrateParams var4, byte[]... var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public long waitReplicas(int var1, long var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.WAIT, (byte[][])(Protocol.toByteArray(var1), Protocol.toByteArray(var2)));
      return this.connection.getIntegerReply();
   }

   public KeyValue<Long, Long> waitAOF(long var1, long var3, long var5) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.WAITAOF, (byte[][])(Protocol.toByteArray(var1), Protocol.toByteArray(var3), Protocol.toByteArray(var5)));
      return BuilderFactory.LONG_LONG_PAIR.build(this.connection.getOne());
   }

   public long pfadd(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pfadd(var1, var2));
   }

   public long pfcount(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pfcount(var1));
   }

   public String pfmerge(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.pfmerge(var1, var2));
   }

   public long pfcount(byte[]... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pfcount(var1));
   }

   public ScanResult<byte[]> scan(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.scan(var1));
   }

   public ScanResult<byte[]> scan(byte[] var1, ScanParams var2) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.scan(var1, var2));
   }

   public ScanResult<byte[]> scan(byte[] var1, ScanParams var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.scan(var1, var2, var3));
   }

   public ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] var1, byte[] var2) {
      return this.hscan(var1, var2, new ScanParams());
   }

   public ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] var1, byte[] var2, ScanParams var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.hscan(var1, var2, var3));
   }

   public ScanResult<byte[]> sscan(byte[] var1, byte[] var2) {
      return this.sscan(var1, var2, new ScanParams());
   }

   public ScanResult<byte[]> sscan(byte[] var1, byte[] var2, ScanParams var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.sscan(var1, var2, var3));
   }

   public ScanResult<Tuple> zscan(byte[] var1, byte[] var2) {
      return this.zscan(var1, var2, new ScanParams());
   }

   public ScanResult<Tuple> zscan(byte[] var1, byte[] var2, ScanParams var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.zscan(var1, var2, var3));
   }

   public long geoadd(byte[] var1, double var2, double var4, byte[] var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geoadd(var1, var2, var4, var6));
   }

   public long geoadd(byte[] var1, Map<byte[], GeoCoordinate> var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geoadd(var1, var2));
   }

   public long geoadd(byte[] var1, GeoAddParams var2, Map<byte[], GeoCoordinate> var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geoadd(var1, var2, var3));
   }

   public Double geodist(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.geodist(var1, var2, var3));
   }

   public Double geodist(byte[] var1, byte[] var2, byte[] var3, GeoUnit var4) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.geodist(var1, var2, var3, var4));
   }

   public List<byte[]> geohash(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geohash(var1, var2));
   }

   public List<GeoCoordinate> geopos(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geopos(var1, var2));
   }

   public List<GeoRadiusResponse> georadius(byte[] var1, double var2, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8));
   }

   public List<GeoRadiusResponse> georadiusReadonly(byte[] var1, double var2, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8));
   }

   public List<GeoRadiusResponse> georadius(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8, var9));
   }

   public long georadiusStore(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9, GeoRadiusStoreParam var10) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.georadiusStore(var1, var2, var4, var6, var8, var9, var10));
   }

   public List<GeoRadiusResponse> georadiusReadonly(byte[] var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8, var9));
   }

   public List<GeoRadiusResponse> georadiusByMember(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> georadiusByMemberReadonly(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> georadiusByMember(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5, var6));
   }

   public long georadiusByMemberStore(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6, GeoRadiusStoreParam var7) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.georadiusByMemberStore(var1, var2, var3, var5, var6, var7));
   }

   public List<GeoRadiusResponse> geosearch(byte[] var1, byte[] var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> geosearch(byte[] var1, GeoCoordinate var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> geosearch(byte[] var1, byte[] var2, double var3, double var5, GeoUnit var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public List<GeoRadiusResponse> geosearch(byte[] var1, GeoCoordinate var2, double var3, double var5, GeoUnit var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public List<GeoRadiusResponse> geosearch(byte[] var1, GeoSearchParam var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2));
   }

   public long geosearchStore(byte[] var1, byte[] var2, byte[] var3, double var4, GeoUnit var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public long geosearchStore(byte[] var1, byte[] var2, GeoCoordinate var3, double var4, GeoUnit var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public long geosearchStore(byte[] var1, byte[] var2, byte[] var3, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public long geosearchStore(byte[] var1, byte[] var2, GeoCoordinate var3, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public long geosearchStore(byte[] var1, byte[] var2, GeoSearchParam var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3));
   }

   public long geosearchStoreStoreDist(byte[] var1, byte[] var2, GeoSearchParam var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStoreStoreDist(var1, var2, var3));
   }

   public List<GeoRadiusResponse> georadiusByMemberReadonly(byte[] var1, byte[] var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5, var6));
   }

   public List<Long> bitfield(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.bitfield(var1, var2));
   }

   public List<Long> bitfieldReadonly(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.bitfieldReadonly(var1, var2));
   }

   public long hstrlen(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hstrlen(var1, var2));
   }

   public List<Object> xread(XReadParams var1, Map.Entry<byte[], byte[]>... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xread(var1, var2));
   }

   public List<Object> xreadGroup(byte[] var1, byte[] var2, XReadGroupParams var3, Map.Entry<byte[], byte[]>... var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xreadGroup(var1, var2, var3, var4));
   }

   public byte[] xadd(byte[] var1, XAddParams var2, Map<byte[], byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.xadd(var1, var2, var3));
   }

   public long xlen(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xlen(var1));
   }

   public List<Object> xrange(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrange(var1, var2, var3));
   }

   public List<Object> xrange(byte[] var1, byte[] var2, byte[] var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrange(var1, var2, var3, var4));
   }

   public List<Object> xrevrange(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrevrange(var1, var2, var3));
   }

   public List<Object> xrevrange(byte[] var1, byte[] var2, byte[] var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrevrange(var1, var2, var3, var4));
   }

   public long xack(byte[] var1, byte[] var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xack(var1, var2, var3));
   }

   public String xgroupCreate(byte[] var1, byte[] var2, byte[] var3, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.xgroupCreate(var1, var2, var3, var4));
   }

   public String xgroupSetID(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.xgroupSetID(var1, var2, var3));
   }

   public long xgroupDestroy(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xgroupDestroy(var1, var2));
   }

   public boolean xgroupCreateConsumer(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.xgroupCreateConsumer(var1, var2, var3));
   }

   public long xgroupDelConsumer(byte[] var1, byte[] var2, byte[] var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xgroupDelConsumer(var1, var2, var3));
   }

   public long xdel(byte[] var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xdel(var1, var2));
   }

   public long xtrim(byte[] var1, long var2, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xtrim(var1, var2, var4));
   }

   public long xtrim(byte[] var1, XTrimParams var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xtrim(var1, var2));
   }

   public Object xpending(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.xpending(var1, var2));
   }

   public List<Object> xpending(byte[] var1, byte[] var2, XPendingParams var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xpending(var1, var2, var3));
   }

   public List<byte[]> xclaim(byte[] var1, byte[] var2, byte[] var3, long var4, XClaimParams var6, byte[]... var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xclaim(var1, var2, var3, var4, var6, var7));
   }

   public List<byte[]> xclaimJustId(byte[] var1, byte[] var2, byte[] var3, long var4, XClaimParams var6, byte[]... var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public List<Object> xautoclaim(byte[] var1, byte[] var2, byte[] var3, long var4, byte[] var6, XAutoClaimParams var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xautoclaim(var1, var2, var3, var4, var6, var7));
   }

   public List<Object> xautoclaimJustId(byte[] var1, byte[] var2, byte[] var3, long var4, byte[] var6, XAutoClaimParams var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xautoclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public Object xinfoStream(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.xinfoStream(var1));
   }

   public Object xinfoStreamFull(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.xinfoStreamFull(var1));
   }

   public Object xinfoStreamFull(byte[] var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.xinfoStreamFull(var1, var2));
   }

   public List<Object> xinfoGroups(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xinfoGroups(var1));
   }

   public List<Object> xinfoConsumers(byte[] var1, byte[] var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xinfoConsumers(var1, var2));
   }

   public Object sendCommand(ProtocolCommand var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(var1, var2);
      return this.connection.getOne();
   }

   public Object sendBlockingCommand(ProtocolCommand var1, byte[]... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(var1, var2);
      this.connection.setTimeoutInfinite();

      Object var3;
      try {
         var3 = this.connection.getOne();
      } finally {
         this.connection.rollbackTimeout();
      }

      return var3;
   }

   public Object sendCommand(ProtocolCommand var1) {
      return this.sendCommand(var1, DUMMY_ARRAY);
   }

   public boolean copy(String var1, String var2, int var3, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.copy(var1, var2, var3, var4));
   }

   public boolean copy(String var1, String var2, boolean var3) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.copy(var1, var2, var3));
   }

   public String ping(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PING, (String[])(var1));
      return this.connection.getBulkReply();
   }

   public String set(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.set(var1, var2));
   }

   public String set(String var1, String var2, SetParams var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.set(var1, var2, var3));
   }

   public String get(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.get(var1));
   }

   public String setGet(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.setGet(var1, var2));
   }

   public String setGet(String var1, String var2, SetParams var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.setGet(var1, var2, var3));
   }

   public String getDel(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.getDel(var1));
   }

   public String getEx(String var1, GetExParams var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.getEx(var1, var2));
   }

   public long exists(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.exists(var1));
   }

   public boolean exists(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.exists(var1));
   }

   public long del(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.del(var1));
   }

   public long del(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.del(var1));
   }

   public long unlink(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.unlink(var1));
   }

   public long unlink(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.unlink(var1));
   }

   public String type(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.type(var1));
   }

   public Set<String> keys(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.keys(var1));
   }

   public String randomKey() {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.randomKey());
   }

   public String rename(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.rename(var1, var2));
   }

   public long renamenx(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.renamenx(var1, var2));
   }

   public long expire(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expire(var1, var2));
   }

   public long expire(String var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expire(var1, var2, var4));
   }

   public long pexpire(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpire(var1, var2));
   }

   public long pexpire(String var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpire(var1, var2, var4));
   }

   public long expireTime(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expireTime(var1));
   }

   public long pexpireTime(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpireTime(var1));
   }

   public long expireAt(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expireAt(var1, var2));
   }

   public long expireAt(String var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.expireAt(var1, var2, var4));
   }

   public long pexpireAt(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpireAt(var1, var2));
   }

   public long pexpireAt(String var1, long var2, ExpiryOption var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pexpireAt(var1, var2, var4));
   }

   public long ttl(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.ttl(var1));
   }

   public long touch(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.touch(var1));
   }

   public long touch(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.touch(var1));
   }

   public long move(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MOVE, (byte[][])(SafeEncoder.encode(var1), Protocol.toByteArray(var2)));
      return this.connection.getIntegerReply();
   }

   public String getSet(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.getSet(var1, var2));
   }

   public List<String> mget(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.mget(var1));
   }

   public long setnx(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.setnx(var1, var2));
   }

   public String setex(String var1, long var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.setex(var1, var2, var4));
   }

   public String mset(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.mset(var1));
   }

   public long msetnx(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.msetnx(var1));
   }

   public long decrBy(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.decrBy(var1, var2));
   }

   public long decr(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.decr(var1));
   }

   public long incrBy(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.incrBy(var1, var2));
   }

   public double incrByFloat(String var1, double var2) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.incrByFloat(var1, var2));
   }

   public long incr(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.incr(var1));
   }

   public long append(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.append(var1, var2));
   }

   public String substr(String var1, int var2, int var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.substr(var1, var2, var3));
   }

   public long hset(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hset(var1, var2, var3));
   }

   public long hset(String var1, Map<String, String> var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hset(var1, var2));
   }

   public String hget(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.hget(var1, var2));
   }

   public long hsetnx(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hsetnx(var1, var2, var3));
   }

   public String hmset(String var1, Map<String, String> var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.hmset(var1, var2));
   }

   public List<String> hmget(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hmget(var1, var2));
   }

   public long hincrBy(String var1, String var2, long var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hincrBy(var1, var2, var3));
   }

   public double hincrByFloat(String var1, String var2, double var3) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.hincrByFloat(var1, var2, var3));
   }

   public boolean hexists(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.hexists(var1, var2));
   }

   public long hdel(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hdel(var1, var2));
   }

   public long hlen(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hlen(var1));
   }

   public Set<String> hkeys(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.hkeys(var1));
   }

   public List<String> hvals(String var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hvals(var1));
   }

   public Map<String, String> hgetAll(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Map)this.connection.executeCommand(this.commandObjects.hgetAll(var1));
   }

   public String hrandfield(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.hrandfield(var1));
   }

   public List<String> hrandfield(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hrandfield(var1, var2));
   }

   public List<Map.Entry<String, String>> hrandfieldWithValues(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.hrandfieldWithValues(var1, var2));
   }

   public long rpush(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.rpush(var1, var2));
   }

   public long lpush(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpush(var1, var2));
   }

   public long llen(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.llen(var1));
   }

   public List<String> lrange(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.lrange(var1, var2, var4));
   }

   public String ltrim(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.ltrim(var1, var2, var4));
   }

   public String lindex(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.lindex(var1, var2));
   }

   public String lset(String var1, long var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.lset(var1, var2, var4));
   }

   public long lrem(String var1, long var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lrem(var1, var2, var4));
   }

   public String lpop(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.lpop(var1));
   }

   public List<String> lpop(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.lpop(var1, var2));
   }

   public Long lpos(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpos(var1, var2));
   }

   public Long lpos(String var1, String var2, LPosParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpos(var1, var2, var3));
   }

   public List<Long> lpos(String var1, String var2, LPosParams var3, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.lpos(var1, var2, var3, var4));
   }

   public String rpop(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.rpop(var1));
   }

   public List<String> rpop(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.rpop(var1, var2));
   }

   public String rpoplpush(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.rpoplpush(var1, var2));
   }

   public long sadd(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sadd(var1, var2));
   }

   public Set<String> smembers(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.smembers(var1));
   }

   public long srem(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.srem(var1, var2));
   }

   public String spop(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.spop(var1));
   }

   public Set<String> spop(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.spop(var1, var2));
   }

   public long smove(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.smove(var1, var2, var3));
   }

   public long scard(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.scard(var1));
   }

   public boolean sismember(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.sismember(var1, var2));
   }

   public List<Boolean> smismember(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.smismember(var1, var2));
   }

   public Set<String> sinter(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.sinter(var1));
   }

   public long sinterstore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sinterstore(var1, var2));
   }

   public long sintercard(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sintercard(var1));
   }

   public long sintercard(int var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sintercard(var1, var2));
   }

   public Set<String> sunion(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.sunion(var1));
   }

   public long sunionstore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sunionstore(var1, var2));
   }

   public Set<String> sdiff(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Set)this.connection.executeCommand(this.commandObjects.sdiff(var1));
   }

   public long sdiffstore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sdiffstore(var1, var2));
   }

   public String srandmember(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.srandmember(var1));
   }

   public List<String> srandmember(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.srandmember(var1, var2));
   }

   public long zadd(String var1, double var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2, var4));
   }

   public long zadd(String var1, double var2, String var4, ZAddParams var5) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2, var4, var5));
   }

   public long zadd(String var1, Map<String, Double> var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2));
   }

   public long zadd(String var1, Map<String, Double> var2, ZAddParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zadd(var1, var2, var3));
   }

   public Double zaddIncr(String var1, double var2, String var4, ZAddParams var5) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zaddIncr(var1, var2, var4, var5));
   }

   public List<String> zdiff(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zdiff(var1));
   }

   public List<Tuple> zdiffWithScores(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zdiffWithScores(var1));
   }

   @Deprecated
   public long zdiffStore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zdiffStore(var1, var2));
   }

   public long zdiffstore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zdiffstore(var1, var2));
   }

   public List<String> zrange(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrange(var1, var2, var4));
   }

   public long zrem(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrem(var1, var2));
   }

   public double zincrby(String var1, double var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zincrby(var1, var2, var4));
   }

   public Double zincrby(String var1, double var2, String var4, ZIncrByParams var5) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zincrby(var1, var2, var4, var5));
   }

   public Long zrank(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrank(var1, var2));
   }

   public Long zrevrank(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrevrank(var1, var2));
   }

   public KeyValue<Long, Double> zrankWithScore(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zrankWithScore(var1, var2));
   }

   public KeyValue<Long, Double> zrevrankWithScore(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zrevrankWithScore(var1, var2));
   }

   public List<String> zrevrange(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrange(var1, var2, var4));
   }

   public List<Tuple> zrangeWithScores(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeWithScores(var1, var2, var4));
   }

   public List<Tuple> zrevrangeWithScores(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeWithScores(var1, var2, var4));
   }

   public List<String> zrange(String var1, ZRangeParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrange(var1, var2));
   }

   public List<Tuple> zrangeWithScores(String var1, ZRangeParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeWithScores(var1, var2));
   }

   public long zrangestore(String var1, String var2, ZRangeParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zrangestore(var1, var2, var3));
   }

   public String zrandmember(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.zrandmember(var1));
   }

   public List<String> zrandmember(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrandmember(var1, var2));
   }

   public List<Tuple> zrandmemberWithScores(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrandmemberWithScores(var1, var2));
   }

   public long zcard(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zcard(var1));
   }

   public Double zscore(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.zscore(var1, var2));
   }

   public List<Double> zmscore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zmscore(var1, var2));
   }

   public Tuple zpopmax(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Tuple)this.connection.executeCommand(this.commandObjects.zpopmax(var1));
   }

   public List<Tuple> zpopmax(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zpopmax(var1, var2));
   }

   public Tuple zpopmin(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Tuple)this.connection.executeCommand(this.commandObjects.zpopmin(var1));
   }

   public List<Tuple> zpopmin(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zpopmin(var1, var2));
   }

   public String watch(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.WATCH, (String[])var1);
      String var2 = this.connection.getStatusCodeReply();
      this.isInWatch = true;
      return var2;
   }

   public List<String> sort(String var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.sort(var1));
   }

   public List<String> sort(String var1, SortingParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.sort(var1, var2));
   }

   public long sort(String var1, SortingParams var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sort(var1, var2, var3));
   }

   public List<String> sortReadonly(String var1, SortingParams var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.sortReadonly(var1, var2));
   }

   public long sort(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.sort(var1, var2));
   }

   public String lmove(String var1, String var2, ListDirection var3, ListDirection var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.lmove(var1, var2, var3, var4));
   }

   public String blmove(String var1, String var2, ListDirection var3, ListDirection var4, double var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.blmove(var1, var2, var3, var4, var5));
   }

   public List<String> blpop(int var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.blpop(var1, var2));
   }

   public KeyValue<String, String> blpop(double var1, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blpop(var1, var3));
   }

   public List<String> brpop(int var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.brpop(var1, var2));
   }

   public KeyValue<String, String> brpop(double var1, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.brpop(var1, var3));
   }

   public KeyValue<String, List<String>> lmpop(ListDirection var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.lmpop(var1, var2));
   }

   public KeyValue<String, List<String>> lmpop(ListDirection var1, int var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.lmpop(var1, var2, var3));
   }

   public KeyValue<String, List<String>> blmpop(double var1, ListDirection var3, String... var4) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blmpop(var1, var3, var4));
   }

   public KeyValue<String, List<String>> blmpop(double var1, ListDirection var3, int var4, String... var5) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blmpop(var1, var3, var4, var5));
   }

   public KeyValue<String, Tuple> bzpopmax(double var1, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzpopmax(var1, var3));
   }

   public KeyValue<String, Tuple> bzpopmin(double var1, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzpopmin(var1, var3));
   }

   public List<String> blpop(int var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.blpop(var1, var2));
   }

   public KeyValue<String, String> blpop(double var1, String var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.blpop(var1, var3));
   }

   public List<String> brpop(int var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.brpop(var1, var2));
   }

   public KeyValue<String, String> brpop(double var1, String var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.brpop(var1, var3));
   }

   public long zcount(String var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zcount(var1, var2, var4));
   }

   public long zcount(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zcount(var1, var2, var3));
   }

   public List<String> zrangeByScore(String var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var4));
   }

   public List<String> zrangeByScore(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var3));
   }

   public List<String> zrangeByScore(String var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var4, var6, var7));
   }

   public List<String> zrangeByScore(String var1, String var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScore(var1, var2, var3, var4, var5));
   }

   public List<Tuple> zrangeByScoreWithScores(String var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4));
   }

   public List<Tuple> zrangeByScoreWithScores(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3));
   }

   public List<Tuple> zrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public List<Tuple> zrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public List<String> zrevrangeByScore(String var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4));
   }

   public List<String> zrevrangeByScore(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3));
   }

   public List<String> zrevrangeByScore(String var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var4, var6, var7));
   }

   public List<Tuple> zrevrangeByScoreWithScores(String var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4));
   }

   public List<Tuple> zrevrangeByScoreWithScores(String var1, double var2, double var4, int var6, int var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var4, var6, var7));
   }

   public List<Tuple> zrevrangeByScoreWithScores(String var1, String var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3, var4, var5));
   }

   public List<String> zrevrangeByScore(String var1, String var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScore(var1, var2, var3, var4, var5));
   }

   public List<Tuple> zrevrangeByScoreWithScores(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByScoreWithScores(var1, var2, var3));
   }

   public long zremrangeByRank(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByRank(var1, var2, var4));
   }

   public long zremrangeByScore(String var1, double var2, double var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByScore(var1, var2, var4));
   }

   public long zremrangeByScore(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByScore(var1, var2, var3));
   }

   public List<String> zunion(ZParams var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zunion(var1, var2));
   }

   public List<Tuple> zunionWithScores(ZParams var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zunionWithScores(var1, var2));
   }

   public long zunionstore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zunionstore(var1, var2));
   }

   public long zunionstore(String var1, ZParams var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zunionstore(var1, var2, var3));
   }

   public List<String> zinter(ZParams var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zinter(var1, var2));
   }

   public List<Tuple> zinterWithScores(ZParams var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zinterWithScores(var1, var2));
   }

   public long zintercard(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zintercard(var1));
   }

   public long zintercard(long var1, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zintercard(var1, var3));
   }

   public long zinterstore(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zinterstore(var1, var2));
   }

   public long zinterstore(String var1, ZParams var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zinterstore(var1, var2, var3));
   }

   public long zlexcount(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zlexcount(var1, var2, var3));
   }

   public List<String> zrangeByLex(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByLex(var1, var2, var3));
   }

   public List<String> zrangeByLex(String var1, String var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrangeByLex(var1, var2, var3, var4, var5));
   }

   public List<String> zrevrangeByLex(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3));
   }

   public List<String> zrevrangeByLex(String var1, String var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.zrevrangeByLex(var1, var2, var3, var4, var5));
   }

   public long zremrangeByLex(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.zremrangeByLex(var1, var2, var3));
   }

   public KeyValue<String, List<Tuple>> zmpop(SortedSetOption var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zmpop(var1, var2));
   }

   public KeyValue<String, List<Tuple>> zmpop(SortedSetOption var1, int var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.zmpop(var1, var2, var3));
   }

   public KeyValue<String, List<Tuple>> bzmpop(double var1, SortedSetOption var3, String... var4) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzmpop(var1, var3, var4));
   }

   public KeyValue<String, List<Tuple>> bzmpop(double var1, SortedSetOption var3, int var4, String... var5) {
      this.checkIsInMultiOrPipeline();
      return (KeyValue)this.connection.executeCommand(this.commandObjects.bzmpop(var1, var3, var4, var5));
   }

   public long strlen(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.strlen(var1));
   }

   public LCSMatchResult lcs(String var1, String var2, LCSParams var3) {
      this.checkIsInMultiOrPipeline();
      return (LCSMatchResult)this.connection.executeCommand(this.commandObjects.lcs(var1, var2, var3));
   }

   public long lpushx(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.lpushx(var1, var2));
   }

   public long persist(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.persist(var1));
   }

   public long rpushx(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.rpushx(var1, var2));
   }

   public String echo(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ECHO, (String[])(var1));
      return this.connection.getBulkReply();
   }

   public long linsert(String var1, ListPosition var2, String var3, String var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.linsert(var1, var2, var3, var4));
   }

   public String brpoplpush(String var1, String var2, int var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.brpoplpush(var1, var2, var3));
   }

   public boolean setbit(String var1, long var2, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.setbit(var1, var2, var4));
   }

   public boolean getbit(String var1, long var2) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.getbit(var1, var2));
   }

   public long setrange(String var1, long var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.setrange(var1, var2, var4));
   }

   public String getrange(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.getrange(var1, var2, var4));
   }

   public long bitpos(String var1, boolean var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitpos(var1, var2));
   }

   public long bitpos(String var1, boolean var2, BitPosParams var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitpos(var1, var2, var3));
   }

   public List<Object> role() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.ROLE);
      return BuilderFactory.ENCODED_OBJECT_LIST.build(this.connection.getOne());
   }

   public Map<String, String> configGet(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (String[])(Protocol.Keyword.GET.name(), var1));
      return BuilderFactory.STRING_MAP.build(this.connection.getOne());
   }

   public Map<String, String> configGet(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (String[])joinParameters(Protocol.Keyword.GET.name(), var1));
      return BuilderFactory.STRING_MAP.build(this.connection.getOne());
   }

   public String configSet(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (String[])(Protocol.Keyword.SET.name(), var1, var2));
      return this.connection.getStatusCodeReply();
   }

   public String configSet(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CONFIG, (String[])joinParameters(Protocol.Keyword.SET.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String configSet(Map<String, String> var1) {
      this.checkIsInMultiOrPipeline();
      CommandArguments var2 = (new CommandArguments(Protocol.Command.CONFIG)).add(Protocol.Keyword.SET);
      var1.forEach((var1x, var2x) -> var2.add(var1x).add(var2x));
      this.connection.sendCommand(var2);
      return this.connection.getStatusCodeReply();
   }

   public long publish(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBLISH, (String[])(var1, var2));
      return this.connection.getIntegerReply();
   }

   public void subscribe(JedisPubSub var1, String... var2) {
      var1.proceed(this.connection, var2);
   }

   public void psubscribe(JedisPubSub var1, String... var2) {
      var1.proceedWithPatterns(this.connection, var2);
   }

   public List<String> pubsubChannels() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (Rawable)Protocol.Keyword.CHANNELS);
      return this.connection.getMultiBulkReply();
   }

   public List<String> pubsubChannels(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (String[])(Protocol.Keyword.CHANNELS.name(), var1));
      return this.connection.getMultiBulkReply();
   }

   public Long pubsubNumPat() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (Rawable)Protocol.Keyword.NUMPAT);
      return this.connection.getIntegerReply();
   }

   public Map<String, Long> pubsubNumSub(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (String[])joinParameters(Protocol.Keyword.NUMSUB.name(), var1));
      return BuilderFactory.PUBSUB_NUMSUB_MAP.build(this.connection.getOne());
   }

   public List<String> pubsubShardChannels() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (Rawable)Protocol.Keyword.SHARDCHANNELS);
      return this.connection.getMultiBulkReply();
   }

   public List<String> pubsubShardChannels(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (String[])(Protocol.Keyword.SHARDCHANNELS.name(), var1));
      return this.connection.getMultiBulkReply();
   }

   public Map<String, Long> pubsubShardNumSub(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.PUBSUB, (String[])joinParameters(Protocol.Keyword.SHARDNUMSUB.name(), var1));
      return BuilderFactory.PUBSUB_NUMSUB_MAP.build(this.connection.getOne());
   }

   public Object eval(String var1, int var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Object eval(String var1, List<String> var2, List<String> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.eval(var1, var2, var3));
   }

   public Object evalReadonly(String var1, List<String> var2, List<String> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalReadonly(var1, var2, var3));
   }

   public Object eval(String var1) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.eval(var1));
   }

   public Object evalsha(String var1) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalsha(var1));
   }

   public Object evalsha(String var1, List<String> var2, List<String> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Object evalshaReadonly(String var1, List<String> var2, List<String> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalshaReadonly(var1, var2, var3));
   }

   public Object evalsha(String var1, int var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.evalsha(var1, var2, var3));
   }

   public Boolean scriptExists(String var1) {
      String[] var2 = new String[]{var1};
      return (Boolean)this.scriptExists(var2).get(0);
   }

   public List<Boolean> scriptExists(String... var1) {
      this.connection.sendCommand(Protocol.Command.SCRIPT, (String[])joinParameters(Protocol.Keyword.EXISTS.name(), var1));
      return BuilderFactory.BOOLEAN_LIST.build(this.connection.getOne());
   }

   public String scriptLoad(String var1) {
      this.connection.sendCommand(Protocol.Command.SCRIPT, (String[])(Protocol.Keyword.LOAD.name(), var1));
      return this.connection.getBulkReply();
   }

   public List<Slowlog> slowlogGet() {
      this.connection.sendCommand(Protocol.Command.SLOWLOG, (Rawable)Protocol.Keyword.GET);
      return Slowlog.from(this.connection.getObjectMultiBulkReply());
   }

   public List<Slowlog> slowlogGet(long var1) {
      this.connection.sendCommand(Protocol.Command.SLOWLOG, (byte[][])(Protocol.Keyword.GET.getRaw(), Protocol.toByteArray(var1)));
      return Slowlog.from(this.connection.getObjectMultiBulkReply());
   }

   public Long objectRefcount(String var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (String[])(Protocol.Keyword.REFCOUNT.name(), var1));
      return this.connection.getIntegerReply();
   }

   public String objectEncoding(String var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (String[])(Protocol.Keyword.ENCODING.name(), var1));
      return this.connection.getBulkReply();
   }

   public Long objectIdletime(String var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (String[])(Protocol.Keyword.IDLETIME.name(), var1));
      return this.connection.getIntegerReply();
   }

   public List<String> objectHelp() {
      this.connection.sendCommand(Protocol.Command.OBJECT, (Rawable)Protocol.Keyword.HELP);
      return this.connection.getMultiBulkReply();
   }

   public Long objectFreq(String var1) {
      this.connection.sendCommand(Protocol.Command.OBJECT, (String[])(Protocol.Keyword.FREQ.name(), var1));
      return this.connection.getIntegerReply();
   }

   public long bitcount(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitcount(var1));
   }

   public long bitcount(String var1, long var2, long var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitcount(var1, var2, var4));
   }

   public long bitcount(String var1, long var2, long var4, BitCountOption var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitcount(var1, var2, var4, var6));
   }

   public long bitop(BitOP var1, String var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.bitop(var1, var2, var3));
   }

   public long commandCount() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.COMMAND, (Rawable)Protocol.Keyword.COUNT);
      return this.connection.getIntegerReply();
   }

   public Map<String, CommandDocument> commandDocs(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.COMMAND, (String[])joinParameters(Protocol.Keyword.DOCS.name(), var1));
      return BuilderFactory.COMMAND_DOCS_RESPONSE.build(this.connection.getOne());
   }

   public List<String> commandGetKeys(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.COMMAND, (String[])joinParameters(Protocol.Keyword.GETKEYS.name(), var1));
      return BuilderFactory.STRING_LIST.build(this.connection.getOne());
   }

   public List<KeyValue<String, List<String>>> commandGetKeysAndFlags(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.COMMAND, (String[])joinParameters(Protocol.Keyword.GETKEYSANDFLAGS.name(), var1));
      return BuilderFactory.KEYED_STRING_LIST_LIST.build(this.connection.getOne());
   }

   public Map<String, CommandInfo> commandInfo(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.COMMAND, (String[])joinParameters(Protocol.Keyword.INFO.name(), var1));
      return BuilderFactory.COMMAND_INFO_RESPONSE.build(this.connection.getOne());
   }

   public List<String> commandList() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.COMMAND, (Rawable)Protocol.Keyword.LIST);
      return BuilderFactory.STRING_LIST.build(this.connection.getOne());
   }

   public List<String> commandListFilterBy(CommandListFilterByParams var1) {
      this.checkIsInMultiOrPipeline();
      CommandArguments var2 = (new CommandArguments(Protocol.Command.COMMAND)).add(Protocol.Keyword.LIST).addParams(var1);
      this.connection.sendCommand(var2);
      return BuilderFactory.STRING_LIST.build(this.connection.getOne());
   }

   public String sentinelMyId() {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (Rawable)Protocol.SentinelKeyword.MYID);
      return this.connection.getBulkReply();
   }

   public List<Map<String, String>> sentinelMasters() {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (Rawable)Protocol.SentinelKeyword.MASTERS);
      Stream var10000 = this.connection.getObjectMultiBulkReply().stream();
      Builder var10001 = BuilderFactory.STRING_MAP;
      var10001.getClass();
      return (List)var10000.map(var10001::build).collect(Collectors.toList());
   }

   public Map<String, String> sentinelMaster(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.MASTER.name(), var1));
      return BuilderFactory.STRING_MAP.build(this.connection.getOne());
   }

   public List<Map<String, String>> sentinelSentinels(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.SENTINELS.name(), var1));
      Stream var10000 = this.connection.getObjectMultiBulkReply().stream();
      Builder var10001 = BuilderFactory.STRING_MAP;
      var10001.getClass();
      return (List)var10000.map(var10001::build).collect(Collectors.toList());
   }

   public List<String> sentinelGetMasterAddrByName(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (byte[][])(Protocol.SentinelKeyword.GET_MASTER_ADDR_BY_NAME.getRaw(), SafeEncoder.encode(var1)));
      return this.connection.getMultiBulkReply();
   }

   public Long sentinelReset(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.RESET.name(), var1));
      return this.connection.getIntegerReply();
   }

   @Deprecated
   public List<Map<String, String>> sentinelSlaves(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.SLAVES.name(), var1));
      Stream var10000 = this.connection.getObjectMultiBulkReply().stream();
      Builder var10001 = BuilderFactory.STRING_MAP;
      var10001.getClass();
      return (List)var10000.map(var10001::build).collect(Collectors.toList());
   }

   public List<Map<String, String>> sentinelReplicas(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.REPLICAS.name(), var1));
      Stream var10000 = this.connection.getObjectMultiBulkReply().stream();
      Builder var10001 = BuilderFactory.STRING_MAP;
      var10001.getClass();
      return (List)var10000.map(var10001::build).collect(Collectors.toList());
   }

   public String sentinelFailover(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.FAILOVER.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String sentinelMonitor(String var1, String var2, int var3, int var4) {
      CommandArguments var5 = (new CommandArguments(Protocol.Command.SENTINEL)).add(Protocol.SentinelKeyword.MONITOR).add(var1).add(var2).add(var3).add(var4);
      this.connection.sendCommand(var5);
      return this.connection.getStatusCodeReply();
   }

   public String sentinelRemove(String var1) {
      this.connection.sendCommand(Protocol.Command.SENTINEL, (String[])(Protocol.SentinelKeyword.REMOVE.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String sentinelSet(String var1, Map<String, String> var2) {
      CommandArguments var3 = (new CommandArguments(Protocol.Command.SENTINEL)).add(Protocol.SentinelKeyword.SET).add(var1);
      var2.entrySet().forEach((var1x) -> var3.add(var1x.getKey()).add(var1x.getValue()));
      this.connection.sendCommand(var3);
      return this.connection.getStatusCodeReply();
   }

   public byte[] dump(String var1) {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.dump(var1));
   }

   public String restore(String var1, long var2, byte[] var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.restore(var1, var2, var4));
   }

   public String restore(String var1, long var2, byte[] var4, RestoreParams var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.restore(var1, var2, var4, var5));
   }

   public long pttl(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pttl(var1));
   }

   public String psetex(String var1, long var2, String var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.psetex(var1, var2, var4));
   }

   public String aclSetUser(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (String[])(Protocol.Keyword.SETUSER.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String aclSetUser(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (String[])joinParameters(Protocol.Keyword.SETUSER.name(), var1, var2));
      return this.connection.getStatusCodeReply();
   }

   public long aclDelUser(String... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (String[])joinParameters(Protocol.Keyword.DELUSER.name(), var1));
      return this.connection.getIntegerReply();
   }

   public AccessControlUser aclGetUser(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (String[])(Protocol.Keyword.GETUSER.name(), var1));
      return BuilderFactory.ACCESS_CONTROL_USER.build(this.connection.getOne());
   }

   public List<String> aclUsers() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.USERS);
      return BuilderFactory.STRING_LIST.build(this.connection.getObjectMultiBulkReply());
   }

   public List<String> aclList() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.LIST);
      return this.connection.getMultiBulkReply();
   }

   public String aclWhoAmI() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.WHOAMI);
      return this.connection.getStatusCodeReply();
   }

   public List<String> aclCat() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.CAT);
      return BuilderFactory.STRING_LIST.build(this.connection.getOne());
   }

   public List<String> aclCat(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (String[])(Protocol.Keyword.CAT.name(), var1));
      return BuilderFactory.STRING_LIST.build(this.connection.getOne());
   }

   public List<AccessControlLogEntry> aclLog() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.LOG);
      return BuilderFactory.ACCESS_CONTROL_LOG_ENTRY_LIST.build(this.connection.getOne());
   }

   public List<AccessControlLogEntry> aclLog(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.LOG.getRaw(), Protocol.toByteArray(var1)));
      return BuilderFactory.ACCESS_CONTROL_LOG_ENTRY_LIST.build(this.connection.getOne());
   }

   public String aclLoad() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.LOAD);
      return this.connection.getStatusCodeReply();
   }

   public String aclSave() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.SAVE);
      return this.connection.getStatusCodeReply();
   }

   public String aclGenPass() {
      this.connection.sendCommand(Protocol.Command.ACL, (Rawable)Protocol.Keyword.GENPASS);
      return this.connection.getBulkReply();
   }

   public String aclGenPass(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])(Protocol.Keyword.GENPASS.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getBulkReply();
   }

   public String aclDryRun(String var1, String var2, String... var3) {
      this.checkIsInMultiOrPipeline();
      String[] var4 = new String[3 + var3.length];
      var4[0] = Protocol.Keyword.DRYRUN.name();
      var4[1] = var1;
      var4[2] = var2;
      System.arraycopy(var3, 0, var4, 3, var3.length);
      this.connection.sendCommand(Protocol.Command.ACL, (String[])var4);
      return this.connection.getBulkReply();
   }

   public String aclDryRun(String var1, CommandArguments var2) {
      this.checkIsInMultiOrPipeline();
      CommandArguments var3 = (new CommandArguments(Protocol.Command.ACL)).add(Protocol.Keyword.DRYRUN).add(var1);
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         var3.add(var4.next());
      }

      this.connection.sendCommand(var3);
      return this.connection.getBulkReply();
   }

   public byte[] aclDryRunBinary(byte[] var1, byte[] var2, byte[]... var3) {
      this.checkIsInMultiOrPipeline();
      byte[][] var4 = new byte[3 + var3.length][];
      var4[0] = Protocol.Keyword.DRYRUN.getRaw();
      var4[1] = var1;
      var4[2] = var2;
      System.arraycopy(var3, 0, var4, 3, var3.length);
      this.connection.sendCommand(Protocol.Command.ACL, (byte[][])var4);
      return this.connection.getBinaryBulkReply();
   }

   public byte[] aclDryRunBinary(byte[] var1, CommandArguments var2) {
      this.checkIsInMultiOrPipeline();
      CommandArguments var3 = (new CommandArguments(Protocol.Command.ACL)).add(Protocol.Keyword.DRYRUN).add(var1);
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         var3.add(var4.next());
      }

      this.connection.sendCommand(var3);
      return this.connection.getBinaryBulkReply();
   }

   public String clientKill(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (String[])(Protocol.Keyword.KILL.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String clientGetname() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.GETNAME);
      return this.connection.getBulkReply();
   }

   public String clientList() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.LIST);
      return this.connection.getBulkReply();
   }

   public String clientList(ClientType var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.LIST.getRaw(), Protocol.Keyword.TYPE.getRaw(), var1.getRaw()));
      return this.connection.getBulkReply();
   }

   public String clientList(long... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])this.clientListParams(var1));
      return this.connection.getBulkReply();
   }

   public String clientInfo() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (Rawable)Protocol.Keyword.INFO);
      return this.connection.getBulkReply();
   }

   public String clientSetInfo(ClientAttributeOption var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (byte[][])(Protocol.Keyword.SETINFO.getRaw(), var1.getRaw(), SafeEncoder.encode(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String clientSetname(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLIENT, (String[])(Protocol.Keyword.SETNAME.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String migrate(String var1, int var2, String var3, int var4, int var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public String migrate(String var1, int var2, int var3, int var4, MigrateParams var5, String... var6) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5, var6));
   }

   public String migrate(String var1, int var2, String var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4));
   }

   public String migrate(String var1, int var2, int var3, MigrateParams var4, String... var5) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public ScanResult<String> scan(String var1) {
      return (ScanResult)this.connection.executeCommand(this.commandObjects.scan(var1));
   }

   public ScanResult<String> scan(String var1, ScanParams var2) {
      return (ScanResult)this.connection.executeCommand(this.commandObjects.scan(var1, var2));
   }

   public ScanResult<String> scan(String var1, ScanParams var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.scan(var1, var2, var3));
   }

   public ScanResult<Map.Entry<String, String>> hscan(String var1, String var2, ScanParams var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.hscan(var1, var2, var3));
   }

   public ScanResult<String> sscan(String var1, String var2, ScanParams var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.sscan(var1, var2, var3));
   }

   public ScanResult<Tuple> zscan(String var1, String var2, ScanParams var3) {
      this.checkIsInMultiOrPipeline();
      return (ScanResult)this.connection.executeCommand(this.commandObjects.zscan(var1, var2, var3));
   }

   public String readonly() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.READONLY);
      return this.connection.getStatusCodeReply();
   }

   public String readwrite() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.READWRITE);
      return this.connection.getStatusCodeReply();
   }

   public String clusterNodes() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.NODES);
      return this.connection.getBulkReply();
   }

   public String clusterMeet(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])(Protocol.ClusterKeyword.MEET.name(), var1, Integer.toString(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterReset() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.RESET);
      return this.connection.getStatusCodeReply();
   }

   public String clusterReset(ClusterResetType var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.RESET.getRaw(), var1.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public String clusterAddSlots(int... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])joinParameters(Protocol.ClusterKeyword.ADDSLOTS.getRaw(), joinParameters(var1)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterDelSlots(int... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])joinParameters(Protocol.ClusterKeyword.DELSLOTS.getRaw(), joinParameters(var1)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterInfo() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.INFO);
      return this.connection.getStatusCodeReply();
   }

   public List<String> clusterGetKeysInSlot(int var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.GETKEYSINSLOT.getRaw(), Protocol.toByteArray(var1), Protocol.toByteArray(var2)));
      return this.connection.getMultiBulkReply();
   }

   public List<byte[]> clusterGetKeysInSlotBinary(int var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.GETKEYSINSLOT.getRaw(), Protocol.toByteArray(var1), Protocol.toByteArray(var2)));
      return this.connection.getBinaryMultiBulkReply();
   }

   public String clusterSetSlotNode(int var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.SETSLOT.getRaw(), Protocol.toByteArray(var1), Protocol.ClusterKeyword.NODE.getRaw(), SafeEncoder.encode(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterSetSlotMigrating(int var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.SETSLOT.getRaw(), Protocol.toByteArray(var1), Protocol.ClusterKeyword.MIGRATING.getRaw(), SafeEncoder.encode(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterSetSlotImporting(int var1, String var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.SETSLOT.getRaw(), Protocol.toByteArray(var1), Protocol.ClusterKeyword.IMPORTING.getRaw(), SafeEncoder.encode(var2)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterSetSlotStable(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.SETSLOT.getRaw(), Protocol.toByteArray(var1), Protocol.ClusterKeyword.STABLE.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   public String clusterForget(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])(Protocol.ClusterKeyword.FORGET.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String clusterFlushSlots() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.FLUSHSLOTS);
      return this.connection.getStatusCodeReply();
   }

   public long clusterKeySlot(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])(Protocol.ClusterKeyword.KEYSLOT.name(), var1));
      return this.connection.getIntegerReply();
   }

   public long clusterCountFailureReports(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])("COUNT-FAILURE-REPORTS", var1));
      return this.connection.getIntegerReply();
   }

   public long clusterCountKeysInSlot(int var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.COUNTKEYSINSLOT.getRaw(), Protocol.toByteArray(var1)));
      return this.connection.getIntegerReply();
   }

   public String clusterSaveConfig() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.SAVECONFIG);
      return this.connection.getStatusCodeReply();
   }

   public String clusterSetConfigEpoch(long var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])("SET-CONFIG-EPOCH", Long.toString(var1)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterBumpEpoch() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.BUMPEPOCH);
      return this.connection.getBulkReply();
   }

   public String clusterReplicate(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])(Protocol.ClusterKeyword.REPLICATE.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   @Deprecated
   public List<String> clusterSlaves(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])(Protocol.ClusterKeyword.SLAVES.name(), var1));
      return this.connection.getMultiBulkReply();
   }

   public List<String> clusterReplicas(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (String[])(Protocol.ClusterKeyword.REPLICAS.name(), var1));
      return this.connection.getMultiBulkReply();
   }

   public String clusterFailover() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.FAILOVER);
      return this.connection.getStatusCodeReply();
   }

   public String clusterFailover(ClusterFailoverOption var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])(Protocol.ClusterKeyword.FAILOVER.getRaw(), var1.getRaw()));
      return this.connection.getStatusCodeReply();
   }

   @Deprecated
   public List<Object> clusterSlots() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.SLOTS);
      return this.connection.getObjectMultiBulkReply();
   }

   public List<ClusterShardInfo> clusterShards() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.SHARDS);
      return BuilderFactory.CLUSTER_SHARD_INFO_LIST.build(this.connection.getObjectMultiBulkReply());
   }

   public String clusterMyId() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.MYID);
      return this.connection.getBulkReply();
   }

   public String clusterMyShardId() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.MYSHARDID);
      return this.connection.getBulkReply();
   }

   public List<Map<String, Object>> clusterLinks() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (Rawable)Protocol.ClusterKeyword.LINKS);
      Stream var10000 = this.connection.getObjectMultiBulkReply().stream();
      Builder var10001 = BuilderFactory.ENCODED_OBJECT_MAP;
      var10001.getClass();
      return (List)var10000.map(var10001::build).collect(Collectors.toList());
   }

   public String clusterAddSlotsRange(int... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])joinParameters(Protocol.ClusterKeyword.ADDSLOTSRANGE.getRaw(), joinParameters(var1)));
      return this.connection.getStatusCodeReply();
   }

   public String clusterDelSlotsRange(int... var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.CLUSTER, (byte[][])joinParameters(Protocol.ClusterKeyword.DELSLOTSRANGE.getRaw(), joinParameters(var1)));
      return this.connection.getStatusCodeReply();
   }

   public String asking() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.ASKING);
      return this.connection.getStatusCodeReply();
   }

   public long pfadd(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pfadd(var1, var2));
   }

   public long pfcount(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pfcount(var1));
   }

   public long pfcount(String... var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.pfcount(var1));
   }

   public String pfmerge(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.pfmerge(var1, var2));
   }

   public Object fcall(String var1, List<String> var2, List<String> var3) {
      return this.connection.executeCommand(this.commandObjects.fcall(var1, var2, var3));
   }

   public Object fcallReadonly(String var1, List<String> var2, List<String> var3) {
      return this.connection.executeCommand(this.commandObjects.fcallReadonly(var1, var2, var3));
   }

   public String functionDelete(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionDelete(var1));
   }

   public String functionLoad(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionLoad(var1));
   }

   public String functionLoadReplace(String var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionLoadReplace(var1));
   }

   public FunctionStats functionStats() {
      this.checkIsInMultiOrPipeline();
      return (FunctionStats)this.connection.executeCommand(this.commandObjects.functionStats());
   }

   public String functionFlush() {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionFlush());
   }

   public String functionFlush(FlushMode var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionFlush(var1));
   }

   public String functionKill() {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionKill());
   }

   public List<LibraryInfo> functionList() {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionList());
   }

   public List<LibraryInfo> functionList(String var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionList(var1));
   }

   public List<LibraryInfo> functionListWithCode() {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionListWithCode());
   }

   public List<LibraryInfo> functionListWithCode(String var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionListWithCode(var1));
   }

   public long geoadd(String var1, double var2, double var4, String var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geoadd(var1, var2, var4, var6));
   }

   public long geoadd(String var1, Map<String, GeoCoordinate> var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geoadd(var1, var2));
   }

   public long geoadd(String var1, GeoAddParams var2, Map<String, GeoCoordinate> var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geoadd(var1, var2, var3));
   }

   public Double geodist(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.geodist(var1, var2, var3));
   }

   public Double geodist(String var1, String var2, String var3, GeoUnit var4) {
      this.checkIsInMultiOrPipeline();
      return (Double)this.connection.executeCommand(this.commandObjects.geodist(var1, var2, var3, var4));
   }

   public List<String> geohash(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geohash(var1, var2));
   }

   public List<GeoCoordinate> geopos(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geopos(var1, var2));
   }

   public List<GeoRadiusResponse> georadius(String var1, double var2, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8));
   }

   public List<GeoRadiusResponse> georadiusReadonly(String var1, double var2, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8));
   }

   public List<GeoRadiusResponse> georadius(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadius(var1, var2, var4, var6, var8, var9));
   }

   public long georadiusStore(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9, GeoRadiusStoreParam var10) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.georadiusStore(var1, var2, var4, var6, var8, var9, var10));
   }

   public List<GeoRadiusResponse> georadiusReadonly(String var1, double var2, double var4, double var6, GeoUnit var8, GeoRadiusParam var9) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusReadonly(var1, var2, var4, var6, var8, var9));
   }

   public List<GeoRadiusResponse> georadiusByMember(String var1, String var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> georadiusByMemberReadonly(String var1, String var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> georadiusByMember(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMember(var1, var2, var3, var5, var6));
   }

   public long georadiusByMemberStore(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6, GeoRadiusStoreParam var7) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.georadiusByMemberStore(var1, var2, var3, var5, var6, var7));
   }

   public List<GeoRadiusResponse> georadiusByMemberReadonly(String var1, String var2, double var3, GeoUnit var5, GeoRadiusParam var6) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.georadiusByMemberReadonly(var1, var2, var3, var5, var6));
   }

   public List<GeoRadiusResponse> geosearch(String var1, String var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> geosearch(String var1, GeoCoordinate var2, double var3, GeoUnit var5) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5));
   }

   public List<GeoRadiusResponse> geosearch(String var1, String var2, double var3, double var5, GeoUnit var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public List<GeoRadiusResponse> geosearch(String var1, GeoCoordinate var2, double var3, double var5, GeoUnit var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2, var3, var5, var7));
   }

   public List<GeoRadiusResponse> geosearch(String var1, GeoSearchParam var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.geosearch(var1, var2));
   }

   public long geosearchStore(String var1, String var2, String var3, double var4, GeoUnit var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public long geosearchStore(String var1, String var2, GeoCoordinate var3, double var4, GeoUnit var6) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6));
   }

   public long geosearchStore(String var1, String var2, String var3, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public long geosearchStore(String var1, String var2, GeoCoordinate var3, double var4, double var6, GeoUnit var8) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3, var4, var6, var8));
   }

   public long geosearchStore(String var1, String var2, GeoSearchParam var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStore(var1, var2, var3));
   }

   public long geosearchStoreStoreDist(String var1, String var2, GeoSearchParam var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.geosearchStoreStoreDist(var1, var2, var3));
   }

   public String moduleLoad(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MODULE, (String[])(Protocol.Keyword.LOAD.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public String moduleLoad(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MODULE, (String[])joinParameters(Protocol.Keyword.LOAD.name(), var1, var2));
      return this.connection.getStatusCodeReply();
   }

   public String moduleLoadEx(String var1, ModuleLoadExParams var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((new CommandArguments(Protocol.Command.MODULE)).add(Protocol.Keyword.LOADEX).add(var1).addParams(var2));
      return this.connection.getStatusCodeReply();
   }

   public String moduleUnload(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MODULE, (String[])(Protocol.Keyword.UNLOAD.name(), var1));
      return this.connection.getStatusCodeReply();
   }

   public List<Module> moduleList() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MODULE, (Rawable)Protocol.Keyword.LIST);
      return BuilderFactory.MODULE_LIST.build(this.connection.getOne());
   }

   public List<Long> bitfield(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.bitfield(var1, var2));
   }

   public List<Long> bitfieldReadonly(String var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.bitfieldReadonly(var1, var2));
   }

   public long hstrlen(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.hstrlen(var1, var2));
   }

   public String memoryDoctor() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (Rawable)Protocol.Keyword.DOCTOR);
      return this.connection.getBulkReply();
   }

   public Long memoryUsage(String var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (String[])(Protocol.Keyword.USAGE.name(), var1));
      return this.connection.getIntegerReply();
   }

   public Long memoryUsage(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (byte[][])(Protocol.Keyword.USAGE.getRaw(), SafeEncoder.encode(var1), Protocol.Keyword.SAMPLES.getRaw(), Protocol.toByteArray(var2)));
      return this.connection.getIntegerReply();
   }

   public String memoryPurge() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (Rawable)Protocol.Keyword.PURGE);
      return this.connection.getBulkReply();
   }

   public Map<String, Object> memoryStats() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.MEMORY, (Rawable)Protocol.Keyword.STATS);
      return BuilderFactory.ENCODED_OBJECT_MAP.build(this.connection.getOne());
   }

   public String lolwut() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.LOLWUT);
      return this.connection.getBulkReply();
   }

   public String lolwut(LolwutParams var1) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand((new CommandArguments(Protocol.Command.LOLWUT)).addParams(var1));
      return this.connection.getBulkReply();
   }

   public String reset() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.RESET);
      return this.connection.getStatusCodeReply();
   }

   public String latencyDoctor() {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(Protocol.Command.LATENCY, (Rawable)Protocol.Keyword.DOCTOR);
      return this.connection.getBulkReply();
   }

   public StreamEntryID xadd(String var1, StreamEntryID var2, Map<String, String> var3) {
      this.checkIsInMultiOrPipeline();
      return (StreamEntryID)this.connection.executeCommand(this.commandObjects.xadd(var1, var2, var3));
   }

   public StreamEntryID xadd(String var1, XAddParams var2, Map<String, String> var3) {
      this.checkIsInMultiOrPipeline();
      return (StreamEntryID)this.connection.executeCommand(this.commandObjects.xadd(var1, var2, var3));
   }

   public long xlen(String var1) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xlen(var1));
   }

   public List<StreamEntry> xrange(String var1, StreamEntryID var2, StreamEntryID var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrange(var1, var2, var3));
   }

   public List<StreamEntry> xrange(String var1, StreamEntryID var2, StreamEntryID var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrange(var1, var2, var3, var4));
   }

   public List<StreamEntry> xrevrange(String var1, StreamEntryID var2, StreamEntryID var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrevrange(var1, var2, var3));
   }

   public List<StreamEntry> xrevrange(String var1, StreamEntryID var2, StreamEntryID var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrevrange(var1, var2, var3, var4));
   }

   public List<StreamEntry> xrange(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrange(var1, var2, var3));
   }

   public List<StreamEntry> xrange(String var1, String var2, String var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrange(var1, var2, var3, var4));
   }

   public List<StreamEntry> xrevrange(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrevrange(var1, var2, var3));
   }

   public List<StreamEntry> xrevrange(String var1, String var2, String var3, int var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xrevrange(var1, var2, var3, var4));
   }

   public List<Map.Entry<String, List<StreamEntry>>> xread(XReadParams var1, Map<String, StreamEntryID> var2) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xread(var1, var2));
   }

   public long xack(String var1, String var2, StreamEntryID... var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xack(var1, var2, var3));
   }

   public String xgroupCreate(String var1, String var2, StreamEntryID var3, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.xgroupCreate(var1, var2, var3, var4));
   }

   public String xgroupSetID(String var1, String var2, StreamEntryID var3) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.xgroupSetID(var1, var2, var3));
   }

   public long xgroupDestroy(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xgroupDestroy(var1, var2));
   }

   public boolean xgroupCreateConsumer(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Boolean)this.connection.executeCommand(this.commandObjects.xgroupCreateConsumer(var1, var2, var3));
   }

   public long xgroupDelConsumer(String var1, String var2, String var3) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xgroupDelConsumer(var1, var2, var3));
   }

   public long xdel(String var1, StreamEntryID... var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xdel(var1, var2));
   }

   public long xtrim(String var1, long var2, boolean var4) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xtrim(var1, var2, var4));
   }

   public long xtrim(String var1, XTrimParams var2) {
      this.checkIsInMultiOrPipeline();
      return (Long)this.connection.executeCommand(this.commandObjects.xtrim(var1, var2));
   }

   public List<Map.Entry<String, List<StreamEntry>>> xreadGroup(String var1, String var2, XReadGroupParams var3, Map<String, StreamEntryID> var4) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xreadGroup(var1, var2, var3, var4));
   }

   public StreamPendingSummary xpending(String var1, String var2) {
      this.checkIsInMultiOrPipeline();
      return (StreamPendingSummary)this.connection.executeCommand(this.commandObjects.xpending(var1, var2));
   }

   public List<StreamPendingEntry> xpending(String var1, String var2, XPendingParams var3) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xpending(var1, var2, var3));
   }

   public List<StreamEntry> xclaim(String var1, String var2, String var3, long var4, XClaimParams var6, StreamEntryID... var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xclaim(var1, var2, var3, var4, var6, var7));
   }

   public List<StreamEntryID> xclaimJustId(String var1, String var2, String var3, long var4, XClaimParams var6, StreamEntryID... var7) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.xclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public Map.Entry<StreamEntryID, List<StreamEntry>> xautoclaim(String var1, String var2, String var3, long var4, StreamEntryID var6, XAutoClaimParams var7) {
      this.checkIsInMultiOrPipeline();
      return (Map.Entry)this.connection.executeCommand(this.commandObjects.xautoclaim(var1, var2, var3, var4, var6, var7));
   }

   public Map.Entry<StreamEntryID, List<StreamEntryID>> xautoclaimJustId(String var1, String var2, String var3, long var4, StreamEntryID var6, XAutoClaimParams var7) {
      this.checkIsInMultiOrPipeline();
      return (Map.Entry)this.connection.executeCommand(this.commandObjects.xautoclaimJustId(var1, var2, var3, var4, var6, var7));
   }

   public StreamInfo xinfoStream(String var1) {
      return (StreamInfo)this.connection.executeCommand(this.commandObjects.xinfoStream(var1));
   }

   public StreamFullInfo xinfoStreamFull(String var1) {
      this.checkIsInMultiOrPipeline();
      return (StreamFullInfo)this.connection.executeCommand(this.commandObjects.xinfoStreamFull(var1));
   }

   public StreamFullInfo xinfoStreamFull(String var1, int var2) {
      this.checkIsInMultiOrPipeline();
      return (StreamFullInfo)this.connection.executeCommand(this.commandObjects.xinfoStreamFull(var1, var2));
   }

   public List<StreamGroupInfo> xinfoGroups(String var1) {
      return (List)this.connection.executeCommand(this.commandObjects.xinfoGroups(var1));
   }

   public List<StreamConsumersInfo> xinfoConsumers(String var1, String var2) {
      return (List)this.connection.executeCommand(this.commandObjects.xinfoConsumers(var1, var2));
   }

   public List<StreamConsumerInfo> xinfoConsumers2(String var1, String var2) {
      return (List)this.connection.executeCommand(this.commandObjects.xinfoConsumers2(var1, var2));
   }

   public Object fcall(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.fcall(var1, var2, var3));
   }

   public Object fcallReadonly(byte[] var1, List<byte[]> var2, List<byte[]> var3) {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.fcallReadonly(var1, var2, var3));
   }

   public String functionDelete(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionDelete(var1));
   }

   public byte[] functionDump() {
      this.checkIsInMultiOrPipeline();
      return (byte[])this.connection.executeCommand(this.commandObjects.functionDump());
   }

   public List<Object> functionListBinary() {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionListBinary());
   }

   public List<Object> functionList(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionList(var1));
   }

   public List<Object> functionListWithCodeBinary() {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionListWithCodeBinary());
   }

   public List<Object> functionListWithCode(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (List)this.connection.executeCommand(this.commandObjects.functionListWithCode(var1));
   }

   public String functionLoad(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionLoad(var1));
   }

   public String functionLoadReplace(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionLoadReplace(var1));
   }

   public String functionRestore(byte[] var1) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionRestore(var1));
   }

   public String functionRestore(byte[] var1, FunctionRestorePolicy var2) {
      this.checkIsInMultiOrPipeline();
      return (String)this.connection.executeCommand(this.commandObjects.functionRestore(var1, var2));
   }

   public Object functionStatsBinary() {
      this.checkIsInMultiOrPipeline();
      return this.connection.executeCommand(this.commandObjects.functionStatsBinary());
   }

   public Object sendCommand(ProtocolCommand var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(var1, var2);
      return this.connection.getOne();
   }

   public Object sendBlockingCommand(ProtocolCommand var1, String... var2) {
      this.checkIsInMultiOrPipeline();
      this.connection.sendCommand(var1, var2);
      this.connection.setTimeoutInfinite();

      Object var3;
      try {
         var3 = this.connection.getOne();
      } finally {
         this.connection.rollbackTimeout();
      }

      return var3;
   }

   private static byte[][] joinParameters(int... var0) {
      byte[][] var1 = new byte[var0.length][];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = Protocol.toByteArray(var0[var2]);
      }

      return var1;
   }

   private static byte[][] joinParameters(byte[] var0, byte[][] var1) {
      byte[][] var2 = new byte[var1.length + 1][];
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return var2;
   }

   private static byte[][] joinParameters(byte[] var0, byte[] var1, byte[][] var2) {
      byte[][] var3 = new byte[var2.length + 2][];
      var3[0] = var0;
      var3[1] = var1;
      System.arraycopy(var2, 0, var3, 2, var2.length);
      return var3;
   }

   private static String[] joinParameters(String var0, String[] var1) {
      String[] var2 = new String[var1.length + 1];
      var2[0] = var0;
      System.arraycopy(var1, 0, var2, 1, var1.length);
      return var2;
   }

   private static String[] joinParameters(String var0, String var1, String[] var2) {
      String[] var3 = new String[var2.length + 2];
      var3[0] = var0;
      var3[1] = var1;
      System.arraycopy(var2, 0, var3, 2, var2.length);
      return var3;
   }
}
