package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.SQLExceptionOverride;
import com.zaxxer.hikari.util.FastList;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProxyConnection implements Connection {
   static final int DIRTY_BIT_READONLY = 1;
   static final int DIRTY_BIT_AUTOCOMMIT = 2;
   static final int DIRTY_BIT_ISOLATION = 4;
   static final int DIRTY_BIT_CATALOG = 8;
   static final int DIRTY_BIT_NETTIMEOUT = 16;
   static final int DIRTY_BIT_SCHEMA = 32;
   private static final Logger LOGGER = LoggerFactory.getLogger(ProxyConnection.class);
   private static final Set<String> ERROR_STATES = new HashSet();
   private static final Set<Integer> ERROR_CODES;
   protected Connection delegate;
   private final PoolEntry poolEntry;
   private final ProxyLeakTask leakTask;
   private final FastList<Statement> openStatements;
   private int dirtyBits;
   private boolean isCommitStateDirty;
   private boolean isReadOnly;
   private boolean isAutoCommit;
   private int networkTimeout;
   private int transactionIsolation;
   private String dbcatalog;
   private String dbschema;

   protected ProxyConnection(PoolEntry var1, Connection var2, FastList<Statement> var3, ProxyLeakTask var4, boolean var5, boolean var6) {
      this.poolEntry = var1;
      this.delegate = var2;
      this.openStatements = var3;
      this.leakTask = var4;
      this.isReadOnly = var5;
      this.isAutoCommit = var6;
   }

   public final String toString() {
      String var10000 = this.getClass().getSimpleName();
      return var10000 + "@" + System.identityHashCode(this) + " wrapping " + this.delegate;
   }

   final boolean getAutoCommitState() {
      return this.isAutoCommit;
   }

   final String getCatalogState() {
      return this.dbcatalog;
   }

   final String getSchemaState() {
      return this.dbschema;
   }

   final int getTransactionIsolationState() {
      return this.transactionIsolation;
   }

   final boolean getReadOnlyState() {
      return this.isReadOnly;
   }

   final int getNetworkTimeoutState() {
      return this.networkTimeout;
   }

   final PoolEntry getPoolEntry() {
      return this.poolEntry;
   }

   final SQLException checkException(SQLException var1) {
      boolean var2 = false;
      SQLException var3 = var1;
      SQLExceptionOverride var4 = this.poolEntry.getPoolBase().exceptionOverride;

      for(int var5 = 0; this.delegate != ProxyConnection.ClosedConnection.CLOSED_CONNECTION && var3 != null && var5 < 10; ++var5) {
         String var6 = var3.getSQLState();
         if (var6 != null && var6.startsWith("08") || var3 instanceof SQLTimeoutException || ERROR_STATES.contains(var6) || ERROR_CODES.contains(var3.getErrorCode())) {
            if (var4 == null || var4.adjudicate(var3) != SQLExceptionOverride.Override.DO_NOT_EVICT) {
               var2 = true;
            }
            break;
         }

         var3 = var3.getNextException();
      }

      if (var2) {
         SQLException var7 = var3 != null ? var3 : var1;
         LOGGER.warn("{} - Connection {} marked as broken because of SQLSTATE({}), ErrorCode({})", this.poolEntry.getPoolName(), this.delegate, var7.getSQLState(), var7.getErrorCode(), var7);
         this.leakTask.cancel();
         this.poolEntry.evict("(connection is broken)");
         this.delegate = ProxyConnection.ClosedConnection.CLOSED_CONNECTION;
      }

      return var1;
   }

   final synchronized void untrackStatement(Statement var1) {
      this.openStatements.remove(var1);
   }

   final void markCommitStateDirty() {
      if (!this.isAutoCommit) {
         this.isCommitStateDirty = true;
      }

   }

   void cancelLeakTask() {
      this.leakTask.cancel();
   }

   private synchronized <T extends Statement> T trackStatement(T var1) {
      this.openStatements.add(var1);
      return (T)var1;
   }

   private synchronized void closeStatements() {
      int var1 = this.openStatements.size();
      if (var1 > 0) {
         for(int var2 = 0; var2 < var1 && this.delegate != ProxyConnection.ClosedConnection.CLOSED_CONNECTION; ++var2) {
            try {
               Statement var3 = this.openStatements.get(var2);
               if (var3 != null) {
                  var3.close();
               }
            } catch (SQLException var4) {
               LOGGER.warn((String)"{} - Connection {} marked as broken because of an exception closing open statements during Connection.close()", (Object)this.poolEntry.getPoolName(), (Object)this.delegate);
               this.leakTask.cancel();
               this.poolEntry.evict("(exception closing Statements during Connection.close())");
               this.delegate = ProxyConnection.ClosedConnection.CLOSED_CONNECTION;
            }
         }

         this.openStatements.clear();
      }

   }

   public final void close() throws SQLException {
      this.closeStatements();
      if (this.delegate != ProxyConnection.ClosedConnection.CLOSED_CONNECTION) {
         this.leakTask.cancel();

         try {
            if (this.isCommitStateDirty && !this.isAutoCommit) {
               this.delegate.rollback();
               LOGGER.debug((String)"{} - Executed rollback on connection {} due to dirty commit state on close().", (Object)this.poolEntry.getPoolName(), (Object)this.delegate);
            }

            if (this.dirtyBits != 0) {
               this.poolEntry.resetConnectionState(this, this.dirtyBits);
            }

            this.delegate.clearWarnings();
         } catch (SQLException var5) {
            if (!this.poolEntry.isMarkedEvicted()) {
               throw this.checkException(var5);
            }
         } finally {
            this.delegate = ProxyConnection.ClosedConnection.CLOSED_CONNECTION;
            this.poolEntry.recycle();
         }
      }

   }

   public boolean isClosed() throws SQLException {
      return this.delegate == ProxyConnection.ClosedConnection.CLOSED_CONNECTION;
   }

   public Statement createStatement() throws SQLException {
      return ProxyFactory.getProxyStatement(this, this.trackStatement(this.delegate.createStatement()));
   }

   public Statement createStatement(int var1, int var2) throws SQLException {
      return ProxyFactory.getProxyStatement(this, this.trackStatement(this.delegate.createStatement(var1, var2)));
   }

   public Statement createStatement(int var1, int var2, int var3) throws SQLException {
      return ProxyFactory.getProxyStatement(this, this.trackStatement(this.delegate.createStatement(var1, var2, var3)));
   }

   public CallableStatement prepareCall(String var1) throws SQLException {
      return ProxyFactory.getProxyCallableStatement(this, (CallableStatement)this.trackStatement(this.delegate.prepareCall(var1)));
   }

   public CallableStatement prepareCall(String var1, int var2, int var3) throws SQLException {
      return ProxyFactory.getProxyCallableStatement(this, (CallableStatement)this.trackStatement(this.delegate.prepareCall(var1, var2, var3)));
   }

   public CallableStatement prepareCall(String var1, int var2, int var3, int var4) throws SQLException {
      return ProxyFactory.getProxyCallableStatement(this, (CallableStatement)this.trackStatement(this.delegate.prepareCall(var1, var2, var3, var4)));
   }

   public PreparedStatement prepareStatement(String var1) throws SQLException {
      return ProxyFactory.getProxyPreparedStatement(this, (PreparedStatement)this.trackStatement(this.delegate.prepareStatement(var1)));
   }

   public PreparedStatement prepareStatement(String var1, int var2) throws SQLException {
      return ProxyFactory.getProxyPreparedStatement(this, (PreparedStatement)this.trackStatement(this.delegate.prepareStatement(var1, var2)));
   }

   public PreparedStatement prepareStatement(String var1, int var2, int var3) throws SQLException {
      return ProxyFactory.getProxyPreparedStatement(this, (PreparedStatement)this.trackStatement(this.delegate.prepareStatement(var1, var2, var3)));
   }

   public PreparedStatement prepareStatement(String var1, int var2, int var3, int var4) throws SQLException {
      return ProxyFactory.getProxyPreparedStatement(this, (PreparedStatement)this.trackStatement(this.delegate.prepareStatement(var1, var2, var3, var4)));
   }

   public PreparedStatement prepareStatement(String var1, int[] var2) throws SQLException {
      return ProxyFactory.getProxyPreparedStatement(this, (PreparedStatement)this.trackStatement(this.delegate.prepareStatement(var1, var2)));
   }

   public PreparedStatement prepareStatement(String var1, String[] var2) throws SQLException {
      return ProxyFactory.getProxyPreparedStatement(this, (PreparedStatement)this.trackStatement(this.delegate.prepareStatement(var1, var2)));
   }

   public DatabaseMetaData getMetaData() throws SQLException {
      this.markCommitStateDirty();
      return ProxyFactory.getProxyDatabaseMetaData(this, this.delegate.getMetaData());
   }

   public void commit() throws SQLException {
      this.delegate.commit();
      this.isCommitStateDirty = false;
   }

   public void rollback() throws SQLException {
      this.delegate.rollback();
      this.isCommitStateDirty = false;
   }

   public void rollback(Savepoint var1) throws SQLException {
      this.delegate.rollback(var1);
      this.isCommitStateDirty = false;
   }

   public void setAutoCommit(boolean var1) throws SQLException {
      this.delegate.setAutoCommit(var1);
      this.isAutoCommit = var1;
      this.dirtyBits |= 2;
   }

   public void setReadOnly(boolean var1) throws SQLException {
      this.delegate.setReadOnly(var1);
      this.isReadOnly = var1;
      this.isCommitStateDirty = false;
      this.dirtyBits |= 1;
   }

   public void setTransactionIsolation(int var1) throws SQLException {
      this.delegate.setTransactionIsolation(var1);
      this.transactionIsolation = var1;
      this.dirtyBits |= 4;
   }

   public void setCatalog(String var1) throws SQLException {
      this.delegate.setCatalog(var1);
      this.dbcatalog = var1;
      this.dirtyBits |= 8;
   }

   public void setNetworkTimeout(Executor var1, int var2) throws SQLException {
      this.delegate.setNetworkTimeout(var1, var2);
      this.networkTimeout = var2;
      this.dirtyBits |= 16;
   }

   public void setSchema(String var1) throws SQLException {
      this.delegate.setSchema(var1);
      this.dbschema = var1;
      this.dirtyBits |= 32;
   }

   public final boolean isWrapperFor(Class<?> var1) throws SQLException {
      return var1.isInstance(this.delegate) || this.delegate != null && this.delegate.isWrapperFor(var1);
   }

   public final <T> T unwrap(Class<T> var1) throws SQLException {
      if (var1.isInstance(this.delegate)) {
         return (T)this.delegate;
      } else if (this.delegate != null) {
         return (T)this.delegate.unwrap(var1);
      } else {
         throw new SQLException("Wrapped connection is not an instance of " + var1);
      }
   }

   static {
      ERROR_STATES.add("0A000");
      ERROR_STATES.add("57P01");
      ERROR_STATES.add("57P02");
      ERROR_STATES.add("57P03");
      ERROR_STATES.add("01002");
      ERROR_STATES.add("JZ0C0");
      ERROR_STATES.add("JZ0C1");
      ERROR_CODES = new HashSet();
      ERROR_CODES.add(500150);
      ERROR_CODES.add(2399);
      ERROR_CODES.add(1105);
   }

   private static final class ClosedConnection {
      static final Connection CLOSED_CONNECTION = getClosedConnection();

      private static Connection getClosedConnection() {
         InvocationHandler var0 = (var0x, var1, var2) -> {
            String var3 = var1.getName();
            if ("isClosed".equals(var3)) {
               return Boolean.TRUE;
            } else if ("isValid".equals(var3)) {
               return Boolean.FALSE;
            } else if ("abort".equals(var3)) {
               return Void.TYPE;
            } else if ("close".equals(var3)) {
               return Void.TYPE;
            } else if ("toString".equals(var3)) {
               return ClosedConnection.class.getCanonicalName();
            } else {
               throw new SQLException("Connection is closed");
            }
         };
         return (Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class[]{Connection.class}, var0);
      }
   }
}
