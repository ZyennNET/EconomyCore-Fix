package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.args.BitCountOption;

public class BitPosParams implements IParams {
   private Long start;
   private Long end;
   private BitCountOption modifier;

   public BitPosParams() {
   }

   public BitPosParams(long var1) {
      this.start = var1;
   }

   public BitPosParams(long var1, long var3) {
      this(var1);
      this.end = var3;
   }

   public static BitPosParams bitPosParams() {
      return new BitPosParams();
   }

   public BitPosParams start(long var1) {
      this.start = var1;
      return this;
   }

   public BitPosParams end(long var1) {
      this.end = var1;
      return this;
   }

   public BitPosParams modifier(BitCountOption var1) {
      this.modifier = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.start != null) {
         var1.add(this.start);
         if (this.end != null) {
            var1.add(this.end);
            if (this.modifier != null) {
               var1.add(this.modifier);
            }
         }
      }

   }
}
