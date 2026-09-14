package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.SaveMode;

public class ShutdownParams implements IParams {
   private SaveMode saveMode;
   private boolean now;
   private boolean force;

   public static ShutdownParams shutdownParams() {
      return new ShutdownParams();
   }

   public ShutdownParams saveMode(SaveMode var1) {
      this.saveMode = var1;
      return this;
   }

   public ShutdownParams nosave() {
      return this.saveMode(SaveMode.NOSAVE);
   }

   public ShutdownParams save() {
      return this.saveMode(SaveMode.SAVE);
   }

   public ShutdownParams now() {
      this.now = true;
      return this;
   }

   public ShutdownParams force() {
      this.force = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.saveMode != null) {
         var1.add(this.saveMode);
      }

      if (this.now) {
         var1.add(Protocol.Keyword.NOW);
      }

      if (this.force) {
         var1.add(Protocol.Keyword.FORCE);
      }

   }
}
