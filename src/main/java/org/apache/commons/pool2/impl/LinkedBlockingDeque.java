package org.apache.commons.pool2.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.time.Duration;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

class LinkedBlockingDeque<E> extends AbstractQueue<E> implements Deque<E>, Serializable {
   private static final long serialVersionUID = -387911632671998426L;
   private transient Node<E> first;
   private transient Node<E> last;
   private transient int count;
   private final int capacity;
   private final InterruptibleReentrantLock lock;
   private final Condition notEmpty;
   private final Condition notFull;

   public LinkedBlockingDeque() {
      this(Integer.MAX_VALUE);
   }

   public LinkedBlockingDeque(boolean var1) {
      this(Integer.MAX_VALUE, var1);
   }

   public LinkedBlockingDeque(Collection<? extends E> var1) {
      this(Integer.MAX_VALUE);
      this.lock.lock();

      try {
         for(Object var3 : var1) {
            Objects.requireNonNull(var3);
            if (!this.linkLast(var3)) {
               throw new IllegalStateException("Deque full");
            }
         }
      } finally {
         this.lock.unlock();
      }

   }

   public LinkedBlockingDeque(int var1) {
      this(var1, false);
   }

   public LinkedBlockingDeque(int var1, boolean var2) {
      if (var1 <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.capacity = var1;
         this.lock = new InterruptibleReentrantLock(var2);
         this.notEmpty = this.lock.newCondition();
         this.notFull = this.lock.newCondition();
      }
   }

   public boolean add(E var1) {
      this.addLast(var1);
      return true;
   }

   public void addFirst(E var1) {
      if (!this.offerFirst(var1)) {
         throw new IllegalStateException("Deque full");
      }
   }

   public void addLast(E var1) {
      if (!this.offerLast(var1)) {
         throw new IllegalStateException("Deque full");
      }
   }

   public void clear() {
      this.lock.lock();

      try {
         Node var2;
         for(Node var1 = this.first; var1 != null; var1 = var2) {
            var1.item = null;
            var2 = var1.next;
            var1.prev = null;
            var1.next = null;
         }

         this.first = this.last = null;
         this.count = 0;
         this.notFull.signalAll();
      } finally {
         this.lock.unlock();
      }

   }

