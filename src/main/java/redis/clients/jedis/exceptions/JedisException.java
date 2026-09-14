package redis.clients.jedis.exceptions;

public class JedisException extends RuntimeException {
   private static final long serialVersionUID = -2946266495682282677L;

   public JedisException(String var1) {
      super(var1);
   }

   public JedisException(Throwable var1) {
      super(var1);
   }

   public JedisException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
