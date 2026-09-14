package com.h2ph.N;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class A implements Listener, CommandExecutor, TabCompleter {
   private final PrismSurvival H;
   private FileConfiguration Q;
   private final File O;
   private FileConfiguration J;
   private final File G;
   private final Pattern K = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private final Map<String, String> N = new HashMap();
   private final Map<String, Location> P = new HashMap();
   private final Map<String, BukkitTask> E = new HashMap();
   private final Map<String, Boolean> L = new HashMap();
   private final Map<String, Boolean> F = new HashMap();
   private final Map<String, Boolean> D = new HashMap();
   private final Map<UUID, String> C = new HashMap();
   private final Map<UUID, String> A = new HashMap();
   private final Map<UUID, String> M = new HashMap();
   private final Map<UUID, String> R = new HashMap();
   private final Map<UUID, BukkitTask> I = new HashMap();
   private final Set<UUID> B = new HashSet();

   public A(PrismSurvival var1) {
      this.H = var1;
      this.O = new File(var1.getDataFolder(), "teleport.yml");
      this.G = new File(var1.getDataFolder(), "tpadata.yml");
      this.A();
      this.B();
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   private void A() {
      if (!this.O.exists()) {
         this.H.saveResource("teleport.yml", false);
      }

      this.Q = YamlConfiguration.loadConfiguration(this.O);
   }

   private void D() {
      this.A();
   }

   private void B() {
      if (!this.G.exists()) {
         try {
            this.G.createNewFile();
         } catch (IOException var3) {
            this.H.getLogger().warning("Could not create tpadata.yml: " + var3.getMessage());
         }
      }

      this.J = YamlConfiguration.loadConfiguration(this.G);
      if (this.J.contains("tpatoggle")) {
         for(String var2 : this.J.getConfigurationSection("tpatoggle").getKeys(false)) {
            this.L.put(var2, this.J.getBoolean("tpatoggle." + var2, false));
         }
      }

      if (this.J.contains("tpaheretoggle")) {
         for(String var5 : this.J.getConfigurationSection("tpaheretoggle").getKeys(false)) {
            this.F.put(var5, this.J.getBoolean("tpaheretoggle." + var5, false));
         }
      }

   }

   private void C() {
      for(Map.Entry var2 : this.L.entrySet()) {
         this.J.set("tpatoggle." + (String)var2.getKey(), var2.getValue());
      }

      for(Map.Entry var5 : this.F.entrySet()) {
         this.J.set("tpaheretoggle." + (String)var5.getKey(), var5.getValue());
      }

      try {
         this.J.save(this.G);
      } catch (IOException var3) {
         this.H.getLogger().warning("Could not save tpadata.yml: " + var3.getMessage());
      }

   }

   private String E(String var1) {
      if (var1 == null) {
         return "";
      } else {
         Matcher var2 = this.K.matcher(var1);
         StringBuffer var3 = new StringBuffer();

         while(var2.find()) {
            String var4 = var2.group(1);
            char var10002 = var4.charAt(0);
            var2.appendReplacement(var3, "§x§" + var10002 + "§" + var4.charAt(1) + "§" + var4.charAt(2) + "§" + var4.charAt(3) + "§" + var4.charAt(4) + "§" + var4.charAt(5));
         }

         var1 = var2.appendTail(var3).toString();
         return ChatColor.translateAlternateColorCodes('&', var1);
      }
   }

   private void E(Player var1, String var2) {
      String var3 = this.Q.getString("messages." + var2);
      if (var3 != null) {
         var1.sendMessage(this.E(var3));
      }

   }

   private void A(Player var1, String var2, String var3, String var4) {
      String var5 = this.Q.getString("messages." + var2);
      if (var5 != null) {
         var1.sendMessage(this.E(var5.replace(var3, var4)));
      }

   }

   private void A(Player var1, String var2) {
      String var3 = this.Q.getString("sounds." + var2);
      if (var3 != null && !var3.isEmpty()) {
         try {
            Sound var4 = Sound.valueOf(var3.toUpperCase());
            var1.playSound(var1.getLocation(), var4, 1.0F, 1.0F);
         } catch (IllegalArgumentException var5) {
         }
      }

   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         var1.sendMessage("Players only.");
         return true;
      } else {
         Player var5 = (Player)var1;
         switch (var2.getName().toLowerCase()) {
            case "tpa":
               if (var4.length == 0) {
                  this.E(var5, "usage-tpa");
                  return true;
               }

               this.G(var5, var4[0]);
               return true;
            case "tpahere":
               if (var4.length == 0) {
                  this.E(var5, "usage-tpahere");
                  return true;
               }

               this.C(var5, var4[0]);
               return true;
            case "tpaccept":
               if (var4.length == 0) {
                  this.E(var5);
               } else {
                  this.D(var5, var4[0]);
               }

               return true;
            case "tpacancel":
               if (var4.length == 0) {
                  this.E(var5, "request-not-exist");
                  this.A(var5, "error");
                  return true;
               }

               this.F(var5, var4[0]);
               return true;
            case "tpatoggle":
               this.C(var5);
               return true;
            case "tpaheretoggle":
               this.J(var5);
               return true;
            case "tpauto":
               this.H(var5);
               return true;
            default:
               return false;
         }
      }
   }

   private void G(Player var1, String var2) {
      Player var3 = Bukkit.getPlayer(var2);
      if (var3 == null) {
         this.E(var1, "player-not-found");
         this.A(var1, "error");
      } else if (var1.equals(var3)) {
         this.A(var1, "error");
      } else if ((Boolean)this.L.getOrDefault(var3.getUniqueId().toString(), false)) {
         this.E(var1, "user-disabled-tpa");
         this.A(var1, "error");
      } else {
         String var10000 = String.valueOf(var1.getUniqueId());
         String var4 = var10000 + var3.getUniqueId().toString();
         if (this.N.containsKey(var4)) {
            this.E(var1, "on-cooldown");
            this.A(var1, "error");
         } else if ((Boolean)this.D.getOrDefault(var3.getUniqueId().toString(), false)) {
            this.A(var1, var3, "To");
         } else {
            this.B(var1, var3, "tpa");
         }
      }
   }

   private void C(Player var1, String var2) {
      Player var3 = Bukkit.getPlayer(var2);
      if (var3 == null) {
         this.E(var1, "player-not-found");
         this.A(var1, "error");
      } else if (var1.equals(var3)) {
         this.A(var1, "error");
      } else if ((Boolean)this.F.getOrDefault(var3.getUniqueId().toString(), false)) {
         this.E(var1, "user-disabled-tpahere");
         this.A(var1, "error");
      } else {
         String var10000 = String.valueOf(var1.getUniqueId());
         String var4 = var10000 + var3.getUniqueId().toString();
         if (this.N.containsKey(var4)) {
            this.E(var1, "on-cooldown");
            this.A(var1, "error");
         } else if ((Boolean)this.D.getOrDefault(var3.getUniqueId().toString(), false)) {
            this.A(var1, var3, "Here");
         } else {
            this.B(var1, var3, "tpahere");
         }
      }
   }

   private void A(Player var1, Player var2, String var3) {
      String var10000 = String.valueOf(var1.getUniqueId());
      final String var4 = var10000 + var2.getUniqueId().toString();
      this.N.put(var4, var3);
      this.M.put(var2.getUniqueId(), var1.getName());
      this.A(var2, "auto-accepted", "%player%", var1.getName());
      this.A(var1, "auto-accepted-notify", "%player%", var2.getName());
      this.A(var2, "success");
      this.A(var1, "success");
      if (var3.equals("To")) {
         this.A(var1, var2.getLocation());
      } else {
         this.A(var2, var1.getLocation());
      }

      (new BukkitRunnable() {
         public void run() {
            A.this.N.remove(var4);
         }
      }).runTaskLater(this.H, (long)this.Q.getInt("teleport.request-timeout", 60) * 20L);
   }

   private void B(Player var1, Player var2, String var3) {
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, 27, this.E(this.Q.getString("gui.confirm-request", "ᴄᴏɴꜰɪʀᴍ ʀᴇqᴜᴇꜱᴛ")));
      this.C.put(var1.getUniqueId(), var2.getName());
      this.A.put(var1.getUniqueId(), var3);
      var4.setItem(10, this.A(Material.RED_STAINED_GLASS_PANE, "&#FC0000ᴄᴀɴᴄᴇʟ", "&7Click to cancel"));
      var4.setItem(12, this.I(var2));
      var4.setItem(13, this.B(var2));
      var4.setItem(14, this.A(Material.FEATHER, "&#00F986ʀᴇɢɪᴏɴ", "&#A8A8A8Ping: &f" + var2.getPing() + "ms"));
      var4.setItem(16, this.A(Material.LIME_STAINED_GLASS_PANE, "&#00FC00ᴄᴏɴꜰɪʀᴍ", "&7Send request"));
      var1.openInventory(var4);
      this.A(var1, "gui-click");
   }

   private void A(Player var1, Player var2) {
      String var3 = (String)this.R.getOrDefault(var1.getUniqueId(), "tpa");
      String var4 = var3.equals("tpahere") ? "gui.accept-request-tpahere" : "gui.accept-request-tpa";
      String var5 = var3.equals("tpahere") ? "ᴀᴄᴄᴇᴘᴛ ᴛᴘᴀʜᴇʀᴇ" : "ᴀᴄᴄᴇᴘᴛ ᴛᴘᴀ";
      String var6 = this.E(this.Q.getString(var4, var5));
      Inventory var7 = Bukkit.createInventory((InventoryHolder)null, 27, var6);
      this.C.put(var1.getUniqueId(), var2.getName());
      this.A.put(var1.getUniqueId(), "accept");
      String var8 = var3.equals("tpahere") ? "gui.accept-lore-tpahere" : "gui.accept-lore-tpa";
      String var9 = var3.equals("tpahere") ? "&7Accept - &bthey teleport to me" : "&7Accept - &bI teleport to them";
      String var10 = this.Q.getString(var8, var9);
      String var11 = this.Q.getString("gui.confirm-name", "&#00FC00ᴀᴄᴄᴇᴘᴛ");
      String var12 = this.Q.getString("gui.deny-name", "&#FC0000ᴅᴇɴʏ");
      String var13 = this.Q.getString("gui.deny-lore", "&7Click to decline");
      var7.setItem(10, this.A(Material.RED_STAINED_GLASS_PANE, var12, var13));
      var7.setItem(12, this.I(var2));
      var7.setItem(13, this.B(var2));
      var7.setItem(14, this.A(Material.FEATHER, "&#00F986ʀᴇɢɪᴏɴ", "&#A8A8A8Ping: &f" + var2.getPing() + "ms"));
      var7.setItem(16, this.A(Material.LIME_STAINED_GLASS_PANE, var11, var10));
      var1.openInventory(var7);
      this.A(var1, "gui-click");
   }

   private void E(Player var1) {
      String var2 = (String)this.M.get(var1.getUniqueId());
      if (var2 == null) {
         this.E(var1, "request-not-exist");
         this.A(var1, "error");
      } else {
         Player var3 = Bukkit.getPlayer(var2);
         if (var3 == null) {
            this.E(var1, "player-not-found");
            this.A(var1, "error");
         } else {
            String var10000 = String.valueOf(var3.getUniqueId());
            String var4 = var10000 + var1.getUniqueId().toString();
            if (!this.N.containsKey(var4)) {
               this.E(var1, "request-not-exist");
               this.A(var1, "error");
            } else {
               this.A(var1, var3);
            }
         }
      }
   }

   private void D(Player var1, String var2) {
      Player var3 = Bukkit.getPlayer(var2);
      if (var3 == null) {
         this.E(var1, "player-not-found");
         this.A(var1, "error");
      } else {
         String var10000 = String.valueOf(var3.getUniqueId());
         String var4 = var10000 + var1.getUniqueId().toString();
         if (!this.N.containsKey(var4)) {
            this.E(var1, "request-not-exist");
            this.A(var1, "error");
         } else {
            this.A(var1, var3);
         }
      }
   }

   private void F(Player var1, String var2) {
      Player var3 = Bukkit.getPlayer(var2);
      if (var3 == null) {
         this.E(var1, "request-not-exist");
         this.A(var1, "error");
      } else {
         String var10000 = String.valueOf(var3.getUniqueId());
         String var4 = var10000 + var1.getUniqueId().toString();
         if (!this.N.containsKey(var4)) {
            this.E(var1, "request-not-exist");
            this.A(var1, "error");
         } else {
            this.N.remove(var4);
            this.A(var1, "request-denied", "%player%", var3.getName());
            this.A(var3, "request-denied-notify", "%player%", var1.getName());
            this.A(var1, "success");
            this.A(var3, "error");
         }
      }
   }

   private void C(Player var1) {
      String var2 = var1.getUniqueId().toString();
      boolean var3 = (Boolean)this.L.getOrDefault(var2, false);
      this.L.put(var2, !var3);
      this.E(var1, var3 ? "tpa-enabled" : "tpa-disabled");
      this.A(var1, "success");
      this.C();
   }

   private void J(Player var1) {
      String var2 = var1.getUniqueId().toString();
      boolean var3 = (Boolean)this.F.getOrDefault(var2, false);
      this.F.put(var2, !var3);
      this.E(var1, var3 ? "tpahere-enabled" : "tpahere-disabled");
      this.A(var1, "success");
      this.C();
   }

   private void H(Player var1) {
      String var2 = var1.getUniqueId().toString();
      boolean var3 = (Boolean)this.D.getOrDefault(var2, false);
      this.D.put(var2, !var3);
      if (!var3) {
         this.E(var1, "tpauto-enabled");
         this.D(var1);
      } else {
         this.E(var1, "tpauto-disabled");
         this.A(var1);
      }

      this.A(var1, "success");
   }

   private void D(final Player var1) {
      this.A(var1);
      BukkitTask var2 = (new BukkitRunnable() {
         public void run() {
            if (var1.isOnline() && (Boolean)A.this.D.getOrDefault(var1.getUniqueId().toString(), false)) {
               var1.sendActionBar(A.this.E("&a⚡ TP Auto‑accept Enabled"));
            } else {
               this.cancel();
            }
         }
      }).runTaskTimer(this.H, 0L, 20L);
      this.I.put(var1.getUniqueId(), var2);
   }

   private void A(Player var1) {
      BukkitTask var2 = (BukkitTask)this.I.remove(var1.getUniqueId());
      if (var2 != null) {
         var2.cancel();
      }

      var1.sendActionBar("");
   }

   private void C(Player var1, Player var2) {
      this.R.put(var2.getUniqueId(), "tpa");
      this.A(var1, "request-sent-tpa", "%player%", var2.getName());
      this.A(var1, "success");
      this.B(var2, var1.getName(), "tpa");
      this.A(var2, "success");
   }

   private void B(Player var1, Player var2) {
      this.R.put(var2.getUniqueId(), "tpahere");
      this.A(var1, "request-sent-tpahere", "%player%", var2.getName());
      this.A(var1, "success");
      this.B(var2, var1.getName(), "tpahere");
      this.A(var2, "success");
   }

   private void B(Player var1, String var2, String var3) {
      List var4 = this.Q.getStringList("messages.tp-trap-warning");
      if (var4.isEmpty()) {
         String var5 = this.Q.getString("messages.tp-trap-warning", "&#00A4FB%playername% &7sent you a %teleporttype% %tpabutton%");
         var4 = Collections.singletonList(var5);
      }

      for(String var6 : var4) {
         String var7 = this.A(var6, "%playername%", var2);
         var7 = this.A(var7, "%teleporttype%", var3);
         if (var7.contains("%tpabutton%")) {
            this.A(var1, var7, var2);
         } else {
            var1.sendMessage(this.E(var7));
         }
      }

   }

   private String C(String var1) {
      Matcher var2 = this.K.matcher(var1);
      int var3 = -1;

      String var4;
      for(var4 = ""; var2.find(); var4 = var2.group(0)) {
         var3 = var2.end();
      }

      Matcher var5 = Pattern.compile("&[0-9a-fA-FrRkKlLmMnNoO]").matcher(var1);
      int var6 = -1;

      String var7;
      for(var7 = ""; var5.find(); var7 = var5.group(0)) {
         var6 = var5.end();
      }

      if (var3 >= var6) {
         return var4;
      } else {
         return var6 > -1 ? var7 : "";
      }
   }

   private String A(String var1, String var2, String var3) {
      int var4 = var1.indexOf(var2);
      if (var4 == -1) {
         return var1;
      } else {
         String var5 = "";
         String var6 = var1.substring(0, var4);
         Matcher var7 = this.K.matcher(var6);
         int var8 = -1;

         String var9;
         for(var9 = ""; var7.find(); var9 = var7.group(0)) {
            var8 = var7.end();
         }

         Matcher var10 = Pattern.compile("&[0-9a-fA-FrRkKlLmMnNoO]").matcher(var6);
         int var11 = -1;

         String var12;
         for(var12 = ""; var10.find(); var12 = var10.group(0)) {
            var11 = var10.end();
         }

         if (var8 >= var11) {
            var5 = var9;
         } else if (var11 > -1) {
            var5 = var12;
         }

         String var10000 = var1.substring(0, var4);
         return var10000 + var5 + var3 + var1.substring(var4 + var2.length());
      }
   }

   private void A(Player var1, String var2, String var3) {
      String var4 = "/tpaccept " + var3;
      String var5 = this.Q.getString("messages.tpabutton-label", "&#00A4FB[CLICK HERE]");
      String var6 = this.Q.getString("messages.tpabutton-hover", "&7Click to accept");
      if (var2.contains("%tpabutton%")) {
         String[] var7 = var2.split("%tpabutton%", 2);
         String var8 = var7[0];
         String var9 = var7.length > 1 ? var7[1] : "";
         Component var10 = this.D(var8);
         Component var11 = this.D(var5).clickEvent(ClickEvent.runCommand(var4)).hoverEvent(HoverEvent.showText(this.D(var6)));
         Component var12 = this.D(var9);
         var1.sendMessage(var10.append(var11).append(var12));
      } else {
         var1.sendMessage(this.D(var2));
      }

   }

   private Component D(String var1) {
      return var1 != null && !var1.isEmpty() ? LegacyComponentSerializer.legacySection().deserialize(this.A(var1)) : Component.empty();
   }

   private String A(String var1) {
      Matcher var2 = this.K.matcher(var1);
      StringBuffer var3 = new StringBuffer();

      while(var2.find()) {
         String var4 = var2.group(1).toUpperCase();
         StringBuilder var5 = new StringBuilder("§x");

         for(char var9 : var4.toCharArray()) {
            var5.append('§').append(var9);
         }

         var2.appendReplacement(var3, var5.toString());
      }

      var2.appendTail(var3);
      return ChatColor.translateAlternateColorCodes('&', var3.toString());
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getWhoClicked() instanceof Player) {
         Player var2 = (Player)var1.getWhoClicked();
         String var3 = var1.getView().getTitle();
         boolean var4 = var3.equals(this.E(this.Q.getString("gui.confirm-request", "ᴄᴏɴꜰɪʀᴍ ʀᴇqᴜᴇꜱᴛ")));
         boolean var5 = var3.equals(this.E(this.Q.getString("gui.accept-request-tpa", "ᴀᴄᴄᴇᴘᴛ ᴛᴘᴀ")));
         boolean var6 = var3.equals(this.E(this.Q.getString("gui.accept-request-tpahere", "ᴀᴄᴄᴇᴘᴛ ᴛᴘᴀʜᴇʀᴇ")));
         if (var4 || var5 || var6) {
            var1.setCancelled(true);
            if (var1.getSlot() == 10) {
               var2.closeInventory();
               this.A(var2, "gui-click");
               this.C.remove(var2.getUniqueId());
               this.A.remove(var2.getUniqueId());
            } else if (var1.getSlot() == 16) {
               String var7 = (String)this.C.get(var2.getUniqueId());
               String var8 = (String)this.A.get(var2.getUniqueId());
               if (var7 == null || var8 == null) {
                  return;
               }

               var2.closeInventory();
               this.A(var2, "gui-click");
               this.C.remove(var2.getUniqueId());
               this.A.remove(var2.getUniqueId());
               if (var8.equals("accept")) {
                  Player var9 = Bukkit.getPlayer(var7);
                  if (var9 == null) {
                     this.E(var2, "player-not-found");
                     return;
                  }

                  String var10000 = String.valueOf(var9.getUniqueId());
                  String var10 = var10000 + var2.getUniqueId().toString();
                  String var11 = (String)this.N.remove(var10);
                  if (var11 == null) {
                     this.E(var2, "request-not-exist");
                     return;
                  }

                  this.M.remove(var2.getUniqueId());
                  this.A(var9, "request-accepted", "%player%", var2.getName());
                  this.E(var2, "self-accepted");
                  this.A(var9, "success");
                  this.A(var2, "success");
                  if (var11.equals("To")) {
                     this.G(var9);
                     this.A(var9, var2.getLocation());
                  } else {
                     this.A(var2, var9.getLocation());
                  }
               } else {
                  Player var12 = Bukkit.getPlayer(var7);
                  if (var12 == null) {
                     this.E(var2, "player-not-found");
                     return;
                  }

                  String var14 = String.valueOf(var2.getUniqueId());
                  final String var13 = var14 + var12.getUniqueId().toString();
                  if (this.N.containsKey(var13)) {
                     return;
                  }

                  this.N.put(var13, var8.equals("tpa") ? "To" : "Here");
                  this.M.put(var12.getUniqueId(), var2.getName());
                  this.R.put(var12.getUniqueId(), var8);
                  (new BukkitRunnable() {
                     public void run() {
                        A.this.N.remove(var13);
                     }
                  }).runTaskLater(this.H, (long)this.Q.getInt("teleport.request-timeout", 60) * 20L);
                  if (var8.equals("tpa")) {
                     this.C(var2, var12);
                  } else {
                     this.B(var2, var12);
                  }
               }
            }

         }
      }
   }

   private void A(final Player var1, final Location var2) {
      final String var3 = var1.getUniqueId().toString();
      if (this.E.containsKey(var3)) {
         ((BukkitTask)this.E.get(var3)).cancel();
         this.E.remove(var3);
      }

      this.P.put(var3, var1.getLocation());
      final int[] var4 = new int[]{this.Q.getInt("teleport.countdown", 5)};
      BukkitTask var5 = (new BukkitRunnable() {
         public void run() {
            if (var1.isOnline() && A.this.P.containsKey(var3)) {
               if (var4[0] > 0) {
                  A.this.A(var1, "teleporting-countdown", "%seconds%", String.valueOf(var4[0]));
                  A.this.A(var1, "teleport-start");
                  int var10002 = var4[0]--;
               } else {
                  A.this.E.remove(var3);
                  A.this.P.remove(var3);
                  this.cancel();
                  A.this.G(var1);
                  A.this.E(var1, "teleport-preparing");
                  int var1x = A.this.Q.getInt("teleport.preload-radius", 3);
                  World var2x = var2.getWorld();
                  if (var2x == null) {
                     var1.teleportAsync(var2);
                     A.this.E(var1, "teleport-complete");
                     A.this.A(var1, "teleport-complete");
                  } else {
                     int var3x = var2.getBlockX() >> 4;
                     int var4x = var2.getBlockZ() >> 4;
                     ArrayList var5 = new ArrayList();

                     for(int var6 = -var1x; var6 <= var1x; ++var6) {
                        for(int var7 = -var1x; var7 <= var1x; ++var7) {
                           var5.add(var2x.getChunkAtAsync(var3x + var6, var4x + var7));
                        }
                     }

                     CompletableFuture.allOf((CompletableFuture[])var5.toArray(new CompletableFuture[0])).orTimeout(10L, TimeUnit.SECONDS).whenComplete((var3xx, var4xx) -> Bukkit.getScheduler().runTask(A.this.H, () -> {
                           if (var1.isOnline()) {
                              var1.teleportAsync(var2);
                              A.this.E(var1, "teleport-complete");
                              A.this.A(var1, "teleport-complete");
                           }
                        }));
                  }
               }
            } else {
               this.cancel();
               A.this.E.remove(var3);
               A.this.P.remove(var3);
            }
         }
      }).runTaskTimer(this.H, 0L, 20L);
      this.E.put(var3, var5);
   }

   private void G(Player var1) {
      if (!var1.getAllowFlight()) {
         var1.setAllowFlight(true);
         var1.setFlying(true);
         this.B.add(var1.getUniqueId());
      }
   }

   private void F(Player var1) {
      if (this.B.remove(var1.getUniqueId())) {
         var1.setFlying(false);
         var1.setAllowFlight(false);
      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      String var3 = var2.getUniqueId().toString();
      if (this.B.contains(var2.getUniqueId())) {
         Location var4 = var1.getFrom();
         Location var5 = var1.getTo();
         if (var5 != null && (var4.getBlockX() != var5.getBlockX() || var4.getBlockY() != var5.getBlockY() || var4.getBlockZ() != var5.getBlockZ())) {
            this.F(var2);
         }
      }

      if (this.P.containsKey(var3)) {
         Location var9 = (Location)this.P.get(var3);
         Location var10 = var2.getLocation();
         double var6 = this.Q.getDouble("teleport.movement-threshold", (double)0.5F);
         if (var9.getWorld() != var10.getWorld() || var9.distance(var10) > var6) {
            this.P.remove(var3);
            BukkitTask var8 = (BukkitTask)this.E.remove(var3);
            if (var8 != null) {
               var8.cancel();
            }

            this.E(var2, "teleport-cancelled-moved");
            this.A(var2, "error");
         }
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      String var3 = var2.toString();
      this.P.remove(var3);
      BukkitTask var4 = (BukkitTask)this.E.remove(var3);
      if (var4 != null) {
         var4.cancel();
      }

      this.C.remove(var2);
      this.A.remove(var2);
      this.M.remove(var2);
      this.R.remove(var2);
      this.N.entrySet().removeIf((var1x) -> ((String)var1x.getKey()).contains(var3));
      this.A(var1.getPlayer());
      this.B.remove(var2);
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         return Collections.emptyList();
      } else if (var4.length != 1) {
         return Collections.emptyList();
      } else {
         String var5 = var2.getName().toLowerCase();
         if (!var5.equals("tpa") && !var5.equals("tpahere") && !var5.equals("tpacancel")) {
            if (var5.equals("tpaccept")) {
               Player var6 = (Player)var1;
               return (List)this.N.keySet().stream().filter((var1x) -> var1x.endsWith(var6.getUniqueId().toString())).map((var0) -> {
                  try {
                     return Bukkit.getPlayer(UUID.fromString(var0.substring(0, 36)));
                  } catch (Exception var2) {
                     return null;
                  }
               }).filter(Objects::nonNull).map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
            } else {
               return Collections.emptyList();
            }
         } else {
            return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
         }
      }
   }

   private ItemStack A(Material var1, String var2, String... var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(this.E(var2));
         if (var3.length > 0) {
            ArrayList var6 = new ArrayList();

            for(String var10 : var3) {
               var6.add(this.E(var10));
            }

            var5.setLore(var6);
         }

         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack I(Player var1) {
      String var2 = var1.getWorld().getName();
      Material var3 = Material.GRASS_BLOCK;
      if (var2.contains("nether")) {
         var3 = Material.NETHERRACK;
      } else if (var2.contains("end")) {
         var3 = Material.END_STONE;
      } else if (var2.equals("spawn")) {
         var3 = Material.IRON_BLOCK;
      }

      String var4 = this.Q.getString("worlds." + var2, "&7" + var2);
      return this.A(var3, "&#00F986ʟᴏᴄᴀᴛɪᴏɴ", var4);
   }

   private ItemStack B(Player var1) {
      ItemStack var2 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var3 = (SkullMeta)var2.getItemMeta();
      if (var3 != null) {
         var3.setOwningPlayer(var1);
         var3.setDisplayName(this.E("&#00F986ᴘʟᴀʏᴇʀ"));
         var3.setLore(Collections.singletonList(this.E("&7" + var1.getName())));
         var2.setItemMeta(var3);
      }

      return var2;
   }
}
