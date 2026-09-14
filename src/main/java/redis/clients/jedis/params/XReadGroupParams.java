package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class XReadGroupParams implements IParams {
   private Integer count = null;
   private Integer block = null;
   private boolean noack = false;

   public static XReadGroupParams xReadGroupParams() {
      return new XReadGroupParams();
   }

   public XReadGroupParams count(int var1) {
      this.count = var1;
      return this;
   }

   public XReadGroupParams block(int var1) {
      this.block = var1;
      return this;
   }

   public XReadGroupParams noAck() {
      this.noack = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.count != null) {
         var1.add(Protocol.Keyword.COUNT).add(this.count);
      }

      if (this.block != null) {
         var1.add(Protocol.Keyword.BLOCK).add(this.block).blocking();
      }

      if (this.noack) {
         var1.add(Protocol.Keyword.NOACK);
      }

   }
}
