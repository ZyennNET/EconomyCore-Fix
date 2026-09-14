package redis.clients.jedis.bloom;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class CFReserveParams implements IParams {
   private Long bucketSize;
   private Integer maxIterations;
   private Integer expansion;

   public static CFReserveParams reserveParams() {
      return new CFReserveParams();
   }

   public CFReserveParams bucketSize(long var1) {
      this.bucketSize = var1;
      return this;
   }

   public CFReserveParams maxIterations(int var1) {
      this.maxIterations = var1;
      return this;
   }

   public CFReserveParams expansion(int var1) {
      this.expansion = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.bucketSize != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.BUCKETSIZE).add(Protocol.toByteArray(this.bucketSize));
      }

      if (this.maxIterations != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.MAXITERATIONS).add(Protocol.toByteArray(this.maxIterations));
      }

      if (this.expansion != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.EXPANSION).add(Protocol.toByteArray(this.expansion));
      }

   }
}
