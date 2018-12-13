import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // testSavings1();
        testSavings2();
    }

    static void testSavings1() {
        int maxThreads = 1000;
        Thread[] threads = new Thread[maxThreads];
        SavingsAccount1 account = new SavingsAccount1(0);

        for(int i = 0; i < maxThreads; i++)
            threads[i] = new Thread(new Account1(account));

        for(int i = 0; i < maxThreads; i++)
            threads[i].start();

        try {
            for(int i = 0; i < maxThreads; i++)
            threads[i].join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void testSavings2() {
        int maxThreads = 1000;
        Thread[] threads = new Thread[maxThreads];
        SavingsAccount2 account = new SavingsAccount2(0);

        for(int i = 0; i < maxThreads; i++)
            threads[i] = new Thread(new Account2(account));

        for(int i = 0; i < maxThreads; i++)
            threads[i].start();

        try {
            for(int i = 0; i < maxThreads; i++)
            threads[i].join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Account1 implements Runnable {
    SavingsAccount1 account;

    Account1(SavingsAccount1 account) {
        this.account = account;
    }

    // testando usando depositos e retiradas de valores aleatorios
    @Override
    public void run() {
        try {
            // apenas testando alguns casos de teste (provalvemente deveria desenvolver melhor
            // mas é suficiente pra ilustrar a utilização das threads)
            System.out.println("- Balance: " + this.account.getBalance());

            int depositValue = ThreadLocalRandom.current().nextInt(1,10);
            System.out.println("[" + Thread.currentThread().getId() + "]" + " Depositing " + depositValue);
            this.account.deposit(depositValue);


            int withdrawValue = ThreadLocalRandom.current().nextInt(1,10);
            System.out.println("[" + Thread.currentThread().getId() + "]" + " Withdrawing " + withdrawValue);
            this.account.withdraw(withdrawValue);

        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Final balance: " + this.account.getBalance());
        }
    }
}

class Account2 implements Runnable {
    SavingsAccount2 account;

    Account2(SavingsAccount2 account) {
        this.account = account;
    }

    @Override
    public void run() {
        try {
            // apenas testando alguns casos de teste (provalvemente deveria desenvolver melhor
            // mas é suficiente pra ilustrar a utilização das threads)
            System.out.println("- Balance: " + this.account.getBalance());

            int depositValue = ThreadLocalRandom.current().nextInt(1,20);
            System.out.println("[" + Thread.currentThread().getId() + "]" + " Depositing " + depositValue);
            this.account.deposit(depositValue);

            int ordinaryValue = ThreadLocalRandom.current().nextInt(1, 10);
            System.out.println("[" + Thread.currentThread().getId() + "]" + " ordinary withdrawing " + ordinaryValue);
            this.account.ordinaryWithdraw(ordinaryValue);

            int preferredValue = ThreadLocalRandom.current().nextInt(1, 10);
            System.out.println("[" + Thread.currentThread().getId() + "]" + " preferred withdrawing " + preferredValue);
            this.account.preferredWithdraw(preferredValue);

        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Final balance: " + this.account.getBalance());
        }
    }
}