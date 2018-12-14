public class Prisoner extends Thread {
    int id;
    Room room;
    Warden warden;
    int state = 0;
    int count = 0;

    final Object inCell = new Object();
    final Object leaveCell = new Object();

    public static final int
            IN_CELL    = 1,
            IN_ROOM    = 2,
            FREE       = 3,
            DEAD       = 4;

    public Prisoner(int id, Room room, Warden warden){
        this.id = id;
        this.room = room;
        this.warden = warden;
    }

    public void run() {
        try {
            Thread.currentThread().setName("Prisoner"+id);
            this.live();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void live() throws InterruptedException {
        while (true) {

            synchronized (inCell) {
                this.state = IN_CELL;
                inCell.notifyAll();
            }

            synchronized (leaveCell) {
                System.out.println("Prisoner: " + this.id + " wait in cell");
                while (this.state == IN_CELL)
                    leaveCell.wait();
            }

            // I got out of the cell...
            // am I free or dead? or should I go to the room?
            if (this.state != IN_ROOM)
                break;

            // I am in the room
            // do my custom action
            this.roomAction();

            // tell warden that I'm leaving the room
            this.warden.notifyPrisonerLeavingRoom();
        }

        if (this.state == FREE)
            // I'm free... I'm free!!!
            System.out.println("Prisoner " + this.id + " is free!");
        else
            // DEAD
            System.out.println("Prisoner " + this.id + " is dead!");
    }

    protected void roomAction() {
        // If I didn't switch the trigger twice, then do it
        if (this.count < 2 && !this.room.isTriggerSet())
        {
            System.out.println("Prisoner: " + this.id + " turns trigger on!");
            this.count++;
            this.room.setTrigger();
        }
        else {
            System.out.println("Prisoner: " + this.id + " in room... nothing to do!");
        }
    }

    public void free() {
        synchronized (leaveCell) {
            this.state = FREE;
            leaveCell.notifyAll();
        }
    }

    public void kill() {
        synchronized (leaveCell) {
            this.state = DEAD;
            leaveCell.notifyAll();
        }
    }

    public void gotoRoom() {
        synchronized (leaveCell) {
            System.out.println("Prisoner: " + this.id + " going to room.");
            this.state = IN_ROOM;
            leaveCell.notifyAll();
        }
    }

    public void waitUntilInCell() {
        synchronized (inCell) {
            try {
                while (this.state != Prisoner.IN_CELL) {
                    this.inCell.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
