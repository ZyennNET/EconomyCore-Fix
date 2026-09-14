package redis.clients.jedis;

import java.net.URI;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.util.Pool;

public class JedisPool extends Pool<Jedis> {
   private static final Logger log = LoggerFactory.getLogger(JedisPool.class);

   public JedisPool() {
      this((String)"127.0.0.1", 6379);
   }

   public JedisPool(String var1) {
      this(URI.create(var1));
   }

   public JedisPool(String var1, SSLSocketFactory var2, SSLParameters var3, HostnameVerifier var4) {
      this((GenericObjectPoolConfig)(new GenericObjectPoolConfig()), (PooledObjectFactory)(new JedisFactory(URI.create(var1), 2000, 2000, (String)null, var2, var3, var4)));
   }

   public JedisPool(String var1, int var2) {
      this(new HostAndPort(var1, var2), (JedisClientConfig)DefaultJedisClientConfig.builder().build());
   }

   public JedisPool(String var1, int var2, boolean var3) {
      this(new HostAndPort(var1, var2), (JedisClientConfig)DefaultJedisClientConfig.builder().ssl(var3).build());
   }

   public JedisPool(String var1, int var2, boolean var3, SSLSocketFactory var4, SSLParameters var5, HostnameVerifier var6) {
      this(new HostAndPort(var1, var2), (JedisClientConfig)DefaultJedisClientConfig.builder().ssl(var3).sslSocketFactory(var4).sslParameters(var5).hostnameVerifier(var6).build());
   }

   public JedisPool(String var1, int var2, String var3, String var4) {
      this(new HostAndPort(var1, var2), (JedisClientConfig)DefaultJedisClientConfig.builder().user(var3).password(var4).build());
   }

   public JedisPool(HostAndPort var1, JedisClientConfig var2) {
      this((PooledObjectFactory)(new JedisFactory(var1, var2)));
   }

