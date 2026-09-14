package redis.clients.jedis;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.util.IOUtils;

public class DefaultJedisSocketFactory implements JedisSocketFactory {
   protected static final HostAndPort DEFAULT_HOST_AND_PORT = new HostAndPort("127.0.0.1", 6379);
   private volatile HostAndPort hostAndPort;
   private int connectionTimeout;
   private int socketTimeout;
   private boolean ssl;
   private SSLSocketFactory sslSocketFactory;
   private SSLParameters sslParameters;
   private HostnameVerifier hostnameVerifier;
   private HostAndPortMapper hostAndPortMapper;

   public DefaultJedisSocketFactory() {
      this.hostAndPort = DEFAULT_HOST_AND_PORT;
      this.connectionTimeout = 2000;
      this.socketTimeout = 2000;
      this.ssl = false;
      this.sslSocketFactory = null;
      this.sslParameters = null;
      this.hostnameVerifier = null;
      this.hostAndPortMapper = null;
   }

   public DefaultJedisSocketFactory(HostAndPort var1) {
      this(var1, (JedisClientConfig)null);
   }

   public DefaultJedisSocketFactory(JedisClientConfig var1) {
      this((HostAndPort)null, var1);
   }

   public DefaultJedisSocketFactory(HostAndPort var1, JedisClientConfig var2) {
      this.hostAndPort = DEFAULT_HOST_AND_PORT;
      this.connectionTimeout = 2000;
      this.socketTimeout = 2000;
      this.ssl = false;
      this.sslSocketFactory = null;
      this.sslParameters = null;
      this.hostnameVerifier = null;
      this.hostAndPortMapper = null;
      if (var1 != null) {
         this.hostAndPort = var1;
      }

      if (var2 != null) {
         this.connectionTimeout = var2.getConnectionTimeoutMillis();
         this.socketTimeout = var2.getSocketTimeoutMillis();
         this.ssl = var2.isSsl();
         this.sslSocketFactory = var2.getSslSocketFactory();
         this.sslParameters = var2.getSslParameters();
         this.hostnameVerifier = var2.getHostnameVerifier();
         this.hostAndPortMapper = var2.getHostAndPortMapper();
      }

   }

   private Socket connectToFirstSuccessfulHost(HostAndPort var1) throws Exception {
      List var2 = Arrays.asList(InetAddress.getAllByName(var1.getHost()));
      if (var2.size() > 1) {
         Collections.shuffle(var2);
      }

      JedisConnectionException var3 = new JedisConnectionException("Failed to connect to any host resolved for DNS name.");

      for(InetAddress var5 : var2) {
         try {
            Socket var6 = new Socket();
            var6.setReuseAddress(true);
            var6.setKeepAlive(true);
            var6.setTcpNoDelay(true);
            var6.setSoLinger(true, 0);
            var6.connect(new InetSocketAddress(var5, var1.getPort()), this.connectionTimeout);
            return var6;
         } catch (Exception var7) {
            var3.addSuppressed(var7);
         }
      }

      throw var3;
   }

   public Socket createSocket() throws JedisConnectionException {
      Socket var1 = null;

      try {
         HostAndPort var2 = this.getSocketHostAndPort();
         var1 = this.connectToFirstSuccessfulHost(var2);
         var1.setSoTimeout(this.socketTimeout);
         if (this.ssl) {
            SSLSocketFactory var3 = this.sslSocketFactory;
            if (null == var3) {
               var3 = (SSLSocketFactory)SSLSocketFactory.getDefault();
            }

            var1 = var3.createSocket(var1, var2.getHost(), var2.getPort(), true);
            if (null != this.sslParameters) {
               ((SSLSocket)var1).setSSLParameters(this.sslParameters);
            }

            if (null != this.hostnameVerifier && !this.hostnameVerifier.verify(var2.getHost(), ((SSLSocket)var1).getSession())) {
               String var4 = String.format("The connection to '%s' failed ssl/tls hostname verification.", var2.getHost());
               throw new JedisConnectionException(var4);
            }
         }

         return var1;
      } catch (Exception var5) {
         IOUtils.closeQuietly(var1);
         if (var5 instanceof JedisConnectionException) {
            throw (JedisConnectionException)var5;
         } else {
            throw new JedisConnectionException("Failed to create socket.", var5);
         }
      }
   }

   public void updateHostAndPort(HostAndPort var1) {
      this.hostAndPort = var1;
   }

   public HostAndPort getHostAndPort() {
      return this.hostAndPort;
   }

   protected HostAndPort getSocketHostAndPort() {
      HostAndPortMapper var1 = this.hostAndPortMapper;
      HostAndPort var2 = this.hostAndPort;
      if (var1 != null) {
         HostAndPort var3 = var1.getHostAndPort(var2);
         if (var3 != null) {
            return var3;
         }
      }

      return var2;
   }

   public String toString() {
      return "DefaultJedisSocketFactory{" + this.hostAndPort.toString() + "}";
   }
}
