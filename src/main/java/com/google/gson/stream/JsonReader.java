package com.google.gson.stream;

import com.google.gson.Strictness;
import com.google.gson.internal.JsonReaderInternalAccess;
import com.google.gson.internal.TroubleshootingGuide;
import com.google.gson.internal.bind.JsonTreeReader;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;

public class JsonReader implements Closeable {
   private static final long MIN_INCOMPLETE_INTEGER = -922337203685477580L;
   private static final int PEEKED_NONE = 0;
   private static final int PEEKED_BEGIN_OBJECT = 1;
   private static final int PEEKED_END_OBJECT = 2;
   private static final int PEEKED_BEGIN_ARRAY = 3;
   private static final int PEEKED_END_ARRAY = 4;
   private static final int PEEKED_TRUE = 5;
   private static final int PEEKED_FALSE = 6;
   private static final int PEEKED_NULL = 7;
   private static final int PEEKED_SINGLE_QUOTED = 8;
   private static final int PEEKED_DOUBLE_QUOTED = 9;
   private static final int PEEKED_UNQUOTED = 10;
   private static final int PEEKED_BUFFERED = 11;
   private static final int PEEKED_SINGLE_QUOTED_NAME = 12;
   private static final int PEEKED_DOUBLE_QUOTED_NAME = 13;
   private static final int PEEKED_UNQUOTED_NAME = 14;
   private static final int PEEKED_LONG = 15;
   private static final int PEEKED_NUMBER = 16;
   private static final int PEEKED_EOF = 17;
   private static final int NUMBER_CHAR_NONE = 0;
   private static final int NUMBER_CHAR_SIGN = 1;
   private static final int NUMBER_CHAR_DIGIT = 2;
   private static final int NUMBER_CHAR_DECIMAL = 3;
   private static final int NUMBER_CHAR_FRACTION_DIGIT = 4;
   private static final int NUMBER_CHAR_EXP_E = 5;
   private static final int NUMBER_CHAR_EXP_SIGN = 6;
   private static final int NUMBER_CHAR_EXP_DIGIT = 7;
   private final Reader in;
   private Strictness strictness;
   static final int BUFFER_SIZE = 1024;
   private final char[] buffer;
   private int pos;
   private int limit;
   private int lineNumber;
   private int lineStart;
   int peeked;
   private long peekedLong;
   private int peekedNumberLength;
   private String peekedString;
   private int[] stack;
   private int stackSize;
   private String[] pathNames;
   private int[] pathIndices;

   public JsonReader(Reader var1) {
      this.strictness = Strictness.LEGACY_STRICT;
      this.buffer = new char[1024];
      this.pos = 0;
      this.limit = 0;
      this.lineNumber = 0;
      this.lineStart = 0;
      this.peeked = 0;
      this.stack = new int[32];
      this.stackSize = 0;
      this.stack[this.stackSize++] = 6;
      this.pathNames = new String[32];
      this.pathIndices = new int[32];
      this.in = (Reader)Objects.requireNonNull(var1, "in == null");
   }

   @Deprecated
   public final void setLenient(boolean var1) {
      this.setStrictness(var1 ? Strictness.LENIENT : Strictness.LEGACY_STRICT);
   }

   public final boolean isLenient() {
      return this.strictness == Strictness.LENIENT;
   }

   public final void setStrictness(Strictness var1) {
      Objects.requireNonNull(var1);
      this.strictness = var1;
   }

   public final Strictness getStrictness() {
      return this.strictness;
   }

