package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class XReadParams implements IParams {
   private Integer count = null;
   private Integer block = null;

   public static XReadParams xReadParams() {
      return new XReadParams();
   }

   public XReadParams count(int var1) {
      this.count = var1;
      return this;
   }

   public XReadParams block(int var1) {
      this.block = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.count != null) {
         var1.add(Protocol.Keyword.COUNT).add(this.count);
      }

      if (this.block != null) {
         var1.add(Protocol.Keyword.BLOCK).add(this.block).blocking();
      }

   }
}
