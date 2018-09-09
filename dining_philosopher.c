/* 
    Trying to solve the Dining Philosophers Problem
    NOTE
        change waiter's function so can use "turn" semaphore to signalize who can talk to him atm.
        the code is poorly written, I shall refactor it once I get it to work
            there are lots of unnecessary pieces of code and I did not remove them because I wasn't sure which technique I was going to use
 */

#include <stdio.h>
#include <stdlib.h>
#include <semaphore.h>
#include <pthread.h>
#include <unistd.h>
#include <stdbool.h>

// philosophers quantity
#define N 5

// defining possible states that a philosopher can assume (probably not going to use it anymore)
#define THINKING 0
#define HUNGRY 1
#define EATING 2

// prettifying output
#define RED     "\x1b[31m"
#define GREEN   "\x1b[32m"
#define YELLOW  "\x1b[33m"
#define BLUE    "\x1b[34m"
#define MAGENTA "\x1b[35m"
#define CYAN    "\x1b[36m"
#define RESET   "\x1b[0m"

// defining semaphores to be used throughout the program
sem_t mutex;
sem_t let_me_eat;
sem_t waiter_is_free;
sem_t can_eat[N+1];
sem_t turn[N+1];

// defining some functions
void *philosopher(void *);
void *waiter();
void enqueue(int);
int dequeue();
void ask_for_chopstick(int);
void return_chopstick(int);

int phil_num[N+1];
int state[N+1];
int current;
int chopsticks_available = N;

// defining our queue
typedef struct _Node {
    struct _Node *next;
    int id;
} Node;

// defining the root (first element) of our queue
Node *root = NULL;

void enqueue(int ph_id) {
    Node *n = malloc(sizeof(Node));
    n->id   = ph_id;
    n->next = NULL;
    if(!root) { // checks if our queue is empty
        root = n;
    } else {
        // iterating in the queue until it finds an empty node
        Node *it = root;

        while(it) {
            it = it->next;
        }
        it = n;
    }
}
int dequeue() {
    int ph_id = root->id;
    root = root->next;
    return ph_id;
}

int main() {
    printf("Dining philosopher problem.\n");

    pthread_t waiter_thread;
    pthread_t threads_id[N+1];

    // initializing our mutex
    sem_init(&mutex,      0, 1);
    sem_init(&let_me_eat, 0, 0);

    // initializing waiter
    pthread_create(&waiter_thread, NULL, waiter, NULL);

    sleep(2);

    int i;

    // initializing semaphores
    for (i = 1; i <= N; i++) {
        sem_init(&can_eat[i], 0, 0);
        sem_init(&turn[i], 0, 0);
    }

    // initializing philosophers
    for(i = 1; i <= N; i++) {
        phil_num[i] = i;
        pthread_create(&threads_id[i], NULL, philosopher, &phil_num[i]);
    }
    for (i = 1; i <= N; i++)
        pthread_join(threads_id[i], NULL);
}

// handler to act as our waiter
void *waiter() {
    // initializing semaphore "waiter_is_free" as 0,1 to let the philosophers know that it can serve them right away.
    sem_init(&waiter_is_free, 0, 1);
    printf(BLUE "Waiter is here!\n" RESET);
    while(1) {
        // waits until a philosopher asks to eat
        sem_wait(&let_me_eat);
        {
            // printf(YELLOW "[DEBUG - WAITER] Waiter received a let_me_eat signal and now waits for the mutex\n " RESET);
            // can only change the value of available chopsticks if you have the mutex
            sem_wait(&mutex);
            {
                // checks if there are enough chopsticks
                if(chopsticks_available > 1) {
                    // sem_post(&can_eat[current]); // send a signal to the current philosopher so he can eat
                    // printf(YELLOW "[DEBUG - QUEUE] There are enough chopsticks. Philosopher %d is allowed to eat!\n" RESET, c);
                    sem_post(&can_eat[current]);
                    chopsticks_available -= 2;
                } else {
                    enqueue(current);
                    printf(RED "Philosopher %d was unable to eat, adding to queue\n" RESET, current);
                }
            }
            sem_post(&mutex);
        }
        sem_post(&waiter_is_free);
    }
}

void *philosopher(void *num) {
    int i = *((int*)num);
    printf("Philosopher %d is thinking\n", i);
    while (1) {
        sleep(1);
        ask_for_chopstick(i);
        printf(GREEN "Philosopher %d is eating\n" RESET, i);
        sleep(0);
        return_chopstick(i);
    }
}

void ask_for_chopstick(int ph_num) {

    sem_wait(&waiter_is_free);
    {
        // enqueue(ph_num);
        current = ph_num;
        // printf(YELLOW "[DEBUG - QUEUE] Philosopher %d is now on queue\n" RESET, ph_num);
        sem_post(&let_me_eat);
        // printf(YELLOW "[DEBUG] Philosopher %d is waiting a signal that he can eat\n" RESET, ph_num);
        sem_wait(&can_eat[ph_num]);
        // printf(YELLOW "[DEBUG] Philosopher %d just received a signal that he can eat\n" RESET, ph_num);
    }
}

void return_chopstick(int ph_num) {
    // we need to return the chopsticks to the waiter so another philosopher can eat
    sem_wait(&mutex);
    {
        chopsticks_available += 2;
        printf(CYAN "Philosopher %d returned chopsticks to the waiter\n" RESET, ph_num);
    }
    sem_post(&mutex);
    printf("Philosopher %d is thinking\n", ph_num);
}
