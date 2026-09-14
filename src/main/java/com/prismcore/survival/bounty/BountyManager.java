package com.prismcore.survival.bounty;

import com.h2ph.PrismSurvival;
import com.h2ph.T.A.K;
import com.prismcore.survival.manager.PlayerData;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class BountyManager {
   private final PrismSurvival plugin;
   private final File bountiesFile;
   private final Map<UUID, InternalBounty> bounties = new ConcurrentHashMap();
   private final Map<UUID, SortType> playerSortPrefs = new HashMap();
   private static final DecimalFormat ONE_DECIMAL = new DecimalFormat("#.#");

   public BountyManager(PrismSurvival var1, K var2) {
      this.plugin = var1;
      this.bountiesFile = new File(var1.getDataFolder(), "bounties.yml");
      this.loadFromYaml();
      var1.getLogger().info("BountyManager using YAML storage (bounties.yml)");
   }

   public void addBounty(OfflinePlayer var1, OfflinePlayer var2, double var3) {
      if (!(var3 <= (double)0.0F)) {
         if (!this.withdrawMoney(var1, var3)) {
            if (var1.isOnline()) {
               ((Player)var1).sendMessage(String.valueOf(ChatColor.RED) + "You do not have enough money.");
            }

         } else {
            InternalBounty var5 = (InternalBounty)this.bounties.get(var2.getUniqueId());
            if (var5 == null) {
               this.bounties.put(var2.getUniqueId(), new InternalBounty(var2.getUniqueId(), var2.getName(), var3, System.currentTimeMillis()));
            } else {
               var5.amount += var3;
               var5.lastUpdated = System.currentTimeMillis();
               var5.name = var2.getName();
            }

            this.saveToYaml();
            this.plugin.getSchedulerAdapter().runTask(() -> {
               if (var1.isOnline()) {
                  Player var5 = (Player)var1;
                  String var10001 = this.formatBountyMoney(var3);
                  String var6 = ChatColor.translateAlternateColorCodes('&', "&7You added &4$" + var10001 + "&7 to &c" + var2.getName() + "'s&7 bounty.");
                  var5.sendMessage(var6);
                  var5.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var6));
               }

               if (var2.isOnline()) {
                  Player var7 = (Player)var2;
                  String var9 = var1.getName();
                  String var8 = ChatColor.translateAlternateColorCodes('&', "&c" + var9 + "&7 added &4$" + this.formatBountyMoney(var3) + "&7 to your bounty.");
                  var7.sendMessage(var8);
                  var7.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var8));
               }

            });
         }
      }
   }

   public List<K._A> getBounties(SortType var1) {
      ArrayList var2 = new ArrayList();

      for(InternalBounty var4 : this.bounties.values()) {
         var2.add(var4.toImmutable());
      }

      if (var1 == BountyManager.SortType.AMOUNT) {
         var2.sort((var0, var1x) -> Double.compare(var1x.A, var0.A));
      } else {
         var2.sort((var0, var1x) -> Long.compare(var1x.D, var0.D));
      }

      return var2;
   }

   public SortType getSortType(UUID var1) {
      return (SortType)this.playerSortPrefs.getOrDefault(var1, BountyManager.SortType.AMOUNT);
   }

   public void toggleSort(UUID var1) {
      SortType var2 = this.getSortType(var1);
      this.playerSortPrefs.put(var1, var2 == BountyManager.SortType.AMOUNT ? BountyManager.SortType.RECENT : BountyManager.SortType.AMOUNT);
   }

   public void refreshCache() {
   }

   public void claimBounty(Player var1, Player var2) {
      double var3 = this.getBountyAmount(var1.getUniqueId());
      if (!(var3 <= (double)0.0F)) {
         this.bounties.remove(var1.getUniqueId());
         this.saveToYaml();
         RegisteredServiceProvider var5 = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
         if (var5 != null) {
            ((Economy)var5.getProvider()).depositPlayer(var2, var3);
         } else {
            PlayerData var6 = this.plugin.getPlayerDataManager().get(var2.getUniqueId());
            var6.setMoney(var6.getMoney() + var3);
         }

         String var8 = this.formatBountyMoney(var3);
         Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&lBOUNTY! &c" + var2.getName() + " &fhas claimed the bounty of &c$" + var8 + " &fon &c" + var1.getName() + "&f!"));
         String var7 = ChatColor.translateAlternateColorCodes('&', "&fYou received &4$" + var8 + "&f for killing &c" + var1.getName());
         var2.sendMessage(var7);
         var2.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(var7));
      }
   }

   public double getBountyAmount(UUID var1) {
      InternalBounty var2 = (InternalBounty)this.bounties.get(var1);
      return var2 == null ? (double)0.0F : var2.amount;
   }

   private void loadFromYaml() {
      this.bounties.clear();
      if (!this.bountiesFile.exists()) {
         try {
            this.bountiesFile.getParentFile().mkdirs();
            this.bountiesFile.createNewFile();
            this.plugin.getLogger().info("Created new bounties.yml");
         } catch (IOException var10) {
            this.plugin.getLogger().severe("Could not create bounties.yml: " + var10.getMessage());
         }

      } else {
         YamlConfiguration var1 = YamlConfiguration.loadConfiguration(this.bountiesFile);

         for(String var3 : var1.getKeys(false)) {
            try {
               UUID var4 = UUID.fromString(var3);
               String var5 = var1.getString(var3 + ".name");
               double var6 = var1.getDouble(var3 + ".amount");
               long var8 = var1.getLong(var3 + ".lastUpdated");
               this.bounties.put(var4, new InternalBounty(var4, var5, var6, var8));
            } catch (Exception var11) {
               this.plugin.getLogger().warning("Failed to load bounty entry: " + var3);
            }
         }

         this.plugin.getLogger().info("Loaded " + this.bounties.size() + " bounties from YAML.");
      }
   }

   private void saveToYaml() {
      YamlConfiguration var1 = new YamlConfiguration();

      for(InternalBounty var3 : this.bounties.values()) {
         String var4 = var3.uuid.toString();
         var1.set(var4 + ".name", var3.name);
         var1.set(var4 + ".amount", var3.amount);
         var1.set(var4 + ".lastUpdated", var3.lastUpdated);
      }

      try {
         var1.save(this.bountiesFile);
      } catch (IOException var5) {
         this.plugin.getLogger().severe("Could not save bounties.yml: " + var5.getMessage());
      }

   }

   private boolean withdrawMoney(OfflinePlayer var1, double var2) {
      if (this.plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
         PlayerData var6 = this.plugin.getPlayerDataManager().get(var1.getUniqueId());
         if (var6.getMoney() >= var2) {
            var6.setMoney(var6.getMoney() - var2);
            return true;
         } else {
            return false;
         }
      } else {
         RegisteredServiceProvider var4 = this.plugin.getServer().getServicesManager().getRegistration(Economy.class);
         if (var4 == null) {
            return false;
         } else {
            Economy var5 = (Economy)var4.getProvider();
            return var5.withdrawPlayer(var1, var2).transactionSuccess();
         }
      }
   }

   public String formatBountyMoney(double var1) {
      if (var1 >= 1.0E12) {
         return this.formatSuffix(var1, 1.0E12, "t");
      } else if (var1 >= (double)1.0E9F) {
         return this.formatSuffix(var1, (double)1.0E9F, "b");
      } else if (var1 >= (double)1000000.0F) {
         return this.formatSuffix(var1, (double)1000000.0F, "m");
      } else if (var1 >= (double)1000.0F) {
         return this.formatSuffix(var1, (double)1000.0F, "k");
      } else {
         return var1 % (double)1.0F == (double)0.0F ? String.format("%.0f", var1) : String.format("%.1f", var1);
      }
   }

   private String formatSuffix(double var1, double var3, String var5) {
      double var6 = var1 / var3;
      String var10000 = ONE_DECIMAL.format(var6);
      return var10000 + var5;
   }

   private static class InternalBounty {
      UUID uuid;
      String name;
      double amount;
      long lastUpdated;

      InternalBounty(UUID var1, String var2, double var3, long var5) {
         this.uuid = var1;
         this.name = var2;
         this.amount = var3;
         this.lastUpdated = var5;
      }

      K._A toImmutable() {
         return new K._A(this.uuid, this.name, this.amount, this.lastUpdated);
      }
   }

   public static enum SortType {
      AMOUNT,
      RECENT;

      // $FF: synthetic method
      private static SortType[] $values() {
         return new SortType[]{AMOUNT, RECENT};
      }
   }
}
