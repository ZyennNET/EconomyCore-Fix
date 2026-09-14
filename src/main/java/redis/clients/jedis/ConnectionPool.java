package redis.clients.jedis;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.util.Pool;

public class ConnectionPool extends Pool<Connection> {
   public ConnectionPool(HostAndPort var1, JedisClientConfig var2) {
      this(new ConnectionFactory(var1, var2));
   }

   public ConnectionPool(PooledObjectFactory<Connection> var1) {
      super(var1);
   }

   public ConnectionPool(HostAndPort var1, JedisClientConfig var2, GenericObjectPoolConfig<Connection> var3) {
      this((PooledObjectFactory)(new ConnectionFactory(var1, var2)), (GenericObjectPoolConfig)var3);
   }

   public ConnectionPool(PooledObjectFactory<Connection> var1, GenericObjectPoolConfig<Connection> var2) {
      super(var1, var2);
   }

   public Connection getResource() {
      Connection var1 = (Connection)super.getResource();
      var1.setHandlingPool(this);
      return var1;
   }
}
