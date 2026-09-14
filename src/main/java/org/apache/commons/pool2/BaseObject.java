package org.apache.commons.pool2;

public abstract class BaseObject {
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getClass().getSimpleName());
      var1.append(" [");
      this.toStringAppendFields(var1);
      var1.append("]");
      return var1.toString();
   }

   protected void toStringAppendFields(StringBuilder var1) {
   }
}
