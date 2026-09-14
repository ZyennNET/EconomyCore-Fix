package com.prismcore.survival.tools;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
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

public class DrillBlockBreakListener implements Listener {
   private static final ThreadLocal<Boolean> DRILLING = ThreadLocal.withInitial(() -> false);
   private final ToolsManager manager;

   public DrillBlockBreakListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent var1) {
      if (!(Boolean)DRILLING.get()) {
         Player var4 = var1.getPlayer();
         ItemStack var5 = var4.getInventory().getItemInMainHand();
         if (var5 != null) {
            ItemMeta var6 = var5.getItemMeta();
            if (var6 != null) {
               if (!var6.getPersistentDataContainer().has(ToolsManager.MULTI_KEY, PersistentDataType.BYTE)) {
                  ConfigurationSection var7 = this.manager.getConfig().getConfigurationSection("drill");
                  if (var7 != null) {
                     String var8 = var7.getString("material", "DIAMOND_PICKAXE").toUpperCase();

                     Material var3;
                     try {
                        var3 = Material.valueOf(var8);
                     } catch (IllegalArgumentException var24) {
                        return;
                     }

                     if (var5.getType() == var3) {
                        ConfigurationSection var9 = var7.getConfigurationSection("enchantments");
                        if (var9 != null) {
                           for(String var11 : var9.getKeys(false)) {
                              Enchantment var12 = Enchantment.getByName(var11.toUpperCase());
                              if (var12 == null || !var6.hasEnchant(var12)) {
                                 return;
                              }
                           }

                           if (var6.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG) || var6.getPersistentDataContainer().has(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG)) {
                              boolean var25 = var7.getBoolean("play-sound", true);
                              if (var25) {
                                 String var28 = var7.getString("sound", "BLOCK_STONE_BREAK");

                                 Sound var26;
                                 try {
                                    var26 = Sound.valueOf(var28.toUpperCase());
                                 } catch (IllegalArgumentException var23) {
                                    var26 = Sound.BLOCK_STONE_BREAK;
                                 }

                                 var4.playSound(var4.getLocation(), var26, 1.0F, 1.0F);
                              }

                              Block var27 = var1.getBlock();
                              Set var29 = Set.copyOf(var7.getStringList("disabled-blocks"));
                              if (!var29.contains(var27.getType().name())) {
                                 int var13 = var7.getInt("particle.count", 10);
                                 Color var14 = Color.fromRGB(var7.getInt("particle.color.r", 255), var7.getInt("particle.color.g", 255), var7.getInt("particle.color.b", 255));

                                 Particle var2;
                                 try {
                                    var2 = Particle.valueOf(var7.getString("particle.type", "DUST").toUpperCase());
                                 } catch (IllegalArgumentException var22) {
                                    var2 = Particle.DUST;
                                 }

                                 int var15 = var7.getInt("radius.x", 1);
                                 int var16 = var7.getInt("radius.z", 1);
                                 BlockFace var17 = Utils.getBlockFace(var4);
                                 HashSet var18 = new HashSet();
                                 if (var17 != BlockFace.UP && var17 != BlockFace.DOWN) {
                                    if (var17 == BlockFace.NORTH || var17 == BlockFace.SOUTH) {
                                       for(int var31 = -var15; var31 <= var15; ++var31) {
                                          for(int var34 = -var15; var34 <= var15; ++var34) {
                                             for(int var37 = -var16; var37 <= var16; ++var37) {
                                                var18.add(var27.getRelative(var31, var34, var37));
                                             }
                                          }
                                       }
                                    } else if (var17 == BlockFace.EAST || var17 == BlockFace.WEST) {
                                       for(int var30 = -var15; var30 <= var15; ++var30) {
                                          for(int var33 = -var15; var33 <= var15; ++var33) {
                                             for(int var36 = -var16; var36 <= var16; ++var36) {
                                                var18.add(var27.getRelative(var36, var33, var30));
                                             }
                                          }
                                       }
                                    } else {
                                       var18.add(var27);
                                    }
                                 } else {
                                    for(int var19 = -var15; var19 <= var15; ++var19) {
                                       for(int var20 = -var15; var20 <= var15; ++var20) {
                                          for(int var21 = -var16; var21 <= var16; ++var21) {
                                             var18.add(var27.getRelative(var19, var21, var20));
                                          }
                                       }
                                    }
                                 }

                                 var18.removeIf((var0) -> var0.getType() == Material.AIR);
                                 var18.remove(var27);

                                 for(Block var35 : var18) {
                                    if (!var29.contains(var35.getType().name())) {
                                       BlockBreakEvent var38 = new BlockBreakEvent(var35, var4);
                                       DRILLING.set(true);
                                       Bukkit.getPluginManager().callEvent(var38);
                                       DRILLING.set(false);
                                       if (!var38.isCancelled()) {
                                          var35.breakNaturally(var5);
                                          var35.getWorld().spawnParticle(var2, var35.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), var13, new Particle.DustOptions(var14, 1.0F));
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
         }
      }
   }
}
