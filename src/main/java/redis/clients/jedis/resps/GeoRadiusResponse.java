package redis.clients.jedis.resps;

import java.util.Arrays;
import java.util.Objects;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.util.SafeEncoder;

public class GeoRadiusResponse {
   private byte[] member;
   private double distance;
   private GeoCoordinate coordinate;
   private long rawScore;

   public GeoRadiusResponse(byte[] var1) {
      this.member = var1;
   }

   public void setDistance(double var1) {
      this.distance = var1;
   }

   public void setCoordinate(GeoCoordinate var1) {
      this.coordinate = var1;
   }

   public void setRawScore(long var1) {
      this.rawScore = var1;
   }

   public byte[] getMember() {
      return this.member;
   }

   public String getMemberByString() {
      return SafeEncoder.encode(this.member);
   }

   public double getDistance() {
      return this.distance;
   }

   public GeoCoordinate getCoordinate() {
      return this.coordinate;
   }

   public long getRawScore() {
      return this.rawScore;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GeoRadiusResponse)) {
         return false;
      } else {
         GeoRadiusResponse var2 = (GeoRadiusResponse)var1;
         return Double.compare(this.distance, var2.getDistance()) == 0 && this.rawScore == var2.getRawScore() && this.coordinate.equals(var2.coordinate) && Arrays.equals(this.member, var2.getMember());
      }
   }

   public int hashCode() {
      int var1 = 7;
      var1 = 67 * var1 + Arrays.hashCode(this.member);
      var1 = 67 * var1 + (int)(Double.doubleToLongBits(this.distance) ^ Double.doubleToLongBits(this.distance) >>> 32);
      var1 = 67 * var1 + Objects.hashCode(this.coordinate);
      var1 = 67 * var1 + (int)(this.rawScore ^ this.rawScore >>> 32);
      return var1;
   }
}
