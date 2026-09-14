package redis.clients.jedis;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import redis.clients.jedis.args.ClientAttributeOption;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.exceptions.JedisValidationException;
import redis.clients.jedis.util.IOUtils;
import redis.clients.jedis.util.RedisInputStream;
import redis.clients.jedis.util.RedisOutputStream;
import redis.clients.jedis.util.SafeEncoder;

public class Connection implements Closeable {
   private ConnectionPool memberOf;
   private RedisProtocol protocol;
   private final JedisSocketFactory socketFactory;
   private Socket socket;
   private RedisOutputStream outputStream;
   private RedisInputStream inputStream;
   private int soTimeout;
   private int infiniteSoTimeout;
   private boolean broken;

   public Connection() {
      this("127.0.0.1", 6379);
   }

   public Connection(String var1, int var2) {
      this(new HostAndPort(var1, var2));
   }

   public Connection(HostAndPort var1) {
      this((JedisSocketFactory)(new DefaultJedisSocketFactory(var1)));
   }

   public Connection(HostAndPort var1, JedisClientConfig var2) {
      this((JedisSocketFactory)(new DefaultJedisSocketFactory(var1, var2)));
      this.infiniteSoTimeout = var2.getBlockingSocketTimeoutMillis();
      this.initializeFromClientConfig(var2);
   }

   public Connection(JedisSocketFactory var1) {
      this.soTimeout = 0;
      this.infiniteSoTimeout = 0;
      this.broken = false;
      this.socketFactory = var1;
   }

   public Connection(JedisSocketFactory var1, JedisClientConfig var2) {
      this.soTimeout = 0;
      this.infiniteSoTimeout = 0;
      this.broken = false;
      this.socketFactory = var1;
      this.soTimeout = var2.getSocketTimeoutMillis();
      this.infiniteSoTimeout = var2.getBlockingSocketTimeoutMillis();
      this.initializeFromClientConfig(var2);
   }

   public String toString() {
      return "Connection{" + this.socketFactory + "}";
   }

   public final RedisProtocol getRedisProtocol() {
      return this.protocol;
   }

   public final void setHandlingPool(ConnectionPool var1) {
      this.memberOf = var1;
   }

   final HostAndPort getHostAndPort() {
      return ((DefaultJedisSocketFactory)this.socketFactory).getHostAndPort();
   }

   public int getSoTimeout() {
      return this.soTimeout;
   }

   public void setSoTimeout(int var1) {
      this.soTimeout = var1;
      if (this.socket != null) {
         try {
            this.socket.setSoTimeout(var1);
         } catch (SocketException var3) {
            this.broken = true;
            throw new JedisConnectionException(var3);
         }
      }

   }

   public void setTimeoutInfinite() {
      try {
         if (!this.isConnected()) {
            this.connect();
         }

         this.socket.setSoTimeout(this.infiniteSoTimeout);
      } catch (SocketException var2) {
         this.broken = true;
         throw new JedisConnectionException(var2);
      }
   }

   public void rollbackTimeout() {
      try {
         this.socket.setSoTimeout(this.soTimeout);
      } catch (SocketException var2) {
         this.broken = true;
         throw new JedisConnectionException(var2);
      }
   }

   public Object executeCommand(ProtocolCommand var1) {
      return this.executeCommand(new CommandArguments(var1));
   }

   public Object executeCommand(CommandArguments var1) {
      this.sendCommand(var1);
      return this.getOne();
   }

   public <T> T executeCommand(CommandObject<T> var1) {
      CommandArguments var2 = var1.getArguments();
      this.sendCommand(var2);
      if (!var2.isBlocking()) {
         return (T)var1.getBuilder().build(this.getOne());
      } else {
         Object var3;
         try {
            this.setTimeoutInfinite();
            var3 = var1.getBuilder().build(this.getOne());
         } finally {
            this.rollbackTimeout();
         }

         return (T)var3;
      }
   }

   public void sendCommand(ProtocolCommand var1) {
      this.sendCommand(new CommandArguments(var1));
   }

   public void sendCommand(ProtocolCommand var1, Rawable var2) {
      this.sendCommand((new CommandArguments(var1)).add(var2));
   }

   public void sendCommand(ProtocolCommand var1, String... var2) {
      this.sendCommand((new CommandArguments(var1)).addObjects(var2));
   }

   public void sendCommand(ProtocolCommand var1, byte[]... var2) {
      this.sendCommand((new CommandArguments(var1)).addObjects(var2));
   }

