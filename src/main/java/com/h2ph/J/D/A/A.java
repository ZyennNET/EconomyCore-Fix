package com.h2ph.J.D.A;

import com.h2ph.PrismSurvival;
import com.h2ph.C.B;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class A implements CommandExecutor, TabCompleter, Listener {
   private final PrismSurvival C;
   private final com.h2ph.C.A A;
   private final String D = ChatColor.translateAlternateColorCodes('&', "&7ᴀꜰᴋ ᴀʀᴇᴀѕ");
   private final Map<UUID, BukkitTask> B = new HashMap();

   public A(PrismSurvival var1) {
      this.C = var1;
      this.A = var1.getAfkManager();
   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else {
         if (var4.length > 0) {
            if (var4[0].equalsIgnoreCase("setspawn")) {
               if (!var5.hasPermission("economysmpcore.admin.afk")) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
                  return true;
               }

               B var15 = this.A.A(var5.getLocation());
               if (var15 == null) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "You must be inside an AFK region to set its spawn point.");
                  return true;
               }

               this.A.A(var15.D(), var5.getLocation());
               String var17 = String.valueOf(ChatColor.GREEN);
               var5.sendMessage(var17 + "Spawn point set for AFK region: " + String.valueOf(ChatColor.YELLOW) + var15.D());
               return true;
            }

            if (var4[0].equalsIgnoreCase("remove")) {
               if (!var5.hasPermission("economysmpcore.admin.afk")) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "You do not have permission to use this command.");
                  return true;
               }

               if (var4.length < 2) {
                  var5.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /afk remove <region_name>");
                  return true;
               }

               if (this.A.D(var4[1])) {
                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var5.sendMessage(var10001 + "Removed AFK region: " + String.valueOf(ChatColor.YELLOW) + var4[1]);
               } else {
                  String var16 = String.valueOf(ChatColor.RED);
                  var5.sendMessage(var16 + "Region not found: " + var4[1]);
               }

               return true;
            }

            try {
               int var6 = Integer.parseInt(var4[0]);
               int var7 = var6 - 1;
               ArrayList var8 = new ArrayList(this.A.D());
               var8.sort(Comparator.comparing(B::D));
               if (var7 >= 0 && var7 < var8.size()) {
                  B var9 = (B)var8.get(var7);
                  if (var9.A() == null) {
                     var5.sendMessage(String.valueOf(ChatColor.RED) + "This region has no spawn point set.");
                     return true;
                  }

                  World var10 = Bukkit.getWorld(var9.E());
                  int var11 = var10 != null ? var10.getPlayers().size() : 0;
                  int var12 = Bukkit.getMaxPlayers();
                  boolean var13 = var11 >= var12 && var12 > 0;
                  if (var13) {
                     var5.sendMessage(String.valueOf(ChatColor.RED) + "This area is full.");
                     var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     return true;
                  }

                  this.A(var5, var9.D());
                  return true;
               }
            } catch (NumberFormatException var14) {
            }
         }

         this.B(var5);
         return true;
      }
   }

   private void B(Player var1) {
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 54, this.D);
      ArrayList var3 = new ArrayList(this.A.D());
      var3.sort(Comparator.comparing(B::D));
      int var4 = 0;

      for(B var6 : var3) {
         if (var4 >= 45) {
            break;
         }

         String var7 = var6.E();
         World var8 = Bukkit.getWorld(var7);
         int var9 = 0;
         int var10 = Bukkit.getMaxPlayers();
         if (var8 != null) {
            var9 = var8.getPlayers().size();
         }

         boolean var11 = var9 >= var10 && var10 > 0;
         boolean var12 = var6.A() != null;
         ItemStack var13;
         if (!var11 && var12) {
            var13 = new ItemStack(Material.ITEM_FRAME);
         } else {
            var13 = new ItemStack(Material.REDSTONE_BLOCK);
         }

         ItemMeta var14 = var13.getItemMeta();
         if (var14 != null) {
            var14.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5" + var6.D().toUpperCase()));
            ArrayList var15 = new ArrayList();
            var15.add(ChatColor.translateAlternateColorCodes('&', "&7World: &f" + var7));
            var15.add(ChatColor.translateAlternateColorCodes('&', "&8" + var9 + "/" + var10));
            if (!var12) {
               var15.add(ChatColor.translateAlternateColorCodes('&', "&cNo spawn point set."));
            } else if (var11) {
               var15.add(ChatColor.translateAlternateColorCodes('&', "&cThis area is full."));
            } else {
               var15.add(ChatColor.translateAlternateColorCodes('&', "&7Click to warp."));
            }

            var14.setLore(var15);
            var14.getPersistentDataContainer().set(new NamespacedKey(this.C, "afk_region"), PersistentDataType.STRING, var6.D());
            var13.setItemMeta(var14);
         }

         var2.setItem(var4, var13);
         ++var4;
      }

      ItemStack var16 = new ItemStack(Material.AMETHYST_BLOCK);
      ItemMeta var17 = var16.getItemMeta();
      if (var17 != null) {
         var17.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aRandom AFK"));
         ArrayList var18 = new ArrayList();
         var18.add(ChatColor.translateAlternateColorCodes('&', "&fClick to teleport to a random area"));
         var17.setLore(var18);
         var17.getPersistentDataContainer().set(new NamespacedKey(this.C, "afk_random"), PersistentDataType.BYTE, (byte)1);
         var16.setItemMeta(var17);
      }

      var2.setItem(49, var16);
      var1.openInventory(var2);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getView().getTitle().equals(this.D)) {
         var1.setCancelled(true);
         if (var1.getClickedInventory() == var1.getView().getTopInventory()) {
            Player var2 = (Player)var1.getWhoClicked();
            ItemStack var3 = var1.getCurrentItem();
            if (var3 == null || var3.getType() == Material.AIR) {
               return;
            }

            var2.playSound(var2.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_OFF, 1.0F, 1.0F);
            ItemMeta var4 = var3.getItemMeta();
            if (var4 == null) {
               return;
            }

            if (var4.getPersistentDataContainer().has(new NamespacedKey(this.C, "afk_random"), PersistentDataType.BYTE)) {
               var2.closeInventory();
               this.A(var2);
               return;
            }

            String var5 = (String)var4.getPersistentDataContainer().get(new NamespacedKey(this.C, "afk_region"), PersistentDataType.STRING);
            if (var5 == null) {
               return;
            }

            if (var3.getType() == Material.REDSTONE_BLOCK) {
               var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               return;
            }

            var2.closeInventory();
            this.A(var2, var5);
         }
      }

   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent var1) {
      if (var1.getFrom().getBlockX() != var1.getTo().getBlockX() || var1.getFrom().getBlockY() != var1.getTo().getBlockY() || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ()) {
         Player var2 = var1.getPlayer();
         if (this.B.containsKey(var2.getUniqueId())) {
            this.B(var2, "&cTeleport cancelled because you moved.");
         }

      }
   }

   private void B(Player var1, String var2) {
      BukkitTask var3 = (BukkitTask)this.B.remove(var1.getUniqueId());
      if (var3 != null && !var3.isCancelled()) {
         var3.cancel();
      }

      String var4 = ChatColor.translateAlternateColorCodes('&', var2);
      var1.sendMessage(var4);
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var4));
      var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
   }

   private void A(Player var1) {
      if (this.B.containsKey(var1.getUniqueId())) {
         this.B(var1, "&cPrevious teleport cancelled.");
      }

      ArrayList var2 = new ArrayList(this.A.D());
      List var3 = (List)var2.stream().filter((var0) -> var0.A() != null).collect(Collectors.toList());
      if (!var3.isEmpty()) {
         Random var4 = new Random();
         B var5 = (B)var3.get(var4.nextInt(var3.size()));
         this.A(var1, var5.D());
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "No valid AFK areas available.");
      }

   }

   private void A(final Player var1, final String var2) {
      if (this.B.containsKey(var1.getUniqueId())) {
         BukkitTask var3 = (BukkitTask)this.B.remove(var1.getUniqueId());
         if (var3 != null && !var3.isCancelled()) {
            var3.cancel();
         }
      }

      BukkitTask var4 = (new BukkitRunnable() {
         int countdown = 5;

         public void run() {
            if (var1.isOnline() && A.this.B.containsKey(var1.getUniqueId())) {
               if (this.countdown > 0) {
                  String var3 = ChatColor.translateAlternateColorCodes('&', "&7Teleporting in &5" + this.countdown + "s");
                  var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var3));
                  var1.playSound(var1.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0F, 1.0F);
                  --this.countdown;
               } else {
                  this.cancel();
                  A.this.B.remove(var1.getUniqueId());
                  B var1x = A.this.A.B(var2);
                  if (var1x != null) {
                     Location var2x = var1x.A();
                     if (var2x != null && var2x.getWorld() != null) {
                        var1.teleportAsync(var2x);
                        var1.playSound(var1.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
                        Player var10000 = var1;
                        String var10001 = String.valueOf(ChatColor.GREEN);
                        var10000.sendMessage(var10001 + "Teleported to " + var2);
                     } else {
                        var1.sendMessage(String.valueOf(ChatColor.RED) + "Teleport failed: World not loaded.");
                     }
                  } else {
                     var1.sendMessage(String.valueOf(ChatColor.RED) + "Region no longer exists.");
                  }

               }
            } else {
               this.cancel();
               A.this.B.remove(var1.getUniqueId());
            }
         }
      }).runTaskTimer(this.C, 0L, 20L);
      this.B.put(var1.getUniqueId(), var4);
   }

   public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length != 1) {
         return var1.hasPermission("economysmpcore.admin.afk") && var4.length == 2 && var4[0].equalsIgnoreCase("remove") ? this.A((List)(new ArrayList(this.A.E())), var4[1]) : Collections.emptyList();
      } else {
         ArrayList var5 = new ArrayList();
         ArrayList var6 = new ArrayList(this.A.D());

         for(int var7 = 1; var7 <= var6.size(); ++var7) {
            var5.add(String.valueOf(var7));
         }

         if (var1.hasPermission("economysmpcore.admin.afk")) {
            var5.add("setspawn");
            var5.add("remove");
         }

         return this.A((List)var5, var4[0]);
      }
   }

   private List<String> A(List<String> var1, String var2) {
      return (List)var1.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var2.toLowerCase())).collect(Collectors.toList());
   }
}
