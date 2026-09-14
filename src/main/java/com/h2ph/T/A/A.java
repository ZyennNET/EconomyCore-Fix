package com.h2ph.T.A;

import com.h2ph.PrismSurvival;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class A implements F {
   private final PrismSurvival B;
   private final com.h2ph.T.B A;

   public A(PrismSurvival var1, com.h2ph.T.B var2) {
      this.B = var1;
      this.A = var2;
   }

   private void H(UUID var1) {
   }

   public void D(UUID var1) {
      this.A(var1, "wins", 1);
      this.A(var1, true);
   }

   public void C(UUID var1) {
      this.A(var1, "losses", 1);
      this.A(var1, false);
   }

   private void A(UUID var1, String var2, int var3) {
      try {
         Connection var4 = this.A.G();

         try {
            String var5 = "INSERT INTO duel_stats (uuid, " + var2 + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + var2 + " = " + var2 + " + ?";
            PreparedStatement var6 = var4.prepareStatement(var5);

            try {
               var6.setString(1, var1.toString());
               var6.setInt(2, var3);
               var6.setInt(3, var3);
               var6.executeUpdate();
            } catch (Throwable var11) {
               if (var6 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (var6 != null) {
               var6.close();
            }
         } catch (Throwable var12) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var9) {
                  var12.addSuppressed(var9);
               }
            }

            throw var12;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (SQLException var13) {
         this.B.getLogger().severe("Failed to update duel stat " + var2 + " for " + String.valueOf(var1));
         var13.printStackTrace();
      }

   }

   public void A(UUID var1, boolean var2) {
      try {
         Connection var3 = this.A.G();

         try {
            String var4;
            if (var2) {
               var4 = "INSERT INTO duel_stats (uuid, streak) VALUES (?, 1) ON DUPLICATE KEY UPDATE streak = IF(streak > 0, streak + 1, 1)";
            } else {
               var4 = "INSERT INTO duel_stats (uuid, streak) VALUES (?, -1) ON DUPLICATE KEY UPDATE streak = IF(streak < 0, streak - 1, -1)";
            }

            PreparedStatement var5 = var3.prepareStatement(var4);

            try {
               var5.setString(1, var1.toString());
               var5.executeUpdate();
            } catch (Throwable var10) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (Throwable var11) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var12) {
         this.B.getLogger().severe("Failed to update streak for " + String.valueOf(var1));
         var12.printStackTrace();
      }

   }

   public int A(UUID var1) {
      return this.A(var1, "wins");
   }

   public int G(UUID var1) {
      return this.A(var1, "losses");
   }

   public int E(UUID var1) {
      return this.A(var1, "streak");
   }

   private int A(UUID var1, String var2) {
      try {
         Connection var3 = this.A.G();

         int var6;
         label80: {
            try {
               PreparedStatement var4 = var3.prepareStatement("SELECT " + var2 + " FROM duel_stats WHERE uuid = ?");

               label74: {
                  try {
                     var4.setString(1, var1.toString());
                     ResultSet var5 = var4.executeQuery();
                     if (!var5.next()) {
                        break label74;
                     }

                     var6 = var5.getInt(var2);
                  } catch (Throwable var9) {
                     if (var4 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var8) {
                           var9.addSuppressed(var8);
                        }
                     }

                     throw var9;
                  }

                  if (var4 != null) {
                     var4.close();
                  }
                  break label80;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var10) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var7) {
                     var10.addSuppressed(var7);
                  }
               }

               throw var10;
            }

            if (var3 != null) {
               var3.close();
            }

            return 0;
         }

         if (var3 != null) {
            var3.close();
         }

         return var6;
      } catch (SQLException var11) {
         this.B.getLogger().severe("Failed to get duel stat " + var2 + " for " + String.valueOf(var1));
         var11.printStackTrace();
         return 0;
      }
   }

   public String F(UUID var1) {
      int var2 = this.A(var1);
      int var3 = this.G(var1);
      int var4 = var2 + var3;
      if (var4 == 0) {
         return "0.00%";
      } else {
         double var5 = (double)var2 / (double)var4 * (double)100.0F;
         return String.format("%.2f%%", var5);
      }
   }

   public CompletableFuture<Void> B(UUID var1) {
      return CompletableFuture.completedFuture((Object)null);
   }
}
