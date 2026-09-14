package com.prismcore.survival.orders;

import com.prismcore.survival.orders.util.TaskUtil;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class Utils {
   private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
   public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

   private Utils() {
   }

   public static void sendActionBar(Player var0, String var1) {
      if (var0 != null && var1 != null) {
         var0.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(formatColors(var1)));
      }
   }

   public static String formatColors(String var0) {
      if (var0 == null) {
         return null;
      } else {
         Matcher var1 = HEX_PATTERN.matcher(var0);
         StringBuffer var2 = new StringBuffer(var0.length() + 32);

         while(var1.find()) {
            String var3 = var1.group(1);
            StringBuilder var4 = new StringBuilder("§x");

            for(char var8 : var3.toCharArray()) {
               var4.append('§').append(var8);
            }

            var1.appendReplacement(var2, Matcher.quoteReplacement(var4.toString()));
         }

         var1.appendTail(var2);
         return ChatColor.translateAlternateColorCodes('&', var2.toString());
      }
   }

   public static List<String> formatColors(List<String> var0) {
      return (List)var0.stream().map(Utils::formatColors).collect(Collectors.toList());
   }

   public static String applyPlaceholders(String var0, Map<String, String> var1) {
      if (var0 == null) {
         return null;
      } else {
         String var2 = var0;

         for(Map.Entry var4 : var1.entrySet()) {
            var2 = var2.replace("{" + (String)var4.getKey() + "}", String.valueOf(var4.getValue()));
         }

         return formatColors(var2);
      }
   }

   public static List<String> applyPlaceholders(List<String> var0, Map<String, String> var1) {
      return (List)var0.stream().map((var1x) -> applyPlaceholders(var1x, var1)).collect(Collectors.toList());
   }

   public static String abbr(double var0) {
      boolean var3 = var0 < (double)0.0F;
      double var4 = Math.abs(var0);
      String[] var6 = new String[]{"", "K", "M", "B", "T"};

      int var2;
      for(var2 = 0; var4 >= (double)1000.0F && var2 < var6.length - 1; ++var2) {
         var4 /= (double)1000.0F;
      }

      String var7 = ONE_DECIMAL.format(var4);
      if (var7.endsWith(".0")) {
         var7 = var7.substring(0, var7.length() - 2);
      }

      return (var3 ? "-" : "") + var7 + var6[var2];
   }

   public static double parseNumber(String var0) throws NumberFormatException {
      if (var0 != null && !var0.isBlank()) {
         String var1 = var0.trim().toLowerCase().replace(" ", "").replace(",", ".");
         double var2 = (double)1.0F;
         if (var1.endsWith("k")) {
            var2 = (double)1000.0F;
            var1 = var1.substring(0, var1.length() - 1);
         } else if (var1.endsWith("m")) {
            var2 = (double)1000000.0F;
            var1 = var1.substring(0, var1.length() - 1);
         } else if (var1.endsWith("b")) {
            var2 = (double)1.0E9F;
            var1 = var1.substring(0, var1.length() - 1);
         } else if (var1.endsWith("t")) {
            var2 = 1.0E12;
            var1 = var1.substring(0, var1.length() - 1);
         }

         return Double.parseDouble(var1) * var2;
      } else {
         throw new NumberFormatException("Empty input");
      }
   }

   public static final class SignInputUtil {
      private static final Map<UUID, Session> sessions = new ConcurrentHashMap();

      public static void open(JavaPlugin var0, Player var1, List<String> var2, int var3, boolean var4, ResultHandler var5) {
         cleanupAndRemove(var1, var0, true);
         Location var7 = findNearbyAir(var1);
         if (var7 == null) {
            var1.sendMessage(Utils.formatColors("&#ff4444No space to open sign input."));
         } else {
            Block var8 = var7.getBlock();
            BlockData var9 = var8.getBlockData();
            int var10 = Math.max(1, Math.min(4, var3)) - 1;
            Object var6 = var2 == null ? new ArrayList() : new ArrayList(var2);

            while(((List)var6).size() < 4) {
               ((List)var6).add("");
            }

            if (((List)var6).size() > 4) {
               var6 = ((List)var6).subList(0, 4);
            }

            String[] var12 = new String[4];

            for(int var13 = 0; var13 < 4; ++var13) {
               String var14 = Utils.formatColors((String)((List)var6).get(var13));
               String var15 = ChatColor.stripColor(var14);
               var12[var13] = var15 == null ? "" : var15;
            }

            Session var16 = new Session(var1.getUniqueId(), var7.clone(), var9, var10, var5);
            sessions.put(var1.getUniqueId(), var16);
            Location var17 = var7.clone();
            TaskUtil.runAtLocation(var0, var17, () -> {
               Block var6 = var17.getBlock();
               var6.setType(Material.OAK_SIGN, false);
               if (!(var6.getState() instanceof Sign)) {
                  cleanupAndRemove(var1, var0, true);
                  var1.sendMessage(Utils.formatColors("&#ff4444Failed to create sign."));
               } else {
                  Sign var7 = (Sign)var6.getState();
                  applyLines(var7, var12);
                  setEditable(var7, var1.getUniqueId());
                  var7.update(true, false);
                  TaskUtil.runEntity(var0, var1, () -> var1.sendBlockChange(var17, var7.getBlockData()));
                  if (var4) {
                     startHideTask(var0, var1, var16);
                  }

                  TaskUtil.runEntityLater(var0, var1, () -> {
                     Block var3 = var17.getBlock();
                     if (!(var3.getState() instanceof Sign)) {
                        cleanupAndRemove(var1, var0, true);
                        var1.sendMessage(Utils.formatColors("&#ff4444Sign disappeared."));
                     } else {
                        try {
                           var1.openSign((Sign)var3.getState());
                        } catch (Throwable var5) {
                           cleanupAndRemove(var1, var0, true);
                           var1.sendMessage(Utils.formatColors("&#ff4444Failed to open sign input."));
                        }

                     }
                  }, 1L);
               }
            });
         }
      }

      public static void openFromConfig(JavaPlugin var0, Player var1, ConfigurationSection var2, boolean var3, ResultHandler var4) {
         List var5 = var2 == null ? Collections.emptyList() : var2.getStringList("lines");
         int var6 = var2 == null ? 2 : var2.getInt("input-line", 2);
         open(var0, var1, var5, var6, var3, var4);
      }

      public static void forceClose(JavaPlugin var0, Player var1) {
         cleanupAndRemove(var1, var0, true);
      }

      private static void startHideTask(JavaPlugin var0, Player var1, Session var2) {
         sendOriginalToOthers(var0, var1, var2.loc, var2.originalData);
         var2.hideTask = TaskUtil.runGlobalTimer(var0, () -> {
            if (var1.isOnline()) {
               sendOriginalToOthers(var0, var1, var2.loc, var2.originalData);
            }
         }, 1L, 1L);
         TaskUtil.runGlobalLater(var0, () -> {
            if (var2.hideTask != null) {
               var2.hideTask.cancel();
               var2.hideTask = null;
            }

         }, 10L);
      }

      private static void sendOriginalToOthers(JavaPlugin var0, Player var1, Location var2, BlockData var3) {
         for(Player var5 : Bukkit.getOnlinePlayers()) {
            if (!var5.equals(var1) && var5.getWorld().equals(var2.getWorld()) && !(var5.getLocation().distanceSquared(var2) > (double)9216.0F)) {
               TaskUtil.runEntity(var0, var5, () -> var5.sendBlockChange(var2, var3));
            }
         }

      }

      private static Session cleanupAndRemove(Player var0, JavaPlugin var1, boolean var2) {
         Session var3 = (Session)sessions.remove(var0.getUniqueId());
         if (var3 == null) {
            return null;
         } else {
            if (var3.hideTask != null) {
               var3.hideTask.cancel();
               var3.hideTask = null;
            }

            if (var2) {
               TaskUtil.runAtLocation(var1, var3.loc, () -> {
                  Block var3x = var3.loc.getBlock();
                  var3x.setBlockData(var3.originalData, false);
                  TaskUtil.runEntity(var1, var0, () -> var0.sendBlockChange(var3.loc, var3.originalData));
               });
            }

            return var3;
         }
      }

      private static boolean sameBlock(Location var0, Location var1) {
         if (var0 != null && var1 != null) {
            if (var0.getWorld() != null && var1.getWorld() != null) {
               if (!var0.getWorld().equals(var1.getWorld())) {
                  return false;
               } else {
                  return var0.getBlockX() == var1.getBlockX() && var0.getBlockY() == var1.getBlockY() && var0.getBlockZ() == var1.getBlockZ();
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      private static Location findNearbyAir(Player var0) {
         Location var1 = var0.getLocation();
         ArrayList var2 = new ArrayList();
         var2.add(var1.clone().add((double)0.0F, (double)2.0F, (double)0.0F));
         Vector var3 = var1.getDirection();
         var3.setY(0);
         if (var3.lengthSquared() > 1.0E-4) {
            var3.normalize();
            var2.add(var1.clone().add(var3).add((double)0.0F, (double)1.0F, (double)0.0F));
            var2.add(var1.clone().add(var3.multiply(2)).add((double)0.0F, (double)1.0F, (double)0.0F));
         }

         var2.add(var1.clone().add((double)0.0F, (double)1.0F, (double)0.0F));

         for(Location var5 : var2) {
            Block var6 = var5.getBlock();
            Material var7 = var6.getType();
            if (var7 == Material.AIR || var7 == Material.CAVE_AIR || var7 == Material.VOID_AIR) {
               return var6.getLocation();
            }
         }

         return null;
      }

      private static void applyLines(Sign var0, String[] var1) {
         try {
            Class var2 = Class.forName("org.bukkit.block.sign.Side");
            Method var11 = var0.getClass().getMethod("getSide", var2);
            Object[] var4 = var2.getEnumConstants();
            Object var5 = var4 != null && var4.length > 0 ? var4[0] : null;
            Object var6 = var11.invoke(var0, var5);
            Method var7 = var6.getClass().getMethod("setLine", Integer.TYPE, String.class);

            for(int var8 = 0; var8 < 4; ++var8) {
               var7.invoke(var6, var8, var1[var8]);
            }

         } catch (Throwable var10) {
            for(int var3 = 0; var3 < 4; ++var3) {
               try {
                  var0.setLine(var3, var1[var3]);
               } catch (Throwable var9) {
               }
            }

         }
      }

      private static void setEditable(Sign var0, UUID var1) {
         try {
            Method var2 = var0.getClass().getMethod("setWaxed", Boolean.TYPE);
            var2.invoke(var0, false);
         } catch (Throwable var5) {
         }

         try {
            Method var6 = var0.getClass().getMethod("setEditable", Boolean.TYPE);
            var6.invoke(var0, true);
         } catch (Throwable var4) {
         }

         try {
            Method var7 = var0.getClass().getMethod("setAllowedEditorUniqueId", UUID.class);
            var7.invoke(var0, var1);
         } catch (Throwable var3) {
         }

      }

      @FunctionalInterface
      public interface ResultHandler {
         void onResult(Player var1, String var2);
      }

      private static final class Session {
         final UUID uuid;
         final Location loc;
         final BlockData originalData;
         final int inputLineIndex;
         final ResultHandler handler;
         TaskUtil.Handle hideTask;

         Session(UUID var1, Location var2, BlockData var3, int var4, ResultHandler var5) {
            this.uuid = var1;
            this.loc = var2;
            this.originalData = var3;
            this.inputLineIndex = var4;
            this.handler = var5;
         }
      }

      public static final class SignListener implements Listener {
         private final JavaPlugin plugin;

         public SignListener(JavaPlugin var1) {
            this.plugin = var1;
         }

         @EventHandler
         public void onSignChange(SignChangeEvent var1) {
            Player var2 = var1.getPlayer();
            UUID var3 = var2.getUniqueId();
            Session var4 = (Session)Utils.SignInputUtil.sessions.get(var3);
            if (var4 != null) {
               if (Utils.SignInputUtil.sameBlock(var1.getBlock().getLocation(), var4.loc)) {
                  Session var5 = Utils.SignInputUtil.cleanupAndRemove(var2, this.plugin, true);
                  if (var5 != null) {
                     String var6 = "";

                     try {
                        var6 = var1.getLine(var5.inputLineIndex);
                     } catch (Throwable var8) {
                     }

                     if (var6 == null) {
                        var6 = "";
                     }

                     String var9;
                     String var7 = (var9 = ChatColor.stripColor(var6).trim()).equals("-") ? "" : var9;
                     TaskUtil.runEntity(this.plugin, var2, () -> {
                        try {
                           if (var5.handler != null) {
                              var5.handler.onResult(var2, var7);
                           }
                        } catch (Throwable var5x) {
                           this.plugin.getLogger().warning("SignInputUtil handler error: " + var5x.getMessage());
                        }

                     });
                  }
               }
            }
         }

         @EventHandler
         public void onQuit(PlayerQuitEvent var1) {
            Utils.SignInputUtil.cleanupAndRemove(var1.getPlayer(), this.plugin, true);
         }
      }
   }
}
