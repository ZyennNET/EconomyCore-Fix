package redis.clients.jedis.exceptions;

public class JedisNoScriptException extends JedisDataException {
   private static final long serialVersionUID = 4674378093072060731L;

   public JedisNoScriptException(String var1) {
      super(var1);
   }

   public JedisNoScriptException(Throwable var1) {
      super(var1);
   }

   public JedisNoScriptException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
