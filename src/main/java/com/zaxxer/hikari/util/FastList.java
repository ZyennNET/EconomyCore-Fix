package com.zaxxer.hikari.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public final class FastList<T> implements List<T>, RandomAccess, Serializable {
   private static final long serialVersionUID = -4598088075242913858L;
   private final Class<?> clazz;
   private T[] elementData;
   private int size;

   public FastList(Class<?> var1) {
      this.elementData = (T[])((Object[])Array.newInstance(var1, 32));
      this.clazz = var1;
   }

   public FastList(Class<?> var1, int var2) {
      this.elementData = (T[])((Object[])Array.newInstance(var1, var2));
      this.clazz = var1;
   }

   public boolean add(T var1) {
      if (this.size < this.elementData.length) {
         this.elementData[this.size++] = var1;
      } else {
         int var2 = this.elementData.length;
         int var3 = var2 << 1;
         Object[] var4 = Array.newInstance(this.clazz, var3);
         System.arraycopy(this.elementData, 0, var4, 0, var2);
         var4[this.size++] = var1;
         this.elementData = (T[])var4;
      }

      return true;
   }

   public T get(int var1) {
      return (T)this.elementData[var1];
   }

   public T removeLast() {
      Object var1 = this.elementData[--this.size];
      this.elementData[this.size] = null;
      return (T)var1;
   }

   public boolean remove(Object var1) {
      for(int var2 = this.size - 1; var2 >= 0; --var2) {
         if (var1 == this.elementData[var2]) {
            int var3 = this.size - var2 - 1;
            if (var3 > 0) {
               System.arraycopy(this.elementData, var2 + 1, this.elementData, var2, var3);
            }

            this.elementData[--this.size] = null;
            return true;
         }
      }

      return false;
   }

   public void clear() {
      for(int var1 = 0; var1 < this.size; ++var1) {
         this.elementData[var1] = null;
      }

      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public T set(int var1, T var2) {
      Object var3 = this.elementData[var1];
      this.elementData[var1] = var2;
      return (T)var3;
   }

   public T remove(int var1) {
      if (this.size == 0) {
         return null;
      } else {
         Object var2 = this.elementData[var1];
         int var3 = this.size - var1 - 1;
         if (var3 > 0) {
            System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var3);
         }

         this.elementData[--this.size] = null;
         return (T)var2;
      }
   }

   public boolean contains(Object var1) {
      throw new UnsupportedOperationException();
   }

   public Iterator<T> iterator() {
      return new Iterator<T>() {
         private int index;

         public boolean hasNext() {
            return this.index < FastList.this.size;
         }

         public T next() {
            if (this.index < FastList.this.size) {
               return (T)FastList.this.elementData[this.index++];
            } else {
               throw new NoSuchElementException("No more elements in FastList");
            }
         }
      };
   }

   public Object[] toArray() {
      throw new UnsupportedOperationException();
   }

   public <E> E[] toArray(E[] var1) {
      throw new UnsupportedOperationException();
   }

   public boolean containsAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(Collection<? extends T> var1) {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int var1, Collection<? extends T> var2) {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(Collection<?> var1) {
      throw new UnsupportedOperationException();
   }

   public void add(int var1, T var2) {
      throw new UnsupportedOperationException();
   }

   public int indexOf(Object var1) {
      throw new UnsupportedOperationException();
   }

   public int lastIndexOf(Object var1) {
      throw new UnsupportedOperationException();
   }

   public ListIterator<T> listIterator() {
      throw new UnsupportedOperationException();
   }

   public ListIterator<T> listIterator(int var1) {
      throw new UnsupportedOperationException();
   }

   public List<T> subList(int var1, int var2) {
      throw new UnsupportedOperationException();
   }

   public Object clone() {
      throw new UnsupportedOperationException();
   }

   public void forEach(Consumer<? super T> var1) {
      throw new UnsupportedOperationException();
   }

   public Spliterator<T> spliterator() {
      throw new UnsupportedOperationException();
   }

   public boolean removeIf(Predicate<? super T> var1) {
      throw new UnsupportedOperationException();
   }

   public void replaceAll(UnaryOperator<T> var1) {
      throw new UnsupportedOperationException();
   }

   public void sort(Comparator<? super T> var1) {
      throw new UnsupportedOperationException();
   }
}
