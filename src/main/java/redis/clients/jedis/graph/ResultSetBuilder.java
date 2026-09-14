package redis.clients.jedis.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.graph.entities.Edge;
import redis.clients.jedis.graph.entities.GraphEntity;
import redis.clients.jedis.graph.entities.Node;
import redis.clients.jedis.graph.entities.Path;
import redis.clients.jedis.graph.entities.Point;
import redis.clients.jedis.util.SafeEncoder;

@Deprecated
class ResultSetBuilder extends Builder<ResultSet> {
   private final GraphCache graphCache;
   private static final ScalarType[] SCALAR_TYPES = ResultSetBuilder.ScalarType.values();
   private static final ResultSet.ColumnType[] COLUMN_TYPES = ResultSet.ColumnType.values();

   ResultSetBuilder(GraphCache var1) {
      this.graphCache = var1;
   }

   public ResultSet build(Object var1) {
      List var2 = (List)var1;
      if (var2.get(var2.size() - 1) instanceof JedisDataException) {
         throw (JedisDataException)var2.get(var2.size() - 1);
      } else {
         Object var3;
         Object var4;
         Object var5;
         if (var2.size() == 1) {
            var3 = Collections.emptyList();
            var4 = Collections.emptyList();
            var5 = var2.get(0);
         } else {
            if (var2.size() != 3) {
               throw new JedisException("Unrecognized graph response format.");
            }

            var3 = var2.get(0);
            var4 = var2.get(1);
            var5 = var2.get(2);
         }

         HeaderImpl var6 = this.parseHeader(var3);
         List var7 = this.parseRecords(var6, var4);
         StatisticsImpl var8 = this.parseStatistics(var5);
         return new ResultSetImpl(var6, var7, var8);
      }
   }

   private List<Record> parseRecords(Header var1, Object var2) {
      List var3 = (List)var2;
      if (var3 != null && !var3.isEmpty()) {
         ArrayList var4 = new ArrayList(var3.size());

         for(List var6 : var3) {
            ArrayList var7 = new ArrayList(var6.size());

            for(int var8 = 0; var8 < var6.size(); ++var8) {
               List var9 = (List)var6.get(var8);
               ResultSet.ColumnType var10 = (ResultSet.ColumnType)var1.getSchemaTypes().get(var8);
               switch (var10) {
                  case NODE:
                     var7.add(this.deserializeNode(var9));
                     break;
                  case RELATION:
                     var7.add(this.deserializeEdge(var9));
                     break;
                  case SCALAR:
                     var7.add(this.deserializeScalar(var9));
                     break;
                  default:
                     var7.add((Object)null);
               }
            }

            RecordImpl var11 = new RecordImpl(var1.getSchemaNames(), var7);
            var4.add(var11);
         }

         return var4;
      } else {
         return new ArrayList(0);
      }
   }

   private Node deserializeNode(List<Object> var1) {
      List var2 = (List)var1.get(1);
      List var3 = (List)var1.get(2);
      Node var4 = new Node(var2.size(), var3.size());
      this.deserializeGraphEntityId(var4, (Long)var1.get(0));

      for(Long var6 : var2) {
         String var7 = this.graphCache.getLabel(var6.intValue());
         var4.addLabel(var7);
      }

      this.deserializeGraphEntityProperties(var4, var3);
      return var4;
   }

   private void deserializeGraphEntityId(GraphEntity var1, long var2) {
      var1.setId(var2);
   }

   private Edge deserializeEdge(List<Object> var1) {
      List var2 = (List)var1.get(4);
      Edge var3 = new Edge(var2.size());
      this.deserializeGraphEntityId(var3, (Long)var1.get(0));
      String var4 = this.graphCache.getRelationshipType(((Long)var1.get(1)).intValue());
      var3.setRelationshipType(var4);
      var3.setSource((Long)var1.get(2));
      var3.setDestination((Long)var1.get(3));
      this.deserializeGraphEntityProperties(var3, var2);
      return var3;
   }

   private void deserializeGraphEntityProperties(GraphEntity var1, List<List<Object>> var2) {
      for(List var4 : var2) {
         String var5 = this.graphCache.getPropertyName(((Long)var4.get(0)).intValue());
         List var6 = var4.subList(1, var4.size());
         var1.addProperty(var5, this.deserializeScalar(var6));
      }

   }

