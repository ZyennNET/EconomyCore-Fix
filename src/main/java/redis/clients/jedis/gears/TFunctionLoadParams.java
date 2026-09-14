package redis.clients.jedis.gears;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class TFunctionLoadParams implements IParams {
   private boolean replace = false;
   private String config;

   public static TFunctionLoadParams loadParams() {
      return new TFunctionLoadParams();
   }

   public void addParams(CommandArguments var1) {
      if (this.replace) {
         var1.add(RedisGearsProtocol.GearsKeyword.REPLACE);
      }

      if (this.config != null && !this.config.isEmpty()) {
         var1.add(RedisGearsProtocol.GearsKeyword.CONFIG).add(this.config);
      }

   }

   public TFunctionLoadParams replace() {
      this.replace = true;
      return this;
   }

   public TFunctionLoadParams config(String var1) {
      this.config = var1;
      return this;
   }
}
