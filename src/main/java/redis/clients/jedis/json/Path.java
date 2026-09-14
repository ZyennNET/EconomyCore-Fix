package redis.clients.jedis.json;

@Deprecated
public class Path {
   public static final Path ROOT_PATH = new Path(".");
   private final String strPath;

   public Path(String var1) {
      this.strPath = var1;
   }

   public String toString() {
      return this.strPath;
   }

   public static Path of(String var0) {
      return new Path(var0);
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof Path)) {
         return false;
      } else {
         return var1 == this ? true : this.toString().equals(((Path)var1).toString());
      }
   }

   public int hashCode() {
      return this.strPath.hashCode();
   }
}
