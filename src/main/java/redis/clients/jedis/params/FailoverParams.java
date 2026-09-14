package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Protocol;

public class FailoverParams implements IParams {
   private HostAndPort to;
   private boolean force;
   private Long timeout;

   public static FailoverParams failoverParams() {
      return new FailoverParams();
   }

   public FailoverParams to(String var1, int var2) {
      return this.to(new HostAndPort(var1, var2));
   }

   public FailoverParams to(HostAndPort var1) {
      this.to = var1;
      return this;
   }

   public FailoverParams force() {
      this.force = true;
      return this;
   }

   public FailoverParams timeout(long var1) {
      this.timeout = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.to != null) {
         var1.add(Protocol.Keyword.TO).add(this.to.getHost()).add(this.to.getPort());
      }

      if (this.force) {
         if (this.to == null || this.timeout == null) {
            throw new IllegalArgumentException("FAILOVER with force option requires both a timeout and target HOST and IP.");
         }

         var1.add(Protocol.Keyword.FORCE);
      }

      if (this.timeout != null) {
         var1.add(Protocol.Keyword.TIMEOUT).add(this.timeout);
      }

   }
}
