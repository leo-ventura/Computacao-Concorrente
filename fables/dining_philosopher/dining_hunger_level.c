/***********************************************************
 * == DERIVATIVE WORK LICENSE ==
 * This code is a modified version of the one found at
 * www.c-program-example.com. This code is subject to the
 * original license as stated below. As for the changes that
 * I did, you can use it however you wish, as long as you
 * don't hold me liable. This means that if
 * the original author gives permission for you to use his
 * code in some way, then you can also use this derivative
 * work the same way.
 *
 * == UNDERLYING WORK LICENSE ==
 * You can use all the programs on  www.c-program-example.com
 * for personal and learning purposes. For permissions to use the
 * programs for commercial purposes,
 * contact info@c-program-example.com
 * To find more C programs, do visit www.c-program-example.com
 * and browse!
 * 
 *                      Happy Coding
 ***********************************************************/

// SOLUTION:
// The philosophers will always get the left chopstick first
// and then get the right one. To avoid the deadlock problem,
// if the right chopstick is not available, the philosopher
// drops the one that he got already. But when that happens,
// the philosopher gets hungrier. If he gets too hungry, he
// decides to stop trying to avoid deadlock and holds the first
// chopstick until he gets the second one.

#include <stdio.h>
#include <semaphore.h>
#include <pthread.h>
#include <unistd.h>

#define N 5 // number of philosophers

#define LEFT ((ph_num + 0) % N)
#define RIGHT ((ph_num + 1) % N)

#define MAX_HUNGER 2

sem_t chopsticks[N]; // binary semaphores controling the access to each chopstick
int hunger_level[N];

void *philospher(void *num);
void take_fork(int);
void put_fork(int);

#define ANSI_COLOR_RED     "\x1b[31m"
#define ANSI_COLOR_GREEN   "\x1b[32m"
#define ANSI_COLOR_YELLOW  "\x1b[33m"
#define ANSI_COLOR_BLUE    "\x1b[34m"
#define ANSI_COLOR_MAGENTA "\x1b[35m"
#define ANSI_COLOR_CYAN    "\x1b[36m"
#define ANSI_COLOR_RESET   "\x1b[0m"

int main()
{
    printf("Dining Philosophers: starving example 1\n");

    int phil_num[N];
    int i;
    pthread_t thread_id[N]; // each thread will simulate the behavior of one philosopher

    for (i = 0; i < N; i++)
        sem_init(&chopsticks[i], 0, 1);

    for (i = 0; i < N; i++)
    {
        phil_num[i] = i;
        pthread_create(&thread_id[i], NULL, philospher, &phil_num[i]);
    }

//    for (i = 0; i < N; i++)
i = 0;
        pthread_join(thread_id[i], NULL);
}

void *philospher(void *num)
{
    int i = *((int *)num);
    hunger_level[i] = 0;
    while (1)
    {
        printf("Philosopher %d is thinking\n", i + 1);
        sleep(1);

        printf("Philosopher %d is hungry\n", i + 1);
        take_fork(i);

        printf(ANSI_COLOR_GREEN "Philosopher %d is eating\n" ANSI_COLOR_RESET, i + 1);
        hunger_level[i] = 0;
        sleep(1);

        put_fork(i);
    }
}

void take_fork(int ph_num)
{
    while (1)
    {
        // waiting for left chopstick
        printf("Philosopher %d is Hungry and waits for chopstick %d\n", ph_num + 1, LEFT + 1);
        sem_wait(&chopsticks[LEFT]);

        // got left chopstick
        printf("Philosopher %d got chopstick %d\n", ph_num + 1, LEFT + 1);
        sleep(1);

        // waiting for right chopstick
        printf("Philosopher %d now try taking chopstick %d\n", ph_num + 1, RIGHT + 1);
        // plays nice if not that hungry
        if (hunger_level[ph_num] < MAX_HUNGER)
        {
            if (sem_trywait(&chopsticks[RIGHT]) == 0)
            {
                // got right chopstick
                printf("Philosopher %d took chopstick %d and %d, and now is Eating\n", ph_num + 1, LEFT + 1, RIGHT + 1);
                sleep(1);
                // leaves the loop
                goto got_forks;
            }
            else
            {
                // could not get right chopstick, releasing the left chopstick
                printf(ANSI_COLOR_RED "Philosopher %d unable to get both chopsticks %d and %d, releasing left one\n" ANSI_COLOR_RESET, ph_num + 1, LEFT + 1, RIGHT + 1);
                sem_post(&chopsticks[LEFT]);
                hunger_level[ph_num]++;
                sleep(5);
            }
        }
        // don't play nice if really hungry
        else
        {
            sem_wait(&chopsticks[RIGHT]);
            goto got_forks;
        }
    }
    got_forks:;
}

void put_fork(int ph_num)
{
    printf("Philosopher %d putting fork %d and %d down\n", ph_num + 1, LEFT + 1, ph_num + 1);
    sem_post(&chopsticks[LEFT]);
    sleep(1);
    sem_post(&chopsticks[RIGHT]);
    sleep(1);
}
