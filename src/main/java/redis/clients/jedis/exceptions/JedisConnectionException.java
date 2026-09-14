package redis.clients.jedis.exceptions;

public class JedisConnectionException extends JedisException {
   private static final long serialVersionUID = 3878126572474819403L;

   public JedisConnectionException(String var1) {
      super(var1);
   }

   public JedisConnectionException(Throwable var1) {
      super(var1);
   }

   public JedisConnectionException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
