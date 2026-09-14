package com.h2ph.W;

import com.h2ph.PrismSurvival;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class D implements Listener {
   private final PrismSurvival A;

   public D(PrismSurvival var1) {
      this.A = var1;
   }

   @EventHandler
   public void onPlayerEditBook(PlayerEditBookEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.isPlayerMarkedAsAdvisorWriter(var2.getUniqueId())) {
         try {
            BookMeta var3 = var1.getNewBookMeta();
            if (var3 != null) {
               List var4 = var3.getPages();
               if (var4 != null && !var4.isEmpty()) {
                  this.A.setActiveAdvisor(var4);
                  var2.sendMessage(String.valueOf(ChatColor.GREEN) + "Advisor content updated successfully!");
                  var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
               }
            }
         } catch (Exception var5) {
            var2.sendMessage(String.valueOf(ChatColor.RED) + "An error occurred while saving the advisor book.");
            var5.printStackTrace();
         }

         this.A.unmarkPlayerAsAdvisorWriter(var2.getUniqueId());
         this.A.getSchedulerAdapter().runTaskLater(() -> {
            if (var2.isOnline()) {
               ItemStack var1 = var2.getInventory().getItemInMainHand();
               if (var1 != null && (var1.getType() == Material.WRITABLE_BOOK || var1.getType() == Material.WRITTEN_BOOK)) {
                  var2.getInventory().setItemInMainHand((ItemStack)null);
               }

               ItemStack var2x = var2.getInventory().getItemInOffHand();
               if (var2x != null && (var2x.getType() == Material.WRITABLE_BOOK || var2x.getType() == Material.WRITTEN_BOOK)) {
                  var2.getInventory().setItemInOffHand((ItemStack)null);
               }

            }
         }, 1L);
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      if (this.A.isPlayerMarkedAsAdvisorWriter(var1.getPlayer().getUniqueId())) {
         this.A.unmarkPlayerAsAdvisorWriter(var1.getPlayer().getUniqueId());
      }

   }
}
