package redis.clients.jedis;

public abstract class JedisMonitor {
   protected Connection client;

   public void proceed(Connection var1) {
      this.client = var1;
      this.client.setTimeoutInfinite();

      do {
         String var2 = var1.getBulkReply();
         this.onCommand(var2);
      } while(var1.isConnected());

   }

   public abstract void onCommand(String var1);
}
