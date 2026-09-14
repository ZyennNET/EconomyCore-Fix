package redis.clients.jedis.timeseries;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public final class TimeSeriesBuilderFactory {
   public static final Builder<TSElement> TIMESERIES_ELEMENT = new Builder<TSElement>() {
      public TSElement build(Object var1) {
         List var2 = (List)var1;
         return var2 != null && !var2.isEmpty() ? new TSElement((Long)BuilderFactory.LONG.build(var2.get(0)), (Double)BuilderFactory.DOUBLE.build(var2.get(1))) : null;
      }
   };
   public static final Builder<List<TSElement>> TIMESERIES_ELEMENT_LIST = new Builder<List<TSElement>>() {
      public List<TSElement> build(Object var1) {
         return (List)((List)var1).stream().map((var0) -> (List)var0).map((var0) -> new TSElement((Long)BuilderFactory.LONG.build(var0.get(0)), (Double)BuilderFactory.DOUBLE.build(var0.get(1)))).collect(Collectors.toList());
      }
   };
   public static final Builder<Map<String, TSMRangeElements>> TIMESERIES_MRANGE_RESPONSE = new Builder<Map<String, TSMRangeElements>>() {
      public Map<String, TSMRangeElements> build(Object var1) {
         return (Map)((List)var1).stream().map((var0) -> (List)var0).map((var0) -> new TSMRangeElements(BuilderFactory.STRING.build(var0.get(0)), BuilderFactory.STRING_MAP_FROM_PAIRS.build(var0.get(1)), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST.build(var0.get(2)))).collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, Function.identity(), (var0, var1x) -> var0, LinkedHashMap::new));
      }
   };
   public static final Builder<Map<String, TSMRangeElements>> TIMESERIES_MRANGE_RESPONSE_RESP3 = new Builder<Map<String, TSMRangeElements>>() {
      public Map<String, TSMRangeElements> build(Object var1) {
         List var2 = (List)var1;
         LinkedHashMap var3 = new LinkedHashMap(var2.size() / 2, 1.0F);

         for(KeyValue var5 : var2) {
            String var6 = BuilderFactory.STRING.build(var5.getKey());
            List var7 = (List)var5.getValue();
            TSMRangeElements var8;
            switch (var7.size()) {
               case 3:
                  List var9 = (List)var7.get(1);
                  KeyValue var10 = (KeyValue)var9.get(0);

                  assert "aggregators".equalsIgnoreCase(BuilderFactory.STRING.build(var10.getKey()));

                  Map var10003 = BuilderFactory.STRING_MAP.build(var7.get(0));
                  Stream var10004 = ((List)var10.getValue()).stream();
                  Builder var10005 = BuilderFactory.STRING;
                  var10005.getClass();
                  var8 = new TSMRangeElements(var6, var10003, (List)var10004.map(var10005::build).map(AggregationType::safeValueOf).collect(Collectors.toList()), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST.build(var7.get(2)));
                  break;
               case 4:
                  List var11 = (List)var7.get(1);

                  assert "reducers".equalsIgnoreCase(BuilderFactory.STRING.build(((KeyValue)var11.get(0)).getKey()));

                  List var12 = (List)var7.get(2);

                  assert "sources".equalsIgnoreCase(BuilderFactory.STRING.build(((KeyValue)var12.get(0)).getKey()));

                  var8 = new TSMRangeElements(var6, BuilderFactory.STRING_MAP.build(var7.get(0)), BuilderFactory.STRING_LIST.build(((KeyValue)var11.get(0)).getValue()), BuilderFactory.STRING_LIST.build(((KeyValue)var12.get(0)).getValue()), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT_LIST.build(var7.get(3)));
                  break;
               default:
                  throw new IllegalStateException();
            }

            var3.put(var6, var8);
         }

         return var3;
      }
   };
   public static final Builder<Map<String, TSMGetElement>> TIMESERIES_MGET_RESPONSE = new Builder<Map<String, TSMGetElement>>() {
      public Map<String, TSMGetElement> build(Object var1) {
         return (Map)((List)var1).stream().map((var0) -> (List)var0).map((var0) -> new TSMGetElement(BuilderFactory.STRING.build(var0.get(0)), BuilderFactory.STRING_MAP_FROM_PAIRS.build(var0.get(1)), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT.build(var0.get(2)))).collect(Collectors.toMap(AbstractMap.SimpleImmutableEntry::getKey, Function.identity()));
      }
   };
   public static final Builder<Map<String, TSMGetElement>> TIMESERIES_MGET_RESPONSE_RESP3 = new Builder<Map<String, TSMGetElement>>() {
      public Map<String, TSMGetElement> build(Object var1) {
         List var2 = (List)var1;
         LinkedHashMap var3 = new LinkedHashMap(var2.size());

         for(KeyValue var5 : var2) {
            String var6 = BuilderFactory.STRING.build(var5.getKey());
            List var7 = (List)var5.getValue();
            TSMGetElement var8 = new TSMGetElement(var6, BuilderFactory.STRING_MAP.build(var7.get(0)), TimeSeriesBuilderFactory.TIMESERIES_ELEMENT.build(var7.get(1)));
            var3.put(var6, var8);
         }

         return var3;
      }
   };

   private TimeSeriesBuilderFactory() {
      throw new InstantiationError("Must not instantiate this class");
   }
}
