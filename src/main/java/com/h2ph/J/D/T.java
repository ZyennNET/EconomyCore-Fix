package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class T implements CommandExecutor, Listener {
   private final PrismSurvival B;
   private FileConfiguration A;
   private File C;

   public T(PrismSurvival var1) {
      this.B = var1;
      this.loadConfig();
   }

   public void loadConfig() {
      this.C = new File(this.B.getDataFolder(), "survival/rules/media.yml");
      if (!this.C.exists()) {
         this.B.saveResource("survival/rules/media.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(this.C);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else {
         this.A(var5);
         return true;
      }
   }

   private void A(Player var1) {
      if (this.A == null) {
         this.loadConfig();
      }

      String var2 = ChatColor.translateAlternateColorCodes('&', this.A.getString("gui.title", "&8ᴍᴇᴅɪᴀ ʀᴀɴᴋ"));
      int var3 = this.A.getInt("gui.size", 27);
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, var3, var2);
      if (this.A.contains("gui.items")) {
         for(String var6 : this.A.getConfigurationSection("gui.items").getKeys(false)) {
            String var7 = "gui.items." + var6;
            int var8 = this.A.getInt(var7 + ".slot");
            String var9 = this.A.getString(var7 + ".material", "PAPER");
            String var10 = this.A.getString(var7 + ".name", "&5ᴍᴇᴅɪᴀ ʀᴀɴᴋ");
            List var11 = this.A.getStringList(var7 + ".lore");
            Material var12 = Material.matchMaterial(var9);
            if (var12 == null) {
               var12 = Material.PAPER;
            }

            ItemStack var13 = new ItemStack(var12);
            ItemMeta var14 = var13.getItemMeta();
            if (var14 != null) {
               var14.setDisplayName(ChatColor.translateAlternateColorCodes('&', var10));
               ArrayList var15 = new ArrayList();

               for(String var17 : var11) {
                  var15.add(ChatColor.translateAlternateColorCodes('&', var17));
               }

               var14.setLore(var15);
               var13.setItemMeta(var14);
            }

            if (var8 >= 0 && var8 < var3) {
               var4.setItem(var8, var13);
            }
         }
      }

      var1.openInventory(var4);
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', this.A.getString("gui.title", "&8ᴍᴇᴅɪᴀ ʀᴀɴᴋ")))) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent var1) {
      if (var1.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', this.A.getString("gui.title", "&8ᴍᴇᴅɪᴀ ʀᴀɴᴋ")))) {
         var1.setCancelled(true);
      }

   }
}
