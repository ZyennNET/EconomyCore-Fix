package com.google.gson.internal.reflect;

import com.google.gson.JsonIOException;
import com.google.gson.internal.TroubleshootingGuide;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionHelper {
   private static final RecordHelper RECORD_HELPER;

   private ReflectionHelper() {
   }

   private static String getInaccessibleTroubleshootingSuffix(Exception var0) {
      if (!var0.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException")) {
         return "";
      } else {
         String var1 = var0.getMessage();
         String var2 = var1 != null && var1.contains("to module com.google.gson") ? "reflection-inaccessible-to-module-gson" : "reflection-inaccessible";
         return "\nSee " + TroubleshootingGuide.createUrl(var2);
      }
   }

   public static void makeAccessible(AccessibleObject var0) throws JsonIOException {
      try {
         var0.setAccessible(true);
      } catch (Exception var3) {
         String var2 = getAccessibleObjectDescription(var0, false);
         throw new JsonIOException("Failed making " + var2 + " accessible; either increase its visibility or write a custom TypeAdapter for its declaring type." + getInaccessibleTroubleshootingSuffix(var3), var3);
      }
   }

   public static String getAccessibleObjectDescription(AccessibleObject var0, boolean var1) {
      String var2;
      if (var0 instanceof Field) {
         var2 = "field '" + fieldToString((Field)var0) + "'";
      } else if (var0 instanceof Method) {
         Method var3 = (Method)var0;
         StringBuilder var4 = new StringBuilder(var3.getName());
         appendExecutableParameters(var3, var4);
         String var5 = var4.toString();
         var2 = "method '" + var3.getDeclaringClass().getName() + "#" + var5 + "'";
      } else if (var0 instanceof Constructor) {
         var2 = "constructor '" + constructorToString((Constructor)var0) + "'";
      } else {
         var2 = "<unknown AccessibleObject> " + var0.toString();
      }

      if (var1 && Character.isLowerCase(var2.charAt(0))) {
         var2 = Character.toUpperCase(var2.charAt(0)) + var2.substring(1);
      }

      return var2;
   }

   public static String fieldToString(Field var0) {
      return var0.getDeclaringClass().getName() + "#" + var0.getName();
   }

   public static String constructorToString(Constructor<?> var0) {
      StringBuilder var1 = new StringBuilder(var0.getDeclaringClass().getName());
      appendExecutableParameters(var0, var1);
      return var1.toString();
   }

   private static void appendExecutableParameters(AccessibleObject var0, StringBuilder var1) {
      var1.append('(');
      Class[] var2 = var0 instanceof Method ? ((Method)var0).getParameterTypes() : ((Constructor)var0).getParameterTypes();

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var3 > 0) {
            var1.append(", ");
         }

         var1.append(var2[var3].getSimpleName());
      }

      var1.append(')');
   }

   public static boolean isStatic(Class<?> var0) {
      return Modifier.isStatic(var0.getModifiers());
   }

   public static boolean isAnonymousOrNonStaticLocal(Class<?> var0) {
      return !isStatic(var0) && (var0.isAnonymousClass() || var0.isLocalClass());
   }

   public static String tryMakeAccessible(Constructor<?> var0) {
      try {
         var0.setAccessible(true);
         return null;
      } catch (Exception var2) {
         return "Failed making constructor '" + constructorToString(var0) + "' accessible; either increase its visibility or write a custom InstanceCreator or TypeAdapter for its declaring type: " + var2.getMessage() + getInaccessibleTroubleshootingSuffix(var2);
      }
   }

   public static boolean isRecord(Class<?> var0) {
      return RECORD_HELPER.isRecord(var0);
   }

   public static String[] getRecordComponentNames(Class<?> var0) {
      return RECORD_HELPER.getRecordComponentNames(var0);
   }

   public static Method getAccessor(Class<?> var0, Field var1) {
      return RECORD_HELPER.getAccessor(var0, var1);
   }

   public static <T> Constructor<T> getCanonicalRecordConstructor(Class<T> var0) {
      return RECORD_HELPER.<T>getCanonicalRecordConstructor(var0);
   }

   public static RuntimeException createExceptionForUnexpectedIllegalAccess(IllegalAccessException var0) {
      throw new RuntimeException("Unexpected IllegalAccessException occurred (Gson 2.11.0). Certain ReflectionAccessFilter features require Java >= 9 to work correctly. If you are not using ReflectionAccessFilter, report this to the Gson maintainers.", var0);
   }

   private static RuntimeException createExceptionForRecordReflectionException(ReflectiveOperationException var0) {
      throw new RuntimeException("Unexpected ReflectiveOperationException occurred (Gson 2.11.0). To support Java records, reflection is utilized to read out information about records. All these invocations happens after it is established that records exist in the JVM. This exception is unexpected behavior.", var0);
   }

   static {
      Object var0;
      try {
         var0 = new RecordSupportedHelper();
      } catch (ReflectiveOperationException var2) {
         var0 = new RecordNotSupportedHelper();
      }

      RECORD_HELPER = (RecordHelper)var0;
   }

   private abstract static class RecordHelper {
      private RecordHelper() {
      }

      abstract boolean isRecord(Class<?> var1);

      abstract String[] getRecordComponentNames(Class<?> var1);

      abstract <T> Constructor<T> getCanonicalRecordConstructor(Class<T> var1);

      public abstract Method getAccessor(Class<?> var1, Field var2);
   }

   private static class RecordNotSupportedHelper extends RecordHelper {
      private RecordNotSupportedHelper() {
      }

      boolean isRecord(Class<?> var1) {
         return false;
      }

      String[] getRecordComponentNames(Class<?> var1) {
         throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
      }

      <T> Constructor<T> getCanonicalRecordConstructor(Class<T> var1) {
         throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
      }

      public Method getAccessor(Class<?> var1, Field var2) {
         throw new UnsupportedOperationException("Records are not supported on this JVM, this method should not be called");
      }
   }

   private static class RecordSupportedHelper extends RecordHelper {
      private final Method isRecord;
      private final Method getRecordComponents;
      private final Method getName;
      private final Method getType;

      private RecordSupportedHelper() throws NoSuchMethodException, ClassNotFoundException {
         this.isRecord = Class.class.getMethod("isRecord");
         this.getRecordComponents = Class.class.getMethod("getRecordComponents");
         Class var1 = Class.forName("java.lang.reflect.RecordComponent");
         this.getName = var1.getMethod("getName");
         this.getType = var1.getMethod("getType");
      }

      boolean isRecord(Class<?> var1) {
         try {
            return (Boolean)this.isRecord.invoke(var1);
         } catch (ReflectiveOperationException var3) {
            throw ReflectionHelper.createExceptionForRecordReflectionException(var3);
         }
      }

      String[] getRecordComponentNames(Class<?> var1) {
         try {
            Object[] var2 = this.getRecordComponents.invoke(var1);
            String[] var3 = new String[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
               var3[var4] = (String)this.getName.invoke(var2[var4]);
            }

            return var3;
         } catch (ReflectiveOperationException var5) {
            throw ReflectionHelper.createExceptionForRecordReflectionException(var5);
         }
      }

      public <T> Constructor<T> getCanonicalRecordConstructor(Class<T> var1) {
         try {
            Object[] var2 = this.getRecordComponents.invoke(var1);
            Class[] var3 = new Class[var2.length];

            for(int var4 = 0; var4 < var2.length; ++var4) {
               var3[var4] = (Class)this.getType.invoke(var2[var4]);
            }

            return var1.getDeclaredConstructor(var3);
         } catch (ReflectiveOperationException var5) {
            throw ReflectionHelper.createExceptionForRecordReflectionException(var5);
         }
      }

      public Method getAccessor(Class<?> var1, Field var2) {
         try {
            return var1.getMethod(var2.getName());
         } catch (ReflectiveOperationException var4) {
            throw ReflectionHelper.createExceptionForRecordReflectionException(var4);
         }
      }
   }
}
