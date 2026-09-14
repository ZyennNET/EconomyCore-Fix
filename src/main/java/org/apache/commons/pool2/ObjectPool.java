package org.apache.commons.pool2;

import java.io.Closeable;

public interface ObjectPool<T> extends Closeable {
   void addObject() throws Exception;

   default void addObjects(int var1) throws Exception {
      for(int var2 = 0; var2 < var1; ++var2) {
         this.addObject();
      }

   }

   T borrowObject() throws Exception;

   void clear() throws Exception;

   void close();

   int getNumActive();

   int getNumIdle();

   void invalidateObject(T var1) throws Exception;

   default void invalidateObject(T var1, DestroyMode var2) throws Exception {
      this.invalidateObject(var1);
   }

   void returnObject(T var1) throws Exception;
}
