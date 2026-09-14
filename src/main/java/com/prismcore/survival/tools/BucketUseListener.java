package com.prismcore.survival.tools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class BucketUseListener implements Listener {
   private final ToolsManager manager;

   public BucketUseListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onBucketFill(PlayerBucketFillEvent var1) {
      Player var2 = var1.getPlayer();
      ItemStack var3 = var2.getInventory().getItemInMainHand();
      if (var3 != null && var3.hasItemMeta()) {
         ItemMeta var4 = var3.getItemMeta();
         if (var4 != null) {
            if (var4.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG) || var4.getPersistentDataContainer().has(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG)) {
               Block var5 = var1.getBlockClicked();
               if (var5 == null) {
                  var1.setCancelled(true);
               } else if (var5.getType() != Material.WATER) {
                  var1.setCancelled(true);
               } else {
                  ConfigurationSection var6 = this.manager.getConfig().getConfigurationSection("bucket");
                  if (var6 == null) {
                     var1.setCancelled(true);
                  } else {
                     int var7 = var6.getInt("remove-radius.x");
                     int var8 = var6.getInt("remove-radius.y");
                     int var9 = var6.getInt("remove-radius.z");

                     for(int var10 = -var7; var10 <= var7; ++var10) {
                        for(int var11 = -var8; var11 <= var8; ++var11) {
                           for(int var12 = -var9; var12 <= var9; ++var12) {
                              Block var13 = var5.getRelative(var10, var11, var12);
                              if (var13.getType() == Material.WATER) {
                                 var13.setType(Material.AIR);
                              }
                           }
                        }
                     }

                     var1.setCancelled(true);
                  }
               }
            }
         }
      }
   }
}
