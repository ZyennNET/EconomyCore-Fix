package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class SetParams implements IParams {
   private Protocol.Keyword existance;
   private Protocol.Keyword expiration;
   private Long expirationValue;

   public static SetParams setParams() {
      return new SetParams();
   }

   public SetParams nx() {
      this.existance = Protocol.Keyword.NX;
      return this;
   }

   public SetParams xx() {
      this.existance = Protocol.Keyword.XX;
      return this;
   }

   private SetParams expiration(Protocol.Keyword var1, Long var2) {
      this.expiration = var1;
      this.expirationValue = var2;
      return this;
   }

   public SetParams ex(long var1) {
      return this.expiration(Protocol.Keyword.EX, var1);
   }

   public SetParams px(long var1) {
      return this.expiration(Protocol.Keyword.PX, var1);
   }

   public SetParams exAt(long var1) {
      return this.expiration(Protocol.Keyword.EXAT, var1);
   }

   public SetParams pxAt(long var1) {
      return this.expiration(Protocol.Keyword.PXAT, var1);
   }

   public SetParams keepttl() {
      return this.keepTtl();
   }

   public SetParams keepTtl() {
      return this.expiration(Protocol.Keyword.KEEPTTL, (Long)null);
   }

   public void addParams(CommandArguments var1) {
      if (this.existance != null) {
         var1.add(this.existance);
      }

      if (this.expiration != null) {
         var1.add(this.expiration);
         if (this.expirationValue != null) {
            var1.add(this.expirationValue);
         }
      }

   }
}
