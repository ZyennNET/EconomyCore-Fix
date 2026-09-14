package org.slf4j;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.event.SubstituteLoggingEvent;
import org.slf4j.helpers.NOP_FallbackServiceProvider;
import org.slf4j.helpers.SubstituteLogger;
import org.slf4j.helpers.SubstituteServiceProvider;
import org.slf4j.helpers.Util;
import org.slf4j.spi.SLF4JServiceProvider;

public final class LoggerFactory {
   static final String CODES_PREFIX = "https://www.slf4j.org/codes.html";
   static final String NO_PROVIDERS_URL = "https://www.slf4j.org/codes.html#noProviders";
   static final String IGNORED_BINDINGS_URL = "https://www.slf4j.org/codes.html#ignoredBindings";
   static final String MULTIPLE_BINDINGS_URL = "https://www.slf4j.org/codes.html#multiple_bindings";
   static final String VERSION_MISMATCH = "https://www.slf4j.org/codes.html#version_mismatch";
   static final String SUBSTITUTE_LOGGER_URL = "https://www.slf4j.org/codes.html#substituteLogger";
   static final String LOGGER_NAME_MISMATCH_URL = "https://www.slf4j.org/codes.html#loggerNameMismatch";
   static final String REPLAY_URL = "https://www.slf4j.org/codes.html#replay";
   static final String UNSUCCESSFUL_INIT_URL = "https://www.slf4j.org/codes.html#unsuccessfulInit";
   static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also https://www.slf4j.org/codes.html#unsuccessfulInit";
   public static final String PROVIDER_PROPERTY_KEY = "slf4j.provider";
   static final int UNINITIALIZED = 0;
   static final int ONGOING_INITIALIZATION = 1;
   static final int FAILED_INITIALIZATION = 2;
   static final int SUCCESSFUL_INITIALIZATION = 3;
   static final int NOP_FALLBACK_INITIALIZATION = 4;
   static volatile int INITIALIZATION_STATE = 0;
   static final SubstituteServiceProvider SUBST_PROVIDER = new SubstituteServiceProvider();
   static final NOP_FallbackServiceProvider NOP_FALLBACK_SERVICE_PROVIDER = new NOP_FallbackServiceProvider();
   static final String DETECT_LOGGER_NAME_MISMATCH_PROPERTY = "slf4j.detectLoggerNameMismatch";
   static final String JAVA_VENDOR_PROPERTY = "java.vendor.url";
   static boolean DETECT_LOGGER_NAME_MISMATCH = Util.safeGetBooleanSystemProperty("slf4j.detectLoggerNameMismatch");
   static volatile SLF4JServiceProvider PROVIDER;
   private static final String[] API_COMPATIBILITY_LIST = new String[]{"2.0"};
   private static final String STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";

