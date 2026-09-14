package redis.clients.jedis.search;

import java.util.List;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.params.IParams;

public class FieldName implements IParams {
   private final String name;
   private String attribute;

   public FieldName(String var1) {
      this.name = var1;
   }

   public FieldName(String var1, String var2) {
      this.name = var1;
      this.attribute = var2;
   }

   public FieldName as(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Setting null as field attribute is not allowed.");
      } else if (this.attribute != null) {
         throw new IllegalStateException("Attribute for this field is already set.");
      } else {
         this.attribute = var1;
         return this;
      }
   }

   public final String getName() {
      return this.name;
   }

   public final String getAttribute() {
      return this.attribute;
   }

   public int addCommandArguments(List<Object> var1) {
      var1.add(this.name);
      if (this.attribute == null) {
         return 1;
      } else {
         var1.add(SearchProtocol.SearchKeyword.AS);
         var1.add(this.attribute);
         return 3;
      }
   }

   public int addCommandArguments(CommandArguments var1) {
      var1.add(this.name);
      if (this.attribute == null) {
         return 1;
      } else {
         var1.add(SearchProtocol.SearchKeyword.AS);
         var1.add(this.attribute);
         return 3;
      }
   }

   public void addParams(CommandArguments var1) {
      this.addCommandArguments(var1);
   }

   public String toString() {
      return this.attribute == null ? this.name : this.name + " AS " + this.attribute;
   }

   public static FieldName of(String var0) {
      return new FieldName(var0);
   }

   public static FieldName[] convert(String... var0) {
      if (var0 == null) {
         return null;
      } else {
         FieldName[] var1 = new FieldName[var0.length];

         for(int var2 = 0; var2 < var0.length; ++var2) {
            var1[var2] = of(var0[var2]);
         }

         return var1;
      }
   }
}
