package com.h2ph.W;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.io.IOException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.TileState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class G implements Listener {
   private final PrismSurvival A;
   private final NamespacedKey B;

   public G(PrismSurvival var1) {
      this.A = var1;
      this.B = new NamespacedKey(var1, "crate_id");
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent var1) {
      ItemStack var2 = var1.getItemInHand();
      Material var3 = var2.getType();
      if (var3 == Material.CHEST || var3 == Material.ENDER_CHEST || var3.name().endsWith("SHULKER_BOX")) {
         if (var2.hasItemMeta()) {
            PersistentDataContainer var4 = var2.getItemMeta().getPersistentDataContainer();
            if (var4.has(this.B, PersistentDataType.STRING)) {
               String var5 = (String)var4.get(this.B, PersistentDataType.STRING);
               if (var1.getBlockPlaced().getState() instanceof TileState) {
                  TileState var6 = (TileState)var1.getBlockPlaced().getState();
                  var6.getPersistentDataContainer().set(this.B, PersistentDataType.STRING, var5);
                  var6.update();
                  this.A.getCrateLocationRegistry().addLocation(var5, var1.getBlockPlaced().getLocation());
                  var1.getPlayer().sendMessage(String.valueOf(ChatColor.GREEN) + "Crate placed successfully!");
               }
            }

         }
      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent var1) {
      if (var1.getBlock().getState() instanceof TileState) {
         TileState var2 = (TileState)var1.getBlock().getState();
         if (var2.getPersistentDataContainer().has(this.B, PersistentDataType.STRING)) {
            this.A.getCrateLocationRegistry().removeLocation(var1.getBlock().getLocation());
         }
      }

   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent var1) {
      if (var1.getAction() == Action.RIGHT_CLICK_BLOCK) {
         if (var1.getClickedBlock() != null) {
            Material var2 = var1.getClickedBlock().getType();
            if (var2 == Material.CHEST || var2 == Material.ENDER_CHEST || var2.name().endsWith("SHULKER_BOX")) {
               if (var1.getClickedBlock().getState() instanceof TileState) {
                  TileState var3 = (TileState)var1.getClickedBlock().getState();
                  PersistentDataContainer var4 = var3.getPersistentDataContainer();
                  if (var4.has(this.B, PersistentDataType.STRING)) {
                     var1.setCancelled(true);
                     String var5 = (String)var4.get(this.B, PersistentDataType.STRING);
                     var1.getPlayer().setMetadata("prism_active_crate", new FixedMetadataValue(this.A, var5));
                     this.A(var1.getPlayer(), var5);
                  }

               }
            }
         }
      }
   }

   private void A(Player var1, String var2) {
      File var3 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var3.exists()) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Error: Crate configuration not found.");
      } else {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         String var5 = ((FileConfiguration)var4).getString("type", "NORMAL");
         if (var5.equalsIgnoreCase("CAROUSEL")) {
            this.A.getCarouselManager().openCarouselGUI(var1, var2);
         } else {
            Inventory var6 = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.translateAlternateColorCodes('&', "&8ᴄʜᴏᴏѕᴇ 1 ɪᴛᴇᴍ"));
            if (((FileConfiguration)var4).contains("contents")) {
               ConfigurationSection var7 = ((FileConfiguration)var4).getConfigurationSection("contents");

               for(String var9 : var7.getKeys(false)) {
                  try {
                     int var10 = Integer.parseInt(var9);
                     ItemStack var11 = ((FileConfiguration)var4).getItemStack("contents." + var9);
                     var6.setItem(var10, var11);
                  } catch (NumberFormatException var12) {
                  }
               }
            }

            var1.openInventory(var6);
         }
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      Player var3 = (Player)var1.getWhoClicked();
      if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄʜᴏᴏѕᴇ 1 ɪᴛᴇᴍ"))) {
         var1.setCancelled(true);
         if (var1.getClickedInventory() == var1.getView().getTopInventory()) {
            ItemStack var10 = var1.getCurrentItem();
            if (var10 != null && var10.getType() != Material.AIR) {
               int var13 = var1.getSlot();
               if (var13 >= 10 && var13 <= 16) {
                  if (var3.hasMetadata("prism_active_crate")) {
                     String var15 = ((MetadataValue)var3.getMetadata("prism_active_crate").get(0)).asString();
                     if (!this.B(var3, var15)) {
                        var3.playSound(var3.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        return;
                     }
                  }

                  this.A(var3, var10);
               }

            }
         }
      } else if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄʟɪᴄᴋ ѕᴛᴀʀᴛ ᴛᴏ ѕᴘɪɴ"))) {
         var1.setCancelled(true);
         if (var1.getSlot() == 22 && var3.hasMetadata("prism_active_crate")) {
            String var9 = ((MetadataValue)var3.getMetadata("prism_active_crate").get(0)).asString();
            this.A.getCarouselManager().handleStartClick(var3, var9, var1.getInventory());
         }

      } else if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄᴏɴꜰɪʀᴍ"))) {
         var1.setCancelled(true);
         if (var1.getClickedInventory() == var1.getView().getTopInventory()) {
            int var8 = var1.getSlot();
            if (var8 == 11) {
               if (var3.hasMetadata("prism_active_crate")) {
                  String var11 = ((MetadataValue)var3.getMetadata("prism_active_crate").get(0)).asString();
                  this.A(var3, var11);
               } else {
                  var3.closeInventory();
               }
            } else if (var8 == 15) {
               if (var3.hasMetadata("prism_active_crate")) {
                  String var12 = ((MetadataValue)var3.getMetadata("prism_active_crate").get(0)).asString();
                  ItemStack var14 = var1.getInventory().getItem(13);
                  if (var14 != null) {
                     this.A(var3, var12, var14);
                  }
               } else {
                  var3.closeInventory();
               }
            }

         }
      } else {
         if (var2.startsWith(ChatColor.translateAlternateColorCodes('&', "&eEditing: ")) && var1.getClickedInventory() != null && var1.getClickedInventory().equals(var1.getView().getTopInventory())) {
            int var4 = var1.getSlot();
            String var5 = ChatColor.stripColor(var2).replace("Editing: ", "");
            File var6 = new File(this.A.getDataFolder(), "crates/crate/" + var5 + "-crate.yml");
            if (var6.exists()) {
               YamlConfiguration var7 = YamlConfiguration.loadConfiguration(var6);
               if (((FileConfiguration)var7).getString("type", "NORMAL").equalsIgnoreCase("CAROUSEL") && (var4 == 4 || var4 == 22)) {
                  var1.setCancelled(true);
                  var3.sendMessage(String.valueOf(ChatColor.RED) + "You cannot modify this slot.");
                  var3.playSound(var3.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               }
            }
         }

      }
   }

   private boolean B(Player var1, String var2) {
      File var3 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var3.exists()) {
         return false;
      } else {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         String var5 = ((FileConfiguration)var4).getString("key");
         PlayerData var6 = this.A.getPlayerDataManager().get(var1.getUniqueId());
         return var6 != null && var6.getKeyCount(var5) > 0;
      }
   }

   private void A(Player var1, ItemStack var2) {
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 27, ChatColor.translateAlternateColorCodes('&', "&8ᴄᴏɴꜰɪʀᴍ"));
      ItemStack var4 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4ᴄᴀɴᴄᴇʟ"));
      var4.setItemMeta(var5);
      var3.setItem(11, var4);
      var3.setItem(13, var2.clone());
      ItemStack var6 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var7 = var6.getItemMeta();
      var7.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aᴄᴏɴꜰɪʀᴍ"));
      var6.setItemMeta(var7);
      var3.setItem(15, var6);
      var1.openInventory(var3);
   }

   private void A(Player var1, String var2, ItemStack var3) {
      File var4 = new File(this.A.getDataFolder(), "crates/crate/" + var2 + "-crate.yml");
      if (!var4.exists()) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Error: Crate config missing.");
         var1.closeInventory();
      } else {
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
         String var6 = ((FileConfiguration)var5).getString("key");
         PlayerData var7 = this.A.getPlayerDataManager().get(var1.getUniqueId());
         if (var7 != null) {
            int var8 = var7.getKeyCount(var6);
            if (var8 > 0) {
               if (var1.getInventory().firstEmpty() == -1) {
                  var1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour inventory is full."));
                  var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', "&cYour inventory is full.")));
                  var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  var1.closeInventory();
                  return;
               }

               var7.removeKey(var6);
               var1.getInventory().addItem(new ItemStack[]{var3.clone()});
               var1.closeInventory();
            } else {
               var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               var1.closeInventory();
            }

         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.startsWith(ChatColor.translateAlternateColorCodes('&', "&eEditing: "))) {
         String var3 = ChatColor.stripColor(var2).replace("Editing: ", "");
         this.A(var3, var1.getInventory());
         HumanEntity var10000 = var1.getPlayer();
         String var10001 = String.valueOf(ChatColor.GREEN);
         var10000.sendMessage(var10001 + "Crate contents saved for " + var3 + "!");
      } else if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄʟɪᴄᴋ ѕᴛᴀʀᴛ ᴛᴏ ѕᴘɪɴ"))) {
         this.A.getCarouselManager().handleClose((Player)var1.getPlayer());
      }

   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.startsWith(ChatColor.translateAlternateColorCodes('&', "&eEditing: "))) {
         String var3 = ChatColor.stripColor(var2).replace("Editing: ", "");
         File var4 = new File(this.A.getDataFolder(), "crates/crate/" + var3 + "-crate.yml");
         if (var4.exists()) {
            YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var4);
            if (((FileConfiguration)var5).getString("type", "NORMAL").equalsIgnoreCase("CAROUSEL")) {
               for(int var7 : var1.getRawSlots()) {
                  if (var7 == 4 || var7 == 22) {
                     var1.setCancelled(true);
                     ((Player)var1.getWhoClicked()).sendMessage(String.valueOf(ChatColor.RED) + "You cannot modify this slot.");
                     return;
                  }
               }
            }
         }
      } else if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄᴏɴꜰɪʀᴍ"))) {
         var1.setCancelled(true);
      } else if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ᴄʜᴏᴏѕᴇ 1 ɪᴛᴇᴍ"))) {
         var1.setCancelled(true);
      }

   }

   private void A(String var1, Inventory var2) {
      File var3 = new File(this.A.getDataFolder(), "crates/crate/" + var1 + "-crate.yml");
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      ((FileConfiguration)var4).set("contents", (Object)null);
      String var5 = ((FileConfiguration)var4).getString("type", "NORMAL");

      for(int var6 = 0; var6 < var2.getSize(); ++var6) {
         if (!var5.equalsIgnoreCase("CAROUSEL") || var6 != 4 && var6 != 22) {
            ItemStack var7 = var2.getItem(var6);
            if (var7 != null && var7.getType() != Material.AIR) {
               ((FileConfiguration)var4).set("contents." + var6, var7);
            }
         }
      }

      try {
         ((FileConfiguration)var4).save(var3);
      } catch (IOException var8) {
         this.A.getLogger().severe("Could not save crate config for " + var1);
         var8.printStackTrace();
      }

   }
}
