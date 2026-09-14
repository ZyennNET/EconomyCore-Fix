package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

public class F implements Listener {
   private final PrismSurvival E;
   private final C D;
   private final B C;
   private E A;
   private final Map<UUID, Long> F = new ConcurrentHashMap();
   private final Map<UUID, BukkitTask> B = new ConcurrentHashMap();
   private final Map<UUID, BukkitTask> G = new ConcurrentHashMap();
   public static final String QUEUE_GUI_TITLE = ChatColor.translateAlternateColorCodes('&', "&8ᴅᴜᴇʟ ǫᴜᴇᴜᴇ & ᴄᴏɴꜰɪʀᴍ");

   public F(PrismSurvival var1, C var2, B var3) {
      this.E = var1;
      this.D = var2;
      this.C = var3;
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   public void setRequestManager(E var1) {
      this.A = var1;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.equals(QUEUE_GUI_TITLE)) {
         var1.setCancelled(true);
         if (var1.getRawSlot() < var1.getView().getTopInventory().getSize()) {
            if (var1.getWhoClicked() instanceof Player) {
               Player var3 = (Player)var1.getWhoClicked();
               if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
                  try {
                     var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
                  } catch (Exception var6) {
                  }
               }

               int var4 = var1.getRawSlot();
               if (var4 == 10) {
                  this.leaveQueue(var3);
                  var3.closeInventory();
               } else if (var4 == 16) {
                  if (this.isInQueue(var3.getUniqueId())) {
                     this.leaveQueue(var3);
                     Inventory var5 = var1.getView().getTopInventory();
                     if (var5.getSize() >= 27) {
                        this.A(var5, var3);
                     }
                  } else {
                     this.joinQueue(var3);
                     var3.closeInventory();
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.equals(QUEUE_GUI_TITLE)) {
         if (var1.getPlayer() instanceof Player) {
            Player var3 = (Player)var1.getPlayer();
            this.cancelGuiUpdates(var3.getUniqueId());
         }

      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      if (this.F.remove(var3) != null) {
         this.A(var3);
      }

      this.cancelGuiUpdates(var3);
   }

   public void openQueueGUI(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 27, QUEUE_GUI_TITLE);
      this.A(var2, var1);
      var1.openInventory(var2);
      BukkitTask var3 = this.E.getSchedulerAdapter().runEntityTaskTimer(var1, () -> {
         if (var1.getOpenInventory().getTitle().equals(QUEUE_GUI_TITLE)) {
            this.A(var1.getOpenInventory().getTopInventory(), var1);
         } else {
            this.cancelGuiUpdates(var1.getUniqueId());
         }

      }, 20L, 20L);
      this.B.put(var1.getUniqueId(), var3);
   }

   private void A(Inventory var1, Player var2) {
      UUID var3 = var2.getUniqueId();
      int var4 = this.D.G(var3);
      int var5 = this.D.F(var3);
      int var6 = this.D.A(var3);
      int var7 = this.F.size();
      String var8 = this.A(var7);
      int var9 = this.B(var2);
      ItemStack var10 = this.A(Material.RED_STAINED_GLASS_PANE, "&4ᴄᴀɴᴄᴇʟ", "&fClick to cancel");
      var1.setItem(10, var10);
      ItemStack var11 = this.A(Material.CLOCK, "&aᴡᴀɪᴛ ᴛɪᴍᴇ", "&7Estimated Wait: &f" + var8, "&7Currently queued: &f" + var7);
      var1.setItem(12, var11);
      ItemStack var12 = this.A(Material.GRAY_DYE, "&aѕᴛᴀᴛɪѕᴛɪᴄѕ", "&7Wins: &f" + var4, "&7Losses: &f" + var5, "&7Streak: &f" + var6);
      var1.setItem(13, var12);
      ItemStack var13 = this.A(Material.FEATHER, "&aʀᴇɢɪᴏɴ", "&7Europe (&5" + var9 + "ms&7)");
      var1.setItem(14, var13);
      boolean var14 = this.F.containsKey(var3);
      ItemStack var15;
      if (var14) {
         long var16 = (System.currentTimeMillis() - (Long)this.F.get(var3)) / 1000L;
         var15 = this.A(Material.LIME_STAINED_GLASS_PANE, "&aѕᴇᴀʀᴄʜɪɴɢ...", "&7Searching for &f" + var16 + "s", "&cClick to leave queue");
      } else {
         var15 = this.A(Material.GREEN_STAINED_GLASS_PANE, "&aᴄᴏɴꜰɪʀᴍ", "&fClick to start searching for match");
      }

      var1.setItem(16, var15);
   }

   private String A(int var1) {
      if (var1 == 0) {
         return "Instant";
      } else if (var1 == 1) {
         return "~30s";
      } else if (var1 <= 3) {
         return "~1min";
      } else {
         return var1 <= 5 ? "~2min" : "~" + var1 / 2 + "min";
      }
   }

   private int B(Player var1) {
      try {
         return var1.getPing();
      } catch (Exception var3) {
         return 0;
      }
   }

   public void joinQueue(Player var1) {
      if (this.A != null && this.A.A(var1)) {
         this.A.B(var1);
      }

      long var2 = System.currentTimeMillis();
      this.F.put(var1.getUniqueId(), var2);
      var1.sendMessage(String.valueOf(ChatColor.GREEN) + "You are now searching for a match...");
      BukkitTask var4 = this.E.getSchedulerAdapter().runEntityTaskTimer(var1, () -> {
         if (!this.F.containsKey(var1.getUniqueId())) {
            this.A(var1.getUniqueId());
         } else {
            _A var4 = this.A();
            if (this.F.containsKey(var1.getUniqueId())) {
               if (var4 == F._A.A) {
                  String var11 = ChatColor.translateAlternateColorCodes('&', "&cNo available regions right now &7- waiting for one to free up...");
                  var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var11));
               } else {
                  long var5 = (System.currentTimeMillis() - var2) / 1000L;
                  if (var5 >= 30L) {
                     String var12 = ChatColor.translateAlternateColorCodes('&', "&cUnable to find players to match");
                     var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var12));

                     try {
                        var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     } catch (Exception var10) {
                     }

                     this.leaveQueue(var1);
                  } else {
                     String var8 = this.A(this.F.size());
                     String var7 = ChatColor.translateAlternateColorCodes('&', "&7Searching for a Casual Duel... Estimated Time:&5 " + var8);
                     var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var7));
                  }
               }
            }
         }
      }, 0L, 40L);
      this.G.put(var1.getUniqueId(), var4);
      this.A();
   }

   public void leaveQueue(Player var1) {
      if (this.F.remove(var1.getUniqueId()) != null) {
         this.A(var1.getUniqueId());
      }

   }

   private void A(UUID var1) {
      BukkitTask var2 = (BukkitTask)this.G.remove(var1);
      if (var2 != null) {
         try {
            var2.cancel();
         } catch (Exception var4) {
         }
      }

   }

   public void cancelGuiUpdates(UUID var1) {
      BukkitTask var2 = (BukkitTask)this.B.remove(var1);
      if (var2 != null) {
         try {
            var2.cancel();
         } catch (Exception var4) {
         }
      }

   }

   public boolean isInQueue(UUID var1) {
      return this.F.containsKey(var1);
   }

   public int getQueueCount() {
      return this.F.size();
   }

   private _A A() {
      if (this.F.size() < 2) {
         return F._A.C;
      } else {
         Iterator var1 = this.F.keySet().iterator();
         UUID var2 = (UUID)var1.next();
         UUID var3 = (UUID)var1.next();
         Player var4 = Bukkit.getPlayer(var2);
         Player var5 = Bukkit.getPlayer(var3);
         if (var4 != null && var5 != null && var4.isOnline() && var5.isOnline()) {
            boolean var6 = this.C.D(var4, var5);
            if (var6) {
               this.F.remove(var2);
               this.F.remove(var3);
               this.A(var2);
               this.A(var3);
               this.cancelGuiUpdates(var2);
               this.cancelGuiUpdates(var3);
               var4.closeInventory();
               var5.closeInventory();
               return F._A.D;
            } else {
               return F._A.A;
            }
         } else {
            if (var4 == null || !var4.isOnline()) {
               this.F.remove(var2);
               this.A(var2);
            }

            if (var5 == null || !var5.isOnline()) {
               this.F.remove(var3);
               this.A(var3);
            }

            return F._A.C;
         }
      }
   }

   public void onGuiClose(Player var1) {
      this.cancelGuiUpdates(var1.getUniqueId());
   }

   private ItemStack A(Material var1, String var2, String... var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
         ArrayList var6 = new ArrayList();

         for(String var10 : var3) {
            var6.add(ChatColor.translateAlternateColorCodes('&', var10));
         }

         var5.setLore(var6);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private static enum _A {
      D,
      C,
      A;

      // $FF: synthetic method
      private static _A[] A() {
         return new _A[]{D, C, A};
      }
   }
}
