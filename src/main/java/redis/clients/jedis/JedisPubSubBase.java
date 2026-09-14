package redis.clients.jedis;

import java.util.Arrays;
import java.util.List;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.util.SafeEncoder;

public abstract class JedisPubSubBase<T> {
   private int subscribedChannels = 0;
   private volatile Connection client;

   public void onMessage(T var1, T var2) {
   }

   public void onPMessage(T var1, T var2, T var3) {
   }

   public void onSubscribe(T var1, int var2) {
   }

   public void onUnsubscribe(T var1, int var2) {
   }

   public void onPUnsubscribe(T var1, int var2) {
   }

   public void onPSubscribe(T var1, int var2) {
   }

   public void onPong(T var1) {
   }

   private void sendAndFlushCommand(Protocol.Command var1, T... var2) {
      if (this.client == null) {
         throw new JedisException(this.getClass() + " is not connected to a Connection.");
      } else {
         CommandArguments var3 = (new CommandArguments(var1)).addObjects(var2);
         this.client.sendCommand(var3);
         this.client.flush();
      }
   }

   public final void unsubscribe() {
      this.sendAndFlushCommand(Protocol.Command.UNSUBSCRIBE);
   }

   public final void unsubscribe(T... var1) {
      this.sendAndFlushCommand(Protocol.Command.UNSUBSCRIBE, var1);
   }

   public final void subscribe(T... var1) {
      this.sendAndFlushCommand(Protocol.Command.SUBSCRIBE, var1);
   }

   public final void psubscribe(T... var1) {
      this.sendAndFlushCommand(Protocol.Command.PSUBSCRIBE, var1);
   }

   public final void punsubscribe() {
      this.sendAndFlushCommand(Protocol.Command.PUNSUBSCRIBE);
   }

   public final void punsubscribe(T... var1) {
      this.sendAndFlushCommand(Protocol.Command.PUNSUBSCRIBE, var1);
   }

   public final void ping() {
      this.sendAndFlushCommand(Protocol.Command.PING);
   }

   public final void ping(T var1) {
      this.sendAndFlushCommand(Protocol.Command.PING, var1);
   }

   public final boolean isSubscribed() {
      return this.subscribedChannels > 0;
   }

   public final int getSubscribedChannels() {
      return this.subscribedChannels;
   }

   public final void proceed(Connection var1, T... var2) {
      this.client = var1;
      this.client.setTimeoutInfinite();

      try {
         this.subscribe(var2);
         this.process();
      } finally {
         this.client.rollbackTimeout();
      }

   }

   public final void proceedWithPatterns(Connection var1, T... var2) {
      this.client = var1;
      this.client.setTimeoutInfinite();

      try {
         this.psubscribe(var2);
         this.process();
      } finally {
         this.client.rollbackTimeout();
      }

   }

   protected abstract T encode(byte[] var1);

   private void process() {
      do {
         Object var1 = this.client.getUnflushedObject();
         if (var1 instanceof List) {
            List var2 = (List)var1;
            Object var3 = var2.get(0);
            if (!(var3 instanceof byte[])) {
               throw new JedisException("Unknown message type: " + var3);
            }

            byte[] var4 = (byte[])var3;
            if (Arrays.equals(Protocol.ResponseKeyword.SUBSCRIBE.getRaw(), var4)) {
               this.subscribedChannels = ((Long)var2.get(2)).intValue();
               byte[] var5 = (byte[])var2.get(1);
               Object var6 = var5 == null ? null : this.encode(var5);
               this.onSubscribe(var6, this.subscribedChannels);
            } else if (Arrays.equals(Protocol.ResponseKeyword.UNSUBSCRIBE.getRaw(), var4)) {
               this.subscribedChannels = ((Long)var2.get(2)).intValue();
               byte[] var12 = (byte[])var2.get(1);
               Object var18 = var12 == null ? null : this.encode(var12);
               this.onUnsubscribe(var18, this.subscribedChannels);
            } else if (Arrays.equals(Protocol.ResponseKeyword.MESSAGE.getRaw(), var4)) {
               byte[] var13 = (byte[])var2.get(1);
               byte[] var19 = (byte[])var2.get(2);
               Object var7 = var13 == null ? null : this.encode(var13);
               Object var8 = var19 == null ? null : this.encode(var19);
               this.onMessage(var7, var8);
            } else if (Arrays.equals(Protocol.ResponseKeyword.PMESSAGE.getRaw(), var4)) {
               byte[] var14 = (byte[])var2.get(1);
               byte[] var20 = (byte[])var2.get(2);
               byte[] var24 = (byte[])var2.get(3);
               Object var25 = var14 == null ? null : this.encode(var14);
               Object var9 = var20 == null ? null : this.encode(var20);
               Object var10 = var24 == null ? null : this.encode(var24);
               this.onPMessage(var25, var9, var10);
            } else if (Arrays.equals(Protocol.ResponseKeyword.PSUBSCRIBE.getRaw(), var4)) {
               this.subscribedChannels = ((Long)var2.get(2)).intValue();
               byte[] var15 = (byte[])var2.get(1);
               Object var21 = var15 == null ? null : this.encode(var15);
               this.onPSubscribe(var21, this.subscribedChannels);
            } else if (Arrays.equals(Protocol.ResponseKeyword.PUNSUBSCRIBE.getRaw(), var4)) {
               this.subscribedChannels = ((Long)var2.get(2)).intValue();
               byte[] var16 = (byte[])var2.get(1);
               Object var22 = var16 == null ? null : this.encode(var16);
               this.onPUnsubscribe(var22, this.subscribedChannels);
            } else {
               if (!Arrays.equals(Protocol.ResponseKeyword.PONG.getRaw(), var4)) {
                  throw new JedisException("Unknown message type: " + var3);
               }

               byte[] var17 = (byte[])var2.get(1);
               Object var23 = var17 == null ? null : this.encode(var17);
               this.onPong(var23);
            }
         } else {
            if (!(var1 instanceof byte[])) {
               throw new JedisException("Unknown message type: " + var1);
            }

            byte[] var11 = (byte[])var1;
            if ("PONG".equals(SafeEncoder.encode(var11))) {
               this.onPong((Object)null);
            } else {
               this.onPong(this.encode(var11));
            }
         }
      } while(this.isSubscribed());

   }
}
