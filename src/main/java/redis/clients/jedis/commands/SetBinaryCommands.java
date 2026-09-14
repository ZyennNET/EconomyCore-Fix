package redis.clients.jedis.commands;

import java.util.List;
import java.util.Set;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.resps.ScanResult;

public interface SetBinaryCommands {
   long sadd(byte[] var1, byte[]... var2);

   Set<byte[]> smembers(byte[] var1);

   long srem(byte[] var1, byte[]... var2);

   byte[] spop(byte[] var1);

   Set<byte[]> spop(byte[] var1, long var2);

   long scard(byte[] var1);

   boolean sismember(byte[] var1, byte[] var2);

   List<Boolean> smismember(byte[] var1, byte[]... var2);

   byte[] srandmember(byte[] var1);

   List<byte[]> srandmember(byte[] var1, int var2);

   default ScanResult<byte[]> sscan(byte[] var1, byte[] var2) {
      return this.sscan(var1, var2, new ScanParams());
   }

   ScanResult<byte[]> sscan(byte[] var1, byte[] var2, ScanParams var3);

   Set<byte[]> sdiff(byte[]... var1);

   long sdiffstore(byte[] var1, byte[]... var2);

   Set<byte[]> sinter(byte[]... var1);

   long sinterstore(byte[] var1, byte[]... var2);

   long sintercard(byte[]... var1);

   long sintercard(int var1, byte[]... var2);

   Set<byte[]> sunion(byte[]... var1);

   long sunionstore(byte[] var1, byte[]... var2);

   long smove(byte[] var1, byte[] var2, byte[] var3);
}
