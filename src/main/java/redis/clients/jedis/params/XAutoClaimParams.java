package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class XAutoClaimParams implements IParams {
   private Integer count;

   public static XAutoClaimParams xAutoClaimParams() {
      return new XAutoClaimParams();
   }

   public XAutoClaimParams count(int var1) {
      this.count = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.count != null) {
         var1.add(Protocol.Keyword.COUNT.getRaw()).add(this.count);
      }

   }
}
