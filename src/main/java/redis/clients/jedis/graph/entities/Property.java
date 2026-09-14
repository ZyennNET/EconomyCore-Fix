package redis.clients.jedis.graph.entities;

import java.util.Objects;

@Deprecated
public class Property<T> {
   private final String name;
   private final T value;

   public Property(String var1, T var2) {
      this.name = var1;
      this.value = var2;
   }

   public String getName() {
      return this.name;
   }

   public T getValue() {
      return this.value;
   }

   private boolean valueEquals(Object var1, Object var2) {
      if (var1 instanceof Integer) {
         var1 = ((Integer)var1).longValue();
      }

      if (var2 instanceof Integer) {
         var2 = ((Integer)var2).longValue();
      }

      return Objects.equals(var1, var2);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Property)) {
         return false;
      } else {
         Property var2 = (Property)var1;
         return Objects.equals(this.name, var2.name) && this.valueEquals(this.value, var2.value);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.name, this.value});
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("Property{");
      var1.append("name='").append(this.name).append('\'');
      var1.append(", value=").append(this.value);
      var1.append('}');
      return var1.toString();
   }
}
