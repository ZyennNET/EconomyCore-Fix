package redis.clients.jedis.json;

import com.google.gson.Gson;

public class DefaultGsonObjectMapper implements JsonObjectMapper {
   private final Gson gson = new Gson();

   public <T> T fromJson(String var1, Class<T> var2) {
      return (T)this.gson.fromJson(var1, var2);
   }

   public String toJson(Object var1) {
      return this.gson.toJson(var1);
   }
}
