package redis.clients.jedis;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisException;

public class ConnectionFactory implements PooledObjectFactory<Connection> {
   private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);
   private final JedisSocketFactory jedisSocketFactory;
   private final JedisClientConfig clientConfig;

   public ConnectionFactory(HostAndPort var1) {
      this.clientConfig = DefaultJedisClientConfig.builder().build();
      this.jedisSocketFactory = new DefaultJedisSocketFactory(var1);
   }

   public ConnectionFactory(HostAndPort var1, JedisClientConfig var2) {
      this.clientConfig = DefaultJedisClientConfig.copyConfig(var2);
      this.jedisSocketFactory = new DefaultJedisSocketFactory(var1, this.clientConfig);
   }

   public ConnectionFactory(JedisSocketFactory var1, JedisClientConfig var2) {
      this.clientConfig = DefaultJedisClientConfig.copyConfig(var2);
      this.jedisSocketFactory = var1;
   }

   public void activateObject(PooledObject<Connection> var1) throws Exception {
   }

   public void destroyObject(PooledObject<Connection> var1) throws Exception {
      Connection var2 = (Connection)var1.getObject();
      if (var2.isConnected()) {
         try {
            var2.close();
         } catch (RuntimeException var4) {
            logger.debug((String)"Error while close", (Throwable)var4);
         }
      }

   }

   public PooledObject<Connection> makeObject() throws Exception {
      Connection var1 = null;

      try {
         var1 = new Connection(this.jedisSocketFactory, this.clientConfig);
         return new DefaultPooledObject<Connection>(var1);
      } catch (JedisException var3) {
         logger.debug((String)"Error while makeObject", (Throwable)var3);
         throw var3;
      }
   }

   public void passivateObject(PooledObject<Connection> var1) throws Exception {
   }

   public boolean validateObject(PooledObject<Connection> var1) {
      Connection var2 = (Connection)var1.getObject();

      try {
         return var2.isConnected() && var2.ping();
      } catch (Exception var4) {
         logger.error((String)"Error while validating pooled Connection object.", (Throwable)var4);
         return false;
      }
   }
}
