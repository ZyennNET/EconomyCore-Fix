package redis.clients.jedis.resps;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.StreamEntryID;

public class StreamConsumerFullInfo implements Serializable {
   public static final String NAME = "name";
   public static final String SEEN_TIME = "seen-time";
   public static final String ACTIVE_TIME = "active-time";
   public static final String PEL_COUNT = "pel-count";
   public static final String PENDING = "pending";
   private final String name;
   private final Long seenTime;
   private final Long activeTime;
   private final Long pelCount;
   private final List<List<Object>> pending;
   private final Map<String, Object> consumerInfo;

   public StreamConsumerFullInfo(Map<String, Object> var1) {
      this.consumerInfo = var1;
      this.name = (String)var1.get("name");
      this.seenTime = (Long)var1.get("seen-time");
      this.activeTime = (Long)var1.get("active-time");
      this.pending = (List)var1.get("pending");
      this.pelCount = (Long)var1.get("pel-count");
      this.pending.forEach((var0) -> var0.set(0, new StreamEntryID((String)var0.get(0))));
   }

   public String getName() {
      return this.name;
   }

   public Long getSeenTime() {
      return this.seenTime;
   }

   public Long getActiveTime() {
      return this.activeTime;
   }

   public Long getPelCount() {
      return this.pelCount;
   }

   public List<List<Object>> getPending() {
      return this.pending;
   }

   public Map<String, Object> getConsumerInfo() {
      return this.consumerInfo;
   }
}
