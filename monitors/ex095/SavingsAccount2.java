import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class SavingsAccount2 {
    int balance;
    ReentrantLock lock = new ReentrantLock();
    Condition balanceCondition = lock.newCondition();
    int preferedWithdrawCount  = 0;

    SavingsAccount2(int balance) {
        this.balance = balance;
    }

    int getBalance() {
        return this.balance;
    }

    int getPreferedWithdrawCount() {
        return this.preferedWithdrawCount;
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
            while(this.balance < k || this.preferedWithdrawCount > 0) {
                System.out.println("[*] ordinaryWithdraw blocked due to " + (this.balance < k ? "balance!" : "preferedWithdraw!"));
                balanceCondition.await();
            }
            this.balance -= k;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    void preferedWithdraw(int k) {
        try {
            lock.lock();
            synchronized(this) {
                this.preferedWithdrawCount++;
            }
            System.out.println("- preferedWithdrawCount: " + this.preferedWithdrawCount);
            while(this.balance < k) {
                System.out.println("[*] preferedWithdraw transaction blocked!");
                balanceCondition.await();
            }
            this.balance -= k;
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {
            synchronized(this) {
                this.preferedWithdrawCount--;
            }
            lock.unlock();
        }
    }
}