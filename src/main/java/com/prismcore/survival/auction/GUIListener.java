package com.prismcore.survival.auction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class GUIListener implements Listener {
   private final AuctionController controller;

   public GUIListener(AuctionController var1) {
      this.controller = var1;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getWhoClicked() instanceof Player) {
         Player var2 = (Player)var1.getWhoClicked();
         FileConfiguration var3 = this.controller.getConfig();
         InventoryHolder var4 = var1.getView().getTopInventory().getHolder();
         Sound var5 = Sound.valueOf(var3.getString("sounds.prev-page"));
         Sound var6 = Sound.valueOf(var3.getString("sounds.next-page"));
         Sound var7 = Sound.valueOf(var3.getString("sounds.refresh"));
         Sound var8 = Sound.valueOf(var3.getString("sounds.default-button"));
         Sound var9 = Sound.valueOf(var3.getString("sounds.search"));
         Sound var10 = Sound.valueOf(var3.getString("sounds.confirm-sell"));
         Sound var11 = Sound.valueOf(var3.getString("sounds.villager-no"));
         if (var4 instanceof GUIHandler.MainHolder) {
            var1.setCancelled(true);
            int var36 = var1.getRawSlot();
            int var48 = (Integer)var2.getMetadata("ah-page").stream().findFirst().map(MetadataValue::asInt).orElse(1);
            if (var36 == var3.getInt("main-gui.items.previous-page.slot")) {
               if (var48 > 1) {
                  var2.playSound(var2.getLocation(), var5, 1.0F, 1.0F);
                  GUIHandler.openMainGUI(var2, Math.max(1, var48 - 1), this.controller);
               }

            } else if (var36 == var3.getInt("main-gui.items.sort.slot")) {
               var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
               String var57 = this.getNextSortMode(this.controller.getAuctionManager().getPlayerSort(var2.getUniqueId()));
               this.controller.getAuctionManager().setPlayerSort(var2.getUniqueId(), var57);
               GUIHandler.openMainGUI(var2, var48, this.controller);
            } else if (var36 == var3.getInt("main-gui.items.search.slot")) {
               var2.playSound(var2.getLocation(), var9, 1.0F, 1.0F);
               var2.setMetadata("ah-switching", new FixedMetadataValue(this.controller.getPlugin(), true));
               var2.closeInventory();
               this.controller.getPlugin().getSignInput().getSearchInput(var2, (var2x) -> {
                  String var3 = var2x.trim().toLowerCase();
                  var2.setMetadata("ah-filter", new FixedMetadataValue(this.controller.getPlugin(), var3));
                  GUIHandler.openMainGUI(var2, 1, this.controller);
               });
            } else if (var36 == 48) {
               var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               String var56 = var2.hasMetadata("ah-cat") ? ((MetadataValue)var2.getMetadata("ah-cat").get(0)).asString() : "All";
               String var64 = GUIHandler.getNextCategory(this.controller, var56);
               var2.setMetadata("ah-cat", new FixedMetadataValue(this.controller.getPlugin(), var64));
               GUIHandler.openMainGUI(var2, var48, this.controller);
            } else if (var36 == var3.getInt("main-gui.items.refresh.slot")) {
               var2.playSound(var2.getLocation(), var7, 1.0F, 1.0F);
               this.controller.getAuctionManager().refresh(() -> GUIHandler.openMainGUI(var2, var48, this.controller));
            } else if (var36 == var3.getInt("main-gui.items.your-items.slot")) {
               var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
               GUIHandler.openYourItemsGUI(var2, this.controller);
            } else if (var36 == var3.getInt("main-gui.items.next-page.slot")) {
               var2.playSound(var2.getLocation(), var6, 1.0F, 1.0F);
               GUIHandler.openMainGUI(var2, var48 + 1, this.controller);
            } else {
               int var55 = var3.getInt("main-gui.items-per-page", 45);
               if (var36 >= 0 && var36 < var55) {
                  String var62 = var2.hasMetadata("ah-filter") ? ((MetadataValue)var2.getMetadata("ah-filter").get(0)).asString() : "";
                  if (var62 == null) {
                     var62 = "";
                  }

                  var62 = var62.trim().toLowerCase();
                  String var70 = var2.hasMetadata("ah-cat") ? ((MetadataValue)var2.getMetadata("ah-cat").get(0)).asString() : "All";
                  List var72 = (List)this.controller.getAuctionManager().getActiveItems().stream().filter((var3x) -> {
                     boolean var4 = var62.isEmpty() || var3x.getSearchName().contains(var62) || var3x.getSearchSeller().contains(var62);
                     boolean var5 = var70.equals("All") || this.controller.getFilterConfig().getStringList(var70).contains(var3x.getItemStack().getType().name());
                     return var4 && var5;
                  }).collect(Collectors.toList());
                  GUIHandler.sortItems(var72, this.controller.getAuctionManager().getPlayerSort(var2.getUniqueId()));
                  int var74 = (var48 - 1) * var55 + var36;
                  if (var74 < var72.size()) {
                     var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                     AuctionItem var75 = (AuctionItem)var72.get(var74);
                     if (var2.hasMetadata("ah-admin-view")) {
                        GUIHandler.openAdminPlayerDetailsGUI(var2, var75.getSeller(), this.controller);
                        return;
                     }

                     if (var75.getSeller().equals(var2.getName())) {
                        var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                     } else {
                        GUIHandler.openBuyConfirm(var2, var75, this.controller);
                     }
                  }
               }

            }
         } else if (var4 instanceof GUIHandler.FilterHolder) {
            var1.setCancelled(true);
            if (var1.getCurrentItem() != null) {
               String var35 = ChatColor.stripColor(var1.getCurrentItem().getItemMeta().getDisplayName());
               var2.setMetadata("ah-cat", new FixedMetadataValue(this.controller.getPlugin(), var35));
               GUIHandler.openMainGUI(var2, 1, this.controller);
            }
         } else if (var4 instanceof GUIHandler.SellConfirmHolder) {
            var1.setCancelled(true);
            int var34 = var1.getRawSlot();
            if (var34 == this.controller.getConfig().getInt("sell-confirm-gui.decline-button.slot")) {
               var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
               var2.closeInventory();
            } else {
               if (var34 == this.controller.getConfig().getInt("sell-confirm-gui.confirm-button.slot")) {
                  var2.playSound(var2.getLocation(), var10, 1.0F, 1.0F);
                  List var47 = var2.getMetadata("ah-sell-price");
                  double var54 = var47.isEmpty() ? (double)0.0F : ((MetadataValue)var47.get(0)).asDouble();
                  ItemStack var67 = var2.getInventory().getItemInMainHand();
                  if (var67 == null || var67.getType() == Material.AIR) {
                     var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.item-not-available")));
                     var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                     var2.closeInventory();
                     return;
                  }

                  var2.getInventory().setItemInMainHand((ItemStack)null);
                  AuctionItem var69 = new AuctionItem(UUID.randomUUID(), var2.getName(), var67, var54, System.currentTimeMillis(), this.controller.getAuctionManager().getDefaultTime());
                  this.controller.getAuctionManager().addItem(var69);
                  var2.playSound(var2.getLocation(), var10, 1.0F, 1.0F);
                  var2.closeInventory();
               }

            }
         } else if (var4 instanceof GUIHandler.BuyConfirmHolder) {
            var1.setCancelled(true);
            int var33 = var1.getRawSlot();
            if (var33 == this.controller.getConfig().getInt("purchase-confirm-gui.decline-button.slot")) {
               var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
               int var46 = (Integer)var2.getMetadata("ah-page").stream().findFirst().map(MetadataValue::asInt).orElse(1);
               GUIHandler.openMainGUI(var2, var46, this.controller);
            } else {
               if (var33 == this.controller.getConfig().getInt("purchase-confirm-gui.confirm-button.slot")) {
                  var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                  List var45 = var2.getMetadata("ah-buy-item");
                  if (var45.isEmpty()) {
                     return;
                  }

                  Optional var53 = this.controller.getAuctionManager().getItems().stream().filter((var1x) -> var1x.getId().toString().equals(((MetadataValue)var45.get(0)).asString())).findFirst();
                  if (!var53.isPresent()) {
                     var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.item-not-available")));
                     var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                     var2.closeInventory();
                     return;
                  }

                  AuctionItem var61 = (AuctionItem)var53.get();
                  if (this.controller.getAuctionManager().isExpired(var61)) {
                     var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.item-not-available")));
                     var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                     var2.closeInventory();
                     return;
                  }

                  if (var2.getInventory().firstEmpty() == -1) {
                     var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.inventory-full")));
                     var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                     var2.closeInventory();
                     return;
                  }

                  if (!EconomyHandler.chargePlayer(var2, var61.getPrice())) {
                     var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.insufficient-funds")));
                     var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                     var2.closeInventory();
                     return;
                  }

                  String var66 = var61.getSeller();
                  boolean var68 = EconomyHandler.depositByName(var66, var61.getPrice());
                  var2.getInventory().addItem(new ItemStack[]{var61.getItemStack()});
                  this.controller.getAuctionManager().removeItem(var61);
                  this.controller.getTransactionManager().recordSale(var61.getItemStack(), var61.getPrice(), var61.getSeller(), var2.getName());
                  String var18 = Utils.prettifyMaterialName(var61.getItemStack().getType());
                  var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.purchase-success").replace("{priceFormatted}", Utils.formatNumber(var61.getPrice())).replace("{seller}", var66).replace("{item}", var18)));

                  try {
                     Sound var19 = Sound.valueOf(this.controller.getConfig().getString("sounds.sale-notify", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                     var2.playSound(var2.getLocation(), var19, 1.0F, 1.0F);
                  } catch (Exception var24) {
                  }

                  Player var71 = Bukkit.getPlayer(var66);
                  if (var71 != null && var71.isOnline()) {
                     String var73 = this.controller.getConfig().getString("messages.sold-notify").replace("{item}", var18).replace("{buyer}", var2.getName()).replace("{priceFormatted}", Utils.formatNumber(var61.getPrice()));
                     String var21 = Utils.formatColors(var73);
                     var71.sendMessage(var21);
                     var71.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var21));

                     try {
                        Sound var22 = Sound.valueOf(this.controller.getConfig().getString("sounds.sale-notify", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                        var71.playSound(var71.getLocation(), var22, 1.0F, 1.0F);
                     } catch (Exception var23) {
                     }
                  } else {
                     UUID var20 = Bukkit.getOfflinePlayer(var66).getUniqueId();
                     this.controller.getAuctionManager().addPendingSale(var20, var2.getName(), var18, var61.getPrice());
                  }

                  var2.closeInventory();
                  if (!var68) {
                     Logger var10000 = this.controller.getPlugin().getLogger();
                     double var10001 = var61.getPrice();
                     var10000.warning("[Auction] Failed to deposit " + var10001 + " to " + var66 + " via Vault. Check your economy plugin.");
                  }
               }

            }
         } else if (!(var4 instanceof GUIHandler.YourItemsHolder)) {
            if (var4 instanceof GUIHandler.TransactionsHolder) {
               var1.setCancelled(true);
               int var32 = var1.getRawSlot();
               int var44 = (Integer)var2.getMetadata("tx-page").stream().findFirst().map(MetadataValue::asInt).orElse(1);
               if (var32 == var3.getInt("transactions-gui.items.previous-page.slot")) {
                  if (var44 > 1) {
                     var2.playSound(var2.getLocation(), var5, 1.0F, 1.0F);
                     GUIHandler.openTransactionsGUI(var2, Math.max(1, var44 - 1), this.controller);
                  }

               } else if (var32 == var3.getInt("transactions-gui.items.refresh.slot")) {
                  var2.playSound(var2.getLocation(), var7, 1.0F, 1.0F);
                  var2.removeMetadata("tx-filter", this.controller.getPlugin());
                  var2.removeMetadata("awaiting-tx-search", this.controller.getPlugin());
                  GUIHandler.openTransactionsGUI(var2, 1, this.controller);
               } else if (var32 == var3.getInt("transactions-gui.items.search.slot")) {
                  var2.playSound(var2.getLocation(), var9, 1.0F, 1.0F);
                  var2.setMetadata("ah-switching", new FixedMetadataValue(this.controller.getPlugin(), true));
                  var2.closeInventory();
                  this.controller.getPlugin().getSignInput().getSearchInput(var2, (var2x) -> {
                     String var3 = var2x.trim().toLowerCase();
                     var2.setMetadata("tx-filter", new FixedMetadataValue(this.controller.getPlugin(), var3));
                     GUIHandler.openTransactionsGUI(var2, 1, this.controller);
                  });
               } else if (var32 == var3.getInt("transactions-gui.items.next-page.slot")) {
                  var2.playSound(var2.getLocation(), var6, 1.0F, 1.0F);
                  GUIHandler.openTransactionsGUI(var2, var44 + 1, this.controller);
               }
            } else if (var4 instanceof GUIHandler.AdminPlayerDetailsHolder) {
               var1.setCancelled(true);
               int var31 = var1.getRawSlot();
               if (var31 >= 0 && var31 < 27) {
                  if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
                     String var43 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                     List var52 = (List)this.controller.getAuctionManager().getItems().stream().filter((var1x) -> var1x.getSeller().equalsIgnoreCase(var43)).collect(Collectors.toList());
                     if (var31 < var52.size()) {
                        AuctionItem var60 = (AuctionItem)var52.get(var31);
                        var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                        GUIHandler.openItemManagementGUI(var2, var60, this.controller);
                     }
                  }

               } else if (var31 == 48) {
                  String var42 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                  GUIHandler.openAdminDeleteConfirmGUI(var2, var42, this.controller);
               } else if (var31 == 49) {
                  var2.playSound(var2.getLocation(), var9, 1.0F, 1.0F);
                  var2.setMetadata("ah-switching", new FixedMetadataValue(this.controller.getPlugin(), true));
                  var2.closeInventory();
                  this.controller.getPlugin().getSignInput().getSearchInput(var2, (var2x) -> {
                     String var3 = var2x.trim().toLowerCase();
                     var2.setMetadata("ah-filter", new FixedMetadataValue(this.controller.getPlugin(), var3));
                     GUIHandler.openMainGUI(var2, 1, this.controller);
                  });
               } else if (var31 == 50) {
                  var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                  String var41 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                  var2.removeMetadata("tx-filter", this.controller.getPlugin());
                  GUIHandler.openAdminTransactionsGUI(var2, Bukkit.getOfflinePlayer(var41), 1, this.controller);
               } else if (var31 == 45) {
                  var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                  GUIHandler.openAdminPlayerListGUI(var2, 1, this.controller);
               }
            } else if (var4 instanceof GUIHandler.AdminPlayerListHolder) {
               var1.setCancelled(true);
               int var30 = var1.getRawSlot();
               int var40 = (Integer)var2.getMetadata("ah-admin-list-page").stream().findFirst().map(MetadataValue::asInt).orElse(1);
               if (var30 == 45 && var40 > 1) {
                  var2.playSound(var2.getLocation(), var5, 1.0F, 1.0F);
                  GUIHandler.openAdminPlayerListGUI(var2, var40 - 1, this.controller);
               } else if (var30 == 48) {
                  var2.playSound(var2.getLocation(), var9, 1.0F, 1.0F);
                  var2.setMetadata("ah-switching", new FixedMetadataValue(this.controller.getPlugin(), true));
                  var2.closeInventory();
                  this.controller.getPlugin().getSignInput().getSearchInput(var2, (var2x) -> {
                     String var3 = var2x.trim().toLowerCase();
                     var2.setMetadata("ah-admin-player-filter", new FixedMetadataValue(this.controller.getPlugin(), var3));
                     GUIHandler.openAdminPlayerListGUI(var2, 1, this.controller);
                  });
               } else if (var30 == 49) {
                  var2.playSound(var2.getLocation(), var7, 1.0F, 1.0F);
                  GUIHandler.openAdminPlayerListGUI(var2, var40, this.controller);
               } else if (var30 == 53 && var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
                  var2.playSound(var2.getLocation(), var6, 1.0F, 1.0F);
                  GUIHandler.openAdminPlayerListGUI(var2, var40 + 1, this.controller);
               } else {
                  if (var30 < 45 && var1.getCurrentItem() != null && var1.getCurrentItem().getType() == Material.PLAYER_HEAD) {
                     ItemMeta var51 = var1.getCurrentItem().getItemMeta();
                     PersistentDataContainer var59 = var51.getPersistentDataContainer();
                     NamespacedKey var65 = new NamespacedKey(this.controller.getPlugin(), "admin-head-target");
                     if (var59.has(var65, PersistentDataType.STRING)) {
                        String var17 = (String)var59.get(var65, PersistentDataType.STRING);
                        var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                        GUIHandler.openAdminPlayerDetailsGUI(var2, var17, this.controller);
                     }
                  }

               }
            } else if (var4 instanceof GUIHandler.AdminTransactionsHolder) {
               var1.setCancelled(true);
               int var29 = var1.getRawSlot();
               int var39 = (Integer)var2.getMetadata("tx-page").stream().findFirst().map(MetadataValue::asInt).orElse(1);
               String var50 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
               if (var29 == 45) {
                  if (var39 > 1) {
                     var2.playSound(var2.getLocation(), var5, 1.0F, 1.0F);
                     GUIHandler.openAdminTransactionsGUI(var2, Bukkit.getOfflinePlayer(var50), var39 - 1, this.controller);
                  } else {
                     var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                     GUIHandler.openAdminPlayerDetailsGUI(var2, var50, this.controller);
                  }

               } else if (var29 == 49) {
                  var2.playSound(var2.getLocation(), var7, 1.0F, 1.0F);
                  var2.removeMetadata("tx-filter", this.controller.getPlugin());
                  GUIHandler.openAdminTransactionsGUI(var2, Bukkit.getOfflinePlayer(var50), var39, this.controller);
               } else if (var29 == 50) {
                  var2.playSound(var2.getLocation(), var9, 1.0F, 1.0F);
                  var2.setMetadata("ah-switching", new FixedMetadataValue(this.controller.getPlugin(), true));
                  var2.closeInventory();
                  this.controller.getPlugin().getSignInput().getSearchInput(var2, (var2x) -> {
                     if (var2.hasMetadata("ah-admin-target")) {
                        String var3 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                        String var4 = var2x.trim().toLowerCase();
                        var2.setMetadata("tx-filter", new FixedMetadataValue(this.controller.getPlugin(), var4));
                        GUIHandler.openAdminTransactionsGUI(var2, Bukkit.getOfflinePlayer(var3), 1, this.controller);
                     }
                  });
               } else if (var29 == 53) {
                  var2.playSound(var2.getLocation(), var6, 1.0F, 1.0F);
                  GUIHandler.openAdminTransactionsGUI(var2, Bukkit.getOfflinePlayer(var50), var39 + 1, this.controller);
               }
            } else if (var4 instanceof GUIHandler.ItemManagementHolder) {
               var1.setCancelled(true);
               int var28 = var1.getRawSlot();
               if (!var2.hasMetadata("ah-manage-item")) {
                  var2.closeInventory();
               } else {
                  String var38 = ((MetadataValue)var2.getMetadata("ah-manage-item").get(0)).asString();
                  if (var28 == 11) {
                     var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                     var2.setMetadata("ah-switching", new FixedMetadataValue(this.controller.getPlugin(), true));
                     var2.closeInventory();
                     var2.setMetadata("ah-price-edit-id", new FixedMetadataValue(this.controller.getPlugin(), var38));
                     this.controller.getPlugin().getSignInput().getSearchInput(var2, (var2x) -> {
                        String var3 = var2x.trim().toLowerCase();
                        if (var2.hasMetadata("ah-price-edit-id")) {
                           String var4 = ((MetadataValue)var2.getMetadata("ah-price-edit-id").get(0)).asString();
                           var2.removeMetadata("ah-price-edit-id", this.controller.getPlugin());
                           double var5 = Utils.parsePrice(var3);
                           if (var5 < (double)0.0F) {
                              var2.sendMessage(Utils.formatColors("&#ff4444Invalid price! Use numbers or k/m/b/t."));
                           } else if (var5 > 1.0E12) {
                              var2.sendMessage(Utils.formatColors("&#ff4444Price cannot exceed 1T!"));
                           } else {
                              try {
                                 UUID var7 = UUID.fromString(var4);
                                 this.controller.getAuctionManager().updatePrice(var7, var5);
                                 var2.sendMessage(Utils.formatColors("&#34ee80Price updated!"));
                              } catch (Exception var8) {
                              }
                           }

                           if (var2.hasMetadata("ah-admin-target")) {
                              String var9 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                              GUIHandler.openAdminPlayerDetailsGUI(var2, var9, this.controller);
                           } else {
                              GUIHandler.openMainGUI(var2, 1, this.controller);
                           }

                        }
                     });
                  } else if (var28 == 15) {
                     Optional var49 = this.controller.getAuctionManager().getItems().stream().filter((var1x) -> var1x.getId().toString().equals(var38)).findFirst();
                     if (var49.isPresent()) {
                        AuctionItem var15 = (AuctionItem)var49.get();
                        this.controller.getAuctionManager().removeItem(var15);

                        try {
                           var2.playSound(var2.getLocation(), Sound.UI_STONECUTTER_TAKE_RESULT, 1.0F, 1.0F);
                        } catch (Exception var25) {
                           var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                        }
                     }

                     if (var2.hasMetadata("ah-admin-target")) {
                        String var58 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                        GUIHandler.openAdminPlayerDetailsGUI(var2, var58, this.controller);
                     } else {
                        GUIHandler.openMainGUI(var2, 1, this.controller);
                     }

                  }
               }
            } else if (var4 instanceof GUIHandler.AdminDeleteConfirmHolder) {
               var1.setCancelled(true);
               int var27 = var1.getRawSlot();
               String var37 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
               if (var27 == 11) {
                  var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                  GUIHandler.openAdminPlayerDetailsGUI(var2, var37, this.controller);
               } else if (var27 == 15) {
                  this.controller.getAuctionManager().removeAllItems(var37);

                  try {
                     var2.playSound(var2.getLocation(), Sound.UI_STONECUTTER_TAKE_RESULT, 1.0F, 1.0F);
                  } catch (Exception var26) {
                     var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                  }

                  GUIHandler.openAdminPlayerDetailsGUI(var2, var37, this.controller);
               }
            }
         } else {
            var1.setCancelled(true);
            int var12 = var1.getRawSlot();
            if (var12 == this.controller.getConfig().getInt("your-items-gui.back-button.slot")) {
               var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
               GUIHandler.openMainGUI(var2, 1, this.controller);
            } else if (var12 == this.controller.getConfig().getInt("your-items-gui.transactions-button.slot")) {
               var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
               GUIHandler.openTransactionsGUI(var2, 1, this.controller);
            } else {
               if (var12 >= 0 && var12 < 18 || var12 >= 19 && var12 < 26) {
                  List var14 = (List)this.controller.getAuctionManager().getItems().stream().filter((var1x) -> var1x.getSeller().equalsIgnoreCase(var2.getName())).collect(Collectors.toList());
                  int var13 = var12 < 18 ? var12 : var12 - 1;
                  if (var13 < var14.size()) {
                     var2.playSound(var2.getLocation(), var8, 1.0F, 1.0F);
                     AuctionItem var16 = (AuctionItem)var14.get(var13);
                     if (var2.getInventory().firstEmpty() == -1) {
                        var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.inventory-full")));
                        var2.playSound(var2.getLocation(), var11, 1.0F, 1.0F);
                        return;
                     }

                     this.controller.getAuctionManager().removeItem(var16);
                     var2.getInventory().addItem(new ItemStack[]{var16.getItemStack()});
                     var2.sendMessage(Utils.formatColors(this.controller.getConfig().getString("messages.returned-item")));
                     var2.closeInventory();
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (var1.getPlayer() instanceof Player) {
         Player var2 = (Player)var1.getPlayer();
         InventoryHolder var3 = var1.getView().getTopInventory().getHolder();
         if (var3 instanceof GUIHandler.AdminDeleteConfirmHolder && !var2.hasMetadata("ah-switching") && var2.hasMetadata("ah-admin-target")) {
            String var4 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
            var2.getScheduler().run(this.controller.getPlugin(), (var3x) -> GUIHandler.openAdminPlayerDetailsGUI(var2, var4, this.controller), (Runnable)null);
         }

         if (var3 instanceof GUIHandler.ItemManagementHolder && !var2.hasMetadata("ah-switching") && var2.hasMetadata("ah-admin-target")) {
            String var5 = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
            var2.getScheduler().run(this.controller.getPlugin(), (var3x) -> GUIHandler.openAdminPlayerDetailsGUI(var2, var5, this.controller), (Runnable)null);
         }

         this.controller.stopUpdateTask(var2);
         if (var2.hasMetadata("ah-switching")) {
            var2.removeMetadata("ah-switching", this.controller.getPlugin());
         } else {
            this.controller.getPlugin().getSchedulerAdapter().runEntityTaskLater(var2, () -> {
               if (var3 instanceof GUIHandler.TransactionsHolder) {
                  GUIHandler.openYourItemsGUI(var2, this.controller);
               } else if (var3 instanceof GUIHandler.YourItemsHolder) {
                  GUIHandler.openMainGUI(var2, 1, this.controller);
               } else if (var3 instanceof GUIHandler.AdminTransactionsHolder) {
                  String var3x = ((MetadataValue)var2.getMetadata("ah-admin-target").get(0)).asString();
                  GUIHandler.openAdminPlayerDetailsGUI(var2, var3x, this.controller);
               } else if (var3 instanceof GUIHandler.AdminPlayerDetailsHolder) {
                  if (var2.hasMetadata("ah-admin-view")) {
                     GUIHandler.openAdminPlayerListGUI(var2, 1, this.controller);
                  } else {
                     GUIHandler.openMainGUI(var2, 1, this.controller);
                  }
               }

            }, 1L);
         }
      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.controller.getPlugin().getSchedulerAdapter().runTaskLater(() -> {
         if (var2.isOnline()) {
            List var2x = this.controller.getAuctionManager().getPendingSales(var2.getUniqueId());
            if (!var2x.isEmpty()) {
               if (var2x.size() == 1) {
                  AuctionManager.OfflineSale var3 = (AuctionManager.OfflineSale)var2x.get(0);
                  String var4 = this.controller.getConfig().getString("messages.sold-notify-offline").replace("{buyer}", var3.buyer).replace("{item}", var3.item).replace("{priceFormatted}", Utils.formatNumber(var3.price));
                  String var5 = Utils.formatColors(var4);
                  var2.sendMessage(var5);
                  var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var5));
               } else {
                  double var8 = var2x.stream().mapToDouble((var0) -> var0.price).sum();
                  String var10 = this.controller.getConfig().getString("messages.sold-notify-offline-multi").replace("{priceFormatted}", Utils.formatNumber(var8));
                  String var6 = Utils.formatColors(var10);
                  var2.sendMessage(var6);
                  var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var6));
               }

               try {
                  Sound var9 = Sound.valueOf(this.controller.getConfig().getString("sounds.sale-notify", "ENTITY_EXPERIENCE_ORB_PICKUP"));
                  var2.playSound(var2.getLocation(), var9, 1.0F, 1.0F);
               } catch (Exception var7) {
               }

               this.controller.getAuctionManager().clearPendingSales(var2.getUniqueId());
            }
         }
      }, 40L);
   }

   private String getNextSortMode(String var1) {
      switch (var1) {
         case "Highest Price" -> {
            return "Lowest Price";
         }
         case "Lowest Price" -> {
            return "Last Listed";
         }
         case "Last Listed" -> {
            return "Recently Listed";
         }
         case "Recently Listed" -> {
            return "Highest Price";
         }
         default -> {
            return "Highest Price";
         }
      }
   }
}
