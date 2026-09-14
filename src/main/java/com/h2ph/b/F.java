package com.h2ph.b;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.h2ph.PrismSurvival;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class F {
   public static void A(Player var0, String var1) {
      PrismSurvival var2 = (PrismSurvival)JavaPlugin.getPlugin(PrismSurvival.class);
      ByteArrayDataOutput var3 = ByteStreams.newDataOutput();
      var3.writeUTF("Connect");
      var3.writeUTF(var1);
      var0.sendPluginMessage(var2, "BungeeCord", var3.toByteArray());
   }
}
