package redis.clients.jedis;

public class Module {
   private final String name;
   private final int version;

   public Module(String var1, int var2) {
      this.name = var1;
      this.version = var2;
   }

   public String getName() {
      return this.name;
   }

   public int getVersion() {
      return this.version;
   }

   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Module)) {
         return false;
      } else {
         Module var2 = (Module)var1;
         if (this.version != var2.version) {
            return false;
         } else {
            boolean var10000;
            label44: {
               if (this.name != null) {
                  if (this.name.equals(var2.name)) {
                     break label44;
                  }
               } else if (var2.name == null) {
                  break label44;
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }
   }

   public int hashCode() {
      int var1 = this.name != null ? this.name.hashCode() : 0;
      var1 = 31 * var1 + this.version;
      return var1;
   }
}
