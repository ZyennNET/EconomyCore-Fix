package com.h2ph.g;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.StringUtil;

public class A implements Listener, CommandExecutor, TabCompleter {
   private final PrismSurvival C;
   private FileConfiguration A;
   private File E;
   private final Map<Integer, List<_B>> D = new HashMap();
   private final Map<Location, _A> B = new ConcurrentHashMap();

   public A(PrismSurvival var1) {
      this.C = var1;
      this.B();
      this.D();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("spawnstash").setExecutor(this);
      var1.getCommand("spawnstash").setTabCompleter(this);
      var1.getLogger().info("SpawnStashManager loaded (custom layouts, removable within 10 minutes).");
   }

   private void B() {
      this.E = new File(this.C.getDataFolder(), "spawnstash/config.yml");
      if (!this.E.exists()) {
         this.E.getParentFile().mkdirs();
         this.C();
      }

      this.A = YamlConfiguration.loadConfiguration(this.E);
      if (!this.A.contains("layouts.1") || !this.A.contains("layouts.2")) {
         this.C.getLogger().warning("SpawnStash config incomplete, recreating default.");
         this.C();
         this.A = YamlConfiguration.loadConfiguration(this.E);
      }

   }

   private void C() {
      this.A = new YamlConfiguration();
      this.A.set("messages.prefix", "&8[&5SpawnStash&8]&r ");
      this.A.set("messages.placed", "&aStash type %type% placed at %x%, %y%, %z%.");
      this.A.set("messages.removed", "&aStash type %type% removed (placed %minutes% minutes ago).");
      this.A.set("messages.not-found", "&cNo stash found at this location or older than 10 minutes.");
      this.A.set("messages.not-found-all", "&cNo stashes placed by you in the last 10 minutes.");
      this.A.set("messages.removed-all", "&aRemoved %count% stash(es) placed in the last 10 minutes.");
      this.A.set("messages.no-permission", "&cYou don't have permission.");
      this.A.set("messages.invalid-type", "&cInvalid stash type. Use 1 or 2.");
      this.A.set("messages.unknown-top-block", "&cUnknown top block type. Use 'stone', 'deepslate', or 'air'.");
      this.A.set("messages.remove-usage", "&cUsage: /spawnstash remove <type|all> [time]");
      this.A.set("messages.reloaded", "&aSpawnStash configuration reloaded.");
      ArrayList var1 = new ArrayList();

      for(int var2 = -1; var2 <= 1; ++var2) {
         for(int var3 = 0; var3 <= 3; ++var3) {
            var1.add(this.A(var2, 0, var3, "STONE"));
         }
      }

      for(int var5 = 0; var5 <= 3; ++var5) {
         var1.add(this.A(-1, 1, var5, "SPAWNER", "SKELETON"));
      }

      for(int var6 = 0; var6 <= 3; ++var6) {
         var1.add(this.A(-1, 2, var6, "CHEST", "double"));
      }

      var1.add(this.A(1, 1, 1, "ENDER_CHEST"));
      var1.add(this.A(1, 2, 1, "PURPLE_SHULKER_BOX"));
      this.A.set("layouts.1.blocks", var1);
      ArrayList var7 = new ArrayList();

      for(int var8 = 0; var8 <= 3; ++var8) {
         var7.add(this.A(var8, 0, 0, "STONE"));
      }

      var7.add(this.A(0, 1, 0, "SPAWNER", "SKELETON"));
      var7.add(this.A(1, 1, 0, "AMETHYST_BLOCK"));
      var7.add(this.A(1, 2, 0, "AMETHYST_CLUSTER"));
      this.A.set("layouts.2.blocks", var7);
      this.A.set("layouts.2.spawner-top-block.default", "AIR");

      try {
         this.A.save(this.E);
      } catch (Exception var4) {
         this.C.getLogger().warning("Could not save default spawnstash config: " + var4.getMessage());
      }

   }

   private Map<String, Object> A(int var1, int var2, int var3, String var4) {
      HashMap var5 = new HashMap();
      var5.put("x", var1);
      var5.put("y", var2);
      var5.put("z", var3);
      var5.put("material", var4);
      return var5;
   }

   private Map<String, Object> A(int var1, int var2, int var3, String var4, String var5) {
      Map var6 = this.A(var1, var2, var3, var4);
      if (var4.equals("SPAWNER")) {
         var6.put("spawner-type", var5);
      } else if (var4.equals("CHEST")) {
         var6.put("double", var5);
      }

      return var6;
   }

