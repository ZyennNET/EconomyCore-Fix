package redis.clients.jedis.search.aggr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.search.FieldName;
import redis.clients.jedis.search.SearchProtocol;
import redis.clients.jedis.util.LazyRawable;

public class AggregationBuilder implements IParams {
   private final List<Object> aggrArgs;
   private Integer dialect;
   private boolean isWithCursor;

   public AggregationBuilder(String var1) {
      this.aggrArgs = new ArrayList();
      this.isWithCursor = false;
      this.aggrArgs.add(var1);
   }

   public AggregationBuilder() {
      this("*");
   }

   public AggregationBuilder load(String... var1) {
      return this.load(FieldName.convert(var1));
   }

   public AggregationBuilder load(FieldName... var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.LOAD);
      LazyRawable var2 = new LazyRawable();
      this.aggrArgs.add(var2);
      int var3 = 0;

      for(FieldName var7 : var1) {
         var3 += var7.addCommandArguments(this.aggrArgs);
      }

      var2.setRaw(Protocol.toByteArray(var3));
      return this;
   }

   public AggregationBuilder loadAll() {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.LOAD);
      this.aggrArgs.add(Protocol.BYTES_ASTERISK);
      return this;
   }

   public AggregationBuilder limit(int var1, int var2) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.LIMIT);
      this.aggrArgs.add(var1);
      this.aggrArgs.add(var2);
      return this;
   }

   public AggregationBuilder limit(int var1) {
      return this.limit(0, var1);
   }

   public AggregationBuilder sortBy(SortedField... var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.SORTBY);
      this.aggrArgs.add(Integer.toString(var1.length * 2));

      for(SortedField var5 : var1) {
         this.aggrArgs.add(var5.getField());
         this.aggrArgs.add(var5.getOrder());
      }

      return this;
   }

   public AggregationBuilder sortByAsc(String var1) {
      return this.sortBy(SortedField.asc(var1));
   }

   public AggregationBuilder sortByDesc(String var1) {
      return this.sortBy(SortedField.desc(var1));
   }

   public AggregationBuilder sortByMax(int var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.MAX);
      this.aggrArgs.add(var1);
      return this;
   }

   public AggregationBuilder sortBy(int var1, SortedField... var2) {
      this.sortBy(var2);
      this.sortByMax(var1);
      return this;
   }

   public AggregationBuilder apply(String var1, String var2) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.APPLY);
      this.aggrArgs.add(var1);
      this.aggrArgs.add(SearchProtocol.SearchKeyword.AS);
      this.aggrArgs.add(var2);
      return this;
   }

   public AggregationBuilder groupBy(Group var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.GROUPBY);
      var1.addArgs(this.aggrArgs);
      return this;
   }

   public AggregationBuilder groupBy(Collection<String> var1, Collection<Reducer> var2) {
      String[] var3 = new String[var1.size()];
      Group var4 = new Group((String[])var1.toArray(var3));
      var2.forEach((var1x) -> var4.reduce(var1x));
      this.groupBy(var4);
      return this;
   }

   public AggregationBuilder groupBy(String var1, Reducer... var2) {
      return this.groupBy(Collections.singletonList(var1), Arrays.asList(var2));
   }

   public AggregationBuilder filter(String var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.FILTER);
      this.aggrArgs.add(var1);
      return this;
   }

   public AggregationBuilder cursor(int var1) {
      this.isWithCursor = true;
      this.aggrArgs.add(SearchProtocol.SearchKeyword.WITHCURSOR);
      this.aggrArgs.add(SearchProtocol.SearchKeyword.COUNT);
      this.aggrArgs.add(var1);
      return this;
   }

   public AggregationBuilder cursor(int var1, long var2) {
      this.isWithCursor = true;
      this.aggrArgs.add(SearchProtocol.SearchKeyword.WITHCURSOR);
      this.aggrArgs.add(SearchProtocol.SearchKeyword.COUNT);
      this.aggrArgs.add(var1);
      this.aggrArgs.add(SearchProtocol.SearchKeyword.MAXIDLE);
      this.aggrArgs.add(var2);
      return this;
   }

   public AggregationBuilder verbatim() {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.VERBATIM);
      return this;
   }

   public AggregationBuilder timeout(long var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.TIMEOUT);
      this.aggrArgs.add(var1);
      return this;
   }

   public AggregationBuilder params(Map<String, Object> var1) {
      this.aggrArgs.add(SearchProtocol.SearchKeyword.PARAMS);
      this.aggrArgs.add(var1.size() * 2);
      var1.forEach((var1x, var2) -> {
         this.aggrArgs.add(var1x);
         this.aggrArgs.add(var2);
      });
      return this;
   }

   public AggregationBuilder dialect(int var1) {
      this.dialect = var1;
      return this;
   }

   public AggregationBuilder dialectOptional(int var1) {
      if (var1 != 0 && this.dialect == null) {
         this.dialect = var1;
      }

      return this;
   }

   public boolean isWithCursor() {
      return this.isWithCursor;
   }

   public void addParams(CommandArguments var1) {
      var1.addObjects((Collection)this.aggrArgs);
      if (this.dialect != null) {
         var1.add(SearchProtocol.SearchKeyword.DIALECT).add(this.dialect);
      }

   }
}
