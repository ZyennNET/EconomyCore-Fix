package redis.clients.jedis.util;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

public class Pool<T> extends GenericObjectPool<T> {
   public Pool(GenericObjectPoolConfig<T> var1, PooledObjectFactory<T> var2) {
      this(var2, var1);
   }

   public Pool(PooledObjectFactory<T> var1, GenericObjectPoolConfig<T> var2) {
      super(var1, var2);
   }

   public Pool(PooledObjectFactory<T> var1) {
      super(var1);
   }

   public void close() {
      this.destroy();
   }

   public void destroy() {
      try {
         super.close();
      } catch (RuntimeException var2) {
         throw new JedisException("Could not destroy the pool", var2);
      }
   }

   public T getResource() {
      try {
         return (T)super.borrowObject();
      } catch (JedisException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new JedisException("Could not get a resource from the pool", var3);
      }
   }

   public void returnResource(T var1) {
      if (var1 != null) {
         try {
            super.returnObject(var1);
         } catch (RuntimeException var3) {
            throw new JedisException("Could not return the resource to the pool", var3);
         }
      }
   }

   public void returnBrokenResource(T var1) {
      if (var1 != null) {
         try {
            super.invalidateObject(var1);
         } catch (Exception var3) {
            throw new JedisException("Could not return the broken resource to the pool", var3);
         }
      }
   }

   public void addObjects(int var1) {
      try {
         for(int var2 = 0; var2 < var1; ++var2) {
            this.addObject();
         }

      } catch (Exception var3) {
         throw new JedisException("Error trying to add idle objects", var3);
      }
   }
}
