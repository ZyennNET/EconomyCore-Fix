package redis.clients.jedis;

import java.util.Arrays;
import java.util.HashSet;
import redis.clients.jedis.exceptions.JedisValidationException;

public final class ClientSetInfoConfig {
   private final boolean disabled;
   private final String libNameSuffix;
   private static final HashSet<Character> BRACES = new HashSet(Arrays.asList('(', ')', '[', ']', '{', '}'));
   public static final ClientSetInfoConfig DEFAULT = new ClientSetInfoConfig();
   public static final ClientSetInfoConfig DISABLED = new ClientSetInfoConfig(true);

   public ClientSetInfoConfig() {
      this(false, (String)null);
   }

   public ClientSetInfoConfig(boolean var1) {
      this(var1, (String)null);
   }

   public ClientSetInfoConfig(String var1) {
      this(false, var1);
   }

   private ClientSetInfoConfig(boolean var1, String var2) {
      this.disabled = var1;
      this.libNameSuffix = validateLibNameSuffix(var2);
   }

   private static String validateLibNameSuffix(String var0) {
      if (var0 != null && !var0.trim().isEmpty()) {
         for(int var1 = 0; var1 < var0.length(); ++var1) {
            char var2 = var0.charAt(var1);
            if (var2 < ' ' || var2 > '~' || BRACES.contains(var2)) {
               throw new JedisValidationException("lib-name suffix cannot contain braces, newlines or special characters.");
            }
         }

         return var0.replaceAll("\\s", "-");
      } else {
         return null;
      }
   }

   public final boolean isDisabled() {
      return this.disabled;
   }

   public final String getLibNameSuffix() {
      return this.libNameSuffix;
   }

   public static ClientSetInfoConfig withLibNameSuffix(String var0) {
      return new ClientSetInfoConfig(var0);
   }
}
