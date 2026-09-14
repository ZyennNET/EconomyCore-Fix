package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class CommandListFilterByParams implements IParams {
   private String moduleName;
   private String category;
   private String pattern;

   public static CommandListFilterByParams commandListFilterByParams() {
      return new CommandListFilterByParams();
   }

   public CommandListFilterByParams filterByModule(String var1) {
      this.moduleName = var1;
      return this;
   }

   public CommandListFilterByParams filterByAclCat(String var1) {
      this.category = var1;
      return this;
   }

   public CommandListFilterByParams filterByPattern(String var1) {
      this.pattern = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      var1.add(Protocol.Keyword.FILTERBY);
      if (this.moduleName != null && this.category == null && this.pattern == null) {
         var1.add(Protocol.Keyword.MODULE);
         var1.add(this.moduleName);
      } else if (this.moduleName == null && this.category != null && this.pattern == null) {
         var1.add(Protocol.Keyword.ACLCAT);
         var1.add(this.category);
      } else {
         if (this.moduleName != null || this.category != null || this.pattern == null) {
            throw new IllegalArgumentException("Must choose exactly one filter in " + this.getClass().getSimpleName());
         }

         var1.add(Protocol.Keyword.PATTERN);
         var1.add(this.pattern);
      }

   }
}
