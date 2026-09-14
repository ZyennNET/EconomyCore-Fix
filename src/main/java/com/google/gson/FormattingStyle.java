package com.google.gson;

import java.util.Objects;

public class FormattingStyle {
   private final String newline;
   private final String indent;
   private final boolean spaceAfterSeparators;
   public static final FormattingStyle COMPACT = new FormattingStyle("", "", false);
   public static final FormattingStyle PRETTY = new FormattingStyle("\n", "  ", true);

   private FormattingStyle(String var1, String var2, boolean var3) {
      Objects.requireNonNull(var1, "newline == null");
      Objects.requireNonNull(var2, "indent == null");
      if (!var1.matches("[\r\n]*")) {
         throw new IllegalArgumentException("Only combinations of \\n and \\r are allowed in newline.");
      } else if (!var2.matches("[ \t]*")) {
         throw new IllegalArgumentException("Only combinations of spaces and tabs are allowed in indent.");
      } else {
         this.newline = var1;
         this.indent = var2;
         this.spaceAfterSeparators = var3;
      }
   }

   public FormattingStyle withNewline(String var1) {
      return new FormattingStyle(var1, this.indent, this.spaceAfterSeparators);
   }

   public FormattingStyle withIndent(String var1) {
      return new FormattingStyle(this.newline, var1, this.spaceAfterSeparators);
   }

   public FormattingStyle withSpaceAfterSeparators(boolean var1) {
      return new FormattingStyle(this.newline, this.indent, var1);
   }

   public String getNewline() {
      return this.newline;
   }

   public String getIndent() {
      return this.indent;
   }

   public boolean usesSpaceAfterSeparators() {
      return this.spaceAfterSeparators;
   }
}
