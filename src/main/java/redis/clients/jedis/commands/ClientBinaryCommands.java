package redis.clients.jedis.commands;

import redis.clients.jedis.args.ClientAttributeOption;
import redis.clients.jedis.args.ClientPauseMode;
import redis.clients.jedis.args.ClientType;
import redis.clients.jedis.args.UnblockType;
import redis.clients.jedis.params.ClientKillParams;

public interface ClientBinaryCommands {
   String clientKill(byte[] var1);

   String clientKill(String var1, int var2);

   long clientKill(ClientKillParams var1);

   byte[] clientGetnameBinary();

   byte[] clientListBinary();

   byte[] clientListBinary(ClientType var1);

   byte[] clientListBinary(long... var1);

   byte[] clientInfoBinary();

   String clientSetInfo(ClientAttributeOption var1, byte[] var2);

   String clientSetname(byte[] var1);

   long clientId();

   long clientUnblock(long var1);

   long clientUnblock(long var1, UnblockType var3);

   String clientPause(long var1);

   String clientPause(long var1, ClientPauseMode var3);

   String clientUnpause();

   String clientNoEvictOn();

   String clientNoEvictOff();
}
