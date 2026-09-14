package redis.clients.jedis.resps;

import java.util.ArrayList;
import java.util.List;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.util.SafeEncoder;

public class Slowlog {
   private final long id;
   private final long timeStamp;
   private final long executionTime;
   private final List<String> args;
   private HostAndPort clientIpPort;
   private String clientName;
   private static final String COMMA = ",";

   private Slowlog(List<Object> var1) {
      this.id = (Long)var1.get(0);
      this.timeStamp = (Long)var1.get(1);
      this.executionTime = (Long)var1.get(2);
      this.args = BuilderFactory.STRING_LIST.build(var1.get(3));
      if (var1.size() != 4) {
         this.clientIpPort = HostAndPort.from(SafeEncoder.encode((byte[])var1.get(4)));
         this.clientName = SafeEncoder.encode((byte[])var1.get(5));
      }
   }

   public static List<Slowlog> from(List<Object> var0) {
      ArrayList var1 = new ArrayList(var0.size());

      for(Object var3 : var0) {
         List var4 = (List)var3;
         var1.add(new Slowlog(var4));
      }

      return var1;
   }

   public long getId() {
      return this.id;
   }

   public long getTimeStamp() {
      return this.timeStamp;
   }

   public long getExecutionTime() {
      return this.executionTime;
   }

   public List<String> getArgs() {
      return this.args;
   }

   public HostAndPort getClientIpPort() {
      return this.clientIpPort;
   }

   public String getClientName() {
      return this.clientName;
   }

   public String toString() {
      return this.id + "," + this.timeStamp + "," + this.executionTime + "," + this.args;
   }
}
