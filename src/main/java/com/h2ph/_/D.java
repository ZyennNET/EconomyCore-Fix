package com.h2ph._;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class D {
   private final PrismSurvival E;
   private final File G;
   private final File D;
   private FileConfiguration C;
   private FileConfiguration F;
   private final Map<String, _A> B = new LinkedHashMap();
   private final Map<UUID, String> A = new HashMap();

   public D(PrismSurvival var1) {
      this.E = var1;
      this.G = new File(var1.getDataFolder(), "disguise/config.yml");
      this.D = new File(var1.getDataFolder(), "disguise/data.yml");
      this.B();
      this.F();
      this.C();
   }

   private void B() {
      if (!this.G.exists()) {
         this.G.getParentFile().mkdirs();
         this.E.saveResource("disguise/config.yml", false);
      }

   }

   public void F() {
      this.C = YamlConfiguration.loadConfiguration(this.G);
      this.B.clear();
      if (this.C.isConfigurationSection("aliases")) {
         for(String var2 : this.C.getConfigurationSection("aliases").getKeys(false)) {
            String var3 = "aliases." + var2 + ".";
            String var4 = this.C.getString(var3 + "display-name", var2);
            String var5 = this.C.getString(var3 + "skin-username", var2);
            this.B.put(var2, new _A(var2, var4, var5));
         }
      }

   }

   private void C() {
      if (!this.D.exists()) {
         try {
            this.D.getParentFile().mkdirs();
            this.D.createNewFile();
         } catch (IOException var5) {
            this.E.getLogger().warning("Could not create disguise/data.yml: " + var5.getMessage());
         }
      }

      this.F = YamlConfiguration.loadConfiguration(this.D);
      this.A.clear();
      if (this.F.isConfigurationSection("disguised")) {
         for(String var2 : this.F.getConfigurationSection("disguised").getKeys(false)) {
            try {
               this.A.put(UUID.fromString(var2), this.F.getString("disguised." + var2));
            } catch (IllegalArgumentException var4) {
            }
         }
      }

   }

   private void D() {
      this.F.set("disguised", (Object)null);

      for(Map.Entry var2 : this.A.entrySet()) {
         this.F.set("disguised." + ((UUID)var2.getKey()).toString(), var2.getValue());
      }

      try {
         this.F.save(this.D);
      } catch (IOException var3) {
         this.E.getLogger().warning("Could not save disguise/data.yml: " + var3.getMessage());
      }

   }

   public String E() {
      return this.C.getString("gui-title", "&8Disguise Menu");
   }

   public Collection<_A> A() {
      return this.B.values();
   }

   public _A A(String var1) {
      return (_A)this.B.get(var1);
   }

   public boolean B(UUID var1) {
      return this.A.containsKey(var1);
   }

   public String A(UUID var1) {
      return (String)this.A.get(var1);
   }

   public void A(Player var1, String var2) {
      _A var3 = (_A)this.B.get(var2);
      if (var3 != null) {
         this.A.put(var1.getUniqueId(), var2);
         this.D();
         this.B(var1, var3.A);
      }
   }

   public void C(Player var1) {
      this.A.remove(var1.getUniqueId());
      this.D();
      this.B(var1);
   }

   private void B(Player var1, String var2) {
      String var3 = ChatColor.translateAlternateColorCodes('&', var2);
      var1.setPlayerListName(var3);
      var1.setDisplayName(var3);
   }

   private void B(Player var1) {
      var1.setPlayerListName(var1.getName());
      var1.setDisplayName(var1.getName());
   }

   public void A(Player var1) {
      String var2 = (String)this.A.get(var1.getUniqueId());
      if (var2 != null) {
         _A var3 = (_A)this.B.get(var2);
         if (var3 != null) {
            this.B(var1, var3.A);
         }
      }
   }

   public static class _A {
      public final String B;
      public final String A;
      public final String C;

      public _A(String var1, String var2, String var3) {
         this.B = var1;
         this.A = var2;
         this.C = var3;
      }
   }
}
