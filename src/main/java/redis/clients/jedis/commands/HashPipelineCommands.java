package redis.clients.jedis.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public interface HashPipelineCommands {
   Response<Long> hset(String var1, String var2, String var3);

   Response<Long> hset(String var1, Map<String, String> var2);

   Response<String> hget(String var1, String var2);

   Response<Long> hsetnx(String var1, String var2, String var3);

   Response<String> hmset(String var1, Map<String, String> var2);

   Response<List<String>> hmget(String var1, String... var2);

   Response<Long> hincrBy(String var1, String var2, long var3);

   Response<Double> hincrByFloat(String var1, String var2, double var3);

   Response<Boolean> hexists(String var1, String var2);

   Response<Long> hdel(String var1, String... var2);

   Response<Long> hlen(String var1);

   Response<Set<String>> hkeys(String var1);

   Response<List<String>> hvals(String var1);

   Response<Map<String, String>> hgetAll(String var1);

   Response<String> hrandfield(String var1);

   Response<List<String>> hrandfield(String var1, long var2);

   Response<List<Map.Entry<String, String>>> hrandfieldWithValues(String var1, long var2);

   default Response<ScanResult<Map.Entry<String, String>>> hscan(String var1, String var2) {
      return this.hscan(var1, var2, new ScanParams());
   }

   Response<ScanResult<Map.Entry<String, String>>> hscan(String var1, String var2, ScanParams var3);

   Response<Long> hstrlen(String var1, String var2);
}
