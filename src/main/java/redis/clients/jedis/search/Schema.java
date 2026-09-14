package redis.clients.jedis.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class Schema {
   public final List<Field> fields = new ArrayList();

   public static Schema from(Field... var0) {
      Schema var1 = new Schema();

      for(Field var5 : var0) {
         var1.addField(var5);
      }

      return var1;
   }

   public Schema addTextField(String var1, double var2) {
      this.fields.add(new TextField(var1, var2));
      return this;
   }

   public Schema addSortableTextField(String var1, double var2) {
      this.fields.add(new TextField(var1, var2, true));
      return this;
   }

   public Schema addGeoField(String var1) {
      this.fields.add(new Field(var1, Schema.FieldType.GEO, false));
      return this;
   }

   public Schema addNumericField(String var1) {
      this.fields.add(new Field(var1, Schema.FieldType.NUMERIC, false));
      return this;
   }

   public Schema addSortableNumericField(String var1) {
      this.fields.add(new Field(var1, Schema.FieldType.NUMERIC, true));
      return this;
   }

   public Schema addTagField(String var1) {
      this.fields.add(new TagField(var1));
      return this;
   }

   public Schema addTagField(String var1, String var2) {
      this.fields.add(new TagField(var1, var2));
      return this;
   }

   public Schema addTagField(String var1, boolean var2) {
      this.fields.add(new TagField(var1, var2, false));
      return this;
   }

   public Schema addTagField(String var1, String var2, boolean var3) {
      this.fields.add(new TagField(var1, var2, var3, false));
      return this;
   }

   public Schema addSortableTagField(String var1, String var2) {
      this.fields.add(new TagField(var1, var2, true));
      return this;
   }

   public Schema addSortableTagField(String var1, boolean var2) {
      this.fields.add(new TagField(var1, var2, true));
      return this;
   }

   public Schema addSortableTagField(String var1, String var2, boolean var3) {
      this.fields.add(new TagField(var1, var2, var3, true));
      return this;
   }

   public Schema addVectorField(String var1, VectorField.VectorAlgo var2, Map<String, Object> var3) {
      this.fields.add(new VectorField(var1, var2, var3));
      return this;
   }

   public Schema addFlatVectorField(String var1, Map<String, Object> var2) {
      this.fields.add(new VectorField(var1, Schema.VectorField.VectorAlgo.FLAT, var2));
      return this;
   }

   public Schema addHNSWVectorField(String var1, Map<String, Object> var2) {
      this.fields.add(new VectorField(var1, Schema.VectorField.VectorAlgo.HNSW, var2));
      return this;
   }

   public Schema addField(Field var1) {
      this.fields.add(var1);
      return this;
   }

   public Schema as(String var1) {
      ((Field)this.fields.get(this.fields.size() - 1)).as(var1);
      return this;
   }

   public String toString() {
      return "Schema{fields=" + this.fields + "}";
   }

   public static class Field implements IParams {
      protected final FieldName fieldName;
      protected final FieldType type;
      protected final boolean sortable;
      protected final boolean noIndex;

      public Field(String var1, FieldType var2) {
         this(var1, var2, false, false);
      }

      public Field(String var1, FieldType var2, boolean var3) {
         this(var1, var2, var3, false);
      }

      public Field(String var1, FieldType var2, boolean var3, boolean var4) {
         this(FieldName.of(var1), var2, var3, var4);
      }

      public Field(FieldName var1, FieldType var2) {
         this(var1, var2, false, false);
      }

      public Field(FieldName var1, FieldType var2, boolean var3, boolean var4) {
         this.fieldName = var1;
         this.type = var2;
         this.sortable = var3;
         this.noIndex = var4;
      }

      public void as(String var1) {
         this.fieldName.as(var1);
      }

      public final void addParams(CommandArguments var1) {
         this.fieldName.addParams(var1);
         var1.add(this.type.name());
         this.addTypeArgs(var1);
         if (this.sortable) {
            var1.add("SORTABLE");
         }

         if (this.noIndex) {
            var1.add("NOINDEX");
         }

      }

      protected void addTypeArgs(CommandArguments var1) {
      }

      public String toString() {
         return "Field{name='" + this.fieldName + "', type=" + this.type + ", sortable=" + this.sortable + ", noindex=" + this.noIndex + "}";
      }
   }

   public static enum FieldType {
      TAG,
      TEXT,
      GEO,
      NUMERIC,
      VECTOR;
   }

   public static class TagField extends Field {
      private final String separator;
      private final boolean caseSensitive;

      public TagField(String var1) {
         this(var1, (String)null);
      }

      public TagField(String var1, String var2) {
         this(var1, var2, false);
      }

      public TagField(String var1, boolean var2) {
         this((String)var1, (String)null, var2);
      }

      public TagField(String var1, String var2, boolean var3) {
         this(var1, var2, false, var3);
      }

      public TagField(String var1, boolean var2, boolean var3) {
         this((String)var1, (String)null, var2, var3);
      }

      public TagField(String var1, String var2, boolean var3, boolean var4) {
         super(var1, Schema.FieldType.TAG, var4);
         this.separator = var2;
         this.caseSensitive = var3;
      }

      public TagField(FieldName var1, String var2, boolean var3) {
         this(var1, var2, false, var3);
      }

      public TagField(FieldName var1, String var2, boolean var3, boolean var4) {
         super(var1, Schema.FieldType.TAG, var4, false);
         this.separator = var2;
         this.caseSensitive = var3;
      }

      public void addTypeArgs(CommandArguments var1) {
         if (this.separator != null) {
            var1.add("SEPARATOR");
            var1.add(this.separator);
         }

         if (this.caseSensitive) {
            var1.add("CASESENSITIVE");
         }

      }

      public String toString() {
         return "TagField{name='" + this.fieldName + "', type=" + this.type + ", sortable=" + this.sortable + ", noindex=" + this.noIndex + ", separator='" + this.separator + ", caseSensitive='" + this.caseSensitive + "'}";
      }
   }

   public static class TextField extends Field {
      private final double weight;
      private final boolean nostem;
      private final String phonetic;

      public TextField(String var1) {
         this(var1, (double)1.0F);
      }

      public TextField(FieldName var1) {
         this((FieldName)var1, (double)1.0F, false, false, false, (String)null);
      }

      public TextField(String var1, double var2) {
         this(var1, var2, false);
      }

      public TextField(String var1, double var2, boolean var4) {
         this(var1, var2, var4, false);
      }

      public TextField(String var1, double var2, boolean var4, boolean var5) {
         this(var1, var2, var4, var5, false);
      }

      public TextField(String var1, double var2, boolean var4, boolean var5, boolean var6) {
         this((String)var1, var2, var4, var5, var6, (String)null);
      }

      public TextField(String var1, double var2, boolean var4, boolean var5, boolean var6, String var7) {
         super(var1, Schema.FieldType.TEXT, var4, var6);
         this.weight = var2;
         this.nostem = var5;
         this.phonetic = var7;
      }

      public TextField(FieldName var1, double var2, boolean var4, boolean var5, boolean var6, String var7) {
         super(var1, Schema.FieldType.TEXT, var4, var6);
         this.weight = var2;
         this.nostem = var5;
         this.phonetic = var7;
      }

      protected void addTypeArgs(CommandArguments var1) {
         if (this.weight != (double)1.0F) {
            var1.add("WEIGHT");
            var1.add(Double.toString(this.weight));
         }

         if (this.nostem) {
            var1.add("NOSTEM");
         }

         if (this.phonetic != null) {
            var1.add("PHONETIC");
            var1.add(this.phonetic);
         }

      }

      public String toString() {
         return "TextField{name='" + this.fieldName + "', type=" + this.type + ", sortable=" + this.sortable + ", noindex=" + this.noIndex + ", weight=" + this.weight + ", nostem=" + this.nostem + ", phonetic='" + this.phonetic + "'}";
      }
   }

   public static class VectorField extends Field {
      private final VectorAlgo algorithm;
      private final Map<String, Object> attributes;

      public VectorField(String var1, VectorAlgo var2, Map<String, Object> var3) {
         super(var1, Schema.FieldType.VECTOR);
         this.algorithm = var2;
         this.attributes = var3;
      }

      public void addTypeArgs(CommandArguments var1) {
         var1.add(this.algorithm);
         var1.add(this.attributes.size() * 2);

         for(Map.Entry var3 : this.attributes.entrySet()) {
            var1.add(var3.getKey());
            var1.add(var3.getValue());
         }

      }

      public String toString() {
         return "VectorField{name='" + this.fieldName + "', type=" + this.type + ", algorithm=" + this.algorithm + ", attributes=" + this.attributes + "}";
      }

      public static enum VectorAlgo {
         FLAT,
         HNSW;
      }
   }
}
