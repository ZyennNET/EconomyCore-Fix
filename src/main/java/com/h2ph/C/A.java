package com.h2ph.c;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class A {
   private static final LegacyComponentSerializer A = LegacyComponentSerializer.builder().hexColors().character('&').hexCharacter('#').build();

   private A() {
   }

   public static Component A(String var0) {
      return (Component)(var0 != null && !var0.isEmpty() ? A.deserialize(var0).decoration(TextDecoration.ITALIC, false) : Component.empty());
   }

   public static String B(String var0) {
      return LegacyComponentSerializer.legacySection().serialize(A(var0));
   }
}
