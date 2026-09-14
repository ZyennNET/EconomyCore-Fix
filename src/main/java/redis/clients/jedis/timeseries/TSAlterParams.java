package redis.clients.jedis.timeseries;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class TSAlterParams implements IParams {
   private Long retentionPeriod;
   private Long chunkSize;
   private DuplicatePolicy duplicatePolicy;
   private Map<String, String> labels;

   public static TSAlterParams alterParams() {
      return new TSAlterParams();
   }

   public TSAlterParams retention(long var1) {
      this.retentionPeriod = var1;
      return this;
   }

   public TSAlterParams chunkSize(long var1) {
      this.chunkSize = var1;
      return this;
   }

   public TSAlterParams duplicatePolicy(DuplicatePolicy var1) {
      this.duplicatePolicy = var1;
      return this;
   }

   public TSAlterParams labels(Map<String, String> var1) {
      this.labels = var1;
      return this;
   }

   public TSAlterParams label(String var1, String var2) {
      if (this.labels == null) {
         this.labels = new LinkedHashMap();
      }

      this.labels.put(var1, var2);
      return this;
   }

   public TSAlterParams labelsReset() {
      return this.labels(Collections.emptyMap());
   }

   public void addParams(CommandArguments var1) {
      if (this.retentionPeriod != null) {
         var1.add(TimeSeriesProtocol.TimeSeriesKeyword.RETENTION).add(Protocol.toByteArray(this.retentionPeriod));
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
