package com.prismcore.survival.tools;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ShardBoosterListener implements Listener {
   private final PrismSurvival plugin;
   private final ToolsManager manager;

   public ShardBoosterListener(ToolsManager var1, PrismSurvival var2) {
      this.manager = var1;
      this.plugin = var2;
   }

   @EventHandler
   public void onPotionDrink(PlayerItemConsumeEvent var1) {
      ItemStack var2 = var1.getItem();
      if (var2 != null && var2.hasItemMeta()) {
         ItemMeta var3 = var2.getItemMeta();
         if (var3 != null) {
            if (var3.getPersistentDataContainer().has(ToolsManager.BOOSTER_KEY, PersistentDataType.BYTE)) {
               Player var4 = var1.getPlayer();
               long var5 = 86400L;
               if (var3.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG)) {
                  var5 = (Long)var3.getPersistentDataContainer().get(ToolsManager.REMAINING_KEY, PersistentDataType.LONG);
               }

               long var7 = System.currentTimeMillis() + var5 * 1000L;
               PlayerData var9 = this.plugin.getPlayerDataManager().get(var4.getUniqueId());
               var9.setShardBoosterExpiry(var7);
               this.plugin.getPlayerDataManager().savePlayer(var4.getUniqueId());
               ConfigurationSection var10 = this.manager.getConfig().getConfigurationSection("shardbooster");
               String var11 = var10 != null ? var10.getString("activation-sound", "ENTITY_PLAYER_LEVELUP") : "ENTITY_PLAYER_LEVELUP";

               try {
                  Sound var12 = Sound.valueOf(var11.toUpperCase());
                  var4.playSound(var4.getLocation(), var12, 1.0F, 1.0F);
               } catch (IllegalArgumentException var13) {
                  var4.playSound(var4.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
               }

               String var14 = var10 != null ? var10.getString("activation-message", "&aYou have activated your &5Shard Booster&a for 24h.") : "&aYou have activated your &5Shard Booster&a for 24h.";
               var4.sendMessage(ChatColor.translateAlternateColorCodes('&', var14));
            }
         }
      }
   }
}
