package redis.clients.jedis.graph;

@Deprecated
public interface ResultSet extends Iterable<Record> {
   int size();

   Header getHeader();

   Statistics getStatistics();

   public static enum ColumnType {
      UNKNOWN,
      SCALAR,
      NODE,
      RELATION;
   }
}
