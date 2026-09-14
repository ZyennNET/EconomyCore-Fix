package com.h2ph.J.B.F;

import com.h2ph.PrismSurvival;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class C implements CommandExecutor, Listener {
   private final JavaPlugin C;
   private final String D;
   private final String B;
   private final Map<UUID, _A> A;
   private final Map<UUID, Integer> E;

   public C(JavaPlugin var1) {
      String var10001 = String.valueOf(ChatColor.DARK_GRAY);
      this.D = var10001 + this.A("suspicious activity");
      var10001 = String.valueOf(ChatColor.DARK_GRAY);
      this.B = var10001 + this.A("security") + " " + String.valueOf(ChatColor.RESET);
      this.A = new HashMap();
      this.E = new HashMap();
      this.C = var1;
      this.A();
      this.D();
   }

   private void D() {
      if (this.C instanceof PrismSurvival) {
         ((PrismSurvival)this.C).getSchedulerAdapter().runTaskTimer(() -> {
            if (!this.A.isEmpty()) {
               this.A.clear();
            }

         }, 2400L, 2400L);
      } else {
         (new BukkitRunnable() {
            public void run() {
               if (!C.this.A.isEmpty()) {
                  C.this.A.clear();
               }

            }
         }).runTaskTimer(this.C, 2400L, 2400L);
      }

   }

   private void A() {
      try {
         Class var1 = Class.forName("org.apache.logging.log4j.LogManager");
         Class var2 = Class.forName("org.apache.logging.log4j.core.Logger");
         Class var3 = Class.forName("org.apache.logging.log4j.core.Filter");
         final Class var4 = Class.forName("org.apache.logging.log4j.core.Filter$Result");
         Object var5 = var1.getMethod("getRootLogger").invoke((Object)null);
         Object var6 = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{var3}, new InvocationHandler() {
            public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
               if (var2.getName().equals("filter") && var3.length > 0) {
                  Object var4x = var3[0];
                  if (var4x != null) {
                     Method var5 = var4x.getClass().getMethod("getMessage");
                     Object var6 = var5.invoke(var4x);
                     Method var7 = var6.getClass().getMethod("getFormattedMessage");
                     String var8 = (String)var7.invoke(var6);
                     C.this.B(var8);
                  }
               }

               return Enum.valueOf(var4, "NEUTRAL");
            }
         });
         Method var7 = var2.getMethod("addFilter", var3);
         var7.invoke(var5, var6);
         Bukkit.getLogger().info(this.B + "Log Hook Active.");
      } catch (Exception var8) {
         Bukkit.getLogger().warning(this.B + "Log Hook Failed. Using fallback.");
         this.C();
      }

   }

   private void C() {
      Bukkit.getLogger().addHandler(new Handler() {
         public void publish(LogRecord var1) {
            if (var1.getMessage() != null) {
               C.this.B(var1.getMessage());
            }

         }

         public void flush() {
         }

         public void close() throws SecurityException {
         }
      });
   }

   private void B(String var1) {
      String var2 = ChatColor.stripColor(var1).replaceAll("\\u001B\\[[;\\d]*m", "");
      if (var2.contains("Grim") && (var2.contains("»") || var2.contains(">>")) && var2.contains("failed")) {
         this.A(var2, "Grim");
      } else if (!var2.contains("Matrix") && !var2.contains("[Matrix]")) {
         if (var2.contains("Vulcan") && var2.contains("failed")) {
            this.A(var2, "Vulcan");
         }
      } else if (var2.contains("using") || var2.contains("tried") || var2.contains("failed") || var2.contains("combat") || var2.contains("abnormally") || var2.contains("tring") || var2.contains("kicked") || var2.contains("speed")) {
         this.A(var2, "Matrix");
      }

   }

   private void A(String var1, String var2) {
      for(Player var4 : Bukkit.getOnlinePlayers()) {
         if (var1.contains(var4.getName())) {
            this.A(var4, var2, var1);
            break;
         }
      }

   }

   private void A(Player var1, String var2, String var3) {
      this.A.putIfAbsent(var1.getUniqueId(), new _A(var1.getName()));
      _A var4 = (_A)this.A.get(var1.getUniqueId());
      ++var4.D;
      var4.C = System.currentTimeMillis();
      String var5 = "Check";
      if (var2.equals("Grim")) {
         try {
            int var6 = var3.indexOf("failed");
            if (var6 != -1) {
               String var7 = var3.substring(var6 + 7).trim();
               int var8 = var7.indexOf("(");
               if (var8 != -1) {
                  var5 = var7.substring(0, var8).trim();
                  if (var5.endsWith("*")) {
                     var5 = var5.substring(0, var5.length() - 1);
                  }
               } else {
                  var5 = var7.split(" ")[0];
               }
            }
         } catch (Exception var9) {
         }
      } else if (var2.equals("Vulcan") && var3.contains("failed")) {
         int var11 = var3.indexOf("failed");
         if (var11 != -1 && var11 + 7 < var3.length()) {
            String var13 = var3.substring(var11 + 7);
            var5 = var13.split(" ")[0];
         }
      } else if (var2.equals("Matrix")) {
         Pattern var10 = Pattern.compile("\\(([^)]+)\\)");
         Matcher var12 = var10.matcher(var3);
         if (var12.find()) {
            var5 = var12.group(1);
         } else if (var3.contains("combat")) {
            var5 = "KillAura";
         } else if (var3.contains("speed")) {
            var5 = "Speed";
         } else if (var3.contains("abnormally")) {
            var5 = "Move";
         } else if (var3.contains("tring")) {
            var5 = "Delay";
         } else if (var3.contains("bridge")) {
            var5 = "Scaffold";
         } else if (var3.contains("reach")) {
            var5 = "Reach";
         } else {
            var5 = "Matrix";
         }
      }

      var4.A = var5;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var1.hasPermission("economysmpcore.admin.sus")) {
         String var10001 = String.valueOf(ChatColor.DARK_GRAY);
         var1.sendMessage(var10001 + this.A("no permission"));
         return true;
      } else {
         if (var1 instanceof Player) {
            this.A((Player)var1, 0);
         } else {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Console cannot use GUI.");
         }

         return true;
      }
   }

   private void A(Player var1, int var2) {
      Inventory var3 = Bukkit.createInventory((InventoryHolder)null, 54, this.D);
      ArrayList var4 = new ArrayList();

      for(_A var6 : this.A.values()) {
         Player var7 = Bukkit.getPlayer(var6.B);
         if (var7 != null && var7.isOnline()) {
            var4.add(var6);
         }
      }

      var4.sort((var0, var1x) -> Integer.compare(var1x.D, var0.D));
      byte var13 = 45;
      int var14 = var4.size();
      int var15 = (int)Math.ceil((double)var14 / (double)var13);
      if (var2 < 0) {
         var2 = 0;
      }

      if (var15 > 0 && var2 >= var15) {
         var2 = var15 - 1;
      }

      int var8 = var2 * var13;
      int var9 = Math.min(var8 + var13, var14);

      for(int var10 = var8; var10 < var9; ++var10) {
         _A var11 = (_A)var4.get(var10);
         Player var12 = Bukkit.getPlayer(var11.B);
         if (var12 != null) {
            var3.addItem(new ItemStack[]{this.A(var12, var11)});
         }
      }

      var3.setItem(49, this.A(Material.NETHER_STAR, String.valueOf(ChatColor.AQUA) + this.A("refresh"), String.valueOf(ChatColor.GRAY) + "Click to reload"));
      if (var2 > 0) {
         var3.setItem(45, this.A(Material.ARROW, String.valueOf(ChatColor.GREEN) + "ᴘʀᴇᴠɪᴏᴜѕ", String.valueOf(ChatColor.WHITE) + "Click to previous"));
      }

      if (var2 < var15 - 1) {
         var3.setItem(53, this.A(Material.ARROW, String.valueOf(ChatColor.GREEN) + "ɴᴇхᴛ ᴘᴀɢᴇ", String.valueOf(ChatColor.WHITE) + "Click to next"));
      }

      var1.openInventory(var3);
      this.E.put(var1.getUniqueId(), var2);
   }

   private ItemStack A(Player var1, _A var2) {
      ItemStack var3 = new ItemStack(Material.PLAYER_HEAD);
      SkullMeta var4 = (SkullMeta)var3.getItemMeta();
      if (var4 != null) {
         var4.setOwningPlayer(var1);
         String var10001 = String.valueOf(ChatColor.RED);
         var4.setDisplayName(var10001 + var1.getName());
         long var5 = (System.currentTimeMillis() - var2.C) / 1000L;
         ArrayList var7 = new ArrayList();
         var10001 = String.valueOf(ChatColor.GRAY);
         var7.add(var10001 + "Flag: " + String.valueOf(ChatColor.LIGHT_PURPLE) + var2.A);
         var10001 = String.valueOf(ChatColor.GRAY);
         var7.add(var10001 + "Total Flags: " + String.valueOf(ChatColor.WHITE) + var2.D);
         var10001 = String.valueOf(ChatColor.GRAY);
         var7.add(var10001 + "Last: " + var5 + "s ago");
         var7.add("");
         var7.add(String.valueOf(ChatColor.YELLOW) + "Click to Teleport");
         var4.setLore(var7);
         var3.setItemMeta(var4);
      }

      return var3;
   }

   private ItemStack A(Material var1, String var2, String var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      if (var5 != null) {
         var5.setDisplayName(var2);
         var5.setLore(Collections.singletonList(var3));
         var4.setItemMeta(var5);
      }

      return var4;
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent var1) {
      if (var1.getView().getTitle().equals(this.D)) {
         var1.setCancelled(true);
         if (var1.getCurrentItem() != null && var1.getCurrentItem().getType() != Material.AIR) {
            Player var2 = (Player)var1.getWhoClicked();
            ItemStack var3 = var1.getCurrentItem();
            int var4 = (Integer)this.E.getOrDefault(var2.getUniqueId(), 0);
            if (var3.getType() == Material.NETHER_STAR) {
               this.A(var2, var4);
            } else if (var3.getType() == Material.ARROW && var3.getItemMeta().getDisplayName().contains("ᴘʀᴇᴠɪᴏᴜѕ")) {
               this.A(var2, var4 - 1);
            } else if (var3.getType() == Material.ARROW && var3.getItemMeta().getDisplayName().contains("ɴᴇхᴛ ᴘᴀɢᴇ")) {
               this.A(var2, var4 + 1);
            } else if (var3.getType() == Material.PLAYER_HEAD) {
               SkullMeta var5 = (SkullMeta)var3.getItemMeta();
               if (var5 != null && var5.getOwningPlayer() != null) {
                  Player var6 = var5.getOwningPlayer().getPlayer();
                  if (var6 != null && var6.isOnline()) {
                     var2.teleportAsync(var6.getLocation());
                     String var10001 = this.B;
                     var2.sendMessage(var10001 + String.valueOf(ChatColor.YELLOW) + this.A("teleported to") + " " + var6.getName());
                     var2.closeInventory();
                  }
               }
            }

         }
      }
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

   private static class _A {
      String B;
      int D = 0;
      String A = "None";
      long C = System.currentTimeMillis();

      public _A(String var1) {
         this.B = var1;
      }
   }
}
