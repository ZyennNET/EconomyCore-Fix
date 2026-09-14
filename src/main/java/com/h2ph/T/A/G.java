package com.h2ph.T.A;

import com.prismcore.survival.auction.AuctionItem;
import com.prismcore.survival.auction.AuctionManager;
import com.prismcore.survival.auction.Transaction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class G implements B {
   private final com.h2ph.T.B A;

   public G(com.h2ph.T.B var1) {
      this.A = var1;
   }

   public void A(AuctionItem var1) {
      String var2 = "REPLACE INTO auctions (id, seller_uuid, seller_name, item_base64, price, listed_at, duration) VALUES (?, ?, ?, ?, ?, ?, ?)";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               var4.setString(1, var1.getId().toString());
               var4.setString(2, (String)null);
               var4.setString(3, var1.getSeller());
               var4.setString(4, this.A(var1.getItemStack()));
               var4.setDouble(5, var1.getPrice());
               var4.setLong(6, var1.getListedAt());
               var4.setInt(7, var1.getDuration());
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

   public void C(UUID var1) {
      String var2 = "DELETE FROM auctions WHERE id = ?";

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

   public void A(UUID var1, double var2) {
      String var4 = "UPDATE auctions SET price = ? WHERE id = ?";

      try {
         Connection var5 = this.A.G();

         try {
            PreparedStatement var6 = var5.prepareStatement(var4);

            try {
               var6.setDouble(1, var2);
               var6.setString(2, var1.toString());
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

   public List<AuctionItem> A() {
      ArrayList var1 = new ArrayList();
      String var2 = "SELECT * FROM auctions";

      try {
         Connection var3 = this.A.G();

         try {
            PreparedStatement var4 = var3.prepareStatement(var2);

            try {
               ResultSet var5 = var4.executeQuery();

               try {
                  while(var5.next()) {
                     try {
                        UUID var6 = UUID.fromString(var5.getString("id"));
                        String var7 = var5.getString("seller_name");
                        ItemStack var8 = this.A(var5.getString("item_base64"));
                        double var9 = var5.getDouble("price");
                        long var11 = var5.getLong("listed_at");
                        int var13 = var5.getInt("duration");
                        AuctionItem var14 = new AuctionItem(var6, var7, var8, var9, var11, var13);
                        var1.add(var14);
                     } catch (Exception var18) {
                        var18.printStackTrace();
                     }
                  }
               } catch (Throwable var19) {
                  if (var5 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var17) {
                        var19.addSuppressed(var17);
                     }
                  }

                  throw var19;
               }

               if (var5 != null) {
                  var5.close();
               }
            } catch (Throwable var20) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var16) {
                     var20.addSuppressed(var16);
                  }
               }

               throw var20;
            }

            if (var4 != null) {
               var4.close();
            }
         } catch (Throwable var21) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var15) {
                  var21.addSuppressed(var15);
               }
            }

            throw var21;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (SQLException var22) {
         var22.printStackTrace();
      }

      return var1;
   }

   public void A(UUID var1, String var2, String var3, double var4) {
      String var6 = "INSERT INTO auction_pending_sales (seller_uuid, buyer_name, item_name, price) VALUES (?, ?, ?, ?)";

      try {
         Connection var7 = this.A.G();

         try {
            PreparedStatement var8 = var7.prepareStatement(var6);

            try {
               var8.setString(1, var1.toString());
               var8.setString(2, var2);
               var8.setString(3, var3);
               var8.setDouble(4, var4);
               var8.executeUpdate();
            } catch (Throwable var13) {
               if (var8 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }
               }

               throw var13;
            }

            if (var8 != null) {
               var8.close();
            }
         } catch (Throwable var14) {
            if (var7 != null) {
               try {
                  var7.close();
               } catch (Throwable var11) {
                  var14.addSuppressed(var11);
               }
            }

            throw var14;
         }

         if (var7 != null) {
            var7.close();
         }
      } catch (SQLException var15) {
         var15.printStackTrace();
      }

   }

   public List<AuctionManager.OfflineSale> B(UUID var1) {
      ArrayList var2 = new ArrayList();
      String var3 = "SELECT * FROM auction_pending_sales WHERE seller_uuid = ?";

      try {
         Connection var4 = this.A.G();

         try {
            PreparedStatement var5 = var4.prepareStatement(var3);

            try {
               var5.setString(1, var1.toString());
               ResultSet var6 = var5.executeQuery();

               try {
                  while(var6.next()) {
                     String var7 = var6.getString("buyer_name");
                     String var8 = var6.getString("item_name");
                     double var9 = var6.getDouble("price");
                     var2.add(new AuctionManager.OfflineSale(var7, var8, var9));
                  }
               } catch (Throwable var14) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var13) {
                        var14.addSuppressed(var13);
                     }
                  }

                  throw var14;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var15) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var12) {
                     var15.addSuppressed(var12);
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
               } catch (Throwable var11) {
                  var16.addSuppressed(var11);
               }
            }

            throw var16;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (SQLException var17) {
         var17.printStackTrace();
      }

      return var2;
   }

   public void E(UUID var1) {
      String var2 = "DELETE FROM auction_pending_sales WHERE seller_uuid = ?";

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

   public void A(UUID var1, String var2) {
      String var3 = "REPLACE INTO auction_preferences (uuid, sort_mode) VALUES (?, ?)";

      try {
         Connection var4 = this.A.G();

         try {
            PreparedStatement var5 = var4.prepareStatement(var3);

            try {
               var5.setString(1, var1.toString());
               var5.setString(2, var2);
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
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (SQLException var12) {
         var12.printStackTrace();
      }

   }

   public String A(UUID var1) {
      String var2 = "SELECT sort_mode FROM auction_preferences WHERE uuid = ?";

      try {
         Connection var3 = this.A.G();

         String var6;
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
                              var6 = var5.getString("sort_mode");
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

            return "Highest Price";
         }

         if (var3 != null) {
            var3.close();
         }

         return var6;
      } catch (SQLException var14) {
         var14.printStackTrace();
         return "Highest Price";
      }
   }

   private String A(ItemStack var1) throws IllegalStateException {
      try {
         ByteArrayOutputStream var2 = new ByteArrayOutputStream();
         BukkitObjectOutputStream var3 = new BukkitObjectOutputStream(var2);
         var3.writeObject(var1);
         var3.close();
         return Base64Coder.encodeLines(var2.toByteArray());
      } catch (Exception var4) {
         throw new IllegalStateException("Unable to save item stacks.", var4);
      }
   }

   private ItemStack A(String var1) throws IOException {
      try {
         ByteArrayInputStream var2 = new ByteArrayInputStream(Base64Coder.decodeLines(var1));
         BukkitObjectInputStream var3 = new BukkitObjectInputStream(var2);
         ItemStack var4 = (ItemStack)var3.readObject();
         var3.close();
         return var4;
      } catch (ClassNotFoundException var5) {
         throw new IOException("Unable to decode class type.", var5);
      }
   }

   public void A(UUID var1, Transaction var2) {
      String var3 = "INSERT INTO auction_transactions (player_uuid, item_base64, price, buyer, seller, timestamp, is_sale) VALUES (?, ?, ?, ?, ?, ?, ?)";

      try {
         Connection var4 = this.A.G();

         try {
            PreparedStatement var5 = var4.prepareStatement(var3);

            try {
               var5.setString(1, var1.toString());
               var5.setString(2, this.A(var2.getItem()));
               var5.setDouble(3, var2.getPrice());
               var5.setString(4, var2.getBuyer());
               var5.setString(5, var2.getSeller());
               var5.setLong(6, var2.getTimestamp());
               var5.setBoolean(7, var2.isSale());
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
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var8) {
                  var11.addSuppressed(var8);
               }
            }

            throw var11;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (SQLException var12) {
         var12.printStackTrace();
      }

   }

   public List<Transaction> D(UUID var1) {
      ArrayList var2 = new ArrayList();
      String var3 = "SELECT * FROM auction_transactions WHERE player_uuid = ? ORDER BY timestamp DESC";

      try {
         Connection var4 = this.A.G();

         try {
            PreparedStatement var5 = var4.prepareStatement(var3);

            try {
               var5.setString(1, var1.toString());
               ResultSet var6 = var5.executeQuery();

               try {
                  while(var6.next()) {
                     try {
                        ItemStack var7 = this.A(var6.getString("item_base64"));
                        double var8 = var6.getDouble("price");
                        String var10 = var6.getString("buyer");
                        String var11 = var6.getString("seller");
                        long var12 = var6.getLong("timestamp");
                        boolean var14 = var6.getBoolean("is_sale");
                        var2.add(new Transaction(var7, var8, var10, var11, var12, var14));
                     } catch (IOException var18) {
                        var18.printStackTrace();
                     }
                  }
               } catch (Throwable var19) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var17) {
                        var19.addSuppressed(var17);
                     }
                  }

                  throw var19;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var20) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var16) {
                     var20.addSuppressed(var16);
                  }
               }

               throw var20;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (Throwable var21) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var15) {
                  var21.addSuppressed(var15);
               }
            }

            throw var21;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (SQLException var22) {
         var22.printStackTrace();
      }

      return var2;
   }
}
