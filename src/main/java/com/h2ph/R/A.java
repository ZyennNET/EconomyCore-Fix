package com.h2ph.R;

import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class A implements Listener, CommandExecutor {
   private final PrismSurvival F;
   private FileConfiguration C;
   private Chat A;
   private static final String B = "signinput_active";
   private static final Pattern E = Pattern.compile("&#([A-Fa-f0-9]{6})");
   private static final Pattern D = Pattern.compile("&x(&[0-9a-fA-F]){6}");

   public A(PrismSurvival var1) {
      this.F = var1;
      this.B();
      this.A();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("donutchathover").setExecutor(this);
   }

   private void B() {
      File var1 = new File(this.F.getDataFolder(), "chathover/config.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.F.saveResource("chathover/config.yml", false);
      }

      this.C = YamlConfiguration.loadConfiguration(var1);
   }

   private void A() {
      if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
         RegisteredServiceProvider var1 = Bukkit.getServicesManager().getRegistration(Chat.class);
         if (var1 != null) {
            this.A = (Chat)var1.getProvider();
         }

      }
   }

   private String A(String var1) {
      if (var1 == null) {
         return "";
      } else {
         Matcher var2 = D.matcher(var1);
         StringBuffer var3 = new StringBuffer();

         while(var2.find()) {
            String var4 = var2.group();
            String var5 = var4.replace('&', '§');
            var2.appendReplacement(var3, Matcher.quoteReplacement(var5));
         }

         var2.appendTail(var3);
         var1 = var3.toString();
         Matcher var14 = E.matcher(var1);
         StringBuffer var15 = new StringBuffer();

         while(var14.find()) {
            String var6 = var14.group(1);
            StringBuilder var7 = new StringBuilder("§x");

            for(char var11 : var6.toCharArray()) {
               var7.append('§').append(var11);
            }

            var14.appendReplacement(var15, var7.toString());
         }

         var14.appendTail(var15);
         var1 = var15.toString();
         return ChatColor.translateAlternateColorCodes('&', var1);
      }
   }

   private String A(Player var1, String var2, String var3) {
      World var4 = var1.getWorld();
      String var5 = var4.getName();
      var2 = var2.replace("{message}", var3);
      var2 = var2.replace("{name}", var1.getName());
      var2 = var2.replace("{world}", var5);
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         var2 = var2.replace("{prefix}", "%vault_prefix%");
         var2 = var2.replace("{suffix}", "%vault_suffix%");
         var2 = PlaceholderAPI.setPlaceholders(var1, var2);
      } else if (this.A != null) {
         String var6 = this.A.getPlayerPrefix(var1);
         String var7 = this.A.getPlayerSuffix(var1);
         var2 = var2.replace("{prefix}", var6 != null ? this.A(var6) : "");
         var2 = var2.replace("{suffix}", var7 != null ? this.A(var7) : "");
      } else {
         var2 = var2.replace("{prefix}", "");
         var2 = var2.replace("{suffix}", "");
      }

      var2 = var2.replace("{displayname}", var1.getDisplayName());
      return this.A(var2);
   }

   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public void onChat(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.hasMetadata("signinput_active")) {
         var1.setCancelled(true);
      } else {
         String var3 = var1.getMessage();
         String var4 = this.C.getString("chat-format", "{prefix}{name}{suffix}&7: {message}");
         var4 = this.A(var2, var4, var3);
         BaseComponent[] var5 = TextComponent.fromLegacyText(var4);
         HoverEvent var6 = null;
         String var7 = this.C.getString("hover-text", "");
         if (!var7.isEmpty()) {
            var7 = this.A(var2, var7, var3);
            var6 = new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(var7));
         }

         ClickEvent var8 = null;
         String var9 = this.C.getString("click-command", "");
         if (!var9.isEmpty()) {
            var9 = var9.replace("%player%", var2.getName());
            var8 = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, var9);
         }

         if (var6 != null || var8 != null) {
            for(BaseComponent var13 : var5) {
               if (var6 != null) {
                  var13.setHoverEvent(var6);
               }

               if (var8 != null) {
                  var13.setClickEvent(var8);
               }
            }
         }

         var1.setCancelled(true);

         for(Player var18 : Bukkit.getOnlinePlayers()) {
            if (this.F.getChatToggleManager() == null || this.F.getChatToggleManager().isChatVisible(var18.getUniqueId())) {
               var18.spigot().sendMessage(var5);
            }
         }

      }
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var2.getName().equalsIgnoreCase("donutchathover")) {
         return false;
      } else if (var4.length > 0 && var4[0].equalsIgnoreCase("reload")) {
         if (!var1.hasPermission("economysmpcore.chathover.reload")) {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "No permission.");
            return true;
         } else {
            this.B();
            var1.sendMessage(String.valueOf(ChatColor.GREEN) + "ChatHover config reloaded.");
            return true;
         }
      } else {
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "DonutCore ChatHover by MrNaruto");
         return true;
      }
   }
}
