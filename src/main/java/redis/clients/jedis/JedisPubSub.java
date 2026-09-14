package redis.clients.jedis;

import redis.clients.jedis.util.SafeEncoder;

public abstract class JedisPubSub extends JedisPubSubBase<String> {
   protected final String encode(byte[] var1) {
      return SafeEncoder.encode(var1);
   }
}
