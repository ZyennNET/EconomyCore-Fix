package redis.clients.jedis;

import java.net.URI;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.InvalidURIException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.util.JedisURIHelper;

public class JedisFactory implements PooledObjectFactory<Jedis> {
   private static final Logger logger = LoggerFactory.getLogger(JedisFactory.class);
   private final JedisSocketFactory jedisSocketFactory;
   private final JedisClientConfig clientConfig;

   protected JedisFactory(String var1, int var2, int var3, int var4, String var5, int var6, String var7) {
      this(var1, var2, var3, var4, var5, var6, var7, false, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   protected JedisFactory(String var1, int var2, int var3, int var4, String var5, String var6, int var7, String var8) {
      this(var1, var2, var3, var4, 0, var5, var6, var7, var8);
   }

   protected JedisFactory(String var1, int var2, int var3, int var4, int var5, String var6, String var7, int var8, String var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, false, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   JedisFactory(int var1, int var2, int var3, String var4, String var5, int var6, String var7) {
      this(var1, var2, var3, var4, var5, var6, var7, false, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   protected JedisFactory(String var1, int var2, int var3, int var4, String var5, int var6, String var7, boolean var8, SSLSocketFactory var9, SSLParameters var10, HostnameVerifier var11) {
      this(var1, var2, var3, var4, (String)null, var5, var6, var7, var8, var9, var10, var11);
   }

   protected JedisFactory(String var1, int var2, int var3, int var4, String var5, String var6, int var7, String var8, boolean var9, SSLSocketFactory var10, SSLParameters var11, HostnameVerifier var12) {
      this(var1, var2, var3, var4, 0, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   protected JedisFactory(HostAndPort var1, JedisClientConfig var2) {
      this.clientConfig = DefaultJedisClientConfig.copyConfig(var2);
      this.jedisSocketFactory = new DefaultJedisSocketFactory(var1, this.clientConfig);
   }

   protected JedisFactory(String var1, int var2, int var3, int var4, int var5, String var6, String var7, int var8, String var9, boolean var10, SSLSocketFactory var11, SSLParameters var12, HostnameVerifier var13) {
      this.clientConfig = DefaultJedisClientConfig.builder().connectionTimeoutMillis(var3).socketTimeoutMillis(var4).blockingSocketTimeoutMillis(var5).user(var6).password(var7).database(var8).clientName(var9).ssl(var10).sslSocketFactory(var11).sslParameters(var12).hostnameVerifier(var13).build();
      this.jedisSocketFactory = new DefaultJedisSocketFactory(new HostAndPort(var1, var2), this.clientConfig);
   }

   protected JedisFactory(JedisSocketFactory var1, JedisClientConfig var2) {
      this.clientConfig = DefaultJedisClientConfig.copyConfig(var2);
      this.jedisSocketFactory = var1;
   }

   JedisFactory(int var1, int var2, int var3, String var4, String var5, int var6, String var7, boolean var8, SSLSocketFactory var9, SSLParameters var10, HostnameVerifier var11) {
      this(DefaultJedisClientConfig.builder().connectionTimeoutMillis(var1).socketTimeoutMillis(var2).blockingSocketTimeoutMillis(var3).user(var4).password(var5).database(var6).clientName(var7).ssl(var8).sslSocketFactory(var9).sslParameters(var10).hostnameVerifier(var11).build());
   }

   JedisFactory(JedisClientConfig var1) {
      this.clientConfig = var1;
      this.jedisSocketFactory = new DefaultJedisSocketFactory(var1);
   }

   protected JedisFactory(URI var1, int var2, int var3, String var4) {
      this(var1, var2, var3, var4, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   protected JedisFactory(URI var1, int var2, int var3, String var4, SSLSocketFactory var5, SSLParameters var6, HostnameVerifier var7) {
      this(var1, var2, var3, 0, var4, var5, var6, var7);
   }

   protected JedisFactory(URI var1, int var2, int var3, int var4, String var5, SSLSocketFactory var6, SSLParameters var7, HostnameVerifier var8) {
      if (!JedisURIHelper.isValid(var1)) {
         throw new InvalidURIException(String.format("Cannot open Redis connection due invalid URI. %s", var1.toString()));
      } else {
         this.clientConfig = DefaultJedisClientConfig.builder().connectionTimeoutMillis(var2).socketTimeoutMillis(var3).blockingSocketTimeoutMillis(var4).user(JedisURIHelper.getUser(var1)).password(JedisURIHelper.getPassword(var1)).database(JedisURIHelper.getDBIndex(var1)).clientName(var5).protocol(JedisURIHelper.getRedisProtocol(var1)).ssl(JedisURIHelper.isRedisSSLScheme(var1)).sslSocketFactory(var6).sslParameters(var7).hostnameVerifier(var8).build();
         this.jedisSocketFactory = new DefaultJedisSocketFactory(new HostAndPort(var1.getHost(), var1.getPort()), this.clientConfig);
      }
   }

   void setHostAndPort(HostAndPort var1) {
      if (!(this.jedisSocketFactory instanceof DefaultJedisSocketFactory)) {
         throw new IllegalStateException("setHostAndPort method has limited capability.");
      } else {
         ((DefaultJedisSocketFactory)this.jedisSocketFactory).updateHostAndPort(var1);
      }
   }

   public void activateObject(PooledObject<Jedis> var1) throws Exception {
      Jedis var2 = (Jedis)var1.getObject();
      if (var2.getDB() != this.clientConfig.getDatabase()) {
         var2.select(this.clientConfig.getDatabase());
      }

   }

   public void destroyObject(PooledObject<Jedis> var1) throws Exception {
      Jedis var2 = (Jedis)var1.getObject();
      if (var2.isConnected()) {
         try {
            var2.close();
         } catch (RuntimeException var4) {
            logger.debug((String)"Error while close", (Throwable)var4);
         }
      }

   }

   public PooledObject<Jedis> makeObject() throws Exception {
      Jedis var1 = null;

      try {
         var1 = new Jedis(this.jedisSocketFactory, this.clientConfig);
         return new DefaultPooledObject<Jedis>(var1);
      } catch (JedisException var3) {
         logger.debug((String)"Error while makeObject", (Throwable)var3);
         throw var3;
      }
   }

   public void passivateObject(PooledObject<Jedis> var1) throws Exception {
   }

   public boolean validateObject(PooledObject<Jedis> var1) {
      Jedis var2 = (Jedis)var1.getObject();

      try {
         boolean var3 = true;
         if (this.jedisSocketFactory instanceof DefaultJedisSocketFactory) {
            HostAndPort var4 = ((DefaultJedisSocketFactory)this.jedisSocketFactory).getHostAndPort();
            HostAndPort var5 = var2.getConnection().getHostAndPort();
            var3 = var4.getHost().equals(var5.getHost()) && var4.getPort() == var5.getPort();
         }

         return var3 && var2.getConnection().isConnected() && var2.ping().equals("PONG");
      } catch (Exception var6) {
         logger.error((String)"Error while validating pooled Jedis object.", (Throwable)var6);
         return false;
      }
   }
}
