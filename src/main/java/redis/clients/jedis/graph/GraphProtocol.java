package redis.clients.jedis.graph;

import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

@Deprecated
public class GraphProtocol {
   @Deprecated
   public static enum GraphCommand implements ProtocolCommand {
      QUERY,
      RO_QUERY,
      DELETE,
      LIST,
      PROFILE,
      EXPLAIN,
      SLOWLOG,
      CONFIG;

      private final byte[] raw = SafeEncoder.encode("GRAPH." + this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   @Deprecated
   public static enum GraphKeyword implements Rawable {
      CYPHER,
      TIMEOUT,
      SET,
      GET,
      __COMPACT("--COMPACT");

      private final byte[] raw;

      private GraphKeyword() {
         this.raw = SafeEncoder.encode(this.name());
      }

      private GraphKeyword(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
