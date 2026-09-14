package com.h2ph.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class B {
   private final PrismSurvival D;
   private boolean B;
   private File A;
   private FileConfiguration C;

   public B(PrismSurvival var1) {
      this.D = var1;
      this.A = new File(var1.getDataFolder(), "maintenance/data.yml");
      this.E();
      this.A();
   }

   private void E() {
      File var1 = new File(this.D.getDataFolder(), "maintenance/config.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.D.saveResource("maintenance/config.yml", false);
      }

   }

   private void A() {
      if (!this.A.exists()) {
         try {
            this.A.getParentFile().mkdirs();
            this.A.createNewFile();
         } catch (IOException var2) {
            this.D.getLogger().severe("Could not create maintenance data file!");
            var2.printStackTrace();
         }
      }

      this.C = YamlConfiguration.loadConfiguration(this.A);
      this.B = this.C.getBoolean("enabled", false);
   }

   public void A(boolean var1) {
      this.B = var1;
      this.C.set("enabled", var1);

      try {
         this.C.save(this.A);
      } catch (IOException var3) {
         this.D.getLogger().severe("Could not save maintenance state!");
         var3.printStackTrace();
      }

      if (var1) {
         this.D();
      }

   }

   public boolean C() {
      return this.B;
   }

   public void D() {
      FileConfiguration var1 = this.B();
      List var2 = var1.getStringList("disconnect-message");
      String var3 = ChatColor.translateAlternateColorCodes('&', String.join("\n", var2));

      for(Player var5 : this.D.getServer().getOnlinePlayers()) {
         if (!this.A(var5)) {
            var5.kickPlayer(var3);
         }
      }

   }

   public boolean A(Player var1) {
      return var1.isOp() || var1.hasPermission("prismcore.maintenance.bypass");
   }

   public FileConfiguration B() {
      File var1 = new File(this.D.getDataFolder(), "maintenance/config.yml");
      if (!var1.exists()) {
         this.D.saveResource("maintenance/config.yml", false);
      }

      return YamlConfiguration.loadConfiguration(var1);
   }
}
