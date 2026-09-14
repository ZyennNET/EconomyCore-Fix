package com.h2ph.c;

import com.h2ph.PrismSurvival;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class G {
   private final PrismSurvival B;
   private FileConfiguration A;

   public G(PrismSurvival var1) {
      this.B = var1;
      this.A();
   }

   public void A() {
      File var1 = new File(this.B.getDataFolder(), "leaderboards/messages.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.B.saveResource("leaderboards/messages.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(var1);
   }

   public String A(String var1, String var2) {
      return this.A.getString(var1, var2);
   }

   public String A(String var1) {
      return this.A.getString(var1, var1);
   }
}
