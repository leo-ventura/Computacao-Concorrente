import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SavingsAccount1 {
    int balance;
    ReentrantLock lock = new ReentrantLock();
    Condition balanceCondition = lock.newCondition();

    SavingsAccount1(int balance) {
        this.balance = balance;
    }

    int getBalance() {
        return this.balance;
    }

    void deposit(int k) {
        try {
            lock.lock();
            this.balance += k;
            balanceCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    void withdraw(int k) throws InterruptedException {
        try {
            lock.lock();
            while(this.balance < k) {
                System.out.println("[*] Account blocked! Waiting credit to proceed.");
                balanceCondition.await();
            }
            this.balance -= k;
        } finally {
            lock.unlock();
        }
    }
}