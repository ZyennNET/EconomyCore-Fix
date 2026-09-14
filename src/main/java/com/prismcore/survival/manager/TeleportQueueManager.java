package com.prismcore.survival.manager;

import com.h2ph.PrismSurvival;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.entity.Player;

public class TeleportQueueManager {
   private final PrismSurvival plugin;
   private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue();
   private final AtomicBoolean isProcessing = new AtomicBoolean(false);

   public TeleportQueueManager(PrismSurvival var1) {
      this.plugin = var1;
   }

   public int getQueuePosition() {
      return this.queue.size() + 1;
   }

   public void submit(Player var1, Runnable var2) {
      this.queue.add(var2);
      this.processQueue();
   }

   private void processQueue() {
      if (this.isProcessing.compareAndSet(false, true)) {
         this.plugin.getSchedulerAdapter().runTaskAsync(() -> {
            while(!this.queue.isEmpty()) {
               Runnable var1 = (Runnable)this.queue.poll();
               if (var1 != null) {
                  try {
                     var1.run();
                  } catch (Exception var3) {
                     var3.printStackTrace();
                  }
               }
            }

            this.isProcessing.set(false);
            if (!this.queue.isEmpty()) {
               this.processQueue();
            }

         });
      }

   }
}
