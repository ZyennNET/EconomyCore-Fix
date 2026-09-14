package redis.clients.jedis.exceptions;

public class JedisClusterException extends JedisDataException {
   private static final long serialVersionUID = 3878126572474819403L;

   public JedisClusterException(Throwable var1) {
      super(var1);
   }

   public JedisClusterException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public JedisClusterException(String var1) {
      super(var1);
   }
}
