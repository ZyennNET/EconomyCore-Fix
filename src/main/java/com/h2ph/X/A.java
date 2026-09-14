package com.h2ph.X;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType.Play.Server;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class A implements Listener {
   private final PrismSurvival I;
   private FileConfiguration U;
   private final File R;
   private static final Pattern O = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private final Map<UUID, ScheduledTask> Q = new ConcurrentHashMap();
   private final Map<UUID, String> C = new ConcurrentHashMap();
   private final Map<UUID, LinkedHashSet<UUID>> G = new ConcurrentHashMap();
   private final Map<UUID, String> L = new ConcurrentHashMap();
   private final ConcurrentHashMap<UUID, String> E = new ConcurrentHashMap();
   private final ConcurrentHashMap<UUID, Long> J = new ConcurrentHashMap();
   private final PacketListenerCommon S;
   private int T = 4;
   private int A = 20;
   private int D;
   private boolean N;
   private final Map<String, Integer> F;
   private final List<Player> M;
   private long B;
   private double P;
   private long H;
   private double K;

   public A(PrismSurvival var1) {
      this.D = this.T * this.A;
      this.N = true;
      this.F = new ConcurrentHashMap();
      this.M = new CopyOnWriteArrayList();
      this.B = 0L;
      this.P = (double)20.0F;
      this.H = 0L;
      this.K = (double)50.0F;
      this.I = var1;
      this.R = new File(var1.getDataFolder(), "tablist/config.yml");
      this.C();
      this.G.clear();
      this.S = new PacketListenerCommon(PacketListenerPriority.HIGH) {
         public void onPacketSend(PacketSendEvent var1) {
            A.this.A(var1);
         }
      };
      PacketEvents.getAPI().getEventManager().registerListener(this.S);
      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   private void C() {
      if (!this.R.exists()) {
         this.R.getParentFile().mkdirs();
         this.I.saveResource("tablist/config.yml", false);
      }

      this.U = YamlConfiguration.loadConfiguration(this.R);
      this.B();
   }

   private void B() {
      int var1 = Math.max(1, this.U.getInt("TAB.MAX_COLUMNS", 4));
      int var2 = Math.max(1, this.U.getInt("TAB.MAX_ROWS", 20));
      this.T = var1;
      this.A = var2;
      this.D = var1 * var2;
      this.N = this.U.getBoolean("TAB.GROUP_SORTING.ENABLED", false);
      this.F.clear();
      if (this.U.isConfigurationSection("TAB.GROUP_SORTING.RANKINGS")) {
         for(String var4 : this.U.getConfigurationSection("TAB.GROUP_SORTING.RANKINGS").getKeys(false)) {
            int var5 = this.U.getInt("TAB.GROUP_SORTING.RANKINGS." + var4, 0);
            this.F.put(var4.toLowerCase(), var5);
         }
      }

   }

   public void reloadTabList() {
      this.G.clear();
      this.C();
      this.B();
      this.D();

      for(Player var2 : Bukkit.getOnlinePlayers()) {
         this.updateTabList(var2);
      }

   }

   public void setup() {
      this.D();

      for(Player var2 : Bukkit.getOnlinePlayers()) {
         this.A(var2);
         this.C(var2);
      }

   }

   public void shutdown() {
      for(ScheduledTask var2 : this.Q.values()) {
         var2.cancel();
      }

      this.Q.clear();
      this.C.clear();
      this.G.clear();
      this.E.clear();
      PacketEvents.getAPI().getEventManager().unregisterListener(this.S);
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent var1) {
      Player var2 = var1.getPlayer();
      this.L.put(var2.getUniqueId(), var2.getName());
      this.D();
      this.A(var2);
      this.C(var2);
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      Player var2 = var1.getPlayer();
      this.F(var2);
      UUID var3 = var2.getUniqueId();
      this.C.remove(var3);
      this.G.remove(var3);
      this.L.remove(var3);
      this.E.remove(var3);
      this.J.remove(var3);
      this.D();
   }

   private void C(Player var1) {
      this.F(var1);
      long var2 = (long)Math.floorMod(var1.getUniqueId().hashCode(), 40) + 1L;
      ScheduledTask var4 = var1.getScheduler().runAtFixedRate(this.I, (var2x) -> this.updateTabList(var1), (Runnable)null, var2, 40L);
      this.Q.put(var1.getUniqueId(), var4);
   }

   private void F(Player var1) {
      ScheduledTask var2 = (ScheduledTask)this.Q.remove(var1.getUniqueId());
      if (var2 != null) {
         var2.cancel();
      }

   }

   private void A(Player var1) {
      if (this.U.getBoolean("TAB.ENABLED", true)) {
         this.B(var1);
      }
   }

   public void updateTabList(Player var1) {
      if (this.U.getBoolean("TAB.ENABLED", true)) {
         this.B(var1);
      }
   }

   private void B(Player var1) {
      List var2 = this.U.getStringList("TAB.TITLE.header");
      List var3 = this.U.getStringList("TAB.TITLE.footer");
      if (!var2.isEmpty() || !var3.isEmpty()) {
         StringBuilder var4 = new StringBuilder();

         for(int var5 = 0; var5 < var2.size(); ++var5) {
            if (var5 > 0) {
               var4.append("\n");
            }

            var4.append(this.A(var1, (String)var2.get(var5)));
         }

         StringBuilder var9 = new StringBuilder();

         for(int var6 = 0; var6 < var3.size(); ++var6) {
            if (var6 > 0) {
               var9.append("\n");
            }

            var9.append(this.A(var1, (String)var3.get(var6)));
         }

         String var10000 = String.valueOf(var1.getUniqueId());
         String var10 = var10000 + ":" + String.valueOf(var4) + ":" + String.valueOf(var9);
         if (!((String)this.C.getOrDefault(var1.getUniqueId(), "")).equals(var10)) {
            TextComponent var7 = LegacyComponentSerializer.legacySection().deserialize(this.B(var4.toString()));
            TextComponent var8 = LegacyComponentSerializer.legacySection().deserialize(this.B(var9.toString()));
            var1.sendPlayerListHeaderAndFooter(var7, var8);
            this.C.put(var1.getUniqueId(), var10);
         }
      }
   }

   private void D() {
      ArrayList var1 = new ArrayList(Bukkit.getOnlinePlayers());
      if (this.N) {
         var1.sort(this::A);
      } else {
         var1.sort(Comparator.comparing((var1x) -> this.A(var1x.getName()), String.CASE_INSENSITIVE_ORDER));
      }

      this.M.clear();
      this.M.addAll(var1);
   }

   private String D(Player var1) {
      long var2 = System.currentTimeMillis();
      UUID var4 = var1.getUniqueId();
      String var5 = (String)this.E.get(var4);
      Long var6 = (Long)this.J.get(var4);
      if (var5 != null && var6 != null && var2 - var6 < 30000L) {
         return var5;
      } else {
         String var7 = "";
         if (this.I.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
               var7 = PlaceholderAPI.setPlaceholders(var1, "%luckperms_prefix%");
               if (var7 == null || var7.equals("%luckperms_prefix%")) {
                  var7 = "";
               }
            } catch (Exception var9) {
            }
         }

         var7 = this.B(var7);
         this.E.put(var4, var7);
         this.J.put(var4, var2);
         return var7;
      }
   }

   private int G(Player var1) {
      return !this.N ? 0 : (Integer)this.F.getOrDefault("default", 0);
   }

   private int A(Player var1, Player var2) {
      int var3 = this.G(var1);
      int var4 = this.G(var2);
      int var5 = Integer.compare(var4, var3);
      return var5 != 0 ? var5 : this.A(var1.getName()).compareToIgnoreCase(this.A(var2.getName()));
   }

   public void refreshTabListSorting() {
      if (this.U.getBoolean("TAB.ENABLED", true)) {
         this.D();

         for(Player var2 : Bukkit.getOnlinePlayers()) {
            this.updateTabList(var2);
         }

      }
   }

   public Map<String, Integer> getGroupRankings() {
      return new HashMap(this.F);
   }

   public void setGroupRanking(String var1, int var2) {
      this.F.put(var1.toLowerCase(), var2);
      this.refreshTabListSorting();
   }

   public boolean isGroupSortingEnabled() {
      return this.N;
   }

   public void setGroupSortingEnabled(boolean var1) {
      this.N = var1;
      this.refreshTabListSorting();
   }

   private void A(PacketSendEvent var1) {
      if (var1 instanceof PacketPlaySendEvent var2) {
         if (var2.getPacketType() == Server.PLAYER_INFO) {
            Player var3 = (Player)var2.getPlayer();
            if (var3 != null) {
               WrapperPlayServerPlayerInfo var4 = new WrapperPlayServerPlayerInfo(var2);
               var4.read();
               WrapperPlayServerPlayerInfo.Action var5 = var4.getAction();
               if (var5 != null) {
                  ArrayList var6 = new ArrayList(var4.getPlayerDataList());
                  UUID var7 = var3.getUniqueId();
                  LinkedHashSet var8 = (LinkedHashSet)this.G.computeIfAbsent(var7, (var0) -> new LinkedHashSet());
                  boolean var9;
                  synchronized(var8) {
                     switch (var5) {
                        case ADD_PLAYER -> var9 = this.A(var6, var8, var7);
                        case REMOVE_PLAYER -> var9 = this.B(var6, var8);
                        default -> var9 = this.A((List)var6, (LinkedHashSet)var8);
                     }
                  }

                  if (var9) {
                     for(WrapperPlayServerPlayerInfo.PlayerData var11 : var6) {
                        UUID var12 = this.A(var11);
                        if (var12 != null) {
                           Player var13 = Bukkit.getPlayer(var12);
                           if (var13 != null) {
                              String var14 = this.D(var13);
                              PlayerData var15 = this.I.getPlayerDataManager().get(var12);
                              boolean var16 = var15 != null && var15.isDisguised() && (var7.equals(var12) || !var3.hasPermission("economysmpcore.disguise.see"));
                              String var17;
                              if (var16 && var15.getDisguiseName() != null) {
                                 String var18 = var15.getDisguiseName();
                                 String var20 = var14.isEmpty() ? "" : var14;
                                 var17 = var20 + this.A(var18);
                              } else {
                                 String var10000 = var14.isEmpty() ? "" : var14;
                                 var17 = var10000 + this.A(var13.getName());
                              }

                              var11.setDisplayName(LegacyComponentSerializer.legacySection().deserialize(var17));
                           }
                        }
                     }

                     var6.sort((var1x, var2x) -> {
                        UUID var3 = this.A(var1x);
                        UUID var4 = this.A(var2x);
                        if (var3 != null && var4 != null) {
                           Player var5 = Bukkit.getPlayer(var3);
                           Player var6 = Bukkit.getPlayer(var4);
                           int var7 = var5 != null ? this.G(var5) : (Integer)this.F.getOrDefault("default", 0);
                           int var8 = var6 != null ? this.G(var6) : (Integer)this.F.getOrDefault("default", 0);
                           int var9 = Integer.compare(var8, var7);
                           if (var9 != 0) {
                              return var9;
                           } else {
                              String var10 = var5 != null ? this.A(var5.getName()) : (String)this.L.getOrDefault(var3, "");
                              String var11 = var6 != null ? this.A(var6.getName()) : (String)this.L.getOrDefault(var4, "");
                              return var10.compareToIgnoreCase(var11);
                           }
                        } else {
                           return 0;
                        }
                     });
                     var4.setPlayerDataList(var6);
                     var4.write();
                     var1.setLastUsedWrapper(var4);
                  }

               }
            }
         }
      }
   }

   private boolean A(List<WrapperPlayServerPlayerInfo.PlayerData> var1, LinkedHashSet<UUID> var2, UUID var3) {
      boolean var4 = false;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         WrapperPlayServerPlayerInfo.PlayerData var6 = (WrapperPlayServerPlayerInfo.PlayerData)var5.next();
         UUID var7 = this.A(var6);
         if (var7 != null) {
            if (var7.equals(var3)) {
               var2.add(var7);
            } else if (!var2.contains(var7)) {
               if (var2.size() >= this.D) {
                  var5.remove();
                  var4 = true;
               } else {
                  var2.add(var7);
               }
            }
         }
      }

      return var4;
   }

   private boolean B(List<WrapperPlayServerPlayerInfo.PlayerData> var1, LinkedHashSet<UUID> var2) {
      boolean var3 = false;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         WrapperPlayServerPlayerInfo.PlayerData var5 = (WrapperPlayServerPlayerInfo.PlayerData)var4.next();
         UUID var6 = this.A(var5);
         if (var6 != null && !var2.remove(var6)) {
            var4.remove();
            var3 = true;
         }
      }

      return var3;
   }

   private boolean A(List<WrapperPlayServerPlayerInfo.PlayerData> var1, LinkedHashSet<UUID> var2) {
      boolean var3 = false;
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         WrapperPlayServerPlayerInfo.PlayerData var5 = (WrapperPlayServerPlayerInfo.PlayerData)var4.next();
         UUID var6 = this.A(var5);
         if (var6 == null || !var2.contains(var6)) {
            var4.remove();
            var3 = true;
         }
      }

      return var3;
   }

   private UUID A(WrapperPlayServerPlayerInfo.PlayerData var1) {
      UserProfile var2 = var1.getUserProfile();
      return var2 != null ? var2.getUUID() : null;
   }

   private String A(Player var1, String var2) {
      if (var2 != null && !var2.isEmpty()) {
         var2 = var2.replace("{ping}", String.valueOf(var1.getPing())).replace("{tps}", String.format("%.2f", this.G())).replace("{mspt}", String.format("%.2f", this.E())).replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()));
         if (this.I.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            var2 = PlaceholderAPI.setPlaceholders(var1, var2);
         }

         return this.B(var2);
      } else {
         return "";
      }
   }

   private double G() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.B < 1000L) {
         return this.P;
      } else {
         this.P = this.F();
         this.B = var1;
         return this.P;
      }
   }

   private double E() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.H < 1000L) {
         return this.K;
      } else {
         this.K = this.A();
         this.H = var1;
         return this.K;
      }
   }

   private double F() {
      try {
         org.bukkit.Server var1 = Bukkit.getServer();
         Method var2 = var1.getClass().getMethod("recentTps");
         Object var3 = var2.invoke(var1);
         if (var3 instanceof double[] var4) {
            if (var4.length > 0) {
               return Math.min(var4[0], (double)20.0F);
            }
         }
      } catch (Exception var5) {
      }

      return (double)20.0F;
   }

   private double A() {
      try {
         org.bukkit.Server var1 = Bukkit.getServer();
         Method var2 = var1.getClass().getMethod("getAverageTickTime");
         Object var3 = var2.invoke(var1);
         if (var3 instanceof double[] var4) {
            if (var4.length > 0) {
               return Math.max((double)0.0F, Math.min(var4[0], (double)100.0F));
            }
         }
      } catch (Exception var5) {
      }

      return (double)50.0F;
   }

   public String getRealPlayerName(Player var1) {
      return (String)this.L.getOrDefault(var1.getUniqueId(), var1.getName());
   }

   private String A(String var1) {
      return var1 == null ? "" : var1.replaceAll("§[0-9a-fk-or]", "");
   }

   private String B(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         Matcher var2 = O.matcher(var1);
         StringBuffer var3 = new StringBuffer();

         while(var2.find()) {
            var2.appendReplacement(var3, ChatColor.of("#" + var2.group(1)).toString());
         }

         return org.bukkit.ChatColor.translateAlternateColorCodes('&', var2.appendTail(var3).toString());
      } else {
         return "";
      }
   }
}
