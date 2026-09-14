package redis.clients.jedis;

public final class DefaultRedisCredentialsProvider implements RedisCredentialsProvider {
   private volatile RedisCredentials credentials;

   public DefaultRedisCredentialsProvider(RedisCredentials var1) {
      this.credentials = var1;
   }

   public void setCredentials(RedisCredentials var1) {
      this.credentials = var1;
   }

   public RedisCredentials get() {
      return this.credentials;
   }
}
