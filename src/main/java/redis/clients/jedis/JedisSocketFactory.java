package redis.clients.jedis;

import java.net.Socket;
import redis.clients.jedis.exceptions.JedisConnectionException;

public interface JedisSocketFactory {
   Socket createSocket() throws JedisConnectionException;
}
