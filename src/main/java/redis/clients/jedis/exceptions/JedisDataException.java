package redis.clients.jedis.exceptions;

public class JedisDataException extends JedisException {
   private static final long serialVersionUID = 3878126572474819403L;

   public JedisDataException(String var1) {
      super(var1);
   }

   public JedisDataException(Throwable var1) {
      super(var1);
   }

   public JedisDataException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