   public void beginArray() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 3) {
         this.push(1);
         this.pathIndices[this.stackSize - 1] = 0;
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("BEGIN_ARRAY");
      }
   }

   public void endArray() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 4) {
         --this.stackSize;
         int var10002 = this.pathIndices[this.stackSize - 1]++;
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("END_ARRAY");
      }
   }

   public void beginObject() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 1) {
         this.push(3);
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("BEGIN_OBJECT");
      }
   }

   public void endObject() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 2) {
         --this.stackSize;
         this.pathNames[this.stackSize] = null;
         int var10002 = this.pathIndices[this.stackSize - 1]++;
         this.peeked = 0;
      } else {
         throw this.unexpectedTokenError("END_OBJECT");
      }
   }

   public boolean hasNext() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      return var1 != 2 && var1 != 4 && var1 != 17;
   }

   public JsonToken peek() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      switch (var1) {
         case 1:
            return JsonToken.BEGIN_OBJECT;
         case 2:
            return JsonToken.END_OBJECT;
         case 3:
            return JsonToken.BEGIN_ARRAY;
         case 4:
            return JsonToken.END_ARRAY;
         case 5:
         case 6:
            return JsonToken.BOOLEAN;
         case 7:
            return JsonToken.NULL;
         case 8:
         case 9:
         case 10:
         case 11:
            return JsonToken.STRING;
         case 12:
         case 13:
         case 14:
            return JsonToken.NAME;
         case 15:
         case 16:
            return JsonToken.NUMBER;
         case 17:
            return JsonToken.END_DOCUMENT;
         default:
            throw new AssertionError();
      }
   }

   int doPeek() throws IOException {
      int var1 = this.stack[this.stackSize - 1];
      if (var1 == 1) {
         this.stack[this.stackSize - 1] = 2;
      } else if (var1 == 2) {
         int var7 = this.nextNonWhitespace(true);
         switch (var7) {
            case 44:
               break;
            case 59:
               this.checkLenient();
               break;
            case 93:
               return this.peeked = 4;
            default:
               throw this.syntaxError("Unterminated array");
         }
      } else {
         if (var1 == 3 || var1 == 5) {
            this.stack[this.stackSize - 1] = 4;
            if (var1 == 5) {
               int var5 = this.nextNonWhitespace(true);
               switch (var5) {
                  case 44:
                     break;
                  case 59:
                     this.checkLenient();
                     break;
                  case 125:
                     return this.peeked = 2;
                  default:
                     throw this.syntaxError("Unterminated object");
               }
            }

            int var6 = this.nextNonWhitespace(true);
            switch (var6) {
               case 34:
                  return this.peeked = 13;
               case 39:
                  this.checkLenient();
                  return this.peeked = 12;
               case 125:
                  if (var1 != 5) {
                     return this.peeked = 2;
                  }

                  throw this.syntaxError("Expected name");
               default:
                  this.checkLenient();
                  --this.pos;
                  if (this.isLiteral((char)var6)) {
                     return this.peeked = 14;
                  }

                  throw this.syntaxError("Expected name");
            }
         }

         if (var1 == 4) {
            this.stack[this.stackSize - 1] = 5;
            int var2 = this.nextNonWhitespace(true);
            switch (var2) {
               case 58:
                  break;
               case 61:
                  this.checkLenient();
                  if ((this.pos < this.limit || this.fillBuffer(1)) && this.buffer[this.pos] == '>') {
                     ++this.pos;
                  }
                  break;
               default:
                  throw this.syntaxError("Expected ':'");
            }
         } else if (var1 == 6) {
            if (this.strictness == Strictness.LENIENT) {
               this.consumeNonExecutePrefix();
            }

            this.stack[this.stackSize - 1] = 7;
         } else if (var1 == 7) {
            int var4 = this.nextNonWhitespace(false);
            if (var4 == -1) {
               return this.peeked = 17;
            }

            this.checkLenient();
            --this.pos;
         } else if (var1 == 8) {
            throw new IllegalStateException("JsonReader is closed");
         }
      }

      int var8 = this.nextNonWhitespace(true);
      switch (var8) {
         case 34:
            return this.peeked = 9;
         case 39:
            this.checkLenient();
            return this.peeked = 8;
         case 91:
            return this.peeked = 3;
         case 93:
            if (var1 == 1) {
               return this.peeked = 4;
            }
         case 44:
         case 59:
            if (var1 != 1 && var1 != 2) {
               throw this.syntaxError("Unexpected value");
            }

            this.checkLenient();
            --this.pos;
            return this.peeked = 7;
         case 123:
            return this.peeked = 1;
         default:
            --this.pos;
            int var3 = this.peekKeyword();
            if (var3 != 0) {
               return var3;
            } else {
               var3 = this.peekNumber();
               if (var3 != 0) {
                  return var3;
               } else if (!this.isLiteral(this.buffer[this.pos])) {
                  throw this.syntaxError("Expected value");
               } else {
                  this.checkLenient();
                  return this.peeked = 10;
               }
            }
      }
   }

   private int peekKeyword() throws IOException {
      char var1 = this.buffer[this.pos];
      String var2;
      String var3;
      byte var4;
      if (var1 != 't' && var1 != 'T') {
         if (var1 != 'f' && var1 != 'F') {
            if (var1 != 'n' && var1 != 'N') {
               return 0;
            }

            var2 = "null";
            var3 = "NULL";
            var4 = 7;
         } else {
            var2 = "false";
            var3 = "FALSE";
            var4 = 6;
         }
      } else {
         var2 = "true";
         var3 = "TRUE";
         var4 = 5;
      }

      boolean var5 = this.strictness != Strictness.STRICT;
      int var6 = var2.length();

      for(int var7 = 0; var7 < var6; ++var7) {
         if (this.pos + var7 >= this.limit && !this.fillBuffer(var7 + 1)) {
            return 0;
         }

         var1 = this.buffer[this.pos + var7];
         boolean var8 = var1 == var2.charAt(var7) || var5 && var1 == var3.charAt(var7);
         if (!var8) {
            return 0;
         }
      }

      if ((this.pos + var6 < this.limit || this.fillBuffer(var6 + 1)) && this.isLiteral(this.buffer[this.pos + var6])) {
         return 0;
      } else {
         this.pos += var6;
         return this.peeked = var4;
      }
   }

   private int peekNumber() throws IOException {
      char[] var1 = this.buffer;
      int var2 = this.pos;
      int var3 = this.limit;
      long var4 = 0L;
      boolean var6 = false;
      boolean var7 = true;
      byte var8 = 0;
      int var9 = 0;

      label132:
      while(true) {
         if (var2 + var9 == var3) {
            if (var9 == var1.length) {
               return 0;
            }

            if (!this.fillBuffer(var9 + 1)) {
               break;
            }

            var2 = this.pos;
            var3 = this.limit;
         }

         char var10 = var1[var2 + var9];
         switch (var10) {
            case '+':
               if (var8 != 5) {
                  return 0;
               }

               var8 = 6;
               break;
            case '-':
               if (var8 == 0) {
                  var6 = true;
                  var8 = 1;
               } else {
                  if (var8 != 5) {
                     return 0;
                  }

                  var8 = 6;
               }
               break;
            case '.':
               if (var8 != 2) {
                  return 0;
               }

               var8 = 3;
               break;
            case 'E':
            case 'e':
               if (var8 != 2 && var8 != 4) {
                  return 0;
               }

               var8 = 5;
               break;
            default:
               if (var10 < '0' || var10 > '9') {
                  if (this.isLiteral(var10)) {
                     return 0;
                  }
                  break label132;
               }

               if (var8 != 1 && var8 != 0) {
                  if (var8 == 2) {
                     if (var4 == 0L) {
                        return 0;
                     }

                     long var11 = var4 * 10L - (long)(var10 - 48);
                     var7 &= var4 > -922337203685477580L || var4 == -922337203685477580L && var11 < var4;
                     var4 = var11;
                  } else if (var8 == 3) {
                     var8 = 4;
                  } else if (var8 == 5 || var8 == 6) {
                     var8 = 7;
                  }
               } else {
                  var4 = (long)(-(var10 - 48));
                  var8 = 2;
               }
         }

         ++var9;
      }

      if (var8 != 2 || !var7 || var4 == Long.MIN_VALUE && !var6 || var4 == 0L && var6) {
         if (var8 != 2 && var8 != 4 && var8 != 7) {
            return 0;
         } else {
            this.peekedNumberLength = var9;
            return this.peeked = 16;
         }
      } else {
         this.peekedLong = var6 ? var4 : -var4;
         this.pos += var9;
         return this.peeked = 15;
      }
   }

   private boolean isLiteral(char var1) throws IOException {
      switch (var1) {
         case '#':
         case '/':
         case ';':
         case '=':
         case '\\':
            this.checkLenient();
         case '\t':
         case '\n':
         case '\f':
         case '\r':
         case ' ':
         case ',':
         case ':':
         case '[':
         case ']':
         case '{':
         case '}':
            return false;
         default:
            return true;
      }
   }

   public String nextName() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      String var2;
      if (var1 == 14) {
         var2 = this.nextUnquotedValue();
      } else if (var1 == 12) {
         var2 = this.nextQuotedValue('\'');
      } else {
         if (var1 != 13) {
            throw this.unexpectedTokenError("a name");
         }

         var2 = this.nextQuotedValue('"');
      }

      this.peeked = 0;
      this.pathNames[this.stackSize - 1] = var2;
      return var2;
   }

   public String nextString() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      String var2;
      if (var1 == 10) {
         var2 = this.nextUnquotedValue();
      } else if (var1 == 8) {
         var2 = this.nextQuotedValue('\'');
      } else if (var1 == 9) {
         var2 = this.nextQuotedValue('"');
      } else if (var1 == 11) {
         var2 = this.peekedString;
         this.peekedString = null;
      } else if (var1 == 15) {
         var2 = Long.toString(this.peekedLong);
      } else {
         if (var1 != 16) {
            throw this.unexpectedTokenError("a string");
         }

         var2 = new String(this.buffer, this.pos, this.peekedNumberLength);
         this.pos += this.peekedNumberLength;
      }

      this.peeked = 0;
      int var10002 = this.pathIndices[this.stackSize - 1]++;
      return var2;
   }

   public boolean nextBoolean() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 5) {
         this.peeked = 0;
         int var2 = this.pathIndices[this.stackSize - 1]++;
         return true;
      } else if (var1 == 6) {
         this.peeked = 0;
         int var10002 = this.pathIndices[this.stackSize - 1]++;
         return false;
      } else {
         throw this.unexpectedTokenError("a boolean");
      }
   }

   public void nextNull() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 7) {
         this.peeked = 0;
         int var10002 = this.pathIndices[this.stackSize - 1]++;
      } else {
         throw this.unexpectedTokenError("null");
      }
   }

   public double nextDouble() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 15) {
         this.peeked = 0;
         int var4 = this.pathIndices[this.stackSize - 1]++;
         return (double)this.peekedLong;
      } else {
         if (var1 == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
         } else if (var1 != 8 && var1 != 9) {
            if (var1 == 10) {
               this.peekedString = this.nextUnquotedValue();
            } else if (var1 != 11) {
               throw this.unexpectedTokenError("a double");
            }
         } else {
            this.peekedString = this.nextQuotedValue((char)(var1 == 8 ? '\'' : '"'));
         }

         this.peeked = 11;
         double var2 = Double.parseDouble(this.peekedString);
         if (this.strictness == Strictness.LENIENT || !Double.isNaN(var2) && !Double.isInfinite(var2)) {
            this.peekedString = null;
            this.peeked = 0;
            int var10002 = this.pathIndices[this.stackSize - 1]++;
            return var2;
         } else {
            throw this.syntaxError("JSON forbids NaN and infinities: " + var2);
         }
      }
   }

   public long nextLong() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 15) {
         this.peeked = 0;
         int var9 = this.pathIndices[this.stackSize - 1]++;
         return this.peekedLong;
      } else {
         if (var1 == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
         } else {
            if (var1 != 8 && var1 != 9 && var1 != 10) {
               throw this.unexpectedTokenError("a long");
            }

            if (var1 == 10) {
               this.peekedString = this.nextUnquotedValue();
            } else {
               this.peekedString = this.nextQuotedValue((char)(var1 == 8 ? '\'' : '"'));
            }

            try {
               long var7 = Long.parseLong(this.peekedString);
               this.peeked = 0;
               int var8 = this.pathIndices[this.stackSize - 1]++;
               return var7;
            } catch (NumberFormatException var6) {
            }
         }

         this.peeked = 11;
         double var2 = Double.parseDouble(this.peekedString);
         long var4 = (long)var2;
         if ((double)var4 != var2) {
            throw new NumberFormatException("Expected a long but was " + this.peekedString + this.locationString());
         } else {
            this.peekedString = null;
            this.peeked = 0;
            int var10002 = this.pathIndices[this.stackSize - 1]++;
            return var4;
         }
      }
   }

   private String nextQuotedValue(char var1) throws IOException {
      char[] var2 = this.buffer;
      StringBuilder var3 = null;

      do {
         int var4 = this.pos;
         int var5 = this.limit;
         int var6 = var4;

         while(var4 < var5) {
            char var7 = var2[var4++];
            if (this.strictness == Strictness.STRICT && var7 < ' ') {
               throw this.syntaxError("Unescaped control characters (\\u0000-\\u001F) are not allowed in strict mode");
            }

            if (var7 == var1) {
               this.pos = var4;
               int var11 = var4 - var6 - 1;
               if (var3 == null) {
                  return new String(var2, var6, var11);
               }

               var3.append(var2, var6, var11);
               return var3.toString();
            }

            if (var7 == '\\') {
               this.pos = var4;
               int var8 = var4 - var6 - 1;
               if (var3 == null) {
                  int var9 = (var8 + 1) * 2;
                  var3 = new StringBuilder(Math.max(var9, 16));
               }

               var3.append(var2, var6, var8);
               var3.append(this.readEscapeCharacter());
               var4 = this.pos;
               var5 = this.limit;
               var6 = var4;
            } else if (var7 == '\n') {
               ++this.lineNumber;
               this.lineStart = var4;
            }
         }

         if (var3 == null) {
            int var10 = (var4 - var6) * 2;
            var3 = new StringBuilder(Math.max(var10, 16));
         }

         var3.append(var2, var6, var4 - var6);
         this.pos = var4;
      } while(this.fillBuffer(1));

      throw this.syntaxError("Unterminated string");
   }

   private String nextUnquotedValue() throws IOException {
      StringBuilder var1 = null;
      int var2 = 0;

      label35:
      while(true) {
         if (this.pos + var2 < this.limit) {
            switch (this.buffer[this.pos + var2]) {
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
               case ',':
               case ':':
               case '[':
               case ']':
               case '{':
               case '}':
                  break label35;
               case '#':
               case '/':
               case ';':
               case '=':
               case '\\':
                  this.checkLenient();
                  break label35;
               default:
                  ++var2;
            }
         } else if (var2 < this.buffer.length) {
            if (this.fillBuffer(var2 + 1)) {
               continue;
            }
            break;
         } else {
            if (var1 == null) {
               var1 = new StringBuilder(Math.max(var2, 16));
            }

            var1.append(this.buffer, this.pos, var2);
            this.pos += var2;
            var2 = 0;
            if (!this.fillBuffer(1)) {
               break;
            }
         }
      }

      String var3 = var1 == null ? new String(this.buffer, this.pos, var2) : var1.append(this.buffer, this.pos, var2).toString();
      this.pos += var2;
      return var3;
   }

   private void skipQuotedValue(char var1) throws IOException {
      char[] var2 = this.buffer;

      do {
         int var3 = this.pos;
         int var4 = this.limit;

         while(var3 < var4) {
            char var5 = var2[var3++];
            if (var5 == var1) {
               this.pos = var3;
               return;
            }

            if (var5 == '\\') {
               this.pos = var3;
               char var6 = this.readEscapeCharacter();
               var3 = this.pos;
               var4 = this.limit;
            } else if (var5 == '\n') {
               ++this.lineNumber;
               this.lineStart = var3;
            }
         }

         this.pos = var3;
      } while(this.fillBuffer(1));

      throw this.syntaxError("Unterminated string");
   }

   private void skipUnquotedValue() throws IOException {
      do {
         int var1;
         for(var1 = 0; this.pos + var1 < this.limit; ++var1) {
            switch (this.buffer[this.pos + var1]) {
               case '#':
               case '/':
               case ';':
               case '=':
               case '\\':
                  this.checkLenient();
               case '\t':
               case '\n':
               case '\f':
               case '\r':
               case ' ':
               case ',':
               case ':':
               case '[':
               case ']':
               case '{':
               case '}':
                  this.pos += var1;
                  return;
               default:
            }
         }

         this.pos += var1;
      } while(this.fillBuffer(1));

   }

   public int nextInt() throws IOException {
      int var1 = this.peeked;
      if (var1 == 0) {
         var1 = this.doPeek();
      }

      if (var1 == 15) {
         int var7 = (int)this.peekedLong;
         if (this.peekedLong != (long)var7) {
            throw new NumberFormatException("Expected an int but was " + this.peekedLong + this.locationString());
         } else {
            this.peeked = 0;
            int var9 = this.pathIndices[this.stackSize - 1]++;
            return var7;
         }
      } else {
         if (var1 == 16) {
            this.peekedString = new String(this.buffer, this.pos, this.peekedNumberLength);
            this.pos += this.peekedNumberLength;
         } else {
            if (var1 != 8 && var1 != 9 && var1 != 10) {
               throw this.unexpectedTokenError("an int");
            }

            if (var1 == 10) {
               this.peekedString = this.nextUnquotedValue();
            } else {
               this.peekedString = this.nextQuotedValue((char)(var1 == 8 ? '\'' : '"'));
            }

            try {
               int var6 = Integer.parseInt(this.peekedString);
               this.peeked = 0;
               int var8 = this.pathIndices[this.stackSize - 1]++;
               return var6;
            } catch (NumberFormatException var5) {
            }
         }

         this.peeked = 11;
         double var3 = Double.parseDouble(this.peekedString);
         int var2 = (int)var3;
         if ((double)var2 != var3) {
            throw new NumberFormatException("Expected an int but was " + this.peekedString + this.locationString());
         } else {
            this.peekedString = null;
            this.peeked = 0;
            int var10002 = this.pathIndices[this.stackSize - 1]++;
            return var2;
         }
      }
   }

   public void close() throws IOException {
      this.peeked = 0;
      this.stack[0] = 8;
      this.stackSize = 1;
      this.in.close();
   }

   public void skipValue() throws IOException {
      int var1 = 0;

      do {
         int var2 = this.peeked;
         if (var2 == 0) {
            var2 = this.doPeek();
         }

         switch (var2) {
            case 1:
               this.push(3);
               ++var1;
               break;
            case 2:
               if (var1 == 0) {
                  this.pathNames[this.stackSize - 1] = null;
               }

               --this.stackSize;
               --var1;
               break;
            case 3:
               this.push(1);
               ++var1;
               break;
            case 4:
               --this.stackSize;
               --var1;
            case 5:
            case 6:
            case 7:
            case 11:
            case 15:
            default:
               break;
            case 8:
               this.skipQuotedValue('\'');
               break;
            case 9:
               this.skipQuotedValue('"');
               break;
            case 10:
               this.skipUnquotedValue();
               break;
            case 12:
               this.skipQuotedValue('\'');
               if (var1 == 0) {
                  this.pathNames[this.stackSize - 1] = "<skipped>";
               }
               break;
            case 13:
               this.skipQuotedValue('"');
               if (var1 == 0) {
                  this.pathNames[this.stackSize - 1] = "<skipped>";
               }
               break;
            case 14:
               this.skipUnquotedValue();
               if (var1 == 0) {
                  this.pathNames[this.stackSize - 1] = "<skipped>";
               }
               break;
            case 16:
               this.pos += this.peekedNumberLength;
               break;
            case 17:
               return;
         }

         this.peeked = 0;
      } while(var1 > 0);

      int var10002 = this.pathIndices[this.stackSize - 1]++;
   }

   private void push(int var1) {
      if (this.stackSize == this.stack.length) {
         int var2 = this.stackSize * 2;
         this.stack = Arrays.copyOf(this.stack, var2);
         this.pathIndices = Arrays.copyOf(this.pathIndices, var2);
         this.pathNames = (String[])Arrays.copyOf(this.pathNames, var2);
      }

      this.stack[this.stackSize++] = var1;
   }

   private boolean fillBuffer(int var1) throws IOException {
      char[] var2 = this.buffer;
      this.lineStart -= this.pos;
      if (this.limit != this.pos) {
         this.limit -= this.pos;
         System.arraycopy(var2, this.pos, var2, 0, this.limit);
      } else {
         this.limit = 0;
      }

      this.pos = 0;

      int var3;
      while((var3 = this.in.read(var2, this.limit, var2.length - this.limit)) != -1) {
         this.limit += var3;
         if (this.lineNumber == 0 && this.lineStart == 0 && this.limit > 0 && var2[0] == '\ufeff') {
            ++this.pos;
            ++this.lineStart;
            ++var1;
         }

         if (this.limit >= var1) {
            return true;
         }
      }

      return false;
   }

   private int nextNonWhitespace(boolean var1) throws IOException {
      char[] var2 = this.buffer;
      int var3 = this.pos;
      int var4 = this.limit;

      while(true) {
         if (var3 == var4) {
            this.pos = var3;
            if (!this.fillBuffer(1)) {
               if (var1) {
                  throw new EOFException("End of input" + this.locationString());
               }

               return -1;
            }

            var3 = this.pos;
            var4 = this.limit;
         }

         char var5 = var2[var3++];
         if (var5 == '\n') {
            ++this.lineNumber;
            this.lineStart = var3;
         } else if (var5 != ' ' && var5 != '\r' && var5 != '\t') {
            if (var5 == '/') {
               this.pos = var3;
               if (var3 == var4) {
                  --this.pos;
                  boolean var6 = this.fillBuffer(2);
                  ++this.pos;
                  if (!var6) {
                     return var5;
                  }
               }

               this.checkLenient();
               char var7 = var2[this.pos];
               switch (var7) {
                  case '*':
                     ++this.pos;
                     if (!this.skipTo("*/")) {
                        throw this.syntaxError("Unterminated comment");
                     }

                     var3 = this.pos + 2;
                     var4 = this.limit;
                     break;
                  case '/':
                     ++this.pos;
                     this.skipToEndOfLine();
                     var3 = this.pos;
                     var4 = this.limit;
                     break;
                  default:
                     return var5;
               }
            } else {
               if (var5 != '#') {
                  this.pos = var3;
                  return var5;
               }

               this.pos = var3;
               this.checkLenient();
               this.skipToEndOfLine();
               var3 = this.pos;
               var4 = this.limit;
            }
         }
      }
   }

   private void checkLenient() throws MalformedJsonException {
      if (this.strictness != Strictness.LENIENT) {
         throw this.syntaxError("Use JsonReader.setStrictness(Strictness.LENIENT) to accept malformed JSON");
      }
   }

   private void skipToEndOfLine() throws IOException {
      while(this.pos < this.limit || this.fillBuffer(1)) {
         char var1 = this.buffer[this.pos++];
         if (var1 == '\n') {
            ++this.lineNumber;
            this.lineStart = this.pos;
         } else if (var1 != '\r') {
            continue;
         }
         break;
      }

   }

   private boolean skipTo(String var1) throws IOException {
      label30:
      for(int var2 = var1.length(); this.pos + var2 <= this.limit || this.fillBuffer(var2); ++this.pos) {
         if (this.buffer[this.pos] == '\n') {
            ++this.lineNumber;
            this.lineStart = this.pos + 1;
         } else {
            for(int var3 = 0; var3 < var2; ++var3) {
               if (this.buffer[this.pos + var3] != var1.charAt(var3)) {
                  continue label30;
               }
            }

            return true;
         }
      }

      return false;
   }

   public String toString() {
      return this.getClass().getSimpleName() + this.locationString();
   }

   String locationString() {
      int var1 = this.lineNumber + 1;
      int var2 = this.pos - this.lineStart + 1;
      return " at line " + var1 + " column " + var2 + " path " + this.getPath();
   }

   private String getPath(boolean var1) {
      StringBuilder var2 = (new StringBuilder()).append('$');

      for(int var3 = 0; var3 < this.stackSize; ++var3) {
         int var4 = this.stack[var3];
         switch (var4) {
            case 1:
            case 2:
               int var5 = this.pathIndices[var3];
               if (var1 && var5 > 0 && var3 == this.stackSize - 1) {
                  --var5;
               }

               var2.append('[').append(var5).append(']');
               break;
            case 3:
            case 4:
            case 5:
               var2.append('.');
               if (this.pathNames[var3] != null) {
                  var2.append(this.pathNames[var3]);
               }
            case 6:
            case 7:
            case 8:
               break;
            default:
               throw new AssertionError("Unknown scope value: " + var4);
         }
      }

      return var2.toString();
   }

   public String getPath() {
      return this.getPath(false);
   }

   public String getPreviousPath() {
      return this.getPath(true);
   }

   private char readEscapeCharacter() throws IOException {
      if (this.pos == this.limit && !this.fillBuffer(1)) {
         throw this.syntaxError("Unterminated escape sequence");
      } else {
         char var1 = this.buffer[this.pos++];
         switch (var1) {
            case '\n':
               if (this.strictness == Strictness.STRICT) {
                  throw this.syntaxError("Cannot escape a newline character in strict mode");
               } else {
                  ++this.lineNumber;
                  this.lineStart = this.pos;
               }
            case '\'':
               if (this.strictness == Strictness.STRICT) {
                  throw this.syntaxError("Invalid escaped character \"'\" in strict mode");
               }
            case '"':
            case '/':
            case '\\':
               return var1;
            case 'b':
               return '\b';
            case 'f':
               return '\f';
            case 'n':
               return '\n';
            case 'r':
               return '\r';
            case 't':
               return '\t';
            case 'u':
               if (this.pos + 4 > this.limit && !this.fillBuffer(4)) {
                  throw this.syntaxError("Unterminated escape sequence");
               } else {
                  int var2 = 0;
                  int var3 = this.pos;

                  for(int var4 = var3 + 4; var3 < var4; ++var3) {
                     char var5 = this.buffer[var3];
                     var2 <<= 4;
                     if (var5 >= '0' && var5 <= '9') {
                        var2 += var5 - 48;
                     } else if (var5 >= 'a' && var5 <= 'f') {
                        var2 += var5 - 97 + 10;
                     } else {
                        if (var5 < 'A' || var5 > 'F') {
                           throw this.syntaxError("Malformed Unicode escape \\u" + new String(this.buffer, this.pos, 4));
                        }

                        var2 += var5 - 65 + 10;
                     }
                  }

                  this.pos += 4;
                  return (char)var2;
               }
            default:
               throw this.syntaxError("Invalid escape sequence");
         }
      }
   }

   private MalformedJsonException syntaxError(String var1) throws MalformedJsonException {
      throw new MalformedJsonException(var1 + this.locationString() + "\nSee " + TroubleshootingGuide.createUrl("malformed-json"));
   }

   private IllegalStateException unexpectedTokenError(String var1) throws IOException {
      JsonToken var2 = this.peek();
      String var3 = var2 == JsonToken.NULL ? "adapter-not-null-safe" : "unexpected-json-structure";
      return new IllegalStateException("Expected " + var1 + " but was " + this.peek() + this.locationString() + "\nSee " + TroubleshootingGuide.createUrl(var3));
   }

   private void consumeNonExecutePrefix() throws IOException {
      int var1 = this.nextNonWhitespace(true);
      --this.pos;
      if (this.pos + 5 <= this.limit || this.fillBuffer(5)) {
         int var2 = this.pos;
         char[] var3 = this.buffer;
         if (var3[var2] == ')' && var3[var2 + 1] == ']' && var3[var2 + 2] == '}' && var3[var2 + 3] == '\'' && var3[var2 + 4] == '\n') {
            this.pos += 5;
         }
      }
   }

   static {
      JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
         public void promoteNameToValue(JsonReader var1) throws IOException {
            if (var1 instanceof JsonTreeReader) {
               ((JsonTreeReader)var1).promoteNameToValue();
            } else {
               int var2 = var1.peeked;
               if (var2 == 0) {
                  var2 = var1.doPeek();
               }

               if (var2 == 13) {
                  var1.peeked = 9;
               } else if (var2 == 12) {
                  var1.peeked = 8;
               } else {
                  if (var2 != 14) {
                     throw var1.unexpectedTokenError("a name");
                  }

                  var1.peeked = 10;
               }

            }
         }
      };
   }
}
