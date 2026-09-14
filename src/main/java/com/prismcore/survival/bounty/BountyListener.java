package com.prismcore.survival.bounty;

import com.h2ph.PrismSurvival;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.projectiles.ProjectileSource;

public class BountyListener implements Listener {
   private final PrismSurvival plugin;
   private final BountyManager bountyManager;

   public BountyListener(PrismSurvival var1, BountyManager var2) {
      this.plugin = var1;
      this.bountyManager = var2;
   }

   @EventHandler
   public void onDeath(PlayerDeathEvent var1) {
      Player var2 = var1.getEntity();
      Player var3 = var2.getKiller();
      if (var3 == null) {
         var3 = this.getExplosionKiller(var2);
      }

      if (var3 != null && !var3.equals(var2)) {
         this.bountyManager.claimBounty(var2, var3);
      }

   }

   private Player getExplosionKiller(Player var1) {
      EntityDamageEvent var2 = var1.getLastDamageCause();
      if (var2 == null) {
         return null;
      } else {
         if (var2 instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent var3 = (EntityDamageByEntityEvent)var2;
            Entity var4 = var3.getDamager();
            if (var4 instanceof TNTPrimed) {
               TNTPrimed var5 = (TNTPrimed)var4;
               Entity var6 = var5.getSource();
               if (var6 instanceof Player) {
                  return (Player)var6;
               }
            }

            if (var4 instanceof Projectile) {
               ProjectileSource var7 = ((Projectile)var4).getShooter();
               if (var7 instanceof Player) {
                  return (Player)var7;
               }
            }
         }

         return null;
      }
   }
}
