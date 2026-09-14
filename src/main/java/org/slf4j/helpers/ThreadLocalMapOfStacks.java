package org.slf4j.helpers;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class ThreadLocalMapOfStacks {
   final ThreadLocal<Map<String, Deque<String>>> tlMapOfStacks = new ThreadLocal();

   public void pushByKey(String var1, String var2) {
      if (var1 != null) {
         Object var3 = (Map)this.tlMapOfStacks.get();
         if (var3 == null) {
            var3 = new HashMap();
            this.tlMapOfStacks.set(var3);
         }

         Object var4 = (Deque)((Map)var3).get(var1);
         if (var4 == null) {
            var4 = new ArrayDeque();
         }

         ((Deque)var4).push(var2);
         ((Map)var3).put(var1, var4);
      }
   }

   public String popByKey(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Map var2 = (Map)this.tlMapOfStacks.get();
         if (var2 == null) {
            return null;
         } else {
            Deque var3 = (Deque)var2.get(var1);
            return var3 == null ? null : (String)var3.pop();
         }
      }
   }

   public Deque<String> getCopyOfDequeByKey(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Map var2 = (Map)this.tlMapOfStacks.get();
         if (var2 == null) {
            return null;
         } else {
            Deque var3 = (Deque)var2.get(var1);
            return var3 == null ? null : new ArrayDeque(var3);
         }
      }
   }

   public void clearDequeByKey(String var1) {
      if (var1 != null) {
         Map var2 = (Map)this.tlMapOfStacks.get();
         if (var2 != null) {
            Deque var3 = (Deque)var2.get(var1);
            if (var3 != null) {
               var3.clear();
            }
         }
      }
   }
}
