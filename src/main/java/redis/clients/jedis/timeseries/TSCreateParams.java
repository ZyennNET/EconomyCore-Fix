package redis.clients.jedis.timeseries;

import java.util.LinkedHashMap;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class TSCreateParams implements IParams {
   private Long retentionPeriod;
   private boolean uncompressed;
   private boolean compressed;
   private Long chunkSize;
   private DuplicatePolicy duplicatePolicy;
   private Map<String, String> labels;

   public static TSCreateParams createParams() {
      return new TSCreateParams();
   }

   public TSCreateParams retention(long var1) {
      this.retentionPeriod = var1;
      return this;
   }

   public TSCreateParams uncompressed() {
      this.uncompressed = true;
      return this;
   }

   public TSCreateParams compressed() {
      this.compressed = true;
      return this;
   }

   public TSCreateParams chunkSize(long var1) {
      this.chunkSize = var1;
      return this;
   }

   public TSCreateParams duplicatePolicy(DuplicatePolicy var1) {
      this.duplicatePolicy = var1;
      return this;
   }

   public TSCreateParams labels(Map<String, String> var1) {
      this.labels = var1;
      return this;
   }

   public TSCreateParams label(String var1, String var2) {
      if (this.labels == null) {
         this.labels = new LinkedHashMap();
      }

      this.labels.put(var1, var2);
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.retentionPeriod != null) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.RETENTION).add(Protocol.toByteArray(this.retentionPeriod));
      }

      if (this.uncompressed) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.ENCODING).add(TimeSeriesProtocol.TimeSeriesKeyword.UNCOMPRESSED);
      } else if (this.compressed) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.ENCODING).add(TimeSeriesProtocol.TimeSeriesKeyword.COMPRESSED);
      }

      if (this.chunkSize != null) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.CHUNK_SIZE).add(Protocol.toByteArray(this.chunkSize));
      }

      if (this.duplicatePolicy != null) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.DUPLICATE_POLICY).add(this.duplicatePolicy);
      }

      if (this.labels != null) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.LABELS);
         this.labels.entrySet().forEach((var1x) -> var1.add(var1x.getKey()).add(var1x.getValue()));
      }

   }
}
