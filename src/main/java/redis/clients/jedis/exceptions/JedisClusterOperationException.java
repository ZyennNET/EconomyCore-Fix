package redis.clients.jedis.exceptions;

public class JedisClusterOperationException extends JedisException {
   private static final long serialVersionUID = 8124535086306604887L;

   public JedisClusterOperationException(String var1) {
      super(var1);
   }

   public JedisClusterOperationException(Throwable var1) {
      super(var1);
   }

   public JedisClusterOperationException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
