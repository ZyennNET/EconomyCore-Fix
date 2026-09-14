package org.apache.commons.pool2.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Objects;
import org.apache.commons.pool2.PooledObject;

public class DefaultPooledObjectInfo implements DefaultPooledObjectInfoMBean {
   private static final String PATTERN = "yyyy-MM-dd HH:mm:ss Z";
   private final PooledObject<?> pooledObject;

   public DefaultPooledObjectInfo(PooledObject<?> var1) {
      this.pooledObject = (PooledObject)Objects.requireNonNull(var1, "pooledObject");
   }

   public long getBorrowedCount() {
      return this.pooledObject.getBorrowedCount();
   }

   public long getCreateTime() {
      return this.pooledObject.getCreateInstant().toEpochMilli();
   }

   public String getCreateTimeFormatted() {
      return this.getTimeMillisFormatted(this.getCreateTime());
   }

   public long getLastBorrowTime() {
      return this.pooledObject.getLastBorrowInstant().toEpochMilli();
   }

   public String getLastBorrowTimeFormatted() {
      return this.getTimeMillisFormatted(this.getLastBorrowTime());
   }

   public String getLastBorrowTrace() {
      StringWriter var1 = new StringWriter();
      this.pooledObject.printStackTrace(new PrintWriter(var1));
      return var1.toString();
   }

   public long getLastReturnTime() {
      return this.pooledObject.getLastReturnInstant().toEpochMilli();
   }

   public String getLastReturnTimeFormatted() {
      return this.getTimeMillisFormatted(this.getLastReturnTime());
   }

   public String getPooledObjectToString() {
      return Objects.toString(this.pooledObject.getObject(), (String)null);
   }

   public String getPooledObjectType() {
      Object var1 = this.pooledObject.getObject();
      return var1 != null ? var1.getClass().getName() : null;
   }

   private String getTimeMillisFormatted(long var1) {
      return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")).format(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("DefaultPooledObjectInfo [pooledObject=");
      var1.append(this.pooledObject);
      var1.append("]");
      return var1.toString();
   }
}
