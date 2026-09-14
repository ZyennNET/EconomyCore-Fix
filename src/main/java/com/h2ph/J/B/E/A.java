package com.h2ph.J.B.E;

import com.h2ph.PrismSurvival;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class A implements CommandExecutor {
   private final PrismSurvival A;

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage("This command can only be used by a player.");
         return true;
      } else if (!var5.isOp() && !var5.hasPermission("emporium.update")) {
         var5.sendMessage(String.valueOf(ChatColor.RED) + "You don't have permission to use this command.");
         return true;
      } else {
         String var6 = "ѕᴇʀᴠᴇʀ ᴜᴘᴅᴀᴛᴇ";
         String var7 = "%player%";
         HashMap var8 = new HashMap();
         var8.put("opened", "&aWrite your update and sign/save it — it will be queued for the next joiner.");
         var8.put("given_in_hand", "&aA writable book has been placed in your hand. Write and sign it to queue the update.");
         var8.put("timeout", "&eUpdate timed out — reopen with /update if you still want to send it.");
         var8.put("no-permission", "&cYou don't have permission to use this command.");
         var8.put("queued", "&aUpdate queued: it will be shown to the next player who joins.");
         ItemStack var9 = new ItemStack(Material.WRITABLE_BOOK);
         BookMeta var10 = (BookMeta)var9.getItemMeta();
         if (var10 != null) {
            var10.setTitle(var6);
            String var11 = "%player%".equals(var7) ? var5.getName() : this.A.getName();
            var10.setAuthor(var11);
            String var12 = "&aѕᴇʀᴠᴇʀ ᴜᴘᴅᴀᴛᴇ";
            var10.setDisplayName(ChatColor.translateAlternateColorCodes('&', var12));
            var10.setPages(Collections.singletonList(""));
            var9.setItemMeta(var10);
         }

         this.A.markPlayerAsUpdateWriter(var5.getUniqueId());
         var5.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', (String)var8.get("opened")));
         this.A.getLogger().info("Player marked as update writer: " + var5.getName());
         this.A.getSchedulerAdapter().runAtLocation(var5.getLocation(), () -> {
            boolean var4 = false;

            try {
               try {
                  Method var5x = Player.class.getMethod("openBook", ItemStack.class);
                  if (var5x != null) {
                     var5x.invoke(var5, var9);
                     var4 = true;
                  }
               } catch (NoSuchMethodException var9x) {
               } catch (Throwable var10) {
               }

               if (!var4) {
                  int var12 = var5.getInventory().getHeldItemSlot();
                  ItemStack var6 = var5.getInventory().getItem(var12);
                  var5.setMetadata("update_prev_hand", new FixedMetadataValue(this.A, var6));
                  var5.setMetadata("update_prev_slot", new FixedMetadataValue(this.A, var12));
                  var5.setMetadata("update_given_book", new FixedMetadataValue(this.A, true));
                  var5.getInventory().setItem(var12, var9);
                  var5.updateInventory();

                  try {
                     var5.openBook(var9);
                  } catch (Throwable var8x) {
                  }

                  var5.sendMessage(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', (String)var8.get("given_in_hand")));
               }
            } catch (Throwable var11) {
               this.A.unmarkPlayerAsUpdateWriter(var5.getUniqueId());
               String var10001 = String.valueOf(ChatColor.RED);
               var5.sendMessage(var10001 + "Failed to open book editor: " + var11.getMessage());
            }

         });
         this.A.getSchedulerAdapter().runTaskLater(() -> {
            if (this.A.isPlayerMarkedAsUpdateWriter(var5.getUniqueId())) {
               this.A.unmarkPlayerAsUpdateWriter(var5.getUniqueId());
               this.A.getLogger().info("Auto-unmarked update writer due to timeout: " + var5.getName());

               try {
                  var5.sendMessage(String.valueOf(ChatColor.YELLOW) + "Update timed out — reopen with /update if you still want to send it.");
               } catch (Throwable var3) {
               }
            }

         }, 2400L);
         return true;
      }
   }
}
