import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;

public class SavingsAccount1 {
    int balance;
    Reentrant lock = ReentrantLock();
    Condition balanceCondition = lock.newCondition();

    SavingsAccount(int balance) {
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

    void withdraw(int k) throws Exception {
        try {
            lock.lock();
            while(this.balance < k) {
                balanceCondition.await();
            }
            this.balance -= k;
        } finally {
            lock.unlock();
        }
    }
}