package redis.clients.jedis.exceptions;

import redis.clients.jedis.HostAndPort;

public class JedisRedirectionException extends JedisDataException {
   private static final long serialVersionUID = 3878126572474819403L;
   private final HostAndPort targetNode;
   private final int slot;

   public JedisRedirectionException(String var1, HostAndPort var2, int var3) {
      super(var1);
      this.targetNode = var2;
      this.slot = var3;
   }

   public JedisRedirectionException(Throwable var1, HostAndPort var2, int var3) {
      super(var1);
      this.targetNode = var2;
      this.slot = var3;
   }

   public JedisRedirectionException(String var1, Throwable var2, HostAndPort var3, int var4) {
      super(var1, var2);
      this.targetNode = var3;
      this.slot = var4;
   }

   public final HostAndPort getTargetNode() {
      return this.targetNode;
   }

   public final int getSlot() {
      return this.slot;
   }
}
