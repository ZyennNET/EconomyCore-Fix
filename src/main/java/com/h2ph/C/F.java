package com.h2ph.c;

import com.h2ph.PrismSurvival;

public final class F {
   private final PrismSurvival C;
   private G B;
   private H A;

   public F(PrismSurvival var1) {
      this.C = var1;
   }

   public void D() {
      this.B = new G(this.C);
      this.A = new H(this.C);
      E.A(this);
      C var1 = new C(this);

      for(String var5 : new String[]{"lb", "leaderboard", "leaderboards"}) {
         if (this.C.getCommand(var5) != null) {
            this.C.getCommand(var5).setExecutor(var1);
            this.C.getCommand(var5).setTabCompleter(var1);
         }
      }

      this.C.getServer().getPluginManager().registerEvents(new D(this), this.C);
      this.C.getServer().getPluginManager().registerEvents(new B(this), this.C);
      this.C.getServer().getScheduler().runTaskTimerAsynchronously(this.C, () -> this.A.B(), 40L, 1200L);
      this.C.getServer().getScheduler().runTaskTimer(this.C, () -> this.A.A(), 2400L, 2400L);
      this.C.getLogger().info("[Leaderboards] Loaded (/lb, /leaderboard, /leaderboards).");
   }

   public void E() {
      if (this.A != null) {
         this.A.A();
      }

   }

   public void B() {
      this.B.A();
      E.A(this);
      this.A.C();
      this.A.B();
   }

   public PrismSurvival H() {
      return this.C;
   }

   public G A() {
      return this.B;
   }

   public H G() {
      return this.A;
   }
}
