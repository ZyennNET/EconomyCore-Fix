package redis.clients.jedis.json;

public class Path2 {
   public static final Path2 ROOT_PATH = new Path2("$");
   private final String str;

   public Path2(String var1) {
      if (var1 == null) {
         throw new NullPointerException("Path cannot be null.");
      } else if (var1.isEmpty()) {
         throw new IllegalArgumentException("Path cannot be empty.");
      } else {
         if (var1.charAt(0) == '$') {
            this.str = var1;
         } else if (var1.charAt(0) == '.') {
            this.str = '$' + var1;
         } else {
            this.str = "$." + var1;
         }

      }
   }

   public String toString() {
      return this.str;
   }

   public static Path2 of(String var0) {
      return new Path2(var0);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof Path2)) {
         return false;
      } else {
         return var1 == this ? true : this.toString().equals(((Path2)var1).toString());
      }
   }

   public int hashCode() {
      return this.str.hashCode();
   }
}
