package redis.clients.jedis.gears;

import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public class RedisGearsProtocol {
   public static enum GearsCommand implements ProtocolCommand {
      TFUNCTION,
      TFCALL,
      TFCALLASYNC;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum GearsKeyword implements Rawable {
      CONFIG,
      REPLACE,
      LOAD,
      DELETE,
      LIST,
      WITHCODE,
      LIBRARY,
      VERBOSE;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
