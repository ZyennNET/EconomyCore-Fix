package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class ZIncrByParams implements IParams {
   private Protocol.Keyword existance;

   public static ZIncrByParams zIncrByParams() {
      return new ZIncrByParams();
   }

   public ZIncrByParams nx() {
      this.existance = Protocol.Keyword.NX;
      return this;
   }

   public ZIncrByParams xx() {
      this.existance = Protocol.Keyword.XX;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.existance != null) {
         var1.add(this.existance);
      }

      var1.add(Protocol.Keyword.INCR);
   }
}
