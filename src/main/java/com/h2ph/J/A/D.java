package com.h2ph.J.A;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

public class D implements InventoryHolder {
   private final PrismSurvival G;
   private final UUID C;
   private Inventory B;
   private PlayerData D;
   private boolean A;
   private String E;
   private int F;
   private String K;
   private Location J;
   private final NamespacedKey L;
   private final NamespacedKey I;
   private static final String H = "economysmpcore.profile.punishments";

   public D(PrismSurvival var1, UUID var2) {
      this.G = var1;
      this.C = var2;
      this.L = new NamespacedKey(var1, "profile_action");
      this.I = new NamespacedKey(var1, "profile_home_index");
   }

   private void B() {
      Player var1 = Bukkit.getPlayer(this.C);
      this.A = var1 != null;
      this.D = this.G.getPlayerDataManager().get(this.C);
      if (this.D == null) {
         this.D = new PlayerData(this.C);
      }

      this.E = this.A ? var1.getName() : Bukkit.getOfflinePlayer(this.C).getName();
      this.F = this.A(this.C);

      try {
         com.h2ph.H.A var2 = this.G.getDonutTeamModule();
         com.h2ph.H.A._H var3 = var2.getPlayerTeam(this.C);
         this.K = var3 != null ? var3.D() : "None";
      } catch (Exception var4) {
         this.K = "None";
      }

      this.J = this.A ? var1.getLocation() : null;
   }

   private int A(UUID var1) {
      int var2 = 0;

      for(int var3 = 1; var3 <= 5; ++var3) {
         if (this.getHome(var1, var3) != null) {
            ++var2;
         }
      }

      return var2;
   }

   public UUID getTargetUuid() {
      return this.C;
   }

   public Location getHome(UUID var1, int var2) {
      File var3 = new File(this.G.getDataFolder(), "homes/" + var1.toString() + ".yml");
      if (!var3.exists()) {
         return null;
      } else {
         YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
         String var5 = "home" + var2;
         if (!var4.contains(var5 + ".world")) {
            return null;
         } else {
            String var6 = var4.getString(var5 + ".world");
            if (Bukkit.getWorld(var6) == null) {
               return null;
            } else {
               double var7 = var4.getDouble(var5 + ".x");
               double var9 = var4.getDouble(var5 + ".y");
               double var11 = var4.getDouble(var5 + ".z");
               float var13 = (float)var4.getDouble(var5 + ".yaw");
               float var14 = (float)var4.getDouble(var5 + ".pitch");
               return new Location(Bukkit.getWorld(var6), var7, var9, var11, var13, var14);
            }
         }
      }
   }

   public void open(Player var1) {
      this.B();
      int var2 = this.G.getConfig().getInt("profile.gui-size", 54);
      String var3 = this.G.getConfig().getString("profile.gui-title", "&8{username}'s Profile").replace("{username}", this.E);
      this.B = Bukkit.createInventory(this, var2, ChatColor.translateAlternateColorCodes('&', var3));
      this.A();

      for(Map var6 : this.G.getConfig().getMapList("profile.items")) {
         int var7 = ((Number)var6.get("slot")).intValue();
         if (var7 >= 0 && var7 < var2) {
            String var8 = (String)var6.get("action");
            if (!"PUNISHMENT_HISTORY".equals(var8) || var1.hasPermission("economysmpcore.profile.punishments")) {
               String var9 = (String)var6.get("material");
               Material var10 = Material.getMaterial(var9);
               if (var10 == null) {
                  var10 = Material.STONE;
               }

               String var11 = (String)var6.get("name");
               List var12 = (List)var6.get("lore");
               Integer var13 = var6.containsKey("home-index") ? ((Number)var6.get("home-index")).intValue() : null;
               if (var8 != null && var8.equals("HOME") && var13 != null) {
                  Location var14 = this.getHome(this.C, var13);
                  var10 = var14 == null ? Material.GRAY_BED : Material.LIGHT_BLUE_BED;
               }

               ItemStack var16 = this.A(var10, var11, var12, this.E, this.F, this.J, var13);
               if (var8 != null) {
                  ItemMeta var15 = var16.getItemMeta();
                  if (var15 != null) {
                     var15.getPersistentDataContainer().set(this.L, PersistentDataType.STRING, var8);
                     if (var13 != null) {
                        var15.getPersistentDataContainer().set(this.I, PersistentDataType.INTEGER, var13);
                     }

                     var16.setItemMeta(var15);
                  }
               }

               this.B.setItem(var7, var16);
            }
         }
      }

      var1.openInventory(this.B);
   }

   private ItemStack A(Material var1, String var2, List<String> var3, String var4, int var5, Location var6, Integer var7) {
      ItemStack var8 = new ItemStack(var1);
      ItemMeta var9 = var8.getItemMeta();
      if (var9 != null) {
         if (var2 != null) {
            String var10 = var2.replace("{username}", var4).replace("{homes}", String.valueOf(var5));
            var9.setDisplayName(ChatColor.translateAlternateColorCodes('&', var10));
         }

         if (var3 != null) {
            ArrayList var15 = new ArrayList();

            for(String var12 : var3) {
               String var13 = var12.replace("{username}", var4).replace("{homes}", String.valueOf(var5));
               if (var6 != null && var13.contains("{world}")) {
                  var13 = var13.replace("{world}", this.A(var6.getWorld().getName())).replace("{x}", String.valueOf(var6.getBlockX())).replace("{y}", String.valueOf(var6.getBlockY())).replace("{z}", String.valueOf(var6.getBlockZ()));
               }

               Player var14 = Bukkit.getPlayer(this.C);
               if (var14 != null && var13.contains("%")) {
                  var13 = PlaceholderAPI.setPlaceholders(var14, var13);
               }

               var15.add(ChatColor.translateAlternateColorCodes('&', var13));
            }

            var9.setLore(var15);
         }

         var8.setItemMeta(var9);
      }

      if (var1 == Material.PLAYER_HEAD && var9 instanceof SkullMeta var16) {
         var16.setOwningPlayer(Bukkit.getOfflinePlayer(this.C));
         var8.setItemMeta(var16);
      }

      return var8;
   }

   private void A() {
      ItemStack var1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var2 = var1.getItemMeta();
      var2.setDisplayName(" ");
      var1.setItemMeta(var2);

      for(int var3 = 0; var3 < this.B.getSize(); ++var3) {
         if (this.B.getItem(var3) == null) {
            this.B.setItem(var3, var1);
         }
      }

   }

   private String A(String var1) {
      if (var1 == null) {
         return "Unknown";
      } else {
         String var2 = var1.toLowerCase();
         if (var2.contains("nether")) {
            return "Nether";
         } else {
            return var2.contains("end") ? "End" : "Overworld";
         }
      }
   }

   public Inventory getInventory() {
      return this.B;
   }

   public Location getTeamHome(UUID var1) {
      File var2 = new File(this.G.getDataFolder(), "homes/" + var1.toString() + ".yml");
      if (!var2.exists()) {
         return null;
      } else {
         YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
         if (!var3.contains("teamhome.world")) {
            return null;
         } else {
            String var4 = var3.getString("teamhome.world");
            if (Bukkit.getWorld(var4) == null) {
               return null;
            } else {
               double var5 = var3.getDouble("teamhome.x");
               double var7 = var3.getDouble("teamhome.y");
               double var9 = var3.getDouble("teamhome.z");
               float var11 = (float)var3.getDouble("teamhome.yaw");
               float var12 = (float)var3.getDouble("teamhome.pitch");
               return new Location(Bukkit.getWorld(var4), var5, var7, var9, var11, var12);
            }
         }
      }
   }
}
