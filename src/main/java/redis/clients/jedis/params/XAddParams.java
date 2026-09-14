package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;

public class XAddParams implements IParams {
   private Rawable id;
   private Long maxLen;
   private boolean approximateTrimming;
   private boolean exactTrimming;
   private boolean nomkstream;
   private String minId;
   private Long limit;

   public static XAddParams xAddParams() {
      return new XAddParams();
   }

   public XAddParams noMkStream() {
      this.nomkstream = true;
      return this;
   }

   public XAddParams id(byte[] var1) {
      this.id = RawableFactory.from(var1);
      return this;
   }

   public XAddParams id(String var1) {
      this.id = RawableFactory.from(var1);
      return this;
   }

   public XAddParams id(StreamEntryID var1) {
      return this.id(var1.toString());
   }

   public XAddParams id(long var1, long var3) {
      return this.id(var1 + "-" + var3);
   }

   public XAddParams id(long var1) {
      return this.id(var1 + "-*");
   }

   public XAddParams maxLen(long var1) {
      this.maxLen = var1;
      return this;
   }

   public XAddParams minId(String var1) {
      this.minId = var1;
      return this;
   }

   public XAddParams approximateTrimming() {
      this.approximateTrimming = true;
      return this;
   }

   public XAddParams exactTrimming() {
      this.exactTrimming = true;
      return this;
   }

   public XAddParams limit(long var1) {
      this.limit = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.nomkstream) {
         var1.add(Protocol.Keyword.NOMKSTREAM);
      }

      if (this.maxLen != null) {
         var1.add(Protocol.Keyword.MAXLEN);
         if (this.approximateTrimming) {
            var1.add(Protocol.BYTES_TILDE);
         } else if (this.exactTrimming) {
            var1.add(Protocol.BYTES_EQUAL);
         }

         var1.add(this.maxLen);
      } else if (this.minId != null) {
         var1.add(Protocol.Keyword.MINID);
         if (this.approximateTrimming) {
            var1.add(Protocol.BYTES_TILDE);
         } else if (this.exactTrimming) {
            var1.add(Protocol.BYTES_EQUAL);
         }

         var1.add(this.minId);
      }

      if (this.limit != null) {
         var1.add(Protocol.Keyword.LIMIT).add(this.limit);
      }

      var1.add(this.id != null ? this.id : StreamEntryID.NEW_ENTRY);
   }
}
