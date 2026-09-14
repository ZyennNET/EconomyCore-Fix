package com.google.gson.internal.bind;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JsonTreeWriter extends JsonWriter {
   private static final Writer UNWRITABLE_WRITER = new Writer() {
      public void write(char[] var1, int var2, int var3) {
         throw new AssertionError();
      }

      public void flush() {
         throw new AssertionError();
      }

      public void close() {
         throw new AssertionError();
      }
   };
   private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
   private final List<JsonElement> stack = new ArrayList();
   private String pendingName;
   private JsonElement product;

   public JsonTreeWriter() {
      super(UNWRITABLE_WRITER);
      this.product = JsonNull.INSTANCE;
   }

   public JsonElement get() {
      if (!this.stack.isEmpty()) {
         throw new IllegalStateException("Expected one JSON element but was " + this.stack);
      } else {
         return this.product;
      }
   }

   private JsonElement peek() {
      return (JsonElement)this.stack.get(this.stack.size() - 1);
   }

   private void put(JsonElement var1) {
      if (this.pendingName != null) {
         if (!var1.isJsonNull() || this.getSerializeNulls()) {
            JsonObject var2 = (JsonObject)this.peek();
            var2.add(this.pendingName, var1);
         }

         this.pendingName = null;
      } else if (this.stack.isEmpty()) {
         this.product = var1;
      } else {
         JsonElement var3 = this.peek();
         if (!(var3 instanceof JsonArray)) {
            throw new IllegalStateException();
         }

         ((JsonArray)var3).add(var1);
      }

   }

   @CanIgnoreReturnValue
   public JsonWriter beginArray() throws IOException {
      JsonArray var1 = new JsonArray();
      this.put(var1);
      this.stack.add(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter endArray() throws IOException {
      if (!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement var1 = this.peek();
         if (var1 instanceof JsonArray) {
            this.stack.remove(this.stack.size() - 1);
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter beginObject() throws IOException {
      JsonObject var1 = new JsonObject();
      this.put(var1);
      this.stack.add(var1);
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter endObject() throws IOException {
      if (!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement var1 = this.peek();
         if (var1 instanceof JsonObject) {
            this.stack.remove(this.stack.size() - 1);
            return this;
         } else {
            throw new IllegalStateException();
         }
      } else {
         throw new IllegalStateException();
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter name(String var1) throws IOException {
      Objects.requireNonNull(var1, "name == null");
      if (!this.stack.isEmpty() && this.pendingName == null) {
         JsonElement var2 = this.peek();
         if (var2 instanceof JsonObject) {
            this.pendingName = var1;
            return this;
         } else {
            throw new IllegalStateException("Please begin an object before writing a name.");
         }
      } else {
         throw new IllegalStateException("Did not expect a name");
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(String var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         this.put(new JsonPrimitive(var1));
         return this;
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(boolean var1) throws IOException {
      this.put(new JsonPrimitive(var1));
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter value(Boolean var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         this.put(new JsonPrimitive(var1));
         return this;
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(float var1) throws IOException {
      if (this.isLenient() || !Float.isNaN(var1) && !Float.isInfinite(var1)) {
         this.put(new JsonPrimitive(var1));
         return this;
      } else {
         throw new IllegalArgumentException("JSON forbids NaN and infinities: " + var1);
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(double var1) throws IOException {
      if (this.isLenient() || !Double.isNaN(var1) && !Double.isInfinite(var1)) {
         this.put(new JsonPrimitive(var1));
         return this;
      } else {
         throw new IllegalArgumentException("JSON forbids NaN and infinities: " + var1);
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(long var1) throws IOException {
      this.put(new JsonPrimitive(var1));
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter value(Number var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         if (!this.isLenient()) {
            double var2 = var1.doubleValue();
            if (Double.isNaN(var2) || Double.isInfinite(var2)) {
               throw new IllegalArgumentException("JSON forbids NaN and infinities: " + var1);
            }
         }

         this.put(new JsonPrimitive(var1));
         return this;
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter nullValue() throws IOException {
      this.put(JsonNull.INSTANCE);
      return this;
   }

   public JsonWriter jsonValue(String var1) throws IOException {
      throw new UnsupportedOperationException();
   }

   public void flush() throws IOException {
   }

   public void close() throws IOException {
      if (!this.stack.isEmpty()) {
         throw new IOException("Incomplete document");
      } else {
         this.stack.add(SENTINEL_CLOSED);
      }
   }
}
