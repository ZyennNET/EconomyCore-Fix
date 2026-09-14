package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.util.KeyValue;

public class FTSpellCheckParams implements IParams {
   private Collection<Map.Entry<String, Rawable>> terms;
   private Integer distance;
   private Integer dialect;

   public static FTSpellCheckParams spellCheckParams() {
      return new FTSpellCheckParams();
   }

   public FTSpellCheckParams includeTerm(String var1) {
      return this.addTerm(var1, SearchProtocol.SearchKeyword.INCLUDE);
   }

   public FTSpellCheckParams excludeTerm(String var1) {
      return this.addTerm(var1, SearchProtocol.SearchKeyword.EXCLUDE);
   }

   private FTSpellCheckParams addTerm(String var1, Rawable var2) {
      if (this.terms == null) {
         this.terms = new ArrayList();
      }

      this.terms.add(KeyValue.of(var1, var2));
      return this;
   }

   public FTSpellCheckParams distance(int var1) {
      this.distance = var1;
      return this;
   }

   public FTSpellCheckParams dialect(int var1) {
      this.dialect = var1;
      return this;
   }

   public FTSpellCheckParams dialectOptional(int var1) {
      if (var1 != 0 && this.dialect == null) {
         this.dialect = var1;
      }

      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.terms != null) {
         this.terms.forEach((var1x) -> var1.add(SearchProtocol.SearchKeyword.TERMS).add(var1x.getValue()).add(var1x.getKey()));
      }

      if (this.distance != null) {
         var1.add(SearchProtocol.SearchKeyword.DISTANCE).add(this.distance);
      }

      if (this.dialect != null) {
         var1.add(SearchProtocol.SearchKeyword.DIALECT).add(this.dialect);
      }

   }
}
