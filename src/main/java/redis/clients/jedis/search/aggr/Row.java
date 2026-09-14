package redis.clients.jedis.search.aggr;

import java.util.Map;
import redis.clients.jedis.util.DoublePrecision;

public class Row {
   private final Map<String, Object> fields;

   public Row(Map<String, Object> var1) {
      this.fields = var1;
   }

   public boolean containsKey(String var1) {
      return this.fields.containsKey(var1);
   }

   public Object get(String var1) {
      return this.fields.get(var1);
   }

   public String getString(String var1) {
      return !this.containsKey(var1) ? "" : (String)this.fields.get(var1);
   }

   public long getLong(String var1) {
      return !this.containsKey(var1) ? 0L : Long.parseLong((String)this.fields.get(var1));
   }

   public double getDouble(String var1) {
      return !this.containsKey(var1) ? (double)0.0F : DoublePrecision.parseFloatingPointNumber((String)this.fields.get(var1));
   }

   public String toString() {
      return String.valueOf(this.fields);
   }
}
