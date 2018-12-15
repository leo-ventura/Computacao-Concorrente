> Exercise 95. A savings account object holds a nonnegative balance, and provides
> deposit(k) and withdraw(k) methods, where deposit(k) adds k to the balance,
> and withdraw(k) subtracts k, if the balance is at least k, and otherwise
> blocks until the balance becomes k or greater.

***

# Primeira Questão
> Implement this savings account using locks and conditions.

A implementação desse sistema está definido em SavingsAccount1.java e seu caso de teste foi feito em testSavings1() em Main.java. Utilizamos o ReentrantLock dispoível em java.util.concurrent.locks e as condições também disponível no mesmo módulo. Dessa forma, ao tentar fazer uma retirada de valor k, checamos se o valor é menor que o que há disponível na conta, pois, se for, devemos entrar em modo de espera com await(). Além disso, ao fazer um depósito, mandamos as threads acordarem e checarem se ainda está com crédito insuficiente (motivo de precisarmos envolver o bloco por um loop).

# Segunda Questão
> Now suppose there are two kinds of withdrawals: ordinary and preferred.
> Devise an implementation that ensures that no ordinary withdrawal occurs
> if there is a preferred withdrawal waiting to occur.

A implementação dessa vez conta com duas funções substitutas a withdraw (ordinaryWithdraw e preferredWithdraw). Para garantir que não ocorra ordinaryWithdraw antes que todas as preferredWithdraws tenham sido realizadas, precisamos adicionar uma variável preferredWithdrawCount e checar se ela é 0 quando formos executar o ordinaryWithdraw. Dessa forma, conseguimos implementar o que foi pedido pelo enunciado.

# Terceira Questão
Explicada claramente em [questao3.md](https://github.com/leo-ventura/Computacao-Concorrente/blob/master/monitors/E95/questao3.md). Achei melhor explicar usando um markdown separado para ficar mais organizado.