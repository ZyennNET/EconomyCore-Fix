package redis.clients.jedis.graph.entities;

import java.util.List;
import java.util.Objects;

@Deprecated
public final class Point {
   private static final double EPSILON = 1.0E-5;
   private final double latitude;
   private final double longitude;

   public Point(double var1, double var3) {
      this.latitude = var1;
      this.longitude = var3;
   }

   public Point(List<Double> var1) {
      if (var1 != null && var1.size() == 2) {
         this.latitude = (Double)var1.get(0);
         this.longitude = (Double)var1.get(1);
      } else {
         throw new IllegalArgumentException("Point requires two doubles.");
      }
   }

   public double getLatitude() {
      return this.latitude;
   }

   public double getLongitude() {
      return this.longitude;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Point)) {
         return false;
      } else {
         Point var2 = (Point)var1;
         return Math.abs(this.latitude - var2.latitude) < 1.0E-5 && Math.abs(this.longitude - var2.longitude) < 1.0E-5;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.latitude, this.longitude});
   }

   public String toString() {
      return "Point{latitude=" + this.latitude + ", longitude=" + this.longitude + "}";
   }
}
