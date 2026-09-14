package redis.clients.jedis.bloom;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class CFInsertParams implements IParams {
   private Long capacity;
   private boolean noCreate = false;

   public static CFInsertParams insertParams() {
      return new CFInsertParams();
   }

   public CFInsertParams capacity(long var1) {
      this.capacity = var1;
      return this;
   }

   public CFInsertParams noCreate() {
      this.noCreate = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.capacity != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.CAPACITY).add(Protocol.toByteArray(this.capacity));
      }

      if (this.noCreate) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.NOCREATE);
      }

   }
}
