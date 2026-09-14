package com.h2ph.J.B.F;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.h2ph.PrismSurvival;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.util.StringUtil;

public class A implements CommandExecutor, TabCompleter, com.h2ph.T.A.C {
   private final PrismSurvival H;
   private FileConfiguration D;
   private final File B;
   private final File G;
   private final Map<UUID, com.h2ph.T.A.C._A> I = new ConcurrentHashMap();
   private final Map<UUID, _C> C = new ConcurrentHashMap();
   private final Map<UUID, _C> E = new ConcurrentHashMap();
   private final Map<String, Set<UUID>> M = new ConcurrentHashMap();
   private final Map<UUID, Set<String>> L = new ConcurrentHashMap();
   private FileConfiguration K;
   private final File J;
   private final File F;

   public A(PrismSurvival var1, com.h2ph.T.A.C var2) {
      this.H = var1;
      this.B = new File(var1.getDataFolder(), "survival/moderations/offend/config.yml");
      this.G = new File(var1.getDataFolder(), "survival/moderations/offend/data/bans.yml");
      this.J = new File(var1.getDataFolder(), "survival/moderations/mute/config.yml");
      this.F = new File(var1.getDataFolder(), "survival/moderations/mute/data.yml");
      this.loadOffendConfig();
      this.G();
      this.F();
      this.D();
      this.A();
      var1.getCommand("offend").setExecutor(this);
      var1.getCommand("offend").setTabCompleter(this);
      var1.getCommand("unban").setExecutor(this);
      var1.getCommand("unban").setTabCompleter(this);
      var1.getCommand("checkban").setExecutor(this);
      var1.getCommand("checkban").setTabCompleter(this);
      var1.getServer().getPluginManager().registerEvents(new com.h2ph.W.A(this), var1);
      var1.getCommand("mute").setExecutor(this);
      var1.getCommand("mute").setTabCompleter(this);
      var1.getCommand("unmute").setExecutor(this);
      var1.getCommand("unmute").setTabCompleter(this);
      var1.getCommand("ipmute").setExecutor(this);
      var1.getCommand("ipmute").setTabCompleter(this);
      var1.getServer().getPluginManager().registerEvents(new _B(), var1);
      var1.getServer().getPluginManager().registerEvents(new _A(), var1);
      var1.getLogger().info("[Mute] Voice chat integration disabled. Voice mute commands will store data but will not block voice packets.");
   }

   public void addBan(UUID var1, String var2, String var3, String var4, String var5, int var6, long var7, long var9, String var11) {
      com.h2ph.T.A.C._A var12 = new com.h2ph.T.A.C._A();
      var12.A = var1.toString();
      var12.E = var2;
      var12.B = var3;
      var12.I = var4;
      var12.G = var5;
      var12.H = var6;
      var12.D = var7;
      var12.C = var9;
      var12.F = var11;
      this.I.put(var1, var12);
      this.B();
   }

   public void removeBan(UUID var1) {
      this.I.remove(var1);
      this.B();
   }

   public void removeBan(String var1) {
      this.I.entrySet().removeIf((var1x) -> ((com.h2ph.T.A.C._A)var1x.getValue()).E.equalsIgnoreCase(var1));
      this.B();
   }

   public void removeBanById(String var1) {
      this.I.entrySet().removeIf((var1x) -> ((com.h2ph.T.A.C._A)var1x.getValue()).B.equals(var1));
      this.B();
   }

   public com.h2ph.T.A.C._A getBanInfo(UUID var1) {
      com.h2ph.T.A.C._A var2 = (com.h2ph.T.A.C._A)this.I.get(var1);
      if (var2 == null || var2.C != -1L && var2.C <= System.currentTimeMillis()) {
         if (var2 != null) {
            this.I.remove(var1);
         }

         return null;
      } else {
         return var2;
      }
   }

   public com.h2ph.T.A.C._A getBanInfoByName(String var1) {
      for(com.h2ph.T.A.C._A var3 : this.I.values()) {
         if (var3.E.equalsIgnoreCase(var1)) {
            if (var3.C == -1L || var3.C > System.currentTimeMillis()) {
               return var3;
            }

            this.I.remove(UUID.fromString(var3.A));
         }
      }

      return null;
   }

   public com.h2ph.T.A.C._A getBanInfoById(String var1) {
      for(com.h2ph.T.A.C._A var3 : this.I.values()) {
         if (var3.B.equals(var1)) {
            if (var3.C == -1L || var3.C > System.currentTimeMillis()) {
               return var3;
            }

            this.I.remove(UUID.fromString(var3.A));
         }
      }

      return null;
   }

   public boolean isBanned(UUID var1) {
      return this.getBanInfo(var1) != null;
   }

   public int getOffenseCount(UUID var1, String var2) {
      com.h2ph.T.A.C._A var3 = (com.h2ph.T.A.C._A)this.I.get(var1);
      return var3 != null && var3.I.equals(var2) ? var3.H : 0;
   }

   public void setOffenseCount(UUID var1, String var2, int var3) {
      com.h2ph.T.A.C._A var4 = (com.h2ph.T.A.C._A)this.I.get(var1);
      if (var4 != null && var4.I.equals(var2)) {
         var4.H = var3;
         this.B();
      }

   }

   public List<String> getBannedPlayerNames() {
      ArrayList var1 = new ArrayList();

      for(com.h2ph.T.A.C._A var3 : this.I.values()) {
         if (var3.C == -1L || var3.C > System.currentTimeMillis()) {
            var1.add(var3.E);
         }
      }

      return var1;
   }

