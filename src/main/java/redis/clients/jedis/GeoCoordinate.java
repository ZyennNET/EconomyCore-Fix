package redis.clients.jedis;

public class GeoCoordinate {
   private double longitude;
   private double latitude;

   public GeoCoordinate(double var1, double var3) {
      this.longitude = var1;
      this.latitude = var3;
   }

   public double getLongitude() {
      return this.longitude;
   }

   public double getLatitude() {
      return this.latitude;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GeoCoordinate)) {
         return false;
      } else {
         GeoCoordinate var2 = (GeoCoordinate)var1;
         if (Double.compare(var2.longitude, this.longitude) != 0) {
            return false;
         } else {
            return Double.compare(var2.latitude, this.latitude) == 0;
         }
      }
   }

   public int hashCode() {
      long var2 = Double.doubleToLongBits(this.longitude);
      int var1 = (int)(var2 ^ var2 >>> 32);
      var2 = Double.doubleToLongBits(this.latitude);
      var1 = 31 * var1 + (int)(var2 ^ var2 >>> 32);
      return var1;
   }

   public String toString() {
      return "(" + this.longitude + "," + this.latitude + ")";
   }
}
