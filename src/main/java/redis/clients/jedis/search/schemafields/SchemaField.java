package redis.clients.jedis.search.schemafields;

import redis.clients.jedis.params.IParams;
import redis.clients.jedis.search.FieldName;

public abstract class SchemaField implements IParams {
   protected final FieldName fieldName;

   public SchemaField(String var1) {
      this.fieldName = new FieldName(var1);
   }

   public SchemaField(FieldName var1) {
      this.fieldName = var1;
   }

   public SchemaField as(String var1) {
      this.fieldName.as(var1);
      return this;
   }

   public final FieldName getFieldName() {
      return this.fieldName;
   }

   public final String getName() {
      return this.fieldName.getName();
   }
}
