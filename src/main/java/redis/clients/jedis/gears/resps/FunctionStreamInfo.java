package redis.clients.jedis.gears.resps;

import java.util.List;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;

public class FunctionStreamInfo {
   private final String name;
   private final String idToReadFrom;
   private final String lastError;
   private final long lastLag;
   private final long lastProcessedTime;
   private final long totalLag;
   private final long totalProcessedTime;
   private final long totalRecordProcessed;
   private final List<String> pendingIds;
   public static final Builder<List<FunctionStreamInfo>> STREAM_INFO_LIST = new Builder<List<FunctionStreamInfo>>() {
      public List<FunctionStreamInfo> build(Object var1) {
         return (List)((List)var1).stream().map((var0) -> (List)var0).map((var0) -> new FunctionStreamInfo(BuilderFactory.STRING.build(var0.get(9)), BuilderFactory.STRING.build(var0.get(1)), BuilderFactory.STRING.build(var0.get(3)), (Long)BuilderFactory.LONG.build(var0.get(7)), (Long)BuilderFactory.LONG.build(var0.get(5)), (Long)BuilderFactory.LONG.build(var0.get(13)), (Long)BuilderFactory.LONG.build(var0.get(15)), (Long)BuilderFactory.LONG.build(var0.get(17)), BuilderFactory.STRING_LIST.build(var0.get(11)))).collect(Collectors.toList());
      }
   };

   public String getName() {
      return this.name;
   }

   public String getIdToReadFrom() {
      return this.idToReadFrom;
   }

   public String getLastError() {
      return this.lastError;
   }

   public long getLastLag() {
      return this.lastLag;
   }

   public long getLastProcessedTime() {
      return this.lastProcessedTime;
   }

   public long getTotalLag() {
      return this.totalLag;
   }

   public long getTotalProcessedTime() {
      return this.totalProcessedTime;
   }

   public long getTotalRecordProcessed() {
      return this.totalRecordProcessed;
   }

   public List<String> getPendingIds() {
      return this.pendingIds;
   }

   public FunctionStreamInfo(String var1, String var2, String var3, long var4, long var6, long var8, long var10, long var12, List<String> var14) {
      this.name = var1;
      this.idToReadFrom = var2;
      this.lastError = var3;
      this.lastProcessedTime = var4;
      this.lastLag = var6;
      this.totalLag = var8;
      this.totalProcessedTime = var10;
      this.totalRecordProcessed = var12;
      this.pendingIds = var14;
   }
}
