package com.h2ph.J.B;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class C implements CommandExecutor {
   private static final Pattern A = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private final PrismSurvival B;

   public C(PrismSurvival var1) {
      this.B = var1;
      var1.getCommand("announce").setExecutor(this);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 0) {
         this.A(var1);
         return true;
      } else {
         int var5 = 1;
         ArrayList var7 = new ArrayList(Arrays.asList(var4));
         if (var7.size() >= 3 && ((String)var7.get(var7.size() - 2)).equalsIgnoreCase("repeat")) {
            try {
               var5 = Integer.parseInt((String)var7.get(var7.size() - 1));
               if (var5 <= 0) {
                  this.A(var1);
                  return true;
               }

               var7.remove(var7.size() - 1);
               var7.remove(var7.size() - 1);
            } catch (NumberFormatException var16) {
               this.A(var1);
               return true;
            }
         }

         if (var7.isEmpty()) {
            this.A(var1);
            return true;
         } else {
            String var6 = String.join(" ", var7);
            String var8 = this.A("&d&l" + com.h2ph.b.C.A("announcements"));
            String var9 = this.A(var6);
            TextComponent var10 = LegacyComponentSerializer.legacySection().deserialize(var8);
            TextComponent var11 = LegacyComponentSerializer.legacySection().deserialize(var9);
            Title var12 = Title.title(var10, var11);

            for(int var13 = 0; var13 < var5; ++var13) {
               for(Player var15 : Bukkit.getOnlinePlayers()) {
                  var15.showTitle(var12);
                  var15.sendActionBar(var11);
               }
            }

            return true;
         }
      }
   }

   private void A(CommandSender var1) {
      if (var1 instanceof Player) {
         ((Player)var1).playSound(((Player)var1).getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
      }

   }

   private String A(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         Matcher var2 = A.matcher(var1);
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
