package redis.clients.jedis.search;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class IndexDefinition implements IParams {
   private final Type type;
   private String[] prefixes;
   private String filter;
   private String languageField;
   private String language;
   private String scoreFiled;
   private double score;

   public IndexDefinition() {
      this((Type)null);
   }

   public IndexDefinition(Type var1) {
      this.score = (double)1.0F;
      this.type = var1;
   }

   public Type getType() {
      return this.type;
   }

   public String[] getPrefixes() {
      return this.prefixes;
   }

   public IndexDefinition setPrefixes(String... var1) {
      this.prefixes = var1;
      return this;
   }

   public String getFilter() {
      return this.filter;
   }

   public IndexDefinition setFilter(String var1) {
      this.filter = var1;
      return this;
   }

   public String getLanguageField() {
      return this.languageField;
   }

   public IndexDefinition setLanguageField(String var1) {
      this.languageField = var1;
      return this;
   }

   public String getLanguage() {
      return this.language;
   }

   public IndexDefinition setLanguage(String var1) {
      this.language = var1;
      return this;
   }

   public String getScoreFiled() {
      return this.scoreFiled;
   }

   public IndexDefinition setScoreFiled(String var1) {
      this.scoreFiled = var1;
      return this;
   }

   public double getScore() {
      return this.score;
   }

   public IndexDefinition setScore(double var1) {
      this.score = var1;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.type != null) {
         var1.add(SearchProtocol.SearchKeyword.ON.name());
         var1.add(this.type.name());
      }

      if (this.prefixes != null && this.prefixes.length > 0) {
         var1.add(SearchProtocol.SearchKeyword.PREFIX.name());
         var1.add(Integer.toString(this.prefixes.length));
         var1.addObjects(this.prefixes);
      }

      if (this.filter != null) {
         var1.add(SearchProtocol.SearchKeyword.FILTER.name());
         var1.add(this.filter);
      }

      if (this.languageField != null) {
         var1.add(SearchProtocol.SearchKeyword.LANGUAGE_FIELD.name());
         var1.add(this.languageField);
      }

      if (this.language != null) {
         var1.add(SearchProtocol.SearchKeyword.LANGUAGE.name());
         var1.add(this.language);
      }

      if (this.scoreFiled != null) {
         var1.add(SearchProtocol.SearchKeyword.SCORE_FIELD.name());
         var1.add(this.scoreFiled);
      }

      if (this.score != (double)1.0F) {
         var1.add(SearchProtocol.SearchKeyword.SCORE.name());
         var1.add(Double.toString(this.score));
      }

   }

   public static enum Type {
      HASH,
      JSON;
   }
}
