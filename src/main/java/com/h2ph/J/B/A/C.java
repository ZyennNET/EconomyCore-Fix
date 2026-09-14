package com.h2ph.J.B.A;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;

public class C {
   private final PrismSurvival B;
   private final File A;
   private final com.h2ph.T.A.F C;

   public C(PrismSurvival var1) {
      this.B = var1;
      this.A = new File(var1.getDataFolder(), "survival/duels/stats");
      if (!this.A.exists()) {
         this.A.mkdirs();
      }

      if (var1.getDatabaseManager() != null && var1.getDatabaseManager().F()) {
         this.C = new com.h2ph.T.A.A(var1, var1.getDatabaseManager());
      } else {
         this.C = null;
      }

   }

   public com.h2ph.T.A.F A() {
      return this.C;
   }

   private File C(UUID var1) {
      return new File(this.A, var1.toString() + ".yml");
   }

   private YamlConfiguration A(File var1) {
      return YamlConfiguration.loadConfiguration(var1);
   }

   private void A(File var1, YamlConfiguration var2) {
      try {
         var2.save(var1);
      } catch (IOException var4) {
         this.B.getLogger().severe("Could not save duel stats for " + var1.getName());
         var4.printStackTrace();
      }

   }

   public void E(UUID var1) {
      if (this.C != null) {
         this.C.D(var1);
      } else {
         File var2 = this.C(var1);
         YamlConfiguration var3 = this.A(var2);
         int var4 = var3.getInt("wins", 0);
         var3.set("wins", var4 + 1);
         this.A(var2, var3);
      }
   }

   public void D(UUID var1) {
      if (this.C != null) {
         this.C.C(var1);
      } else {
         File var2 = this.C(var1);
         YamlConfiguration var3 = this.A(var2);
         int var4 = var3.getInt("losses", 0);
         var3.set("losses", var4 + 1);
         this.A(var2, var3);
      }
   }

   public String B(UUID var1) {
      if (this.C != null) {
         return this.C.F(var1);
      } else {
         File var2 = this.C(var1);
         if (!var2.exists()) {
            return "0.00%";
         } else {
            YamlConfiguration var3 = this.A(var2);
            int var4 = var3.getInt("wins", 0);
            int var5 = var3.getInt("losses", 0);
            int var6 = var4 + var5;
            if (var6 == 0) {
               return "0.00%";
            } else {
               double var7 = (double)var4 / (double)var6 * (double)100.0F;
               return String.format("%.2f%%", var7);
            }
         }
      }
   }

   public int G(UUID var1) {
      if (this.C != null) {
         return this.C.A(var1);
      } else {
         File var2 = this.C(var1);
         if (!var2.exists()) {
            return 0;
         } else {
            YamlConfiguration var3 = this.A(var2);
            return var3.getInt("wins", 0);
         }
      }
   }

   public int F(UUID var1) {
      if (this.C != null) {
         return this.C.G(var1);
      } else {
         File var2 = this.C(var1);
         if (!var2.exists()) {
            return 0;
         } else {
            YamlConfiguration var3 = this.A(var2);
            return var3.getInt("losses", 0);
         }
      }
   }

   public int A(UUID var1) {
      if (this.C != null) {
         return this.C.E(var1);
      } else {
         File var2 = this.C(var1);
         if (!var2.exists()) {
            return 0;
         } else {
            YamlConfiguration var3 = this.A(var2);
            return var3.getInt("streak", 0);
         }
      }
   }

   public void A(UUID var1, boolean var2) {
      if (this.C != null) {
         this.C.A(var1, var2);
      } else {
         File var3 = this.C(var1);
         YamlConfiguration var4 = this.A(var3);
         int var5 = var4.getInt("streak", 0);
         if (var2) {
            var5 = Math.max(1, var5 + 1);
         } else {
            var5 = Math.min(-1, var5 - 1);
         }

         var4.set("streak", var5);
         this.A(var3, var4);
      }
   }
}
