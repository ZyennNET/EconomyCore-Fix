package redis.clients.jedis;

import java.util.function.Supplier;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;

public final class DefaultJedisClientConfig implements JedisClientConfig {
   private final RedisProtocol redisProtocol;
   private final int connectionTimeoutMillis;
   private final int socketTimeoutMillis;
   private final int blockingSocketTimeoutMillis;
   private volatile Supplier<RedisCredentials> credentialsProvider;
   private final int database;
   private final String clientName;
   private final boolean ssl;
   private final SSLSocketFactory sslSocketFactory;
   private final SSLParameters sslParameters;
   private final HostnameVerifier hostnameVerifier;
   private final HostAndPortMapper hostAndPortMapper;
   private final ClientSetInfoConfig clientSetInfoConfig;

   private DefaultJedisClientConfig(RedisProtocol var1, int var2, int var3, int var4, Supplier<RedisCredentials> var5, int var6, String var7, boolean var8, SSLSocketFactory var9, SSLParameters var10, HostnameVerifier var11, HostAndPortMapper var12, ClientSetInfoConfig var13) {
      this.redisProtocol = var1;
      this.connectionTimeoutMillis = var2;
      this.socketTimeoutMillis = var3;
      this.blockingSocketTimeoutMillis = var4;
      this.credentialsProvider = var5;
      this.database = var6;
      this.clientName = var7;
      this.ssl = var8;
      this.sslSocketFactory = var9;
      this.sslParameters = var10;
      this.hostnameVerifier = var11;
      this.hostAndPortMapper = var12;
      this.clientSetInfoConfig = var13;
   }

   public RedisProtocol getRedisProtocol() {
      return this.redisProtocol;
   }

   public int getConnectionTimeoutMillis() {
      return this.connectionTimeoutMillis;
   }

   public int getSocketTimeoutMillis() {
      return this.socketTimeoutMillis;
   }

   public int getBlockingSocketTimeoutMillis() {
      return this.blockingSocketTimeoutMillis;
   }

   public String getUser() {
      return ((RedisCredentials)this.credentialsProvider.get()).getUser();
   }

   public String getPassword() {
      char[] var1 = ((RedisCredentials)this.credentialsProvider.get()).getPassword();
      return var1 == null ? null : new String(var1);
   }

   public Supplier<RedisCredentials> getCredentialsProvider() {
      return this.credentialsProvider;
   }

   public int getDatabase() {
      return this.database;
   }

   public String getClientName() {
      return this.clientName;
   }

   public boolean isSsl() {
      return this.ssl;
   }

   public SSLSocketFactory getSslSocketFactory() {
      return this.sslSocketFactory;
   }

   public SSLParameters getSslParameters() {
      return this.sslParameters;
   }

   public HostnameVerifier getHostnameVerifier() {
      return this.hostnameVerifier;
   }

   public HostAndPortMapper getHostAndPortMapper() {
      return this.hostAndPortMapper;
   }

   public ClientSetInfoConfig getClientSetInfoConfig() {
      return this.clientSetInfoConfig;
   }

   public static Builder builder() {
      return new Builder();
   }

   public static DefaultJedisClientConfig create(int var0, int var1, int var2, String var3, String var4, int var5, String var6, boolean var7, SSLSocketFactory var8, SSLParameters var9, HostnameVerifier var10, HostAndPortMapper var11) {
      return new DefaultJedisClientConfig((RedisProtocol)null, var0, var1, var2, new DefaultRedisCredentialsProvider(new DefaultRedisCredentials(var3, var4)), var5, var6, var7, var8, var9, var10, var11, (ClientSetInfoConfig)null);
   }

   public static DefaultJedisClientConfig copyConfig(JedisClientConfig var0) {
      return new DefaultJedisClientConfig(var0.getRedisProtocol(), var0.getConnectionTimeoutMillis(), var0.getSocketTimeoutMillis(), var0.getBlockingSocketTimeoutMillis(), var0.getCredentialsProvider(), var0.getDatabase(), var0.getClientName(), var0.isSsl(), var0.getSslSocketFactory(), var0.getSslParameters(), var0.getHostnameVerifier(), var0.getHostAndPortMapper(), var0.getClientSetInfoConfig());
   }

