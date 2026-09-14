package redis.clients.jedis.graph.entities;

import java.util.List;
import java.util.Objects;

@Deprecated
public final class Path {
   private final List<Node> nodes;
   private final List<Edge> edges;

   public Path(List<Node> var1, List<Edge> var2) {
      this.nodes = var1;
      this.edges = var2;
   }

   public List<Node> getNodes() {
      return this.nodes;
   }

   public List<Edge> getEdges() {
      return this.edges;
   }

   public int length() {
      return this.edges.size();
   }

   public int nodeCount() {
      return this.nodes.size();
   }

   public Node firstNode() {
      return (Node)this.nodes.get(0);
   }

   public Node lastNode() {
      return (Node)this.nodes.get(this.nodes.size() - 1);
   }

   public Node getNode(int var1) {
      return (Node)this.nodes.get(var1);
   }

   public Edge getEdge(int var1) {
      return (Edge)this.edges.get(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         Path var2 = (Path)var1;
         return Objects.equals(this.nodes, var2.nodes) && Objects.equals(this.edges, var2.edges);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.nodes, this.edges});
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("Path{");
      var1.append("nodes=").append(this.nodes);
      var1.append(", edges=").append(this.edges);
      var1.append('}');
      return var1.toString();
   }
}
