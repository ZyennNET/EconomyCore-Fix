package redis.clients.jedis.search.aggr;

public class SortedField {
   private final String fieldName;
   private final SortOrder sortOrder;

   public SortedField(String var1, SortOrder var2) {
      this.fieldName = var1;
      this.sortOrder = var2;
   }

   public final String getOrder() {
      return this.sortOrder.toString();
   }

   public final String getField() {
      return this.fieldName;
   }

   public static SortedField asc(String var0) {
      return new SortedField(var0, SortedField.SortOrder.ASC);
   }

   public static SortedField desc(String var0) {
      return new SortedField(var0, SortedField.SortOrder.DESC);
   }

   public static enum SortOrder {
      ASC,
      DESC;
   }
}
