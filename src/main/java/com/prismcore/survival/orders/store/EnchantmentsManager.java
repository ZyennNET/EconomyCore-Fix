package com.prismcore.survival.orders.store;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class EnchantmentsManager {
   private final PrismOrders pl;
   private final GUI gui = new GUI();
   private final Messages messages = new Messages();
   private final Map<String, List<EnchantOption>> byCategory = new HashMap();
   private final Map<Material, String> materialCategory = new HashMap();
   private Integer pinnedMending = 18;
   private final Map<Integer, Integer> pinnedUnbreaking = new HashMap();

   public EnchantmentsManager(PrismOrders var1) {
      this.pl = var1;
      this.pinnedUnbreaking.put(1, 17);
      this.pinnedUnbreaking.put(2, 26);
      this.pinnedUnbreaking.put(3, 35);
      this.ensureDefaultFile();
      this.loadFromFile();
      this.mapMaterials();
   }

   public void reload() {
      this.pl.getPlugin().getLogger().info("[Enchantments] Reload requested.");
      this.ensureDefaultFile();
      this.loadFromFile();
   }

   private File getFile() {
      return new File(new File(this.pl.getPlugin().getDataFolder(), "economy/orders"), "enchantments.yml");
   }

   private void ensureDefaultFile() {
      File var1 = this.getFile();
      if (!var1.exists()) {
         this.pl.getPlugin().saveResource("economy/orders/enchantments.yml", false);
      }

   }

   private void loadFromFile() {
      File var3 = this.getFile();
      YamlConfiguration var4 = YamlConfiguration.loadConfiguration(var3);
      ConfigurationSection var5 = var4.getConfigurationSection("messages");
      if (var5 != null) {
         this.messages.loreSelect = Utils.formatColors(var5.getString("select", this.messages.loreSelect));
         this.messages.loreSelected = Utils.formatColors(var5.getString("selected", this.messages.loreSelected));
         this.messages.loreCannot = Utils.formatColors(var5.getString("cannot", this.messages.loreCannot));
      }

      ConfigurationSection var2;
      if ((var2 = var4.getConfigurationSection("gui")) != null) {
         this.gui.title = Utils.formatColors(var2.getString("title", this.gui.title));
         this.gui.rows = var2.getInt("rows", this.gui.rows);
         ConfigurationSection var8 = var2.getConfigurationSection("slots");
         if (var8 != null) {
            this.gui.slotItem = var8.getInt("item", this.gui.slotItem);
            this.gui.slotCancel = var8.getInt("cancel", this.gui.slotCancel);
            this.gui.slotPrev = var8.getInt("prev", this.gui.slotPrev);
            this.gui.slotNext = var8.getInt("next", this.gui.slotNext);
            this.gui.slotConfirm = var8.getInt("confirm", this.gui.slotConfirm);
         }

         ConfigurationSection var7;
         if ((var7 = var2.getConfigurationSection("buttons")) != null) {
            ConfigurationSection var12 = var7.getConfigurationSection("cancel");
            if (var12 != null) {
               this.gui.cancelMat = mat(var12.getString("material"), this.gui.cancelMat);
               this.gui.cancelName = var12.getString("name", this.gui.cancelName);
               this.gui.cancelLore = var12.getStringList("lore");
            }

            ConfigurationSection var11;
            if ((var11 = var7.getConfigurationSection("confirm")) != null) {
               this.gui.confirmMat = mat(var11.getString("material"), this.gui.confirmMat);
               this.gui.confirmName = var11.getString("name", this.gui.confirmName);
               this.gui.confirmLore = var11.getStringList("lore");
            }

            ConfigurationSection var10;
            if ((var10 = var7.getConfigurationSection("page_filler")) != null) {
               this.gui.fillerEnabled = var10.getBoolean("enabled", this.gui.fillerEnabled);
               this.gui.fillerMat = mat(var10.getString("material"), this.gui.fillerMat);
               this.gui.fillerName = var10.getString("name", this.gui.fillerName);
            }

            ConfigurationSection var9;
            if ((var9 = var7.getConfigurationSection("filler")) != null) {
               this.gui.extraFillerEnabled = var9.getBoolean("enabled", this.gui.extraFillerEnabled);
               this.gui.extraFillerMat = mat(var9.getString("material"), this.gui.extraFillerMat);
               this.gui.extraFillerName = var9.getString("displayname", this.gui.extraFillerName);
               this.gui.extraFillerSlots = parseSlots(var9.getString("slots", ""));
            }
         }

         ConfigurationSection var6;
         if ((var6 = var2.getConfigurationSection("sounds")) != null) {
            ConfigurationSection var20 = var6.getConfigurationSection("click");
            if (var20 != null) {
               this.gui.click = sound(var20.getString("name"), this.gui.click);
               this.gui.clickVol = (float)var20.getDouble("volume", (double)this.gui.clickVol);
               this.gui.clickPitch = (float)var20.getDouble("pitch", (double)this.gui.clickPitch);
            }

            ConfigurationSection var18;
            if ((var18 = var6.getConfigurationSection("cancel")) != null) {
               this.gui.cancel = sound(var18.getString("name"), this.gui.cancel);
               this.gui.cancelVol = (float)var18.getDouble("volume", (double)this.gui.cancelVol);
               this.gui.cancelPitch = (float)var18.getDouble("pitch", (double)this.gui.cancelPitch);
            }
         }
      }

      ConfigurationSection var1;
      if ((var1 = var4.getConfigurationSection("pinned-slots")) != null) {
         int var14 = var1.getInt("mending", this.pinnedMending != null ? this.pinnedMending : 18);
         this.pinnedMending = var14;
         this.pinnedUnbreaking.clear();
         ConfigurationSection var16 = var1.getConfigurationSection("unbreaking");
         if (var16 != null) {
            for(String var19 : var16.getKeys(false)) {
               try {
                  this.pinnedUnbreaking.put(Integer.parseInt(var19), var16.getInt(var19));
               } catch (Exception var13) {
               }
            }
         } else {
            this.pinnedUnbreaking.put(1, 17);
            this.pinnedUnbreaking.put(2, 26);
            this.pinnedUnbreaking.put(3, 35);
         }
      }

      this.byCategory.clear();
      boolean var15 = this.loadCategoriesSection(var4);
      if (!var15) {
         this.loadTopLevelCategories(var4);
      }

   }

   private boolean loadCategoriesSection(YamlConfiguration var1) {
      ConfigurationSection var2 = var1.getConfigurationSection("categories");
      if (var2 == null) {
         return false;
      } else {
         boolean var3 = false;

         for(String var5 : var2.getKeys(false)) {
            ConfigurationSection var6 = var2.getConfigurationSection(var5);
            if (var6 != null) {
               List var7 = this.parseOptionsSection(var5, var6);
               this.applyPinned(var7);
               this.byCategory.put(var5, var7);
               if (!var7.isEmpty()) {
                  var3 = true;
               }
            }
         }

         return var3;
      }
   }

   private boolean loadTopLevelCategories(YamlConfiguration var1) {
      Set var2 = Set.of("messages", "gui", "sounds", "pinned-slots", "categories");
      boolean var3 = false;

      for(String var5 : var1.getKeys(false)) {
         ConfigurationSection var6;
         if (!var2.contains(var5) && (var6 = var1.getConfigurationSection(var5)) != null) {
            List var7 = this.parseOptionsSection(var5, var6);
            this.applyPinned(var7);
            this.byCategory.put(var5, var7);
            if (!var7.isEmpty()) {
               var3 = true;
            }
         }
      }

      return var3;
   }

   private List<EnchantOption> parseOptionsSection(String var1, ConfigurationSection var2) {
      ArrayList var3 = new ArrayList();

      for(String var5 : var2.getKeys(false)) {
         ConfigurationSection var8 = var2.getConfigurationSection(var5);
         String var7;
         if (var8 != null && !(var7 = var8.getString("enchantment", "")).isEmpty() && var7.contains(";")) {
            String[] var9 = var7.split(";", 2);
            Enchantment var10 = null;

            try {
               var10 = Enchantment.getByKey(NamespacedKey.minecraft(var9[0]));
               if (var10 == null) {
                  var10 = Enchantment.getByName(var9[0].toUpperCase(Locale.ENGLISH));
               }
            } catch (Throwable var12) {
            }

            if (var10 != null) {
               int var6;
               try {
                  var6 = Integer.parseInt(var9[1]);
               } catch (Exception var13) {
                  continue;
               }

               EnchantOption var11 = new EnchantOption();
               var11.key = var5;
               var11.ench = var10;
               var11.level = var6;
               var11.category = var1;
               var11.slot = var8.isInt("slot") ? var8.getInt("slot") : null;
               var11.page = Math.max(1, var8.getInt("page", 1));
               var3.add(var11);
            }
         }
      }

      return var3;
   }

   private void applyPinned(List<EnchantOption> var1) {
      for(EnchantOption var3 : var1) {
         if (var3.ench != null) {
            String var5 = var3.ench.getKey() != null ? var3.ench.getKey().getKey() : var3.ench.getName().toLowerCase(Locale.ENGLISH);
            if (this.pinnedMending != null && "mending".equals(var5)) {
               var3.slot = this.pinnedMending;
            }

            Integer var4;
            if ("unbreaking".equals(var5) && (var4 = (Integer)this.pinnedUnbreaking.get(var3.level)) != null) {
               var3.slot = var4;
            }
         }
      }

   }

   private void mapMaterials() {
      this.materialCategory.clear();

      for(Material var4 : Material.values()) {
         if (var4.isItem()) {
            String var5 = var4.name();
            if (var5.endsWith("_SWORD")) {
               this.materialCategory.put(var4, "sword");
            } else if (var5.endsWith("_PICKAXE")) {
               this.materialCategory.put(var4, "pickaxe");
            } else if (var5.endsWith("_AXE")) {
               this.materialCategory.put(var4, "axe");
            } else if (var5.endsWith("_SHOVEL")) {
               this.materialCategory.put(var4, "shovel");
            } else if (var5.endsWith("_HOE")) {
               this.materialCategory.put(var4, "hoe");
            } else if (var5.endsWith("_HELMET")) {
               this.materialCategory.put(var4, "helmet");
            } else if (var5.endsWith("_CHESTPLATE")) {
               this.materialCategory.put(var4, "chestplate");
            } else if (var5.endsWith("_LEGGINGS")) {
               this.materialCategory.put(var4, "leggings");
            } else if (var5.endsWith("_BOOTS")) {
               this.materialCategory.put(var4, "boots");
            } else if (var5.equals("BOW")) {
               this.materialCategory.put(var4, "bow");
            } else if (var5.equals("CROSSBOW")) {
               this.materialCategory.put(var4, "crossbow");
            } else if (var5.equals("TRIDENT")) {
               this.materialCategory.put(var4, "trident");
            } else if (var5.equals("SHIELD")) {
               this.materialCategory.put(var4, "shield");
            } else if (var5.equals("FISHING_ROD")) {
               this.materialCategory.put(var4, "fishing_rod");
            } else if (var5.equals("ELYTRA")) {
               this.materialCategory.put(var4, "elytra");
            }
         }
      }

   }

   private static Material mat(String var0, Material var1) {
      if (var0 == null) {
         return var1;
      } else {
         Material var2 = Material.matchMaterial(var0);
         return var2 != null ? var2 : var1;
      }
   }

   private static Sound sound(String var0, Sound var1) {
      if (var0 == null) {
         return var1;
      } else {
         try {
            return Sound.valueOf(var0);
         } catch (Exception var3) {
            return var1;
         }
      }
   }

   private static List<Integer> parseSlots(String var0) {
      if (var0 != null && !var0.isBlank()) {
         ArrayList var1 = new ArrayList();

         for(String var5 : var0.split("\\s*,\\s*")) {
            try {
               var1.add(Integer.parseInt(var5));
            } catch (Exception var7) {
            }
         }

         return var1;
      } else {
         return List.of();
      }
   }

   public GUI getGui() {
      return this.gui;
   }

   public Messages getMessages() {
      return this.messages;
   }

   public boolean hasOptionsFor(Material var1) {
      String var2 = (String)this.materialCategory.get(var1);
      return var2 != null && this.byCategory.containsKey(var2) && !((List)this.byCategory.get(var2)).isEmpty();
   }

   public List<EnchantOption> optionsFor(Material var1) {
      String var2 = (String)this.materialCategory.get(var1);
      return var2 == null ? List.of() : (List)this.byCategory.getOrDefault(var2, List.of());
   }

   public int maxPage(List<EnchantOption> var1) {
      int var2 = 1;

      for(EnchantOption var4 : var1) {
         var2 = Math.max(var2, Math.max(1, var4.page));
      }

      return Math.max(0, var2 - 1);
   }

   public void playClick(Player var1) {
      if (this.gui.click != null) {
         var1.playSound(var1.getLocation(), this.gui.click, this.gui.clickVol, this.gui.clickPitch);
      }

   }

   public void playCancel(Player var1) {
      if (this.gui.cancel != null) {
         var1.playSound(var1.getLocation(), this.gui.cancel, this.gui.cancelVol, this.gui.cancelPitch);
      }

   }

   public static class EnchantOption {
      public String key;
      public Enchantment ench;
      public int level;
      public Integer slot;
      public int page = 1;
      public String category;
   }

   public static class GUI {
      public String title = "&#44b3ffPick Enchantments";
      public int rows = 6;
      public int slotItem = 0;
      public int slotCancel = 46;
      public int slotPrev = 45;
      public int slotNext = 53;
      public int slotConfirm = 52;
      public Material cancelMat;
      public String cancelName;
      public List<String> cancelLore;
      public Material confirmMat;
      public String confirmName;
      public List<String> confirmLore;
      public boolean fillerEnabled;
      public Material fillerMat;
      public String fillerName;
      public boolean extraFillerEnabled;
      public Material extraFillerMat;
      public String extraFillerName;
      public List<Integer> extraFillerSlots;
      public Sound click;
      public float clickVol;
      public float clickPitch;
      public Sound cancel;
      public float cancelVol;
      public float cancelPitch;

      public GUI() {
         this.cancelMat = Material.RED_STAINED_GLASS_PANE;
         this.cancelName = "&cCANCEL";
         this.cancelLore = List.of("&fClick to return");
         this.confirmMat = Material.LIME_STAINED_GLASS_PANE;
         this.confirmName = "&aCONFIRM";
         this.confirmLore = List.of("&fClick to confirm enchants");
         this.fillerEnabled = false;
         this.fillerMat = Material.GRAY_STAINED_GLASS_PANE;
         this.fillerName = "&7 ";
         this.extraFillerEnabled = false;
         this.extraFillerMat = Material.GRAY_STAINED_GLASS_PANE;
         this.extraFillerName = "&7 ";
         this.extraFillerSlots = List.of();
         this.click = Sound.BLOCK_ENCHANTMENT_TABLE_USE;
         this.clickVol = 1.0F;
         this.clickPitch = 1.0F;
         this.cancel = Sound.BLOCK_NOTE_BLOCK_BASS;
         this.cancelVol = 1.0F;
         this.cancelPitch = 0.8F;
      }
   }

   public static class Messages {
      public String loreSelect = "&7Click to select";
      public String loreSelected = "&7Selected";
      public String loreCannot = "&7Cannot add this enchantment";
   }
}
