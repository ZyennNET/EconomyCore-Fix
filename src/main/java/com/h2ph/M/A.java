package com.h2ph.M;

import com.cryptomorin.xseries.XMaterial;
import com.h2ph.PrismSurvival;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class A implements Listener, CommandExecutor {
   private final PrismSurvival H;
   private boolean C;
   private final Map<UUID, Boolean> G;
   private FileConfiguration F;
   private final File M;
   private String D;
   private String I;
   private String A;
   private String B;
   private String K;
   private String J;
   private String L;
   private String E;

   public A(PrismSurvival var1) {
      this.H = var1;
      this.M = new File(var1.getDataFolder(), "chainmail/playerdata.yml");
      this.G = new HashMap();
      this.B();
      this.A();
      var1.getServer().getPluginManager().registerEvents(this, var1);
      var1.getCommand("chainmail").setExecutor(this);
   }

   private void B() {
      File var1 = new File(this.H.getDataFolder(), "chainmail/config.yml");
      if (!var1.exists()) {
         var1.getParentFile().mkdirs();
         this.H.saveResource("chainmail/config.yml", false);
      }

      YamlConfiguration var2 = YamlConfiguration.loadConfiguration(var1);
      this.C = ((FileConfiguration)var2).getBoolean("global-enabled", true);
      this.D = this.A(((FileConfiguration)var2).getString("messages.prefix", "&7[&6Chainmail&7]&r "));
      this.I = this.A(((FileConfiguration)var2).getString("messages.kit-given", "&aYou received your respawn kit!"));
      this.A = this.A(((FileConfiguration)var2).getString("messages.player-toggled-on", "&aYou will now receive the respawn kit on death."));
      this.B = this.A(((FileConfiguration)var2).getString("messages.player-toggled-off", "&cYou will no longer receive the respawn kit on death."));
      this.K = this.A(((FileConfiguration)var2).getString("messages.admin-toggled-on", "&aGlobal respawn kit enabled."));
      this.J = this.A(((FileConfiguration)var2).getString("messages.admin-toggled-off", "&cGlobal respawn kit disabled."));
      this.L = this.A(((FileConfiguration)var2).getString("messages.reloaded", "&aChainmail config reloaded."));
      this.E = this.A(((FileConfiguration)var2).getString("messages.no-permission", "&cYou don't have permission."));
   }

   private void A() {
      if (!this.M.exists()) {
         this.M.getParentFile().mkdirs();

         try {
            this.M.createNewFile();
         } catch (Exception var4) {
            this.H.getLogger().warning("Could not create chainmail/playerdata.yml");
         }
      }

      this.F = YamlConfiguration.loadConfiguration(this.M);
      this.G.clear();

      for(String var2 : this.F.getKeys(false)) {
         boolean var3 = this.F.getBoolean(var2 + ".want-kit", true);
         this.G.put(UUID.fromString(var2), var3);
      }

   }

   private void A(UUID var1) {
      boolean var2 = (Boolean)this.G.getOrDefault(var1, true);
      this.F.set(var1.toString() + ".want-kit", var2);

      try {
         this.F.save(this.M);
      } catch (Exception var4) {
         this.H.getLogger().warning("Failed to save chainmail player data for " + String.valueOf(var1));
      }

   }

   private boolean A(Player var1) {
      return !this.C ? false : (Boolean)this.G.getOrDefault(var1.getUniqueId(), true);
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent var1) {
      if (this.A(var1.getPlayer())) {
         Player var2 = var1.getPlayer();
         PlayerInventory var3 = var2.getInventory();
         var3.setHelmet(XMaterial.CHAINMAIL_HELMET.parseItem());
         var3.setChestplate(XMaterial.CHAINMAIL_CHESTPLATE.parseItem());
         var3.setLeggings(XMaterial.CHAINMAIL_LEGGINGS.parseItem());
         var3.setBoots(XMaterial.CHAINMAIL_BOOTS.parseItem());
         ItemStack var4 = XMaterial.STONE_SWORD.parseItem();
         ItemStack var5 = XMaterial.COOKED_BEEF.parseItem();
         if (var5 != null) {
            var5.setAmount(16);
         }

         var3.addItem(new ItemStack[]{var4});
         if (var5 != null) {
            var3.addItem(new ItemStack[]{var5});
         }

         var2.sendMessage(this.D + this.I);
      }
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!var2.getName().equalsIgnoreCase("chainmail")) {
         return false;
      } else if (var4.length > 0 && var4[0].equalsIgnoreCase("admin")) {
         if (!var1.hasPermission("economysmpcore.chainmail.admin")) {
            var1.sendMessage(this.E);
            return true;
         } else if (var4.length > 1 && var4[1].equalsIgnoreCase("reload")) {
            this.B();
            this.A();
            var1.sendMessage(this.D + this.L);
            return true;
         } else if (var4.length > 1 && var4[1].equalsIgnoreCase("toggle")) {
            this.C = !this.C;
            File var9 = new File(this.H.getDataFolder(), "chainmail/config.yml");
            YamlConfiguration var10 = YamlConfiguration.loadConfiguration(var9);
            ((FileConfiguration)var10).set("global-enabled", this.C);

            try {
               ((FileConfiguration)var10).save(var9);
            } catch (Exception var8) {
               this.H.getLogger().warning("Failed to save chainmail config");
            }

            String var11 = this.D;
            var1.sendMessage(var11 + (this.C ? this.K : this.J));
            return true;
         } else {
            var1.sendMessage(String.valueOf(ChatColor.RED) + "Usage: /chainmail admin <reload|toggle>");
            return true;
         }
      } else if (!(var1 instanceof Player)) {
         var1.sendMessage("Only players can use /chainmail");
         return true;
      } else {
         Player var5 = (Player)var1;
         if (!var5.hasPermission("economysmpcore.chainmail.use")) {
            var5.sendMessage(this.E);
            return true;
         } else {
            boolean var6 = (Boolean)this.G.getOrDefault(var5.getUniqueId(), true);
            boolean var7 = !var6;
            this.G.put(var5.getUniqueId(), var7);
            this.A(var5.getUniqueId());
            String var10001 = this.D;
            var5.sendMessage(var10001 + (var7 ? this.A : this.B));
            return true;
         }
      }
   }

   private String A(String var1) {
      return ChatColor.translateAlternateColorCodes('&', var1);
   }
}
