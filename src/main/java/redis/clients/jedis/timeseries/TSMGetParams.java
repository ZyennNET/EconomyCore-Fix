package redis.clients.jedis.timeseries;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class TSMGetParams implements IParams {
   private boolean latest;
   private boolean withLabels;
   private String[] selectedLabels;

   public static TSMGetParams multiGetParams() {
      return new TSMGetParams();
   }

   public TSMGetParams latest() {
      this.latest = true;
      return this;
   }

   public TSMGetParams withLabels(boolean var1) {
      this.withLabels = var1;
      return this;
   }

   public TSMGetParams withLabels() {
      return this.withLabels(true);
   }

   public TSMGetParams selectedLabels(String... var1) {
      this.selectedLabels = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.latest) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.LATEST);
      }

      if (this.withLabels) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.WITHLABELS);
      } else if (this.selectedLabels != null) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.SELECTED_LABELS);

         for(String var5 : this.selectedLabels) {
            var1.add(var5);
         }
      }

   }
}
