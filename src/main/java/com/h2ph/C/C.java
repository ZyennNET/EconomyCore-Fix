package com.h2ph.c;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class C implements CommandExecutor, TabCompleter {
   private final F A;

   public C(F var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length > 0 && var4[0].equalsIgnoreCase("reload")) {
         if (!var1.hasPermission("economysmpcore.leaderboards.admin")) {
            var1.sendMessage(com.h2ph.c.A.B(this.A.A().A("no-permission", "&cYou have no permission for that!")));
            return true;
         } else {
            this.A.B();
            var1.sendMessage(com.h2ph.c.A.B(this.A.A().A("reloaded", "&aLeaderboards reloaded.")));
            return true;
         }
      } else if (var1 instanceof Player) {
         Player var5 = (Player)var1;
         if (!var5.hasPermission("economysmpcore.leaderboards.use")) {
            var5.sendMessage(com.h2ph.c.A.B(this.A.A().A("no-permission", "&cYou have no permission for that!")));
            return true;
         } else {
            E.A(var5);
            return true;
         }
      } else {
         var1.sendMessage(com.h2ph.c.A.B(this.A.A().A("player-only", "&cOnly players can use this command!")));
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      ArrayList var5 = new ArrayList();
      if (var4.length == 1 && var1.hasPermission("economysmpcore.leaderboards.admin") && "reload".startsWith(var4[0].toLowerCase(Locale.ROOT))) {
         var5.add("reload");
      }

      return var5;
   }
}
