package com.h2ph.c;

import java.util.UUID;
import org.bukkit.entity.Player;

public final class J {
   private J() {
   }

   public static void A(F var0, Player var1) {
      I.C.put(var1.getUniqueId(), I._A.A);
      var1.closeInventory();
      var1.sendMessage(A.A(var0.A().A("search-prompt", "&fType a player name in chat, or &ccancel &fto cancel.")));
   }

   public static void A(Player var0) {
   }

   public static boolean A(UUID var0) {
      return false;
   }
}
