package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class E {
   private final PrismSurvival H;
   private final B I;
   private final Map<UUID, UUID> C = new HashMap();
   private final Map<UUID, Long> E = new HashMap();
   private final Map<UUID, Long> D = new HashMap();
   private int B = 60;
   private int G = 10;
   private final Map<UUID, Integer> F = new HashMap();
   private final Map<UUID, String> A = new HashMap();

   public E(PrismSurvival var1, B var2) {
      this.H = var1;
      this.I = var2;
      this.A();
   }

   public void A(Player var1, String var2) {
      UUID var3 = var1.getUniqueId();
      UUID var4 = this.A(var3, var2);
      if (var4 == null) {
         String var17 = String.valueOf(ChatColor.RED) + "You do not have a pending request from that player.";
         var1.sendMessage(var17);
         this.B(var1, var17);
      } else {
         Player var5 = Bukkit.getPlayer(var4);
         if (var5 == null) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "That player is no longer online.");
         } else {
            int var6 = (Integer)this.F.getOrDefault(var4, 5);
            String var7 = (String)this.A.getOrDefault(var4, "Random");
            String var8 = ChatColor.translateAlternateColorCodes('&', "&7Searching for regions...");
            var5.sendMessage(var8);
            var1.sendMessage(var8);

            try {
               var5.playSound(var5.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5F, 1.0F);
               var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5F, 1.0F);
            } catch (IllegalArgumentException | NoSuchFieldError var16) {
               try {
                  var5.playSound(var5.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.5F, 1.0F);
                  var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.5F, 1.0F);
               } catch (Exception var15) {
               }
            }

            this.C.remove(var4);
            this.E.remove(var4);
            AtomicInteger var12 = new AtomicInteger(0);
            AtomicReference var13 = new AtomicReference();
            BukkitTask var14 = this.H.getSchedulerAdapter().runEntityTaskTimer(var5, () -> {
               int var8 = var12.incrementAndGet();
               if (var5.isOnline() && var1.isOnline()) {
                  boolean var14 = this.I.A(var5, var1, var6, var7);
                  if (var14) {
                     BukkitTask var15 = (BukkitTask)var13.get();
                     if (var15 != null) {
                        var15.cancel();
                     }

                     this.F.remove(var4);
                     this.A.remove(var4);
                     var1.sendMessage(String.valueOf(ChatColor.GREEN) + "You accepted the duel!");
                     this.A(var1, Sound.ENTITY_PLAYER_LEVELUP);
                     String var10001 = String.valueOf(ChatColor.GREEN);
                     var5.sendMessage(var10001 + var1.getName() + " accepted your duel request!");
                     this.A(var5, Sound.ENTITY_PLAYER_LEVELUP);
                  } else {
                     if (var8 >= 30) {
                        BukkitTask var10 = (BukkitTask)var13.get();
                        if (var10 != null) {
                           var10.cancel();
                        }

                        this.F.remove(var4);
                        this.A.remove(var4);
                        String var11 = ChatColor.translateAlternateColorCodes('&', "&cUnable to find available regions to play.");
                        var5.sendMessage(var11);
                        var1.sendMessage(var11);

                        try {
                           var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        } catch (IllegalArgumentException | NoSuchFieldError var13x) {
                        }
                     }

                  }
               } else {
                  BukkitTask var9 = (BukkitTask)var13.get();
                  if (var9 != null) {
                     var9.cancel();
                  }

                  this.F.remove(var4);
                  this.A.remove(var4);
               }
            }, 0L, 20L);
            var13.set(var14);
         }
      }
   }

   public void A() {
      File var1 = new File(this.H.getDataFolder(), "survival/duels/config.yml");
      if (var1.exists()) {
         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
         this.B = var2.getInt("pending-timeout", 60);
         this.G = var2.getInt("request-cooldown", 10);
      }

   }

   public void A(Player var1, Player var2) {
      this.A(var1, var2, 5, "Random");
   }

   public void A(Player var1, Player var2, int var3, String var4) {
      UUID var5 = var1.getUniqueId();
      UUID var6 = var2.getUniqueId();
      if (var5.equals(var6)) {
         String var18 = String.valueOf(ChatColor.RED) + "You cannot duel yourself.";
         var1.sendMessage(var18);
         this.B(var1, var18);
      } else if (!this.I.I(var2) && !this.I.N(var2)) {
         if (this.D.containsKey(var5)) {
            long var16 = (Long)this.D.get(var5);
            long var9 = (System.currentTimeMillis() - var16) / 1000L;
            if (var9 < (long)this.G) {
               UUID var11 = (UUID)this.C.get(var5);
               if (var11 != null && var11.equals(var6)) {
                  long var12 = (long)this.G - var9;
                  String var14 = ChatColor.translateAlternateColorCodes('&', "&fPlease wait " + var12 + " seconds before requesting again.");
                  var1.sendMessage(var14);
                  this.B(var1, var14);
                  return;
               }
            }
         }

         this.C.put(var5, var6);
         this.E.put(var5, System.currentTimeMillis());
         this.F.put(var5, var3);
         this.A.put(var5, var4);
         this.D.put(var5, System.currentTimeMillis());
         var1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYou requested &a" + var2.getName() + "&f to play on a duel match."));
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + var1.getName() + "&f has requested you to fight on a duel match."));
         TextComponent var17 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "Type /duel accept or "));
         TextComponent var8 = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a[Click me]"));
         var8.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/duel accept " + var1.getName()));
         var8.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Click to accept duel from " + var1.getName())).create()));
         var17.addExtra(var8);
         var17.addExtra(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&f to accept the challenge.")));
         var2.spigot().sendMessage(var17);
         this.A(var2, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
         this.A(var1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
      } else {
         String var7 = ChatColor.translateAlternateColorCodes('&', "&cThis player is currently on a duel.");
         var1.sendMessage(var7);
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var7));

         try {
            var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } catch (Exception var15) {
         }

      }
   }

   public boolean A(Player var1) {
      return this.C.containsKey(var1.getUniqueId());
   }

   public void B(Player var1) {
      UUID var2 = var1.getUniqueId();
      if (this.C.containsKey(var2)) {
         this.C.remove(var2);
         this.E.remove(var2);
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Pending duel request cancelled.");
         this.A(var1, Sound.UI_BUTTON_CLICK);
      } else {
         String var3 = String.valueOf(ChatColor.RED) + "You do not have any pending duel requests.";
         var1.sendMessage(var3);
         this.B(var1, var3);
      }

   }

   public void C(Player var1, String var2) {
      UUID var3 = var1.getUniqueId();
      UUID var4 = this.A(var3, var2);
      if (var4 == null) {
         String var6 = String.valueOf(ChatColor.RED) + "You do not have a pending request from that player.";
         var1.sendMessage(var6);
         this.B(var1, var6);
      } else {
         this.C.remove(var4);
         this.E.remove(var4);
         var1.sendMessage(String.valueOf(ChatColor.RED) + "You declined the duel request.");
         this.A(var1, Sound.UI_BUTTON_CLICK);
         Player var5 = Bukkit.getPlayer(var4);
         if (var5 != null) {
            var5.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + var1.getName() + "&f declined your duel match."));
            this.A(var5, Sound.ENTITY_VILLAGER_NO);
         }

      }
   }

   private UUID A(UUID var1, String var2) {
      for(Map.Entry var4 : this.C.entrySet()) {
         if (((UUID)var4.getValue()).equals(var1)) {
            UUID var5 = (UUID)var4.getKey();
            if (this.A(var5)) {
               Player var6 = Bukkit.getPlayer(var5);
               if (var6 != null && (var2 == null || var6.getName().equalsIgnoreCase(var2))) {
                  return var5;
               }
            }
         }
      }

      return null;
   }

   public boolean A(UUID var1) {
      if (!this.C.containsKey(var1)) {
         return false;
      } else {
         long var2 = (Long)this.E.get(var1);
         long var4 = (System.currentTimeMillis() - var2) / 1000L;
         if (var4 > (long)this.B) {
            this.C.remove(var1);
            this.E.remove(var1);
            return false;
         } else {
            return true;
         }
      }
   }

   public void B(Player var1, String var2) {
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var2));
      this.A(var1, Sound.ENTITY_VILLAGER_NO);
   }

   private void A(Player var1, Sound var2) {
      try {
         var1.playSound(var1.getLocation(), var2, 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }
}
