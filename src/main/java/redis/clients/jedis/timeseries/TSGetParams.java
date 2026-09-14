package redis.clients.jedis.timeseries;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class TSGetParams implements IParams {
   private boolean latest;

   public static TSGetParams getParams() {
      return new TSGetParams();
   }

   public TSGetParams latest() {
      this.latest = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.latest) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.LATEST);
      }

   }
}
