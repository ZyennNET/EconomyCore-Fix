package com.zaxxer.hikari.util;

import com.zaxxer.hikari.HikariConfig;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyElf {
   private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].+");

   private PropertyElf() {
   }

   public static void setTargetFromProperties(Object var0, Properties var1) {
      if (var0 != null && var1 != null) {
         List var2 = Arrays.asList(var0.getClass().getMethods());
         var1.forEach((var2x, var3) -> {
            if (var0 instanceof HikariConfig && var2x.toString().startsWith("dataSource.")) {
               ((HikariConfig)var0).addDataSourceProperty(var2x.toString().substring("dataSource.".length()), var3);
            } else {
               setProperty(var0, var2x.toString(), var3, var2);
            }

         });
      }
   }

   public static Set<String> getPropertyNames(Class<?> var0) {
      HashSet var1 = new HashSet();
      Matcher var2 = GETTER_PATTERN.matcher("");

      for(Method var6 : var0.getMethods()) {
         String var7 = var6.getName();
         if (var6.getParameterTypes().length == 0 && var2.reset(var7).matches()) {
            var7 = var7.replaceFirst("(get|is)", "");

            try {
               if (var0.getMethod("set" + var7, var6.getReturnType()) != null) {
                  char var10000 = Character.toLowerCase(var7.charAt(0));
                  var7 = var10000 + var7.substring(1);
                  var1.add(var7);
               }
            } catch (Exception var9) {
            }
         }
      }

      return var1;
   }

   public static Object getProperty(String var0, Object var1) {
      try {
         String var8 = var0.substring(0, 1).toUpperCase(Locale.ENGLISH);
         String var2 = "get" + var8 + var0.substring(1);
         Method var7 = var1.getClass().getMethod(var2);
         return var7.invoke(var1);
      } catch (Exception var6) {
         try {
            String var10000 = var0.substring(0, 1).toUpperCase(Locale.ENGLISH);
            String var3 = "is" + var10000 + var0.substring(1);
            Method var4 = var1.getClass().getMethod(var3);
            return var4.invoke(var1);
         } catch (Exception var5) {
            return null;
         }
      }
   }

   public static Properties copyProperties(Properties var0) {
      Properties var1 = new Properties();
      var0.forEach((var1x, var2) -> var1.setProperty(var1x.toString(), var2.toString()));
      return var1;
   }

   private static void setProperty(Object var0, String var1, Object var2, List<Method> var3) {
      Logger var4 = LoggerFactory.getLogger(PropertyElf.class);
      String var10000 = var1.substring(0, 1).toUpperCase(Locale.ENGLISH);
      String var5 = "set" + var10000 + var1.substring(1);
      Method var6 = (Method)var3.stream().filter((var1x) -> var1x.getName().equals(var5) && var1x.getParameterCount() == 1).findFirst().orElse((Object)null);
      if (var6 == null) {
         String var7 = "set" + var1.toUpperCase(Locale.ENGLISH);
         var6 = (Method)var3.stream().filter((var1x) -> var1x.getName().equals(var7) && var1x.getParameterCount() == 1).findFirst().orElse((Object)null);
      }

      if (var6 == null) {
         var4.error((String)"Property {} does not exist on target {}", (Object)var1, (Object)var0.getClass());
         throw new RuntimeException(String.format("Property %s does not exist on target %s", var1, var0.getClass()));
      } else {
         try {
            Class var11 = var6.getParameterTypes()[0];
            if (var11 == Integer.TYPE) {
               var6.invoke(var0, Integer.parseInt(var2.toString()));
            } else if (var11 == Long.TYPE) {
               var6.invoke(var0, Long.parseLong(var2.toString()));
            } else if (var11 == Short.TYPE) {
               var6.invoke(var0, Short.parseShort(var2.toString()));
            } else if (var11 != Boolean.TYPE && var11 != Boolean.class) {
               if (var11.isArray() && Character.TYPE.isAssignableFrom(var11.getComponentType())) {
                  var6.invoke(var0, var2.toString().toCharArray());
               } else if (var11 == String.class) {
                  var6.invoke(var0, var2.toString());
               } else {
                  try {
                     var4.debug("Try to create a new instance of \"{}\"", var2);
                     var6.invoke(var0, Class.forName(var2.toString()).getDeclaredConstructor().newInstance());
                  } catch (ClassNotFoundException | InstantiationException var9) {
                     var4.debug("Class \"{}\" not found or could not instantiate it (Default constructor)", var2);
                     var6.invoke(var0, var2);
                  }
               }
            } else {
               var6.invoke(var0, Boolean.parseBoolean(var2.toString()));
            }

         } catch (Exception var10) {
            var4.error("Failed to set property {} on target {}", var1, var0.getClass(), var10);
            throw new RuntimeException(var10);
         }
      }
   }
}
