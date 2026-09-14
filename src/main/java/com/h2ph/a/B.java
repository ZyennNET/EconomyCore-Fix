package com.h2ph.A;

import com.h2ph.PrismSurvival;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class B implements Listener, CommandExecutor {
   private final PrismSurvival B;
   private final Map<UUID, Boolean> A = new HashMap();

   public B(PrismSurvival var1) {
      this.B = var1;
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Players only.");
         return true;
      } else {
         boolean var6 = this.isChatVisible(var5.getUniqueId());
         boolean var7 = !var6;
         this.setChatVisible(var5.getUniqueId(), var7);
         if (var7) {
            var5.sendMessage(String.valueOf(ChatColor.GREEN) + "You will now see public chat messages.");
         } else {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You will no longer see public chat messages.");
         }

         return true;
      }
   }

   public boolean isChatVisible(UUID var1) {
      return (Boolean)this.A.getOrDefault(var1, true);
   }

   public void setChatVisible(UUID var1, boolean var2) {
      this.A.put(var1, var2);
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onChatSend(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      if (!this.isChatVisible(var2.getUniqueId())) {
         var1.setCancelled(true);
         var2.sendActionBar(ChatColor.translateAlternateColorCodes('&', "&cYou have disabled chat"));
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou have disabled chat. Use &7/chat toggle&c to re-enable."));
      }

   }
}
