package redis.clients.jedis.util;

import java.util.AbstractMap;

public class KeyValue<K, V> extends AbstractMap.SimpleImmutableEntry<K, V> {
   public KeyValue(K var1, V var2) {
      super(var1, var2);
   }

   public static <K, V> KeyValue<K, V> of(K var0, V var1) {
      return new KeyValue<K, V>(var0, var1);
   }
}
