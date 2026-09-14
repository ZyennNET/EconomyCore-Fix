package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class A implements InventoryHolder, Listener {
   private final PrismSurvival D;
   private final E I;
   private final Player B;
   private final Player F;
   private final Inventory A;
   private int C = 5;
   private String H = "Random";
   private final Map<String, Material> G = new HashMap();
   private final List<String> E = new ArrayList();

   public A(PrismSurvival var1, E var2, Player var3, Player var4) {
      this.D = var1;
      this.I = var2;
      this.B = var3;
      this.F = var4;
      String var10004 = com.h2ph.J.B.A.D.A("create a duel");
      this.A = Bukkit.createInventory(this, 27, ChatColor.translateAlternateColorCodes('&', "&8" + var10004 + " - " + com.h2ph.J.B.A.D.A(var4.getName())));
      this.B();
      this.A();
   }

   private void B() {
      this.G.put("Plains", Material.GRASS_BLOCK);
      this.G.put("Meadow", Material.GRASS_BLOCK);
      this.G.put("Sunflower Plains", Material.SUNFLOWER);
      this.G.put("Desert", Material.SAND);
      this.G.put("Badlands", Material.TERRACOTTA);
      this.G.put("Wooded Badlands", Material.TERRACOTTA);
      this.G.put("Eroded Badlands", Material.RED_TERRACOTTA);
      this.G.put("Snow", Material.SNOW_BLOCK);
      this.G.put("Snowy Plains", Material.SNOW_BLOCK);
      this.G.put("Snowy Taiga", Material.SNOW_BLOCK);
      this.G.put("Ice Spikes", Material.PACKED_ICE);
      this.G.put("Frozen Ocean", Material.BLUE_ICE);
      this.G.put("Frozen River", Material.ICE);
      this.G.put("Nether", Material.NETHERRACK);
      this.G.put("Nether Wastes", Material.NETHERRACK);
      this.G.put("Crimson Forest", Material.CRIMSON_NYLIUM);
      this.G.put("Warped Forest", Material.WARPED_NYLIUM);
      this.G.put("Soul Sand Valley", Material.SOUL_SAND);
      this.G.put("Basalt Deltas", Material.BASALT);
      this.G.put("End", Material.END_STONE);
      this.G.put("The End", Material.END_STONE);
      this.G.put("End Highlands", Material.CHORUS_FLOWER);
      this.G.put("Forest", Material.OAK_LOG);
      this.G.put("Birch Forest", Material.BIRCH_LOG);
      this.G.put("Dark Forest", Material.DARK_OAK_LOG);
      this.G.put("Flower Forest", Material.ROSE_BUSH);
      this.G.put("Jungle", Material.JUNGLE_LOG);
      this.G.put("Bamboo Jungle", Material.BAMBOO);
      this.G.put("Sparse Jungle", Material.JUNGLE_LEAVES);
      this.G.put("Taiga", Material.SPRUCE_LOG);
      this.G.put("Old Growth Pine Taiga", Material.PODZOL);
      this.G.put("Old Growth Spruce Taiga", Material.SPRUCE_LOG);
      this.G.put("Swamp", Material.LILY_PAD);
      this.G.put("Mangrove Swamp", Material.MANGROVE_LOG);
      this.G.put("Mountains", Material.STONE);
      this.G.put("Windswept Hills", Material.STONE);
      this.G.put("Stony Peaks", Material.STONE);
      this.G.put("Jagged Peaks", Material.SNOW_BLOCK);
      this.G.put("Frozen Peaks", Material.PACKED_ICE);
      this.G.put("Ocean", Material.WATER_BUCKET);
      this.G.put("Deep Ocean", Material.PRISMARINE);
      this.G.put("Warm Ocean", Material.BRAIN_CORAL_BLOCK);
      this.G.put("Lukewarm Ocean", Material.TUBE_CORAL_BLOCK);
      this.G.put("Lush Caves", Material.MOSS_BLOCK);
      this.G.put("Dripstone Caves", Material.DRIPSTONE_BLOCK);
      this.G.put("Deep Dark", Material.SCULK);
      this.G.put("Savanna", Material.ACACIA_LOG);
      this.G.put("Windswept Savanna", Material.ACACIA_LOG);

      try {
         this.G.put("Cherry Grove", Material.valueOf("CHERRY_LOG"));
      } catch (IllegalArgumentException var4) {
         this.G.put("Cherry Grove", Material.PINK_PETALS);
      }

      this.G.put("Mushroom Fields", Material.MYCELIUM);
      this.G.put("Beach", Material.SAND);
      this.G.put("River", Material.WATER_BUCKET);

      for(String var3 : this.D.getDuelArenaManager().E()) {
         if (!this.E.contains(var3)) {
            this.E.add(var3);
         }
      }

      Collections.sort(this.E);
      if (!this.E.contains("Random")) {
         this.E.add(0, "Random");
      } else {
         this.E.remove("Random");
         this.E.add(0, "Random");
      }

   }

   public Inventory getInventory() {
      return this.A;
   }

   public void open() {
      this.B.openInventory(this.A);
   }

   private void A() {
      this.A.setItem(10, this.A(Material.RED_STAINED_GLASS_PANE, "&4" + com.h2ph.J.B.A.D.A("cancel"), "&fClick to cancel"));
      Material var1 = Material.BEDROCK;
      if (this.H.equals("Random")) {
         try {
            var1 = Material.valueOf("RECOVERY_COMPASS");
         } catch (IllegalArgumentException var5) {
            var1 = Material.COMPASS;
         }
      } else {
         var1 = (Material)this.G.getOrDefault(this.H, Material.GRASS_BLOCK);
      }

      ArrayList var2 = new ArrayList();
      var2.add("&fClick to change map");
      String var3 = this.H;
      if (var3.contains("_") || var3.toUpperCase().equals(var3)) {
         var3 = (String)Arrays.stream(var3.replace("_", " ").split(" ")).filter((var0) -> !var0.isEmpty()).map((var0) -> {
            char var10000 = Character.toUpperCase(var0.charAt(0));
            return var10000 + var0.substring(1).toLowerCase();
         }).collect(Collectors.joining(" "));
      }

      var2.add("&7(" + var3 + ")");
      this.A.setItem(12, this.A((Material)var1, "&a" + com.h2ph.J.B.A.D.A("biome"), var2));
      ArrayList var4 = new ArrayList();
      var4.add("&7(" + this.C + "m)");
      this.A.setItem(13, this.A((Material)Material.CLOCK, "&a" + com.h2ph.J.B.A.D.A("time"), var4));
      this.A.setItem(14, this.A(Material.FLOW_BANNER_PATTERN, "&a" + com.h2ph.J.B.A.D.A("region"), "&7Europe"));
      this.A.setItem(16, this.A(Material.GREEN_STAINED_GLASS_PANE, "&a" + com.h2ph.J.B.A.D.A("send"), "&fClick to send request"));
   }

   @EventHandler
   public void onClick(InventoryClickEvent var1) {
      if (var1.getInventory().equals(this.A)) {
         var1.setCancelled(true);
         if (var1.getWhoClicked() == this.B) {
            int var2 = var1.getSlot();
            if (var2 == 10) {
               this.B.closeInventory();

               try {
                  this.B.playSound(this.B.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
               } catch (Exception var8) {
               }
            }

            if (var2 == 12) {
               int var3 = this.E.indexOf(this.H);
               ++var3;
               if (var3 >= this.E.size()) {
                  var3 = 0;
               }

               this.H = (String)this.E.get(var3);
               this.A();

               try {
                  this.B.playSound(this.B.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
               } catch (Exception var7) {
               }
            }

            if (var2 == 13) {
               if (var1.isLeftClick()) {
                  if (this.C < 20) {
                     ++this.C;
                  }
               } else if (var1.isRightClick() && this.C > 5) {
                  --this.C;
               }

               this.A();

               try {
                  this.B.playSound(this.B.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
               } catch (Exception var6) {
               }
            }

            if (var2 == 16) {
               try {
                  this.B.playSound(this.B.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 0.5F, 1.2F);
               } catch (Exception var5) {
               }

               this.B.closeInventory();
               this.I.A(this.B, this.F, this.C, this.H);
            }

         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent var1) {
      if (var1.getInventory().equals(this.A)) {
         HandlerList.unregisterAll(this);
      }

   }

   private ItemStack A(Material var1, String var2, List<String> var3) {
      return this.A(var1, var2, (String[])var3.toArray(new String[0]));
   }

   private ItemStack A(Material var1, String var2, String... var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
         ArrayList var6 = new ArrayList();

         for(String var10 : var3) {
            var6.add(ChatColor.translateAlternateColorCodes('&', var10));
         }

         var5.setLore(var6);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack A(String var1, String var2, String... var3) {
      ItemStack var4 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var5 = (SkullMeta)var4.getItemMeta();
      if (var5 != null) {
         try {
            var5.setOwner(var1);
         } catch (Exception var11) {
         }

         var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', var2));
         ArrayList var6 = new ArrayList();

         for(String var10 : var3) {
            var6.add(ChatColor.translateAlternateColorCodes('&', var10));
         }

         var5.setLore(var6);
         var4.setItemMeta(var5);
      }

      return var4;
   }
}
