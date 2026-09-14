package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.args.SortingOrder;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.util.LazyRawable;

public class FTSearchParams implements IParams {
   private boolean noContent = false;
   private boolean verbatim = false;
   private boolean noStopwords = false;
   private boolean withScores = false;
   private final List<IParams> filters = new LinkedList();
   private Collection<String> inKeys;
   private Collection<String> inFields;
   private Collection<String> returnFields;
   private Collection<FieldName> returnFieldNames;
   private boolean summarize;
   private SummarizeParams summarizeParams;
   private boolean highlight;
   private HighlightParams highlightParams;
   private Integer slop;
   private Long timeout;
   private boolean inOrder;
   private String language;
   private String expander;
   private String scorer;
   private String sortBy;
   private SortingOrder sortOrder;
   private int[] limit;
   private Map<String, Object> params;
   private Integer dialect;

   public static FTSearchParams searchParams() {
      return new FTSearchParams();
   }

   public void addParams(CommandArguments var1) {
      if (this.noContent) {
         var1.add(SearchProtocol.SearchKeyword.NOCONTENT);
      }

      if (this.verbatim) {
         var1.add(SearchProtocol.SearchKeyword.VERBATIM);
      }

      if (this.noStopwords) {
         var1.add(SearchProtocol.SearchKeyword.NOSTOPWORDS);
      }

      if (this.withScores) {
         var1.add(SearchProtocol.SearchKeyword.WITHSCORES);
      }

      if (!this.filters.isEmpty()) {
         this.filters.forEach((var1x) -> var1x.addParams(var1));
      }

      if (this.inKeys != null && !this.inKeys.isEmpty()) {
         var1.add(SearchProtocol.SearchKeyword.INKEYS).add(this.inKeys.size()).addObjects(this.inKeys);
      }

      if (this.inFields != null && !this.inFields.isEmpty()) {
         var1.add(SearchProtocol.SearchKeyword.INFIELDS).add(this.inFields.size()).addObjects(this.inFields);
      }

      if (this.returnFieldNames != null && !this.returnFieldNames.isEmpty()) {
         var1.add(SearchProtocol.SearchKeyword.RETURN);
         LazyRawable var2 = new LazyRawable();
         var1.add(var2);
         int var3 = 0;

         for(FieldName var5 : this.returnFieldNames) {
            var3 += var5.addCommandArguments(var1);
         }

         var2.setRaw(Protocol.toByteArray(var3));
      } else if (this.returnFields != null && !this.returnFields.isEmpty()) {
         var1.add(SearchProtocol.SearchKeyword.RETURN).add(this.returnFields.size()).addObjects(this.returnFields);
      }

      if (this.summarizeParams != null) {
         var1.addParams(this.summarizeParams);
      } else if (this.summarize) {
         var1.add(SearchProtocol.SearchKeyword.SUMMARIZE);
      }

      if (this.highlightParams != null) {
         var1.addParams(this.highlightParams);
      } else if (this.highlight) {
         var1.add(SearchProtocol.SearchKeyword.HIGHLIGHT);
      }

      if (this.slop != null) {
         var1.add(SearchProtocol.SearchKeyword.SLOP).add(this.slop);
      }

      if (this.timeout != null) {
         var1.add(SearchProtocol.SearchKeyword.TIMEOUT).add(this.timeout);
      }

      if (this.inOrder) {
         var1.add(SearchProtocol.SearchKeyword.INORDER);
      }

      if (this.language != null) {
         var1.add(SearchProtocol.SearchKeyword.LANGUAGE).add(this.language);
      }

      if (this.expander != null) {
         var1.add(SearchProtocol.SearchKeyword.EXPANDER).add(this.expander);
      }

      if (this.scorer != null) {
         var1.add(SearchProtocol.SearchKeyword.SCORER).add(this.scorer);
      }

      if (this.sortBy != null) {
         var1.add(SearchProtocol.SearchKeyword.SORTBY).add(this.sortBy);
         if (this.sortOrder != null) {
            var1.add(this.sortOrder);
         }
      }

      if (this.limit != null) {
         var1.add(SearchProtocol.SearchKeyword.LIMIT).add(this.limit[0]).add(this.limit[1]);
      }

      if (this.params != null && !this.params.isEmpty()) {
         var1.add(SearchProtocol.SearchKeyword.PARAMS).add(this.params.size() * 2);
         this.params.entrySet().forEach((var1x) -> var1.add(var1x.getKey()).add(var1x.getValue()));
      }

      if (this.dialect != null) {
         var1.add(SearchProtocol.SearchKeyword.DIALECT).add(this.dialect);
      }

   }

