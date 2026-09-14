package redis.clients.jedis.graph;

import java.util.HashMap;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.jedis.params.IParams;

@Deprecated
public class GraphQueryParams implements IParams {
   private boolean readonly;
   private String query;
   private Map<String, Object> params;
   private Long timeout;

   public GraphQueryParams() {
   }

   public static GraphQueryParams queryParams() {
      return new GraphQueryParams();
   }

   public GraphQueryParams(String var1) {
      this.query = var1;
   }

   public static GraphQueryParams queryParams(String var0) {
      return new GraphQueryParams(var0);
   }

   public GraphQueryParams readonly() {
      return this.readonly(true);
   }

   public GraphQueryParams readonly(boolean var1) {
      this.readonly = var1;
      return this;
   }

   public GraphQueryParams query(String var1) {
      this.query = var1;
      return this;
   }

   public GraphQueryParams params(Map<String, Object> var1) {
      this.params = var1;
      return this;
   }

   public GraphQueryParams addParam(String var1, Object var2) {
      if (this.params == null) {
         this.params = new HashMap();
      }

      this.params.put(var1, var2);
      return this;
   }

   public GraphQueryParams timeout(long var1) {
      this.timeout = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.query == null) {
         throw new JedisException("Query string must be set.");
      } else {
         if (this.params == null) {
            var1.add(this.query);
         } else {
            var1.add(RedisGraphQueryUtil.prepareQuery(this.query, this.params));
         }

         var1.add(GraphProtocol.GraphKeyword.__COMPACT);
         if (this.timeout != null) {
            var1.add(GraphProtocol.GraphKeyword.TIMEOUT).add(this.timeout).blocking();
         }

      }
   }

   public boolean isReadonly() {
      return this.readonly;
   }
}
