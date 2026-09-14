package com.prismcore.survival.auction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public final class Utils {
   private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
   public static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

   private Utils() {
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

   public static String prettifyMaterialName(Material var0) {
      String var1 = var0.name().toLowerCase().replace('_', ' ');
      StringBuilder var2 = new StringBuilder();

      for(String var6 : var1.split(" ")) {
         if (var6.length() > 0) {
            var2.append(Character.toUpperCase(var6.charAt(0))).append(var6.substring(1)).append(" ");
         }
      }

      return var2.toString().trim();
   }

   public static double parsePrice(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         var0 = var0.trim().replace(",", "");
         char var1 = var0.charAt(var0.length() - 1);
         double var2 = (double)1.0F;
         String var4 = var0;
         switch (Character.toLowerCase(var1)) {
            case 'b':
               var2 = (double)1.0E9F;
               var4 = var0.substring(0, var0.length() - 1);
               break;
            case 'k':
               var2 = (double)1000.0F;
               var4 = var0.substring(0, var0.length() - 1);
               break;
            case 'm':
               var2 = (double)1000000.0F;
               var4 = var0.substring(0, var0.length() - 1);
               break;
            case 't':
               var2 = 1.0E12;
               var4 = var0.substring(0, var0.length() - 1);
         }

         try {
            double var5 = Double.parseDouble(var4);
            return var5 * var2;
         } catch (NumberFormatException var7) {
            return (double)-1.0F;
         }
      } else {
         return (double)-1.0F;
      }
   }

   public static String formatNumber(double var0) {
      if (var0 >= 1.0E12) {
         return ONE_DECIMAL.format(var0 / 1.0E12) + "T";
      } else if (var0 >= (double)1.0E9F) {
         return ONE_DECIMAL.format(var0 / (double)1.0E9F) + "B";
      } else if (var0 >= (double)1000000.0F) {
         return ONE_DECIMAL.format(var0 / (double)1000000.0F) + "M";
      } else if (var0 >= (double)1000.0F) {
         return ONE_DECIMAL.format(var0 / (double)1000.0F) + "K";
      } else {
         return Math.floor(var0) == var0 ? String.valueOf((long)var0) : ONE_DECIMAL.format(var0);
      }
   }

   public static List<String> buildSortLore(String var0, FileConfiguration var1) {
      List var2 = var1.getStringList("main-gui.items.sort.lore");
      String var3 = var1.getString("main-gui.sort-colors.current");
      String var4 = var1.getString("main-gui.sort-colors.not-current");
      ArrayList var5 = new ArrayList();

      for(Object var7 : var2) {
         String var8 = (String)var7;
         if (var8.contains("{mode‐highest}")) {
            String var9 = var8.split("\\{mode‐highest\\}", 2)[0];
            String var10 = var8.split("\\{mode‐highest\\}", 2).length > 1 ? var8.split("\\{mode‐highest\\}", 2)[1] : "";
            String var11 = "Highest Price";
            String var12 = var0.equals("Highest Price") ? var3 : var4;
            var8 = var12 + var9 + var11 + var10;
         } else if (var8.contains("{mode‐lowest}")) {
            String var13 = var8.split("\\{mode‐lowest\\}", 2)[0];
            String var16 = var8.split("\\{mode‐lowest\\}", 2).length > 1 ? var8.split("\\{mode‐lowest\\}", 2)[1] : "";
            String var19 = "Lowest Price";
            String var22 = var0.equals("Lowest Price") ? var3 : var4;
            var8 = var22 + var13 + var19 + var16;
         } else if (var8.contains("{mode‐lastlisted}")) {
            String var14 = var8.split("\\{mode‐lastlisted\\}", 2)[0];
            String var17 = var8.split("\\{mode‐lastlisted\\}", 2).length > 1 ? var8.split("\\{mode‐lastlisted\\}", 2)[1] : "";
            String var20 = "Last Listed";
            String var23 = var0.equals("Last Listed") ? var3 : var4;
            var8 = var23 + var14 + var20 + var17;
         } else if (var8.contains("{mode‐recentlylisted}")) {
            String var15 = var8.split("\\{mode‐recentlylisted\\}", 2)[0];
            String var18 = var8.split("\\{mode‐recentlylisted\\}", 2).length > 1 ? var8.split("\\{mode‐recentlylisted\\}", 2)[1] : "";
            String var21 = "Recently Listed";
            String var24 = var0.equals("Recently Listed") ? var3 : var4;
            var8 = var24 + var15 + var21 + var18;
         }

         var5.add(formatColors(var8));
      }

      return var5;
   }

   public static String toSmallCaps(String var0) {
      String var1 = "abcdefghijklmnopqrstuvwxyz";
      String var2 = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ";
      StringBuilder var3 = new StringBuilder();

      for(char var7 : var0.toLowerCase().toCharArray()) {
         int var8 = var1.indexOf(var7);
         if (var8 != -1) {
            var3.append(var2.charAt(var8));
         } else {
            var3.append(var7);
         }
      }

      return var3.toString();
   }
}
