#include <stdio.h>
#include <stdlib.h>
#include <semaphore.h>
#include <pthread.h>
#include <unistd.h>
#include <stdbool.h>

#define N_SEATS 1
#define N_CLIENT_THREADS 4

int wait_length;
int next_client;
sem_t wait_room[N_SEATS];

pthread_mutex_t chair;
pthread_mutex_t wait_state;

sem_t sleeping;
sem_t wake_up;
sem_t can_cut;
sem_t haircut_done;

void *barber(void *num);
void *client(void *num);
int time_to_next_arrival(void);

#define COLOR_RED     "\x1b[31m"
#define COLOR_GREEN   "\x1b[32m"
#define COLOR_YELLOW  "\x1b[33m"
#define COLOR_BLUE    "\x1b[34m"
#define COLOR_MAGENTA "\x1b[35m"
#define COLOR_CYAN    "\x1b[36m"
#define COLOR_RESET   "\x1b[0m"

#define BARBER_COLOR COLOR_BLUE
#define CLIENT_COLOR COLOR_GREEN

/* variaveis para ajustar os tempos e debugar o programa */
int first_client_delay[N_CLIENT_THREADS] = {1, 2, 3, 4};
int delay_between_clients[N_CLIENT_THREADS] = {6, 3, 10, 10};

int main(void)
{
	printf("Sleeping barber problem.\n\n");

	/* barbeiro sentado na cadeira, dormindo e esperando ser acordado*/
	pthread_mutex_init(&chair, NULL);
	pthread_mutex_lock(&chair);
	sem_init(&sleeping, 0, 1);
	sem_init(&wake_up, 0, 0);

	/* sala de espera vazia */
	next_client = 0;
	wait_length = 0;
	for (int i = 0; i < N_SEATS; i++)
		sem_init(&wait_room[i], 0, 0);
	

	/* inicializacao de outras variaveis */
	sem_init(&can_cut, 0, 0);
	sem_init(&haircut_done, 0, 0);
	pthread_mutex_init(&wait_state, NULL);
	
	pthread_t barber_thread;
	pthread_create(&barber_thread, NULL, barber, NULL);

	pthread_t client_thread[N_CLIENT_THREADS];
	int thread_num[N_CLIENT_THREADS];

	for (int i = 0; i < N_CLIENT_THREADS; i++)
		thread_num[i] = i;
	
	for (int i = 0; i < N_CLIENT_THREADS; i++)
		pthread_create(&client_thread[i], NULL, client, &thread_num[i]);
	
	pthread_join(barber_thread, NULL);
	for (int i = 0; i < N_CLIENT_THREADS; i++)
		pthread_join(client_thread[i], NULL);
	
	return 0;
}

void *barber(void *num)
{	int haircut_time = 4;
	while (1) {
		/* barbeiro dormindo na cadeira */
		sem_wait(&wake_up);
		printf(BARBER_COLOR "Barber woke up.\n" COLOR_RESET);
		pthread_mutex_unlock(&chair);
		
                /* barbeiro acordado fora da cadeira esperando cliente sentar */
		sem_wait(&can_cut);

		/* corta o cabelo do cliente */
		printf(BARBER_COLOR "Barber is cutting the hair of a client.\n"
		       COLOR_RESET);
		sleep(haircut_time);
		sem_post(&haircut_done);

		while (wait_length > 0) {
			/* pára a fila para chamar o próximo */
			pthread_mutex_lock(&wait_state);
			sem_post(&wait_room[next_client]);
			next_client = (next_client + 1) % N_SEATS;
			wait_length--;
			pthread_mutex_unlock(&wait_state);			

			/* corta */
			sem_wait(&can_cut);
			printf(BARBER_COLOR
			       "Barber is cutting the hair of a client.\n"
			       COLOR_RESET);
			sleep(haircut_time);
			sem_post(&haircut_done);
		}

		/* senta na cadeira e dorme */
		pthread_mutex_lock(&chair);
		sem_post(&sleeping);
		printf(BARBER_COLOR "Barber is going to take a nap.\n"
		       COLOR_RESET);
	}
}

void *client(void *num)
{
	int i = *((int *) num);
	sleep(first_client_delay[i]);
	
	while (1) {
		/* se o barbeiro estiver dormindo */
		if (sem_trywait(&sleeping) == 0) {
			printf(CLIENT_COLOR
			       "Client arrived and will be served.\n"
			       COLOR_RESET);
			/* acorda ele */
			sem_post(&wake_up);

			/* senta na cadeira até terminar o corte */
			pthread_mutex_lock(&chair);
			sem_post(&can_cut);
			sem_wait(&haircut_done);
			pthread_mutex_unlock(&chair);
			
			printf(CLIENT_COLOR
			       "Haircut done. Client is leaving.\n"
			       COLOR_RESET);
		} else {
			/* bloqueia a fila para poder olhar pra ela */
			pthread_mutex_lock(&wait_state);
			/* sala de espera com lugar */
			if (wait_length < N_SEATS) {
				int seat = (next_client + wait_length) % N_SEATS;
				wait_length++;
				pthread_mutex_unlock(&wait_state);

				printf(CLIENT_COLOR
				       "Client arrived and is waiting.\n"
				       COLOR_RESET);
				sem_wait(&wait_room[seat]);

				pthread_mutex_lock(&chair);
				sem_post(&can_cut);
				sem_wait(&haircut_done);
				pthread_mutex_unlock(&chair);
				printf(CLIENT_COLOR
				       "Haircut done. Client is leaving.\n"
				       COLOR_RESET);
				
			/* sala de espera vazia */
			} else {
				pthread_mutex_unlock(&wait_state);
				printf(COLOR_RED
				       "Waiting room full. New client is leaving.\n"
				       COLOR_RESET);
			}
		}
		sleep(delay_between_clients[i]);
	}
}
