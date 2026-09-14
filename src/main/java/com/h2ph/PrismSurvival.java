package com.h2ph;

import com.h2ph.J.B.A.B;
import com.h2ph.J.B.A.G;
import com.h2ph.J.C.C;
import com.h2ph.J.D.A;
import com.h2ph.J.D.H;
import com.h2ph.J.D.J;
import com.h2ph.J.D.L;
import com.h2ph.J.D.N;
import com.h2ph.J.D.P;
import com.h2ph.J.D.Q;
import com.h2ph.J.D.R;
import com.h2ph.J.D.S;
import com.h2ph.J.D.T;
import com.h2ph.J.D.U;
import com.h2ph.T.A.E;
import com.h2ph.T.A.I;
import com.h2ph.T.A.K;
import com.h2ph._.D;
import com.h2ph.c.F;
import com.prismcore.survival.auction.AuctionController;
import com.prismcore.survival.bounty.BountyListener;
import com.prismcore.survival.bounty.BountyManager;
import com.prismcore.survival.manager.CarouselManager;
import com.prismcore.survival.manager.CrateEffectsManager;
import com.prismcore.survival.manager.CrateLocationRegistry;
import com.prismcore.survival.manager.KeyAllManager;
import com.prismcore.survival.manager.PlayerDataManager;
import com.prismcore.survival.manager.SpawnManager;
import com.prismcore.survival.manager.TeleportManager;
import com.prismcore.survival.manager.TeleportQueueManager;
import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.scheduler.SchedulerAdapter;
import com.prismcore.survival.shards.ShardsManager;
import com.prismcore.survival.survival.ChatFilter;
import com.prismcore.survival.survival.ItemMerger;
import com.prismcore.survival.survival.MessageHider;
import com.prismcore.survival.tools.ToolCommand;
import com.prismcore.survival.tools.ToolsManager;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class PrismSurvival extends JavaPlugin {
   private PlayerDataManager m;
   private SchedulerAdapter q;
   private C E;
   private A N;
   private com.h2ph.J.D.C o;
   private P v;
   private static PrismSurvival y;
   private com.h2ph.C.A ¥;
   private FileConfiguration Â;
   private com.h2ph.J.B.F.A z;
   private com.h2ph.U.A f;
   private KeyAllManager J;
   private CarouselManager a;
   private CrateLocationRegistry A;
   private CrateEffectsManager e;
   private SpawnManager Q;
   private TeleportManager U;
   private TeleportQueueManager l;
   private ShardsManager O;
   private AuctionController t;
   private com.h2ph.Z.A w;
   private com.h2ph.B.A Á;
   private com.h2ph.J.B.A.C £;
   private B L;
   private G p;
   private com.h2ph.D.B T;
   private com.h2ph.T.B R;
   private com.h2ph.T.C n;
   private com.h2ph.T.A h;
   private com.h2ph.T.A.C r;
   private com.h2ph.T.A.B G;
   private K g;
   private com.h2ph.e.A Y;
   private PrismOrders H;
   private com.h2ph.P.A M;
   private com.h2ph.I.A d;
   private com.h2ph.S.A x;
   private com.h2ph.R.A c;
   private com.h2ph.a.B W;
   private com.h2ph.F.A S;
   private D i;
   private com.h2ph.A.A º;
   private com.h2ph.A.B u;
   private L s;
   private com.h2ph.Y.A C;
   private com.h2ph.O.A ¤;
   private com.h2ph.H.A ¢;
   private com.h2ph.G.A µ;
   private com.h2ph.g.A B;
   private com.h2ph.d.A V;
   private F j;
   private com.h2ph.Q.A X;
   private com.h2ph.X.A _;
   private com.h2ph.W.F ª;
   private final Set<UUID> Z = new HashSet();
   private List<String> F = new ArrayList();
   private long b = 0L;
   private final Set<UUID> P = new HashSet();
   private List<String> À = new ArrayList();
   private FileConfiguration k;
   private FileConfiguration K;
   private FileConfiguration I;
   private com.h2ph.b.B D;

   public static PrismSurvival getInstance() {
      return y;
   }

   public void onLoad() {
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
   }

   public void onEnable() {
      y = this;
      this.saveDefaultConfig();
      this.C();
      if (!this.D()) {
         this.getLogger().severe("==========================================");
         this.getLogger().severe("INVALID LICENSE KEY!");
         this.getLogger().severe("Plugin disabled.");
         this.getLogger().severe("==========================================");
         this.getServer().getPluginManager().disablePlugin(this);
      } else {
         this.B();
         this.R = new com.h2ph.T.B(this);
         this.r = new I(this.R);
         this.G = new com.h2ph.T.A.G(this.R);
         this.g = new E(this.R);
         this.loadSurvivalConfig();
         this.ª = new com.h2ph.W.F(this);
         this.getServer().getPluginManager().registerEvents(this.ª, this);
         new MessageHider(this);
         this.getServer().getPluginManager().registerEvents(new ItemMerger(this), this);
         this.getServer().getPluginManager().registerEvents(new ChatFilter(this), this);
         this.loadUpdateFromConfig();
         this.getCommand("update").setExecutor(new com.h2ph.J.B.E.A(this));
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.C(this), this);
         this.loadAdvisorFromConfig();
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.I(), this);
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.L(this), this);
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.E(this), this);
         this.m = new PlayerDataManager(this);
         this.q = new SchedulerAdapter(this);
         this.n = new com.h2ph.T.C(this);
         this.n.C();
         com.h2ph.E.A.B();
         if (this.getConfig().getBoolean("modules.report-system", true)) {
            this.w = new com.h2ph.Z.A(this);
            this.getLogger().info("ReportManager loaded.");
         } else {
            this.getLogger().info("ReportManager disabled by config.");
         }

         this.J = new KeyAllManager(this);
         this.a = new CarouselManager(this);
         this.A = new CrateLocationRegistry(this);
         this.e = new CrateEffectsManager(this);
         this.Q = new SpawnManager(this);
         this.U = new TeleportManager(this);
         this.l = new TeleportQueueManager(this);
         if (this.getConfig().getBoolean("modules.spawners-system", true)) {
            this.Á = new com.h2ph.B.A(this);
            this.getLogger().info("DonutSpawnersModule loaded.");
         } else {
            this.getLogger().info("DonutSpawnersModule disabled by config.");
         }

         if (this.getConfig().getBoolean("modules.toggle-system", true)) {
            this.C = new com.h2ph.Y.A(this);
            this.getLogger().info("DonutToggleManager loaded.");
         } else {
            this.getLogger().info("DonutToggleManager disabled by config.");
         }

         if (this.getConfig().getBoolean("modules.old-spawn-system", true)) {
            this.X = new com.h2ph.Q.A(this);
            this.getLogger().info("Old SpawnManager loaded.");
         } else {
            this.getLogger().info("Old SpawnManager disabled by config.");
         }

         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.E(this), this);
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.G(this), this);
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.K(this), this);
         if (this.getConfig().getBoolean("modules.shop-system", true)) {
            this.E = new C(this);
            this.getCommand("shop").setExecutor(this.E);
            this.getServer().getPluginManager().registerEvents(this.E, this);
            this.getLogger().info("ShopCommand loaded.");
         } else {
            this.B("shop");
            this.getLogger().info("ShopCommand disabled by config (freed /shop for other plugins).");
         }

         this.z = new com.h2ph.J.B.F.A(this, this.r);
         new ToolsManager(this);
         if (this.getConfig().getBoolean("modules.teleport-system", true)) {
            com.h2ph.N.A var1 = new com.h2ph.N.A(this);
            this.getCommand("tpa").setExecutor(var1);
            this.getCommand("tpa").setTabCompleter(var1);
            this.getCommand("tpahere").setExecutor(var1);
            this.getCommand("tpahere").setTabCompleter(var1);
            this.getCommand("tpaccept").setExecutor(var1);
            this.getCommand("tpaccept").setTabCompleter(var1);
            this.getCommand("tpacancel").setExecutor(var1);
            this.getCommand("tpacancel").setTabCompleter(var1);
            this.getCommand("tpatoggle").setExecutor(var1);
            this.getCommand("tpaheretoggle").setExecutor(var1);
            this.getCommand("tpauto").setExecutor(var1);
            this.getLogger().info("NewTeleportSystem loaded.");
         } else {
            this.B("tpa");
            this.B("tpahere");
            this.B("tpaccept");
            this.B("tpacancel");
            this.B("tpatoggle");
            this.B("tpaheretoggle");
            this.B("tpauto");
            this.getLogger().info("NewTeleportSystem disabled by config (freed /tpa and related commands for other plugins).");
         }

         this.D = new com.h2ph.b.B(this);
         this.getServer().getPluginManager().registerEvents(this.D, this);
         if (this.getConfig().getBoolean("modules.tools-system", true)) {
            this.getCommand("tools").setExecutor(new ToolCommand(ToolsManager.getInstance()));
            this.getCommand("tools").setTabCompleter(new ToolCommand(ToolsManager.getInstance()));
            this.getLogger().info("ToolCommand loaded.");
         } else {
            this.B("tools");
            this.getLogger().info("ToolCommand disabled by config (freed /tools for other plugins).");
         }

         com.h2ph.J.B.B.D var23 = new com.h2ph.J.B.B.D(this);
         this.getCommand("shards").setExecutor(var23);
         this.getCommand("shards").setTabCompleter(var23);
         com.h2ph.J.B.D.B var2 = new com.h2ph.J.B.D.B(this);
         this.getCommand("crate").setExecutor(var2);
         this.getCommand("crate").setTabCompleter(var2);
         com.h2ph.J.B.D.A var3 = new com.h2ph.J.B.D.A(this);
         this.getCommand("key").setExecutor(var3);
         this.getCommand("key").setTabCompleter(var3);
         this.getCommand("keyall").setExecutor(var3);
         this.getCommand("keyall").setTabCompleter(var3);
         com.h2ph.J.B.B.B var4 = new com.h2ph.J.B.B.B(this);
         this.getCommand("billford").setExecutor(var4);
         this.getCommand("billford").setTabCompleter(var4);
         this.getServer().getPluginManager().registerEvents(var4, this);
         com.h2ph.J.B.B.C var5 = new com.h2ph.J.B.B.C(this);
         this.getCommand("baltop").setExecutor(var5);
         this.getServer().getPluginManager().registerEvents(var5, this);
         if (this.getConfig().getBoolean("modules.bounty-system", true)) {
            BountyManager var6 = new BountyManager(this, this.g);
            J var7 = new J(this, var6);
            this.getCommand("bounty").setExecutor(var7);
            this.getCommand("bounty").setTabCompleter((var0, var1x, var2x, var3x) -> var3x.length == 1 ? null : Collections.emptyList());
            this.getServer().getPluginManager().registerEvents(var7, this);
            this.getServer().getPluginManager().registerEvents(new BountyListener(this, var6), this);
            this.getLogger().info("BountyCommand loaded.");
         } else {
            this.B("bounty");
            this.getLogger().info("BountyCommand disabled by config (freed /bounty for other plugins).");
         }

         com.h2ph.J.C.B var24 = new com.h2ph.J.C.B(this);
         this.getCommand("balance").setExecutor(var24);
         this.getCommand("balance").setTabCompleter(var24);
         if (this.getConfig().getBoolean("modules.pay-system", true)) {
            com.h2ph.J.C.A var25 = new com.h2ph.J.C.A(this);
            this.getCommand("pay").setExecutor(var25);
            this.getCommand("pay").setTabCompleter(var25);
            this.getLogger().info("PayCommand loaded.");
         } else {
            this.B("pay");
            this.getLogger().info("PayCommand disabled by config (freed /pay for other plugins).");
         }

         if (this.getConfig().getBoolean("modules.worth-system", true)) {
            this.getCommand("worth").setExecutor(new H(this));
            this.getLogger().info("WorthCommand loaded.");
         } else {
            this.B("worth");
            this.getLogger().info("WorthCommand disabled by config (freed /worth for other plugins).");
         }

         this.getCommand("profile").setExecutor(new com.h2ph.J.A.B(this));
         this.getServer().getPluginManager().registerEvents(new com.h2ph.J.A.E(this), this);
         com.h2ph.J.A.F var26 = new com.h2ph.J.A.F(this);
         this.getCommand("invsee").setExecutor(var26);
         this.getCommand("invsee").setTabCompleter(var26);
         if (this.getConfig().getBoolean("modules.chainmail-system", true)) {
            new com.h2ph.M.A(this);
            this.getLogger().info("ChainmailManager loaded.");
         } else {
            this.getLogger().info("ChainmailManager disabled by config.");
         }

         if (this.getConfig().getBoolean("modules.spawn-system", true)) {
            this.¤ = new com.h2ph.O.A(this);
            this.getLogger().info("DonutSpawn (new spawn system) loaded.");
         } else {
            this.getLogger().info("DonutSpawn disabled by config.");
         }

         if (this.getConfig().getBoolean("modules.team-system", true)) {
            this.¢ = new com.h2ph.H.A(this);
            this.getLogger().info("DonutTeamModule loaded.");
         } else {
            this.getLogger().info("DonutTeamModule disabled by config.");
         }

         if (this.getConfig().getBoolean("modules.donutworth-system", true)) {
            this.µ = new com.h2ph.G.A(this);
            this.getLogger().info("DonutWorthManager loaded.");
         } else {
            this.getLogger().info("DonutWorthManager disabled by config.");
         }

         this.B = new com.h2ph.g.A(this);
         this.getLogger().info("SpawnStashManager loaded.");
         this.V = new com.h2ph.d.A(this);
         this.getLogger().info("DonutStatsManager loaded.");
         this.j = new F(this);
         this.j.D();
         if (this.getConfig().getBoolean("modules.tablist-system", true)) {
            this._ = new com.h2ph.X.A(this);
            this.getLogger().info("TabListManager loaded.");
         } else {
            this.getLogger().info("TabListManager disabled by config.");
         }

         new com.h2ph.J.B.C(this);
         new U();
         this.getCommand("anvil").setExecutor(new U());
         new S(this);
         new com.h2ph.J.D.F();
         this.getCommand("craftingtable").setExecutor(new com.h2ph.J.D.F());
         new com.h2ph.J.D.I(this);
         new N(this);
         new com.h2ph.J.D.B(this);
         this.s = new L(this);
         new Q();
         if (this._ != null) {
            new com.h2ph.J.B.B(this, this._);
         }

         File var8 = new File(this.getDataFolder(), "economy/config.yml");
         YamlConfiguration var9 = YamlConfiguration.loadConfiguration(var8);
         boolean var10 = ((FileConfiguration)var9).getBoolean("vault-enabled", true);
         if (var10) {
            com.h2ph.J.B.B.A var11 = new com.h2ph.J.B.B.A(this);
            this.getCommand("economy").setExecutor(var11);
            this.getCommand("economy").setTabCompleter(var11);
         } else {
            this.getLogger().info("Economy Command disabled in config (vault-enabled: false).");
            this.getCommand("economy").setExecutor((var0, var1x, var2x, var3x) -> {
               var0.sendMessage(String.valueOf(ChatColor.RED) + "PrismEconomy is currently disabled.");
               return true;
            });
            this.getCommand("economy").setTabCompleter((var0, var1x, var2x, var3x) -> Collections.emptyList());
         }

         this.¥ = new com.h2ph.C.A(this);
         this.¥.H();
         this.O = new ShardsManager(this);
         if (!this.getConfig().getBoolean("modules.afk-system", true)) {
            this.B("afk");
            this.B("setafk");
            this.getLogger().info("AFK system disabled by config (freed /afk, /setafk for other plugins).");
         } else if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null && this.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") == null) {
            this.getLogger().warning("WorldEdit/FAWE not found! /setafk and /afk commands disabled.");
         } else {
            this.getCommand("setafk").setExecutor(new com.h2ph.J.B.C.A(this));
            com.h2ph.J.D.A.A var27 = new com.h2ph.J.D.A.A(this);
            this.getCommand("afk").setExecutor(var27);
            this.getCommand("afk").setTabCompleter(var27);
            this.getServer().getPluginManager().registerEvents(var27, this);
            this.getLogger().info("WorldEdit found! AFK region features enabled.");
         }

         com.h2ph.J.B.F.C var28 = new com.h2ph.J.B.F.C(this);
         this.getCommand("sus").setExecutor(var28);
         this.getServer().getPluginManager().registerEvents(var28, this);
         com.h2ph.J.B.F.D var12 = new com.h2ph.J.B.F.D(this);
         this.getCommand("gmsp").setExecutor(var12);
         this.getServer().getPluginManager().registerEvents(var12, this);
         this.£ = new com.h2ph.J.B.A.C(this);
         this.L = new B(this, this.£);
         if (!this.getConfig().getBoolean("modules.duel-system", true)) {
            this.B("duel");
            this.getLogger().info("Duel system disabled by config (freed /duel for other plugins).");
         } else if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null && this.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") == null) {
            this.getLogger().warning("WorldEdit/FAWE not found! /duel command disabled.");
         } else {
            this.p = new G(this, this.L);
            this.getCommand("duel").setExecutor(this.p);
            this.getCommand("duel").setTabCompleter(this.p);
            this.getServer().getPluginManager().registerEvents(new com.h2ph.W.J(), this);
            this.getServer().getPluginManager().registerEvents(new com.h2ph.W.B(this.L), this);
         }

         com.h2ph.J.D.K var13 = new com.h2ph.J.D.K();
         this.getCommand("rtp").setExecutor(var13);
         this.getCommand("rtp").setTabCompleter(var13);
         this.N = new A(this);
         this.getCommand("rules").setExecutor(this.N);
         this.getServer().getPluginManager().registerEvents(this.N, this);
         this.v = new P(this);
         this.getCommand("help").setExecutor(this.v);
         this.getServer().getPluginManager().registerEvents(this.v, this);
         if (this.getConfig().getBoolean("modules.media-system", true)) {
            T var14 = new T(this);
            this.getCommand("media").setExecutor(var14);
            this.getServer().getPluginManager().registerEvents(var14, this);
            this.getLogger().info("MediaCommand loaded.");
         } else {
            this.B("media");
            this.getLogger().info("MediaCommand disabled by config (freed /media for other plugins).");
         }

         R var29 = new R(this);
         this.getCommand("advisor").setExecutor(var29);
         this.getCommand("advisor").setTabCompleter(var29);
         this.getServer().getPluginManager().registerEvents(new com.h2ph.W.D(this), this);
         if (this.getConfig().getBoolean("modules.home-system", true)) {
            com.h2ph.J.D.E var15 = new com.h2ph.J.D.E(this);
            this.getCommand("home").setExecutor(var15);
            this.getCommand("home").setTabCompleter(var15);
            this.getLogger().info("HomeCommand loaded.");
         } else {
            this.B("home");
            this.getLogger().info("HomeCommand disabled by config (freed /home for other plugins).");
         }

         if (this.getConfig().getBoolean("modules.sell-system", true)) {
            com.h2ph.J.C.D var30 = new com.h2ph.J.C.D(this);
            PluginCommand var10000 = this.getCommand("sell");
            Objects.requireNonNull(var30);
            var10000.setExecutor(var30.new _B());
            var10000 = this.getCommand("sellmulti");
            Objects.requireNonNull(var30);
            var10000.setExecutor(var30.new _G());
            var10000 = this.getCommand("sellhistory");
            Objects.requireNonNull(var30);
            var10000.setExecutor(var30.new _C());
            this.getServer().getPluginManager().registerEvents(var30, this);
            this.getLogger().info("SellCommand system loaded.");
         } else {
            this.B("sell");
            this.B("sellmulti");
            this.B("sellhistory");
            this.getLogger().info("SellCommand disabled by config (freed /sell, /sellmulti, /sellhistory for other plugins).");
         }

         if (this.getConfig().getBoolean("modules.warp-system", true)) {
            this.o = new com.h2ph.J.D.C(this);
            this.getCommand("warp").setExecutor(this.o);
            this.getCommand("warp").setTabCompleter(this.o);
            this.getCommand("setwarp").setExecutor(this.o);
            this.getCommand("delwarp").setExecutor(this.o);
            this.getLogger().info("WarpCommand loaded.");
         } else {
            this.B("warp");
            this.B("setwarp");
            this.B("delwarp");
            this.getLogger().info("WarpCommand disabled by config (freed /warp, /setwarp, /delwarp for other plugins).");
         }

         this.H = new PrismOrders(this);
         this.H.init();
         this.M = new com.h2ph.P.A(this);
         this.d = new com.h2ph.I.A(this);
         this.x = new com.h2ph.S.A(this);
         if (this.getConfig().getBoolean("modules.chathover-system", true)) {
            this.c = new com.h2ph.R.A(this);
            this.getLogger().info("ChatHoverManager loaded.");
         } else {
            this.getLogger().info("ChatHoverManager disabled by config.");
         }

         this.W = new com.h2ph.a.B(this);
         com.h2ph.a.A var31 = new com.h2ph.a.A(this, this.W);
         this.getCommand("settings").setExecutor(var31);
         this.getCommand("settings").setTabCompleter((var0, var1x, var2x, var3x) -> Collections.emptyList());
         this.S = new com.h2ph.F.A(this);
         com.h2ph.F.B var16 = new com.h2ph.F.B(this);
         this.getCommand("clearlag").setExecutor(var16);
         this.getLogger().info("ClearLagManager loaded.");
         this.i = new D(this);
         com.h2ph._.C var17 = new com.h2ph._.C(this);
         com.h2ph._.B var18 = new com.h2ph._.B(this, var17);
         this.getCommand("disguise").setExecutor(var18);
         this.getCommand("undisguise").setExecutor(var18);
         this.getServer().getPluginManager().registerEvents(new com.h2ph._.A(this), this);
         this.getLogger().info("DisguiseManager loaded.");
         this.º = new com.h2ph.A.A(this);
         com.h2ph.A.C var19 = new com.h2ph.A.C(this, this.º);
         this.getCommand("msg").setExecutor(var19);
         this.getCommand("msg").setTabCompleter(var19);
         this.getCommand("message").setExecutor(var19);
         this.getCommand("message").setTabCompleter(var19);
         this.getCommand("whisper").setExecutor(var19);
         this.getCommand("whisper").setTabCompleter(var19);
         this.getCommand("tell").setExecutor(var19);
         this.getCommand("tell").setTabCompleter(var19);
         this.getCommand("dm").setExecutor(var19);
         this.getCommand("dm").setTabCompleter(var19);
         this.getCommand("w").setExecutor(var19);
         this.getCommand("w").setTabCompleter(var19);
         this.getCommand("reply").setExecutor(var19);
         this.getCommand("reply").setTabCompleter(var19);
         this.getCommand("r").setExecutor(var19);
         this.getCommand("r").setTabCompleter(var19);
         this.getCommand("block").setExecutor(var19);
         this.getCommand("block").setTabCompleter(var19);
         this.getCommand("ignore").setExecutor(var19);
         this.getCommand("ignore").setTabCompleter(var19);
         this.getCommand("unblock").setExecutor(var19);
         this.getCommand("unblock").setTabCompleter(var19);
         this.getCommand("unignore").setExecutor(var19);
         this.getCommand("unignore").setTabCompleter(var19);
         this.getCommand("msgtoggle").setExecutor(var19);
         this.u = new com.h2ph.A.B(this);
         this.getCommand("chat").setExecutor(this.u);
         if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            (new com.h2ph.K.A(this)).register();
            (new com.h2ph.K.A(this, "economysmpcore")).register();
            this.getLogger().info("PlaceholderAPI expansions registered (donutcore + economysmpcore)!");
         } else {
            this.getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
         }

         if (this.getServer().getPluginManager().getPlugin("Vault") != null) {
            if (var10) {
               com.h2ph.L.A var20 = new com.h2ph.L.A(this);
               this.getServer().getServicesManager().register(Economy.class, var20, this, ServicePriority.Highest);
               this.getLogger().info("Registered PrismEconomy as Vault provider!");
            } else {
               this.getLogger().info("PrismEconomy Vault hook disabled in config.");
            }
         } else {
            this.getLogger().warning("Vault not found! Economy features will be disabled.");
         }

         this.T = new com.h2ph.D.B(this);
         this.getCommand("maintenance").setExecutor(new com.h2ph.D.C(this.T));
         this.getServer().getPluginManager().registerEvents(new com.h2ph.D.A(this.T), this);
         this.f = new com.h2ph.U.A(this);
         this.f.C();
         this.h = new com.h2ph.T.A(this, this.R, this.m.getPlayerDAO(), this.£.A(), this.r, this.G);

         try {
            this.h.A();
         } catch (Exception var22) {
            this.getLogger().severe("Migration failed: " + var22.getMessage());
            var22.printStackTrace();
         }

         this.t = new AuctionController(this);
         this.t.enable();
         this.getCommand("donutreload").setExecutor(new com.h2ph.J.B.A(this));
         this.A(var10);

         try {
            Logger var32 = (Logger)LogManager.getRootLogger();
            var32.addFilter(new com.h2ph.f.A());
         } catch (Exception var21) {
            this.getLogger().warning("Failed to register Log4j Filter: " + var21.getMessage());
         }

         this.Y = new com.h2ph.e.A(this);
         this.Y.setup();
         this.getCommand("sb").setExecutor(this.Y);
      }
   }

   private void C() {
      File var1 = new File(this.getDataFolder(), "license.yml");
      if (!var1.exists()) {
         YamlConfiguration var2 = new YamlConfiguration();
         var2.set("license.key", "YOUR-LICENSE-KEY-HERE");
         var2.set("license.instructions", "Get your license key from the website");

         try {
            var2.save(var1);
            this.getLogger().info("Created license.yml - Please add your license key!");
         } catch (Exception var4) {
            this.getLogger().severe("Could not create license.yml: " + var4.getMessage());
         }
      }

   }

   private boolean D() {
      boolean var1 = Boolean.getBoolean("DonutCore.TestMode") || (new File(this.getDataFolder(), "testmode")).exists() || "TestServer".equals(System.getProperty("DonutCore.LicenseKey"));
      if (var1) {
         this.getLogger().info("==========================================");
         this.getLogger().info("TEST MODE ACTIVE – License check bypassed.");
         this.getLogger().info("==========================================");
         return true;
      } else {
         File var2 = new File(this.getDataFolder(), "license.yml");
         if (!var2.exists()) {
            return false;
         } else {
            YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
            String var4 = var3.getString("license.key");
            if (var4 != null && !var4.isEmpty() && !var4.equals("YOUR-LICENSE-KEY-HERE")) {
               try {
                  URL var5 = new URL("https://ukjepjnlxjbyhcppemle.supabase.co/functions/v1/validate-license");
                  HttpURLConnection var6 = (HttpURLConnection)var5.openConnection();
                  var6.setRequestMethod("POST");
                  var6.setRequestProperty("Content-Type", "application/json");
                  var6.setDoOutput(true);
                  var6.setConnectTimeout(10000);
                  var6.setReadTimeout(10000);
                  String var7 = "{\"plugin_name\":\"DonutCore\",\"license_key\":\"" + var4 + "\"}";
                  OutputStream var8 = var6.getOutputStream();

                  try {
                     var8.write(var7.getBytes("UTF-8"));
                     var8.flush();
                  } catch (Throwable var14) {
                     if (var8 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var13) {
                           var14.addSuppressed(var13);
                        }
                     }

                     throw var14;
                  }

                  if (var8 != null) {
                     var8.close();
                  }

                  int var16 = var6.getResponseCode();
                  this.getLogger().info("License server response code: " + var16);
                  if (var16 != 200) {
                     this.getLogger().warning("License server returned: " + var16);
                     return false;
                  } else {
                     BufferedReader var9 = new BufferedReader(new InputStreamReader(var6.getInputStream()));
                     StringBuilder var10 = new StringBuilder();

                     String var11;
                     while((var11 = var9.readLine()) != null) {
                        var10.append(var11);
                     }

                     var9.close();
                     String var12 = var10.toString();
                     this.getLogger().info("License server response: " + var12);
                     return var12.contains("\"valid\":true") || var12.contains("\"valid\": true");
                  }
               } catch (Exception var15) {
                  this.getLogger().warning("License validation error: " + var15.getMessage());
                  return false;
               }
            } else {
               this.getLogger().warning("No valid license key found in license.yml!");
               return false;
            }
         }
      }
   }

   public void onDisable() {
      if (this.j != null) {
         this.j.E();
      }

      if (this.S != null) {
         this.S.J();
      }

      if (this.H != null) {
         this.H.shutdown();
      }

      if (this.Á != null) {
         this.Á.disable();
      }

      if (this.¢ != null) {
         this.¢.disable();
      }

      if (this.f != null) {
         this.f.A();
      }

      if (this.Y != null) {
         this.Y.shutdown();
      }

      if (this.t != null) {
         this.t.disable();
      }

      if (this.D != null) {
         this.D.cleanup();
      }

      if (this.m != null) {
         for(Player var2 : this.getServer().getOnlinePlayers()) {
            this.m.savePlayer(var2.getUniqueId());
         }
      }

      if (this.R != null) {
         this.R.C();
      }

      if (this._ != null) {
         this._.shutdown();
      }

      this.getLogger().info("DonutCore has been disabled!");
      y = null;
   }

   public PlayerDataManager getPlayerDataManager() {
      return this.m;
   }

   public SchedulerAdapter getSchedulerAdapter() {
      return this.q;
   }

   public com.h2ph.C.A getAfkManager() {
      return this.¥;
   }

   public ShardsManager getShardsManager() {
      return this.O;
   }

   public AuctionController getAuctionController() {
      return this.t;
   }

   public com.h2ph.D.B getMaintenanceManager() {
      return this.T;
   }

   public com.h2ph.T.B getDatabaseManager() {
      return this.R;
   }

   public com.h2ph.T.C getServerStatusManager() {
      return this.n;
   }

   public com.h2ph.T.A.C getOffendDAO() {
      return this.r;
   }

   public com.h2ph.T.A.B getAuctionDAO() {
      return this.G;
   }

   public com.h2ph.e.A getScoreboardManager() {
      return this.Y;
   }

   public PrismOrders getPrismOrders() {
      return this.H;
   }

   public com.h2ph.P.A getCombatLogManager() {
      return this.M;
   }

   public com.h2ph.I.A getEnderChestManager() {
      return this.d;
   }

   public com.h2ph.S.A getNightVisionManager() {
      return this.x;
   }

   public com.h2ph.R.A getChatHoverManager() {
      return this.c;
   }

   public com.h2ph.a.B getSettingsManager() {
      return this.W;
   }

   public com.h2ph.F.A getClearLagManager() {
      return this.S;
   }

   public D getDisguiseManager() {
      return this.i;
   }

   public com.h2ph.Y.A getDonutToggleManager() {
      return this.C;
   }

   public com.h2ph.A.A getPrivateMessageManager() {
      return this.º;
   }

   public com.h2ph.A.B getChatToggleManager() {
      return this.u;
   }

   public com.h2ph.X.A getTabListManager() {
      return this._;
   }

   public com.h2ph.W.F getCommandHideListener() {
      return this.ª;
   }

   public com.h2ph.H.A getDonutTeamModule() {
      return this.¢;
   }

   public com.h2ph.J.D.C getWarpCommand() {
      return this.o;
   }

   public L getQuickGameMode() {
      return this.s;
   }

   public String normalizeKeyName(String var1) {
      return var1.toLowerCase().replace(" ", "_");
   }

   private void B() {
      this.A("economy/shop/config.yml");
      this.A("economy/shop/categories/end.yml");
      this.A("economy/shop/categories/food.yml");
      this.A("economy/shop/categories/gear.yml");
      this.A("economy/shop/categories/nether.yml");
      this.A("economy/shop/categories/redstone.yml");
      this.A("economy/shop/categories/shard.yml");
      this.A("survival/AFK/config.yml");
      this.A("economy/config.yml");
      this.A("rtp/asia/config.yml");
      this.A("rtp/europe/config.yml");
      this.A("rtp/east/config.yml");
      this.A("rtp/config.yml");
      this.A("crates/keys/config.yml");
      this.A("crates/crate/amethyst-crate.yml");
      this.A("crates/crate/common-crate.yml");
      this.A("crates/crate/crimson-crate.yml");
      this.A("crates/crate/gold-crate.yml");
      this.A("crates/crate/prime-crate.yml");
      this.A("teleport.yml");
      this.A("warps.yml");
      this.A("warps/messages.yml");
      this.A("warps/config.yml");
      this.A("enderchest/config.yml");
      this.A("nightvision.yml");
      this.A("chathover/config.yml");
      this.A("settings/config.yml");
   }

   private void A(String var1) {
      if (!(new File(this.getDataFolder(), var1)).exists()) {
         try {
            this.saveResource(var1, false);
         } catch (Exception var3) {
            this.getLogger().warning("Failed to save resource: " + var1);
         }
      }

   }

   public void loadSurvivalConfig() {
      File var1 = new File(this.getDataFolder(), "survival/config.yml");
      if (!var1.exists()) {
         this.saveResource("survival/config.yml", false);
      }

      this.Â = YamlConfiguration.loadConfiguration(var1);
   }

   public FileConfiguration getSurvivalConfig() {
      if (this.Â == null) {
         this.loadSurvivalConfig();
      }

      return this.Â;
   }

   public void markPlayerAsUpdateWriter(UUID var1) {
      this.Z.add(var1);
   }

   public void unmarkPlayerAsUpdateWriter(UUID var1) {
      this.Z.remove(var1);
   }

   public boolean isPlayerMarkedAsUpdateWriter(UUID var1) {
      return this.Z.contains(var1);
   }

   public void setActiveUpdate(List<String> var1) {
      this.F = new ArrayList(var1);
      this.b = System.currentTimeMillis();
      this.A();
   }

   public List<String> getActiveUpdatePages() {
      return this.F;
   }

   public long getActiveUpdateVersion() {
      return this.b;
   }

   public boolean hasActiveUpdate() {
      return this.F != null && !this.F.isEmpty();
   }

   private void A() {
      File var1 = new File(this.getDataFolder(), "survival/update.yml");
      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      ((FileConfiguration)var2).set("pages", this.F);
      ((FileConfiguration)var2).set("version", this.b);

      try {
         ((FileConfiguration)var2).save(var1);
      } catch (Exception var4) {
         this.getLogger().warning("Failed to save update.yml");
      }

   }

   public void loadUpdateFromConfig() {
      File var1 = new File(this.getDataFolder(), "survival/update.yml");
      if (var1.exists()) {
         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
         if (((FileConfiguration)var2).contains("pages")) {
            this.F = ((FileConfiguration)var2).getStringList("pages");
         }

         this.b = ((FileConfiguration)var2).getLong("version", 0L);
      }

   }

   public void markPlayerAsAdvisorWriter(UUID var1) {
      this.P.add(var1);
   }

   public void unmarkPlayerAsAdvisorWriter(UUID var1) {
      this.P.remove(var1);
   }

   public boolean isPlayerMarkedAsAdvisorWriter(UUID var1) {
      return this.P.contains(var1);
   }

   public void setActiveAdvisor(List<String> var1) {
      this.À = new ArrayList(var1);
      this.E();
   }

   public List<String> getActiveAdvisorPages() {
      return this.À;
   }

   public boolean hasActiveAdvisor() {
      return this.À != null && !this.À.isEmpty();
   }

   private void E() {
      File var1 = new File(this.getDataFolder(), "survival/advisor.yml");
      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      ((FileConfiguration)var2).set("pages", this.À);

      try {
         ((FileConfiguration)var2).save(var1);
      } catch (Exception var4) {
         this.getLogger().warning("Failed to save advisor.yml");
      }

   }

   public void loadAdvisorFromConfig() {
      File var1 = new File(this.getDataFolder(), "survival/advisor.yml");
      if (var1.exists()) {
         YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
         if (((FileConfiguration)var2).contains("pages")) {
            this.À = ((FileConfiguration)var2).getStringList("pages");
         }
      }

   }

   public com.h2ph.J.B.F.A getOffendPlugin() {
      return this.z;
   }

   public com.h2ph.H.A getTeamModule() {
      return this.¢;
   }

   public com.h2ph.U.A getApiServer() {
      return this.f;
   }

   public KeyAllManager getKeyAllManager() {
      return this.J;
   }

   public CarouselManager getCarouselManager() {
      return this.a;
   }

   public CrateLocationRegistry getCrateLocationRegistry() {
      return this.A;
   }

   public CrateEffectsManager getCrateEffectsManager() {
      return this.e;
   }

   public SpawnManager getSpawnManager() {
      return this.Q;
   }

   public TeleportManager getTeleportManager() {
      return this.U;
   }

   public TeleportQueueManager getTeleportQueueManager() {
      return this.l;
   }

   public C getShopCommand() {
      return this.E;
   }

   public A getRulesCommand() {
      return this.N;
   }

   public P getServerInfoCommand() {
      return this.v;
   }

   public B getDuelArenaManager() {
      return this.L;
   }

   public G getDuelCommand() {
      return this.p;
   }

   public void loadChatFilterConfig() {
      File var1 = new File(this.getDataFolder(), "survival/chatfilter/config.yml");
      if (!var1.exists()) {
         this.A("survival/chatfilter/config.yml");
      }

      this.k = YamlConfiguration.loadConfiguration(var1);
   }

   public FileConfiguration getChatFilterConfig() {
      if (this.k == null) {
         this.loadChatFilterConfig();
      }

      return this.k;
   }

   public void loadRTPConfig() {
      File var1 = new File(this.getDataFolder(), "rtp/europe/config.yml");
      if (!var1.exists()) {
         this.A("rtp/europe/config.yml");
      }

      this.K = YamlConfiguration.loadConfiguration(var1);
   }

   public FileConfiguration getRTPConfig() {
      if (this.K == null) {
         this.loadRTPConfig();
      }

      return this.K;
   }

   public FileConfiguration getRTPRegionConfig(String var1) {
      File var2 = new File(this.getDataFolder(), "rtp/" + var1 + "/config.yml");
      return var2.exists() ? YamlConfiguration.loadConfiguration(var2) : null;
   }

   public void loadGlobalRTPConfig() {
      File var1 = new File(this.getDataFolder(), "rtp/config.yml");
      if (!var1.exists()) {
         this.A("rtp/config.yml");
      }

      this.I = YamlConfiguration.loadConfiguration(var1);
   }

   public FileConfiguration getGlobalRTPConfig() {
      if (this.I == null) {
         this.loadGlobalRTPConfig();
      }

      return this.I;
   }

   public com.h2ph.b.B getSignInput() {
      return this.D;
   }

   private void B(String var1) {
      try {
         CommandMap var2 = this.getServer().getCommandMap();
         PluginCommand var3 = this.getCommand(var1);
         if (var3 != null) {
            ((Command)var3).unregister(var2);
         }

         Map var4 = var2.getKnownCommands();
         var4.remove(var1.toLowerCase(Locale.ROOT));
         String var10001 = this.getDescription().getName().toLowerCase(Locale.ROOT);
         var4.remove(var10001 + ":" + var1.toLowerCase(Locale.ROOT));
      } catch (Exception var5) {
         this.getLogger().warning("Failed to free up command '" + var1 + "': " + var5.getMessage());
      }

   }

   private void A(boolean var1) {
      ConsoleCommandSender var2 = this.getServer().getConsoleSender();
      String var3 = this.getDescription().getVersion();
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l  ____                    _    ____                 "));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l |  _ \\  ___  _ __  _   _| |_ / ___|___  _ __ ___  "));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l | | | |/ _ \\| '_ \\| | | | __| |   / _ \\| '__/ _ \\ "));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l | |_| | (_) | | | | |_| | |_| |__| (_) | | |  __/ "));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l |____/ \\___/|_| |_|\\__,_|\\__|\\____\\___/|_|  \\___| "));
      var2.sendMessage("");
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7        Running &bDonutCore &7v" + var3 + " by &bMrNaruto"));
      var2.sendMessage("");
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m--------------------------------------------------"));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f  &lMODULE STATUS:"));
      if (var1 && this.getServer().getPluginManager().getPlugin("Vault") != null) {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [+] &fEconomy System: &a&lONLINE &7(Vault Hooked)"));
      } else {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [-] &fEconomy System: &c&lOFFLINE &7(Vault Missing/Disabled)"));
      }

      if (this.f != null) {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [+] &fWeb API Server: &a&lONLINE"));
      } else {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [-] &fWeb API Server: &c&lOFFLINE"));
      }

      if (this.z != null) {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [+] &fModeration Core: &a&lONLINE"));
      }

      if (this.getServer().getPluginManager().getPlugin("WorldEdit") == null && this.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit") == null) {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [-] &fWorldEdit Hook: &c&lNOT FOUND"));
      } else {
         var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b  [+] &fWorldEdit Hook: &a&lCONNECTED"));
      }

      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m--------------------------------------------------"));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l  DONUTCORE SUCCESSFULLY INITIALIZED"));
      var2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m--------------------------------------------------"));
   }
}