   public JedisPool(PooledObjectFactory<Jedis> var1) {
      super(var1);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1) {
      this(var1, (String)"127.0.0.1", 6379);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2) {
      this(var1, URI.create(var2));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3) {
      this(var1, (String)var2, var3, 2000);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, boolean var4) {
      this(var1, var2, var3, 2000, var4);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, boolean var4, SSLSocketFactory var5, SSLParameters var6, HostnameVerifier var7) {
      this(var1, var2, var3, 2000, var4, var5, var6, var7);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, String var4, String var5) {
      this(var1, var2, var3, 2000, var4, var5, 0);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4) {
      this(var1, var2, var3, var4, (String)null);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, boolean var5) {
      this(var1, var2, var3, var4, (String)null, var5);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, boolean var5, SSLSocketFactory var6, SSLParameters var7, HostnameVerifier var8) {
      this(var1, var2, var3, var4, (String)null, var5, var6, var7, var8);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5) {
      this(var1, var2, var3, var4, var5, 0);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, boolean var6) {
      this(var1, var2, var3, var4, var5, 0, var6);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, boolean var6, SSLSocketFactory var7, SSLParameters var8, HostnameVerifier var9) {
      this(var1, var2, var3, var4, var5, 0, var6, var7, var8, var9);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, String var6) {
      this(var1, var2, var3, var4, var5, var6, 0);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, String var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, 0, var7);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, int var6) {
      this(var1, var2, var3, var4, var5, var6, (String)null);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, int var6, boolean var7) {
      this(var1, var2, var3, var4, var5, var6, (String)null, var7);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, int var6, boolean var7, SSLSocketFactory var8, SSLParameters var9, HostnameVerifier var10) {
      this(var1, var2, var3, var4, var5, var6, (String)null, var7, var8, var9, var10);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, String var6, int var7) {
      this(var1, var2, var3, var4, var5, var6, var7, (String)null);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, String var6, int var7, boolean var8) {
      this(var1, var2, var3, var4, var5, var6, var7, (String)null, var8);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, int var6, String var7) {
      this(var1, var2, var3, var4, var4, var5, var6, var7);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, int var6, String var7, boolean var8) {
      this(var1, var2, var3, var4, var4, var5, var6, var7, var8);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, int var6, String var7, boolean var8, SSLSocketFactory var9, SSLParameters var10, HostnameVerifier var11) {
      this(var1, var2, var3, var4, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, String var6, int var7, String var8) {
      this(var1, var2, var3, var4, var4, var5, var6, var7, var8);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, String var5, String var6, int var7, String var8, boolean var9) {
      this(var1, var2, var3, var4, var4, var5, var6, var7, var8, var9);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, String var6, int var7, String var8) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, var5, var6, var7, var8)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, String var6, int var7, String var8, boolean var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, String var6, int var7, String var8, boolean var9, SSLSocketFactory var10, SSLParameters var11, HostnameVerifier var12) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, String var6, String var7, int var8, String var9) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, var5, var6, var7, var8, var9)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, String var6, String var7, int var8, String var9, boolean var10) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, String var6, String var7, int var8, String var9, boolean var10, SSLSocketFactory var11, SSLParameters var12, HostnameVerifier var13) {
      this(var1, var2, var3, var4, var5, 0, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, int var6, String var7, int var8, String var9, boolean var10, SSLSocketFactory var11, SSLParameters var12, HostnameVerifier var13) {
      this(var1, var2, var3, var4, var5, var6, (String)null, var7, var8, var9, var10, var11, var12, var13);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, int var6, String var7, String var8, int var9, String var10) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, var5, var6, var7, var8, var9, var10)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, String var2, int var3, int var4, int var5, int var6, String var7, String var8, int var9, String var10, boolean var11, SSLSocketFactory var12, SSLParameters var13, HostnameVerifier var14) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14)));
   }

   public JedisPool(URI var1) {
      this(new GenericObjectPoolConfig(), var1);
   }

   public JedisPool(URI var1, SSLSocketFactory var2, SSLParameters var3, HostnameVerifier var4) {
      this(new GenericObjectPoolConfig(), var1, var2, var3, var4);
   }

   public JedisPool(URI var1, int var2) {
      this(new GenericObjectPoolConfig(), var1, var2);
   }

   public JedisPool(URI var1, int var2, SSLSocketFactory var3, SSLParameters var4, HostnameVerifier var5) {
      this(new GenericObjectPoolConfig(), var1, var2, var3, var4, var5);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2) {
      this(var1, (URI)var2, 2000);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2, SSLSocketFactory var3, SSLParameters var4, HostnameVerifier var5) {
      this(var1, var2, 2000, var3, var4, var5);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2, int var3) {
      this(var1, var2, var3, var3);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2, int var3, SSLSocketFactory var4, SSLParameters var5, HostnameVerifier var6) {
      this(var1, var2, var3, var3, var4, var5, var6);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2, int var3, int var4) {
      this(var1, var2, var3, var4, (SSLSocketFactory)null, (SSLParameters)null, (HostnameVerifier)null);
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2, int var3, int var4, SSLSocketFactory var5, SSLParameters var6, HostnameVerifier var7) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, (String)null, var5, var6, var7)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, URI var2, int var3, int var4, int var5, SSLSocketFactory var6, SSLParameters var7, HostnameVerifier var8) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3, var4, var5, (String)null, var6, var7, var8)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, HostAndPort var2, JedisClientConfig var3) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, JedisSocketFactory var2, JedisClientConfig var3) {
      this((GenericObjectPoolConfig)var1, (PooledObjectFactory)(new JedisFactory(var2, var3)));
   }

   public JedisPool(GenericObjectPoolConfig<Jedis> var1, PooledObjectFactory<Jedis> var2) {
      super(var1, var2);
   }

   public Jedis getResource() {
      Jedis var1 = (Jedis)super.getResource();
      var1.setDataSource(this);
      return var1;
   }

   public void returnResource(Jedis var1) {
      if (var1 != null) {
         try {
            var1.resetState();
            super.returnResource(var1);
         } catch (RuntimeException var3) {
            super.returnBrokenResource(var1);
            log.warn((String)"Resource is returned to the pool as broken", (Throwable)var3);
         }
      }

   }
}
