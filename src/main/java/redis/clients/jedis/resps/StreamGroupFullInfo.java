package redis.clients.jedis.resps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.StreamEntryID;

public class StreamGroupFullInfo implements Serializable {
   public static final String NAME = "name";
   public static final String CONSUMERS = "consumers";
   public static final String PENDING = "pending";
   public static final String LAST_DELIVERED = "last-delivered-id";
   public static final String PEL_COUNT = "pel-count";
   private final String name;
   private final List<StreamConsumerFullInfo> consumers;
   private final List<List<Object>> pending;
   private final Long pelCount;
   private final StreamEntryID lastDeliveredId;
   private final Map<String, Object> groupFullInfo;

   public StreamGroupFullInfo(Map<String, Object> var1) {
      this.groupFullInfo = var1;
      this.name = (String)var1.get("name");
      this.consumers = (List)var1.get("consumers");
      this.pending = (List)var1.get("pending");
      this.lastDeliveredId = (StreamEntryID)var1.get("last-delivered-id");
      this.pelCount = (Long)var1.get("pel-count");
      this.pending.stream().forEach((var0) -> var0.set(0, new StreamEntryID((String)var0.get(0))));
   }

   public String getName() {
      return this.name;
   }

   public List<StreamConsumerFullInfo> getConsumers() {
      return this.consumers;
   }

   public List<List<Object>> getPending() {
      return this.pending;
   }

   public StreamEntryID getLastDeliveredId() {
      return this.lastDeliveredId;
   }

   public Map<String, Object> getGroupFullInfo() {
      return this.groupFullInfo;
   }

   public Long getPelCount() {
      return this.pelCount;
   }
}
