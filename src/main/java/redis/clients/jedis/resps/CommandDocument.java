package redis.clients.jedis.resps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class CommandDocument {
   private static final String SUMMARY_STR = "summary";
   private static final String SINCE_STR = "since";
   private static final String GROUP_STR = "group";
   private static final String COMPLEXITY_STR = "complexity";
   private static final String HISTORY_STR = "history";
   private final String summary;
   private final String since;
   private final String group;
   private final String complexity;
   private final List<String> history;
   @Deprecated
   public static final Builder<CommandDocument> COMMAND_DOCUMENT_BUILDER = new Builder<CommandDocument>() {
      public CommandDocument build(Object var1) {
         List var2 = (List)var1;
         String var3 = BuilderFactory.STRING.build(var2.get(1));
         String var4 = BuilderFactory.STRING.build(var2.get(3));
         String var5 = BuilderFactory.STRING.build(var2.get(5));
         String var6 = BuilderFactory.STRING.build(var2.get(7));
         ArrayList var7 = null;
         if (((String)BuilderFactory.STRING.build(var2.get(8))).equals("history")) {
            List var8 = (List)var2.get(9);
            var7 = new ArrayList(var8.size());

            for(List var10 : var8) {
               var7.add((String)BuilderFactory.STRING.build(var10.get(0)) + ": " + (String)BuilderFactory.STRING.build(var10.get(1)));
            }
         }

         return new CommandDocument(var3, var4, var5, var6, var7);
      }
   };

   @Deprecated
   public CommandDocument(String var1, String var2, String var3, String var4, List<String> var5) {
      this.summary = var1;
      this.since = var2;
      this.group = var3;
      this.complexity = var4;
      this.history = var5;
   }

   public CommandDocument(Map<String, Object> var1) {
      this.summary = (String)var1.get("summary");
      this.since = (String)var1.get("since");
      this.group = (String)var1.get("group");
      this.complexity = (String)var1.get("complexity");
      List var2 = (List)var1.get("history");
      if (var2 == null) {
         this.history = null;
      } else if (var2.isEmpty()) {
         this.history = Collections.emptyList();
      } else if (var2.get(0) instanceof KeyValue) {
         this.history = (List)var2.stream().map((var0) -> (KeyValue)var0).map((var0) -> (String)var0.getKey() + ": " + (String)var0.getValue()).collect(Collectors.toList());
      } else {
         this.history = (List)var2.stream().map((var0) -> (List)var0).map((var0) -> (String)var0.get(0) + ": " + (String)var0.get(1)).collect(Collectors.toList());
      }

   }

   public String getSummary() {
      return this.summary;
   }

   public String getSince() {
      return this.since;
   }

   public String getGroup() {
      return this.group;
   }

   public String getComplexity() {
      return this.complexity;
   }

   public List<String> getHistory() {
      return this.history;
   }
}
