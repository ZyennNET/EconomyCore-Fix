package com.h2ph.I;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class A implements Listener, CommandExecutor {
   private final PrismSurvival K;
   private String A;
   private int O;
   private int Q;
   private String B;
   private int E;
   private Material L;
   private String J;
   private String G;
   private int D;
   private Material P;
   private String I;
   private String F;
   private final Map<UUID, UUID> N = new HashMap();
   private final Map<UUID, String> C = new HashMap();
   private final Map<UUID, UUID> H = new HashMap();
   private final Map<UUID, String> M = new HashMap();

   public A(PrismSurvival var1) {
      this.K = var1;
      this.A();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("echest").setExecutor(this);
      var1.getCommand("endersee").setExecutor(this);
   }

   private void A() {
      File var1 = new File(this.K.getDataFolder(), "enderchest/config.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.K.saveResource("enderchest/config.yml", false);
      }

      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      this.A = ((FileConfiguration)var2).getString("chest-title", "&6{player}'s Ender Chest");
      this.O = ((FileConfiguration)var2).getInt("sizes.normal", 27);
      this.Q = ((FileConfiguration)var2).getInt("sizes.vip", 54);
      this.B = this.B(((FileConfiguration)var2).getString("wipe-gui.title", "&4Confirm"));
      this.E = ((FileConfiguration)var2).getInt("wipe-gui.cancel.slot", 12);
      this.L = this.A(((FileConfiguration)var2).getString("wipe-gui.cancel.material", "RED_STAINED_GLASS_PANE"), Material.RED_STAINED_GLASS_PANE);
      this.J = this.B(((FileConfiguration)var2).getString("wipe-gui.cancel.name", "&cCancel"));
      this.G = ((FileConfiguration)var2).getString("wipe-gui.cancel.lore", "Click to cancel");
      this.D = ((FileConfiguration)var2).getInt("wipe-gui.confirm.slot", 16);
      this.P = this.A(((FileConfiguration)var2).getString("wipe-gui.confirm.material", "LIME_STAINED_GLASS_PANE"), Material.LIME_STAINED_GLASS_PANE);
      this.I = this.B(((FileConfiguration)var2).getString("wipe-gui.confirm.name", "&aConfirm"));
      this.F = ((FileConfiguration)var2).getString("wipe-gui.confirm.lore", "Click to Confirm");
   }

   private Material A(String var1, Material var2) {
      try {
         return Material.valueOf(var1.toUpperCase());
      } catch (IllegalArgumentException var4) {
         this.K.getLogger().warning("[EnderChestManager] Unknown material '" + var1 + "' in config — using default.");
         return var2;
      }
   }

   private int A(Player var1) {
      return var1.hasPermission("economysmpcore.enderchest.vip") ? this.Q : this.O;
   }

   private int A(OfflinePlayer var1) {
      return this.O;
   }

   private File A(UUID var1) {
      File var2 = new File(this.K.getDataFolder(), "enderchest/data");
      if (!var2.exists()) {
         var2.mkdirs();
      }

      return new File(var2, var1.toString() + ".yml");
   }

   private void A(Player var1, ItemStack[] var2) {
      File var3 = this.A(var1.getUniqueId());
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);

      for(int var5 = 0; var5 < var2.length; ++var5) {
         ((FileConfiguration)var4).set("slot" + var5, var2[var5]);
      }

      try {
         ((FileConfiguration)var4).save(var3);
      } catch (IOException var6) {
         this.K.getLogger().warning("Failed to save ender chest for " + var1.getName());
      }

   }

   private ItemStack[] A(UUID var1, int var2) {
      File var3 = this.A(var1);
      ItemStack[] var4 = new ItemStack[var2];
      if (var3.exists()) {
         YamlConfiguration var5 = YamlConfiguration.loadConfiguration(var3);

         for(int var6 = 0; var6 < var2; ++var6) {
            var4[var6] = ((FileConfiguration)var5).getItemStack("slot" + var6);
         }
      }

      return var4;
   }

   private void A(UUID var1, String var2, int var3) {
      File var4 = this.A(var1);
      YamlConfiguration var5 = new YamlConfiguration();

      for(int var6 = 0; var6 < var3; ++var6) {
         ((FileConfiguration)var5).set("slot" + var6, (Object)null);
      }

      try {
         ((FileConfiguration)var5).save(var4);
      } catch (IOException var7) {
         this.K.getLogger().warning("Failed to wipe ender chest for " + var2);
      }

   }

   private void A(Player var1, UUID var2, String var3) {
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, 27, this.B);
      ItemStack var5 = new ItemStack(this.L);
      ItemMeta var6 = var5.getItemMeta();
      var6.setDisplayName(this.J);
      String[] var10001 = new String[1];
      String var10004 = String.valueOf(ChatColor.GRAY);
      var10001[0] = var10004 + this.G;
      var6.setLore(Arrays.asList(var10001));
      var5.setItemMeta(var6);
      var4.setItem(this.E, var5);
      ItemStack var7 = new ItemStack(this.P);
      ItemMeta var8 = var7.getItemMeta();
      var8.setDisplayName(this.I);
      var10001 = new String[1];
      var10004 = String.valueOf(ChatColor.GRAY);
      var10001[0] = var10004 + this.F;
      var8.setLore(Arrays.asList(var10001));
      var7.setItemMeta(var8);
      var4.setItem(this.D, var7);
      this.H.put(var1.getUniqueId(), var2);
      this.M.put(var1.getUniqueId(), var3);
      var1.openInventory(var4);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      HumanEntity var3 = var1.getWhoClicked();
      if (var3 instanceof Player var2) {
         if (var1.getView().getTitle().equals(this.B)) {
            var1.setCancelled(true);
            UUID var8 = (UUID)this.H.get(var2.getUniqueId());
            String var4 = (String)this.M.get(var2.getUniqueId());
            if (var8 != null && var4 != null) {
               int var5 = var1.getRawSlot();
               if (var5 == this.D) {
                  OfflinePlayer var6 = Bukkit.getOfflinePlayer(var8);
                  int var7 = var6.isOnline() && var6.getPlayer() != null ? this.A(var6.getPlayer()) : this.A(var6);
                  this.A(var8, var4, var7);
                  this.H.remove(var2.getUniqueId());
                  this.M.remove(var2.getUniqueId());
                  var2.closeInventory();
                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var2.sendMessage(var10001 + "Successfully wiped " + var4 + "'s ender chest.");
               } else if (var5 == this.E) {
                  this.H.remove(var2.getUniqueId());
                  this.M.remove(var2.getUniqueId());
                  var2.closeInventory();
                  var2.sendMessage(String.valueOf(ChatColor.YELLOW) + "Wipe cancelled.");
               }

            }
         }
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (var1.getPlayer() instanceof Player) {
         Player var2 = (Player)var1.getPlayer();
         Inventory var3 = var1.getInventory();
         if (var1.getView().getTitle().equals(this.B)) {
            this.H.remove(var2.getUniqueId());
            this.M.remove(var2.getUniqueId());
         } else {
            UUID var4 = (UUID)this.N.get(var2.getUniqueId());
            String var5 = (String)this.C.get(var2.getUniqueId());
            if (var4 != null && var5 != null) {
               String var6 = this.A.replace("{player}", var5);
               if (var1.getView().getTitle().equals(var6)) {
                  File var7 = this.A(var4);
                  YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var7);
                  ItemStack[] var9 = var3.getContents();

                  for(int var10 = 0; var10 < var9.length; ++var10) {
                     ((FileConfiguration)var8).set("slot" + var10, var9[var10]);
                  }

                  try {
                     ((FileConfiguration)var8).save(var7);
                  } catch (IOException var11) {
                     this.K.getLogger().warning("Failed to save ender chest for " + var5);
                  }

                  this.N.remove(var2.getUniqueId());
                  this.C.remove(var2.getUniqueId());
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryOpen(InventoryOpenEvent var1) {
      if (var1.getPlayer() instanceof Player) {
         Player var2 = (Player)var1.getPlayer();
         if (var1.getInventory().getType().name().equals("ENDER_CHEST")) {
            if (!var2.hasPermission("economysmpcore.enderchest.use")) {
               var2.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
               var1.setCancelled(true);
            } else {
               var1.setCancelled(true);
               this.A(var2, var2.getUniqueId(), var2.getName(), this.A(var2));
            }
         }
      }
   }

   private void A(Player var1, UUID var2, String var3, int var4) {
      String var5 = this.A.replace("{player}", var3);
      Inventory var6 = Bukkit.createInventory(var1, var4, var5);
      var6.setContents(this.A(var2, var4));
      this.N.put(var1.getUniqueId(), var2);
      this.C.put(var1.getUniqueId(), var3);
      var1.openInventory(var6);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (var2.getName().equalsIgnoreCase("endersee")) {
         if (!var5.hasPermission("economysmpcore.enderchest.see")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to view other players' ender chests.");
            return true;
         } else if (var4.length == 0) {
            String var12 = String.valueOf(ChatColor.RED);
            var5.sendMessage(var12 + "Usage: /" + var3 + " <player>");
            return true;
         } else {
            OfflinePlayer var9 = this.A(var4[0]);
            if (var9 != null && var9.hasPlayedBefore()) {
               if (var9.getUniqueId().equals(var5.getUniqueId())) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "Use /echest to view your own ender chest.");
                  return true;
               } else {
                  int var10 = var9.isOnline() && var9.getPlayer() != null ? this.A(var9.getPlayer()) : this.A(var9);
                  this.A(var5, var9.getUniqueId(), var9.getName(), var10);
                  String var11 = String.valueOf(ChatColor.GREEN);
                  var5.sendMessage(var11 + "Viewing " + var9.getName() + "'s ender chest.");
                  return true;
               }
            } else {
               var5.sendMessage(String.valueOf(ChatColor.RED) + "Player never played before.");
               return true;
            }
         }
      } else if (!var2.getName().equalsIgnoreCase("echest")) {
         return false;
      } else if (var4.length == 2 && var4[0].equalsIgnoreCase("wipe")) {
         if (!var5.hasPermission("economysmpcore.enderchest.wipe")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to wipe ender chests.");
            return true;
         } else {
            OfflinePlayer var8 = this.A(var4[1]);
            if (var8 != null && var8.hasPlayedBefore()) {
               this.A(var5, var8.getUniqueId(), var8.getName());
               return true;
            } else {
               var5.sendMessage(String.valueOf(ChatColor.RED) + "Player never played before.");
               return true;
            }
         }
      } else if (var4.length == 0) {
         if (!var5.hasPermission("economysmpcore.enderchest.use")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
            return true;
         } else {
            this.A(var5, var5.getUniqueId(), var5.getName(), this.A(var5));
            return true;
         }
      } else if (var4.length == 1 && var5.hasPermission("economysmpcore.enderchest.admin")) {
         OfflinePlayer var6 = this.A(var4[0]);
         if (var6 != null && var6.hasPlayedBefore()) {
            int var7 = var6.isOnline() && var6.getPlayer() != null ? this.A(var6.getPlayer()) : this.A(var6);
            this.A(var5, var6.getUniqueId(), var6.getName(), var7);
            String var10001 = String.valueOf(ChatColor.GREEN);
            var5.sendMessage(var10001 + "Viewing " + var6.getName() + "'s ender chest.");
            return true;
         } else {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "Player never played before.");
            return true;
         }
      } else {
         var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /echest [player] | /echest wipe <player>");
         return true;
      }
   }

   private OfflinePlayer A(String var1) {
      Player var2 = Bukkit.getPlayer(var1);
      return (OfflinePlayer)(var2 != null ? var2 : Bukkit.getOfflinePlayer(var1));
   }

   private String B(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         Matcher var2 = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(var1);
         StringBuffer var3 = new StringBuffer();

         while(var2.find()) {
            var2.appendReplacement(var3, net.md_5.bungee.api.ChatColor.of("#" + var2.group(1)).toString());
         }

         return ChatColor.translateAlternateColorCodes('&', var2.appendTail(var3).toString());
      } else {
         return "";
      }
   }
}
