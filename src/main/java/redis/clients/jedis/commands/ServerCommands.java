package redis.clients.jedis.commands;

import redis.clients.jedis.args.FlushMode;
import redis.clients.jedis.args.SaveMode;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.LolwutParams;
import redis.clients.jedis.params.ShutdownParams;
import redis.clients.jedis.util.KeyValue;

public interface ServerCommands {
   String ping();

   String ping(String var1);

   String echo(String var1);

   byte[] echo(byte[] var1);

   String flushDB();

   String flushDB(FlushMode var1);

   String flushAll();

   String flushAll(FlushMode var1);

   String auth(String var1);

   String auth(String var1, String var2);

   String save();

   String bgsave();

   String bgsaveSchedule();

   String bgrewriteaof();

   long lastsave();

   void shutdown() throws JedisException;

   default void shutdown(SaveMode var1) throws JedisException {
      this.shutdown(ShutdownParams.shutdownParams().saveMode(var1));
   }

   void shutdown(ShutdownParams var1) throws JedisException;

   String shutdownAbort();

   String info();

   String info(String var1);

   @Deprecated
   String slaveof(String var1, int var2);

   @Deprecated
   String slaveofNoOne();

   String replicaof(String var1, int var2);

   String replicaofNoOne();

   long waitReplicas(int var1, long var2);

   KeyValue<Long, Long> waitAOF(long var1, long var3, long var5);

   String lolwut();

   String lolwut(LolwutParams var1);

   String reset();

   String latencyDoctor();
}
