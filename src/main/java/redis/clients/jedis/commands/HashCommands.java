package redis.clients.jedis.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public interface HashCommands {
   long hset(String var1, String var2, String var3);

   long hset(String var1, Map<String, String> var2);

   String hget(String var1, String var2);

   long hsetnx(String var1, String var2, String var3);

   String hmset(String var1, Map<String, String> var2);

   List<String> hmget(String var1, String... var2);

   long hincrBy(String var1, String var2, long var3);

   double hincrByFloat(String var1, String var2, double var3);

   boolean hexists(String var1, String var2);

   long hdel(String var1, String... var2);

   long hlen(String var1);

   Set<String> hkeys(String var1);

   List<String> hvals(String var1);

   Map<String, String> hgetAll(String var1);

   String hrandfield(String var1);

   List<String> hrandfield(String var1, long var2);

   List<Map.Entry<String, String>> hrandfieldWithValues(String var1, long var2);

   default ScanResult<Map.Entry<String, String>> hscan(String var1, String var2) {
      return this.hscan(var1, var2, new ScanParams());
   }

   ScanResult<Map.Entry<String, String>> hscan(String var1, String var2, ScanParams var3);

   long hstrlen(String var1, String var2);
}
