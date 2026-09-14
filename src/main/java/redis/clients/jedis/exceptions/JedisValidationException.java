package redis.clients.jedis.exceptions;

public class JedisValidationException extends JedisException {
   private static final long serialVersionUID = 1134169242443303479L;

   public JedisValidationException(String var1) {
      super(var1);
   }

   public JedisValidationException(Throwable var1) {
      super(var1);
   }

   public JedisValidationException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
