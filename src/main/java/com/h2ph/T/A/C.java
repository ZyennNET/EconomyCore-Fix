package com.h2ph.T.A;

import java.util.List;
import java.util.UUID;

public interface C {
   void addBan(UUID var1, String var2, String var3, String var4, String var5, int var6, long var7, long var9, String var11);

   void removeBan(UUID var1);

   void removeBan(String var1);

   void removeBanById(String var1);

   _A getBanInfo(UUID var1);

   _A getBanInfoByName(String var1);

   _A getBanInfoById(String var1);

   boolean isBanned(UUID var1);

   int getOffenseCount(UUID var1, String var2);

   void setOffenseCount(UUID var1, String var2, int var3);

   List<String> getBannedPlayerNames();

   List<_A> getAllActiveBans();

   public static class _A {
      public String A;
      public String E;
      public String B;
      public String I;
      public String G;
      public int H;
      public long D;
      public long C;
      public String F;
   }
}
