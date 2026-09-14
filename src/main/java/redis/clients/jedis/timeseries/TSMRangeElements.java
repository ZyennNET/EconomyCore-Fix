package redis.clients.jedis.timeseries;

import java.util.List;
import java.util.Map;
import redis.clients.jedis.util.KeyValue;

public class TSMRangeElements extends KeyValue<String, List<TSElement>> {
   private final Map<String, String> labels;
   private final List<AggregationType> aggregators;
   private final List<String> reducers;
   private final List<String> sources;

   public TSMRangeElements(String var1, Map<String, String> var2, List<TSElement> var3) {
      super(var1, var3);
      this.labels = var2;
      this.aggregators = null;
      this.reducers = null;
      this.sources = null;
   }

   public TSMRangeElements(String var1, Map<String, String> var2, List<AggregationType> var3, List<TSElement> var4) {
      super(var1, var4);
      this.labels = var2;
      this.aggregators = var3;
      this.reducers = null;
      this.sources = null;
   }

   public TSMRangeElements(String var1, Map<String, String> var2, List<String> var3, List<String> var4, List<TSElement> var5) {
      super(var1, var5);
      this.labels = var2;
      this.aggregators = null;
      this.reducers = var3;
      this.sources = var4;
   }

   public Map<String, String> getLabels() {
      return this.labels;
   }

   public List<AggregationType> getAggregators() {
      return this.aggregators;
   }

   public List<String> getReducers() {
      return this.reducers;
   }

   public List<String> getSources() {
      return this.sources;
   }

   public List<TSElement> getElements() {
      return (List)this.getValue();
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder()).append(this.getClass().getSimpleName()).append("{key=").append((String)this.getKey()).append(", labels=").append(this.labels);
      if (this.aggregators != null) {
         var1.append(", aggregators=").append(this.aggregators);
      }

      if (this.reducers != null && this.sources != null) {
         var1.append(", reducers").append(this.reducers).append(", sources").append(this.sources);
      }

      return var1.append(", elements=").append(this.getElements()).append('}').toString();
   }
}
