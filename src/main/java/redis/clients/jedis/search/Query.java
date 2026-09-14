package redis.clients.jedis.search;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.util.LazyRawable;
import redis.clients.jedis.util.SafeEncoder;

public class Query implements IParams {
   private final List<Filter> _filters;
   private final String _queryString;
   private final Paging _paging;
   private boolean _verbatim;
   private boolean _noContent;
   private boolean _noStopwords;
   private boolean _withScores;
   private String _language;
   private String[] _fields;
   private String[] _keys;
   private String[] _returnFields;
   private FieldName[] returnFieldNames;
   private String[] highlightFields;
   private String[] summarizeFields;
   private String[] highlightTags;
   private String summarizeSeparator;
   private int summarizeNumFragments;
   private int summarizeFragmentLen;
   private String _sortBy;
   private boolean _sortAsc;
   private boolean wantsHighlight;
   private boolean wantsSummarize;
   private String _scorer;
   private Map<String, Object> _params;
   private Integer _dialect;
   private int _slop;
   private long _timeout;
   private boolean _inOrder;
   private String _expander;

   public Query() {
      this("*");
   }

   public Query(String var1) {
      this._filters = new LinkedList();
      this._paging = new Paging(0, 10);
      this._verbatim = false;
      this._noContent = false;
      this._noStopwords = false;
      this._withScores = false;
      this._language = null;
      this._fields = null;
      this._keys = null;
      this._returnFields = null;
      this.returnFieldNames = null;
      this.highlightFields = null;
      this.summarizeFields = null;
      this.highlightTags = null;
      this.summarizeSeparator = null;
      this.summarizeNumFragments = -1;
      this.summarizeFragmentLen = -1;
      this._sortBy = null;
      this._sortAsc = true;
      this.wantsHighlight = false;
      this.wantsSummarize = false;
      this._scorer = null;
      this._params = null;
      this._slop = -1;
      this._timeout = -1L;
      this._inOrder = false;
      this._expander = null;
      this._queryString = var1;
   }

