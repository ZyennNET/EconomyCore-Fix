package redis.clients.jedis.exceptions;

public class InvalidURIException extends JedisException {
   private static final long serialVersionUID = -781691993326357802L;

   public InvalidURIException(String var1) {
      super(var1);
   }

   public InvalidURIException(Throwable var1) {
      super(var1);
   }

   public InvalidURIException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
