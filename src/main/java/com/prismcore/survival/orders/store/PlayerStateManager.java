package com.prismcore.survival.orders.store;

import com.prismcore.survival.orders.PrismOrders;
import com.prismcore.survival.orders.data.AlphaSort;
import com.prismcore.survival.orders.data.SortType;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStateManager {
   private final Map<UUID, View> main = new HashMap();
   private final Map<UUID, ItemView> selectItem = new HashMap();

   public PlayerStateManager(PrismOrders var1) {
   }

   public View main(UUID var1) {
      return (View)this.main.computeIfAbsent(var1, (var0) -> new View());
   }

   public ItemView items(UUID var1) {
      return (ItemView)this.selectItem.computeIfAbsent(var1, (var0) -> new ItemView());
   }

   public ItemView select(UUID var1) {
      return this.items(var1);
   }

   public void saveAllPrefs() {
   }

   public static class ItemView {
      public int page = 0;
      public SortType sort;
      public AlphaSort alpha;
      public String filter;
      public String search;

      public ItemView() {
         this.sort = SortType.MOST_MONEY_PER_ITEM;
         this.alpha = AlphaSort.A_Z;
         this.filter = "All";
         this.search = "";
      }

      public void reset() {
         this.page = 0;
         this.sort = SortType.MOST_PAID;
         this.alpha = AlphaSort.A_Z;
         this.filter = "All";
         this.search = null;
      }
   }

   public static class View {
      public int page = 0;
      public SortType sort;
      public String filter;
      public String search;

      public View() {
         this.sort = SortType.MOST_PAID;
         this.filter = "All";
         this.search = "";
      }
   }
}
