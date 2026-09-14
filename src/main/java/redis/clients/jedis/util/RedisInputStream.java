package redis.clients.jedis.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisInputStream extends FilterInputStream {
   private static final int INPUT_BUFFER_SIZE = Integer.parseInt(System.getProperty("jedis.bufferSize.input", System.getProperty("jedis.bufferSize", "8192")));
   protected final byte[] buf;
   protected int count;
   protected int limit;

   public RedisInputStream(InputStream var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.buf = new byte[var2];
      }
   }

   public RedisInputStream(InputStream var1) {
      this(var1, INPUT_BUFFER_SIZE);
   }

   public byte readByte() throws JedisConnectionException {
      this.ensureFill();
      return this.buf[this.count++];
   }

   private void ensureCrLf() {
      byte[] var1 = this.buf;
      this.ensureFill();
      if (var1[this.count++] == 13) {
         this.ensureFill();
         if (var1[this.count++] == 10) {
            return;
         }
      }

      throw new JedisConnectionException("Unexpected character!");
   }

   public String readLine() {
      StringBuilder var1 = new StringBuilder();

      while(true) {
         this.ensureFill();
         byte var2 = this.buf[this.count++];
         if (var2 == 13) {
            this.ensureFill();
            byte var3 = this.buf[this.count++];
            if (var3 == 10) {
               String var4 = var1.toString();
               if (var4.length() == 0) {
                  throw new JedisConnectionException("It seems like server has closed the connection.");
               }

               return var4;
            }

            var1.append((char)var2);
            var1.append((char)var3);
         } else {
            var1.append((char)var2);
         }
      }
   }

   public byte[] readLineBytes() {
      this.ensureFill();
      int var1 = this.count;
      byte[] var2 = this.buf;

      while(var1 != this.limit) {
         if (var2[var1++] == 13) {
            if (var1 == this.limit) {
               return this.readLineBytesSlowly();
            }

            if (var2[var1++] == 10) {
               int var3 = var1 - this.count - 2;
               byte[] var4 = new byte[var3];
               System.arraycopy(var2, this.count, var4, 0, var3);
               this.count = var1;
               return var4;
            }
         }
      }

      return this.readLineBytesSlowly();
   }

   private byte[] readLineBytesSlowly() {
      ByteArrayOutputStream var1 = null;

      while(true) {
         this.ensureFill();
         byte var2 = this.buf[this.count++];
         if (var2 == 13) {
            this.ensureFill();
            byte var3 = this.buf[this.count++];
            if (var3 == 10) {
               return var1 == null ? new byte[0] : var1.toByteArray();
            }

            if (var1 == null) {
               var1 = new ByteArrayOutputStream(16);
            }

            var1.write(var2);
            var1.write(var3);
         } else {
            if (var1 == null) {
               var1 = new ByteArrayOutputStream(16);
            }

            var1.write(var2);
         }
      }
   }

   public Object readNullCrLf() {
      this.ensureCrLf();
      return null;
   }

   public boolean readBooleanCrLf() {
      byte[] var1 = this.buf;
      this.ensureFill();
      byte var2 = var1[this.count++];
      this.ensureCrLf();
      switch (var2) {
         case 102:
            return false;
         case 116:
            return true;
         default:
            throw new JedisConnectionException("Unexpected character!");
      }
   }

   public int readIntCrLf() {
      return (int)this.readLongCrLf();
   }

   public long readLongCrLf() {
      byte[] var1 = this.buf;
      this.ensureFill();
      boolean var2 = var1[this.count] == 45;
      if (var2) {
         ++this.count;
      }

      long var3 = 0L;

      while(true) {
         this.ensureFill();
         byte var5 = var1[this.count++];
         if (var5 == 13) {
            this.ensureFill();
            if (var1[this.count++] != 10) {
               throw new JedisConnectionException("Unexpected character!");
            } else {
               return var2 ? -var3 : var3;
            }
         }

         var3 = var3 * 10L + (long)var5 - 48L;
      }
   }

   public double readDoubleCrLf() {
      return DoublePrecision.parseFloatingPointNumber(this.readLine());
   }

   public BigInteger readBigIntegerCrLf() {
      return new BigInteger(this.readLine());
   }

   public int read(byte[] var1, int var2, int var3) throws JedisConnectionException {
      this.ensureFill();
      int var4 = Math.min(this.limit - this.count, var3);
      System.arraycopy(this.buf, this.count, var1, var2, var4);
      this.count += var4;
      return var4;
   }

   private void ensureFill() throws JedisConnectionException {
      if (this.count >= this.limit) {
         try {
            this.limit = this.in.read(this.buf);
            this.count = 0;
            if (this.limit == -1) {
               throw new JedisConnectionException("Unexpected end of stream.");
            }
         } catch (IOException var2) {
            throw new JedisConnectionException(var2);
         }
      }

   }
}