   public void sendCommand(CommandArguments var1) {
      try {
         this.connect();
         Protocol.sendCommand(this.outputStream, var1);
      } catch (JedisConnectionException var5) {
         JedisConnectionException var2 = var5;

         try {
            String var3 = Protocol.readErrorLineIfPossible(this.inputStream);
            if (var3 != null && var3.length() > 0) {
               var2 = new JedisConnectionException(var3, var2.getCause());
            }
         } catch (Exception var4) {
         }

         this.broken = true;
         throw var2;
      }
   }

   public void connect() throws JedisConnectionException {
      if (!this.isConnected()) {
         try {
            this.socket = this.socketFactory.createSocket();
            this.soTimeout = this.socket.getSoTimeout();
            this.outputStream = new RedisOutputStream(this.socket.getOutputStream());
            this.inputStream = new RedisInputStream(this.socket.getInputStream());
            this.broken = false;
         } catch (JedisConnectionException var6) {
            this.setBroken();
            throw var6;
         } catch (IOException var7) {
            this.setBroken();
            throw new JedisConnectionException("Failed to create input/output stream", var7);
         } finally {
            if (this.broken) {
               IOUtils.closeQuietly(this.socket);
            }

         }
      }

   }

   public void close() {
      if (this.memberOf != null) {
         ConnectionPool var1 = this.memberOf;
         this.memberOf = null;
         if (this.isBroken()) {
            var1.returnBrokenResource(this);
         } else {
            var1.returnResource(this);
         }
      } else {
         this.disconnect();
      }

   }

   public void disconnect() {
      if (this.isConnected()) {
         try {
            this.outputStream.flush();
            this.socket.close();
         } catch (IOException var5) {
            throw new JedisConnectionException(var5);
         } finally {
            IOUtils.closeQuietly(this.socket);
            this.setBroken();
         }
      }

   }

   public boolean isConnected() {
      return this.socket != null && this.socket.isBound() && !this.socket.isClosed() && this.socket.isConnected() && !this.socket.isInputShutdown() && !this.socket.isOutputShutdown();
   }

   public boolean isBroken() {
      return this.broken;
   }

   public void setBroken() {
      this.broken = true;
   }

   public String getStatusCodeReply() {
      this.flush();
      byte[] var1 = (byte[])this.readProtocolWithCheckingBroken();
      return null == var1 ? null : SafeEncoder.encode(var1);
   }

   public String getBulkReply() {
      byte[] var1 = this.getBinaryBulkReply();
      return null != var1 ? SafeEncoder.encode(var1) : null;
   }

   public byte[] getBinaryBulkReply() {
      this.flush();
      return (byte[])this.readProtocolWithCheckingBroken();
   }

   public Long getIntegerReply() {
      this.flush();
      return (Long)this.readProtocolWithCheckingBroken();
   }

   public List<String> getMultiBulkReply() {
      return BuilderFactory.STRING_LIST.build(this.getBinaryMultiBulkReply());
   }

   public List<byte[]> getBinaryMultiBulkReply() {
      this.flush();
      return (List)this.readProtocolWithCheckingBroken();
   }

   @Deprecated
   public List<Object> getUnflushedObjectMultiBulkReply() {
      return (List)this.readProtocolWithCheckingBroken();
   }

   public Object getUnflushedObject() {
      return this.readProtocolWithCheckingBroken();
   }

   public List<Object> getObjectMultiBulkReply() {
      this.flush();
      return (List)this.readProtocolWithCheckingBroken();
   }

   public List<Long> getIntegerMultiBulkReply() {
      this.flush();
      return (List)this.readProtocolWithCheckingBroken();
   }

   public Object getOne() {
      this.flush();
      return this.readProtocolWithCheckingBroken();
   }

   protected void flush() {
      try {
         this.outputStream.flush();
      } catch (IOException var2) {
         this.broken = true;
         throw new JedisConnectionException(var2);
      }
   }

   protected Object readProtocolWithCheckingBroken() {
      if (this.broken) {
         throw new JedisConnectionException("Attempting to read from a broken connection");
      } else {
         try {
            return Protocol.read(this.inputStream);
         } catch (JedisConnectionException var2) {
            this.broken = true;
            throw var2;
         }
      }
   }

   public List<Object> getMany(int var1) {
      this.flush();
      ArrayList var2 = new ArrayList(var1);

      for(int var3 = 0; var3 < var1; ++var3) {
         try {
            var2.add(this.readProtocolWithCheckingBroken());
         } catch (JedisDataException var5) {
            var2.add(var5);
         }
      }

      return var2;
   }

   private static boolean validateClientInfo(String var0) {
      for(int var1 = 0; var1 < var0.length(); ++var1) {
         char var2 = var0.charAt(var1);
         if (var2 < '!' || var2 > '~') {
            throw new JedisValidationException("client info cannot contain spaces, newlines or special characters.");
         }
      }

      return true;
   }