   public static class Builder {
      private RedisProtocol redisProtocol;
      private int connectionTimeoutMillis;
      private int socketTimeoutMillis;
      private int blockingSocketTimeoutMillis;
      private String user;
      private String password;
      private Supplier<RedisCredentials> credentialsProvider;
      private int database;
      private String clientName;
      private boolean ssl;
      private SSLSocketFactory sslSocketFactory;
      private SSLParameters sslParameters;
      private HostnameVerifier hostnameVerifier;
      private HostAndPortMapper hostAndPortMapper;
      private ClientSetInfoConfig clientSetInfoConfig;

      private Builder() {
         this.redisProtocol = null;
         this.connectionTimeoutMillis = 2000;
         this.socketTimeoutMillis = 2000;
         this.blockingSocketTimeoutMillis = 0;
         this.user = null;
         this.password = null;
         this.database = 0;
         this.clientName = null;
         this.ssl = false;
         this.sslSocketFactory = null;
         this.sslParameters = null;
         this.hostnameVerifier = null;
         this.hostAndPortMapper = null;
         this.clientSetInfoConfig = ClientSetInfoConfig.DEFAULT;
      }

      public DefaultJedisClientConfig build() {
         if (this.credentialsProvider == null) {
            this.credentialsProvider = new DefaultRedisCredentialsProvider(new DefaultRedisCredentials(this.user, this.password));
         }

         return new DefaultJedisClientConfig(this.redisProtocol, this.connectionTimeoutMillis, this.socketTimeoutMillis, this.blockingSocketTimeoutMillis, this.credentialsProvider, this.database, this.clientName, this.ssl, this.sslSocketFactory, this.sslParameters, this.hostnameVerifier, this.hostAndPortMapper, this.clientSetInfoConfig);
      }

      public Builder resp3() {
         return this.protocol(RedisProtocol.RESP3);
      }

      public Builder protocol(RedisProtocol var1) {
         this.redisProtocol = var1;
         return this;
      }

      public Builder timeoutMillis(int var1) {
         this.connectionTimeoutMillis = var1;
         this.socketTimeoutMillis = var1;
         return this;
      }

      public Builder connectionTimeoutMillis(int var1) {
         this.connectionTimeoutMillis = var1;
         return this;
      }

      public Builder socketTimeoutMillis(int var1) {
         this.socketTimeoutMillis = var1;
         return this;
      }

      public Builder blockingSocketTimeoutMillis(int var1) {
         this.blockingSocketTimeoutMillis = var1;
         return this;
      }

      public Builder user(String var1) {
         this.user = var1;
         return this;
      }

      public Builder password(String var1) {
         this.password = var1;
         return this;
      }

      public Builder credentials(RedisCredentials var1) {
         this.credentialsProvider = new DefaultRedisCredentialsProvider(var1);
         return this;
      }

      public Builder credentialsProvider(Supplier<RedisCredentials> var1) {
         this.credentialsProvider = var1;
         return this;
      }

      public Builder database(int var1) {
         this.database = var1;
         return this;
      }

      public Builder clientName(String var1) {
         this.clientName = var1;
         return this;
      }

      public Builder ssl(boolean var1) {
         this.ssl = var1;
         return this;
      }

      public Builder sslSocketFactory(SSLSocketFactory var1) {
         this.sslSocketFactory = var1;
         return this;
      }

      public Builder sslParameters(SSLParameters var1) {
         this.sslParameters = var1;
         return this;
      }

      public Builder hostnameVerifier(HostnameVerifier var1) {
         this.hostnameVerifier = var1;
         return this;
      }

      public Builder hostAndPortMapper(HostAndPortMapper var1) {
         this.hostAndPortMapper = var1;
         return this;
      }

      public Builder clientSetInfoConfig(ClientSetInfoConfig var1) {
         this.clientSetInfoConfig = var1;
         return this;
      }
   }
}
