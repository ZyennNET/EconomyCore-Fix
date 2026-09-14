package redis.clients.jedis.bloom;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class BFReserveParams implements IParams {
   private Integer expansion;
   private boolean nonScaling = false;

   public static BFReserveParams reserveParams() {
      return new BFReserveParams();
   }

   public BFReserveParams expansion(int var1) {
      this.expansion = var1;
      return this;
   }

   public BFReserveParams nonScaling() {
      this.nonScaling = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.expansion != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.EXPANSION).add(Protocol.toByteArray(this.expansion));
      }

      if (this.nonScaling) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.NONSCALING);
      }

   }
}
