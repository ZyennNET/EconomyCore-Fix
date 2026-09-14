package redis.clients.jedis.timeseries;

public class TSElement {
   private final long timestamp;
   private final double value;

   public TSElement(long var1, double var3) {
      this.timestamp = var1;
      this.value = var3;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public double getValue() {
      return this.value;
   }

   public int hashCode() {
      return 31 * Long.hashCode(this.timestamp) + Long.hashCode(Double.doubleToLongBits(this.value));
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TSElement)) {
         return false;
      } else {
         TSElement var2 = (TSElement)var1;
         return this.timestamp == var2.timestamp && this.value == var2.value;
      }
   }

   public String toString() {
      return "(" + this.timestamp + ":" + this.value + ")";
   }
}
