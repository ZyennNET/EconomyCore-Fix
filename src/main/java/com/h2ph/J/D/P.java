package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class P implements CommandExecutor, Listener {
   private final PrismSurvival B;
   private File D;
   private FileConfiguration A;
   private String C;
   private final Map<Integer, String> E = new HashMap();

   public P(PrismSurvival var1) {
      this.B = var1;
      this.loadConfig();
   }

   public void loadConfig() {
      this.D = new File(this.B.getDataFolder(), "serverinfo/config.yml");
      if (!this.D.exists()) {
         this.D.getParentFile().mkdirs();
         this.B.saveResource("serverinfo/config.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(this.D);
      this.C = com.h2ph.b.D.A(this.A.getString("title", "&fServer Info"));
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         this.A(var5);
         return true;
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      }
   }

   private void A(Player var1) {
      if (this.A == null) {
         this.loadConfig();
      }

      int var2 = this.A.getInt("size", 27);
      var2 = Math.max(9, Math.min(54, var2 / 9 * 9 == var2 ? var2 : (var2 / 9 + 1) * 9));
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, var2, this.C);
      this.E.clear();
      ConfigurationSection var4 = this.A.getConfigurationSection("items");
      if (var4 != null) {
         for(String var6 : var4.getKeys(false)) {
            String var7 = "items." + var6;
            int var8 = this.A.getInt(var7 + ".slot", -1);
            if (var8 >= 0 && var8 < var2) {
               String var9 = this.A.getString(var7 + ".material", "PAPER");
               Material var10 = Material.matchMaterial(var9);
               if (var10 == null) {
                  var10 = Material.PAPER;
               }

               String var11 = this.A.getString(var7 + ".name", "&fInfo");
               List var12 = this.A.getStringList(var7 + ".lore");
               var12 = com.h2ph.b.A.A(var12);
               ItemStack var13 = new ItemStack(var10);
               ItemMeta var14 = var13.getItemMeta();
               if (var14 != null) {
                  var14.setDisplayName(com.h2ph.b.D.A(var11));
                  var14.setLore(com.h2ph.b.D.A(var12));
                  var13.setItemMeta(var14);
               }

               var3.setItem(var8, var13);
               String var15 = this.A.getString(var7 + ".command", (String)null);
               if (var15 != null && !var15.isEmpty()) {
                  this.E.put(var8, var15);
               }
            }
         }
      }

      var1.openInventory(var3);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      HumanEntity var3 = var1.getWhoClicked();
      if (var3 instanceof Player var2) {
         if (var1.getView().getTitle().equals(this.C)) {
            var1.setCancelled(true);
            String var4 = (String)this.E.get(var1.getRawSlot());
            if (var4 != null) {
               var2.playSound(var2.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);
               var2.closeInventory();
               Bukkit.dispatchCommand(var2, var4.startsWith("/") ? var4.substring(1) : var4);
            }

         }
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent var1) {
      if (var1.getView().getTitle().equals(this.C)) {
         var1.setCancelled(true);
      }

   }
}
