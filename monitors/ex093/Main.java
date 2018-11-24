public class Main {
    public static void main(String[] args) throws InterruptedException {
        SyncSimpleReadWriteLock rwLock = new SyncSimpleReadWriteLock();
        int readDuration = 1000;
        int writeDuration = 2000;

        Thread r1 = new Thread(new Reader(rwLock, readDuration), "r1");
        Thread r2 = new Thread(new Reader(rwLock, readDuration), "r2");
        Thread r3 = new Thread(new Reader(rwLock, readDuration), "r3");
        Thread w1 = new Thread(new Writer(rwLock, writeDuration), "w1");
        Thread w2 = new Thread(new Writer(rwLock, writeDuration), "w2");
        Thread w3 = new Thread(new Writer(rwLock, writeDuration), "w3");
        w1.start();
        r1.start();
        r2.start();
        Thread.sleep(2500);
        r3.start();
        w2.start();
        w3.start();
    }
}
 class Reader implements Runnable {
    SyncSimpleReadWriteLock rwLock;
    int duration;

    Reader(SyncSimpleReadWriteLock rwLock, int duration) {
        this.rwLock = rwLock;
        this.duration = duration;
    }

    @Override
    public void run() {
        rwLock.readLock().lock();
        System.out.println(Thread.currentThread().getName() + " is reading.");
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " finished reading.");
        rwLock.readLock().unlock();
    }
}

class Writer implements Runnable {
    SyncSimpleReadWriteLock rwLock;
    int duration;

    Writer(SyncSimpleReadWriteLock rwLock, int duration) {
        this.rwLock = rwLock;
        this.duration = duration;
    }

    @Override
    public void run() {
        rwLock.writeLock().lock();
        System.out.println(Thread.currentThread().getName() + " is writing.");
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " finished writing.");
        rwLock.writeLock().unlock();
    }
}



