package com.h2ph.J.A;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class C implements InventoryHolder {
   private final PrismSurvival B;
   private final UUID D;
   private Inventory F;
   private int C;
   private int A;
   private List<_A> E;

   public C(PrismSurvival var1, UUID var2) {
      this.B = var1;
      this.D = var2;
   }

   public void open(Player var1) {
      this.B();
      this.C = 0;
      this.A(var1);
      var1.openInventory(this.F);
   }

   private void B() {
      this.E = new ArrayList();
      File var1 = new File(this.B.getDataFolder(), "homes/" + this.D.toString() + ".yml");
      if (!var1.exists()) {
         this.A = 1;
      } else {
         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);

         for(int var3 = 1; var3 <= 5; ++var3) {
            String var4 = "home" + var3;
            if (var2.contains(var4 + ".world")) {
               String var5 = var2.getString(var4 + ".world");
               if (Bukkit.getWorld(var5) != null) {
                  double var6 = var2.getDouble(var4 + ".x");
                  double var8 = var2.getDouble(var4 + ".y");
                  double var10 = var2.getDouble(var4 + ".z");
                  float var12 = (float)var2.getDouble(var4 + ".yaw");
                  float var13 = (float)var2.getDouble(var4 + ".pitch");
                  Location var14 = new Location(Bukkit.getWorld(var5), var6, var8, var10, var12, var13);
                  this.E.add(new _A("Home " + var3, var14));
               }
            }
         }

         this.A = (int)Math.ceil((double)this.E.size() / (double)45.0F);
         if (this.A < 1) {
            this.A = 1;
         }

      }
   }

   private void A(Player var1) {
      String var2 = this.A();
      String var3 = ChatColor.translateAlternateColorCodes('&', "&8" + var2 + "'s Homes (Page " + (this.C + 1) + "/" + this.A + ")");
      this.F = Bukkit.createInventory(this, 54, var3);
      this.C();
      int var4 = this.C * 45;
      int var5 = Math.min(var4 + 45, this.E.size());
      int var6 = 0;

      for(int var7 = var4; var7 < var5; ++var7) {
         _A var8 = (_A)this.E.get(var7);
         ItemStack var9 = new ItemStack(Material.RED_BED);
         ItemMeta var10 = var9.getItemMeta();
         var10.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b" + var8.B));
         Location var11 = var8.A;
         String var12 = this.A(var11.getWorld().getName());
         ArrayList var13 = new ArrayList();
         var13.add(ChatColor.translateAlternateColorCodes('&', "&7World: &f" + var12));
         int var10002 = var11.getBlockX();
         var13.add(ChatColor.translateAlternateColorCodes('&', "&7X: &f" + var10002 + " Y: &f" + var11.getBlockY() + " Z: &f" + var11.getBlockZ()));
         var13.add(ChatColor.translateAlternateColorCodes('&', "&aClick to teleport"));
         var10.setLore(var13);
         var9.setItemMeta(var10);
         this.F.setItem(var6++, var9);
      }

      if (this.C > 0) {
         this.F.setItem(45, this.A(Material.ARROW, "&aPrevious Page"));
      }

      if (this.C < this.A - 1) {
         this.F.setItem(53, this.A(Material.ARROW, "&aNext Page"));
      }

      this.F.setItem(49, this.A(Material.BARRIER, "&cClose"));
   }

   private void C() {
      ItemStack var1 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var2 = var1.getItemMeta();
      var2.setDisplayName(" ");
      var1.setItemMeta(var2);

      for(int var3 = 0; var3 < 54; ++var3) {
         this.F.setItem(var3, var1);
      }

   }

   private ItemStack A(Material var1, String var2) {
      ItemStack var3 = new ItemStack(var1);
      ItemMeta var4 = var3.getItemMeta();
      var4.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
      var3.setItemMeta(var4);
      return var3;
   }

   private String A() {
      Player var1 = Bukkit.getPlayer(this.D);
      return var1 != null ? var1.getName() : Bukkit.getOfflinePlayer(this.D).getName();
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

   public boolean hasPreviousPage() {
      return this.C > 0;
   }

   public boolean hasNextPage() {
      return this.C < this.A - 1;
   }

   public void previousPage(Player var1) {
      --this.C;
      this.A(var1);
      var1.openInventory(this.F);
   }

   public void nextPage(Player var1) {
      ++this.C;
      this.A(var1);
      var1.openInventory(this.F);
   }

   public Location getHomeAtSlot(int var1) {
      int var2 = this.C * 45 + var1;
      return var2 >= 0 && var2 < this.E.size() ? ((_A)this.E.get(var2)).A : null;
   }

   public String getHomeNameAtSlot(int var1) {
      int var2 = this.C * 45 + var1;
      return var2 >= 0 && var2 < this.E.size() ? ((_A)this.E.get(var2)).B : "Home";
   }

   public Inventory getInventory() {
      return this.F;
   }

   private static class _A {
      String B;
      Location A;

      _A(String var1, Location var2) {
         this.B = var1;
         this.A = var2;
      }
   }
}
