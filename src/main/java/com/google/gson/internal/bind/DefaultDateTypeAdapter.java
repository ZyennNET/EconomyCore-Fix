package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.JavaVersion;
import com.google.gson.internal.PreJava9DateFormatProvider;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public final class DefaultDateTypeAdapter<T extends Date> extends TypeAdapter<T> {
   private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
   public static final TypeAdapterFactory DEFAULT_STYLE_FACTORY = new TypeAdapterFactory() {
      public <T> TypeAdapter<T> create(Gson var1, TypeToken<T> var2) {
         return var2.getRawType() == Date.class ? new DefaultDateTypeAdapter(DefaultDateTypeAdapter.DateType.DATE, 2, 2) : null;
      }

      public String toString() {
         return "DefaultDateTypeAdapter#DEFAULT_STYLE_FACTORY";
      }
   };
   private final DateType<T> dateType;
   private final List<DateFormat> dateFormats;

   private DefaultDateTypeAdapter(DateType<T> var1, String var2) {
      this.dateFormats = new ArrayList();
      this.dateType = (DateType)Objects.requireNonNull(var1);
      this.dateFormats.add(new SimpleDateFormat(var2, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(new SimpleDateFormat(var2));
      }

   }

   private DefaultDateTypeAdapter(DateType<T> var1, int var2, int var3) {
      this.dateFormats = new ArrayList();
      this.dateType = (DateType)Objects.requireNonNull(var1);
      this.dateFormats.add(DateFormat.getDateTimeInstance(var2, var3, Locale.US));
      if (!Locale.getDefault().equals(Locale.US)) {
         this.dateFormats.add(DateFormat.getDateTimeInstance(var2, var3));
      }

      if (JavaVersion.isJava9OrLater()) {
         this.dateFormats.add(PreJava9DateFormatProvider.getUsDateTimeFormat(var2, var3));
      }

   }

   public void write(JsonWriter var1, Date var2) throws IOException {
      if (var2 == null) {
         var1.nullValue();
      } else {
         DateFormat var3 = (DateFormat)this.dateFormats.get(0);
         String var4;
         synchronized(this.dateFormats) {
            var4 = var3.format(var2);
         }

         var1.value(var4);
      }
   }

   public T read(JsonReader var1) throws IOException {
      if (var1.peek() == JsonToken.NULL) {
         var1.nextNull();
         return null;
      } else {
         Date var2 = this.deserializeToDate(var1);
         return this.dateType.deserialize(var2);
      }
   }

   private Date deserializeToDate(JsonReader var1) throws IOException {
      String var2 = var1.nextString();
      synchronized(this.dateFormats) {
         for(DateFormat var5 : this.dateFormats) {
            TimeZone var6 = var5.getTimeZone();

            try {
               Date var7 = var5.parse(var2);
               return var7;
            } catch (ParseException var15) {
            } finally {
               var5.setTimeZone(var6);
            }
         }
      }

      try {
         return ISO8601Utils.parse(var2, new ParsePosition(0));
      } catch (ParseException var14) {
         throw new JsonSyntaxException("Failed parsing '" + var2 + "' as Date; at path " + var1.getPreviousPath(), var14);
      }
   }

   public String toString() {
      DateFormat var1 = (DateFormat)this.dateFormats.get(0);
      return var1 instanceof SimpleDateFormat ? "DefaultDateTypeAdapter(" + ((SimpleDateFormat)var1).toPattern() + ')' : "DefaultDateTypeAdapter(" + var1.getClass().getSimpleName() + ')';
   }

   public abstract static class DateType<T extends Date> {
      public static final DateType<Date> DATE = new DateType<Date>(Date.class) {
         protected Date deserialize(Date var1) {
            return var1;
         }
      };
      private final Class<T> dateClass;

      protected DateType(Class<T> var1) {
         this.dateClass = var1;
      }

      protected abstract T deserialize(Date var1);

      private TypeAdapterFactory createFactory(DefaultDateTypeAdapter<T> var1) {
         return TypeAdapters.newFactory(this.dateClass, var1);
      }

      public final TypeAdapterFactory createAdapterFactory(String var1) {
         return this.createFactory(new DefaultDateTypeAdapter(this, var1));
      }

      public final TypeAdapterFactory createAdapterFactory(int var1, int var2) {
         return this.createFactory(new DefaultDateTypeAdapter(this, var1, var2));
      }
   }
}
