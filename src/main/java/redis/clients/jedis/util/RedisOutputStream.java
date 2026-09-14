package redis.clients.jedis.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class RedisOutputStream extends FilterOutputStream {
   private static final int OUTPUT_BUFFER_SIZE = Integer.parseInt(System.getProperty("jedis.bufferSize.output", System.getProperty("jedis.bufferSize", "8192")));
   protected final byte[] buf;
   protected int count;
   private static final int[] sizeTable = new int[]{9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};
   private static final byte[] DigitTens = new byte[]{48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 51, 51, 51, 51, 51, 51, 51, 51, 51, 51, 52, 52, 52, 52, 52, 52, 52, 52, 52, 52, 53, 53, 53, 53, 53, 53, 53, 53, 53, 53, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 57, 57, 57, 57, 57, 57, 57, 57, 57, 57};
   private static final byte[] DigitOnes = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
   private static final byte[] digits = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};

   public RedisOutputStream(OutputStream var1) {
      this(var1, OUTPUT_BUFFER_SIZE);
   }

   public RedisOutputStream(OutputStream var1, int var2) {
      super(var1);
      if (var2 <= 0) {
         throw new IllegalArgumentException("Buffer size <= 0");
      } else {
         this.buf = new byte[var2];
      }
   }

   private void flushBuffer() throws IOException {
      if (this.count > 0) {
         this.out.write(this.buf, 0, this.count);
         this.count = 0;
      }

   }

   public void write(byte var1) throws IOException {
      if (this.count == this.buf.length) {
         this.flushBuffer();
      }

      this.buf[this.count++] = var1;
   }

   public void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (var3 >= this.buf.length) {
         this.flushBuffer();
         this.out.write(var1, var2, var3);
      } else {
         if (var3 >= this.buf.length - this.count) {
            this.flushBuffer();
         }

         System.arraycopy(var1, var2, this.buf, this.count, var3);
         this.count += var3;
      }

   }

   public void writeCrLf() throws IOException {
      if (2 >= this.buf.length - this.count) {
         this.flushBuffer();
      }

      this.buf[this.count++] = 13;
      this.buf[this.count++] = 10;
   }

   public void writeIntCrLf(int var1) throws IOException {
      if (var1 < 0) {
         this.write((byte)45);
         var1 = -var1;
      }

      int var2;
      for(var2 = 0; var1 > sizeTable[var2]; ++var2) {
      }

      ++var2;
      if (var2 >= this.buf.length - this.count) {
         this.flushBuffer();
      }

      int var4;
      int var5;
      for(var5 = this.count + var2; var1 >= 65536; this.buf[var5] = DigitTens[var4]) {
         int var3 = var1 / 100;
         var4 = var1 - ((var3 << 6) + (var3 << 5) + (var3 << 2));
         var1 = var3;
         --var5;
         this.buf[var5] = DigitOnes[var4];
         --var5;
      }

      int var7;
      do {
         var7 = var1 * '쳍' >>> 19;
         var4 = var1 - ((var7 << 3) + (var7 << 1));
         --var5;
         this.buf[var5] = digits[var4];
         var1 = var7;
      } while(var7 != 0);

      this.count += var2;
      this.writeCrLf();
   }

   public void flush() throws IOException {
      this.flushBuffer();
      this.out.flush();
   }
}
