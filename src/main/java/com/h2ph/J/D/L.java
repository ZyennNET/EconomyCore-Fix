package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class L implements CommandExecutor {
   private static final Map<String, String> B = Map.of("gmc", "creative", "gms", "survival", "gma", "adventure", "gmspec", "spectator");
   private static final Map<String, GameMode> D;
   private final PrismSurvival C;
   private File E;
   private FileConfiguration A;

   public L(PrismSurvival var1) {
      this.C = var1;
      this.A();

      for(String var3 : B.keySet()) {
         if (var1.getCommand(var3) != null) {
            var1.getCommand(var3).setExecutor(this);
         } else {
            var1.getLogger().warning("QuickGameMode: /" + var3 + " is missing from plugin.yml, skipping registration.");
         }
      }

   }

   private void A() {
      this.E = new File(this.C.getDataFolder(), "quickgamemode/config.yml");
      if (!this.E.exists()) {
         this.E.getParentFile().mkdirs();
         this.C.saveResource("quickgamemode/config.yml", false);
      }

      this.A = YamlConfiguration.loadConfiguration(this.E);
   }

   public void reload() {
      this.A();
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var1 instanceof Player var5) {
         String var6 = (String)B.get(var3.toLowerCase(Locale.ROOT));
         if (var6 == null) {
            return false;
         } else {
            String var7 = "modes." + var6;
            if (!this.A.getBoolean(var7 + ".enabled", true)) {
               var5.sendMessage(com.h2ph.b.D.A(this.A.getString("disabled", "&cThat gamemode command is currently disabled.")));
               return true;
            } else {
               String var8 = this.A.getString(var7 + ".permission", this.B(var6));
               if (!var5.hasPermission(var8)) {
                  var5.sendMessage(com.h2ph.b.D.A(this.A.getString("no-permission", "&cYou do not have permission to do that.")));
                  return true;
               } else {
                  GameMode var9 = (GameMode)D.get(var6);
                  String var10 = this.A.getString(var7 + ".display-name", this.A(var6));
                  if (var5.getGameMode() == var9) {
                     this.A(var5, this.A.getString("already-in-mode", "&7You are already in &a%mode%&7 mode.").replace("%mode%", var10));
                     return true;
                  } else {
                     var5.setGameMode(var9);
                     String var11 = this.A.getString(var7 + ".message", this.A.getString("default-message", "&7You set your gamemode to &a%mode%&7 mode."));
                     this.A(var5, var11.replace("%mode%", var10));
                     this.A(var5);
                     return true;
                  }
               }
            }
         }
      } else {
         var1.sendMessage("This command can only be used by players.");
         return true;
      }
   }

   private void A(Player var1, String var2) {
      String var3 = com.h2ph.b.D.A(var2);
      if ("chat".equalsIgnoreCase(this.A.getString("display", "actionbar"))) {
         var1.sendMessage(var3);
      } else {
         var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var3));
      }

   }

   private void A(Player var1) {
      if (this.A.getBoolean("sounds.enabled", true)) {
         String var2 = this.A.getString("sounds.on-change.sound", (String)null);
         if (var2 != null && !var2.isEmpty()) {
            try {
               Sound var3 = Sound.valueOf(var2.toUpperCase(Locale.ROOT));
               float var4 = (float)this.A.getDouble("sounds.on-change.volume", (double)1.0F);
               float var5 = (float)this.A.getDouble("sounds.on-change.pitch", (double)1.0F);
               var1.playSound(var1.getLocation(), var3, var4, var5);
            } catch (IllegalArgumentException var6) {
            }

         }
      }
   }

   private String B(String var1) {
      return "economysmpcore.gamemode." + var1;
   }

   private String A(String var1) {
      if (var1.isEmpty()) {
         return var1;
      } else {
         char var10000 = Character.toUpperCase(var1.charAt(0));
         return var10000 + var1.substring(1);
      }
   }

   static {
      D = Map.of("creative", GameMode.CREATIVE, "survival", GameMode.SURVIVAL, "adventure", GameMode.ADVENTURE, "spectator", GameMode.SPECTATOR);
   }
}