   public boolean contains(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         this.lock.lock();

         try {
            for(Node var2 = this.first; var2 != null; var2 = var2.next) {
               if (var1.equals(var2.item)) {
                  boolean var3 = true;
                  return var3;
               }
            }

            boolean var7 = false;
            return var7;
         } finally {
            this.lock.unlock();
         }
      }
   }

   public Iterator<E> descendingIterator() {
      return new DescendingItr();
   }

   public int drainTo(Collection<? super E> var1) {
      return this.drainTo(var1, Integer.MAX_VALUE);
   }

   public int drainTo(Collection<? super E> var1, int var2) {
      Objects.requireNonNull(var1, "c");
      if (var1 == this) {
         throw new IllegalArgumentException();
      } else {
         this.lock.lock();

         int var8;
         try {
            int var3 = Math.min(var2, this.count);

            for(int var4 = 0; var4 < var3; ++var4) {
               var1.add(this.first.item);
               this.unlinkFirst();
            }

            var8 = var3;
         } finally {
            this.lock.unlock();
         }

         return var8;
      }
   }

   public E element() {
      return (E)this.getFirst();
   }

   public E getFirst() {
      Object var1 = this.peekFirst();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (E)var1;
      }
   }

   public E getLast() {
      Object var1 = this.peekLast();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (E)var1;
      }
   }

   public int getTakeQueueLength() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.lock.getWaitQueueLength(this.notEmpty);
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public boolean hasTakeWaiters() {
      this.lock.lock();

      boolean var1;
      try {
         var1 = this.lock.hasWaiters(this.notEmpty);
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public void interuptTakeWaiters() {
      this.lock.lock();

      try {
         this.lock.interruptWaiters(this.notEmpty);
      } finally {
         this.lock.unlock();
      }

   }

   public Iterator<E> iterator() {
      return new Itr();
   }

   private boolean linkFirst(E var1) {
      if (this.count >= this.capacity) {
         return false;
      } else {
         Node var2 = this.first;
         Node var3 = new Node(var1, (Node)null, var2);
         this.first = var3;
         if (this.last == null) {
            this.last = var3;
         } else {
            var2.prev = var3;
         }

         ++this.count;
         this.notEmpty.signal();
         return true;
      }
   }

   private boolean linkLast(E var1) {
      if (this.count >= this.capacity) {
         return false;
      } else {
         Node var2 = this.last;
         Node var3 = new Node(var1, var2, (Node)null);
         this.last = var3;
         if (this.first == null) {
            this.first = var3;
         } else {
            var2.next = var3;
         }

         ++this.count;
         this.notEmpty.signal();
         return true;
      }
   }

   public boolean offer(E var1) {
      return this.offerLast(var1);
   }

   boolean offer(E var1, Duration var2) throws InterruptedException {
      return this.offerLast(var1, var2);
   }

   public boolean offer(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.offerLast(var1, var2, var4);
   }

   public boolean offerFirst(E var1) {
      Objects.requireNonNull(var1, "e");
      this.lock.lock();

      boolean var2;
      try {
         var2 = this.linkFirst(var1);
      } finally {
         this.lock.unlock();
      }

      return var2;
   }

   public boolean offerFirst(E var1, Duration var2) throws InterruptedException {
      Objects.requireNonNull(var1, "e");
      long var3 = var2.toNanos();
      this.lock.lockInterruptibly();

      try {
         while(true) {
            if (!this.linkFirst(var1)) {
               if (var3 > 0L) {
                  var3 = this.notFull.awaitNanos(var3);
                  continue;
               }

               boolean var9 = false;
               return var9;
            }

            boolean var5 = true;
            return var5;
         }
      } finally {
         this.lock.unlock();
      }
   }

   public boolean offerFirst(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.offerFirst(var1, PoolImplUtils.toDuration(var2, var4));
   }

   public boolean offerLast(E var1) {
      Objects.requireNonNull(var1, "e");
      this.lock.lock();

      boolean var2;
      try {
         var2 = this.linkLast(var1);
      } finally {
         this.lock.unlock();
      }

      return var2;
   }

   boolean offerLast(E var1, Duration var2) throws InterruptedException {
      Objects.requireNonNull(var1, "e");
      long var3 = var2.toNanos();
      this.lock.lockInterruptibly();

      try {
         while(true) {
            if (!this.linkLast(var1)) {
               if (var3 > 0L) {
                  var3 = this.notFull.awaitNanos(var3);
                  continue;
               }

               boolean var9 = false;
               return var9;
            }

            boolean var5 = true;
            return var5;
         }
      } finally {
         this.lock.unlock();
      }
   }

   public boolean offerLast(E var1, long var2, TimeUnit var4) throws InterruptedException {
      return this.offerLast(var1, PoolImplUtils.toDuration(var2, var4));
   }

   public E peek() {
      return (E)this.peekFirst();
   }

   public E peekFirst() {
      this.lock.lock();

      Object var1;
      try {
         var1 = this.first == null ? null : this.first.item;
      } finally {
         this.lock.unlock();
      }

      return (E)var1;
   }

   public E peekLast() {
      this.lock.lock();

      Object var1;
      try {
         var1 = this.last == null ? null : this.last.item;
      } finally {
         this.lock.unlock();
      }

      return (E)var1;
   }

   public E poll() {
      return (E)this.pollFirst();
   }

   E poll(Duration var1) throws InterruptedException {
      return (E)this.pollFirst(var1);
   }

   public E poll(long var1, TimeUnit var3) throws InterruptedException {
      return (E)this.pollFirst(var1, var3);
   }

   public E pollFirst() {
      this.lock.lock();

      Object var1;
      try {
         var1 = this.unlinkFirst();
      } finally {
         this.lock.unlock();
      }

      return (E)var1;
   }

   E pollFirst(Duration var1) throws InterruptedException {
      long var2 = var1.toNanos();
      this.lock.lockInterruptibly();

      try {
         while(true) {
            Object var4;
            if ((var4 = this.unlinkFirst()) == null) {
               if (var2 > 0L) {
                  var2 = this.notEmpty.awaitNanos(var2);
                  continue;
               }

               Object var9 = null;
               return (E)var9;
            }

            Object var5 = var4;
            return (E)var5;
         }
      } finally {
         this.lock.unlock();
      }
   }

   public E pollFirst(long var1, TimeUnit var3) throws InterruptedException {
      return (E)this.pollFirst(PoolImplUtils.toDuration(var1, var3));
   }

   public E pollLast() {
      this.lock.lock();

      Object var1;
      try {
         var1 = this.unlinkLast();
      } finally {
         this.lock.unlock();
      }

      return (E)var1;
   }

   public E pollLast(Duration var1) throws InterruptedException {
      long var2 = var1.toNanos();
      this.lock.lockInterruptibly();

      try {
         while(true) {
            Object var4;
            if ((var4 = this.unlinkLast()) == null) {
               if (var2 > 0L) {
                  var2 = this.notEmpty.awaitNanos(var2);
                  continue;
               }

               Object var9 = null;
               return (E)var9;
            }

            Object var5 = var4;
            return (E)var5;
         }
      } finally {
         this.lock.unlock();
      }
   }

   public E pollLast(long var1, TimeUnit var3) throws InterruptedException {
      return (E)this.pollLast(PoolImplUtils.toDuration(var1, var3));
   }

   public E pop() {
      return (E)this.removeFirst();
   }

   public void push(E var1) {
      this.addFirst(var1);
   }

   public void put(E var1) throws InterruptedException {
      this.putLast(var1);
   }

   public void putFirst(E var1) throws InterruptedException {
      Objects.requireNonNull(var1, "e");
      this.lock.lock();

      try {
         while(!this.linkFirst(var1)) {
            this.notFull.await();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public void putLast(E var1) throws InterruptedException {
      Objects.requireNonNull(var1, "e");
      this.lock.lock();

      try {
         while(!this.linkLast(var1)) {
            this.notFull.await();
         }
      } finally {
         this.lock.unlock();
      }

   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      var1.defaultReadObject();
      this.count = 0;
      this.first = null;
      this.last = null;

      while(true) {
         Object var2 = var1.readObject();
         if (var2 == null) {
            return;
         }

         this.add(var2);
      }
   }

   public int remainingCapacity() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.capacity - this.count;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public E remove() {
      return (E)this.removeFirst();
   }

   public boolean remove(Object var1) {
      return this.removeFirstOccurrence(var1);
   }

   public E removeFirst() {
      Object var1 = this.pollFirst();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (E)var1;
      }
   }

   public boolean removeFirstOccurrence(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         this.lock.lock();

         try {
            for(Node var2 = this.first; var2 != null; var2 = var2.next) {
               if (var1.equals(var2.item)) {
                  this.unlink(var2);
                  boolean var3 = true;
                  return var3;
               }
            }

            boolean var7 = false;
            return var7;
         } finally {
            this.lock.unlock();
         }
      }
   }

   public E removeLast() {
      Object var1 = this.pollLast();
      if (var1 == null) {
         throw new NoSuchElementException();
      } else {
         return (E)var1;
      }
   }

   public boolean removeLastOccurrence(Object var1) {
      if (var1 == null) {
         return false;
      } else {
         this.lock.lock();

         try {
            for(Node var2 = this.last; var2 != null; var2 = var2.prev) {
               if (var1.equals(var2.item)) {
                  this.unlink(var2);
                  boolean var3 = true;
                  return var3;
               }
            }

            boolean var7 = false;
            return var7;
         } finally {
            this.lock.unlock();
         }
      }
   }

   public int size() {
      this.lock.lock();

      int var1;
      try {
         var1 = this.count;
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   public E take() throws InterruptedException {
      return (E)this.takeFirst();
   }

   public E takeFirst() throws InterruptedException {
      this.lock.lock();

      Object var2;
      try {
         Object var1;
         while((var1 = this.unlinkFirst()) == null) {
            this.notEmpty.await();
         }

         var2 = var1;
      } finally {
         this.lock.unlock();
      }

      return (E)var2;
   }

   public E takeLast() throws InterruptedException {
      this.lock.lock();

      Object var2;
      try {
         Object var1;
         while((var1 = this.unlinkLast()) == null) {
            this.notEmpty.await();
         }

         var2 = var1;
      } finally {
         this.lock.unlock();
      }

      return (E)var2;
   }

   public Object[] toArray() {
      this.lock.lock();

      Object[] var7;
      try {
         Object[] var1 = new Object[this.count];
         int var2 = 0;

         for(Node var3 = this.first; var3 != null; var3 = var3.next) {
            var1[var2++] = var3.item;
         }

         var7 = var1;
      } finally {
         this.lock.unlock();
      }

      return var7;
   }

   public <T> T[] toArray(T[] var1) {
      this.lock.lock();

      Object[] var7;
      try {
         if (var1.length < this.count) {
            var1 = Array.newInstance(var1.getClass().getComponentType(), this.count);
         }

         int var2 = 0;

         for(Node var3 = this.first; var3 != null; var3 = var3.next) {
            var1[var2++] = var3.item;
         }

         if (var1.length > var2) {
            var1[var2] = null;
         }

         var7 = var1;
      } finally {
         this.lock.unlock();
      }

      return (T[])var7;
   }

   public String toString() {
      this.lock.lock();

      String var1;
      try {
         var1 = super.toString();
      } finally {
         this.lock.unlock();
      }

      return var1;
   }

   private void unlink(Node<E> var1) {
      Node var2 = var1.prev;
      Node var3 = var1.next;
      if (var2 == null) {
         this.unlinkFirst();
      } else if (var3 == null) {
         this.unlinkLast();
      } else {
         var2.next = var3;
         var3.prev = var2;
         var1.item = null;
         --this.count;
         this.notFull.signal();
      }

   }

   private E unlinkFirst() {
      Node var1 = this.first;
      if (var1 == null) {
         return null;
      } else {
         Node var2 = var1.next;
         Object var3 = var1.item;
         var1.item = null;
         var1.next = var1;
         this.first = var2;
         if (var2 == null) {
            this.last = null;
         } else {
            var2.prev = null;
         }

         --this.count;
         this.notFull.signal();
         return (E)var3;
      }
   }

   private E unlinkLast() {
      Node var1 = this.last;
      if (var1 == null) {
         return null;
      } else {
         Node var2 = var1.prev;
         Object var3 = var1.item;
         var1.item = null;
         var1.prev = var1;
         this.last = var2;
         if (var2 == null) {
            this.first = null;
         } else {
            var2.next = null;
         }

         --this.count;
         this.notFull.signal();
         return (E)var3;
      }
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      this.lock.lock();

      try {
         var1.defaultWriteObject();

         for(Node var2 = this.first; var2 != null; var2 = var2.next) {
            var1.writeObject(var2.item);
         }

         var1.writeObject((Object)null);
      } finally {
         this.lock.unlock();
      }

   }

   private abstract class AbstractItr implements Iterator<E> {
      Node<E> next;
      E nextItem;
      private Node<E> lastRet;

      AbstractItr() {
         LinkedBlockingDeque.this.lock.lock();

         try {
            this.next = this.firstNode();
            this.nextItem = (E)(this.next == null ? null : this.next.item);
         } finally {
            LinkedBlockingDeque.this.lock.unlock();
         }

      }

      void advance() {
         LinkedBlockingDeque.this.lock.lock();

         try {
            this.next = this.succ(this.next);
            this.nextItem = (E)(this.next == null ? null : this.next.item);
         } finally {
            LinkedBlockingDeque.this.lock.unlock();
         }

      }

      abstract Node<E> firstNode();

      public boolean hasNext() {
         return this.next != null;
      }

      public E next() {
         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            this.lastRet = this.next;
            Object var1 = this.nextItem;
            this.advance();
            return (E)var1;
         }
      }

      abstract Node<E> nextNode(Node<E> var1);

      public void remove() {
         Node var1 = this.lastRet;
         if (var1 == null) {
            throw new IllegalStateException();
         } else {
            this.lastRet = null;
            LinkedBlockingDeque.this.lock.lock();

            try {
               if (var1.item != null) {
                  LinkedBlockingDeque.this.unlink(var1);
               }
            } finally {
               LinkedBlockingDeque.this.lock.unlock();
            }

         }
      }

      private Node<E> succ(Node<E> var1) {
         while(true) {
            Node var2 = this.nextNode(var1);
            if (var2 == null) {
               return null;
            }

            if (var2.item != null) {
               return var2;
            }

            if (var2 == var1) {
               return this.firstNode();
            }

            var1 = var2;
         }
      }
   }

   private class DescendingItr extends LinkedBlockingDeque<E>.AbstractItr {
      private DescendingItr() {
      }

      Node<E> firstNode() {
         return LinkedBlockingDeque.this.last;
      }

      Node<E> nextNode(Node<E> var1) {
         return var1.prev;
      }
   }

   private class Itr extends LinkedBlockingDeque<E>.AbstractItr {
      private Itr() {
      }

      Node<E> firstNode() {
         return LinkedBlockingDeque.this.first;
      }

      Node<E> nextNode(Node<E> var1) {
         return var1.next;
      }
   }

   private static final class Node<E> {
      E item;
      Node<E> prev;
      Node<E> next;

      Node(E var1, Node<E> var2, Node<E> var3) {
         this.item = var1;
         this.prev = var2;
         this.next = var3;
      }
   }
}
