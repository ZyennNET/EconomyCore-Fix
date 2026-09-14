package redis.clients.jedis.graph.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Deprecated
public abstract class GraphEntity {
   protected long id;
   protected final Map<String, Property<?>> propertyMap;

   public GraphEntity() {
      this.propertyMap = new HashMap();
   }

   public GraphEntity(int var1) {
      this.propertyMap = new HashMap(var1);
   }

   public long getId() {
      return this.id;
   }

   public void setId(long var1) {
      this.id = var1;
   }

   public void addProperty(String var1, Object var2) {
      this.addProperty(new Property(var1, var2));
   }

   public Set<String> getEntityPropertyNames() {
      return this.propertyMap.keySet();
   }

   public void addProperty(Property var1) {
      this.propertyMap.put(var1.getName(), var1);
   }

   public int getNumberOfProperties() {
      return this.propertyMap.size();
   }

   public Property getProperty(String var1) {
      return (Property)this.propertyMap.get(var1);
   }

   public void removeProperty(String var1) {
      this.propertyMap.remove(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof GraphEntity)) {
         return false;
      } else {
         GraphEntity var2 = (GraphEntity)var1;
         return this.id == var2.id && Objects.equals(this.propertyMap, var2.propertyMap);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.propertyMap});
   }

   public abstract String toString();
}
