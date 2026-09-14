package com.zaxxer.hikari.util;

import java.util.concurrent.TimeUnit;

public interface ClockSource {
   ClockSource CLOCK = ClockSource.Factory.create();
   TimeUnit[] TIMEUNITS_DESCENDING = new TimeUnit[]{TimeUnit.DAYS, TimeUnit.HOURS, TimeUnit.MINUTES, TimeUnit.SECONDS, TimeUnit.MILLISECONDS, TimeUnit.MICROSECONDS, TimeUnit.NANOSECONDS};
   String[] TIMEUNIT_DISPLAY_VALUES = new String[]{"ns", "µs", "ms", "s", "m", "h", "d"};

   static long currentTime() {
      return CLOCK.currentTime0();
   }

   long currentTime0();

   static long toMillis(long var0) {
      return CLOCK.toMillis0(var0);
   }

   long toMillis0(long var1);

   static long toNanos(long var0) {
      return CLOCK.toNanos0(var0);
   }

   long toNanos0(long var1);

   static long elapsedMillis(long var0) {
      return CLOCK.elapsedMillis0(var0);
   }

   long elapsedMillis0(long var1);

   static long elapsedMillis(long var0, long var2) {
      return CLOCK.elapsedMillis0(var0, var2);
   }

   long elapsedMillis0(long var1, long var3);

   static long elapsedNanos(long var0) {
      return CLOCK.elapsedNanos0(var0);
   }

   long elapsedNanos0(long var1);

   static long elapsedNanos(long var0, long var2) {
      return CLOCK.elapsedNanos0(var0, var2);
   }

   long elapsedNanos0(long var1, long var3);

   static long plusMillis(long var0, long var2) {
      return CLOCK.plusMillis0(var0, var2);
   }

   long plusMillis0(long var1, long var3);

   static TimeUnit getSourceTimeUnit() {
      return CLOCK.getSourceTimeUnit0();
   }

   TimeUnit getSourceTimeUnit0();

   static String elapsedDisplayString(long var0, long var2) {
      return CLOCK.elapsedDisplayString0(var0, var2);
   }

   default String elapsedDisplayString0(long var1, long var3) {
      long var5 = this.elapsedNanos0(var1, var3);
      StringBuilder var7 = new StringBuilder(var5 < 0L ? "-" : "");
      var5 = Math.abs(var5);

      for(TimeUnit var11 : TIMEUNITS_DESCENDING) {
         long var12 = var11.convert(var5, TimeUnit.NANOSECONDS);
         if (var12 > 0L) {
            var7.append(var12).append(TIMEUNIT_DISPLAY_VALUES[var11.ordinal()]);
            var5 -= TimeUnit.NANOSECONDS.convert(var12, var11);
         }
      }

      return var7.toString();
   }

   public static class Factory {
      private static ClockSource create() {
         String var0 = System.getProperty("os.name");
         return (ClockSource)("Mac OS X".equals(var0) ? new MillisecondClockSource() : new NanosecondClockSource());
      }
   }

   public static final class MillisecondClockSource implements ClockSource {
      public long currentTime0() {
         return System.currentTimeMillis();
      }

      public long elapsedMillis0(long var1) {
         return System.currentTimeMillis() - var1;
      }

      public long elapsedMillis0(long var1, long var3) {
         return var3 - var1;
      }

      public long elapsedNanos0(long var1) {
         return TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - var1);
      }

      public long elapsedNanos0(long var1, long var3) {
         return TimeUnit.MILLISECONDS.toNanos(var3 - var1);
      }

      public long toMillis0(long var1) {
         return var1;
      }

      public long toNanos0(long var1) {
         return TimeUnit.MILLISECONDS.toNanos(var1);
      }

      public long plusMillis0(long var1, long var3) {
         return var1 + var3;
      }

      public TimeUnit getSourceTimeUnit0() {
         return TimeUnit.MILLISECONDS;
      }
   }

   public static class NanosecondClockSource implements ClockSource {
      public long currentTime0() {
         return System.nanoTime();
      }

      public long toMillis0(long var1) {
         return TimeUnit.NANOSECONDS.toMillis(var1);
      }

      public long toNanos0(long var1) {
         return var1;
      }

      public long elapsedMillis0(long var1) {
         return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - var1);
      }

      public long elapsedMillis0(long var1, long var3) {
         return TimeUnit.NANOSECONDS.toMillis(var3 - var1);
      }

      public long elapsedNanos0(long var1) {
         return System.nanoTime() - var1;
      }

      public long elapsedNanos0(long var1, long var3) {
         return var3 - var1;
      }

      public long plusMillis0(long var1, long var3) {
         return var1 + TimeUnit.MILLISECONDS.toNanos(var3);
      }

      public TimeUnit getSourceTimeUnit0() {
         return TimeUnit.NANOSECONDS;
      }
   }
}
