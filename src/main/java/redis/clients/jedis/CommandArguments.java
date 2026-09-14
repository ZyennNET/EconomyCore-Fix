package redis.clients.jedis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import redis.clients.jedis.args.Rawable;
import redis.clients.jedis.args.RawableFactory;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.params.IParams;
import redis.clients.jedis.search.RediSearchUtil;

public class CommandArguments implements Iterable<Rawable> {
   private final ArrayList<Rawable> args;
   private boolean blocking;

   private CommandArguments() {
      throw new InstantiationError();
   }

   public CommandArguments(ProtocolCommand var1) {
      this.args = new ArrayList();
      this.args.add(var1);
   }

   public ProtocolCommand getCommand() {
      return (ProtocolCommand)this.args.get(0);
   }

   public CommandArguments add(Object var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null is not a valid argument.");
      } else {
         if (var1 instanceof Rawable) {
            this.args.add((Rawable)var1);
         } else if (var1 instanceof byte[]) {
            this.args.add(RawableFactory.from((byte[])var1));
         } else if (var1 instanceof Integer) {
            this.args.add(RawableFactory.from((Integer)var1));
         } else if (var1 instanceof Double) {
            this.args.add(RawableFactory.from((Double)var1));
         } else if (var1 instanceof Boolean) {
            this.args.add(RawableFactory.from((Boolean)var1 ? 1 : 0));
         } else if (var1 instanceof float[]) {
            this.args.add(RawableFactory.from(RediSearchUtil.toByteArray((float[])var1)));
         } else if (var1 instanceof String) {
            this.args.add(RawableFactory.from((String)var1));
         } else if (var1 instanceof GeoCoordinate) {
            GeoCoordinate var2 = (GeoCoordinate)var1;
            this.args.add(RawableFactory.from(var2.getLongitude() + "," + var2.getLatitude()));
         } else {
            this.args.add(RawableFactory.from(String.valueOf(var1)));
         }

         return this;
      }
   }

   public CommandArguments addObjects(Object... var1) {
      for(Object var5 : var1) {
         this.add(var5);
      }

      return this;
   }

   public CommandArguments addObjects(Collection var1) {
      var1.forEach((var1x) -> this.add(var1x));
      return this;
   }

   public CommandArguments key(Object var1) {
      if (var1 instanceof Rawable) {
         Rawable var2 = (Rawable)var1;
         this.processKey(var2.getRaw());
         this.args.add(var2);
      } else if (var1 instanceof byte[]) {
         byte[] var3 = (byte[])var1;
         this.processKey(var3);
         this.args.add(RawableFactory.from(var3));
      } else {
         if (!(var1 instanceof String)) {
            throw new IllegalArgumentException("\"" + var1.toString() + "\" is not a valid argument.");
         }

         String var4 = (String)var1;
         this.processKey(var4);
         this.args.add(RawableFactory.from(var4));
      }

      return this;
   }

   public final CommandArguments keys(Object... var1) {
      for(Object var5 : var1) {
         this.key(var5);
      }

      return this;
   }

   public final CommandArguments keys(Collection var1) {
      var1.forEach((var1x) -> this.key(var1x));
      return this;
   }

   public final CommandArguments addParams(IParams var1) {
      var1.addParams(this);
      return this;
   }

   protected CommandArguments processKey(byte[] var1) {
      return this;
   }

   protected final CommandArguments processKeys(byte[]... var1) {
      for(byte[] var5 : var1) {
         this.processKey(var5);
      }

      return this;
   }

   protected CommandArguments processKey(String var1) {
      return this;
   }

   protected final CommandArguments processKeys(String... var1) {
      for(String var5 : var1) {
         this.processKey(var5);
      }

      return this;
   }

   public int size() {
      return this.args.size();
   }

   public Iterator<Rawable> iterator() {
      return this.args.iterator();
   }

   public boolean isBlocking() {
      return this.blocking;
   }

   public CommandArguments blocking() {
      this.blocking = true;
      return this;
   }
}
