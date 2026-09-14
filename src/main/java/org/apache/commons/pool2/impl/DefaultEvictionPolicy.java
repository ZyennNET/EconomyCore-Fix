package org.apache.commons.pool2.impl;

import org.apache.commons.pool2.PooledObject;

public class DefaultEvictionPolicy<T> implements EvictionPolicy<T> {
   public boolean evict(EvictionConfig var1, PooledObject<T> var2, int var3) {
      return var1.getIdleSoftEvictDuration().compareTo(var2.getIdleDuration()) < 0 && var1.getMinIdle() < var3 || var1.getIdleEvictDuration().compareTo(var2.getIdleDuration()) < 0;
   }
}
