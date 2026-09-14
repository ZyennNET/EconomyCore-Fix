package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class XClaimParams implements IParams {
   private Long idleTime;
   private Long idleUnixTime;
   private Integer retryCount;
   private boolean force;

   public static XClaimParams xClaimParams() {
      return new XClaimParams();
   }

   public XClaimParams idle(long var1) {
      this.idleTime = var1;
      return this;
   }

   public XClaimParams time(long var1) {
      this.idleUnixTime = var1;
      return this;
   }

   public XClaimParams retryCount(int var1) {
      this.retryCount = var1;
      return this;
   }

   public XClaimParams force() {
      this.force = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.idleTime != null) {
         var1.add(Protocol.Keyword.IDLE).add(this.idleTime);
      }

      if (this.idleUnixTime != null) {
         var1.add(Protocol.Keyword.TIME).add(this.idleUnixTime);
      }

      if (this.retryCount != null) {
         var1.add(Protocol.Keyword.RETRYCOUNT).add(this.retryCount);
      }

      if (this.force) {
         var1.add(Protocol.Keyword.FORCE);
      }

   }
}
