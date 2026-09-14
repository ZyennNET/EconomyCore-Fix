package com.h2ph.W;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class A implements Listener {
   private final com.h2ph.J.B.F.A A;

   public A(com.h2ph.J.B.F.A var1) {
      this.A = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onLogin(AsyncPlayerPreLoginEvent var1) {
      UUID var2 = var1.getUniqueId();
      if (this.A.isBanned(var2)) {
         com.h2ph.T.A.C._A var3 = this.A.getBanInfo(var2);
         if (var3 != null) {
            var1.disallow(Result.KICK_BANNED, this.A(var3));
         } else {
            var1.disallow(Result.KICK_BANNED, "You are banned.");
         }
      }

   }

   private String A(com.h2ph.T.A.C._A var1) {
      SimpleDateFormat var2 = new SimpleDateFormat("yyyy-MM-dd");
      String var3 = var2.format(new Date(var1.D));
      int var5 = var1.H;
      String var4;
      if (var5 % 10 == 1 && var5 % 100 != 11) {
         var4 = "st";
      } else if (var5 % 10 == 2 && var5 % 100 != 11) {
         var4 = "nd";
      } else if (var5 % 10 == 3 && var5 % 100 != 11) {
         var4 = "rd";
      } else {
         var4 = "th";
      }

      String var6;
      if (var1.C == -1L) {
         var6 = "Permanent";
      } else {
         long var7 = var1.C - System.currentTimeMillis();
         long var9 = TimeUnit.MILLISECONDS.toDays(var7);
         long var11 = TimeUnit.MILLISECONDS.toHours(var7) % 24L;
         long var13 = TimeUnit.MILLISECONDS.toMinutes(var7) % 60L;
         if (var9 > 0L) {
            var6 = var9 + " day" + (var9 != 1L ? "s" : "");
         } else if (var11 > 0L) {
            var6 = var11 + " hour" + (var11 != 1L ? "s" : "");
         } else {
            var6 = var13 + " minute" + (var13 != 1L ? "s" : "");
         }
      }

      String var15 = this.A.getOffendConfig().getString("messages.ban_layout");
      if (var15 == null) {
         var15 = "&cYou are banned from this server!\n\n&fBanned on: &f%banned_on%\n&fReason: &f%reason%\n&fBan ID: &b#%id%\n\n&fExpires in: &f%time_left%";
      }

      return ChatColor.translateAlternateColorCodes('&', var15.replace("%banned_on%", var3).replace("%reason%", var1.G).replace("%count%", String.valueOf(var1.H)).replace("%ordinal%", var4).replace("%id%", var1.B).replace("%time_left%", var6));
   }
}
