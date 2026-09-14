package com.h2ph.B;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Hopper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class A implements Listener {
   private final PrismSurvival E;
   private final _E C;
   private final _B J;
   private final _L B;
   private final _M G;
   private final _A D;
   private final _D K;
   private FileConfiguration F;
   private final Map<UUID, Set<Location>> I = new ConcurrentHashMap();
   private BukkitTask H;
   private ProtocolManager A;

   public A(PrismSurvival var1) {
      this.E = var1;
      this.B();
      this.C = new _E();
      this.J = new _B();
      this.B = new _L();
      this.G = new _M();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      this.C();
      this.A();
      if (var1.getCommand("spawner") != null) {
         var1.getCommand("spawner").setExecutor(this.G);
         var1.getCommand("spawner").setTabCompleter(this.G);
      } else {
         var1.getLogger().warning("[DonutSpawners] 'spawner' isn't declared in plugin.yml — /spawner won't work.");
      }

      long var2 = this.F.getLong("settings.production_interval", 600L);
      this.D = new _A();
      this.D.runTaskTimer(var1, var2, var2);
      int var4 = this.F.getInt("hopper.stack_per_transfer", 5);
      this.K = new _D(var4);
      this.K.runTaskTimer(var1, 20L, 20L);
      if (this.F.getBoolean("ANTI_ESP_ENABLED", false)) {
         try {
            this.A = ProtocolLibrary.getProtocolManager();
            this.D();
            this.E();
            var1.getLogger().info("[DonutSpawners] Anti-ESP enabled via ProtocolLib.");
         } catch (Exception var7) {
            var1.getLogger().warning("[DonutSpawners] ProtocolLib not found — Anti-ESP disabled: " + var7.getMessage());
         }
      }

      if (this.F.getBoolean("MENU_AUTO_UPDATE_ENABLED", true)) {
         long var5 = this.F.getLong("MENU_AUTO_UPDATE_PERIOD", 60L);
         (new BukkitRunnable() {
            public void run() {
               for(Player var2 : Bukkit.getOnlinePlayers()) {
                  Inventory var3 = var2.getOpenInventory().getTopInventory();
                  InventoryHolder var5 = var3.getHolder();
                  if (var5 instanceof _K var4) {
                     (A.this.new _F(var4.getData(), var4.isStorage(), var4.getPage())).A(var2);
                  }
               }

            }
         }).runTaskTimer(var1, var5, var5);
      }

      var1.getLogger().info("[DonutSpawners] Module loaded.");
   }

   private void B() {
      File var1 = new File(this.E.getDataFolder(), "spawners/config.yml");
      var1.getParentFile().mkdirs();
      if (!var1.exists()) {
         this.A(var1);
      }

      this.F = YamlConfiguration.loadConfiguration(var1);
   }

   public void reloadConfig() {
      File var1 = new File(this.E.getDataFolder(), "spawners/config.yml");
      if (!var1.exists()) {
         this.A(var1);
      }

      this.F = YamlConfiguration.loadConfiguration(var1);
      this.E.getLogger().info("[DonutSpawners] Config reloaded.");
   }

   private void A(File var1) {
      String var2 = "# ════════════════════════════════════════════════════\n# DonutSpawners — Full Configuration\n# Every piece of text, slot, material and title\n# is controlled here.\n# ════════════════════════════════════════════════════\n\nECONOMY_ENABLED: true\nDROP_TO_INVENTORY: false\nCOLLECTION_RADIUS: 16\n\n# ── Anti-ESP ──────────────────────────────────────────\n# Spawner block hidden >radius blocks via ProtocolLib packets.\n# Spawner DATA is NEVER deleted – client view only.\nANTI_ESP_ENABLED: false\nANTI_ESP_RADIUS: 16\n\n# ── Feature Toggles ───────────────────────────────────\nFILTER_GUI_ENABLED: false\n\n# ── Storage GUI (54-slot) ──────────────────────────────\nSTORAGE_GUI:\n  # {STACK}, {TYPE_DISPLAY}, {PAGE}, {PAGES}\n  TITLE: \"&#6BF18D{STACK} {TYPE_DISPLAY} ѕᴛᴏʀᴀɢᴇ &7({PAGE}/{PAGES})\"\n  PREV_BUTTON:\n    SLOT: 45\n    MATERIAL: SPECTRAL_ARROW\n    NAME: \"&aBack\"\n    LORE:\n      - \"&fPrevious page\"\n  NEXT_BUTTON:\n    SLOT: 53\n    MATERIAL: SPECTRAL_ARROW\n    NAME: \"&aNext\"\n    LORE:\n      - \"&fNext page\"\n  SELL_BUTTON:\n    SLOT: 48\n    MATERIAL: GOLD_INGOT\n    NAME: \"&cѕᴇʟʟ ᴀʟʟ\"\n    LORE:\n      - \"&fClick to sell all mob drops!\"\n  INFO_BUTTON:\n    SLOT: 49\n    MATERIAL: AUTO\n    NAME: \"&#6BF18D{STACK} {TYPE_DISPLAY} ѕᴘᴀᴡɴᴇʀѕ\"\n    LORE:\n      - \"&#6BF18D({FILLED_PERCENT}% filled)\"\n  DROP_BUTTON:\n    SLOT: 50\n    MATERIAL: DROPPER\n    NAME: \"&#6BF18Dᴅʀᴏᴘ ʟᴏᴏᴛ\"\n    LORE:\n      - \"&fClick to drop all loot on the page\"\n  FILTER_BUTTON:\n    SLOT: 46\n    MATERIAL: HOPPER\n    NAME: \"&bFILTER\"\n    LORE:\n      - \"&fClick to manage loot filters\"\n\n# ── Sell Confirm GUI (27-slot) ─────────────────────────\nCONFIRM_SELL_GUI:\n  TITLE: \"&8ᴄᴏɴꜰɪʀᴍ ѕᴇʟʟ\"\n  CANCEL_BUTTON:\n    SLOT: 11\n    MATERIAL: RED_STAINED_GLASS_PANE\n    NAME: \"&cᴄᴀɴᴄᴇʟ\"\n    LORE:\n      - \"&fClick to cancel\"\n  INFO_BUTTON:\n    SLOT: 13\n    MATERIAL: PLAYER_HEAD\n    NAME: \"&#6BF18Dʟᴏᴏᴛ\"\n    LORE: []\n    DROPS_LINE_TEMPLATE: \"&#6BF18D{AMOUNT} &f{ITEM}\"\n  CONFIRM_BUTTON:\n    SLOT: 15\n    MATERIAL: LIME_STAINED_GLASS_PANE\n    NAME: \"&aᴄᴏɴꜰɪʀᴍ\"\n    LORE:\n      - \"&fClick to sell all items\"\n      - \"&7({TOTAL_MONEY})\"\n\n# ── Filter GUI (27-slot) ───────────────────────────────\nFILTER_GUI:\n  TITLE: \"&8FILTER\"\n  BACK_BUTTON:\n    SLOT: 22\n    MATERIAL: RED_STAINED_GLASS_PANE\n    NAME: \"&4BACK\"\n    LORE:\n      - \"&fReturn to storage\"\n  ENABLED_LORE: \"&a✓ Enabled (click to blacklist)\"\n  BLACKLISTED_LORE: \"&c✗ Blacklisted (click to enable)\"\n\n# ── Spawner Admin GUI (54-slot, /spawner admin) ─────────\nSPAWNER_ADMIN_GUI:\n  # {PAGE}, {PAGES}\n  TITLE: \"&8Spawner Admin &7(Page {PAGE}/{PAGES})\"\n  PREV_BUTTON:\n    SLOT: 45\n    MATERIAL: ARROW\n    NAME: \"&aBack\"\n    LORE:\n      - \"&fClick to go to the previous page\"\n  NEXT_BUTTON:\n    SLOT: 53\n    MATERIAL: ARROW\n    NAME: \"&aNext\"\n    LORE:\n      - \"&fClick to go to the next page\"\n  REFRESH_BUTTON:\n    SLOT: 49\n    MATERIAL: ANVIL\n    NAME: \"&dSpawner Admin\"\n    LORE:\n      - \"&fClick to refresh\"\n      - \"&7Total spawners: &f{TOTAL}\"\n  ITEM:\n    # {TYPE_DISPLAY}, {WORLD}, {X}, {Y}, {Z}, {AMOUNT}\n    MATERIAL: SPAWNER\n    NAME: \"&d{TYPE_DISPLAY} Spawner\"\n    LORE:\n      - \"&7World: &f{WORLD}\"\n      - \"&7Coords: &f{X}, {Y}, {Z}\"\n      - \"&7Type: &f{TYPE_DISPLAY}\"\n      - \"&7Amount: &f{AMOUNT}\"\n      - \"&8Click to teleport\"\n\n# ── Spawner Item ───────────────────────────────────────\nSPAWNER_ITEM:\n  NAME_TEMPLATE: \"&dSpawner\"\n  LORE:\n    - \"&7Type: &e{TYPE_DISPLAY}\"\n\n# ── Messages ───────────────────────────────────────────\nMESSAGES:\n  NO_PERMISSION:           \"&cYou don't have permission.\"\n  PLAYER_NOT_FOUND:        \"&cPlayer not found.\"\n  INVALID_SPAWNER_ITEM:    \"&cInvalid spawner item.\"\n  TYPE_MISMATCH:           \"&cSpawner type does not match.\"\n  MUST_HOLD_SPAWNER:       \"&cYou must hold a spawner.\"\n  STACKED_ONE:             \"&aYou just added another spawner! Now {STACK}x in the stack.\"\n  BEING_VIEWED:            \"&cSpawner is being viewed by another player!\"\n  NO_ECONOMY:              \"&cNo economy provider found.\"\n  NOTHING_TO_SELL:         \"&eNothing to sell.\"\n  SOLD_ALL:                \"&7You sold items for {MONEY}\"\n  TRANSACTION_FAILED:      \"&cTransaction failed.\"\n  NO_LOCATION:             \"&cSpawner has no location.\"\n  INVALID_WORLD:           \"&cInvalid world.\"\n  DROPPED_PAGE:            \"&7Dropped current page loot.\"\n  NO_LOOT_PAGE:            \"&cSpawner has no loot.\"\n  NO_XP:                   \"&cNo XP to collect.\"\n  NO_ITEMS_OR_XP:          \"&cNo items or XP to collect.\"\n  INVALID_TYPE:            \"&cInvalid spawner type: &f{TYPE}\"\n  INVALID_AMOUNT:          \"&cInvalid amount. Must be between 1 and 64.\"\n  RECEIVED_SPAWNERS:       \"&7You received &#6BF18Dx{GIVEN} {TYPE_NAME} &7spawner.\"\n  GAVE_SPAWNERS:           \"&7Gave &#6BF18Dx{GIVEN} {TYPE_NAME} &7spawner to &#6BF18D{TARGET}\"\n  CANNOT_BREAK_NO_SILK:    \"&cYou cannot break spawners without Silk Touch!\"\n  CONFIG_RELOADED:         \"&aSpawners config reloaded.\"\n  SPAWNER_STACKED_ACTION:  \"&aStack: {STACK}x\"\n\n# ── Internal settings ──────────────────────────────────\nsettings:\n  production_interval: 600\n  storage_capacity: 1000000\n  isolation_radius: 5\n  require_silk_touch: true\n  natural_spawners_virtual: false\n\nMENU_AUTO_UPDATE_ENABLED: true\nMENU_AUTO_UPDATE_PERIOD: 60\n\nhopper:\n  enabled: true\n  stack_per_transfer: 5\n\nxp:\n  amount_per_cycle: 5\n\nisolated_bonus: 0.5\n\nmultipliers:\n  SKELETON: 1.0\n  ZOMBIE: 1.0\n  SPIDER: 1.0\n  CREEPER: 1.0\n  ENDERMAN: 1.0\n  BLAZE: 1.0\n  IRON_GOLEM: 1.5\n\nprices:\n  BONE: 2.0\n  ARROW: 1.5\n  ROTTEN_FLESH: 1.0\n  STRING: 2.0\n  SPIDER_EYE: 2.5\n  GUNPOWDER: 3.0\n  ENDER_PEARL: 5.0\n  BLAZE_ROD: 8.0\n  GHAST_TEAR: 10.0\n  IRON_INGOT: 4.0\n  GOLD_INGOT: 6.0\n  EMERALD: 10.0\n  COAL: 1.0\n  TOTEM_OF_UNDYING: 300.0\n  PRISMARINE_SHARD: 3.0\n  PRISMARINE_CRYSTALS: 5.0\n  SHULKER_SHELL: 20.0\n  TRIDENT: 50.0\n  PHANTOM_MEMBRANE: 4.0\n  SLIME_BALL: 2.0\n  MAGMA_CREAM: 3.0\n  LEATHER: 2.0\n  BEEF: 1.5\n  PORKCHOP: 1.5\n  FEATHER: 1.0\n  HONEYCOMB: 3.0\n  INK_SAC: 2.0\n  GLOW_INK_SAC: 4.0\n";

      try {
         Files.writeString(var1.toPath(), var2);
      } catch (Exception var4) {
         this.E.getLogger().warning("[DonutSpawners] Could not write default config: " + var4.getMessage());
      }

   }

   private boolean A(String var1, boolean var2) {
      return this.F.getBoolean(var1, var2);
   }

   private int A(String var1, int var2) {
      return this.F.getInt(var1, var2);
   }

   private long A(String var1, long var2) {
      return this.F.getLong(var1, var2);
   }

   private double A(String var1, double var2) {
      return this.F.getDouble(var1, var2);
   }

   private String A(String var1, String var2) {
      String var3 = this.F.getString(var1, var2);
      return var3 == null ? var2 : var3;
   }

   private List<String> A(String var1) {
      return this.F.getStringList(var1);
   }

   private String D(String var1) {
      return colorize(this.A("MESSAGES." + var1, "&c[MSG:" + var1 + "]"));
   }

   private String A(String var1, Map<String, String> var2) {
      String var3 = this.A("MESSAGES." + var1, "&c[MSG:" + var1 + "]");

      for(Map.Entry var5 : var2.entrySet()) {
         var3 = var3.replace((CharSequence)var5.getKey(), (CharSequence)var5.getValue());
      }

      return colorize(var3);
   }

   static String colorize(String var0) {
      if (var0 == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder(var0.length() + 32);
         int var2 = 0;

         while(var2 < var0.length()) {
            if (var2 + 7 <= var0.length() && var0.charAt(var2) == '&' && var0.charAt(var2 + 1) == '#') {
               String var3 = var0.substring(var2 + 2, var2 + 8);
               boolean var4 = var3.chars().allMatch((var0x) -> var0x >= 48 && var0x <= 57 || var0x >= 97 && var0x <= 102 || var0x >= 65 && var0x <= 70);
               if (var4) {
                  var1.append(ChatColor.of("#" + var3));
                  var2 += 8;
                  continue;
               }
            }

            var1.append(var0.charAt(var2++));
         }

         return org.bukkit.ChatColor.translateAlternateColorCodes('&', var1.toString());
      }
   }

   private List<String> A(List<String> var1) {
      ArrayList var2 = new ArrayList();
      if (var1 != null) {
         for(String var4 : var1) {
            var2.add(colorize(var4));
         }
      }

      return var2;
   }

   private Material A(String var1, Material var2) {
      if (var1 == null) {
         return var2;
      } else {
         try {
            return Material.valueOf(var1.toUpperCase());
         } catch (IllegalArgumentException var4) {
            return var2;
         }
      }
   }

   private void E() {
   }

   private void D() {
      final int var1 = this.A((String)"ANTI_ESP_RADIUS", 16);
      this.H = (new BukkitRunnable() {
         public void run() {
            for(Player var2 : Bukkit.getOnlinePlayers()) {
               A.this.A(var2, var1);
            }

         }
      }).runTaskTimer(this.E, 20L, 20L);
   }

   private void A(Player var1, int var2) {
      if (this.A != null) {
         Set var3 = (Set)this.I.computeIfAbsent(var1.getUniqueId(), (var0) -> ConcurrentHashMap.newKeySet());

         for(_C var5 : this.C.B().values()) {
            Location var6 = var5.D();
            if (var6.getWorld() != null && var6.getWorld().equals(var1.getWorld())) {
               double var7 = var6.distance(var1.getLocation());
               if (var7 > (double)var2) {
                  if (!var3.contains(var6)) {
                     this.A(var1, var6, Material.AIR);
                     var3.add(var6);
                  }
               } else if (var3.contains(var6)) {
                  this.A(var1, var6, Material.SPAWNER);
                  var3.remove(var6);
               }
            }
         }

      }
   }

   private void A(Player var1, Location var2, Material var3) {
      try {
         PacketContainer var4 = this.A.createPacket(Server.BLOCK_CHANGE);
         var4.getBlockPositionModifier().write(0, new BlockPosition(var2.getBlockX(), var2.getBlockY(), var2.getBlockZ()));
         var4.getBlockData().write(0, WrappedBlockData.createData(var3));
         this.A.sendServerPacket(var1, var4);
         if (var3 == Material.SPAWNER) {
            Bukkit.getScheduler().runTaskLater(this.E, () -> {
               if (var1.isOnline()) {
                  this.A(var1, var2);
               }

            }, 1L);
         }
      } catch (Exception var5) {
         this.E.getLogger().warning("[DonutSpawners] ESP packet error: " + var5.getMessage());
      }

   }

   private void A(Player var1, Location var2) {
      try {
         Block var3 = var2.getBlock();
         BlockState var5 = var3.getState();
         if (!(var5 instanceof CreatureSpawner)) {
            return;
         }

         CreatureSpawner var4 = (CreatureSpawner)var5;
         EntityType var11 = var4.getSpawnedType();
         if (var11 == null) {
            return;
         }

         NbtCompound var6 = NbtFactory.ofCompound("");
         var6.put("id", "minecraft:mob_spawner");
         var6.put("x", var2.getBlockX());
         var6.put("y", var2.getBlockY());
         var6.put("z", var2.getBlockZ());
         NbtCompound var7 = NbtFactory.ofCompound("SpawnData");
         NbtCompound var8 = NbtFactory.ofCompound("entity");
         var8.put("id", "minecraft:" + var11.getKey().getKey());
         var7.put("entity", var8);
         var6.put("SpawnData", var7);
         PacketContainer var9 = this.A.createPacket(Server.TILE_ENTITY_DATA);
         var9.getBlockPositionModifier().write(0, new BlockPosition(var2.getBlockX(), var2.getBlockY(), var2.getBlockZ()));
         var9.getIntegers().write(0, 1);
         var9.getNbtModifier().write(0, var6);
         this.A.sendServerPacket(var1, var9);
      } catch (Exception var10) {
         this.E.getLogger().log(Level.WARNING, "[DonutSpawners] ESP tile entity packet error", var10);
      }

   }

   private void A(Player var1) {
      Set var2 = (Set)this.I.remove(var1.getUniqueId());
      if (var2 != null) {
         for(Location var4 : var2) {
            this.A(var1, var4, Material.SPAWNER);
         }

      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent var1) {
      if (this.A("ANTI_ESP_ENABLED", false) && this.A != null) {
         Location var2 = var1.getFrom();
         Location var3 = var1.getTo();
         if (var3 != null) {
            if (var2.getBlockX() != var3.getBlockX() || var2.getBlockY() != var3.getBlockY() || var2.getBlockZ() != var3.getBlockZ()) {
               this.A(var1.getPlayer(), this.A((String)"ANTI_ESP_RADIUS", 16));
            }
         }
      }
   }

   private ItemStack A(_H var1, int var2) {
      ItemStack var3 = new ItemStack(Material.SPAWNER, var2);
      ItemMeta var4 = var3.getItemMeta();
      String var5 = this.A("SPAWNER_ITEM.NAME_TEMPLATE", "&dSpawner");
      var4.setDisplayName(colorize(var5));
      ArrayList var6 = new ArrayList();

      for(String var8 : this.A("SPAWNER_ITEM.LORE")) {
         var6.add(colorize(var8.replace("{TYPE_DISPLAY}", var1.A()).replace("{TYPE}", var1.name())));
      }

      if (var6.isEmpty()) {
         var6.add(colorize("&7Type: &e" + var1.A()));
      }

      var4.setLore(var6);
      var3.setItemMeta(var4);
      return var3;
   }

   private _H A(ItemStack var1) {
      if (var1 != null && var1.getType() == Material.SPAWNER && var1.hasItemMeta()) {
         ItemMeta var2 = var1.getItemMeta();
         if (!var2.hasLore()) {
            return null;
         } else {
            for(String var4 : var2.getLore()) {
               String var5 = org.bukkit.ChatColor.stripColor(var4);
               if (var5 != null && var5.startsWith("Type: ")) {
                  return A._H.A(var5.substring(6).trim());
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private void C() {
      try {
         CommandMap var1 = Bukkit.getCommandMap();
         Command var2 = new Command("givespawner") {
            public boolean execute(CommandSender var1, String var2, String[] var3) {
               return A.this.B.A(var1, this, var2, var3);
            }

            public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
               return A.this.B.A(var1, var3);
            }
         };
         var2.setDescription("Give a spawner item");
         var2.setUsage("/givespawner <player> <type> [amount]");
         var2.setPermission("economysmpcore.spawners.admin");
         var1.register(this.E.getDescription().getName().toLowerCase(), var2);
      } catch (Exception var3) {
         this.E.getLogger().warning("Failed to register /givespawner: " + var3.getMessage());
      }

   }

   private void A() {
      try {
         CommandMap var1 = Bukkit.getCommandMap();
         Command var2 = new Command("spawnerreload") {
            public boolean execute(CommandSender var1, String var2, String[] var3) {
               if (!var1.hasPermission("economysmpcore.spawners.reload")) {
                  var1.sendMessage(A.this.D("NO_PERMISSION"));
                  return true;
               } else {
                  A.this.reloadConfig();
                  var1.sendMessage(A.this.D("CONFIG_RELOADED"));
                  return true;
               }
            }

            public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
               return Collections.emptyList();
            }
         };
         var2.setDescription("Reload spawners config");
         var2.setUsage("/spawnerreload");
         var2.setPermission("economysmpcore.spawners.reload");
         var1.register(this.E.getDescription().getName().toLowerCase(), var2);
      } catch (Exception var3) {
         this.E.getLogger().warning("Failed to register /spawnerreload: " + var3.getMessage());
      }

   }

   @EventHandler
   public void onSpawnerAdminClick(InventoryClickEvent var1) {
      InventoryHolder var3 = var1.getInventory().getHolder();
      if (var3 instanceof _I var2) {
         var1.setCancelled(true);
         HumanEntity var4 = var1.getWhoClicked();
         if (var4 instanceof Player var16) {
            int var17 = var1.getRawSlot();
            int var5 = var2.getPage();
            int var6 = this.A((String)"SPAWNER_ADMIN_GUI.PREV_BUTTON.SLOT", 45);
            int var7 = this.A((String)"SPAWNER_ADMIN_GUI.NEXT_BUTTON.SLOT", 53);
            int var8 = this.A((String)"SPAWNER_ADMIN_GUI.REFRESH_BUTTON.SLOT", 49);
            if (var17 == var6) {
               (new _G(var5 - 1)).A(var16);
            } else if (var17 == var7) {
               (new _G(var5 + 1)).A(var16);
            } else if (var17 == var8) {
               (new _G(var5)).A(var16);
            } else if (var17 >= 0 && var17 < 45) {
               ItemStack var9 = var1.getCurrentItem();
               Material var10 = this.A(this.A("SPAWNER_ADMIN_GUI.ITEM.MATERIAL", "SPAWNER"), Material.SPAWNER);
               if (var9 != null && var9.getType() == var10 && var9.hasItemMeta()) {
                  ItemMeta var11 = var9.getItemMeta();
                  if (var11 != null && var11.hasLore()) {
                     ArrayList var12 = new ArrayList(this.C.B().values());
                     var12.sort(Comparator.comparing((var0) -> var0.D().getWorld() == null ? "" : var0.D().getWorld().getName()).thenComparingInt((var0) -> var0.D().getBlockX()));
                     int var13 = var5 * 45 + var17;
                     if (var13 >= 0 && var13 < var12.size()) {
                        _C var14 = (_C)var12.get(var13);
                        Location var15 = var14.D();
                        if (var15.getWorld() != null) {
                           var16.closeInventory();
                           var16.teleport(var15.clone().add((double)0.5F, (double)1.0F, (double)0.5F));
                           String var10001 = var14.C().A();
                           var16.sendMessage(colorize("&aTeleported to &e" + var10001 + " &aspawner at &e" + var15.getBlockX() + ", " + var15.getBlockY() + ", " + var15.getBlockZ() + "&a."));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onSpawnerPlace(BlockPlaceEvent var1) {
      if (!var1.isCancelled()) {
         ItemStack var2 = var1.getItemInHand();
         if (var2.getType() == Material.SPAWNER) {
            _H var3 = this.A(var2);
            if (var3 != null) {
               Location var4 = var1.getBlock().getLocation();
               this.C.E(new _C(var4, var1.getPlayer().getUniqueId(), var3, 1));
               BlockState var6 = var1.getBlock().getState();
               if (var6 instanceof CreatureSpawner) {
                  CreatureSpawner var5 = (CreatureSpawner)var6;
                  var5.setSpawnedType(var3.D());
                  var5.update();
               }

               if (this.A("ANTI_ESP_ENABLED", false)) {
                  for(Player var9 : Bukkit.getOnlinePlayers()) {
                     Set var7 = (Set)this.I.get(var9.getUniqueId());
                     if (var7 != null) {
                        var7.remove(var4);
                     }
                  }
               }

            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onSpawnerBreak(BlockBreakEvent var1) {
      if (!var1.isCancelled()) {
         Block var2 = var1.getBlock();
         if (var2.getType() == Material.SPAWNER) {
            Player var3 = var1.getPlayer();
            _C var4 = this.C.B(var2.getLocation());
            if (var4 != null) {
               if (this.A("settings.require_silk_touch", true) && !var3.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                  var3.sendMessage(this.D("CANNOT_BREAK_NO_SILK"));
                  var1.setCancelled(true);
                  return;
               }

               var1.setDropItems(false);
               var1.setExpToDrop(0);
               if (var3.isSneaking()) {
                  int var6 = var4.G();
                  if (var6 > 64) {
                     var2.getWorld().dropItemNaturally(var2.getLocation(), this.A((_H)var4.C(), 64));
                     var4.A(var6 - 64);
                     var1.setCancelled(true);
                  } else {
                     var2.getWorld().dropItemNaturally(var2.getLocation(), this.A(var4.C(), var6));
                     this.C.C(var2.getLocation());
                  }
               } else if (var4.G() > 1) {
                  var4.A(var4.G() - 1);
                  var2.getWorld().dropItemNaturally(var2.getLocation(), this.A((_H)var4.C(), 1));
                  var1.setCancelled(true);
               } else {
                  var2.getWorld().dropItemNaturally(var2.getLocation(), this.A((_H)var4.C(), 1));
                  this.C.C(var2.getLocation());
               }
            } else if (this.A("settings.natural_spawners_virtual", false)) {
               BlockState var8 = var2.getState();
               if (var8 instanceof CreatureSpawner) {
                  CreatureSpawner var5 = (CreatureSpawner)var8;
                  if (this.A("settings.require_silk_touch", true) && !var3.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                     var3.sendMessage(this.D("CANNOT_BREAK_NO_SILK"));
                     var1.setCancelled(true);
                     return;
                  }

                  try {
                     _H var9 = A._H.valueOf(var5.getSpawnedType().name());
                     var1.setDropItems(false);
                     var1.setExpToDrop(0);
                     var2.getWorld().dropItemNaturally(var2.getLocation(), this.A((_H)var9, 1));
                  } catch (IllegalArgumentException var7) {
                  }
               }
            }

         }
      }
   }

   @EventHandler
   public void onSpawnerInteract(PlayerInteractEvent var1) {
      if (var1.getHand() != EquipmentSlot.OFF_HAND) {
         if (var1.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block var2 = var1.getClickedBlock();
            if (var2 != null && var2.getType() == Material.SPAWNER) {
               Player var3 = var1.getPlayer();
               ItemStack var4 = var3.getInventory().getItemInMainHand();
               _C var5 = this.C.B(var2.getLocation());
               if (var4.getType() == Material.SPAWNER && var5 != null) {
                  _H var6 = this.A(var4);
                  if (var6 != null && var6 == var5.C()) {
                     var1.setCancelled(true);
                     int var13 = var3.isSneaking() ? var4.getAmount() : 1;
                     var5.A(var5.G() + var13);
                     if (var3.isSneaking()) {
                        var3.getInventory().setItemInMainHand((ItemStack)null);
                     } else if (var4.getAmount() > 1) {
                        var4.setAmount(var4.getAmount() - 1);
                     } else {
                        var3.getInventory().setItemInMainHand((ItemStack)null);
                     }

                     var3.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(this.A("STACKED_ONE", Map.of("{STACK}", String.valueOf(var5.G())))));
                     return;
                  }
               }

               if (var5 != null) {
                  var1.setCancelled(true);
                  UUID var7 = this.C.C(var5);
                  if (var7 != null && !var7.equals(var3.getUniqueId())) {
                     var3.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(this.D("BEING_VIEWED")));
                     return;
                  }

                  if (var7 == null && !this.C.B(var5, var3.getUniqueId())) {
                     return;
                  }

                  (new _F(var5, true)).A(var3);
               } else if (this.A("settings.natural_spawners_virtual", false)) {
                  BlockState var11 = var2.getState();
                  if (var11 instanceof CreatureSpawner) {
                     CreatureSpawner var10 = (CreatureSpawner)var11;

                     try {
                        _H var12 = A._H.valueOf(var10.getSpawnedType().name());
                        _C var8 = new _C(var2.getLocation(), var3.getUniqueId(), var12, 1);
                        this.C.E(var8);
                        this.C.B(var8, var3.getUniqueId());
                        (new _F(var8, true)).A(var3);
                     } catch (IllegalArgumentException var9) {
                     }
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void onInventoryOpen(InventoryOpenEvent var1) {
      InventoryHolder var3 = var1.getInventory().getHolder();
      if (var3 instanceof _K var2) {
         this.C.A(var2.getData());
         HumanEntity var4 = var1.getPlayer();
         if (var4 instanceof Player var5) {
            this.C.B(var2.getData(), var5.getUniqueId());
         }
      }

   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      InventoryHolder var3 = var1.getInventory().getHolder();
      if (var3 instanceof _K var2) {
         HumanEntity var4 = var1.getPlayer();
         if (var4 instanceof Player var5) {
            this.C.A(var2.getData(), var5.getUniqueId());
         }

         this.C.B(var2.getData());
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      this.C.B().values().forEach((var2x) -> this.C.A(var2x, var2));
      if (this.A("ANTI_ESP_ENABLED", false)) {
         this.A(var1.getPlayer());
      }

   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() instanceof _K) {
         var1.setCancelled(true);
         if (var1.getClickedInventory() == var1.getView().getTopInventory()) {
            Player var2 = (Player)var1.getWhoClicked();
            _K var3 = (_K)var1.getInventory().getHolder();
            _C var4 = var3.getData();
            int var5 = var1.getSlot();
            int var6 = var3.getPage();
            int var7 = this.A((String)"STORAGE_GUI.PREV_BUTTON.SLOT", 45);
            int var8 = this.A((String)"STORAGE_GUI.SELL_BUTTON.SLOT", 48);
            int var9 = this.A((String)"STORAGE_GUI.DROP_BUTTON.SLOT", 50);
            int var10 = this.A((String)"STORAGE_GUI.FILTER_BUTTON.SLOT", 46);
            int var11 = this.A((String)"STORAGE_GUI.PREV_BUTTON.SLOT", 45);
            int var12 = this.A((String)"STORAGE_GUI.NEXT_BUTTON.SLOT", 53);
            int var13 = this.C(var4);
            if (var5 == var11 && var6 > 1) {
               (new _F(var4, true, var6 - 1)).A(var2);
            } else if (var5 == var12 && var6 < var13) {
               (new _F(var4, true, var6 + 1)).A(var2);
            } else if (var5 == var10 && this.A("FILTER_GUI_ENABLED", false)) {
               (new _J(var4)).A(var2);
            } else if (var5 == var8) {
               this.B(var2, var4, var6);
            } else if (var5 == var9) {
               this.A(var2, var4, var6);
            } else {
               if (var5 < 45) {
                  ItemStack var14 = var1.getCurrentItem();
                  if (var14 != null && var14.getType() != Material.AIR) {
                     HashMap var15 = var2.getInventory().addItem(new ItemStack[]{var14.clone()});
                     int var16 = var14.getAmount() - (var15.isEmpty() ? 0 : ((ItemStack)var15.values().iterator().next()).getAmount());
                     if (var16 > 0) {
                        long var17 = (Long)var4.F().getOrDefault(var14.getType(), 0L);
                        if (var17 <= (long)var16) {
                           var4.F().remove(var14.getType());
                        } else {
                           var4.F().put(var14.getType(), var17 - (long)var16);
                        }

                        (new _F(var4, true, var6)).A(var2);
                     }
                  }
               }

            }
         }
      }
   }

   private void B(Player var1, _C var2, int var3) {
      long var4 = var2.F().values().stream().mapToLong(Long::longValue).sum();
      if (var4 <= 0L) {
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(this.D("NOTHING_TO_SELL")));
      } else {
         String var6 = colorize(this.A("CONFIRM_SELL_GUI.TITLE", "&8ᴄᴏɴꜰɪʀᴍ ѕᴇʟʟ"));
         Inventory var7 = Bukkit.createInventory((InventoryHolder)null, 27, var6);
         int var8 = this.A((String)"CONFIRM_SELL_GUI.CANCEL_BUTTON.SLOT", 11);
         ItemStack var9 = this.A(this.A("CONFIRM_SELL_GUI.CANCEL_BUTTON.MATERIAL", "RED_STAINED_GLASS_PANE"), Material.RED_STAINED_GLASS_PANE, this.A("CONFIRM_SELL_GUI.CANCEL_BUTTON.NAME", "&cᴄᴀɴᴄᴇʟ"), this.A("CONFIRM_SELL_GUI.CANCEL_BUTTON.LORE"), (String)null, var2, 0);
         var7.setItem(var8, var9);
         int var10 = this.A((String)"CONFIRM_SELL_GUI.INFO_BUTTON.SLOT", 13);
         String var11 = this.A("CONFIRM_SELL_GUI.INFO_BUTTON.MATERIAL", "PLAYER_HEAD");
         Material var12 = "PLAYER_HEAD".equalsIgnoreCase(var11) ? Material.PLAYER_HEAD : this.A(var11, var2.C().C());
         ItemStack var13 = new ItemStack(var12);
         ItemMeta var14 = var13.getItemMeta();
         var14.setDisplayName(colorize(this.A("CONFIRM_SELL_GUI.INFO_BUTTON.NAME", "&#6BF18Dʟᴏᴏᴛ")));
         String var15 = this.A("CONFIRM_SELL_GUI.INFO_BUTTON.DROPS_LINE_TEMPLATE", "&#6BF18D{AMOUNT} &f{ITEM}");
         ArrayList var16 = new ArrayList(this.A(this.A("CONFIRM_SELL_GUI.INFO_BUTTON.LORE")));
         var2.F().entrySet().stream().sorted(Entry.comparingByValue(Comparator.reverseOrder())).forEach((var3x) -> var16.add(colorize(var15.replace("{AMOUNT}", this.B((Long)var3x.getValue())).replace("{ITEM}", this.B(((Material)var3x.getKey()).name())))));
         var14.setLore(var16);
         var13.setItemMeta(var14);
         var7.setItem(var10, var13);
         double var17 = this.J.A(var2.F());
         int var19 = this.A((String)"CONFIRM_SELL_GUI.CONFIRM_BUTTON.SLOT", 15);
         ArrayList var20 = new ArrayList();

         for(String var22 : this.A("CONFIRM_SELL_GUI.CONFIRM_BUTTON.LORE")) {
            var20.add(colorize(var22.replace("{TOTAL_MONEY}", "$" + (new DecimalFormat("#,##0.00")).format(var17))));
         }

         ItemStack var23 = new ItemStack(this.A(this.A("CONFIRM_SELL_GUI.CONFIRM_BUTTON.MATERIAL", "LIME_STAINED_GLASS_PANE"), Material.LIME_STAINED_GLASS_PANE));
         ItemMeta var24 = var23.getItemMeta();
         var24.setDisplayName(colorize(this.A("CONFIRM_SELL_GUI.CONFIRM_BUTTON.NAME", "&aᴄᴏɴꜰɪʀᴍ")));
         var24.setLore(var20);
         var23.setItemMeta(var24);
         var7.setItem(var19, var23);
         var1.setMetadata("spawner_confirm_page", new FixedMetadataValue(this.E, var3));
         var1.setMetadata("spawner_confirm_data", new FixedMetadataValue(this.E, this.A(var2.D())));
         var1.openInventory(var7);
      }
   }

   @EventHandler
   public void onConfirmClick(InventoryClickEvent var1) {
      String var2 = colorize(this.A("CONFIRM_SELL_GUI.TITLE", "&8ᴄᴏɴꜰɪʀᴍ ѕᴇʟʟ"));
      if (var1.getView().getTitle().equals(var2)) {
         var1.setCancelled(true);
         HumanEntity var4 = var1.getWhoClicked();
         if (var4 instanceof Player) {
            Player var3 = (Player)var4;
            int var7 = this.A((String)"CONFIRM_SELL_GUI.CANCEL_BUTTON.SLOT", 11);
            int var5 = this.A((String)"CONFIRM_SELL_GUI.CONFIRM_BUTTON.SLOT", 15);
            int var6 = var1.getSlot();
            if (var6 == var7) {
               this.A(var3, false);
            } else if (var6 == var5) {
               this.A(var3, true);
            }

         }
      }
   }

   private void A(Player var1, boolean var2) {
      if (var1.hasMetadata("spawner_confirm_page") && var1.hasMetadata("spawner_confirm_data")) {
         int var3 = ((MetadataValue)var1.getMetadata("spawner_confirm_page").get(0)).asInt();
         String var4 = ((MetadataValue)var1.getMetadata("spawner_confirm_data").get(0)).asString();
         var1.removeMetadata("spawner_confirm_page", this.E);
         var1.removeMetadata("spawner_confirm_data", this.E);
         Location var5 = this.C(var4);
         if (var5 == null) {
            var1.closeInventory();
         } else {
            _C var6 = this.C.B(var5);
            if (var6 == null) {
               var1.closeInventory();
            } else {
               if (var2 && !var6.F().isEmpty()) {
                  double var7 = this.J.A(var1, var6.F());
                  var6.F().clear();
                  var1.playSound(var1.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                  var1.sendMessage(this.A("SOLD_ALL", Map.of("{MONEY}", "$" + (new DecimalFormat("#,##0.00")).format(var7))));
               }

               (new _F(var6, true, var3)).A(var1);
            }
         }
      } else {
         var1.closeInventory();
      }
   }

   private void A(Player var1, _C var2, int var3) {
      int var4 = (var3 - 1) * 45;
      int var5 = 0;
      HashMap var6 = new HashMap();

      for(Map.Entry var8 : (new HashMap(var2.F())).entrySet()) {
         for(long var9 = (Long)var8.getValue(); var9 > 0L && var5 < var4 + 45; ++var5) {
            if (var5 >= var4) {
               int var11 = (int)Math.min(var9, 64L);
               Location var12 = var1.getEyeLocation();
               var1.getWorld().dropItem(var12.clone().add(var12.getDirection().normalize().multiply((double)0.5F)), new ItemStack((Material)var8.getKey(), var11)).setVelocity(var12.getDirection().normalize().multiply(0.35));
               var6.merge((Material)var8.getKey(), (long)var11, Long::sum);
            }

            var9 -= 64L;
         }

         if (var5 >= var4 + 45) {
            break;
         }
      }

      for(Map.Entry var14 : var6.entrySet()) {
         long var15 = (Long)var2.F().getOrDefault(var14.getKey(), 0L);
         if (var15 <= (Long)var14.getValue()) {
            var2.F().remove(var14.getKey());
         } else {
            var2.F().put((Material)var14.getKey(), var15 - (Long)var14.getValue());
         }
      }

      var1.sendMessage(this.D("DROPPED_PAGE"));
      (new _F(var2, true, var3)).A(var1);
   }

   @EventHandler
   public void onFilterClick(InventoryClickEvent var1) {
      String var2 = colorize(this.A("FILTER_GUI.TITLE", "&8FILTER"));
      if (var1.getView().getTitle().equals(var2)) {
         var1.setCancelled(true);
         HumanEntity var4 = var1.getWhoClicked();
         if (var4 instanceof Player) {
            Player var3 = (Player)var4;
            int var10 = var1.getSlot();
            if (var3.hasMetadata("filter_gui_data")) {
               Location var5 = this.C(((MetadataValue)var3.getMetadata("filter_gui_data").get(0)).asString());
               if (var5 != null) {
                  _C var6 = this.C.B(var5);
                  if (var6 != null) {
                     int var7 = this.A((String)"FILTER_GUI.BACK_BUTTON.SLOT", 22);
                     if (var10 == var7) {
                        (new _F(var6, true)).A(var3);
                        var3.removeMetadata("filter_gui_data", this.E);
                     } else {
                        ItemStack var8 = var1.getCurrentItem();
                        if (var8 != null && var8.getType() != Material.AIR) {
                           Material var9 = var8.getType();
                           if (var6.B().contains(var9)) {
                              var6.B().remove(var9);
                           } else {
                              var6.B().add(var9);
                           }

                           (new _J(var6)).A(var3);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private int C(_C var1) {
      int var2 = var1.F().values().stream().mapToLong((var0) -> (var0 + 63L) / 64L).mapToInt((var0) -> (int)var0).sum();
      return Math.max(1, (int)Math.ceil((double)var2 / (double)45.0F));
   }

   private ItemStack A(String var1, Material var2, String var3, List<String> var4, String var5, _C var6, int var7) {
      Material var8 = this.A(var1, var2);
      ArrayList var9 = new ArrayList();
      if (var4 != null) {
         for(String var11 : var4) {
            var9.add(colorize(var11));
         }
      }

      return this.A((Material)var8, (String)var3, (List)var9);
   }

   private ItemStack A(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(colorize(var2));
      var5.setLore(var3);
      var4.setItemMeta(var5);
      return var4;
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onLavaFlow(BlockFromToEvent var1) {
      Block var2 = var1.getToBlock();
      if (var2.getType() == Material.SPAWNER && this.C.B(var2.getLocation()) != null) {
         var1.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onSpawnerBurn(BlockBurnEvent var1) {
      Block var2 = var1.getBlock();
      if (var2.getType() == Material.SPAWNER && this.C.B(var2.getLocation()) != null) {
         var1.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onSpawnerItemDamage(EntityDamageEvent var1) {
      Entity var3 = var1.getEntity();
      if (var3 instanceof Item var2) {
         if (var2.getItemStack().getType() == Material.SPAWNER && var2.getItemStack().hasItemMeta() && var2.getItemStack().getItemMeta().hasDisplayName()) {
            var1.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onEntityExplode(EntityExplodeEvent var1) {
      var1.blockList().removeIf((var1x) -> var1x.getType() == Material.SPAWNER && this.C.B(var1x.getLocation()) != null);
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBlockExplode(BlockExplodeEvent var1) {
      var1.blockList().removeIf((var1x) -> var1x.getType() == Material.SPAWNER && this.C.B(var1x.getLocation()) != null);
   }

   @EventHandler
   public void onSpawnerSpawn(SpawnerSpawnEvent var1) {
      if (var1.getSpawner() != null && this.C.B(var1.getSpawner().getLocation()) != null) {
         var1.setCancelled(true);
      }

   }

   private String A(Location var1) {
      String var10000 = var1.getWorld().getName();
      return var10000 + "," + var1.getBlockX() + "," + var1.getBlockY() + "," + var1.getBlockZ();
   }

   private Location C(String var1) {
      try {
         String[] var2 = var1.split(",");
         World var3 = Bukkit.getWorld(var2[0]);
         if (var3 != null) {
            return new Location(var3, (double)Integer.parseInt(var2[1]), (double)Integer.parseInt(var2[2]), (double)Integer.parseInt(var2[3]));
         }
      } catch (Exception var4) {
      }

      return null;
   }

   private String B(long var1) {
      if (var1 >= 1000000L) {
         return String.format("%.1fM", (double)var1 / (double)1000000.0F);
      } else {
         return var1 >= 1000L ? String.format("%.0fK", (double)var1 / (double)1000.0F) : String.valueOf(var1);
      }
   }

   private String B(String var1) {
      StringBuilder var2 = new StringBuilder();

      for(String var6 : var1.toLowerCase().split("_")) {
         if (!var6.isEmpty()) {
            var2.append(Character.toUpperCase(var6.charAt(0))).append(var6.substring(1)).append(" ");
         }
      }

      return var2.toString().trim();
   }

   private List<Material> A(_H var1) {
      ArrayList var2 = new ArrayList();
      switch (var1.ordinal()) {
         case 0:
            var2.add(Material.BONE);
            var2.add(Material.ARROW);
            break;
         case 1:
            var2.add(Material.ROTTEN_FLESH);
            break;
         case 2:
            var2.add(Material.STRING);
            var2.add(Material.SPIDER_EYE);
            break;
         case 3:
            var2.add(Material.GUNPOWDER);
            break;
         case 4:
            var2.add(Material.ENDER_PEARL);
            break;
         case 5:
            var2.add(Material.BLAZE_ROD);
            break;
         case 6:
            var2.add(Material.GHAST_TEAR);
            break;
         case 7:
            var2.add(Material.STICK);
            var2.add(Material.GLASS_BOTTLE);
            break;
         case 8:
            var2.add(Material.GOLD_INGOT);
            break;
         case 9:
            var2.add(Material.GOLDEN_SWORD);
            break;
         case 10:
            var2.add(Material.COOKED_PORKCHOP);
            break;
         case 11:
            var2.add(Material.ROTTEN_FLESH);
            break;
         case 12:
            var2.add(Material.STRING);
            break;
         case 13:
            var2.add(Material.MAGMA_CREAM);
            break;
         case 14:
            var2.add(Material.SLIME_BALL);
            break;
         case 15:
            var2.add(Material.PHANTOM_MEMBRANE);
            break;
         case 16:
            var2.add(Material.ROTTEN_FLESH);
            var2.add(Material.TRIDENT);
            break;
         case 17:
            var2.add(Material.ROTTEN_FLESH);
            break;
         case 18:
            var2.add(Material.BONE);
            var2.add(Material.ARROW);
            break;
         case 19:
            var2.add(Material.BONE);
            var2.add(Material.COAL);
            break;
         case 20:
            var2.add(Material.ARROW);
            break;
         case 21:
            var2.add(Material.EMERALD);
            break;
         case 22:
            var2.add(Material.TOTEM_OF_UNDYING);
            break;
         case 23:
            var2.add(Material.SADDLE);
         case 24:
         case 27:
         case 28:
         case 33:
         case 34:
         case 44:
         case 52:
         case 54:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         default:
            break;
         case 25:
            var2.add(Material.PRISMARINE_SHARD);
            break;
         case 26:
            var2.add(Material.PRISMARINE_CRYSTALS);
            break;
         case 29:
            var2.add(Material.STRING);
            var2.add(Material.SPIDER_EYE);
            break;
         case 30:
            var2.add(Material.SHULKER_SHELL);
            break;
         case 31:
            var2.add(Material.IRON_INGOT);
            break;
         case 32:
            var2.add(Material.SNOWBALL);
            break;
         case 35:
            var2.add(Material.STRING);
            break;
         case 36:
         case 37:
         case 38:
         case 41:
         case 42:
            var2.add(Material.LEATHER);
            break;
         case 39:
            var2.add(Material.BONE);
            break;
         case 40:
            var2.add(Material.ROTTEN_FLESH);
            break;
         case 43:
            var2.add(Material.FEATHER);
            break;
         case 45:
            var2.add(Material.COD);
            break;
         case 46:
            var2.add(Material.SALMON);
            break;
         case 47:
            var2.add(Material.PUFFERFISH);
            break;
         case 48:
            var2.add(Material.TROPICAL_FISH);
            break;
         case 49:
            var2.add(Material.INK_SAC);
            break;
         case 50:
            var2.add(Material.GLOW_INK_SAC);
            break;
         case 51:
            var2.add(Material.TURTLE_SCUTE);
            break;
         case 53:
            var2.add(Material.BAMBOO);
            break;
         case 55:
            var2.add(Material.HONEYCOMB);
            break;
         case 56:
            var2.add(Material.FEATHER);
            var2.add(Material.CHICKEN);
            break;
         case 57:
            var2.add(Material.LEATHER);
            var2.add(Material.BEEF);
            break;
         case 58:
            var2.add(Material.PORKCHOP);
            break;
         case 59:
            var2.add(Material.WHITE_WOOL);
            var2.add(Material.MUTTON);
            break;
         case 60:
            var2.add(Material.RABBIT);
            var2.add(Material.RABBIT_FOOT);
            break;
         case 67:
            var2.add(Material.SCULK);
      }

      return var2;
   }

   public void disable() {
      this.C.C();
      if (this.D != null) {
         this.D.cancel();
      }

      if (this.K != null) {
         this.K.cancel();
      }

      if (this.H != null) {
         this.H.cancel();
      }

      for(Player var2 : Bukkit.getOnlinePlayers()) {
         this.A(var2);
      }

   }

   private class _A extends BukkitRunnable {
      public void run() {
         for(_C var2 : A.this.C.B().values()) {
            double var3 = A.this.A("multipliers." + var2.C().name(), (double)1.0F);
            if (A.this.C.A(var2.D())) {
               var3 += A.this.A("isolated_bonus", (double)0.5F);
            }

            int var5 = (int)((double)var2.G() * var3);
            if (var5 > 0) {
               var2.A(this.A(var2.C(), var5));
               var2.A((long)var5 * A.this.A("xp.amount_per_cycle", 5L));
            }
         }

      }

      private Map<Material, Long> A(_H var1, int var2) {
         HashMap var3 = new HashMap();
         switch (var1.ordinal()) {
            case 0:
               var3.put(Material.BONE, (long)var2);
               var3.put(Material.ARROW, (long)var2);
               break;
            case 1:
               var3.put(Material.ROTTEN_FLESH, (long)var2);
               break;
            case 2:
               var3.put(Material.STRING, (long)var2);
               var3.put(Material.SPIDER_EYE, (long)(var2 / 2));
               break;
            case 3:
               var3.put(Material.GUNPOWDER, (long)var2);
               break;
            case 4:
               var3.put(Material.ENDER_PEARL, (long)var2);
               break;
            case 5:
               var3.put(Material.BLAZE_ROD, (long)var2);
               break;
            case 6:
               var3.put(Material.GHAST_TEAR, (long)var2);
               break;
            case 7:
               var3.put(Material.STICK, (long)var2);
               var3.put(Material.GLASS_BOTTLE, (long)(var2 / 2));
               break;
            case 8:
               var3.put(Material.GOLD_INGOT, (long)var2);
               break;
            case 9:
               var3.put(Material.GOLDEN_SWORD, (long)var2);
               break;
            case 10:
               var3.put(Material.COOKED_PORKCHOP, (long)var2);
               break;
            case 11:
               var3.put(Material.ROTTEN_FLESH, (long)var2);
               break;
            case 12:
               var3.put(Material.STRING, (long)var2);
               break;
            case 13:
               var3.put(Material.MAGMA_CREAM, (long)var2);
               break;
            case 14:
               var3.put(Material.SLIME_BALL, (long)var2);
               break;
            case 15:
               var3.put(Material.PHANTOM_MEMBRANE, (long)var2);
               break;
            case 16:
               var3.put(Material.ROTTEN_FLESH, (long)var2);
               var3.put(Material.TRIDENT, (long)(var2 / 10));
               break;
            case 17:
               var3.put(Material.ROTTEN_FLESH, (long)var2);
               break;
            case 18:
               var3.put(Material.BONE, (long)var2);
               var3.put(Material.ARROW, (long)var2);
               break;
            case 19:
               var3.put(Material.BONE, (long)var2);
               var3.put(Material.COAL, (long)var2);
               break;
            case 20:
               var3.put(Material.ARROW, (long)var2);
               break;
            case 21:
               var3.put(Material.EMERALD, (long)var2);
               break;
            case 22:
               var3.put(Material.TOTEM_OF_UNDYING, (long)(var2 / 10));
               break;
            case 23:
               var3.put(Material.SADDLE, (long)var2);
            case 24:
            case 27:
            case 28:
            case 33:
            case 34:
            case 44:
            case 52:
            case 54:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            default:
               break;
            case 25:
               var3.put(Material.PRISMARINE_SHARD, (long)var2);
               break;
            case 26:
               var3.put(Material.PRISMARINE_CRYSTALS, (long)var2);
               break;
            case 29:
               var3.put(Material.STRING, (long)var2);
               var3.put(Material.SPIDER_EYE, (long)(var2 / 2));
               break;
            case 30:
               var3.put(Material.SHULKER_SHELL, (long)var2);
               break;
            case 31:
               var3.put(Material.IRON_INGOT, (long)var2);
               break;
            case 32:
               var3.put(Material.SNOWBALL, (long)var2);
               break;
            case 35:
               var3.put(Material.STRING, (long)var2);
               break;
            case 36:
            case 37:
            case 38:
            case 41:
            case 42:
               var3.put(Material.LEATHER, (long)var2);
               break;
            case 39:
               var3.put(Material.BONE, (long)var2);
               break;
            case 40:
               var3.put(Material.ROTTEN_FLESH, (long)var2);
               break;
            case 43:
               var3.put(Material.FEATHER, (long)var2);
               break;
            case 45:
               var3.put(Material.COD, (long)var2);
               break;
            case 46:
               var3.put(Material.SALMON, (long)var2);
               break;
            case 47:
               var3.put(Material.PUFFERFISH, (long)var2);
               break;
            case 48:
               var3.put(Material.TROPICAL_FISH, (long)var2);
               break;
            case 49:
               var3.put(Material.INK_SAC, (long)var2);
               break;
            case 50:
               var3.put(Material.GLOW_INK_SAC, (long)var2);
               break;
            case 51:
               var3.put(Material.TURTLE_SCUTE, (long)var2);
               break;
            case 53:
               var3.put(Material.BAMBOO, (long)var2);
               break;
            case 55:
               var3.put(Material.HONEYCOMB, (long)var2);
               break;
            case 56:
               var3.put(Material.FEATHER, (long)var2);
               var3.put(Material.CHICKEN, (long)var2);
               break;
            case 57:
               var3.put(Material.LEATHER, (long)var2);
               var3.put(Material.BEEF, (long)var2);
               break;
            case 58:
               var3.put(Material.PORKCHOP, (long)var2);
               break;
            case 59:
               var3.put(Material.WHITE_WOOL, (long)var2);
               var3.put(Material.MUTTON, (long)var2);
               break;
            case 60:
               var3.put(Material.RABBIT, (long)var2);
               var3.put(Material.RABBIT_FOOT, (long)(var2 / 10));
               break;
            case 67:
               var3.put(Material.SCULK, (long)var2);
         }

         return var3;
      }
   }

   private class _B {
      double A(Material var1) {
         return A.this.A("prices." + var1.name(), (double)0.0F);
      }

      void A(Player var1, double var2) {
         if (!(var2 <= (double)0.0F)) {
            PlayerData var4 = A.this.E.getPlayerDataManager().get(var1.getUniqueId());
            var4.addMoney(var2);
            A.this.E.getPlayerDataManager().savePlayer(var1.getUniqueId());
         }
      }

      double A(Player var1, Map<Material, Long> var2) {
         double var3 = (double)0.0F;

         for(Map.Entry var6 : var2.entrySet()) {
            var3 += this.A((Material)var6.getKey()) * (double)(Long)var6.getValue();
         }

         if (var3 > (double)0.0F) {
            this.A(var1, var3);
         }

         return var3;
      }

      double A(Map<Material, Long> var1) {
         double var2 = (double)0.0F;

         for(Map.Entry var5 : var1.entrySet()) {
            var2 += this.A((Material)var5.getKey()) * (double)(Long)var5.getValue();
         }

         return var2;
      }
   }

   public static class _C {
      private Location B;
      private UUID A;
      private _H E;
      private int D;
      private Map<Material, Long> F;
      private long C;
      private Set<Material> G;

      public _C(Location var1, UUID var2, _H var3) {
         this(var1, var2, var3, 1);
      }

      public _C(Location var1, UUID var2, _H var3, int var4) {
         this.B = var1;
         this.A = var2;
         this.E = var3;
         this.D = var4;
         this.F = new ConcurrentHashMap();
         this.G = ConcurrentHashMap.newKeySet();
      }

      public Location D() {
         return this.B;
      }

      public void A(Location var1) {
         this.B = var1;
      }

      public UUID A() {
         return this.A;
      }

      public void A(UUID var1) {
         this.A = var1;
      }

      public _H C() {
         return this.E;
      }

      public void A(_H var1) {
         this.E = var1;
      }

      public int G() {
         return this.D;
      }

      public void A(int var1) {
         this.D = var1;
      }

      public Map<Material, Long> F() {
         return this.F;
      }

      public void B(Map<Material, Long> var1) {
         this.F = new ConcurrentHashMap(var1);
      }

      public long E() {
         return this.C;
      }

      public void B(long var1) {
         this.C = var1;
      }

      public Set<Material> B() {
         return this.G;
      }

      public void A(Set<Material> var1) {
         this.G = ConcurrentHashMap.newKeySet();
         this.G.addAll(var1);
      }

      public void A(Map<Material, Long> var1) {
         for(Map.Entry var3 : var1.entrySet()) {
            if (!this.G.contains(var3.getKey())) {
               this.F.merge((Material)var3.getKey(), (Long)var3.getValue(), Long::sum);
            }
         }

      }

      public void A(long var1) {
         this.C += var1;
      }
   }

   private class _D extends BukkitRunnable {
      private final int A;
      private final boolean B;

      _D(int var2) {
         this.A = var2;
         boolean var3 = false;

         try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            var3 = true;
         } catch (Exception var5) {
         }

         this.B = var3;
      }

      public void run() {
         if (A.this.A("hopper.enabled", true)) {
            for(_C var2 : A.this.C.B().values()) {
               Location var3 = var2.D();
               if (var3 != null && var3.getWorld() != null) {
                  if (this.B) {
                     this.A(var3, var2);
                  } else {
                     this.B(var2, var3);
                  }
               }
            }

         }
      }

      private void A(Location var1, _C var2) {
         try {
            Object var3 = Bukkit.getServer().getClass().getMethod("getRegionScheduler").invoke(Bukkit.getServer());
            Method var4 = var3.getClass().getMethod("execute", Plugin.class, Location.class, Runnable.class);
            var4.invoke(var3, A.this.E, var1, (Runnable)() -> this.B(var2, var1));
         } catch (Exception var5) {
            A.this.E.getLogger().severe("Hopper region fail: " + var5.getMessage());
         }

      }

      private void B(_C var1, Location var2) {
         if (!A.this.C.D(var1)) {
            Block var3 = var2.getBlock().getRelative(0, -1, 0);
            if (var3.getType() == Material.HOPPER && var3.getState() instanceof Hopper) {
               Inventory var4 = ((Hopper)var3.getState()).getInventory();
               AtomicInteger var5 = new AtomicInteger(0);
               Iterator var6 = var1.F().entrySet().iterator();

               while(var6.hasNext() && var5.get() < this.A) {
                  Map.Entry var7 = (Map.Entry)var6.next();
                  long var8 = (Long)var7.getValue();
                  if (var8 <= 0L) {
                     var6.remove();
                  } else {
                     int var10 = (int)Math.min(64L, var8);
                     HashMap var11 = var4.addItem(new ItemStack[]{new ItemStack((Material)var7.getKey(), var10)});
                     int var12 = var10 - (var11.isEmpty() ? 0 : ((ItemStack)var11.values().iterator().next()).getAmount());
                     if (var12 <= 0) {
                        var5.set(this.A);
                        break;
                     }

                     var8 -= (long)var12;
                     var5.incrementAndGet();
                     if (var8 <= 0L) {
                        var6.remove();
                     } else {
                        var7.setValue(var8);
                     }
                  }
               }

            }
         }
      }
   }

   private class _E {
      private final Map<Location, _C> E = new ConcurrentHashMap();
      private final Map<Location, Boolean> B = new ConcurrentHashMap();
      private final Map<_C, UUID> F = new ConcurrentHashMap();
      private final File C;
      private YamlConfiguration D;

      _E() {
         this.C = new File(A.this.E.getDataFolder(), "spawners/spawners.yml");
         this.A();
      }

      public Map<Location, _C> B() {
         return this.E;
      }

      public _C B(Location var1) {
         return (_C)this.E.get(var1);
      }

      public void E(_C var1) {
         this.E.put(var1.D(), var1);
         this.C();
      }

      public void C(Location var1) {
         this.E.remove(var1);
         this.B.remove(var1);
         this.C();
      }

      public void A(_C var1) {
         if (var1 != null) {
            this.B.put(var1.D(), true);
         }

      }

      public void B(_C var1) {
         if (var1 != null) {
            this.B.remove(var1.D());
         }

      }

      public boolean D(_C var1) {
         return var1 != null && (Boolean)this.B.getOrDefault(var1.D(), false);
      }

      public UUID C(_C var1) {
         return (UUID)this.F.get(var1);
      }

      public boolean B(_C var1, UUID var2) {
         if (this.F.containsKey(var1) && !((UUID)this.F.get(var1)).equals(var2)) {
            return false;
         } else {
            this.F.put(var1, var2);
            return true;
         }
      }

      public void A(_C var1, UUID var2) {
         if (var2.equals(this.F.get(var1))) {
            this.F.remove(var1);
         }

      }

      public boolean A(Location var1) {
         int var2 = A.this.A((String)"settings.isolation_radius", 5);

         for(int var3 = -var2; var3 <= var2; ++var3) {
            for(int var4 = -var2; var4 <= var2; ++var4) {
               if ((var3 != 0 || var4 != 0) && this.E.containsKey(var1.clone().add((double)var3, (double)0.0F, (double)var4))) {
                  return false;
               }
            }
         }

         return true;
      }

      private void A() {
         if (this.C.exists()) {
            this.D = YamlConfiguration.loadConfiguration(this.C);

            for(String var2 : this.D.getKeys(false)) {
               String[] var3 = var2.split(",");
               if (var3.length == 4) {
                  World var4 = A.this.E.getServer().getWorld(var3[0]);
                  if (var4 != null) {
                     Location var5 = new Location(var4, (double)Integer.parseInt(var3[1]), (double)Integer.parseInt(var3[2]), (double)Integer.parseInt(var3[3]));
                     UUID var6 = UUID.fromString(this.D.getString(var2 + ".owner"));
                     _H var7 = A._H.A(this.D.getString(var2 + ".type"));
                     if (var7 != null) {
                        _C var8 = new _C(var5, var6, var7, this.D.getInt(var2 + ".stackSize"));
                        if (this.D.contains(var2 + ".drops")) {
                           ConcurrentHashMap var9 = new ConcurrentHashMap();

                           for(String var11 : this.D.getConfigurationSection(var2 + ".drops").getKeys(false)) {
                              Material var12 = Material.getMaterial(var11);
                              if (var12 != null) {
                                 var9.put(var12, this.D.getLong(var2 + ".drops." + var11));
                              }
                           }

                           var8.B(var9);
                        }

                        var8.B(this.D.getLong(var2 + ".xp", 0L));
                        if (this.D.contains(var2 + ".blacklist")) {
                           ConcurrentHashMap.KeySetView var13 = ConcurrentHashMap.newKeySet();

                           for(String var15 : this.D.getStringList(var2 + ".blacklist")) {
                              Material var16 = Material.getMaterial(var15);
                              if (var16 != null) {
                                 var13.add(var16);
                              }
                           }

                           var8.A((Set)var13);
                        }

                        this.E.put(var5, var8);
                     }
                  }
               }
            }

         }
      }

      public void C() {
         this.D = new YamlConfiguration();

         for(_C var2 : this.E.values()) {
            Location var3 = var2.D();
            String var10000 = var3.getWorld().getName();
            String var4 = var10000 + "," + var3.getBlockX() + "," + var3.getBlockY() + "," + var3.getBlockZ();
            this.D.set(var4 + ".owner", var2.A().toString());
            this.D.set(var4 + ".type", var2.C().name());
            this.D.set(var4 + ".stackSize", var2.G());

            for(Map.Entry var6 : var2.F().entrySet()) {
               this.D.set(var4 + ".drops." + ((Material)var6.getKey()).name(), var6.getValue());
            }

            this.D.set(var4 + ".xp", var2.E());
            if (!var2.B().isEmpty()) {
               ArrayList var9 = new ArrayList();

               for(Material var7 : var2.B()) {
                  var9.add(var7.name());
               }

               this.D.set(var4 + ".blacklist", var9);
            }
         }

         try {
            this.D.save(this.C);
         } catch (Exception var8) {
            A.this.E.getLogger().warning("Failed to save: " + var8.getMessage());
         }

      }
   }

   private class _F {
      private final _C E;
      private final boolean B;
      private final int D;
      private final int C;
      private final Inventory F;

      _F(_C var2, boolean var3) {
         this(var2, var3, 1);
      }

      _F(_C var2, boolean var3, int var4) {
         this.E = var2;
         this.B = var3;
         this.D = var4;
         this.C = var3 ? A.this.C(var2) : 1;
         String var5 = A.this.A("STORAGE_GUI.TITLE", "&#6BF18D{STACK} {TYPE_DISPLAY} ѕᴛᴏʀᴀɢᴇ &7({PAGE}/{PAGES})");
         String var6 = com.h2ph.B.A.colorize(var5.replace("{STACK}", String.valueOf(var2.G())).replace("{TYPE_DISPLAY}", var2.C().A()).replace("{PAGE}", String.valueOf(var4)).replace("{PAGES}", String.valueOf(this.C)));
         this.F = Bukkit.createInventory(new _K(var2, var3, var4), 54, var6);
         this.A();
      }

      private void A() {
         int var1 = (this.D - 1) * 45;
         int var2 = 0;
         int var3 = 0;

         for(Map.Entry var5 : this.E.F().entrySet()) {
            long var6 = (Long)var5.getValue();
            long var8 = var6;
            int var10 = (int)((var6 + 63L) / 64L);

            for(int var11 = 0; var11 < var10; ++var11) {
               if (var3 >= var1 && var2 < 45) {
                  int var12 = (int)Math.min(64L, var8);
                  ItemStack var13 = new ItemStack((Material)var5.getKey(), var12);
                  ItemMeta var14 = var13.getItemMeta();
                  String var10001 = A.this.B(((Material)var5.getKey()).name());
                  var14.setDisplayName(com.h2ph.B.A.colorize("&e" + var10001 + " x" + var12));
                  var13.setItemMeta(var14);
                  this.F.setItem(var2++, var13);
                  var8 -= (long)var12;
               }

               ++var3;
               if (var2 >= 45) {
                  break;
               }
            }

            if (var2 >= 45) {
               break;
            }
         }

         int var19 = A.this.A((String)"STORAGE_GUI.PREV_BUTTON.SLOT", 45);
         if (this.D > 1) {
            this.F.setItem(var19, A.this.A(A.this.A("STORAGE_GUI.PREV_BUTTON.MATERIAL", "SPECTRAL_ARROW"), Material.SPECTRAL_ARROW, A.this.A("STORAGE_GUI.PREV_BUTTON.NAME", "&aBack"), A.this.A("STORAGE_GUI.PREV_BUTTON.LORE"), (String)null, this.E, 0));
         }

         if (this.D < this.C) {
            int var20 = A.this.A((String)"STORAGE_GUI.NEXT_BUTTON.SLOT", 53);
            this.F.setItem(var20, A.this.A(A.this.A("STORAGE_GUI.NEXT_BUTTON.MATERIAL", "SPECTRAL_ARROW"), Material.SPECTRAL_ARROW, A.this.A("STORAGE_GUI.NEXT_BUTTON.NAME", "&aNext"), A.this.A("STORAGE_GUI.NEXT_BUTTON.LORE"), (String)null, this.E, 0));
         }

         int var21 = A.this.A((String)"STORAGE_GUI.SELL_BUTTON.SLOT", 48);
         this.F.setItem(var21, A.this.A(A.this.A("STORAGE_GUI.SELL_BUTTON.MATERIAL", "GOLD_INGOT"), Material.GOLD_INGOT, A.this.A("STORAGE_GUI.SELL_BUTTON.NAME", "&cѕᴇʟʟ ᴀʟʟ"), A.this.A("STORAGE_GUI.SELL_BUTTON.LORE"), (String)null, this.E, 0));
         int var22 = A.this.A((String)"STORAGE_GUI.INFO_BUTTON.SLOT", 49);
         String var7 = A.this.A("STORAGE_GUI.INFO_BUTTON.MATERIAL", "AUTO");
         Material var23 = "AUTO".equalsIgnoreCase(var7) ? this.E.C().C() : A.this.A(var7, this.E.C().C());
         long var9 = this.E.F().values().stream().mapToLong(Long::longValue).sum();
         long var24 = A.this.A("settings.storage_capacity", 1000000L);
         double var25 = Math.min((double)100.0F, (double)var9 * (double)100.0F / (double)var24);
         String var15 = A.this.A("STORAGE_GUI.INFO_BUTTON.NAME", "&#6BF18D{STACK} {TYPE_DISPLAY} ѕᴘᴀᴡɴᴇʀѕ").replace("{STACK}", String.valueOf(this.E.G())).replace("{TYPE_DISPLAY}", this.E.C().A());
         ArrayList var16 = new ArrayList();

         for(String var18 : A.this.A("STORAGE_GUI.INFO_BUTTON.LORE")) {
            var16.add(com.h2ph.B.A.colorize(var18.replace("{FILLED_PERCENT}", (new DecimalFormat("#.#")).format(var25))));
         }

         this.F.setItem(var22, A.this.A((Material)var23, (String)var15, (List)var16));
         int var26 = A.this.A((String)"STORAGE_GUI.DROP_BUTTON.SLOT", 50);
         this.F.setItem(var26, A.this.A(A.this.A("STORAGE_GUI.DROP_BUTTON.MATERIAL", "DROPPER"), Material.DROPPER, A.this.A("STORAGE_GUI.DROP_BUTTON.NAME", "&#6BF18Dᴅʀᴏᴘ ʟᴏᴏᴛ"), A.this.A("STORAGE_GUI.DROP_BUTTON.LORE"), (String)null, this.E, 0));
         if (A.this.A("FILTER_GUI_ENABLED", false)) {
            int var27 = A.this.A((String)"STORAGE_GUI.FILTER_BUTTON.SLOT", 46);
            this.F.setItem(var27, A.this.A(A.this.A("STORAGE_GUI.FILTER_BUTTON.MATERIAL", "HOPPER"), Material.HOPPER, A.this.A("STORAGE_GUI.FILTER_BUTTON.NAME", "&bFILTER"), A.this.A("STORAGE_GUI.FILTER_BUTTON.LORE"), (String)null, this.E, 0));
         }

      }

      void A(Player var1) {
         var1.setMetadata("filter_gui_data", new FixedMetadataValue(A.this.E, A.this.A(this.E.D())));
         var1.openInventory(this.F);
      }
   }

   private class _G {
      private final int C;
      private static final int B = 45;

      _G(int var2) {
         this.C = var2;
      }

      void A(Player var1) {
         ArrayList var2 = new ArrayList(A.this.C.B().values());
         var2.sort(Comparator.comparing((var0) -> var0.D().getWorld() == null ? "" : var0.D().getWorld().getName()).thenComparingInt((var0) -> var0.D().getBlockX()));
         int var3 = (int)Math.max((double)0.0F, Math.ceil((double)var2.size() / (double)45.0F) - (double)1.0F);
         int var4 = Math.min(Math.max(this.C, 0), var3);
         String var5 = com.h2ph.B.A.colorize(A.this.A("SPAWNER_ADMIN_GUI.TITLE", "&8Spawner Admin &7(Page {PAGE}/{PAGES})").replace("{PAGE}", String.valueOf(var4 + 1)).replace("{PAGES}", String.valueOf(var3 + 1)));
         Inventory var6 = Bukkit.createInventory(new _I(var4), 54, var5);
         int var7 = var4 * 45;
         int var8 = Math.min(var7 + 45, var2.size());
         int var9 = 0;

         for(int var10 = var7; var10 < var8; ++var10) {
            _C var11 = (_C)var2.get(var10);
            var6.setItem(var9, this.B(var11));
            ++var9;
            if (var9 >= 45) {
               break;
            }
         }

         if (var4 > 0) {
            var6.setItem(A.this.A((String)"SPAWNER_ADMIN_GUI.PREV_BUTTON.SLOT", 45), A.this.A(A.this.A("SPAWNER_ADMIN_GUI.PREV_BUTTON.MATERIAL", "ARROW"), Material.ARROW, A.this.A("SPAWNER_ADMIN_GUI.PREV_BUTTON.NAME", "&aBack"), A.this.A("SPAWNER_ADMIN_GUI.PREV_BUTTON.LORE"), (String)null, (_C)null, 0));
         }

         if (var8 < var2.size()) {
            var6.setItem(A.this.A((String)"SPAWNER_ADMIN_GUI.NEXT_BUTTON.SLOT", 53), A.this.A(A.this.A("SPAWNER_ADMIN_GUI.NEXT_BUTTON.MATERIAL", "ARROW"), Material.ARROW, A.this.A("SPAWNER_ADMIN_GUI.NEXT_BUTTON.NAME", "&aNext"), A.this.A("SPAWNER_ADMIN_GUI.NEXT_BUTTON.LORE"), (String)null, (_C)null, 0));
         }

         ArrayList var13 = new ArrayList();

         for(String var12 : A.this.A("SPAWNER_ADMIN_GUI.REFRESH_BUTTON.LORE")) {
            var13.add(var12.replace("{TOTAL}", String.valueOf(var2.size())));
         }

         var6.setItem(A.this.A((String)"SPAWNER_ADMIN_GUI.REFRESH_BUTTON.SLOT", 49), A.this.A(A.this.A("SPAWNER_ADMIN_GUI.REFRESH_BUTTON.MATERIAL", "ANVIL"), Material.ANVIL, A.this.A("SPAWNER_ADMIN_GUI.REFRESH_BUTTON.NAME", "&dSpawner Admin"), var13, (String)null, (_C)null, 0));
         var1.openInventory(var6);
      }

      private ItemStack B(_C var1) {
         Location var2 = var1.D();
         String var3 = var2.getWorld() != null ? var2.getWorld().getName() : "unknown";
         String var4 = var1.C().A();
         String var5 = A.this.A("SPAWNER_ADMIN_GUI.ITEM.MATERIAL", "SPAWNER");
         Material var6 = A.this.A(var5, Material.SPAWNER);
         String var7 = A.this.A("SPAWNER_ADMIN_GUI.ITEM.NAME", "&d{TYPE_DISPLAY} Spawner").replace("{TYPE_DISPLAY}", var4);
         ArrayList var8 = new ArrayList();

         for(String var10 : A.this.A("SPAWNER_ADMIN_GUI.ITEM.LORE")) {
            var8.add(com.h2ph.B.A.colorize(var10.replace("{TYPE_DISPLAY}", var4).replace("{WORLD}", var3).replace("{X}", String.valueOf(var2.getBlockX())).replace("{Y}", String.valueOf(var2.getBlockY())).replace("{Z}", String.valueOf(var2.getBlockZ())).replace("{AMOUNT}", String.valueOf(var1.G()))));
         }

         ItemStack var11 = new ItemStack(var6);
         ItemMeta var12 = var11.getItemMeta();
         if (var12 != null) {
            var12.setDisplayName(com.h2ph.B.A.colorize(var7));
            var12.setLore(var8);
            var11.setItemMeta(var12);
         }

         return var11;
      }
   }

   public static enum _H {
      ¥(EntityType.SKELETON, Material.SKELETON_SKULL, "Skeleton"),
      Ã(EntityType.ZOMBIE, Material.ZOMBIE_HEAD, "Zombie"),
      Î(EntityType.SPIDER, Material.COBWEB, "Spider"),
      k(EntityType.CREEPER, Material.CREEPER_HEAD, "Creeper"),
      v(EntityType.ENDERMAN, Material.ENDER_PEARL, "Enderman"),
      j(EntityType.BLAZE, Material.BLAZE_ROD, "Blaze"),
      l(EntityType.GHAST, Material.GHAST_TEAR, "Ghast"),
      r(EntityType.WITCH, Material.GLASS_BOTTLE, "Witch"),
      É(EntityType.PIGLIN, Material.GOLD_INGOT, "Piglin"),
      H(EntityType.PIGLIN_BRUTE, Material.GOLDEN_SWORD, "Piglin Brute"),
      s(EntityType.HOGLIN, Material.COOKED_PORKCHOP, "Hoglin"),
      V(EntityType.ZOGLIN, Material.ROTTEN_FLESH, "Zoglin"),
      Ì(EntityType.STRIDER, Material.STRING, "Strider"),
      Â(EntityType.MAGMA_CUBE, Material.MAGMA_CREAM, "Magma Cube"),
      M(EntityType.SLIME, Material.SLIME_BALL, "Slime"),
      Á(EntityType.PHANTOM, Material.PHANTOM_MEMBRANE, "Phantom"),
      C(EntityType.DROWNED, Material.TRIDENT, "Drowned"),
      W(EntityType.HUSK, Material.ROTTEN_FLESH, "Husk"),
      Ë(EntityType.STRAY, Material.ARROW, "Stray"),
      c(EntityType.WITHER_SKELETON, Material.COAL, "Wither Skeleton"),
      x(EntityType.PILLAGER, Material.CROSSBOW, "Pillager"),
      g(EntityType.VINDICATOR, Material.EMERALD, "Vindicator"),
      ¢(EntityType.EVOKER, Material.TOTEM_OF_UNDYING, "Evoker"),
      L(EntityType.RAVAGER, Material.SADDLE, "Ravager"),
      u(EntityType.VEX, Material.IRON_SWORD, "Vex"),
      O(EntityType.GUARDIAN, Material.PRISMARINE_SHARD, "Guardian"),
      Ä(EntityType.ELDER_GUARDIAN, Material.PRISMARINE_CRYSTALS, "Elder Guardian"),
      N(EntityType.SILVERFISH, Material.STONE, "Silverfish"),
      t(EntityType.ENDERMITE, Material.ENDER_EYE, "Endermite"),
      X(EntityType.CAVE_SPIDER, Material.SPIDER_EYE, "Cave Spider"),
      b(EntityType.SHULKER, Material.SHULKER_SHELL, "Shulker"),
      n(EntityType.IRON_GOLEM, Material.IRON_INGOT, "Iron Golem"),
      Q(EntityType.IRON_GOLEM, Material.SNOWBALL, "Snow Golem"),
      J(EntityType.WOLF, Material.BONE, "Wolf"),
      À(EntityType.OCELOT, Material.COD, "Ocelot"),
      È(EntityType.CAT, Material.STRING, "Cat"),
      I(EntityType.HORSE, Material.LEATHER, "Horse"),
      º(EntityType.DONKEY, Material.LEATHER, "Donkey"),
      w(EntityType.MULE, Material.LEATHER, "Mule"),
      m(EntityType.SKELETON_HORSE, Material.BONE, "Skeleton Horse"),
      G(EntityType.ZOMBIE_HORSE, Material.ROTTEN_FLESH, "Zombie Horse"),
      ª(EntityType.LLAMA, Material.LEATHER, "Llama"),
      Ç(EntityType.TRADER_LLAMA, Material.LEATHER, "Trader Llama"),
      h(EntityType.PARROT, Material.FEATHER, "Parrot"),
      Ê(EntityType.BAT, Material.FERMENTED_SPIDER_EYE, "Bat"),
      £(EntityType.COD, Material.COD, "Cod"),
      A(EntityType.SALMON, Material.SALMON, "Salmon"),
      Z(EntityType.PUFFERFISH, Material.PUFFERFISH, "Pufferfish"),
      e(EntityType.TROPICAL_FISH, Material.TROPICAL_FISH, "Tropical Fish"),
      p(EntityType.SQUID, Material.INK_SAC, "Squid"),
      Í(EntityType.GLOW_SQUID, Material.GLOW_INK_SAC, "Glow Squid"),
      Å(EntityType.TURTLE, Material.TURTLE_SCUTE, "Turtle"),
      d(EntityType.DOLPHIN, Material.COD, "Dolphin"),
      µ(EntityType.PANDA, Material.BAMBOO, "Panda"),
      i(EntityType.FOX, Material.SWEET_BERRIES, "Fox"),
      Y(EntityType.BEE, Material.HONEYCOMB, "Bee"),
      U(EntityType.CHICKEN, Material.FEATHER, "Chicken"),
      q(EntityType.COW, Material.BEEF, "Cow"),
      P(EntityType.PIG, Material.PORKCHOP, "Pig"),
      f(EntityType.SHEEP, Material.MUTTON, "Sheep"),
      Æ(EntityType.RABBIT, Material.RABBIT_FOOT, "Rabbit"),
      ¤(EntityType.POLAR_BEAR, Material.SALMON, "Polar Bear"),
      y(EntityType.AXOLOTL, Material.AXOLOTL_SPAWN_EGG, "Axolotl"),
      a(EntityType.GOAT, Material.GOAT_HORN, "Goat"),
      F(EntityType.FROG, Material.FROGSPAWN, "Frog"),
      T(EntityType.ALLAY, Material.AMETHYST_SHARD, "Allay"),
      R(EntityType.TADPOLE, Material.TADPOLE_BUCKET, "Tadpole"),
      z(EntityType.WARDEN, Material.SCULK, "Warden"),
      E(EntityType.CAMEL, Material.SAND, "Camel"),
      o(EntityType.SNIFFER, Material.TORCHFLOWER, "Sniffer"),
      K(EntityType.ARMADILLO, Material.ARMADILLO_SCUTE, "Armadillo");

      private final EntityType S;
      private final Material B;
      private final String D;

      private _H(EntityType var3, Material var4, String var5) {
         this.S = var3;
         this.B = var4;
         this.D = var5;
      }

      public EntityType D() {
         return this.S;
      }

      public Material C() {
         return this.B;
      }

      public String A() {
         return this.D;
      }

      public static _H A(String var0) {
         if (var0 == null) {
            return null;
         } else {
            try {
               return valueOf(var0.trim().toUpperCase().replace(' ', '_').replace('-', '_'));
            } catch (IllegalArgumentException var2) {
               return null;
            }
         }
      }

      // $FF: synthetic method
      private static _H[] B() {
         return new _H[]{¥, Ã, Î, k, v, j, l, r, É, H, s, V, Ì, Â, M, Á, C, W, Ë, c, x, g, ¢, L, u, O, Ä, N, t, X, b, n, Q, J, À, È, I, º, w, m, G, ª, Ç, h, Ê, £, A, Z, e, p, Í, Å, d, µ, i, Y, U, q, P, f, Æ, ¤, y, a, F, T, R, z, E, o, K};
      }
   }

   private static class _I implements InventoryHolder {
      private final int A;

      _I(int var1) {
         this.A = var1;
      }

      public int getPage() {
         return this.A;
      }

      public Inventory getInventory() {
         return null;
      }
   }

   private class _J {
      private final _C B;
      private final Inventory C;

      _J(_C var2) {
         this.B = var2;
         String var3 = com.h2ph.B.A.colorize(A.this.A("FILTER_GUI.TITLE", "&8FILTER"));
         this.C = Bukkit.createInventory((InventoryHolder)null, 27, var3);
         this.A();
      }

      private void A() {
         List var1 = A.this.A(this.B.C());
         Set var2 = this.B.B();
         int var3 = 0;

         for(Material var5 : var1) {
            if (var3 >= 27) {
               break;
            }

            ItemStack var6 = new ItemStack(var5);
            ItemMeta var7 = var6.getItemMeta();
            A var10001 = A.this;
            var7.setDisplayName(com.h2ph.B.A.colorize("&f" + var10001.B(var5.name())));
            var7.setLore(List.of(var2.contains(var5) ? com.h2ph.B.A.colorize(A.this.A("FILTER_GUI.BLACKLISTED_LORE", "&c✗ Blacklisted (click to enable)")) : com.h2ph.B.A.colorize(A.this.A("FILTER_GUI.ENABLED_LORE", "&a✓ Enabled (click to blacklist)"))));
            var6.setItemMeta(var7);
            this.C.setItem(var3++, var6);
         }

         int var8 = A.this.A((String)"FILTER_GUI.BACK_BUTTON.SLOT", 22);
         this.C.setItem(var8, A.this.A(A.this.A("FILTER_GUI.BACK_BUTTON.MATERIAL", "RED_STAINED_GLASS_PANE"), Material.RED_STAINED_GLASS_PANE, A.this.A("FILTER_GUI.BACK_BUTTON.NAME", "&4BACK"), A.this.A("FILTER_GUI.BACK_BUTTON.LORE"), (String)null, this.B, 0));
      }

      void A(Player var1) {
         var1.openInventory(this.C);
      }
   }

   public static class _K implements InventoryHolder {
      private final _C C;
      private final boolean A;
      private int B;

      _K(_C var1, boolean var2, int var3) {
         this.C = var1;
         this.A = var2;
         this.B = var3;
      }

      public Inventory getInventory() {
         return null;
      }

      public _C getData() {
         return this.C;
      }

      public boolean isStorage() {
         return this.A;
      }

      public int getPage() {
         return this.B;
      }

      public void setPage(int var1) {
         this.B = var1;
      }
   }

   private class _L {
      boolean A(CommandSender var1, Command var2, String var3, String[] var4) {
         if (!var1.hasPermission("economysmpcore.spawners.admin")) {
            var1.sendMessage(A.this.D("NO_PERMISSION"));
            return true;
         } else if (var4.length < 2) {
            var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Usage: /givespawner <player> <type> [amount]");
            return true;
         } else {
            Player var5 = Bukkit.getPlayer(var4[0]);
            if (var5 == null) {
               var1.sendMessage(A.this.D("PLAYER_NOT_FOUND"));
               return true;
            } else {
               _H var6 = A._H.A(var4[1]);
               if (var6 == null) {
                  var1.sendMessage(A.this.A("INVALID_TYPE", Map.of("{TYPE}", var4[1])));
                  return true;
               } else {
                  int var7 = 1;
                  if (var4.length > 2) {
                     try {
                        var7 = Integer.parseInt(var4[2]);
                     } catch (NumberFormatException var9) {
                     }

                     if (var7 < 1 || var7 > 64) {
                        var1.sendMessage(A.this.D("INVALID_AMOUNT"));
                        return true;
                     }
                  }

                  var5.getInventory().addItem(new ItemStack[]{A.this.A(var6, var7)});
                  Map var8 = Map.of("{GIVEN}", String.valueOf(var7), "{TYPE_NAME}", var6.A(), "{TARGET}", var5.getName());
                  var5.sendMessage(A.this.A("RECEIVED_SPAWNERS", var8));
                  var1.sendMessage(A.this.A("GAVE_SPAWNERS", var8));
                  return true;
               }
            }
         }
      }

      List<String> A(CommandSender var1, String[] var2) {
         if (var2.length == 1) {
            return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> var1x.toLowerCase().startsWith(var2[0].toLowerCase())).collect(Collectors.toList());
         } else if (var2.length == 2) {
            return (List)Arrays.stream(A._H.values()).map(Enum::name).filter((var1x) -> var1x.toLowerCase().startsWith(var2[1].toLowerCase())).collect(Collectors.toList());
         } else {
            return var2.length == 3 ? List.of("1", "8", "16", "32", "64") : Collections.emptyList();
         }
      }
   }

   private class _M implements CommandExecutor, TabCompleter {
      public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
         if (var4.length == 0) {
            var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Usage: /spawner <give|remove|admin>");
            return true;
         } else {
            switch (var4[0].toLowerCase()) {
               case "give":
                  String[] var9 = (String[])Arrays.copyOfRange(var4, 1, var4.length);
                  return A.this.B.A(var1, (Command)null, "spawner", var9);
               case "remove":
                  if (!var1.hasPermission("economysmpcore.spawners.remove")) {
                     var1.sendMessage(A.this.D("NO_PERMISSION"));
                     return true;
                  } else {
                     if (var1 instanceof Player) {
                        Player var8 = (Player)var1;
                        return this.A(var8);
                     }

                     var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only players can use this.");
                     return true;
                  }
               case "admin":
                  if (!var1.hasPermission("economysmpcore.spawners.adminmenu")) {
                     var1.sendMessage(A.this.D("NO_PERMISSION"));
                     return true;
                  } else {
                     if (var1 instanceof Player) {
                        Player var7 = (Player)var1;
                        (A.this.new _G(0)).A(var7);
                        return true;
                     }

                     var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only players can use this.");
                     return true;
                  }
               default:
                  var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Usage: /spawner <give|remove|admin>");
                  return true;
            }
         }
      }

      private boolean A(Player var1) {
         Block var2 = var1.getTargetBlockExact(6, FluidCollisionMode.NEVER);
         if (var2 != null && var2.getType() == Material.SPAWNER) {
            Location var3 = var2.getLocation();
            _C var4 = A.this.C.B(var3);
            if (var4 == null) {
               var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "That spawner isn't tracked by this plugin.");
               return true;
            } else {
               int var5 = var4.G();
               _H var6 = var4.C();
               A.this.C.C(var3);
               var3.getBlock().setType(Material.AIR, false);
               var1.sendMessage(com.h2ph.B.A.colorize("&aRemoved &e" + var5 + "x &a" + var6.A() + " &aspawner stack at &e" + var3.getBlockX() + ", " + var3.getBlockY() + ", " + var3.getBlockZ() + "&a."));
               return true;
            }
         } else {
            var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "You must be looking at a spawner.");
            return true;
         }
      }

      public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
         if (var4.length == 1) {
            return (List)Stream.of("give", "remove", "admin").filter((var1x) -> var1x.startsWith(var4[0].toLowerCase())).collect(Collectors.toList());
         } else {
            return var4.length > 1 && var4[0].equalsIgnoreCase("give") ? A.this.B.A(var1, (String[])Arrays.copyOfRange(var4, 1, var4.length)) : Collections.emptyList();
         }
      }
   }
}
