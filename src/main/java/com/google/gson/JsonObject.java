package com.google.gson;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.internal.LinkedTreeMap;
import java.util.Map;
import java.util.Set;

public final class JsonObject extends JsonElement {
   private final LinkedTreeMap<String, JsonElement> members = new LinkedTreeMap<String, JsonElement>(false);

   public JsonObject deepCopy() {
      JsonObject var1 = new JsonObject();

      for(Map.Entry var3 : this.members.entrySet()) {
         var1.add((String)var3.getKey(), ((JsonElement)var3.getValue()).deepCopy());
      }

      return var1;
   }

   public void add(String var1, JsonElement var2) {
      this.members.put(var1, var2 == null ? JsonNull.INSTANCE : var2);
   }

   @CanIgnoreReturnValue
   public JsonElement remove(String var1) {
      return this.members.remove(var1);
   }

   public void addProperty(String var1, String var2) {
      this.add(var1, (JsonElement)(var2 == null ? JsonNull.INSTANCE : new JsonPrimitive(var2)));
   }

   public void addProperty(String var1, Number var2) {
      this.add(var1, (JsonElement)(var2 == null ? JsonNull.INSTANCE : new JsonPrimitive(var2)));
   }

   public void addProperty(String var1, Boolean var2) {
      this.add(var1, (JsonElement)(var2 == null ? JsonNull.INSTANCE : new JsonPrimitive(var2)));
   }

   public void addProperty(String var1, Character var2) {
      this.add(var1, (JsonElement)(var2 == null ? JsonNull.INSTANCE : new JsonPrimitive(var2)));
   }

   public Set<Map.Entry<String, JsonElement>> entrySet() {
      return this.members.entrySet();
   }

   public Set<String> keySet() {
      return this.members.keySet();
   }

   public int size() {
      return this.members.size();
   }

   public boolean isEmpty() {
      return this.members.size() == 0;
   }

   public boolean has(String var1) {
      return this.members.containsKey(var1);
   }

   public JsonElement get(String var1) {
      return this.members.get(var1);
   }

   public JsonPrimitive getAsJsonPrimitive(String var1) {
      return (JsonPrimitive)this.members.get(var1);
   }

   public JsonArray getAsJsonArray(String var1) {
      return (JsonArray)this.members.get(var1);
   }

   public JsonObject getAsJsonObject(String var1) {
      return (JsonObject)this.members.get(var1);
   }

   public Map<String, JsonElement> asMap() {
      return this.members;
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof JsonObject && ((JsonObject)var1).members.equals(this.members);
   }

   public int hashCode() {
      return this.members.hashCode();
   }
}
