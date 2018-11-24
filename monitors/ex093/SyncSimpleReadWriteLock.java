import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SyncSimpleReadWriteLock {
    int readers;
    boolean writer;
    Lock readLock;
    Lock writeLock;
    final Object monitor;     // will use this object for synchronization

    public SyncSimpleReadWriteLock() {
        writer = false;
        readers = 0;
        readLock = new ReadLock();
        writeLock = new WriteLock();
        monitor = new Object();
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    class ReadLock implements Lock {
        @Override
        public void lock() {
            synchronized (monitor) {
                try {
                    while (writer) {
                        monitor.wait();
                    }
                    readers++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void unlock() {
            synchronized (monitor) {
                readers--;
                if (readers == 0)
                monitor.notifyAll();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryLock() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }

    class WriteLock implements Lock {
        @Override
        public void lock() {
            synchronized (monitor) {
                try {
                    while (readers > 0 || writer) {
                        monitor.wait();
                    }
                    writer = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void unlock() {
            synchronized (monitor) {
                writer = false;
                monitor.notifyAll();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryLock() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }
}