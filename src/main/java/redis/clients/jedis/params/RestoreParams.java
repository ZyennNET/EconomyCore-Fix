package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class RestoreParams implements IParams {
   private boolean replace;
   private boolean absTtl;
   private Long idleTime;
   private Long frequency;

   public static RestoreParams restoreParams() {
      return new RestoreParams();
   }

   public RestoreParams replace() {
      this.replace = true;
      return this;
   }

   public RestoreParams absTtl() {
      this.absTtl = true;
      return this;
   }

   public RestoreParams idleTime(long var1) {
      this.idleTime = var1;
      return this;
   }

   public RestoreParams frequency(long var1) {
      this.frequency = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.replace) {
         var1.add(Protocol.Keyword.REPLACE);
      }

      if (this.absTtl) {
         var1.add(Protocol.Keyword.ABSTTL);
      }

      if (this.idleTime != null) {
         var1.add(Protocol.Keyword.IDLETIME).add(this.idleTime);
      }

      if (this.frequency != null) {
         var1.add(Protocol.Keyword.FREQ).add(this.frequency);
      }

   }
}
