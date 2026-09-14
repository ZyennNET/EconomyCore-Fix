package redis.clients.jedis.timeseries;

import java.util.Map;
import redis.clients.jedis.util.KeyValue;

public class TSMGetElement extends KeyValue<String, TSElement> {
   private final Map<String, String> labels;

   public TSMGetElement(String var1, Map<String, String> var2, TSElement var3) {
      super(var1, var3);
      this.labels = var2;
   }

   public Map<String, String> getLabels() {
      return this.labels;
   }

   public TSElement getElement() {
      return (TSElement)this.getValue();
   }

   public String toString() {
      return this.getClass().getSimpleName() + "{key=" + (String)this.getKey() + ", labels=" + this.labels + ", element=" + this.getElement() + '}';
   }
}
