package redis.clients.jedis.commands;

import java.util.List;
import redis.clients.jedis.Response;
import redis.clients.jedis.args.FlushMode;
import redis.clients.jedis.util.KeyValue;

public interface SampleKeyedPipelineCommands {
   Response<Long> waitReplicas(String var1, int var2, long var3);

   Response<KeyValue<Long, Long>> waitAOF(String var1, long var2, long var4, long var6);

   Response<Object> eval(String var1, String var2);

   Response<Object> evalsha(String var1, String var2);

   Response<List<Boolean>> scriptExists(String var1, String... var2);

   Response<String> scriptLoad(String var1, String var2);

   Response<String> scriptFlush(String var1);

   Response<String> scriptFlush(String var1, FlushMode var2);

   Response<String> scriptKill(String var1);
}
