package com.h2ph.T.A;

import com.prismcore.survival.manager.PlayerData;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface D {
   void A(PlayerData var1);

   PlayerData A(UUID var1);

   CompletableFuture<Void> B(PlayerData var1);

   CompletableFuture<PlayerData> B(UUID var1);
}
