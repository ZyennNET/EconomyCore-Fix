package redis.clients.jedis;

public class CommandObject<T> {
   private final CommandArguments arguments;
   private final Builder<T> builder;

   public CommandObject(CommandArguments var1, Builder<T> var2) {
      this.arguments = var1;
      this.builder = var2;
   }

   public CommandArguments getArguments() {
      return this.arguments;
   }

   public Builder<T> getBuilder() {
      return this.builder;
   }
}
