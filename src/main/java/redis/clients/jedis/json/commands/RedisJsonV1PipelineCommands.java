package redis.clients.jedis.json.commands;

import java.util.List;
import redis.clients.jedis.Response;
import redis.clients.jedis.json.JsonSetParams;
import redis.clients.jedis.json.Path;

@Deprecated
public interface RedisJsonV1PipelineCommands {
   @Deprecated
   default Response<String> jsonSetLegacy(String var1, Object var2) {
      return this.jsonSet(var1, Path.ROOT_PATH, var2);
   }

   @Deprecated
   default Response<String> jsonSetLegacy(String var1, Object var2, JsonSetParams var3) {
      return this.jsonSet(var1, Path.ROOT_PATH, var2, var3);
   }

   @Deprecated
   Response<String> jsonSet(String var1, Path var2, Object var3);

   @Deprecated
   Response<String> jsonSet(String var1, Path var2, Object var3, JsonSetParams var4);

   @Deprecated
   Response<String> jsonMerge(String var1, Path var2, Object var3);

   Response<Object> jsonGet(String var1);

   @Deprecated
   <T> Response<T> jsonGet(String var1, Class<T> var2);

   @Deprecated
   Response<Object> jsonGet(String var1, Path... var2);

   @Deprecated
   <T> Response<T> jsonGet(String var1, Class<T> var2, Path... var3);

   @Deprecated
   default <T> Response<List<T>> jsonMGet(Class<T> var1, String... var2) {
      return this.jsonMGet(Path.ROOT_PATH, var1, var2);
   }

   @Deprecated
   <T> Response<List<T>> jsonMGet(Path var1, Class<T> var2, String... var3);

   Response<Long> jsonDel(String var1);

   @Deprecated
   Response<Long> jsonDel(String var1, Path var2);

   @Deprecated
   Response<Long> jsonClear(String var1);

   @Deprecated
   Response<Long> jsonClear(String var1, Path var2);

   @Deprecated
   Response<String> jsonToggle(String var1, Path var2);

   @Deprecated
   Response<Class<?>> jsonType(String var1);

   @Deprecated
   Response<Class<?>> jsonType(String var1, Path var2);

   @Deprecated
   Response<Long> jsonStrAppend(String var1, Object var2);

   @Deprecated
   Response<Long> jsonStrAppend(String var1, Path var2, Object var3);

   @Deprecated
   Response<Long> jsonStrLen(String var1);

   @Deprecated
   Response<Long> jsonStrLen(String var1, Path var2);

   @Deprecated
   Response<Double> jsonNumIncrBy(String var1, Path var2, double var3);

   @Deprecated
   Response<Long> jsonArrAppend(String var1, Path var2, Object... var3);

   @Deprecated
   Response<Long> jsonArrIndex(String var1, Path var2, Object var3);

   @Deprecated
   Response<Long> jsonArrInsert(String var1, Path var2, int var3, Object... var4);

   @Deprecated
   Response<Object> jsonArrPop(String var1);

   @Deprecated
   <T> Response<T> jsonArrPop(String var1, Class<T> var2);

   @Deprecated
   Response<Object> jsonArrPop(String var1, Path var2);

   @Deprecated
   <T> Response<T> jsonArrPop(String var1, Class<T> var2, Path var3);

   @Deprecated
   Response<Object> jsonArrPop(String var1, Path var2, int var3);

   @Deprecated
   <T> Response<T> jsonArrPop(String var1, Class<T> var2, Path var3, int var4);

   @Deprecated
   Response<Long> jsonArrLen(String var1);

   @Deprecated
   Response<Long> jsonArrLen(String var1, Path var2);

   @Deprecated
   Response<Long> jsonArrTrim(String var1, Path var2, int var3, int var4);
}
