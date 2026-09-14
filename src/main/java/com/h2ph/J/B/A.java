package com.h2ph.J.B;

import com.h2ph.PrismSurvival;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class A implements CommandExecutor {
   private final PrismSurvival A;

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   private String A(String var1) {
      String var2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
      String var3 = "ᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘꞯʀꜱᴛᴜᴠᴡxʏᴢ";
      StringBuilder var4 = new StringBuilder();

      for(char var8 : var1.toCharArray()) {
         int var9 = var2.indexOf(var8);
         var4.append(var9 != -1 ? var3.charAt(var9) : var8);
      }

      return var4.toString();
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("economysmpcore.admin.reload")) {
         String var11 = String.valueOf(ChatColor.DARK_GRAY);
         var1.sendMessage(var11 + this.A("no permission"));
         return true;
      } else {
         long var5 = System.currentTimeMillis();

         try {
            this.A.loadSurvivalConfig();
            this.A.loadChatFilterConfig();
            this.A.loadUpdateFromConfig();
            this.A.loadRTPConfig();
            this.A.loadGlobalRTPConfig();
            if (this.A.getOffendPlugin() != null) {
               this.A.getOffendPlugin().loadOffendConfig();
            }

            if (this.A.getAfkManager() != null) {
               this.A.getAfkManager().I();
               this.A.getAfkManager().B();
            }

            if (this.A.getShardsManager() != null) {
               this.A.getShardsManager().reloadConfig();
            }

            if (this.A.getWarpCommand() != null) {
               this.A.getWarpCommand().reloadAll();
            }

            if (this.A.getShopCommand() != null) {
               this.A.getShopCommand().reload();
            }

            if (this.A.getRulesCommand() != null) {
               this.A.getRulesCommand().loadConfig();
            }

            if (this.A.getServerInfoCommand() != null) {
               this.A.getServerInfoCommand().loadConfig();
            }

            if (this.A.getPrivateMessageManager() != null) {
               this.A.getPrivateMessageManager().D();
            }

            if (this.A.getQuickGameMode() != null) {
               this.A.getQuickGameMode().reload();
            }

            if (this.A.getClearLagManager() != null) {
               this.A.getClearLagManager().T();
            }

            this.A.loadAdvisorFromConfig();
            if (this.A.getSpawnManager() != null) {
               this.A.getSpawnManager().reloadConfig();
            }

            if (this.A.getCommandHideListener() != null) {
               this.A.getCommandHideListener().reload();
            }

            long var7 = System.currentTimeMillis() - var5;
            var1.sendMessage("");
            String var10 = String.valueOf(ChatColor.DARK_GRAY);
            var1.sendMessage(var10 + this.A("economysmpcore") + " " + String.valueOf(ChatColor.GREEN) + this.A("reloaded successfully") + String.valueOf(ChatColor.GRAY) + " (" + var7 + "ms)");
            var1.sendMessage("");
         } catch (Exception var9) {
            String var10001 = String.valueOf(ChatColor.DARK_GRAY);
            var1.sendMessage(var10001 + this.A("economysmpcore") + " " + String.valueOf(ChatColor.RED) + this.A("reload failed (check console)"));
            var9.printStackTrace();
         }

         return true;
      }
   }
}
