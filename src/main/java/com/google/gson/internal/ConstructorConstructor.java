package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.ReflectionAccessFilter;
import com.google.gson.internal.reflect.ReflectionHelper;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class ConstructorConstructor {
   private final Map<Type, InstanceCreator<?>> instanceCreators;
   private final boolean useJdkUnsafe;
   private final List<ReflectionAccessFilter> reflectionFilters;

   public ConstructorConstructor(Map<Type, InstanceCreator<?>> var1, boolean var2, List<ReflectionAccessFilter> var3) {
      this.instanceCreators = var1;
      this.useJdkUnsafe = var2;
      this.reflectionFilters = var3;
   }

   static String checkInstantiable(Class<?> var0) {
      int var1 = var0.getModifiers();
      if (Modifier.isInterface(var1)) {
         return "Interfaces can't be instantiated! Register an InstanceCreator or a TypeAdapter for this type. Interface name: " + var0.getName();
      } else {
         return Modifier.isAbstract(var1) ? "Abstract classes can't be instantiated! Adjust the R8 configuration or register an InstanceCreator or a TypeAdapter for this type. Class name: " + var0.getName() + "\nSee " + TroubleshootingGuide.createUrl("r8-abstract-class") : null;
      }
   }

   public <T> ObjectConstructor<T> get(TypeToken<T> var1) {
      final Type var2 = var1.getType();
      Class var3 = var1.getRawType();
      final InstanceCreator var4 = (InstanceCreator)this.instanceCreators.get(var2);
      if (var4 != null) {
         return new ObjectConstructor<T>() {
            public T construct() {
               return (T)var4.createInstance(var2);
            }
         };
      } else {
         final InstanceCreator var5 = (InstanceCreator)this.instanceCreators.get(var3);
         if (var5 != null) {
            return new ObjectConstructor<T>() {
               public T construct() {
                  return (T)var5.createInstance(var2);
               }
            };
         } else {
            ObjectConstructor var6 = newSpecialCollectionConstructor(var2, var3);
            if (var6 != null) {
               return var6;
            } else {
               ReflectionAccessFilter.FilterResult var7 = ReflectionAccessFilterHelper.getFilterResult(this.reflectionFilters, var3);
               ObjectConstructor var8 = newDefaultConstructor(var3, var7);
               if (var8 != null) {
                  return var8;
               } else {
                  ObjectConstructor var9 = newDefaultImplementationConstructor(var2, var3);
                  if (var9 != null) {
                     return var9;
                  } else {
                     final String var10 = checkInstantiable(var3);
                     if (var10 != null) {
                        return new ObjectConstructor<T>() {
                           public T construct() {
                              throw new JsonIOException(var10);
                           }
                        };
                     } else if (var7 == ReflectionAccessFilter.FilterResult.ALLOW) {
                        return this.<T>newUnsafeAllocator(var3);
                     } else {
                        final String var11 = "Unable to create instance of " + var3 + "; ReflectionAccessFilter does not permit using reflection or Unsafe. Register an InstanceCreator or a TypeAdapter for this type or adjust the access filter to allow using reflection.";
                        return new ObjectConstructor<T>() {
                           public T construct() {
                              throw new JsonIOException(var11);
                           }
                        };
                     }
                  }
               }
            }
         }
      }
   }

   private static <T> ObjectConstructor<T> newSpecialCollectionConstructor(final Type var0, Class<? super T> var1) {
      if (EnumSet.class.isAssignableFrom(var1)) {
         return new ObjectConstructor<T>() {
            public T construct() {
               if (var0 instanceof ParameterizedType) {
                  Type var1 = ((ParameterizedType)var0).getActualTypeArguments()[0];
                  if (var1 instanceof Class) {
                     EnumSet var2 = EnumSet.noneOf((Class)var1);
                     return (T)var2;
                  } else {
                     throw new JsonIOException("Invalid EnumSet type: " + var0.toString());
                  }
               } else {
                  throw new JsonIOException("Invalid EnumSet type: " + var0.toString());
               }
            }
         };
      } else {
         return var1 == EnumMap.class ? new ObjectConstructor<T>() {
            public T construct() {
               if (var0 instanceof ParameterizedType) {
                  Type var1 = ((ParameterizedType)var0).getActualTypeArguments()[0];
                  if (var1 instanceof Class) {
                     EnumMap var2 = new EnumMap((Class)var1);
                     return (T)var2;
                  } else {
                     throw new JsonIOException("Invalid EnumMap type: " + var0.toString());
                  }
               } else {
                  throw new JsonIOException("Invalid EnumMap type: " + var0.toString());
               }
            }
         } : null;
      }
   }

   private static <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> var0, ReflectionAccessFilter.FilterResult var1) {
      if (Modifier.isAbstract(var0.getModifiers())) {
         return null;
      } else {
         final Constructor var2;
         try {
            var2 = var0.getDeclaredConstructor();
         } catch (NoSuchMethodException var5) {
            return null;
         }

         boolean var3 = var1 == ReflectionAccessFilter.FilterResult.ALLOW || ReflectionAccessFilterHelper.canAccess(var2, (Object)null) && (var1 != ReflectionAccessFilter.FilterResult.BLOCK_ALL || Modifier.isPublic(var2.getModifiers()));
         if (!var3) {
            final String var6 = "Unable to invoke no-args constructor of " + var0 + "; constructor is not accessible and ReflectionAccessFilter does not permit making it accessible. Register an InstanceCreator or a TypeAdapter for this type, change the visibility of the constructor or adjust the access filter.";
            return new ObjectConstructor<T>() {
               public T construct() {
                  throw new JsonIOException(var6);
               }
            };
         } else {
            if (var1 == ReflectionAccessFilter.FilterResult.ALLOW) {
               final String var4 = ReflectionHelper.tryMakeAccessible(var2);
               if (var4 != null) {
                  return new ObjectConstructor<T>() {
                     public T construct() {
                        throw new JsonIOException(var4);
                     }
                  };
               }
            }

            return new ObjectConstructor<T>() {
               public T construct() {
                  try {
                     Object var1 = var2.newInstance();
                     return (T)var1;
                  } catch (InstantiationException var2x) {
                     throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(var2) + "' with no args", var2x);
                  } catch (InvocationTargetException var3) {
                     throw new RuntimeException("Failed to invoke constructor '" + ReflectionHelper.constructorToString(var2) + "' with no args", var3.getCause());
                  } catch (IllegalAccessException var4) {
                     throw ReflectionHelper.createExceptionForUnexpectedIllegalAccess(var4);
                  }
               }
            };
         }
      }
   }

   private static <T> ObjectConstructor<T> newDefaultImplementationConstructor(Type var0, Class<? super T> var1) {
      if (Collection.class.isAssignableFrom(var1)) {
         if (SortedSet.class.isAssignableFrom(var1)) {
            return new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new TreeSet());
               }
            };
         } else if (Set.class.isAssignableFrom(var1)) {
            return new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new LinkedHashSet());
               }
            };
         } else {
            return Queue.class.isAssignableFrom(var1) ? new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new ArrayDeque());
               }
            } : new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new ArrayList());
               }
            };
         }
      } else if (Map.class.isAssignableFrom(var1)) {
         if (ConcurrentNavigableMap.class.isAssignableFrom(var1)) {
            return new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new ConcurrentSkipListMap());
               }
            };
         } else if (ConcurrentMap.class.isAssignableFrom(var1)) {
            return new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new ConcurrentHashMap());
               }
            };
         } else if (SortedMap.class.isAssignableFrom(var1)) {
            return new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new TreeMap());
               }
            };
         } else {
            return var0 instanceof ParameterizedType && !String.class.isAssignableFrom(TypeToken.get(((ParameterizedType)var0).getActualTypeArguments()[0]).getRawType()) ? new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new LinkedHashMap());
               }
            } : new ObjectConstructor<T>() {
               public T construct() {
                  return (T)(new LinkedTreeMap());
               }
            };
         }
      } else {
         return null;
      }
   }

   private <T> ObjectConstructor<T> newUnsafeAllocator(final Class<? super T> var1) {
      if (this.useJdkUnsafe) {
         return new ObjectConstructor<T>() {
            public T construct() {
               try {
                  Object var1x = UnsafeAllocator.INSTANCE.newInstance(var1);
                  return (T)var1x;
               } catch (Exception var2) {
                  throw new RuntimeException("Unable to create instance of " + var1 + ". Registering an InstanceCreator or a TypeAdapter for this type, or adding a no-args constructor may fix this problem.", var2);
               }
            }
         };
      } else {
         final String var2 = "Unable to create instance of " + var1 + "; usage of JDK Unsafe is disabled. Registering an InstanceCreator or a TypeAdapter for this type, adding a no-args constructor, or enabling usage of JDK Unsafe may fix this problem.";
         if (var1.getDeclaredConstructors().length == 0) {
            var2 = var2 + " Or adjust your R8 configuration to keep the no-args constructor of the class.";
         }

         return new ObjectConstructor<T>() {
            public T construct() {
               throw new JsonIOException(var2);
            }
         };
      }
   }

   public String toString() {
      return this.instanceCreators.toString();
   }
}
