package com.h2ph.T;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.h2ph.PrismSurvival;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class C {
   private final PrismSurvival C;
   private final Map<String, _A> B = new ConcurrentHashMap();
   private final Gson A = new Gson();

   public C(PrismSurvival var1) {
      this.C = var1;
   }

   public void C() {
      this.C.getSchedulerAdapter().runTaskTimerAsync(() -> {
         this.E();
         this.A();
      }, 60L, 60L);
   }

   private void E() {
      if (this.C.getDatabaseManager().F()) {
         String var1 = this.C.getSurvivalConfig().getString("current-region");
         if (var1 != null && !var1.isEmpty()) {
            int var2 = Bukkit.getOnlinePlayers().size();
            int var3 = this.B();
            int var4 = Bukkit.getMaxPlayers();
            JsonObject var5 = new JsonObject();
            var5.addProperty("players", (Number)var2);
            var5.addProperty("max_players", (Number)var4);
            var5.addProperty("ms", (Number)var3);
            var5.addProperty("last_update", (Number)System.currentTimeMillis());
            this.C.getDatabaseManager().A("region_stats_" + var1.toLowerCase(), var5.toString());
         }
      }
   }

   private void A() {
      if (this.C.getDatabaseManager().F()) {
         String[] var1 = new String[]{"na", "europe", "asia"};

         for(String var5 : var1) {
            String var6 = this.C.getDatabaseManager().B("region_stats_" + var5);
            if (var6 != null) {
               try {
                  JsonObject var7 = (JsonObject)this.A.fromJson(var6, JsonObject.class);
                  int var8 = var7.has("players") ? var7.get("players").getAsInt() : 0;
                  int var9 = var7.has("max_players") ? var7.get("max_players").getAsInt() : 100;
                  int var10 = var7.has("ms") ? var7.get("ms").getAsInt() : 0;
                  long var11 = var7.has("last_update") ? var7.get("last_update").getAsLong() : 0L;
                  if (System.currentTimeMillis() - var11 > 10000L) {
                  }

                  this.B.put(var5, new _A(var8, var9, var10));
               } catch (Exception var13) {
                  var13.printStackTrace();
               }
            } else {
               this.B.put(var5, new _A(0, 100, 0));
            }
         }

      }
   }

   private int B() {
      if (Bukkit.getOnlinePlayers().isEmpty()) {
         return 0;
      } else {
         int var1 = 0;
         int var2 = 0;

         for(Player var4 : Bukkit.getOnlinePlayers()) {
            var1 += this.A(var4);
            ++var2;
         }

         return var2 == 0 ? 0 : var1 / var2;
      }
   }

   private int A(Player var1) {
      try {
         Object var2 = var1.getClass().getMethod("getHandle").invoke(var1);
         return var2.getClass().getField("ping").getInt(var2);
      } catch (Exception var5) {
         try {
            return var1.getPing();
         } catch (NoSuchMethodError var4) {
            return 0;
         }
      }
   }

   public _A A(String var1) {
      return (_A)this.B.getOrDefault(var1.toLowerCase(), new _A(0, 100, 0));
   }

   public static class _A {
      private final int C;
      private final int B;
      private final int A;

      public _A(int var1, int var2, int var3) {
         this.C = var1;
         this.B = var2;
         this.A = var3;
      }

      public int C() {
         return this.C;
      }

      public int B() {
         return this.B;
      }

      public int A() {
         return this.A;
      }
   }
}
