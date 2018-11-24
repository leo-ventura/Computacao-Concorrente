#include <stdio.h>
#include <semaphore.h>
#include <pthread.h>
#include <stdlib.h>
#include <unistd.h>

/* number of smokers and number of resources */
#define N 3

sem_t can_serve;
sem_t resources[N];
sem_t can_access_table[N];

void *agent(void *num);
void *smoker(void *num);
int random_resource(int not_this_one);

#define COLOR_RED     "\x1b[31m"
#define COLOR_GREEN   "\x1b[32m"
#define COLOR_YELLOW  "\x1b[33m"
#define COLOR_BLUE    "\x1b[34m"
#define COLOR_MAGENTA "\x1b[35m"
#define COLOR_CYAN    "\x1b[36m"
#define COLOR_RESET   "\x1b[0m"

#define AGENT_COLOR COLOR_GREEN
#define SMOKER_COLOR COLOR_CYAN

int main(void)
{
	srand(0);
	
	/* agente pode servir e nao ha nada na mesa */
	sem_init(&can_serve, 0, 1);
	for (int i = 0; i < N; i++)
		sem_init(&resources[i], 0, 0);

	/* inicializa o semaforo que controla acesso à mesa */
	for (int i = 0; i < N; i++)
		sem_init(&can_access_table[i], 0, 1);

	/* cria thread do agente */
	pthread_t agent_thread;
	pthread_create(&agent_thread, NULL, agent, NULL);

	/*cria thread dos fumantes */
	int thread_num[N];
	for (int i = 0; i < N; i++)
		thread_num[i] = i;

	pthread_t smoker_thread[N];
	for (int i = 0; i < N; i++)
		pthread_create(&smoker_thread[i], NULL, smoker, &thread_num[i]);

	/* finaliza */
	pthread_join(agent_thread, NULL);
	for (int i = 0; i < N; i++)
		pthread_join(smoker_thread[i], NULL);
		
	return 0;
}

/* gera um numero aleatorio entre 0 e N exceto o passado como argumento */
int random_resource(int not_this_one)
{
	int x;
	do 
		x = rand() % N;
	while (x == not_this_one);
	return x;
}

/* agente esta fixo para 2 recursos. */
void *agent(void *num)
{
	int first, second;
	while (1) {
		sem_wait(&can_serve);
	
		first = random_resource(-1);
		second = random_resource(first);

		printf(AGENT_COLOR "Agent put %d and %d on the table\n"
		       COLOR_RESET, first, second);
		
		sem_post(&resources[first]);
		sem_post(&resources[second]);
	}
}

void *smoker(void *num)
{
	int id = *((int *) num);
	
	while (1) {
		/* verifica se os dois recursos que precisa estao na mesa */
		int resource_count = 0;
		sem_wait(&can_access_table[id]);
		for (int i = 0; i < N; i++) {
			if (i != id) {
				int val;
				sem_getvalue(&resources[i], &val);
				if (val > 0) {
					resource_count++;
				}
			}
		}
		sem_post(&can_access_table[id]);

		/* encontrou todos os recursos que precisa */
		if (resource_count == N-1) {
			/* faz os outros pararem de acessar a mesa */
			for (int i = 0; i < N; i++)
				sem_wait(&can_access_table[i]);
		
			/* pega os recursos */
			for (int i = 0; i < N; i++) {
				if (i != id) {
					sem_wait(&resources[i]);
				}
			}

			/* fuma */
			printf(SMOKER_COLOR "Smoker %d is smoking\n"
			       COLOR_RESET, id);
			sleep(1);

			/* acorda o agente */
			sem_post(&can_serve);

			/* devolve permissao para acessar a mesa */
			for (int i = 0; i < N; i++)
				sem_post(&can_access_table[i]);
		}
	}
}
