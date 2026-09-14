package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class FTCreateParams implements IParams {
   private IndexDataType dataType;
   private Collection<String> prefix;
   private String filter;
   private String language;
   private String languageField;
   private Double score;
   private String scoreField;
   private boolean maxTextFields;
   private boolean noOffsets;
   private Long temporary;
   private boolean noHL;
   private boolean noFields;
   private boolean noFreqs;
   private Collection<String> stopwords;
   private boolean skipInitialScan;

   public static FTCreateParams createParams() {
      return new FTCreateParams();
   }

   public FTCreateParams on(IndexDataType var1) {
      this.dataType = var1;
      return this;
   }

   public FTCreateParams prefix(String... var1) {
      if (this.prefix == null) {
         this.prefix = new ArrayList(var1.length);
      }

      Arrays.stream(var1).forEach((var1x) -> this.prefix.add(var1x));
      return this;
   }

   public FTCreateParams addPrefix(String var1) {
      if (this.prefix == null) {
         this.prefix = new ArrayList();
      }

      this.prefix.add(var1);
      return this;
   }

   public FTCreateParams filter(String var1) {
      this.filter = var1;
      return this;
   }

   public FTCreateParams language(String var1) {
      this.language = var1;
      return this;
   }

   public FTCreateParams languageField(String var1) {
      this.languageField = var1;
      return this;
   }

   public FTCreateParams score(double var1) {
      this.score = var1;
      return this;
   }

   public FTCreateParams scoreField(String var1) {
      this.scoreField = var1;
      return this;
   }

   public FTCreateParams maxTextFields() {
      this.maxTextFields = true;
      return this;
   }

   public FTCreateParams noOffsets() {
      this.noOffsets = true;
      return this;
   }

   public FTCreateParams temporary(long var1) {
      this.temporary = var1;
      return this;
   }

   public FTCreateParams noHL() {
      this.noHL = true;
      return this;
   }

   public FTCreateParams noHighlights() {
      return this.noHL();
   }

   public FTCreateParams noFields() {
      this.noFields = true;
      return this;
   }

   public FTCreateParams noFreqs() {
      this.noFreqs = true;
      return this;
   }

   public FTCreateParams stopwords(String... var1) {
      this.stopwords = Arrays.asList(var1);
      return this;
   }

   public FTCreateParams noStopwords() {
      this.stopwords = Collections.emptyList();
      return this;
   }

   public FTCreateParams skipInitialScan() {
      this.skipInitialScan = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.dataType != null) {
         var1.add(SearchProtocol.SearchKeyword.ON).add(this.dataType);
      }

      if (this.prefix != null) {
         var1.add(SearchProtocol.SearchKeyword.PREFIX).add(this.prefix.size()).addObjects(this.prefix);
      }

      if (this.filter != null) {
         var1.add(SearchProtocol.SearchKeyword.FILTER).add(this.filter);
      }

      if (this.language != null) {
         var1.add(SearchProtocol.SearchKeyword.LANGUAGE).add(this.language);
      }

      if (this.languageField != null) {
         var1.add(SearchProtocol.SearchKeyword.LANGUAGE_FIELD).add(this.languageField);
      }

      if (this.score != null) {
         var1.add(SearchProtocol.SearchKeyword.SCORE).add(this.score);
      }

      if (this.scoreField != null) {
         var1.add(SearchProtocol.SearchKeyword.SCORE_FIELD).add(this.scoreField);
      }

      if (this.maxTextFields) {
         var1.add(SearchProtocol.SearchKeyword.MAXTEXTFIELDS);
      }

      if (this.noOffsets) {
         var1.add(SearchProtocol.SearchKeyword.NOOFFSETS);
      }

      if (this.temporary != null) {
         var1.add(SearchProtocol.SearchKeyword.TEMPORARY).add(this.temporary);
      }

      if (this.noHL) {
         var1.add(SearchProtocol.SearchKeyword.NOHL);
      }

      if (this.noFields) {
         var1.add(SearchProtocol.SearchKeyword.NOFIELDS);
      }

      if (this.noFreqs) {
         var1.add(SearchProtocol.SearchKeyword.NOFREQS);
      }

      if (this.stopwords != null) {
         var1.add(SearchProtocol.SearchKeyword.STOPWORDS).add(this.stopwords.size());
         this.stopwords.forEach((var1x) -> var1.add(var1x));
      }

      if (this.skipInitialScan) {
         var1.add(SearchProtocol.SearchKeyword.SKIPINITIALSCAN);
      }

   }
}
