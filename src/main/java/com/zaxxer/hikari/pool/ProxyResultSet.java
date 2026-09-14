package com.zaxxer.hikari.pool;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyResultSet implements ResultSet {
   protected final ProxyConnection connection;
   protected final ProxyStatement statement;
   final ResultSet delegate;

   protected ProxyResultSet(ProxyConnection var1, ProxyStatement var2, ResultSet var3) {
      this.connection = var1;
      this.statement = var2;
      this.delegate = var3;
   }

   final SQLException checkException(SQLException var1) {
      return this.connection.checkException(var1);
   }

   public String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "@" + System.identityHashCode(this) + " wrapping " + this.delegate;
   }

   public final Statement getStatement() throws SQLException {
      return this.statement;
   }

   public void updateRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.updateRow();
   }

   public void insertRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.insertRow();
   }

   public void deleteRow() throws SQLException {
      this.connection.markCommitStateDirty();
      this.delegate.deleteRow();
   }

   public final <T> T unwrap(Class<T> var1) throws SQLException {
      if (var1.isInstance(this.delegate)) {
         return (T)this.delegate;
      } else if (this.delegate != null) {
         return (T)this.delegate.unwrap(var1);
      } else {
         throw new SQLException("Wrapped ResultSet is not an instance of " + var1);
      }
   }
}
