package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.SortingOrder;

public class GeoRadiusParam implements IParams {
   private boolean withCoord = false;
   private boolean withDist = false;
   private boolean withHash = false;
   private Integer count = null;
   private boolean any = false;
   private SortingOrder sortingOrder = null;

   public static GeoRadiusParam geoRadiusParam() {
      return new GeoRadiusParam();
   }

   public GeoRadiusParam withCoord() {
      this.withCoord = true;
      return this;
   }

   public GeoRadiusParam withDist() {
      this.withDist = true;
      return this;
   }

   public GeoRadiusParam withHash() {
      this.withHash = true;
      return this;
   }

   public GeoRadiusParam sortAscending() {
      return this.sortingOrder(SortingOrder.ASC);
   }

   public GeoRadiusParam sortDescending() {
      return this.sortingOrder(SortingOrder.DESC);
   }

   public GeoRadiusParam sortingOrder(SortingOrder var1) {
      this.sortingOrder = var1;
      return this;
   }

   public GeoRadiusParam count(int var1) {
      this.count = var1;
      return this;
   }

   public GeoRadiusParam count(int var1, boolean var2) {
      this.count = var1;
      this.any = var2;
      return this;
   }

   public GeoRadiusParam any() {
      if (this.count == null) {
         throw new IllegalArgumentException("COUNT must be set before ANY to be set");
      } else {
         this.any = true;
         return this;
      }
   }

   public void addParams(CommandArguments var1) {
      if (this.withCoord) {
         var1.add(Protocol.Keyword.WITHCOORD);
      }

      if (this.withDist) {
         var1.add(Protocol.Keyword.WITHDIST);
      }

      if (this.withHash) {
         var1.add(Protocol.Keyword.WITHHASH);
      }

      if (this.count != null) {
         var1.add(Protocol.Keyword.COUNT).add(this.count);
         if (this.any) {
            var1.add(Protocol.Keyword.ANY);
         }
      }

      if (this.sortingOrder != null) {
         var1.add(this.sortingOrder);
      }

   }
}
