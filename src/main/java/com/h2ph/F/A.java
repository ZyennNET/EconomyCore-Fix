package com.h2ph.f;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

public class A extends AbstractFilter {
   public Filter.Result filter(LogEvent var1) {
      if (var1 == null) {
         return Result.NEUTRAL;
      } else {
         Message var2 = var1.getMessage();
         if (var2 != null) {
            String var3 = var2.getFormattedMessage();
            if (this.A(var3)) {
               return Result.DENY;
            }
         }

         for(Throwable var4 = var1.getThrown(); var4 != null; var4 = var4.getCause()) {
            if (this.A(var4.getMessage())) {
               return Result.DENY;
            }
         }

         return Result.NEUTRAL;
      }
   }

   private boolean A(String var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.contains("Status: 429") || var1.contains("Couldn't look up profile properties") || var1.contains("MinecraftClientHttpException");
      }
   }
}