   private Object deserializeScalar(List<Object> var1) {
      ScalarType var2 = this.getValueTypeFromObject(var1.get(0));
      Object var3 = var1.get(1);
      switch (var2) {
         case NULL:
            return null;
         case BOOLEAN:
            return Boolean.parseBoolean(SafeEncoder.encode((byte[])var3));
         case DOUBLE:
            return BuilderFactory.DOUBLE.build(var3);
         case INTEGER:
            return (Long)var3;
         case STRING:
            return SafeEncoder.encode((byte[])var3);
         case ARRAY:
            return this.deserializeArray(var3);
         case NODE:
            return this.deserializeNode((List)var3);
         case EDGE:
            return this.deserializeEdge((List)var3);
         case PATH:
            return this.deserializePath(var3);
         case MAP:
            return this.deserializeMap(var3);
         case POINT:
            return this.deserializePoint(var3);
         case UNKNOWN:
         default:
            return var3;
      }
   }

   private Object deserializePoint(Object var1) {
      return new Point(BuilderFactory.DOUBLE_LIST.build(var1));
   }

   private Map<String, Object> deserializeMap(Object var1) {
      List var2 = (List)var1;
      int var3 = var2.size();
      HashMap var4 = new HashMap(var3 >> 1);

      for(int var5 = 0; var5 < var3; var5 += 2) {
         String var6 = SafeEncoder.encode((byte[])var2.get(var5));
         Object var7 = this.deserializeScalar((List)var2.get(var5 + 1));
         var4.put(var6, var7);
      }

      return var4;
   }

   private Path deserializePath(Object var1) {
      List var2 = (List)var1;
      List var3 = (List)this.deserializeScalar((List)var2.get(0));
      List var4 = (List)this.deserializeScalar((List)var2.get(1));
      return new Path(var3, var4);
   }

   private List<Object> deserializeArray(Object var1) {
      List var2 = (List)var1;
      ArrayList var3 = new ArrayList(var2.size());

      for(List var5 : var2) {
         var3.add(this.deserializeScalar(var5));
      }

      return var3;
   }

   private ScalarType getValueTypeFromObject(Object var1) {
      return getScalarType(((Long)var1).intValue());
   }

   private static ScalarType getScalarType(int var0) {
      try {
         return SCALAR_TYPES[var0];
      } catch (IndexOutOfBoundsException var2) {
         throw new JedisException("Unrecognized response type");
      }
   }

   private HeaderImpl parseHeader(Object var1) {
      if (var1 == null) {
         return new HeaderImpl();
      } else {
         List var2 = (List)var1;
         ArrayList var3 = new ArrayList(var2.size());
         ArrayList var4 = new ArrayList(var2.size());

         for(List var6 : var2) {
            var3.add(COLUMN_TYPES[((Long)var6.get(0)).intValue()]);
            var4.add(SafeEncoder.encode((byte[])var6.get(1)));
         }

         return new HeaderImpl(var3, var4);
      }
   }

   private StatisticsImpl parseStatistics(Object var1) {
      Map var2 = (Map)((List)var1).stream().map(SafeEncoder::encode).map((var0) -> var0.split(": ")).collect(Collectors.toMap((var0) -> var0[0], (var0) -> var0[1]));
      return new StatisticsImpl(var2);
   }

   private class HeaderImpl implements Header {
      private final List<ResultSet.ColumnType> schemaTypes;
      private final List<String> schemaNames;

      private HeaderImpl() {
         this.schemaTypes = Collections.emptyList();
         this.schemaNames = Collections.emptyList();
      }

      private HeaderImpl(List<ResultSet.ColumnType> var2, List<String> var3) {
         this.schemaTypes = var2;
         this.schemaNames = var3;
      }

      public List<String> getSchemaNames() {
         return this.schemaNames;
      }

