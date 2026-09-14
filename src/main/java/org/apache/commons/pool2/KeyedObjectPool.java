package org.apache.commons.pool2;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface KeyedObjectPool<K, V> extends Closeable {
   void addObject(K var1) throws Exception;

   default void addObjects(Collection<K> var1, int var2) throws Exception {
      if (var1 == null) {
         throw new IllegalArgumentException("keys must not be null.");
      } else {
         for(Object var4 : var1) {
            this.addObjects(var4, var2);
         }

      }
   }

   default void addObjects(K var1, int var2) throws Exception {
      if (var1 == null) {
         throw new IllegalArgumentException("key must not be null.");
      } else {
         for(int var3 = 0; var3 < var2; ++var3) {
            this.addObject(var1);
         }

      }
   }

   V borrowObject(K var1) throws Exception;

   void clear() throws Exception;

   void clear(K var1) throws Exception;

   void close();

   default List<K> getKeys() {
      return Collections.emptyList();
   }

   int getNumActive();

   int getNumActive(K var1);

   int getNumIdle();

   int getNumIdle(K var1);

   void invalidateObject(K var1, V var2) throws Exception;

   default void invalidateObject(K var1, V var2, DestroyMode var3) throws Exception {
      this.invalidateObject(var1, var2);
   }

   void returnObject(K var1, V var2) throws Exception;
}
