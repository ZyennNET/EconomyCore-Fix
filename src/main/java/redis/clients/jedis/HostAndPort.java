package redis.clients.jedis;

import java.io.Serializable;

public class HostAndPort implements Serializable {
   private static final long serialVersionUID = -519876229978427751L;
   private final String host;
   private final int port;

   public HostAndPort(String var1, int var2) {
      this.host = var1;
      this.port = var2;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof HostAndPort)) {
         return false;
      } else {
         HostAndPort var2 = (HostAndPort)var1;
         return this.port == var2.port && this.host.equals(var2.host);
      }
   }

   public int hashCode() {
      return 31 * this.host.hashCode() + this.port;
   }

   public String toString() {
      return this.host + ":" + this.port;
   }

   public static HostAndPort from(String var0) {
      int var1 = var0.lastIndexOf(":");
      String var2 = var0.substring(0, var1);
      int var3 = Integer.parseInt(var0.substring(var1 + 1));
      return new HostAndPort(var2, var3);
   }
}
