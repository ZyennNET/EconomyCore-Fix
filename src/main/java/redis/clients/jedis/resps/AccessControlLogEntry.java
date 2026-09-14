package redis.clients.jedis.resps;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class AccessControlLogEntry implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String COUNT = "count";
   public static final String REASON = "reason";
   public static final String CONTEXT = "context";
   public static final String OBJECT = "object";
   public static final String USERNAME = "username";
   public static final String AGE_SECONDS = "age-seconds";
   public static final String CLIENT_INFO = "client-info";
   public static final String ENTRY_ID = "entry-id";
   public static final String TIMESTAMP_CREATED = "timestamp-created";
   public static final String TIMESTAMP_LAST_UPDATED = "timestamp-last-updated";
   private final long count;
   private final String reason;
   private final String context;
   private final String object;
   private final String username;
   private final Double ageSeconds;
   private final Map<String, String> clientInfo;
   private final Map<String, Object> logEntry;
   private final long entryId;
   private final long timestampCreated;
   private final long timestampLastUpdated;

   public AccessControlLogEntry(Map<String, Object> var1) {
      this.count = (Long)var1.get("count");
      this.reason = (String)var1.get("reason");
      this.context = (String)var1.get("context");
      this.object = (String)var1.get("object");
      this.username = (String)var1.get("username");
      this.ageSeconds = (Double)var1.get("age-seconds");
      this.clientInfo = this.getMapFromRawClientInfo((String)var1.get("client-info"));
      this.logEntry = var1;
      this.entryId = (Long)var1.get("entry-id");
      this.timestampCreated = (Long)var1.get("timestamp-created");
      this.timestampLastUpdated = (Long)var1.get("timestamp-last-updated");
   }

   public long getCount() {
      return this.count;
   }

   public String getReason() {
      return this.reason;
   }

   public String getContext() {
      return this.context;
   }

   public String getObject() {
      return this.object;
   }

   public String getUsername() {
      return this.username;
   }

   public Double getAgeSeconds() {
      return this.ageSeconds;
   }

   public Map<String, String> getClientInfo() {
      return this.clientInfo;
   }

   public Map<String, Object> getlogEntry() {
      return this.logEntry;
   }

   public long getEntryId() {
      return this.entryId;
   }

   public long getTimestampCreated() {
      return this.timestampCreated;
   }

   public long getTimestampLastUpdated() {
      return this.timestampLastUpdated;
   }

   private Map<String, String> getMapFromRawClientInfo(String var1) {
      String[] var2 = var1.split(" ");
      LinkedHashMap var3 = new LinkedHashMap(var2.length);

      for(String var7 : var2) {
         String[] var8 = var7.split("=");
         var3.put(var8[0], var8.length == 2 ? var8[1] : "");
      }

      return var3;
   }

   public String toString() {
      return "AccessControlLogEntry{count=" + this.count + ", reason='" + this.reason + '\'' + ", context='" + this.context + '\'' + ", object='" + this.object + '\'' + ", username='" + this.username + '\'' + ", ageSeconds='" + this.ageSeconds + '\'' + ", clientInfo=" + this.clientInfo + ", entryId=" + this.entryId + ", timestampCreated=" + this.timestampCreated + ", timestampLastUpdated=" + this.timestampLastUpdated + '}';
   }
}