      public List<ResultSet.ColumnType> getSchemaTypes() {
         return this.schemaTypes;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof HeaderImpl)) {
            return false;
         } else {
            HeaderImpl var2 = (HeaderImpl)var1;
            return Objects.equals(this.getSchemaTypes(), var2.getSchemaTypes()) && Objects.equals(this.getSchemaNames(), var2.getSchemaNames());
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.getSchemaTypes(), this.getSchemaNames()});
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("HeaderImpl{");
         var1.append("schemaTypes=").append(this.schemaTypes);
         var1.append(", schemaNames=").append(this.schemaNames);
         var1.append('}');
         return var1.toString();
      }
   }

   private class RecordImpl implements Record {
      private final List<String> header;
      private final List<Object> values;

      public RecordImpl(List<String> var2, List<Object> var3) {
         this.header = var2;
         this.values = var3;
      }

      public <T> T getValue(int var1) {
         return (T)this.values.get(var1);
      }

      public <T> T getValue(String var1) {
         return (T)this.getValue(this.header.indexOf(var1));
      }

      public String getString(int var1) {
         return this.values.get(var1).toString();
      }

      public String getString(String var1) {
         return this.getString(this.header.indexOf(var1));
      }

      public List<String> keys() {
         return this.header;
      }

      public List<Object> values() {
         return this.values;
      }

      public boolean containsKey(String var1) {
         return this.header.contains(var1);
      }

      public int size() {
         return this.header.size();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof RecordImpl)) {
            return false;
         } else {
            RecordImpl var2 = (RecordImpl)var1;
            return Objects.equals(this.header, var2.header) && Objects.equals(this.values, var2.values);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.header, this.values});
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("Record{");
         var1.append("values=").append(this.values);
         var1.append('}');
         return var1.toString();
      }
   }

   private class ResultSetImpl implements ResultSet {
      private final Header header;
      private final List<Record> results;
      private final Statistics statistics;

      private ResultSetImpl(Header var2, List<Record> var3, Statistics var4) {
         this.header = var2;
         this.results = var3;
         this.statistics = var4;
      }

      public Header getHeader() {
         return this.header;
      }

      public Statistics getStatistics() {
         return this.statistics;
      }

      public int size() {
         return this.results.size();
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ResultSetImpl)) {
            return false;
         } else {
            ResultSetImpl var2 = (ResultSetImpl)var1;
            return Objects.equals(this.getHeader(), var2.getHeader()) && Objects.equals(this.getStatistics(), var2.getStatistics()) && Objects.equals(this.results, var2.results);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.getHeader(), this.getStatistics(), this.results});
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("ResultSetImpl{");
         var1.append("header=").append(this.header);
         var1.append(", statistics=").append(this.statistics);
         var1.append(", results=").append(this.results);
         var1.append('}');
         return var1.toString();
      }

      public Iterator<Record> iterator() {
         return this.results.iterator();
      }
   }

   private static enum ScalarType {
      UNKNOWN,
      NULL,
      STRING,
      INTEGER,
      BOOLEAN,
      DOUBLE,
      ARRAY,
      EDGE,
      NODE,
      PATH,
      MAP,
      POINT;
   }

   private class StatisticsImpl implements Statistics {
      private final Map<String, String> statistics;

      private StatisticsImpl(Map<String, String> var2) {
         this.statistics = var2;
      }

      public String getStringValue(String var1) {
         return (String)this.statistics.get(var1);
      }

      private int getIntValue(String var1) {
         String var2 = this.getStringValue(var1);
         return var2 == null ? 0 : Integer.parseInt(var2);
      }

      public int nodesCreated() {
         return this.getIntValue("Nodes created");
      }

      public int nodesDeleted() {
         return this.getIntValue("Nodes deleted");
      }

      public int indicesCreated() {
         return this.getIntValue("Indices created");
      }

      public int indicesDeleted() {
         return this.getIntValue("Indices deleted");
      }

      public int labelsAdded() {
         return this.getIntValue("Labels added");
      }

      public int relationshipsDeleted() {
         return this.getIntValue("Relationships deleted");
      }

      public int relationshipsCreated() {
         return this.getIntValue("Relationships created");
      }

      public int propertiesSet() {
         return this.getIntValue("Properties set");
      }

      public boolean cachedExecution() {
         return "1".equals(this.getStringValue("Cached execution"));
      }

      public String queryIntervalExecutionTime() {
         return this.getStringValue("Query internal execution time");
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof StatisticsImpl)) {
            return false;
         } else {
            StatisticsImpl var2 = (StatisticsImpl)var1;
            return Objects.equals(this.statistics, var2.statistics);
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.statistics});
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder("Statistics{");
         var1.append(this.statistics);
         var1.append('}');
         return var1.toString();
      }
   }
}
