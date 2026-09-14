package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class ZAddParams implements IParams {
   private Protocol.Keyword existence;
   private Protocol.Keyword comparison;
   private boolean change;

   public static ZAddParams zAddParams() {
      return new ZAddParams();
   }

   public ZAddParams nx() {
      this.existence = Protocol.Keyword.NX;
      return this;
   }

   public ZAddParams xx() {
      this.existence = Protocol.Keyword.XX;
      return this;
   }

   public ZAddParams gt() {
      this.comparison = Protocol.Keyword.GT;
      return this;
   }

   public ZAddParams lt() {
      this.comparison = Protocol.Keyword.LT;
      return this;
   }

   public ZAddParams ch() {
      this.change = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.existence != null) {
         var1.add(this.existence);
      }

      if (this.comparison != null) {
         var1.add(this.comparison);
      }

      if (this.change) {
         var1.add(Protocol.Keyword.CH);
      }

   }
}
