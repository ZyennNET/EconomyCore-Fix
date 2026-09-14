package redis.clients.jedis.graph.entities;

import java.util.Objects;

@Deprecated
public class Edge extends GraphEntity {
   private String relationshipType;
   private long source;
   private long destination;

   public Edge() {
   }

   public Edge(int var1) {
      super(var1);
   }

   public String getRelationshipType() {
      return this.relationshipType;
   }

   public void setRelationshipType(String var1) {
      this.relationshipType = var1;
   }

   public long getSource() {
      return this.source;
   }

   public void setSource(long var1) {
      this.source = var1;
   }

   public long getDestination() {
      return this.destination;
   }

   public void setDestination(long var1) {
      this.destination = var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Edge)) {
         return false;
      } else if (!super.equals(var1)) {
         return false;
      } else {
         Edge var2 = (Edge)var1;
         return this.source == var2.source && this.destination == var2.destination && Objects.equals(this.relationshipType, var2.relationshipType);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{super.hashCode(), this.relationshipType, this.source, this.destination});
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("Edge{");
      var1.append("relationshipType='").append(this.relationshipType).append('\'');
      var1.append(", source=").append(this.source);
      var1.append(", destination=").append(this.destination);
      var1.append(", id=").append(this.id);
      var1.append(", propertyMap=").append(this.propertyMap);
      var1.append('}');
      return var1.toString();
   }
}
