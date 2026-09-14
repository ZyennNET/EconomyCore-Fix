package redis.clients.jedis.commands;

import java.util.List;
import java.util.Map;
import java.util.Set;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public interface HashBinaryCommands {
   long hset(byte[] var1, byte[] var2, byte[] var3);

   long hset(byte[] var1, Map<byte[], byte[]> var2);

   byte[] hget(byte[] var1, byte[] var2);

   long hsetnx(byte[] var1, byte[] var2, byte[] var3);

   String hmset(byte[] var1, Map<byte[], byte[]> var2);

   List<byte[]> hmget(byte[] var1, byte[]... var2);

   long hincrBy(byte[] var1, byte[] var2, long var3);

   double hincrByFloat(byte[] var1, byte[] var2, double var3);

   boolean hexists(byte[] var1, byte[] var2);

   long hdel(byte[] var1, byte[]... var2);

   long hlen(byte[] var1);

   Set<byte[]> hkeys(byte[] var1);

   List<byte[]> hvals(byte[] var1);

   Map<byte[], byte[]> hgetAll(byte[] var1);

   byte[] hrandfield(byte[] var1);

   List<byte[]> hrandfield(byte[] var1, long var2);

   List<Map.Entry<byte[], byte[]>> hrandfieldWithValues(byte[] var1, long var2);

   default ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] var1, byte[] var2) {
      return this.hscan(var1, var2, new ScanParams());
   }

   ScanResult<Map.Entry<byte[], byte[]>> hscan(byte[] var1, byte[] var2, ScanParams var3);

   long hstrlen(byte[] var1, byte[] var2);
}
