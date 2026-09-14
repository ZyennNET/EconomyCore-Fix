package com.google.gson.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.RandomAccess;

public class NonNullElementWrapperList<E> extends AbstractList<E> implements RandomAccess {
   private final ArrayList<E> delegate;

   public NonNullElementWrapperList(ArrayList<E> var1) {
      this.delegate = (ArrayList)Objects.requireNonNull(var1);
   }

   public E get(int var1) {
      return (E)this.delegate.get(var1);
   }

   public int size() {
      return this.delegate.size();
   }

   private E nonNull(E var1) {
      if (var1 == null) {
         throw new NullPointerException("Element must be non-null");
      } else {
         return var1;
      }
   }

   public E set(int var1, E var2) {
      return (E)this.delegate.set(var1, this.nonNull(var2));
   }

   public void add(int var1, E var2) {
      this.delegate.add(var1, this.nonNull(var2));
   }

   public E remove(int var1) {
      return (E)this.delegate.remove(var1);
   }

   public void clear() {
      this.delegate.clear();
   }

   public boolean remove(Object var1) {
      return this.delegate.remove(var1);
   }

   public boolean removeAll(Collection<?> var1) {
      return this.delegate.removeAll(var1);
   }

   public boolean retainAll(Collection<?> var1) {
      return this.delegate.retainAll(var1);
   }

   public boolean contains(Object var1) {
      return this.delegate.contains(var1);
   }

   public int indexOf(Object var1) {
      return this.delegate.indexOf(var1);
   }

   public int lastIndexOf(Object var1) {
      return this.delegate.lastIndexOf(var1);
   }

   public Object[] toArray() {
      return this.delegate.toArray();
   }

   public <T> T[] toArray(T[] var1) {
      return (T[])this.delegate.toArray(var1);
   }

   public boolean equals(Object var1) {
      return this.delegate.equals(var1);
   }

   public int hashCode() {
      return this.delegate.hashCode();
   }
}
