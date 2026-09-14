package org.apache.commons.pool2.impl;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class InterruptibleReentrantLock extends ReentrantLock {
   private static final long serialVersionUID = 1L;

   public InterruptibleReentrantLock(boolean var1) {
      super(var1);
   }

   public void interruptWaiters(Condition var1) {
      this.getWaitingThreads(var1).forEach(Thread::interrupt);
   }
}
