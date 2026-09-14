package redis.clients.jedis.util;

public final class DoublePrecision {
   private DoublePrecision() {
      throw new InstantiationError("Must not instantiate this class");
   }

   public static Double parseFloatingPointNumber(String var0) throws NumberFormatException {
      if (var0 == null) {
         return null;
      } else {
         try {
            return Double.valueOf(var0);
         } catch (NumberFormatException var4) {
            switch (var0) {
               case "inf":
               case "+inf":
                  return Double.POSITIVE_INFINITY;
               case "-inf":
                  return Double.NEGATIVE_INFINITY;
               case "nan":
               case "-nan":
                  return Double.NaN;
               default:
                  throw var4;
            }
         }
      }
   }

   public static Double parseEncodedFloatingPointNumber(Object var0) throws NumberFormatException {
      if (var0 == null) {
         return null;
      } else {
         return var0 instanceof Double ? (Double)var0 : parseFloatingPointNumber((String)var0);
      }
   }
}
