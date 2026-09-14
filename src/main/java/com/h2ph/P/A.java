package com.h2ph.P;

import com.h2ph.PrismSurvival;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class A implements Listener, CommandExecutor, TabCompleter {
   private static final List<String> B = List.of("tag", "cooldown", "reveal", "reload", "deathmsg", "help");
   private static final List<String> C = List.of("pearl", "firework");
   private static final String S = "economysmpcore.combatlog.bypass";
   private static final String P = "economysmpcore.combatlog.tag";
   private static final String L = "economysmpcore.combatlog.cooldown";
   private static final String T = "economysmpcore.combatlog.reload";
   private final PrismSurvival E;
   private FileConfiguration V;
   private final File R;
   private final Map<UUID, Integer> I = new HashMap();
   private final Map<UUID, BukkitRunnable> F = new HashMap();
   private final Set<UUID> K = new HashSet();
   private final File H;
   private YamlConfiguration M;
   private boolean Q = false;
   private _B G;
   private final Map<UUID, Location> J = new HashMap();
   private final Map<UUID, Long> D = new HashMap();
   private final Map<UUID, Long> U = new HashMap();
   private final Map<String, Location[]> O = new HashMap();
   private final Map<UUID, Map<String, _A>> A = new HashMap();
   private final Map<UUID, BukkitRunnable> N = new HashMap();

   public A(PrismSurvival var1) {
      this.E = var1;
      this.R = new File(var1.getDataFolder(), "combatlog.yml");
      this.H = new File(var1.getDataFolder(), "combatlog_deathmsg.yml");
      this.C();
      this.A();
      this.B();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("combatlog").setExecutor(this);
      var1.getCommand("combatlog").setTabCompleter(this);
      var1.getCommand("combatlogreload").setExecutor(this);
   }

   private void B() {
      if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
         this.Q = false;
         this.G = null;
      } else {
         try {
            this.G = new _B();
            this.Q = true;
            this.E.getLogger().info("WorldGuard detected — combat region restriction is available.");
         } catch (Throwable var2) {
            this.E.getLogger().warning("Found WorldGuard but failed to hook into it (region restriction disabled): " + var2.getMessage());
            this.Q = false;
            this.G = null;
         }

      }
   }

   private void C() {
      boolean var1 = !this.R.exists();
      this.V = var1 ? new YamlConfiguration() : YamlConfiguration.loadConfiguration(this.R);
      boolean var2 = this.E();
      if (var1 || var2) {
         try {
            this.V.save(this.R);
         } catch (Exception var4) {
            this.E.getLogger().warning("Failed to save combatlog.yml: " + var4.getMessage());
         }
      }

   }

   private boolean E() {
      boolean var1 = false;
      var1 |= this.A((String)"combat-duration", (int)10);
      var1 |= this.A((String)"combat-message", (Object)"&7Combat: &b%time%");
      var1 |= this.A((String)"blocked-message", (Object)"&cYou cannot do this in combat.");
      var1 |= this.A((String)"logout-message", (Object)"&c%player% logged out during combat and died!");
      var1 |= this.A((String)"SETTINGS.RADIUS", (Object)false);
      var1 |= this.A((String)"SETTINGS.CHUNKS", (int)5);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.ENABLED", (Object)false);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.REGIONS", (Object)Arrays.asList("safezone", "spawn"));
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.ACTION", (Object)"TELEPORT");
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.PUSHBACK-STRENGTH", (Object)1.2);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.MESSAGE", (Object)"&cYou cannot enter that region while in combat!");
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.NO-PEARL-IN-REGION", (Object)true);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.NO-ELYTRA-IN-REGION", (Object)true);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.NO-PEARL-MESSAGE", (Object)"&cYou cannot use ender pearls in this region!");
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.NO-ELYTRA-MESSAGE", (Object)"&cYou cannot fly with elytra in this region!");
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.BARRIER.ENABLED", (Object)true);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.BARRIER.MATERIAL", (Object)"RED_STAINED_GLASS");
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.BARRIER.HEIGHT", (int)3);
      var1 |= this.A((String)"SETTINGS.REGION-RESTRICTION.BARRIER.REVEAL-DISTANCE", (int)5);
      var1 |= this.A((String)"SETTINGS.REGION-PVP-DISABLE.ENABLED", (Object)false);
      var1 |= this.A((String)"SETTINGS.REGION-PVP-DISABLE.REGIONS", (Object)Arrays.asList("safezone", "spawn"));
      var1 |= this.A((String)"SETTINGS.REGION-PVP-DISABLE.MESSAGE", (Object)"&cPvP is disabled in this region.");
      var1 |= this.A((String)"SETTINGS.PEARL-COOLDOWN.ENABLED", (Object)true);
      var1 |= this.A((String)"SETTINGS.PEARL-COOLDOWN.SECONDS", (int)15);
      var1 |= this.A((String)"SETTINGS.PEARL-COOLDOWN.ONLY-IN-COMBAT", (Object)true);
      var1 |= this.A((String)"SETTINGS.PEARL-COOLDOWN.MESSAGE", (Object)"&cYou must wait &e%time%s &cbefore throwing another ender pearl!");
      var1 |= this.A((String)"SETTINGS.FIREWORK-COOLDOWN.ENABLED", (Object)true);
      var1 |= this.A((String)"SETTINGS.FIREWORK-COOLDOWN.SECONDS", (int)5);
      var1 |= this.A((String)"SETTINGS.FIREWORK-COOLDOWN.ONLY-IN-COMBAT", (Object)true);
      var1 |= this.A((String)"SETTINGS.FIREWORK-COOLDOWN.MESSAGE", (Object)"&cYou must wait &e%time%s &cbefore using another firework!");
      var1 |= this.A((String)"SETTINGS.ANTI-ELYTRA.ENABLED", (Object)true);
      var1 |= this.A((String)"SETTINGS.ANTI-ELYTRA.MESSAGE", (Object)"&cYou cannot use an elytra while in combat!");
      var1 |= this.A((String)"SETTINGS.BANNED-POTIONS.ENABLED", (Object)true);
      var1 |= this.A((String)"SETTINGS.BANNED-POTIONS.MESSAGE", (Object)"&cThat potion effect is banned in combat!");
      var1 |= this.A((String)"SETTINGS.BANNED-POTIONS.EFFECTS", (Object)Arrays.asList("INVISIBILITY", "SPEED"));
      var1 |= this.A((String)"MESSAGES.ENABLED", (Object)true);
      var1 |= this.A((String)"MESSAGES.PREFIX", (Object)"&c☠ ");
      var1 |= this.A((String)"MESSAGES.BLOCK-EXPLOSION", (Object)"{player} ɢᴏᴛ ʙʟᴏᴡɴ ᴛᴏ ᴘɪᴇᴄᴇѕ");
      var1 |= this.A((String)"MESSAGES.CONTACT", (Object)"{player} ᴡᴀѕ ᴘʀɪᴄᴋᴇᴅ ᴛᴏ ᴅᴇᴀᴛʜ");
      var1 |= this.A((String)"MESSAGES.DROWNING.NORMAL", (Object)"{player} drowned!");
      var1 |= this.A((String)"MESSAGES.DROWNING.PVP", (Object)"{player} ᴅʀᴏᴡɴᴇᴅ ᴡʜɪʟѕᴛ ᴛʀʏɪɴɢ ᴛᴏ ᴇѕᴄᴀᴘᴇ {killer}");
      var1 |= this.A((String)"MESSAGES.ENTITY-ATTACK", (Object)"{player} ᴡᴀѕ ѕʟᴀɪɴ ʙʏ {killer}");
      var1 |= this.A((String)"MESSAGES.FALL.NORMAL", (Object)"{player} ʜɪᴛ ᴛʜᴇ ɢʀᴏᴜɴᴅ ᴛᴏᴏ ʜᴀʀᴅ");
      var1 |= this.A((String)"MESSAGES.FALL.PVP", (Object)"{player} ᴡᴀѕ ᴅᴏᴏᴍᴇᴅ ᴛᴏ ꜰᴀʟʟ ʙʏ {killer}");
      var1 |= this.A((String)"MESSAGES.FALLING-BLOCK", (Object)"{player} ɢᴏᴛ ꜰʀᴇᴀᴋɪɴɢ ѕǫᴜᴀѕʜᴇᴅ ʙʏ ᴀ ʙʟᴏᴄᴋ");
      var1 |= this.A((String)"MESSAGES.FIRE.NORMAL", (Object)"{player} ᴡᴇɴᴛ ᴜᴘ ɪɴ ꜰʟᴀᴍᴇѕ");
      var1 |= this.A((String)"MESSAGES.FIRE.PVP", (Object)"{player} ᴡᴀʟᴋᴇᴅ ɪɴᴛᴏ ᴀ ꜰɪʀᴇ ᴡʜɪʟѕᴛ ꜰɪɢʜᴛɪɴɢ {killer}");
      var1 |= this.A((String)"MESSAGES.FIRE-TICK.NORMAL", (Object)"{player} ʙᴜʀɴᴇᴅ ᴛᴏ ᴅᴇᴀᴛʜ");
      var1 |= this.A((String)"MESSAGES.FIRE-TICK.PVP", (Object)"{player} ᴡᴀѕ ʙᴜʀɴᴛ ᴛᴏ ᴀ ᴄʀɪѕᴘ ᴡʜɪʟѕᴛ ꜰɪɢʜᴛɪɴɢ {killer}");
      var1 |= this.A((String)"MESSAGES.LAVA.NORMAL", (Object)"{player} ᴛʀɪᴇᴅ ᴛᴏ ѕᴡɪᴍ ɪɴ ʟᴀᴠᴀ");
      var1 |= this.A((String)"MESSAGES.LAVA.PVP", (Object)"{player} ᴛʀɪᴇᴅ ᴛᴏ ѕᴡɪᴍ ɪɴ ʟᴀᴠᴀ ᴡʜɪʟᴇ ᴛʀʏɪɴɢ ᴛᴏ ᴇѕᴄᴀᴘᴇ {killer}");
      var1 |= this.A((String)"MESSAGES.LIGHTNING", (Object)"{player} ɢᴏᴛ ʟɪᴛ ᴛʜᴇ ʜᴇʟʟ ᴜᴘ ʙʏ ᴀ ʟɪɢʜᴛɴɪɴɢ");
      var1 |= this.A((String)"MESSAGES.POISON", (Object)"{player} ᴡᴀѕ ᴘᴏɪѕᴏɴᴇᴅ");
      var1 |= this.A((String)"MESSAGES.PROJECTILE.NORMAL", (Object)"{player} ᴡᴀѕ ѕʜᴏᴛ");
      var1 |= this.A((String)"MESSAGES.PROJECTILE.PVP", (Object)"{player} ᴡᴀѕ ѕʜᴏᴛ ʙʏ {killer}");
      var1 |= this.A((String)"MESSAGES.STARVATION", (Object)"{player} ѕᴛᴀʀᴠᴇᴅ ᴛᴏ ᴅᴇᴀᴛʜ");
      var1 |= this.A((String)"MESSAGES.SUFFOCATION", (Object)"{player} ѕᴜꜰꜰᴏᴄᴀᴛᴇᴅ ɪɴ ᴀ ᴡᴀʟʟ");
      var1 |= this.A((String)"MESSAGES.SUICIDE", (Object)"{player} ᴛᴏᴏᴋ ʜɪѕ ᴏᴡɴ ʟɪꜰᴇ ʟɪᴋᴇ ᴀ ᴘᴇᴀѕᴀɴᴛ");
      var1 |= this.A((String)"MESSAGES.THORNS", (Object)"{player} ᴋɪʟʟᴇᴅ ᴛʜᴇᴍѕᴇʟꜰ ʙʏ ᴛʀʏɪɴɢ ᴛᴏ ᴋɪʟʟ ѕᴏᴍᴇᴏɴᴇ");
      var1 |= this.A((String)"MESSAGES.VOID.NORMAL", (Object)"{player} ꜰᴇʟʟ ᴏᴜᴛ ᴏꜰ ᴛʜᴇ ᴡᴏʀʟᴅ");
      var1 |= this.A((String)"MESSAGES.VOID.PVP", (Object)"{player} ᴡᴀѕ ᴋɴᴏᴄᴋᴇᴅ ɪɴᴛᴏ ᴛʜᴇ ᴠᴏɪᴅ ʙʏ {killer}");
      var1 |= this.A((String)"MESSAGES.WITHER", (Object)"{player} ᴡɪᴛʜᴇʀᴇᴅ ᴀᴡᴀʏ");
      var1 |= this.A((String)"MESSAGES.ENTITY-EXPLOSION.NORMAL", (Object)"{player} ᴡᴀѕ ʙʟᴏᴡɴ ᴜᴘ");
      var1 |= this.A((String)"MESSAGES.ENTITY-EXPLOSION.PVP", (Object)"{player} ᴡᴀѕ ʙʟᴏᴡɴ ᴜᴘ ʙʏ {killer}");
      var1 |= this.A((String)"MESSAGES.DEFAULT", (Object)"{player} ᴅɪᴇᴅ");
      var1 |= this.A((String)"enable-kill-reward", (Object)false);
      var1 |= this.A((String)"kill-reward-command", (Object)"eco give %killer% 100");
      var1 |= this.A((String)"disabled-worlds", (Object)Arrays.asList("disabled_world", "spawn_world"));
      var1 |= this.A((String)"blocked-commands", (Object)Arrays.asList("spawn", "home", "tpa", "warp"));
      return var1;
   }

   private boolean A(String var1, Object var2) {
      if (this.V.contains(var1)) {
         return false;
      } else {
         this.V.set(var1, var2);
         return true;
      }
   }

   private void G() {
      try {
         this.V.save(this.R);
      } catch (Exception var2) {
         this.E.getLogger().warning("Failed to save combatlog.yml: " + var2.getMessage());
      }

   }

   private void F() {
      this.C();
      this.A();
      this.O.clear();
      this.E.getLogger().info("CombatLog config reloaded.");
   }

   private void A() {
      this.M = this.H.exists() ? YamlConfiguration.loadConfiguration(this.H) : new YamlConfiguration();
      this.K.clear();

      for(String var2 : this.M.getKeys(false)) {
         if (!this.M.getBoolean(var2, true)) {
            this.K.add(UUID.fromString(var2));
         }
      }

   }

   private void D() {
      try {
         this.M.save(this.H);
      } catch (Exception var2) {
         this.E.getLogger().warning("Failed to save combatlog_deathmsg.yml: " + var2.getMessage());
      }

   }

   private boolean B(UUID var1) {
      return !this.K.contains(var1);
   }

   private void E(Player var1) {
      UUID var2 = var1.getUniqueId();
      if (this.K.contains(var2)) {
         this.K.remove(var2);
         this.M.set(var2.toString(), true);
         var1.sendMessage(this.B("&aCustom death messages &aenabled&a."));
      } else {
         this.K.add(var2);
         this.M.set(var2.toString(), false);
         var1.sendMessage(this.B("&cCustom death messages &cdisabled&c."));
      }

      this.D();
   }

   private boolean F(Player var1) {
      return this.V.getStringList("disabled-worlds").contains(var1.getWorld().getName());
   }

   private String B(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private boolean C(Player var1) {
      return var1.hasPermission("economysmpcore.combatlog.bypass");
   }

   private boolean A(Entity var1) {
      if (var1.hasMetadata("NPC")) {
         return true;
      } else if (var1.hasMetadata("fancynpcs")) {
         return true;
      } else if (var1.hasMetadata("znpcs")) {
         return true;
      } else {
         return var1.hasMetadata("shopkeeper");
      }
   }

   private Player B(Player var1) {
      if (var1.getKiller() != null) {
         return var1.getKiller();
      } else {
         EntityDamageEvent var3 = var1.getLastDamageCause();
         if (var3 instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent var2 = (EntityDamageByEntityEvent)var3;
            Entity var7 = var2.getDamager();
            if (var7 instanceof Player) {
               Player var8 = (Player)var7;
               return var8;
            }

            if (var7 instanceof Projectile) {
               Projectile var4 = (Projectile)var7;
               ProjectileSource var6 = var4.getShooter();
               if (var6 instanceof Player) {
                  Player var5 = (Player)var6;
                  return var5;
               }
            }
         }

         return null;
      }
   }

   private boolean B(Player var1, Player var2) {
      if (!this.V.getBoolean("SETTINGS.RADIUS", false)) {
         return true;
      } else {
         int var3 = this.V.getInt("SETTINGS.CHUNKS", 5);
         double var4 = (double)var3 * (double)16.0F;
         return var1.getWorld().equals(var2.getWorld()) && var1.getLocation().distance(var2.getLocation()) <= var4;
      }
   }

   private String A(Player var1, Player var2) {
      if (!this.V.getBoolean("MESSAGES.ENABLED", true)) {
         return null;
      } else {
         String var3 = this.B(this.V.getString("MESSAGES.PREFIX", "&c☠ "));
         boolean var5 = var2 != null;
         String var4;
         if (var1.getLastDamageCause() == null) {
            var4 = "MESSAGES.DEFAULT";
         } else {
            switch (var1.getLastDamageCause().getCause()) {
               case BLOCK_EXPLOSION:
                  var4 = "MESSAGES.BLOCK-EXPLOSION";
                  break;
               case CONTACT:
                  var4 = "MESSAGES.CONTACT";
                  break;
               case DROWNING:
                  var4 = var5 ? "MESSAGES.DROWNING.PVP" : "MESSAGES.DROWNING.NORMAL";
                  break;
               case ENTITY_ATTACK:
               case ENTITY_SWEEP_ATTACK:
                  var4 = "MESSAGES.ENTITY-ATTACK";
                  break;
               case FALL:
                  var4 = var5 ? "MESSAGES.FALL.PVP" : "MESSAGES.FALL.NORMAL";
                  break;
               case FALLING_BLOCK:
                  var4 = "MESSAGES.FALLING-BLOCK";
                  break;
               case FIRE:
               case FIRE_TICK:
                  var4 = var5 ? "MESSAGES.FIRE-TICK.PVP" : "MESSAGES.FIRE-TICK.NORMAL";
                  break;
               case LAVA:
                  var4 = var5 ? "MESSAGES.LAVA.PVP" : "MESSAGES.LAVA.NORMAL";
                  break;
               case LIGHTNING:
                  var4 = "MESSAGES.LIGHTNING";
                  break;
               case POISON:
                  var4 = "MESSAGES.POISON";
                  break;
               case PROJECTILE:
                  var4 = var5 ? "MESSAGES.PROJECTILE.PVP" : "MESSAGES.PROJECTILE.NORMAL";
                  break;
               case STARVATION:
                  var4 = "MESSAGES.STARVATION";
                  break;
               case SUFFOCATION:
                  var4 = "MESSAGES.SUFFOCATION";
                  break;
               case SUICIDE:
                  var4 = "MESSAGES.SUICIDE";
                  break;
               case THORNS:
                  var4 = "MESSAGES.THORNS";
                  break;
               case VOID:
                  var4 = var5 ? "MESSAGES.VOID.PVP" : "MESSAGES.VOID.NORMAL";
                  break;
               case WITHER:
                  var4 = "MESSAGES.WITHER";
                  break;
               case ENTITY_EXPLOSION:
                  var4 = var5 ? "MESSAGES.ENTITY-EXPLOSION.PVP" : "MESSAGES.ENTITY-EXPLOSION.NORMAL";
                  break;
               default:
                  var4 = "MESSAGES.DEFAULT";
            }
         }

         String var6 = this.V.getString(var4, "{player} died");
         String var7 = var2 != null ? var2.getName() : "unknown";
         String var8 = var3 + var6.replace("{player}", var1.getName()).replace("{killer}", var7);
         return this.B(var8);
      }
   }

   private void C(UUID var1) {
      this.I.remove(var1);
      BukkitRunnable var2 = (BukkitRunnable)this.F.remove(var1);
      if (var2 != null) {
         var2.cancel();
      }

      this.J.remove(var1);
      this.E(var1);
   }

   private void H(Player var1) {
      this.A(var1, false);
   }

   private void A(final Player var1, boolean var2) {
      if (!this.F(var1)) {
         if (var2 || !this.C(var1)) {
            final UUID var3 = var1.getUniqueId();
            if (this.F.containsKey(var3)) {
               ((BukkitRunnable)this.F.get(var3)).cancel();
               this.F.remove(var3);
            }

            int var4 = this.V.getInt("combat-duration", 10);
            this.I.put(var3, var4);
            BukkitRunnable var5 = new BukkitRunnable() {
               public void run() {
                  if (!A.this.I.containsKey(var3)) {
                     this.cancel();
                     A.this.F.remove(var3);
                  } else {
                     int var1x = (Integer)A.this.I.get(var3);
                     if (var1x <= 0) {
                        A.this.I.remove(var3);
                        A.this.F.remove(var3);
                        this.cancel();
                     } else {
                        A.this.I.put(var3, var1x - 1);
                        String var2 = A.this.V.getString("combat-message", "&7Combat: &b%time%").replace("%time%", String.valueOf(var1x));
                        var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(A.this.B(var2)));
                     }
                  }
               }
            };
            var5.runTaskTimer(this.E, 0L, 20L);
            this.F.put(var3, var5);
            this.D(var1);
         }
      }
   }

   @EventHandler(
      priority = EventPriority.NORMAL
   )
   public void onDamage(EntityDamageByEntityEvent var1) {
      Player var2 = null;
      Entity var6 = var1.getDamager();
      if (var6 instanceof Player var3) {
         var2 = var3;
      } else {
         var6 = var1.getDamager();
         if (var6 instanceof Projectile var4) {
            ProjectileSource var13 = var4.getShooter();
            if (var13 instanceof Player var5) {
               var2 = var5;
            }
         }
      }

      Entity var9 = var1.getEntity();
      if (var9 instanceof Player var8) {
         if (!this.A((Entity)var8)) {
            if (var2 != null && !this.A((Entity)var2)) {
               if (!var2.equals(var8)) {
                  if (this.Q && this.G != null && this.V.getBoolean("SETTINGS.REGION-PVP-DISABLE.ENABLED", false) && !this.C(var2)) {
                     List var10 = this.V.getStringList("SETTINGS.REGION-PVP-DISABLE.REGIONS");

                     try {
                        if (this.G.A(var8.getLocation(), var10) || this.G.A(var2.getLocation(), var10)) {
                           var1.setCancelled(true);
                           String var11 = this.V.getString("SETTINGS.REGION-PVP-DISABLE.MESSAGE", "&cPvP is disabled in this region.");
                           var2.sendMessage(this.B(var11));
                           return;
                        }
                     } catch (Throwable var7) {
                        this.E.getLogger().warning("WorldGuard PvP-region check failed: " + var7.getMessage());
                     }
                  }

                  this.H(var8);
                  this.H(var2);
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onMove(PlayerMoveEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      if (!this.I.containsKey(var3)) {
         this.J.put(var3, var2.getLocation());
      } else if (this.Q && this.G != null) {
         if (this.V.getBoolean("SETTINGS.REGION-RESTRICTION.ENABLED", false)) {
            if (!this.C(var2)) {
               if (var1.getFrom().getBlockX() != var1.getTo().getBlockX() || var1.getFrom().getBlockY() != var1.getTo().getBlockY() || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ()) {
                  List var4 = this.V.getStringList("SETTINGS.REGION-RESTRICTION.REGIONS");

                  boolean var5;
                  try {
                     var5 = this.G.A(var1.getTo(), var4);
                  } catch (Throwable var12) {
                     this.E.getLogger().warning("WorldGuard region check failed: " + var12.getMessage());
                     return;
                  }

                  if (!var5) {
                     this.J.put(var3, var1.getTo());
                  } else {
                     String var6 = this.V.getString("SETTINGS.REGION-RESTRICTION.MESSAGE", "&cYou cannot enter that region while in combat!");
                     String var7 = this.V.getString("SETTINGS.REGION-RESTRICTION.ACTION", "TELEPORT").toUpperCase(Locale.ROOT);
                     var1.setCancelled(true);
                     var2.sendMessage(this.B(var6));
                     Location var8 = (Location)this.J.getOrDefault(var3, var1.getFrom());
                     var2.teleport(var8);
                     if (var7.equals("PUSHBACK")) {
                        Vector var9 = var1.getFrom().toVector().subtract(var1.getTo().toVector());
                        if (var9.lengthSquared() == (double)0.0F) {
                           var9 = var2.getLocation().getDirection().multiply(-1);
                        }

                        var9.setY((double)0.25F);
                        double var10 = this.V.getDouble("SETTINGS.REGION-RESTRICTION.PUSHBACK-STRENGTH", 1.2);
                        var2.setVelocity(var9.normalize().multiply(var10));
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onEnderPearlTeleport(PlayerTeleportEvent var1) {
      PlayerTeleportEvent.TeleportCause var2 = var1.getCause();
      if (var2 == TeleportCause.ENDER_PEARL || var2 == TeleportCause.CHORUS_FRUIT) {
         Player var3 = var1.getPlayer();
         UUID var4 = var3.getUniqueId();
         if (this.I.containsKey(var4)) {
            if (this.Q && this.G != null) {
               if (this.V.getBoolean("SETTINGS.REGION-RESTRICTION.ENABLED", false)) {
                  if (!this.C(var3)) {
                     Location var5 = var1.getTo();
                     if (var5 != null) {
                        List var6 = this.V.getStringList("SETTINGS.REGION-RESTRICTION.REGIONS");

                        boolean var7;
                        try {
                           var7 = this.G.A(var5, var6);
                        } catch (Throwable var9) {
                           this.E.getLogger().warning("WorldGuard region check failed: " + var9.getMessage());
                           return;
                        }

                        if (var7) {
                           var1.setCancelled(true);
                           var3.sendMessage(this.B(this.V.getString("SETTINGS.REGION-RESTRICTION.MESSAGE", "&cYou cannot enter that region while in combat!")));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onToggleGlide(EntityToggleGlideEvent var1) {
      Entity var3 = var1.getEntity();
      if (var3 instanceof Player var2) {
         if (var1.isGliding()) {
            if (!this.C(var2)) {
               boolean var4 = this.I.containsKey(var2.getUniqueId());
               if (this.V.getBoolean("SETTINGS.ANTI-ELYTRA.ENABLED", true) && var4) {
                  var1.setCancelled(true);
                  var2.sendMessage(this.B(this.V.getString("SETTINGS.ANTI-ELYTRA.MESSAGE", "&cYou cannot use an elytra while in combat!")));
               } else {
                  if (this.V.getBoolean("SETTINGS.REGION-RESTRICTION.NO-ELYTRA-IN-REGION", true) && this.I(var2)) {
                     var1.setCancelled(true);
                     var2.sendMessage(this.B(this.V.getString("SETTINGS.REGION-RESTRICTION.NO-ELYTRA-MESSAGE", "&cYou cannot fly with elytra in this region!")));
                  }

               }
            }
         }
      }
   }

   @EventHandler
   public void onProjectileLaunch(ProjectileLaunchEvent var1) {
      ProjectileSource var3 = var1.getEntity().getShooter();
      if (var3 instanceof Player var2) {
         if (!this.C(var2)) {
            if (var1.getEntity() instanceof EnderPearl) {
               this.A(var1, var2);
            } else {
               if (var1.getEntity() instanceof ThrownPotion) {
                  this.B(var1, var2);
               }

            }
         }
      }
   }

   private void A(ProjectileLaunchEvent var1, Player var2) {
      boolean var3 = this.I.containsKey(var2.getUniqueId());
      if (this.V.getBoolean("SETTINGS.REGION-RESTRICTION.NO-PEARL-IN-REGION", true) && this.I(var2)) {
         var1.setCancelled(true);
         var2.sendMessage(this.B(this.V.getString("SETTINGS.REGION-RESTRICTION.NO-PEARL-MESSAGE", "&cYou cannot use ender pearls in this region!")));
      } else if (this.V.getBoolean("SETTINGS.PEARL-COOLDOWN.ENABLED", true)) {
         if (!this.V.getBoolean("SETTINGS.PEARL-COOLDOWN.ONLY-IN-COMBAT", true) || var3) {
            UUID var4 = var2.getUniqueId();
            long var5 = System.currentTimeMillis();
            Long var7 = (Long)this.D.get(var4);
            if (var7 != null && var5 < var7) {
               long var8 = (var7 - var5 + 999L) / 1000L;
               var1.setCancelled(true);
               var2.sendMessage(this.B(this.V.getString("SETTINGS.PEARL-COOLDOWN.MESSAGE", "&cYou must wait &e%time%s &cbefore throwing another ender pearl!").replace("%time%", String.valueOf(var8))));
            } else {
               this.D.put(var4, var5 + (long)this.V.getInt("SETTINGS.PEARL-COOLDOWN.SECONDS", 15) * 1000L);
            }
         }
      }
   }

   private void B(ProjectileLaunchEvent var1, Player var2) {
      if (this.V.getBoolean("SETTINGS.BANNED-POTIONS.ENABLED", true)) {
         if (this.I.containsKey(var2.getUniqueId())) {
            ItemStack var3 = var2.getInventory().getItemInMainHand();
            ItemMeta var5 = var3.getItemMeta();
            if (var5 instanceof PotionMeta) {
               PotionMeta var4 = (PotionMeta)var5;
               if (this.A(var4)) {
                  var1.setCancelled(true);
                  var2.sendMessage(this.B(this.V.getString("SETTINGS.BANNED-POTIONS.MESSAGE", "&cThat potion effect is banned in combat!")));
               }

            }
         }
      }
   }

   @EventHandler
   public void onPotionConsume(PlayerItemConsumeEvent var1) {
      if (this.V.getBoolean("SETTINGS.BANNED-POTIONS.ENABLED", true)) {
         Player var2 = var1.getPlayer();
         if (!this.C(var2)) {
            if (this.I.containsKey(var2.getUniqueId())) {
               ItemStack var3 = var1.getItem();
               if (var3.getType() == Material.POTION) {
                  ItemMeta var5 = var3.getItemMeta();
                  if (var5 instanceof PotionMeta) {
                     PotionMeta var4 = (PotionMeta)var5;
                     if (this.A(var4)) {
                        var1.setCancelled(true);
                        var2.sendMessage(this.B(this.V.getString("SETTINGS.BANNED-POTIONS.MESSAGE", "&cThat potion effect is banned in combat!")));
                     }

                  }
               }
            }
         }
      }
   }

   private boolean A(PotionMeta var1) {
      List var2 = this.V.getStringList("SETTINGS.BANNED-POTIONS.EFFECTS");
      if (var2.isEmpty()) {
         return false;
      } else {
         if (var1.getBasePotionType() != null) {
            String var3 = var1.getBasePotionType().name();
            if (var2.stream().anyMatch((var1x) -> var1x.equalsIgnoreCase(var3))) {
               return true;
            }
         }

         for(PotionEffect var4 : var1.getCustomEffects()) {
            if (var2.stream().anyMatch((var1x) -> var1x.equalsIgnoreCase(var4.getType().getName()))) {
               return true;
            }
         }

         return false;
      }
   }

   @EventHandler
   public void onFireworkInteract(PlayerInteractEvent var1) {
      if (this.V.getBoolean("SETTINGS.FIREWORK-COOLDOWN.ENABLED", true)) {
         Player var2 = var1.getPlayer();
         if (!this.C(var2)) {
            ItemStack var3 = var1.getItem();
            if (var3 != null && var3.getType() == Material.FIREWORK_ROCKET) {
               if (var2.isGliding()) {
                  boolean var4 = this.I.containsKey(var2.getUniqueId());
                  if (!this.V.getBoolean("SETTINGS.FIREWORK-COOLDOWN.ONLY-IN-COMBAT", true) || var4) {
                     UUID var5 = var2.getUniqueId();
                     long var6 = System.currentTimeMillis();
                     Long var8 = (Long)this.U.get(var5);
                     if (var8 != null && var6 < var8) {
                        long var9 = (var8 - var6 + 999L) / 1000L;
                        var1.setCancelled(true);
                        var2.sendMessage(this.B(this.V.getString("SETTINGS.FIREWORK-COOLDOWN.MESSAGE", "&cYou must wait &e%time%s &cbefore using another firework!").replace("%time%", String.valueOf(var9))));
                     } else {
                        this.U.put(var5, var6 + (long)this.V.getInt("SETTINGS.FIREWORK-COOLDOWN.SECONDS", 5) * 1000L);
                     }
                  }
               }
            }
         }
      }
   }

   private void D(final Player var1) {
      final UUID var2 = var1.getUniqueId();
      this.E(var2);
      if (this.Q && this.G != null) {
         if (this.V.getBoolean("SETTINGS.REGION-RESTRICTION.ENABLED", false)) {
            BukkitRunnable var3 = new BukkitRunnable() {
               public void run() {
                  if (var1.isOnline() && A.this.I.containsKey(var2)) {
                     if (!A.this.C(var1) && A.this.V.getBoolean("SETTINGS.REGION-RESTRICTION.ENABLED", false) && A.this.V.getBoolean("SETTINGS.REGION-RESTRICTION.BARRIER.ENABLED", true)) {
                        A.this.A(var1);
                     } else {
                        A.this.A(var2);
                     }
                  } else {
                     this.cancel();
                     A.this.N.remove(var2);
                     A.this.A(var2);
                  }
               }
            };
            var3.runTaskTimer(this.E, 5L, 10L);
            this.N.put(var2, var3);
         }
      }
   }

   private void E(UUID var1) {
      BukkitRunnable var2 = (BukkitRunnable)this.N.remove(var1);
      if (var2 != null) {
         var2.cancel();
      }

      this.A(var1);
   }

   private void A(Player var1) {
      UUID var2 = var1.getUniqueId();
      double var3 = Math.max((double)0.0F, this.V.getDouble("SETTINGS.REGION-RESTRICTION.BARRIER.REVEAL-DISTANCE", (double)5.0F));
      List var5 = this.V.getStringList("SETTINGS.REGION-RESTRICTION.REGIONS");
      Map var6 = (Map)this.A.computeIfAbsent(var2, (var0) -> new HashMap());

      for(String var8 : var5) {
         if (var8 != null && !var8.isBlank()) {
            Location[] var9 = this.A(var1.getWorld(), var8);
            _A var10 = (_A)var6.get(var8);
            if (var9 == null) {
               if (var10 != null) {
                  this.A(var1, var10.A);
                  var6.remove(var8);
               }
            } else {
               double var11 = this.A(var1.getLocation(), var9[0], var9[1]);
               boolean var13 = var11 <= var3;
               int var14 = var1.getLocation().getBlockY();
               if (!var13) {
                  if (var10 != null) {
                     this.A(var1, var10.A);
                     var6.remove(var8);
                  }
               } else if (var10 == null || var10.B != var14) {
                  if (var10 != null) {
                     this.A(var1, var10.A);
                  }

                  List var15 = this.A(var1.getWorld(), var9[0], var9[1], var14);
                  Material var16 = this.H();

                  for(Location var18 : var15) {
                     var1.sendBlockChange(var18, var16.createBlockData());
                  }

                  var6.put(var8, new _A(var15, var14));
               }
            }
         }
      }

   }

   private Material H() {
      String var1 = this.V.getString("SETTINGS.REGION-RESTRICTION.BARRIER.MATERIAL", "RED_STAINED_GLASS");
      Material var2 = Material.matchMaterial(var1);
      return var2 != null ? var2 : Material.RED_STAINED_GLASS;
   }

   private Location[] A(World var1, String var2) {
      String var10000 = var1.getName();
      String var3 = var10000 + ":" + var2.toLowerCase();
      Location[] var4 = (Location[])this.O.get(var3);
      if (var4 != null) {
         return var4;
      } else {
         try {
            Location[] var5 = this.G.A(var1, var2);
            if (var5 != null) {
               this.O.put(var3, var5);
            }

            return var5;
         } catch (Throwable var6) {
            this.E.getLogger().warning("WorldGuard region lookup failed for '" + var2 + "': " + var6.getMessage());
            return null;
         }
      }
   }

   private double A(Location var1, Location var2, Location var3) {
      double var4 = this.A(var1.getX(), (double)var2.getBlockX(), (double)(var3.getBlockX() + 1));
      double var6 = this.A(var1.getY(), (double)var2.getBlockY(), (double)(var3.getBlockY() + 1));
      double var8 = this.A(var1.getZ(), (double)var2.getBlockZ(), (double)(var3.getBlockZ() + 1));
      double var10 = var1.getX() - var4;
      double var12 = var1.getY() - var6;
      double var14 = var1.getZ() - var8;
      return Math.sqrt(var10 * var10 + var12 * var12 + var14 * var14);
   }

   private double A(double var1, double var3, double var5) {
      return Math.max(var3, Math.min(var5, var1));
   }

   private List<Location> A(World var1, Location var2, Location var3, int var4) {
      ArrayList var5 = new ArrayList();
      int var6 = var2.getBlockX();
      int var7 = var2.getBlockZ();
      int var8 = var3.getBlockX();
      int var9 = var3.getBlockZ();
      int var10 = Math.max(1, this.V.getInt("SETTINGS.REGION-RESTRICTION.BARRIER.HEIGHT", 3));
      int var11 = var6 - 1;
      int var12 = var8 + 1;
      int var13 = var7 - 1;
      int var14 = var9 + 1;

      for(int var15 = var11; var15 <= var12; ++var15) {
         for(int var16 = 0; var16 < var10; ++var16) {
            var5.add(new Location(var1, (double)var15, (double)(var4 + var16), (double)var13));
            var5.add(new Location(var1, (double)var15, (double)(var4 + var16), (double)var14));
         }
      }

      for(int var17 = var13; var17 <= var14; ++var17) {
         for(int var18 = 0; var18 < var10; ++var18) {
            var5.add(new Location(var1, (double)var11, (double)(var4 + var18), (double)var17));
            var5.add(new Location(var1, (double)var12, (double)(var4 + var18), (double)var17));
         }
      }

      return var5;
   }

   private void A(Player var1, List<Location> var2) {
      if (var1 != null && var1.isOnline()) {
         for(Location var4 : var2) {
            var1.sendBlockChange(var4, var4.getBlock().getBlockData());
         }

      }
   }

   private void A(UUID var1) {
      Map var2 = (Map)this.A.remove(var1);
      if (var2 != null && !var2.isEmpty()) {
         Player var3 = Bukkit.getPlayer(var1);

         for(_A var5 : var2.values()) {
            this.A(var3, var5.A);
         }

      }
   }

   private boolean I(Player var1) {
      if (this.Q && this.G != null) {
         if (!this.V.getBoolean("SETTINGS.REGION-RESTRICTION.ENABLED", false)) {
            return false;
         } else {
            List var2 = this.V.getStringList("SETTINGS.REGION-RESTRICTION.REGIONS");

            try {
               return this.G.A(var1.getLocation(), var2);
            } catch (Throwable var4) {
               this.E.getLogger().warning("WorldGuard region check failed: " + var4.getMessage());
               return false;
            }
         }
      } else {
         return false;
      }
   }

   @EventHandler
   public void onPlayerCommand(PlayerCommandPreprocessEvent var1) {
      Player var2 = var1.getPlayer();
      if (this.I.containsKey(var2.getUniqueId())) {
         if (!this.F(var2)) {
            if (!this.C(var2)) {
               String var3 = var1.getMessage().toLowerCase();

               for(String var5 : this.V.getStringList("blocked-commands")) {
                  if (var3.startsWith("/" + var5.toLowerCase())) {
                     var2.sendMessage(this.B(this.V.getString("blocked-message", "&cYou cannot do this in combat.")));
                     var1.setCancelled(true);
                     break;
                  }
               }

            }
         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      this.G(var1.getPlayer());
   }

   @EventHandler
   public void onKick(PlayerKickEvent var1) {
      this.G(var1.getPlayer());
   }

   private void G(Player var1) {
      if (this.I.containsKey(var1.getUniqueId())) {
         if (this.F(var1)) {
            this.C(var1.getUniqueId());
         } else {
            var1.setHealth((double)0.0F);
            String var2 = this.V.getString("logout-message", "&c%player% logged out during combat and died!").replace("%player%", var1.getName());
            Bukkit.broadcastMessage(this.B(var2));
            this.C(var1.getUniqueId());
         }
      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerDeath(PlayerDeathEvent var1) {
      Player var2 = var1.getEntity();
      this.C(var2.getUniqueId());
      Player var3 = this.B(var2);
      if (var3 != null) {
         this.C(var3.getUniqueId());
         if (this.V.getBoolean("enable-kill-reward", false)) {
            String var4 = this.V.getString("kill-reward-command", "").replace("%killer%", var3.getName()).replace("%player%", var2.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var4);
         }
      }

      if (this.B(var2.getUniqueId())) {
         String var8 = this.A(var2, var3);
         if (var8 != null) {
            boolean var5 = this.V.getBoolean("SETTINGS.RADIUS", false);
            if (var5) {
               for(Player var7 : Bukkit.getOnlinePlayers()) {
                  if (this.B(var7, var2)) {
                     var7.sendMessage(var8);
                  }
               }

               var1.setDeathMessage((String)null);
            } else {
               var1.setDeathMessage(var8);
            }
         }
      }

   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var2.getName().equalsIgnoreCase("combatlogreload")) {
         if (!var1.hasPermission("economysmpcore.combatlog.reload")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
            return true;
         } else {
            this.F();
            var1.sendMessage(String.valueOf(ChatColor.GREEN) + "CombatLog config reloaded.");
            return true;
         }
      } else if (!var2.getName().equalsIgnoreCase("combatlog")) {
         return false;
      } else if (var4.length == 0) {
         this.A(var1);
         return true;
      } else {
         switch (var4[0].toLowerCase()) {
            case "tag":
               if (!var1.hasPermission("economysmpcore.combatlog.tag")) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
                  return true;
               }

               if (!(var1 instanceof Player)) {
                  var1.sendMessage("Only players can use this command.");
                  return true;
               }

               Player var13 = (Player)var1;
               double var14 = Math.max(var13.getHealth() - (double)1.0F, (double)1.0F);
               var13.setHealth(var14);
               this.A(var13, true);
               var13.sendMessage(this.B("&eYou have been combat tagged for testing."));
               break;
            case "cooldown":
               if (!var1.hasPermission("economysmpcore.combatlog.cooldown")) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
                  return true;
               }

               if (var4.length < 3) {
                  var1.sendMessage(this.B("&cUsage: /combatlog cooldown <pearl|firework> <seconds, e.g. 1s or 15>"));
                  return true;
               }

               String var12 = var4[1].toLowerCase();
               if (!C.contains(var12)) {
                  var1.sendMessage(this.B("&cUnknown cooldown type. Valid types: pearl, firework"));
                  return true;
               }

               Integer var8 = this.A(var4[2]);
               if (var8 == null || var8 < 0) {
                  var1.sendMessage(this.B("&cInvalid duration. Use a number of seconds like &e5 &cor &e5s&c."));
                  return true;
               }

               switch (var12) {
                  case "pearl":
                     this.V.set("SETTINGS.PEARL-COOLDOWN.SECONDS", var8);
                     this.G();
                     var1.sendMessage(this.B("&aEnder pearl cooldown set to &e" + var8 + "s&a."));
                     return true;
                  case "firework":
                     this.V.set("SETTINGS.FIREWORK-COOLDOWN.SECONDS", var8);
                     this.G();
                     var1.sendMessage(this.B("&aFirework cooldown set to &e" + var8 + "s&a."));
                     return true;
                  default:
                     return true;
               }
            case "reveal":
               if (!var1.hasPermission("economysmpcore.combatlog.cooldown")) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
                  return true;
               }

               if (var4.length < 2) {
                  var1.sendMessage(this.B("&cUsage: /combatlog reveal <blocks> &7- e.g. /combatlog reveal 5"));
                  return true;
               }

               Integer var11 = this.A(var4[1]);
               if (var11 == null || var11 < 0) {
                  var1.sendMessage(this.B("&cInvalid distance. Use a number of blocks like &e5&c."));
                  return true;
               }

               this.V.set("SETTINGS.REGION-RESTRICTION.BARRIER.REVEAL-DISTANCE", var11);
               this.G();
               var1.sendMessage(this.B("&aRegion wall will now appear within &e" + var11 + " blocks&a of the border."));
               break;
            case "reload":
               if (!var1.hasPermission("economysmpcore.combatlog.reload")) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission.");
                  return true;
               }

               this.F();
               var1.sendMessage(String.valueOf(ChatColor.GREEN) + "CombatLog config reloaded.");
               break;
            case "deathmsg":
               if (!(var1 instanceof Player)) {
                  var1.sendMessage("Only players can toggle death messages.");
                  return true;
               }

               Player var7 = (Player)var1;
               this.E(var7);
               break;
            case "help":
               this.A(var1);
               break;
            default:
               this.A(var1);
         }

         return true;
      }
   }

   private void A(CommandSender var1) {
      var1.sendMessage(this.B("&8&m----------&r &bCombatLog &8&m----------"));
      var1.sendMessage(this.B("&b/combatlog deathmsg &7- toggle custom death messages"));
      var1.sendMessage(this.B("&b/combatlog reload &7- reload the config"));
      var1.sendMessage(this.B("&b/combatlog tag &7- combat-tag yourself for testing"));
      var1.sendMessage(this.B("&b/combatlog cooldown <pearl|firework> <seconds> &7- e.g. /combatlog cooldown pearl 5s"));
      var1.sendMessage(this.B("&b/combatlog reveal <blocks> &7- how close you must be to see the region wall"));
   }

   private Integer A(String var1) {
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim().toLowerCase();
         if (var2.endsWith("s")) {
            var2 = var2.substring(0, var2.length() - 1);
         }

         try {
            return Integer.parseInt(var2);
         } catch (NumberFormatException var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var2.getName().equalsIgnoreCase("combatlog")) {
         return List.of();
      } else if (var4.length != 1) {
         if (var4.length == 2 && var4[0].equalsIgnoreCase("cooldown") && var1.hasPermission("economysmpcore.combatlog.cooldown")) {
            String var9 = var4[1].toLowerCase();
            return (List)C.stream().filter((var1x) -> var1x.startsWith(var9)).collect(Collectors.toList());
         } else {
            return var4.length == 3 && var4[0].equalsIgnoreCase("cooldown") && var1.hasPermission("economysmpcore.combatlog.cooldown") ? List.of("1s", "5s", "10s", "15s", "30s") : List.of();
         }
      } else {
         String var5 = var4[0].toLowerCase();
         ArrayList var6 = new ArrayList();

         for(String var8 : B) {
            if ((!var8.equals("tag") || var1.hasPermission("economysmpcore.combatlog.tag")) && (!var8.equals("reload") || var1.hasPermission("economysmpcore.combatlog.reload")) && (!var8.equals("cooldown") || var1.hasPermission("economysmpcore.combatlog.cooldown")) && (!var8.equals("reveal") || var1.hasPermission("economysmpcore.combatlog.cooldown"))) {
               var6.add(var8);
            }
         }

         return (List)var6.stream().filter((var1x) -> var1x.startsWith(var5)).collect(Collectors.toList());
      }
   }

   private static class _A {
      final List<Location> A;
      final int B;

      _A(List<Location> var1, int var2) {
         this.A = var1;
         this.B = var2;
      }
   }

   private static class _B {
      boolean A(Location var1, List<String> var2) {
         if (var2 != null && !var2.isEmpty()) {
            RegionContainer var3 = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery var4 = var3.createQuery();
            ApplicableRegionSet var5 = var4.getApplicableRegions(BukkitAdapter.adapt(var1));
            if (var5 != null && var5.size() != 0) {
               for(String var7 : var2) {
                  if (var7 != null && !var7.isBlank()) {
                     boolean var8 = var5.getRegions().stream().anyMatch((var1x) -> var1x.getId().equalsIgnoreCase(var7));
                     if (var8) {
                        return true;
                     }
                  }
               }

               return false;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      Location[] A(World var1, String var2) {
         if (var1 != null && var2 != null && !var2.isBlank()) {
            RegionContainer var3 = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager var4 = var3.get(BukkitAdapter.adapt(var1));
            if (var4 == null) {
               return null;
            } else {
               ProtectedRegion var5 = var4.getRegion(var2);
               if (var5 == null) {
                  var5 = (ProtectedRegion)var4.getRegions().values().stream().filter((var1x) -> var1x.getId().equalsIgnoreCase(var2)).findFirst().orElse((Object)null);
               }

               if (var5 == null) {
                  return null;
               } else {
                  BlockVector3 var6 = var5.getMinimumPoint();
                  BlockVector3 var7 = var5.getMaximumPoint();
                  Location var8 = new Location(var1, (double)var6.getX(), (double)var6.getY(), (double)var6.getZ());
                  Location var9 = new Location(var1, (double)var7.getX(), (double)var7.getY(), (double)var7.getZ());
                  return new Location[]{var8, var9};
               }
            }
         } else {
            return null;
         }
      }
   }
}
