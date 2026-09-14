package com.prismcore.survival.orders.input;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.Utils;
import com.prismcore.survival.orders.gui.NewOrderMenu;
import com.prismcore.survival.orders.gui.OrdersMainMenu;
import com.prismcore.survival.orders.gui.SelectItemMenu;
import com.prismcore.survival.orders.util.TaskUtil;
import com.prismcore.survival.orders.utils.SignInputUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatInputManager implements Listener {
   private final PrismOrders plugin;
   private final Map<UUID, Prompt> prompts = new HashMap();
   private final Map<UUID, Consumer<String>> rawPrompts = new HashMap();
   private final Map<UUID, NewOrderSession> sessions = new HashMap();

   public ChatInputManager(PrismOrders var1) {
      this.plugin = var1;
   }

   public void prompt(Player var1, Kind var2, String var3) {
      String var4 = this.plugin.getPlugin().getConfig().getString("input-method", "chat");
      if ("sign".equalsIgnoreCase(var4)) {
         var1.closeInventory();
         SignInputUtil.open(this.plugin.getPlugin(), var1, var3, (var3x) -> {
            if (var1.isOnline()) {
               String var4 = var3x == null ? "" : var3x.trim();
               this.processInput(var1, var2, var4);
            }
         });
      } else {
         this.prompts.put(var1.getUniqueId(), new Prompt(var2));
         var1.closeInventory();
         var1.sendMessage(var3);
      }
   }

   public void promptRaw(Player var1, String var2, Consumer<String> var3) {
      this.rawPrompts.put(var1.getUniqueId(), var3);
      var1.closeInventory();
      var1.sendMessage(Utils.formatColors(var2));
   }

   public void promptConfigured(Player var1, String var2, Consumer<String> var3) {
      String var4 = this.plugin.getPlugin().getConfig().getString("input-method", "chat");
      if ("sign".equalsIgnoreCase(var4)) {
         SignInputUtil.open(this.plugin.getPlugin(), var1, var2, var3);
      } else {
         this.promptRaw(var1, var2, var3);
      }

   }

   public NewOrderSession session(UUID var1) {
      return (NewOrderSession)this.sessions.computeIfAbsent(var1, (var0) -> new NewOrderSession());
   }

   public void clearSession(UUID var1) {
      this.sessions.remove(var1);
   }

   @EventHandler
   public void onChat(AsyncPlayerChatEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      Consumer var4 = (Consumer)this.rawPrompts.remove(var3);
      if (var4 != null) {
         var1.setCancelled(true);
         String var7 = var1.getMessage().trim();
         TaskUtil.runEntity(this.plugin.getPlugin(), var2, () -> var4.accept(var7));
      } else {
         Prompt var5 = (Prompt)this.prompts.remove(var3);
         if (var5 != null) {
            var1.setCancelled(true);
            String var6 = var1.getMessage().trim();
            this.processInput(var2, var5.kind, var6);
         }
      }
   }

   private void processInput(Player var1, Kind var2, String var3) {
      UUID var4 = var1.getUniqueId();
      switch (var2.ordinal()) {
         case 0:
            this.plugin.state().main(var4).search = var3;
            var1.sendMessage(Utils.formatColors("&aSearch set: &f" + var3));
            TaskUtil.runEntity(this.plugin.getPlugin(), var1, () -> (new OrdersMainMenu(this.plugin, var1)).open());
            break;
         case 1:
            this.plugin.state().items(var4).search = var3;
            var1.sendMessage(Utils.formatColors("&aSearch set: &f" + var3));
            TaskUtil.runEntity(this.plugin.getPlugin(), var1, () -> (new SelectItemMenu(this.plugin, var1)).open());
            break;
         case 2:
            try {
               int var9 = Integer.parseInt(var3);
               if (var9 <= 0) {
                  throw new NumberFormatException();
               }

               this.session(var4).amount = var9;
               var1.sendMessage(Utils.formatColors("&aAmount set: &f" + var9));
               TaskUtil.runEntity(this.plugin.getPlugin(), var1, () -> (new NewOrderMenu(this.plugin, var1)).open());
            } catch (NumberFormatException var8) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid amount.");
            }
            break;
         case 3:
            try {
               double var5 = Double.parseDouble(var3);
               if (var5 <= (double)0.0F) {
                  throw new NumberFormatException();
               }

               this.session(var4).priceEach = var5;
               var1.sendMessage(Utils.formatColors("&aPrice set: &f$" + var5));
               TaskUtil.runEntity(this.plugin.getPlugin(), var1, () -> (new NewOrderMenu(this.plugin, var1)).open());
            } catch (NumberFormatException var7) {
               var1.sendMessage(String.valueOf(ChatColor.RED) + "Invalid price.");
            }
      }

   }

   public static enum Kind {
      SEARCH_MAIN,
      SEARCH_SELECT,
      AMOUNT,
      PRICE;

      // $FF: synthetic method
      private static Kind[] $values() {
         return new Kind[]{SEARCH_MAIN, SEARCH_SELECT, AMOUNT, PRICE};
      }
   }

   public static class NewOrderSession {
      public String chosenItem;
      public Integer amount;
      public Double priceEach;
   }

   public static class Prompt {
      public final Kind kind;

      public Prompt(Kind var1) {
         this.kind = var1;
      }
   }
}
