package redis.clients.jedis.bloom;

import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.params.IParams;

public class TDigestMergeParams implements IParams {
   private Integer compression;
   private boolean override = false;

   public static TDigestMergeParams mergeParams() {
      return new TDigestMergeParams();
   }

   public TDigestMergeParams compression(int var1) {
      this.compression = var1;
      return this;
   }

   public TDigestMergeParams override() {
      this.override = true;
      return this;
   }

   public void addParams(CommandArguments var1) {
      if (this.compression != null) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.COMPRESSION).add(Protocol.toByteArray(this.compression));
      }

      if (this.override) {
         var1.add(RedisBloomProtocol.RedisBloomKeyword.OVERRIDE);
      }

   }
}
