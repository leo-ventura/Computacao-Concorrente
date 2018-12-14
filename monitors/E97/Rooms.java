import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Rooms {
    int nRooms;
    int threadsInside;
    ReentrantLock lock;
    Condition[] canEnter;
    Handler[] handlers;

    // inUse vai guardar o número do quarto em uso
    // casos especiais: nenhum quarto em uso, handler rodando
    int inUse;
    static final int allFree = -1;
    static final int exitHandlerFlag = -2;

    public Rooms(int m) {
        nRooms = m;
        inUse = allFree;
        threadsInside = 0;
        lock = new ReentrantLock(true);
        canEnter = new Condition[m];
        handlers = new ExitHandler[m];

        for (int i = 0; i < m; i++) {
            canEnter[i] = lock.newCondition();
            setExitHandler(i, new ExitHandler(i));
        }
    }

    public void enter(int i) {
        lock.lock();
//        System.out.println("Trying room " + i);
        try {
            while (inUse != i && inUse != allFree) {
//                System.out.println("Room " + i + " unusable. Awaiting");
                canEnter[i].await();
            }
//            System.out.println("Entering room " + i);
            inUse = i;
            threadsInside++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public boolean exit() {
        lock.lock();
        try {
            threadsInside--;
//            System.out.println("Leaving");
            if (threadsInside == 0) {
                inUse = exitHandlerFlag;
                return true;
            }
        }finally {
            lock.unlock();
        }
        return false;
    }

    public void callExitHandler(int i) {
//        System.out.println("Calling exitHandler on room " + i);
        handlers[i].onEmpty();
    }


   public void setExitHandler(int i, Rooms.Handler h) {
        handlers[i] = h;
    }

    public interface Handler {
        void onEmpty();

    }

    private class ExitHandler implements Handler {
        int roomNum;
        ExitHandler(int roomNum) {
            this.roomNum = roomNum;
        }

        @Override
        public void onEmpty() {
            lock.lock();
            try {
                inUse = nextFreeRoom();
                if (inUse >= 0) {
//                    System.out.println("Notifying people waiting for room " + inUse);
                    canEnter[inUse].signalAll();
                }
            } finally {
                lock.unlock();
            }
        }

        private int nextFreeRoom() {
            for (int i = 0; i < nRooms; i++) {
                int candidate = (nRooms + i + 1) % nRooms;
                if (lock.hasWaiters(canEnter[candidate])) {
                    return candidate;
                }
            }
            return allFree;
        }
    }
}
