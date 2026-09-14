package redis.clients.jedis.timeseries;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.util.SafeEncoder;

public class TSRangeParams implements IParams {
   private Long fromTimestamp;
   private Long toTimestamp;
   private boolean latest;
   private long[] filterByTimestamps;
   private double[] filterByValues;
   private Integer count;
   private byte[] align;
   private AggregationType aggregationType;
   private long bucketDuration;
   private byte[] bucketTimestamp;
   private boolean empty;

   public TSRangeParams(long var1, long var3) {
      this.fromTimestamp = var1;
      this.toTimestamp = var3;
   }

   public static TSRangeParams rangeParams(long var0, long var2) {
      return new TSRangeParams(var0, var2);
   }

   public TSRangeParams() {
   }

   public static TSRangeParams rangeParams() {
      return new TSRangeParams();
   }

   public TSRangeParams fromTimestamp(long var1) {
      this.fromTimestamp = var1;
      return this;
   }

   public TSRangeParams toTimestamp(long var1) {
      this.toTimestamp = var1;
      return this;
   }

   public TSRangeParams latest() {
      this.latest = true;
      return this;
   }

   public TSRangeParams filterByTS(long... var1) {
      this.filterByTimestamps = var1;
      return this;
   }

   public TSRangeParams filterByValues(double var1, double var3) {
      this.filterByValues = new double[]{var1, var3};
      return this;
   }

   public TSRangeParams count(int var1) {
      this.count = var1;
      return this;
   }

   private TSRangeParams align(byte[] var1) {
      this.align = var1;
      return this;
   }

   public TSRangeParams align(long var1) {
      return this.align(Protocol.toByteArray(var1));
   }

   public TSRangeParams alignStart() {
      return this.align(TimeSeriesProtocol.MINUS);
   }

   public TSRangeParams alignEnd() {
      return this.align(TimeSeriesProtocol.PLUS);
   }

   public TSRangeParams aggregation(AggregationType var1, long var2) {
      this.aggregationType = var1;
      this.bucketDuration = var2;
      return this;
   }

   public TSRangeParams bucketTimestamp(String var1) {
      this.bucketTimestamp = SafeEncoder.encode(var1);
      return this;
   }

   public TSRangeParams bucketTimestampLow() {
      this.bucketTimestamp = TimeSeriesProtocol.MINUS;
      return this;
   }

   public TSRangeParams bucketTimestampHigh() {
      this.bucketTimestamp = TimeSeriesProtocol.PLUS;
      return this;
   }

   public TSRangeParams bucketTimestampMid() {
      this.bucketTimestamp = Protocol.BYTES_TILDE;
      return this;
   }

   public TSRangeParams empty() {
      this.empty = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
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

         for(double var10 : this.filterByValues) {
            var1.add(Protocol.toByteArray(var10));
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

   }
}
