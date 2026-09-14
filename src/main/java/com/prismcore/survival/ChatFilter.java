package com.prismcore.survival.survival;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChatFilter implements Listener {
   private final PrismSurvival plugin;
   private final Map<UUID, Long> chatCooldowns = new HashMap();
   private final Map<UUID, String> lastMessages = new HashMap();
   private final List<Pattern> badWordPatterns = new ArrayList();

   public ChatFilter(PrismSurvival var1) {
      this.plugin = var1;
      this.loadConfigAndPatterns();
   }

   private void loadConfigAndPatterns() {
      FileConfiguration var1 = this.plugin.getChatFilterConfig();
      this.badWordPatterns.clear();
      List var2 = var1.getStringList("chat-filter.bad-words");
      if (!var2.contains("fucm")) {
         var2.add("fucm");
      }

      if (!var2.contains("fck")) {
         var2.add("fck");
      }

      for(String var4 : var2) {
         this.badWordPatterns.add(this.buildSmartPattern(var4));
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onPlayerChat(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      FileConfiguration var3 = this.plugin.getChatFilterConfig();
      if (!var2.hasPermission("prism.chat.bypass")) {
         UUID var4 = var2.getUniqueId();
         long var5 = System.currentTimeMillis();
         String var7 = var1.getMessage();
         long var8 = var3.getLong("chat-filter.cooldown-ms", 2000L);
         if (this.chatCooldowns.containsKey(var4)) {
            long var10 = var5 - (Long)this.chatCooldowns.get(var4);
            if (var10 < var8) {
               var1.setCancelled(true);
               long var15 = (var8 - var10) / 1000L + 1L;
               String var10001 = String.valueOf(ChatColor.RED);
               var2.sendMessage(var10001 + "Please wait " + var15 + " second" + (var15 != 1L ? "s" : "") + " before your next message.");
               return;
            }
         }

         if (var3.getBoolean("chat-filter.block-repeats", true) && this.lastMessages.containsKey(var4) && ((String)this.lastMessages.get(var4)).equalsIgnoreCase(var7)) {
            var1.setCancelled(true);
            var2.sendMessage(String.valueOf(ChatColor.RED) + "Please do not repeat the same (or similar) message.");
         } else {
            String var14 = ChatColor.stripColor(var7);

            for(Pattern var12 : this.badWordPatterns) {
               Matcher var13 = var12.matcher(var14);
               if (var13.find()) {
                  var1.setCancelled(true);
                  if (this.plugin.getApiServer() != null) {
                     this.plugin.getApiServer().B(var2.getName(), var7, var13.group());
                  }

                  return;
               }
            }

            this.chatCooldowns.put(var4, var5);
            this.lastMessages.put(var4, var7);
         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      UUID var2 = var1.getPlayer().getUniqueId();
      this.chatCooldowns.remove(var2);
      this.lastMessages.remove(var2);
   }

   private Pattern buildSmartPattern(String var1) {
      StringBuilder var2 = new StringBuilder();

      for(char var6 : var1.toLowerCase().toCharArray()) {
         String var7 = this.getCharRegex(var6);
         var2.append(var7).append("+[\\W_]*");
      }

      return Pattern.compile(var2.toString(), 2);
   }

   private String getCharRegex(char var1) {
      switch (var1) {
         case 'a':
            return "[aA@4äãâ]";
         case 'b':
            return "[bB8]";
         case 'c':
            return "[cCkK(<]";
         case 'd':
         case 'j':
         case 'm':
         case 'n':
         case 'p':
         case 'q':
         case 'r':
         default:
            return Pattern.quote(String.valueOf(var1));
         case 'e':
            return "[eE3ëé]";
         case 'f':
            return "[fF]";
         case 'g':
            return "[gG69]";
         case 'h':
            return "[hH]";
         case 'i':
            return "[iI1!|lï]";
         case 'k':
            return "[kKcC]";
         case 'l':
            return "[lL1|!]";
         case 'o':
            return "[oO0öô]";
         case 's':
            return "[sS$5zZ]";
         case 't':
            return "[tT7+]";
         case 'u':
            return "[uUvV0*!#]";
         case 'v':
            return "[vVuU]";
      }
   }
}
