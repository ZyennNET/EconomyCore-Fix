package redis.clients.jedis;

import java.io.Closeable;

public abstract class AbstractPipeline extends PipeliningBase implements Closeable {
   protected AbstractPipeline(CommandObjects var1) {
      super(var1);
   }

   public abstract void close();

   public abstract void sync();

   public Response<Long> publish(String var1, String var2) {
      return this.appendCommand(this.commandObjects.publish(var1, var2));
   }

   public Response<Long> publish(byte[] var1, byte[] var2) {
      return this.appendCommand(this.commandObjects.publish(var1, var2));
   }
}
