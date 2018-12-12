import java.util.concurrent.atomic.AtomicLong;

public class SyncSharedBathroom implements SharedBathroom {
    boolean maleBlocked;
    final Object maleWaiting;
    AtomicLong nMale;
    boolean femaleBlocked;
    final Object femaleWaiting;
    AtomicLong nFemale;

    SyncSharedBathroom(){
        maleBlocked = false;
        maleWaiting = new Object();
        nMale = new AtomicLong(0);
        femaleBlocked = false;
        femaleWaiting = new Object();
        nFemale = new AtomicLong(0);
    }

    @Override
    public synchronized void enterMale() {
        synchronized (maleWaiting) {
            while (maleBlocked) {
                try {
                    maleWaiting.wait();
                    femaleBlocked = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nMale.incrementAndGet();
        }
    }

    @Override
    public synchronized void leaveMale() {
        synchronized (femaleWaiting) {
            nMale.decrementAndGet();
            if (nMale.get() == 0) {
                femaleBlocked = false;
            }
            femaleWaiting.notifyAll();
        }
    }

    @Override
    public synchronized void enterFemale() {
        synchronized (femaleWaiting) {
            while (femaleBlocked) {
                try {
                    femaleWaiting.wait();
                    maleBlocked = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            nFemale.incrementAndGet();
        }
    }

    @Override
    public synchronized void leaveFemale() {
        synchronized (maleWaiting) {
            nFemale.decrementAndGet();
            if (nFemale.get() == 0) {
                maleBlocked = false;
            }
            maleWaiting.notifyAll();
        }
    }
}