   private void D() {
      this.D.clear();
      ConfigurationSection var1 = this.A.getConfigurationSection("layouts");
      if (var1 == null) {
         this.C.getLogger().warning("No 'layouts' section in spawnstash config.");
      } else {
         for(String var3 : var1.getKeys(false)) {
            try {
               int var4 = Integer.parseInt(var3);
               ConfigurationSection var5 = var1.getConfigurationSection(var3);
               if (var5 != null) {
                  List var6 = var5.getMapList("blocks");
                  ArrayList var7 = new ArrayList();

                  for(Map var9 : var6) {
                     int var10 = ((Number)var9.get("x")).intValue();
                     int var11 = ((Number)var9.get("y")).intValue();
                     int var12 = ((Number)var9.get("z")).intValue();
                     String var13 = (String)var9.get("material");
                     Material var14 = Material.getMaterial(var13);
                     if (var14 != null) {
                        _B var15 = new _B(var10, var11, var12, var14);
                        if (var9.containsKey("spawner-type")) {
                           try {
                              var15.D = EntityType.valueOf(((String)var9.get("spawner-type")).toUpperCase());
                           } catch (IllegalArgumentException var17) {
                           }
                        }

                        if (var9.containsKey("double")) {
                           var15.B = true;
                        }

                        var7.add(var15);
                     }
                  }

                  this.D.put(var4, var7);
               }
            } catch (NumberFormatException var18) {
            }
         }

      }
   }

   private void A() {
      this.B();
      this.D();
      this.C.getLogger().info("SpawnStash configuration reloaded.");
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player && var1.hasPermission("economysmpcore.spawnstash.admin")) {
         if (var4.length == 1) {
            ArrayList var8 = new ArrayList(Arrays.asList("remove", "reload", "1", "2"));
            return (List)StringUtil.copyPartialMatches(var4[0], var8, new ArrayList());
         } else if (var4.length == 2 && var4[0].equalsIgnoreCase("remove")) {
            ArrayList var7 = new ArrayList(Arrays.asList("1", "2", "all"));
            return (List)StringUtil.copyPartialMatches(var4[1], var7, new ArrayList());
         } else if (var4.length == 3 && var4[0].equalsIgnoreCase("remove") && !var4[1].equalsIgnoreCase("all")) {
            ArrayList var6 = new ArrayList(Arrays.asList("stone", "deepslate", "air"));
            return (List)StringUtil.copyPartialMatches(var4[2], var6, new ArrayList());
         } else if (var4.length == 2 && (var4[0].equals("1") || var4[0].equals("2")) && var4[0].equals("2")) {
            ArrayList var5 = new ArrayList(Arrays.asList("stone", "deepslate", "air"));
            return (List)StringUtil.copyPartialMatches(var4[1], var5, new ArrayList());
         } else {
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         if (!var5.hasPermission("economysmpcore.spawnstash.admin")) {
            var5.sendMessage(this.A(this.A.getString("messages.no-permission", "&cNo permission.")));
            return true;
         } else if (var4.length == 0) {
            this.A(var5, 1, (Material)null);
            return true;
         } else if (var4[0].equalsIgnoreCase("reload")) {
            this.A();
            var5.sendMessage(this.A(this.A.getString("messages.reloaded", "&aSpawnStash configuration reloaded.")));
            return true;
         } else if (var4[0].equalsIgnoreCase("remove")) {
            if (var4.length < 2) {
               var5.sendMessage(this.A(this.A.getString("messages.remove-usage", "&cUsage: /spawnstash remove <type|all> [time]")));
               return true;
            } else {
               String var14 = var4[1];
               if (var14.equalsIgnoreCase("all")) {
                  this.A(var5);
                  return true;
               } else {
                  try {
                     int var15 = Integer.parseInt(var14);
                     this.A(var5, var15);
                     return true;
                  } catch (NumberFormatException var12) {
                     var5.sendMessage(this.A(this.A.getString("messages.invalid-type", "&cInvalid stash type.")));
                     return true;
                  }
               }
            }
         } else {
            int var6 = 1;
            if (var4.length > 0) {
               try {
                  var6 = Integer.parseInt(var4[0]);
               } catch (NumberFormatException var13) {
                  var5.sendMessage(this.A(this.A.getString("messages.invalid-type", "&cInvalid stash type.")));
                  return true;
               }
            }

            List var7 = (List)this.D.get(var6);
            if (var7 == null) {
               var5.sendMessage(this.A(this.A.getString("messages.invalid-type", "&cInvalid stash type.")));
               return true;
            } else {
               Material var8 = Material.AIR;
               if (var6 == 2 && var4.length >= 2) {
                  switch (var4[1].toLowerCase()) {
                     case "stone":
                        var8 = Material.STONE;
                        break;
                     case "deepslate":
                        var8 = Material.DEEPSLATE;
                        break;
                     case "air":
                        var8 = Material.AIR;
                        break;
                     default:
                        var5.sendMessage(this.A(this.A.getString("messages.unknown-top-block", "&cUnknown top block type.")));
                        return true;
                  }
               }

               this.A(var5, var6, var8);
               return true;
            }
         }
      } else {
         var1.sendMessage(this.A("&cOnly players can use this command."));
         return true;
      }
   }

