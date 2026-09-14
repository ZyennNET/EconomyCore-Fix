package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class LPosParams implements IParams {
   private Integer rank;
   private Integer maxlen;

   public static LPosParams lPosParams() {
      return new LPosParams();
   }

   public LPosParams rank(int var1) {
      this.rank = var1;
      return this;
   }

   public LPosParams maxlen(int var1) {
      this.maxlen = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.rank != null) {
         var1.add(Protocol.Keyword.RANK).add(this.rank);
      }

      if (this.maxlen != null) {
         var1.add(Protocol.Keyword.MAXLEN).add(this.maxlen);
      }

   }
}
