package redis.clients.jedis;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.resps.AccessControlLogEntry;
import redis.clients.jedis.resps.AccessControlUser;
import redis.clients.jedis.resps.ClusterShardInfo;
import redis.clients.jedis.resps.ClusterShardNodeInfo;
import redis.clients.jedis.resps.CommandDocument;
import redis.clients.jedis.resps.CommandInfo;
import redis.clients.jedis.resps.GeoRadiusResponse;
import redis.clients.jedis.resps.LCSMatchResult;
import redis.clients.jedis.resps.LibraryInfo;
import redis.clients.jedis.resps.ScanResult;
import redis.clients.jedis.resps.StreamConsumerFullInfo;
import redis.clients.jedis.resps.StreamConsumerInfo;
import redis.clients.jedis.resps.StreamConsumersInfo;
import redis.clients.jedis.resps.StreamEntry;
import redis.clients.jedis.resps.StreamFullInfo;
import redis.clients.jedis.resps.StreamGroupFullInfo;
import redis.clients.jedis.resps.StreamGroupInfo;
import redis.clients.jedis.resps.StreamInfo;
import redis.clients.jedis.resps.StreamPendingEntry;
import redis.clients.jedis.resps.StreamPendingSummary;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.util.DoublePrecision;
import redis.clients.jedis.util.JedisByteHashMap;
import redis.clients.jedis.util.KeyValue;
import redis.clients.jedis.util.SafeEncoder;

