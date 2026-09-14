package com.h2ph.L;

import com.h2ph.PrismSurvival;
import com.prismcore.survival.manager.PlayerData;
import java.util.Collections;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class A implements Economy {
   private final PrismSurvival A;

   public A(PrismSurvival var1) {
      this.A = var1;
   }

   public boolean isEnabled() {
      return this.A.isEnabled();
   }

   public String getName() {
      return "PrismEconomy";
   }

   public boolean hasBankSupport() {
      return false;
   }

   public int fractionalDigits() {
      return 2;
   }

   public String format(double var1) {
      Object[] var10001 = new Object[]{var1};
      return "$" + String.format("%.2f", var10001);
   }

   public String currencyNamePlural() {
      return "Dollars";
   }

   public String currencyNameSingular() {
      return "Dollar";
   }

   public boolean hasAccount(String var1) {
      return true;
   }

   public boolean hasAccount(OfflinePlayer var1) {
      return true;
   }

   public boolean hasAccount(String var1, String var2) {
      return true;
   }

   public boolean hasAccount(OfflinePlayer var1, String var2) {
      return true;
   }

   public double getBalance(String var1) {
      OfflinePlayer var2 = Bukkit.getOfflinePlayer(var1);
      return this.getBalance(var2);
   }

   public double getBalance(OfflinePlayer var1) {
      PlayerData var2 = this.A.getPlayerDataManager().get(var1.getUniqueId());
      return var2.getMoney();
   }

   public double getBalance(String var1, String var2) {
      return this.getBalance(var1);
   }

   public double getBalance(OfflinePlayer var1, String var2) {
      return this.getBalance(var1);
   }

   public boolean has(String var1, double var2) {
      return this.getBalance(var1) >= var2;
   }

   public boolean has(OfflinePlayer var1, double var2) {
      return this.getBalance(var1) >= var2;
   }

   public boolean has(String var1, String var2, double var3) {
      return this.has(var1, var3);
   }

   public boolean has(OfflinePlayer var1, String var2, double var3) {
      return this.has(var1, var3);
   }

   public EconomyResponse withdrawPlayer(String var1, double var2) {
      OfflinePlayer var4 = Bukkit.getOfflinePlayer(var1);
      return this.withdrawPlayer(var4, var2);
   }

   public EconomyResponse withdrawPlayer(OfflinePlayer var1, double var2) {
      if (var2 < (double)0.0F) {
         return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.FAILURE, "Cannot withdraw negative amount");
      } else {
         PlayerData var4 = this.A.getPlayerDataManager().get(var1.getUniqueId());
         double var5 = var4.getMoney();
         if (var5 < var2) {
            return new EconomyResponse((double)0.0F, var5, ResponseType.FAILURE, "Insufficient funds");
         } else {
            var4.removeMoney(var2);
            this.A.getPlayerDataManager().savePlayer(var1.getUniqueId());
            return new EconomyResponse(var2, var4.getMoney(), ResponseType.SUCCESS, (String)null);
         }
      }
   }

   public EconomyResponse withdrawPlayer(String var1, String var2, double var3) {
      return this.withdrawPlayer(var1, var3);
   }

   public EconomyResponse withdrawPlayer(OfflinePlayer var1, String var2, double var3) {
      return this.withdrawPlayer(var1, var3);
   }

   public EconomyResponse depositPlayer(String var1, double var2) {
      OfflinePlayer var4 = Bukkit.getOfflinePlayer(var1);
      return this.depositPlayer(var4, var2);
   }

   public EconomyResponse depositPlayer(OfflinePlayer var1, double var2) {
      if (var2 < (double)0.0F) {
         return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.FAILURE, "Cannot deposit negative amount");
      } else {
         PlayerData var4 = this.A.getPlayerDataManager().get(var1.getUniqueId());
         var4.addMoney(var2);
         this.A.getPlayerDataManager().savePlayer(var1.getUniqueId());
         return new EconomyResponse(var2, var4.getMoney(), ResponseType.SUCCESS, (String)null);
      }
   }

   public EconomyResponse depositPlayer(String var1, String var2, double var3) {
      return this.depositPlayer(var1, var3);
   }

   public EconomyResponse depositPlayer(OfflinePlayer var1, String var2, double var3) {
      return this.depositPlayer(var1, var3);
   }

   public EconomyResponse createBank(String var1, String var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse createBank(String var1, OfflinePlayer var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse deleteBank(String var1) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankBalance(String var1) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankHas(String var1, double var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankWithdraw(String var1, double var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse bankDeposit(String var1, double var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankOwner(String var1, String var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankOwner(String var1, OfflinePlayer var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankMember(String var1, String var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public EconomyResponse isBankMember(String var1, OfflinePlayer var2) {
      return new EconomyResponse((double)0.0F, (double)0.0F, ResponseType.NOT_IMPLEMENTED, "Banks not supported");
   }

   public List<String> getBanks() {
      return Collections.emptyList();
   }

   public boolean createPlayerAccount(String var1) {
      return true;
   }

   public boolean createPlayerAccount(OfflinePlayer var1) {
      return true;
   }

   public boolean createPlayerAccount(String var1, String var2) {
      return true;
   }

   public boolean createPlayerAccount(OfflinePlayer var1, String var2) {
      return true;
   }
}
