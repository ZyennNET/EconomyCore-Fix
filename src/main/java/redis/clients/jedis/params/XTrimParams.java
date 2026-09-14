package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class XTrimParams implements IParams {
   private Long maxLen;
   private boolean approximateTrimming;
   private boolean exactTrimming;
   private String minId;
   private Long limit;

   public static XTrimParams xTrimParams() {
      return new XTrimParams();
   }

   public XTrimParams maxLen(long var1) {
      this.maxLen = var1;
      return this;
   }

   public XTrimParams minId(String var1) {
      this.minId = var1;
      return this;
   }

   public XTrimParams approximateTrimming() {
      this.approximateTrimming = true;
      return this;
   }

   public XTrimParams exactTrimming() {
      this.exactTrimming = true;
      return this;
   }

   public XTrimParams limit(long var1) {
      this.limit = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.maxLen != null) {
         var1.add(Protocol.Keyword.MAXLEN);
         if (this.approximateTrimming) {
            var1.add(Protocol.BYTES_TILDE);
         } else if (this.exactTrimming) {
            var1.add(Protocol.BYTES_EQUAL);
         }

         var1.add(Protocol.toByteArray(this.maxLen));
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

   }
}
