package com.google.gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public final class FieldAttributes {
   private final Field field;

   public FieldAttributes(Field var1) {
      this.field = (Field)Objects.requireNonNull(var1);
   }

   public Class<?> getDeclaringClass() {
      return this.field.getDeclaringClass();
   }

   public String getName() {
      return this.field.getName();
   }

   public Type getDeclaredType() {
      return this.field.getGenericType();
   }

   public Class<?> getDeclaredClass() {
      return this.field.getType();
   }

   public <T extends Annotation> T getAnnotation(Class<T> var1) {
      return (T)this.field.getAnnotation(var1);
   }

   public Collection<Annotation> getAnnotations() {
      return Arrays.asList(this.field.getAnnotations());
   }

   public boolean hasModifier(int var1) {
      return (this.field.getModifiers() & var1) != 0;
   }

   public String toString() {
      return this.field.toString();
   }
}
