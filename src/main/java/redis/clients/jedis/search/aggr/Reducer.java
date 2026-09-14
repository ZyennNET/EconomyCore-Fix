package redis.clients.jedis.search.aggr;

import java.util.List;
import redis.clients.jedis.search.SearchProtocol;

public abstract class Reducer {
   private final String name;
   private final String field;
   private String alias;

   protected Reducer(String var1) {
      this.name = var1;
      this.field = null;
   }

   protected Reducer(String var1, String var2) {
      this.name = var1;
      this.field = var2;
   }

   public final Reducer as(String var1) {
      this.alias = var1;
      return this;
   }

   protected abstract List<Object> getOwnArgs();

   public final void addArgs(List<Object> var1) {
      var1.add(SearchProtocol.SearchKeyword.REDUCE);
      var1.add(this.name);
      List var2 = this.getOwnArgs();
      if (this.field != null) {
         var1.add(1 + var2.size());
         var1.add(this.field);
      } else {
         var1.add(var2.size());
      }

      var1.addAll(var2);
      if (this.alias != null) {
         var1.add(SearchProtocol.SearchKeyword.AS);
         var1.add(this.alias);
      }

   }
}
