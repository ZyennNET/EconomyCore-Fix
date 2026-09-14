package com.h2ph.c;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class D implements Listener {
   private final F B;
   private final Map<UUID, Boolean> A = new HashMap();

   public D(F var1) {
      this.B = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onClick(InventoryClickEvent var1) {
      HumanEntity var2 = var1.getWhoClicked();
      if (var2 instanceof Player var3) {
         I._A var4 = (I._A)I.C.get(var3.getUniqueId());
         if (var4 != null) {
            var1.setCancelled(true);
            if (var1.getClickedInventory() != null && var1.getClickedInventory() == var1.getView().getTopInventory()) {
               this.B(var3);
               int var5 = var1.getRawSlot();
               if (var4 == I._A.D) {
                  this.A(var3, var5);
               } else if (var4 == I._A.C) {
                  this.B(var3, var5);
               }

            }
         }
      }
   }

   private void A(Player var1, int var2) {
      File var3 = new File(this.B.H().getDataFolder(), "leaderboards/gui/main.yml");
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      ConfigurationSection var5 = var4.getConfigurationSection("items");
      if (var5 != null) {
         for(String var7 : var5.getKeys(false)) {
            ConfigurationSection var8 = var5.getConfigurationSection(var7);
            if (var8 != null && var8.getBoolean("enabled", true) && var8.getInt("slot", -1) == var2) {
               H._A var9 = H.A(var7);
               if (var9 == null) {
                  return;
               }

               this.B.G().B();
               E.A(this.B, var1, var9, 0);
               return;
            }
         }

      }
   }

   private void B(Player var1, int var2) {
      H._A var3 = (H._A)I.B.get(var1.getUniqueId());
      if (var3 != null) {
         int var4 = (Integer)I.A.getOrDefault(var1.getUniqueId(), 0);
         String var5 = H.C(var3);
         File var6 = new File(this.B.H().getDataFolder(), "leaderboards/gui/" + var5 + ".yml");
         YamlConfiguration var7 = YamlConfiguration.loadConfiguration(var6);
         ConfigurationSection var8 = var7.getConfigurationSection("items");
         if (var8 != null) {
            if (this.A(var8, "back", var2)) {
               if (var4 > 0) {
                  E.A(this.B, var1, var3, var4 - 1);
               } else {
                  E.A(var1);
               }

            } else if (this.A(var8, "next", var2)) {
               E.A(this.B, var1, var3, var4 + 1);
            } else if (this.A(var8, "refresh", var2)) {
               this.B.G().B();
               E.A(this.B, var1, var3, var4);
            } else if (this.A(var8, "search", var2)) {
               this.A.put(var1.getUniqueId(), true);
               J.A(this.B, var1);
            } else {
               ConfigurationSection var9 = var8.getConfigurationSection("player");
               String var10 = var9 != null ? var9.getString("command", "") : "";
               if (var10 != null && !var10.isBlank() && var2 >= 0 && var2 < 45) {
                  int var11 = var4 * 45 + var2;
                  List var12 = this.B.G().B(var3);
                  if (var11 >= 0 && var11 < var12.size()) {
                     String var13 = var10.replace("%player%", ((H._B)var12.get(var11)).B());
                     var1.closeInventory();
                     Bukkit.dispatchCommand(var1, var13);
                  }
               }

            }
         }
      }
   }

   private boolean A(ConfigurationSection var1, String var2, int var3) {
      ConfigurationSection var4 = var1.getConfigurationSection(var2);
      return var4 != null && var4.getInt("slot", -1) == var3;
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      if (Boolean.TRUE.equals(this.A.get(var2.getUniqueId()))) {
         if (!J.A(var2.getUniqueId())) {
            var1.setCancelled(true);
            this.A.remove(var2.getUniqueId());
            String var3 = var1.getMessage().trim();
            H._A var4 = (H._A)I.B.get(var2.getUniqueId());
            Bukkit.getScheduler().runTask(this.B.H(), () -> this.B(var2, var4, var3));
         }
      }
   }

   void handleSignResult(Player var1, String var2) {
      this.A.remove(var1.getUniqueId());
      H._A var3 = (H._A)I.B.get(var1.getUniqueId());
      Bukkit.getScheduler().runTask(this.B.H(), () -> {
         J.A(var1);
         this.B(var1, var3, var2);
      });
   }

   private void B(Player var1, H._A var2, String var3) {
      if (var3 != null && !var3.isEmpty() && !var3.equalsIgnoreCase("cancel")) {
         if (var2 == null) {
            E.A(var1);
         } else {
            int var4 = this.B.G().A(var2, var3, 45);
            if (var4 < 0) {
               var1.sendMessage(com.h2ph.c.A.A(this.B.A().A("player-not-found", "&cPlayer not found")));
               E.A(this.B, var1, var2, 0);
            } else {
               E.A(this.B, var1, var2, var4);
            }
         }
      } else {
         var1.sendMessage(com.h2ph.c.A.A(this.B.A().A("search-cancelled", "&cSearch cancelled!")));
         if (var2 != null) {
            E.A(this.B, var1, var2, (Integer)I.A.getOrDefault(var1.getUniqueId(), 0));
         } else {
            E.A(var1);
         }

      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      J.A(var1.getPlayer());
      this.A.remove(var1.getPlayer().getUniqueId());
      I.A(var1.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onClose(InventoryCloseEvent var1) {
      HumanEntity var2 = var1.getPlayer();
      if (var2 instanceof Player var3) {
         if (I.C.get(var3.getUniqueId()) != null) {
            if (!Boolean.TRUE.equals(this.A.get(var3.getUniqueId())) && !J.A(var3.getUniqueId())) {
               Bukkit.getScheduler().runTask(this.B.H(), () -> {
                  if (!var3.isOnline()) {
                     I.A(var3.getUniqueId());
                  } else {
                     int var2 = var3.getOpenInventory().getTopInventory().getSize();
                     if (var2 <= 5 || !I.C.containsKey(var3.getUniqueId())) {
                        if (!Boolean.TRUE.equals(this.A.get(var3.getUniqueId())) && !J.A(var3.getUniqueId())) {
                           I.A(var3.getUniqueId());
                        }
                     }
                  }
               });
            }
         }
      }
   }

   private void B(Player var1) {
      try {
         String var2 = this.B.H().getConfig().getString("leaderboards.click-sound", "UI_BUTTON_CLICK");
         var1.playSound(var1.getLocation(), Sound.valueOf(var2.toUpperCase()), 1.0F, 1.0F);
      } catch (Exception var3) {
      }

   }
}
