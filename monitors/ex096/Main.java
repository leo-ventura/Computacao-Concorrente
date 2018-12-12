import java.util.concurrent.ThreadLocalRandom;

public class Main {
    static long start;

    public static void main(String[] args) throws InterruptedException {
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            System.out.println(i);
            testRun1();
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    static void testRun1() {
//        SharedBathroom bathroom = new LockSharedBathroom();
        SharedBathroom bathroom = new SyncSharedBathroom();
        int maxThreads= 1000;
        Thread[] males = new Thread[ThreadLocalRandom.current().nextInt(1, maxThreads)];
        Thread[] females = new Thread[ThreadLocalRandom.current().nextInt(1, maxThreads)];

        for (int i = 0; i < males.length; i++)
            males[i] = new Thread(new MaleThread(bathroom, 5));

        for (int i = 0; i < females.length; i++)
            females[i] = new Thread(new FemaleThread(bathroom, 5));

        for (int i = 0; i < males.length; i++)
            males[i].start();

        for (int i = 0; i < females.length; i++)
            females[i].start();

        try {
            for (int i = 0; i < males.length; i++)
                males[i].join();

            for (int i = 0; i < females.length; i++)
                females[i].join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

class MaleThread implements Runnable {
    SharedBathroom bathroom;
    long maxtime;
    MaleThread(SharedBathroom bathroom, long maxtime) {
        this.bathroom = bathroom;
        this.maxtime = maxtime;
    }
    @Override
    public void run() {
        long arrival = ThreadLocalRandom.current().nextLong(maxtime);
        long action = ThreadLocalRandom.current().nextLong(maxtime);

        try {
            Thread.sleep(arrival);
//            System.out.println("- M\t" + (System.currentTimeMillis() - Main.start));
            bathroom.enterMale();
            Thread.sleep(action);
            bathroom.leaveMale();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class FemaleThread implements Runnable {
    SharedBathroom bathroom;
    long maxtime;
    FemaleThread(SharedBathroom bathroom, long maxtime) {
        this.bathroom = bathroom;
        this.maxtime = maxtime;
    }
    @Override
    public void run() {
        long arrival = ThreadLocalRandom.current().nextLong(maxtime);
        long action = ThreadLocalRandom.current().nextLong(maxtime);

        try {
            Thread.sleep(arrival);
//            System.out.println("- F\t" + (System.currentTimeMillis() - Main.start));            bathroom.enterFemale();
            Thread.sleep(action);
            bathroom.leaveFemale();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

