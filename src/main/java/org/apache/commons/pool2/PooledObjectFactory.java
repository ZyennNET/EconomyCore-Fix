package org.apache.commons.pool2;

public interface PooledObjectFactory<T> {
   void activateObject(PooledObject<T> var1) throws Exception;

   void destroyObject(PooledObject<T> var1) throws Exception;

   default void destroyObject(PooledObject<T> var1, DestroyMode var2) throws Exception {
      this.destroyObject(var1);
   }

   PooledObject<T> makeObject() throws Exception;

   void passivateObject(PooledObject<T> var1) throws Exception;

   boolean validateObject(PooledObject<T> var1);
}
