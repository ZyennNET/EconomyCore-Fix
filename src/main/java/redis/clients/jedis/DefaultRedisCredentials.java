package redis.clients.jedis;

public final class DefaultRedisCredentials implements RedisCredentials {
   private final String user;
   private final char[] password;

   public DefaultRedisCredentials(String var1, char[] var2) {
      this.user = var1;
      this.password = var2;
   }

   public DefaultRedisCredentials(String var1, CharSequence var2) {
      this.user = var1;
      this.password = var2 == null ? null : (var2 instanceof String ? ((String)var2).toCharArray() : toCharArray(var2));
   }

   public String getUser() {
      return this.user;
   }

   public char[] getPassword() {
      return this.password;
   }

   private static char[] toCharArray(CharSequence var0) {
      int var1 = var0.length();
      char[] var2 = new char[var1];

      for(int var3 = 0; var3 < var1; ++var3) {
         var2[var3] = var0.charAt(var3);
      }

      return var2;
   }
}
