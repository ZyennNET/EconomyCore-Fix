package com.prismcore.survival.utils;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;

public class BlockStatsUtils {
   public static int getTotalBlocksBroken(OfflinePlayer var0) {
      int var1 = 0;

      for(Material var5 : Material.values()) {
         if (var5.isBlock()) {
            try {
               var1 += var0.getStatistic(Statistic.MINE_BLOCK, var5);
            } catch (IllegalArgumentException var7) {
            }
         }
      }

      return var1;
   }

   public static int getTotalBlocksPlaced(OfflinePlayer var0) {
      int var1 = 0;

      for(Material var5 : Material.values()) {
         if (var5.isBlock()) {
            try {
               var1 += var0.getStatistic(Statistic.USE_ITEM, var5);
            } catch (IllegalArgumentException var7) {
            }
         }
      }

      return var1;
   }
}
