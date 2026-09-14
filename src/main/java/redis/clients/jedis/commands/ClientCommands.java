package redis.clients.jedis.commands;

import redis.clients.jedis.args.ClientAttributeOption;
import redis.clients.jedis.args.ClientPauseMode;
import redis.clients.jedis.args.ClientType;
import redis.clients.jedis.args.UnblockType;
import redis.clients.jedis.params.ClientKillParams;

public interface ClientCommands {
   String clientKill(String var1);

   String clientKill(String var1, int var2);

   long clientKill(ClientKillParams var1);

   String clientGetname();

   String clientList();

   String clientList(ClientType var1);

   String clientList(long... var1);

   String clientInfo();

   String clientSetInfo(ClientAttributeOption var1, String var2);

   String clientSetname(String var1);

   long clientId();

   long clientUnblock(long var1);

   long clientUnblock(long var1, UnblockType var3);

   String clientPause(long var1);

   String clientPause(long var1, ClientPauseMode var3);

   String clientUnpause();

   String clientNoEvictOn();

   String clientNoEvictOff();

   String clientNoTouchOn();

   String clientNoTouchOff();
}
