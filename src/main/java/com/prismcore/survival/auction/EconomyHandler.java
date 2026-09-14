package com.prismcore.survival.auction;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyHandler {
   private static Economy vaultEcon;
   private static PrismSurvival plugin;
   private static boolean useVault;

   public static void setup(PrismSurvival var0, boolean var1) {
      plugin = var0;
      useVault = var1;
      if (useVault) {
         if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("Vault not found! Falling back to internal economy.");
            useVault = false;
            return;
         }

         RegisteredServiceProvider var2 = plugin.getServer().getServicesManager().getRegistration(Economy.class);
         if (var2 == null) {
            plugin.getLogger().warning("Vault found, but no Economy provider registered! Falling back to internal economy.");
            useVault = false;
            return;
         }

         vaultEcon = (Economy)var2.getProvider();
         plugin.getLogger().info("EconomyHandler hooked into Vault.");
      } else {
         plugin.getLogger().info("EconomyHandler using internal economy (Configured or Fallback).");
      }

   }

   public static boolean chargePlayer(Player var0, double var1) {
      if (useVault && vaultEcon != null) {
         if (vaultEcon.getBalance(var0) < var1) {
            return false;
         } else {
            EconomyResponse var4 = vaultEcon.withdrawPlayer(var0, var1);
            return var4.transactionSuccess();
         }
      } else {
         PlayerData var3 = plugin.getPlayerDataManager().get(var0.getUniqueId());
         if (var3 == null) {
            return false;
         } else if (var3.getMoney() < var1) {
            return false;
         } else {
            var3.removeMoney(var1);
            return true;
         }
      }
   }

   public static void depositPlayer(Player var0, double var1) {
      if (useVault && vaultEcon != null) {
         vaultEcon.depositPlayer(var0, var1);
      } else {
         PlayerData var3 = plugin.getPlayerDataManager().get(var0.getUniqueId());
         if (var3 != null) {
            var3.addMoney(var1);
         }
      }

   }

   public static boolean depositByName(String var0, double var1) {
      if (useVault && vaultEcon != null) {
         OfflinePlayer var5 = plugin.getServer().getOfflinePlayer(var0);
         EconomyResponse var6 = vaultEcon.depositPlayer(var5, var1);
         return var6 != null && var6.transactionSuccess();
      } else {
         OfflinePlayer var3 = plugin.getServer().getOfflinePlayer(var0);
         if (var3.hasPlayedBefore() || var3.isOnline()) {
            PlayerData var4 = plugin.getPlayerDataManager().get(var3.getUniqueId());
            if (var4 != null) {
               var4.addMoney(var1);
               return true;
            }
         }

         return false;
      }
   }

   public static boolean usingVault() {
      return useVault && vaultEcon != null;
   }
}
