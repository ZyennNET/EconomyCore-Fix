package redis.clients.jedis.providers;

import java.util.Collections;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Connection;

public interface ConnectionProvider extends AutoCloseable {
   Connection getConnection();

   Connection getConnection(CommandArguments var1);

   default Map<?, ?> getConnectionMap() {
      Connection var1 = this.getConnection();
      return Collections.singletonMap(var1.toString(), var1);
   }
}
