package com.google.gson.stream;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.FormattingStyle;
import com.google.gson.Strictness;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class JsonWriter implements Closeable, Flushable {
   private static final Pattern VALID_JSON_NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");
   private static final String[] REPLACEMENT_CHARS = new String[128];
   private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
   private final Writer out;
   private int[] stack = new int[32];
   private int stackSize = 0;
   private FormattingStyle formattingStyle;
   private String formattedColon;
   private String formattedComma;
   private boolean usesEmptyNewlineAndIndent;
   private Strictness strictness;
   private boolean htmlSafe;
   private String deferredName;
   private boolean serializeNulls;

   public JsonWriter(Writer var1) {
      this.push(6);
      this.strictness = Strictness.LEGACY_STRICT;
      this.serializeNulls = true;
      this.out = (Writer)Objects.requireNonNull(var1, "out == null");
      this.setFormattingStyle(FormattingStyle.COMPACT);
   }

   public final void setIndent(String var1) {
      if (var1.isEmpty()) {
         this.setFormattingStyle(FormattingStyle.COMPACT);
      } else {
         this.setFormattingStyle(FormattingStyle.PRETTY.withIndent(var1));
      }

   }

   public final void setFormattingStyle(FormattingStyle var1) {
      this.formattingStyle = (FormattingStyle)Objects.requireNonNull(var1);
      this.formattedComma = ",";
      if (this.formattingStyle.usesSpaceAfterSeparators()) {
         this.formattedColon = ": ";
         if (this.formattingStyle.getNewline().isEmpty()) {
            this.formattedComma = ", ";
         }
      } else {
         this.formattedColon = ":";
      }

      this.usesEmptyNewlineAndIndent = this.formattingStyle.getNewline().isEmpty() && this.formattingStyle.getIndent().isEmpty();
   }

   public final FormattingStyle getFormattingStyle() {
      return this.formattingStyle;
   }

   @Deprecated
   public final void setLenient(boolean var1) {
      this.setStrictness(var1 ? Strictness.LENIENT : Strictness.LEGACY_STRICT);
   }

   public boolean isLenient() {
      return this.strictness == Strictness.LENIENT;
   }

   public final void setStrictness(Strictness var1) {
      this.strictness = (Strictness)Objects.requireNonNull(var1);
   }

   public final Strictness getStrictness() {
      return this.strictness;
   }

   public final void setHtmlSafe(boolean var1) {
      this.htmlSafe = var1;
   }

   public final boolean isHtmlSafe() {
      return this.htmlSafe;
   }

   public final void setSerializeNulls(boolean var1) {
      this.serializeNulls = var1;
   }

   public final boolean getSerializeNulls() {
      return this.serializeNulls;
   }

   @CanIgnoreReturnValue
   public JsonWriter beginArray() throws IOException {
      this.writeDeferredName();
      return this.openScope(1, '[');
   }

   @CanIgnoreReturnValue
   public JsonWriter endArray() throws IOException {
      return this.closeScope(1, 2, ']');
   }

   @CanIgnoreReturnValue
   public JsonWriter beginObject() throws IOException {
      this.writeDeferredName();
      return this.openScope(3, '{');
   }

   @CanIgnoreReturnValue
   public JsonWriter endObject() throws IOException {
      return this.closeScope(3, 5, '}');
   }

   @CanIgnoreReturnValue
   private JsonWriter openScope(int var1, char var2) throws IOException {
      this.beforeValue();
      this.push(var1);
      this.out.write(var2);
      return this;
   }

   @CanIgnoreReturnValue
   private JsonWriter closeScope(int var1, int var2, char var3) throws IOException {
      int var4 = this.peek();
      if (var4 != var2 && var4 != var1) {
         throw new IllegalStateException("Nesting problem.");
      } else if (this.deferredName != null) {
         throw new IllegalStateException("Dangling name: " + this.deferredName);
      } else {
         --this.stackSize;
         if (var4 == var2) {
            this.newline();
         }

         this.out.write(var3);
         return this;
      }
   }

   private void push(int var1) {
      if (this.stackSize == this.stack.length) {
         this.stack = Arrays.copyOf(this.stack, this.stackSize * 2);
      }

      this.stack[this.stackSize++] = var1;
   }

   private int peek() {
      if (this.stackSize == 0) {
         throw new IllegalStateException("JsonWriter is closed.");
      } else {
         return this.stack[this.stackSize - 1];
      }
   }

   private void replaceTop(int var1) {
      this.stack[this.stackSize - 1] = var1;
   }

   @CanIgnoreReturnValue
   public JsonWriter name(String var1) throws IOException {
      Objects.requireNonNull(var1, "name == null");
      if (this.deferredName != null) {
         throw new IllegalStateException("Already wrote a name, expecting a value.");
      } else {
         int var2 = this.peek();
         if (var2 != 3 && var2 != 5) {
            throw new IllegalStateException("Please begin an object before writing a name.");
         } else {
            this.deferredName = var1;
            return this;
         }
      }
   }

   private void writeDeferredName() throws IOException {
      if (this.deferredName != null) {
         this.beforeName();
         this.string(this.deferredName);
         this.deferredName = null;
      }

   }

   @CanIgnoreReturnValue
   public JsonWriter value(String var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         this.writeDeferredName();
         this.beforeValue();
         this.string(var1);
         return this;
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(boolean var1) throws IOException {
      this.writeDeferredName();
      this.beforeValue();
      this.out.write(var1 ? "true" : "false");
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter value(Boolean var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         this.writeDeferredName();
         this.beforeValue();
         this.out.write(var1 ? "true" : "false");
         return this;
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(float var1) throws IOException {
      this.writeDeferredName();
      if (this.strictness == Strictness.LENIENT || !Float.isNaN(var1) && !Float.isInfinite(var1)) {
         this.beforeValue();
         this.out.append(Float.toString(var1));
         return this;
      } else {
         throw new IllegalArgumentException("Numeric values must be finite, but was " + var1);
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(double var1) throws IOException {
      this.writeDeferredName();
      if (this.strictness == Strictness.LENIENT || !Double.isNaN(var1) && !Double.isInfinite(var1)) {
         this.beforeValue();
         this.out.append(Double.toString(var1));
         return this;
      } else {
         throw new IllegalArgumentException("Numeric values must be finite, but was " + var1);
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter value(long var1) throws IOException {
      this.writeDeferredName();
      this.beforeValue();
      this.out.write(Long.toString(var1));
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter value(Number var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         this.writeDeferredName();
         String var2 = var1.toString();
         if (!var2.equals("-Infinity") && !var2.equals("Infinity") && !var2.equals("NaN")) {
            Class var3 = var1.getClass();
            if (!isTrustedNumberType(var3) && !VALID_JSON_NUMBER_PATTERN.matcher(var2).matches()) {
               throw new IllegalArgumentException("String created by " + var3 + " is not a valid JSON number: " + var2);
            }
         } else if (this.strictness != Strictness.LENIENT) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + var2);
         }

         this.beforeValue();
         this.out.append(var2);
         return this;
      }
   }

   @CanIgnoreReturnValue
   public JsonWriter nullValue() throws IOException {
      if (this.deferredName != null) {
         if (!this.serializeNulls) {
            this.deferredName = null;
            return this;
         }

         this.writeDeferredName();
      }

      this.beforeValue();
      this.out.write("null");
      return this;
   }

   @CanIgnoreReturnValue
   public JsonWriter jsonValue(String var1) throws IOException {
      if (var1 == null) {
         return this.nullValue();
      } else {
         this.writeDeferredName();
         this.beforeValue();
         this.out.append(var1);
         return this;
      }
   }

   public void flush() throws IOException {
      if (this.stackSize == 0) {
         throw new IllegalStateException("JsonWriter is closed.");
      } else {
         this.out.flush();
      }
   }

   public void close() throws IOException {
      this.out.close();
      int var1 = this.stackSize;
      if (var1 <= 1 && (var1 != 1 || this.stack[var1 - 1] == 7)) {
         this.stackSize = 0;
      } else {
         throw new IOException("Incomplete document");
      }
   }

   private static boolean isTrustedNumberType(Class<? extends Number> var0) {
      return var0 == Integer.class || var0 == Long.class || var0 == Double.class || var0 == Float.class || var0 == Byte.class || var0 == Short.class || var0 == BigDecimal.class || var0 == BigInteger.class || var0 == AtomicInteger.class || var0 == AtomicLong.class;
   }

   private void string(String var1) throws IOException {
      String[] var2 = this.htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
      this.out.write(34);
      int var3 = 0;
      int var4 = var1.length();

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var1.charAt(var5);
         String var7;
         if (var6 < 128) {
            var7 = var2[var6];
            if (var7 == null) {
               continue;
            }
         } else if (var6 == 8232) {
            var7 = "\\u2028";
         } else {
            if (var6 != 8233) {
               continue;
            }

            var7 = "\\u2029";
         }

         if (var3 < var5) {
            this.out.write(var1, var3, var5 - var3);
         }

         this.out.write(var7);
         var3 = var5 + 1;
      }

      if (var3 < var4) {
         this.out.write(var1, var3, var4 - var3);
      }

      this.out.write(34);
   }

   private void newline() throws IOException {
      if (!this.usesEmptyNewlineAndIndent) {
         this.out.write(this.formattingStyle.getNewline());
         int var1 = 1;

         for(int var2 = this.stackSize; var1 < var2; ++var1) {
            this.out.write(this.formattingStyle.getIndent());
         }

      }
   }

   private void beforeName() throws IOException {
      int var1 = this.peek();
      if (var1 == 5) {
         this.out.write(this.formattedComma);
      } else if (var1 != 3) {
         throw new IllegalStateException("Nesting problem.");
      }

      this.newline();
      this.replaceTop(4);
   }

   private void beforeValue() throws IOException {
      switch (this.peek()) {
         case 1:
            this.replaceTop(2);
            this.newline();
            break;
         case 2:
            this.out.append(this.formattedComma);
            this.newline();
            break;
         case 3:
         case 5:
         default:
            throw new IllegalStateException("Nesting problem.");
         case 4:
            this.out.append(this.formattedColon);
            this.replaceTop(5);
            break;
         case 7:
            if (this.strictness != Strictness.LENIENT) {
               throw new IllegalStateException("JSON must have only one top-level value.");
            }
         case 6:
            this.replaceTop(7);
      }

   }

   static {
      for(int var0 = 0; var0 <= 31; ++var0) {
         REPLACEMENT_CHARS[var0] = String.format("\\u%04x", var0);
      }

      REPLACEMENT_CHARS[34] = "\\\"";
      REPLACEMENT_CHARS[92] = "\\\\";
      REPLACEMENT_CHARS[9] = "\\t";
      REPLACEMENT_CHARS[8] = "\\b";
      REPLACEMENT_CHARS[10] = "\\n";
      REPLACEMENT_CHARS[13] = "\\r";
      REPLACEMENT_CHARS[12] = "\\f";
      HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
      HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
      HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
      HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
      HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
      HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
   }
}
