package redis.clients.jedis.commands;

import redis.clients.jedis.Response;

public interface HyperLogLogPipelineBinaryCommands {
   Response<Long> pfadd(byte[] var1, byte[]... var2);

   Response<String> pfmerge(byte[] var1, byte[]... var2);

   Response<Long> pfcount(byte[] var1);

   Response<Long> pfcount(byte[]... var1);
}
