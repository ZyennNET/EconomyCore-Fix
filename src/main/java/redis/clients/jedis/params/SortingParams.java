package redis.clients.jedis.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.SortingOrder;
import redis.clients.jedis.util.SafeEncoder;

public class SortingParams implements IParams {
   private final List<Object> params = new ArrayList();

   public SortingParams by(String var1) {
      return this.by(SafeEncoder.encode(var1));
   }

   public SortingParams by(byte[] var1) {
      this.params.add(Protocol.Keyword.BY);
      this.params.add(var1);
      return this;
   }

   public SortingParams nosort() {
      this.params.add(Protocol.Keyword.BY);
      this.params.add(Protocol.Keyword.NOSORT);
      return this;
   }

   public SortingParams desc() {
      return this.sortingOrder(SortingOrder.DESC);
   }

   public SortingParams asc() {
      return this.sortingOrder(SortingOrder.ASC);
   }

   public SortingParams sortingOrder(SortingOrder var1) {
      this.params.add(var1.getRaw());
      return this;
   }

   public SortingParams limit(int var1, int var2) {
      this.params.add(Protocol.Keyword.LIMIT);
      this.params.add(var1);
      this.params.add(var2);
      return this;
   }

   public SortingParams alpha() {
      this.params.add(Protocol.Keyword.ALPHA);
      return this;
   }

   public SortingParams get(String... var1) {
      for(String var5 : var1) {
         this.params.add(Protocol.Keyword.GET);
         this.params.add(var5);
      }

      return this;
   }

   public SortingParams get(byte[]... var1) {
      for(byte[] var5 : var1) {
         this.params.add(Protocol.Keyword.GET);
         this.params.add(var5);
      }

      return this;
   }

   public void addParams(CommandArguments var1) {
      var1.addObjects((Collection)this.params);
   }
}