   public void addParams(CommandArguments var1) {
      var1.add(SafeEncoder.encode(this._queryString));
      if (this._verbatim) {
         var1.add(SearchProtocol.SearchKeyword.VERBATIM.getRaw());
      }

      if (this._noContent) {
         var1.add(SearchProtocol.SearchKeyword.NOCONTENT.getRaw());
      }

      if (this._noStopwords) {
         var1.add(SearchProtocol.SearchKeyword.NOSTOPWORDS.getRaw());
      }

      if (this._withScores) {
         var1.add(SearchProtocol.SearchKeyword.WITHSCORES.getRaw());
      }

      if (this._language != null) {
         var1.add(SearchProtocol.SearchKeyword.LANGUAGE.getRaw());
         var1.add(SafeEncoder.encode(this._language));
      }

      if (this._scorer != null) {
         var1.add(SearchProtocol.SearchKeyword.SCORER.getRaw());
         var1.add(SafeEncoder.encode(this._scorer));
      }

      if (this._fields != null && this._fields.length > 0) {
         var1.add(SearchProtocol.SearchKeyword.INFIELDS.getRaw());
         var1.add(Protocol.toByteArray(this._fields.length));

         for(String var5 : this._fields) {
            var1.add(SafeEncoder.encode(var5));
         }
      }

      if (this._sortBy != null) {
         var1.add(SearchProtocol.SearchKeyword.SORTBY.getRaw());
         var1.add(SafeEncoder.encode(this._sortBy));
         var1.add((this._sortAsc ? SearchProtocol.SearchKeyword.ASC : SearchProtocol.SearchKeyword.DESC).getRaw());
      }

      if (this._paging.offset != 0 || this._paging.num != 10) {
         var1.add(SearchProtocol.SearchKeyword.LIMIT.getRaw()).add(Protocol.toByteArray(this._paging.offset)).add(Protocol.toByteArray(this._paging.num));
      }

      if (!this._filters.isEmpty()) {
         this._filters.forEach((var1x) -> var1x.addParams(var1));
      }

      if (this.wantsHighlight) {
         var1.add(SearchProtocol.SearchKeyword.HIGHLIGHT.getRaw());
         if (this.highlightFields != null) {
            var1.add(SearchProtocol.SearchKeyword.FIELDS.getRaw());
            var1.add(Protocol.toByteArray(this.highlightFields.length));

            for(String var28 : this.highlightFields) {
               var1.add(SafeEncoder.encode(var28));
            }
         }

         if (this.highlightTags != null) {
            var1.add(SearchProtocol.SearchKeyword.TAGS.getRaw());

            for(String var29 : this.highlightTags) {
               var1.add(SafeEncoder.encode(var29));
            }
         }
      }

      if (this.wantsSummarize) {
         var1.add(SearchProtocol.SearchKeyword.SUMMARIZE.getRaw());
         if (this.summarizeFields != null) {
            var1.add(SearchProtocol.SearchKeyword.FIELDS.getRaw());
            var1.add(Protocol.toByteArray(this.summarizeFields.length));

            for(String var30 : this.summarizeFields) {
               var1.add(SafeEncoder.encode(var30));
            }
         }

         if (this.summarizeNumFragments != -1) {
            var1.add(SearchProtocol.SearchKeyword.FRAGS.getRaw());
            var1.add(Protocol.toByteArray(this.summarizeNumFragments));
         }

         if (this.summarizeFragmentLen != -1) {
            var1.add(SearchProtocol.SearchKeyword.LEN.getRaw());
            var1.add(Protocol.toByteArray(this.summarizeFragmentLen));
         }

         if (this.summarizeSeparator != null) {
            var1.add(SearchProtocol.SearchKeyword.SEPARATOR.getRaw());
            var1.add(SafeEncoder.encode(this.summarizeSeparator));
         }
      }

      if (this._keys != null && this._keys.length > 0) {
         var1.add(SearchProtocol.SearchKeyword.INKEYS.getRaw());
         var1.add(Protocol.toByteArray(this._keys.length));

         for(String var31 : this._keys) {
            var1.add(SafeEncoder.encode(var31));
         }
      }

      if (this._returnFields != null && this._returnFields.length > 0) {
         var1.add(SearchProtocol.SearchKeyword.RETURN.getRaw());
         var1.add(Protocol.toByteArray(this._returnFields.length));

         for(String var33 : this._returnFields) {
            var1.add(SafeEncoder.encode(var33));
         }
      } else if (this.returnFieldNames != null && this.returnFieldNames.length > 0) {
         var1.add(SearchProtocol.SearchKeyword.RETURN.getRaw());
         LazyRawable var12 = new LazyRawable();
         var1.add(var12);
         int var19 = 0;

         for(FieldName var7 : this.returnFieldNames) {
            var19 += var7.addCommandArguments(var1);
         }

         var12.setRaw(Protocol.toByteArray(var19));
      }

      if (this._params != null && this._params.size() > 0) {
         var1.add(SearchProtocol.SearchKeyword.PARAMS.getRaw());
         var1.add(this._params.size() * 2);

         for(Map.Entry var21 : this._params.entrySet()) {
            var1.add(var21.getKey());
            var1.add(var21.getValue());
         }
      }

      if (this._dialect != null) {
         var1.add(SearchProtocol.SearchKeyword.DIALECT.getRaw());
         var1.add(this._dialect);
      }

      if (this._slop >= 0) {
         var1.add(SearchProtocol.SearchKeyword.SLOP.getRaw());
         var1.add(this._slop);
      }

      if (this._timeout >= 0L) {
         var1.add(SearchProtocol.SearchKeyword.TIMEOUT.getRaw());
         var1.add(this._timeout);
      }

      if (this._inOrder) {
         var1.add(SearchProtocol.SearchKeyword.INORDER.getRaw());
      }

      if (this._expander != null) {
         var1.add(SearchProtocol.SearchKeyword.EXPANDER.getRaw());
         var1.add(SafeEncoder.encode(this._expander));
      }

   }

   public Query limit(Integer var1, Integer var2) {
      this._paging.offset = var1;
      this._paging.num = var2;
      return this;
   }

   public Query addFilter(Filter var1) {
      this._filters.add(var1);
      return this;
   }

   public Query setVerbatim() {
      this._verbatim = true;
      return this;
   }

   public boolean getNoContent() {
      return this._noContent;
   }

   public Query setNoContent() {
      this._noContent = true;
      return this;
   }

   public Query setNoStopwords() {
      this._noStopwords = true;
      return this;
   }

   public boolean getWithScores() {
      return this._withScores;
   }

   public Query setWithScores() {
      this._withScores = true;
      return this;
   }

   public Query setLanguage(String var1) {
      this._language = var1;
      return this;
   }

   public Query setScorer(String var1) {
      this._scorer = var1;
      return this;
   }

   public Query limitFields(String... var1) {
      this._fields = var1;
      return this;
   }

