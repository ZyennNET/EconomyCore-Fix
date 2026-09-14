package com.h2ph.O;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class A implements Listener, CommandExecutor, TabCompleter {
   private final PrismSurvival H;
   private final _E E;
   private final _F I;
   private final _C J;
   private final _B F;
   private final Map<UUID, Long> G = new HashMap();
   private final Map<UUID, String> D = new HashMap();
   private final Map<UUID, BukkitTask> C = new HashMap();
   private final Map<UUID, Integer> A = new HashMap();
   private final Random B = new Random();

   public A(PrismSurvival var1) {
      this.H = var1;
      this.E = new _E();
      this.I = new _F();
      this.J = new _C();
      this.F = new _B();
      Bukkit.getPluginManager().registerEvents(this, var1);
      var1.getCommand("spawn").setExecutor(this);
      var1.getCommand("spawn").setTabCompleter(this);
      var1.getCommand("setspawn").setExecutor(this);
      var1.getCommand("setspawn").setTabCompleter(this);
      var1.getCommand("delspawn").setExecutor(this);
      var1.getCommand("delspawn").setTabCompleter(this);
      var1.getCommand("spawns").setExecutor(this);
      var1.getCommand("spawns").setTabCompleter(this);
      var1.getCommand("spawnreload").setExecutor(this);
      var1.getCommand("spawnreload").setTabCompleter(this);
      var1.getLogger().info("DonutSpawn loaded – multiple spawns & GUI active");
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player) {
         Player var5 = (Player)var1;
         switch (var2.getName().toLowerCase()) {
            case "spawn" -> {
               return this.B(var5, var4);
            }
            case "setspawn" -> {
               return this.C(var5, var4);
            }
            case "delspawn" -> {
               return this.A(var5, var4);
            }
            case "spawns" -> {
               return this.A(var5);
            }
            case "spawnreload" -> {
               return this.B(var5);
            }
            default -> {
               return false;
            }
         }
      } else {
         var1.sendMessage("Only players can use this.");
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         return Collections.emptyList();
      } else {
         return (List<String>)(var2.getName().equalsIgnoreCase("delspawn") && var4.length == 1 ? new ArrayList(this.E.B().keySet()) : Collections.emptyList());
      }
   }

   private boolean B(Player var1, String[] var2) {
      if (var2.length == 0) {
         if (this.H.getConfig().getBoolean("spawn-gui", true)) {
            this.F.openGUI(var1, 0);
            this.J.B(var1);
            return true;
         } else {
            String var4 = this.H.getConfig().getString("default-spawn", "spawn1");
            if (!this.E.B(var4)) {
               this.I.A(var1, "no-spawns");
               this.J.D(var1);
               return true;
            } else {
               this.A(var1, var4);
               return true;
            }
         }
      } else {
         String var3 = var2[0].toLowerCase();
         if (!this.E.B(var3)) {
            this.I.A(var1, "spawn-not-found");
            this.J.D(var1);
            return true;
         } else {
            this.A(var1, var3);
            return true;
         }
      }
   }

   private boolean C(Player var1, String[] var2) {
      if (!var1.hasPermission("spawn.admin")) {
         this.I.A(var1, "no-permission");
         this.J.D(var1);
         return true;
      } else {
         String var3 = var2.length == 0 ? "spawn1" : var2[0].toLowerCase();
         int var4 = -1;
         if (var2.length >= 2) {
            try {
               var4 = Integer.parseInt(var2[1]);
               if (var4 < 1 && var4 != -1) {
                  this.I.A(var1, "invalid-max");
                  this.J.D(var1);
                  return true;
               }
            } catch (NumberFormatException var6) {
               this.I.A(var1, "invalid-number");
               this.J.D(var1);
               return true;
            }
         }

         this.E.A(var3, var1.getLocation(), var4);
         HashMap var5 = new HashMap();
         var5.put("name", var3);
         var5.put("max", var4 > 0 ? String.valueOf(var4) : "unlimited");
         this.I.B(var1, "setspawn-success", var5);
         this.J.E(var1);
         return true;
      }
   }

   private boolean A(Player var1, String[] var2) {
      if (!var1.hasPermission("spawn.admin")) {
         this.I.A(var1, "no-permission");
         this.J.D(var1);
         return true;
      } else if (var2.length == 0) {
         this.I.A(var1, true);
         this.J.D(var1);
         return true;
      } else {
         String var3 = var2[0].toLowerCase();
         if (!this.E.B(var3)) {
            this.I.A(var1, "spawn-not-found");
            this.J.D(var1);
            return true;
         } else {
            this.E.A(var3);
            HashMap var4 = new HashMap();
            var4.put("name", var3);
            this.I.B(var1, "delspawn-success", var4);
            this.J.E(var1);
            return true;
         }
      }
   }

   private boolean A(Player var1) {
      if (!var1.hasPermission("spawn.use")) {
         this.I.A(var1, "no-permission");
         this.J.D(var1);
         return true;
      } else {
         this.F.openGUI(var1, 0);
         this.J.B(var1);
         return true;
      }
   }

   private boolean B(Player var1) {
      if (!var1.hasPermission("spawn.admin")) {
         this.I.A(var1, "no-permission");
         this.J.D(var1);
         return true;
      } else {
         this.E.A();
         this.I.A();
         this.F.reload();
         this.H.reloadConfig();
         this.I.A(var1, "reload-success");
         this.J.E(var1);
         return true;
      }
   }

   private void A(final Player var1, final String var2) {
      if (this.C.containsKey(var1.getUniqueId())) {
         this.I.A(var1, "already-teleporting");
         this.J.A(var1);
      } else {
         final _A var3 = this.E.C(var2);
         if (var3 == null) {
            this.I.A(var1, "spawn-not-found");
            this.J.D(var1);
         } else if (var3.A()) {
            this.I.A(var1, "spawn-full");
            this.J.D(var1);
         } else {
            int var4 = this.H.getConfig().getInt("spawn-delay", 5);
            if (!var1.hasPermission("spawn.bypass") && var4 > 0) {
               final UUID var5 = var1.getUniqueId();
               long var6 = System.currentTimeMillis() + (long)var4 * 1000L;
               this.G.put(var5, var6);
               this.D.put(var5, var2);
               HashMap var8 = new HashMap();
               var8.put("count", String.valueOf(var4));
               this.I.A(var1, "teleport-started", var8);
               this.J.F(var1);
               BukkitTask var9 = (new BukkitRunnable() {
                  public void run() {
                     A.this.A(var1, var5, var2, var3.G());
                  }
               }).runTaskTimer(this.H, 20L, 20L);
               this.C.put(var5, var9);
            } else {
               this.A(var1, var2, var3.G());
            }
         }
      }
   }

   private void A(Player var1, UUID var2, String var3, Location var4) {
      if (var1.isOnline() && this.G.containsKey(var2)) {
         long var5 = (Long)this.G.get(var2) - System.currentTimeMillis();
         long var7 = Math.max(0L, (var5 + 999L) / 1000L);
         if (var7 <= 0L) {
            this.A(var2);
            this.A(var1, var3, var4);
         } else {
            HashMap var9 = new HashMap();
            var9.put("count", String.valueOf(var7));
            this.I.A(var1, "teleport-started", var9);
            this.J.F(var1);
         }
      } else {
         this.A(var2);
      }
   }

   private void A(Player var1, String var2, Location var3) {
      _A var4 = this.E.C(var2);
      if (var4 != null) {
         var4.C();
      }

      var1.teleportAsync(var3.clone());
      HashMap var5 = new HashMap();
      var5.put("name", var2);
      this.I.B(var1, "teleport-completed", var5);
      this.I.A(var1, "teleport-completed", var5);
      this.J.C(var1);
   }

   private void A(UUID var1) {
      this.G.remove(var1);
      this.D.remove(var1);
      BukkitTask var2 = (BukkitTask)this.C.remove(var1);
      if (var2 != null) {
         var2.cancel();
      }

   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent var1) {
      if (this.H.getConfig().getBoolean("cancel-on-move", true)) {
         Player var2 = var1.getPlayer();
         if (this.C.containsKey(var2.getUniqueId())) {
            Location var3 = var1.getFrom();
            Location var4 = var1.getTo();
            if (var4 != null && (var3.getBlockX() != var4.getBlockX() || var3.getBlockY() != var4.getBlockY() || var3.getBlockZ() != var4.getBlockZ())) {
               this.A(var2.getUniqueId());
               this.I.A(var2, "teleport-cancelled");
               this.J.A(var2);
            }

         }
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      this.A(var1.getPlayer().getUniqueId());
      Player var2 = var1.getPlayer();
      String var3 = this.E.C(var2.getUniqueId());
      if (var3 != null) {
         _A var4 = this.E.C(var3);
         if (var4 != null) {
            var4.B();
         }

         this.E.A(var2.getUniqueId());
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerRespawn(PlayerRespawnEvent var1) {
      final String var2 = this.H.getConfig().getString("default-spawn", "spawn1");
      final _A var3 = this.E.C(var2);
      if (var3 != null && !var3.A()) {
         boolean var4 = this.H.getConfig().getBoolean("teleport-on-respawn", true);
         if (var4) {
            var1.setRespawnLocation(var3.G());
         }

         boolean var5 = this.H.getConfig().getBoolean("teleport-on-death", true);
         if (var5) {
            final Player var6 = var1.getPlayer();
            (new BukkitRunnable() {
               public void run() {
                  if (var6.isOnline()) {
                     A.this.A(var6, var2, var3.G());
                  }

               }
            }).runTaskLater(this.H, 1L);
         }

      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      final Player var2 = var1.getPlayer();
      if (this.H.getConfig().getBoolean("first-join.enabled", false) && !this.E.B(var2.getUniqueId())) {
         final String var3 = this.H.getConfig().getString("first-join.spawn-name", "spawn1");
         if (this.E.B(var3)) {
            (new BukkitRunnable() {
               public void run() {
                  if (var2.isOnline()) {
                     A.this.A(var2, var3, A.this.E.C(var3).G());
                     A.this.E.A(var2.getUniqueId(), true);
                  }

               }
            }).runTaskLater(this.H, 5L);
            return;
         }
      }

      if (this.H.getConfig().getBoolean("join.enable", false)) {
         final String var4 = this.H.getConfig().getString("join.spawn-name", "spawn1");
         if (this.E.B(var4)) {
            (new BukkitRunnable() {
               public void run() {
                  if (var2.isOnline()) {
                     A.this.A(var2, var4, A.this.E.C(var4).G());
                  }

               }
            }).runTaskLater(this.H, 5L);
         }
      }

   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() instanceof _B && var1.getCurrentItem() != null) {
         var1.setCancelled(true);
         Player var2 = (Player)var1.getWhoClicked();
         int var3 = var1.getSlot();
         int var4 = (Integer)this.A.getOrDefault(var2.getUniqueId(), 0);
         int var5 = Math.max(0, this.F.H - 9);
         if (this.F.B != null && var3 == this.F.B.D) {
            ArrayList var10 = new ArrayList();

            for(_A var8 : this.E.B().values()) {
               if (!var8.A()) {
                  var10.add(var8);
               }
            }

            if (var10.isEmpty()) {
               this.I.A(var2, "all-full");
               this.J.D(var2);
            } else {
               var2.closeInventory();
               _A var12 = (_A)var10.get(this.B.nextInt(var10.size()));
               this.A(var2, var12.D());
               this.J.B(var2);
            }
         } else if (this.F.A != null && var3 == this.F.A.D) {
            if (var4 > 0) {
               this.F.openGUI(var2, var4 - 1);
               this.J.B(var2);
            } else {
               this.J.D(var2);
            }

         } else if (this.F.F != null && var3 == this.F.F.D) {
            ArrayList var9 = new ArrayList(this.E.B().values());
            if ((var4 + 1) * var5 < var9.size()) {
               this.F.openGUI(var2, var4 + 1);
               this.J.B(var2);
            } else {
               this.J.D(var2);
            }

         } else if (var3 < var5) {
            ArrayList var6 = new ArrayList(this.E.B().values());
            var6.sort(Comparator.comparing(_A::D));
            int var7 = var4 * var5 + var3;
            if (var7 >= 0 && var7 < var6.size()) {
               var2.closeInventory();
               this.A(var2, ((_A)var6.get(var7)).D());
               this.J.B(var2);
            }
         }
      }
   }

   public static class _A {
      private final String C;
      private final Location A;
      private final int B;
      private int D;

      public _A(String var1, Location var2, int var3) {
         this.C = var1;
         this.A = var2;
         this.B = var3;
      }

      public String D() {
         return this.C;
      }

      public Location G() {
         return this.A;
      }

      public int E() {
         return this.B;
      }

      public int F() {
         return this.D;
      }

      public void A(int var1) {
         this.D = var1;
      }

      public boolean A() {
         return this.B > 0 && this.D >= this.B;
      }

      public void C() {
         if (!this.A()) {
            ++this.D;
         }

      }

      public void B() {
         if (this.D > 0) {
            --this.D;
         }

      }
   }

   private class _B implements InventoryHolder {
      private String E = "&8Spawns";
      private int G = 6;
      private int H = 54;
      private _A C;
      private _A D;
      private _B B;
      private _B A;
      private _B F;

      _B() {
         this.A();
      }

      void reload() {
         this.A();
      }

      void openGUI(Player var1, int var2) {
         Inventory var3 = Bukkit.createInventory(this, this.H, A._D.A(this.E));
         A.this.A.put(var1.getUniqueId(), var2);
         ArrayList var4 = new ArrayList(A.this.E.B().values());
         var4.sort(Comparator.comparing(_A::D));
         int var5 = Math.max(0, this.H - 9);
         int var6 = var2 * var5;
         int var7 = Math.min(var4.size(), var6 + var5);

         for(int var8 = 0; var8 < var7 - var6; ++var8) {
            _A var9 = (_A)var4.get(var6 + var8);
            _A var10 = var9.A() ? this.D : this.C;
            if (var10 != null && var10.B != null) {
               ItemStack var11 = new ItemStack(var10.B);
               ItemMeta var12 = var11.getItemMeta();
               var12.displayName(A._D.A(var10.A.replace("%name%", var9.D())));
               var12.lore(A._D.A(this.A(var10.C, var9)));
               var11.setItemMeta(var12);
               var3.setItem(var8, var11);
            }
         }

         this.A(var3, this.B);
         this.A(var3, this.A);
         this.A(var3, this.F);
         var1.openInventory(var3);
      }

      private void A(Inventory var1, _B var2) {
         if (var2 != null && var2.B != null && var2.D >= 0 && var2.D < this.H) {
            ItemStack var3 = new ItemStack(var2.B);
            ItemMeta var4 = var3.getItemMeta();
            var4.displayName(A._D.A(var2.A));
            var4.lore(A._D.A(var2.C));
            var3.setItemMeta(var4);
            var1.setItem(var2.D, var3);
         }
      }

      private List<String> A(List<String> var1, _A var2) {
         ArrayList var3 = new ArrayList();

         for(String var5 : var1) {
            var3.add(var5.replace("%name%", var2.D()).replace("%current%", String.valueOf(var2.F())).replace("%max%", var2.E() > 0 ? String.valueOf(var2.E()) : "∞"));
         }

         return var3;
      }

      private void A() {
         File var1 = new File(A.this.H.getDataFolder(), "spawnv2/gui/spawn-gui.yml");
         if (!var1.exists()) {
            var1.getParentFile().mkdirs();
            A.this.H.saveResource("spawnv2/gui/spawn-gui.yml", false);
         }

         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
         ConfigurationSection var3 = var2.getConfigurationSection("spawn-gui");
         if (var3 != null) {
            this.E = var3.getString("title", "&8Spawns");
            this.G = Math.max(1, Math.min(6, var3.getInt("row", 6)));
            this.H = this.G * 9;
            ConfigurationSection var4 = var3.getConfigurationSection("gui-settings");
            if (var4 != null) {
               this.C = this.A(var4.getConfigurationSection("spawn"), Material.ITEM_FRAME, "&bSpawn %name%");
               this.D = this.A(var4.getConfigurationSection("maxed-players"), Material.REDSTONE_BLOCK, "&cSpawn %name%");
               this.B = this.A(var4.getConfigurationSection("random-spawn"), Material.LIGHT_BLUE_GLAZED_TERRACOTTA, "&aRandom Spawn", 49);
               this.A = this.A(var4.getConfigurationSection("back"), Material.ARROW, "&aBack", 45);
               this.F = this.A(var4.getConfigurationSection("next"), Material.ARROW, "&aNext", 53);
            }
         }
      }

      private _A A(ConfigurationSection var1, Material var2, String var3) {
         _A var4 = new _A();
         var4.B = this.A(var1 == null ? null : var1.getString("material"), var2);
         var4.A = var1 == null ? var3 : var1.getString("displayName", var3);
         var4.C = var1 == null ? List.of() : var1.getStringList("lore");
         return var4;
      }

      private _B A(ConfigurationSection var1, Material var2, String var3, int var4) {
         _B var5 = new _B();
         var5.B = this.A(var1 == null ? null : var1.getString("material"), var2);
         var5.A = var1 == null ? var3 : var1.getString("displayName", var3);
         var5.C = var1 == null ? List.of() : var1.getStringList("lore");
         var5.D = var1 == null ? var4 : var1.getInt("slot", var4);
         return var5;
      }

      private Material A(String var1, Material var2) {
         if (var1 == null) {
            return var2;
         } else {
            Material var3 = Material.matchMaterial(var1);
            return var3 == null ? var2 : var3;
         }
      }

      public Inventory getInventory() {
         return null;
      }

      private class _A {
         Material B;
         String A;
         List<String> C;
      }

      private class _B extends _A {
         int D;
      }
   }

   private class _C {
      private final Map<String, String> B = new HashMap();

      _C() {
         this.B.put("teleport-cooldown", "block.note_block.pling");
         this.B.put("teleport-complete", "entity.enderman.teleport");
         this.B.put("teleport-cancel", "entity.villager.no");
         this.B.put("no-permission", "entity.villager.no");
         this.B.put("reload-success", "entity.player.levelup");
         this.B.put("click-sound", "ui.button.click");
      }

      void F(Player var1) {
         this.A(var1, "teleport-cooldown");
      }

      void C(Player var1) {
         this.A(var1, "teleport-complete");
      }

      void A(Player var1) {
         this.A(var1, "teleport-cancel");
      }

      void D(Player var1) {
         this.A(var1, "no-permission");
      }

      void B(Player var1) {
         this.A(var1, "click-sound");
      }

      void E(Player var1) {
         this.A(var1, "reload-success");
      }

      void A(Player var1, String var2) {
         try {
            String var3 = A.this.H.getConfig().getString("Sounds." + var2, (String)this.B.get(var2));
            if (var3 != null && !var3.isBlank()) {
               var1.playSound(var1.getLocation(), var3, 1.0F, 1.0F);
            }
         } catch (Exception var5) {
            String var4 = (String)this.B.get(var2);
            if (var4 != null) {
               var1.playSound(var1.getLocation(), var4, 1.0F, 1.0F);
            }
         }

      }
   }

   public static class _D {
      private static final Pattern A = Pattern.compile("&#([A-Fa-f0-9]{6})");
      private static final LegacyComponentSerializer B = LegacyComponentSerializer.builder().character('§').hexColors().build();

      public static String B(String var0) {
         if (var0 == null) {
            return null;
         } else {
            Matcher var1 = A.matcher(var0);
            StringBuffer var2 = new StringBuffer();

            while(var1.find()) {
               String var3 = var1.group(1);
               char var10000 = var3.charAt(0);
               String var4 = "§x§" + var10000 + "§" + var3.charAt(1) + "§" + var3.charAt(2) + "§" + var3.charAt(3) + "§" + var3.charAt(4) + "§" + var3.charAt(5);
               var1.appendReplacement(var2, Matcher.quoteReplacement(var4));
            }

            var1.appendTail(var2);
            return ChatColor.translateAlternateColorCodes('&', var2.toString());
         }
      }

      public static Component A(String var0) {
         return (Component)(var0 != null && !var0.isEmpty() ? B.deserialize(B(var0)).decoration(TextDecoration.ITALIC, false) : Component.empty());
      }

      public static List<Component> A(List<String> var0) {
         ArrayList var1 = new ArrayList();
         if (var0 == null) {
            return var1;
         } else {
            for(String var3 : var0) {
               var1.add(A(var3));
            }

            return var1;
         }
      }
   }

   private class _E {
      private final File G;
      private final File H;
      private YamlConfiguration E;
      private YamlConfiguration F;
      private final Map<String, _A> D;
      private final Map<UUID, Boolean> B;
      private final Map<UUID, String> C;

      _E() {
         this.G = new File(A.this.H.getDataFolder(), "spawnv2/spawns.yml");
         this.H = new File(A.this.H.getDataFolder(), "spawnv2/playerdata.yml");
         this.D = new HashMap();
         this.B = new HashMap();
         this.C = new HashMap();
         this.A();
      }

      void A() {
         this.C();
         this.D();
      }

      private void C() {
         this.D.clear();
         if (!this.G.exists()) {
            this.E = new YamlConfiguration();
            this.A(this.E, this.G);
         } else {
            this.E = YamlConfiguration.loadConfiguration(this.G);
            ConfigurationSection var1 = this.E.getConfigurationSection("spawns");
            if (var1 != null) {
               for(String var3 : var1.getKeys(false)) {
                  String var4 = "spawns." + var3 + ".";
                  String var5 = this.E.getString(var4 + "world");
                  if (var5 != null) {
                     World var6 = A.this.H.getServer().getWorld(var5);
                     if (var6 != null) {
                        Location var7 = new Location(var6, this.E.getDouble(var4 + "x"), this.E.getDouble(var4 + "y"), this.E.getDouble(var4 + "z"), (float)this.E.getDouble(var4 + "yaw"), (float)this.E.getDouble(var4 + "pitch"));
                        int var8 = this.E.getInt(var4 + "maxPlayers", -1);
                        this.D.put(var3, new _A(var3, var7, var8));
                     }
                  }
               }

            }
         }
      }

      private void D() {
         this.B.clear();
         this.C.clear();
         if (!this.H.exists()) {
            this.F = new YamlConfiguration();
            this.A(this.F, this.H);
         } else {
            this.F = YamlConfiguration.loadConfiguration(this.H);
            ConfigurationSection var1 = this.F.getConfigurationSection("players");
            if (var1 != null) {
               for(String var3 : var1.getKeys(false)) {
                  try {
                     UUID var4 = UUID.fromString(var3);
                     boolean var5 = this.F.getBoolean("players." + var3 + ".first-join-done", false);
                     this.B.put(var4, var5);
                     String var6 = this.F.getString("players." + var3 + ".current-spawn");
                     if (var6 != null) {
                        this.C.put(var4, var6);
                     }
                  } catch (IllegalArgumentException var7) {
                  }
               }

            }
         }
      }

      void A(String var1, Location var2, int var3) {
         this.D.put(var1, new _A(var1, var2, var3));
         if (this.E == null) {
            this.E = new YamlConfiguration();
         }

         String var4 = "spawns." + var1 + ".";
         this.E.set(var4 + "world", var2.getWorld().getName());
         this.E.set(var4 + "x", var2.getX());
         this.E.set(var4 + "y", var2.getY());
         this.E.set(var4 + "z", var2.getZ());
         this.E.set(var4 + "yaw", var2.getYaw());
         this.E.set(var4 + "pitch", var2.getPitch());
         this.E.set(var4 + "maxPlayers", var3);
         this.A(this.E, this.G);
      }

      void A(String var1) {
         this.D.remove(var1);
         if (this.E != null) {
            this.E.set("spawns." + var1, (Object)null);
            this.A(this.E, this.G);
         }

      }

      _A C(String var1) {
         return (_A)this.D.get(var1);
      }

      Map<String, _A> B() {
         return new HashMap(this.D);
      }

      boolean B(String var1) {
         return this.D.containsKey(var1);
      }

      void A(UUID var1, boolean var2) {
         this.B.put(var1, var2);
         if (this.F == null) {
            this.F = new YamlConfiguration();
         }

         this.F.set("players." + String.valueOf(var1) + ".first-join-done", var2);
         this.F.set("players." + String.valueOf(var1) + ".last-joined", System.currentTimeMillis());
         this.A(this.F, this.H);
      }

      boolean B(UUID var1) {
         return (Boolean)this.B.getOrDefault(var1, false);
      }

      void A(UUID var1, String var2) {
         this.C.put(var1, var2);
      }

      String C(UUID var1) {
         return (String)this.C.get(var1);
      }

      void A(UUID var1) {
         this.C.remove(var1);
      }

      private void A(YamlConfiguration var1, File var2) {
         try {
            var1.save(var2);
         } catch (IOException var4) {
            Logger var10000 = A.this.H.getLogger();
            String var10001 = var2.getName();
            var10000.warning("Failed to save " + var10001 + ": " + var4.getMessage());
         }

      }
   }

   private class _F {
      private final File E;
      private YamlConfiguration G;
      private final Map<String, String> A;
      private final Map<String, String> B;
      private boolean I;
      private String D;
      private String F;
      private String H;

      _F() {
         this.E = new File(A.this.H.getDataFolder(), "spawnv2/lang.yml");
         this.A = new HashMap();
         this.B = new HashMap();
         this.I = true;
         this.D = "&#00A0FC&lSPAWN &7»";
         this.F = "";
         this.H = "";
         this.A();
      }

      void A() {
         if (!this.E.exists()) {
            this.E.getParentFile().mkdirs();
            A.this.H.saveResource("spawnv2/lang.yml", false);
         }

         this.G = YamlConfiguration.loadConfiguration(this.E);
         this.A.clear();
         this.B.clear();
         this.I = this.G.getBoolean("prefix-enable", true);
         this.D = this.G.getString("prefix", "&#00A0FC&lSPAWN &7»");
         this.F = this.G.getString("command-usage", "");
         this.H = this.G.getString("command-usage-admin", "");
         this.A("messages", this.A);
         this.A("action-bars", this.B);
      }

      private void A(String var1, Map<String, String> var2) {
         ConfigurationSection var3 = this.G.getConfigurationSection(var1);
         if (var3 != null) {
            for(String var5 : var3.getKeys(false)) {
               String var6 = var3.getString(var5);
               if (var6 != null) {
                  var2.put(var5, var6);
               }
            }

         }
      }

      String B(String var1) {
         return (String)this.A.getOrDefault(var1, "&cMissing message: " + var1);
      }

      String A(String var1) {
         return (String)this.B.getOrDefault(var1, "&cMissing action bar: " + var1);
      }

      void A(Player var1, String var2) {
         var1.sendMessage(A._D.A(this.C(this.B(var2))));
      }

      void B(Player var1, String var2, Map<String, String> var3) {
         String var4 = this.B(var2);

         for(Map.Entry var6 : var3.entrySet()) {
            var4 = var4.replace("%" + (String)var6.getKey() + "%", (CharSequence)var6.getValue());
         }

         var1.sendMessage(A._D.A(this.C(var4)));
      }

      void B(Player var1, String var2) {
         var1.sendActionBar(A._D.A(this.A(var2)));
      }

      void A(Player var1, String var2, Map<String, String> var3) {
         String var4 = this.A(var2);

         for(Map.Entry var6 : var3.entrySet()) {
            var4 = var4.replace("%" + (String)var6.getKey() + "%", (CharSequence)var6.getValue());
         }

         var1.sendActionBar(A._D.A(var4));
      }

      void A(Player var1, boolean var2) {
         var1.sendMessage(A._D.A(var2 ? this.H : this.F));
      }

      private String C(String var1) {
         return this.I ? this.D + " " + var1 : var1;
      }
   }
}