   public FTSearchParams noContent() {
      this.noContent = true;
      return this;
   }

   public FTSearchParams verbatim() {
      this.verbatim = true;
      return this;
   }

   public FTSearchParams noStopwords() {
      this.noStopwords = true;
      return this;
   }

   public FTSearchParams withScores() {
      this.withScores = true;
      return this;
   }

   public FTSearchParams filter(String var1, double var2, double var4) {
      return this.filter(new NumericFilter(var1, var2, var4));
   }

   public FTSearchParams filter(String var1, double var2, boolean var4, double var5, boolean var7) {
      return this.filter(new NumericFilter(var1, var2, var4, var5, var7));
   }

   public FTSearchParams filter(NumericFilter var1) {
      this.filters.add(var1);
      return this;
   }

   public FTSearchParams geoFilter(String var1, double var2, double var4, double var6, GeoUnit var8) {
      return this.geoFilter(new GeoFilter(var1, var2, var4, var6, var8));
   }

   public FTSearchParams geoFilter(GeoFilter var1) {
      this.filters.add(var1);
      return this;
   }

   public FTSearchParams inKeys(String... var1) {
      return this.inKeys((Collection)Arrays.asList(var1));
   }

   public FTSearchParams inKeys(Collection<String> var1) {
      this.inKeys = var1;
      return this;
   }

   public FTSearchParams inFields(String... var1) {
      return this.inFields((Collection)Arrays.asList(var1));
   }

   public FTSearchParams inFields(Collection<String> var1) {
      if (this.inFields == null) {
         this.inFields = new ArrayList(var1);
      } else {
         this.inFields.addAll(var1);
      }

      return this;
   }

   public FTSearchParams returnFields(String... var1) {
      if (this.returnFieldNames != null) {
         Arrays.stream(var1).forEach((var1x) -> this.returnFieldNames.add(FieldName.of(var1x)));
      } else {
         if (this.returnFields == null) {
            this.returnFields = new ArrayList();
         }

         Arrays.stream(var1).forEach((var1x) -> this.returnFields.add(var1x));
      }

      return this;
   }

   public FTSearchParams returnField(FieldName var1) {
      this.initReturnFieldNames();
      this.returnFieldNames.add(var1);
      return this;
   }

   public FTSearchParams returnFields(FieldName... var1) {
      return this.returnFields((Collection)Arrays.asList(var1));
   }

   public FTSearchParams returnFields(Collection<FieldName> var1) {
      this.initReturnFieldNames();
      this.returnFieldNames.addAll(var1);
      return this;
   }

   private void initReturnFieldNames() {
      if (this.returnFieldNames == null) {
         this.returnFieldNames = new ArrayList();
      }

      if (this.returnFields != null) {
         this.returnFields.forEach((var1) -> this.returnFieldNames.add(FieldName.of(var1)));
         this.returnFields = null;
      }

   }

   public FTSearchParams summarize() {
      this.summarize = true;
      return this;
   }

   public FTSearchParams summarize(SummarizeParams var1) {
      this.summarizeParams = var1;
      return this;
   }

   public FTSearchParams highlight() {
      this.highlight = true;
      return this;
   }

   public FTSearchParams highlight(HighlightParams var1) {
      this.highlightParams = var1;
      return this;
   }

   public FTSearchParams scorer(String var1) {
      this.scorer = var1;
      return this;
   }

   public FTSearchParams slop(int var1) {
      this.slop = var1;
      return this;
   }

   public FTSearchParams timeout(long var1) {
      this.timeout = var1;
      return this;
   }

   public FTSearchParams inOrder() {
      this.inOrder = true;
      return this;
   }

   public FTSearchParams language(String var1) {
      this.language = var1;
      return this;
   }

   public FTSearchParams sortBy(String var1, SortingOrder var2) {
      this.sortBy = var1;
      this.sortOrder = var2;
      return this;
   }

   public FTSearchParams limit(int var1, int var2) {
      this.limit = new int[]{var1, var2};
      return this;
   }

   public FTSearchParams addParam(String var1, Object var2) {
      if (this.params == null) {
         this.params = new HashMap();
      }

      this.params.put(var1, var2);
      return this;
   }

