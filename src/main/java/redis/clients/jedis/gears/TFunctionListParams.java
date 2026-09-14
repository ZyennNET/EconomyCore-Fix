package redis.clients.jedis.gears;

import java.util.Collections;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class TFunctionListParams implements IParams {
   private boolean withCode = false;
   private int verbose;
   private String libraryName;

   public static TFunctionListParams listParams() {
      return new TFunctionListParams();
   }

   public void addParams(CommandArguments var1) {
      if (this.withCode) {
         var1.add(RedisGearsProtocol.GearsKeyword.WITHCODE);
      }

      if (this.verbose > 0 && this.verbose < 4) {
         var1.add(String.join("", Collections.nCopies(this.verbose, "v")));
      } else if (this.verbose != 0) {
         throw new IllegalArgumentException("verbose must be between 1 and 3");
      }

      if (this.libraryName != null) {
         var1.add(RedisGearsProtocol.GearsKeyword.LIBRARY).add(this.libraryName);
      }

   }

   public TFunctionListParams withCode() {
      this.withCode = true;
      return this;
   }

   public TFunctionListParams verbose(int var1) {
      this.verbose = var1;
      return this;
   }

   public TFunctionListParams library(String var1) {
      this.libraryName = var1;
      return this;
   }
}
