package redis.clients.jedis.params;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;

public class GeoAddParams implements IParams {
   private boolean nx = false;
   private boolean xx = false;
   private boolean ch = false;

   public static GeoAddParams geoAddParams() {
      return new GeoAddParams();
   }

   public GeoAddParams nx() {
      this.nx = true;
      return this;
   }

   public GeoAddParams xx() {
      this.xx = true;
      return this;
   }

   public GeoAddParams ch() {
      this.ch = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.nx) {
         var1.add(Protocol.Keyword.NX);
      } else if (this.xx) {
         var1.add(Protocol.Keyword.XX);
      }

      if (this.ch) {
         var1.add(Protocol.Keyword.CH);
      }

   }
}
