import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockSharedBathroom implements SharedBathroom {
    boolean maleBlocked;
    long nMale;
    boolean femaleBlocked;
    long nFemale;
    Lock lock;
    Condition maleUnblocked;
    Condition femaleUnblocked;


    public LockSharedBathroom() {
        maleBlocked = false;
        femaleBlocked = false;
        nMale = 0;
        nFemale = 0;
        lock = new ReentrantLock(true);
        maleUnblocked = lock.newCondition();
        femaleUnblocked = lock.newCondition();
    }

    @Override
    public void enterMale() {
        lock.lock();
        try {
            while (maleBlocked) {
                maleUnblocked.await();
                femaleBlocked = true;
            }
            nMale++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void leaveMale() {
        lock.lock();
        try {
            nMale--;
            if (nMale == 0) {
                femaleBlocked = false;
            }
            femaleUnblocked.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void enterFemale() {
        lock.lock();
        try {
            while(femaleBlocked) {
                femaleUnblocked.await();
                maleBlocked = true;
            }
            nFemale++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void leaveFemale() {
        lock.lock();
        try {
            nFemale--;
            if (nFemale == 0) {
                maleBlocked = false;
            }
            maleUnblocked.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
