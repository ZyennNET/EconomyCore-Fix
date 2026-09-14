package org.slf4j.helpers;

import java.util.Deque;
import java.util.Map;
import org.slf4j.spi.MDCAdapter;

public class NOPMDCAdapter implements MDCAdapter {
   public void clear() {
   }

   public String get(String var1) {
      return null;
   }

   public void put(String var1, String var2) {
   }

   public void remove(String var1) {
   }

   public Map<String, String> getCopyOfContextMap() {
      return null;
   }

   public void setContextMap(Map<String, String> var1) {
   }

   public void pushByKey(String var1, String var2) {
   }

   public String popByKey(String var1) {
      return null;
   }

   public Deque<String> getCopyOfDequeByKey(String var1) {
      return null;
   }

   public void clearDequeByKey(String var1) {
   }
}
