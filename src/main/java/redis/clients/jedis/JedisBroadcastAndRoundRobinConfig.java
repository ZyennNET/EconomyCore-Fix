package redis.clients.jedis;

public interface JedisBroadcastAndRoundRobinConfig {
   RediSearchMode getRediSearchModeInCluster();

   public static enum RediSearchMode {
      DEFAULT,
      LIGHT;
   }
}
