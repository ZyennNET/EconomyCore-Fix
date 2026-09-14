package com.prismcore.survival.auction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.StringUtil;

public class AHCommand implements CommandExecutor, TabCompleter {
   private final AuctionController controller;

   public AHCommand(AuctionController var1) {
      this.controller = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(Utils.formatColors("&#ff4444Only players can use this command!"));
         return true;
      } else {
         FileConfiguration var6 = this.controller.getConfig();
         Sound var7 = Sound.valueOf(var6.getString("sounds.villager-no"));
         if (var4.length == 0) {
            var5.removeMetadata("ah-filter", this.controller.getPlugin());
            var5.removeMetadata("ah-admin-view", this.controller.getPlugin());
            this.controller.getAuctionManager().refresh(() -> GUIHandler.openMainGUI(var5, 1, this.controller));
            return true;
         } else if (var4.length >= 1 && var4[0].trim().equalsIgnoreCase("admin")) {
            if (!var5.hasPermission("auction.admin")) {
               var5.removeMetadata("ah-filter", this.controller.getPlugin());
               var5.removeMetadata("ah-admin-view", this.controller.getPlugin());
               GUIHandler.openMainGUI(var5, 1, this.controller);
               return true;
            } else {
               var5.setMetadata("ah-admin-view", new FixedMetadataValue(this.controller.getPlugin(), true));
               var5.removeMetadata("ah-admin-player-filter", this.controller.getPlugin());
               var5.removeMetadata("ah-admin-target", this.controller.getPlugin());
               GUIHandler.openAdminPlayerListGUI(var5, 1, this.controller);
               return true;
            }
         } else if (var4.length >= 2 && var4[0].equalsIgnoreCase("sell")) {
            int var21 = var6.getInt("settings.max-auction-listed");
            int var23 = 0;

            for(AuctionItem var27 : this.controller.getAuctionManager().getActiveItems()) {
               if (var27.getSeller().equals(var5.getName())) {
                  ++var23;
               }
            }

            if (var23 >= var21) {
               String var26 = var6.getString("messages.max-listings").replace("{count}", String.valueOf(var23)).replace("{max}", String.valueOf(var21));
               var5.sendMessage(Utils.formatColors(var26));
               var5.playSound(var5.getLocation(), var7, 1.0F, 1.0F);
               return true;
            } else {
               String var25 = var4[1];
               double var28 = Utils.parsePrice(var25);
               if (!Double.isNaN(var28) && !Double.isInfinite(var28) && !(var28 <= (double)0.0F)) {
                  double var13 = var6.getDouble("settings.max-auction-price", 1.0E12);
                  if (var28 > var13) {
                     var5.sendMessage(Utils.formatColors(var6.getString("messages.max-price-exceeded", "&#ff4444Price exceeds the maximum allowed limit!")));
                     var5.playSound(var5.getLocation(), var7, 1.0F, 1.0F);
                     return true;
                  } else {
                     ItemStack var15 = var5.getInventory().getItemInMainHand();
                     if (var15 != null && !var15.getType().isAir() && var15.getAmount() >= 1) {
                        String var16 = var15.getType().name();

                        for(Object var19 : var6.getStringList("settings.disabled-items")) {
                           String var20 = (String)var19;
                           if (var20.trim().equalsIgnoreCase(var16)) {
                              var5.sendMessage(Utils.formatColors(var6.getString("messages.disabled-item")));
                              var5.playSound(var5.getLocation(), var7, 1.0F, 1.0F);
                              return true;
                           }
                        }

                        GUIHandler.openSellConfirm(var5, var28, this.controller);
                        return true;
                     } else {
                        var5.sendMessage(Utils.formatColors("&#ff4444You must hold an item to sell!"));
                        var5.playSound(var5.getLocation(), var7, 1.0F, 1.0F);
                        return true;
                     }
                  }
               } else {
                  var5.sendMessage(Utils.formatColors("&#ff4444Invalid price format. Use numbers or K/M/B/T suffix."));
                  var5.playSound(var5.getLocation(), var7, 1.0F, 1.0F);
                  return true;
               }
            }
         } else {
            StringBuilder var8 = new StringBuilder();

            for(String var12 : var4) {
               var8.append(var12).append(" ");
            }

            String var22 = var8.toString().trim().toLowerCase();
            var5.setMetadata("ah-filter", new FixedMetadataValue(this.controller.getPlugin(), var22));
            var5.removeMetadata("ah-admin-view", this.controller.getPlugin());
            GUIHandler.openMainGUI(var5, 1, this.controller);
            return true;
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         ArrayList var5 = new ArrayList();
         if (var1.hasPermission("auction.admin")) {
            var5.add("admin");
         }

         var5.add("<search>");
         return (List)StringUtil.copyPartialMatches(var4[0], var5, new ArrayList());
      } else {
         return Collections.emptyList();
      }
   }
}
