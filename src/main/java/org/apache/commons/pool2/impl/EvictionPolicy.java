package org.apache.commons.pool2.impl;

import org.apache.commons.pool2.PooledObject;

public interface EvictionPolicy<T> {
   boolean evict(EvictionConfig var1, PooledObject<T> var2, int var3);
}
