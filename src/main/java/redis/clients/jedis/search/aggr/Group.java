package redis.clients.jedis.search.aggr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Group {
   private final List<String> fields = new ArrayList();
   private final List<Reducer> reducers = new ArrayList();

   public Group(String... var1) {
      this.fields.addAll(Arrays.asList(var1));
   }

   public Group reduce(Reducer var1) {
      this.reducers.add(var1);
      return this;
   }

   public void addArgs(List<Object> var1) {
      var1.add(this.fields.size());
      var1.addAll(this.fields);
      this.reducers.forEach((var1x) -> var1x.addArgs(var1));
   }
}
