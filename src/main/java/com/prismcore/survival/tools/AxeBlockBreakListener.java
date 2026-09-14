package com.prismcore.survival.tools;

import java.util.HashSet;
import java.util.LinkedList;
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

public class AxeBlockBreakListener implements Listener {
   private static final ThreadLocal<Boolean> CHOPPING = ThreadLocal.withInitial(() -> false);
   private static final BlockFace[] FACES;
   private final ToolsManager manager;

   public AxeBlockBreakListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockBreak(BlockBreakEvent var1) {
      Player var3 = var1.getPlayer();
      ItemStack var4 = var3.getInventory().getItemInMainHand();
      if (var4 == null || !var4.hasItemMeta() || !var4.getItemMeta().getPersistentDataContainer().has(ToolsManager.MULTI_KEY, PersistentDataType.BYTE)) {
         if (!(Boolean)CHOPPING.get()) {
            Player var5 = var1.getPlayer();
            ItemStack var6 = var5.getInventory().getItemInMainHand();
            ConfigurationSection var7 = this.manager.getConfig().getConfigurationSection("axe");
            if (var6 != null && var7 != null) {
               ItemMeta var8 = var6.getItemMeta();
               if (var8 != null && (var8.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG) || var8.getPersistentDataContainer().has(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG))) {
                  if (var6.getType() == Material.valueOf(var7.getString("material"))) {
                     for(String var10 : var7.getConfigurationSection("enchantments").getKeys(false)) {
                        Enchantment var11 = Enchantment.getByName(var10);
                        if (var11 == null || !var8.hasEnchant(var11)) {
                           return;
                        }
                     }

                     Block var21 = var1.getBlock();
                     if (var21.getType().name().endsWith("_LOG")) {
                        var1.setDropItems(false);
                        boolean var22 = var7.getBoolean("destory-leaves", true);
                        HashSet var23 = new HashSet();
                        LinkedList var12 = new LinkedList();
                        var12.add(var21);
                        var23.add(var21);

                        while(!var12.isEmpty()) {
                           Block var13 = (Block)var12.poll();
                           boolean var14 = var13.getType().name().endsWith("_LOG");

                           for(BlockFace var18 : FACES) {
                              Block var19 = var13.getRelative(var18);
                              String var20 = var19.getType().name();
                              if (var20.endsWith("_LOG")) {
                                 if (var14 && !var23.contains(var19)) {
                                    var23.add(var19);
                                    var12.add(var19);
                                 }
                              } else if (var22 && var20.endsWith("_LEAVES") && !var23.contains(var19)) {
                                 var23.add(var19);
                                 var12.add(var19);
                              }
                           }
                        }

                        for(Block var25 : var23) {
                           if (var25 != null && var25.getType() != Material.AIR) {
                              BlockBreakEvent var26 = new BlockBreakEvent(var25, var5);
                              CHOPPING.set(true);
                              Bukkit.getPluginManager().callEvent(var26);
                              CHOPPING.set(false);
                              if (!var26.isCancelled()) {
                                 var25.breakNaturally(var6);
                                 int var27 = var7.getInt("particle.count");
                                 Color var28 = Color.fromRGB(var7.getInt("particle.color.r"), var7.getInt("particle.color.g"), var7.getInt("particle.color.b"));
                                 Particle var29 = Particle.valueOf(var7.getString("particle.type"));
                                 var25.getWorld().spawnParticle(var29, var25.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), var27, new Particle.DustOptions(var28, 1.0F));
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

   static {
      FACES = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
   }
}
