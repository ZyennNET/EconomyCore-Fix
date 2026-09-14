package redis.clients.jedis;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import redis.clients.jedis.commands.DatabasePipelineCommands;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.graph.GraphCommandObjects;
import redis.clients.jedis.params.MigrateParams;
import redis.clients.jedis.util.KeyValue;

public class Pipeline extends PipelineBase implements DatabasePipelineCommands, Closeable {
   private final Queue<Response<?>> pipelinedResponses;
   protected final Connection connection;
   private final boolean closeConnection;

   public Pipeline(Jedis var1) {
      this(var1.getConnection(), false);
   }

   public Pipeline(Connection var1) {
      this(var1, false);
   }

   public Pipeline(Connection var1, boolean var2) {
      super(new CommandObjects());
      this.pipelinedResponses = new LinkedList();
      this.connection = var1;
      this.closeConnection = var2;
      RedisProtocol var3 = this.connection.getRedisProtocol();
      if (var3 != null) {
         this.commandObjects.setProtocol(var3);
      }

      this.setGraphCommands(new GraphCommandObjects(this.connection));
   }

   public final <T> Response<T> appendCommand(CommandObject<T> var1) {
      this.connection.sendCommand(var1.getArguments());
      Response var2 = new Response(var1.getBuilder());
      this.pipelinedResponses.add(var2);
      return var2;
   }

   public void close() {
      this.sync();
      if (this.closeConnection) {
         this.connection.close();
      }

   }

   public void sync() {
      if (this.hasPipelinedResponse()) {
         for(Object var3 : this.connection.getMany(this.pipelinedResponses.size())) {
            ((Response)this.pipelinedResponses.poll()).set(var3);
         }

      }
   }

   public List<Object> syncAndReturnAll() {
      if (!this.hasPipelinedResponse()) {
         return Collections.emptyList();
      } else {
         List var1 = this.connection.getMany(this.pipelinedResponses.size());
         ArrayList var2 = new ArrayList();

         for(Object var4 : var1) {
            try {
               Response var5 = (Response)this.pipelinedResponses.poll();
               var5.set(var4);
               var2.add(var5.get());
            } catch (JedisDataException var6) {
               var2.add(var6);
            }
         }

         return var2;
      }
   }

   public final boolean hasPipelinedResponse() {
      return this.pipelinedResponses.size() > 0;
   }

   public Response<Long> waitReplicas(int var1, long var2) {
      return this.<Long>appendCommand(this.commandObjects.waitReplicas(var1, var2));
   }

   public Response<KeyValue<Long, Long>> waitAOF(long var1, long var3, long var5) {
      return this.<KeyValue<Long, Long>>appendCommand(this.commandObjects.waitAOF(var1, var3, var5));
   }

   public Response<List<String>> time() {
      return this.<List<String>>appendCommand(new CommandObject(this.commandObjects.commandArguments(Protocol.Command.TIME), BuilderFactory.STRING_LIST));
   }

   public Response<String> select(int var1) {
      return this.<String>appendCommand(new CommandObject(this.commandObjects.commandArguments(Protocol.Command.SELECT).add(var1), BuilderFactory.STRING));
   }

   public Response<Long> dbSize() {
      return this.<Long>appendCommand(new CommandObject(this.commandObjects.commandArguments(Protocol.Command.DBSIZE), BuilderFactory.LONG));
   }

   public Response<String> swapDB(int var1, int var2) {
      return this.<String>appendCommand(new CommandObject(this.commandObjects.commandArguments(Protocol.Command.SWAPDB).add(var1).add(var2), BuilderFactory.STRING));
   }

   public Response<Long> move(String var1, int var2) {
      return this.<Long>appendCommand(new CommandObject(this.commandObjects.commandArguments(Protocol.Command.MOVE).key(var1).add(var2), BuilderFactory.LONG));
   }

   public Response<Long> move(byte[] var1, int var2) {
      return this.<Long>appendCommand(new CommandObject(this.commandObjects.commandArguments(Protocol.Command.MOVE).key(var1).add(var2), BuilderFactory.LONG));
   }

   public Response<Boolean> copy(String var1, String var2, int var3, boolean var4) {
      return this.<Boolean>appendCommand(this.commandObjects.copy(var1, var2, var3, var4));
   }

   public Response<Boolean> copy(byte[] var1, byte[] var2, int var3, boolean var4) {
      return this.<Boolean>appendCommand(this.commandObjects.copy(var1, var2, var3, var4));
   }

   public Response<String> migrate(String var1, int var2, byte[] var3, int var4, int var5) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public Response<String> migrate(String var1, int var2, String var3, int var4, int var5) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5));
   }

   public Response<String> migrate(String var1, int var2, int var3, int var4, MigrateParams var5, byte[]... var6) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5, var6));
   }

   public Response<String> migrate(String var1, int var2, int var3, int var4, MigrateParams var5, String... var6) {
      return this.<String>appendCommand(this.commandObjects.migrate(var1, var2, var3, var4, var5, var6));
   }
}
