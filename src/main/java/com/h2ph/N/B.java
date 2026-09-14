package com.h2ph.N;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class B {
   private final PrismSurvival F;
   private final Map<UUID, List<D>> G = new ConcurrentHashMap();
   private final Set<UUID> C = ConcurrentHashMap.newKeySet();
   private static final Map<UUID, Location> B = new ConcurrentHashMap();
   private static final Map<UUID, BukkitTask> E = new ConcurrentHashMap();
   private final Map<UUID, Long> A = new ConcurrentHashMap();
   private static final Random D = new Random();

   public B(PrismSurvival var1) {
      this.F = var1;
   }

   public void A(Player var1, Player var2, D._A var3) {
      if (this.A.containsKey(var1.getUniqueId())) {
         long var4 = (Long)this.A.get(var1.getUniqueId());
         long var6 = System.currentTimeMillis() - var4;
         if (var6 < 10000L) {
            long var8 = (10000L - var6) / 1000L;
            ++var8;
            var1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease wait " + var8 + "s before requesting again."));
            return;
         }
      }

      this.A.put(var1.getUniqueId(), System.currentTimeMillis());
      if (this.A(var2)) {
         if (var3 == D._A.B) {
            String var11 = "&5" + var2.getName() + "&7 accepted your teleport request.";
            String var13 = "&7You accepted &5" + var1.getName() + "'s&7 teleport request.";
            var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var11));
            var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
            var2.sendMessage(ChatColor.translateAlternateColorCodes('&', var13));
            this.A(var1, var2);
         } else {
            String var12 = "&5" + var2.getName() + "&7 accepted your tphere request.";
            String var14 = "&7You accepted &5" + var1.getName() + "'s&7 tphere request.";
            var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var12));
            var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
            var2.sendMessage(ChatColor.translateAlternateColorCodes('&', var14));
            this.A(var2, var1);
         }

      } else {
         D var10 = new D(var1.getUniqueId(), var2.getUniqueId(), var3);
         List var5 = (List)this.G.getOrDefault(var2.getUniqueId(), new ArrayList());
         var5.removeIf((var1x) -> var1x.A().equals(var1.getUniqueId()));
         var5.add(var10);
         this.G.put(var2.getUniqueId(), var5);
         String var7;
         String var15;
         if (var3 == D._A.B) {
            var15 = "&7You sent &5" + var2.getName() + "&7 a teleport request.";
            var7 = "&5" + var1.getName() + "&7 sent you a teleport request.";
         } else {
            var15 = "&7You sent &5" + var2.getName() + "&7 a tphere request.";
            var7 = "&5" + var1.getName() + "&7 sent you a tphere request.";
         }

         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var15));
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var15)));
         var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_COW_BELL, 1.0F, 1.0F);
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', var7));
         var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var7)));
         var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
      }
   }

   public void B(Player var1, String var2) {
      List var3 = (List)this.G.get(var1.getUniqueId());
      if (var3 != null && !var3.isEmpty()) {
         var3.removeIf(D::C);
         if (var3.isEmpty()) {
            this.G.remove(var1.getUniqueId());
            this.C(var1);
         } else {
            D var4 = null;
            if (var2 == null) {
               if (!var3.isEmpty()) {
                  var4 = (D)var3.get(var3.size() - 1);
               }
            } else {
               for(D var6 : var3) {
                  Player var7 = Bukkit.getPlayer(var6.A());
                  if (var7 != null && var7.getName().equalsIgnoreCase(var2)) {
                     var4 = var6;
                     break;
                  }
               }

               if (var4 == null) {
                  this.C(var1);
                  return;
               }
            }

            Player var10 = Bukkit.getPlayer(var4.A());
            if (var10 != null && var10.isOnline()) {
               Player var11;
               Player var12;
               if (var4.E() == D._A.B) {
                  var11 = var10;
                  var12 = var1;
               } else {
                  var11 = var1;
                  var12 = var10;
               }

               String var8;
               String var9;
               if (var4.E() == D._A.B) {
                  var8 = "&5" + var1.getName() + "&7 accepted your teleport request.";
                  var9 = "&7You accepted &5" + var10.getName() + "'s&7 teleport request.";
               } else {
                  var8 = "&5" + var1.getName() + "&7 accepted your tphere request.";
                  var9 = "&7You accepted &5" + var10.getName() + "'s&7 tphere request.";
               }

               var10.sendMessage(ChatColor.translateAlternateColorCodes('&', var8));
               var10.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var8)));
               var10.playSound(var10.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0F, 1.0F);
               var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var9));
               var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var9)));
               this.A(var11, var12);
               var3.remove(var4);
               if (var3.isEmpty()) {
                  this.G.remove(var1.getUniqueId());
               }

            } else {
               var1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis user is not online."));
               var3.remove(var4);
            }
         }
      } else {
         this.C(var1);
      }
   }

   private void A(final Player var1, final Player var2) {
      if (E.containsKey(var1.getUniqueId())) {
         ((BukkitTask)E.get(var1.getUniqueId())).cancel();
      }

      B.put(var1.getUniqueId(), var1.getLocation());
      final AtomicInteger var3 = new AtomicInteger(5);
      Runnable var4 = new Runnable() {
         public void run() {
            if (var1.isOnline() && var2.isOnline()) {
               if (B.this.B(var1)) {
                  String var3x = "&cTeleport cancelled because you moved.";
                  var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var3x));
                  var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var3x)));
                  var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  B.this.A(var1.getUniqueId());
                  com.h2ph.N.B.B.remove(var1.getUniqueId());
               } else {
                  int var1x = var3.getAndDecrement();
                  if (var1x > 0) {
                     String var2x = "&7Teleporting in &5" + var1x + "&7 seconds";
                     var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2x));
                     var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var2x)));
                     var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                     B.this.F.getSchedulerAdapter().runTaskLater(() -> {
                        if (com.h2ph.N.B.E.containsKey(var1.getUniqueId())) {
                           var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        }

                     }, 10L);
                  } else {
                     var1.teleportAsync(var2.getLocation()).thenAccept((var2xx) -> {
                        if (var2xx) {
                           String var3x = "&7You were teleported to &5" + var2.getName();
                           var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var3x));
                           var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var3x)));
                           var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        }

                     });
                     B.this.A(var1.getUniqueId());
                     com.h2ph.N.B.B.remove(var1.getUniqueId());
                  }

               }
            } else {
               B.this.A(var1.getUniqueId());
               com.h2ph.N.B.B.remove(var1.getUniqueId());
            }
         }
      };
      BukkitTask var5 = this.F.getSchedulerAdapter().runEntityTaskTimer(var1, var4, 0L, 20L);
      E.put(var1.getUniqueId(), var5);
   }

   private void A(UUID var1) {
      if (E.containsKey(var1)) {
         ((BukkitTask)E.get(var1)).cancel();
         E.remove(var1);
      }

   }

   private boolean B(Player var1) {
      if (!B.containsKey(var1.getUniqueId())) {
         return false;
      } else {
         Location var2 = (Location)B.get(var1.getUniqueId());
         Location var3 = var1.getLocation();
         if (var2.getWorld() != var3.getWorld()) {
            return true;
         } else if (var2.getBlockX() == var3.getBlockX() && var2.getBlockZ() == var3.getBlockZ()) {
            return var1.isOnGround() && var2.getBlockY() != var3.getBlockY();
         } else {
            return true;
         }
      }
   }

   public void A(Player var1, String var2) {
      List var3 = (List)this.G.get(var1.getUniqueId());
      if (var3 != null && !var3.isEmpty()) {
         var3.removeIf(D::C);
         if (var3.isEmpty()) {
            this.G.remove(var1.getUniqueId());
            this.C(var1);
         } else {
            D var4 = null;
            if (var2 == null) {
               if (!var3.isEmpty()) {
                  var4 = (D)var3.get(var3.size() - 1);
               }
            } else {
               for(D var6 : var3) {
                  Player var7 = Bukkit.getPlayer(var6.A());
                  if (var7 != null && var7.getName().equalsIgnoreCase(var2)) {
                     var4 = var6;
                     break;
                  }
               }

               if (var4 == null) {
                  this.C(var1);
                  return;
               }
            }

            Player var8 = Bukkit.getPlayer(var4.A());
            String var9;
            String var10;
            if (var4.E() == D._A.B) {
               String var10000 = var8 != null ? var8.getName() : "Unknown";
               var9 = "&7You denied &5" + var10000 + "'s&7 teleport request.";
               var10 = "&5" + var1.getName() + "&7 denied your teleport request.";
            } else {
               String var11 = var8 != null ? var8.getName() : "Unknown";
               var9 = "&7You denied &5" + var11 + "'s&7 tphere request.";
               var10 = "&5" + var1.getName() + "&7 denied your tphere request.";
            }

            var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var9));
            var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var9)));
            if (var8 != null && var8.isOnline()) {
               var8.sendMessage(ChatColor.translateAlternateColorCodes('&', var10));
               var8.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var10)));
            }

            var3.remove(var4);
         }
      } else {
         this.C(var1);
      }
   }

   public void D(Player var1) {
      boolean var2 = false;
      Iterator var3 = this.G.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry var4 = (Map.Entry)var3.next();
         List var5 = (List)var4.getValue();
         boolean var6 = var5.removeIf((var1x) -> var1x.A().equals(var1.getUniqueId()));
         if (var6) {
            var2 = true;
            if (var5.isEmpty()) {
               var3.remove();
            }
         }
      }

      if (var2) {
         String var7 = "&7You cancelled your tpa requests.";
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var7));
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var7)));
      } else {
         this.C(var1);
      }

   }

   public void E(Player var1) {
      if (this.C.contains(var1.getUniqueId())) {
         this.C.remove(var1.getUniqueId());
         String var2 = "&7You turned off tpauto.";
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2));
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var2)));
         var1.playSound(var1.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1.0F, 1.0F);
      } else {
         this.C.add(var1.getUniqueId());
         String var3 = "&7You turned on tpauto.";
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var3));
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var3)));
         var1.playSound(var1.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 1.0F);
      }

   }

   public boolean A(Player var1) {
      return this.C.contains(var1.getUniqueId());
   }

   public Set<UUID> A() {
      return Collections.unmodifiableSet(this.C);
   }

   private void C(Player var1) {
      String var2 = "&cThis teleport request does not exist.";
      var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2));
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var2)));
      var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
   }
}
