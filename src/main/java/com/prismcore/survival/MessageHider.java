package com.prismcore.survival.survival;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

public class MessageHider implements Listener {
   public MessageHider(Plugin var1) {
      for(World var3 : Bukkit.getWorlds()) {
         this.setGamerule(var3);
      }

      var1.getServer().getPluginManager().registerEvents(this, var1);
   }

   private void setGamerule(World var1) {
      try {
         var1.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
      } catch (Throwable var3) {
      }

   }

   @EventHandler
   public void onWorldLoad(WorldLoadEvent var1) {
      this.setGamerule(var1.getWorld());
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent var1) {
      var1.setJoinMessage((String)null);
      this.setGamerule(var1.getPlayer().getWorld());
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      var1.setQuitMessage((String)null);
   }

   @EventHandler
   public void onAdvancement(PlayerAdvancementDoneEvent var1) {
      var1.message((Component)null);
   }
}
