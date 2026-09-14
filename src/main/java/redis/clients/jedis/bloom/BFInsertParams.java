package redis.clients.jedis.bloom;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class BFInsertParams implements IParams {
   private Long capacity;
   private Double errorRate;
   private Integer expansion;
   private boolean noCreate = false;
   private boolean nonScaling = false;

   public static BFInsertParams insertParams() {
      return new BFInsertParams();
   }

   public BFInsertParams capacity(long var1) {
      this.capacity = var1;
      return this;
   }

   public BFInsertParams error(double var1) {
      this.errorRate = var1;
      return this;
   }

   public BFInsertParams expansion(int var1) {
      this.expansion = var1;
      return this;
   }

   public BFInsertParams noCreate() {
      this.noCreate = true;
      return this;
   }

   public BFInsertParams nonScaling() {
      this.nonScaling = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.capacity != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.CAPACITY).add(Protocol.toByteArray(this.capacity));
      }

      if (this.errorRate != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.ERROR).add(Protocol.toByteArray(this.errorRate));
      }

      if (this.expansion != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.EXPANSION).add(Protocol.toByteArray(this.expansion));
      }

      if (this.noCreate) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.NOCREATE);
      }

      if (this.nonScaling) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.NONSCALING);
      }

   }
}
