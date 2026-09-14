package com.prismcore.survival.tools;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class MultitoolBlockBreakListener implements Listener {
   private static final ThreadLocal<Boolean> HANDLING = ThreadLocal.withInitial(() -> false);
   private final ToolsManager manager;

   public MultitoolBlockBreakListener(ToolsManager var1) {
      this.manager = var1;
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onBlockDamage(BlockDamageEvent var1) {
      if (!(Boolean)HANDLING.get()) {
         Player var5 = var1.getPlayer();
         ItemStack var6 = var5.getInventory().getItemInMainHand();
         if (var6 != null) {
            ItemMeta var7 = var6.getItemMeta();
            if (var7 != null) {
               if (var7.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG) || var7.getPersistentDataContainer().has(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG)) {
                  if (var7.getPersistentDataContainer().has(ToolsManager.MULTI_KEY, PersistentDataType.BYTE)) {
                     ConfigurationSection var8 = this.manager.getConfig().getConfigurationSection("multitool");
                     if (var8 != null) {
                        Material var4;
                        try {
                           var4 = Material.valueOf(var8.getString("pickaxe"));
                        } catch (IllegalArgumentException var27) {
                           var4 = Material.NETHERITE_PICKAXE;
                        }

                        Material var3;
                        try {
                           var3 = Material.valueOf(var8.getString("axe"));
                        } catch (IllegalArgumentException var26) {
                           var3 = Material.NETHERITE_AXE;
                        }

                        Material var2;
                        try {
                           var2 = Material.valueOf(var8.getString("shovel"));
                        } catch (IllegalArgumentException var25) {
                           var2 = Material.NETHERITE_SHOVEL;
                        }

                        Material var9 = var6.getType();
                        boolean var10 = var9 == var4 || var9 == var3 || var9 == var2;
                        if (var10) {
                           List var11 = var8.getStringList("pickaxe-blocks");
                           List var12 = var8.getStringList("axe-blocks");
                           List var13 = var8.getStringList("shovel-blocks");
                           Set var14 = (Set)var11.stream().flatMap((var0) -> {
                              try {
                                 return Stream.of(Material.valueOf(var0));
                              } catch (IllegalArgumentException var2) {
                                 return Stream.empty();
                              }
                           }).collect(Collectors.toSet());
                           Set var15 = (Set)var12.stream().flatMap((var0) -> {
                              try {
                                 return Stream.of(Material.valueOf(var0));
                              } catch (IllegalArgumentException var2) {
                                 return Stream.empty();
                              }
                           }).collect(Collectors.toSet());
                           Set var16 = (Set)var13.stream().flatMap((var0) -> {
                              try {
                                 return Stream.of(Material.valueOf(var0));
                              } catch (IllegalArgumentException var2) {
                                 return Stream.empty();
                              }
                           }).collect(Collectors.toSet());
                           Block var17 = var1.getBlock();
                           Material var18 = var17.getType();
                           Material var19 = null;
                           if (var14.contains(var18)) {
                              var19 = var4;
                           } else if (var15.contains(var18)) {
                              var19 = var3;
                           } else {
                              if (!var16.contains(var18)) {
                                 return;
                              }

                              var19 = var2;
                           }

                           if (var9 != var19) {
                              ItemStack var20 = new ItemStack(var19, 1);
                              ItemMeta var21 = var20.getItemMeta();
                              if (var21 != null) {
                                 var7.getEnchants().forEach((var1x, var2x) -> var21.addEnchant(var1x, var2x, true));
                                 if (var7.hasDisplayName()) {
                                    var21.setDisplayName(var7.getDisplayName());
                                 }

                                 if (var7.hasLore()) {
                                    var21.setLore(var7.getLore());
                                 }

                                 if (var7 instanceof Damageable) {
                                    Damageable var23 = (Damageable)var7;
                                    if (var21 instanceof Damageable) {
                                       Damageable var24 = (Damageable)var21;
                                       var24.setDamage(var23.getDamage());
                                    }
                                 }

                                 if (var7.getPersistentDataContainer().has(ToolsManager.REMAINING_KEY, PersistentDataType.LONG)) {
                                    long var29 = (Long)var7.getPersistentDataContainer().get(ToolsManager.REMAINING_KEY, PersistentDataType.LONG);
                                    var21.getPersistentDataContainer().set(ToolsManager.REMAINING_KEY, PersistentDataType.LONG, var29);
                                 } else if (var7.getPersistentDataContainer().has(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG)) {
                                    long var30 = (Long)var7.getPersistentDataContainer().get(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG);
                                    var21.getPersistentDataContainer().set(ToolsManager.EXPIRY_KEY, PersistentDataType.LONG, var30);
                                 }

                                 byte var31 = (Byte)var7.getPersistentDataContainer().get(ToolsManager.MULTI_KEY, PersistentDataType.BYTE);
                                 var21.getPersistentDataContainer().set(ToolsManager.MULTI_KEY, PersistentDataType.BYTE, var31);
                                 var20.setItemMeta(var21);
                              }

                              HANDLING.set(true);
                              var5.getInventory().setItemInMainHand(var20);
                              HANDLING.set(false);
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
