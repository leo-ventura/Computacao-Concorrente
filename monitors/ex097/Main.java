import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static Rooms rooms;

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(i);
            test();
        }
    }

    public static void test() {
        int nRooms = 8;
        int nThreads = 1000;
        int maxTime = 25;
        rooms = new Rooms(nRooms);
        Thread[] ts = new Thread[nThreads];

        for (int i = 0; i < ts.length; i++) {
            ts[i] = new Thread(new Guest(ThreadLocalRandom.current().nextInt(nRooms), maxTime));
        }

        for (Thread t : ts) {
            t.start();
        }

        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class Guest implements Runnable {
    int room;
    int maxTime;

    public Guest(int room, int maxtime) {
        this.room = room;
        this.maxTime = maxtime;
    }

    @Override
    public void run() {
        int arrival = ThreadLocalRandom.current().nextInt(maxTime);
        int stay = ThreadLocalRandom.current().nextInt(maxTime);

        try {
            Thread.sleep(arrival);
            Main.rooms.enter(room);
            Thread.sleep(stay);
            if (Main.rooms.exit()) {
                Main.rooms.callExitHandler(room);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}


