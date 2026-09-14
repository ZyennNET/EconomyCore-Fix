package org.slf4j.event;

public enum Level {
   ERROR(40, "ERROR"),
   WARN(30, "WARN"),
   INFO(20, "INFO"),
   DEBUG(10, "DEBUG"),
   TRACE(0, "TRACE");

   private final int levelInt;
   private final String levelStr;

   private Level(int var3, String var4) {
      this.levelInt = var3;
      this.levelStr = var4;
   }

   public int toInt() {
      return this.levelInt;
   }

   public static Level intToLevel(int var0) {
      switch (var0) {
         case 0:
            return TRACE;
         case 10:
            return DEBUG;
         case 20:
            return INFO;
         case 30:
            return WARN;
         case 40:
            return ERROR;
         default:
            throw new IllegalArgumentException("Level integer [" + var0 + "] not recognized.");
      }
   }

   public String toString() {
      return this.levelStr;
   }

   // $FF: synthetic method
   private static Level[] $values() {
      return new Level[]{ERROR, WARN, INFO, DEBUG, TRACE};
   }
}
