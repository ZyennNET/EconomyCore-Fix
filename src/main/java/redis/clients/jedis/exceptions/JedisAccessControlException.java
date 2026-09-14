package redis.clients.jedis.exceptions;

public class JedisAccessControlException extends JedisDataException {
   public JedisAccessControlException(String var1) {
      super(var1);
   }

   public JedisAccessControlException(Throwable var1) {
      super(var1);
   }

   public JedisAccessControlException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
