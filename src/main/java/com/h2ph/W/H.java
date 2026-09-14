package com.h2ph.W;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class H implements Listener {
   public static final String GUI_TITLE = ChatColor.translateAlternateColorCodes('&', "&8ᴄᴏɴꜰɪʀᴍ ʀᴇǫᴜᴇѕᴛ");
   private final com.h2ph.N.B A;

   public H(com.h2ph.N.B var1) {
      this.A = var1;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getView().getTitle().equals(GUI_TITLE)) {
         var1.setCancelled(true);
         if (var1.getWhoClicked() instanceof Player) {
            Player var2 = (Player)var1.getWhoClicked();
            int var3 = var1.getRawSlot();
            if (var3 < var1.getView().getTopInventory().getSize()) {
               if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
                  var2.playSound(var2.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
               }

               InventoryHolder var4 = var1.getInventory().getHolder();
               if (var4 instanceof _A) {
                  _A var5 = (_A)var4;
                  String var6 = var5.B;
                  com.h2ph.N.D._A var7 = var5.A;
                  if (var3 == 10) {
                     var2.closeInventory();
                  } else if (var3 == 16) {
                     var2.closeInventory();
                     Player var8 = Bukkit.getPlayer(var6);
                     if (var8 == null) {
                        var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis user is not online."));
                        var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        return;
                     }

                     this.A.A(var2, var8, var7);
                  }

               }
            }
         }
      }
   }

   public static class _A implements InventoryHolder {
      private final String B;
      private final com.h2ph.N.D._A A;

      public _A(String var1, com.h2ph.N.D._A var2) {
         this.B = var1;
         this.A = var2;
      }

      public Inventory getInventory() {
         return null;
      }
   }
}