public final class BuilderFactory {
   public static final Builder<Object> RAW_OBJECT = new Builder<Object>() {
      public Object build(Object var1) {
         return var1;
      }

      public String toString() {
         return "Object";
      }
   };
   public static final Builder<List<Object>> RAW_OBJECT_LIST = new Builder<List<Object>>() {
      public List<Object> build(Object var1) {
         return (List)var1;
      }

      public String toString() {
         return "List<Object>";
      }
   };
   public static final Builder<Object> ENCODED_OBJECT = new Builder<Object>() {
      public Object build(Object var1) {
         return SafeEncoder.encodeObject(var1);
      }

      public String toString() {
         return "Object";
      }
   };
   public static final Builder<List<Object>> ENCODED_OBJECT_LIST = new Builder<List<Object>>() {
      public List<Object> build(Object var1) {
         return (List)SafeEncoder.encodeObject(var1);
      }

      public String toString() {
         return "List<Object>";
      }
   };
   public static final Builder<Long> LONG = new Builder<Long>() {
      public Long build(Object var1) {
         return (Long)var1;
      }

      public String toString() {
         return "Long";
      }
   };
   public static final Builder<List<Long>> LONG_LIST = new Builder<List<Long>>() {
      public List<Long> build(Object var1) {
         return null == var1 ? null : (List)var1;
      }

      public String toString() {
         return "List<Long>";
      }
   };
   public static final Builder<Double> DOUBLE = new Builder<Double>() {
      public Double build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            return var1 instanceof Double ? (Double)var1 : DoublePrecision.parseFloatingPointNumber(BuilderFactory.STRING.build(var1));
         }
      }

      public String toString() {
         return "Double";
      }
   };
   public static final Builder<List<Double>> DOUBLE_LIST = new Builder<List<Double>>() {
      public List<Double> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.DOUBLE;
            var10001.getClass();
            return (List)var10000.map(var10001::build).collect(Collectors.toList());
         }
      }

      public String toString() {
         return "List<Double>";
      }
   };
   public static final Builder<Boolean> BOOLEAN = new Builder<Boolean>() {
      public Boolean build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            return var1 instanceof Boolean ? (Boolean)var1 : (Long)var1 == 1L;
         }
      }

      public String toString() {
         return "Boolean";
      }
   };
   public static final Builder<List<Boolean>> BOOLEAN_LIST = new Builder<List<Boolean>>() {
      public List<Boolean> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.BOOLEAN;
            var10001.getClass();
            return (List)var10000.map(var10001::build).collect(Collectors.toList());
         }
      }

      public String toString() {
         return "List<Boolean>";
      }
   };
   public static final Builder<List<Boolean>> BOOLEAN_WITH_ERROR_LIST = new Builder<List<Boolean>>() {
      public List<Boolean> build(Object var1) {
         return null == var1 ? null : (List)((List)var1).stream().map((var0) -> var0 instanceof JedisDataException ? null : (Boolean)BuilderFactory.BOOLEAN.build(var0)).collect(Collectors.toList());
      }

      public String toString() {
         return "List<Boolean>";
      }
   };
   public static final Builder<byte[]> BINARY = new Builder<byte[]>() {
      public byte[] build(Object var1) {
         return (byte[])var1;
      }

      public String toString() {
         return "byte[]";
      }
   };
   public static final Builder<List<byte[]>> BINARY_LIST = new Builder<List<byte[]>>() {
      public List<byte[]> build(Object var1) {
         return (List)var1;
      }

      public String toString() {
         return "List<byte[]>";
      }
   };
   public static final Builder<Set<byte[]>> BINARY_SET = new Builder<Set<byte[]>>() {
      public Set<byte[]> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = BuilderFactory.BINARY_LIST.build(var1);
            return BuilderFactory.SetFromList.<byte[]>of(var2);
         }
      }

      public String toString() {
         return "Set<byte[]>";
      }
   };
   public static final Builder<List<Map.Entry<byte[], byte[]>>> BINARY_PAIR_LIST = new Builder<List<Map.Entry<byte[], byte[]>>>() {
      public List<Map.Entry<byte[], byte[]>> build(Object var1) {
         List var2 = (List)var1;
         ArrayList var3 = new ArrayList();
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            var3.add(new AbstractMap.SimpleEntry(var4.next(), var4.next()));
         }

         return var3;
      }

      public String toString() {
         return "List<Map.Entry<byte[], byte[]>>";
      }
   };
   public static final Builder<List<Map.Entry<byte[], byte[]>>> BINARY_PAIR_LIST_FROM_PAIRS = new Builder<List<Map.Entry<byte[], byte[]>>>() {
      public List<Map.Entry<byte[], byte[]>> build(Object var1) {
         List var2 = (List)var1;
         ArrayList var3 = new ArrayList();

         for(Object var5 : var2) {
            List var6 = (List)var5;
            var3.add(new AbstractMap.SimpleEntry(var6.get(0), var6.get(1)));
         }

         return var3;
      }

      public String toString() {
         return "List<Map.Entry<byte[], byte[]>>";
      }
   };
   public static final Builder<String> STRING = new Builder<String>() {
      public String build(Object var1) {
         return var1 == null ? null : SafeEncoder.encode((byte[])var1);
      }

      public String toString() {
         return "String";
      }
   };
   public static final Builder<List<String>> STRING_LIST = new Builder<List<String>>() {
      public List<String> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.STRING;
            var10001.getClass();
            return (List)var10000.map(var10001::build).collect(Collectors.toList());
         }
      }

      public String toString() {
         return "List<String>";
      }
   };
   public static final Builder<Set<String>> STRING_SET = new Builder<Set<String>>() {
      public Set<String> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.STRING;
            var10001.getClass();
            return (Set)var10000.map(var10001::build).collect(Collectors.toSet());
         }
      }

      public String toString() {
         return "Set<String>";
      }
   };
   public static final Builder<Map<byte[], byte[]>> BINARY_MAP = new Builder<Map<byte[], byte[]>>() {
      public Map<byte[], byte[]> build(Object var1) {
         List var2 = (List)var1;
         if (var2.isEmpty()) {
            return Collections.emptyMap();
         } else if (var2.get(0) instanceof KeyValue) {
            JedisByteHashMap var6 = new JedisByteHashMap();

            for(KeyValue var5 : var2) {
               var6.put(BuilderFactory.BINARY.build(var5.getKey()), BuilderFactory.BINARY.build(var5.getValue()));
            }

            return var6;
         } else {
            JedisByteHashMap var3 = new JedisByteHashMap();
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               var3.put(BuilderFactory.BINARY.build(var4.next()), BuilderFactory.BINARY.build(var4.next()));
            }

            return var3;
         }
      }

      public String toString() {
         return "Map<byte[], byte[]>";
      }
   };
   public static final Builder<Map<String, String>> STRING_MAP = new Builder<Map<String, String>>() {
      public Map<String, String> build(Object var1) {
         List var2 = (List)var1;
         if (var2.isEmpty()) {
            return Collections.emptyMap();
         } else if (var2.get(0) instanceof KeyValue) {
            HashMap var6 = new HashMap(var2.size(), 1.0F);

            for(KeyValue var5 : var2) {
               var6.put(BuilderFactory.STRING.build(var5.getKey()), BuilderFactory.STRING.build(var5.getValue()));
            }

            return var6;
         } else {
            HashMap var3 = new HashMap(var2.size() / 2, 1.0F);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               var3.put(BuilderFactory.STRING.build(var4.next()), BuilderFactory.STRING.build(var4.next()));
            }

            return var3;
         }
      }

      public String toString() {
         return "Map<String, String>";
      }
   };
   public static final Builder<Map<String, Object>> ENCODED_OBJECT_MAP = new Builder<Map<String, Object>>() {
      public Map<String, Object> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return Collections.emptyMap();
            } else if (var2.get(0) instanceof KeyValue) {
               HashMap var6 = new HashMap(var2.size(), 1.0F);

               for(KeyValue var5 : var2) {
                  var6.put(BuilderFactory.STRING.build(var5.getKey()), BuilderFactory.ENCODED_OBJECT.build(var5.getValue()));
               }

               return var6;
            } else {
               HashMap var3 = new HashMap(var2.size() / 2, 1.0F);
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  var3.put(BuilderFactory.STRING.build(var4.next()), BuilderFactory.ENCODED_OBJECT.build(var4.next()));
               }

               return var3;
            }
         }
      }
   };
   public static final Builder<Object> AGGRESSIVE_ENCODED_OBJECT = new Builder<Object>() {
      public Object build(Object var1) {
         if (var1 == null) {
            return null;
         } else if (var1 instanceof List) {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return Collections.emptyMap();
            } else {
               return var2.get(0) instanceof KeyValue ? ((List)var1).stream().filter((var0) -> var0 != null && var0.getKey() != null && var0.getValue() != null).collect(Collectors.toMap((var0) -> BuilderFactory.STRING.build(var0.getKey()), (var1x) -> this.build(var1x.getValue()))) : var2.stream().map(this::build).collect(Collectors.toList());
            }
         } else {
            return var1 instanceof byte[] ? BuilderFactory.STRING.build(var1) : var1;
         }
      }
   };
   public static final Builder<Map<String, Object>> AGGRESSIVE_ENCODED_OBJECT_MAP = new Builder<Map<String, Object>>() {
      public Map<String, Object> build(Object var1) {
         return (Map)BuilderFactory.AGGRESSIVE_ENCODED_OBJECT.build(var1);
      }
   };
   public static final Builder<List<Map.Entry<String, String>>> STRING_PAIR_LIST = new Builder<List<Map.Entry<String, String>>>() {
      public List<Map.Entry<String, String>> build(Object var1) {
         List var2 = (List)var1;
         ArrayList var3 = new ArrayList(var2.size() / 2);
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            var3.add(KeyValue.of(BuilderFactory.STRING.build(var4.next()), BuilderFactory.STRING.build(var4.next())));
         }

         return var3;
      }

      public String toString() {
         return "List<Map.Entry<String, String>>";
      }
   };
   public static final Builder<List<Map.Entry<String, String>>> STRING_PAIR_LIST_FROM_PAIRS = new Builder<List<Map.Entry<String, String>>>() {
      public List<Map.Entry<String, String>> build(Object var1) {
         return (List)((List)var1).stream().map((var0) -> (List)var0).map((var0) -> KeyValue.of(BuilderFactory.STRING.build(var0.get(0)), BuilderFactory.STRING.build(var0.get(1)))).collect(Collectors.toList());
      }

      public String toString() {
         return "List<Map.Entry<String, String>>";
      }
   };
   public static final Builder<Map<String, Long>> STRING_LONG_MAP = new Builder<Map<String, Long>>() {
      public Map<String, Long> build(Object var1) {
         List var2 = (List)var1;
         if (var2.isEmpty()) {
            return Collections.emptyMap();
         } else if (var2.get(0) instanceof KeyValue) {
            LinkedHashMap var6 = new LinkedHashMap(var2.size(), 1.0F);

            for(KeyValue var5 : var2) {
               var6.put(BuilderFactory.STRING.build(var5.getKey()), BuilderFactory.LONG.build(var5.getValue()));
            }

            return var6;
         } else {
            LinkedHashMap var3 = new LinkedHashMap(var2.size() / 2, 1.0F);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               var3.put(BuilderFactory.STRING.build(var4.next()), BuilderFactory.LONG.build(var4.next()));
            }

            return var3;
         }
      }

      public String toString() {
         return "Map<String, Long>";
      }
   };
   public static final Builder<KeyValue<String, String>> KEYED_ELEMENT = new Builder<KeyValue<String, String>>() {
      public KeyValue<String, String> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return KeyValue.<String, String>of(BuilderFactory.STRING.build(var2.get(0)), BuilderFactory.STRING.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<String, String>";
      }
   };
   public static final Builder<KeyValue<byte[], byte[]>> BINARY_KEYED_ELEMENT = new Builder<KeyValue<byte[], byte[]>>() {
      public KeyValue<byte[], byte[]> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return KeyValue.<byte[], byte[]>of(BuilderFactory.BINARY.build(var2.get(0)), BuilderFactory.BINARY.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<byte[], byte[]>";
      }
   };
   public static final Builder<KeyValue<Long, Double>> ZRANK_WITHSCORE_PAIR = new Builder<KeyValue<Long, Double>>() {
      public KeyValue<Long, Double> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return new KeyValue<Long, Double>(BuilderFactory.LONG.build(var2.get(0)), BuilderFactory.DOUBLE.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<Long, Double>";
      }
   };
   public static final Builder<KeyValue<String, List<String>>> KEYED_STRING_LIST = new Builder<KeyValue<String, List<String>>>() {
      public KeyValue<String, List<String>> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return new KeyValue<String, List<String>>(BuilderFactory.STRING.build(var2.get(0)), BuilderFactory.STRING_LIST.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<String, List<String>>";
      }
   };
   public static final Builder<KeyValue<Long, Long>> LONG_LONG_PAIR = new Builder<KeyValue<Long, Long>>() {
      public KeyValue<Long, Long> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return new KeyValue<Long, Long>(BuilderFactory.LONG.build(var2.get(0)), BuilderFactory.LONG.build(var2.get(1)));
         }
      }
   };
   public static final Builder<List<KeyValue<String, List<String>>>> KEYED_STRING_LIST_LIST = new Builder<List<KeyValue<String, List<String>>>>() {
      public List<KeyValue<String, List<String>>> build(Object var1) {
         List var2 = (List)var1;
         Stream var10000 = var2.stream();
         Builder var10001 = BuilderFactory.KEYED_STRING_LIST;
         var10001.getClass();
         return (List)var10000.map(var10001::build).collect(Collectors.toList());
      }
   };
   public static final Builder<KeyValue<byte[], List<byte[]>>> KEYED_BINARY_LIST = new Builder<KeyValue<byte[], List<byte[]>>>() {
      public KeyValue<byte[], List<byte[]>> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return new KeyValue<byte[], List<byte[]>>(BuilderFactory.BINARY.build(var2.get(0)), BuilderFactory.BINARY_LIST.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<byte[], List<byte[]>>";
      }
   };
   public static final Builder<Tuple> TUPLE = new Builder<Tuple>() {
      public Tuple build(Object var1) {
         List var2 = (List)var1;
         return var2.isEmpty() ? null : new Tuple((byte[])var2.get(0), BuilderFactory.DOUBLE.build(var2.get(1)));
      }

      public String toString() {
         return "Tuple";
      }
   };
   public static final Builder<KeyValue<String, Tuple>> KEYED_TUPLE = new Builder<KeyValue<String, Tuple>>() {
      public KeyValue<String, Tuple> build(Object var1) {
         List var2 = (List)var1;
         return var2.isEmpty() ? null : KeyValue.of(BuilderFactory.STRING.build(var2.get(0)), new Tuple(BuilderFactory.BINARY.build(var2.get(1)), BuilderFactory.DOUBLE.build(var2.get(2))));
      }

      public String toString() {
         return "KeyValue<String, Tuple>";
      }
   };
   public static final Builder<KeyValue<byte[], Tuple>> BINARY_KEYED_TUPLE = new Builder<KeyValue<byte[], Tuple>>() {
      public KeyValue<byte[], Tuple> build(Object var1) {
         List var2 = (List)var1;
         return var2.isEmpty() ? null : KeyValue.of(BuilderFactory.BINARY.build(var2.get(0)), new Tuple(BuilderFactory.BINARY.build(var2.get(1)), BuilderFactory.DOUBLE.build(var2.get(2))));
      }

      public String toString() {
         return "KeyValue<byte[], Tuple>";
      }
   };
   public static final Builder<List<Tuple>> TUPLE_LIST = new Builder<List<Tuple>>() {
      public List<Tuple> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            ArrayList var3 = new ArrayList(var2.size() / 2);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               var3.add(new Tuple((byte[])var4.next(), BuilderFactory.DOUBLE.build(var4.next())));
            }

            return var3;
         }
      }

      public String toString() {
         return "List<Tuple>";
      }
   };
   public static final Builder<List<Tuple>> TUPLE_LIST_RESP3 = new Builder<List<Tuple>>() {
      public List<Tuple> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.TUPLE;
            var10001.getClass();
            return (List)var10000.map(var10001::build).collect(Collectors.toList());
         }
      }

      public String toString() {
         return "List<Tuple>";
      }
   };
   public static final Builder<Set<Tuple>> TUPLE_ZSET = new Builder<Set<Tuple>>() {
      public Set<Tuple> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            LinkedHashSet var3 = new LinkedHashSet(var2.size() / 2, 1.0F);
            Iterator var4 = var2.iterator();

            while(var4.hasNext()) {
               var3.add(new Tuple((byte[])var4.next(), BuilderFactory.DOUBLE.build(var4.next())));
            }

            return var3;
         }
      }

      public String toString() {
         return "ZSet<Tuple>";
      }
   };
   public static final Builder<Set<Tuple>> TUPLE_ZSET_RESP3 = new Builder<Set<Tuple>>() {
      public Set<Tuple> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.TUPLE;
            var10001.getClass();
            return (Set)var10000.map(var10001::build).collect(Collectors.toCollection(LinkedHashSet::new));
         }
      }

      public String toString() {
         return "ZSet<Tuple>";
      }
   };
   private static final Builder<List<Tuple>> TUPLE_LIST_FROM_PAIRS = new Builder<List<Tuple>>() {
      public List<Tuple> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            Stream var10000 = ((List)var1).stream();
            Builder var10001 = BuilderFactory.TUPLE;
            var10001.getClass();
            return (List)var10000.map(var10001::build).collect(Collectors.toList());
         }
      }

      public String toString() {
         return "List<Tuple>";
      }
   };
   public static final Builder<KeyValue<String, List<Tuple>>> KEYED_TUPLE_LIST = new Builder<KeyValue<String, List<Tuple>>>() {
      public KeyValue<String, List<Tuple>> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return new KeyValue<String, List<Tuple>>(BuilderFactory.STRING.build(var2.get(0)), BuilderFactory.TUPLE_LIST_FROM_PAIRS.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<String, List<Tuple>>";
      }
   };
   public static final Builder<KeyValue<byte[], List<Tuple>>> BINARY_KEYED_TUPLE_LIST = new Builder<KeyValue<byte[], List<Tuple>>>() {
      public KeyValue<byte[], List<Tuple>> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            return new KeyValue<byte[], List<Tuple>>(BuilderFactory.BINARY.build(var2.get(0)), BuilderFactory.TUPLE_LIST_FROM_PAIRS.build(var2.get(1)));
         }
      }

      public String toString() {
         return "KeyValue<byte[], List<Tuple>>";
      }
   };
   public static final Builder<ScanResult<String>> SCAN_RESPONSE = new Builder<ScanResult<String>>() {
      public ScanResult<String> build(Object var1) {
         List var2 = (List)var1;
         String var3 = new String((byte[])var2.get(0));
         List var4 = (List)var2.get(1);
         ArrayList var5 = new ArrayList(var4.size());

         for(byte[] var7 : var4) {
            var5.add(SafeEncoder.encode(var7));
         }

         return new ScanResult<String>(var3, var5);
      }
   };
   public static final Builder<ScanResult<Map.Entry<String, String>>> HSCAN_RESPONSE = new Builder<ScanResult<Map.Entry<String, String>>>() {
      public ScanResult<Map.Entry<String, String>> build(Object var1) {
         List var2 = (List)var1;
         String var3 = new String((byte[])var2.get(0));
         List var4 = (List)var2.get(1);
         ArrayList var5 = new ArrayList(var4.size() / 2);
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            var5.add(new AbstractMap.SimpleEntry(SafeEncoder.encode((byte[])var6.next()), SafeEncoder.encode((byte[])var6.next())));
         }

         return new ScanResult<Map.Entry<String, String>>(var3, var5);
      }
   };
   public static final Builder<ScanResult<String>> SSCAN_RESPONSE = new Builder<ScanResult<String>>() {
      public ScanResult<String> build(Object var1) {
         List var2 = (List)var1;
         String var3 = new String((byte[])var2.get(0));
         List var4 = (List)var2.get(1);
         ArrayList var5 = new ArrayList(var4.size());

         for(byte[] var7 : var4) {
            var5.add(SafeEncoder.encode(var7));
         }

         return new ScanResult<String>(var3, var5);
      }
   };
   public static final Builder<ScanResult<Tuple>> ZSCAN_RESPONSE = new Builder<ScanResult<Tuple>>() {
      public ScanResult<Tuple> build(Object var1) {
         List var2 = (List)var1;
         String var3 = new String((byte[])var2.get(0));
         List var4 = (List)var2.get(1);
         ArrayList var5 = new ArrayList(var4.size() / 2);
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            var5.add(new Tuple((byte[])var6.next(), BuilderFactory.DOUBLE.build(var6.next())));
         }

         return new ScanResult<Tuple>(var3, var5);
      }
   };
   public static final Builder<ScanResult<byte[]>> SCAN_BINARY_RESPONSE = new Builder<ScanResult<byte[]>>() {
      public ScanResult<byte[]> build(Object var1) {
         List var2 = (List)var1;
         byte[] var3 = (byte[])var2.get(0);
         List var4 = (List)var2.get(1);
         return new ScanResult<byte[]>(var3, var4);
      }
   };
   public static final Builder<ScanResult<Map.Entry<byte[], byte[]>>> HSCAN_BINARY_RESPONSE = new Builder<ScanResult<Map.Entry<byte[], byte[]>>>() {
      public ScanResult<Map.Entry<byte[], byte[]>> build(Object var1) {
         List var2 = (List)var1;
         byte[] var3 = (byte[])var2.get(0);
         List var4 = (List)var2.get(1);
         ArrayList var5 = new ArrayList(var4.size() / 2);
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            var5.add(new AbstractMap.SimpleEntry(var6.next(), var6.next()));
         }

         return new ScanResult<Map.Entry<byte[], byte[]>>(var3, var5);
      }
   };
   public static final Builder<ScanResult<byte[]>> SSCAN_BINARY_RESPONSE = new Builder<ScanResult<byte[]>>() {
      public ScanResult<byte[]> build(Object var1) {
         List var2 = (List)var1;
         byte[] var3 = (byte[])var2.get(0);
         List var4 = (List)var2.get(1);
         return new ScanResult<byte[]>(var3, var4);
      }
   };
   public static final Builder<Map<String, Long>> PUBSUB_NUMSUB_MAP = new Builder<Map<String, Long>>() {
      public Map<String, Long> build(Object var1) {
         List var2 = (List)var1;
         HashMap var3 = new HashMap(var2.size() / 2, 1.0F);
         Iterator var4 = var2.iterator();

         while(var4.hasNext()) {
            var3.put(SafeEncoder.encode((byte[])var4.next()), (Long)var4.next());
         }

         return var3;
      }

      public String toString() {
         return "PUBSUB_NUMSUB_MAP<String, String>";
      }
   };
   public static final Builder<List<GeoCoordinate>> GEO_COORDINATE_LIST = new Builder<List<GeoCoordinate>>() {
      public List<GeoCoordinate> build(Object var1) {
         return null == var1 ? null : this.interpretGeoposResult((List)var1);
      }

      public String toString() {
         return "List<GeoCoordinate>";
      }

      private List<GeoCoordinate> interpretGeoposResult(List<Object> var1) {
         ArrayList var2 = new ArrayList(var1.size());

         for(Object var4 : var1) {
            if (var4 == null) {
               var2.add((Object)null);
            } else {
               List var5 = (List)var4;
               GeoCoordinate var6 = new GeoCoordinate((Double)BuilderFactory.DOUBLE.build(var5.get(0)), (Double)BuilderFactory.DOUBLE.build(var5.get(1)));
               var2.add(var6);
            }
         }

         return var2;
      }
   };
   public static final Builder<List<GeoRadiusResponse>> GEORADIUS_WITH_PARAMS_RESULT = new Builder<List<GeoRadiusResponse>>() {
      public List<GeoRadiusResponse> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            ArrayList var3 = new ArrayList(var2.size());
            if (var2.isEmpty()) {
               return var3;
            } else {
               if (var2.get(0) instanceof List) {
                  for(Object var6 : var2) {
                     List var7 = (List)var6;
                     GeoRadiusResponse var4 = new GeoRadiusResponse((byte[])var7.get(0));
                     int var8 = var7.size();

                     for(int var9 = 1; var9 < var8; ++var9) {
                        Object var10 = var7.get(var9);
                        if (var10 instanceof List) {
                           List var11 = (List)var10;
                           var4.setCoordinate(new GeoCoordinate((Double)BuilderFactory.DOUBLE.build(var11.get(0)), (Double)BuilderFactory.DOUBLE.build(var11.get(1))));
                        } else if (var10 instanceof Long) {
                           var4.setRawScore((Long)BuilderFactory.LONG.build(var10));
                        } else {
                           var4.setDistance((Double)BuilderFactory.DOUBLE.build(var10));
                        }
                     }

                     var3.add(var4);
                  }
               } else {
                  for(Object var13 : var2) {
                     var3.add(new GeoRadiusResponse((byte[])var13));
                  }
               }

               return var3;
            }
         }
      }

      public String toString() {
         return "GeoRadiusWithParamsResult";
      }
   };
   public static final Builder<Map<String, CommandDocument>> COMMAND_DOCS_RESPONSE = new Builder<Map<String, CommandDocument>>() {
      public Map<String, CommandDocument> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return Collections.emptyMap();
            } else if (var2.get(0) instanceof KeyValue) {
               HashMap var6 = new HashMap(var2.size(), 1.0F);

               for(KeyValue var5 : var2) {
                  var6.put(BuilderFactory.STRING.build(var5.getKey()), new CommandDocument(BuilderFactory.ENCODED_OBJECT_MAP.build(var5.getValue())));
               }

               return var6;
            } else {
               HashMap var3 = new HashMap(var2.size() / 2, 1.0F);
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  var3.put(BuilderFactory.STRING.build(var4.next()), new CommandDocument(BuilderFactory.ENCODED_OBJECT_MAP.build(var4.next())));
               }

               return var3;
            }
         }
      }
   };
   public static final Builder<Map<String, CommandInfo>> COMMAND_INFO_RESPONSE = new Builder<Map<String, CommandInfo>>() {
      public Map<String, CommandInfo> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            HashMap var3 = new HashMap(var2.size());

            for(Object var5 : var2) {
               if (var5 != null) {
                  List var6 = (List)var5;
                  String var7 = BuilderFactory.STRING.build(var6.get(0));
                  CommandInfo var8 = CommandInfo.COMMAND_INFO_BUILDER.build(var6);
                  var3.put(var7, var8);
               }
            }

            return var3;
         }
      }
   };
   private static final Builder<List<List<Long>>> CLUSTER_SHARD_SLOTS_RANGES = new Builder<List<List<Long>>>() {
      public List<List<Long>> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            ArrayList var3 = new ArrayList();

            for(int var4 = 0; var4 < var2.size(); var4 += 2) {
               var3.add(Arrays.asList((Long)var2.get(var4), (Long)var2.get(var4 + 1)));
            }

            return var3;
         }
      }
   };
   private static final Builder<List<ClusterShardNodeInfo>> CLUSTER_SHARD_NODE_INFO_LIST = new Builder<List<ClusterShardNodeInfo>>() {
      final Map<String, Builder> mappingFunctions = this.createDecoderMap();

      private Map<String, Builder> createDecoderMap() {
         HashMap var1 = new HashMap();
         var1.put("id", BuilderFactory.STRING);
         var1.put("endpoint", BuilderFactory.STRING);
         var1.put("ip", BuilderFactory.STRING);
         var1.put("hostname", BuilderFactory.STRING);
         var1.put("port", BuilderFactory.LONG);
         var1.put("tls-port", BuilderFactory.LONG);
         var1.put("role", BuilderFactory.STRING);
         var1.put("replication-offset", BuilderFactory.LONG);
         var1.put("health", BuilderFactory.STRING);
         return var1;
      }

      public List<ClusterShardNodeInfo> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            ArrayList var2 = new ArrayList();

            for(Object var5 : (List)var1) {
               List var6 = (List)var5;
               Iterator var7 = var6.iterator();
               var2.add(new ClusterShardNodeInfo(BuilderFactory.createMapFromDecodingFunctions(var7, this.mappingFunctions)));
            }

            return var2;
         }
      }

      public String toString() {
         return "List<ClusterShardNodeInfo>";
      }
   };
   public static final Builder<List<ClusterShardInfo>> CLUSTER_SHARD_INFO_LIST = new Builder<List<ClusterShardInfo>>() {
      final Map<String, Builder> mappingFunctions = this.createDecoderMap();

      private Map<String, Builder> createDecoderMap() {
         HashMap var1 = new HashMap();
         var1.put("slots", BuilderFactory.CLUSTER_SHARD_SLOTS_RANGES);
         var1.put("nodes", BuilderFactory.CLUSTER_SHARD_NODE_INFO_LIST);
         return var1;
      }

      public List<ClusterShardInfo> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            ArrayList var2 = new ArrayList();

            for(Object var5 : (List)var1) {
               List var6 = (List)var5;
               Iterator var7 = var6.iterator();
               var2.add(new ClusterShardInfo(BuilderFactory.createMapFromDecodingFunctions(var7, this.mappingFunctions)));
            }

            return var2;
         }
      }

      public String toString() {
         return "List<ClusterShardInfo>";
      }
   };
   public static final Builder<List<Module>> MODULE_LIST = new Builder<List<Module>>() {
      public List<Module> build(Object var1) {
         if (var1 == null) {
            return null;
         } else {
            List var2 = (List)var1;
            ArrayList var3 = new ArrayList(var2.size());
            if (var2.isEmpty()) {
               return var3;
            } else {
               for(List var5 : var2) {
                  if (var5.get(0) instanceof KeyValue) {
                     var3.add(new Module(BuilderFactory.STRING.build(((KeyValue)var5.get(0)).getValue()), ((Long)BuilderFactory.LONG.build(((KeyValue)var5.get(1)).getValue())).intValue()));
                  } else {
                     Module var6 = new Module(SafeEncoder.encode((byte[])var5.get(1)), ((Long)var5.get(3)).intValue());
                     var3.add(var6);
                  }
               }

               return var3;
            }
         }
      }

      public String toString() {
         return "List<Module>";
      }
   };
   public static final Builder<AccessControlUser> ACCESS_CONTROL_USER = new Builder<AccessControlUser>() {
      public AccessControlUser build(Object var1) {
         Map var2 = BuilderFactory.ENCODED_OBJECT_MAP.build(var1);
         return var2 == null ? null : new AccessControlUser(var2);
      }

      public String toString() {
         return "AccessControlUser";
      }
   };
   public static final Builder<List<AccessControlLogEntry>> ACCESS_CONTROL_LOG_ENTRY_LIST = new Builder<List<AccessControlLogEntry>>() {
      private final Map<String, Builder> mappingFunctions = this.createDecoderMap();

      private Map<String, Builder> createDecoderMap() {
         HashMap var1 = new HashMap();
         var1.put("count", BuilderFactory.LONG);
         var1.put("reason", BuilderFactory.STRING);
         var1.put("context", BuilderFactory.STRING);
         var1.put("object", BuilderFactory.STRING);
         var1.put("username", BuilderFactory.STRING);
         var1.put("age-seconds", BuilderFactory.DOUBLE);
         var1.put("client-info", BuilderFactory.STRING);
         var1.put("entry-id", BuilderFactory.LONG);
         var1.put("timestamp-created", BuilderFactory.LONG);
         var1.put("timestamp-last-updated", BuilderFactory.LONG);
         return var1;
      }

      public List<AccessControlLogEntry> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            ArrayList var2 = new ArrayList();

            for(List var5 : (List)var1) {
               Iterator var6 = var5.iterator();
               AccessControlLogEntry var7 = new AccessControlLogEntry(BuilderFactory.createMapFromDecodingFunctions(var6, this.mappingFunctions, BuilderFactory.BACKUP_BUILDERS_FOR_DECODING_FUNCTIONS));
               var2.add(var7);
            }

            return var2;
         }
      }

      public String toString() {
         return "List<AccessControlLogEntry>";
      }
   };
   public static final Builder<StreamEntryID> STREAM_ENTRY_ID = new Builder<StreamEntryID>() {
      public StreamEntryID build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            String var2 = SafeEncoder.encode((byte[])var1);
            return new StreamEntryID(var2);
         }
      }

      public String toString() {
         return "StreamEntryID";
      }
   };
   public static final Builder<List<StreamEntryID>> STREAM_ENTRY_ID_LIST = new Builder<List<StreamEntryID>>() {
      public List<StreamEntryID> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            ArrayList var3 = new ArrayList(var2.size());
            if (!var2.isEmpty()) {
               for(Object var5 : var2) {
                  var3.add(BuilderFactory.STREAM_ENTRY_ID.build(var5));
               }
            }

            return var3;
         }
      }
   };
   public static final Builder<StreamEntry> STREAM_ENTRY = new Builder<StreamEntry>() {
      public StreamEntry build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return null;
            } else {
               String var3 = SafeEncoder.encode((byte[])var2.get(0));
               StreamEntryID var4 = new StreamEntryID(var3);
               List var5 = (List)var2.get(1);
               Iterator var6 = var5.iterator();
               HashMap var7 = new HashMap(var5.size() / 2, 1.0F);

               while(var6.hasNext()) {
                  var7.put(SafeEncoder.encode((byte[])var6.next()), SafeEncoder.encode((byte[])var6.next()));
               }

               return new StreamEntry(var4, var7);
            }
         }
      }

      public String toString() {
         return "StreamEntry";
      }
   };
   public static final Builder<List<StreamEntry>> STREAM_ENTRY_LIST = new Builder<List<StreamEntry>>() {
      public List<StreamEntry> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            ArrayList var3 = new ArrayList(var2.size() / 2);
            if (var2.isEmpty()) {
               return var3;
            } else {
               for(ArrayList var5 : var2) {
                  if (var5 == null) {
                     var3.add((Object)null);
                  } else {
                     String var6 = SafeEncoder.encode((byte[])var5.get(0));
                     StreamEntryID var7 = new StreamEntryID(var6);
                     List var8 = (List)var5.get(1);
                     if (var8 == null) {
                        var3.add(new StreamEntry(var7, (Map)null));
                     } else {
                        Iterator var9 = var8.iterator();
                        HashMap var10 = new HashMap(var8.size() / 2, 1.0F);

                        while(var9.hasNext()) {
                           var10.put(SafeEncoder.encode((byte[])var9.next()), SafeEncoder.encode((byte[])var9.next()));
                        }

                        var3.add(new StreamEntry(var7, var10));
                     }
                  }
               }

               return var3;
            }
         }
      }

      public String toString() {
         return "List<StreamEntry>";
      }
   };
   public static final Builder<Map.Entry<StreamEntryID, List<StreamEntry>>> STREAM_AUTO_CLAIM_RESPONSE = new Builder<Map.Entry<StreamEntryID, List<StreamEntry>>>() {
      public Map.Entry<StreamEntryID, List<StreamEntry>> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            return new AbstractMap.SimpleEntry(BuilderFactory.STREAM_ENTRY_ID.build(var2.get(0)), BuilderFactory.STREAM_ENTRY_LIST.build(var2.get(1)));
         }
      }

      public String toString() {
         return "Map.Entry<StreamEntryID, List<StreamEntry>>";
      }
   };
   public static final Builder<Map.Entry<StreamEntryID, List<StreamEntryID>>> STREAM_AUTO_CLAIM_JUSTID_RESPONSE = new Builder<Map.Entry<StreamEntryID, List<StreamEntryID>>>() {
      public Map.Entry<StreamEntryID, List<StreamEntryID>> build(Object var1) {
         if (null == var1) {
            return null;
         } else {
            List var2 = (List)var1;
            return new AbstractMap.SimpleEntry(BuilderFactory.STREAM_ENTRY_ID.build(var2.get(0)), BuilderFactory.STREAM_ENTRY_ID_LIST.build(var2.get(1)));
         }
      }

      public String toString() {
         return "Map.Entry<StreamEntryID, List<StreamEntryID>>";
      }
   };
   @Deprecated
   public static final Builder<Map.Entry<StreamEntryID, List<StreamEntryID>>> STREAM_AUTO_CLAIM_ID_RESPONSE;
   public static final Builder<List<Map.Entry<String, List<StreamEntry>>>> STREAM_READ_RESPONSE;
   public static final Builder<List<StreamPendingEntry>> STREAM_PENDING_ENTRY_LIST;
   public static final Builder<StreamInfo> STREAM_INFO;
   public static final Builder<List<StreamGroupInfo>> STREAM_GROUP_INFO_LIST;
   @Deprecated
   public static final Builder<List<StreamConsumersInfo>> STREAM_CONSUMERS_INFO_LIST;
   public static final Builder<List<StreamConsumerInfo>> STREAM_CONSUMER_INFO_LIST;
   private static final Builder<List<StreamConsumerFullInfo>> STREAM_CONSUMER_FULL_INFO_LIST;
   private static final Builder<List<StreamGroupFullInfo>> STREAM_GROUP_FULL_INFO_LIST;
   public static final Builder<StreamFullInfo> STREAM_FULL_INFO;
   @Deprecated
   public static final Builder<StreamFullInfo> STREAM_INFO_FULL;
   public static final Builder<StreamPendingSummary> STREAM_PENDING_SUMMARY;
   private static final List<Builder> BACKUP_BUILDERS_FOR_DECODING_FUNCTIONS;
   public static final Builder<LCSMatchResult> STR_ALGO_LCS_RESULT_BUILDER;
   public static final Builder<Map<String, String>> STRING_MAP_FROM_PAIRS;
   public static final Builder<Map<String, Object>> ENCODED_OBJECT_MAP_FROM_PAIRS;
   @Deprecated
   public static final Builder<List<LibraryInfo>> LIBRARY_LIST;
   public static final Builder<List<List<String>>> STRING_LIST_LIST;
   public static final Builder<List<List<Object>>> ENCODED_OBJECT_LIST_LIST;

   private static Map<String, Object> createMapFromDecodingFunctions(Iterator<Object> var0, Map<String, Builder> var1) {
      return createMapFromDecodingFunctions(var0, var1, (Collection)null);
   }

   private static Map<String, Object> createMapFromDecodingFunctions(Iterator<Object> var0, Map<String, Builder> var1, Collection<Builder> var2) {
      if (!var0.hasNext()) {
         return Collections.emptyMap();
      } else {
         HashMap var3 = new HashMap();

         while(var0.hasNext()) {
            Object var4 = var0.next();
            String var5;
            Object var6;
            if (var4 instanceof KeyValue) {
               KeyValue var7 = (KeyValue)var4;
               var5 = STRING.build(var7.getKey());
               var6 = var7.getValue();
            } else {
               var5 = STRING.build(var4);
               var6 = var0.next();
            }

            if (var1.containsKey(var5)) {
               var3.put(var5, ((Builder)var1.get(var5)).build(var6));
            } else {
               for(Builder var9 : var2 != null ? var2 : var1.values()) {
                  try {
                     var3.put(var5, var9.build(var6));
                     break;
                  } catch (ClassCastException var11) {
                  }
               }
            }
         }

         return var3;
      }
   }

   private BuilderFactory() {
      throw new InstantiationError("Must not instantiate this class");
   }

   static {
      STREAM_AUTO_CLAIM_ID_RESPONSE = STREAM_AUTO_CLAIM_JUSTID_RESPONSE;
      STREAM_READ_RESPONSE = new Builder<List<Map.Entry<String, List<StreamEntry>>>>() {
         public List<Map.Entry<String, List<StreamEntry>>> build(Object var1) {
            if (var1 == null) {
               return null;
            } else {
               List var2 = (List)var1;
               if (var2.isEmpty()) {
                  return Collections.emptyList();
               } else if (var2.get(0) instanceof KeyValue) {
                  return (List)var2.stream().map((var0) -> new KeyValue(BuilderFactory.STRING.build(var0.getKey()), BuilderFactory.STREAM_ENTRY_LIST.build(var0.getValue()))).collect(Collectors.toList());
               } else {
                  ArrayList var3 = new ArrayList(var2.size());

                  for(Object var5 : var2) {
                     List var6 = (List)var5;
                     String var7 = BuilderFactory.STRING.build(var6.get(0));
                     List var8 = BuilderFactory.STREAM_ENTRY_LIST.build(var6.get(1));
                     var3.add(KeyValue.of(var7, var8));
                  }

                  return var3;
               }
            }
         }

         public String toString() {
            return "List<Entry<String, List<StreamEntry>>>";
         }
      };
      STREAM_PENDING_ENTRY_LIST = new Builder<List<StreamPendingEntry>>() {
         public List<StreamPendingEntry> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               List var2 = (List)var1;
               ArrayList var3 = new ArrayList(var2.size());

               for(Object var5 : var2) {
                  List var6 = (List)var5;
                  String var7 = SafeEncoder.encode((byte[])var6.get(0));
                  String var8 = SafeEncoder.encode((byte[])var6.get(1));
                  long var9 = (Long)BuilderFactory.LONG.build(var6.get(2));
                  long var11 = (Long)BuilderFactory.LONG.build(var6.get(3));
                  var3.add(new StreamPendingEntry(new StreamEntryID(var7), var8, var9, var11));
               }

               return var3;
            }
         }

         public String toString() {
            return "List<StreamPendingEntry>";
         }
      };
      STREAM_INFO = new Builder<StreamInfo>() {
         Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("last-generated-id", BuilderFactory.STREAM_ENTRY_ID);
            var1.put("first-entry", BuilderFactory.STREAM_ENTRY);
            var1.put("length", BuilderFactory.LONG);
            var1.put("radix-tree-keys", BuilderFactory.LONG);
            var1.put("radix-tree-nodes", BuilderFactory.LONG);
            var1.put("last-entry", BuilderFactory.STREAM_ENTRY);
            var1.put("groups", BuilderFactory.LONG);
            return var1;
         }

         public StreamInfo build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               List var2 = (List)var1;
               Iterator var3 = var2.iterator();
               return new StreamInfo(BuilderFactory.createMapFromDecodingFunctions(var3, this.mappingFunctions));
            }
         }

         public String toString() {
            return "StreamInfo";
         }
      };
      STREAM_GROUP_INFO_LIST = new Builder<List<StreamGroupInfo>>() {
         Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("name", BuilderFactory.STRING);
            var1.put("consumers", BuilderFactory.LONG);
            var1.put("pending", BuilderFactory.LONG);
            var1.put("last-delivered-id", BuilderFactory.STREAM_ENTRY_ID);
            return var1;
         }

         public List<StreamGroupInfo> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               ArrayList var2 = new ArrayList();

               for(List var5 : (List)var1) {
                  Iterator var6 = var5.iterator();
                  StreamGroupInfo var7 = new StreamGroupInfo(BuilderFactory.createMapFromDecodingFunctions(var6, this.mappingFunctions));
                  var2.add(var7);
               }

               return var2;
            }
         }

         public String toString() {
            return "List<StreamGroupInfo>";
         }
      };
      STREAM_CONSUMERS_INFO_LIST = new Builder<List<StreamConsumersInfo>>() {
         Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("name", BuilderFactory.STRING);
            var1.put("idle", BuilderFactory.LONG);
            var1.put("pending", BuilderFactory.LONG);
            return var1;
         }

         public List<StreamConsumersInfo> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               ArrayList var2 = new ArrayList();

               for(List var5 : (List)var1) {
                  Iterator var6 = var5.iterator();
                  StreamConsumersInfo var7 = new StreamConsumersInfo(BuilderFactory.createMapFromDecodingFunctions(var6, this.mappingFunctions));
                  var2.add(var7);
               }

               return var2;
            }
         }

         public String toString() {
            return "List<StreamConsumersInfo>";
         }
      };
      STREAM_CONSUMER_INFO_LIST = new Builder<List<StreamConsumerInfo>>() {
         Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("name", BuilderFactory.STRING);
            var1.put("idle", BuilderFactory.LONG);
            var1.put("pending", BuilderFactory.LONG);
            return var1;
         }

         public List<StreamConsumerInfo> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               ArrayList var2 = new ArrayList();

               for(List var5 : (List)var1) {
                  Iterator var6 = var5.iterator();
                  StreamConsumerInfo var7 = new StreamConsumerInfo(BuilderFactory.createMapFromDecodingFunctions(var6, this.mappingFunctions));
                  var2.add(var7);
               }

               return var2;
            }
         }

         public String toString() {
            return "List<StreamConsumerInfo>";
         }
      };
      STREAM_CONSUMER_FULL_INFO_LIST = new Builder<List<StreamConsumerFullInfo>>() {
         final Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("name", BuilderFactory.STRING);
            var1.put("seen-time", BuilderFactory.LONG);
            var1.put("pel-count", BuilderFactory.LONG);
            var1.put("pending", BuilderFactory.ENCODED_OBJECT_LIST);
            return var1;
         }

         public List<StreamConsumerFullInfo> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               ArrayList var2 = new ArrayList();

               for(Object var5 : (List)var1) {
                  List var6 = (List)var5;
                  Iterator var7 = var6.iterator();
                  StreamConsumerFullInfo var8 = new StreamConsumerFullInfo(BuilderFactory.createMapFromDecodingFunctions(var7, this.mappingFunctions));
                  var2.add(var8);
               }

               return var2;
            }
         }

         public String toString() {
            return "List<StreamConsumerFullInfo>";
         }
      };
      STREAM_GROUP_FULL_INFO_LIST = new Builder<List<StreamGroupFullInfo>>() {
         final Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("name", BuilderFactory.STRING);
            var1.put("consumers", BuilderFactory.STREAM_CONSUMER_FULL_INFO_LIST);
            var1.put("pending", BuilderFactory.ENCODED_OBJECT_LIST);
            var1.put("last-delivered-id", BuilderFactory.STREAM_ENTRY_ID);
            var1.put("pel-count", BuilderFactory.LONG);
            return var1;
         }

         public List<StreamGroupFullInfo> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               ArrayList var2 = new ArrayList();

               for(Object var5 : (List)var1) {
                  List var6 = (List)var5;
                  Iterator var7 = var6.iterator();
                  StreamGroupFullInfo var8 = new StreamGroupFullInfo(BuilderFactory.createMapFromDecodingFunctions(var7, this.mappingFunctions));
                  var2.add(var8);
               }

               return var2;
            }
         }

         public String toString() {
            return "List<StreamGroupFullInfo>";
         }
      };
      STREAM_FULL_INFO = new Builder<StreamFullInfo>() {
         final Map<String, Builder> mappingFunctions = this.createDecoderMap();

         private Map<String, Builder> createDecoderMap() {
            HashMap var1 = new HashMap();
            var1.put("last-generated-id", BuilderFactory.STREAM_ENTRY_ID);
            var1.put("length", BuilderFactory.LONG);
            var1.put("radix-tree-keys", BuilderFactory.LONG);
            var1.put("radix-tree-nodes", BuilderFactory.LONG);
            var1.put("groups", BuilderFactory.STREAM_GROUP_FULL_INFO_LIST);
            var1.put("entries", BuilderFactory.STREAM_ENTRY_LIST);
            return var1;
         }

         public StreamFullInfo build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               List var2 = (List)var1;
               Iterator var3 = var2.iterator();
               return new StreamFullInfo(BuilderFactory.createMapFromDecodingFunctions(var3, this.mappingFunctions));
            }
         }

         public String toString() {
            return "StreamFullInfo";
         }
      };
      STREAM_INFO_FULL = STREAM_FULL_INFO;
      STREAM_PENDING_SUMMARY = new Builder<StreamPendingSummary>() {
         public StreamPendingSummary build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               List var2 = (List)var1;
               long var3 = (Long)BuilderFactory.LONG.build(var2.get(0));
               String var5 = SafeEncoder.encode((byte[])var2.get(1));
               String var6 = SafeEncoder.encode((byte[])var2.get(2));
               List var7 = (List)var2.get(3);
               HashMap var8 = new HashMap(var7.size());

               for(List var10 : var7) {
                  var8.put(SafeEncoder.encode((byte[])var10.get(0)), Long.parseLong(SafeEncoder.encode((byte[])var10.get(1))));
               }

               return new StreamPendingSummary(var3, new StreamEntryID(var5), new StreamEntryID(var6), var8);
            }
         }

         public String toString() {
            return "StreamPendingSummary";
         }
      };
      BACKUP_BUILDERS_FOR_DECODING_FUNCTIONS = Arrays.asList(STRING, LONG, DOUBLE);
      STR_ALGO_LCS_RESULT_BUILDER = new Builder<LCSMatchResult>() {
         public LCSMatchResult build(Object var1) {
            if (var1 == null) {
               return null;
            } else if (var1 instanceof byte[]) {
               return new LCSMatchResult(BuilderFactory.STRING.build(var1));
            } else if (var1 instanceof Long) {
               return new LCSMatchResult((Long)BuilderFactory.LONG.build(var1));
            } else {
               long var2 = 0L;
               ArrayList var4 = new ArrayList();
               List var5 = (List)var1;
               if (var5.get(0) instanceof KeyValue) {
                  for(KeyValue var7 : var5) {
                     if ("matches".equalsIgnoreCase(BuilderFactory.STRING.build(var7.getKey()))) {
                        this.addMatchedPosition(var4, var7.getValue());
                     } else if ("len".equalsIgnoreCase(BuilderFactory.STRING.build(var7.getKey()))) {
                        var2 = (Long)BuilderFactory.LONG.build(var7.getValue());
                     }
                  }
               } else {
                  for(int var8 = 0; var8 < var5.size(); var8 += 2) {
                     if ("matches".equalsIgnoreCase(BuilderFactory.STRING.build(var5.get(var8)))) {
                        this.addMatchedPosition(var4, var5.get(var8 + 1));
                     } else if ("len".equalsIgnoreCase(BuilderFactory.STRING.build(var5.get(var8)))) {
                        var2 = (Long)BuilderFactory.LONG.build(var5.get(var8 + 1));
                     }
                  }
               }

               return new LCSMatchResult(var4, var2);
            }
         }

         private void addMatchedPosition(List<LCSMatchResult.MatchedPosition> var1, Object var2) {
            for(Object var5 : (List)var2) {
               if (var5 instanceof List) {
                  List var6 = (List)var5;
                  LCSMatchResult.Position var7 = new LCSMatchResult.Position((Long)BuilderFactory.LONG.build(((List)var6.get(0)).get(0)), (Long)BuilderFactory.LONG.build(((List)var6.get(0)).get(1)));
                  LCSMatchResult.Position var8 = new LCSMatchResult.Position((Long)BuilderFactory.LONG.build(((List)var6.get(1)).get(0)), (Long)BuilderFactory.LONG.build(((List)var6.get(1)).get(1)));
                  long var9 = 0L;
                  if (var6.size() >= 3) {
                     var9 = (Long)BuilderFactory.LONG.build(var6.get(2));
                  }

                  var1.add(new LCSMatchResult.MatchedPosition(var7, var8, var9));
               }
            }

         }
      };
      STRING_MAP_FROM_PAIRS = new Builder<Map<String, String>>() {
         public Map<String, String> build(Object var1) {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return Collections.emptyMap();
            } else if (var2.get(0) instanceof KeyValue) {
               return (Map)var2.stream().collect(Collectors.toMap((var0) -> BuilderFactory.STRING.build(var0.getKey()), (var0) -> BuilderFactory.STRING.build(var0.getValue())));
            } else {
               HashMap var3 = new HashMap(var2.size());

               for(Object var5 : var2) {
                  if (var5 != null) {
                     List var6 = (List)var5;
                     if (!var6.isEmpty()) {
                        var3.put(BuilderFactory.STRING.build(var6.get(0)), BuilderFactory.STRING.build(var6.get(1)));
                     }
                  }
               }

               return var3;
            }
         }

         public String toString() {
            return "Map<String, String>";
         }
      };
      ENCODED_OBJECT_MAP_FROM_PAIRS = new Builder<Map<String, Object>>() {
         public Map<String, Object> build(Object var1) {
            List var2 = (List)var1;
            if (var2.isEmpty()) {
               return Collections.emptyMap();
            } else if (var2.get(0) instanceof KeyValue) {
               return (Map)var2.stream().collect(Collectors.toMap((var0) -> BuilderFactory.STRING.build(var0.getKey()), (var0) -> BuilderFactory.ENCODED_OBJECT.build(var0.getValue())));
            } else {
               HashMap var3 = new HashMap(var2.size());

               for(Object var5 : var2) {
                  if (var5 != null) {
                     List var6 = (List)var5;
                     if (!var6.isEmpty()) {
                        var3.put(BuilderFactory.STRING.build(var6.get(0)), BuilderFactory.STRING.build(var6.get(1)));
                     }
                  }
               }

               return var3;
            }
         }

         public String toString() {
            return "Map<String, String>";
         }
      };
      LIBRARY_LIST = LibraryInfo.LIBRARY_INFO_LIST;
      STRING_LIST_LIST = new Builder<List<List<String>>>() {
         public List<List<String>> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               Stream var10000 = ((List)var1).stream();
               Builder var10001 = BuilderFactory.STRING_LIST;
               var10001.getClass();
               return (List)var10000.map(var10001::build).collect(Collectors.toList());
            }
         }

         public String toString() {
            return "List<List<String>>";
         }
      };
      ENCODED_OBJECT_LIST_LIST = new Builder<List<List<Object>>>() {
         public List<List<Object>> build(Object var1) {
            if (null == var1) {
               return null;
            } else {
               Stream var10000 = ((List)var1).stream();
               Builder var10001 = BuilderFactory.ENCODED_OBJECT_LIST;
               var10001.getClass();
               return (List)var10000.map(var10001::build).collect(Collectors.toList());
            }
         }

         public String toString() {
            return "List<List<Object>>";
         }
      };
   }

   protected static class SetFromList<E> extends AbstractSet<E> implements Serializable {
      private static final long serialVersionUID = -2850347066962734052L;
      private final List<E> list;

      private SetFromList(List<E> var1) {
         this.list = var1;
      }

      public void clear() {
         this.list.clear();
      }

      public int size() {
         return this.list.size();
      }

      public boolean isEmpty() {
         return this.list.isEmpty();
      }

      public boolean contains(Object var1) {
         return this.list.contains(var1);
      }

      public boolean remove(Object var1) {
         return this.list.remove(var1);
      }

      public boolean add(E var1) {
         return !this.contains(var1) && this.list.add(var1);
      }

      public Iterator<E> iterator() {
         return this.list.iterator();
      }

      public Object[] toArray() {
         return this.list.toArray();
      }

      public <T> T[] toArray(T[] var1) {
         return (T[])this.list.toArray(var1);
      }

      public String toString() {
         return this.list.toString();
      }

      public int hashCode() {
         return this.list.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         } else if (var1 == this) {
            return true;
         } else if (!(var1 instanceof Set)) {
            return false;
         } else {
            Collection var2 = (Collection)var1;
            return var2.size() != this.size() ? false : this.containsAll(var2);
         }
      }

      public boolean containsAll(Collection<?> var1) {
         return this.list.containsAll(var1);
      }

      public boolean removeAll(Collection<?> var1) {
         return this.list.removeAll(var1);
      }

      public boolean retainAll(Collection<?> var1) {
         return this.list.retainAll(var1);
      }

      protected static <E> SetFromList<E> of(List<E> var0) {
         return var0 == null ? null : new SetFromList(var0);
      }
   }
}
