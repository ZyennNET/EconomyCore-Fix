package com.h2ph.J.C;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class D implements Listener {
   private static final DecimalFormat E;
   private static final int V = 3;
   private static final Pattern N;
   private final PrismSurvival D;
   private final Map<String, Double> L = new HashMap();
   private final Set<String> C = new HashSet();
   private FileConfiguration d;
   private String K;
   private String J;
   private String R;
   private String S;
   private String W;
   private String _;
   private String c;
   private String P;
   private String a;
   private String M;
   private String I;
   private String A;
   private double H;
   private double O;
   private double X;
   private List<Double> T;
   private final Map<_I, _F> Q = new HashMap();
   private final Map<UUID, Map<_I, _A>> U = new ConcurrentHashMap();
   private final File B;
   private FileConfiguration b;
   private final Map<UUID, Map<String, double[]>> G = new ConcurrentHashMap();
   private final File Y;
   private FileConfiguration Z;
   private final Map<Material, _I> F = new HashMap();

   public D(PrismSurvival var1) {
      this.D = var1;
      this.B = new File(var1.getDataFolder(), "sell_progress.yml");
      this.Y = new File(var1.getDataFolder(), "sell_history.yml");
      this.E();
      this.B();
      this.A();
      this.C();
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   private String A(String var1) {
      if (var1 == null) {
         return "";
      } else {
         Matcher var2 = N.matcher(var1);
         StringBuffer var3 = new StringBuffer();

         while(var2.find()) {
            String var4 = var2.group(1);
            var2.appendReplacement(var3, ChatColor.of("#" + var4).toString());
         }

         var2.appendTail(var3);
         return org.bukkit.ChatColor.translateAlternateColorCodes('&', var3.toString());
      }
   }

   private void E() {
      File var1 = new File(this.D.getDataFolder(), "economy/sell/config.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.A(var1);
      }

      this.d = YamlConfiguration.loadConfiguration(var1);
      this.K = this.A(this.d.getString("gui-titles.sell", "&8ᴘʟᴀᴄᴇ ɪᴛᴇᴍѕ ɪɴ ʜᴇʀᴇ ᴛᴏ ѕᴇʟʟ"));
      this.J = this.A(this.d.getString("gui-titles.sellmulti", "&8sᴇʟʟ ᴍᴜʟᴛɪᴘʟɪᴇʀ"));
      this.R = this.A(this.d.getString("gui-titles.sellhistory", "&8sᴇʟʟ ʜɪsᴛᴏʀʏ"));
      this.S = this.A(this.d.getString("gui-titles.category-history", "&8%s% ʜɪѕᴛᴏʀʏ"));
      this.W = this.A(this.d.getString("gui-items.sort-item.name", "&eѕᴏʀᴛ"));
      this._ = this.A(this.d.getString("gui-items.sort-item.lore", "&7%s% &7click to change"));
      this.c = this.A(this.d.getString("gui-items.prev-page.name", "&aᴘʀᴇᴠɪᴏᴜѕ"));
      this.P = this.A(this.d.getString("gui-items.next-page.name", "&aɴᴇxᴛ"));
      this.a = this.A(this.d.getString("gui-items.back-button.name", "&cʙᴀᴄᴋ"));
      this.M = this.A(this.d.getString("messages.sold-total", "#00f900+$%amount%"));
      this.I = this.A(this.d.getString("messages.sold-total-action-bar", "#00f900+$%amount%"));
      this.A = this.A(this.d.getString("messages.cannot-be-sold", "&cThis item cannot be sold."));
      this.H = this.d.getDouble("settings.base-multiplier", (double)1.0F);
      this.O = this.d.getDouble("settings.multiplier-increment", 0.1);
      this.X = this.d.getDouble("settings.max-multiplier", (double)3.0F);
      this.T = new ArrayList();

      for(Object var3 : this.d.getList("level-prices", new ArrayList())) {
         if (var3 instanceof Number) {
            this.T.add(((Number)var3).doubleValue());
         }
      }

      if (this.T.isEmpty()) {
         this.T = Arrays.asList((double)25000.0F, (double)150000.0F, (double)500000.0F, (double)1000000.0F, (double)5000000.0F, (double)2.5E7F, (double)2.5E8F, (double)5.5E8F, (double)8.5E8F, (double)1.0E9F, (double)2.0E9F, (double)4.0E9F, (double)8.0E9F, (double)1.0E10F, (double)2.0E10F, (double)4.0E10F, (double)8.0E10F, (double)1.6E11F, (double)3.2E11F, (double)6.4E11F);
      }

      this.Q.clear();
      ConfigurationSection var10 = this.d.getConfigurationSection("gui-items.category-icons");
      if (var10 != null) {
         for(_I var6 : D._I.values()) {
            String var7 = var6.C();
            String var8 = var10.getString(var7 + ".display-name");
            String var9 = var10.getString(var7 + ".lore");
            if (var8 == null) {
               var8 = "&a" + var6.B().toUpperCase();
            }

            if (var9 == null) {
               var9 = "&7Click to view your &e" + var6.B() + "&7 history\n&7Current multiplier: &a%multiplier%x";
            }

            this.Q.put(var6, new _F(this.A(var8), this.A(var9)));
         }
      } else {
         for(_I var15 : D._I.values()) {
            this.Q.put(var15, new _F(this.A("&a" + var15.B().toUpperCase()), this.A("&7Click to view your &e" + var15.B() + "&7 history\n&7Current multiplier: &a%multiplier%x")));
         }
      }

   }

   private void A(File var1) {
      YamlConfiguration var2 = new YamlConfiguration();
      var2.set("gui-titles.sell", "&8ᴘʟᴀᴄᴇ ɪᴛᴇᴍѕ ɪɴ ʜᴇʀᴇ ᴛᴏ ѕᴇʟʟ");
      var2.set("gui-titles.sellmulti", "&8sᴇʟʟ ᴍᴜʟᴛɪᴘʟɪᴇʀ");
      var2.set("gui-titles.sellhistory", "&8sᴇʟʟ ʜɪsᴛᴏʀʏ");
      var2.set("gui-titles.category-history", "&8%s% ʜɪѕᴛᴏʀʏ");
      var2.set("gui-items.sort-item.name", "&eѕᴏʀᴛ");
      var2.set("gui-items.sort-item.lore", "&7%s% &7click to change");
      var2.set("gui-items.prev-page.name", "&aᴘʀᴇᴠɪᴏᴜѕ");
      var2.set("gui-items.next-page.name", "&aɴᴇxᴛ");
      var2.set("gui-items.back-button.name", "&cʙᴀᴄᴋ");

      for(_I var6 : D._I.values()) {
         String var7 = var6.C();
         var2.set("gui-items.category-icons." + var7 + ".display-name", "&a" + var6.B().toUpperCase());
         var2.set("gui-items.category-icons." + var7 + ".lore", "&7Click to view your &e" + var6.B() + "&7 history\n&7Current multiplier: &a%multiplier%x");
      }

      var2.set("messages.sold-total", "&a+$%amount%");
      var2.set("messages.sold-total-action-bar", "&a+$%amount%");
      var2.set("messages.cannot-be-sold", "&cThis item cannot be sold.");
      var2.set("settings.base-multiplier", (double)1.0F);
      var2.set("settings.multiplier-increment", 0.1);
      var2.set("settings.max-multiplier", (double)3.0F);
      var2.set("level-prices", Arrays.asList((double)25000.0F, (double)150000.0F, (double)500000.0F, (double)1000000.0F, (double)5000000.0F, (double)2.5E7F, (double)2.5E8F, (double)5.5E8F, (double)8.5E8F, (double)1.0E9F, (double)2.0E9F, (double)4.0E9F, (double)8.0E9F, (double)1.0E10F, (double)2.0E10F, (double)4.0E10F, (double)8.0E10F, (double)1.6E11F, (double)3.2E11F, (double)6.4E11F));

      try {
         var2.save(var1);
      } catch (IOException var8) {
         this.D.getLogger().warning("Could not create economy/sell/config.yml: " + var8.getMessage());
      }

   }

   private void G() {
      this.E();
      this.B();
      this.D.getLogger().info("Sell system reloaded by operator.");
   }

   private void A(Player var1) {
      Inventory var2 = Bukkit.createInventory(new _H(), 54, this.K);
      var1.openInventory(var2);
   }

   private void C(Player var1) {
      Inventory var2 = Bukkit.createInventory(new _D(), 27, this.J);
      int[] var3 = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17};
      int var4 = 0;

      for(_I var8 : D._I.values()) {
         if (var4 >= var3.length) {
            break;
         }

         _F var9 = (_F)this.Q.get(var8);
         if (var9 != null) {
            _A var10 = this.B(var1, var8);
            double var11 = Math.min(var10.B, this.X);
            String var13 = var9.A;
            String var14 = var9.B.replace("%multiplier%", String.format("%.1f", var11));
            List var15 = Arrays.asList(var14.split("\n"));
            ItemStack var16 = new ItemStack(var8.D());
            ItemMeta var17 = var16.getItemMeta();
            var17.setDisplayName(var13);
            var17.setLore(var15);
            var16.setItemMeta(var17);
            var2.setItem(var3[var4++], var16);
         }
      }

      var1.openInventory(var2);
   }

   private void A(Player var1, int var2, _J var3) {
      Map var4 = (Map)this.G.getOrDefault(var1.getUniqueId(), new HashMap());
      ArrayList var5 = new ArrayList(var4.entrySet());
      if (var3 == D._J.A) {
         var5.sort((var0, var1x) -> Double.compare(((double[])var1x.getValue())[0], ((double[])var0.getValue())[0]));
      } else {
         var5.sort(Comparator.comparing(Map.Entry::getKey, String.CASE_INSENSITIVE_ORDER));
      }

      byte var6 = 45;
      int var7 = Math.max(1, (int)Math.ceil((double)var5.size() / (double)var6));
      if (var2 < 1) {
         var2 = 1;
      }

      if (var2 > var7) {
         var2 = var7;
      }

      int var8 = (var2 - 1) * var6;
      int var9 = Math.min(var5.size(), var8 + var6);
      String var10 = this.R + " &7(Page " + var2 + "/" + var7 + ")";
      Inventory var11 = Bukkit.createInventory((InventoryHolder)null, 54, this.A(var10));
      int var12 = 0;

      for(int var13 = var8; var13 < var9; ++var13) {
         Map.Entry var14 = (Map.Entry)var5.get(var13);
         Material var15 = Material.getMaterial((String)var14.getKey());
         if (var15 != null) {
            ItemStack var16 = new ItemStack(var15);
            ItemMeta var17 = var16.getItemMeta();
            String var10002 = this.D((String)var14.getKey());
            var17.setDisplayName(this.A("&f" + var10002));
            ArrayList var18 = new ArrayList();
            double[] var30 = (double[])var14.getValue();
            var18.add(this.A("&7Amount sold: &e" + (long)var30[0]));
            String var31 = this.B(((double[])var14.getValue())[1]);
            var18.add(this.A("&7Total earned: &a$" + var31));
            var17.setLore(com.h2ph.b.A.A(var18));
            var16.setItemMeta(var17);
            var11.setItem(var12++, var16);
         }
      }

      ItemStack var19 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var20 = var19.getItemMeta();
      var20.setDisplayName(" ");
      var19.setItemMeta(var20);

      for(int var21 = var12; var21 < 45; ++var21) {
         var11.setItem(var21, var19);
      }

      if (var2 > 1) {
         ItemStack var22 = new ItemStack(Material.ARROW);
         ItemMeta var25 = var22.getItemMeta();
         var25.setDisplayName(this.c);
         var22.setItemMeta(var25);
         var11.setItem(45, var22);
      }

      if (var2 < var7) {
         ItemStack var23 = new ItemStack(Material.ARROW);
         ItemMeta var26 = var23.getItemMeta();
         var26.setDisplayName(this.P);
         var23.setItemMeta(var26);
         var11.setItem(53, var23);
      }

      ItemStack var24 = new ItemStack(Material.ANVIL);
      ItemMeta var27 = var24.getItemMeta();
      var27.setDisplayName(this.W);
      String var28 = var3 == D._J.A ? "&aMost sold" : "&aName (A-Z)";
      String var29 = this._.replace("%s%", var28);
      var27.setLore(Collections.singletonList(var29));
      var24.setItemMeta(var27);
      var11.setItem(49, var24);
      var1.setMetadata("sell_history_sort", new FixedMetadataValue(this.D, var3.name()));
      var1.setMetadata("sell_history_page", new FixedMetadataValue(this.D, var2));
      var1.openInventory(var11);
   }

   private void A(Player var1, _I var2) {
      String var3 = this.S.replace("%s%", var2.B().toUpperCase());
      Inventory var4 = Bukkit.createInventory((InventoryHolder)null, 54, this.A(var3));
      Map var5 = (Map)this.G.getOrDefault(var1.getUniqueId(), new HashMap());
      ArrayList var6 = new ArrayList();

      for(Map.Entry var8 : var5.entrySet()) {
         Material var9 = Material.getMaterial((String)var8.getKey());
         if (var9 != null && this.A(new ItemStack(var9)) == var2) {
            var6.add(var8);
         }
      }

      var6.sort((var0, var1x) -> Double.compare(((double[])var1x.getValue())[1], ((double[])var0.getValue())[1]));
      int var14 = 0;

      for(int var15 = 0; var15 < Math.min(45, var6.size()); ++var15) {
         Map.Entry var17 = (Map.Entry)var6.get(var15);
         Material var10 = Material.getMaterial((String)var17.getKey());
         if (var10 != null) {
            ItemStack var11 = new ItemStack(var10);
            ItemMeta var12 = var11.getItemMeta();
            String var10002 = this.D((String)var17.getKey());
            var12.setDisplayName(this.A("&f" + var10002));
            ArrayList var13 = new ArrayList();
            double[] var22 = (double[])var17.getValue();
            var13.add(this.A("&7Amount sold: &e" + (long)var22[0]));
            String var23 = this.B(((double[])var17.getValue())[1]);
            var13.add(this.A("&7Total earned: &a$" + var23));
            var12.setLore(com.h2ph.b.A.A(var13));
            var11.setItemMeta(var12);
            var4.setItem(var14++, var11);
         }
      }

      ItemStack var16 = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
      ItemMeta var18 = var16.getItemMeta();
      var18.setDisplayName(" ");
      var16.setItemMeta(var18);

      for(int var19 = var14; var19 < 45; ++var19) {
         var4.setItem(var19, var16);
      }

      ItemStack var20 = new ItemStack(Material.BARRIER);
      ItemMeta var21 = var20.getItemMeta();
      var21.setDisplayName(this.a);
      var20.setItemMeta(var21);
      var4.setItem(49, var20);
      var1.openInventory(var4);
   }

   private void B() {
      this.L.clear();
      this.C.clear();
      this.F.clear();
      File var1 = new File(this.D.getDataFolder(), "economy/worth");
      if (!var1.exists()) {
         var1.mkdirs();
      }

      File var2 = new File(var1, "config.yml");
      if (!var2.exists()) {
         this.D.saveResource("economy/worth/config.yml", false);
      }

      YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);

      for(String var6 : ((FileConfiguration)var3).getStringList("BLOCK-ITEMS")) {
         if (var6 != null && !var6.isEmpty()) {
            this.C.add(var6.trim().toUpperCase(Locale.ROOT));
         }
      }

      File var23 = new File(var1, "categories");
      if (!var23.exists()) {
         var23.mkdirs();
      }

      String[] var24 = new String[]{"armor_tools.yml", "blocks.yml", "book.yml", "crops.yml", "fish.yml", "mobs.yml", "natural.yml", "ores.yml", "potion.yml"};

      for(String var10 : var24) {
         File var11 = new File(var23, var10);
         if (!var11.exists()) {
            this.D.saveResource("economy/worth/categories/" + var10, false);
         }
      }

      File[] var25 = var23.listFiles((var0, var1x) -> var1x.toLowerCase().endsWith(".yml"));
      if (var25 != null) {
         for(File var29 : var25) {
            _I var12 = D._I.A(var29.getName());
            if (var12 != null) {
               YamlConfiguration var13 = YamlConfiguration.loadConfiguration(var29);

               for(String var15 : ((FileConfiguration)var13).getKeys(false)) {
                  ConfigurationSection var16 = ((FileConfiguration)var13).getConfigurationSection(var15);
                  if (var16 != null) {
                     for(String var18 : var16.getKeys(false)) {
                        double var19 = var16.getDouble(var18, (double)-1.0F);
                        String var21 = var18.trim().toUpperCase(Locale.ROOT);
                        this.L.put(var21, var19);
                        Material var22 = Material.getMaterial(var21);
                        if (var22 != null && !this.F.containsKey(var22)) {
                           this.F.put(var22, var12);
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void A() {
      this.U.clear();
      if (this.B.exists()) {
         this.b = YamlConfiguration.loadConfiguration(this.B);

         for(String var2 : this.b.getKeys(false)) {
            UUID var3 = UUID.fromString(var2);
            HashMap var4 = new HashMap();

            for(_I var8 : D._I.values()) {
               double var9 = this.b.getDouble(var2 + "." + var8.name() + ".progress", (double)0.0F);
               double var11 = this.b.getDouble(var2 + "." + var8.name() + ".multiplier", (double)1.0F);
               if (var11 < (double)1.0F) {
                  var11 = (double)1.0F;
               }

               var4.put(var8, new _A(var9, var11));
            }

            this.U.put(var3, var4);
         }

      }
   }

   private void F() {
      if (this.b == null) {
         this.b = new YamlConfiguration();
      }

      for(Map.Entry var2 : this.U.entrySet()) {
         String var3 = ((UUID)var2.getKey()).toString();

         for(Map.Entry var5 : ((Map)var2.getValue()).entrySet()) {
            _I var6 = (_I)var5.getKey();
            _A var7 = (_A)var5.getValue();
            this.b.set(var3 + "." + var6.name() + ".progress", var7.A);
            this.b.set(var3 + "." + var6.name() + ".multiplier", var7.B);
         }
      }

      try {
         this.b.save(this.B);
      } catch (IOException var8) {
         this.D.getLogger().warning("Failed to save sell progress: " + var8.getMessage());
      }

   }

   private _A B(Player var1, _I var2) {
      UUID var3 = var1.getUniqueId();
      Map var4 = (Map)this.U.computeIfAbsent(var3, (var1x) -> {
         HashMap var2 = new HashMap();

         for(_I var6 : D._I.values()) {
            var2.put(var6, new _A((double)0.0F, this.H));
         }

         return var2;
      });
      _A var5 = (_A)var4.get(var2);
      if (var5.B > this.X) {
         var5.B = this.X;
      }

      return var5;
   }

   private void C() {
      this.G.clear();
      if (this.Y.exists()) {
         this.Z = YamlConfiguration.loadConfiguration(this.Y);

         for(String var2 : this.Z.getKeys(false)) {
            UUID var3 = UUID.fromString(var2);
            HashMap var4 = new HashMap();
            ConfigurationSection var5 = this.Z.getConfigurationSection(var2);
            if (var5 != null) {
               for(String var7 : var5.getKeys(false)) {
                  long var8 = var5.getLong(var7 + ".amount", 0L);
                  double var10 = var5.getDouble(var7 + ".total", (double)0.0F);
                  var4.put(var7, new double[]{(double)var8, var10});
               }
            }

            this.G.put(var3, var4);
         }

      }
   }

   private void D() {
      if (this.Z == null) {
         this.Z = new YamlConfiguration();
      }

      for(Map.Entry var2 : this.G.entrySet()) {
         String var3 = ((UUID)var2.getKey()).toString();

         for(Map.Entry var5 : ((Map)var2.getValue()).entrySet()) {
            this.Z.set(var3 + "." + (String)var5.getKey() + ".amount", ((double[])var5.getValue())[0]);
            this.Z.set(var3 + "." + (String)var5.getKey() + ".total", ((double[])var5.getValue())[1]);
         }
      }

      try {
         this.Z.save(this.Y);
      } catch (IOException var6) {
         this.D.getLogger().warning("Failed to save sell history: " + var6.getMessage());
      }

   }

   private void A(UUID var1, String var2, long var3, double var5) {
      Map var7 = (Map)this.G.computeIfAbsent(var1, (var0) -> new ConcurrentHashMap());
      double[] var8 = (double[])var7.computeIfAbsent(var2, (var0) -> new double[]{(double)0.0F, (double)0.0F});
      var8[0] += (double)var3;
      var8[1] += var5;
   }

   private _I A(ItemStack var1) {
      if (var1 == null) {
         return null;
      } else {
         Material var2 = var1.getType();
         if (this.F.containsKey(var2)) {
            return (_I)this.F.get(var2);
         } else {
            String var3 = var2.name();
            if (!var3.contains("LOG") && !var3.contains("LEAVES") && !var3.contains("SAPLING")) {
               if (!var3.contains("ORE") && !var3.contains("INGOT") && !var3.contains("NUGGET")) {
                  if (!var3.contains("SWORD") && !var3.contains("AXE") && !var3.contains("PICKAXE") && !var3.contains("SHOVEL") && !var3.contains("HOE") && !var3.contains("HELMET") && !var3.contains("CHESTPLATE") && !var3.contains("LEGGINGS") && !var3.contains("BOOTS")) {
                     if (!var3.contains("POTION") && !var3.contains("BREWING")) {
                        if (var3.contains("BOOK")) {
                           return D._I.A;
                        } else if (var3.contains("FISH")) {
                           return D._I.F;
                        } else if (!var3.contains("ROTTEN_FLESH") && !var3.contains("BONE") && !var3.contains("STRING") && !var3.contains("GUNPOWDER") && !var3.contains("ENDER_PEARL") && !var3.contains("BLAZE_ROD") && !var3.contains("GHAST_TEAR")) {
                           if (!var3.contains("WHEAT") && !var3.contains("CARROT") && !var3.contains("POTATO") && !var3.contains("BEETROOT") && !var3.contains("MELON") && !var3.contains("PUMPKIN") && !var3.contains("SUGAR_CANE")) {
                              return !var3.contains("STONE") && !var3.contains("BRICK") && !var3.contains("SAND") && !var3.contains("GRAVEL") && !var3.contains("GLASS") ? null : D._I.I;
                           } else {
                              return D._I.E;
                           }
                        } else {
                           return D._I.D;
                        }
                     } else {
                        return D._I.L;
                     }
                  } else {
                     return D._I.B;
                  }
               } else {
                  return D._I.G;
               }
            } else {
               return D._I.J;
            }
         }
      }
   }

   private void A(Player var1, _I var2, double var3) {
      if (var2 != null && !(var3 <= (double)0.0F)) {
         _A var5 = this.B(var1, var2);
         var5.A += var3;
         int var6 = this.A(var5.B);
         if (var6 < this.T.size() && var5.A >= (Double)this.T.get(var6)) {
            double var7 = var5.B + this.O;
            if (var7 <= this.X) {
               var5.B = var7;
               String var10002 = var2.B();
               var1.sendMessage(this.A("&aYour " + var10002 + " multiplier increased to " + String.format("%.1f", var5.B) + "x!"));
            }
         }

         Bukkit.getScheduler().runTaskAsynchronously(this.D, this::F);
      }
   }

   private int A(double var1) {
      return (int)Math.round((var1 - this.H) / this.O);
   }

   private _E A(ItemStack var1, int var2, Player var3, Map<_I, Double> var4) {
      _E var5 = new _E();
      if (var1 != null && var1.getType() != Material.AIR) {
         if (var2 > 3) {
            var5.A.add(var1.clone());
            return var5;
         } else {
            String var6 = var1.getType().name();
            if (this.C.contains(var6)) {
               var5.A.add(var1.clone());
               return var5;
            } else {
               double var7 = this.B(var1);
               _I var9 = this.A(var1);
               ItemMeta var11 = var1.getItemMeta();
               if (var11 instanceof BlockStateMeta) {
                  BlockStateMeta var10 = (BlockStateMeta)var11;
                  BlockState var19 = var10.getBlockState();
                  if (var19 instanceof Container) {
                     Container var12 = (Container)var19;
                     if (var12.getInventory() != null) {
                        for(ItemStack var16 : var12.getInventory().getContents()) {
                           _E var17 = this.A(var16, var2 + 1, var3, var4);
                           var5.B += var17.B;
                           var5.A.addAll(var17.A);
                        }

                        if (var7 > (double)0.0F) {
                           double var21 = this.C(var3, var9);
                           double var22 = var7 * (double)var1.getAmount() * var21;
                           var5.B += var22;
                           if (var9 != null) {
                              var4.put(var9, (Double)var4.getOrDefault(var9, (double)0.0F) + var7 * (double)var1.getAmount());
                           }
                        }

                        return var5;
                     }
                  }
               }

               if (var7 > (double)0.0F) {
                  double var18 = this.C(var3, var9);
                  double var20 = var7 * (double)var1.getAmount() * var18;
                  var5.B += var20;
                  if (var9 != null) {
                     var4.put(var9, (Double)var4.getOrDefault(var9, (double)0.0F) + var7 * (double)var1.getAmount());
                  }
               } else {
                  var5.A.add(var1.clone());
               }

               return var5;
            }
         }
      } else {
         return var5;
      }
   }

   private double C(Player var1, _I var2) {
      if (var2 == null) {
         return (double)1.0F;
      } else {
         _A var3 = this.B(var1, var2);
         return Math.min(var3.B, this.X);
      }
   }

   private double B(ItemStack var1) {
      String var2 = var1.getType().name();
      ItemMeta var4 = var1.getItemMeta();
      if (var4 instanceof EnchantmentStorageMeta var3) {
         if (!var3.getStoredEnchants().isEmpty()) {
            double var12 = (double)-1.0F;

            for(Map.Entry var7 : var3.getStoredEnchants().entrySet()) {
               String var10000 = ((Enchantment)var7.getKey()).getKey().getKey().toUpperCase(Locale.ROOT);
               String var8 = "ENCHANTED_BOOK:" + var10000 + ":" + String.valueOf(var7.getValue()) + ":";
               Double var9 = (Double)this.L.get(var8);
               if (var9 != null && var9 > var12) {
                  var12 = var9;
               }
            }

            if (var12 > (double)0.0F) {
               return var12;
            }
         }
      }

      var4 = var1.getItemMeta();
      if (var4 instanceof PotionMeta var10) {
         PotionData var14 = var10.getBasePotionData();
         PotionType var5 = var14.getType();
         String var15 = this.A(var5);
         String var16 = var14.isUpgraded() ? "2:" : "";
         String var17 = var1.getType().name() + ":" + var15 + ":" + var16;
         Double var18 = (Double)this.L.get(var17.toUpperCase(Locale.ROOT));
         if (var18 != null) {
            return var18;
         }
      }

      Double var11 = (Double)this.L.get(var2);
      return var11 != null ? var11 : (double)-1.0F;
   }

   private String A(PotionType var1) {
      switch (var1.name()) {
         case "SPEED":
         case "SWIFTNESS":
            return "SWIFTNESS";
         case "INSTANT_HEAL":
         case "INSTANT_HEALTH":
         case "HEALING":
            return "HEALING";
         case "INSTANT_DAMAGE":
         case "INSTANT_HARM":
         case "HARMING":
            return "HARMING";
         case "JUMP":
         case "LEAPING":
            return "LEAPING";
         case "SLOW_FALLING":
            return "SLOW_FALLING";
         case "FIRE_RESISTANCE":
            return "FIRE_RESISTANCE";
         case "LUCK":
            return "LUCK";
         case "NIGHT_VISION":
            return "NIGHT_VISION";
         case "REGEN":
         case "REGENERATION":
            return "REGENERATION";
         case "STRENGTH":
            return "STRENGTH";
         case "TURTLE_MASTER":
            return "TURTLE_MASTER";
         case "WATER_BREATHING":
            return "WATER_BREATHING";
         default:
            return var1.name();
      }
   }

   private void A(Player var1, List<ItemStack> var2) {
      if (!var2.isEmpty()) {
         HashMap var3 = var1.getInventory().addItem((ItemStack[])var2.toArray(new ItemStack[0]));
         if (!var3.isEmpty()) {
            var3.values().forEach((var1x) -> var1.getWorld().dropItemNaturally(var1.getLocation(), var1x));
         }

      }
   }

   private String D(String var1) {
      String[] var2 = var1.toLowerCase().split("_");
      StringBuilder var3 = new StringBuilder();

      for(String var7 : var2) {
         var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1)).append(" ");
      }

      return var3.toString().trim();
   }

   private String B(double var1) {
      double var3 = Math.abs(var1);
      double var5 = var1;
      String var7 = "";
      if (var3 >= 1.0E12) {
         var5 = var1 / 1.0E12;
         var7 = "t";
      } else if (var3 >= (double)1.0E9F) {
         var5 = var1 / (double)1.0E9F;
         var7 = "b";
      } else if (var3 >= (double)1000000.0F) {
         var5 = var1 / (double)1000000.0F;
         var7 = "m";
      } else if (var3 >= (double)1000.0F) {
         var5 = var1 / (double)1000.0F;
         var7 = "k";
      }

      if (Math.abs(var5 - (double)Math.round(var5)) < 1.0E-9) {
         String var8 = String.valueOf(Math.round(var5));
         return var8 + var7;
      } else {
         String var10000 = E.format(var5);
         return var10000 + var7;
      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() instanceof _H) {
         HumanEntity var3 = var1.getPlayer();
         if (var3 instanceof Player) {
            Player var2 = (Player)var3;
            Inventory var19 = var1.getInventory();
            ItemStack[] var4 = var19.getContents();
            var19.clear();
            double var5 = (double)0.0F;
            ArrayList var7 = new ArrayList();
            HashMap var8 = new HashMap();
            HashMap var9 = new HashMap();

            for(ItemStack var13 : var4) {
               if (var13 != null && var13.getType() != Material.AIR) {
                  _E var14 = this.A(var13, 0, var2, var8);
                  var5 += var14.B;
                  var7.addAll(var14.A);
                  if (var14.B > (double)0.0F) {
                     double var15 = this.B(var13);
                     if (var15 > (double)0.0F) {
                        String var17 = var13.getType().name();
                        double[] var18 = (double[])var9.computeIfAbsent(var17, (var0) -> new double[]{(double)0.0F, (double)0.0F});
                        var18[0] += (double)var13.getAmount();
                        var18[1] += var14.B;
                     }
                  }
               }
            }

            if (var5 > (double)0.0F) {
               PlayerData var20 = this.D.getPlayerDataManager().get(var2.getUniqueId());
               if (var20 != null) {
                  var20.setMoney(var20.getMoney() + var5);
                  Bukkit.getScheduler().runTaskAsynchronously(this.D, () -> this.D.getPlayerDataManager().savePlayer(var2.getUniqueId()));
               }

               for(Map.Entry var25 : var9.entrySet()) {
                  this.A(var2.getUniqueId(), (String)var25.getKey(), (long)((double[])var25.getValue())[0], ((double[])var25.getValue())[1]);
               }

               Bukkit.getScheduler().runTaskAsynchronously(this.D, this::D);
               String var23 = this.B(var5);
               String var26 = this.M.replace("%amount%", var23);
               var2.sendMessage(var26);
               var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(this.I.replace("%amount%", var23)));
               var2.playSound(var2.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }

            if (!var7.isEmpty()) {
               var2.sendMessage(this.A);
               var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
               this.A((Player)var2, (List)var7);
            }

            var2.updateInventory();

            for(Map.Entry var24 : var8.entrySet()) {
               this.A(var2, (_I)var24.getKey(), (Double)var24.getValue());
            }

         }
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      InventoryHolder var2 = var1.getInventory().getHolder();
      if (var2 instanceof _D) {
         var1.setCancelled(true);
         Player var9 = (Player)var1.getWhoClicked();
         int var11 = var1.getRawSlot();
         if (var11 >= 9 && var11 <= 17) {
            int var13 = var11 - 9;
            _I[] var16 = D._I.values();
            if (var13 < var16.length) {
               this.A(var9, var16[var13]);
            }
         }

      } else if (!(var2 instanceof _H)) {
         String var3 = var1.getView().getTitle();
         if (var3.contains(this.R)) {
            var1.setCancelled(true);
            Player var10 = (Player)var1.getWhoClicked();
            int var12 = var1.getRawSlot();
            if (var12 == 45) {
               int var6 = ((MetadataValue)var10.getMetadata("sell_history_page").get(0)).asInt();
               _J var7 = D._J.valueOf(((MetadataValue)var10.getMetadata("sell_history_sort").get(0)).asString());
               this.A(var10, var6 - 1, var7);
            } else if (var12 == 53) {
               int var14 = ((MetadataValue)var10.getMetadata("sell_history_page").get(0)).asInt();
               _J var17 = D._J.valueOf(((MetadataValue)var10.getMetadata("sell_history_sort").get(0)).asString());
               this.A(var10, var14 + 1, var17);
            } else if (var12 == 49) {
               int var15 = ((MetadataValue)var10.getMetadata("sell_history_page").get(0)).asInt();
               _J var18 = D._J.valueOf(((MetadataValue)var10.getMetadata("sell_history_sort").get(0)).asString());
               _J var8 = var18 == D._J.A ? D._J.C : D._J.A;
               this.A(var10, var15, var8);
            }

         } else {
            if (var3.contains("ʜɪѕᴛᴏʀʏ") && var2 == null) {
               var1.setCancelled(true);
               Player var4 = (Player)var1.getWhoClicked();
               int var5 = var1.getRawSlot();
               if (var5 == 49) {
                  this.C(var4);
               }
            }

         }
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof _D) {
         var1.setCancelled(true);
      }

   }

   static {
      E = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.US));
      N = Pattern.compile("&#([A-Fa-f0-9]{6})");
   }

   private static class _A {
      double A;
      double B;

      _A(double var1, double var3) {
         this.A = var1;
         this.B = var3;
      }
   }

   public class _B implements CommandExecutor {
      public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
         if (var4.length > 0 && var4[0].equalsIgnoreCase("reload")) {
            if (!var1.isOp()) {
               var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only operators can reload the sell system.");
               return true;
            } else {
               D.this.G();
               var1.sendMessage(String.valueOf(org.bukkit.ChatColor.GREEN) + "Sell configuration and prices reloaded.");
               return true;
            }
         } else if (var1 instanceof Player) {
            Player var5 = (Player)var1;
            D.this.A(var5);
            return true;
         } else {
            var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only players can use this command (or use /sell reload).");
            return true;
         }
      }
   }

   public class _C implements CommandExecutor {
      public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
         if (var1 instanceof Player var5) {
            D.this.A(var5, 1, D._J.A);
            return true;
         } else {
            var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only players can use this command.");
            return true;
         }
      }
   }

   private static class _D implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   private static class _E {
      double B = (double)0.0F;
      List<ItemStack> A = new ArrayList();
   }

   private static class _F {
      String A;
      String B;

      _F(String var1, String var2) {
         this.A = var1;
         this.B = var2;
      }
   }

   public class _G implements CommandExecutor {
      public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
         if (var1 instanceof Player var5) {
            D.this.C(var5);
            return true;
         } else {
            var1.sendMessage(String.valueOf(org.bukkit.ChatColor.RED) + "Only players can use this command.");
            return true;
         }
      }
   }

   private static class _H implements InventoryHolder {
      public Inventory getInventory() {
         return null;
      }
   }

   public static enum _I {
      E("Crops", Material.WHEAT),
      G("Ores", Material.DIAMOND),
      D("Mob Drops", Material.ROTTEN_FLESH),
      J("Natural", Material.OAK_LOG),
      B("Armor & Tools", Material.IRON_CHESTPLATE),
      F("Fish", Material.COD),
      A("Books", Material.ENCHANTED_BOOK),
      L("Potions", Material.POTION),
      I("Blocks", Material.BRICK);

      private final String K;
      private final Material H;

      private _I(String var3, Material var4) {
         this.K = var3;
         this.H = var4;
      }

      public String B() {
         return this.K;
      }

      public Material D() {
         return this.H;
      }

      public String C() {
         return this.name().toLowerCase();
      }

      public static _I A(String var0) {
         String var1 = var0.replace(".yml", "").toUpperCase();

         try {
            return valueOf(var1);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      }

      // $FF: synthetic method
      private static _I[] A() {
         return new _I[]{E, G, D, J, B, F, A, L, I};
      }
   }

   private static enum _J {
      A,
      C;

      // $FF: synthetic method
      private static _J[] A() {
         return new _J[]{A, C};
      }
   }
}
