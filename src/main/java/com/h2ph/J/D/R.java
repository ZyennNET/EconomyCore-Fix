package com.h2ph.J.D;

import com.h2ph.PrismSurvival;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class R implements CommandExecutor, TabCompleter {
   private final PrismSurvival A;

   public R(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (var4.length > 0 && var4[0].equalsIgnoreCase("admin")) {
         if (!var5.hasPermission("economysmpcore.admin.advisor")) {
            var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to use this command.");
            return true;
         } else {
            this.B(var5);
            return true;
         }
      } else {
         this.A(var5);
         return true;
      }
   }

   private void B(Player var1) {
      ItemStack var2 = new ItemStack(Material.WRITABLE_BOOK);
      BookMeta var3 = (BookMeta)var2.getItemMeta();
      if (var3 != null) {
         var3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Advisor Book Editor"));
         List var4 = this.A.getActiveAdvisorPages();
         if (var4 != null && !var4.isEmpty()) {
            var3.setPages(var4);
         }

         var2.setItemMeta(var3);
      }

      this.A.markPlayerAsAdvisorWriter(var1.getUniqueId());
      int var5 = var1.getInventory().firstEmpty();
      if (var5 != -1) {
         var1.getInventory().setItem(var5, var2);
         var1.sendMessage(String.valueOf(ChatColor.GREEN) + "You have been given the Advisor Editor book.");
         var1.sendMessage(String.valueOf(ChatColor.GRAY) + "Write your content and sign/save the book to update the /advisor command.");
      } else {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Your inventory is full! Please clear a slot.");
         this.A.unmarkPlayerAsAdvisorWriter(var1.getUniqueId());
      }

   }

   private void A(Player var1) {
      if (!this.A.hasActiveAdvisor()) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "There is no advisor content available yet.");
      } else {
         ItemStack var2 = new ItemStack(Material.WRITTEN_BOOK);
         BookMeta var3 = (BookMeta)var2.getItemMeta();
         if (var3 != null) {
            var3.setTitle("Server Advisor");
            var3.setAuthor("Server Admin");
            var3.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6Server Advisor"));
            var3.setPages(this.A.getActiveAdvisorPages());
            var2.setItemMeta(var3);
         }

         var1.openBook(var2);
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length == 1) {
         ArrayList var5 = new ArrayList();
         if (var1.hasPermission("economysmpcore.admin.advisor") && "admin".startsWith(var4[0].toLowerCase())) {
            var5.add("admin");
         }

         return var5;
      } else {
         return Collections.emptyList();
      }
   }
}
