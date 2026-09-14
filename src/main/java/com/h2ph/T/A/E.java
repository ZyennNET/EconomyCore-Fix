package com.h2ph.T.A;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class E implements K {
   private final com.h2ph.T.B A;

   public E(com.h2ph.T.B var1) {
      this.A = var1;
   }

   public void A(UUID var1, String var2, double var3) {
      String var5 = "INSERT INTO bounties (target_uuid, target_name, amount, last_updated) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE amount = amount + VALUES(amount), last_updated = VALUES(last_updated), target_name = VALUES(target_name)";

      try {
         Connection var6 = this.A.G();

         try {
            PreparedStatement var7 = var6.prepareStatement(var5);

            try {
               var7.setString(1, var1.toString());
               var7.setString(2, var2);
               var7.setDouble(3, var3);
               var7.setLong(4, System.currentTimeMillis());
               var7.executeUpdate();
            } catch (Throwable var12) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var11) {
                     var12.addSuppressed(var11);
                  }
               }

               throw var12;
            }

            if (var7 != null) {
               var7.close();
            }
         } catch (Throwable var13) {
            if (var6 != null) {
               try {
                  var6.close();
               } catch (Throwable var10) {
                  var13.addSuppressed(var10);
               }
            }

            throw var13;
         }

         if (var6 != null) {
            var6.close();
         }
      } catch (SQLException var14) {
         var14.printStackTrace();
      }

   }

   public void A(UUID var1) {
      String var2 = "DELETE FROM bounties WHERE target_uuid = ?";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               var4.setString(1, var1.toString());
               var4.executeUpdate();
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
      } catch (SQLException var11) {
         var11.printStackTrace();
      }

   }

   public double B(UUID var1) {
      String var2 = "SELECT amount FROM bounties WHERE target_uuid = ?";

      try {
         Connection var3 = this.A.G();

         double var6;
         label114: {
            try {
               PreparedStatement var4;
               label106: {
                  var4 = var3.prepareStatement(var2);

                  try {
                     var4.setString(1, var1.toString());
                     ResultSet var5 = var4.executeQuery();

                     label86: {
                        try {
                           if (var5.next()) {
                              var6 = var5.getDouble("amount");
                              break label86;
                           }
                        } catch (Throwable var11) {
                           if (var5 != null) {
                              try {
                                 var5.close();
                              } catch (Throwable var10) {
                                 var11.addSuppressed(var10);
                              }
                           }

                           throw var11;
                        }

                        if (var5 != null) {
                           var5.close();
                        }
                        break label106;
                     }

                     if (var5 != null) {
                        var5.close();
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
                  break label114;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var13) {
               if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var8) {
                     var13.addSuppressed(var8);
                  }
               }

               throw var13;
            }

            if (var3 != null) {
               var3.close();
            }

            return (double)0.0F;
         }

         if (var3 != null) {
            var3.close();
         }

         return var6;
      } catch (SQLException var14) {
         var14.printStackTrace();
         return (double)0.0F;
      }
   }

   public List<K._A> A() {
      ArrayList var1 = new ArrayList();
      String var2 = "SELECT * FROM bounties";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               ResultSet var5 = var4.executeQuery();

               try {
                  while(var5.next()) {
                     UUID var6 = UUID.fromString(var5.getString("target_uuid"));
                     String var7 = var5.getString("target_name");
                     double var8 = var5.getDouble("amount");
                     long var10 = var5.getLong("last_updated");
                     var1.add(new K._A(var6, var7, var8, var10));
                  }
               } catch (Throwable var15) {
                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                     }
                  }

                  throw var15;
               }

               if (var5 != null) {
                  var5.close();
               }
            } catch (Throwable var16) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var13) {
                     var16.addSuppressed(var13);
                  }
               }

               throw var16;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (Throwable var17) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var12) {
                  var17.addSuppressed(var12);
               }
            }

            throw var17;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var18) {
         var18.printStackTrace();
      }

      return var1;
   }
}
