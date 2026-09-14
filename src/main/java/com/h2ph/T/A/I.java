package com.h2ph.T.A;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class I implements C {
   private final com.h2ph.T.B A;

   public I(com.h2ph.T.B var1) {
      this.A = var1;
   }

   public void addBan(UUID var1, String var2, String var3, String var4, String var5, int var6, long var7, long var9, String var11) {
      String var12 = "REPLACE INTO bans (uuid, player_name, ban_id, reason_key, display_reason, offense_count, date_banned, expiry, banned_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

      try {
         Connection var13 = this.A.G();

         try {
            PreparedStatement var14 = var13.prepareStatement(var12);

            try {
               var14.setString(1, var1.toString());
               var14.setString(2, var2);
               var14.setString(3, var3);
               var14.setString(4, var4);
               var14.setString(5, var5);
               var14.setInt(6, var6);
               var14.setLong(7, var7);
               var14.setLong(8, var9);
               var14.setString(9, var11);
               var14.executeUpdate();
            } catch (Throwable var19) {
               if (var14 != null) {
                  try {
                     var14.close();
                  } catch (Throwable var18) {
                     var19.addSuppressed(var18);
                  }
               }

               throw var19;
            }

            if (var14 != null) {
               var14.close();
            }
         } catch (Throwable var20) {
            if (var13 != null) {
               try {
                  var13.close();
               } catch (Throwable var17) {
                  var20.addSuppressed(var17);
               }
            }

            throw var20;
         }

         if (var13 != null) {
            var13.close();
         }
      } catch (SQLException var21) {
         var21.printStackTrace();
      }

   }

   public void removeBan(UUID var1) {
      String var2 = "DELETE FROM bans WHERE uuid = ?";

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

   public void removeBan(String var1) {
      String var2 = "DELETE FROM bans WHERE player_name = ?";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               var4.setString(1, var1);
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

   public void removeBanById(String var1) {
      String var2 = "DELETE FROM bans WHERE ban_id = ?";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               var4.setString(1, var1);
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

   public boolean isBanned(UUID var1) {
      return this.getBanInfo(var1) != null;
   }

   public C._A getBanInfo(UUID var1) {
      String var2 = "SELECT * FROM bans WHERE uuid = ? AND (expiry = -1 OR expiry > ?) ORDER BY date_banned DESC LIMIT 1";

      try {
         Connection var3 = this.A.G();

         C._A var6;
         label114: {
            try {
               PreparedStatement var4;
               label106: {
                  var4 = var3.prepareStatement(var2);

                  try {
                     var4.setString(1, var1.toString());
                     var4.setLong(2, System.currentTimeMillis());
                     ResultSet var5 = var4.executeQuery();

                     label86: {
                        try {
                           if (var5.next()) {
                              var6 = this.A(var5);
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

            return null;
         }

         if (var3 != null) {
            var3.close();
         }

         return var6;
      } catch (SQLException var14) {
         var14.printStackTrace();
         return null;
      }
   }

   public C._A getBanInfoByName(String var1) {
      String var2 = "SELECT * FROM bans WHERE player_name LIKE ? AND (expiry = -1 OR expiry > ?) ORDER BY date_banned DESC LIMIT 1";

      try {
         Connection var3 = this.A.G();

         C._A var6;
         label114: {
            try {
               PreparedStatement var4;
               label106: {
                  var4 = var3.prepareStatement(var2);

                  try {
                     var4.setString(1, var1);
                     var4.setLong(2, System.currentTimeMillis());
                     ResultSet var5 = var4.executeQuery();

                     label86: {
                        try {
                           if (var5.next()) {
                              var6 = this.A(var5);
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

            return null;
         }

         if (var3 != null) {
            var3.close();
         }

         return var6;
      } catch (SQLException var14) {
         var14.printStackTrace();
         return null;
      }
   }

   public C._A getBanInfoById(String var1) {
      String var2 = "SELECT * FROM bans WHERE ban_id = ?";

      try {
         Connection var3 = this.A.G();

         C._A var6;
         label114: {
            try {
               PreparedStatement var4;
               label106: {
                  var4 = var3.prepareStatement(var2);

                  try {
                     var4.setString(1, var1);
                     ResultSet var5 = var4.executeQuery();

                     label86: {
                        try {
                           if (var5.next()) {
                              var6 = this.A(var5);
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

            return null;
         }

         if (var3 != null) {
            var3.close();
         }

         return var6;
      } catch (SQLException var14) {
         var14.printStackTrace();
         return null;
      }
   }

   public List<String> getBannedPlayerNames() {
      ArrayList var1 = new ArrayList();
      String var2 = "SELECT DISTINCT player_name FROM bans WHERE expiry = -1 OR expiry > ?";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               var4.setLong(1, System.currentTimeMillis());
               ResultSet var5 = var4.executeQuery();

               try {
                  while(var5.next()) {
                     var1.add(var5.getString("player_name"));
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
      } catch (SQLException var14) {
         var14.printStackTrace();
      }

      return var1;
   }

   public int getOffenseCount(UUID var1, String var2) {
      String var3 = "SELECT count FROM offenses WHERE uuid = ? AND reason_key = ?";

      try {
         Connection var4 = this.A.G();

         int var7;
         label114: {
            try {
               PreparedStatement var5;
               label106: {
                  var5 = var4.prepareStatement(var3);

                  try {
                     var5.setString(1, var1.toString());
                     var5.setString(2, var2);
                     ResultSet var6 = var5.executeQuery();

                     label86: {
                        try {
                           if (var6.next()) {
                              var7 = var6.getInt("count");
                              break label86;
                           }
                        } catch (Throwable var12) {
                           if (var6 != null) {
                              try {
                                 var6.close();
                              } catch (Throwable var11) {
                                 var12.addSuppressed(var11);
                              }
                           }

                           throw var12;
                        }

                        if (var6 != null) {
                           var6.close();
                        }
                        break label106;
                     }

                     if (var6 != null) {
                        var6.close();
                     }
                  } catch (Throwable var13) {
                     if (var5 != null) {
                        try {
                           var5.close();
                        } catch (Throwable var10) {
                           var13.addSuppressed(var10);
                        }
                     }

                     throw var13;
                  }

                  if (var5 != null) {
                     var5.close();
                  }
                  break label114;
               }

               if (var5 != null) {
                  var5.close();
               }
            } catch (Throwable var14) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var9) {
                     var14.addSuppressed(var9);
                  }
               }

               throw var14;
            }

            if (var4 != null) {
               var4.close();
            }

            return 0;
         }

         if (var4 != null) {
            var4.close();
         }

         return var7;
      } catch (SQLException var15) {
         var15.printStackTrace();
         return 0;
      }
   }

   public void setOffenseCount(UUID var1, String var2, int var3) {
      String var4 = "INSERT INTO offenses (uuid, reason_key, count) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE count = VALUES(count)";

      try {
         Connection var5 = this.A.G();

         try {
            PreparedStatement var6 = var5.prepareStatement(var4);

            try {
               var6.setString(1, var1.toString());
               var6.setString(2, var2);
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
            if (var5 != null) {
               try {
                  var5.close();
               } catch (Throwable var9) {
                  var12.addSuppressed(var9);
               }
            }

            throw var12;
         }

         if (var5 != null) {
            var5.close();
         }
      } catch (SQLException var13) {
         var13.printStackTrace();
      }

   }

   public List<C._A> getAllActiveBans() {
      ArrayList var1 = new ArrayList();
      String var2 = "SELECT * FROM bans WHERE expiry = -1 OR expiry > ?";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               var4.setLong(1, System.currentTimeMillis());
               ResultSet var5 = var4.executeQuery();

               try {
                  while(var5.next()) {
                     var1.add(this.A(var5));
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
      } catch (SQLException var14) {
         var14.printStackTrace();
      }

      return var1;
   }

   private C._A A(ResultSet var1) throws SQLException {
      C._A var2 = new C._A();
      var2.A = var1.getString("uuid");
      var2.E = var1.getString("player_name");
      var2.B = var1.getString("ban_id");
      var2.I = var1.getString("reason_key");
      var2.G = var1.getString("display_reason");
      var2.H = var1.getInt("offense_count");
      var2.D = var1.getLong("date_banned");
      var2.C = var1.getLong("expiry");
      var2.F = var1.getString("banned_by");
      return var2;
   }
}
