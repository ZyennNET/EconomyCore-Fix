package redis.clients.jedis.timeseries;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.util.SafeEncoder;

public class TSMRangeParams implements IParams {
   private Long fromTimestamp;
   private Long toTimestamp;
   private boolean latest;
   private long[] filterByTimestamps;
   private double[] filterByValues;
   private boolean withLabels;
   private String[] selectedLabels;
   private Integer count;
   private byte[] align;
   private AggregationType aggregationType;
   private long bucketDuration;
   private byte[] bucketTimestamp;
   private boolean empty;
   private String[] filters;
   private String groupByLabel;
   private String groupByReduce;

   public TSMRangeParams(long var1, long var3) {
      this.fromTimestamp = var1;
      this.toTimestamp = var3;
   }

   public static TSMRangeParams multiRangeParams(long var0, long var2) {
      return new TSMRangeParams(var0, var2);
   }

   public TSMRangeParams() {
   }

   public static TSMRangeParams multiRangeParams() {
      return new TSMRangeParams();
   }

   public TSMRangeParams fromTimestamp(long var1) {
      this.fromTimestamp = var1;
      return this;
   }

   public TSMRangeParams toTimestamp(long var1) {
      this.toTimestamp = var1;
      return this;
   }

   public TSMRangeParams latest() {
      this.latest = true;
      return this;
   }

   public TSMRangeParams filterByTS(long... var1) {
      this.filterByTimestamps = var1;
      return this;
   }

   public TSMRangeParams filterByValues(double var1, double var3) {
      this.filterByValues = new double[]{var1, var3};
      return this;
   }

   public TSMRangeParams withLabels(boolean var1) {
      this.withLabels = var1;
      return this;
   }

   public TSMRangeParams withLabels() {
      return this.withLabels(true);
   }

   public TSMRangeParams selectedLabels(String... var1) {
      this.selectedLabels = var1;
      return this;
   }

   public TSMRangeParams count(int var1) {
      this.count = var1;
      return this;
   }

   private TSMRangeParams align(byte[] var1) {
      this.align = var1;
      return this;
   }

   public TSMRangeParams align(long var1) {
      return this.align(Protocol.toByteArray(var1));
   }

   public TSMRangeParams alignStart() {
      return this.align(TimeSeriesProtocol.MINUS);
   }

   public TSMRangeParams alignEnd() {
      return this.align(TimeSeriesProtocol.PLUS);
   }

   public TSMRangeParams aggregation(AggregationType var1, long var2) {
      this.aggregationType = var1;
      this.bucketDuration = var2;
      return this;
   }

   public TSMRangeParams bucketTimestamp(String var1) {
      this.bucketTimestamp = SafeEncoder.encode(var1);
      return this;
   }

   public TSMRangeParams bucketTimestampLow() {
      this.bucketTimestamp = TimeSeriesProtocol.MINUS;
      return this;
   }

   public TSMRangeParams bucketTimestampHigh() {
      this.bucketTimestamp = TimeSeriesProtocol.PLUS;
      return this;
   }

   public TSMRangeParams bucketTimestampMid() {
      this.bucketTimestamp = Protocol.BYTES_TILDE;
      return this;
   }

   public TSMRangeParams empty() {
      this.empty = true;
      return this;
   }

   public TSMRangeParams filter(String... var1) {
      this.filters = var1;
      return this;
   }

   public TSMRangeParams groupBy(String var1, String var2) {
      this.groupByLabel = var1;
      this.groupByReduce = var2;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.filters == null) {
         throw new IllegalArgumentException("FILTER arguments must be set.");
      } else {
         if (this.fromTimestamp == null) {
            var1.add(TimeSeriesProtocol.MINUS);
         } else {
            var1.add(Protocol.toByteArray(this.fromTimestamp));
         }

         if (this.toTimestamp == null) {
            var1.add(TimeSeriesProtocol.PLUS);
         } else {
            var1.add(Protocol.toByteArray(this.toTimestamp));
         }

         if (this.latest) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.LATEST);
         }

         if (this.filterByTimestamps != null) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.FILTER_BY_TS);

            for(long var5 : this.filterByTimestamps) {
               var1.add(Protocol.toByteArray(var5));
            }
         }

         if (this.filterByValues != null) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.FILTER_BY_VALUE);

            for(double var16 : this.filterByValues) {
               var1.add(Protocol.toByteArray(var16));
            }
         }

         if (this.withLabels) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.WITHLABELS);
         } else if (this.selectedLabels != null) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.SELECTED_LABELS);

            for(String var17 : this.selectedLabels) {
               var1.add(var17);
            }
         }

         if (this.count != null) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.COUNT).add(Protocol.toByteArray(this.count));
         }

         if (this.aggregationType != null) {
            if (this.align != null) {
               var1.add(TimeSeriesProtocol.TimeSeriesKeyword.ALIGN).add(this.align);
            }

            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.AGGREGATION).add(this.aggregationType).add(Protocol.toByteArray(this.bucketDuration));
            if (this.bucketTimestamp != null) {
               var1.add(TimeSeriesProtocol.TimeSeriesKeyword.BUCKETTIMESTAMP).add(this.bucketTimestamp);
            }

            if (this.empty) {
               var1.add(TimeSeriesProtocol.TimeSeriesKeyword.EMPTY);
            }
         }

         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.FILTER);

         for(String var18 : this.filters) {
            var1.add(var18);
         }

         if (this.groupByLabel != null && this.groupByReduce != null) {
            var1.add(TimeSeriesProtocol.TimeSeriesKeyword.GROUPBY).add(this.groupByLabel).add(TimeSeriesProtocol.TimeSeriesKeyword.REDUCE).add(this.groupByReduce);
         }

      }
   }
}
