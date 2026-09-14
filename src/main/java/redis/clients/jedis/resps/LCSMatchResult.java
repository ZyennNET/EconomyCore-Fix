package redis.clients.jedis.resps;

import java.util.Collections;
import java.util.List;

public class LCSMatchResult {
   private String matchString;
   private List<MatchedPosition> matches;
   private long len;

   public LCSMatchResult(String var1) {
      this.matchString = var1;
   }

   public LCSMatchResult(long var1) {
      this.len = var1;
   }

   public LCSMatchResult(List<MatchedPosition> var1, long var2) {
      this.matches = var1;
      this.len = var2;
   }

   public LCSMatchResult(String var1, List<MatchedPosition> var2, long var3) {
      this.matchString = var1;
      this.matches = Collections.unmodifiableList(var2);
      this.len = var3;
   }

   public String getMatchString() {
      return this.matchString;
   }

   public List<MatchedPosition> getMatches() {
      return this.matches;
   }

   public long getLen() {
      return this.len;
   }

   public static class MatchedPosition {
      private final Position a;
      private final Position b;
      private final long matchLen;

      public MatchedPosition(Position var1, Position var2, long var3) {
         this.a = var1;
         this.b = var2;
         this.matchLen = var3;
      }

      public Position getA() {
         return this.a;
      }

      public Position getB() {
         return this.b;
      }

      public long getMatchLen() {
         return this.matchLen;
      }
   }

   public static class Position {
      private final long start;
      private final long end;

      public Position(long var1, long var3) {
         this.start = var1;
         this.end = var3;
      }

      public long getStart() {
         return this.start;
      }

      public long getEnd() {
         return this.end;
      }
   }
}
