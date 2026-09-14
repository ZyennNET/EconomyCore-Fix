package redis.clients.jedis.graph.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Deprecated
public class Node extends GraphEntity {
   private final List<String> labels;

   public Node() {
      this.labels = new ArrayList();
   }

   public Node(int var1, int var2) {
      super(var2);
      this.labels = new ArrayList(var1);
   }

   public void addLabel(String var1) {
      this.labels.add(var1);
   }

   public void removeLabel(String var1) {
      this.labels.remove(var1);
   }

   public String getLabel(int var1) {
      return (String)this.labels.get(var1);
   }

   public int getNumberOfLabels() {
      return this.labels.size();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Node)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         Node var2 = (Node)var1;
         return Objects.equals(this.labels, var2.labels);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{super.hashCode(), this.labels});
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("Node{");
      var1.append("labels=").append(this.labels);
      var1.append(", id=").append(this.id);
      var1.append(", propertyMap=").append(this.propertyMap);
      var1.append('}');
      return var1.toString();
   }
}
