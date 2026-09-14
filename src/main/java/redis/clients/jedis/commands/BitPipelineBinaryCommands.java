package redis.clients.jedis.commands;

import java.util.List;
import redis.clients.jedis.Response;
import redis.clients.jedis.args.BitCountOption;
import redis.clients.jedis.args.BitOP;
import redis.clients.jedis.params.BitPosParams;

public interface BitPipelineBinaryCommands {
   Response<Boolean> setbit(byte[] var1, long var2, boolean var4);

   Response<Boolean> getbit(byte[] var1, long var2);

   Response<Long> bitcount(byte[] var1);

   Response<Long> bitcount(byte[] var1, long var2, long var4);

   Response<Long> bitcount(byte[] var1, long var2, long var4, BitCountOption var6);

   Response<Long> bitpos(byte[] var1, boolean var2);

   Response<Long> bitpos(byte[] var1, boolean var2, BitPosParams var3);

   Response<List<Long>> bitfield(byte[] var1, byte[]... var2);

   Response<List<Long>> bitfieldReadonly(byte[] var1, byte[]... var2);

   Response<Long> bitop(BitOP var1, byte[] var2, byte[]... var3);
}
