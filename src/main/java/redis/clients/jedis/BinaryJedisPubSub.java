package redis.clients.jedis;

public abstract class BinaryJedisPubSub extends JedisPubSubBase<byte[]> {
   protected final byte[] encode(byte[] var1) {
      return var1;
   }
}
