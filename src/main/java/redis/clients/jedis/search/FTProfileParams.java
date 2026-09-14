package redis.clients.jedis.search;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class FTProfileParams implements IParams {
   private boolean limited;

   public static FTProfileParams profileParams() {
      return new FTProfileParams();
   }

   public FTProfileParams limited() {
      this.limited = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.limited) {
         var1.add(SearchProtocol.SearchKeyword.LIMITED);
      }

   }
}
