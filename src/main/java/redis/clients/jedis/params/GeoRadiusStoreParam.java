package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class GeoRadiusStoreParam implements IParams {
   private boolean store = false;
   private boolean storeDist = false;
   private String key;

   public static GeoRadiusStoreParam geoRadiusStoreParam() {
      return new GeoRadiusStoreParam();
   }

   public GeoRadiusStoreParam store(String var1) {
      if (var1 != null) {
         this.store = true;
         this.key = var1;
      }

      return this;
   }

   public GeoRadiusStoreParam storeDist(String var1) {
      if (var1 != null) {
         this.storeDist = true;
         this.key = var1;
      }

      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.storeDist) {
         var1.add(Protocol.Keyword.STOREDIST).key(this.key);
      } else {
         if (!this.store) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " must has store or storedist option");
         }

         var1.add(Protocol.Keyword.STORE).key(this.key);
      }

   }
}
