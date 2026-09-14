package com.h2ph.J.C;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class C implements CommandExecutor, Listener {
   private final PrismSurvival J;
   private final Map<String, FileConfiguration> M = new HashMap();
   private FileConfiguration A;
   private final Map<Integer, String> B = new HashMap();
   private final Map<UUID, _B> K = new HashMap();
   private final Map<UUID, _A> O = new HashMap();
   private final Map<String, FileConfiguration> G = new HashMap();
   private final Map<String, Map<Integer, ItemStack>> I = new HashMap();
   private final Map<String, Map<Integer, _D>> D = new HashMap();
   private final Map<String, Map<Integer, _C>> C = new HashMap();
   private final Map<Integer, ItemStack> E = new HashMap();
   private String N;
   private String L;
   private String H;
   private String F;

   public C(PrismSurvival var1) {
      this.J = var1;
      this.A();
   }

   public void reload() {
      this.A();
   }

   private void A() {
      this.M.clear();
      this.B.clear();
      File var1 = new File(this.J.getDataFolder(), "economy/shop/config.yml");
      if (!var1.exists()) {
         this.J.saveResource("economy/shop/config.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(var1);
      File var2 = new File(this.J.getDataFolder(), "economy/shop/categories");
      if (!var2.exists()) {
         var2.mkdirs();
      }

      String[] var3 = new String[]{"end.yml", "nether.yml", "gear.yml", "food.yml", "shard.yml"};

      for(String var7 : var3) {
         File var8 = new File(var2, var7);
         if (!var8.exists()) {
            try {
               this.J.saveResource("economy/shop/categories/" + var7, false);
            } catch (Exception var30) {
            }
         }
      }

      File[] var31 = var2.listFiles((var0, var1x) -> var1x.toLowerCase().endsWith(".yml"));
      if (var31 != null) {
         for(File var47 : var31) {
            String var9 = var47.getName();
            YamlConfiguration var10 = YamlConfiguration.loadConfiguration(var47);
            this.M.put(var9, var10);
         }
      }

      if (this.A.contains("categories")) {
         ConfigurationSection var33 = this.A.getConfigurationSection("categories");

         for(String var43 : var33.getKeys(false)) {
            int var48 = var33.getInt(var43 + ".slot");
            String var51 = var33.getString(var43 + ".file");
            this.B.put(var48, var51);
         }
      }

      this.N = this.A(this.A.getString("gui-title", "&8ѕʜᴏᴘ"));
      this.L = this.A("&8ѕʜᴏᴘ - ");
      this.H = this.A("&8ʙᴜʏɪɴɢ");
      this.F = this.A("&8ᴄᴏɴꜰɪʀᴍ ᴘᴜʀᴄʜᴀѕᴇ");
      this.G.clear();

      for(FileConfiguration var39 : this.M.values()) {
         if (var39.contains("gui-title")) {
            String var44 = this.A(var39.getString("gui-title"));
            this.G.put(var44, var39);
         }
      }

      this.E.clear();
      if (this.A.contains("categories")) {
         ConfigurationSection var35 = this.A.getConfigurationSection("categories");

         for(String var45 : var35.getKeys(false)) {
            int var49 = var35.getInt(var45 + ".slot");
            this.E.put(var49, this.A(var35.getConfigurationSection(var45)));
         }
      }

      this.I.clear();
      this.D.clear();
      this.C.clear();

      for(Map.Entry var41 : this.M.entrySet()) {
         String var46 = (String)var41.getKey();
         FileConfiguration var50 = (FileConfiguration)var41.getValue();
         HashMap var52 = new HashMap();
         HashMap var53 = new HashMap();
         HashMap var11 = new HashMap();
         ConfigurationSection var12 = var50.getConfigurationSection("items");
         if (var12 != null) {
            for(String var14 : var12.getKeys(false)) {
               int var15 = var12.getInt(var14 + ".slot");
               String var16 = var12.getString(var14 + ".material", "STONE");
               Material var17 = Material.getMaterial(var16.toUpperCase());
               if (var17 == null) {
                  var17 = Material.STONE;
               }

               boolean var18 = var12.contains(var14 + ".shard_price") || var12.contains(var14 + ".price");
               boolean var19 = var12.contains(var14 + ".command");
               if (!var12.contains(var14 + ".shard_price") && !var19) {
                  if (var18) {
                     double var54 = var12.getDouble(var14 + ".price", (double)0.0F);
                     int var55 = var12.getInt(var14 + ".amount", 1);
                     List var23 = var12.getIntegerList(var14 + ".values");
                     if (var23.isEmpty()) {
                        var23 = Arrays.asList(1, 10, 64);
                     }

                     List var56 = var12.getStringList(var14 + ".effects");
                     int var57 = var12.getInt(var14 + ".effect_duration", 30);
                     int var58 = var12.getInt(var14 + ".effect_level", 1);
                     var53.put(var15, new _D(var17, var54, var23, var56, var57, var58));
                     ItemStack var59 = new ItemStack(var17, var55);
                     ItemMeta var60 = var59.getItemMeta();
                     Object[] var61 = new Object[]{var54};
                     var60.setLore(Collections.singletonList(this.A("&fBuy price: &a$" + String.format("%,.0f", var61))));
                     var59.setItemMeta(var60);
                     var52.put(var15, var59);
                  }
               } else {
                  String var20 = var12.getString(var14 + ".name", "");
                  String var21 = var12.getString(var14 + ".currency", "SHARDS").toUpperCase();
                  double var22 = var21.equals("MONEY") ? var12.getDouble(var14 + ".price", (double)0.0F) : (double)var12.getInt(var14 + ".shard_price", var12.getInt(var14 + ".price", 0));
                  String var24 = var12.getString(var14 + ".key", (String)null);
                  String var25 = var12.getString(var14 + ".spawner", (String)null);
                  String var26 = var12.getString(var14 + ".command", (String)null);
                  var11.put(var15, new _C(var14, var20, var22, var21, var24, var25, var26, var46, var17));
                  ItemStack var27 = new ItemStack(var17, 1);
                  ItemMeta var28 = var27.getItemMeta();
                  if (!var20.isEmpty()) {
                     var28.setDisplayName(this.A(var20));
                  }

                  ArrayList var29 = new ArrayList();
                  if (var21.equals("MONEY")) {
                     Object[] var10003 = new Object[]{var22};
                     var29.add(this.A("&fBuy price: &a$" + String.format("%,.0f", var10003)));
                  } else {
                     var29.add(this.A("&fBuy price: &5" + (int)var22 + "x &lShards"));
                  }

                  var28.setLore(var29);
                  var27.setItemMeta(var28);
                  var52.put(var15, var27);
               }
            }
         }

         this.I.put(var46, var52);
         this.D.put(var46, var53);
         this.C.put(var46, var11);
      }

   }

   public boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
      if (var4.length > 0 && var4[0].equalsIgnoreCase("reload") && var1.hasPermission("prismcore.admin.shop")) {
         this.A();
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "Shop configuration reloaded.");
         return true;
      } else if (var1 instanceof Player) {
         Player var5 = (Player)var1;
         this.C(var5);
         return true;
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use the shop.");
         return true;
      }
   }

   private void C(Player var1) {
      this.K.remove(var1.getUniqueId());
      Inventory var2 = Bukkit.createInventory((InventoryHolder)null, 27, this.N);

      for(Map.Entry var4 : this.E.entrySet()) {
         var2.setItem((Integer)var4.getKey(), ((ItemStack)var4.getValue()).clone());
      }

      var1.openInventory(var2);
   }

   private void A(Player var1, String var2) {
      this.K.remove(var1.getUniqueId());
      this.O.remove(var1.getUniqueId());
      FileConfiguration var3 = (FileConfiguration)this.M.get(var2);
      if (var3 == null) {
         String var10001 = String.valueOf(ChatColor.RED);
         var1.sendMessage(var10001 + "Category file not found: " + var2);
      } else {
         String var4 = this.A(var3.getString("gui-title", "&8ѕʜᴏᴘ"));
         Inventory var5 = Bukkit.createInventory((InventoryHolder)null, 27, var4);
         Map var6 = (Map)this.I.get(var2);
         if (var6 != null) {
            for(Map.Entry var8 : var6.entrySet()) {
               var5.setItem((Integer)var8.getKey(), ((ItemStack)var8.getValue()).clone());
            }
         }

         var5.setItem(18, this.A(Material.RED_STAINED_GLASS_PANE, "&cʙᴀᴄᴋ", "&fClick to return"));
         var1.openInventory(var5);
      }
   }

   private void A(Player var1, _B var2) {
      int var3 = this.A((Inventory)var1.getInventory(), (ItemStack)var2.G);
      if (var3 <= 0) {
         this.B(var1);
      } else {
         String var4 = this.A(var2.G).toUpperCase();
         String var5 = this.B(var4);
         String var6 = this.A("&8ʙᴜʏɪɴɢ " + var5);
         if (var6.length() > 32) {
            var6 = var6.substring(0, 32);
         }

         Inventory var7 = Bukkit.createInventory((InventoryHolder)null, 27, var6);
         int var8 = var2.G.getMaxStackSize();
         ItemStack var9 = var2.G.clone();
         var9.setAmount(Math.min(var2.F, var8));
         ItemMeta var10 = var9.getItemMeta();
         double var11 = var2.C * (double)var2.F;
         String var13 = String.format("%,.0f", var11);
         ArrayList var14 = new ArrayList();
         var14.add(this.A("&fBuy price: &a$" + var13));
         var10.setLore(var14);
         var9.setItemMeta(var10);
         var7.setItem(13, var9);
         var7.setItem(21, this.A(Material.RED_STAINED_GLASS_PANE, "&4ᴄᴀɴᴄᴇʟ", "&fClick to cancel"));
         var7.setItem(23, this.A(Material.LIME_STAINED_GLASS_PANE, "&aᴄᴏɴꜰɪʀᴍ", "&fClick to buy"));
         List var15 = var2.E;
         int[] var16 = new int[]{15, 16, 17};
         int[] var17 = new int[]{11, 10, 9};

         for(int var18 = 0; var18 < var15.size() && var18 < var16.length; ++var18) {
            int var19 = (Integer)var15.get(var18);
            if (var2.F < var8) {
               var7.setItem(var16[var18], this.A(Material.LIME_STAINED_GLASS_PANE, "&aAdd " + var19, ""));
            }

            boolean var20 = var2.F - var19 >= 1;
            boolean var21 = var2.F == var8 && var19 == var8 && var8 > 1;
            if (var20 || var21) {
               var7.setItem(var17[var18], this.A(Material.RED_STAINED_GLASS_PANE, "&cRemove " + var19, ""));
            }
         }

         var1.openInventory(var7);
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent var1) {
      String var2 = var1.getView().getTitle();
      if (var2.equals(this.N) || var2.startsWith(this.L) || var2.startsWith(this.H)) {
         int var3 = var1.getView().getTopInventory().getSize();

         for(int var5 : var1.getRawSlots()) {
            if (var5 < var3) {
               var1.setCancelled(true);
               return;
            }
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = false
   )
   public void onInventoryClick(InventoryClickEvent var1) {
      String var2 = var1.getView().getTitle();
      Player var3 = (Player)var1.getWhoClicked();
      Inventory var4 = var1.getClickedInventory();
      Inventory var5 = var1.getView().getTopInventory();
      if (var4 != null) {
         boolean var6 = var2.equals(this.N) || var2.startsWith(this.L) || var2.startsWith(this.H) || var2.equals(this.F);
         if (var6) {
            if (var1.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
               var1.setCancelled(true);
            } else if (!var4.equals(var5)) {
               if (var1.isShiftClick()) {
                  var1.setCancelled(true);
               } else {
                  var1.setCancelled(false);
               }

            } else {
               var1.setCancelled(true);
               if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
                  if (var1.getCurrentItem().getType() != Material.BLACK_STAINED_GLASS_PANE) {
                     this.A(var3, Sound.BLOCK_TRIPWIRE_CLICK_ON);
                  }

                  int var7 = var1.getSlot();
                  if (var2.equals(this.N)) {
                     if (this.B.containsKey(var7)) {
                        this.A(var3, (String)this.B.get(var7));
                     }

                  } else if (var2.startsWith(this.L)) {
                     if (var7 == 18 && var1.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                        this.C(var3);
                     } else {
                        _A var16 = this.B(var3, var2, var7);
                        if (var16 != null) {
                           this.O.put(var3.getUniqueId(), var16);
                           this.B(var3, var16);
                        } else {
                           _B var17 = this.A(var3, var2, var7);
                           if (var17 != null) {
                              if (this.A((Inventory)var3.getInventory(), (ItemStack)var17.G) <= 0) {
                                 this.B(var3);
                                 return;
                              }

                              this.K.put(var3.getUniqueId(), var17);
                              this.A(var3, var17);
                           }

                        }
                     }
                  } else {
                     if (this.K.containsKey(var3.getUniqueId()) && var2.startsWith(this.H)) {
                        _B var8 = (_B)this.K.get(var3.getUniqueId());
                        int var9 = var8.G.getMaxStackSize();
                        if (var7 == 21) {
                           this.A(var3, var8.A);
                           return;
                        }

                        if (var7 == 23) {
                           this.B(var3, var8);
                           return;
                        }

                        List var10 = var8.E;
                        int[] var11 = new int[]{15, 16, 17};
                        int[] var12 = new int[]{11, 10, 9};

                        for(int var13 = 0; var13 < var11.length; ++var13) {
                           if (var7 == var11[var13] && var13 < var10.size()) {
                              int var14 = (Integer)var10.get(var13);
                              if (var8.F < var9) {
                                 var8.F = Math.min(var8.F + var14, var9);
                                 this.A(var3, var8);
                              }

                              return;
                           }
                        }

                        for(int var18 = 0; var18 < var12.length; ++var18) {
                           if (var7 == var12[var18] && var18 < var10.size()) {
                              int var19 = (Integer)var10.get(var18);
                              if (var8.F - var19 >= 1) {
                                 var8.F -= var19;
                                 this.A(var3, var8);
                              } else if (var8.F == var9 && var19 == var9 && var9 > 1) {
                                 var8.F = 1;
                                 this.A(var3, var8);
                              }

                              return;
                           }
                        }
                     }

                     if (this.O.containsKey(var3.getUniqueId()) && var2.equals(this.F)) {
                        var1.setCancelled(true);
                        _A var15 = (_A)this.O.get(var3.getUniqueId());
                        if (var7 == 11 && var1.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {
                           this.O.remove(var3.getUniqueId());
                           this.A(var3, var15.D);
                           return;
                        }

                        if (var7 == 15 && var1.getCurrentItem().getType() == Material.GREEN_STAINED_GLASS_PANE) {
                           this.C(var3, var15);
                           return;
                        }
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onClose(InventoryCloseEvent var1) {
   }

   private void B(Player var1, _B var2) {
      int var3 = this.A((Inventory)var1.getInventory(), (ItemStack)var2.G);
      if (var3 <= 0) {
         this.B(var1);
      } else {
         int var4 = Math.min(var2.F, var3);
         if (this.J.getServer().getServicesManager().getRegistration(Economy.class) == null) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Shop is currently unavailable (Economy plugin missing). Please contact an admin.");
            this.J.getLogger().warning("Vault Economy provider not found! Please install an economy plugin (Essentials, etc).");
         } else {
            Economy var5 = (Economy)this.J.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            double var6 = var2.C * (double)var4;
            if (!var5.has(var1, var6)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "You do not have enough money!");
               this.A(var1, Sound.ENTITY_VILLAGER_NO);
            } else {
               var5.withdrawPlayer(var1, var6);
               ItemStack var8 = var2.G.clone();
               var8.setAmount(var4);
               if (var2.B != null && !var2.B.isEmpty() && (var8.getType() == Material.ARROW || var8.getType() == Material.TIPPED_ARROW)) {
                  if (var8.getType() == Material.ARROW) {
                     var8.setType(Material.TIPPED_ARROW);
                  }

                  PotionMeta var9 = (PotionMeta)var8.getItemMeta();
                  if (var9 != null) {
                     for(String var11 : var2.B) {
                        try {
                           PotionEffectType var12 = PotionEffectType.getByName(var11);
                           if (var12 != null) {
                              int var13 = var2.D * 20;
                              int var14 = var2.H - 1;
                              PotionEffect var15 = new PotionEffect(var12, var13, var14, false, true, true);
                              var9.addCustomEffect(var15, true);
                           }
                        } catch (Exception var16) {
                        }
                     }

                     var8.setItemMeta(var9);
                  }
               }

               var1.getInventory().addItem(new ItemStack[]{var8});
               this.A(var1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
               PlayerData var17 = this.J.getPlayerDataManager().get(var1.getUniqueId());
               if (var17 != null) {
                  var17.addShopSpent(var6);
                  Bukkit.getScheduler().runTaskAsynchronously(this.J, () -> this.J.getPlayerDataManager().savePlayer(var1.getUniqueId()));
               }

            }
         }
      }
   }

   private void B(Player var1) {
      String var2 = this.A("&cYour inventory is full!");
      var1.sendMessage(var2);
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var2));
      this.A(var1, Sound.ENTITY_VILLAGER_NO);
   }

   private int A(Inventory var1, ItemStack var2) {
      int var3 = 0;
      int var4 = var2.getMaxStackSize();

      for(ItemStack var8 : var1.getStorageContents()) {
         if (var8 != null && var8.getType() != Material.AIR) {
            if (var8.isSimilar(var2)) {
               var3 += var4 - var8.getAmount();
            }
         } else {
            var3 += var4;
         }
      }

      return var3;
   }

   private _B A(Player var1, String var2, int var3) {
      FileConfiguration var4 = (FileConfiguration)this.G.get(var2);
      if (var4 == null) {
         return null;
      } else {
         String var5 = null;

         for(Map.Entry var7 : this.M.entrySet()) {
            if (((FileConfiguration)var7.getValue()).equals(var4)) {
               var5 = (String)var7.getKey();
               break;
            }
         }

         if (var5 == null) {
            return null;
         } else {
            _D var8 = (_D)((Map)this.D.getOrDefault(var5, Collections.emptyMap())).get(var3);
            return var8 == null ? null : new _B(new ItemStack(var8.B), var8.D, var5, var8.A, var8.C, var8.E, var8.F);
         }
      }
   }

   private ItemStack A(ConfigurationSection var1) {
      String var2 = var1.getString("material", "STONE");
      Material var3 = Material.getMaterial(var2.toUpperCase());
      if (var3 == null) {
         var3 = Material.STONE;
      }

      ItemStack var4 = new ItemStack(var3, 1);
      ItemMeta var5 = var4.getItemMeta();
      if (var1.contains("name")) {
         var5.setDisplayName(this.A(var1.getString("name")));
      }

      if (var1.contains("lore")) {
         ArrayList var6 = new ArrayList();

         for(String var8 : var1.getStringList("lore")) {
            var6.add(this.A(var8));
         }

         var5.setLore(var6);
      }

      var4.setItemMeta(var5);
      return var4;
   }

   private ItemStack A(Material var1, String var2, String var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(this.A(var2));
      if (!var3.isEmpty()) {
         var5.setLore(Collections.singletonList(this.A(var3)));
      }

      var4.setItemMeta(var5);
      return var4;
   }

   private String A(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private void A(Player var1, Sound var2) {
      try {
         var1.playSound(var1.getLocation(), var2, 1.0F, 1.0F);
      } catch (Exception var4) {
      }

   }

   private String A(ItemStack var1) {
      return var1.getType().name().toLowerCase().replace("_", " ");
   }

   private String B(String var1) {
      StringBuilder var2 = new StringBuilder();

      for(char var6 : var1.toCharArray()) {
         switch (var6) {
            case ' ':
               var2.append(" ");
               break;
            case '!':
            case '"':
            case '#':
            case '$':
            case '%':
            case '&':
            case '\'':
            case '(':
            case ')':
            case '*':
            case '+':
            case ',':
            case '-':
            case '.':
            case '/':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            default:
               var2.append(var6);
               break;
            case 'A':
               var2.append("ᴀ");
               break;
            case 'B':
               var2.append("ʙ");
               break;
            case 'C':
               var2.append("ᴄ");
               break;
            case 'D':
               var2.append("ᴅ");
               break;
            case 'E':
               var2.append("ᴇ");
               break;
            case 'F':
               var2.append("ꜰ");
               break;
            case 'G':
               var2.append("ɢ");
               break;
            case 'H':
               var2.append("ʜ");
               break;
            case 'I':
               var2.append("ɪ");
               break;
            case 'J':
               var2.append("ᴊ");
               break;
            case 'K':
               var2.append("ᴋ");
               break;
            case 'L':
               var2.append("ʟ");
               break;
            case 'M':
               var2.append("ᴍ");
               break;
            case 'N':
               var2.append("ɴ");
               break;
            case 'O':
               var2.append("ᴏ");
               break;
            case 'P':
               var2.append("ᴘ");
               break;
            case 'Q':
               var2.append("ǫ");
               break;
            case 'R':
               var2.append("ʀ");
               break;
            case 'S':
               var2.append("ѕ");
               break;
            case 'T':
               var2.append("ᴛ");
               break;
            case 'U':
               var2.append("ᴜ");
               break;
            case 'V':
               var2.append("ᴠ");
               break;
            case 'W':
               var2.append("ᴡ");
               break;
            case 'X':
               var2.append("x");
               break;
            case 'Y':
               var2.append("ʏ");
               break;
            case 'Z':
               var2.append("ᴢ");
         }
      }

      return var2.toString();
   }

   private void C(Player var1, _A var2) {
      PlayerData var3 = this.J.getPlayerDataManager().get(var1.getUniqueId());
      if (var3 == null) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Error loading your data!");
         this.A(var1, Sound.ENTITY_VILLAGER_NO);
      } else {
         if (var2.H.equals("MONEY")) {
            Economy var4 = (Economy)this.J.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            if (!var4.has(var1, var2.F)) {
               String var5 = String.valueOf(ChatColor.RED) + "You don't have enough money!";
               var1.sendMessage(var5);
               var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var5));
               this.A(var1, Sound.ENTITY_VILLAGER_NO);
               this.J.getSchedulerAdapter().runTaskLater(() -> {
                  if (this.O.containsKey(var1.getUniqueId())) {
                     this.B(var1, var2);
                  }

               }, 1L);
               return;
            }

            var4.withdrawPlayer(var1, var2.F);
         } else {
            double var7 = var3.getShards();
            if (var7 < var2.F) {
               String var6 = String.valueOf(ChatColor.RED) + "You don't have enough shards!";
               var1.sendMessage(var6);
               var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var6));
               this.A(var1, Sound.ENTITY_VILLAGER_NO);
               this.J.getSchedulerAdapter().runTaskLater(() -> {
                  if (this.O.containsKey(var1.getUniqueId())) {
                     this.B(var1, var2);
                  }

               }, 1L);
               return;
            }

            var3.setShards(var7 - var2.F);
            this.J.getPlayerDataManager().savePlayer(var1.getUniqueId());
         }

         if (var2.B != null && !var2.B.isEmpty()) {
            String var11 = var2.B.replace("{gamertag}", var1.getName());
            this.J.getSchedulerAdapter().runTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var11));
         } else if (var2.E != null) {
            String var8 = this.J.normalizeKeyName(var2.E);
            var3.addKey(var8);
            this.J.getPlayerDataManager().savePlayer(var1.getUniqueId());
         } else {
            if (var2.C == null) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Error: Item type not recognized!");
               if (var2.H.equals("MONEY")) {
                  Economy var10 = (Economy)this.J.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
                  var10.depositPlayer(var1, var2.F);
               } else {
                  var3.setShards(var3.getShards() + var2.F);
                  this.J.getPlayerDataManager().savePlayer(var1.getUniqueId());
               }

               this.A(var1, Sound.ENTITY_VILLAGER_NO);
               return;
            }

            String var10000 = var1.getName();
            String var9 = "spawner give " + var10000 + " " + var2.C + " 1";
            this.J.getSchedulerAdapter().runTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), var9));
         }

         this.A(var1, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
         String var12 = var2.D;
         this.O.remove(var1.getUniqueId());
         this.A(var1, var12);
      }
   }

   private _A B(Player var1, String var2, int var3) {
      FileConfiguration var4 = (FileConfiguration)this.G.get(var2);
      if (var4 == null) {
         return null;
      } else {
         String var5 = null;

         for(Map.Entry var7 : this.M.entrySet()) {
            if (((FileConfiguration)var7.getValue()).equals(var4)) {
               var5 = (String)var7.getKey();
               break;
            }
         }

         if (var5 == null) {
            return null;
         } else {
            _C var8 = (_C)((Map)this.C.getOrDefault(var5, Collections.emptyMap())).get(var3);
            return var8 == null ? null : new _A(var8.H, var8.G, var8.E, var8.F, var8.D, var8.B, var8.A, var8.C, var8.I);
         }
      }
   }

   private void B(Player var1, _A var2) {
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 27, this.A("&8ᴄᴏɴꜰɪʀᴍ ᴘᴜʀᴄʜᴀѕᴇ"));
      ItemStack var4 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(this.A("&4ᴄᴀɴᴄᴇʟ"));
      ArrayList var6 = new ArrayList();
      var6.add(this.A("&fClick to cancel"));
      var5.setLore(var6);
      var4.setItemMeta(var5);
      var3.setItem(11, var4);
      ItemStack var7 = new ItemStack(var2.G);
      ItemMeta var8 = var7.getItemMeta();
      if (!var2.I.isEmpty()) {
         var8.setDisplayName(this.A(var2.I));
      }

      ArrayList var9 = new ArrayList();
      if (var2.H.equals("MONEY")) {
         String var10 = String.format("%,.0f", var2.F);
         var9.add(this.A("&fBuy price: &a$" + var10));
      } else {
         var9.add(this.A("&fBuy price: &5" + (int)var2.F + "x &lShards"));
      }

      var8.setLore(var9);
      var7.setItemMeta(var8);
      var3.setItem(13, var7);
      ItemStack var13 = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
      ItemMeta var11 = var13.getItemMeta();
      var11.setDisplayName(this.A("&aᴄᴏɴꜰɪʀᴍ"));
      ArrayList var12 = new ArrayList();
      var12.add(this.A("&fClick to confirm"));
      var11.setLore(var12);
      var13.setItemMeta(var11);
      var3.setItem(15, var13);
      var1.openInventory(var3);
   }

   private static class _A {
      String A;
      String I;
      double F;
      String H;
      String E;
      String C;
      String B;
      String D;
      Material G;

      public _A(String var1, String var2, double var3, String var5, String var6, String var7, String var8, String var9, Material var10) {
         this.A = var1;
         this.I = var2;
         this.F = var3;
         this.H = var5;
         this.E = var6;
         this.C = var7;
         this.B = var8;
         this.D = var9;
         this.G = var10;
      }
   }

   private static class _B {
      ItemStack G;
      double C;
      int F;
      String A;
      List<Integer> E;
      List<String> B;
      int D;
      int H;

      public _B(ItemStack var1, double var2, String var4, List<Integer> var5, List<String> var6, int var7, int var8) {
         this.G = var1;
         this.C = var2;
         this.A = var4;
         this.E = var5;
         this.B = var6;
         this.D = var7;
         this.H = var8;
         this.F = 1;
      }
   }

   private static class _C {
      final String H;
      final String G;
      final String F;
      final String D;
      final String B;
      final String A;
      final String C;
      final double E;
      final Material I;

      _C(String var1, String var2, double var3, String var5, String var6, String var7, String var8, String var9, Material var10) {
         this.H = var1;
         this.G = var2;
         this.E = var3;
         this.F = var5;
         this.D = var6;
         this.B = var7;
         this.A = var8;
         this.C = var9;
         this.I = var10;
      }
   }

   private static class _D {
      final Material B;
      final double D;
      final List<Integer> A;
      final List<String> C;
      final int E;
      final int F;

      _D(Material var1, double var2, List<Integer> var4, List<String> var5, int var6, int var7) {
         this.B = var1;
         this.D = var2;
         this.A = var4;
         this.C = var5;
         this.E = var6;
         this.F = var7;
      }
   }
}
