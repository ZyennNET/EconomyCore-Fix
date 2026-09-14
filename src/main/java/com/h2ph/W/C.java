package com.h2ph.W;

import com.h2ph.PrismSurvival;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.MetadataValue;

public class C implements Listener {
   private final PrismSurvival A;

   public C(PrismSurvival var1) {
      this.A = var1;
   }

   @EventHandler
   public void onPlayerEditBook(PlayerEditBookEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.A.isPlayerMarkedAsUpdateWriter(var2.getUniqueId())) {
         try {
            BookMeta var3 = var1.getNewBookMeta();
            if (var3 != null) {
               List var4 = var3.getPages();
               if (var4 != null && !var4.isEmpty()) {
                  this.A.setActiveUpdate(var4);
                  String var5 = "&aUpdate queued: it will be shown to the next player who joins.";
                  var2.sendMessage(ChatColor.translateAlternateColorCodes('&', var5));

                  try {
                     var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
                  } catch (Throwable var8) {
                  }
               }
            }
         } catch (Throwable var9) {
         }

         try {
            this.A.unmarkPlayerAsUpdateWriter(var2.getUniqueId());
         } catch (Throwable var7) {
         }

         this.A.getSchedulerAdapter().runTaskLater(() -> {
            try {
               boolean var2x = var2.hasMetadata("update_given_book");
               if (!var2.hasMetadata("update_prev_slot")) {
                  if (var2x) {
                     try {
                        var2.getInventory().setItem(var2.getInventory().getHeldItemSlot(), (ItemStack)null);
                        var2.updateInventory();
                     } catch (Throwable var10) {
                     }
                  }
               } else {
                  try {
                     List var3 = var2.getMetadata("update_prev_slot");
                     if (var3 != null && !var3.isEmpty()) {
                        Object var4 = ((MetadataValue)var3.get(0)).value();
                        int var5 = var4 instanceof Number ? ((Number)var4).intValue() : var2.getInventory().getHeldItemSlot();
                        List var6 = var2.getMetadata("update_prev_hand");
                        Object var7 = var6 != null && !var6.isEmpty() ? ((MetadataValue)var6.get(0)).value() : null;

                        try {
                           if (var7 instanceof ItemStack) {
                              var2.getInventory().setItem(var5, (ItemStack)var7);
                           } else {
                              var2.getInventory().setItem(var5, (ItemStack)null);
                           }

                           var2.updateInventory();
                        } catch (Throwable var13) {
                        }
                     }
                  } catch (Throwable var14) {
                  }

                  try {
                     var2.removeMetadata("update_prev_slot", this.A);
                  } catch (Throwable var12) {
                  }

                  try {
                     var2.removeMetadata("update_prev_hand", this.A);
                  } catch (Throwable var11) {
                  }
               }

               if (var2.hasMetadata("update_given_book")) {
                  try {
                     var2.removeMetadata("update_given_book", this.A);
                  } catch (Throwable var9) {
                  }
               }
            } catch (Throwable var15) {
            }

         }, 1L);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      final Player var2 = var1.getPlayer();
      if (this.A.hasActiveUpdate()) {
         long var3 = this.A.getActiveUpdateVersion();
         long var5 = this.A.getPlayerDataManager().get(var2.getUniqueId()).getLastSeenUpdate();
         if (var5 < var3) {
            this.A.getPlayerDataManager().get(var2.getUniqueId()).setLastSeenUpdate(var3);
            final List var7 = this.A.getActiveUpdatePages();
            if (var7 != null && !var7.isEmpty()) {
               (new Runnable() {
                  private int C = 0;
                  private final int E = 15;

                  public void run() {
                     try {
                        if (!var2.isOnline()) {
                           return;
                        }

                        ItemStack var1 = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta var2x = (BookMeta)var1.getItemMeta();
                        if (var2x != null) {
                           String var3 = "ѕᴇʀᴠᴇʀ ᴜᴘᴅᴀᴛᴇ";
                           String var4 = "%player%";
                           var2x.setTitle(var3);
                           String var5 = "%player%".equals(var4) ? C.this.A.getName() : var4;
                           var2x.setAuthor(var5);
                           String var6 = "&aѕᴇʀᴠᴇʀ ᴜᴘᴅᴀᴛᴇ";
                           var2x.setDisplayName(org.bukkit.ChatColor.translateAlternateColorCodes('&', var6));
                           var2x.setPages(var7);
                           var1.setItemMeta(var2x);
                        }

                        try {
                           var2.openBook(var1);

                           try {
                              var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0F, 1.0F);
                           } catch (Throwable var8) {
                           }

                           return;
                        } catch (Throwable var9) {
                           ++this.C;
                           if (this.C >= 15) {
                              int var11 = var2.getInventory().firstEmpty();
                              if (var11 >= 0) {
                                 var2.getInventory().setItem(var11, var1);
                              }

                              String var12 = "&6A server update has been placed in your inventory.";
                              var2.sendMessage(ChatColor.translateAlternateColorCodes('&', var12));

                              try {
                                 var2.playSound(var2.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0F, 1.0F);
                              } catch (Throwable var7x) {
                              }

                              return;
                           }
                        }

                        C.this.A.getSchedulerAdapter().runTaskLater(this, 20L);
                     } catch (Throwable var10) {
                     }

                  }
               }).run();
            }
         }
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();

      try {
         this.A.unmarkPlayerAsUpdateWriter(var2.getUniqueId());
      } catch (Throwable var4) {
      }

   }
}