   public List<com.h2ph.T.A.C._A> getAllActiveBans() {
      ArrayList var1 = new ArrayList();

      for(com.h2ph.T.A.C._A var3 : this.I.values()) {
         if (var3.C == -1L || var3.C > System.currentTimeMillis()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   private void G() {
      this.I.clear();
      if (!this.G.exists()) {
         this.G.getParentFile().mkdirs();
         this.B();
      } else {
         YamlConfiguration var1 = YamlConfiguration.loadConfiguration(this.G);
         if (((FileConfiguration)var1).contains("bans")) {
            for(String var3 : ((FileConfiguration)var1).getConfigurationSection("bans").getKeys(false)) {
               try {
                  com.h2ph.T.A.C._A var4 = new com.h2ph.T.A.C._A();
                  var4.A = var3;
                  var4.E = ((FileConfiguration)var1).getString("bans." + var3 + ".player_name");
                  var4.B = ((FileConfiguration)var1).getString("bans." + var3 + ".ban_id");
                  var4.I = ((FileConfiguration)var1).getString("bans." + var3 + ".reason_key");
                  var4.G = ((FileConfiguration)var1).getString("bans." + var3 + ".display_reason");
                  var4.H = ((FileConfiguration)var1).getInt("bans." + var3 + ".offense_count");
                  var4.D = ((FileConfiguration)var1).getLong("bans." + var3 + ".date_banned");
                  var4.C = ((FileConfiguration)var1).getLong("bans." + var3 + ".expiry");
                  var4.F = ((FileConfiguration)var1).getString("bans." + var3 + ".banned_by");
                  this.I.put(UUID.fromString(var3), var4);
               } catch (Exception var5) {
                  this.H.getLogger().warning("Failed to load ban: " + var3);
               }
            }
         }

      }
   }

   private void B() {
      YamlConfiguration var1 = new YamlConfiguration();

      for(Map.Entry var3 : this.I.entrySet()) {
         String var4 = ((UUID)var3.getKey()).toString();
         com.h2ph.T.A.C._A var5 = (com.h2ph.T.A.C._A)var3.getValue();
         ((FileConfiguration)var1).set("bans." + var4 + ".player_name", var5.E);
         ((FileConfiguration)var1).set("bans." + var4 + ".ban_id", var5.B);
         ((FileConfiguration)var1).set("bans." + var4 + ".reason_key", var5.I);
         ((FileConfiguration)var1).set("bans." + var4 + ".display_reason", var5.G);
         ((FileConfiguration)var1).set("bans." + var4 + ".offense_count", var5.H);
         ((FileConfiguration)var1).set("bans." + var4 + ".date_banned", var5.D);
         ((FileConfiguration)var1).set("bans." + var4 + ".expiry", var5.C);
         ((FileConfiguration)var1).set("bans." + var4 + ".banned_by", var5.F);
      }

      try {
         ((FileConfiguration)var1).save(this.G);
      } catch (Exception var6) {
         this.H.getLogger().severe("Failed to save bans.yml");
      }

   }

   public void loadOffendConfig() {
      if (!this.B.exists()) {
         this.B.getParentFile().mkdirs();
         this.H.saveResource("survival/moderations/offend/config.yml", false);
      }

      this.D = YamlConfiguration.loadConfiguration(this.B);
   }

   public FileConfiguration getOffendConfig() {
      return this.D;
   }

   public com.h2ph.T.A.C getOffendDAO() {
      return this;
   }

   public OfflinePlayer resolveOfflinePlayer(String var1) {
      Player var2 = Bukkit.getPlayer(var1);
      if (var2 != null) {
         return var2;
      } else {
         UUID var3 = this.B(var1);
         return var3 != null ? Bukkit.getOfflinePlayer(var3) : null;
      }
   }

   public com.h2ph.T.A.C._A banPlayer(CommandSender var1, OfflinePlayer var2, String var3, String var4, String var5) {
      UUID var6 = var2.getUniqueId();
      String var7 = var3 != null ? var3 : (var2.getName() != null ? var2.getName() : "Unknown");
      int var8 = this.getOffenseCount(var6, var4) + 1;
      String var9 = var5;
      if (var5 == null) {
         var9 = this.D.getString("reasons." + var4 + ".offenses." + var8);
         if (var9 == null) {
            ConfigurationSection var10 = this.D.getConfigurationSection("reasons." + var4 + ".offenses");
            int var11 = 0;
            if (var10 != null) {
               for(String var13 : var10.getKeys(false)) {
                  try {
                     int var14 = Integer.parseInt(var13);
                     if (var14 > var11) {
                        var11 = var14;
                     }
                  } catch (NumberFormatException var16) {
                  }
               }
            }

            var9 = this.D.getString("reasons." + var4 + ".offenses." + var11);
            if (var9 == null) {
               var9 = "30d";
            }
         }
      }

      long var17 = this.A(var9);
      String var18 = this.D.getString("reasons." + var4 + ".display_reason", var4);
      String var19 = String.valueOf((new Random()).nextInt(900) + 100);
      this.addBan(var6, var7, var19, var4, var18, var8, System.currentTimeMillis(), var17, var1.getName());
      if (var2.isOnline()) {
         Player var20 = var2.getPlayer();
         if (var20 != null) {
            String var15 = ChatColor.translateAlternateColorCodes('&', "&cYou have been banned.\n&fReason: " + var18 + "\n&fBan ID: #" + var19);
            var20.kickPlayer(var15);
         }
      }

      com.h2ph.T.A.C._A var21 = new com.h2ph.T.A.C._A();
      var21.A = var6.toString();
      var21.E = var7;
      var21.B = var19;
      var21.I = var4;
      var21.G = var18;
      var21.H = var8;
      var21.D = System.currentTimeMillis();
      var21.C = var17;
      var21.F = var1.getName();
      return var21;
   }

   private long A(String var1) {
      if (var1 != null && !var1.equalsIgnoreCase("perm") && !var1.equalsIgnoreCase("permanent")) {
         int var2;
         try {
            var2 = Integer.parseInt(var1.substring(0, var1.length() - 1));
         } catch (NumberFormatException var6) {
            return -1L;
         }

         char var3 = var1.toLowerCase().charAt(var1.length() - 1);
         long var4;
         switch (var3) {
            case 'd' -> var4 = (long)var2 * 24L * 60L * 60L * 1000L;
            case 'h' -> var4 = (long)var2 * 60L * 60L * 1000L;
            case 'm' -> var4 = (long)var2 * 60L * 1000L;
            case 's' -> var4 = (long)var2 * 1000L;
            case 'y' -> var4 = (long)var2 * 365L * 24L * 60L * 60L * 1000L;
            default -> {
               return -1L;
            }
         }

         return System.currentTimeMillis() + var4;
      } else {
         return -1L;
      }
   }

   private UUID B(String var1) {
      try {
         URL var2 = new URL("https://api.mojang.com/users/profiles/minecraft/" + var1);
         HttpURLConnection var3 = (HttpURLConnection)var2.openConnection();
         var3.setRequestMethod("GET");
         var3.setConnectTimeout(5000);
         var3.setReadTimeout(5000);
         if (var3.getResponseCode() == 200) {
            BufferedReader var4 = new BufferedReader(new InputStreamReader(var3.getInputStream()));
            StringBuilder var5 = new StringBuilder();

            String var6;
            while((var6 = var4.readLine()) != null) {
               var5.append(var6);
            }

            var4.close();
            JsonObject var7 = JsonParser.parseString(var5.toString()).getAsJsonObject();
            String var8 = var7.get("id").getAsString();
            String var9 = var8.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
            return UUID.fromString(var9);
         }
      } catch (Exception var10) {
      }

      return null;
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      switch (var2.getName().toLowerCase()) {
         case "mute":
            return this.A(var1, var4);
         case "unmute":
            return this.C(var1, var4);
         case "ipmute":
            return this.F(var1, var4);
         default:
            if ((var2.getName().equalsIgnoreCase("unban") || var2.getName().equalsIgnoreCase("checkban")) && var4.length == 1) {
               List var10 = this.getBannedPlayerNames();
               return (List)StringUtil.copyPartialMatches(var4[0], var10, new ArrayList());
            } else {
               if (var2.getName().equalsIgnoreCase("offend")) {
                  if (var4.length == 1) {
                     ArrayList var9 = new ArrayList();

                     for(Player var7 : Bukkit.getOnlinePlayers()) {
                        var9.add(var7.getName());
                     }

                     return (List)StringUtil.copyPartialMatches(var4[0], var9, new ArrayList());
                  }

                  if (var4.length == 2) {
                     ArrayList var8 = new ArrayList(this.D.getConfigurationSection("reasons").getKeys(false));
                     return (List)StringUtil.copyPartialMatches(var4[1], var8, new ArrayList());
                  }
               }

               return Collections.emptyList();
            }
      }
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      switch (var2.getName().toLowerCase()) {
         case "checkban" -> {
            return this.E(var1, var4);
         }
         case "unban" -> {
            return this.G(var1, var4);
         }
         case "offend" -> {
            return this.B(var1, var4);
         }
         case "mute" -> {
            return this.B(var1, var4, var3);
         }
         case "unmute" -> {
            return this.D(var1, var4);
         }
         case "ipmute" -> {
            return this.A(var1, var4, var3);
         }
         default -> {
            return false;
         }
      }
   }

   private boolean E(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("donut.admin.checkban")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "No permission.");
         return true;
      } else if (var2.length < 1) {
         return true;
      } else {
         String var3 = var2[0];
         this.H.getSchedulerAdapter().runTaskAsynchronously(() -> {
            UUID var3x = null;
            String var4 = null;
            Player var5 = Bukkit.getPlayer(var3);
            if (var5 != null) {
               var3x = var5.getUniqueId();
               var4 = var5.getName();
            } else {
               OfflinePlayer var6 = Bukkit.getOfflinePlayer(var3);
               if (var6.hasPlayedBefore() || var6.getName() != null) {
                  var3x = var6.getUniqueId();
                  var4 = var6.getName();
               }
            }

            com.h2ph.T.A.C._A var9 = var3x != null ? this.getBanInfo(var3x) : this.getBanInfoByName(var3);
            String var8 = var4 != null ? var4 : var3;
            this.H.getSchedulerAdapter().runTask(() -> {
               if (var9 == null || var9.C != -1L && var9.C <= System.currentTimeMillis()) {
                  String var10001 = String.valueOf(ChatColor.RED);
                  var1.sendMessage(var10001 + "No active ban found for " + var8);
               } else {
                  SimpleDateFormat var4 = new SimpleDateFormat("yyyy-MM-dd");
                  String var5 = var4.format(new Date(var9.D));
                  String var6 = this.A(var9.H);
                  String var7 = this.B(var9.C);
                  String var8x = this.D.getString("messages.ban_layout");
                  String var9x = var8x.replace("%banned_on%", var5).replace("%reason%", var9.G).replace("%count%", String.valueOf(var9.H)).replace("%ordinal%", var6).replace("%id%", var9.B).replace("%time_left%", var7);
                  var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var9x));
               }

            });
         });
         return true;
      }
   }

   private boolean G(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("donut.admin.unban")) {
         return true;
      } else if (var2.length < 1) {
         return true;
      } else {
         String var3 = var2[0];
         this.H.getSchedulerAdapter().runTaskAsynchronously(() -> {
            UUID var3x = null;
            Player var4 = Bukkit.getPlayer(var3);
            if (var4 != null) {
               var3x = var4.getUniqueId();
            } else {
               OfflinePlayer var5 = Bukkit.getOfflinePlayer(var3);
               if (var5.hasPlayedBefore()) {
                  var3x = var5.getUniqueId();
               }
            }

            com.h2ph.T.A.C._A var6 = var3x != null ? this.getBanInfo(var3x) : this.getBanInfoByName(var3);
            if (var6 != null) {
               this.removeBan(UUID.fromString(var6.A));
               this.H.getSchedulerAdapter().runTask(() -> {
                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var1.sendMessage(var10001 + "Unbanned " + var6.E);
               });
            } else {
               this.H.getSchedulerAdapter().runTask(() -> {
                  String var10001 = String.valueOf(ChatColor.RED);
                  var1.sendMessage(var10001 + "No active ban found for " + var3);
               });
            }

         });
         return true;
      }
   }

   private boolean B(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("donut.admin.offend")) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "No permission.");
         return true;
      } else if (var2.length < 2) {
         return true;
      } else {
         String var3 = var2[0];
         String var4 = var2[1];
         String var5 = var2.length > 2 ? var2[2] : null;
         Player var6 = Bukkit.getPlayer(var3);
         if (var6 == null) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Player must be online to ban.");
            return true;
         } else {
            UUID var7 = var6.getUniqueId();
            if (this.isBanned(var7)) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Player is already banned.");
               return true;
            } else {
               int var8 = this.getOffenseCount(var7, var4) + 1;
               String var9 = var5;
               if (var5 == null) {
                  var9 = this.D.getString("reasons." + var4 + ".offenses." + var8);
                  if (var9 == null) {
                     ConfigurationSection var10 = this.D.getConfigurationSection("reasons." + var4 + ".offenses");
                     int var11 = 0;
                     if (var10 != null) {
                        for(String var13 : var10.getKeys(false)) {
                           try {
                              int var14 = Integer.parseInt(var13);
                              if (var14 > var11) {
                                 var11 = var14;
                              }
                           } catch (NumberFormatException var18) {
                           }
                        }
                     }

                     var9 = this.D.getString("reasons." + var4 + ".offenses." + var11);
                     if (var9 == null) {
                        var9 = "30d";
                     }
                  }
               }

               long var19 = this.A(var9);
               String var20 = this.D.getString("reasons." + var4 + ".display_reason", var4);
               boolean var21 = this.D.getBoolean("reasons." + var4 + ".delete_data", false);
               String var22 = String.valueOf((new Random()).nextInt(900) + 100);
               if (var21) {
                  this.A(var6);
               }

               this.addBan(var7, var6.getName(), var22, var4, var20, var8, System.currentTimeMillis(), var19, var1.getName());
               String var15 = ChatColor.translateAlternateColorCodes('&', "&cYou have been banned.\n&fReason: " + var20 + "\n&fBan ID: #" + var22);
               var6.kickPlayer(var15);
               String var16 = this.D.getString("messages.admin_line_1", "&cPunishment executed. Offense: %count%").replace("%count%", String.valueOf(var8));
               String var17 = this.D.getString("messages.admin_line_2", "&cBanned %player% for %duration% reason: %reason%").replace("%player%", var6.getName()).replace("%duration%", var9).replace("%reason%", var20);
               var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var16));
               var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var17));
               return true;
            }
         }
      }
   }

   private void A(Player var1) {
      var1.getInventory().clear();
      var1.getEnderChest().clear();
      var1.setExp(0.0F);
      var1.setLevel(0);
      var1.setHealth((double)0.0F);
   }

   private String A(int var1) {
      if (var1 % 10 == 1 && var1 % 100 != 11) {
         return "st";
      } else if (var1 % 10 == 2 && var1 % 100 != 11) {
         return "nd";
      } else {
         return var1 % 10 == 3 && var1 % 100 != 11 ? "rd" : "th";
      }
   }

   private String B(long var1) {
      if (var1 == -1L) {
         return "Permanent";
      } else {
         long var3 = var1 - System.currentTimeMillis();
         if (var3 <= 0L) {
            return "Expired";
         } else {
            long var5 = var3 / 1000L;
            long var7 = var5 / 60L;
            long var9 = var7 / 60L;
            long var11 = var9 / 24L;
            if (var11 > 0L) {
               return var11 + " day" + (var11 != 1L ? "s" : "");
            } else if (var9 > 0L) {
               return var9 + " hour" + (var9 != 1L ? "s" : "");
            } else {
               return var7 > 0L ? var7 + " minute" + (var7 != 1L ? "s" : "") : var5 + " second" + (var5 != 1L ? "s" : "");
            }
         }
      }
   }

   private void F() {
      if (!this.J.exists()) {
         this.J.getParentFile().mkdirs();
         this.K = new YamlConfiguration();
         this.K.set("blocked-commands", Arrays.asList("/msg", "/tell", "/w", "/r", "/reply", "/whisper", "/pm", "/me"));
         this.K.set("messages.muted-chat", "&7You are muted for &f%duration%&7. Reason:&c %reason%");
         this.K.set("messages.admin-mute", "&7You muted &d%player%&7's %type% for &f%duration%&7. Reason:&c %reason%");
         this.K.set("messages.admin-unmute", "&d%player%&7 has been %type%unmuted.");
         this.K.set("messages.target-unmute", "&7You have been %type%unmuted.");
         this.K.set("messages.already-muted", "&cThat player is already %type% muted.");
         this.K.set("messages.not-muted", "&c%player% is not %type% muted.");
         this.K.set("messages.voice-muted-action", "&cYou are voice muted and cannot speak!");

         try {
            this.K.save(this.J);
         } catch (Exception var2) {
            this.H.getLogger().warning("[Mute] Failed to save mute/config.yml");
         }
      }

      this.K = YamlConfiguration.loadConfiguration(this.J);
   }

   public FileConfiguration getMuteConfig() {
      return this.K;
   }

   private void D() {
      this.C.clear();
      this.E.clear();
      if (!this.F.exists()) {
         this.F.getParentFile().mkdirs();
      } else {
         YamlConfiguration var1 = YamlConfiguration.loadConfiguration(this.F);

         for(String var5 : new String[]{"chat", "voice"}) {
            if (((FileConfiguration)var1).contains(var5)) {
               for(String var7 : ((FileConfiguration)var1).getConfigurationSection(var5).getKeys(false)) {
                  try {
                     UUID var8 = UUID.fromString(var7);
                     long var9 = ((FileConfiguration)var1).getLong(var5 + "." + var7 + ".expiry", 0L);
                     String var11 = ((FileConfiguration)var1).getString(var5 + "." + var7 + ".reason", "No reason");
                     boolean var12 = ((FileConfiguration)var1).getBoolean(var5 + "." + var7 + ".isIpMute", false);
                     _C var13 = new _C(var9, var11, var12);
                     if (var5.equals("chat")) {
                        this.C.put(var8, var13);
                     } else {
                        this.E.put(var8, var13);
                     }
                  } catch (Exception var14) {
                     this.H.getLogger().warning("[Mute] Bad entry: " + var7);
                  }
               }
            }
         }

      }
   }

   private void A() {
      File var1 = new File(this.H.getDataFolder(), "survival/moderations/mute/ips.yml");
      if (var1.exists()) {
         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
         if (((FileConfiguration)var2).contains("players")) {
            for(String var4 : ((FileConfiguration)var2).getConfigurationSection("players").getKeys(false)) {
               try {
                  UUID var5 = UUID.fromString(var4);
                  List var6 = ((FileConfiguration)var2).getStringList("players." + var4);
                  ConcurrentHashMap.KeySetView var7 = ConcurrentHashMap.newKeySet();
                  var7.addAll(var6);
                  this.L.put(var5, var7);

                  for(String var9 : var6) {
                     ((Set)this.M.computeIfAbsent(var9, (var0) -> ConcurrentHashMap.newKeySet())).add(var5);
                  }
               } catch (Exception var10) {
               }
            }

         }
      }
   }

   private void E() {
      YamlConfiguration var1 = new YamlConfiguration();

      for(Map.Entry var3 : this.C.entrySet()) {
         String var4 = ((UUID)var3.getKey()).toString();
         ((FileConfiguration)var1).set("chat." + var4 + ".expiry", ((_C)var3.getValue()).A);
         ((FileConfiguration)var1).set("chat." + var4 + ".reason", ((_C)var3.getValue()).C);
         ((FileConfiguration)var1).set("chat." + var4 + ".isIpMute", ((_C)var3.getValue()).B);
      }

      for(Map.Entry var7 : this.E.entrySet()) {
         String var8 = ((UUID)var7.getKey()).toString();
         ((FileConfiguration)var1).set("voice." + var8 + ".expiry", ((_C)var7.getValue()).A);
         ((FileConfiguration)var1).set("voice." + var8 + ".reason", ((_C)var7.getValue()).C);
         ((FileConfiguration)var1).set("voice." + var8 + ".isIpMute", ((_C)var7.getValue()).B);
      }

      try {
         ((FileConfiguration)var1).save(this.F);
      } catch (Exception var5) {
         this.H.getLogger().warning("[Mute] Failed to save mute/data.yml");
      }

   }

   private void C() {
      File var1 = new File(this.H.getDataFolder(), "survival/moderations/mute/ips.yml");
      var1.getParentFile().mkdirs();
      YamlConfiguration var2 = new YamlConfiguration();

      for(Map.Entry var4 : this.L.entrySet()) {
         ((FileConfiguration)var2).set("players." + String.valueOf(var4.getKey()), new ArrayList((Collection)var4.getValue()));
      }

      try {
         ((FileConfiguration)var2).save(var1);
      } catch (Exception var5) {
         this.H.getLogger().warning("[Mute] Failed to save mute/ips.yml");
      }

   }

   public _C getChatMuteInfo(UUID var1) {
      _C var2 = (_C)this.C.get(var1);
      if (var2 == null) {
         return null;
      } else if (var2.A != 0L && System.currentTimeMillis() > var2.A) {
         this.C.remove(var1);
         this.E();
         return null;
      } else {
         return var2;
      }
   }

   public boolean isChatMuted(UUID var1) {
      return this.getChatMuteInfo(var1) != null;
   }

   public boolean isVoiceMuted(UUID var1) {
      _C var2 = (_C)this.E.get(var1);
      if (var2 == null) {
         return false;
      } else if (var2.A > 0L && System.currentTimeMillis() > var2.A) {
         this.E.remove(var1);
         this.E();
         return false;
      } else {
         return true;
      }
   }

   public void setChatMuted(UUID var1, boolean var2, long var3, String var5, boolean var6) {
      if (var2) {
         this.C.put(var1, new _C(var3, var5, var6));
      } else {
         this.C.remove(var1);
      }

      this.E();
   }

   public void setVoiceMuted(UUID var1, boolean var2, long var3, String var5, boolean var6) {
      if (var2) {
         this.E.put(var1, new _C(var3, var5, var6));
      } else {
         this.E.remove(var1);
      }

      this.E();
   }

   private String D(String var1) {
      return this.K.getString("messages." + var1, "");
   }

   private String C(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private long F(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         try {
            String var2 = var1.replaceAll("[^0-9]", "");
            if (var2.isEmpty()) {
               return 0L;
            }

            long var3 = Long.parseLong(var2);
            String var5 = var1.replaceAll("[0-9]", "").toLowerCase();
            if (var5.equals("s")) {
               return var3 * 1000L;
            }

            if (var5.equals("m")) {
               return var3 * 60000L;
            }

            if (var5.equals("h")) {
               return var3 * 3600000L;
            }

            if (var5.equals("d")) {
               return var3 * 86400000L;
            }

            if (var5.equals("y")) {
               return var3 * 31536000000L;
            }
         } catch (Exception var6) {
         }

         return 0L;
      } else {
         return 0L;
      }
   }

   private String A(long var1) {
      if (var1 <= 0L) {
         return "Expired";
      } else {
         long var3 = var1 / 86400L;
         long var5 = var1 % 86400L / 3600L;
         long var7 = var1 % 3600L / 60L;
         long var9 = var1 % 60L;
         StringBuilder var11 = new StringBuilder();
         if (var3 > 0L) {
            var11.append(var3).append("d ");
         }

         if (var5 > 0L) {
            var11.append(var5).append("h ");
         }

         if (var7 > 0L) {
            var11.append(var7).append("m ");
         }

         if (var9 > 0L || var11.length() == 0) {
            var11.append(var9).append("s");
         }

         return var11.toString().trim();
      }
   }

   private void A(Runnable var1) {
      this.H.getSchedulerAdapter().runTaskAsynchronously(var1);
   }

   private Set<UUID> A(boolean var1) {
      HashSet var2 = new HashSet();

      for(UUID var4 : this.C.keySet()) {
         if (this.isChatMuted(var4) && (!var1 || ((_C)this.C.get(var4)).B)) {
            var2.add(var4);
         }
      }

      for(UUID var6 : this.E.keySet()) {
         if (this.isVoiceMuted(var6) && (!var1 || ((_C)this.E.get(var6)).B)) {
            var2.add(var6);
         }
      }

      return var2;
   }

   private void A(CommandSender var1, int var2, String var3, boolean var4) {
      this.A((Runnable)(() -> {
         Set var4x = this.A(var4);
         ArrayList var5 = new ArrayList();

         for(UUID var7 : var4x) {
            String var8 = Bukkit.getOfflinePlayer(var7).getName();
            var5.add(var8 != null ? var8 : var7.toString());
         }

         Collections.sort(var5);
         byte var13 = 10;
         int var14 = var5.size();
         int var15 = Math.max(1, (int)Math.ceil((double)var14 / (double)var13));
         int var9 = Math.max(1, Math.min(var2, var15));
         int var10 = (var9 - 1) * var13;
         int var11 = Math.min(var10 + var13, var14);
         var1.sendMessage(String.valueOf(ChatColor.GRAY) + var9 + " - [" + var14 + "] ========================");

         for(int var12 = var10; var12 < var11; ++var12) {
            String var10001 = String.valueOf(ChatColor.WHITE);
            var1.sendMessage(var10001 + (var12 + 1) + ". " + String.valueOf(ChatColor.LIGHT_PURPLE) + (String)var5.get(var12));
         }

         var1.sendMessage(String.valueOf(ChatColor.GRAY) + "==========================");
      }));
   }

   private boolean B(CommandSender var1, String[] var2, String var3) {
      if (!var1.hasPermission("economysmpcore.mute")) {
         this.A(var1);
         return true;
      } else if (var2.length < 1) {
         this.A(var1);
         return true;
      } else if (var2[0].equalsIgnoreCase("list")) {
         int var13 = var2.length >= 2 ? this.A(var2[1], 1) : 1;
         this.A(var1, var13, var3, false);
         return true;
      } else if (!var2[0].equalsIgnoreCase("chat") && !var2[0].equalsIgnoreCase("voice")) {
         this.A(var1);
         return true;
      } else if (var2.length < 3) {
         this.A(var1);
         return true;
      } else {
         String var4 = var2[0].toLowerCase();
         String var5 = var2[1];
         String var6 = var2[2];
         String var7 = var2.length >= 4 ? String.join(" ", (CharSequence[])Arrays.copyOfRange(var2, 3, var2.length)) : "No Reason Provided";
         long var8 = this.F(var6);
         if (var8 <= 0L) {
            this.A(var1);
            return true;
         } else {
            long var10 = System.currentTimeMillis() + var8;
            String var12 = var4.equals("voice") ? "voicemute" : "chatmute";
            if (!var1.hasPermission("economysmpcore." + var12)) {
               this.A(var1);
               return true;
            } else {
               this.A((Runnable)(() -> {
                  UUID var8 = this.A(var1, var5);
                  if (var8 != null) {
                     String var9 = this.A(var8, var5);
                     if (var4.equals("voice")) {
                        if (this.isVoiceMuted(var8)) {
                           var1.sendMessage(this.C(this.D("already-muted").replace("%type%", "voice ")));
                           return;
                        }

                        this.setVoiceMuted(var8, true, var10, var7, false);
                     } else {
                        if (this.isChatMuted(var8)) {
                           var1.sendMessage(this.C(this.D("already-muted").replace("%type%", "chat ")));
                           return;
                        }

                        this.setChatMuted(var8, true, var10, var7, false);
                     }

                     String var10x = this.C(this.D("admin-mute").replace("%player%", var9).replace("%type%", var4).replace("%duration%", var6).replace("%reason%", var7));
                     var1.sendMessage(var10x);
                     this.B(var1, var10x);
                     Player var11 = Bukkit.getPlayer(var8);
                     if (var11 != null) {
                        String var12 = this.C(this.D("target-mute").isEmpty() ? "&7Your " + var4 + " has been muted for &f" + var6 + "&7. Reason:&c " + var7 : this.D("target-mute").replace("%type%", var4).replace("%duration%", var6).replace("%reason%", var7));
                        var11.sendMessage(var12);
                        this.B((CommandSender)var11, (String)var12);
                        var11.playSound(var11.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
                     }

                  }
               }));
               return true;
            }
         }
      }
   }

   private boolean D(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("economysmpcore.unmute")) {
         this.A(var1);
         return true;
      } else if (var2.length < 2) {
         this.A(var1);
         return true;
      } else {
         String var3 = var2[0].toLowerCase();
         if (!var3.equals("chat") && !var3.equals("voice")) {
            this.A(var1);
            return true;
         } else {
            String var4 = var3.equals("voice") ? "voiceunmute" : "chatunmute";
            if (!var1.hasPermission("economysmpcore." + var4)) {
               this.A(var1);
               return true;
            } else {
               String var5 = var2[1];
               this.A((Runnable)(() -> {
                  UUID var4 = this.A(var1, var5);
                  if (var4 != null) {
                     String var5x = this.A(var4, var5);
                     String var6 = var3.equals("voice") ? "voice " : "";
                     if (var3.equals("voice")) {
                        if (!this.isVoiceMuted(var4)) {
                           var1.sendMessage(this.C(this.D("not-muted").replace("%player%", var5x).replace("%type%", "voice ")));
                           return;
                        }

                        this.setVoiceMuted(var4, false, 0L, (String)null, false);
                     } else {
                        if (!this.isChatMuted(var4)) {
                           var1.sendMessage(this.C(this.D("not-muted").replace("%player%", var5x).replace("%type%", "chat ")));
                           return;
                        }

                        this.setChatMuted(var4, false, 0L, (String)null, false);
                     }

                     String var7 = this.C(this.D("admin-unmute").replace("%player%", var5x).replace("%type%", var6));
                     var1.sendMessage(var7);
                     this.B(var1, var7);
                     Player var8 = Bukkit.getPlayer(var4);
                     if (var8 != null) {
                        String var9 = this.C(this.D("target-unmute").replace("%type%", var6));
                        var8.sendMessage(var9);
                        this.B((CommandSender)var8, (String)var9);
                     }

                  }
               }));
               return true;
            }
         }
      }
   }

   private boolean A(CommandSender var1, String[] var2, String var3) {
      if (!var1.hasPermission("economysmpcore.ipmute")) {
         this.A(var1);
         return true;
      } else if (var2.length < 1) {
         this.A(var1);
         return true;
      } else if (var2[0].equalsIgnoreCase("list")) {
         int var16 = var2.length >= 2 ? this.A(var2[1], 1) : 1;
         this.A(var1, var16, var3, true);
         return true;
      } else {
         String var4 = var2[0].toLowerCase();
         if (!var4.equals("chat") && !var4.equals("voice") && !var4.equals("remove")) {
            this.A(var1);
            return true;
         } else if (!var4.equals("remove") && var2.length < 3) {
            this.A(var1);
            return true;
         } else if (var2.length < 2) {
            this.A(var1);
            return true;
         } else {
            String var5 = var2[1];
            Pattern var6 = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}$");
            boolean var7 = var6.matcher(var5).matches();
            long var8 = 0L;
            String var10 = "No Reason Provided";
            long var11 = 0L;
            if (!var4.equals("remove")) {
               var8 = this.F(var2[2]);
               if (var8 <= 0L) {
                  this.A(var1);
                  return true;
               }

               var10 = var2.length >= 4 ? String.join(" ", (CharSequence[])Arrays.copyOfRange(var2, 3, var2.length)) : var10;
               var11 = System.currentTimeMillis() + var8;
            }

            this.A((Runnable)(() -> {
               HashSet var8 = new HashSet();
               if (var7) {
                  var8.addAll((Collection)this.M.getOrDefault(var5, Collections.emptySet()));
               } else {
                  OfflinePlayer var9 = Bukkit.getOfflinePlayer(var5);
                  if (!var9.hasPlayedBefore() && !var9.isOnline()) {
                     var1.sendMessage(String.valueOf(ChatColor.RED) + "Player not found.");
                     return;
                  }

                  Set var10x = (Set)this.L.getOrDefault(var9.getUniqueId(), Collections.emptySet());
                  if (var10x.isEmpty()) {
                     var1.sendMessage(String.valueOf(ChatColor.RED) + "No IP found for that player.");
                     return;
                  }

                  for(String var12 : var10x) {
                     var8.addAll((Collection)this.M.getOrDefault(var12, Collections.emptySet()));
                  }
               }

               if (var8.isEmpty()) {
                  var1.sendMessage(String.valueOf(ChatColor.RED) + "No accounts found.");
               } else {
                  ArrayList var15 = new ArrayList();

                  for(UUID var17 : var8) {
                     String var18 = Bukkit.getOfflinePlayer(var17).getName();
                     var15.add(var18 != null ? var18 : var17.toString());
                     if (var4.equals("remove")) {
                        this.setChatMuted(var17, false, 0L, (String)null, false);
                        this.setVoiceMuted(var17, false, 0L, (String)null, false);
                     } else if (var4.equals("chat")) {
                        this.setChatMuted(var17, true, var11, var10, true);
                     } else {
                        this.setVoiceMuted(var17, true, var11, var10, true);
                     }

                     Player var13 = Bukkit.getPlayer(var17);
                     if (var13 != null) {
                        if (var4.equals("remove")) {
                           var13.sendMessage(this.C("&7You have been unmuted by an admin."));
                        } else {
                           String var14 = this.C("&7Your " + var4 + " has been muted. Reason:&c " + var10);
                           var13.sendMessage(var14);
                           this.B((CommandSender)var13, (String)var14);
                           var13.playSound(var13.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 1.0F);
                        }
                     }
                  }

                  String var10001 = String.valueOf(ChatColor.GREEN);
                  var1.sendMessage(var10001 + (var4.equals("remove") ? "Removed" : "Applied") + " IP mute for " + var5 + ". Affected: " + String.join(", ", var15));
               }
            }));
            return true;
         }
      }
   }

   private List<String> A(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("economysmpcore.mute")) {
         return Collections.emptyList();
      } else if (var2.length == 1) {
         return this.A(var2[0], Arrays.asList("chat", "voice", "list"));
      } else if (var2.length == 2 && !var2[0].equalsIgnoreCase("list")) {
         return this.G(var2[1]);
      } else {
         return var2.length == 3 ? this.A(var2[2], Arrays.asList("10s", "1m", "1h", "1d", "1y")) : Collections.emptyList();
      }
   }

   private List<String> C(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("economysmpcore.unmute")) {
         return Collections.emptyList();
      } else if (var2.length == 1) {
         return this.A(var2[0], Arrays.asList("chat", "voice"));
      } else {
         return var2.length == 2 ? this.G(var2[1]) : Collections.emptyList();
      }
   }

   private List<String> F(CommandSender var1, String[] var2) {
      if (!var1.hasPermission("economysmpcore.ipmute")) {
         return Collections.emptyList();
      } else if (var2.length == 1) {
         return this.A(var2[0], Arrays.asList("chat", "voice", "remove", "list"));
      } else if (var2.length == 2 && !var2[0].equalsIgnoreCase("list")) {
         return this.G(var2[1]);
      } else {
         return var2.length == 3 && !var2[0].equalsIgnoreCase("remove") ? this.A(var2[2], Arrays.asList("10s", "1m", "1h", "1d", "1y")) : Collections.emptyList();
      }
   }

   private List<String> A(String var1, List<String> var2) {
      return (List)var2.stream().filter((var1x) -> var1x.toLowerCase().startsWith(var1.toLowerCase())).collect(Collectors.toList());
   }

   private List<String> G(String var1) {
      ArrayList var2 = new ArrayList();

      for(Player var4 : Bukkit.getOnlinePlayers()) {
         if (var4.getName().toLowerCase().startsWith(var1.toLowerCase())) {
            var2.add(var4.getName());
         }
      }

      return var2;
   }

   private void A(CommandSender var1) {
      if (var1 instanceof Player var2) {
         var2.playSound(var2.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      }

   }

   private void B(CommandSender var1, String var2) {
      if (var1 instanceof Player var3) {
         var3.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(var2));
      }

   }

   private UUID A(CommandSender var1, String var2) {
      Player var3 = Bukkit.getPlayer(var2);
      if (var3 != null) {
         return var3.getUniqueId();
      } else {
         OfflinePlayer var4 = Bukkit.getOfflinePlayer(var2);
         if (!var4.hasPlayedBefore() && !var4.isOnline()) {
            String var10001 = String.valueOf(ChatColor.RED);
            var1.sendMessage(var10001 + "Player not found: " + var2);
            return null;
         } else {
            return var4.getUniqueId();
         }
      }
   }

   private String A(UUID var1, String var2) {
      String var3 = Bukkit.getOfflinePlayer(var1).getName();
      return var3 != null ? var3 : var2;
   }

   private int A(String var1, int var2) {
      try {
         return Integer.parseInt(var1);
      } catch (Exception var4) {
         return var2;
      }
   }

   private class _A implements Listener {
      @EventHandler(
         priority = EventPriority.MONITOR
      )
      public void onJoin(PlayerJoinEvent var1) {
         if (var1.getPlayer().getAddress() != null) {
            UUID var2 = var1.getPlayer().getUniqueId();
            String var3 = var1.getPlayer().getAddress().getAddress().getHostAddress();
            boolean var4 = false;
            if (((Set)A.this.L.computeIfAbsent(var2, (var0) -> ConcurrentHashMap.newKeySet())).add(var3)) {
               var4 = true;
            }

            if (((Set)A.this.M.computeIfAbsent(var3, (var0) -> ConcurrentHashMap.newKeySet())).add(var2)) {
               var4 = true;
            }

            if (var4) {
               A.this.A(A.this::C);
            }

         }
      }
   }

   private class _B implements Listener {
      private final List<String> A;

      private _B() {
         this.A = A.this.K.getStringList("blocked-commands");
      }

      @EventHandler(
         priority = EventPriority.HIGHEST,
         ignoreCancelled = true
      )
      public void onChat(AsyncPlayerChatEvent var1) {
         if (A.this.isChatMuted(var1.getPlayer().getUniqueId())) {
            var1.setCancelled(true);
            this.A(var1.getPlayer());
         }

      }

      @EventHandler(
         priority = EventPriority.HIGHEST,
         ignoreCancelled = true
      )
      public void onCmd(PlayerCommandPreprocessEvent var1) {
         if (A.this.isChatMuted(var1.getPlayer().getUniqueId())) {
            String var2 = var1.getMessage().toLowerCase();

            for(String var4 : this.A) {
               if (var2.startsWith(var4 + " ") || var2.equals(var4)) {
                  var1.setCancelled(true);
                  this.A(var1.getPlayer());
                  return;
               }
            }

         }
      }

      private void A(Player var1) {
         _C var2 = A.this.getChatMuteInfo(var1.getUniqueId());
         if (var2 != null) {
            long var3 = var2.A > 0L ? Math.max(0L, (var2.A - System.currentTimeMillis()) / 1000L) : -1L;
            String var5 = var3 < 0L ? "Permanent" : A.this.A(var3);
            String var6 = A.this.C(A.this.D("muted-chat").replace("%duration%", var5).replace("%reason%", var2.C != null ? var2.C : "No reason"));
            var1.sendMessage(var6);
            A.this.B((CommandSender)var1, (String)var6);
         }
      }
   }

   public static class _C {
      public final long A;
      public final String C;
      public final boolean B;

      public _C(long var1, String var3, boolean var4) {
         this.A = var1;
         this.C = var3;
         this.B = var4;
      }
   }
}
