package com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ProxyDatabaseMetaData implements DatabaseMetaData {
   protected final ProxyConnection connection;
   protected final DatabaseMetaData delegate;

   ProxyDatabaseMetaData(ProxyConnection var1, DatabaseMetaData var2) {
      this.connection = var1;
      this.delegate = var2;
   }

   final SQLException checkException(SQLException var1) {
      return this.connection.checkException(var1);
   }

   public final String toString() {
      String var1 = this.delegate.toString();
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "@" + System.identityHashCode(this) + " wrapping " + var1;
   }

   public final Connection getConnection() {
      return this.connection;
   }

   public ResultSet getProcedures(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getProcedures(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getProcedureColumns(String var1, String var2, String var3, String var4) throws SQLException {
      ResultSet var5 = this.delegate.getProcedureColumns(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getTables(String var1, String var2, String var3, String[] var4) throws SQLException {
      ResultSet var5 = this.delegate.getTables(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getSchemas() throws SQLException {
      ResultSet var1 = this.delegate.getSchemas();
      Statement var2 = var1.getStatement();
      if (var2 != null) {
         var2 = ProxyFactory.getProxyStatement(this.connection, var2);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var2, var1);
   }

   public ResultSet getCatalogs() throws SQLException {
      ResultSet var1 = this.delegate.getCatalogs();
      Statement var2 = var1.getStatement();
      if (var2 != null) {
         var2 = ProxyFactory.getProxyStatement(this.connection, var2);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var2, var1);
   }

   public ResultSet getTableTypes() throws SQLException {
      ResultSet var1 = this.delegate.getTableTypes();
      Statement var2 = var1.getStatement();
      if (var2 != null) {
         var2 = ProxyFactory.getProxyStatement(this.connection, var2);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var2, var1);
   }

   public ResultSet getColumns(String var1, String var2, String var3, String var4) throws SQLException {
      ResultSet var5 = this.delegate.getColumns(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getColumnPrivileges(String var1, String var2, String var3, String var4) throws SQLException {
      ResultSet var5 = this.delegate.getColumnPrivileges(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getTablePrivileges(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getTablePrivileges(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getBestRowIdentifier(String var1, String var2, String var3, int var4, boolean var5) throws SQLException {
      ResultSet var6 = this.delegate.getBestRowIdentifier(var1, var2, var3, var4, var5);
      Statement var7 = var6.getStatement();
      if (var7 != null) {
         var7 = ProxyFactory.getProxyStatement(this.connection, var7);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var7, var6);
   }

   public ResultSet getVersionColumns(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getVersionColumns(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getPrimaryKeys(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getPrimaryKeys(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getImportedKeys(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getImportedKeys(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getExportedKeys(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getExportedKeys(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getCrossReference(String var1, String var2, String var3, String var4, String var5, String var6) throws SQLException {
      ResultSet var7 = this.delegate.getCrossReference(var1, var2, var3, var4, var5, var6);
      Statement var8 = var7.getStatement();
      if (var8 != null) {
         var8 = ProxyFactory.getProxyStatement(this.connection, var8);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var8, var7);
   }

   public ResultSet getTypeInfo() throws SQLException {
      ResultSet var1 = this.delegate.getTypeInfo();
      Statement var2 = var1.getStatement();
      if (var2 != null) {
         var2 = ProxyFactory.getProxyStatement(this.connection, var2);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var2, var1);
   }

   public ResultSet getIndexInfo(String var1, String var2, String var3, boolean var4, boolean var5) throws SQLException {
      ResultSet var6 = this.delegate.getIndexInfo(var1, var2, var3, var4, var5);
      Statement var7 = var6.getStatement();
      if (var7 != null) {
         var7 = ProxyFactory.getProxyStatement(this.connection, var7);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var7, var6);
   }

   public ResultSet getUDTs(String var1, String var2, String var3, int[] var4) throws SQLException {
      ResultSet var5 = this.delegate.getUDTs(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getSuperTypes(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getSuperTypes(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getSuperTables(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getSuperTables(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getAttributes(String var1, String var2, String var3, String var4) throws SQLException {
      ResultSet var5 = this.delegate.getAttributes(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getSchemas(String var1, String var2) throws SQLException {
      ResultSet var3 = this.delegate.getSchemas(var1, var2);
      Statement var4 = var3.getStatement();
      if (var4 != null) {
         var4 = ProxyFactory.getProxyStatement(this.connection, var4);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var4, var3);
   }

   public ResultSet getClientInfoProperties() throws SQLException {
      ResultSet var1 = this.delegate.getClientInfoProperties();
      Statement var2 = var1.getStatement();
      if (var2 != null) {
         var2 = ProxyFactory.getProxyStatement(this.connection, var2);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var2, var1);
   }

   public ResultSet getFunctions(String var1, String var2, String var3) throws SQLException {
      ResultSet var4 = this.delegate.getFunctions(var1, var2, var3);
      Statement var5 = var4.getStatement();
      if (var5 != null) {
         var5 = ProxyFactory.getProxyStatement(this.connection, var5);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var5, var4);
   }

   public ResultSet getFunctionColumns(String var1, String var2, String var3, String var4) throws SQLException {
      ResultSet var5 = this.delegate.getFunctionColumns(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public ResultSet getPseudoColumns(String var1, String var2, String var3, String var4) throws SQLException {
      ResultSet var5 = this.delegate.getPseudoColumns(var1, var2, var3, var4);
      Statement var6 = var5.getStatement();
      if (var6 != null) {
         var6 = ProxyFactory.getProxyStatement(this.connection, var6);
      }

      return ProxyFactory.getProxyResultSet(this.connection, (ProxyStatement)var6, var5);
   }

   public final <T> T unwrap(Class<T> var1) throws SQLException {
      if (var1.isInstance(this.delegate)) {
         return (T)this.delegate;
      } else if (this.delegate != null) {
         return (T)this.delegate.unwrap(var1);
      } else {
         throw new SQLException("Wrapped DatabaseMetaData is not an instance of " + var1);
      }
   }
}
