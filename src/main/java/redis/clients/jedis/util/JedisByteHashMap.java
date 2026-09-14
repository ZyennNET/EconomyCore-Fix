package redis.clients.jedis.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class JedisByteHashMap implements Map<byte[], byte[]>, Cloneable, Serializable {
   private static final long serialVersionUID = -6971431362627219416L;
   private final Map<ByteArrayWrapper, byte[]> internalMap = new HashMap();

   public void clear() {
      this.internalMap.clear();
   }

   public boolean containsKey(Object var1) {
      return var1 instanceof byte[] ? this.internalMap.containsKey(new ByteArrayWrapper((byte[])var1)) : this.internalMap.containsKey(var1);
   }

   public boolean containsValue(Object var1) {
      return this.internalMap.containsValue(var1);
   }

   public Set<Map.Entry<byte[], byte[]>> entrySet() {
      Iterator var1 = this.internalMap.entrySet().iterator();
      HashSet var2 = new HashSet();

      while(var1.hasNext()) {
         Map.Entry var3 = (Map.Entry)var1.next();
         var2.add(new JedisByteEntry(((ByteArrayWrapper)var3.getKey()).data, (byte[])var3.getValue()));
      }

      return var2;
   }

   public byte[] get(Object var1) {
      return var1 instanceof byte[] ? (byte[])this.internalMap.get(new ByteArrayWrapper((byte[])var1)) : (byte[])this.internalMap.get(var1);
   }

   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   public Set<byte[]> keySet() {
      HashSet var1 = new HashSet();
      Iterator var2 = this.internalMap.keySet().iterator();

      while(var2.hasNext()) {
         var1.add(((ByteArrayWrapper)var2.next()).data);
      }

      return var1;
   }

   public byte[] put(byte[] var1, byte[] var2) {
      return (byte[])this.internalMap.put(new ByteArrayWrapper(var1), var2);
   }

   public void putAll(Map<byte[], byte[]> var1) {
      for(Map.Entry var3 : var1.entrySet()) {
         this.internalMap.put(new ByteArrayWrapper((byte[])var3.getKey()), var3.getValue());
      }

   }

   public byte[] remove(Object var1) {
      return var1 instanceof byte[] ? (byte[])this.internalMap.remove(new ByteArrayWrapper((byte[])var1)) : (byte[])this.internalMap.remove(var1);
   }

   public int size() {
      return this.internalMap.size();
   }

   public Collection<byte[]> values() {
      return this.internalMap.values();
   }

   private static final class ByteArrayWrapper implements Serializable {
      private final byte[] data;

      public ByteArrayWrapper(byte[] var1) {
         if (var1 == null) {
            throw new NullPointerException();
         } else {
            this.data = var1;
         }
      }

      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         } else if (var1 == this) {
            return true;
         } else {
            return !(var1 instanceof ByteArrayWrapper) ? false : Arrays.equals(this.data, ((ByteArrayWrapper)var1).data);
         }
      }

      public int hashCode() {
         return Arrays.hashCode(this.data);
      }
   }

   private static final class JedisByteEntry implements Map.Entry<byte[], byte[]> {
      private byte[] value;
      private byte[] key;

      public JedisByteEntry(byte[] var1, byte[] var2) {
         this.key = var1;
         this.value = var2;
      }

      public byte[] getKey() {
         return this.key;
      }

      public byte[] getValue() {
         return this.value;
      }

      public byte[] setValue(byte[] var1) {
         this.value = var1;
         return var1;
      }
   }
}
