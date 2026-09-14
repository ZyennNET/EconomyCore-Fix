package org.slf4j.event;

import java.util.Objects;

public class KeyValuePair {
   public final String key;
   public final Object value;

   public KeyValuePair(String var1, Object var2) {
      this.key = var1;
      this.value = var2;
   }

   public String toString() {
      return this.key + "=\"" + this.value + "\"";
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         KeyValuePair var2 = (KeyValuePair)var1;
         return Objects.equals(this.key, var2.key) && Objects.equals(this.value, var2.value);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.key, this.value});
   }
}
