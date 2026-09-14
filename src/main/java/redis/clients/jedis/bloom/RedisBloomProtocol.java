package redis.clients.jedis.bloom;

import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.util.SafeEncoder;

public class RedisBloomProtocol {
   public static enum BloomFilterCommand implements ProtocolCommand {
      RESERVE("BF.RESERVE"),
      ADD("BF.ADD"),
      MADD("BF.MADD"),
      EXISTS("BF.EXISTS"),
      MEXISTS("BF.MEXISTS"),
      INSERT("BF.INSERT"),
      SCANDUMP("BF.SCANDUMP"),
      LOADCHUNK("BF.LOADCHUNK"),
      CARD("BF.CARD"),
      INFO("BF.INFO");

      private final byte[] raw;

      private BloomFilterCommand(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum CountMinSketchCommand implements ProtocolCommand {
      INITBYDIM("CMS.INITBYDIM"),
      INITBYPROB("CMS.INITBYPROB"),
      INCRBY("CMS.INCRBY"),
      QUERY("CMS.QUERY"),
      MERGE("CMS.MERGE"),
      INFO("CMS.INFO");

      private final byte[] raw;

      private CountMinSketchCommand(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum CuckooFilterCommand implements ProtocolCommand {
      RESERVE("CF.RESERVE"),
      ADD("CF.ADD"),
      ADDNX("CF.ADDNX"),
      INSERT("CF.INSERT"),
      INSERTNX("CF.INSERTNX"),
      EXISTS("CF.EXISTS"),
      MEXISTS("CF.MEXISTS"),
      DEL("CF.DEL"),
      COUNT("CF.COUNT"),
      SCANDUMP("CF.SCANDUMP"),
      LOADCHUNK("CF.LOADCHUNK"),
      INFO("CF.INFO");

      private final byte[] raw;

      private CuckooFilterCommand(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum RedisBloomKeyword implements Rawable {
      CAPACITY,
      ERROR,
      NOCREATE,
      EXPANSION,
      NONSCALING,
      BUCKETSIZE,
      MAXITERATIONS,
      ITEMS,
      WEIGHTS,
      COMPRESSION,
      OVERRIDE,
      WITHCOUNT;

      private final byte[] raw = SafeEncoder.encode(this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum TDigestCommand implements ProtocolCommand {
      CREATE,
      INFO,
      ADD,
      RESET,
      MERGE,
      CDF,
      QUANTILE,
      MIN,
      MAX,
      TRIMMED_MEAN,
      RANK,
      REVRANK,
      BYRANK,
      BYREVRANK;

      private final byte[] raw = SafeEncoder.encode("TDIGEST." + this.name());

      public byte[] getRaw() {
         return this.raw;
      }
   }

   public static enum TopKCommand implements ProtocolCommand {
      RESERVE("TOPK.RESERVE"),
      ADD("TOPK.ADD"),
      INCRBY("TOPK.INCRBY"),
      QUERY("TOPK.QUERY"),
      LIST("TOPK.LIST"),
      INFO("TOPK.INFO");

      private final byte[] raw;

      private TopKCommand(String var3) {
         this.raw = SafeEncoder.encode(var3);
      }

      public byte[] getRaw() {
         return this.raw;
      }
   }
}