   static List<SLF4JServiceProvider> findServiceProviders() {
      ArrayList var0 = new ArrayList();
      ClassLoader var1 = LoggerFactory.class.getClassLoader();
      SLF4JServiceProvider var2 = loadExplicitlySpecified(var1);
      if (var2 != null) {
         var0.add(var2);
         return var0;
      } else {
         ServiceLoader var3 = getServiceLoader(var1);
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            safelyInstantiate(var0, var4);
         }

         return var0;
      }
   }

   private static ServiceLoader<SLF4JServiceProvider> getServiceLoader(ClassLoader var0) {
      SecurityManager var2 = System.getSecurityManager();
      ServiceLoader var1;
      if (var2 == null) {
         var1 = ServiceLoader.load(SLF4JServiceProvider.class, var0);
      } else {
         PrivilegedAction var3 = () -> ServiceLoader.load(SLF4JServiceProvider.class, var0);
         var1 = (ServiceLoader)AccessController.doPrivileged(var3);
      }

      return var1;
   }

   private static void safelyInstantiate(List<SLF4JServiceProvider> var0, Iterator<SLF4JServiceProvider> var1) {
      try {
         SLF4JServiceProvider var2 = (SLF4JServiceProvider)var1.next();
         var0.add(var2);
      } catch (ServiceConfigurationError var3) {
         Util.report("A SLF4J service provider failed to instantiate:\n" + var3.getMessage());
      }

   }

   private LoggerFactory() {
   }

   static void reset() {
      INITIALIZATION_STATE = 0;
   }

   private static final void performInitialization() {
      bind();
      if (INITIALIZATION_STATE == 3) {
         versionSanityCheck();
      }

   }

   private static final void bind() {
      try {
         List var0 = findServiceProviders();
         reportMultipleBindingAmbiguity(var0);
         if (var0 != null && !var0.isEmpty()) {
            PROVIDER = (SLF4JServiceProvider)var0.get(0);
            PROVIDER.initialize();
            INITIALIZATION_STATE = 3;
            reportActualBinding(var0);
         } else {
            INITIALIZATION_STATE = 4;
            Util.report("No SLF4J providers were found.");
            Util.report("Defaulting to no-operation (NOP) logger implementation");
            Util.report("See https://www.slf4j.org/codes.html#noProviders for further details.");
            Set var1 = findPossibleStaticLoggerBinderPathSet();
            reportIgnoredStaticLoggerBinders(var1);
         }

         postBindCleanUp();
      } catch (Exception var2) {
         failedBinding(var2);
         throw new IllegalStateException("Unexpected initialization failure", var2);
      }
   }

   static SLF4JServiceProvider loadExplicitlySpecified(ClassLoader var0) {
      String var1 = System.getProperty("slf4j.provider");
      if (null != var1 && !var1.isEmpty()) {
         try {
            String var2 = String.format("Attempting to load provider \"%s\" specified via \"%s\" system property", var1, "slf4j.provider");
            Util.report(var2);
            Class var9 = var0.loadClass(var1);
            Constructor var4 = var9.getConstructor();
            Object var5 = var4.newInstance();
            return (SLF4JServiceProvider)var5;
         } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException var6) {
            String var8 = String.format("Failed to instantiate the specified SLF4JServiceProvider (%s)", var1);
            Util.report(var8, var6);
            return null;
         } catch (ClassCastException var7) {
            String var3 = String.format("Specified SLF4JServiceProvider (%s) does not implement SLF4JServiceProvider interface", var1);
            Util.report(var3, var7);
            return null;
         }
      } else {
         return null;
      }
   }

   private static void reportIgnoredStaticLoggerBinders(Set<URL> var0) {
      if (!var0.isEmpty()) {
         Util.report("Class path contains SLF4J bindings targeting slf4j-api versions 1.7.x or earlier.");

         for(URL var2 : var0) {
            Util.report("Ignoring binding found at [" + var2 + "]");
         }

         Util.report("See https://www.slf4j.org/codes.html#ignoredBindings for an explanation.");
      }
   }

   static Set<URL> findPossibleStaticLoggerBinderPathSet() {
      LinkedHashSet var0 = new LinkedHashSet();

      try {
         ClassLoader var1 = LoggerFactory.class.getClassLoader();
         Enumeration var2;
         if (var1 == null) {
            var2 = ClassLoader.getSystemResources("org/slf4j/impl/StaticLoggerBinder.class");
         } else {
            var2 = var1.getResources("org/slf4j/impl/StaticLoggerBinder.class");
         }

         while(var2.hasMoreElements()) {
            URL var3 = (URL)var2.nextElement();
            var0.add(var3);
         }
      } catch (IOException var4) {
         Util.report("Error getting resources from path", var4);
      }

      return var0;
   }

   private static void postBindCleanUp() {
      fixSubstituteLoggers();
      replayEvents();
      SUBST_PROVIDER.getSubstituteLoggerFactory().clear();
   }

   private static void fixSubstituteLoggers() {
      synchronized(SUBST_PROVIDER) {
         SUBST_PROVIDER.getSubstituteLoggerFactory().postInitialization();

         for(SubstituteLogger var2 : SUBST_PROVIDER.getSubstituteLoggerFactory().getLoggers()) {
            Logger var3 = getLogger(var2.getName());
            var2.setDelegate(var3);
         }

      }
   }

   static void failedBinding(Throwable var0) {
      INITIALIZATION_STATE = 2;
      Util.report("Failed to instantiate SLF4J LoggerFactory", var0);
   }

   private static void replayEvents() {
      LinkedBlockingQueue var0 = SUBST_PROVIDER.getSubstituteLoggerFactory().getEventQueue();
      int var1 = var0.size();
      int var2 = 0;
      boolean var3 = true;
      ArrayList var4 = new ArrayList(128);

      while(true) {
         int var5 = var0.drainTo(var4, 128);
         if (var5 == 0) {
            return;
         }

         for(SubstituteLoggingEvent var7 : var4) {
            replaySingleEvent(var7);
            if (var2++ == 0) {
               emitReplayOrSubstituionWarning(var7, var1);
            }
         }

         var4.clear();
      }
   }

   private static void emitReplayOrSubstituionWarning(SubstituteLoggingEvent var0, int var1) {
      if (var0.getLogger().isDelegateEventAware()) {
         emitReplayWarning(var1);
      } else if (!var0.getLogger().isDelegateNOP()) {
         emitSubstitutionWarning();
      }

   }

   private static void replaySingleEvent(SubstituteLoggingEvent var0) {
      if (var0 != null) {
         SubstituteLogger var1 = var0.getLogger();
         String var2 = var1.getName();
         if (var1.isDelegateNull()) {
            throw new IllegalStateException("Delegate logger cannot be null at this state.");
         } else {
            if (!var1.isDelegateNOP()) {
               if (var1.isDelegateEventAware()) {
                  if (var1.isEnabledForLevel(var0.getLevel())) {
                     var1.log(var0);
                  }
               } else {
                  Util.report(var2);
               }
            }

         }
      }
   }

   private static void emitSubstitutionWarning() {
      Util.report("The following set of substitute loggers may have been accessed");
      Util.report("during the initialization phase. Logging calls during this");
      Util.report("phase were not honored. However, subsequent logging calls to these");
      Util.report("loggers will work as normally expected.");
      Util.report("See also https://www.slf4j.org/codes.html#substituteLogger");
   }

   private static void emitReplayWarning(int var0) {
      Util.report("A number (" + var0 + ") of logging calls during the initialization phase have been intercepted and are");
      Util.report("now being replayed. These are subject to the filtering rules of the underlying logging system.");
      Util.report("See also https://www.slf4j.org/codes.html#replay");
   }

   private static final void versionSanityCheck() {
      try {
         String var0 = PROVIDER.getRequestedApiVersion();
         boolean var1 = false;

         for(String var5 : API_COMPATIBILITY_LIST) {
            if (var0.startsWith(var5)) {
               var1 = true;
            }
         }

         if (!var1) {
            Util.report("The requested version " + var0 + " by your slf4j provider is not compatible with " + Arrays.asList(API_COMPATIBILITY_LIST).toString());
            Util.report("See https://www.slf4j.org/codes.html#version_mismatch for further details.");
         }
      } catch (NoSuchFieldError var6) {
      } catch (Throwable var7) {
         Util.report("Unexpected problem occurred during version sanity check", var7);
      }

   }

   private static boolean isAmbiguousProviderList(List<SLF4JServiceProvider> var0) {
      return var0.size() > 1;
   }

   private static void reportMultipleBindingAmbiguity(List<SLF4JServiceProvider> var0) {
      if (isAmbiguousProviderList(var0)) {
         Util.report("Class path contains multiple SLF4J providers.");

         for(SLF4JServiceProvider var2 : var0) {
            Util.report("Found provider [" + var2 + "]");
         }

         Util.report("See https://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
      }

   }

   private static void reportActualBinding(List<SLF4JServiceProvider> var0) {
      if (!var0.isEmpty() && isAmbiguousProviderList(var0)) {
         Util.report("Actual provider is of type [" + var0.get(0) + "]");
      }

   }

   public static Logger getLogger(String var0) {
      ILoggerFactory var1 = getILoggerFactory();
      return var1.getLogger(var0);
   }

   public static Logger getLogger(Class<?> var0) {
      Logger var1 = getLogger(var0.getName());
      if (DETECT_LOGGER_NAME_MISMATCH) {
         Class var2 = Util.getCallingClass();
         if (var2 != null && nonMatchingClasses(var0, var2)) {
            Util.report(String.format("Detected logger name mismatch. Given name: \"%s\"; computed name: \"%s\".", var1.getName(), var2.getName()));
            Util.report("See https://www.slf4j.org/codes.html#loggerNameMismatch for an explanation");
         }
      }

      return var1;
   }

   private static boolean nonMatchingClasses(Class<?> var0, Class<?> var1) {
      return !var1.isAssignableFrom(var0);
   }

   public static ILoggerFactory getILoggerFactory() {
      return getProvider().getLoggerFactory();
   }

   static SLF4JServiceProvider getProvider() {
      if (INITIALIZATION_STATE == 0) {
         synchronized(LoggerFactory.class) {
            if (INITIALIZATION_STATE == 0) {
               INITIALIZATION_STATE = 1;
               performInitialization();
            }
         }
      }

      switch (INITIALIZATION_STATE) {
         case 1:
            return SUBST_PROVIDER;
         case 2:
            throw new IllegalStateException("org.slf4j.LoggerFactory in failed state. Original exception was thrown EARLIER. See also https://www.slf4j.org/codes.html#unsuccessfulInit");
         case 3:
            return PROVIDER;
         case 4:
            return NOP_FALLBACK_SERVICE_PROVIDER;
         default:
            throw new IllegalStateException("Unreachable code");
      }
   }
}
