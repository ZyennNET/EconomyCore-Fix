package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class LCSParams implements IParams {
   private boolean len = false;
   private boolean idx = false;
   private Long minMatchLen;
   private boolean withMatchLen = false;

   public static LCSParams LCSParams() {
      return new LCSParams();
   }

   public LCSParams len() {
      this.len = true;
      return this;
   }

   public LCSParams idx() {
      this.idx = true;
      return this;
   }

   public LCSParams minMatchLen(long var1) {
      this.minMatchLen = var1;
      return this;
   }

   public LCSParams withMatchLen() {
      this.withMatchLen = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.len) {
         var1.add(Protocol.Keyword.LEN);
      }

      if (this.idx) {
         var1.add(Protocol.Keyword.IDX);
      }

      if (this.minMatchLen != null) {
         var1.add(Protocol.Keyword.MINMATCHLEN).add(this.minMatchLen);
      }

      if (this.withMatchLen) {
         var1.add(Protocol.Keyword.WITHMATCHLEN);
      }

   }
}
