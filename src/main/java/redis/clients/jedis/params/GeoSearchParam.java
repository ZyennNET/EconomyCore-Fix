package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.GeoCoordinate;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.args.SortingOrder;

public class GeoSearchParam implements IParams {
   private boolean fromMember = false;
   private boolean fromLonLat = false;
   private String member;
   private GeoCoordinate coord;
   private boolean byRadius = false;
   private boolean byBox = false;
   private double radius;
   private double width;
   private double height;
   private GeoUnit unit;
   private boolean withCoord = false;
   private boolean withDist = false;
   private boolean withHash = false;
   private Integer count = null;
   private boolean any = false;
   private SortingOrder sortingOrder = null;

   public static GeoSearchParam geoSearchParam() {
      return new GeoSearchParam();
   }

   public GeoSearchParam fromMember(String var1) {
      this.fromMember = true;
      this.member = var1;
      return this;
   }

   public GeoSearchParam fromLonLat(double var1, double var3) {
      this.fromLonLat = true;
      this.coord = new GeoCoordinate(var1, var3);
      return this;
   }

   public GeoSearchParam fromLonLat(GeoCoordinate var1) {
      this.fromLonLat = true;
      this.coord = var1;
      return this;
   }

   public GeoSearchParam byRadius(double var1, GeoUnit var3) {
      this.byRadius = true;
      this.radius = var1;
      this.unit = var3;
      return this;
   }

   public GeoSearchParam byBox(double var1, double var3, GeoUnit var5) {
      this.byBox = true;
      this.width = var1;
      this.height = var3;
      this.unit = var5;
      return this;
   }

   public GeoSearchParam withCoord() {
      this.withCoord = true;
      return this;
   }

   public GeoSearchParam withDist() {
      this.withDist = true;
      return this;
   }

   public GeoSearchParam withHash() {
      this.withHash = true;
      return this;
   }

   public GeoSearchParam asc() {
      return this.sortingOrder(SortingOrder.ASC);
   }

   public GeoSearchParam desc() {
      return this.sortingOrder(SortingOrder.DESC);
   }

   public GeoSearchParam sortingOrder(SortingOrder var1) {
      this.sortingOrder = var1;
      return this;
   }

   public GeoSearchParam count(int var1) {
      this.count = var1;
      return this;
   }

   public GeoSearchParam count(int var1, boolean var2) {
      this.count = var1;
      this.any = true;
      return this;
   }

   public GeoSearchParam any() {
      if (this.count == null) {
         throw new IllegalArgumentException("COUNT must be set before ANY to be set");
      } else {
         this.any = true;
         return this;
      }
   }

   public void addParams(CommandArguments var1) {
      if (this.fromMember) {
         var1.add(Protocol.Keyword.FROMMEMBER).add(this.member);
      } else if (this.fromLonLat) {
         var1.add(Protocol.Keyword.FROMLONLAT).add(this.coord.getLongitude()).add(this.coord.getLatitude());
      }

      if (this.byRadius) {
         var1.add(Protocol.Keyword.BYRADIUS).add(this.radius);
      } else if (this.byBox) {
         var1.add(Protocol.Keyword.BYBOX).add(this.width).add(this.height);
      }

      var1.add(this.unit);
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
