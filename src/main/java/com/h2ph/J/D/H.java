package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class H implements CommandExecutor, Listener {
   private final PrismSurvival A;
   private final Map<String, Double> B = new HashMap();
   private static final DecimalFormat C = new DecimalFormat("#.##");

   public H(PrismSurvival var1) {
      this.A = var1;
      this.A();
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   private void A() {
      this.B.clear();
      File var1 = new File(this.A.getDataFolder(), "economy/worth");
      if (var1.exists()) {
         File var2 = new File(var1, "categories");
         if (var2.exists()) {
            File[] var3 = var2.listFiles((var0, var1x) -> var1x.toLowerCase().endsWith(".yml"));
            if (var3 != null) {
               for(File var7 : var3) {
                  YamlConfiguration var8 = YamlConfiguration.loadConfiguration(var7);

                  for(String var10 : ((FileConfiguration)var8).getKeys(false)) {
                     ConfigurationSection var11 = ((FileConfiguration)var8).getConfigurationSection(var10);
                     if (var11 != null) {
                        for(String var13 : var11.getKeys(false)) {
                           double var14 = var11.getDouble(var13, (double)-1.0F);
                           if (var14 > (double)0.0F) {
                              this.B.put(var13.trim().toUpperCase(Locale.ROOT), var14);
                           }
                        }
                     }
                  }
               }

            }
         }
      }
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         this.A(var5, 0, H._C.D, H._A.E);
         return true;
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      }
   }

   private void A(Player var1, int var2, _C var3, _A var4) {
      ArrayList var5 = new ArrayList(this.B.entrySet());
      List var21 = (List)var5.stream().filter((var2x) -> this.A((String)var2x.getKey(), var4)).collect(Collectors.toList());
      switch (var3.ordinal()) {
         case 0 -> var21.sort((var0, var1x) -> Double.compare((Double)var1x.getValue(), (Double)var0.getValue()));
         case 1 -> var21.sort((var0, var1x) -> Double.compare((Double)var0.getValue(), (Double)var1x.getValue()));
         case 2 -> var21.sort((var0, var1x) -> ((String)var0.getKey()).compareToIgnoreCase((String)var1x.getKey()));
      }

      byte var6 = 45;
      int var7 = (int)Math.max((double)0.0F, Math.ceil((double)var21.size() / (double)var6) - (double)1.0F);
      if (var2 > var7) {
         var2 = var7;
      }

      if (var2 < 0) {
         var2 = 0;
      }

      String var8 = this.B("&8ɪᴛᴇᴍ ᴘʀɪᴄᴇѕ (Page " + (var2 + 1) + ")");
      Inventory var9 = Bukkit.createInventory(new _B(var2, var3, var4), 54, var8);
      int var10 = var2 * var6;
      int var11 = Math.min(var10 + var6, var21.size());
      int var12 = 0;

      for(int var13 = var10; var13 < var11; ++var13) {
         Map.Entry var14 = (Map.Entry)var21.get(var13);
         String var15 = (String)var14.getKey();
         String var16 = var15.contains(":") ? var15.substring(0, var15.indexOf(58)) : var15;
         Material var17 = Material.getMaterial(var16);
         if (var17 != null) {
            ItemStack var18 = new ItemStack(var17);
            ItemMeta var19 = var18.getItemMeta();
            if (var19 != null) {
               String var10002 = this.C(var15);
               var19.setDisplayName(this.B("&d" + var10002));
               ArrayList var20 = new ArrayList();
               var10002 = this.A((Double)var14.getValue());
               var20.add(this.B("&fPrice: &a$" + var10002));
               var19.setLore(com.h2ph.b.A.A(var20));
               var18.setItemMeta(var19);
            }

            var9.setItem(var12, var18);
            ++var12;
            if (var12 >= var6) {
               break;
            }
         }
      }

      if (var2 > 0) {
         var9.setItem(45, this.A(Material.ARROW, "&aʙᴀᴄᴋ", "&fClick to go to the previous page"));
      }

      if (var11 < var21.size()) {
         var9.setItem(53, this.A(Material.ARROW, "&aɴᴇхᴛ", "&fClick to go to the next page"));
      }

      ArrayList var22 = new ArrayList();

      for(_C var29 : H._C.values()) {
         if (var29 == var3) {
            var22.add(this.B("&a• " + var29.B()));
         } else {
            var22.add(this.B("&f• " + var29.B()));
         }
      }

      var9.setItem(48, this.A(Material.CAULDRON, "&dѕᴏʀᴛ", (List)var22));
      var9.setItem(49, this.A(Material.ANVIL, "&dɪᴛᴇᴍ ᴘʀɪᴄᴇѕ", "&fClick to refresh"));
      ArrayList var24 = new ArrayList();

      for(_A var31 : H._A.values()) {
         if (var31 == var4) {
            var24.add(this.B("&a• " + var31.B()));
         } else {
            var24.add(this.B("&f• " + var31.B()));
         }
      }

      var9.setItem(50, this.A(Material.HOPPER, "&dꜰɪʟᴛᴇʀ", (List)var24));
      var1.openInventory(var9);
   }

   private boolean A(String var1, _A var2) {
      if (var2 == H._A.E) {
         return true;
      } else {
         String var3 = var1.contains(":") ? var1.substring(0, var1.indexOf(58)) : var1;
         Material var4 = Material.getMaterial(var3);
         if (var4 == null) {
            return false;
         } else {
            String var5 = var4.name();
            switch (var2.ordinal()) {
               case 1 -> {
                  return var4.isBlock();
               }
               case 2 -> {
                  return var5.endsWith("_AXE") || var5.endsWith("_PICKAXE") || var5.endsWith("_SHOVEL") || var5.endsWith("_HOE") || var4 == Material.SHEARS || var4 == Material.FLINT_AND_STEEL || var4 == Material.FISHING_ROD || var4 == Material.BRUSH;
               }
               case 3 -> {
                  return var4.isEdible();
               }
               case 4 -> {
                  return var5.endsWith("_SWORD") || var5.endsWith("_HELMET") || var5.endsWith("_CHESTPLATE") || var5.endsWith("_LEGGINGS") || var5.endsWith("_BOOTS") || var4 == Material.BOW || var4 == Material.CROSSBOW || var4 == Material.TRIDENT || var4 == Material.SHIELD || var4 == Material.MACE;
               }
               case 5 -> {
                  return var5.contains("POTION") || var4 == Material.TIPPED_ARROW;
               }
               case 6 -> {
                  return var4 == Material.ENCHANTED_BOOK || var4 == Material.BOOK || var4 == Material.WRITTEN_BOOK || var4 == Material.WRITABLE_BOOK;
               }
               case 7 -> {
                  return !var4.isBlock() && !var4.isEdible() && !var5.endsWith("_SWORD") && !var5.endsWith("_AXE") && !var5.endsWith("_PICKAXE");
               }
               case 8 -> {
                  return var4 == Material.CHEST || var4 == Material.ENDER_CHEST || var4 == Material.HOPPER || var4 == Material.DISPENSER || var4 == Material.DROPPER || var4 == Material.CRAFTING_TABLE || var4 == Material.FURNACE || var4 == Material.BLAST_FURNACE || var4 == Material.SMOKER || var4 == Material.ANVIL;
               }
               default -> {
                  return true;
               }
            }
         }
      }
   }

   private String A(String var1) {
      String[] var2 = var1.toLowerCase().split("_");
      StringBuilder var3 = new StringBuilder();

      for(String var7 : var2) {
         if (!var7.isEmpty()) {
            var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1)).append(" ");
         }
      }

      return var3.toString().trim();
   }

   private String C(String var1) {
      if (!var1.contains(":")) {
         return this.A(var1);
      } else {
         String[] var2 = var1.split(":");
         String var3 = this.A(var2[0]);
         String var4 = var2.length > 1 ? this.A(var2[1]) : null;
         String var5 = var2.length > 2 ? var2[2] : null;
         StringBuilder var6 = new StringBuilder(var3);
         if (var4 != null && !var4.isEmpty()) {
            var6.append(": ").append(var4);
         }

         if (var5 != null) {
            try {
               var6.append(" ").append(this.A(Integer.parseInt(var5)));
            } catch (NumberFormatException var8) {
               var6.append(" ").append(var5);
            }
         }

         return var6.toString();
      }
   }

   private String A(int var1) {
      String[] var2 = new String[]{"X", "IX", "V", "IV", "I"};
      int[] var3 = new int[]{10, 9, 5, 4, 1};
      if (var1 > 0 && var1 <= 20) {
         StringBuilder var4 = new StringBuilder();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            while(var1 >= var3[var5]) {
               var4.append(var2[var5]);
               var1 -= var3[var5];
            }
         }

         return var4.toString();
      } else {
         return String.valueOf(var1);
      }
   }

   private ItemStack A(Material var1, String var2, String var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(this.B(var2));
         if (var3 != null) {
            var5.setLore(Collections.singletonList(this.B(var3)));
         }

         var4.setItemMeta(var5);
      }

      return var4;
   }

   private ItemStack A(Material var1, String var2, List<String> var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(this.B(var2));
         var5.setLore(var3);
         var4.setItemMeta(var5);
      }

      return var4;
   }

   private String B(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private String A(double var1) {
      if (var1 >= 1.0E12) {
         return this.A(var1, 1.0E12, "T");
      } else if (var1 >= (double)1.0E9F) {
         return this.A(var1, (double)1.0E9F, "B");
      } else if (var1 >= (double)1000000.0F) {
         return this.A(var1, (double)1000000.0F, "M");
      } else {
         return var1 >= (double)1000.0F ? this.A(var1, (double)1000.0F, "K") : C.format(Math.floor(var1 * (double)100.0F) / (double)100.0F);
      }
   }

   private String A(double var1, double var3, String var5) {
      double var6 = var1 / var3;
      var6 = Math.floor(var6 * (double)10.0F) / (double)10.0F;
      if (var6 == (double)((long)var6)) {
         String var9 = String.valueOf((long)var6);
         return var9 + var5;
      } else {
         String var10000 = C.format(var6);
         return var10000 + var5;
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() instanceof _B) {
         var1.setCancelled(true);
         HumanEntity var3 = var1.getWhoClicked();
         if (var3 instanceof Player) {
            Player var2 = (Player)var3;
            int var10 = var1.getRawSlot();
            _B var4 = (_B)var1.getInventory().getHolder();
            int var5 = var4.getPage();
            _C var6 = var4.getSortType();
            _A var7 = var4.getFilterType();
            if (var10 == 45) {
               this.A(var2, var5 - 1, var6, var7);
            } else if (var10 == 53) {
               this.A(var2, var5 + 1, var6, var7);
            } else if (var10 == 48) {
               _C[] var8 = H._C.values();
               int var9 = (var6.ordinal() + 1) % var8.length;
               this.A(var2, 0, var8[var9], var7);
            } else if (var10 == 49) {
               this.A();
               this.A(var2, var5, var6, var7);
            } else if (var10 == 50) {
               _A[] var11 = H._A.values();
               int var12 = (var7.ordinal() + 1) % var11.length;
               this.A(var2, 0, var6, var11[var12]);
            }

         }
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof _B) {
         var1.setCancelled(true);
      }

   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
   }

   public static enum _A {
      E("All"),
      I("Blocks"),
      C("Tools"),
      G("Food"),
      H("Combat"),
      K("Potions"),
      B("Books"),
      A("Ingredients"),
      F("Utilities");

      private final String J;

      private _A(String var3) {
         this.J = var3;
      }

      public String B() {
         return this.J;
      }

      // $FF: synthetic method
      private static _A[] A() {
         return new _A[]{E, I, C, G, H, K, B, A, F};
      }
   }

   private static class _B implements InventoryHolder {
      private final int B;
      private final _C C;
      private final _A A;

      public _B(int var1, _C var2, _A var3) {
         this.B = var1;
         this.C = var2;
         this.A = var3;
      }

      public int getPage() {
         return this.B;
      }

      public _C getSortType() {
         return this.C;
      }

      public _A getFilterType() {
         return this.A;
      }

      public Inventory getInventory() {
         return null;
      }
   }

   public static enum _C {
      E("Highest Price"),
      B("Lowest Price"),
      D("By name");

      private final String A;

      private _C(String var3) {
         this.A = var3;
      }

      public String B() {
         return this.A;
      }

      // $FF: synthetic method
      private static _C[] A() {
         return new _C[]{E, B, D};
      }
   }
}
