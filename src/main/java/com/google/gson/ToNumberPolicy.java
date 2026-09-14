package com.google.gson;

import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.NumberLimits;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.math.BigDecimal;

public enum ToNumberPolicy implements ToNumberStrategy {
   DOUBLE {
      public Double readNumber(JsonReader var1) throws IOException {
         return var1.nextDouble();
      }
   },
   LAZILY_PARSED_NUMBER {
      public Number readNumber(JsonReader var1) throws IOException {
         return new LazilyParsedNumber(var1.nextString());
      }
   },
   LONG_OR_DOUBLE {
      public Number readNumber(JsonReader var1) throws IOException, JsonParseException {
         String var2 = var1.nextString();
         if (var2.indexOf(46) >= 0) {
            return this.parseAsDouble(var2, var1);
         } else {
            try {
               return Long.parseLong(var2);
            } catch (NumberFormatException var4) {
               return this.parseAsDouble(var2, var1);
            }
         }
      }

      private Number parseAsDouble(String var1, JsonReader var2) throws IOException {
         try {
            Double var3 = Double.valueOf(var1);
            if ((var3.isInfinite() || var3.isNaN()) && !var2.isLenient()) {
               throw new MalformedJsonException("JSON forbids NaN and infinities: " + var3 + "; at path " + var2.getPreviousPath());
            } else {
               return var3;
            }
         } catch (NumberFormatException var4) {
            throw new JsonParseException("Cannot parse " + var1 + "; at path " + var2.getPreviousPath(), var4);
         }
      }
   },
   BIG_DECIMAL {
      public BigDecimal readNumber(JsonReader var1) throws IOException {
         String var2 = var1.nextString();

         try {
            return NumberLimits.parseBigDecimal(var2);
         } catch (NumberFormatException var4) {
            throw new JsonParseException("Cannot parse " + var2 + "; at path " + var1.getPreviousPath(), var4);
         }
      }
   };

   private ToNumberPolicy() {
   }
}
