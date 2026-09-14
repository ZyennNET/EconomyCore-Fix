package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class MigrateParams implements IParams {
   private boolean copy = false;
   private boolean replace = false;
   private String username = null;
   private String password = null;

   public static MigrateParams migrateParams() {
      return new MigrateParams();
   }

   public MigrateParams copy() {
      this.copy = true;
      return this;
   }

   public MigrateParams replace() {
      this.replace = true;
      return this;
   }

   public MigrateParams auth(String var1) {
      this.password = var1;
      return this;
   }

   public MigrateParams auth2(String var1, String var2) {
      this.username = var1;
      this.password = var2;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.copy) {
         var1.add(Protocol.Keyword.COPY);
      }

      if (this.replace) {
         var1.add(Protocol.Keyword.REPLACE);
      }

      if (this.username != null) {
         var1.add(Protocol.Keyword.AUTH2).add(this.username).add(this.password);
      } else if (this.password != null) {
         var1.add(Protocol.Keyword.AUTH).add(this.password);
      }

   }
}
