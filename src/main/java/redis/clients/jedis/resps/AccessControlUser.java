package redis.clients.jedis.resps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class AccessControlUser {
   private final Map<String, Object> userInfo;
   private final List<String> flags;
   private final List<String> passwords;
   private final String commands;
   private final List<String> keysList;
   private final String keys;
   private final List<String> channelsList;
   private final String channels;
   private final List<String> selectors;

   public AccessControlUser(Map<String, Object> var1) {
      this.userInfo = var1;
      this.flags = (List)var1.get("flags");
      this.passwords = (List)var1.get("passwords");
      this.commands = (String)var1.get("commands");
      Object var2 = var1.get("keys");
      if (var2 == null) {
         this.keys = null;
         this.keysList = null;
      } else if (var2 instanceof List) {
         this.keysList = (List)var2;
         this.keys = joinStrings(this.keysList);
      } else {
         this.keys = (String)var2;
         this.keysList = Arrays.asList(this.keys.split(" "));
      }

      Object var3 = var1.get("channels");
      if (var3 == null) {
         this.channels = null;
         this.channelsList = null;
      } else if (var3 instanceof List) {
         this.channelsList = (List)var3;
         this.channels = joinStrings(this.channelsList);
      } else {
         this.channels = (String)var3;
         this.channelsList = Arrays.asList(this.channels.split(" "));
      }

      this.selectors = (List)var1.get("selectors");
   }

   private static String joinStrings(List<String> var0) {
      StringJoiner var1 = new StringJoiner(" ");
      var0.forEach((var1x) -> var1.add(var1x));
      return var1.toString();
   }

   public List<String> getFlags() {
      return this.flags;
   }

   @Deprecated
   public List<String> getPassword() {
      return this.passwords;
   }

   public List<String> getPasswords() {
      return this.passwords;
   }

   public String getCommands() {
      return this.commands;
   }

   public Map<String, Object> getUserInfo() {
      return this.userInfo;
   }

   public String getKeys() {
      return this.keys;
   }

   public List<String> getKeysList() {
      return this.keysList;
   }

   public List<String> getChannelsList() {
      return this.channelsList;
   }

   public String getChannels() {
      return this.channels;
   }

   public List<String> getSelectors() {
      return this.selectors;
   }

   public String toString() {
      return "AccessControlUser{flags=" + this.flags + ", passwords=" + this.passwords + ", commands='" + this.commands + "', keys='" + this.keys + "', channels='" + this.channels + "', selectors=" + this.selectors + "}";
   }
}
