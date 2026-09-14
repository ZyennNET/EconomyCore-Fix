package redis.clients.jedis;

public interface RedisCredentials {
   default String getUser() {
      return null;
   }

   default char[] getPassword() {
      return null;
   }
}
