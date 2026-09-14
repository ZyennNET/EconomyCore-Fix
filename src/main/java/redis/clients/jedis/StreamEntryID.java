package redis.clients.jedis;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import redis.clients.jedis.util.SafeEncoder;

public class StreamEntryID implements Comparable<StreamEntryID>, Serializable {
   private static final long serialVersionUID = 1L;
   private long time;
   private long sequence;
   public static final StreamEntryID NEW_ENTRY = new StreamEntryID() {
      private static final long serialVersionUID = 1L;

      public String toString() {
         return "*";
      }
   };
   public static final StreamEntryID LAST_ENTRY = new StreamEntryID() {
      private static final long serialVersionUID = 1L;

      public String toString() {
         return "$";
      }
   };
   public static final StreamEntryID UNRECEIVED_ENTRY = new StreamEntryID() {
      private static final long serialVersionUID = 1L;

      public String toString() {
         return ">";
      }
   };
   public static final StreamEntryID MINIMUM_ID = new StreamEntryID() {
      private static final long serialVersionUID = 1L;

      public String toString() {
         return "-";
      }
   };
   public static final StreamEntryID MAXIMUM_ID = new StreamEntryID() {
      private static final long serialVersionUID = 1L;

      public String toString() {
         return "+";
      }
   };

   public StreamEntryID() {
      this(0L, 0L);
   }

   public StreamEntryID(byte[] var1) {
      this(SafeEncoder.encode(var1));
   }

   public StreamEntryID(String var1) {
      String[] var2 = var1.split("-");
      this.time = Long.parseLong(var2[0]);
      this.sequence = Long.parseLong(var2[1]);
   }

   public StreamEntryID(long var1) {
      this(var1, 0L);
   }

   public StreamEntryID(long var1, long var3) {
      this.time = var1;
      this.sequence = var3;
   }

   public String toString() {
      return this.time + "-" + this.sequence;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         StreamEntryID var2 = (StreamEntryID)var1;
         return this.time == var2.time && this.sequence == var2.sequence;
      }
   }

   public int hashCode() {
      return this.toString().hashCode();
   }

   public int compareTo(StreamEntryID var1) {
      int var2 = Long.compare(this.time, var1.time);
      return var2 != 0 ? var2 : Long.compare(this.sequence, var1.sequence);
   }

   public long getTime() {
      return this.time;
   }

   public long getSequence() {
      return this.sequence;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeLong(this.time);
      var1.writeLong(this.sequence);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.time = var1.readLong();
      this.sequence = var1.readLong();
   }
}
