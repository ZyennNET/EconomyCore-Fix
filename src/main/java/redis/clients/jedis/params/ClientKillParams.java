package redis.clients.jedis.params;

import java.util.ArrayList;
import redis.clients.jedis.CommandArguments;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.args.ClientType;
import redis.clients.jedis.util.KeyValue;

public class ClientKillParams implements IParams {
   private final ArrayList<KeyValue<Protocol.Keyword, Object>> params = new ArrayList();

   public static ClientKillParams clientKillParams() {
      return new ClientKillParams();
   }

   private ClientKillParams addParam(Protocol.Keyword var1, Object var2) {
      this.params.add(KeyValue.of(var1, var2));
      return this;
   }

   public ClientKillParams id(String var1) {
      return this.addParam(Protocol.Keyword.ID, var1);
   }

   public ClientKillParams id(byte[] var1) {
      return this.addParam(Protocol.Keyword.ID, var1);
   }

   public ClientKillParams type(ClientType var1) {
      return this.addParam(Protocol.Keyword.TYPE, var1);
   }

   public ClientKillParams addr(String var1) {
      return this.addParam(Protocol.Keyword.ADDR, var1);
   }

   public ClientKillParams addr(byte[] var1) {
      return this.addParam(Protocol.Keyword.ADDR, var1);
   }

   public ClientKillParams addr(String var1, int var2) {
      return this.addParam(Protocol.Keyword.ADDR, var1 + ':' + var2);
   }

   public ClientKillParams skipMe(SkipMe var1) {
      return this.addParam(Protocol.Keyword.SKIPME, var1);
   }

   public ClientKillParams user(String var1) {
      return this.addParam(Protocol.Keyword.USER, var1);
   }

   public ClientKillParams laddr(String var1) {
      return this.addParam(Protocol.Keyword.LADDR, var1);
   }

   public ClientKillParams laddr(String var1, int var2) {
      return this.addParam(Protocol.Keyword.LADDR, var1 + ':' + var2);
   }

   public void addParams(CommandArguments var1) {
      this.params.forEach((var1x) -> var1.add(var1x.getKey()).add(var1x.getValue()));
   }

   public static enum SkipMe {
      YES,
      NO;
   }
}
