package com.prismcore.survival.tools;

import java.util.HashSet;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ShovelBlockBreakListener implements Listener {
   private static final ThreadLocal<Boolean> DIGGING = ThreadLocal.withInitial(() -> false);
   private final ToolsManager manager;

   public ShovelBlockBreakListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent var1) {
      Player var3 = var1.getPlayer();
      ItemStack var4 = var3.getInventory().getItemInMainHand();
      if (var4 == null || !var4.hasItemMeta() || !var4.getItemMeta().getPersistentDataContainer().has(ToolsManager.MULTI_KEY, PersistentDataType.BYTE)) {
         if (!(Boolean)DIGGING.get()) {
            Player var5 = var1.getPlayer();
            ItemStack var6 = var5.getInventory().getItemInMainHand();
            ConfigurationSection var7 = this.manager.getConfig().getConfigurationSection("shovel");
            if (var6 != null && var7 != null) {
               if (var6.getType() == Material.valueOf(var7.getString("material"))) {
                  ItemMeta var8 = var6.getItemMeta();
                  if (var8 != null && (var8.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG) || var8.getPersistentDataContainer().has(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG))) {
                     for(String var10 : var7.getConfigurationSection("enchantments").getKeys(false)) {
                        Enchantment var11 = Enchantment.getByName(var10);
                        if (var11 == null || !var8.hasEnchant(var11)) {
                           return;
                        }
                     }

                     Block var21 = var1.getBlock();
                     List var22 = var7.getStringList("disabled-blocks");
                     if (!var22.contains(var21.getType().name())) {
                        var1.setDropItems(false);
                        int var23 = var7.getInt("radius.x", 1);
                        int var12 = var7.getInt("radius.z", 0);
                        var12 = var7.getInt("radius.z", 0);
                        BlockFace var13 = Utils.getBlockFace(var5);
                        HashSet var14 = new HashSet();
                        if (var13 != BlockFace.UP && var13 != BlockFace.DOWN) {
                           if (var13 == BlockFace.NORTH || var13 == BlockFace.SOUTH) {
                              for(int var26 = -var23; var26 <= var23; ++var26) {
                                 for(int var29 = -var23; var29 <= var23; ++var29) {
                                    for(int var32 = -var12; var32 <= var12; ++var32) {
                                       var14.add(var21.getRelative(var26, var29, var32));
                                    }
                                 }
                              }
                           } else if (var13 == BlockFace.EAST || var13 == BlockFace.WEST) {
                              for(int var25 = -var23; var25 <= var23; ++var25) {
                                 for(int var28 = -var23; var28 <= var23; ++var28) {
                                    for(int var31 = -var12; var31 <= var12; ++var31) {
                                       var14.add(var21.getRelative(var31, var28, var25));
                                    }
                                 }
                              }
                           } else {
                              var14.add(var21);
                           }
                        } else {
                           for(int var15 = -var23; var15 <= var23; ++var15) {
                              for(int var16 = -var23; var16 <= var23; ++var16) {
                                 for(int var17 = -var12; var17 <= var12; ++var17) {
                                    var14.add(var21.getRelative(var15, var17, var16));
                                 }
                              }
                           }
                        }

                        int var27 = var7.getInt("particle.count");
                        Color var30 = Color.fromRGB(var7.getInt("particle.color.r"), var7.getInt("particle.color.g"), var7.getInt("particle.color.b"));
                        Particle var33 = Particle.valueOf(var7.getString("particle.type"));

                        for(Block var19 : var14) {
                           BlockBreakEvent var20 = new BlockBreakEvent(var19, var5);
                           DIGGING.set(true);
                           Bukkit.getPluginManager().callEvent(var20);
                           DIGGING.set(false);
                           if (!var20.isCancelled()) {
                              var19.breakNaturally(var6);
                              var19.getWorld().spawnParticle(var33, var19.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), var27, new Particle.DustOptions(var30, 1.0F));
                           }
                        }

                     }
                  }
               }
            }
         }
      }
   }
}
