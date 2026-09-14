package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class GetExParams implements IParams {
   private Protocol.Keyword expiration;
   private Long expirationValue;
   private boolean persist;

   public static GetExParams getExParams() {
      return new GetExParams();
   }

   private GetExParams expiration(Protocol.Keyword var1, Long var2) {
      this.expiration = var1;
      this.expirationValue = var2;
      return this;
   }

   public GetExParams ex(long var1) {
      return this.expiration(Protocol.Keyword.EX, var1);
   }

   public GetExParams px(long var1) {
      return this.expiration(Protocol.Keyword.PX, var1);
   }

   public GetExParams exAt(long var1) {
      return this.expiration(Protocol.Keyword.EXAT, var1);
   }

   public GetExParams pxAt(long var1) {
      return this.expiration(Protocol.Keyword.PXAT, var1);
   }

   public GetExParams persist() {
      return this.expiration(Protocol.Keyword.PERSIST, (Long)null);
   }

   public void addParams(CommandArguments var1) {
      if (this.expiration != null) {
         var1.add(this.expiration);
         if (this.expirationValue != null) {
            var1.add(this.expirationValue);
         }
      }

   }
}
