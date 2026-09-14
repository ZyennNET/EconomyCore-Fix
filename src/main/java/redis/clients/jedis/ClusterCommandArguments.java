package redis.clients.jedis;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisClusterOperationException;
import redis.clients.jedis.util.JedisClusterCRC16;

public class ClusterCommandArguments extends CommandArguments {
   private int commandHashSlot = -1;

   public ClusterCommandArguments(ProtocolCommand var1) {
      super(var1);
   }

   public int getCommandHashSlot() {
      return this.commandHashSlot;
   }

   protected CommandArguments processKey(byte[] var1) {
      int var2 = JedisClusterCRC16.getSlot(var1);
      if (this.commandHashSlot < 0) {
         this.commandHashSlot = var2;
      } else if (this.commandHashSlot != var2) {
         throw new JedisClusterOperationException("Keys must belong to same hashslot.");
      }

      return this;
   }

   protected CommandArguments processKey(String var1) {
      int var2 = JedisClusterCRC16.getSlot(var1);
      if (this.commandHashSlot < 0) {
         this.commandHashSlot = var2;
      } else if (this.commandHashSlot != var2) {
         throw new JedisClusterOperationException("Keys must belong to same hashslot.");
      }

      return this;
   }
}
