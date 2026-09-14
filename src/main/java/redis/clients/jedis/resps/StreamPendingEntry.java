package redis.clients.jedis.resps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import redis.clients.jedis.StreamEntryID;

public class StreamPendingEntry implements Serializable {
   private static final long serialVersionUID = 1L;
   private StreamEntryID id;
   private String consumerName;
   private long idleTime;
   private long deliveredTimes;

   public StreamPendingEntry(StreamEntryID var1, String var2, long var3, long var5) {
      this.id = var1;
      this.consumerName = var2;
      this.idleTime = var3;
      this.deliveredTimes = var5;
   }

   public StreamEntryID getID() {
      return this.id;
   }

   public long getIdleTime() {
      return this.idleTime;
   }

   public long getDeliveredTimes() {
      return this.deliveredTimes;
   }

   public String getConsumerName() {
      return this.consumerName;
   }

   public String toString() {
      return this.id + " " + this.consumerName + " idle:" + this.idleTime + " times:" + this.deliveredTimes;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeUnshared(this.id);
      var1.writeUTF(this.consumerName);
      var1.writeLong(this.idleTime);
      var1.writeLong(this.deliveredTimes);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.id = (StreamEntryID)var1.readUnshared();
      this.consumerName = var1.readUTF();
      this.idleTime = var1.readLong();
      this.deliveredTimes = var1.readLong();
   }
}
