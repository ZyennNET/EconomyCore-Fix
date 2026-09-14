package com.h2ph.J.B.F;

import com.h2ph.PrismSurvival;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class D implements CommandExecutor, Listener {
   private final JavaPlugin D;
   private final Set<UUID> A = new HashSet();
   private final Map<UUID, GameMode> B = new HashMap();
   private final String C;

   public D(JavaPlugin var1) {
      String var10001 = String.valueOf(ChatColor.DARK_GRAY);
      this.C = var10001 + this.A("prism") + " " + String.valueOf(ChatColor.RESET);
      this.D = var1;
   }

   private String A(String var1) {
      String var2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
      String var3 = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ";
      StringBuilder var4 = new StringBuilder();

      for(char var8 : var1.toCharArray()) {
         int var9 = var2.indexOf(var8);
         var4.append(var9 != -1 ? var3.charAt(var9) : var8);
      }

      return var4.toString();
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         return true;
      } else if (!var5.hasPermission("economysmpcore.admin.spectator")) {
         String var10001 = String.valueOf(ChatColor.DARK_GRAY);
         var5.sendMessage(var10001 + this.A("no permission"));
         return true;
      } else {
         if (this.A.contains(var5.getUniqueId())) {
            this.B(var5);
         } else {
            this.C(var5);
         }

         return true;
      }
   }

   private void C(Player var1) {
      this.A.add(var1.getUniqueId());
      this.B.put(var1.getUniqueId(), var1.getGameMode());
      var1.setGameMode(GameMode.SPECTATOR);

      for(Player var3 : Bukkit.getOnlinePlayers()) {
         if (!var3.hasPermission("economysmpcore.admin.spectator")) {
            var3.hidePlayer(this.D, var1);
         }
      }

      var1.sendMessage("");
      String var10001 = this.C;
      var1.sendMessage(var10001 + String.valueOf(ChatColor.GREEN) + this.A("silent spectator enabled"));
      var10001 = String.valueOf(ChatColor.DARK_GRAY);
      var1.sendMessage(var10001 + this.A("you are hidden from tab"));
      var1.sendMessage("");
   }

   private void B(Player var1) {
      this.A.remove(var1.getUniqueId());
      GameMode var2 = (GameMode)this.B.remove(var1.getUniqueId());
      if (var2 != null) {
         var1.setGameMode(var2);
      } else {
         var1.setGameMode(GameMode.SURVIVAL);
      }

      for(Player var4 : Bukkit.getOnlinePlayers()) {
         var4.showPlayer(this.D, var1);
      }

      var1.sendMessage("");
      String var10001 = this.C;
      var1.sendMessage(var10001 + String.valueOf(ChatColor.RED) + this.A("silent spectator disabled"));
      var10001 = String.valueOf(ChatColor.DARK_GRAY);
      var1.sendMessage(var10001 + this.A("you are visible"));
      var1.sendMessage("");
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      final Player var2 = var1.getPlayer();
      if (this.A.contains(var2.getUniqueId())) {
         for(Player var4 : Bukkit.getOnlinePlayers()) {
            if (!var4.hasPermission("economysmpcore.admin.spectator")) {
               var4.hidePlayer(this.D, var2);
            }
         }

      } else {
         if (this.D instanceof PrismSurvival) {
            ((PrismSurvival)this.D).getSchedulerAdapter().runTaskLater(() -> {
               for(UUID var3 : this.A) {
                  Player var4 = Bukkit.getPlayer(var3);
                  if (var4 != null && !var2.hasPermission("economysmpcore.admin.spectator")) {
                     var2.hidePlayer(this.D, var4);
                  }
               }

            }, 10L);
         } else {
            (new BukkitRunnable() {
               public void run() {
                  for(UUID var2x : D.this.A) {
                     Player var3 = Bukkit.getPlayer(var2x);
                     if (var3 != null && !var2.hasPermission("economysmpcore.admin.spectator")) {
                        var2.hidePlayer(D.this.D, var3);
                     }
                  }

               }
            }).runTaskLater(this.D, 10L);
         }

      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      this.A.remove(var1.getPlayer().getUniqueId());
      this.B.remove(var1.getPlayer().getUniqueId());
   }
}
