package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class IndexOptions implements IParams {
   public static final int USE_TERM_OFFSETS = 1;
   public static final int KEEP_FIELD_FLAGS = 2;
   public static final int KEEP_TERM_FREQUENCIES = 8;
   public static final int DEFAULT_FLAGS = 11;
   private final int flags;
   private List<String> stopwords;
   private long expire = 0L;
   private IndexDefinition definition;

   public IndexOptions(int var1) {
      this.flags = var1;
   }

   public static IndexOptions defaultOptions() {
      return new IndexOptions(11);
   }

   public IndexOptions setStopwords(String... var1) {
      this.stopwords = Arrays.asList(var1);
      return this;
   }

   public IndexOptions setNoStopwords() {
      this.stopwords = new ArrayList(0);
      return this;
   }

   public IndexOptions setTemporary(long var1) {
      this.expire = var1;
      return this;
   }

   public IndexDefinition getDefinition() {
      return this.definition;
   }

   public IndexOptions setDefinition(IndexDefinition var1) {
      this.definition = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.definition != null) {
         this.definition.addParams(var1);
      }

      if ((this.flags & 1) == 0) {
         var1.add(SearchProtocol.SearchKeyword.NOOFFSETS.name());
      }

      if ((this.flags & 2) == 0) {
         var1.add(SearchProtocol.SearchKeyword.NOFIELDS.name());
      }

      if ((this.flags & 8) == 0) {
         var1.add(SearchProtocol.SearchKeyword.NOFREQS.name());
      }

      if (this.expire > 0L) {
         var1.add(SearchProtocol.SearchKeyword.TEMPORARY.name());
         var1.add(Long.toString(this.expire));
      }

      if (this.stopwords != null) {
         var1.add(SearchProtocol.SearchKeyword.STOPWORDS.name());
         var1.add(Integer.toString(this.stopwords.size()));
         if (!this.stopwords.isEmpty()) {
            var1.addObjects((Collection)this.stopwords);
         }
      }

   }
}
