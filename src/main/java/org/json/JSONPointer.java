package org.json;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONPointer {
   private static final String ENCODING = "utf-8";
   private final List<String> refTokens;

   public static Builder builder() {
      return new Builder();
   }

   public JSONPointer(String var1) {
      if (var1 == null) {
         throw new NullPointerException("pointer cannot be null");
      } else if (!var1.isEmpty() && !var1.equals("#")) {
         String var7;
         if (var1.startsWith("#/")) {
            var7 = var1.substring(2);

            try {
               var7 = URLDecoder.decode(var7, "utf-8");
            } catch (UnsupportedEncodingException var6) {
               throw new RuntimeException(var6);
            }
         } else {
            if (!var1.startsWith("/")) {
               throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'");
            }

            var7 = var1.substring(1);
         }

         this.refTokens = new ArrayList();
         int var3 = -1;
         int var4 = 0;

         do {
            var4 = var3 + 1;
            var3 = var7.indexOf(47, var4);
            if (var4 != var3 && var4 != var7.length()) {
               if (var3 >= 0) {
                  String var5 = var7.substring(var4, var3);
                  this.refTokens.add(unescape(var5));
               } else {
                  String var9 = var7.substring(var4);
                  this.refTokens.add(unescape(var9));
               }
            } else {
               this.refTokens.add("");
            }
         } while(var3 >= 0);

      } else {
         this.refTokens = Collections.emptyList();
      }
   }

   public JSONPointer(List<String> var1) {
      this.refTokens = new ArrayList(var1);
   }

   private static String unescape(String var0) {
      return var0.replace("~1", "/").replace("~0", "~");
   }

   public Object queryFrom(Object var1) throws JSONPointerException {
      if (this.refTokens.isEmpty()) {
         return var1;
      } else {
         Object var2 = var1;

         for(String var4 : this.refTokens) {
            if (var2 instanceof JSONObject) {
               var2 = ((JSONObject)var2).opt(unescape(var4));
            } else {
               if (!(var2 instanceof JSONArray)) {
                  throw new JSONPointerException(String.format("value [%s] is not an array or object therefore its key %s cannot be resolved", var2, var4));
               }

               var2 = readByIndexToken(var2, var4);
            }
         }

         return var2;
      }
   }

   private static Object readByIndexToken(Object var0, String var1) throws JSONPointerException {
      try {
         int var2 = Integer.parseInt(var1);
         JSONArray var3 = (JSONArray)var0;
         if (var2 >= var3.length()) {
            throw new JSONPointerException(String.format("index %s is out of bounds - the array has %d elements", var1, var3.length()));
         } else {
            try {
               return var3.get(var2);
            } catch (JSONException var5) {
               throw new JSONPointerException("Error reading value at index position " + var2, var5);
            }
         }
      } catch (NumberFormatException var6) {
         throw new JSONPointerException(String.format("%s is not an array index", var1), var6);
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("");

      for(String var3 : this.refTokens) {
         var1.append('/').append(escape(var3));
      }

      return var1.toString();
   }

   private static String escape(String var0) {
      return var0.replace("~", "~0").replace("/", "~1");
   }

   public String toURIFragment() {
      try {
         StringBuilder var1 = new StringBuilder("#");

         for(String var3 : this.refTokens) {
            var1.append('/').append(URLEncoder.encode(var3, "utf-8"));
         }

         return var1.toString();
      } catch (UnsupportedEncodingException var4) {
         throw new RuntimeException(var4);
      }
   }

   public static class Builder {
      private final List<String> refTokens = new ArrayList();

      public JSONPointer build() {
         return new JSONPointer(this.refTokens);
      }

      public Builder append(String var1) {
         if (var1 == null) {
            throw new NullPointerException("token cannot be null");
         } else {
            this.refTokens.add(var1);
            return this;
         }
      }

      public Builder append(int var1) {
         this.refTokens.add(String.valueOf(var1));
         return this;
      }
   }
}
