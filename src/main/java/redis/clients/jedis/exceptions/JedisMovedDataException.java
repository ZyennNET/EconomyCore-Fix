package redis.clients.jedis.exceptions;

import redis.clients.jedis.HostAndPort;

public class JedisMovedDataException extends JedisRedirectionException {
   private static final long serialVersionUID = 3878126572474819403L;

   public JedisMovedDataException(String var1, HostAndPort var2, int var3) {
      super(var1, var2, var3);
   }

   public JedisMovedDataException(Throwable var1, HostAndPort var2, int var3) {
      super(var1, var2, var3);
   }

   public JedisMovedDataException(String var1, Throwable var2, HostAndPort var3, int var4) {
      super(var1, var2, var3, var4);
   }
}
