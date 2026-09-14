package redis.clients.jedis.json;

import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public class JsonProtocol {
   public static enum JsonCommand implements ProtocolCommand {
      DEL("JSON.DEL"),
      GET("JSON.GET"),
      MGET("JSON.MGET"),
      MERGE("JSON.MERGE"),
      SET("JSON.SET"),
      TYPE("JSON.TYPE"),
      STRAPPEND("JSON.STRAPPEND"),
      STRLEN("JSON.STRLEN"),
      NUMINCRBY("JSON.NUMINCRBY"),
      ARRAPPEND("JSON.ARRAPPEND"),
      ARRINDEX("JSON.ARRINDEX"),
      ARRINSERT("JSON.ARRINSERT"),
      ARRLEN("JSON.ARRLEN"),
      ARRPOP("JSON.ARRPOP"),
      ARRTRIM("JSON.ARRTRIM"),
      CLEAR("JSON.CLEAR"),
      TOGGLE("JSON.TOGGLE"),
      OBJKEYS("JSON.OBJKEYS"),
      OBJLEN("JSON.OBJLEN"),
      DEBUG("JSON.DEBUG"),
      RESP("JSON.RESP");

      private final byte[] raw;

      private JsonCommand(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
