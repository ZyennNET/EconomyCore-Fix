package com.h2ph.K;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import com.prismcore.survival.utils.BlockStatsUtils;
import com.prismcore.survival.utils.NumberUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

public class A extends PlaceholderExpansion {
   private final PrismSurvival B;
   private final String A;

   public A(PrismSurvival var1) {
      this(var1, "donutcore");
   }

   public A(PrismSurvival var1, String var2) {
      this.B = var1;
      this.A = var2;
   }

   public @NotNull String getIdentifier() {
      return this.A;
   }

   public @NotNull String getAuthor() {
      return "h2ph";
   }

   public @NotNull String getVersion() {
      return "1.0.0";
   }

   public boolean persist() {
      return true;
   }

   public String onRequest(OfflinePlayer var1, @NotNull String var2) {
      if (var1 == null) {
         return "";
      } else if (var2.equalsIgnoreCase("team")) {
         com.h2ph.H.A var7 = this.B.getDonutTeamModule();
         return var7 == null ? null : var7.getTeamPlaceholderValue(var1.getUniqueId());
      } else {
         PlayerData var3 = this.B.getPlayerDataManager().get(var1.getUniqueId());
         if (var3 == null) {
            return "0";
         } else if (var2.equalsIgnoreCase("shards")) {
            return NumberUtils.format(var3.getShards());
         } else if (var2.equalsIgnoreCase("shop_spent")) {
            return NumberUtils.format(var3.getShopSpent());
         } else if (var2.equalsIgnoreCase("balance")) {
            return NumberUtils.format(var3.getMoney());
         } else if (var2.equalsIgnoreCase("keyall")) {
            return this.B.getKeyAllManager().getTimeRemainingFormatted();
         } else if (var2.toLowerCase().startsWith("keys_")) {
            String var10 = var2.substring(5);
            String var11 = this.B.normalizeKeyName(var10);
            return String.valueOf(var3.getKeyCount(var11));
         } else {
            if (var2.toLowerCase().endsWith("_key")) {
               String var4 = var2.substring(0, var2.length() - 4);
               if (this.B.getKeyAllManager().isValidKey(var4)) {
                  return String.valueOf(var3.getKeyCount(var4));
               }
            }

            if (var2.equalsIgnoreCase("kills")) {
               return String.valueOf(var1.getStatistic(Statistic.PLAYER_KILLS));
            } else if (var2.equalsIgnoreCase("deaths")) {
               return String.valueOf(var1.getStatistic(Statistic.DEATHS));
            } else if (var2.equalsIgnoreCase("mobs_killed")) {
               return String.valueOf(var1.getStatistic(Statistic.MOB_KILLS));
            } else if (var2.equalsIgnoreCase("playtime")) {
               int var9 = var1.getStatistic(Statistic.PLAY_ONE_MINUTE);
               long var5 = (long)var9 / 20L;
               return this.A(var5);
            } else if (var2.equalsIgnoreCase("blocks_break")) {
               return String.valueOf(BlockStatsUtils.getTotalBlocksBroken(var1));
            } else if (var2.equalsIgnoreCase("blocks_placed")) {
               return String.valueOf(BlockStatsUtils.getTotalBlocksPlaced(var1));
            } else if (var2.equalsIgnoreCase("shard_booster")) {
               long var8 = var3.getShardBoosterRemainingSeconds();
               return var8 <= 0L ? "0s" : this.A(var8);
            } else {
               return null;
            }
         }
      }
   }

   private String A(long var1) {
      long var3 = var1 / 86400L;
      long var5 = var1 % 86400L;
      long var7 = var5 / 3600L;
      var5 %= 3600L;
      long var9 = var5 / 60L;
      long var11 = var5 % 60L;
      if (var3 > 0L) {
         return var3 + "d " + var7 + "h";
      } else if (var7 > 0L) {
         return var7 + "h " + var9 + "m";
      } else {
         return var9 > 0L ? var9 + "m " + var11 + "s" : var11 + "s";
      }
   }
}
