package com.h2ph.N;

import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class C implements Runnable {
   private final B A;

   public C(B var1) {
      this.A = var1;
   }

   public void run() {
      for(UUID var2 : this.A.A()) {
         Player var3 = Bukkit.getPlayer(var2);
         if (var3 != null && var3.isOnline()) {
            String var4 = "&aYou have tpauto on.";
            var3.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var4)));
         }
      }

   }
}
