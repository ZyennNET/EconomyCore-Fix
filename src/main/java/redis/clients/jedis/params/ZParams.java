package redis.clients.jedis.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.util.SafeEncoder;

public class ZParams implements IParams {
   private final List<Object> params = new ArrayList();

   public ZParams weights(double... var1) {
      this.params.add(Protocol.Keyword.WEIGHTS);

      for(double var5 : var1) {
         this.params.add(var5);
      }

      return this;
   }

   public ZParams aggregate(Aggregate var1) {
      this.params.add(Protocol.Keyword.AGGREGATE);
      this.params.add(var1);
      return this;
   }

   public void addParams(CommandArguments var1) {
      var1.addObjects((Collection)this.params);
   }

   public static enum Aggregate implements Rawable {
      SUM,
      MIN,
      MAX;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
