package com.google.gson.internal.sql;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

final class SqlTimeTypeAdapter extends TypeAdapter<Time> {
   static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         return var2.getRawType() == Time.class ? new SqlTimeTypeAdapter() : null;
      }
   };
   private final DateFormat format;

   private SqlTimeTypeAdapter() {
      this.format = new SimpleDateFormat("hh:mm:ss a");
   }

   public Time read(JsonReader var1) throws IOException {
      if (var1.peek() == JsonToken.NULL) {
         var1.nextNull();
         return null;
      } else {
         String var2 = var1.nextString();
         synchronized(this) {
            TimeZone var4 = this.format.getTimeZone();

            Time var6;
            try {
               Date var5 = this.format.parse(var2);
               var6 = new Time(var5.getTime());
            } catch (ParseException var12) {
               throw new JsonSyntaxException("Failed parsing '" + var2 + "' as SQL Time; at path " + var1.getPreviousPath(), var12);
            } finally {
               this.format.setTimeZone(var4);
            }

            return var6;
         }
      }
   }

   public void write(JsonWriter var1, Time var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         String var3;
         synchronized(this) {
            var3 = this.format.format(var2);
         }

         var1.value(var3);
      }
   }
}
