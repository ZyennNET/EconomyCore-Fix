package com.h2ph.J.B.B;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerDataManager;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class C implements CommandExecutor, Listener {
   private final PrismSurvival D;
   private final Map<UUID, Integer> A = new HashMap();
   private final Map<UUID, String> B = new HashMap();
   private List<PlayerDataManager.LeaderboardEntry> E = null;
   private long G = 0L;
   private static final long C = 30000L;
   private static final DecimalFormat F = new DecimalFormat("#.#");

   public C(PrismSurvival var1) {
      this.D = var1;
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "This command can only be used by players.");
         return true;
      } else {
         this.B.remove(var5.getUniqueId());
         this.A(var5);
         this.A((Player)var5, 1);
         return true;
      }
   }

   private void A(Player var1) {
      String var2 = ChatColor.translateAlternateColorCodes('&', "&8ᴍᴏѕᴛ ᴍᴏɴᴇʏ (loading...)");
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 54, var2);
      ItemStack var4 = new ItemStack(Material.CLOCK);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(String.valueOf(ChatColor.YELLOW) + "Loading...");
         var4.setItemMeta(var5);
      }

      var3.setItem(22, var4);
      var1.openInventory(var3);
   }

   private void A(Player var1, int var2) {
      this.D.getSchedulerAdapter().runTaskAsync(() -> {
         List var3;
         if (this.E != null && System.currentTimeMillis() - this.G < 30000L) {
            var3 = this.E;
         } else {
            var3 = this.D.getPlayerDataManager().getTopMoney(500);
            this.E = var3;
            this.G = System.currentTimeMillis();
         }

         String var4 = (String)this.B.get(var1.getUniqueId());
         List var5;
         if (var4 != null && !var4.isEmpty()) {
            var5 = (List)var3.stream().filter((var1x) -> var1x.name.toLowerCase().contains(var4.toLowerCase())).collect(Collectors.toList());
         } else {
            var5 = var3;
         }

         byte var6 = 45;
         int var7 = var5.size();
         int var8 = (int)Math.ceil((double)var7 / (double)var6);
         if (var8 == 0) {
            var8 = 1;
         }

         int var9 = Math.max(1, Math.min(var2, var8));
         int var10 = (var9 - 1) * var6;
         int var11 = Math.min(var10 + var6, var7);
         ArrayList var12 = new ArrayList();

         for(int var13 = var10; var13 < var11; ++var13) {
            PlayerDataManager.LeaderboardEntry var14 = (PlayerDataManager.LeaderboardEntry)var5.get(var13);
            var12.add(this.A(var14, var3.indexOf(var14) + 1));
         }

         PlayerDataManager.LeaderboardEntry var16 = (PlayerDataManager.LeaderboardEntry)var3.stream().filter((var1x) -> var1x.uuid.equals(var1.getUniqueId())).findFirst().orElse((Object)null);
         int var17 = var16 != null ? var3.indexOf(var16) + 1 : -1;
         this.D.getSchedulerAdapter().runTask(() -> {
            if (var1.isOnline()) {
               this.A.put(var1.getUniqueId(), var9);
               String var8x = ChatColor.translateAlternateColorCodes('&', "&8ᴍᴏѕᴛ ᴍᴏɴᴇʏ (page " + var9 + ")");
               Inventory var9x = Bukkit.createInventory((InventoryHolder)null, 54, var8x);
               int var10 = 0;

               for(ItemStack var12x : var12) {
                  var9x.setItem(var10++, var12x);
               }

               if (var9 > 1) {
                  var9x.setItem(45, this.A(Material.ARROW, "&aPrevious Page", "&7Click to switch page"));
               }

               if (var9 < var8) {
                  var9x.setItem(53, this.A(Material.ARROW, "&aNext Page", "&7Click to switch page"));
               }

               ItemStack var18 = new ItemStack(Material.PLAYER_HEAD);
               SkullMeta var19 = (SkullMeta)var18.getItemMeta();
               if (var19 != null) {
                  var19.setOwningPlayer(var1);
                  var19.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + var1.getName()));
                  ArrayList var13 = new ArrayList();
                  double var14;
                  String var16x;
                  if (var16 != null) {
                     var14 = var16.value;
                     var16x = "&a (#" + var17 + ")";
                  } else {
                     var14 = (double)0.0F;
                     if (this.D.getServer().getPluginManager().isPluginEnabled("Vault")) {
                        RegisteredServiceProvider var17x = this.D.getServer().getServicesManager().getRegistration(Economy.class);
                        if (var17x != null && var17x.getProvider() != null) {
                           var14 = ((Economy)var17x.getProvider()).getBalance(var1);
                        }
                     } else {
                        var14 = this.D.getPlayerDataManager().get(var1.getUniqueId()).getMoney();
                     }

                     var16x = "&7 (Not in top " + var3.size() + ")";
                  }

                  String var10002 = this.A(var14);
                  var13.add(ChatColor.translateAlternateColorCodes('&', "&fMoney:&7 $" + var10002 + var16x));
                  var19.setLore(var13);
                  var18.setItemMeta(var19);
               }

               var9x.setItem(48, var18);
               var9x.setItem(49, this.A(Material.EMERALD, "&aᴍᴏѕᴛ ᴍᴏɴᴇʏ", "&fClick to refresh"));
               var9x.setItem(50, this.A(Material.OAK_SIGN, "&aѕᴇᴀʀᴄʜ", "&fClick to search for players"));
               var1.openInventory(var9x);
            }
         });
      });
   }

   private ItemStack A(PlayerDataManager.LeaderboardEntry var1, int var2) {
      ItemStack var3 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var4 = (SkullMeta)var3.getItemMeta();
      if (var4 != null) {
         var4.setOwningPlayer(Bukkit.getOfflinePlayer(var1.uuid));
         var4.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + var1.name));
         ArrayList var5 = new ArrayList();
         String var10002 = this.A(var1.value);
         var5.add(ChatColor.translateAlternateColorCodes('&', "&fMoney:&7 $" + var10002 + "&a (#" + var2 + ")"));
         var4.setLore(var5);
         var3.setItemMeta(var4);
      }

      return var3;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.startsWith(ChatColor.translateAlternateColorCodes('&', "&8ᴍᴏѕᴛ ᴍᴏɴᴇʏ"))) {
         var1.setCancelled(true);
         if (var1.getWhoClicked() instanceof Player) {
            Player var3 = (Player)var1.getWhoClicked();
            Inventory var4 = var1.getClickedInventory();
            if (var4 != null && var4.equals(var1.getView().getTopInventory())) {
               ItemStack var5 = var1.getCurrentItem();
               if (var5 != null && var5.getType() != Material.AIR) {
                  if (var1.getSlot() < 45) {
                     if (var5.getType() == Material.PLAYER_HEAD) {
                        this.A(var3, Sound.BLOCK_TRIPWIRE_CLICK_ON);
                     }

                  } else {
                     int var6 = (Integer)this.A.getOrDefault(var3.getUniqueId(), 1);
                     if (var1.getSlot() == 45 && var5.getType() == Material.ARROW) {
                        this.A(var3, var6 - 1);
                        this.A(var3, Sound.UI_BUTTON_CLICK);
                     } else if (var1.getSlot() == 53 && var5.getType() == Material.ARROW) {
                        this.A(var3, var6 + 1);
                        this.A(var3, Sound.UI_BUTTON_CLICK);
                     } else if (var1.getSlot() == 49 && var5.getType() == Material.EMERALD) {
                        this.B.remove(var3.getUniqueId());
                        this.E = null;
                        this.A((Player)var3, 1);
                        this.A(var3, Sound.UI_BUTTON_CLICK);
                     } else if (var1.getSlot() == 50 && var5.getType() == Material.OAK_SIGN) {
                        var3.closeInventory();
                        this.D.getSignInput().getSearchInput(var3, (var2x) -> {
                           String var3x = var2x.trim();
                           if (!var3x.isEmpty()) {
                              this.B.put(var3.getUniqueId(), var3x);
                           }

                           this.A((Player)var3, 1);
                        });
                        this.A(var3, Sound.UI_BUTTON_CLICK);
                     }

                  }
               }
            }
         }
      }
   }

   private ItemStack A(Material var1, String var2, String var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
         ArrayList var6 = new ArrayList();
         var6.add(ChatColor.translateAlternateColorCodes('&', var3));
         var5.setLore(var6);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private void A(Player var1, Sound var2) {
      try {
         var1.playSound(var1.getLocation(), var2, 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }

   private String A(double var1) {
      if (var1 >= 1.0E12) {
         return this.A(var1, 1.0E12, "T");
      } else if (var1 >= (double)1.0E9F) {
         return this.A(var1, (double)1.0E9F, "B");
      } else if (var1 >= (double)1000000.0F) {
         return this.A(var1, (double)1000000.0F, "M");
      } else {
         return var1 >= (double)1000.0F ? this.A(var1, (double)1000.0F, "k") : F.format(Math.floor(var1 * (double)10.0F) / (double)10.0F);
      }
   }

   private String A(double var1, double var3, String var5) {
      double var6 = var1 / var3;
      var6 = Math.floor(var6 * (double)10.0F) / (double)10.0F;
      String var10000 = F.format(var6);
      return var10000 + var5;
   }
}
