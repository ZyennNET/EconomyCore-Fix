package com.prismcore.survival.orders.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Client;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.prismcore.survival.orders.PrismOrders;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class SignInputUtil {
   private static final Map<UUID, Consumer<String>> pendingCallbacks = new HashMap();
   private static final Map<UUID, JavaPlugin> pendingPlugins = new HashMap();
   private static boolean interceptorRegistered = false;
   private static final PacketListenerAbstract packetListener = new PacketListenerAbstract() {
      public void onPacketReceive(PacketReceiveEvent var1) {
         if (var1.getPacketType() == Client.CHAT_MESSAGE) {
            WrapperPlayClientChatMessage var2 = new WrapperPlayClientChatMessage(var1);
            UUID var3 = var1.getUser().getUUID();
            Consumer var4 = (Consumer)SignInputUtil.pendingCallbacks.get(var3);
            if (var4 != null) {
               var1.setCancelled(true);
               String var5 = var2.getMessage();
               if ("cancel".equalsIgnoreCase(var5)) {
                  Player var6 = Bukkit.getPlayer(var3);
                  if (var6 != null) {
                     var6.sendMessage(String.valueOf(ChatColor.RED) + "Input cancelled.");
                  }

                  SignInputUtil.finish(var3, (String)null);
               } else if (var5.trim().isEmpty()) {
                  Player var7 = Bukkit.getPlayer(var3);
                  if (var7 != null) {
                     var7.sendMessage(String.valueOf(ChatColor.RED) + "Input cannot be empty. Please try again.");
                  }
               } else {
                  SignInputUtil.finish(var3, var5.trim());
               }
            }
         }

      }
   };

   private static void finish(UUID var0, final String var1) {
      final Consumer var2 = (Consumer)pendingCallbacks.remove(var0);
      JavaPlugin var3 = (JavaPlugin)pendingPlugins.remove(var0);
      if (var2 != null) {
         if (var3 != null) {
            (new BukkitRunnable() {
               public void run() {
                  var2.accept(var1);
               }
            }).runTask(var3);
         } else {
            var2.accept(var1);
         }
      }

   }

   private SignInputUtil() {
   }

   public static void openFromConfig(PrismOrders var0, Player var1, ConfigurationSection var2, Consumer<String> var3) {
      if (var0 != null && var1 != null) {
         String var4 = var2 != null && var2.contains("prompt") ? var2.getString("prompt") : "&ePlease type your input:";
         open(var0.getPlugin(), var1, var4, var3);
      } else {
         if (var3 != null) {
            var3.accept((Object)null);
         }

      }
   }

   public static void open(JavaPlugin var0, Player var1, String var2, Consumer<String> var3) {
      open(var0, var1, var2, var3, 30);
   }

   public static void open(JavaPlugin var0, final Player var1, String var2, Consumer<String> var3, int var4) {
      if (!var1.isOnline()) {
         if (var3 != null) {
            var3.accept((Object)null);
         }

      } else {
         cancel(var1);
         registerInterceptor(var0);
         pendingCallbacks.put(var1.getUniqueId(), var3);
         pendingPlugins.put(var1.getUniqueId(), var0);
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2));
         var1.sendMessage(String.valueOf(ChatColor.GRAY) + "Type 'cancel' to cancel.");
         (new BukkitRunnable() {
            public void run() {
               if (SignInputUtil.pendingCallbacks.containsKey(var1.getUniqueId())) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "Input timed out.");
                  SignInputUtil.finish(var1.getUniqueId(), (String)null);
               }

            }
         }).runTaskLater(var0, (long)var4 * 20L);
      }
   }

   public static void cancel(Player var0) {
      if (var0 != null) {
         finish(var0.getUniqueId(), (String)null);
      }

   }

   private static void registerInterceptor(JavaPlugin var0) {
      if (!interceptorRegistered) {
         try {
            PacketEvents.getAPI().getEventManager().registerListener(packetListener);
            interceptorRegistered = true;
            var0.getLogger().info("Orders chat packet interceptor registered.");
         } catch (Exception var2) {
            var0.getLogger().warning("Failed to register packet interceptor: " + var2.getMessage());
         }

      }
   }
}
