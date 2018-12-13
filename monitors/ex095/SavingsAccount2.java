import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SavingsAccount2 {
    int balance;
    ReentrantLock lock = new ReentrantLock();
    Condition balanceCondition = lock.newCondition();
    int preferredWithdrawCount  = 0;

    SavingsAccount2(int balance) {
        this.balance = balance;
    }

    int getBalance() {
        return this.balance;
    }

    int getPreferredWithdrawCount() {
        return this.preferredWithdrawCount;
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

    void ordinaryWithdraw(int k) throws InterruptedException {
        try {
            lock.lock();
            while(this.balance < k || this.preferredWithdrawCount > 0) {
                System.out.println("[*] ordinaryWithdraw blocked due to " + (this.balance < k ? "balance!" : "preferredWithdraw!"));
                balanceCondition.await();
            }
            this.balance -= k;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void preferredWithdraw(int k) {
        try {
            lock.lock();
            synchronized(this) {
                this.preferredWithdrawCount++;
            }
            System.out.println("- preferredWithdrawCount: " + this.preferredWithdrawCount);
            while(this.balance < k) {
                System.out.println("[*] preferredWithdraw transaction blocked!");
                balanceCondition.await();
            }
            this.balance -= k;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized(this) {
                this.preferredWithdrawCount--;
            }
            lock.unlock();
        }
    }
}