package com.h2ph.T.A;

import com.prismcore.survival.auction.AuctionItem;
import com.prismcore.survival.auction.AuctionManager;
import com.prismcore.survival.auction.Transaction;
import java.util.List;
import java.util.UUID;

public interface B {
   void A(AuctionItem var1);

   void C(UUID var1);

   void A(UUID var1, double var2);

   List<AuctionItem> A();

   void A(UUID var1, String var2, String var3, double var4);

   List<AuctionManager.OfflineSale> B(UUID var1);

   void E(UUID var1);

   void A(UUID var1, String var2);

   String A(UUID var1);

   void A(UUID var1, Transaction var2);

   List<Transaction> D(UUID var1);
}
