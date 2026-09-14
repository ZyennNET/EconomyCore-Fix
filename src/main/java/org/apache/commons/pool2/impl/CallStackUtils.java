package org.apache.commons.pool2.impl;

import java.security.AccessControlException;

public final class CallStackUtils {
   private static boolean canCreateSecurityManager() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 == null) {
         return true;
      } else {
         try {
            var0.checkPermission(new RuntimePermission("createSecurityManager"));
            return true;
         } catch (AccessControlException var2) {
            return false;
         }
      }
   }

   @Deprecated
   public static CallStack newCallStack(String var0, boolean var1) {
      return newCallStack(var0, var1, false);
   }

   public static CallStack newCallStack(String var0, boolean var1, boolean var2) {
      return (CallStack)(canCreateSecurityManager() && !var2 ? new SecurityManagerCallStack(var0, var1) : new ThrowableCallStack(var0, var1));
   }

   private CallStackUtils() {
   }
}
