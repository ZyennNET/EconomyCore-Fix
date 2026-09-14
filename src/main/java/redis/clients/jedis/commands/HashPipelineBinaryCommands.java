package redis.clients.jedis.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.Response;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public interface HashPipelineBinaryCommands {
   Response<Long> hset(byte[] var1, byte[] var2, byte[] var3);

   Response<Long> hset(byte[] var1, Map<byte[], byte[]> var2);

   Response<byte[]> hget(byte[] var1, byte[] var2);

   Response<Long> hsetnx(byte[] var1, byte[] var2, byte[] var3);

   Response<String> hmset(byte[] var1, Map<byte[], byte[]> var2);

   Response<List<byte[]>> hmget(byte[] var1, byte[]... var2);

   Response<Long> hincrBy(byte[] var1, byte[] var2, long var3);

   Response<Double> hincrByFloat(byte[] var1, byte[] var2, double var3);

   Response<Boolean> hexists(byte[] var1, byte[] var2);

   Response<Long> hdel(byte[] var1, byte[]... var2);

   Response<Long> hlen(byte[] var1);

   Response<Set<byte[]>> hkeys(byte[] var1);

   Response<List<byte[]>> hvals(byte[] var1);

   Response<Map<byte[], byte[]>> hgetAll(byte[] var1);

   Response<byte[]> hrandfield(byte[] var1);

   Response<List<byte[]>> hrandfield(byte[] var1, long var2);

   Response<List<Map.Entry<byte[], byte[]>>> hrandfieldWithValues(byte[] var1, long var2);

   default Response<ScanResult<Map.Entry<byte[], byte[]>>> hscan(byte[] var1, byte[] var2) {
      return this.hscan(var1, var2, new ScanParams());
   }

   Response<ScanResult<Map.Entry<byte[], byte[]>>> hscan(byte[] var1, byte[] var2, ScanParams var3);

   Response<Long> hstrlen(byte[] var1, byte[] var2);
}
