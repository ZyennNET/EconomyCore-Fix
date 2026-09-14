package com.h2ph.J.D;

import java.util.Collections;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class W implements CommandExecutor {
   private final com.h2ph.N.B A;

   public W(com.h2ph.N.B var1) {
      this.A = var1;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player var5)) {
         var1.sendMessage(String.valueOf(ChatColor.RED) + "Only players can use this command.");
         return true;
      } else if (var4.length == 0) {
         var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         return true;
      } else {
         String var6 = var4[0];
         if (var6.equalsIgnoreCase(var5.getName())) {
            var5.playSound(var5.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            return true;
         } else {
            Player var7 = Bukkit.getPlayer(var6);
            if (var7 == null) {
               this.A(var5, "&cThis user is not online.");
               return true;
            } else {
               openConfirmationGUI(var5, var7, com.h2ph.N.D._A.B);
               return true;
            }
         }
      }
   }

   private void A(Player var1, String var2) {
      var1.sendMessage(ChatColor.translateAlternateColorCodes('&', var2));
      var1.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', var2)));
      var1.playSound(var1.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
   }

   public static void openConfirmationGUI(Player var0, Player var1, com.h2ph.N.D._A var2) {
      Inventory var3 = Bukkit.createInventory(new com.h2ph.W.H._A(var1.getName(), var2), 27, com.h2ph.W.H.GUI_TITLE);
      ItemStack var4 = new ItemStack(Material.RED_STAINED_GLASS_PANE);
      ItemMeta var5 = var4.getItemMeta();
      var5.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&4ᴄᴀɴᴄᴇʟ"));
      var5.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&fClick to cancel the teleport")));
      var4.setItemMeta(var5);
      var3.setItem(10, var4);
      ItemStack var6;
      String var7;
      switch (var1.getWorld().getEnvironment()) {
         case NETHER:
            var6 = new ItemStack(Material.NETHERRACK);
            var7 = "Nether";
            break;
         case THE_END:
            var6 = new ItemStack(Material.END_STONE);
            var7 = "End";
            break;
         default:
            var6 = new ItemStack(Material.GRASS_BLOCK);
            var7 = "Overworld";
      }

      ItemMeta var8 = var6.getItemMeta();
      var8.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aʟᴏᴄᴀᴛɪᴏɴ"));
      var8.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&7" + var7)));
      var6.setItemMeta(var8);
      var3.setItem(12, var6);
      ItemStack var9 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var10 = (SkullMeta)var9.getItemMeta();
      var10.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aᴘʟᴀʏᴇʀ"));
      var10.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&7" + var1.getName())));
      var10.setOwningPlayer(var1);
      var9.setItemMeta(var10);
      var3.setItem(13, var9);
      ItemStack var11 = new ItemStack(Material.FEATHER);
      ItemMeta var12 = var11.getItemMeta();
      var12.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aʀᴇɢɪᴏɴ"));
      String var13 = A(var1);
      var12.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', "&7Europe (&5" + var13 + "ms&7)")));
      var11.setItemMeta(var12);
      var3.setItem(14, var11);
      ItemStack var14 = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
      ItemMeta var15 = var14.getItemMeta();
      var15.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aᴄᴏɴꜰɪʀᴍ"));
      String var16;
      if (var2 == com.h2ph.N.D._A.B) {
         var16 = "&fClick to send " + var1.getName() + " a tpa request";
      } else {
         var16 = "&fClick to teleport " + var1.getName() + " to you";
      }

      var15.setLore(Collections.singletonList(ChatColor.translateAlternateColorCodes('&', var16)));
      var14.setItemMeta(var15);
      var3.setItem(16, var14);
      var0.openInventory(var3);
   }

   private static String A(Player var0) {
      try {
         return String.valueOf(var0.getPing());
      } catch (Exception var4) {
         try {
            Object var2 = var0.getClass().getMethod("getHandle").invoke(var0);
            return String.valueOf(var2.getClass().getField("ping").getInt(var2));
         } catch (Exception var3) {
            return "0";
         }
      }
   }
}
