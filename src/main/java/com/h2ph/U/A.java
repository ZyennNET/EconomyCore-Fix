package com.h2ph.U;

import com.h2ph.PrismSurvival;
import com.h2ph.T.A.C;
import com.prismcore.survival.manager.PlayerDataManager;
import com.prismcore.survival.utils.BlockStatsUtils;
import com.prismcore.survival.utils.NumberUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.function.ToIntFunction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class A {
   private final PrismSurvival H;
   private HttpServer C;
   private final List<HttpExchange> B = new CopyOnWriteArrayList();
   private final List<HttpExchange> E = new CopyOnWriteArrayList();
   private final List<HttpExchange> G = new CopyOnWriteArrayList();
   private final List<HttpExchange> D = new CopyOnWriteArrayList();
   private String I;
   private String J;
   private final Map<String, _H> K = new ConcurrentHashMap();
   private static final int A = 100;
   private static final long F = 60000L;

   public A(PrismSurvival var1) {
      this.H = var1;
   }

   public void C() {
      File var1 = new File(this.H.getDataFolder(), "survival/api/config.yml");
      if (!var1.exists()) {
         this.H.saveResource("survival/api/config.yml", false);
      }

      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      if (!var2.getBoolean("enabled", true)) {
         this.H.getLogger().info("API Server is disabled in config.");
      } else {
         int var3 = var2.getInt("port", 8081);
         this.I = var2.getString("api_key", "changeme");
         this.J = var2.getString("region", "Europe");

         try {
            this.C = HttpServer.create(new InetSocketAddress(var3), 0);
            this.A((String)"/players/offend/", (HttpHandler)(new _C()));
            this.A((String)"/players/offend/playerlive", (HttpHandler)(new _F()));
            this.A((String)"/players/offend/unban", (HttpHandler)(new _L()));
            this.A((String)"/players/offend/ban", (HttpHandler)(new _G()));
            this.A((String)"/players/filter/playerlive", (HttpHandler)(new _E()));
            this.A((String)"/players/commands/playerlive", (HttpHandler)(new _D()));
            this.A((String)"/players/signs/playerlive", (HttpHandler)(new _K()));
            this.A((String)"/players/stats/", (HttpHandler)(new _B()));
            this.A((String)"/players/money", (HttpHandler)(new _J()));
            this.A((String)"/leaderboard/money/list", (HttpHandler)(new _O()));
            this.A((String)"/leaderboard/shards/list", (HttpHandler)(new _Q()));
            this.A((String)"/players/playtime", (HttpHandler)(new _R()));
            this.A((String)"/leaderboard/playtime/list", (HttpHandler)(new _M()));
            this.A((String)"/players/kill", (HttpHandler)(new _N()));
            this.A((String)"/players/death", (HttpHandler)(new _P()));
            this.A((String)"/players/blocks_break", (HttpHandler)(new _I()));
            this.A((String)"/players/blocks_placed", (HttpHandler)(new _S()));
            this.A((String)"/players/mobs_killed", (HttpHandler)(new _A()));
            this.C.setExecutor(Executors.newCachedThreadPool());
            this.C.start();
            this.H.getLogger().info("API Server started on port " + var3);
            this.H.getSchedulerAdapter().runTaskTimerAsync(this::B, 6000L, 6000L);
         } catch (IOException var5) {
            this.H.getLogger().severe("Failed to start API Server on port " + var3 + ": " + var5.getMessage());
         }

      }
   }

   public void A() {
      if (this.C != null) {
         this.C.stop(0);
         this.H.getLogger().info("API Server stopped.");
      }

   }

   private void A(String var1, HttpHandler var2) {
      try {
         this.C.createContext(var1, var2);
      } catch (IllegalArgumentException var4) {
         this.H.getLogger().severe("FAILED TO REGISTER CONTEXT: " + var1 + " - " + var4.getMessage());
         var4.printStackTrace();
      }

   }

   public void A(String var1, String var2, String var3, String var4, String var5) {
      String var6 = String.format("{\"type\": \"BAN\", \"player\": \"%s\", \"reason\": \"%s\", \"duration\": \"%s\", \"bannedBy\": \"%s\", \"banId\": \"%s\"}", this.A(var1), this.A(var2), this.A(var3), this.A(var4), this.A(var5));
      String var7 = "data: " + var6 + "\n\n";
      byte[] var8 = var7.getBytes(StandardCharsets.UTF_8);
      ArrayList var9 = new ArrayList();

      for(HttpExchange var11 : this.B) {
         try {
            OutputStream var12 = var11.getResponseBody();
            var12.write(var8);
            var12.flush();
         } catch (IOException var13) {
            var9.add(var11);
         }
      }

      this.B.removeAll(var9);
   }

   protected boolean A(HttpExchange var1) {
      if (this.I != null && !this.I.isEmpty() && !this.I.equals("changeme")) {
         List var2 = var1.getRequestHeaders().get("X-API-Key");
         if (var2 != null && !var2.isEmpty() && this.I.equals(var2.get(0))) {
            return true;
         } else {
            String var3 = var1.getRequestURI().getQuery();
            if (var3 != null) {
               for(String var7 : var3.split("&")) {
                  String[] var8 = var7.split("=");
                  if (var8.length == 2 && var8[0].equals("key") && this.I.equals(var8[1])) {
                     return true;
                  }
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private String A(World.Environment var1) {
      switch (var1) {
         case NORMAL -> {
            return "overworld";
         }
         case NETHER -> {
            return "nether";
         }
         case THE_END -> {
            return "end";
         }
         default -> {
            return var1.name().toLowerCase();
         }
      }
   }

   private String A(String var1) {
      return var1 == null ? "" : var1.replace("\"", "\\\"").replace("\n", " ");
   }

   public void B(String var1, String var2) {
      this.H.getSchedulerAdapter().runTaskAsync(() -> {
         String var3 = String.format("{\"type\": \"UNBAN\", \"player\": \"%s\", \"staff\": \"%s\"}", this.A(var1), this.A(var2));
         String var4 = "data: " + var3 + "\n\n";
         byte[] var5 = var4.getBytes(StandardCharsets.UTF_8);
         ArrayList var6 = new ArrayList();

         for(HttpExchange var8 : this.B) {
            try {
               OutputStream var9 = var8.getResponseBody();
               var9.write(var5);
               var9.flush();
            } catch (IOException var10) {
               var6.add(var8);
            }
         }

         this.B.removeAll(var6);
      });
   }

   public void B(String var1, String var2, String var3) {
      this.H.getSchedulerAdapter().runTaskAsync(() -> {
         String var4 = String.format("{\"type\": \"CHAT_FILTER\", \"player\": \"%s\", \"message\": \"%s\", \"detected\": \"%s\"}", this.A(var1), this.A(var2), this.A(var3));
         String var5 = "data: " + var4 + "\n\n";
         byte[] var6 = var5.getBytes(StandardCharsets.UTF_8);
         ArrayList var7 = new ArrayList();

         for(HttpExchange var9 : this.E) {
            try {
               OutputStream var10 = var9.getResponseBody();
               var10.write(var6);
               var10.flush();
            } catch (IOException var11) {
               var7.add(var9);
            }
         }

         this.E.removeAll(var7);
      });
   }

   public void A(Player var1, String var2) {
      String var3 = var1.getName();
      boolean var4 = var1.isOp();
      int var5 = var1.getLocation().getBlockX();
      int var6 = var1.getLocation().getBlockY();
      int var7 = var1.getLocation().getBlockZ();
      String var8 = this.A(var1.getWorld().getEnvironment());
      String var9 = var1.getAddress().getAddress().getHostAddress();
      long var10 = System.currentTimeMillis();
      this.H.getSchedulerAdapter().runTaskAsync(() -> {
         String var11 = String.format("{\"player\": \"%s\", \"command\": \"%s\", \"operator\": %b, \"coords\": \"%s\", \"time\": %d, \"dimension\": \"%s\", \"region\": \"%s\", \"ip\": \"%s\"}", this.A(var3), this.A(var2), var4, var5 + ", " + var6 + ", " + var7, var10, var10, this.A(var8), this.A(this.J), this.A(var9));
         String var12 = "data: " + var11 + "\n\n";
         byte[] var13 = var12.getBytes(StandardCharsets.UTF_8);
         ArrayList var14 = new ArrayList();

         for(HttpExchange var16 : this.G) {
            try {
               OutputStream var17 = var16.getResponseBody();
               var17.write(var13);
               var17.flush();
            } catch (IOException var18) {
               var14.add(var16);
            }
         }

         this.G.removeAll(var14);
      });
   }

   public void A(Player var1, String var2, List<String> var3) {
      String var4 = var1.getName();
      boolean var5 = var1.isOp();
      int var6 = var1.getLocation().getBlockX();
      int var7 = var1.getLocation().getBlockY();
      int var8 = var1.getLocation().getBlockZ();
      String var9 = this.A(var1.getWorld().getEnvironment());
      String var10 = var1.getAddress().getAddress().getHostAddress();
      long var11 = System.currentTimeMillis();
      this.H.getSchedulerAdapter().runTaskAsync(() -> {
         Optional var10000 = var3.stream().map((var1) -> {
            String var10000 = this.A(var1);
            return "\"" + var10000 + "\"";
         }).reduce((var0, var1) -> var0 + ", " + var1);
         String var12 = "[" + (String)var10000.orElse("") + "]";
         String var13 = String.format("{\"player\": \"%s\", \"text\": \"%s\", \"near\": %s, \"operator\": %b, \"coords\": \"%s\", \"time\": %d, \"dimension\": \"%s\", \"region\": \"%s\", \"ip\": \"%s\"}", this.A(var4), this.A(var2), var12, var5, var6 + ", " + var7 + ", " + var8, var11, var11, this.A(var9), this.A(this.J), this.A(var10));
         String var14 = "data: " + var13 + "\n\n";
         byte[] var15 = var14.getBytes(StandardCharsets.UTF_8);
         ArrayList var16 = new ArrayList();

         for(HttpExchange var18 : this.D) {
            try {
               OutputStream var19 = var18.getResponseBody();
               var19.write(var15);
               var19.flush();
            } catch (IOException var20) {
               var16.add(var18);
            }
         }

         this.D.removeAll(var16);
      });
   }

   private String A(long var1) {
      long var3 = var1 / 86400L;
      long var5 = var1 % 86400L;
      long var7 = var5 / 3600L;
      var5 %= 3600L;
      long var9 = var5 / 60L;
      long var11 = var5 % 60L;
      StringBuilder var13 = new StringBuilder();
      if (var3 > 0L) {
         var13.append(var3).append("d ");
      }

      if (var7 > 0L) {
         var13.append(var7).append("h ");
      }

      if (var9 > 0L) {
         var13.append(var9).append("m ");
      }

      if (var11 > 0L || var13.length() == 0) {
         var13.append(var11).append("s");
      }

      return var13.toString().trim();
   }

   private void A(HttpExchange var1, Statistic var2, String var3) throws IOException {
      if (this.B(var1)) {
         this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
      } else if (!this.A(var1)) {
         this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
      } else if (!"GET".equalsIgnoreCase(var1.getRequestMethod())) {
         this.A(var1, 405, "Method Not Allowed");
      } else {
         String var4 = var1.getRequestURI().getQuery();
         String var5 = null;
         if (var4 != null) {
            for(String var9 : var4.split("&")) {
               String[] var10 = var9.split("=");
               if (var10.length >= 2 && var10[0].equals("player")) {
                  var5 = var10[1];
               }
            }
         }

         if (var5 != null && !var5.isEmpty()) {
            Object var11 = this.H.getServer().getPlayer(var5);
            if (var11 == null) {
               var11 = this.H.getServer().getOfflinePlayer(var5);
            }

            if (var11 == null || !((OfflinePlayer)var11).hasPlayedBefore() && !((OfflinePlayer)var11).isOnline()) {
               this.A(var1, 404, "{\"error\": \"Player not found\"}");
            } else {
               int var12 = ((OfflinePlayer)var11).getStatistic(var2);
               String var13 = String.format("{\"player\": \"%s\", \"%s\": %d}", this.A(((OfflinePlayer)var11).getName()), var3, var12);
               this.A(var1, 200, var13);
            }

         } else {
            this.A(var1, 400, "{\"error\": \"Missing player parameter\"}");
         }
      }
   }

   private void A(HttpExchange var1, String var2, ToIntFunction<OfflinePlayer> var3) throws IOException {
      if (this.B(var1)) {
         this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
      } else if (!this.A(var1)) {
         this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
      } else if (!"GET".equalsIgnoreCase(var1.getRequestMethod())) {
         this.A(var1, 405, "Method Not Allowed");
      } else {
         String var4 = var1.getRequestURI().getQuery();
         String var5 = null;
         if (var4 != null) {
            for(String var9 : var4.split("&")) {
               String[] var10 = var9.split("=");
               if (var10.length >= 2 && var10[0].equals("player")) {
                  var5 = var10[1];
               }
            }
         }

         if (var5 != null && !var5.isEmpty()) {
            Object var11 = this.H.getServer().getPlayer(var5);
            if (var11 == null) {
               var11 = this.H.getServer().getOfflinePlayer(var5);
            }

            if (var11 == null || !((OfflinePlayer)var11).hasPlayedBefore() && !((OfflinePlayer)var11).isOnline()) {
               this.A(var1, 404, "{\"error\": \"Player not found\"}");
            } else {
               int var12 = var3.applyAsInt(var11);
               String var13 = String.format("{\"player\": \"%s\", \"%s\": %d}", this.A(((OfflinePlayer)var11).getName()), var2, var12);
               this.A(var1, 200, var13);
            }

         } else {
            this.A(var1, 400, "{\"error\": \"Missing player parameter\"}");
         }
      }
   }

   private Economy D() {
      if (this.H.getServer().getPluginManager().getPlugin("Vault") == null) {
         return null;
      } else {
         RegisteredServiceProvider var1 = this.H.getServer().getServicesManager().getRegistration(Economy.class);
         return var1 == null ? null : (Economy)var1.getProvider();
      }
   }

   private void A(HttpExchange var1, int var2, String var3) throws IOException {
      byte[] var4 = var3.getBytes(StandardCharsets.UTF_8);
      var1.sendResponseHeaders(var2, (long)var4.length);
      OutputStream var5 = var1.getResponseBody();
      var5.write(var4);
      var5.close();
   }

   private boolean B(HttpExchange var1) {
      String var2 = var1.getRemoteAddress().getAddress().getHostAddress();
      long var3 = System.currentTimeMillis();
      this.K.compute(var2, (var2x, var3x) -> {
         if (var3x != null && var3 <= var3x.B) {
            ++var3x.A;
            return var3x;
         } else {
            return new _H(var3 + 60000L, 1);
         }
      });
      return ((_H)this.K.get(var2)).A > 100;
   }

   public void B() {
      long var1 = System.currentTimeMillis();
      this.K.entrySet().removeIf((var2) -> var1 > ((_H)var2.getValue()).B);
   }

   private class _A implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         A.this.A(var1, Statistic.MOB_KILLS, "mobs_killed");
      }
   }

   private class _B implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else if (!"GET".equalsIgnoreCase(var1.getRequestMethod())) {
            A.this.A(var1, 405, "Method Not Allowed");
         } else {
            String var2 = var1.getRequestURI().getPath();
            if (var2.endsWith("/onlineplayers")) {
               ArrayList var23 = new ArrayList();
               Economy var24 = A.this.D();

               for(Player var29 : A.this.H.getServer().getOnlinePlayers()) {
                  double var31 = var24 != null ? var24.getBalance(var29) : (double)0.0F;
                  double var33 = A.this.H.getPlayerDataManager().get(var29.getUniqueId()).getShards();
                  int var34 = var29.getStatistic(Statistic.PLAYER_KILLS);
                  int var35 = var29.getStatistic(Statistic.DEATHS);
                  int var36 = var29.getStatistic(Statistic.MOB_KILLS);
                  int var37 = BlockStatsUtils.getTotalBlocksBroken(var29);
                  int var38 = BlockStatsUtils.getTotalBlocksPlaced(var29);
                  int var39 = var29.getStatistic(Statistic.PLAY_ONE_MINUTE);
                  String var17 = A.this.A((long)var39 / 20L);
                  var23.add(String.format("{\"name\": \"%s\", \"uuid\": \"%s\", \"kills\": %d, \"kills_formatted\": \"%s\", \"deaths\": %d, \"deaths_formatted\": \"%s\", \"blocks_break\": %d, \"blocks_break_formatted\": \"%s\", \"blocks_placed\": %d, \"blocks_placed_formatted\": \"%s\", \"mobs_killed\": %d, \"mobs_killed_formatted\": \"%s\", \"ping\": %d, \"balance\": %.2f, \"balance_formatted\": \"%s\", \"shards\": %.2f, \"shards_formatted\": \"%s\", \"playtime_formatted\": \"%s\"}", A.this.A(var29.getName()), var29.getUniqueId(), var34, NumberUtils.format((double)var34), var35, NumberUtils.format((double)var35), var37, NumberUtils.format((double)var37), var38, NumberUtils.format((double)var38), var36, NumberUtils.format((double)var36), var29.getPing(), var31, NumberUtils.formatMoney(var31), var33, NumberUtils.format(var33), A.this.A(var17)));
               }

               String var27 = "[" + String.join(",", var23) + "]";
               var1.getResponseHeaders().set("Content-Type", "application/json");
               var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
               A.this.A(var1, 200, var27);
            } else {
               String var3 = var1.getRequestURI().getQuery();
               String var4 = null;
               if (var3 != null) {
                  for(String var8 : var3.split("&")) {
                     String[] var9 = var8.split("=");
                     if (var9.length == 2 && var9[0].equals("player")) {
                        var4 = var9[1];
                        break;
                     }
                  }
               }

               if (var4 != null) {
                  Object var25 = A.this.H.getServer().getPlayer(var4);
                  if (var25 == null) {
                     var25 = A.this.H.getServer().getOfflinePlayer(var4);
                  }

                  if (var25 != null && (((OfflinePlayer)var25).hasPlayedBefore() || ((OfflinePlayer)var25).isOnline())) {
                     Economy var28 = A.this.D();
                     double var30 = var28 != null ? var28.getBalance((OfflinePlayer)var25) : (double)0.0F;
                     double var32 = (double)0.0F;

                     try {
                        var32 = A.this.H.getPlayerDataManager().get(((OfflinePlayer)var25).getUniqueId()).getShards();
                     } catch (Exception var22) {
                     }

                     int var11 = ((OfflinePlayer)var25).getStatistic(Statistic.PLAYER_KILLS);
                     int var12 = ((OfflinePlayer)var25).getStatistic(Statistic.DEATHS);
                     int var13 = ((OfflinePlayer)var25).getStatistic(Statistic.MOB_KILLS);
                     int var14 = BlockStatsUtils.getTotalBlocksBroken((OfflinePlayer)var25);
                     int var15 = BlockStatsUtils.getTotalBlocksPlaced((OfflinePlayer)var25);
                     long var16 = ((OfflinePlayer)var25).getLastPlayed();
                     boolean var18 = ((OfflinePlayer)var25).isOnline();
                     int var19 = ((OfflinePlayer)var25).getStatistic(Statistic.PLAY_ONE_MINUTE);
                     String var20 = A.this.A((long)var19 / 20L);
                     String var21 = String.format("{\"name\": \"%s\", \"uuid\": \"%s\", \"kills\": %d, \"kills_formatted\": \"%s\", \"deaths\": %d, \"deaths_formatted\": \"%s\", \"blocks_break\": %d, \"blocks_break_formatted\": \"%s\", \"blocks_placed\": %d, \"blocks_placed_formatted\": \"%s\", \"mobs_killed\": %d, \"mobs_killed_formatted\": \"%s\", \"lastPlayed\": %d, \"playtime_formatted\": \"%s\", \"isOnline\": %b, \"balance\": %.2f, \"balance_formatted\": \"%s\", \"shards\": %.2f, \"shards_formatted\": \"%s\"}", A.this.A(((OfflinePlayer)var25).getName()), ((OfflinePlayer)var25).getUniqueId(), var11, NumberUtils.format((double)var11), var12, NumberUtils.format((double)var12), var14, NumberUtils.format((double)var14), var15, NumberUtils.format((double)var15), var13, NumberUtils.format((double)var13), var16, A.this.A(var20), var18, var30, NumberUtils.formatMoney(var30), var32, NumberUtils.format(var32));
                     var1.getResponseHeaders().set("Content-Type", "application/json");
                     var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
                     A.this.A(var1, 200, var21);
                  } else {
                     A.this.A(var1, 404, "{\"error\": \"Player not found\"}");
                  }

               } else {
                  A.this.A(var1, 400, "{\"error\": \"Invalid request\"}");
               }
            }
         }
      }
   }

   private class _C implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else if (!"GET".equalsIgnoreCase(var1.getRequestMethod())) {
            A.this.A(var1, 405, "Method Not Allowed");
         } else {
            String var2 = var1.getRequestURI().getQuery();
            String var3 = null;
            if (var2 != null && var2.contains("search=")) {
               for(String var7 : var2.split("&")) {
                  String[] var8 = var7.split("=");
                  if (var8.length == 2 && var8[0].equals("search")) {
                     var3 = var8[1];
                     break;
                  }
               }
            }

            if (var3 == null) {
               A.this.A(var1, 400, "Missing search parameter");
            } else {
               String var10 = "{\"error\": \"Database unavailable\"}";
               if (A.this.H.getOffendPlugin() != null) {
                  C var11 = A.this.H.getOffendPlugin().getOffendDAO();
                  Object var12 = null;
                  C._A var13 = var11.getBanInfoById(var3);
                  if (var13 == null) {
                     var13 = var11.getBanInfoByName(var3);
                  }

                  if (var13 == null) {
                     try {
                        UUID var14 = UUID.fromString(var3);
                        var13 = var11.getBanInfo(var14);
                     } catch (IllegalArgumentException var9) {
                     }
                  }

                  if (var13 != null) {
                     var10 = String.format("{\"id\": \"%s\", \"player\": \"%s\", \"reason\": \"%s\", \"date\": %d, \"expire\": %d, \"bannedBy\": \"%s\"}", var13.B, A.this.A(var13.E), A.this.A(var13.G), var13.D, var13.C, A.this.A(var13.F));
                  } else {
                     var10 = "{\"error\": \"Ban not found\"}";
                  }
               }

               var1.getResponseHeaders().set("Content-Type", "application/json");
               var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
               A.this.A(var1, 200, var10);
            }
         }
      }
   }

   private class _D implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            var1.getResponseHeaders().set("Content-Type", "text/event-stream");
            var1.getResponseHeaders().set("Cache-Control", "no-cache");
            var1.getResponseHeaders().set("Connection", "keep-alive");
            var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            var1.sendResponseHeaders(200, 0L);
            A.this.G.add(var1);
         }
      }
   }

   private class _E implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            var1.getResponseHeaders().set("Content-Type", "text/event-stream");
            var1.getResponseHeaders().set("Cache-Control", "no-cache");
            var1.getResponseHeaders().set("Connection", "keep-alive");
            var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            var1.sendResponseHeaders(200, 0L);
            A.this.E.add(var1);
         }
      }
   }

   private class _F implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            var1.getResponseHeaders().set("Content-Type", "text/event-stream");
            var1.getResponseHeaders().set("Cache-Control", "no-cache");
            var1.getResponseHeaders().set("Connection", "keep-alive");
            var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            var1.sendResponseHeaders(200, 0L);
            A.this.B.add(var1);
         }
      }
   }

   private class _G implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else if (!"POST".equalsIgnoreCase(var1.getRequestMethod())) {
            A.this.A(var1, 405, "Method Not Allowed");
         } else {
            String var2 = var1.getRequestURI().getQuery();
            String var3 = null;
            String var4 = null;
            String var5 = null;
            if (var2 != null) {
               for(String var9 : var2.split("&")) {
                  String[] var10 = var9.split("=");
                  if (var10.length >= 2) {
                     String var11 = var10[0];
                     String var12 = var10[1];
                     if (var11.equals("player")) {
                        var3 = var12;
                     } else if (var11.equals("reason")) {
                        var4 = var12;
                     } else if (var11.equals("duration")) {
                        var5 = var12;
                     }
                  }
               }
            }

            if (var3 != null && !var3.isEmpty()) {
               if (A.this.H.getOffendPlugin() == null) {
                  A.this.A(var1, 500, "{\"error\": \"Offend plugin unavailable\"}");
               } else {
                  String var14 = var3;
                  String var15 = var4 != null && !var4.isEmpty() ? var4 : "Banned";
                  String var16 = var5;

                  try {
                     Object var17 = A.this.H.getServer().getPlayer(var14);
                     if (var17 == null) {
                        var17 = A.this.H.getOffendPlugin().resolveOfflinePlayer(var14);
                     }

                     if (var17 == null) {
                        A.this.A(var1, 404, "{\"error\": \"Player not found\"}");
                        return;
                     }

                     C._A var18 = A.this.H.getOffendPlugin().banPlayer(A.this.H.getServer().getConsoleSender(), (OfflinePlayer)var17, var14, var15, var16);
                     String var19 = String.format("{\"status\": \"banned\", \"player\": \"%s\", \"banId\": \"%s\", \"reason\": \"%s\", \"duration\": \"%s\"}", A.this.A(var18.E), A.this.A(var18.B), A.this.A(var18.G), var16 != null ? A.this.A(var16) : "Default");
                     A.this.A(var1, 200, var19);
                  } catch (Exception var13) {
                     var13.printStackTrace();
                     A var10003 = A.this;
                     A.this.A(var1, 500, "{\"error\": \"Internal Server Error: " + var10003.A(var13.getMessage()) + "\"}");
                  }

               }
            } else {
               A.this.A(var1, 400, "{\"error\": \"Missing player parameter\"}");
            }
         }
      }
   }

   private static class _H {
      long B;
      int A;

      _H(long var1, int var3) {
         this.B = var1;
         this.A = var3;
      }
   }

   private class _I implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         A.this.A(var1, "blocks_break", BlockStatsUtils::getTotalBlocksBroken);
      }
   }

   private class _J implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else if (!"GET".equalsIgnoreCase(var1.getRequestMethod())) {
            A.this.A(var1, 405, "Method Not Allowed");
         } else {
            String var2 = var1.getRequestURI().getQuery();
            String var3 = null;
            if (var2 != null) {
               for(String var7 : var2.split("&")) {
                  String[] var8 = var7.split("=");
                  if (var8.length >= 2 && var8[0].equals("player")) {
                     var3 = var8[1];
                  }
               }
            }

            if (var3 != null && !var3.isEmpty()) {
               Economy var9 = A.this.D();
               if (var9 == null) {
                  A.this.A(var1, 500, "{\"error\": \"Economy plugin not found\"}");
               } else {
                  double var10 = var9.getBalance(var3);
                  String var11 = String.format("{\"player\": \"%s\", \"balance\": %.2f}", A.this.A(var3), var10);
                  A.this.A(var1, 200, var11);
               }
            } else {
               A.this.A(var1, 400, "{\"error\": \"Missing player parameter\"}");
            }
         }
      }
   }

   private class _K implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            var1.getResponseHeaders().set("Content-Type", "text/event-stream");
            var1.getResponseHeaders().set("Cache-Control", "no-cache");
            var1.getResponseHeaders().set("Connection", "keep-alive");
            var1.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            var1.sendResponseHeaders(200, 0L);
            A.this.D.add(var1);
         }
      }
   }

   private class _L implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else if (!"POST".equalsIgnoreCase(var1.getRequestMethod())) {
            A.this.A(var1, 405, "Method Not Allowed");
         } else {
            String var2 = var1.getRequestURI().getQuery();
            String var3 = null;
            String var4 = null;
            if (var2 != null) {
               for(String var8 : var2.split("&")) {
                  String[] var9 = var8.split("=");
                  if (var9.length == 2) {
                     String var10 = var9[0];
                     String var11 = var9[1];
                     if (var10.equals("player")) {
                        var3 = var11;
                     } else if (var10.equals("ban") || var10.equals("banId") || var10.equals("ban_id") || var10.equals("id")) {
                        var4 = var11;
                     }
                  }
               }
            }

            if (var3 != null && !var3.isEmpty() || var4 != null && !var4.isEmpty()) {
               if (A.this.H.getOffendPlugin() == null) {
                  A.this.A(var1, 500, "{\"error\": \"Offend plugin / database unavailable\"}");
               } else {
                  C var16 = A.this.H.getOffendPlugin().getOffendDAO();
                  C._A var17 = null;
                  if (var4 != null && !var4.isEmpty()) {
                     var17 = var16.getBanInfoById(var4);
                     if (var17 == null) {
                        A.this.A(var1, 404, "{\"error\": \"Ban ID not found\"}");
                        return;
                     }
                  } else if (var3 != null && !var3.isEmpty()) {
                     var17 = var16.getBanInfoByName(var3);
                     if (var17 == null) {
                        try {
                           UUID var18 = UUID.fromString(var3);
                           var17 = var16.getBanInfo(var18);
                        } catch (IllegalArgumentException var14) {
                        }
                     }

                     if (var17 == null) {
                        String var19 = var3.replace("#", "");
                        var17 = var16.getBanInfoById(var19);
                     }

                     if (var17 == null) {
                        A.this.A(var1, 404, "{\"error\": \"No active ban found for provided identifier\"}");
                        return;
                     }
                  }

                  try {
                     if (var17.B != null && !var17.B.isEmpty()) {
                        var16.removeBanById(var17.B);
                     } else if (var17.A != null && !var17.A.isEmpty()) {
                        try {
                           var16.removeBan(UUID.fromString(var17.A));
                        } catch (IllegalArgumentException var13) {
                           var16.removeBan(var17.E);
                        }
                     } else {
                        var16.removeBan(var17.E);
                     }

                     if (var17.A != null && var17.I != null) {
                        try {
                           var16.setOffenseCount(UUID.fromString(var17.A), var17.I, 0);
                        } catch (Exception var12) {
                        }
                     }

                     A.this.B(var17.E != null ? var17.E : (var17.A != null ? var17.A : "unknown"), "API");
                     String var20 = String.format("{\"status\": \"unbanned\", \"player\": \"%s\", \"banId\": \"%s\"}", A.this.A(var17.E), A.this.A(var17.B));
                     A.this.A(var1, 200, var20);
                  } catch (Exception var15) {
                     A var10003 = A.this;
                     A.this.A(var1, 500, "{\"error\": \"Failed to unban: " + var10003.A(var15.getMessage()) + "\"}");
                  }
               }
            } else {
               A.this.A(var1, 400, "{\"error\": \"Missing player or ban id parameter\"}");
            }
         }
      }
   }

   private class _M implements HttpHandler {
      private List<PlayerDataManager.LeaderboardEntry> D = null;
      private long C = 0L;
      private static final long B = 300000L;

      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            Object var2;
            synchronized(this) {
               if (this.D != null && System.currentTimeMillis() - this.C < 300000L) {
                  var2 = this.D;
               } else {
                  var2 = new ArrayList();

                  try {
                     OfflinePlayer[] var4 = A.this.H.getServer().getOfflinePlayers();

                     for(OfflinePlayer var8 : var4) {
                        if (var8.getName() != null) {
                           try {
                              int var9 = var8.getStatistic(Statistic.PLAY_ONE_MINUTE);
                              if (var9 > 0) {
                                 long var10 = (long)var9 / 20L;
                                 ((List)var2).add(new PlayerDataManager.LeaderboardEntry(var8.getName(), var8.getUniqueId(), (double)var10));
                              }
                           } catch (Exception var13) {
                           }
                        }
                     }

                     ((List)var2).sort((var0, var1x) -> Double.compare(var1x.value, var0.value));
                     if (((List)var2).size() > 10) {
                        var2 = ((List)var2).subList(0, 10);
                     }

                     this.D = (List<PlayerDataManager.LeaderboardEntry>)var2;
                     this.C = System.currentTimeMillis();
                  } catch (Exception var14) {
                     var14.printStackTrace();
                     var2 = new ArrayList();
                  }
               }
            }

            StringBuilder var3 = new StringBuilder("[");

            for(int var16 = 0; var16 < ((List)var2).size(); ++var16) {
               PlayerDataManager.LeaderboardEntry var17 = (PlayerDataManager.LeaderboardEntry)((List)var2).get(var16);
               String var18 = A.this.A((long)var17.value);
               var3.append(String.format("{\"rank\": %d, \"player\": \"%s\", \"playtime_seconds\": %d, \"playtime_formatted\": \"%s\"}", var16 + 1, A.this.A(var17.name), (long)var17.value, A.this.A(var18)));
               if (var16 < ((List)var2).size() - 1) {
                  var3.append(",");
               }
            }

            var3.append("]");
            A.this.A(var1, 200, var3.toString());
         }
      }
   }

   private class _N implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         A.this.A(var1, Statistic.PLAYER_KILLS, "kills");
      }
   }

   private class _O implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            List var2 = A.this.H.getPlayerDataManager().getTopMoney(10);
            StringBuilder var3 = new StringBuilder("[");

            for(int var4 = 0; var4 < var2.size(); ++var4) {
               PlayerDataManager.LeaderboardEntry var5 = (PlayerDataManager.LeaderboardEntry)var2.get(var4);
               var3.append(String.format("{\"rank\": %d, \"player\": \"%s\", \"balance\": %.2f}", var4 + 1, A.this.A(var5.name), var5.value));
               if (var4 < var2.size() - 1) {
                  var3.append(",");
               }
            }

            var3.append("]");
            A.this.A(var1, 200, var3.toString());
         }
      }
   }

   private class _P implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         A.this.A(var1, Statistic.DEATHS, "deaths");
      }
   }

   private class _Q implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else {
            List var2 = A.this.H.getPlayerDataManager().getTopShards(10);
            StringBuilder var3 = new StringBuilder("[");

            for(int var4 = 0; var4 < var2.size(); ++var4) {
               PlayerDataManager.LeaderboardEntry var5 = (PlayerDataManager.LeaderboardEntry)var2.get(var4);
               var3.append(String.format("{\"rank\": %d, \"player\": \"%s\", \"shards\": %.2f}", var4 + 1, A.this.A(var5.name), var5.value));
               if (var4 < var2.size() - 1) {
                  var3.append(",");
               }
            }

            var3.append("]");
            A.this.A(var1, 200, var3.toString());
         }
      }
   }

   private class _R implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         if (A.this.B(var1)) {
            A.this.A(var1, 429, "{\"error\": \"Too Many Requests\"}");
         } else if (!A.this.A(var1)) {
            A.this.A(var1, 401, "{\"error\": \"Unauthorized\"}");
         } else if (!"GET".equalsIgnoreCase(var1.getRequestMethod())) {
            A.this.A(var1, 405, "Method Not Allowed");
         } else {
            String var2 = var1.getRequestURI().getQuery();
            String var3 = null;
            if (var2 != null) {
               for(String var7 : var2.split("&")) {
                  String[] var8 = var7.split("=");
                  if (var8.length >= 2 && var8[0].equals("player")) {
                     var3 = var8[1];
                  }
               }
            }

            if (var3 != null && !var3.isEmpty()) {
               Object var11 = A.this.H.getServer().getPlayer(var3);
               if (var11 == null) {
                  var11 = A.this.H.getServer().getOfflinePlayer(var3);
               }

               if (var11 != null && (((OfflinePlayer)var11).isOnline() || ((OfflinePlayer)var11).hasPlayedBefore())) {
                  int var12 = 0;

                  try {
                     var12 = ((OfflinePlayer)var11).getStatistic(Statistic.PLAY_ONE_MINUTE);
                  } catch (Exception var10) {
                  }

                  long var13 = (long)var12 / 20L;
                  String var14 = A.this.A(var13);
                  String var9 = String.format("{\"player\": \"%s\", \"playtime_seconds\": %d, \"playtime_formatted\": \"%s\"}", A.this.A(((OfflinePlayer)var11).getName()), var13, A.this.A(var14));
                  A.this.A(var1, 200, var9);
               } else {
                  A.this.A(var1, 404, "{\"error\": \"Player not found\"}");
               }
            } else {
               A.this.A(var1, 400, "{\"error\": \"Missing player parameter\"}");
            }
         }
      }
   }

   private class _S implements HttpHandler {
      public void handle(HttpExchange var1) throws IOException {
         A.this.A(var1, "blocks_placed", BlockStatsUtils::getTotalBlocksPlaced);
      }
   }
}
