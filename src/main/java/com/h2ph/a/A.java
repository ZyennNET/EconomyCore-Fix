package com.h2ph.A;

import com.h2ph.PrismSurvival;
import com.h2ph.b.D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class A {
   private final PrismSurvival D;
   private final File E;
   private final Map<UUID, Boolean> F = new ConcurrentHashMap();
   private final Map<UUID, Set<String>> C = new ConcurrentHashMap();
   private final Map<UUID, UUID> B = new ConcurrentHashMap();
   private File A;
   private FileConfiguration G;

   public A(PrismSurvival var1) {
      this.D = var1;
      this.E = new File(var1.getDataFolder(), "settings/data/messaging");
      if (!this.E.exists()) {
         this.E.mkdirs();
      }

      this.E();
   }

   public void E() {
      this.A = new File(this.D.getDataFolder(), "messaging/config.yml");
      if (!this.A.exists()) {
         this.A.getParentFile().mkdirs();
         this.D.saveResource("messaging/config.yml", false);
      }

      this.G = YamlConfiguration.loadConfiguration(this.A);
   }

   public void D() {
      this.E();
   }

   private String A(String var1, String var2) {
      return this.G.getString("msg." + var1, var2);
   }

   public String A() {
      return com.h2ph.b.D.A(this.A("usage-msg", "&cUsage: /msg <player> <message>"));
   }

   public String B() {
      return com.h2ph.b.D.A(this.A("usage-reply", "&cUsage: /r <message>"));
   }

   public String C() {
      return com.h2ph.b.D.A(this.A("player-not-found", "&cThat player is not online."));
   }

   private File C(UUID var1) {
      return new File(this.E, var1.toString() + ".yml");
   }

   private void A(UUID var1) {
      File var2 = this.C(var1);
      if (!var2.exists()) {
         this.F.put(var1, false);
         this.C.put(var1, new HashSet());
         this.B.put(var1, (Object)null);
      } else {
         YamlConfiguration var3 = YamlConfiguration.loadConfiguration(var2);
         this.F.put(var1, var3.getBoolean("toggle", false));
         this.C.put(var1, new HashSet(var3.getStringList("blocked")));
         String var4 = var3.getString("lastReply");
         this.B.put(var1, var4 != null ? UUID.fromString(var4) : null);
      }
   }

   private void F(UUID var1) {
      File var2 = this.C(var1);
      YamlConfiguration var3 = new YamlConfiguration();
      var3.set("toggle", this.F.getOrDefault(var1, false));
      var3.set("blocked", new ArrayList((Collection)this.C.getOrDefault(var1, Collections.emptySet())));
      UUID var4 = (UUID)this.B.get(var1);
      if (var4 != null) {
         var3.set("lastReply", var4.toString());
      }

      try {
         var3.save(var2);
      } catch (Exception var6) {
         this.D.getLogger().warning("Failed to save messaging data for " + String.valueOf(var1));
      }

   }

   public boolean B(UUID var1) {
      if (!this.F.containsKey(var1)) {
         this.A(var1);
      }

      return (Boolean)this.F.getOrDefault(var1, false);
   }

   public void A(UUID var1, boolean var2) {
      this.F.put(var1, var2);
      this.F(var1);
   }

   public Set<String> E(UUID var1) {
      if (!this.C.containsKey(var1)) {
         this.A(var1);
      }

      return new HashSet((Collection)this.C.getOrDefault(var1, Collections.emptySet()));
   }

   public void B(UUID var1, String var2) {
      if (!this.C.containsKey(var1)) {
         this.A(var1);
      }

      ((Set)this.C.get(var1)).add(var2.toLowerCase());
      this.F(var1);
   }

   public void A(UUID var1, String var2) {
      if (!this.C.containsKey(var1)) {
         this.A(var1);
      }

      ((Set)this.C.get(var1)).remove(var2.toLowerCase());
      this.F(var1);
   }

   public void A(UUID var1, UUID var2) {
      this.B.put(var1, var2);
      this.F(var1);
   }

   public UUID D(UUID var1) {
      if (!this.B.containsKey(var1)) {
         this.A(var1);
      }

      return (UUID)this.B.get(var1);
   }

   public boolean A(Player var1, Player var2, String var3) {
      UUID var4 = var1.getUniqueId();
      UUID var5 = var2.getUniqueId();
      if (var4.equals(var5)) {
         var1.sendMessage(com.h2ph.b.D.A(this.A("self-message", "&cYou can't message yourself.")));
         return false;
      } else if (this.B(var5)) {
         var1.sendMessage(com.h2ph.b.D.A(this.A("messages-disabled", "&cThis user has private messages disabled.")));
         return false;
      } else if (this.E(var4).contains(var2.getName().toLowerCase())) {
         var1.sendMessage(com.h2ph.b.D.A(this.A("you-blocked-them", "&cYou have this player blocked.")));
         return false;
      } else if (this.E(var5).contains(var1.getName().toLowerCase())) {
         var1.sendMessage(com.h2ph.b.D.A(this.A("they-blocked-you", "&cYou cannot message this player.")));
         return false;
      } else {
         HashMap var6 = new HashMap();
         var6.put("%target%", var2.getName());
         var6.put("%message%", var3);
         String var7 = com.h2ph.b.D.A(this.A((String)this.A("format-to-sender", "&bYOU &7-> &b%target%: &f%message%"), (Map)var6));
         HashMap var8 = new HashMap();
         var8.put("%sender%", var1.getName());
         var8.put("%message%", var3);
         String var9 = com.h2ph.b.D.A(this.A((String)this.A("format-to-target", "&b%sender% &7-> &bYOU&7: &f%message%"), (Map)var8));
         var1.sendMessage(var7);
         var2.sendMessage(var9);
         this.A(var1, "message-sent");
         this.A(var2, "message-received");
         this.A(var4, var5);
         this.A(var5, var4);
         return true;
      }
   }

   public boolean B(Player var1, String var2) {
      UUID var3 = this.D(var1.getUniqueId());
      if (var3 == null) {
         var1.sendMessage(com.h2ph.b.D.A(this.A("no-reply-target", "&cYou have no one to reply to.")));
         return false;
      } else {
         Player var4 = Bukkit.getPlayer(var3);
         if (var4 != null && var4.isOnline()) {
            return this.A(var1, var4, var2);
         } else {
            var1.sendMessage(com.h2ph.b.D.A(this.A("target-offline", "&cThat player is no longer online.")));
            this.A((UUID)var1.getUniqueId(), (UUID)null);
            return false;
         }
      }
   }

   private String A(String var1, Map<String, String> var2) {
      if (var1 == null) {
         return "";
      } else {
         String var3 = var1;

         for(Map.Entry var5 : var2.entrySet()) {
            var3 = var3.replace((CharSequence)var5.getKey(), (CharSequence)var5.getValue());
         }

         return var3;
      }
   }

   private void A(Player var1, String var2) {
      if (this.G.getBoolean("sounds.enabled", true)) {
         String var3 = this.G.getString("sounds." + var2 + ".sound", (String)null);
         if (var3 != null && !var3.isEmpty()) {
            try {
               Sound var4 = Sound.valueOf(var3.toUpperCase(Locale.ROOT));
               float var5 = (float)this.G.getDouble("sounds." + var2 + ".volume", (double)1.0F);
               float var6 = (float)this.G.getDouble("sounds." + var2 + ".pitch", (double)1.0F);
               var1.playSound(var1.getLocation(), var4, var5, var6);
            } catch (IllegalArgumentException var7) {
            }

         }
      }
   }
}
