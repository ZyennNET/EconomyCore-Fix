package com.h2ph.W;

import com.h2ph.PrismSurvival;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class F implements Listener {
   private static final String C = "economysmpcore.commandwhitelist.bypass";
   private final PrismSurvival A;
   private final Set<String> D;
   private boolean B;

   public F(PrismSurvival var1) {
      this.A = var1;
      this.D = new HashSet();
      this.A();
   }

   private void A() {
      FileConfiguration var1 = this.A.getSurvivalConfig();
      this.B = var1.getBoolean("command-whitelist.enabled", true);
      this.D.clear();

      for(String var4 : var1.getStringList("command-whitelist.allowed-commands")) {
         this.D.add(var4.toLowerCase());
      }

      this.A.getLogger().info("Command whitelist loaded: " + this.D.size() + " commands allowed");
   }

   @EventHandler
   public void onPlayerCommandSend(PlayerCommandSendEvent var1) {
      if (!var1.getPlayer().isOp()) {
         if (!var1.getPlayer().hasPermission("economysmpcore.commandwhitelist.bypass")) {
            if (this.B) {
               Collection var2 = var1.getCommands();
               Player var3 = var1.getPlayer();
               var2.removeIf((var2x) -> {
                  String var3x = var2x.toLowerCase();
                  if (!var3x.startsWith("prismcore:") && !var3x.startsWith("minecraft:")) {
                     String var4 = var3x;
                     if (var3x.contains(":")) {
                        var4 = var3x.substring(var3x.indexOf(":") + 1);
                     }

                     return !this.B(var3, var4);
                  } else {
                     return true;
                  }
               });
            }
         }
      }
   }

   @EventHandler
   public void onCommandPreprocess(PlayerCommandPreprocessEvent var1) {
      if (!var1.getPlayer().isOp()) {
         if (!var1.getPlayer().hasPermission("economysmpcore.commandwhitelist.bypass")) {
            if (this.B) {
               String var2 = var1.getMessage().toLowerCase();
               String var3 = var2.substring(1);
               if (var3.contains(" ")) {
                  var3 = var3.substring(0, var3.indexOf(" "));
               }

               if (var3.contains(":")) {
                  var3 = var3.substring(var3.indexOf(":") + 1);
               }

               if (!this.B(var1.getPlayer(), var3)) {
                  var1.setCancelled(true);
                  var1.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis command does not exist."));
                  String var4 = ChatColor.translateAlternateColorCodes('&', "&cThis command does not exist.");
                  ChatMessageType var5 = ChatMessageType.ACTION_BAR;
                  var1.getPlayer().spigot().sendMessage(var5, TextComponent.fromLegacyText(var4));
                  var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               }

            }
         }
      }
   }

   private boolean B(Player var1, String var2) {
      if (var1.hasPermission("economysmpcore.commandwhitelist.bypass")) {
         return true;
      } else if (!this.D.contains(var2.toLowerCase())) {
         return false;
      } else {
         PluginCommand var3 = Bukkit.getPluginCommand(var2);
         if (var3 != null) {
            String var4 = var3.getPermission();
            if (var4 != null && !var4.isEmpty() && !var1.hasPermission(var4)) {
               return false;
            }
         }

         return true;
      }
   }

   public void reload() {
      this.A();

      for(Player var2 : Bukkit.getOnlinePlayers()) {
         var2.updateCommands();
      }

   }
}
