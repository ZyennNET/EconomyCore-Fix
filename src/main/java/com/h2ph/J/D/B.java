package com.h2ph.J.D;

import com.google.common.collect.Multimap;
import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.TabCompleteEvent;
import sun.misc.Unsafe;

public class B implements CommandExecutor, Listener {
   private final PrismSurvival E;
   private Method B;
   private Method D;
   private long C = -1L;
   private static Unsafe A;

   public B(PrismSurvival var1) {
      this.E = var1;
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("hidename").setExecutor(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("economysmpcore.hidename")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
            return true;
         } else {
            PlayerData var6 = this.E.getPlayerDataManager().get(var5.getUniqueId());
            if (var6.isNameHidden()) {
               var6.setNameHidden(false);
               this.E(var5, "&7Your gamertag is now visible.");
               var5.playSound(var5.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F);
               this.F(var5, var5.getName());
            } else {
               var6.setNameHidden(true);
               this.E(var5, "&7Your gamertag is now hidden.");
               var5.playSound(var5.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
               String var10000 = String.valueOf(ChatColor.MAGIC);
               String var7 = var10000 + var5.getName();
               this.F(var5, var7);
            }

            return true;
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onChat(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = this.E.getPlayerDataManager().get(var2.getUniqueId());
      if (var3 != null && var3.isNameHidden()) {
         String var4 = var1.getMessage().trim();
         String var5 = var2.getDisplayName();
         if (!var4.equalsIgnoreCase("Hi") && !var4.equalsIgnoreCase("Hello")) {
            String var6 = var2.getName();
            String var10000 = String.valueOf(ChatColor.MAGIC);
            String var7 = var10000 + var6;
            String var8 = var5.replace(var6, var7);
            var2.setDisplayName(var8);
            this.E.getSchedulerAdapter().runTask(() -> var2.setDisplayName(var5));
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onTabComplete(TabCompleteEvent var1) {
      CommandSender var3 = var1.getSender();
      if (var3 instanceof Player var2) {
         if (!var2.hasPermission("economysmpcore.hidename.see")) {
            ArrayList var9 = new ArrayList(var1.getCompletions());
            boolean var4 = false;

            for(Player var6 : Bukkit.getOnlinePlayers()) {
               PlayerData var7 = this.E.getPlayerDataManager().get(var6.getUniqueId());
               if (var7 != null && var7.isNameHidden()) {
                  String var8 = var6.getName();
                  if (var9.removeIf((var1x) -> var1x.equalsIgnoreCase(var8))) {
                     var4 = true;
                  }
               }
            }

            if (var4) {
               var1.setCompletions(var9);
            }

         }
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      PlayerData var3 = this.E.getPlayerDataManager().get(var2.getUniqueId());
      if (var3 != null && var3.isNameHidden()) {
         var1.setJoinMessage((String)null);
         this.E.getSchedulerAdapter().runTaskLater(() -> {
            String var10000 = String.valueOf(ChatColor.MAGIC);
            String var2x = var10000 + var2.getName();
            this.F(var2, var2x);
         }, 5L);
      }

      this.E.getSchedulerAdapter().runTaskLater(() -> {
         for(Player var3 : Bukkit.getOnlinePlayers()) {
            if (!var3.equals(var2)) {
               PlayerData var4 = this.E.getPlayerDataManager().get(var3.getUniqueId());
               if (var4 != null && var4.isNameHidden() && !var2.hasPermission("economysmpcore.hidename.see")) {
                  String var10000 = String.valueOf(ChatColor.MAGIC);
                  String var5 = var10000 + var3.getName();
                  this.A(var3, var2, var5);
               }
            }
         }

      }, 10L);
   }

   private void E(Player var1, String var2) {
      try {
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.translateAlternateColorCodes('&', var2)));
      } catch (Throwable var4) {
      }

   }

   private void F(Player var1, String var2) {
      if (var1 != null && var1.isOnline()) {
         String var3 = var1.getName();
         this.D(var1, var2);

         for(Player var5 : Bukkit.getOnlinePlayers()) {
            if (!var5.equals(var1) && var5.isOnline() && !var5.hasPermission("economysmpcore.hidename.see")) {
               try {
                  var5.hidePlayer(this.E, var1);
                  var5.showPlayer(this.E, var1);
               } catch (Exception var7) {
               }
            }
         }

         this.E.getSchedulerAdapter().runTaskLater(() -> {
            if (var1.isOnline()) {
               this.D(var1, var3);
            }

         }, 2L);
      }
   }

   private void A(Player var1, Player var2, String var3) {
      if (var1 != null && var1.isOnline() && var2 != null && var2.isOnline()) {
         String var4 = var1.getName();
         if (this.D(var1, var3)) {
            try {
               var2.hidePlayer(this.E, var1);
               var2.showPlayer(this.E, var1);
            } catch (Exception var6) {
            }

            this.E.getSchedulerAdapter().runTaskLater(() -> {
               if (var1.isOnline()) {
                  this.D(var1, var4);
               }

            }, 2L);
         }

      }
   }

   private boolean D(Player var1, String var2) {
      try {
         if (A == null) {
            return false;
         } else {
            if (this.B == null) {
               this.B = var1.getClass().getMethod("getHandle");
            }

            Object var3 = this.B.invoke(var1);
            if (this.D == null) {
               try {
                  this.D = var3.getClass().getMethod("getGameProfile");
               } catch (NoSuchMethodException var13) {
                  for(Method var8 : var3.getClass().getMethods()) {
                     if (var8.getReturnType().getName().contains("GameProfile") && var8.getParameterCount() == 0) {
                        this.D = var8;
                        break;
                     }
                  }
               }
            }

            if (this.D == null) {
               return false;
            } else {
               Object var4 = this.D.invoke(var3);
               if (var4 == null) {
                  return false;
               } else {
                  Constructor var15 = var4.getClass().getConstructor(UUID.class, String.class);
                  Object var16 = var15.newInstance(var1.getUniqueId(), var2);

                  try {
                     Method var17 = var4.getClass().getMethod("getProperties");
                     Object var19 = var17.invoke(var4);
                     Method var9 = var19.getClass().getMethod("putAll", Multimap.class);
                     Object var10 = var17.invoke(var16);
                     var9.invoke(var10, (Multimap)var19);
                  } catch (Exception var12) {
                  }

                  if (this.C == -1L) {
                     for(Class var18 = var3.getClass(); var18 != Object.class; var18 = var18.getSuperclass()) {
                        for(Field var11 : var18.getDeclaredFields()) {
                           if (var11.getType().getName().contains("GameProfile")) {
                              this.C = A.objectFieldOffset(var11);
                              break;
                           }
                        }

                        if (this.C != -1L) {
                           break;
                        }
                     }
                  }

                  if (this.C != -1L) {
                     A.putObject(var3, this.C, var16);
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      } catch (Exception var14) {
         return false;
      }
   }

   static {
      try {
         Field var0 = Unsafe.class.getDeclaredField("theUnsafe");
         var0.setAccessible(true);
         A = (Unsafe)var0.get((Object)null);
      } catch (Exception var1) {
      }

   }
}
