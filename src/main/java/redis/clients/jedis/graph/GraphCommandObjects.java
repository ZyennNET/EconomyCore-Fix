package redis.clients.jedis.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.CommandObject;
import redis.clients.jedis.Connection;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.providers.ConnectionProvider;

@Deprecated
public class GraphCommandObjects {
   private final RedisGraphCommands graph;
   private final Connection connection;
   private final ConnectionProvider provider;
   private Function<ProtocolCommand, CommandArguments> commArgs = (var0) -> new CommandArguments(var0);
   private final ConcurrentHashMap<String, Builder<ResultSet>> builders = new ConcurrentHashMap();

   public GraphCommandObjects(RedisGraphCommands var1) {
      this.graph = var1;
      this.connection = null;
      this.provider = null;
   }

   public GraphCommandObjects(Connection var1) {
      this.graph = null;
      this.connection = var1;
      this.provider = null;
   }

   public GraphCommandObjects(ConnectionProvider var1) {
      this.graph = null;
      this.connection = null;
      this.provider = var1;
   }

   public void setBaseCommandArgumentsCreator(Function<ProtocolCommand, CommandArguments> var1) {
      this.commArgs = var1;
   }

   public final CommandObject<ResultSet> graphQuery(String var1, String var2) {
      return new CommandObject<ResultSet>(((CommandArguments)this.commArgs.apply(GraphProtocol.GraphCommand.QUERY)).key(var1).add(var2).add(GraphProtocol.GraphKeyword.__COMPACT), this.getBuilder(var1));
   }

   public final CommandObject<ResultSet> graphReadonlyQuery(String var1, String var2) {
      return new CommandObject<ResultSet>(((CommandArguments)this.commArgs.apply(GraphProtocol.GraphCommand.RO_QUERY)).key(var1).add(var2).add(GraphProtocol.GraphKeyword.__COMPACT), this.getBuilder(var1));
   }

   public final CommandObject<ResultSet> graphQuery(String var1, String var2, long var3) {
      return this.graphQuery(var1, GraphQueryParams.queryParams(var2).timeout(var3));
   }

   public final CommandObject<ResultSet> graphReadonlyQuery(String var1, String var2, long var3) {
      return this.graphQuery(var1, GraphQueryParams.queryParams().readonly().query(var2).timeout(var3));
   }

   public final CommandObject<ResultSet> graphQuery(String var1, String var2, Map<String, Object> var3) {
      return this.graphQuery(var1, GraphQueryParams.queryParams(var2).params(var3));
   }

   public final CommandObject<ResultSet> graphReadonlyQuery(String var1, String var2, Map<String, Object> var3) {
      return this.graphQuery(var1, GraphQueryParams.queryParams().readonly().query(var2).params(var3));
   }

   public final CommandObject<ResultSet> graphQuery(String var1, String var2, Map<String, Object> var3, long var4) {
      return this.graphQuery(var1, GraphQueryParams.queryParams(var2).params(var3).timeout(var4));
   }

   public final CommandObject<ResultSet> graphReadonlyQuery(String var1, String var2, Map<String, Object> var3, long var4) {
      return this.graphQuery(var1, GraphQueryParams.queryParams().readonly().query(var2).params(var3).timeout(var4));
   }

   private CommandObject<ResultSet> graphQuery(String var1, GraphQueryParams var2) {
      return new CommandObject<ResultSet>(((CommandArguments)this.commArgs.apply(!var2.isReadonly() ? GraphProtocol.GraphCommand.QUERY : GraphProtocol.GraphCommand.RO_QUERY)).key(var1).addParams(var2), this.getBuilder(var1));
   }

   public final CommandObject<String> graphDelete(String var1) {
      return new CommandObject<String>(((CommandArguments)this.commArgs.apply(GraphProtocol.GraphCommand.DELETE)).key(var1), BuilderFactory.STRING);
   }

   private Builder<ResultSet> getBuilder(String var1) {
      if (!this.builders.containsKey(var1)) {
         this.createBuilder(var1);
      }

      return (Builder)this.builders.get(var1);
   }

   private void createBuilder(String var1) {
      synchronized(this.builders) {
         this.builders.putIfAbsent(var1, new ResultSetBuilder(new GraphCacheImpl(var1)));
      }
   }

   private class GraphCacheImpl implements GraphCache {
      private final GraphCacheList labels;
      private final GraphCacheList propertyNames;
      private final GraphCacheList relationshipTypes;

      public GraphCacheImpl(String var2) {
         this.labels = GraphCommandObjects.this.new GraphCacheList(var2, "db.labels");
         this.propertyNames = GraphCommandObjects.this.new GraphCacheList(var2, "db.propertyKeys");
         this.relationshipTypes = GraphCommandObjects.this.new GraphCacheList(var2, "db.relationshipTypes");
      }

      public String getLabel(int var1) {
         return this.labels.getCachedData(var1);
      }

      public String getRelationshipType(int var1) {
         return this.relationshipTypes.getCachedData(var1);
      }

      public String getPropertyName(int var1) {
         return this.propertyNames.getCachedData(var1);
      }
   }

   private class GraphCacheList {
      private final String name;
      private final String query;
      private final List<String> data = new CopyOnWriteArrayList();

      public GraphCacheList(String var2, String var3) {
         this.name = var2;
         this.query = "CALL " + var3 + "()";
      }

      public String getCachedData(int var1) {
         if (var1 >= this.data.size()) {
            synchronized(this.data) {
               if (var1 >= this.data.size()) {
                  this.getProcedureInfo();
               }
            }
         }

         return (String)this.data.get(var1);
      }

      private void getProcedureInfo() {
         ResultSet var1 = this.callProcedure();
         Iterator var2 = var1.iterator();
         ArrayList var3 = new ArrayList();

         for(int var4 = 0; var2.hasNext(); ++var4) {
            Record var5 = (Record)var2.next();
            if (var4 >= this.data.size()) {
               var3.add(var5.getString(0));
            }
         }

         this.data.addAll(var3);
      }

      private ResultSet callProcedure() {
         if (GraphCommandObjects.this.graph != null) {
            return GraphCommandObjects.this.graph.graphQuery(this.name, this.query);
         } else {
            CommandObject var1 = new CommandObject((new CommandArguments(GraphProtocol.GraphCommand.QUERY)).key(this.name).add(this.query).add(GraphProtocol.GraphKeyword.__COMPACT), GraphCommandObjects.this.getBuilder(this.name));
            if (GraphCommandObjects.this.connection != null) {
               return (ResultSet)GraphCommandObjects.this.connection.executeCommand(var1);
            } else {
               Connection var2 = GraphCommandObjects.this.provider.getConnection(var1.getArguments());
               Throwable var3 = null;

               ResultSet var4;
               try {
                  var4 = (ResultSet)var2.executeCommand(var1);
               } catch (Throwable var13) {
                  var3 = var13;
                  throw var13;
               } finally {
                  if (var2 != null) {
                     if (var3 != null) {
                        try {
                           var2.close();
                        } catch (Throwable var12) {
                           var3.addSuppressed(var12);
                        }
                     } else {
                        var2.close();
                     }
                  }

               }

               return var4;
            }
         }
      }
   }
}
