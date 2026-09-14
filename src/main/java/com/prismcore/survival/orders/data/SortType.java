package com.prismcore.survival.orders.data;

public enum SortType {
   MOST_PAID,
   MOST_DELIVERED,
   RECENTLY_LISTED,
   MOST_MONEY_PER_ITEM;

   public SortType next() {
      SortType var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = MOST_DELIVERED;
         case 1 -> var10000 = RECENTLY_LISTED;
         case 2 -> var10000 = MOST_MONEY_PER_ITEM;
         case 3 -> var10000 = MOST_PAID;
         default -> throw new IllegalStateException("Unexpected ordinal: " + this.ordinal());
      }

      return var10000;
   }

   // $FF: synthetic method
   private static SortType[] $values() {
      return new SortType[]{MOST_PAID, MOST_DELIVERED, RECENTLY_LISTED, MOST_MONEY_PER_ITEM};
   }
}
