package com.h2ph.Z;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class A implements CommandExecutor, TabCompleter {
   private final PrismSurvival G;
   private FileConfiguration E;
   private final Map<UUID, Long> D = new ConcurrentHashMap();
   private List<String> I = new ArrayList();
   private String B = "";
   private String F = "Report Bot";
   private String C = "";
   private int H = 60;
   private String A = "My Minecraft Server";

   public A(PrismSurvival var1) {
      this.G = var1;
      this.D();
      this.C();
   }

   private void D() {
      File var1 = new File(this.G.getDataFolder(), "report/config.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.G.saveResource("report/config.yml", false);
      }

      this.E = YamlConfiguration.loadConfiguration(var1);
      this.B = this.E.getString("webhook-url", "");
      this.F = this.E.getString("webhook-name", "Report Bot");
      this.C = this.E.getString("webhook-avatar-url", "");
      this.H = this.E.getInt("cooldown-seconds", 60);
      this.A = this.E.getString("server-name", "My Minecraft Server");
      this.I = this.E.getStringList("preset-reasons");
      if (this.I.isEmpty()) {
         this.I = Arrays.asList("spamming", "inappropriate language", "cheating/hacking", "harassment", "advertising", "griefing", "threats", "nsfw content", "bypassing filters", "staff disrespect");
         this.E.set("preset-reasons", this.I);
         this.A();
      }

   }

   private void A() {
      try {
         this.E.save(new File(this.G.getDataFolder(), "report/config.yml"));
      } catch (Exception var2) {
         this.G.getLogger().warning("Failed to save report config: " + var2.getMessage());
      }

   }

   private void C() {
      this.G.getCommand("report").setExecutor(this);
      this.G.getCommand("report").setTabCompleter(this);
      this.G.getCommand("reportadmin").setExecutor(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var2.getName().equalsIgnoreCase("reportadmin")) {
         if (!var1.hasPermission("economysmpcore.report.admin")) {
            var1.sendMessage(this.D(this.C("no-permission")));
            return true;
         } else {
            if (var4.length > 0 && var4[0].equalsIgnoreCase("reload")) {
               this.D();
               var1.sendMessage(this.D(this.C("reload-success")));
            } else {
               var1.sendMessage(this.D("&cUsage: /reportadmin reload"));
            }

            return true;
         }
      } else if (!(var1 instanceof Player)) {
         var1.sendMessage("Only players can use this command.");
         return true;
      } else {
         Player var5 = (Player)var1;
         if (!var5.hasPermission("economysmpcore.report.use")) {
            var5.sendMessage(this.D(this.B("no-permission")));
            return true;
         } else if (var4.length < 2) {
            var5.sendMessage(this.D(this.B("invalid-usage")));
            return true;
         } else {
            String var6 = var4[0];
            String var7 = String.join(" ", (CharSequence[])Arrays.copyOfRange(var4, 1, var4.length));
            String var8 = var7;

            try {
               int var9 = Integer.parseInt(var7);
               if (var9 < 1 || var9 > this.I.size()) {
                  var5.sendMessage(this.D(this.B("invalid-reason").replace("{reasons}", this.B())));
                  return true;
               }

               var8 = (String)this.I.get(var9 - 1);
            } catch (NumberFormatException var13) {
               boolean var10 = false;

               for(String var12 : this.I) {
                  if (var12.equalsIgnoreCase(var7)) {
                     var8 = var12;
                     var10 = true;
                     break;
                  }
               }

               if (!var10) {
                  var5.sendMessage(this.D(this.B("invalid-reason").replace("{reasons}", this.B())));
                  return true;
               }
            }

            if (var5.getName().equalsIgnoreCase(var6)) {
               var5.sendMessage(this.D(this.B("self-report")));
               return true;
            } else {
               UUID var14 = var5.getUniqueId();
               if (this.D.containsKey(var14)) {
                  long var15 = ((Long)this.D.get(var14) - System.currentTimeMillis()) / 1000L;
                  if (var15 > 0L) {
                     var5.sendMessage(this.D(this.B("cooldown").replace("{seconds}", String.valueOf(var15))));
                     return true;
                  }

                  this.D.remove(var14);
               }

               if (this.B.isEmpty()) {
                  var5.sendMessage(this.D(this.B("webhook-not-configured")));
                  this.G.getLogger().warning("Webhook URL not set in report/config.yml");
                  return true;
               } else {
                  boolean var16 = this.A(var5, var6, var8);
                  if (var16) {
                     var5.sendMessage(this.D(this.B("report-success")));
                     this.D.put(var14, System.currentTimeMillis() + (long)this.H * 1000L);
                  } else {
                     var5.sendMessage(this.D(this.B("report-failed")));
                  }

                  return true;
               }
            }
         }
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var2.getName().equalsIgnoreCase("report")) {
         if (var4.length == 1) {
            ArrayList var8 = new ArrayList();

            for(Player var7 : Bukkit.getOnlinePlayers()) {
               var8.add(var7.getName());
            }

            return var8;
         }

         if (var4.length == 2) {
            ArrayList var5 = new ArrayList();

            for(int var6 = 1; var6 <= this.I.size(); ++var6) {
               var5.add(String.valueOf(var6));
            }

            var5.addAll(this.I);
            return var5;
         }
      }

      return Collections.emptyList();
   }

   private boolean A(Player var1, String var2, String var3) {
      try {
         String var4 = this.B(var1, var2, var3);
         URL var5 = new URL(this.B);
         HttpsURLConnection var6 = (HttpsURLConnection)var5.openConnection();
         var6.setRequestMethod("POST");
         var6.setRequestProperty("Content-Type", "application/json");
         var6.setDoOutput(true);
         OutputStream var7 = var6.getOutputStream();

         try {
            var7.write(var4.getBytes(StandardCharsets.UTF_8));
         } catch (Throwable var11) {
            if (var7 != null) {
               try {
                  var7.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (var7 != null) {
            var7.close();
         }

         int var13 = var6.getResponseCode();
         return var13 >= 200 && var13 < 300;
      } catch (Exception var12) {
         var12.printStackTrace();
         return false;
      }
   }

   private String B(Player var1, String var2, String var3) {
      String var4 = Instant.now().toString();
      String var5 = String.format("{\"name\":\"Reporter\",\"value\":\"%s\",\"inline\":true},{\"name\":\"Reported Player\",\"value\":\"%s\",\"inline\":true},{\"name\":\"Reason\",\"value\":\"%s\",\"inline\":false}", this.A(var1.getName()), this.A(var2), this.A(var3));
      String var6 = String.format("{\"title\":\"New Report\",\"color\":15158332,\"fields\":[%s],\"footer\":{\"text\":\"%s\"},\"timestamp\":\"%s\"}", var5, this.A(this.A), var4);
      return String.format("{\"username\":\"%s\",\"avatar_url\":\"%s\",\"embeds\":[%s]}", this.A(this.F), this.A(this.C), var6);
   }

   private String A(String var1) {
      return var1.replace("\\", "\\\\").replace("\"", "\\\"");
   }

   private String D(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }

   private String C(String var1) {
      String var2 = "messages." + var1;
      return this.E.getString(var2, "&cMissing message: " + var1);
   }

   private String B(String var1) {
      String var2 = this.C("prefix");
      String var3 = this.C(var1);
      return var2 + var3;
   }

   private String B() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < this.I.size(); ++var2) {
         if (var2 > 0) {
            var1.append(", ");
         }

         var1.append(var2 + 1).append("=").append((String)this.I.get(var2));
      }

      return var1.toString();
   }
}
