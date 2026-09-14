package com.prismcore.survival.tools;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

public final class Utils {
   private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

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

   public static String formatDuration(long var0) {
      if (var0 < 0L) {
         var0 = 0L;
      }

      long var2 = var0 / 86400L;
      long var4 = var0 % 86400L / 3600L;
      long var6 = var0 % 3600L / 60L;
      long var8 = var0 % 60L;
      return String.format("%dd %dh %dm %ds", var2, var4, var6, var8);
   }

   public static BlockFace getBlockFace(Player var0) {
      RayTraceResult var1 = var0.rayTraceBlocks((double)5.0F);
      return var1 != null && var1.getHitBlock() != null ? var1.getHitBlockFace() : BlockFace.SELF;
   }

   public static long parseDuration(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         long var1 = 0L;
         Matcher var3 = Pattern.compile("(\\d+)([wdhmsy])").matcher(var0.toLowerCase());

         while(var3.find()) {
            int var4 = Integer.parseInt(var3.group(1));
            switch (var3.group(2)) {
               case "y":
                  var1 += (long)var4 * 31536000L;
                  break;
               case "w":
                  var1 += (long)var4 * 604800L;
                  break;
               case "d":
                  var1 += (long)var4 * 86400L;
                  break;
               case "h":
                  var1 += (long)var4 * 3600L;
                  break;
               case "m":
                  var1 += (long)var4 * 60L;
                  break;
               case "s":
                  var1 += (long)var4;
            }
         }

         return var1 > 0L ? var1 : -1L;
      } else {
         return -1L;
      }
   }
}
