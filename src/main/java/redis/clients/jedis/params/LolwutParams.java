package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class LolwutParams implements IParams {
   private Integer version;
   private String[] opargs;

   public LolwutParams version(int var1) {
      this.version = var1;
      return this;
   }

   @Deprecated
   public LolwutParams args(String... var1) {
      return this.optionalArguments(var1);
   }

   public LolwutParams optionalArguments(String... var1) {
      this.opargs = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.version != null) {
         var1.add(Protocol.Keyword.VERSION).add(this.version);
         if (this.opargs != null && this.opargs.length > 0) {
            var1.addObjects(this.opargs);
         }
      }

   }
}
