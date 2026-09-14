package com.h2ph.b;

import com.h2ph.PrismSurvival;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class B implements Listener {
   private final Plugin D;
   private final Map<UUID, Location> C = new ConcurrentHashMap();
   private final Map<UUID, BlockData> F = new ConcurrentHashMap();
   private final Map<UUID, BlockData> E = new ConcurrentHashMap();
   private final Map<UUID, Location> A = new ConcurrentHashMap();
   private final Map<UUID, Consumer<String>> B = new ConcurrentHashMap();

   public B(Plugin var1) {
      this.D = var1;
   }

   public void getSearchInput(Player var1, Consumer<String> var2) {
      Location var3 = var1.getLocation().getBlock().getLocation();
      if (var3.getBlock().getState() instanceof TileState) {
         var3.add((double)0.0F, (double)1.0F, (double)0.0F);
      }

      BlockData var4 = var3.getBlock().getBlockData();
      this.C.put(var1.getUniqueId(), var3);
      this.F.put(var1.getUniqueId(), var4);
      this.B.put(var1.getUniqueId(), var2);
      Location var5 = var3.clone().add((double)0.0F, (double)-1.0F, (double)0.0F);
      if (!var5.getBlock().getType().isSolid()) {
         BlockData var6 = var5.getBlock().getBlockData();
         this.E.put(var1.getUniqueId(), var6);
         this.A.put(var1.getUniqueId(), var5);
         var5.getBlock().setType(Material.BEDROCK, false);
      }

      ((PrismSurvival)this.D).getSchedulerAdapter().runEntityTaskLater(var1, () -> {
         if (!var1.isOnline()) {
            this.A(var1.getUniqueId());
         } else {
            var3.getBlock().setType(Material.OAK_SIGN, false);
            if (var3.getBlock().getState() instanceof Sign) {
               Sign var3x = (Sign)var3.getBlock().getState();
               var1.openSign(var3x);
            }

         }
      }, 2L);
   }

   @EventHandler
   public void onSignChange(SignChangeEvent var1) {
      Player var2 = var1.getPlayer();
      UUID var3 = var2.getUniqueId();
      if (this.C.containsKey(var3)) {
         Location var4 = (Location)this.C.get(var3);
         if (var1.getBlock().getLocation().equals(var4)) {
            var1.setCancelled(true);
            StringBuilder var5 = new StringBuilder();

            for(String var9 : var1.getLines()) {
               var5.append(var9);
            }

            String var10 = var5.toString().trim();
            Consumer var11 = (Consumer)this.B.remove(var3);
            if (var11 != null) {
               ((PrismSurvival)this.D).getSchedulerAdapter().runEntityTask(var2, () -> var11.accept(var10));
            }

            this.A(var3);
         }
      }

   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent var1) {
      this.A(var1.getPlayer().getUniqueId());
   }

   private void A(UUID var1) {
      if (this.C.containsKey(var1)) {
         Location var2 = (Location)this.C.remove(var1);
         BlockData var3 = (BlockData)this.F.remove(var1);
         if (var2 != null && var3 != null) {
            var2.getBlock().setBlockData(var3);
         }
      }

      if (this.A.containsKey(var1)) {
         Location var4 = (Location)this.A.remove(var1);
         BlockData var5 = (BlockData)this.E.remove(var1);
         if (var4 != null && var5 != null) {
            var4.getBlock().setBlockData(var5);
         }
      }

      this.B.remove(var1);
   }

   public void cleanup() {
      for(UUID var2 : new HashSet(this.C.keySet())) {
         this.A(var2);
      }

   }
}
