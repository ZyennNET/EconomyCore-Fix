package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.bounty.BountyManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class J implements CommandExecutor, Listener {
   private final PrismSurvival D;
   private final BountyManager E;
   private final Map<UUID, _A> C = new HashMap();
   private final Map<UUID, Integer> A = new HashMap();
   private final Map<UUID, String> B = new HashMap();

   public J(PrismSurvival var1, BountyManager var2) {
      this.D = var1;
      this.E = var2;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (var4.length == 0) {
         this.B(var5, 1);
         return true;
      } else if (var4[0].equalsIgnoreCase("add") && var4.length >= 3) {
         String var6 = var4[1];
         String var7 = var4[2];
         OfflinePlayer var8 = Bukkit.getOfflinePlayer(var6);
         if (var8 != null && (var8.hasPlayedBefore() || var8.isOnline())) {
            if (var8.getUniqueId().equals(var5.getUniqueId())) {
               String var13 = ChatColor.translateAlternateColorCodes('&', "&cYou cannot add bounty on yourself.");
               var5.sendMessage(var13);
               var5.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var13));
               var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               return true;
            } else {
               try {
                  double var12 = Double.parseDouble(var7);
                  if (var12 <= (double)0.0F) {
                     var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     return true;
                  }

                  this.A(var5, var8, var12);
               } catch (NumberFormatException var11) {
                  var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               }

               return true;
            }
         } else {
            String var9 = ChatColor.translateAlternateColorCodes('&', "&cThat player does not exist.");
            var5.sendMessage(var9);
            var5.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var9));
            var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return true;
         }
      } else if (var4.length == 1) {
         this.B.put(var5.getUniqueId(), var4[0]);
         this.B(var5, 1);
         return true;
      } else {
         return true;
      }
   }

   private void A(Player var1, OfflinePlayer var2, double var3) {
      this.C.put(var1.getUniqueId(), new _A(var2, var3));
      Inventory var5 = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.translateAlternateColorCodes('&', "&8ᴄᴏɴꜰɪʀᴍ ʙᴏᴜɴᴛʏ"));
      ItemStack var6 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var7 = (SkullMeta)var6.getItemMeta();
      if (var7 != null) {
         var7.setOwningPlayer(var2);
         var7.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + var2.getName()));
         var6.setItemMeta(var7);
      }

      var5.setItem(13, var6);
      var5.setItem(11, this.A(Material.RED_STAINED_GLASS_PANE, "&4ᴄᴀɴᴄᴇʟ", "&fClick to cancel"));
      Material var10003 = Material.LIME_STAINED_GLASS_PANE;
      String[] var10005 = new String[]{"&fClick to add bounty", null};
      String var10008 = this.A(var3);
      var10005[1] = "&7($" + var10008 + ")";
      var5.setItem(15, this.A(var10003, "&aᴄᴏɴꜰɪʀᴍ", var10005));
      var1.openInventory(var5);
   }

   private void B(Player var1, int var2) {
      this.D.getSchedulerAdapter().runTaskAsync(() -> {
         BountyManager.SortType var3 = this.E.getSortType(var1.getUniqueId());
         List var4 = this.E.getBounties(var3);
         String var5 = (String)this.B.get(var1.getUniqueId());
         if (var5 != null) {
            var4 = (List)var4.stream().filter((var1x) -> var1x.C.toLowerCase().contains(var5.toLowerCase())).collect(Collectors.toList());
         }

         byte var6 = 45;
         int var7 = (int)Math.ceil((double)var4.size() / (double)var6);
         if (var7 == 0) {
            var7 = 1;
         }

         int var8 = Math.max(1, Math.min(var2, var7));
         int var9 = (var8 - 1) * var6;
         int var10 = Math.min(var9 + var6, var4.size());
         Inventory var11 = Bukkit.createInventory((InventoryHolder)null, 54, ChatColor.translateAlternateColorCodes('&', "&8ʙᴏᴜɴᴛɪᴇѕ (Page " + var8 + ")"));

         for(int var12 = var9; var12 < var10; ++var12) {
            com.h2ph.T.A.K._A var13 = (com.h2ph.T.A.K._A)var4.get(var12);
            var11.setItem(var12 - var9, this.A(var13));
         }

         if (var8 > 1) {
            var11.setItem(45, this.A(Material.ARROW, "&aʙᴀᴄᴋ", "&fClick to go to the previous page"));
         }

         if (var8 < var7) {
            var11.setItem(53, this.A(Material.ARROW, "&aɴᴇхᴛ", "&fClick to go to the next page"));
         }

         String var14 = var3 == BountyManager.SortType.AMOUNT ? "&a(Amount)" : "&a(Recently Set)";
         var11.setItem(48, this.A(Material.HOPPER, "&aѕᴏʀᴛ", "&fClick to sort " + var14));
         var11.setItem(49, this.A(Material.SKELETON_SKULL, "&aʙᴏᴜɴᴛɪᴇѕ", "&fClick to refresh", "&7Set a bounty using this:", "&7&o/bounty add (player) (amount)"));
         var11.setItem(50, this.A(Material.OAK_SIGN, "&aѕᴇᴀʀᴄʜ", "&fClick to search"));
         this.D.getSchedulerAdapter().runTask(() -> {
            this.A.put(var1.getUniqueId(), var8);
            var1.openInventory(var11);
         });
      });
   }

   private ItemStack A(com.h2ph.T.A.K._A var1) {
      ItemStack var2 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var3 = (SkullMeta)var2.getItemMeta();
      if (var3 != null) {
         var3.setOwningPlayer(Bukkit.getOfflinePlayer(var1.B));
         var3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + var1.C));
         ArrayList var4 = new ArrayList();
         String var10002 = this.A(var1.A);
         var4.add(ChatColor.translateAlternateColorCodes('&', "&fBounty: &7$" + var10002));
         var3.setLore(var4);
         var2.setItemMeta(var3);
      }

      return var2;
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

   private String A(double var1) {
      return this.E.formatBountyMoney(var1);
   }

   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var1.getWhoClicked() instanceof Player) {
         Player var3 = (Player)var1.getWhoClicked();
         ItemStack var4 = var1.getCurrentItem();
         if (var2.startsWith(ChatColor.translateAlternateColorCodes('&', "&8ʙᴏᴜɴᴛɪᴇѕ"))) {
            var1.setCancelled(true);
            if (var4 != null && var4.getType() != Material.AIR) {
               if (var1.getClickedInventory() == var1.getView().getTopInventory()) {
                  int var7 = var1.getSlot();
                  int var8 = (Integer)this.A.getOrDefault(var3.getUniqueId(), 1);
                  if (var7 == 45 && var4.getType() == Material.ARROW) {
                     var3.playSound(var3.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
                     this.B(var3, var8 - 1);
                  } else if (var7 == 53 && var4.getType() == Material.ARROW) {
                     var3.playSound(var3.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.0F);
                     this.B(var3, var8 + 1);
                  } else if (var7 == 48 && var4.getType() == Material.HOPPER) {
                     var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
                     this.E.toggleSort(var3.getUniqueId());
                     this.B(var3, var8);
                  } else if (var7 == 49) {
                     var3.playSound(var3.getLocation(), Sound.UI_TOAST_IN, 1.0F, 1.0F);
                     this.E.refreshCache();
                     this.B.remove(var3.getUniqueId());
                     this.B(var3, 1);
                  } else if (var7 == 50 && var4.getType() == Material.OAK_SIGN) {
                     var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
                     var3.closeInventory();
                     this.D.getSignInput().getSearchInput(var3, (var2x) -> {
                        if (var2x.length() > 0) {
                           this.B.put(var3.getUniqueId(), var2x);
                        } else {
                           this.B.remove(var3.getUniqueId());
                        }

                        this.B(var3, 1);
                     });
                  } else {
                     var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
                  }

               }
            }
         } else {
            if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄᴏɴꜰɪʀᴍ ʙᴏᴜɴᴛʏ"))) {
               var1.setCancelled(true);
               if (var4 == null || var4.getType() == Material.AIR) {
                  return;
               }

               if (var1.getClickedInventory() != var1.getView().getTopInventory()) {
                  return;
               }

               int var5 = var1.getSlot();
               _A var6 = (_A)this.C.get(var3.getUniqueId());
               if (var6 == null) {
                  var3.closeInventory();
                  return;
               }

               if (var5 == 11) {
                  var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
                  var3.closeInventory();
                  this.C.remove(var3.getUniqueId());
               } else if (var5 == 15) {
                  var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
                  var3.closeInventory();
                  this.E.addBounty(var3, var6.B, var6.A);
                  this.C.remove(var3.getUniqueId());
               } else {
                  var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
               }
            }

         }
      }
   }

   private static class _A {
      final OfflinePlayer B;
      final double A;

      _A(OfflinePlayer var1, double var2) {
         this.B = var1;
         this.A = var2;
      }
   }
}
