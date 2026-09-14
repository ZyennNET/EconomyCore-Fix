package redis.clients.jedis.params;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

public class ScanParams implements IParams {
   private final Map<Protocol.Keyword, ByteBuffer> params = new EnumMap(Protocol.Keyword.class);
   public static final String SCAN_POINTER_START = String.valueOf(0);
   public static final byte[] SCAN_POINTER_START_BINARY;

   public ScanParams match(byte[] var1) {
      this.params.put(Protocol.Keyword.MATCH, ByteBuffer.wrap(var1));
      return this;
   }

   public ScanParams match(String var1) {
      this.params.put(Protocol.Keyword.MATCH, ByteBuffer.wrap(SafeEncoder.encode(var1)));
      return this;
   }

   public ScanParams count(Integer var1) {
      this.params.put(Protocol.Keyword.COUNT, ByteBuffer.wrap(Protocol.toByteArray(var1)));
      return this;
   }

   public void addParams(CommandArguments var1) {
      for(Map.Entry var3 : this.params.entrySet()) {
         var1.add(var3.getKey());
         var1.add(((ByteBuffer)var3.getValue()).array());
      }

   }

   public byte[] binaryMatch() {
      return this.params.containsKey(Protocol.Keyword.MATCH) ? ((ByteBuffer)this.params.get(Protocol.Keyword.MATCH)).array() : null;
   }

   public String match() {
      return this.params.containsKey(Protocol.Keyword.MATCH) ? new String(((ByteBuffer)this.params.get(Protocol.Keyword.MATCH)).array()) : null;
   }

   static {
      SCAN_POINTER_START_BINARY = SafeEncoder.encode(SCAN_POINTER_START);
   }
}
