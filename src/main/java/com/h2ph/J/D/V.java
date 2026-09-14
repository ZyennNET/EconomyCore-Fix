package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class V implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public V(PrismSurvival var1) {
      this.A = var1;
      var1.getCommand("whereami").setExecutor(this);
      var1.getCommand("whereami").setTabCompleter(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (var4.length == 0) {
            String var9 = this.A(var5.getWorld().getName());
            var5.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize("&7You are currently on &d" + var9));
            return true;
         } else {
            String var6 = var4[0];
            Player var7 = Bukkit.getPlayer(var6);
            if (var7 == null) {
               this.A.getSchedulerAdapter().runTaskAsynchronously(() -> {
                  OfflinePlayer var3 = Bukkit.getOfflinePlayer(var6);
                  this.A.getSchedulerAdapter().runTask(() -> {
                     if (var3.hasPlayedBefore()) {
                        var5.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize("&cPlayer is not online."));
                     } else {
                        var5.sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize("&cThat player does not exist."));
                     }

                     var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  });
               });
               return true;
            } else {
               String var8 = this.A(var7.getWorld().getName());
               LegacyComponentSerializer var10001 = LegacyComponentSerializer.legacyAmpersand();
               String var10002 = var7.getName();
               var5.sendActionBar(var10001.deserialize("&d" + var10002 + "&7 is currently on &d" + var8));
               return true;
            }
         }
      } else {
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 1) {
         return Collections.emptyList();
      } else {
         ArrayList var5 = new ArrayList();

         for(Player var7 : Bukkit.getOnlinePlayers()) {
            var5.add(var7.getName());
         }

         return var5;
      }
   }

   private String A(String var1) {
      if (var1 == null) {
         return "Unknown";
      } else {
         String var2 = var1.toLowerCase();
         if (var2.contains("nether")) {
            return "The Nether";
         } else {
            return var2.contains("end") ? "The End" : "Overworld";
         }
      }
   }
}