   public FTSearchParams params(Map<String, Object> var1) {
      if (this.params == null) {
         this.params = new HashMap(var1);
      } else {
         this.params.putAll(this.params);
      }

      return this;
   }

   public FTSearchParams dialect(int var1) {
      this.dialect = var1;
      return this;
   }

   public FTSearchParams dialectOptional(int var1) {
      if (var1 != 0 && this.dialect == null) {
         this.dialect = var1;
      }

      return this;
   }

   public boolean getNoContent() {
      return this.noContent;
   }

   public boolean getWithScores() {
      return this.withScores;
   }

   public static SummarizeParams summarizeParams() {
      return new SummarizeParams();
   }

   public static HighlightParams highlightParams() {
      return new HighlightParams();
   }

   public static class GeoFilter implements IParams {
      private final String field;
      private final double lon;
      private final double lat;
      private final double radius;
      private final GeoUnit unit;

      public GeoFilter(String var1, double var2, double var4, double var6, GeoUnit var8) {
         this.field = var1;
         this.lon = var2;
         this.lat = var4;
         this.radius = var6;
         this.unit = var8;
      }

      public void addParams(CommandArguments var1) {
         var1.add(SearchProtocol.SearchKeyword.GEOFILTER).add(this.field).add(this.lon).add(this.lat).add(this.radius).add(this.unit);
      }
   }

   public static class HighlightParams implements IParams {
      private Collection<String> fields;
      private String[] tags;

      public HighlightParams fields(String var1) {
         return this.fields((Collection)Arrays.asList(var1));
      }

      public HighlightParams fields(Collection<String> var1) {
         this.fields = var1;
         return this;
      }

      public HighlightParams tags(String var1, String var2) {
         this.tags = new String[]{var1, var2};
         return this;
      }

      public void addParams(CommandArguments var1) {
         var1.add(SearchProtocol.SearchKeyword.HIGHLIGHT);
         if (this.fields != null) {
            var1.add(SearchProtocol.SearchKeyword.FIELDS).add(this.fields.size()).addObjects(this.fields);
         }

         if (this.tags != null) {
            var1.add(SearchProtocol.SearchKeyword.TAGS).add(this.tags[0]).add(this.tags[1]);
         }

      }
   }

   public static class NumericFilter implements IParams {
      private final String field;
      private final double min;
      private final boolean exclusiveMin;
      private final double max;
      private final boolean exclusiveMax;

      public NumericFilter(String var1, double var2, double var4) {
         this(var1, var2, false, var4, false);
      }

      public NumericFilter(String var1, double var2, boolean var4, double var5, boolean var7) {
         this.field = var1;
         this.min = var2;
         this.max = var5;
         this.exclusiveMax = var7;
         this.exclusiveMin = var4;
      }

      public void addParams(CommandArguments var1) {
         var1.add(SearchProtocol.SearchKeyword.FILTER).add(this.field).add(this.formatNum(this.min, this.exclusiveMin)).add(this.formatNum(this.max, this.exclusiveMax));
      }

      private Object formatNum(double var1, boolean var3) {
         return var3 ? "(" + var1 : Protocol.toByteArray(var1);
      }
   }

   public static class SummarizeParams implements IParams {
      private Collection<String> fields;
      private Integer fragsNum;
      private Integer fragSize;
      private String separator;

      public SummarizeParams fields(String... var1) {
         return this.fields((Collection)Arrays.asList(var1));
      }

      public SummarizeParams fields(Collection<String> var1) {
         this.fields = var1;
         return this;
      }

      public SummarizeParams fragsNum(int var1) {
         this.fragsNum = var1;
         return this;
      }

      public SummarizeParams fragSize(int var1) {
         this.fragSize = var1;
         return this;
      }

      public SummarizeParams separator(String var1) {
         this.separator = var1;
         return this;
      }

      public void addParams(CommandArguments var1) {
         var1.add(SearchProtocol.SearchKeyword.SUMMARIZE);
         if (this.fields != null) {
            var1.add(SearchProtocol.SearchKeyword.FIELDS).add(this.fields.size()).addObjects(this.fields);
         }

         if (this.fragsNum != null) {
            var1.add(SearchProtocol.SearchKeyword.FRAGS).add(this.fragsNum);
         }

         if (this.fragSize != null) {
            var1.add(SearchProtocol.SearchKeyword.LEN).add(this.fragSize);
         }

         if (this.separator != null) {
            var1.add(SearchProtocol.SearchKeyword.SEPARATOR).add(this.separator);
         }

      }
   }
}
