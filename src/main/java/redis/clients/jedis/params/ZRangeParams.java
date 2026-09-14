package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;

public class ZRangeParams implements IParams {
   private final Protocol.Keyword by;
   private final Rawable min;
   private final Rawable max;
   private boolean rev;
   private boolean limit;
   private int offset;
   private int count;

   private ZRangeParams() {
      this.rev = false;
      this.limit = false;
      throw new InstantiationError("Empty constructor must not be called.");
   }

   public ZRangeParams(int var1, int var2) {
      this.rev = false;
      this.limit = false;
      this.by = null;
      this.min = RawableFactory.from(var1);
      this.max = RawableFactory.from(var2);
   }

   public static ZRangeParams zrangeParams(int var0, int var1) {
      return new ZRangeParams(var0, var1);
   }

   public ZRangeParams(double var1, double var3) {
      this.rev = false;
      this.limit = false;
      this.by = Protocol.Keyword.BYSCORE;
      this.min = RawableFactory.from(var1);
      this.max = RawableFactory.from(var3);
   }

   public static ZRangeParams zrangeByScoreParams(double var0, double var2) {
      return new ZRangeParams(var0, var2);
   }

   private ZRangeParams(Protocol.Keyword var1, Rawable var2, Rawable var3) {
      this.rev = false;
      this.limit = false;
      if (var1 != null && var1 != Protocol.Keyword.BYSCORE && var1 != Protocol.Keyword.BYLEX) {
         throw new IllegalArgumentException(var1.name() + " is not a valid ZRANGE type argument.");
      } else {
         this.by = var1;
         this.min = var2;
         this.max = var3;
      }
   }

   public ZRangeParams(Protocol.Keyword var1, String var2, String var3) {
      this(var1, RawableFactory.from(var2), RawableFactory.from(var3));
   }

   public ZRangeParams(Protocol.Keyword var1, byte[] var2, byte[] var3) {
      this(var1, RawableFactory.from(var2), RawableFactory.from(var3));
   }

   public static ZRangeParams zrangeByLexParams(String var0, String var1) {
      return new ZRangeParams(Protocol.Keyword.BYLEX, var0, var1);
   }

   public static ZRangeParams zrangeByLexParams(byte[] var0, byte[] var1) {
      return new ZRangeParams(Protocol.Keyword.BYLEX, var0, var1);
   }

   public ZRangeParams rev() {
      this.rev = true;
      return this;
   }

   public ZRangeParams limit(int var1, int var2) {
      this.limit = true;
      this.offset = var1;
      this.count = var2;
      return this;
   }

   public void addParams(CommandArguments var1) {
      var1.add(this.min).add(this.max);
      if (this.by != null) {
         var1.add(this.by);
      }

      if (this.rev) {
         var1.add(Protocol.Keyword.REV);
      }

      if (this.limit) {
         var1.add(Protocol.Keyword.LIMIT).add(this.offset).add(this.count);
      }

   }
}