   private void initializeFromClientConfig(JedisClientConfig var1) {
      try {
         this.connect();
         this.protocol = var1.getRedisProtocol();
         Supplier var2 = var1.getCredentialsProvider();
         if (var2 instanceof RedisCredentialsProvider) {
            RedisCredentialsProvider var3 = (RedisCredentialsProvider)var2;

            try {
               var3.prepare();
               this.helloOrAuth(this.protocol, (RedisCredentials)var3.get());
            } finally {
               var3.cleanUp();
            }
         } else {
            this.helloOrAuth(this.protocol, (RedisCredentials)(var2 != null ? (RedisCredentials)var2.get() : new DefaultRedisCredentials(var1.getUser(), var1.getPassword())));
         }

         ArrayList var14 = new ArrayList();
         String var4 = var1.getClientName();
         if (var4 != null && validateClientInfo(var4)) {
            var14.add((new CommandArguments(Protocol.Command.CLIENT)).add(Protocol.Keyword.SETNAME).add(var4));
         }

         ClientSetInfoConfig var5 = var1.getClientSetInfoConfig();
         if (var5 == null) {
            var5 = ClientSetInfoConfig.DEFAULT;
         }

         if (!var5.isDisabled()) {
            String var6 = JedisMetaInfo.getArtifactId();
            if (var6 != null && validateClientInfo(var6)) {
               String var7 = var5.getLibNameSuffix();
               if (var7 != null) {
                  var6 = var6 + '(' + var7 + ')';
               }

               var14.add((new CommandArguments(Protocol.Command.CLIENT)).add(Protocol.Keyword.SETINFO).add(ClientAttributeOption.LIB_NAME.getRaw()).add(var6));
            }

            String var17 = JedisMetaInfo.getVersion();
            if (var17 != null && validateClientInfo(var17)) {
               var14.add((new CommandArguments(Protocol.Command.CLIENT)).add(Protocol.Keyword.SETINFO).add(ClientAttributeOption.LIB_VER.getRaw()).add(var17));
            }
         }

         for(CommandArguments var18 : var14) {
            this.sendCommand(var18);
         }

         this.getMany(var14.size());
         int var16 = var1.getDatabase();
         if (var16 > 0) {
            this.select(var16);
         }

      } catch (JedisException var13) {
         try {
            this.disconnect();
         } catch (Exception var11) {
         }

         throw var13;
      }
   }

   private void helloOrAuth(RedisProtocol var1, RedisCredentials var2) {
      if (var2 != null && var2.getPassword() != null) {
         ByteBuffer var3 = Protocol.CHARSET.encode(CharBuffer.wrap(var2.getPassword()));
         byte[] var4 = Arrays.copyOfRange(var3.array(), var3.position(), var3.limit());
         Arrays.fill(var3.array(), (byte)0);

         try {
            if (var1 != null) {
               if (var2.getUser() != null) {
                  this.sendCommand(Protocol.Command.HELLO, (byte[][])(SafeEncoder.encode(var1.version()), Protocol.Keyword.AUTH.getRaw(), SafeEncoder.encode(var2.getUser()), var4));
                  this.getOne();
               } else {
                  this.sendCommand(Protocol.Command.AUTH, (byte[][])(var4));
                  this.getStatusCodeReply();
                  this.sendCommand(Protocol.Command.HELLO, (byte[][])(SafeEncoder.encode(var1.version())));
                  this.getOne();
               }
            } else {
               if (var2.getUser() != null) {
                  this.sendCommand(Protocol.Command.AUTH, (byte[][])(SafeEncoder.encode(var2.getUser()), var4));
               } else {
                  this.sendCommand(Protocol.Command.AUTH, (byte[][])(var4));
               }

               this.getStatusCodeReply();
            }
         } finally {
            Arrays.fill(var4, (byte)0);
         }

      } else {
         if (var1 != null) {
            this.sendCommand(Protocol.Command.HELLO, (byte[][])(SafeEncoder.encode(var1.version())));
            this.getOne();
         }

      }
   }

   public String select(int var1) {
      this.sendCommand(Protocol.Command.SELECT, (byte[][])(Protocol.toByteArray(var1)));
      return this.getStatusCodeReply();
   }

   public boolean ping() {
      this.sendCommand((ProtocolCommand)Protocol.Command.PING);
      String var1 = this.getStatusCodeReply();
      if (!"PONG".equals(var1)) {
         throw new JedisException(var1);
      } else {
         return true;
      }
   }
}
