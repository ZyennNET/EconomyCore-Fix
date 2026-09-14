package com.prismcore.survival.orders;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import com.prismcore.survival.orders.cmd.OrdersCommand;
import com.prismcore.survival.orders.cmd.PrismOrderCommand;
import com.prismcore.survival.orders.gui.MenuListener;
import com.prismcore.survival.orders.input.ChatInputManager;
import com.prismcore.survival.orders.store.ConfigManager;
import com.prismcore.survival.orders.store.EnchantmentsManager;
import com.prismcore.survival.orders.store.FilterManager;
import com.prismcore.survival.orders.store.OrderManager;
import com.prismcore.survival.orders.store.PlayerStateManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class PrismOrders {
   private static PrismOrders inst;
   private final PrismSurvival plugin;
   private ConfigManager configManager;
   private FilterManager filterManager;
   private EnchantmentsManager enchantmentsManager;
   private OrderManager orderManager;
   private PlayerStateManager stateManager;
   private ChatInputManager chatInputManager;

   public PrismOrders(PrismSurvival var1) {
      this.plugin = var1;
   }

   public static PrismOrders inst() {
      return inst;
   }

   public PrismSurvival getPlugin() {
      return this.plugin;
   }

   public ConfigManager cfg() {
      return this.configManager;
   }

   public FilterManager filters() {
      return this.filterManager;
   }

   public EnchantmentsManager enchants() {
      return this.enchantmentsManager;
   }

   public OrderManager orders() {
      return this.orderManager;
   }

   public PlayerStateManager state() {
      return this.stateManager;
   }

   public ChatInputManager chat() {
      return this.chatInputManager;
   }

   public void init() {
      inst = this;
      this.configManager = new ConfigManager(this);
      this.filterManager = new FilterManager(this);
      this.enchantmentsManager = new EnchantmentsManager(this);
      this.orderManager = new OrderManager(this);
      this.stateManager = new PlayerStateManager(this);
      this.chatInputManager = new ChatInputManager(this);
      Bukkit.getPluginManager().registerEvents(new MenuListener(this), this.plugin);
      Bukkit.getPluginManager().registerEvents(this.chatInputManager, this.plugin);
      this.plugin.getCommand("order").setExecutor(new OrdersCommand(this));
      PrismOrderCommand var1 = new PrismOrderCommand(this);
      this.plugin.getCommand("prismorder").setExecutor(var1);
      this.plugin.getCommand("prismorder").setTabCompleter(var1);
   }

   public void shutdown() {
      if (this.orderManager != null) {
         this.orderManager.saveAllSync();
      }

   }

   public boolean canAfford(OfflinePlayer var1, double var2) {
      if (var1 != null && var1.isOnline()) {
         Player var4 = var1.getPlayer();
         if (var4 == null) {
            return false;
         } else {
            PlayerData var5 = this.plugin.getPlayerDataManager().get(var4.getUniqueId());
            return var5 != null && var5.getMoney() >= var2;
         }
      } else {
         return false;
      }
   }

   public boolean takeMoney(OfflinePlayer var1, double var2) {
      if (var1 != null && var1.isOnline()) {
         Player var4 = var1.getPlayer();
         if (var4 == null) {
            return false;
         } else {
            PlayerData var5 = this.plugin.getPlayerDataManager().get(var4.getUniqueId());
            if (var5 != null && !(var5.getMoney() < var2)) {
               var5.setMoney(var5.getMoney() - var2);
               this.plugin.getPlayerDataManager().savePlayer(var4.getUniqueId());
               return true;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean giveMoney(OfflinePlayer var1, double var2) {
      if (var1 != null && var1.isOnline()) {
         Player var4 = var1.getPlayer();
         if (var4 == null) {
            return false;
         } else {
            PlayerData var5 = this.plugin.getPlayerDataManager().get(var4.getUniqueId());
            if (var5 == null) {
               return false;
            } else {
               var5.setMoney(var5.getMoney() + var2);
               this.plugin.getPlayerDataManager().savePlayer(var4.getUniqueId());
               return true;
            }
         }
      } else {
         return false;
      }
   }
}
