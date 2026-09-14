package com.google.gson;

import com.google.errorprone.annotations.InlineMe;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public final class JsonParser {
   public static JsonElement parseString(String var0) throws JsonSyntaxException {
      return parseReader((Reader)(new StringReader(var0)));
   }

   public static JsonElement parseReader(Reader var0) throws JsonIOException, JsonSyntaxException {
      try {
         JsonReader var1 = new JsonReader(var0);
         JsonElement var2 = parseReader(var1);
         if (!var2.isJsonNull() && var1.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonSyntaxException("Did not consume the entire document.");
         } else {
            return var2;
         }
      } catch (MalformedJsonException var3) {
         throw new JsonSyntaxException(var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      } catch (NumberFormatException var5) {
         throw new JsonSyntaxException(var5);
      }
   }

   public static JsonElement parseReader(JsonReader var0) throws JsonIOException, JsonSyntaxException {
      Strictness var1 = var0.getStrictness();
      if (var1 == Strictness.LEGACY_STRICT) {
         var0.setStrictness(Strictness.LENIENT);
      }

      JsonElement var2;
      try {
         var2 = Streams.parse(var0);
      } catch (StackOverflowError var7) {
         throw new JsonParseException("Failed parsing JSON source: " + var0 + " to Json", var7);
      } catch (OutOfMemoryError var8) {
         throw new JsonParseException("Failed parsing JSON source: " + var0 + " to Json", var8);
      } finally {
         var0.setStrictness(var1);
      }

      return var2;
   }

   @Deprecated
   @InlineMe(
      replacement = "JsonParser.parseString(json)",
      imports = {"com.google.gson.JsonParser"}
   )
   public JsonElement parse(String var1) throws JsonSyntaxException {
      return parseString(var1);
   }

   @Deprecated
   @InlineMe(
      replacement = "JsonParser.parseReader(json)",
      imports = {"com.google.gson.JsonParser"}
   )
   public JsonElement parse(Reader var1) throws JsonIOException, JsonSyntaxException {
      return parseReader(var1);
   }

   @Deprecated
   @InlineMe(
      replacement = "JsonParser.parseReader(json)",
      imports = {"com.google.gson.JsonParser"}
   )
   public JsonElement parse(JsonReader var1) throws JsonIOException, JsonSyntaxException {
      return parseReader(var1);
   }
}
