package redis.clients.jedis.resps;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import redis.clients.jedis.StreamEntryID;

public class StreamEntry implements Serializable {
   private static final long serialVersionUID = 1L;
   private StreamEntryID id;
   private Map<String, String> fields;

   public StreamEntry(StreamEntryID var1, Map<String, String> var2) {
      this.id = var1;
      this.fields = var2;
   }

   public StreamEntryID getID() {
      return this.id;
   }

   public Map<String, String> getFields() {
      return this.fields;
   }

   public String toString() {
      return this.id + " " + this.fields;
   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      var1.writeUnshared(this.id);
      var1.writeUnshared(this.fields);
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      this.id = (StreamEntryID)var1.readUnshared();
      this.fields = (Map)var1.readUnshared();
   }
}
