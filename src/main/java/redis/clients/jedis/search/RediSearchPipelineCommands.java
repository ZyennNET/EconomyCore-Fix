package redis.clients.jedis.search;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Response;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.search.aggr.AggregationBuilder;
import redis.clients.jedis.search.aggr.AggregationResult;
import redis.clients.jedis.search.schemafields.SchemaField;

public interface RediSearchPipelineCommands {
   Response<String> ftCreate(String var1, IndexOptions var2, Schema var3);

   default Response<String> ftCreate(String var1, SchemaField... var2) {
      return this.ftCreate(var1, (Iterable)Arrays.asList(var2));
   }

   default Response<String> ftCreate(String var1, FTCreateParams var2, SchemaField... var3) {
      return this.ftCreate(var1, (FTCreateParams)var2, (Iterable)Arrays.asList(var3));
   }

   default Response<String> ftCreate(String var1, Iterable<SchemaField> var2) {
      return this.ftCreate(var1, FTCreateParams.createParams(), var2);
   }

   Response<String> ftCreate(String var1, FTCreateParams var2, Iterable<SchemaField> var3);

   default Response<String> ftAlter(String var1, Schema.Field... var2) {
      return this.ftAlter(var1, Schema.from(var2));
   }

   Response<String> ftAlter(String var1, Schema var2);

   default Response<String> ftAlter(String var1, SchemaField... var2) {
      return this.ftAlter(var1, (Iterable)Arrays.asList(var2));
   }

   Response<String> ftAlter(String var1, Iterable<SchemaField> var2);

   Response<String> ftAliasAdd(String var1, String var2);

   Response<String> ftAliasUpdate(String var1, String var2);

   Response<String> ftAliasDel(String var1);

   Response<String> ftDropIndex(String var1);

   Response<String> ftDropIndexDD(String var1);

   default Response<SearchResult> ftSearch(String var1) {
      return this.ftSearch(var1, "*");
   }

   Response<SearchResult> ftSearch(String var1, String var2);

   Response<SearchResult> ftSearch(String var1, String var2, FTSearchParams var3);

   Response<SearchResult> ftSearch(String var1, Query var2);

   @Deprecated
   Response<SearchResult> ftSearch(byte[] var1, Query var2);

   Response<String> ftExplain(String var1, Query var2);

   Response<List<String>> ftExplainCLI(String var1, Query var2);

   Response<AggregationResult> ftAggregate(String var1, AggregationBuilder var2);

   Response<String> ftSynUpdate(String var1, String var2, String... var3);

   Response<Map<String, List<String>>> ftSynDump(String var1);

   Response<Long> ftDictAdd(String var1, String... var2);

   Response<Long> ftDictDel(String var1, String... var2);

   Response<Set<String>> ftDictDump(String var1);

   Response<Long> ftDictAddBySampleKey(String var1, String var2, String... var3);

   Response<Long> ftDictDelBySampleKey(String var1, String var2, String... var3);

   Response<Set<String>> ftDictDumpBySampleKey(String var1, String var2);

   Response<Map<String, Map<String, Double>>> ftSpellCheck(String var1, String var2);

   Response<Map<String, Map<String, Double>>> ftSpellCheck(String var1, String var2, FTSpellCheckParams var3);

   Response<Map<String, Object>> ftInfo(String var1);

   Response<Set<String>> ftTagVals(String var1, String var2);

   Response<Map<String, Object>> ftConfigGet(String var1);

   Response<Map<String, Object>> ftConfigGet(String var1, String var2);

   Response<String> ftConfigSet(String var1, String var2);

   Response<String> ftConfigSet(String var1, String var2, String var3);

   Response<Long> ftSugAdd(String var1, String var2, double var3);

   Response<Long> ftSugAddIncr(String var1, String var2, double var3);

   Response<List<String>> ftSugGet(String var1, String var2);

   Response<List<String>> ftSugGet(String var1, String var2, boolean var3, int var4);

   Response<List<Tuple>> ftSugGetWithScores(String var1, String var2);

   Response<List<Tuple>> ftSugGetWithScores(String var1, String var2, boolean var3, int var4);

   Response<Boolean> ftSugDel(String var1, String var2);

   Response<Long> ftSugLen(String var1);
}