   public Query limitKeys(String... var1) {
      this._keys = var1;
      return this;
   }

   public Query returnFields(String... var1) {
      this._returnFields = var1;
      this.returnFieldNames = null;
      return this;
   }

   public Query returnFields(FieldName... var1) {
      this.returnFieldNames = var1;
      this._returnFields = null;
      return this;
   }

   public Query highlightFields(HighlightTags var1, String... var2) {
      if (var2 == null || var2.length > 0) {
         this.highlightFields = var2;
      }

      if (var1 != null) {
         this.highlightTags = new String[]{var1.open, var1.close};
      } else {
         this.highlightTags = null;
      }

      this.wantsHighlight = true;
      return this;
   }

   public Query highlightFields(String... var1) {
      return this.highlightFields((HighlightTags)null, var1);
   }

   public Query summarizeFields(int var1, int var2, String var3, String... var4) {
      if (var4 == null || var4.length > 0) {
         this.summarizeFields = var4;
      }

      this.summarizeFragmentLen = var1;
      this.summarizeNumFragments = var2;
      this.summarizeSeparator = var3;
      this.wantsSummarize = true;
      return this;
   }

   public Query summarizeFields(String... var1) {
      return this.summarizeFields(-1, -1, (String)null, var1);
   }

   public Query setSortBy(String var1, boolean var2) {
      this._sortBy = var1;
      this._sortAsc = var2;
      return this;
   }

   public Query addParam(String var1, Object var2) {
      if (this._params == null) {
         this._params = new HashMap();
      }

      this._params.put(var1, var2);
      return this;
   }

   public Query dialect(int var1) {
      this._dialect = var1;
      return this;
   }

   public Query dialectOptional(int var1) {
      if (var1 != 0 && this._dialect == null) {
         this._dialect = var1;
      }

      return this;
   }

   public Query slop(int var1) {
      this._slop = var1;
      return this;
   }

   public Query timeout(long var1) {
      this._timeout = var1;
      return this;
   }

   public Query setInOrder() {
      this._inOrder = true;
      return this;
   }

   public Query setExpander(String var1) {
      this._expander = var1;
      return this;
   }

   public abstract static class Filter implements IParams {
      public final String property;

      public Filter(String var1) {
         this.property = var1;
      }
   }

   public static class GeoFilter extends Filter {
      public static final String KILOMETERS = "km";
      public static final String METERS = "m";
      public static final String FEET = "ft";
      public static final String MILES = "mi";
      private final double lon;
      private final double lat;
      private final double radius;
      private final String unit;

      public GeoFilter(String var1, double var2, double var4, double var6, String var8) {
         super(var1);
         this.lon = var2;
         this.lat = var4;
         this.radius = var6;
         this.unit = var8;
      }

      public void addParams(CommandArguments var1) {
         var1.add(SearchProtocol.SearchKeyword.GEOFILTER.getRaw());
         var1.add(SafeEncoder.encode(this.property));
         var1.add(Protocol.toByteArray(this.lon));
         var1.add(Protocol.toByteArray(this.lat));
         var1.add(Protocol.toByteArray(this.radius));
         var1.add(SafeEncoder.encode(this.unit));
      }
   }

   public static class HighlightTags {
      private final String open;
      private final String close;

      public HighlightTags(String var1, String var2) {
         this.open = var1;
         this.close = var2;
      }
   }

   public static class NumericFilter extends Filter {
      private final double min;
      private final boolean exclusiveMin;
      private final double max;
      private final boolean exclusiveMax;

      public NumericFilter(String var1, double var2, boolean var4, double var5, boolean var7) {
         super(var1);
         this.min = var2;
         this.max = var5;
         this.exclusiveMax = var7;
         this.exclusiveMin = var4;
      }

      public NumericFilter(String var1, double var2, double var4) {
         this(var1, var2, false, var4, false);
      }

      private byte[] formatNum(double var1, boolean var3) {
         return var3 ? SafeEncoder.encode("(" + var1) : Protocol.toByteArray(var1);
      }

      public void addParams(CommandArguments var1) {
         var1.add(SearchProtocol.SearchKeyword.FILTER.getRaw());
         var1.add(SafeEncoder.encode(this.property));
         var1.add(this.formatNum(this.min, this.exclusiveMin));
         var1.add(this.formatNum(this.max, this.exclusiveMax));
      }
   }

   public static class Paging {
      int offset;
      int num;

      public Paging(int var1, int var2) {
         this.offset = var1;
         this.num = var2;
      }
   }
}
