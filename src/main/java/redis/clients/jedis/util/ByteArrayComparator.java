package redis.clients.jedis.util;

public final class ByteArrayComparator {
   private ByteArrayComparator() {
      throw new InstantiationError("Must not instantiate this class");
   }

   public static int compare(byte[] var0, byte[] var1) {
      int var2 = var0.length;
      int var3 = var1.length;
      int var4 = Math.min(var2, var3);

      for(int var5 = 0; var5 < var4; ++var5) {
         byte var6 = var0[var5];
         byte var7 = var1[var5];
         if (var6 < var7) {
            return -1;
         }

         if (var6 > var7) {
            return 1;
         }
      }

      if (var2 < var3) {
         return -1;
      } else if (var2 > var3) {
         return 1;
      } else {
         return 0;
      }
   }
}
