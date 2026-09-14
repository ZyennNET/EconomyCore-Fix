package redis.clients.jedis;

public enum RedisProtocol {
   RESP2("2"),
   RESP3("3");

   private final String version;

   private RedisProtocol(String var3) {
      this.version = var3;
   }

   public String version() {
      return this.version;
   }
}
