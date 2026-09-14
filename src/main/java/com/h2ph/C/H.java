package com.h2ph.c;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import com.prismcore.survival.manager.PlayerDataManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class H {
   private final PrismSurvival C;
   private final File B;
   private FileConfiguration D;
   private final Set<UUID> E = ConcurrentHashMap.newKeySet();
   private final Map<_A, List<_B>> A = new ConcurrentHashMap();

   public H(PrismSurvival var1) {
      this.C = var1;
      this.B = new File(var1.getDataFolder(), "leaderboards/data.yml");
      this.C();
      this.B();
   }

   public void C() {
      if (!this.B.exists()) {
         try {
            this.B.getParentFile().mkdirs();
            this.B.createNewFile();
         } catch (IOException var6) {
            this.C.getLogger().warning("[Leaderboards] Could not create leaderboards/data.yml");
         }
      }

      this.D = YamlConfiguration.loadConfiguration(this.B);
      this.E.clear();
      ConfigurationSection var1 = this.D.getConfigurationSection("players");
      if (var1 != null) {
         for(String var3 : var1.getKeys(false)) {
            try {
               this.E.add(UUID.fromString(var3));
            } catch (IllegalArgumentException var5) {
            }
         }
      }

      for(Player var8 : Bukkit.getOnlinePlayers()) {
         this.A(var8);
      }

   }

   public void A() {
      if (this.D != null && this.B != null) {
         try {
            this.D.save(this.B);
         } catch (IOException var2) {
            this.C.getLogger().warning("[Leaderboards] Failed to save leaderboards/data.yml: " + var2.getMessage());
         }

      }
   }

   public void A(Player var1) {
      UUID var2 = var1.getUniqueId();
      this.E.add(var2);
      this.D.set("players." + String.valueOf(var2) + ".name", var1.getName());
   }

   public String A(UUID var1) {
      String var2 = this.D.getString("players." + String.valueOf(var1) + ".name");
      if (var2 != null && !var2.isBlank()) {
         return var2;
      } else {
         OfflinePlayer var3 = Bukkit.getOfflinePlayer(var1);
         return var3.getName() != null ? var3.getName() : var1.toString().substring(0, 8);
      }
   }

   public void B() {
      for(_A var4 : H._A.values()) {
         this.A.put(var4, this.A(var4));
      }

   }

   public List<_B> B(_A var1) {
      List var2 = (List)this.A.get(var1);
      if (var2 == null) {
         var2 = this.A(var1);
         this.A.put(var1, var2);
      }

      return var2;
   }

   public int B(_A var1, UUID var2) {
      List var3 = this.B(var1);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         if (((_B)var3.get(var4)).C().equals(var2)) {
            return var4 + 1;
         }
      }

      return -1;
   }

   public double C(_A var1, UUID var2) {
      for(_B var4 : this.B(var1)) {
         if (var4.C().equals(var2)) {
            return var4.A();
         }
      }

      return this.A(var1, var2);
   }

   public int A(_A var1, String var2, int var3) {
      if (var2 != null && !var2.isBlank()) {
         String var4 = var2.toLowerCase(Locale.ROOT);
         List var5 = this.B(var1);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            if (((_B)var5.get(var6)).B().toLowerCase(Locale.ROOT).contains(var4)) {
               return var6 / var3;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   private List<_B> A(_A var1) {
      HashMap var2 = new HashMap();
      HashSet var3 = new HashSet(this.E);

      for(Player var5 : Bukkit.getOnlinePlayers()) {
         var3.add(var5.getUniqueId());
         this.D.set("players." + String.valueOf(var5.getUniqueId()) + ".name", var5.getName());
      }

      for(UUID var10 : var3) {
         double var6 = this.A(var1, var10);
         if (!(var6 <= (double)0.0F) || var1 == H._A.C) {
            var2.put(var10, new _B(var10, this.A(var10), var6));
         }
      }

      ArrayList var9 = new ArrayList(var2.values());
      var9.sort(Comparator.comparingDouble(_B::A).reversed());
      return var9;
   }

   private double A(_A var1, UUID var2) {
      OfflinePlayer var3 = Bukkit.getOfflinePlayer(var2);
      PlayerDataManager var4 = this.C.getPlayerDataManager();
      PlayerData var5 = var4 != null ? var4.get(var2) : null;

      try {
         double var10000;
         switch (var1.ordinal()) {
            case 0 -> var10000 = var5 != null ? var5.getMoney() : (double)0.0F;
            case 1 -> var10000 = var5 != null ? var5.getShards() : (double)0.0F;
            case 2 -> var10000 = var5 != null ? var5.getShopSpent() : (double)0.0F;
            case 3 -> var10000 = (double)var3.getStatistic(Statistic.PLAY_ONE_MINUTE) / (double)20.0F;
            case 4 -> var10000 = (double)var3.getStatistic(Statistic.DEATHS);
            case 5 -> var10000 = this.A(var2, "blocks-placed");
            case 6 -> var10000 = this.A(var2, "blocks-broken");
            case 7 -> var10000 = (double)var3.getStatistic(Statistic.MOB_KILLS);
            case 8 -> var10000 = (double)var3.getStatistic(Statistic.PLAYER_KILLS);
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      } catch (Exception var7) {
         return (double)0.0F;
      }
   }

   private double A(UUID var1, String var2) {
      return (double)this.D.getLong("players." + String.valueOf(var1) + "." + var2, 0L);
   }

   public void A(Player var1, int var2) {
      this.A(var1);
      String var3 = "players." + String.valueOf(var1.getUniqueId()) + ".blocks-broken";
      this.D.set(var3, this.D.getLong(var3, 0L) + (long)var2);
   }

   public void B(Player var1, int var2) {
      this.A(var1);
      String var3 = "players." + String.valueOf(var1.getUniqueId()) + ".blocks-placed";
      this.D.set(var3, this.D.getLong(var3, 0L) + (long)var2);
   }

   public static String A(_A var0, double var1) {
      if (var0 == H._A.B) {
         long var3 = Math.max(0L, (long)var1);
         long var5 = var3 / 3600L;
         long var7 = var3 % 3600L / 60L;
         return var5 > 0L ? var5 + "h " + var7 + "m" : var7 + "m";
      } else {
         return A(var1);
      }
   }

   public static String A(double var0) {
      if (var0 >= (double)1.0E9F) {
         return String.format(Locale.US, "%.1fB", var0 / (double)1.0E9F).replace(".0B", "B");
      } else if (var0 >= (double)1000000.0F) {
         return String.format(Locale.US, "%.1fM", var0 / (double)1000000.0F).replace(".0M", "M");
      } else if (var0 >= (double)1000.0F) {
         return String.format(Locale.US, "%.1fK", var0 / (double)1000.0F).replace(".0K", "K");
      } else {
         return var0 == (double)((long)var0) ? String.valueOf((long)var0) : String.format(Locale.US, "%.2f", var0);
      }
   }

   public static _A A(String var0) {
      if (var0 == null) {
         return null;
      } else {
         _A var10000;
         switch (var0.toLowerCase(Locale.ROOT).replace('_', '-')) {
            case "money":
               var10000 = H._A.G;
               break;
            case "shards":
               var10000 = H._A.I;
               break;
            case "shop-spent":
            case "shop_spent":
               var10000 = H._A.F;
               break;
            case "playtime":
               var10000 = H._A.B;
               break;
            case "deaths":
               var10000 = H._A.C;
               break;
            case "blocks-placed":
            case "blocks_placed":
               var10000 = H._A.J;
               break;
            case "blocks-broken":
            case "blocks_broken":
               var10000 = H._A.E;
               break;
            case "mobs-killed":
            case "mobs_killed":
               var10000 = H._A.H;
               break;
            case "player-kills":
            case "player_kills":
               var10000 = H._A.A;
               break;
            default:
               var10000 = null;
         }

         return var10000;
      }
   }

   public static String C(_A var0) {
      String var10000;
      switch (var0.ordinal()) {
         case 0 -> var10000 = "money";
         case 1 -> var10000 = "shards";
         case 2 -> var10000 = "shop_spent";
         case 3 -> var10000 = "playtime";
         case 4 -> var10000 = "deaths";
         case 5 -> var10000 = "blocks-placed";
         case 6 -> var10000 = "blocks-broken";
         case 7 -> var10000 = "mobs-killed";
         case 8 -> var10000 = "player-kills";
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static enum _A {
      G,
      I,
      F,
      B,
      C,
      J,
      E,
      H,
      A;

      // $FF: synthetic method
      private static _A[] A() {
         return new _A[]{G, I, F, B, C, J, E, H, A};
      }
   }

   public static record _B(UUID B, String A, double C) {
      public _B(UUID var1, String var2, double var3) {
         this.B = var1;
         this.A = var2;
         this.C = var3;
      }

      public UUID C() {
         return this.B;
      }

      public String B() {
         return this.A;
      }

      public double A() {
         return this.C;
      }
   }
}
