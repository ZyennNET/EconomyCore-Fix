package redis.clients.jedis.resps;

import java.io.Serializable;
import java.util.Map;
import redis.clients.jedis.StreamEntryID;

public class StreamPendingSummary implements Serializable {
   private static final long serialVersionUID = 1L;
   private final long total;
   private final StreamEntryID minId;
   private final StreamEntryID maxId;
   private final Map<String, Long> consumerMessageCount;

   public StreamPendingSummary(long var1, StreamEntryID var3, StreamEntryID var4, Map<String, Long> var5) {
      this.total = var1;
      this.minId = var3;
      this.maxId = var4;
      this.consumerMessageCount = var5;
   }

   public long getTotal() {
      return this.total;
   }

   public StreamEntryID getMinId() {
      return this.minId;
   }

   public StreamEntryID getMaxId() {
      return this.maxId;
   }

   public Map<String, Long> getConsumerMessageCount() {
      return this.consumerMessageCount;
   }
}
