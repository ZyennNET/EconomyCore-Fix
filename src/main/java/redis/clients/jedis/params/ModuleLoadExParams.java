package redis.clients.jedis.params;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.KeyValue;

public class ModuleLoadExParams implements IParams {
   private final List<KeyValue<String, String>> configs = new ArrayList();
   private final List<String> args = new ArrayList();

   public ModuleLoadExParams moduleLoadexParams() {
      return new ModuleLoadExParams();
   }

   public ModuleLoadExParams config(String var1, String var2) {
      this.configs.add(KeyValue.of(var1, var2));
      return this;
   }

   public ModuleLoadExParams arg(String var1) {
      this.args.add(var1);
      return this;
   }

   public void addParams(CommandArguments var1) {
      this.configs.forEach((var1x) -> var1.add(Protocol.Keyword.CONFIG).add(var1x.getKey()).add(var1x.getValue()));
      if (!this.args.isEmpty()) {
         var1.add(Protocol.Keyword.ARGS).addObjects((Collection)this.args);
      }

   }
}
