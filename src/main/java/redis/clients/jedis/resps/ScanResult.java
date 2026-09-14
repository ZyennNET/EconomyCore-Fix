package redis.clients.jedis.resps;

import java.util.List;
import redis.clients.jedis.params.ScanParams;
import redis.clients.jedis.util.SafeEncoder;

public class ScanResult<T> {
   private byte[] cursor;
   private List<T> results;

   public ScanResult(String var1, List<T> var2) {
      this(SafeEncoder.encode(var1), var2);
   }

   public ScanResult(byte[] var1, List<T> var2) {
      this.cursor = var1;
      this.results = var2;
   }

   public String getCursor() {
      return SafeEncoder.encode(this.cursor);
   }

   public boolean isCompleteIteration() {
      return ScanParams.SCAN_POINTER_START.equals(this.getCursor());
   }

   public byte[] getCursorAsBytes() {
      return this.cursor;
   }

   public List<T> getResult() {
      return this.results;
   }
}
