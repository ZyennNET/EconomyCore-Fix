package redis.clients.jedis.resps;

import java.util.Arrays;
import java.util.Objects;
import redis.clients.jedis.util.ByteArrayComparator;
import redis.clients.jedis.util.SafeEncoder;

public class Tuple implements Comparable<Tuple> {
   private byte[] element;
   private Double score;

   public Tuple(String var1, Double var2) {
      this(SafeEncoder.encode(var1), var2);
   }

   public Tuple(byte[] var1, Double var2) {
      this.element = var1;
      this.score = var2;
   }

   public int hashCode() {
      boolean var1 = true;
      int var2 = 1;
      var2 = 31 * var2;
      if (null != this.element) {
         for(byte var6 : this.element) {
            var2 = 31 * var2 + var6;
         }
      }

      long var9 = Double.doubleToLongBits(this.score);
      var2 = 31 * var2 + (int)(var9 ^ var9 >>> 32);
      return var2;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Tuple)) {
         return false;
      } else {
         Tuple var2 = (Tuple)var1;
         return !Arrays.equals(this.element, var2.element) ? false : Objects.equals(this.score, var2.score);
      }
   }

   public int compareTo(Tuple var1) {
      return compare(this, var1);
   }

   public static int compare(Tuple var0, Tuple var1) {
      int var2 = Double.compare(var0.score, var1.score);
      return var2 != 0 ? var2 : ByteArrayComparator.compare(var0.element, var1.element);
   }

   public String getElement() {
      return null != this.element ? SafeEncoder.encode(this.element) : null;
   }

   public byte[] getBinaryElement() {
      return this.element;
   }

   public double getScore() {
      return this.score;
   }

   public String toString() {
      return '[' + SafeEncoder.encode(this.element) + ',' + this.score + ']';
   }
}