   private void A(Player var1, int var2, Material var3) {
      Location var4 = var1.getLocation().getBlock().getLocation().subtract((double)0.0F, (double)1.0F, (double)0.0F);
      List var5 = (List)this.D.get(var2);
      if (var5 == null) {
         var1.sendMessage(this.A(this.A.getString("messages.invalid-type", "&cInvalid stash type.")));
      } else {
         HashMap var6 = new HashMap();

         for(_B var8 : var5) {
            Location var9 = var4.clone().add((double)var8.A, (double)var8.F, (double)var8.E);
            var6.put(var9, var9.getBlock().getType());
         }

         if (var2 == 2 && var3 != null && var3 != Material.AIR) {
            Location var12 = var4.clone().add((double)0.0F, (double)2.0F, (double)0.0F);
            var6.put(var12, var12.getBlock().getType());
         }

         for(_B var16 : var5) {
            Block var17 = var4.clone().add((double)var16.A, (double)var16.F, (double)var16.E).getBlock();
            var17.setType(var16.C);
            if (var16.C == Material.SPAWNER && var16.D != null) {
               BlockState var11 = var17.getState();
               if (var11 instanceof CreatureSpawner) {
                  CreatureSpawner var10 = (CreatureSpawner)var11;
                  var10.setSpawnedType(var16.D);
                  var10.update();
                  continue;
               }
            }

            if (var16.C == Material.CHEST && var16.B) {
               Block var18 = var17.getRelative(1, 0, 0);
               var18.setType(Material.CHEST);
               var6.put(var18.getLocation(), var18.getType());
            }
         }

         if (var2 == 2 && var3 != null && var3 != Material.AIR) {
            Location var14 = var4.clone().add((double)0.0F, (double)2.0F, (double)0.0F);
            var14.getBlock().setType(var3);
         }

         this.B.put(var4, new _A(var2, System.currentTimeMillis(), var6));
         String var15 = this.A.getString("messages.placed", "&aStash type %type% placed at %x%, %y%, %z%.").replace("%type%", String.valueOf(var2)).replace("%x%", String.valueOf(var4.getBlockX())).replace("%y%", String.valueOf(var4.getBlockY())).replace("%z%", String.valueOf(var4.getBlockZ()));
         String var10002 = this.A.getString("messages.prefix", "&8[&5SpawnStash&8]&r ");
         var1.sendMessage(this.A(var10002 + var15));
      }
   }

   private void A(Player var1, int var2) {
      Location var3 = var1.getLocation().getBlock().getLocation();
      long var4 = System.currentTimeMillis();
      long var6 = 600000L;
      _A var8 = null;
      Location var9 = null;

      for(Map.Entry var11 : this.B.entrySet()) {
         Location var12 = (Location)var11.getKey();
         if (var12.getWorld().equals(var3.getWorld()) && var12.distanceSquared(var3) <= (double)10000.0F && ((_A)var11.getValue()).B == var2 && var4 - ((_A)var11.getValue()).C <= var6) {
            var8 = (_A)var11.getValue();
            var9 = var12;
            break;
         }
      }

      if (var8 == null) {
         var1.sendMessage(this.A(this.A.getString("messages.not-found", "&cNo stash found at this location or older than 10 minutes.")));
      } else {
         for(Map.Entry var15 : var8.A.entrySet()) {
            Block var16 = ((Location)var15.getKey()).getBlock();
            var16.setType((Material)var15.getValue());
         }

         this.B.remove(var9);
         long var14 = (var4 - var8.C) / 60000L;
         String var17 = this.A.getString("messages.removed", "&aStash type %type% removed (placed %minutes% minutes ago).").replace("%type%", String.valueOf(var8.B)).replace("%minutes%", String.valueOf(var14));
         String var10002 = this.A.getString("messages.prefix", "&8[&5SpawnStash&8]&r ");
         var1.sendMessage(this.A(var10002 + var17));
      }
   }

   private void A(Player var1) {
      long var2 = System.currentTimeMillis();
      long var4 = 600000L;
      ArrayList var6 = new ArrayList();
      int var7 = 0;

      for(Map.Entry var9 : this.B.entrySet()) {
         if (var2 - ((_A)var9.getValue()).C <= var4) {
            for(Map.Entry var11 : ((_A)var9.getValue()).A.entrySet()) {
               ((Location)var11.getKey()).getBlock().setType((Material)var11.getValue());
            }

            var6.add((Location)var9.getKey());
            ++var7;
         }
      }

      for(Location var14 : var6) {
         this.B.remove(var14);
      }

      if (var7 == 0) {
         var1.sendMessage(this.A(this.A.getString("messages.not-found-all", "&cNo stashes placed by you in the last 10 minutes.")));
      } else {
         String var13 = this.A.getString("messages.removed-all", "&aRemoved %count% stash(es) placed in the last 10 minutes.").replace("%count%", String.valueOf(var7));
         String var10002 = this.A.getString("messages.prefix", "&8[&5SpawnStash&8]&r ");
         var1.sendMessage(this.A(var10002 + var13));
      }

   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent var1) {
   }

   private String A(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private static class _A {
      final int B;
      final long C;
      final Map<Location, Material> A;

      _A(int var1, long var2, Map<Location, Material> var4) {
         this.B = var1;
         this.C = var2;
         this.A = var4;
      }
   }

   private static class _B {
      int A;
      int F;
      int E;
      Material C;
      EntityType D;
      boolean B;

      _B(int var1, int var2, int var3, Material var4) {
         this.A = var1;
         this.F = var2;
         this.E = var3;
         this.C = var4;
      }
   }
}
