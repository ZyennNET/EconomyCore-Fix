package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.util.KeyValue;

public class SearchResult {
   private final long totalResults;
   private final List<Document> documents;
   public static Builder<SearchResult> SEARCH_RESULT_BUILDER = new Builder<SearchResult>() {
      private static final String TOTAL_RESULTS_STR = "total_results";
      private static final String RESULTS_STR = "results";

      public SearchResult build(Object var1) {
         List var2 = (List)var1;
         long var3 = -1L;
         List var5 = null;

         for(KeyValue var7 : var2) {
            switch ((String)BuilderFactory.STRING.build(var7.getKey())) {
               case "total_results":
                  var3 = (Long)BuilderFactory.LONG.build(var7.getValue());
                  break;
               case "results":
                  Stream var10000 = ((List)var7.getValue()).stream();
                  Builder var10001 = Document.SEARCH_DOCUMENT;
                  var10001.getClass();
                  var5 = (List)var10000.map(var10001::build).collect(Collectors.toList());
            }
         }

         return new SearchResult(var3, var5);
      }
   };

   private SearchResult(long var1, List<Document> var3) {
      this.totalResults = var1;
      this.documents = var3;
   }

   public long getTotalResults() {
      return this.totalResults;
   }

   public List<Document> getDocuments() {
      return Collections.unmodifiableList(this.documents);
   }

   public String toString() {
      return this.getClass().getSimpleName() + "{Total results:" + this.totalResults + ", Documents:" + this.documents + "}";
   }

   public static class SearchResultBuilder extends Builder<SearchResult> {
      private final boolean hasContent;
      private final boolean hasScores;
      private final boolean decode;

      public SearchResultBuilder(boolean var1, boolean var2, boolean var3) {
         this.hasContent = var1;
         this.hasScores = var2;
         this.decode = var3;
      }

      public SearchResult build(Object var1) {
         List var2 = (List)var1;
         int var3 = 1;
         byte var4 = 0;
         int var5 = 1;
         if (this.hasScores) {
            ++var3;
            var4 = 1;
            ++var5;
         }

         if (this.hasContent) {
            ++var3;
         }

         long var6 = (Long)var2.get(0);
         ArrayList var8 = new ArrayList(var2.size() - 1);

         for(int var9 = 1; var9 < var2.size(); var9 += var3) {
            String var10 = BuilderFactory.STRING.build(var2.get(var9));
            double var11 = this.hasScores ? (Double)BuilderFactory.DOUBLE.build(var2.get(var9 + var4)) : (double)1.0F;
            List var13 = this.hasContent ? (List)var2.get(var9 + var5) : null;
            var8.add(Document.load(var10, var11, var13, this.decode));
         }

         return new SearchResult(var6, var8);
      }
   }
}
