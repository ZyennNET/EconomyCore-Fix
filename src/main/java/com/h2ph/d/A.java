package com.h2ph.D;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;

public class A implements Listener {
   private final B A;

   public A(B var1) {
      this.A = var1;
   }

   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent var1) {
      if (this.A.C()) {
         if (!this.A.A(var1.getPlayer())) {
            FileConfiguration var2 = this.A.B();
            List var3 = var2.getStringList("disconnect-message");
            String var4 = ChatColor.translateAlternateColorCodes('&', String.join("\n", var3));
            var1.disallow(Result.KICK_OTHER, var4);
         }
      }
   }

   @EventHandler
   public void onServerListPing(ServerListPingEvent var1) {
      if (this.A.C()) {
         FileConfiguration var2 = this.A.B();
         List var3 = var2.getStringList("ping-message");
         if (!var3.isEmpty()) {
            String var10002 = (String)var3.get(0);
            var1.setMotd(ChatColor.translateAlternateColorCodes('&', var10002 + "\n" + (var3.size() > 1 ? (String)var3.get(1) : "")));
         }

      }
   }
}
