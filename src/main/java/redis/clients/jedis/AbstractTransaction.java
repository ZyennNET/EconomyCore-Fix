package redis.clients.jedis;

import java.io.Closeable;
import java.util.List;

public abstract class AbstractTransaction extends PipeliningBase implements Closeable {
   protected AbstractTransaction() {
      super(new CommandObjects());
   }

   public abstract void multi();

   public abstract String watch(String... var1);

   public abstract String watch(byte[]... var1);

   public abstract String unwatch();

   public abstract void close();

   public abstract List<Object> exec();

   public abstract String discard();

   public Response<Long> waitReplicas(int var1, long var2) {
      return this.appendCommand(this.commandObjects.waitReplicas(var1, var2));
   }
}
