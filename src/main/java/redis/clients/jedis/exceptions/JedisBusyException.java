package redis.clients.jedis.exceptions;

public class JedisBusyException extends JedisDataException {
   private static final long serialVersionUID = 3992655220229243478L;

   public JedisBusyException(String var1) {
      super(var1);
   }

   public JedisBusyException(Throwable var1) {
      super(var1);
   }

   public JedisBusyException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
