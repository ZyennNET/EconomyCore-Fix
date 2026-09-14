package com.h2ph.T.A;

import com.h2ph.PrismSurvival;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class J {
   private final PrismSurvival B;
   private final com.h2ph.T.B A;

   public J(PrismSurvival var1, com.h2ph.T.B var2) {
      this.B = var1;
      this.A = var2;
   }

   public synchronized void A(String var1, Location var2) {
      if (this.A.F()) {
         try {
            Connection var3 = this.A.G();

            try {
               PreparedStatement var4 = var3.prepareStatement("INSERT INTO rtp_locations (region, world, x, y, z, created_at) VALUES (?, ?, ?, ?, ?, ?)");

               try {
                  var4.setString(1, var1.toLowerCase());
                  var4.setString(2, var2.getWorld().getName());
                  var4.setInt(3, var2.getBlockX());
                  var4.setInt(4, var2.getBlockY());
                  var4.setInt(5, var2.getBlockZ());
                  var4.setLong(6, System.currentTimeMillis());
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
            this.B.getLogger().severe("Failed to save RTP location for region " + var1);
            var11.printStackTrace();
         }

      }
   }

   public synchronized Location C(String var1) {
      if (!this.A.F()) {
         return null;
      } else {
         Object var2 = null;
         int var3 = -1;

         try {
            Connection var4 = this.A.G();

            try {
               var4.setAutoCommit(false);

               try {
                  PreparedStatement var5 = var4.prepareStatement("SELECT * FROM rtp_locations WHERE region = ? ORDER BY created_at ASC LIMIT 1 FOR UPDATE");

                  try {
                     var5.setString(1, var1.toLowerCase());
                     ResultSet var6 = var5.executeQuery();

                     try {
                        if (var6.next()) {
                           var3 = var6.getInt("id");
                           String var7 = var6.getString("world");
                           int var8 = var6.getInt("x");
                           int var9 = var6.getInt("y");
                           int var10 = var6.getInt("z");
                           World var11 = Bukkit.getWorld(var7);
                        }
                     } catch (Throwable var29) {
                        if (var6 != null) {
                           try {
                              var6.close();
                           } catch (Throwable var27) {
                              var29.addSuppressed(var27);
                           }
                        }

                        throw var29;
                     }

                     if (var6 != null) {
                        var6.close();
                     }
                  } catch (Throwable var30) {
                     if (var5 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var26) {
                           var30.addSuppressed(var26);
                        }
                     }

                     throw var30;
                  }

                  if (var5 != null) {
                     var5.close();
                  }

                  if (var3 != -1) {
                     var5 = var4.prepareStatement("DELETE FROM rtp_locations WHERE id = ?");

                     try {
                        var5.setInt(1, var3);
                        var5.executeUpdate();
                     } catch (Throwable var28) {
                        if (var5 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var25) {
                              var28.addSuppressed(var25);
                           }
                        }

                        throw var28;
                     }

                     if (var5 != null) {
                        var5.close();
                     }

                     var4.commit();
                  }
               } catch (SQLException var31) {
                  var4.rollback();
                  throw var31;
               } finally {
                  var4.setAutoCommit(true);
               }
            } catch (Throwable var33) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var24) {
                     var33.addSuppressed(var24);
                  }
               }

               throw var33;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (SQLException var34) {
            this.B.getLogger().severe("Failed to pop RTP location for region " + var1);
            var34.printStackTrace();
         }

         return null;
      }
   }

   public synchronized _A A(String var1) {
      if (!this.A.F()) {
         return null;
      } else {
         _A var2 = null;
         int var3 = -1;

         try {
            Connection var4 = this.A.G();

            try {
               var4.setAutoCommit(false);

               try {
                  PreparedStatement var5 = var4.prepareStatement("SELECT * FROM rtp_locations WHERE region = ? ORDER BY created_at ASC LIMIT 1 FOR UPDATE");

                  try {
                     var5.setString(1, var1.toLowerCase());
                     ResultSet var6 = var5.executeQuery();

                     try {
                        if (var6.next()) {
                           var3 = var6.getInt("id");
                           var2 = new _A(var6.getString("world"), var6.getInt("x"), var6.getInt("y"), var6.getInt("z"));
                        }
                     } catch (Throwable var26) {
                        if (var6 != null) {
                           try {
                              var6.close();
                           } catch (Throwable var24) {
                              var26.addSuppressed(var24);
                           }
                        }

                        throw var26;
                     }

                     if (var6 != null) {
                        var6.close();
                     }
                  } catch (Throwable var27) {
                     if (var5 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var23) {
                           var27.addSuppressed(var23);
                        }
                     }

                     throw var27;
                  }

                  if (var5 != null) {
                     var5.close();
                  }

                  if (var3 != -1) {
                     var5 = var4.prepareStatement("DELETE FROM rtp_locations WHERE id = ?");

                     try {
                        var5.setInt(1, var3);
                        var5.executeUpdate();
                     } catch (Throwable var25) {
                        if (var5 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var22) {
                              var25.addSuppressed(var22);
                           }
                        }

                        throw var25;
                     }

                     if (var5 != null) {
                        var5.close();
                     }

                     var4.commit();
                  } else {
                     var4.rollback();
                  }
               } catch (SQLException var28) {
                  var4.rollback();
                  throw var28;
               } finally {
                  var4.setAutoCommit(true);
               }
            } catch (Throwable var30) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var21) {
                     var30.addSuppressed(var21);
                  }
               }

               throw var30;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (SQLException var31) {
            this.B.getLogger().severe("Failed to pop RTP location for region " + var1);
            var31.printStackTrace();
         }

         return var3 != -1 ? var2 : null;
      }
   }

   public synchronized int B(String var1) {
      if (!this.A.F()) {
         return 0;
      } else {
         try {
            Connection var2 = this.A.G();

            int var5;
            label117: {
               try {
                  PreparedStatement var3;
                  label108: {
                     var3 = var2.prepareStatement("SELECT COUNT(*) FROM rtp_locations WHERE region = ?");

                     try {
                        var3.setString(1, var1.toLowerCase());
                        ResultSet var4 = var3.executeQuery();

                        label87: {
                           try {
                              if (var4.next()) {
                                 var5 = var4.getInt(1);
                                 break label87;
                              }
                           } catch (Throwable var10) {
                              if (var4 != null) {
                                 try {
                                    var4.close();
                                 } catch (Throwable var9) {
                                    var10.addSuppressed(var9);
                                 }
                              }

                              throw var10;
                           }

                           if (var4 != null) {
                              var4.close();
                           }
                           break label108;
                        }

                        if (var4 != null) {
                           var4.close();
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
                     break label117;
                  }

                  if (var3 != null) {
                     var3.close();
                  }
               } catch (Throwable var12) {
                  if (var2 != null) {
                     try {
                        var2.close();
                     } catch (Throwable var7) {
                        var12.addSuppressed(var7);
                     }
                  }

                  throw var12;
               }

               if (var2 != null) {
                  var2.close();
               }

               return 0;
            }

            if (var2 != null) {
               var2.close();
            }

            return var5;
         } catch (SQLException var13) {
            var13.printStackTrace();
            return 0;
         }
      }
   }

   public static class _A {
      public String B;
      public int A;
      public int D;
      public int C;

      public _A(String var1, int var2, int var3, int var4) {
         this.B = var1;
         this.A = var2;
         this.D = var3;
         this.C = var4;
      }
   }
}
