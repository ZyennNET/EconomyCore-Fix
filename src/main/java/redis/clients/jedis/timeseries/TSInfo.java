package redis.clients.jedis.timeseries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.DoublePrecision;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.SafeEncoder;

public class TSInfo {
   private static final String DUPLICATE_POLICY_PROPERTY = "duplicatePolicy";
   private static final String LABELS_PROPERTY = "labels";
   private static final String RULES_PROPERTY = "rules";
   private static final String CHUNKS_PROPERTY = "Chunks";
   private static final String CHUNKS_BYTES_PER_SAMPLE_PROPERTY = "bytesPerSample";
   private final Map<String, Object> properties;
   private final Map<String, String> labels;
   private final Map<String, Rule> rules;
   private final List<Map<String, Object>> chunks;
   public static Builder<TSInfo> TIMESERIES_INFO = new Builder<TSInfo>() {
      public TSInfo build(Object var1) {
         List var2 = (List)var1;
         HashMap var3 = new HashMap();
         Map var4 = null;
         HashMap var5 = null;
         ArrayList var6 = null;

         for(int var7 = 0; var7 < var2.size(); var7 += 2) {
            String var8 = SafeEncoder.encode((byte[])var2.get(var7));
            Object var9 = var2.get(var7 + 1);
            if (!(var9 instanceof List)) {
               if (var9 instanceof byte[]) {
                  var9 = SafeEncoder.encode((byte[])var9);
                  if ("duplicatePolicy".equals(var8)) {
                     try {
                        var9 = DuplicatePolicy.valueOf(((String)var9).toUpperCase());
                     } catch (Exception var19) {
                     }
                  }
               }
            } else {
               switch (var8) {
                  case "labels":
                     var4 = BuilderFactory.STRING_MAP_FROM_PAIRS.build(var9);
                     var9 = var4;
                     break;
                  case "rules":
                     List var12 = (List)var9;
                     ArrayList var13 = new ArrayList(var12.size());
                     var5 = new HashMap(var12.size());

                     for(Object var21 : var12) {
                        List var22 = (List)SafeEncoder.encodeObject(var21);
                        var13.add(var22);
                        var5.put((String)var22.get(0), new Rule((String)var22.get(0), (Long)var22.get(1), AggregationType.safeValueOf((String)var22.get(2)), (Long)var22.get(3)));
                     }

                     var9 = var13;
                     break;
                  case "Chunks":
                     List var14 = (List)var9;
                     ArrayList var15 = new ArrayList(var14.size());
                     var6 = new ArrayList(var14.size());

                     for(Object var17 : var14) {
                        Map var18 = BuilderFactory.ENCODED_OBJECT_MAP.build(var17);
                        var15.add(new HashMap(var18));
                        if (var18.containsKey("bytesPerSample")) {
                           var18.put("bytesPerSample", DoublePrecision.parseEncodedFloatingPointNumber(var18.get("bytesPerSample")));
                        }

                        var6.add(var18);
                     }

                     var9 = var15;
                     break;
                  default:
                     var9 = SafeEncoder.encodeObject(var9);
               }
            }

            var3.put(var8, var9);
         }

         return new TSInfo(var3, var4, var5, var6);
      }
   };
   public static Builder<TSInfo> TIMESERIES_INFO_RESP3 = new Builder<TSInfo>() {
      public TSInfo build(Object var1) {
         List var2 = (List)var1;
         HashMap var3 = new HashMap();
         Map var4 = null;
         HashMap var5 = null;
         ArrayList var6 = null;

         for(KeyValue var8 : var2) {
            String var9 = BuilderFactory.STRING.build(var8.getKey());
            Object var10 = var8.getValue();
            if (!(var10 instanceof List)) {
               if (var10 instanceof byte[]) {
                  var10 = BuilderFactory.STRING.build(var10);
                  if ("duplicatePolicy".equals(var9)) {
                     try {
                        var10 = DuplicatePolicy.valueOf(((String)var10).toUpperCase());
                     } catch (Exception var20) {
                     }
                  }
               }
            } else {
               switch (var9) {
                  case "labels":
                     var4 = BuilderFactory.STRING_MAP.build(var10);
                     var10 = var4;
                     break;
                  case "rules":
                     List var13 = (List)var10;
                     HashMap var14 = new HashMap(var13.size(), 1.0F);
                     var5 = new HashMap(var13.size());

                     for(KeyValue var22 : var13) {
                        String var23 = BuilderFactory.STRING.build(var22.getKey());
                        List var24 = BuilderFactory.ENCODED_OBJECT_LIST.build(var22.getValue());
                        var14.put(var23, var24);
                        var5.put(var23, new Rule(var23, var24));
                     }

                     var10 = var14;
                     break;
                  case "Chunks":
                     List var15 = (List)var10;
                     ArrayList var16 = new ArrayList(var15.size());
                     var6 = new ArrayList(var15.size());

                     for(List var18 : var15) {
                        Map var19 = (Map)var18.stream().collect(Collectors.toMap((var0) -> BuilderFactory.STRING.build(var0.getKey()), (var0) -> BuilderFactory.ENCODED_OBJECT.build(var0.getValue())));
                        var16.add(var19);
                        var6.add(var19);
                     }

                     var10 = var16;
                     break;
                  default:
                     var10 = SafeEncoder.encodeObject(var10);
               }
            }

            var3.put(var9, var10);
         }

         return new TSInfo(var3, var4, var5, var6);
      }
   };

   private TSInfo(Map<String, Object> var1, Map<String, String> var2, Map<String, Rule> var3, List<Map<String, Object>> var4) {
      this.properties = var1;
      this.labels = var2;
      this.rules = var3;
      this.chunks = var4;
   }

   public Map<String, Object> getProperties() {
      return this.properties;
   }

   public Object getProperty(String var1) {
      return this.properties.get(var1);
   }

   public Long getIntegerProperty(String var1) {
      return (Long)this.properties.get(var1);
   }

   public Map<String, String> getLabels() {
      return this.labels;
   }

   public String getLabel(String var1) {
      return (String)this.labels.get(var1);
   }

   public Map<String, Rule> getRules() {
      return this.rules;
   }

   public Rule getRule(String var1) {
      return (Rule)this.rules.get(var1);
   }

   public List<Map<String, Object>> getChunks() {
      return this.chunks;
   }

   public static class Rule {
      private final String compactionKey;
      private final long bucketDuration;
      private final AggregationType aggregator;
      private final long alignmentTimestamp;

      private Rule(String var1, List<Object> var2) {
         this(var1, (Long)var2.get(0), AggregationType.safeValueOf((String)var2.get(1)), (Long)var2.get(2));
      }

      private Rule(String var1, long var2, AggregationType var4, long var5) {
         this.compactionKey = var1;
         this.bucketDuration = var2;
         this.aggregator = var4;
         this.alignmentTimestamp = var5;
      }

      public String getCompactionKey() {
         return this.compactionKey;
      }

      public long getBucketDuration() {
         return this.bucketDuration;
      }

      public AggregationType getAggregator() {
         return this.aggregator;
      }

      public long getAlignmentTimestamp() {
         return this.alignmentTimestamp;
      }
   }
}
