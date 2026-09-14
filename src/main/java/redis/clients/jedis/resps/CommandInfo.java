package redis.clients.jedis.resps;

import java.util.List;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;

public class CommandInfo {
   private final long arity;
   private final List<String> flags;
   private final long firstKey;
   private final long lastKey;
   private final long step;
   private final List<String> aclCategories;
   private final List<String> tips;
   private final List<String> subcommands;
   public static final Builder<CommandInfo> COMMAND_INFO_BUILDER = new Builder<CommandInfo>() {
      public CommandInfo build(Object var1) {
         List var2 = (List)var1;
         long var3 = (Long)BuilderFactory.LONG.build(var2.get(1));
         List var5 = BuilderFactory.STRING_LIST.build(var2.get(2));
         long var6 = (Long)BuilderFactory.LONG.build(var2.get(3));
         long var8 = (Long)BuilderFactory.LONG.build(var2.get(4));
         long var10 = (Long)BuilderFactory.LONG.build(var2.get(5));
         List var12 = BuilderFactory.STRING_LIST.build(var2.get(6));
         List var13 = BuilderFactory.STRING_LIST.build(var2.get(7));
         List var14 = BuilderFactory.STRING_LIST.build(var2.get(9));
         return new CommandInfo(var3, var5, var6, var8, var10, var12, var13, var14);
      }
   };

   public CommandInfo(long var1, List<String> var3, long var4, long var6, long var8, List<String> var10, List<String> var11, List<String> var12) {
      this.arity = var1;
      this.flags = var3;
      this.firstKey = var4;
      this.lastKey = var6;
      this.step = var8;
      this.aclCategories = var10;
      this.tips = var11;
      this.subcommands = var12;
   }

   public long getArity() {
      return this.arity;
   }

   public List<String> getFlags() {
      return this.flags;
   }

   public long getFirstKey() {
      return this.firstKey;
   }

   public long getLastKey() {
      return this.lastKey;
   }

   public long getStep() {
      return this.step;
   }

   public List<String> getAclCategories() {
      return this.aclCategories;
   }

   public List<String> getTips() {
      return this.tips;
   }

   public List<String> getSubcommands() {
      return this.subcommands;
   }
}
