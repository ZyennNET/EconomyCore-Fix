package redis.clients.jedis.resps;

import java.util.List;
import java.util.Map;

public class ClusterShardInfo {
   public static final String SLOTS = "slots";
   public static final String NODES = "nodes";
   private final List<List<Long>> slots;
   private final List<ClusterShardNodeInfo> nodes;
   private final Map<String, Object> clusterShardInfo;

   public ClusterShardInfo(Map<String, Object> var1) {
      this.slots = (List)var1.get("slots");
      this.nodes = (List)var1.get("nodes");
      this.clusterShardInfo = var1;
   }

   public List<List<Long>> getSlots() {
      return this.slots;
   }

   public List<ClusterShardNodeInfo> getNodes() {
      return this.nodes;
   }

   public Map<String, Object> getClusterShardInfo() {
      return this.clusterShardInfo;
   }
}
