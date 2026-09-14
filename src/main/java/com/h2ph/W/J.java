package com.h2ph.W;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class J implements Listener {
   private final String C = ChatColor.translateAlternateColorCodes('&', "&8ᴅᴜᴇʟ ѕᴇᴛᴛɪɴɢѕ ᴍᴀɴᴀɢᴇᴍᴇɴᴛ");
   private final String B = ChatColor.translateAlternateColorCodes('&', "&8ɢᴇɴᴇʀᴀʟ ѕᴇᴛᴛɪɴɢѕ");
   private final String E = ChatColor.translateAlternateColorCodes('&', "&8ᴍᴀɴᴀɢᴇ ᴘʟᴀʏᴇʀѕ");
   private final Map<UUID, String> A = new HashMap();
   private final Set<UUID> D = new HashSet();

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.equals(this.C)) {
         var1.setCancelled(true);
         if (var1.getRawSlot() >= var1.getView().getTopInventory().getSize()) {
            return;
         }

         if (var1.getRawSlot() == 11) {
            if (var1.getWhoClicked() instanceof Player) {
               Player var3 = (Player)var1.getWhoClicked();
               if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
                  try {
                     var3.playSound(var3.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
                  } catch (Exception var24) {
                  }
               }

               com.h2ph.J.B.A.D var4 = new com.h2ph.J.B.A.D((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class));
               var4.B(var3);
            }
         } else if (var1.getRawSlot() == 13) {
            if (var1.getWhoClicked() instanceof Player) {
               Player var25 = (Player)var1.getWhoClicked();

               try {
                  var25.playSound(var25.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
               } catch (Exception var23) {
               }

               com.h2ph.J.B.A.D var31 = new com.h2ph.J.B.A.D((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class));
               var31.C(var25);
            }
         } else if (var1.getRawSlot() == 15 && var1.getWhoClicked() instanceof Player) {
            Player var26 = (Player)var1.getWhoClicked();

            try {
               var26.playSound(var26.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
            } catch (Exception var22) {
            }

            PrismSurvival var32 = (PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class);
            com.h2ph.J.B.A.D var5 = new com.h2ph.J.B.A.D(var32);
            com.h2ph.J.B.A.B var6 = var32.getDuelArenaManager();
            var5.A(var26, var6.A(), var6);
         }
      } else if (var2.equals(this.B)) {
         var1.setCancelled(true);
         if (var1.getRawSlot() >= var1.getView().getTopInventory().getSize()) {
            return;
         }

         if (!(var1.getWhoClicked() instanceof Player)) {
            return;
         }

         Player var27 = (Player)var1.getWhoClicked();
         PrismSurvival var33 = (PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class);
         com.h2ph.J.B.A.D var37 = new com.h2ph.J.B.A.D(var33);
         int var41 = var1.getRawSlot();
         if (var41 == 22) {
            this.D.add(var27.getUniqueId());
            com.h2ph.J.B.A.D var45 = new com.h2ph.J.B.A.D(var33);
            var45.A(var27);
            return;
         }

         if (var41 != 11 && var41 != 15) {
            return;
         }

         try {
            var27.playSound(var27.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
         } catch (Exception var21) {
         }

         File var7 = new File(var33.getDataFolder(), "survival/duels/config.yml");
         YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var7);
         if (var41 == 11) {
            int var9 = var8.getInt("pending-timeout", 60);
            if (var1.isLeftClick()) {
               var9 = Math.min(300, var9 + 5);
            } else if (var1.isRightClick()) {
               var9 = Math.max(5, var9 - 5);
            }

            var8.set("pending-timeout", var9);
         } else {
            int var50 = var8.getInt("request-cooldown", 10);
            if (var1.isLeftClick()) {
               var50 = Math.min(60, var50 + 1);
            } else if (var1.isRightClick()) {
               var50 = Math.max(0, var50 - 1);
            }

            var8.set("request-cooldown", var50);
         }

         try {
            var8.save(var7);
            var27.playSound(var27.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            if (var33.getDuelCommand() != null) {
               var33.getDuelCommand().getRequestManager().A();
            }
         } catch (Exception var20) {
            String var10001 = String.valueOf(ChatColor.RED);
            var27.sendMessage(var10001 + "Failed to save settings: " + var20.getMessage());
         }

         this.D.add(var27.getUniqueId());
         var37.C(var27);
      } else if (var2.equals(this.E)) {
         var1.setCancelled(true);
         if (var1.getRawSlot() >= var1.getView().getTopInventory().getSize()) {
            return;
         }

         if (var1.getCurrentItem() == null || !(var1.getWhoClicked() instanceof Player)) {
            return;
         }

         Player var28 = (Player)var1.getWhoClicked();
         PrismSurvival var34 = (PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class);
         com.h2ph.J.B.A.D var38 = new com.h2ph.J.B.A.D(var34);
         com.h2ph.J.B.A.B var42 = var34.getDuelArenaManager();
         int var46 = var1.getRawSlot();
         if (var46 == 49) {
            this.D.add(var28.getUniqueId());
            var38.A(var28);
            return;
         }

         if (!var1.getCurrentItem().hasItemMeta()) {
            return;
         }

         PersistentDataContainer var48 = var1.getCurrentItem().getItemMeta().getPersistentDataContainer();
         if (!var48.has(var38.B(), PersistentDataType.STRING)) {
            return;
         }

         String var51 = (String)var48.get(var38.B(), PersistentDataType.STRING);
         Player var10 = Bukkit.getPlayer(UUID.fromString(var51));
         if (var10 != null && var42.I(var10)) {
            var42.K(var10);
            String var55 = String.valueOf(ChatColor.GREEN);
            var28.sendMessage(var55 + "Forced " + var10.getName() + " to forfeit their duel.");

            try {
               var28.playSound(var28.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            } catch (Exception var19) {
            }
         } else {
            var28.sendMessage(String.valueOf(ChatColor.RED) + "That player is no longer in a duel.");
         }

         this.D.add(var28.getUniqueId());
         var38.A(var28, var42.A(), var42);
      } else if (var2.equals(ChatColor.translateAlternateColorCodes('&', "&8ʀᴇɢɪᴏɴѕ"))) {
         var1.setCancelled(true);
         if (var1.getRawSlot() >= var1.getView().getTopInventory().getSize()) {
            return;
         }

         if (var1.getCurrentItem() != null && var1.getWhoClicked() instanceof Player) {
            Player var29 = (Player)var1.getWhoClicked();
            if (var1.getCurrentItem().getType() != Material.AIR) {
               try {
                  var29.playSound(var29.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
               } catch (Exception var18) {
               }
            }

            com.h2ph.J.B.A.D var35 = new com.h2ph.J.B.A.D((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class));
            if (var1.getCurrentItem().hasItemMeta()) {
               PersistentDataContainer var39 = var1.getCurrentItem().getItemMeta().getPersistentDataContainer();
               if (var39.has(var35.A(), PersistentDataType.STRING)) {
                  String var43 = (String)var39.get(var35.A(), PersistentDataType.STRING);
                  var35.A(var29, var43);
               }
            }
         }
      } else if (var2.contains(ChatColor.translateAlternateColorCodes('&', "ѕᴇᴛᴛɪɴɢѕ")) && !var2.equals(this.C)) {
         var1.setCancelled(true);
         if (var1.getRawSlot() >= var1.getView().getTopInventory().getSize()) {
            return;
         }

         if (var1.getCurrentItem() == null || !(var1.getWhoClicked() instanceof Player)) {
            return;
         }

         Player var30 = (Player)var1.getWhoClicked();

         try {
            var30.playSound(var30.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
         } catch (Exception var17) {
         }

         com.h2ph.J.B.A.D var36 = new com.h2ph.J.B.A.D((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class));
         if (!var1.getCurrentItem().hasItemMeta()) {
            return;
         }

         PersistentDataContainer var40 = var1.getCurrentItem().getItemMeta().getPersistentDataContainer();
         if (!var40.has(var36.A(), PersistentDataType.STRING)) {
            return;
         }

         String var44 = (String)var40.get(var36.A(), PersistentDataType.STRING);
         File var47 = new File(((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class)).getDataFolder(), "survival/regions/duels/" + var44 + ".yml");
         YamlConfiguration var49 = YamlConfiguration.loadConfiguration(var47);
         int var52 = var1.getRawSlot();
         boolean var53 = false;
         boolean var11 = false;
         boolean var12 = false;
         if (var1.getCurrentItem().getItemMeta().hasLore()) {
            for(String var14 : var1.getCurrentItem().getItemMeta().getLore()) {
               if (ChatColor.stripColor(var14).contains("Click to unset")) {
                  var12 = true;
                  break;
               }
            }
         }

         if (var52 == 11) {
            if (var12) {
               var49.set("spawn1", (Object)null);
               var30.sendMessage(String.valueOf(ChatColor.GRAY) + "Position 1 unset.");
               var53 = true;
               var11 = true;
            } else {
               this.A.put(var30.getUniqueId(), var44 + ":spawn1");
               this.D.add(var30.getUniqueId());
               var30.closeInventory();
               String var56 = String.valueOf(ChatColor.GRAY);
               var30.sendMessage(var56 + "Go to the location for position 1 to set it and type " + String.valueOf(ChatColor.GREEN) + "confirm" + String.valueOf(ChatColor.GRAY) + " in chat.");
            }
         } else if (var52 == 15) {
            if (var12) {
               var49.set("spawn2", (Object)null);
               var30.sendMessage(String.valueOf(ChatColor.GRAY) + "Position 2 unset.");
               var53 = true;
               var11 = true;
            } else {
               this.A.put(var30.getUniqueId(), var44 + ":spawn2");
               this.D.add(var30.getUniqueId());
               var30.closeInventory();
               String var57 = String.valueOf(ChatColor.GRAY);
               var30.sendMessage(var57 + "Go to the location for position 2 to set it and type " + String.valueOf(ChatColor.GREEN) + "confirm" + String.valueOf(ChatColor.GRAY) + " in chat.");
            }
         } else if (var52 == 13) {
            int var54 = var49.getInt("looting-minutes", 5);
            if (var1.isLeftClick()) {
               if (var54 < 10) {
                  ++var54;
               }
            } else if (var1.isRightClick() && var54 > 3) {
               --var54;
            }

            var49.set("looting-minutes", var54);
            var53 = true;
            var11 = true;
         } else if (var52 == 26) {
            this.D.add(var30.getUniqueId());
            var30.closeInventory();
            if (var47.exists()) {
               var47.delete();
               String var59 = String.valueOf(ChatColor.RED);
               var30.sendMessage(var59 + "Region " + var44 + " deleted.");

               try {
                  var30.playSound(var30.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
               } catch (Exception var15) {
               }
            } else {
               var30.sendMessage(String.valueOf(ChatColor.RED) + "Region file not found.");
            }

            ((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class)).getSchedulerAdapter().runEntityTaskLater(var30, () -> var36.B(var30), 1L);
            return;
         }

         if (var53) {
            try {
               var49.save(var47);
               var30.playSound(var30.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            } catch (Exception var16) {
               String var58 = String.valueOf(ChatColor.RED);
               var30.sendMessage(var58 + "Failed to save settings: " + var16.getMessage());
            }
         }

         if (var11) {
            this.D.add(var30.getUniqueId());
            var36.A(var30, var44);
         }
      }

   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.contains(ChatColor.translateAlternateColorCodes('&', "ѕᴇᴛᴛɪɴɢѕ")) && !var2.equals(this.C) && !var2.equals(this.B) && var1.getPlayer() instanceof Player) {
         Player var3 = (Player)var1.getPlayer();
         if (!this.D.contains(var3.getUniqueId())) {
            ((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class)).getSchedulerAdapter().runEntityTaskLater(var3, () -> {
               com.h2ph.J.B.A.D var1 = new com.h2ph.J.B.A.D((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class));
               var1.B(var3);
            }, 1L);
         }

         this.D.remove(var3.getUniqueId());
      }

   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent var1) {
      if (this.A.containsKey(var1.getPlayer().getUniqueId())) {
         var1.setCancelled(true);
         Player var2 = var1.getPlayer();
         String var3 = var1.getMessage();
         if (var3.equalsIgnoreCase("confirm")) {
            String var4 = (String)this.A.remove(var2.getUniqueId());
            String[] var5 = var4.split(":");
            String var6 = var5[0];
            String var7 = var5[1];
            ((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class)).getSchedulerAdapter().runEntityTask(var2, () -> {
               try {
                  File var3 = new File(((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class)).getDataFolder(), "survival/regions/duels/" + var6 + ".yml");
                  YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
                  Location var5 = var2.getLocation();
                  var4.set(var7 + ".world", var5.getWorld().getName());
                  var4.set(var7 + ".x", var5.getBlockX());
                  var4.set(var7 + ".y", var5.getBlockY());
                  var4.set(var7 + ".z", var5.getBlockZ());
                  var4.set(var7 + ".yaw", var5.getYaw());
                  var4.set(var7 + ".pitch", var5.getPitch());
                  var4.save(var3);
                  var2.sendMessage(String.valueOf(ChatColor.GREEN) + "Position set successfully.");
                  var2.playSound(var2.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                  com.h2ph.J.B.A.D var6x = new com.h2ph.J.B.A.D((PrismSurvival)PrismSurvival.getPlugin(PrismSurvival.class));
                  var6x.A(var2, var6);
               } catch (Exception var7x) {
                  var2.sendMessage(String.valueOf(ChatColor.RED) + "Error saving position.");
                  var7x.printStackTrace();
               }

            });
         } else if (var3.equalsIgnoreCase("cancel")) {
            this.A.remove(var2.getUniqueId());
            var2.sendMessage(String.valueOf(ChatColor.RED) + "Setup cancelled.");
         } else {
            var2.sendMessage(String.valueOf(ChatColor.RED) + "Please type 'confirm' to set the location, or 'cancel' to abort.");
         }
      }

   }
}
