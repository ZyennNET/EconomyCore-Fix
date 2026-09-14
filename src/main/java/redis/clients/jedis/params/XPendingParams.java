package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;

public class XPendingParams implements IParams {
   private Long idle;
   private Rawable start;
   private Rawable end;
   private Integer count;
   private Rawable consumer;

   public XPendingParams(StreamEntryID var1, StreamEntryID var2, int var3) {
      this(var1.toString(), var2.toString(), var3);
   }

   public XPendingParams(String var1, String var2, int var3) {
      this(RawableFactory.from(var1), RawableFactory.from(var2), var3);
   }

   public XPendingParams(byte[] var1, byte[] var2, int var3) {
      this(RawableFactory.from(var1), RawableFactory.from(var2), var3);
   }

   private XPendingParams(Rawable var1, Rawable var2, Integer var3) {
      this.start = var1;
      this.end = var2;
      this.count = var3;
   }

   public XPendingParams() {
      this.start = null;
      this.end = null;
      this.count = null;
   }

   public static XPendingParams xPendingParams(StreamEntryID var0, StreamEntryID var1, int var2) {
      return new XPendingParams(var0, var1, var2);
   }

   public static XPendingParams xPendingParams(String var0, String var1, int var2) {
      return new XPendingParams(var0, var1, var2);
   }

   public static XPendingParams xPendingParams(byte[] var0, byte[] var1, int var2) {
      return new XPendingParams(var0, var1, var2);
   }

   public static XPendingParams xPendingParams() {
      return new XPendingParams();
   }

   public XPendingParams idle(long var1) {
      this.idle = var1;
      return this;
   }

   public XPendingParams start(StreamEntryID var1) {
      this.start = RawableFactory.from(var1.toString());
      return this;
   }

   public XPendingParams end(StreamEntryID var1) {
      this.end = RawableFactory.from(var1.toString());
      return this;
   }

   public XPendingParams count(int var1) {
      this.count = var1;
      return this;
   }

   public XPendingParams consumer(String var1) {
      this.consumer = RawableFactory.from(var1);
      return this;
   }

   public XPendingParams consumer(byte[] var1) {
      this.consumer = RawableFactory.from(var1);
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.count == null) {
         throw new IllegalArgumentException("start, end and count must be set.");
      } else {
         if (this.start == null) {
            this.start = RawableFactory.from("-");
         }

         if (this.end == null) {
            this.end = RawableFactory.from("+");
         }

         if (this.idle != null) {
            var1.add(Protocol.Keyword.IDLE).add(this.idle);
         }

         var1.add(this.start).add(this.end).add(this.count);
         if (this.consumer != null) {
            var1.add(this.consumer);
         }

      }
   }
}
