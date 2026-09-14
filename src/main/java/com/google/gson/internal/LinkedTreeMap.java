package com.google.gson.internal;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public final class LinkedTreeMap<K, V> extends AbstractMap<K, V> implements Serializable {
   private static final Comparator<Comparable> NATURAL_ORDER = new Comparator<Comparable>() {
      public int compare(Comparable var1, Comparable var2) {
         return var1.compareTo(var2);
      }
   };
   private final Comparator<? super K> comparator;
   private final boolean allowNullValues;
   Node<K, V> root;
   int size;
   int modCount;
   final Node<K, V> header;
   private LinkedTreeMap<K, V>.EntrySet entrySet;
   private LinkedTreeMap<K, V>.KeySet keySet;

   public LinkedTreeMap() {
      this(NATURAL_ORDER, true);
   }

   public LinkedTreeMap(boolean var1) {
      this(NATURAL_ORDER, var1);
   }

   public LinkedTreeMap(Comparator<? super K> var1, boolean var2) {
      this.size = 0;
      this.modCount = 0;
      this.comparator = var1 != null ? var1 : NATURAL_ORDER;
      this.allowNullValues = var2;
      this.header = new Node<K, V>(var2);
   }

   public int size() {
      return this.size;
   }

   public V get(Object var1) {
      Node var2 = this.findByObject(var1);
      return (V)(var2 != null ? var2.value : null);
   }

   public boolean containsKey(Object var1) {
      return this.findByObject(var1) != null;
   }

   @CanIgnoreReturnValue
   public V put(K var1, V var2) {
      if (var1 == null) {
         throw new NullPointerException("key == null");
      } else if (var2 == null && !this.allowNullValues) {
         throw new NullPointerException("value == null");
      } else {
         Node var3 = this.find(var1, true);
         Object var4 = var3.value;
         var3.value = var2;
         return (V)var4;
      }
   }

   public void clear() {
      this.root = null;
      this.size = 0;
      ++this.modCount;
      Node var1 = this.header;
      var1.next = var1.prev = var1;
   }

   public V remove(Object var1) {
      Node var2 = this.removeInternalByKey(var1);
      return (V)(var2 != null ? var2.value : null);
   }

   Node<K, V> find(K var1, boolean var2) {
      Comparator var3 = this.comparator;
      Node var4 = this.root;
      int var5 = 0;
      if (var4 != null) {
         Comparable var6 = var3 == NATURAL_ORDER ? (Comparable)var1 : null;

         while(true) {
            var5 = var6 != null ? var6.compareTo(var4.key) : var3.compare(var1, var4.key);
            if (var5 == 0) {
               return var4;
            }

            Node var7 = var5 < 0 ? var4.left : var4.right;
            if (var7 == null) {
               break;
            }

            var4 = var7;
         }
      }

      if (!var2) {
         return null;
      } else {
         Node var8 = this.header;
         Node var9;
         if (var4 == null) {
            if (var3 == NATURAL_ORDER && !(var1 instanceof Comparable)) {
               throw new ClassCastException(var1.getClass().getName() + " is not Comparable");
            }

            var9 = new Node(this.allowNullValues, var4, var1, var8, var8.prev);
            this.root = var9;
         } else {
            var9 = new Node(this.allowNullValues, var4, var1, var8, var8.prev);
            if (var5 < 0) {
               var4.left = var9;
            } else {
               var4.right = var9;
            }

            this.rebalance(var4, true);
         }

         ++this.size;
         ++this.modCount;
         return var9;
      }
   }

   Node<K, V> findByObject(Object var1) {
      try {
         return var1 != null ? this.find(var1, false) : null;
      } catch (ClassCastException var3) {
         return null;
      }
   }

   Node<K, V> findByEntry(Map.Entry<?, ?> var1) {
      Node var2 = this.findByObject(var1.getKey());
      boolean var3 = var2 != null && equal(var2.value, var1.getValue());
      return var3 ? var2 : null;
   }

   private static boolean equal(Object var0, Object var1) {
      return Objects.equals(var0, var1);
   }

   void removeInternal(Node<K, V> var1, boolean var2) {
      if (var2) {
         var1.prev.next = var1.next;
         var1.next.prev = var1.prev;
      }

      Node var3 = var1.left;
      Node var4 = var1.right;
      Node var5 = var1.parent;
      if (var3 != null && var4 != null) {
         Node var6 = var3.height > var4.height ? var3.last() : var4.first();
         this.removeInternal(var6, false);
         int var7 = 0;
         var3 = var1.left;
         if (var3 != null) {
            var7 = var3.height;
            var6.left = var3;
            var3.parent = var6;
            var1.left = null;
         }

         int var8 = 0;
         var4 = var1.right;
         if (var4 != null) {
            var8 = var4.height;
            var6.right = var4;
            var4.parent = var6;
            var1.right = null;
         }

         var6.height = Math.max(var7, var8) + 1;
         this.replaceInParent(var1, var6);
      } else {
         if (var3 != null) {
            this.replaceInParent(var1, var3);
            var1.left = null;
         } else if (var4 != null) {
            this.replaceInParent(var1, var4);
            var1.right = null;
         } else {
            this.replaceInParent(var1, (Node)null);
         }

         this.rebalance(var5, false);
         --this.size;
         ++this.modCount;
      }
   }

   Node<K, V> removeInternalByKey(Object var1) {
      Node var2 = this.findByObject(var1);
      if (var2 != null) {
         this.removeInternal(var2, true);
      }

      return var2;
   }

   private void replaceInParent(Node<K, V> var1, Node<K, V> var2) {
      Node var3 = var1.parent;
      var1.parent = null;
      if (var2 != null) {
         var2.parent = var3;
      }

      if (var3 != null) {
         if (var3.left == var1) {
            var3.left = var2;
         } else {
            assert var3.right == var1;

            var3.right = var2;
         }
      } else {
         this.root = var2;
      }

   }

   private void rebalance(Node<K, V> var1, boolean var2) {
      for(Node var3 = var1; var3 != null; var3 = var3.parent) {
         Node var4 = var3.left;
         Node var5 = var3.right;
         int var6 = var4 != null ? var4.height : 0;
         int var7 = var5 != null ? var5.height : 0;
         int var8 = var6 - var7;
         if (var8 == -2) {
            Node var9 = var5.left;
            Node var10 = var5.right;
            int var11 = var10 != null ? var10.height : 0;
            int var12 = var9 != null ? var9.height : 0;
            int var13 = var12 - var11;
            if (var13 != -1 && (var13 != 0 || var2)) {
               assert var13 == 1;

               this.rotateRight(var5);
               this.rotateLeft(var3);
            } else {
               this.rotateLeft(var3);
            }

            if (var2) {
               break;
            }
         } else if (var8 == 2) {
            Node var14 = var4.left;
            Node var15 = var4.right;
            int var16 = var15 != null ? var15.height : 0;
            int var17 = var14 != null ? var14.height : 0;
            int var18 = var17 - var16;
            if (var18 != 1 && (var18 != 0 || var2)) {
               assert var18 == -1;

               this.rotateLeft(var4);
               this.rotateRight(var3);
            } else {
               this.rotateRight(var3);
            }

            if (var2) {
               break;
            }
         } else if (var8 == 0) {
            var3.height = var6 + 1;
            if (var2) {
               break;
            }
         } else {
            assert var8 == -1 || var8 == 1;

            var3.height = Math.max(var6, var7) + 1;
            if (!var2) {
               break;
            }
         }
      }

   }

   private void rotateLeft(Node<K, V> var1) {
      Node var2 = var1.left;
      Node var3 = var1.right;
      Node var4 = var3.left;
      Node var5 = var3.right;
      var1.right = var4;
      if (var4 != null) {
         var4.parent = var1;
      }

      this.replaceInParent(var1, var3);
      var3.left = var1;
      var1.parent = var3;
      var1.height = Math.max(var2 != null ? var2.height : 0, var4 != null ? var4.height : 0) + 1;
      var3.height = Math.max(var1.height, var5 != null ? var5.height : 0) + 1;
   }

   private void rotateRight(Node<K, V> var1) {
      Node var2 = var1.left;
      Node var3 = var1.right;
      Node var4 = var2.left;
      Node var5 = var2.right;
      var1.left = var5;
      if (var5 != null) {
         var5.parent = var1;
      }

      this.replaceInParent(var1, var2);
      var2.right = var1;
      var1.parent = var2;
      var1.height = Math.max(var3 != null ? var3.height : 0, var5 != null ? var5.height : 0) + 1;
      var2.height = Math.max(var1.height, var4 != null ? var4.height : 0) + 1;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      EntrySet var1 = this.entrySet;
      return var1 != null ? var1 : (this.entrySet = new EntrySet());
   }

   public Set<K> keySet() {
      KeySet var1 = this.keySet;
      return var1 != null ? var1 : (this.keySet = new KeySet());
   }

   private Object writeReplace() throws ObjectStreamException {
      return new LinkedHashMap(this);
   }

   private void readObject(ObjectInputStream var1) throws IOException {
      throw new InvalidObjectException("Deserialization is unsupported");
   }

   class EntrySet extends AbstractSet<Map.Entry<K, V>> {
      public int size() {
         return LinkedTreeMap.this.size;
      }

      public Iterator<Map.Entry<K, V>> iterator() {
         return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<Map.Entry<K, V>>() {
            public Map.Entry<K, V> next() {
               return this.nextNode();
            }
         };
      }

      public boolean contains(Object var1) {
         return var1 instanceof Map.Entry && LinkedTreeMap.this.findByEntry((Map.Entry)var1) != null;
      }

      public boolean remove(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            Node var2 = LinkedTreeMap.this.findByEntry((Map.Entry)var1);
            if (var2 == null) {
               return false;
            } else {
               LinkedTreeMap.this.removeInternal(var2, true);
               return true;
            }
         }
      }

      public void clear() {
         LinkedTreeMap.this.clear();
      }
   }

   final class KeySet extends AbstractSet<K> {
      public int size() {
         return LinkedTreeMap.this.size;
      }

      public Iterator<K> iterator() {
         return new LinkedTreeMap<K, V>.LinkedTreeMapIterator<K>() {
            public K next() {
               return this.nextNode().key;
            }
         };
      }

      public boolean contains(Object var1) {
         return LinkedTreeMap.this.containsKey(var1);
      }

      public boolean remove(Object var1) {
         return LinkedTreeMap.this.removeInternalByKey(var1) != null;
      }

      public void clear() {
         LinkedTreeMap.this.clear();
      }
   }

   private abstract class LinkedTreeMapIterator<T> implements Iterator<T> {
      Node<K, V> next;
      Node<K, V> lastReturned;
      int expectedModCount;

      LinkedTreeMapIterator() {
         this.next = LinkedTreeMap.this.header.next;
         this.lastReturned = null;
         this.expectedModCount = LinkedTreeMap.this.modCount;
      }

      public final boolean hasNext() {
         return this.next != LinkedTreeMap.this.header;
      }

      final Node<K, V> nextNode() {
         Node var1 = this.next;
         if (var1 == LinkedTreeMap.this.header) {
            throw new NoSuchElementException();
         } else if (LinkedTreeMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         } else {
            this.next = var1.next;
            return this.lastReturned = var1;
         }
      }

      public final void remove() {
         if (this.lastReturned == null) {
            throw new IllegalStateException();
         } else {
            LinkedTreeMap.this.removeInternal(this.lastReturned, true);
            this.lastReturned = null;
            this.expectedModCount = LinkedTreeMap.this.modCount;
         }
      }
   }

   static final class Node<K, V> implements Map.Entry<K, V> {
      Node<K, V> parent;
      Node<K, V> left;
      Node<K, V> right;
      Node<K, V> next;
      Node<K, V> prev;
      final K key;
      final boolean allowNullValue;
      V value;
      int height;

      Node(boolean var1) {
         this.key = null;
         this.allowNullValue = var1;
         this.next = this.prev = this;
      }

      Node(boolean var1, Node<K, V> var2, K var3, Node<K, V> var4, Node<K, V> var5) {
         this.parent = var2;
         this.key = var3;
         this.allowNullValue = var1;
         this.height = 1;
         this.next = var4;
         this.prev = var5;
         var5.next = this;
         var4.prev = this;
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V var1) {
         if (var1 == null && !this.allowNullValue) {
            throw new NullPointerException("value == null");
         } else {
            Object var2 = this.value;
            this.value = var1;
            return (V)var2;
         }
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Map.Entry)) {
            return false;
         } else {
            boolean var10000;
            label38: {
               label27: {
                  Map.Entry var2 = (Map.Entry)var1;
                  if (this.key == null) {
                     if (var2.getKey() != null) {
                        break label27;
                     }
                  } else if (!this.key.equals(var2.getKey())) {
                     break label27;
                  }

                  if (this.value == null) {
                     if (var2.getValue() == null) {
                        break label38;
                     }
                  } else if (this.value.equals(var2.getValue())) {
                     break label38;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         }
      }

      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.key + "=" + this.value;
      }

      public Node<K, V> first() {
         Node var1 = this;

         for(Node var2 = this.left; var2 != null; var2 = var2.left) {
            var1 = var2;
         }

         return var1;
      }

      public Node<K, V> last() {
         Node var1 = this;

         for(Node var2 = this.right; var2 != null; var2 = var2.right) {
            var1 = var2;
         }

         return var1;
      }
   }
}
