* Exercício 97
A estrutura básica dessa solução é que há duas variáveis que são usadas para
administrar os quartos. em-uso indica se algum quarto está sendo usado, qual e
se o exit handler está em execução. q indica o número de threads no quarto em
uso. Cada quarto tem seu próprio conjunto de espera. Quando há threads
esperando, o exit handler é o responsável por acordá-las.

- chegada thread para o quarto k
  - enquanto quarto em uso não for k ou -1 (livre):
    - espera na k-ésima sala de espera
  - seta em-uso para k
  - incrementa q (threads no quarto)

- saída de thread do quarto k
  - decrementa q
  - se q = 0:
    - em-uso = -2 (handler executando)
    - chama exit handler do quarto k

- exit handler:
- procura proxima sala de espera populada
  - em-uso = essa sala ou -1 caso não haja ninguém esperando
  - notify (ou signal) All nessa sala

Como o exit handler administra as entradas quando há espera, ele deve ser
programado para evitar starvation. Para isso, fazemos com que sempre que
chamado para o quarto k, ele comece sua busca pelo quarto k+1, incrementando
até terminar no quarto k. Dessa forma, ele garante que um quarto não entrará
duas vezes seguidas caso há threads esperando por outro quarto. Para evitar
starvation dentro do mesmo quarto, usamos o parâmetro de justiça do
ReentrantLock, que favorece threads que estão esperando há mais tempo.

** Desafios encontrados
- O maior desafio foi entender como resolver o problema usando as
  estruturas e protótipos de função dados.
- Dada a semelhança desse problema com o anterior, depois que
  decidimos fazer o exitHandler administrar os quartos de maneira
  sequencial, a solução foi relativamente fácil de se implementar.
- O uso do método ReentrantLock.hasWaiters(Condition) simplificou a
  implementação. Caso optássemos por não usá-lo, deveríamos ter um
  contador para cada condição.

