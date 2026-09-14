package redis.clients.jedis.resps;

import java.io.Serializable;
import java.util.Map;
import redis.clients.jedis.StreamEntryID;

public class StreamInfo implements Serializable {
   public static final String LENGTH = "length";
   public static final String RADIX_TREE_KEYS = "radix-tree-keys";
   public static final String RADIX_TREE_NODES = "radix-tree-nodes";
   public static final String GROUPS = "groups";
   public static final String LAST_GENERATED_ID = "last-generated-id";
   public static final String FIRST_ENTRY = "first-entry";
   public static final String LAST_ENTRY = "last-entry";
   private final long length;
   private final long radixTreeKeys;
   private final long radixTreeNodes;
   private final long groups;
   private final StreamEntryID lastGeneratedId;
   private final StreamEntry firstEntry;
   private final StreamEntry lastEntry;
   private final Map<String, Object> streamInfo;

   public StreamInfo(Map<String, Object> var1) {
      this.streamInfo = var1;
      this.length = (Long)var1.get("length");
      this.radixTreeKeys = (Long)var1.get("radix-tree-keys");
      this.radixTreeNodes = (Long)var1.get("radix-tree-nodes");
      this.groups = (Long)var1.get("groups");
      this.lastGeneratedId = (StreamEntryID)var1.get("last-generated-id");
      this.firstEntry = (StreamEntry)var1.get("first-entry");
      this.lastEntry = (StreamEntry)var1.get("last-entry");
   }

   public long getLength() {
      return this.length;
   }

   public long getRadixTreeKeys() {
      return this.radixTreeKeys;
   }

   public long getRadixTreeNodes() {
      return this.radixTreeNodes;
   }

   public long getGroups() {
      return this.groups;
   }

   public StreamEntryID getLastGeneratedId() {
      return this.lastGeneratedId;
   }

   public StreamEntry getFirstEntry() {
      return this.firstEntry;
   }

   public StreamEntry getLastEntry() {
      return this.lastEntry;
   }

   public Map<String, Object> getStreamInfo() {
      return this.streamInfo;
   }
}
