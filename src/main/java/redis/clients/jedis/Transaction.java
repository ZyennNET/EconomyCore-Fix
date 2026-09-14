package redis.clients.jedis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.graph.GraphCommandObjects;

public class Transaction extends TransactionBase {
   private final Queue<Response<?>> pipelinedResponses;
   private Jedis jedis;
   protected final Connection connection;
   private final boolean closeConnection;
   private boolean broken;
   private boolean inWatch;
   private boolean inMulti;

   public Transaction(Jedis var1) {
      this(var1.getConnection());
      this.jedis = var1;
   }

   public Transaction(Connection var1) {
      this(var1, true);
   }

   public Transaction(Connection var1, boolean var2) {
      this(var1, var2, false);
   }

   public Transaction(Connection var1, boolean var2, boolean var3) {
      this.pipelinedResponses = new LinkedList();
      this.jedis = null;
      this.broken = false;
      this.inWatch = false;
      this.inMulti = false;
      this.connection = var1;
      this.closeConnection = var3;
      this.setGraphCommands(new GraphCommandObjects(this.connection));
      if (var2) {
         this.multi();
      }

   }

   public final void multi() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.MULTI);
      this.inMulti = true;
   }

   public String watch(String... var1) {
      this.connection.sendCommand(Protocol.Command.WATCH, (String[])var1);
      String var2 = this.connection.getStatusCodeReply();
      this.inWatch = true;
      return var2;
   }

   public String watch(byte[]... var1) {
      this.connection.sendCommand(Protocol.Command.WATCH, (byte[][])var1);
      String var2 = this.connection.getStatusCodeReply();
      this.inWatch = true;
      return var2;
   }

   public String unwatch() {
      this.connection.sendCommand((ProtocolCommand)Protocol.Command.UNWATCH);
      String var1 = this.connection.getStatusCodeReply();
      this.inWatch = false;
      return var1;
   }

   protected final <T> Response<T> appendCommand(CommandObject<T> var1) {
      this.connection.sendCommand(var1.getArguments());
      Response var2 = new Response(var1.getBuilder());
      this.pipelinedResponses.add(var2);
      return var2;
   }

   public final void close() {
      try {
         this.clear();
      } finally {
         if (this.closeConnection) {
            this.connection.close();
         }

      }

   }

   @Deprecated
   public final void clear() {
      if (!this.broken) {
         if (this.inMulti) {
            this.discard();
         } else if (this.inWatch) {
            this.unwatch();
         }

      }
   }

   public List<Object> exec() {
      if (!this.inMulti) {
         throw new IllegalStateException("EXEC without MULTI");
      } else {
         ArrayList var2;
         try {
            this.connection.getMany(1 + this.pipelinedResponses.size());
            this.connection.sendCommand((ProtocolCommand)Protocol.Command.EXEC);
            List var1 = this.connection.getObjectMultiBulkReply();
            if (var1 != null) {
               var2 = new ArrayList(var1.size());

               for(Object var4 : var1) {
                  try {
                     Response var5 = (Response)this.pipelinedResponses.poll();
                     var5.set(var4);
                     var2.add(var5.get());
                  } catch (JedisDataException var10) {
                     var2.add(var10);
                  }
               }

               ArrayList var14 = var2;
               return var14;
            }

            this.pipelinedResponses.clear();
            var2 = null;
         } catch (JedisConnectionException var11) {
            this.broken = true;
            throw var11;
         } finally {
            this.inMulti = false;
            this.inWatch = false;
            this.pipelinedResponses.clear();
            if (this.jedis != null) {
               this.jedis.resetState();
            }

         }

         return var2;
      }
   }

   public String discard() {
      if (!this.inMulti) {
         throw new IllegalStateException("DISCARD without MULTI");
      } else {
         String var1;
         try {
            this.connection.getMany(1 + this.pipelinedResponses.size());
            this.connection.sendCommand((ProtocolCommand)Protocol.Command.DISCARD);
            var1 = this.connection.getStatusCodeReply();
         } catch (JedisConnectionException var5) {
            this.broken = true;
            throw var5;
         } finally {
            this.inMulti = false;
            this.inWatch = false;
            this.pipelinedResponses.clear();
            if (this.jedis != null) {
               this.jedis.resetState();
            }

         }

         return var1;
      }
   }
}
